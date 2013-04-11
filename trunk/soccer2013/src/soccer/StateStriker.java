package soccer;

import lejos.nxt.Button;

public class StateStriker extends State {

	private static StateStriker instance = new StateStriker();

	public void enter(Robot robot) {
		debugln("Entered StateStriker");
	}

	// --------------------------------------------------------------------------------------
	public void execute(Robot bot) {
		// moveForward will be changed to kick.
		bot.arduino.update();

		if (Button.ENTER.isDown()) {
			bot.changeState(StateCommand.getInstance());
			return;
		}

		// getDisY will be getUSBall
		if (!(bot.arduino.getDisBall() < 10)) {
			bot.changeState(StateFollowBall.getInstance());
			return;
		}
		
		if(bot.arduino.getDisBall() < 10) {
			bot.changeState(StatePointToGoal.getInstance());
		}
	}

	public static StateStriker getInstance() {
		return instance;
	}

	public void exit(Robot robot) {
		debugln("Exited StateStriker");
	}

}
