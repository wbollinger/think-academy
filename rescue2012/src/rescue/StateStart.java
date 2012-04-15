package rescue;
import lejos.nxt.Sound;
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
		if (robot.getUseCommands()) {
			// setup BT command loop
			robot.changeState(StateCommand.getInstance());
		} else if (robot.getUseRConsole()) {
			RConsole.openBluetooth(120000);
			RConsole.println("Connected!");
			Sound.playTone(440, 200);
		} else {
			// jump straight into line follower
			robot.changeState(StateLineFollow.getInstance());
		}
	}

	public void exit(Robot robot) {
	}
}
