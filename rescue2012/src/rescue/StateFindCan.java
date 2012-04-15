package rescue;
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
		robot.faceTarget(Map2D.CAN);
		robot.debugln("Can Path Followed");
		robot.findCanCoarse();
		robot.debugln("Can Swept");
		robot.correctRight(180);
		robot.backward(5);
		robot.sleep(100);
		robot.liftCan();
		robot.debugln("Can Lifted");
		robot.sleep(100);
		robot.forward(10);
		robot.changeState(StateCommand.getInstance());
	}

	public void exit(Robot robot) {
		debugln("StFindCan exit");
	}

}
