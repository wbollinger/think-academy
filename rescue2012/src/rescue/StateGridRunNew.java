package rescue;
//------------------------------------------------------------------------
//  robot Grid Run state - search Room 3
//------------------------------------------------------------------------
import lejos.nxt.*;

public class StateGridRunNew extends State {

	static private StateGridRunNew instance = new StateGridRunNew();

	int objectFound = 0;
	boolean canFound = false;
	boolean platformFound = false;

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
			

			robot.goUpRight();
			robot.faceDir(270);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
			}
			
			robot.faceDir(180);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
			}
			robot.faceDir(135);
			if(robot.checkForPlatform()&&(!platformFound)) {
				platformFound = true;
			}
			robot.faceDir(90);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
			}
			robot.goRight();
			
			up = robot.map.grid[robot.getX()][robot.getY() + 1];
			upRight = robot.map.grid[robot.getX() + 1][robot.getY() + 1];
			right = robot.map.grid[robot.getX() + 1][robot.getY()];
			downRight = robot.map.grid[robot.getX() + 1][robot.getY() - 1];
			down = robot.map.grid[robot.getX()][robot.getY() - 1];
			downLeft = robot.map.grid[robot.getX() - 1][robot.getY() - 1];
			left = robot.map.grid[robot.getX() - 1][robot.getY()];
			upLeft = robot.map.grid[robot.getX() - 1][robot.getY() + 1];
			
			robot.faceDir(270);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
			}
			robot.faceDir(315);
			if(robot.checkForPlatform()&&(!platformFound)) {
				platformFound = true;
			}
			robot.faceDir(0);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
			}
			robot.faceDir(45);
			if(robot.checkForPlatform()&&(!platformFound)) {
				platformFound = true;
			}
			robot.faceDir(90);
			if(robot.isCanInSquare()&&(!canFound)) {
				canFound = true;
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