package soccer;

import lejos.nxt.Button;

public class StateStriker extends State{

	private static StateStriker instance = new StateStriker();

	//--------------------------------------------------------------------------------------
	public void execute(Robot robot) {
		robot.followBall();
		robot.nav.pointToHeadingArc(Navigator.ENEMY_GOAL);
		robot.nav.pointToGoal();
		//moveForward will be changed to kick. 
		robot.moveForward();
		while(Button.ENTER.isUp()) {
			robot.sleep(3000);
			robot.stopAll();
			robot.nav.pointToHeadingArc(Navigator.ENEMY_GOAL);
			robot.nav.pointToGoal();
			robot.moveForward();
		}
		robot.stopAll();
		robot.changeState(StateCommand.getInstance());
	}
	
	public static StateStriker getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		
	}

	
	public void exit(Robot robot) {
		
	}

}
