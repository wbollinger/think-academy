import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class StateFindCan extends State {

	private static StateFindCan instance = new StateFindCan();

	private StateFindCan() {
	}

	public static StateFindCan getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debugln("StFindCan enter");
		if(!robot.gridDone) {
			debugln("ERROR: LACKING NECESSARY DATA");
			debugln("BREAKING TO COMMAND STATE");
			robot.changeState(StateCommand.getInstance());
			return;
		}
	}

	public void execute(Robot robot) {
		debugln("StFindCan execute");
		
		int dist;
		
		robot.followPath(Map2D.CAN);
		dist = robot.sweepCan();
		robot.forward(dist);
		robot.liftCan();
		robot.backward(dist);
		robot.changeState(StateFindPlatform.getInstance());
	}

	public void exit(Robot robot) {
		debugln("StFindCan exit");
	}

}
