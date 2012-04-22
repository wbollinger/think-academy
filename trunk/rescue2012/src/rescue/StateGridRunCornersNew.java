package rescue;

//------------------------------------------------------------------------
//  robot Grid Run state - search Room 3
//------------------------------------------------------------------------
import lejos.nxt.*;

public class StateGridRunCornersNew extends State {

	static private StateGridRunCornersNew instance = new StateGridRunCornersNew();

	int objectFound = 0;

	int up;
	int upRight;
	int right;
	int downRight;
	int down;
	int downLeft;
	int left;
	int upLeft;

	private StateGridRunCornersNew() {
	}

	// this is a singleton
	public static StateGridRunCornersNew getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debugln("State GridRunNew enter");
		// robot.setDir(90);

		robot.goTo(2, 2);
		updateSquares(robot);

	}

	public void execute(Robot robot) {

		robot.faceUpLeft();
		robot.forward(12);
		if (robot.isCanInSquare()) {
			if (robot.isCanInSquareCorners()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY() + 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			} else {
				robot.platformFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY() + 1] = 3;
				robot.printMap();
				Robot.playTone(440, 100);
				robot.sleep(100);
				if (robot.canHeld) {
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			}
		}

		robot.goTo(3, 2);
		updateSquares(robot);

		robot.faceUpRight();
		robot.forward(12);
		if (robot.isCanInSquare()) {
			if (robot.isCanInSquareCorners()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY() + 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			} else {
				robot.platformFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY() + 1] = 3;
				robot.printMap();
				Robot.playTone(440, 100);
				robot.sleep(100);
				if (robot.canHeld) {
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			}
		}

		robot.faceDownRight();
		robot.forward(12);
		if (robot.isCanInSquare()) {
			if (robot.isCanInSquareCorners()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY() + 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			} else {
				robot.platformFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY() + 1] = 3;
				robot.printMap();
				Robot.playTone(440, 100);
				robot.sleep(100);
				if (robot.canHeld) {
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			}
		}

		// debug(robot.getX() + "	" + robot.getY()+"\n");
		robot.printMap();

		if (robot.getStepMode() == true || Button.ENTER.isDown()) {
			// go back to command loop after each search step or if enter is
			// pressed
			robot.changeState(StateCommand.getInstance());
		}

		if (robot.canFound && robot.platformFound) {
			robot.setGridDone(true);
			if (!robot.canHeld) {
				robot.changeState(StateFindCan.getInstance());
				return;
			} else {
				robot.changeState(StateFindPlatform.getInstance());
				return;
			}
		} else {
			Robot.playTone(440, 100);
			robot.sleep(100);
			Robot.playTone(220, 200);
			robot.sleep(200);
			if (StateCommand.getInstance().getCommandLoopRunning() == true) {
				// Command loop is running; get the next command
				robot.changeState(StateCommand.getInstance());
				return;
			} else {
				robot.changeState(StateExit.getInstance());
			}
		}
	}

	private void updateSquares(Robot robot) {
		up = robot.map.grid[robot.getX()][robot.getY() + 1];
		upRight = robot.map.grid[robot.getX() + 1][robot.getY() + 1];
		right = robot.map.grid[robot.getX() + 1][robot.getY()];
		downRight = robot.map.grid[robot.getX() + 1][robot.getY() - 1];
		down = robot.map.grid[robot.getX()][robot.getY() - 1];
		downLeft = robot.map.grid[robot.getX() - 1][robot.getY() - 1];
		left = robot.map.grid[robot.getX() - 1][robot.getY()];
		upLeft = robot.map.grid[robot.getX() - 1][robot.getY() + 1];
	}

	public void exit(Robot robot) {
		robot.printMap();
		debugln("State GridRunNew exit");
	}
}
