//------------------------------------------------------------------------
//  robot US sweep state - spin and scan
//------------------------------------------------------------------------

public class StateUltrasonicSweep extends State {

	static private StateUltrasonicSweep instance = new StateUltrasonicSweep();

	private StateUltrasonicSweep() {
	}

	// this is a singleton
	public static StateUltrasonicSweep getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
	}

	public void execute(Robot robot) {
		debug("StUSSweep execute\n");

		int dist;
		double angle;
		int x, y;

		robot.resetAngle();
		robot.motLeft.forward();
		robot.motRight.backward();

		while (Math.abs(robot.getAngle()) < 360.0) {
			dist = robot.ultrasonic.getDistance();
			angle = robot.getAngle();
			x = (int) (dist * Math.cos(angle * (Math.PI / 180.0)));
			y = (int) (dist * Math.sin(angle * (Math.PI / 180.0)));
			debug(x + "	" + y + "	" + angle+"\n");
		}

		robot.motRight.stop();
		robot.motLeft.stop();

		robot.changeState(StateExit.getInstance());
	}

	public void exit(Robot robot) {
	}
}
