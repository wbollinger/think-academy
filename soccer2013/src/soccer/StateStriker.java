package soccer;

import lejos.nxt.Button;

public class StateStriker extends State{

	private static StateStriker instance = new StateStriker();

	//--------------------------------------------------------------------------------------
	public void execute(Robot robot) {
		robot.changeState(StateFollowBall.getInstance());
		robot.nav.pointToHeadingArc(Navigator.ENEMY_GOAL);
		robot.nav.pointToGoal();
		//moveForward will be changed to kick. 
		robot.moveForward();
		
		
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
