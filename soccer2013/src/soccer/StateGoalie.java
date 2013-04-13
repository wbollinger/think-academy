package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	int compass;
	int centeredHeading;

	@Override
	public void enter(Robot bot) {
		debugln("Entered stategoalie", 0x80);

		compass = 0;
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {

		if (Button.ENTER.isDown()) {
			debugln("Breaking into StateCommand", 0x80);
			bot.changeState(StateCommand.getInstance());
			return;
		}

		bot.arduino.update();
		bot.EIR.update();
		compass = (int) bot.compass.getDegrees();
		
		debugln("IR|DIR:" + bot.EIR.getDir() + "|STR:" + bot.EIR.getStrength(),
				0x01);
		if (bot.EIR.getDir() == centeredHeading || bot.EIR.getDir() == 5) {
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
		} else if ((bot.EIR.getDir() < centeredHeading || bot.EIR.getDir() < 5)
				&& (bot.arduino.getDisXLeft() > 62)) {
			// moves left, unless at edge of goal
			bot.nav.moveDir(180);
			bot.nav.currentDirection = Navigator.DIRECTION.LEFT;
		} else if ((bot.EIR.getDir() > centeredHeading || bot.EIR.getDir() > 5)
				&& (bot.arduino.getDisXRight() > 62)) {
			// moves right, unless at edge of goal
			// + ":" + bot.arduino.getDisXRight());
			bot.nav.moveDir(0);
			bot.nav.currentDirection = Navigator.DIRECTION.RIGHT;
		} else if (bot.EIR.getDir() == 0) {
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
		}

		if ((Math.abs(bot.arduino.getDisXLeft() - bot.arduino.getDisXRight()) < 40)
				&& ((bot.arduino.getDisXLeft() + bot.arduino.getDisXRight()) > 150)) {
			bot.nav.currentZone = Navigator.ZONE.MIDDLE;
			centeredHeading = 5;
		} else if ((bot.nav.currentDirection == Navigator.DIRECTION.LEFT)
				&& ((bot.nav.lastZone == Navigator.ZONE.MIDDLE)
						|| (bot.nav.lastZone == Navigator.ZONE.MID_LEFT) || (bot.nav.lastZone == Navigator.ZONE.LEFT))) {
			if (bot.arduino.getDisXLeft() < 60) {
				bot.nav.currentZone = Navigator.ZONE.LEFT;
				centeredHeading = 3;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_LEFT;
				centeredHeading = 4;
			}
		} else if (bot.nav.currentDirection == Navigator.DIRECTION.RIGHT
				&& ((bot.nav.lastZone == Navigator.ZONE.MIDDLE)
						|| (bot.nav.lastZone == Navigator.ZONE.MID_RIGHT) || (bot.nav.lastZone == Navigator.ZONE.RIGHT))) {
			if (bot.arduino.getDisXRight() < 60) {
				bot.nav.currentZone = Navigator.ZONE.RIGHT;
				centeredHeading = 7;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_RIGHT;
				centeredHeading = 6;
			}
		}

		if ((bot.nav.currentZone != bot.nav.lastZone)
				|| (bot.nav.currentDirection != bot.nav.lastDirection)) {

			debugln("" + bot.nav.currentZone + ": " + bot.nav.currentDirection,
					0x10);
		}

		bot.nav.lastZone = bot.nav.currentZone;
		bot.nav.lastDirection = bot.nav.currentDirection;

		if (!(((compass + 9) > bot.nav.ENEMY_GOAL) && ((compass - 9) < bot.nav.ENEMY_GOAL))) {
			debugln("Incorrect heading: breaking to StateGoalieReposition",
					0x40);
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}
		
//		if (!((compass + 9) > bot.nav.ENEMY_GOAL)) {
//			debugln("Incorrect heading: breaking to StateGoalieReposition",
//					0x40);
//			bot.changeState(StateGoalieReposition.getInstance());
//			return;
//		} else if(((compass - 9) < bot.nav.ENEMY_GOAL)){
//			
//		}
		
		if ((bot.arduino.getDisYBack() > 22)
				&& !((bot.nav.currentZone == Navigator.ZONE.LEFT) || (bot.nav.currentZone == Navigator.ZONE.RIGHT))) {
			debugln("Too far forward: breaking to StateGoalieReposition", 0x40);
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		} else if (bot.arduino.getDisYBack() < 10) {
			debugln("Too close to goal: breaking to StateGoalieReposition",
					0x40);
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if ((bot.arduino.getLightLeft() < bot.WHITE_VALUE)
				|| (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
			debugln("hit line", 0x40);
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

	}

	// bot.changeState(StateCommand.getInstance());

	public static StateGoalie getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.stopAll();
		// TODO Auto-generated method stub

	}

}
