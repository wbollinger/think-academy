package rescue;
import lejos.nxt.*;

public class StateLineFollowRegulated extends State {

	private static StateLineFollowRegulated instance = new StateLineFollowRegulated();

	// Kc = 4 Pc = .25 dT = .0028
	public double Kp = 4.00; // 6.00
	public double Ki = 0.01; // 0.01
	public double Kd = 0.00; // 0.00

	private StateLineFollowRegulated() {
	}

	public static StateLineFollowRegulated getInstance() {
		return instance;
	}

	private int calcError(LightSensor left, LightSensor right) {
		// 40 is white, 20 is black
		int error = (right.getLightValue() - left.getLightValue());
		if (Math.abs(error) <= 3) {
			error = 0;
		} else {
			if (error < 0) {
				error += 3;
			} else {
				error -= 3;
			}
		}
		//debugln("Error: " + error);
		return error;
	}

	public void enter(Robot robot) {
		debug("StLineFollow enter\n");
	}

	public void execute(Robot robot) {
		debug("StLineFollow execute\n");

		// String msg;

		int integral = 0;
		int lastError = 0;
		int derivative = 0;
		int error = 0;

		int Turn = 0;
		int powerRight = 0;
		int powerLeft = 0;

		robot.motRight.forward();
		robot.motLeft.forward();

		while (!Button.ESCAPE.isDown()) {

			error = calcError(robot.lightLeft, robot.lightRight);
			if (error == 0) {
				integral = 0;
			} else {
				integral += error;
			}
			derivative = error - lastError;
			Turn = (int) Util.round(Kp * error + Ki * integral + Kd
					* derivative);
//			if(Turn == 0) {
//				debugln("I'm driving straight");
//				robot.forward(2);
//			} else {
			    //debugln(""+Turn);
				powerRight = robot.getBaseMotorPower() + Turn;
				powerLeft = robot.getBaseMotorPower() - Turn;
				robot.motRegRight.setSpeed(powerRight);
				robot.motRegLeft.setSpeed(powerLeft);
//			}
			

			lastError = error;
			
			if (robot.ultrasonic.getDistance()< 10) {
				robot.changeState(StateCommand.getInstance());
				break;
			}
//			if (robot.lightLeft.getLightValue() > 56) {
//				Sound.playTone(440, 100);
//				robot.sleep(100);
//			}
		}
	}

	public void exit(Robot robot) {
		debug("StLineFollow exit\n");
		robot.stop();
	}

}
