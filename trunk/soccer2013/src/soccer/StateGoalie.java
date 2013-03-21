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
		int i = 0;
		double heading = bot.compass.getDegrees();
		int compass = (int) bot.compass.getDegrees();;
		int IRDir;
		int USY = bot.USY.getDistance();
		int USX;

		bot.io.debugln("" + heading);
		while (Button.ENTER.isUp()) {
			if(i == 5) {
				compass = (int) bot.compass.getDegrees();
				i = 0;
			} else {
				i++;
			}
			

			if (!(compass + 5 > heading && compass - 5 < heading)) {
				bot.nav.pointToHeading((float) heading);
			}

			if (USY > 20) {
				while (bot.USY.getDistance() > 20) {
					bot.nav.moveDir(270);
				}
				bot.floatAll();
			} else if (bot.USY.getDistance() < 15) {
				while (bot.USY.getDistance() < 15) {
					bot.nav.moveDir(90);
				}
				bot.floatAll();
			}

			IRDir = bot.IR.getDirection();

			if ((IRDir == 5) || (IRDir == 0)) {
				bot.stopAll();
			} else if ((IRDir < 5)
					&& (bot.nav.normalizeMeasurement(bot.USX.getDistance()) < 106)) {
				// moves left, unless at edge of goal
				// bot.io.debugln("Left");
				bot.nav.moveDir(180);
			} else if ((IRDir > 5)
					&& (bot.nav.normalizeMeasurement(bot.USX.getDistance()) > 75)) {
				// moves right, unless at edge of goal
				// bot.io.debugln("Right");
				bot.nav.moveDir(0);
			}
		}
		bot.changeState(StateCommand.getInstance());

	}

	public static StateGoalie getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot r) {
		// TODO Auto-generated method stub

	}

}
