package soccer;

import lejos.nxt.Button;

public class Main {
	public final boolean checkButtons = false;

	public static int checkButtons() {
		// uncomment to enable button check

		// wait for a button press from the user
		int buttons;
		while ((buttons = Button.readButtons()) == 0) {
		}
//		// wait until the button is released!
		while (Button.ENTER.isDown()) {
		}
		return buttons;

		// tell main() to run 
		//return Button.ID_RIGHT;
	}

	public static void main(String[] args) {
		
		//int buttons = checkButtons();
		
		// create a robot
		Robot robot = Robot.getRobot();
		
		while(Button.LEFT.isUp() && Button.RIGHT.isUp())
			;
		
		if(Button.RIGHT.isDown()) {
			robot.nav.ENEMY_GOAL = Navigator.BLUE_GOAL_HEADING;
			robot.nav.ALLY_GOAL = Navigator.YELLOW_GOAL_HEADING;
		} else {
			robot.nav.ENEMY_GOAL = Navigator.YELLOW_GOAL_HEADING;
			robot.nav.ALLY_GOAL = Navigator.BLUE_GOAL_HEADING;
		}

		//if (buttons == Button.ID_ENTER) {
		//	// Run in autonomous mode
		//	robot.io.setUseCommands(false);
		//	robot.io.setUseDebug(false);
		//} else {
			// Use BlueTooth command loop
			robot.io.setUseCommands(true);
			robot.io.setUseDebug(true);
		//}
		//robot.followBall();
		robot.run(); //uncomment to activate state machine
	}
}
