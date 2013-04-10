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
		if(Button.ENTER.isDown()){
		bot.changeState(StateCommand.getInstance());
		return;
		}
		bot.nav.pointToHeadingArc(Navigator.ENEMY_GOAL);
		bot.nav.pointToGoal();
		
		bot.changeState(StateFollowBall.getInstance());
		
	}

	public static StatePointToGoal getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot bot) {
		bot.io.debugln("Exited StatePointToGoal");
	
	}
}