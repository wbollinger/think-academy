package soccer;

import lejos.nxt.Button;

public class StateFollowBall extends State {

	private static StateFollowBall instance = new StateFollowBall();

	@Override
	public void enter(Robot bot) {
		bot.io.debugln("Entered StateFollowBall");
		bot.dribbler.forward();
	}

	@Override
	public void execute(Robot bot) {
		if (Button.ENTER.isDown()) {
			bot.dribbler.stop();
			bot.changeState(StateCommand.getInstance());
			return;
		}
		bot.EIR.update();
		bot.arduino.update();
		bot.io.debugln("IR direction value is:" + bot.EIR.getDir(), 0x01);
		bot.io.debugln("IR strength value is:" + bot.EIR.getStrength(), 0x01);
		bot.io.debugln("Left light value is:" + bot.arduino.getLightLeft(), 0x04);
		bot.io.debugln("Right light value is:" + bot.arduino.getLightRight(), 0x04);
		bot.io.debugln(""+bot.arduino.getLightLeft()+":"+bot.arduino.getLightRight());
		//io.debugln("" + dir + ":" + str);
		if (bot.arduino.disBall < 4) {
			bot.stopAll();
			bot.moveForward(500);
			bot.changeState(StatePointToGoal.getInstance());
			return;
		} else if((bot.arduino.getLightRight() < bot.WHITE_VALUE) && (bot.arduino.getLightLeft() < bot.WHITE_VALUE)) {
			bot.nav.moveDir(270);
			bot.sleep(1000);
		} else if (bot.arduino.getLightRight() < bot.WHITE_VALUE) {
			bot.nav.moveDir(340);
			bot.sleep(200);
		} else if (bot.arduino.getLightLeft() < bot.WHITE_VALUE) {
			bot.nav.moveDir(180);
			bot.sleep(200);
		} else {

			if (bot.EIR.getDir() == 5) {
				bot.moveForward();
			} else if (bot.EIR.getDir() < 5) {
				bot.turnLeft();
			} else if (bot.EIR.getDir() > 5) {
				bot.turnRight();
			} else {
				bot.stopAll();
			}
		}
	}

	public static StateFollowBall getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.io.debugln("Exited StateFollowBall");
		bot.stopAll();

	}

}