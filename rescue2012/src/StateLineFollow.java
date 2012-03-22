import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class StateLineFollow extends State {

	private static StateLineFollow instance = new StateLineFollow();

	// Kc = 4 Pc = .25 dT = .0028
	final double Kp = 3.25;
	final double Ki = 0;
	final double Kd = 0;
	final int threshold = 30;

	private StateLineFollow() {
	}

	public static StateLineFollow getInstance() {
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
		RConsole.println("Error: " + error);
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

		// for(int i = 0; i < 1000; i++) {
		while (!Button.ESCAPE.isDown()) {

			if (error == 0) {
				integral = 0;
			}

			error = calcError(robot.lightLeft, robot.lightRight);
			integral = integral + error;
			derivative = error - lastError;
			Turn = (int) Math.round(Kp * error + Ki * integral + Kd
					* derivative);
			powerRight = robot.getBaseMotorPower() + Turn;
			powerLeft = robot.getBaseMotorPower() - Turn;

			// msg = "" + Kp*error + ", " + Ki*integral;
			// RConsole.println(msg);

			robot.motRight.setPower(powerRight);
			robot.motLeft.setPower(powerLeft);

			lastError = error;
			if (robot.touch.isPressed()) {
				robot.changeState(StateBackward.getInstance());
			}
		}

	}

	public void exit(Robot robot) {
		debug("StLineFollow exit\n");
	}

}
