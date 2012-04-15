import lejos.nxt.*;

public class StateLineFollow extends State {

	private static StateLineFollow instance = new StateLineFollow();

	// Kc = 4 Pc = .25 dT = .0028
	public double Kp = 10.00; // 10.00
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

		robot.motRight.forward();
		robot.motLeft.forward();

		while (!Button.ESCAPE.isDown()) {

			if ((robot.accel.getXAccel() > 50) && (n > 5)) {
				if (robot.getBaseMotorPower() != 100) {
					robot.setBaseMotorPower(100);
				}
				n = 0;
			} else {
				if (robot.getBaseMotorPower() != 65) {
					robot.setBaseMotorPower(65);
				}
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
			turn = (int) Util.round(Kp * error + Ki * integral + Kd * derivative);
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
				robot.changeState(StateCommand.getInstance());
				return;
			}
			// if ((robot.getLightLeft() > 60)||(robot.getLightRight() > 60)) {
			// Sound.playTone(440, 100);
			// robot.stop();
			// robot.changeState(StateCommand.getInstance());
			// return;
			// }
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
