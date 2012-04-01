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
		
		robot.followPath(Map2D.CAN);
		//TODO: sweep for can
		robot.forward();
		robot.liftCan();
		robot.backward();
		robot.changeState(StateFindPlatform.getInstance());
	}

	public void exit(Robot robot) {
		debugln("StFindCan exit");
	}

}
