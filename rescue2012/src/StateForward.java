//------------------------------------------------------------------------
//  robot Forward state - drives the robot forwards
//------------------------------------------------------------------------

public class StateForward extends State {

	static private StateForward instance = new StateForward();

	private StateForward() {
	}

	// this is a singleton
	public static StateForward getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debug("StForward enter\n");
		robot.forward();
	}

	public void execute(Robot robot) {
		while (!robot.touch.isPressed()) {

		}
		robot.changeState(StateAvoidObstacle.getInstance());
	}

	public void exit(Robot robot) {
		debug("StForward exit\n");
	}
}
