package rescue;
import lejos.nxt.*;

public class StateLineFollow extends State {

	private static StateLineFollow instance = new StateLineFollow();

	// Kc = 4 Pc = .25 dT = .0028
	private double KpLocal = 8.00; // 10.00
	private double KiLocal = 0.70; // 0.70
	private double KdLocal = 6.00; // 6.00
	
	public double Kp = 8.00; // 10.00
	public double Ki = 0.70; // 0.70
	public double Kd = 6.00; // 6.00

	private StateLineFollow() {
	}

	public static StateLineFollow getInstance() {
		return instance;
	}

	private int calcError(int left, int right) {
		// 40 is white, 20 is black
		int error = (right - left);
		if (Math.abs(error) <= 3) {
			error = 0;
		} else {
			if (error < 0) {
				error += 3;
			} else {
				error -= 3;
			}
		}
		return error;
	}

	public void enter(Robot robot) {
		//debugln("StLineFollow enter");
		robot.dropCompass();
	}

	public void execute(Robot robot) {
		debugln("StLineFollow execute");
		// String msg;

		int integral = 0;
		int lastError = 0;
		int derivative = 0;
		int error = 0;

		double turn = 0;
		int powerRight = 0;
		int powerLeft = 0;

		int n = 0;
		int rampCount = 0;
		
		int accelAverage = 0;

		robot.motRight.forward();
		robot.motLeft.forward();

		while (!Button.ESCAPE.isDown()) {
			if(n < 7) {
				accelAverage += robot.accel.getXAccel();
				accelAverage /= 2;
				//debugln(""+accelAverage);
				if (accelAverage > 40) {
					if(rampCount < 5) {
						rampCount++;
					}
				} else {
					if(rampCount > -7) {
						rampCount--;
					}
				}
				
				if (rampCount >= 2){
					robot.isOnRamp = true;
				} else if (rampCount <= 0) {
					robot.isOnRamp = false;
				}

				if (robot.isOnRamp){
					if (robot.getBaseMotorPower() != 70) {
						robot.setBaseMotorPower(70);	
					}
					KpLocal = 5.00; // 10.00
					KiLocal = 0.00; // 0.70
					KdLocal = 0.00; // 6.00
				} else {
					if (robot.getBaseMotorPower() != 45) {
						robot.setBaseMotorPower(45);
					}
					KpLocal = Kp; // 10.00
					KiLocal = Ki; // 0.70
					KdLocal = Kd; // 6.00
				}
				
				n = 0;
			}
			

			error = calcError(robot.getLightLeft(), robot.getLightRight());
			if (error == 0) {
				integral = 0;
				derivative = 0;
			} else {
				integral += error;
				derivative = error - lastError;
			}
			derivative = error - lastError;
			// debugln("error: " + error + " integral: " + integral +
			// " derivative: " + derivative);
			turn = (int) Util.round(KpLocal * error + KiLocal * integral + KdLocal * derivative);
			// if(Turn == 0) {
			// debugln("I'm driving straight");
			// robot.forward(2);
			// } else {
			// debugln(""+Turn);
			powerRight = (int) Util.round(robot.getBaseMotorPower() + turn);
			powerLeft = (int) Util.round(robot.getBaseMotorPower() - turn);
			robot.motRight.setPower(powerRight);
			robot.motLeft.setPower(powerLeft);
			// }

			lastError = error;

			if (robot.ultrasonic.getDistance() < 10) {
				robot.changeState(StateAvoidObstacle.getInstance());
				//robot.changeState(StateCommand.getInstance());
				return;
			}
			if ((robot.getLightLeft() > robot.threshSilver)||(robot.getLightRight() > robot.threshSilver)) {
				Sound.playTone(440, 100);
				robot.stop();
				robot.backward(2);
				robot.goToHeading(robot.doorHeading);
				robot.dropCompass();
				robot.forward(30);
				robot.changeState(StateCommand.getInstance());
				return;
			}
			if (Button.ENTER.isDown()) {
				robot.changeState(StateCommand.getInstance());
				return;
			}
			n++;
		}

	}

	public void exit(Robot robot) {
		debugln("StLineFollow exit");
		robot.stop();
	}

}
