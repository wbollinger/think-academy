package rescue;

import lejos.nxt.Sound;

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
		
		while(robot.ultrasonic.getDistance()>10) {
			robot.forward();
		}
		robot.stop();
		robot.backward(10);
		robot.stop();
		robot.correctLeft(180);
		robot.backward(20);
		robot.dropCan();
		Sound.playTone(440, 1000);
		robot.sleep(1000);
		robot.forward(50);
		robot.changeState(StateCommand.getInstance());
	}

	public void exit(Robot robot) {
		//debugln("StFindPlatform exit");
	}

}
