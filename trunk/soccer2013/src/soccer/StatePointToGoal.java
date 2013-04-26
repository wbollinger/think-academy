package soccer;

import lejos.nxt.Button;

public class StatePointToGoal extends State {

	private static StatePointToGoal instance = new StatePointToGoal();
	long time;
	long elapsedTime;

	@Override
	public void enter(Robot bot) {
		bot.io.debugln("Entered StatePointToGoal");
		bot.setPower(100);
		time = System.currentTimeMillis();
		
	}

	@Override
	public void execute(Robot bot) {
		time = System.currentTimeMillis();
		elapsedTime = System.currentTimeMillis() - time;
		
		if (Button.ENTER.isDown() && bot.io.getUseCommands()) { // break to stateCommand if commands enabled
			bot.changeState(StateCommand.getInstance());
			return;
		}

		bot.nav.pointToHeadingArc(bot.nav.ENEMY_GOAL);
		debugln("Facing Enemy Goal");
		//bot.nav.pointToGoal();

		//debugln("Fired");
		//bot.fireSolenoid(); SOLENOID BROKEN
		
		if(bot.arduino.getDisBall() < 4) {
			bot.nav.moveDir(95);
			bot.sleep(2000);
			//bot.nav.pointToHeadingArc(bot.nav.ENEMY_GOAL);
	}
		bot.changeState(StateStriker.getInstance());

	}

	public static StatePointToGoal getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.io.debugln("Exited StatePointToGoal");
		bot.dribbler.stop();

	}
}