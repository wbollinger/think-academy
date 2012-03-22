import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class StateFindPlatform extends State {

	private static StateFindPlatform instance = new StateFindPlatform();

	private StateFindPlatform() {
	}

	public static StateFindPlatform getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debugln("StFindPlatform enter");
		if(!robot.gridDone) {
			debugln("ERROR: LACKING NECESSARY DATA");
			debugln("BREAKING TO COMMAND STATE");
			robot.changeState(StateCommand.getInstance());
			return;
		}
	}

	public void execute(Robot robot) {
		debugln("StFindPlatform execute");
		robot.followPath(Map2D.PLATFORM);
		
		robot.facePlatform();
		
		while(!robot.touch.isPressed()) {
			robot.forward();
		}
		
		robot.dropCan();
		robot.backward(50);
		
	}

	public void exit(Robot robot) {
		debugln("StFindPlatform exit");
	}

}
