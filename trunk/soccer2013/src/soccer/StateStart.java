package soccer;

import lejos.nxt.comm.RConsole;

//------------------------------------------------------------------------
//  robot starting state - decides what to do when robot is turned on
//------------------------------------------------------------------------

public class StateStart extends State {

	static private StateStart instance = new StateStart();

	private StateStart() {
	}

	// this is a singleton
	public static StateStart getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
	}

	public void execute(Robot robot) {
		if (robot.io.getUseCommands()) {
			// setup BT command loop
			robot.changeState(StateCommand.getInstance());
		} else {
			// jump straight into first state (for now, exit)
			robot.changeState(StateExit.getInstance());
		}
	}

	public void exit(Robot robot) {
	}
}
