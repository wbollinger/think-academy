//------------------------------------------------------------------------
//  robot Grid Run state - search Room 3
//------------------------------------------------------------------------
import lejos.nxt.*;

public class StateGridRun extends State {

	static private StateGridRun instance = new StateGridRun();

	int objectFound = 0;

	int up;
	int upRight;
	int right;
	int downRight;
	int down;
	int downLeft;
	int left;
	int upLeft;

	private StateGridRun() {
	}

	// this is a singleton
	public static StateGridRun getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		robot.setNewNorth();
	}

	public void execute(Robot robot) {
		// *
		{
			up = robot.map.grid[robot.getX()][robot.getY() + 1];
			upRight = robot.map.grid[robot.getX() + 1][robot.getY() + 1];
			right = robot.map.grid[robot.getX() + 1][robot.getY()];
			downRight = robot.map.grid[robot.getX() + 1][robot.getY() - 1];
			down = robot.map.grid[robot.getX()][robot.getY() - 1];
			downLeft = robot.map.grid[robot.getX() - 1][robot.getY() - 1];
			left = robot.map.grid[robot.getX() - 1][robot.getY()];
			upLeft = robot.map.grid[robot.getX() - 1][robot.getY() + 1];

			//debugln("" + up + " " + upRight + " " + right + " " + downRight + " " + 
			// + " " + downLeft + " " + left + " " + upLeft);
			
			if (up == 0) {
				objectFound += robot.goUp();
				robot.sleep(1000);
			} else if (upRight == 0) {
				objectFound += robot.goUpRight();
				robot.sleep(1000);
			} else if (right == 0) {
				objectFound += robot.goRight();
				robot.sleep(1000);
			} else if (downRight == 0) {
				objectFound += robot.goDownRight();
				robot.sleep(1000);
			} else if (down == 0) {
				objectFound += robot.goDown();
				robot.sleep(1000);
			} else if (downLeft == 0) {
				objectFound += robot.goDownLeft();
				robot.sleep(1000);
			} else if (left == 0) {
				objectFound += robot.goLeft();
				robot.sleep(1000);
			} else if (upLeft == 0) {
				objectFound += robot.goLeft();
				robot.sleep(1000);
			}

			// debug(robot.getX() + "	" + robot.getY()+"\n");
			robot.printMap();
		}
		
		if (robot.map.grid[1][1] == Map2D.CAN){
			robot.map.grid[1][1] = Map2D.PLATFORM;
		} else if (robot.map.grid[1][Map2D.ROWS-2] == Map2D.CAN) {
			robot.map.grid[1][Map2D.ROWS-2] = Map2D.PLATFORM;
		} else if (robot.map.grid[Map2D.COLS-1][Map2D.ROWS-2] == Map2D.CAN) {
			robot.map.grid[Map2D.COLS-2][Map2D.ROWS-2] = Map2D.PLATFORM;
		} else if (robot.map.grid[Map2D.COLS-2][1] == Map2D.CAN) {
			robot.map.grid[Map2D.COLS-2][1] = Map2D.PLATFORM;
		}
		
		if (robot.getStepMode() == true || Button.ENTER.isDown()) {
			// go back to command loop after each search step or if enter is pressed
			robot.changeState(StateCommand.getInstance());
		} 
		else if (objectFound == 2) {
			robot.gridDone = true;
			robot.changeState(StateFindCan.getInstance());
		}

	}

	public void exit(Robot robot) {
		robot.printMap();
		debugln("State GridRun exit");
	}
}
