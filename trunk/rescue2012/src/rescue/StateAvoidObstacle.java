package rescue;
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
		Obstacle obstacle = new Obstacle(35 , 10);

		double ff;
		ff = 3 + robot.robotDiameter / 2;
		int backDist = 5;
		robot.sleep(500);
		robot.backward(backDist);

		if (robot.obstacleSideCheck()) {

			robot.avoidedLeft = true;
			robot.forward(obstacle.getxLength() / 2 + ff);
			robot.correctRight(90);
			debugln("First turn completed");
			robot.forward((obstacle.getyLength() + 2 * ff
					+ backDist+10)/4);
			if (robot.forwardLookForLine(((obstacle.getyLength() + 2 * ff
					+ backDist+10)*3)/4)) {
				debug("line found on first leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			debugln("First forward completed");
			if (robot.correctRightLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.sleep(500);
			debugln("Second turn completed");
			if (robot.forwardLookForLine(obstacle.getxLength() + ff+5)) {
				debug("line found on second leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			debugln("Second forward completed");
			if (robot.correctRightLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}

			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist+5)) {
				debug("line found on third leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			if (robot.correctRightLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}

			if (robot.forwardLookForLine(obstacle.getxLength() / 2 + ff+5)) {
				debug("line found on fourth leg\n");
				robot.changeState(StateFindLine.getInstance());
				return;
			}
		} else {

			robot.avoidedLeft = false;
			robot.forward(obstacle.getxLength() / 2 + ff);

			robot.correctLeft(90);
			debugln("First turn completed");
			robot.forward((obstacle.getyLength() + 2 * ff
					+ backDist+10)/4);
			if (robot.forwardLookForLine(((obstacle.getyLength() + 2 * ff
					+ backDist+10)*3)/4)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			debugln("First forward completed");
			if (robot.correctLeftLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			robot.sleep(500);
			debugln("Second turn completed");
			if (robot.forwardLookForLine(obstacle.getxLength() + ff+5)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			debugln("Second forward completed");
			if (robot.correctLeftLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			if (robot.forwardLookForLine(obstacle.getyLength() + 2 * ff
					+ backDist+5)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			if (robot.correctLeftLine(90)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}
			if (robot.forwardLookForLine(obstacle.getxLength() / 2 + ff+5)) {
				robot.changeState(StateFindLine.getInstance());
				return;
			}

		}
	}

	public void exit(Robot robot) {
	}
}
