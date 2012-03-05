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
	}

	public void execute(Robot robot) {
		debug("StForward execute\n");
		robot.forward(50);
		robot.changeState(StateExit.getInstance());
	}

	public void exit(Robot robot) {
		debug("StForward exit\n");
	}
}
