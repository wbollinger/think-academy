package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;

public class Main {
	/*
	 * public final boolean checkButtons = false;
	 * 
	 * public static int checkButtons() { // uncomment to enable button check
	 * 
	 * // wait for a button press from the user int buttons; while ((buttons =
	 * Button.readButtons()) == 0) { } // // wait until the button is released!
	 * while (Button.ENTER.isDown()) { } return buttons;
	 * 
	 * // tell main() to run //return Button.ID_RIGHT; }
	 */

	public static void main(String[] args) {

		// int buttons = checkButtons();

		// create a robot
		Robot robot = Robot.getRobot();
		LCD.drawString("Waiting for push", 0, 0);
		
		while(Button.LEFT.isDown() && Button.RIGHT.isDown() && Button.ENTER.isDown())
			;
		while (Button.LEFT.isUp() && Button.RIGHT.isUp() && Button.ENTER.isUp())
			;

		if (Button.RIGHT.isDown()) { // we are team yellow
			robot.nav.ENEMY_GOAL = robot.blueGoalHeading;
			robot.nav.ALLY_GOAL = robot.yellowGoalHeading;
			robot.io.setUseCommands(false);
			robot.io.setUseDebug(false);
		} else if (Button.LEFT.isDown()) { // we are team blue
			robot.nav.ENEMY_GOAL = robot.yellowGoalHeading;
			robot.nav.ALLY_GOAL = robot.blueGoalHeading;
			robot.io.setUseCommands(false);
			robot.io.setUseDebug(false);
		} else if (Button.ENTER.isDown()) { // debug mode
			// Enable BlueTooth command loop
			robot.io.setUseCommands(true);
			robot.io.setUseDebug(true);
		}

		robot.run(); // uncomment to activate state machine
	}
}
