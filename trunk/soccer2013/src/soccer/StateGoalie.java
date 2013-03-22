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
		int compass = (int) bot.compass.getDegrees();;
		int IRDir;
		int USY = bot.USY.getDistance();
		int USX;

		while (Button.ENTER.isUp()) {
			bot.EIR.update();
			if(i == 5) {
				compass = (int) bot.compass.getDegrees();
				USY = bot.USY.getDistance();
				i = 0;
			} else {
				i++;
			}
			

			if (!(compass + 5 > Navigator.ENEMY_GOAL_HEADING && compass - 5 < Navigator.ENEMY_GOAL_HEADING)) {
				bot.nav.pointToHeading((float) Navigator.ENEMY_GOAL_HEADING);
			}

			if (USY > 30) {
				while (bot.USY.getDistance() > 30) {
					bot.nav.moveDir(270);
				}
				bot.floatAll();
			} else if (bot.USY.getDistance() < 25) {
				while (bot.USY.getDistance() < 25) {
					bot.nav.moveDir(90);
				}
				bot.floatAll();
			}

			IRDir = bot.EIR.getDir();

			if ((IRDir == 5) || (IRDir == 0)) {
				bot.stopAll();
			} else if ((IRDir < 5)
					&& (bot.nav.normalizeMeasurement(bot.getUSX()) < 106)) {
				// moves left, unless at edge of goal
				// bot.io.debugln("Left");
				bot.nav.moveDir(180);
			} else if ((IRDir > 5)
					&& (bot.nav.normalizeMeasurement(bot.getUSX()) > 64)) {
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
