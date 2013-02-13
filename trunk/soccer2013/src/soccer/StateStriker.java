package soccer;

public class StateStriker extends State{

	private static StateStriker instance = new StateStriker();
	Robot robot = Robot.getRobot();
	Navigator nav = new Navigator(robot);

	//--------------------------------------------------------------------------------------
	public void execute(Robot robot) {
		robot.followBall();
		nav.pointToGoal();
		//moveForward will be changed to kick. 
		robot.moveForward();
	}
	
	public static StateStriker getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		
	}

	
	public void exit(Robot robot) {
		
	}

}
