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
				robot.goUp();
				robot.sleep(1000);
			} else if (upRight == 0) {
				robot.goUpRight();
				robot.sleep(1000);
			} else if (right == 0) {
				robot.goRight();
				robot.sleep(1000);
			} else if (downRight == 0) {
				robot.goDownRight();
				robot.sleep(1000);
			} else if (down == 0) {
				robot.goDown();
				robot.sleep(1000);
			} else if (downLeft == 0) {
				robot.goDownLeft();
				robot.sleep(1000);
			} else if (left == 0) {
				robot.goLeft();
				robot.sleep(1000);
			} else if (upLeft == 0) {
				robot.goLeft();
				robot.sleep(1000);
			}

			// debug(robot.getX() + "	" + robot.getY()+"\n");
			robot.printMap();
		}

		if (objectFound > 1) {
			// Goal accomplished
			Sound.playTone(440, 100);
			robot.sleep(100);
			Sound.playTone(550, 100);
			robot.sleep(100);
			Sound.playTone(440, 100);
			robot.sleep(100);
			Sound.playTone(330, 100);
			robot.sleep(100);
			//robot.changeState(StateExit.getInstance());
		}
		
		if (robot.getStepMode() == true) {
			// go back to command loop after each search step
			robot.changeState(StateCommand.getInstance());
		} else {
			robot.gridDone = true;
			robot.changeState(StateFindCan.getInstance());
		}

	}

	public void exit(Robot robot) {
	}
}
