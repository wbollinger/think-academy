//------------------------------------------------------------------------
//  robot avoid state
//------------------------------------------------------------------------
public class StateAvoidObstacle extends State {

	static private StateAvoidObstacle instance = new StateAvoidObstacle();

	private StateAvoidObstacle() {
	}

	// this is a singleton
	public static StateAvoidObstacle getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		robot.leftBlack = false;
		robot.rightBlack = false;
		debug("OBENT");
		robot.stop();

	}

	public void execute(Robot robot) {
		// Some code to find obstacle size?
		Obstacle obstacle = new Obstacle(13, 13);

		robot.backward();
		robot.sleep(500);
		
		if (robot.leftSideCheck())
		{robot.squareLeft(obstacle);}
		else
		{robot.squareRight(obstacle);}
		robot.changeState(StateFindLine.getInstance());
	}

	public void exit(Robot robot) {
	}
}
