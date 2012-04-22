package rescue;

import lejos.nxt.Button;

public class Main {

	public static void main(String[] args) {
		int buttons;
		
		// create a robot
		Robot robot = Robot.getRobot();

		// wait for a button press from the user
		while ((buttons = Button.readButtons()) == 0) {
		}
		
		// Important:  now wait until the button is released!
		while (Button.ENTER.isDown()) {
		}
		
		if (buttons == Button.ID_ENTER) {
			// Run in autonomous mode
			robot.setUseCommands(false);
			robot.setUseDebug(false);
		} else {
			// Use BlueTooth command loop
			robot.setUseCommands(true);
			robot.setUseDebug(true);
		}

		robot.run();
	}
}
