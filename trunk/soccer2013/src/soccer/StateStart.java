package soccer;

import lejos.nxt.LCD;

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
			LCD.drawString("BT disabled...", 0, 1);
			// jump straight into appropriate state based on robot position
			if (robot.name.equals("LineBacker") || robot.name.equals("Tim")) {
				robot.changeState(StateGoalie.getInstance());
			} else if (robot.name.equals("bbot") || robot.name.equals("JPNXT")) {
				robot.changeState(StateStriker.getInstance());
			}
		}
	}

	public void exit(Robot robot) {
	}
}
