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
//		if(!robot.isGridDone()) {
//			debugln("ERROR: LACKING NECESSARY DATA");
//			debugln("BREAKING TO COMMAND STATE");
//			robot.changeState(StateCommand.getInstance());
//			return;
//		}
	}

	public void execute(Robot robot) {
		debugln("StFindCan execute");
		
		robot.followPath(Map2D.CAN);
		robot.debugln("Can Path Followed");
		debugln("Facing can");
		robot.findCanCoarseSonic();
		robot.correctRight(180);
		int diff = robot.finalCanDist - 15;
		debugln("Diff: "+diff);
		if(diff > 0){
			robot.backward(diff);
		} else {
			robot.forward(diff);
		}
//		robot.backward(9);
		robot.findCanCoarseEOPD();
		robot.debugln("Can Swept");
		robot.canSequence();
		robot.debugln("Can Lifted");
		robot.canHeld = true;
		robot.map.removeObject(Map2D.CAN);
		robot.sleep(100);
		robot.forward(diff+2);
		robot.changeState(StateGridRunNew.getInstance());
	}

	public void exit(Robot robot) {
		debugln("StFindCan exit");
	}

}
