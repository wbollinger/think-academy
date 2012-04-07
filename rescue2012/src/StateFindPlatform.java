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
		debugln("ajacent to platform");
		robot.faceTarget(Map2D.PLATFORM);
		
		while(!robot.touch.isPressed()) {
			robot.forward();
		}
		
		robot.dropCan();
		robot.backward(50);
		
	}

	public void exit(Robot robot) {
		//debugln("StFindPlatform exit");
	}

}
