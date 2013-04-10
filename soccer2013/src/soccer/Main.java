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
		
		while(Button.LEFT.isUp() && Button.RIGHT.isUp() && Button.ENTER.isUp())
			;
		
		if(Button.RIGHT.isDown()) {
			robot.nav.ENEMY_GOAL = Navigator.BLUE_GOAL_HEADING;
			robot.nav.ALLY_GOAL = Navigator.YELLOW_GOAL_HEADING;
		} else if(Button.LEFT.isDown()){
			robot.nav.ENEMY_GOAL = Navigator.YELLOW_GOAL_HEADING;
			robot.nav.ALLY_GOAL = Navigator.BLUE_GOAL_HEADING;
		} else if(Button.ENTER.isDown()){
			if(robot.compass.getDegrees() > robot.nav.YELLOW_GOAL_HEADING-45 && robot.compass.getDegrees() < robot.nav.YELLOW_GOAL_HEADING+45){
				robot.nav.ENEMY_GOAL = Navigator.YELLOW_GOAL_HEADING;
				robot.nav.ALLY_GOAL = Navigator.BLUE_GOAL_HEADING;
			} else if(robot.compass.getDegrees() > robot.nav.BLUE_GOAL_HEADING-45 && robot.compass.getDegrees() < robot.nav.BLUE_GOAL_HEADING+45){
				robot.nav.ENEMY_GOAL = Navigator.BLUE_GOAL_HEADING;
				robot.nav.ALLY_GOAL = Navigator.YELLOW_GOAL_HEADING;
			} // have to do something if not facing the right way.
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
