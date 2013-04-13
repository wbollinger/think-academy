package soccer;

import lejos.nxt.Button;

public class StateGoalieReposition extends State {

	private static StateGoalieReposition instance = new StateGoalieReposition();

	int compass;

	@Override
	public void enter(Robot bot) {
		debugln("Entered stategoaliereposition", 0x80);
		// TODO Auto-generated method stub
		compass = 0;
	}

	@Override
	public void execute(Robot bot) {

		if (Button.ENTER.isDown()) {
			debugln("Breaking into StateCommand", 0x80);
			bot.changeState(StateCommand.getInstance());
			return;
		}

		compass = (int) bot.compass.getDegrees();
		bot.arduino.update();
		bot.EIR.update();

		if ((bot.arduino.getLightLeft() < bot.WHITE_VALUE)
				&& (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
			debugln("hit line", 0x40);
			bot.nav.moveDir(90);
			bot.sleep(1000);
			// if (bot.nav.currentZone == Navigator.ZONE.LEFT) {
			// bot.nav.moveDir(180);
			// bot.sleep(1000);
			// } else if (bot.nav.currentZone == Navigator.ZONE.RIGHT) {
			// bot.nav.moveDir(0);
			// bot.sleep(1000);
			// }

		} else if (bot.arduino.getLightLeft() < bot.WHITE_VALUE) {
			debugln("hit line left", 0x40);
			if (bot.nav.currentZone == Navigator.ZONE.LEFT) {
				bot.nav.moveDir(0);
				bot.sleep(1000);
			} else if (bot.nav.currentZone == Navigator.ZONE.RIGHT) {
				bot.nav.moveDir(180);
				bot.sleep(1000);
			}
		} else if (bot.arduino.getLightRight() < bot.WHITE_VALUE) {
			debugln("hit line right", 0x40);
			if (bot.nav.currentZone == Navigator.ZONE.LEFT) {
				bot.nav.moveDir(0);
				bot.sleep(1000);
			} else if (bot.nav.currentZone == Navigator.ZONE.RIGHT) {
				bot.nav.moveDir(180);
				bot.sleep(1000);
			}
		} else if (!(((compass + 5) > Navigator.ENEMY_GOAL) && ((compass - 5) < Navigator.ENEMY_GOAL))) {
			bot.nav.pointToHeading((float) Navigator.ENEMY_GOAL);
			debugln("Heading Corrected", 0x40);
		} else if ((bot.arduino.getDisYBack() > 15 && bot.nav.currentZone == Navigator.ZONE.MIDDLE)) {
			bot.nav.moveDir(270);
			debugln("Backing Up: B:" + bot.arduino.getDisYBack() + ":L:"
					+ bot.arduino.getDisXLeft() + ":R:"
					+ bot.arduino.getDisXRight(), 0x02);

		} else if (bot.arduino.getDisYBack() < 12) {
			bot.nav.moveDir(90);
			debugln("Moving Up:" + bot.arduino.getDisYBack() + ":L:"
					+ bot.arduino.getDisXLeft() + ":R:"
					+ bot.arduino.getDisXRight(), 0x02);

		} else {
			bot.changeState(StateGoalie.getInstance());
			return;
		}

	}

	// bot.changeState(StateGoalie.getInstance());

	public static StateGoalieReposition getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.stopAll();
		// TODO Auto-generated method stub

	}

}