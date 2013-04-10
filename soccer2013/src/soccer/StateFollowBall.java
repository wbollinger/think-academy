package soccer;

import lejos.nxt.Button;

public class StateFollowBall extends State {

	private static StateFollowBall instance = new StateFollowBall();

	@Override
	public void enter(Robot bot) {
		bot.io.debugln("Entered StateFollowBall");
	}

	@Override
	public void execute(Robot bot) {
		if (Button.ENTER.isDown()) {
			bot.changeState(StateCommand.getInstance());
			return;
		}
		bot.followBall();
		bot.changeState(StatePointToGoal.getInstance());
	}

	public static StateFollowBall getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.io.debugln("Exited StateFollowBall");

	}

}