package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	int compass;
	int centeredHeading;

	@Override
	public void enter(Robot bot) {
		debugln("Entered stategoalie");

		compass = 0;
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {

		if (Button.ENTER.isDown()) {
			debugln("Breaking into StateCommand");
			bot.changeState(StateCommand.getInstance());
			return;
		}

		bot.arduino.update();
		bot.EIR.update();
		compass = (int) bot.compass.getDegrees();

		if ((bot.arduino.getDisXLeft() + bot.arduino.getDisXRight()) > 150) {
			bot.nav.currentZone = Navigator.ZONE.MIDDLE;
			centeredHeading = 5;
		} else if (bot.nav.currentDirection == Navigator.DIRECTION.LEFT) {
			if (bot.arduino.getDisXLeft() < 50) {
				bot.nav.currentZone = Navigator.ZONE.LEFT;
				centeredHeading = 3;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_LEFT;
				centeredHeading = 4;
			}
		} else if (bot.nav.currentDirection == Navigator.DIRECTION.RIGHT) {
			if (bot.arduino.getDisXRight() < 50) {
				bot.nav.currentZone = Navigator.ZONE.RIGHT;
				centeredHeading = 7;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_RIGHT;
				centeredHeading = 6;
			}
		} else {
			bot.nav.currentZone = Navigator.ZONE.MIDDLE;
			centeredHeading = 5;
		}
		
		if ((bot.nav.currentZone != bot.nav.lastZone)
				|| (bot.nav.currentDirection != bot.nav.lastDirection)) {
			
			debugln("" + bot.nav.currentZone + ": " + bot.nav.currentDirection);
		}

		bot.nav.lastZone = bot.nav.currentZone;
		bot.nav.lastDirection = bot.nav.currentDirection;

		if (!(((compass + 7) > Navigator.ENEMY_GOAL) && ((compass - 7) < Navigator.ENEMY_GOAL))) {
			debugln("Incorrect heading: breaking to StateGoalieReposition");
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if ((bot.arduino.getDisYBack() > 25)
				&& !((bot.nav.currentZone == Navigator.ZONE.LEFT) || (bot.nav.currentZone == Navigator.ZONE.RIGHT))) {
			debugln("Too far forward: breaking to StateGoalieReposition");
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		} else if (bot.arduino.getDisYBack() < 10) {
			debugln("Too close to goal: breaking to StateGoalieReposition");
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if ((bot.arduino.getLightLeft() < bot.WHITE_VALUE)
				|| (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
			debugln("hit line");
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}
		
		debugln("IR|DIR:"+bot.EIR.getDir()+"|STR:"+bot.EIR.getStrength());
		if (bot.EIR.getDir() == 5) {
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
		} else if ((bot.EIR.getDir() < centeredHeading) && (bot.arduino.getDisXLeft() > 62)) {
			// moves left, unless at edge of goal
			bot.nav.moveDir(180);
			bot.nav.currentDirection = Navigator.DIRECTION.LEFT;
		} else if ((bot.EIR.getDir() > centeredHeading) && (bot.arduino.getDisXRight() > 62)) {
			// moves right, unless at edge of goal
			// + ":" + bot.arduino.getDisXRight());
			bot.nav.moveDir(0);
			bot.nav.currentDirection = Navigator.DIRECTION.RIGHT;
		} else if (bot.EIR.getDir() == 0) {
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
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
