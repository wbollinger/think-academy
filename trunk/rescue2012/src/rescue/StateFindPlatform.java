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
		
		if(robot.map.findCoordinates(Map2D.PLATFORM)[0] <= 2) {
			robot.goTo(2, 2);
		} else {
			robot.goTo(3, 2);
		}
		debugln("adjacent to platform");
		
		robot.faceDir(robot.nav.dirTo(Map2D.PLATFORM));
		
		debugln("facing platform");
	
		robot.correctLeft(180);
		while(robot.getEOPDScaled()>10) {
			robot.backward();
		}
		robot.dropCan();
		Robot.playTone(440, 1000);
		robot.sleep(1000);
		robot.forward(50);
		robot.changeState(StateCommand.getInstance());
	}

	public void exit(Robot robot) {
		//debugln("StFindPlatform exit");
	}

}
