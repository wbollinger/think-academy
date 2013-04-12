package soccer;

import lejos.nxt.Button;

public class StatePointToGoal extends State {

	private static StatePointToGoal instance = new StatePointToGoal();

	@Override
	public void enter(Robot bot) {
		bot.io.debugln("Entered StatePointToGoal");
	}

	@Override
	public void execute(Robot bot) {
		if (Button.ENTER.isDown()) {
			bot.changeState(StateCommand.getInstance());
			return;
		}

		bot.nav.pointToHeadingArc(Navigator.ENEMY_GOAL);
		debugln("Facing Enemy Goal");
		bot.nav.pointToGoal();
		
		if(bot.nav.getYLocation() < 50) {
			bot.fireSolenoid();
			bot.changeState(StateCommand.getInstance());
			return;
		} else {
			bot.moveForward();
		}

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