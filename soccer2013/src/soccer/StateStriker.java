package soccer;

public class StateStriker extends State{

	private static StateStriker instance = new StateStriker();

	//--------------------------------------------------------------------------------------
	public void execute(Robot robot) {
		robot.followBall();
		robot.nav.pointToHeading(90);//number will be the north of the field.
		robot.nav.pointToGoal();
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
