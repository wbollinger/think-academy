package rescue;

public class StateFindPlatform extends State {

	private static StateFindPlatform instance = new StateFindPlatform();

	private StateFindPlatform() {
	}

	public static StateFindPlatform getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debugln("StFindPlatform enter");
//		if(!robot.isGridDone()) {
//			debugln("ERROR: LACKING NECESSARY DATA");
//			debugln("BREAKING TO COMMAND STATE");
//			robot.changeState(StateCommand.getInstance());
//			return;
//		}
	}

	public void execute(Robot robot) {
		debugln("StFindPlatform execute");
		
		if(robot.map.findCoordinates(Map2D.PLATFORM)[0] <= 2) {
			debugln("Coordinates found");
			robot.goTo(2, 2);
			robot.printMap();
		} else {
			robot.goTo(3, 2);
			robot.printMap();
		}
		debugln("adjacent to platform");
		
		//robot.faceDir(robot.nav.dirTo(Map2D.PLATFORM));
		
		char a = robot.map.dirTo(Map2D.PLATFORM);
		debugln(""+a);
		robot.faceAway(a);
		debugln("facing platform");
		robot.setBaseMotorSpeed(100);
		while((robot.getEOPDProcessedValue() > 88)||((!robot.motRegRight.isStalled())&&(!robot.motRegLeft.isStalled()))) {
			debugln("EOPD Val = "+robot.getEOPDProcessedValue());
			robot.backward();
		}
		robot.stop();
		robot.setBaseMotorSpeed(500);
		robot.dropCan();
		Robot.playTone(440, 1000);
		robot.sleep(1000);
		robot.forward(30);
		robot.stop();
		robot.victorySong();
		robot.changeState(StateCommand.getInstance());
	}

	public void exit(Robot robot) {
		//debugln("StFindPlatform exit");
	}

}
