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
		if (Button.ENTER.isDown() && bot.io.getUseCommands()) { // break to
																// stateCommand
																// if commands
																// enabled
			bot.dribbler.stop();
			bot.floatAll();
			bot.changeState(StateCommand.getInstance());
			return;
		}
		bot.EIR.update();
		bot.arduino.update();
		bot.io.debugln(
				"IR dir:" + bot.EIR.getDir() + ", str:" + bot.EIR.getStrength(),
				0x01);

		bot.io.debugln(
				"" + bot.arduino.getLightLeft() + ":"
						+ bot.arduino.getLightRight(), 0x04);
		// io.debugln("" + dir + ":" + str);
		if (bot.arduino.disBall < 4) {
			bot.moveForward(300);
			bot.floatAll();
			/*
			 * if (bot.io.getUseCommands()) {
			 * bot.changeState(StateCommand.getInstance()); bot.dribbler.stop();
			 * } else {
			 */
			bot.changeState(StatePointToGoal.getInstance());
			// }
			return;
		} /*
		 * else if (bot.EIR.getStrength() > 200) { bot.setPower(75); } else if
		 * ((bot.arduino.getLightRight() < bot.WHITE_VALUE) &&
		 * (bot.arduino.getLightLeft() < bot.WHITE_VALUE)) {
		 * bot.nav.moveDir(270); bot.sleep(1000); } else if
		 * (bot.arduino.getLightRight() < bot.WHITE_VALUE) {
		 * bot.nav.moveDir(340); bot.sleep(200); } else if
		 * (bot.arduino.getLightLeft() < bot.WHITE_VALUE) {
		 * bot.nav.moveDir(180); bot.sleep(200); }
		 */else {

			if (bot.EIR.getDir() == 5) {
				bot.nav.moveDir(95);
			} else if (bot.EIR.getDir() == 0) {
				bot.setPower(bot.MAX_MOTOR_POWER);
				bot.turnRight();
			} else if (bot.EIR.getDir() == 1) {

				bot.setPower(bot.MAX_MOTOR_POWER - 20);
				bot.turnLeft();
			} else if (bot.EIR.getDir() == 9) {
				bot.setPower(bot.MAX_MOTOR_POWER - 20);
				bot.turnRight();
			} else if (bot.EIR.getDir() == 2) {
				bot.setPower(bot.MAX_MOTOR_POWER - 25);
				bot.turnLeft();
			} else if (bot.EIR.getDir() == 8) {
				bot.setPower(bot.MAX_MOTOR_POWER - 25);
				bot.turnRight();
			} else if (bot.EIR.getDir() == 3) {
				bot.setPower(bot.MAX_MOTOR_POWER - 30);
				bot.turnLeft();
			} else if (bot.EIR.getDir() == 7) {
				bot.setPower(bot.MAX_MOTOR_POWER - 30);
				bot.turnRight();
			} else if (bot.EIR.getDir() == 4) {
				bot.setPower(bot.MAX_MOTOR_POWER - 35);
				bot.turnLeft();
			} else if (bot.EIR.getDir() == 6) {
				bot.setPower(bot.MAX_MOTOR_POWER - 35);
				bot.turnRight();
			}
			// debugln("" + bot.MOTOR_POWER);

			/*
			 * else if (bot.EIR.getDir() == 4) { bot.turnLeft(); } else if
			 * (bot.EIR.getDir() == 3) { bot.turnLeft(); } else if
			 * (bot.EIR.getDir() == 2) { bot.turnLeftPrecise(90); } else if
			 * (bot.EIR.getDir() == 1) { bot.turnLeftPrecise(90); } else if
			 * (bot.EIR.getDir() == 6) { bot.turnRight(); } else if
			 * (bot.EIR.getDir() == 7) { bot.turnRight(); } else if
			 * (bot.EIR.getDir() == 8) { bot.turnRightPrecise(90); } else if
			 * (bot.EIR.getDir() == 9) { bot.turnRightPrecise(90); } else {
			 * bot.nav.moveDir(95); }
			 */
		}
	}

	public static StateFollowBall getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.setPower(100);
		bot.io.debugln("Exited StateFollowBall");
		// bot.stopAll();

	}
	// If you find this put your name and the date of when you found it unless
	// someone already did. Date added 4/16/13. Date found / /

}