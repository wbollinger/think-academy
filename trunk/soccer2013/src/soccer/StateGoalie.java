package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	@Override
	public void enter(Robot bot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {
		int i = 5;
		int compass = 0;
		int IRDir = 0;
		int USY = 0;
		int USLeft = 0;
		int USRight = 0;
		

		while (Button.ENTER.isUp()) {
			USLeft = bot.arduino.getDisXLeft();
			USRight = bot.arduino.getDisXRight();
			
			bot.EIR.update();
			if (i == 5) {
				compass = (int) bot.compass.getDegrees();
				USY = bot.arduino.getDisYBack();
				i = 0;
			} else {
				i++;
			}

			if (!(compass + 5 > Navigator.ALLY_GOAL && compass - 5 < Navigator.ALLY_GOAL)) {
				bot.nav.pointToHeading((float) Navigator.ALLY_GOAL);
			}

			if (USY > 20) {
				while (bot.arduino.getDisYBack() > 20) {
					bot.nav.moveDir(270);
				}
				bot.floatAll();
			} else if (USY < 15) {
				while (bot.arduino.getDisYBack() < 15) {
					bot.nav.moveDir(90);
				}
				bot.floatAll();
			}

			IRDir = bot.EIR.getDir();

			if ((IRDir == 5) || (IRDir == 0)) {
				bot.stopAll();
			} else if ((IRDir < 5) && (bot.nav.normalizeMeasurement(USLeft) > 62)) {
				// moves left, unless at edge of goal
				bot.io.debugln("Left:" + bot.nav.normalizeMeasurement(USLeft)
						+ ":" + USLeft);
				bot.nav.moveDir(180);
			} else if ((IRDir > 5) && (bot.nav.normalizeMeasurement(USRight) > 62)) {
				// moves right, unless at edge of goal
				bot.io.debugln("Right:" + bot.nav.normalizeMeasurement(USRight)
						+ ":" + USRight);
				bot.nav.moveDir(0);
			} else{
				bot.stopAll();
			}
		}

		bot.changeState(StateCommand.getInstance());

	}

	public static StateGoalie getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.stopAll();
		// TODO Auto-generated method stub

	}

}
