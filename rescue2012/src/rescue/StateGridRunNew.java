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

	boolean enterGrid = true;

	private StateGridRunNew() {
	}

	// this is a singleton
	public static StateGridRunNew getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debugln("State GridRunNew enter");
		// robot.setDir(90);
		if (enterGrid) {
			robot.resetGrid();
			robot.sleep(1000);
			robot.dropClaw();
			robot.sleep(1000);
			robot.closeClaw();
			robot.sleep(2000);
			robot.openClaw();
			robot.sleep(2000);
			robot.liftClaw();
			debugln("Entergrid success");
			enterGrid = false;
		}

	}

	public void execute(Robot robot) {

		if (!robot.canFound) {
			robot.faceDownRight();
			robot.forward(8);
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY() - 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.goRight();
				robot.goRight();
				robot.goDown();
				robot.faceLeft();
				robot.changeState(StateFindCan.getInstance());
				return;
			}
			robot.backward(8);
		}

		robot.goTo(2, 2);
		robot.printMap();
		// debugln("Updating squares");
		updateSquares(robot);
		// debugln("Squares updated");

		if (!robot.canFound) {
			robot.faceDown();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX()][robot.getY() - 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}
		
		if (!robot.platformFound) {
			robot.faceDownLeft();
			if (robot.checkForPlatformUS()) {
				robot.platformFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY() - 1] = 3;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				if(robot.canHeld){
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			} else {
				debugln("Platform Not Found");
			}
		}

		if (!robot.canFound) {
			robot.faceLeft();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() - 1][robot.getY()] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		if (!robot.canFound) {
			robot.faceUp();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX()][robot.getY() + 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		if (!robot.canFound) {
			robot.faceRight();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY()] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		if ((!robot.canFound) || (!robot.platformFound)) {
			robot.goTo(3, 2);
			updateSquares(robot);
		}

		if (!robot.canFound) {
			robot.faceUp();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX()][robot.getY() + 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		if (!robot.platformFound) {
			robot.faceUpRight();
			if (robot.checkForPlatformUS()) {
				robot.platformFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY() + 1] = 3;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				if(robot.canHeld){
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			} else {
				debugln("Platform Not Found");
			}
		}

		if (!robot.canFound) {
			robot.faceRight();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY()] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		if (!robot.platformFound) {
			robot.faceDownRight();
			if (robot.checkForPlatformUS()) {
				robot.platformFound = true;
				robot.map.grid[robot.getX() + 1][robot.getY() - 1] = 3;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				if(robot.canHeld){
					robot.changeState(StateFindPlatform.getInstance());
					return;
				}
			} else {
				debugln("Platform Not Found");
			}
		}

		if (!robot.canFound) {
			robot.faceDown();
			if (robot.isCanInSquare()) {
				robot.canFound = true;
				robot.map.grid[robot.getX()][robot.getY() - 1] = 2;
				robot.printMap();
				Robot.playTone(880, 100);
				robot.sleep(100);
				robot.changeState(StateFindCan.getInstance());
				return;
			}
		}

		// debug(robot.getX() + "	" + robot.getY()+"\n");
		robot.printMap();

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
			robot.setGridDone(true);
			if(!robot.canHeld) {
				robot.changeState(StateFindCan.getInstance());
				return;
				
			} else {
				robot.changeState(StateFindPlatform.getInstance());
				return;
			}
			
		} else if(robot.canFound&&!robot.platformFound){
			Robot.playTone(440, 100);
			robot.sleep(100);
			Robot.playTone(220, 200);
			robot.sleep(200);
			robot.changeState(StateGridRunNew.getInstance());
			return;
		} else if(!robot.canFound){
			robot.changeState(StateGridRunCornersNew.getInstance());
			return;
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
