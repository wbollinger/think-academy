package rescue;

//------------------------------------------------------------------------
//  robot Grid Run state - search Room 3
//------------------------------------------------------------------------
import lejos.nxt.*;

public class StateGridRunNew extends State {

	static private StateGridRunNew instance = new StateGridRunNew();

	int objectFound = 0;

	int up;
	int upRight;
	int right;
	int downRight;
	int down;
	int downLeft;
	int left;
	int upLeft;

	private StateGridRunNew() {
	}

	// this is a singleton
	public static StateGridRunNew getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		robot.setDir(90);
		robot.resetGrid();
	}

	public void execute(Robot robot) {
		// *
		{

			// debugln("" + up + " " + upRight + " " + right + " " + downRight +
			// " " +
			// + " " + downLeft + " " + left + " " + upLeft);

			
//			robot.goUpRight();
			robot.goTo(2,2);
			updateSquares(robot);

			
			if (!robot.canFound) {
				robot.faceDir(270);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX()][robot.getY() - 1] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}

			
			if (!robot.canFound) {
				robot.faceDir(180);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX() - 1][robot.getY()] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}
			
			if (!robot.platformFound) {
				robot.faceDir(135);
				if (robot.checkForPlatform()) {
					robot.platformFound = true;
					robot.map.grid[robot.getX() - 1][robot.getY() + 1] = 3;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}
			
			if (!robot.canFound) {
				robot.faceDir(90);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX()][robot.getY() + 1] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}

			
//			robot.goRight();
			if(!robot.canFound||!robot.platformFound){
				robot.goTo(3,2);
				updateSquares(robot);
			}

			
			if (!robot.canFound) {
				robot.faceDir(90);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX()][robot.getY() + 1] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}
			
			
			if (!robot.platformFound) {
				robot.faceDir(45);
				if (robot.checkForPlatform()) {
					robot.platformFound = true;
					robot.map.grid[robot.getX() + 1][robot.getY() + 1] = 3;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}
			
			
			if (!robot.canFound) {
				robot.faceDir(0);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX() + 1][robot.getY()] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}
			
			
			if (!robot.platformFound) {
				robot.faceDir(315);
				if (robot.checkForPlatform()) {
					robot.platformFound = true;
					robot.map.grid[robot.getX() + 1][robot.getY() - 1] = 3;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}

			
			if (!robot.canFound) {
				robot.faceDir(270);
				if (robot.isCanInSquare()) {
					robot.canFound = true;
					robot.map.grid[robot.getX()][robot.getY() - 1] = 2;
					robot.printMap();
					Robot.playTone(880, 100);
					robot.sleep(100);
				}
			}

			// debug(robot.getX() + "	" + robot.getY()+"\n");
			robot.printMap();
		}

		// if (robot.map.grid[1][1] == Map2D.CAN){
		// robot.map.grid[1][1] = Map2D.PLATFORM;
		// } else if (robot.map.grid[1][Map2D.ROWS-2] == Map2D.CAN) {
		// robot.map.grid[1][Map2D.ROWS-2] = Map2D.PLATFORM;
		// } else if (robot.map.grid[Map2D.COLS-1][Map2D.ROWS-2] == Map2D.CAN) {
		// robot.map.grid[Map2D.COLS-2][Map2D.ROWS-2] = Map2D.PLATFORM;
		// } else if (robot.map.grid[Map2D.COLS-2][1] == Map2D.CAN) {
		// robot.map.grid[Map2D.COLS-2][1] = Map2D.PLATFORM;
		// }

		if (robot.getStepMode() == true || Button.ENTER.isDown()) {
			// go back to command loop after each search step or if enter is
			// pressed
			robot.changeState(StateCommand.getInstance());
		}

		if (robot.canFound && robot.platformFound) {
			robot.gridDone = true;
			robot.changeState(StateFindCan.getInstance());
		} else {
			Robot.playTone(440, 100);
			robot.sleep(100);
			Robot.playTone(220, 200);
			robot.sleep(200);
			robot.changeState(StateCommand.getInstance());
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
		debugln("State GridRun exit");
	}
}
