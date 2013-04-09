package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();
	
	private static enum ZONE {
		LEFT,
		MID_LEFT,
		MIDDLE,
		MID_RIGHT,
		RIGHT
	}
	
	ZONE currentZone;

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
			
			if((bot.arduino.getDisXLeft()+bot.arduino.getDisXRight()) > 150) {
				
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

			if ((bot.arduino.getDisYBack() > 20)&&!(false)) {
				while (bot.arduino.getDisYBack() > 20) {
					bot.nav.moveDir(270);
				}
				bot.floatAll();
			} else if (bot.arduino.getDisYBack() < 15) {
				while (bot.arduino.getDisYBack() < 15) {
					bot.nav.moveDir(90);
				}
				bot.floatAll();
			}

			IRDir = bot.EIR.getDir();

			if ((IRDir == 5) || (IRDir == 0)) {
				bot.stopAll();
			} else if ((IRDir < 5) && (bot.nav.normalizeMeasurement(bot.arduino.getDisXLeft()) > 62)) {
				// moves left, unless at edge of goal
				bot.io.debugln("Left:" + bot.nav.normalizeMeasurement(bot.arduino.getDisXLeft())
						+ ":" + bot.arduino.getDisXLeft());
				bot.nav.moveDir(180);
			} else if ((IRDir > 5) && (bot.nav.normalizeMeasurement(bot.arduino.getDisXRight()) > 62)) {
				// moves right, unless at edge of goal
				bot.io.debugln("Right:" + bot.nav.normalizeMeasurement(bot.arduino.getDisXRight())
						+ ":" + bot.arduino.getDisXRight());
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
