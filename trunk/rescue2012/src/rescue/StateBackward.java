package rescue;
//------------------------------------------------------------------------
//  robot Backward state - drives the robot backwards
//------------------------------------------------------------------------
//import lejos.nxt.*;

public class StateBackward extends State {

	static private StateBackward instance = new StateBackward();

	private StateBackward() {
	}

	// this is a singleton
	public static StateBackward getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debug("StBackward enter\n");
	}

	public void execute(Robot robot) {
		debug("StBackward execute\n");
		robot.motRight.backward();
		robot.motLeft.backward();
		while (!robot.touch.isPressed()) {
		}
		robot.changeState(StateForward.getInstance());
	}

	public void exit(Robot robot) {
		debug("StBackward exit\n");
	}
}
