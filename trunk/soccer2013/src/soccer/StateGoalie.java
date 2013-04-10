package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	int compass;

	@Override
	public void enter(Robot bot) {
		debugln("Entered stategoalie");
		bot.nav.currentZone = Navigator.ZONE.MIDDLE;
		compass = 0;
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {

		bot.arduino.update();
		bot.EIR.update();
		compass = (int) bot.compass.getDegrees();

		if ((bot.arduino.getDisXLeft() + bot.arduino.getDisXRight()) > 150) {
			bot.nav.currentZone = Navigator.ZONE.MIDDLE;
		} else if (bot.nav.currentDirection == Navigator.DIRECTION.LEFT) {
			if (bot.arduino.getDisXLeft() < 50) {
				bot.nav.currentZone = Navigator.ZONE.LEFT;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_LEFT;
			}
		} else if (bot.nav.currentDirection == Navigator.DIRECTION.RIGHT) {
			if (bot.arduino.getDisXRight() < 50) {
				bot.nav.currentZone = Navigator.ZONE.RIGHT;
			} else {
				bot.nav.currentZone = Navigator.ZONE.MID_RIGHT;
			}
		}
		
		if (!(((compass + 5) > Navigator.ENEMY_GOAL) && ((compass - 5) < Navigator.ENEMY_GOAL))) {
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if ((bot.arduino.getDisYBack() > 20)
				&& !((bot.nav.currentZone == Navigator.ZONE.LEFT) || (bot.nav.currentZone == Navigator.ZONE.RIGHT))) {
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		} else if (bot.arduino.getDisYBack() < 15) {
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if ((bot.arduino.getLightLeft() < bot.WHITE_VALUE)
				|| (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
			debugln("hit line");
			bot.changeState(StateGoalieReposition.getInstance());
			return;
		}

		if (bot.EIR.getDir() == 5) {
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
		} else if ((bot.EIR.getDir() < 5) && (bot.arduino.getDisXLeft() > 62)) {
			// moves left, unless at edge of goal
			bot.nav.moveDir(180);
			bot.nav.currentDirection = Navigator.DIRECTION.LEFT;
		} else if ((bot.EIR.getDir() > 5) && (bot.arduino.getDisXLeft() > 62)) {
			// moves right, unless at edge of goal
			// + ":" + bot.arduino.getDisXRight());
			bot.nav.moveDir(0);
			bot.nav.currentDirection = Navigator.DIRECTION.RIGHT;
		} else if(bot.EIR.getDir() == 0){
			bot.stopAll();
			bot.nav.currentDirection = Navigator.DIRECTION.STOPPED;
		}

		debugln("" + bot.nav.currentZone + ": " + bot.nav.currentDirection);

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
