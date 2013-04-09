package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	private static enum ZONE {
		LEFT, MID_LEFT, MIDDLE, MID_RIGHT, RIGHT
	}

	private static enum DIRECTION {
		LEFT, RIGHT, STOPPED
	}

	ZONE currentZone;
	DIRECTION currentDirection;

	@Override
	public void enter(Robot bot) {
		currentZone = ZONE.MIDDLE;
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {
		int i = 5;
		int compass = 0;
		int IRDir = 0;

		while (Button.ENTER.isUp()) {
			bot.arduino.update();
			bot.EIR.update();

			if ((bot.arduino.getDisXLeft() + bot.arduino.getDisXRight()) > 150) { // if both pings are reading correctly
				currentZone = ZONE.MIDDLE;
			} else if (currentDirection == DIRECTION.LEFT) {
				if (bot.arduino.getDisXLeft() < 40) {
					currentZone = ZONE.LEFT;
				} else {
					currentZone = ZONE.MID_LEFT;
				}
			} else if (currentDirection == DIRECTION.RIGHT) {
				if (bot.arduino.getDisXRight() < 40) {
					currentZone = ZONE.RIGHT;
				} else {
					currentZone = ZONE.MID_RIGHT;
				}
			}

			if (i == 5) {
				compass = (int) bot.compass.getDegrees();
				i = 0;
			} else {
				i++;
			}

			if (!(compass + 5 > Navigator.ENEMY_GOAL && compass - 5 < Navigator.ENEMY_GOAL)) {
				bot.nav.pointToHeading((float) Navigator.ENEMY_GOAL);
			}

			if ((bot.arduino.getDisYBack() > 20)
					&& !((currentZone == ZONE.LEFT) || (currentZone == ZONE.RIGHT))) {
				while (bot.arduino.getDisYBack() > 20) {
					bot.nav.moveDir(270);
					bot.arduino.update();
				}
				bot.floatAll();
			} else if (bot.arduino.getDisYBack() < 15) {
				while (bot.arduino.getDisYBack() < 15) {
					bot.nav.moveDir(90);
					bot.arduino.update();
				}
				bot.floatAll();
			}
			 if((bot.arduino.getLightLeft() > bot.WHITE_VALUE) && (bot.arduino.getLightRight() > bot.WHITE_VALUE)) {
				bot.nav.moveDir(270);
				bot.sleep(1000);
			} else if (bot.arduino.getLightLeft() > bot.WHITE_VALUE) {
				bot.nav.moveDir(340);
				bot.sleep(200);
			} else if (bot.arduino.getLightRight() > bot.WHITE_VALUE) {
				bot.nav.moveDir(180);
				bot.sleep(200);
			}

			IRDir = bot.EIR.getDir();

			if ((IRDir == 5) || (IRDir == 0)) {
				bot.stopAll();
				currentDirection = DIRECTION.STOPPED;
			} else if ((IRDir < 5)
					&& (bot.nav.normalizeMeasurement(bot.arduino.getDisXLeft()) > 62)) {
				// moves left, unless at edge of goal
				// bot.io.debugln("Left:" +
				// bot.nav.normalizeMeasurement(bot.arduino.getDisXLeft())
				// + ":" + bot.arduino.getDisXLeft());
				bot.nav.moveDir(180);
				currentDirection = DIRECTION.LEFT;
			} else if ((IRDir > 5)
					&& (bot.nav
							.normalizeMeasurement(bot.arduino.getDisXLeft()) > 62)) {
				// moves right, unless at edge of goal
				// bot.io.debugln("Right:" +
				// bot.nav.normalizeMeasurement(bot.arduino.getDisXRight())
				// + ":" + bot.arduino.getDisXRight());
				bot.nav.moveDir(0);
				currentDirection = DIRECTION.RIGHT;
			} else {
				bot.stopAll();
				currentDirection = DIRECTION.STOPPED;
			}

			debugln("" + currentZone + ": " + currentDirection);

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
