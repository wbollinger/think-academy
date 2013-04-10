
package soccer;

import lejos.nxt.Button;

public class StateGoalieReposition extends State {

	private static StateGoalieReposition instance = new StateGoalieReposition();

	@Override
	public void enter(Robot bot) {
	debugln("Entered stategoaliereposition");
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot bot) {
		
		while (Button.ENTER.isUp()) {
			bot.arduino.update();
			bot.EIR.update();

			if ((bot.arduino.getDisYBack() > 20)) {
				debugln("y is greater than 20");
				
				bot.nav.moveDir(270);
				bot.arduino.update();
					
				bot.floatAll();
			} else if (bot.arduino.getDisYBack() < 15) {

				bot.nav.moveDir(90);
				bot.arduino.update();
					
				bot.floatAll();
			} else if((bot.arduino.getLightLeft() < bot.WHITE_VALUE) && (bot.arduino.getLightRight() < bot.WHITE_VALUE)) {
				 debugln("hit line");
				bot.stopAll();
				break;
			} else if (bot.arduino.getLightLeft() < bot.WHITE_VALUE) {
				 debugln("hit line");
				 bot.stopAll();
					break;
			} else if (bot.arduino.getLightRight() < bot.WHITE_VALUE) {
				 debugln("hit line");
				 bot.stopAll();
					break;
			} else if(bot.arduino.getDisYBack() < 20 && bot.arduino.getDisYBack() > 15){
				bot.changeState(StateCommand.getInstance());
			}

		}

		bot.changeState(StateGoalie.getInstance());

	}

	public static StateGoalieReposition getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.stopAll();
		// TODO Auto-generated method stub

	}

}