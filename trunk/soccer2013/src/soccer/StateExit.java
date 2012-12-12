package soccer;

import java.io.IOException;

import lejos.nxt.LCD;

//------------------------------------------------------------------------
//  robot Exit state - clean up here when robot program is exiting
//------------------------------------------------------------------------

public class StateExit extends State {

	static private StateExit instance = new StateExit();

	private StateExit() {
	}

	// this is a singleton
	public static StateExit getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
	}

	public void execute(Robot robot) {
		System.out.println("StateExit");
		robot.motA.stop();
		robot.motB.stop();
		robot.motC.stop();

		if (StateCommand.getInstance().getCommandLoopRunning() == true) {
			// Command loop is running; get the next command
			//robot.changeState(StateCommand.getInstance());
			return;
		}

		// really exit!
		// debug("StExit exec\n");

		if ((robot.io.inStream != null) && (robot.io.outStream != null)) {
			try {
				robot.io.inStream.close();
				robot.io.outStream.close();
			} catch (IOException e) {
				LCD.drawString("Exit IO Excep", 0, 0);
			}
			robot.io.inStream = null;
			robot.io.outStream = null;
		}
		robot.sleep(100); // wait for data to drain
		if (robot.io.btc != null) {
			robot.io.btc.close();
			robot.io.btc = null;
		}
		robot.exit = true;
	}

	public void exit(Robot robot) {
	}
}
