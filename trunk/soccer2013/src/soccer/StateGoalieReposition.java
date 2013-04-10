package soccer;

import lejos.nxt.Button;

public class StateGoalieReposition extends State {

	private static StateGoalieReposition instance = new StateGoalieReposition();

	int compass;
	
	@Override
	public void enter(Robot bot) {
		debugln("Entered stategoaliereposition");
		// TODO Auto-generated method stub
		compass = 0;
	}

	@Override
	public void execute(Robot bot) {

		compass = (int) bot.compass.getDegrees();
		bot.arduino.update();
		bot.EIR.update();
		
		if (!(compass + 5 > Navigator.ENEMY_GOAL && compass - 5 < Navigator.ENEMY_GOAL)) {
			bot.nav.pointToHeading((float) Navigator.ENEMY_GOAL);
		}

		if ((bot.arduino.getDisYBack() > 20)) {
			debugln("y is greater than 20");
			bot.nav.moveDir(270);
			
		} else if (bot.arduino.getDisYBack() < 15) {
			bot.nav.moveDir(90);
			
		} else if ((bot.arduino.getLightLeft() < bot.WHITE_VALUE)
				&& (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
			debugln("hit line");
			bot.stopAll();
		} else if (bot.arduino.getLightLeft() < bot.WHITE_VALUE) {
			debugln("hit line");
			bot.stopAll();
		} else if (bot.arduino.getLightRight() < bot.WHITE_VALUE) {
			debugln("hit line");
			bot.stopAll();
		} else {
			bot.changeState(StateCommand.getInstance());
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