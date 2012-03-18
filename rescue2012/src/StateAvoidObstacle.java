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
		debugln("OBENT");
		robot.stop();

	}

	public void execute(Robot robot) {
		// Some code to find obstacle size?
		Obstacle obstacle = new Obstacle(13, 13);
		
		int ff;

		robot.backward();
		robot.sleep(500);
		
		if (robot.leftSideCheck()) {
			ff = 5;
			robot.left(90);
			robot.forward(obstacle.getxLength() / 2 + ff); 
				
			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + ff + 10)) {
				debug("line found on first leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getxLength() + ff)) {
				debug("line found on second leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + ff + 10)) {
				debug("line found on third leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getxLength() / 2 + ff)) {
				debug("line found on fourth leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
		}
		else {
			ff = 5;
			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getxLength() / 2 + ff)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + ff + 10)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getxLength() + ff)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + ff + 10)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getxLength() / 2 + ff)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}

		}
		
	}

	public void exit(Robot robot) {
	}
}
