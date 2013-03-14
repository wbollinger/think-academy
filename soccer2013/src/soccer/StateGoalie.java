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

		bot.io.debugln("" + heading);
		while (Button.ENTER.isUp()) {

			if (bot.USY.getDistance() > 20
					&& bot.compass.getDegrees() + 5 > heading
					&& bot.compass.getDegrees() - 5 < heading) {
				while (bot.USY.getDistance() > 20) {
					bot.nav.moveDir(270);
				}
				bot.floatAll();
			} else if (bot.USY.getDistance() < 15
					&& bot.compass.getDegrees() + 5 > heading
					&& bot.compass.getDegrees() - 5 < heading) {
				while (bot.USY.getDistance() < 15) {
					bot.nav.moveDir(90);
				}
				bot.floatAll();
			}

			if (i > 5) {
				bot.nav.pointToHeading((float) heading);
				i = 0;
			} else {
				i++;
			}

			if ((bot.IR.getDirection() == 5) || (bot.IR.getDirection() == 0)) {
				bot.stopAll();
			} else if ((bot.IR.getDirection() < 5)
					|| (bot.nav.normalizeMeasurement(bot.USX.getDistance()) < 110)) {
				// moves left, unless at edge of goal
				bot.io.debugln("Left");
				bot.nav.moveDir(180);
			} else if ((bot.IR.getDirection() > 5)
					|| (bot.nav.normalizeMeasurement(bot.USX.getDistance()) > 64)) {
				// moves right, unless at edge of goal
				bot.io.debugln("Right");
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
