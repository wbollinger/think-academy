package soccer;

import lejos.nxt.Button;

public class StatePointToGoal extends State {

	private static StatePointToGoal instance = new StatePointToGoal();
	long time;
	long elapsedTime;

	@Override
	public void enter(Robot bot) {
		bot.io.debugln("Entered StatePointToGoal");
		bot.setPower(bot.MAX_MOTOR_POWER);
		time = System.currentTimeMillis();

	}

	@Override
	public void execute(Robot bot) {
		time = System.currentTimeMillis();
		elapsedTime = System.currentTimeMillis() - time;

		if (Button.ENTER.isDown() && bot.io.getUseCommands()) { // break to
																// stateCommand
																// if commands
																// enabled
			bot.changeState(StateCommand.getInstance());
			return;
		}

		bot.nav.pointToHeadingArc(bot.nav.ENEMY_GOAL);
		debugln("Facing Enemy Goal");
		
		if (!(bot.arduino.getDisBall() < 4)) { // we don't have the ball
			bot.changeState(StateStriker.getInstance());
			return;
		}
		
		bot.nav.pointToGoal();

		if (!(bot.arduino.getDisBall() < 4)) { // we don't have the ball
			bot.changeState(StateStriker.getInstance());
			return;
		}
		
		// debugln("Fired");
		// bot.fireSolenoid(); SOLENOID BROKEN
		
		bot.nav.moveDir(95);
		bot.sleep(2000);
		// bot.nav.pointToHeadingArc(bot.nav.ENEMY_GOAL);

	}

	public static StatePointToGoal getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.io.debugln("Exited StatePointToGoal");
		//bot.dribbler.stop();

	}
}