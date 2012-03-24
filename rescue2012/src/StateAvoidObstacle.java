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
		Obstacle obstacle = new Obstacle(10, 10);

		double ff;
		ff = 3 + robot.robotDiameter / 2;
		int backDist = 5;

		robot.backward(backDist);

		if (robot.leftSideCheck()) {

			robot.avoidedLeft = true;
			robot.left(90);
			robot.forward(obstacle.getxLength() / 2 + ff);

			robot.right(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist)) {
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
			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist)) {
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
		} else {

			robot.avoidedLeft = false;
			robot.right(90);
			robot.forward(obstacle.getxLength() / 2 + ff);

			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getxLength() + ff)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.left(90);
			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist)) {
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
