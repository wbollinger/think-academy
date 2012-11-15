package soccer;

import lejos.nxt.Button;

public class Main {
	public final boolean checkButtons = false;

	public static int checkButtons() {
		// uncomment to enable button check

//		// wait for a button press from the user
//		int buttons;
//		while ((buttons = Button.readButtons()) == 0) {
//		}
//		// wait until the button is released!
//		while (Button.ENTER.isDown()) {
//		}
//		return buttons;

		// tell main() to run 
		return Button.ID_RIGHT;
	}

	public static void main(String[] args) {
		int buttons;

		// create a robot
		Robot robot = Robot.getRobot();

		if (buttons == Button.ID_ENTER) {
			// Run in autonomous mode
			//robot.setUseCommands(false);
			//robot.setUseDebug(false);
		} else {
			// Use BlueTooth command loop
			robot.setUseCommands(true);
			robot.setUseDebug(true);
		}
		robot.run();
	}
}
