package soccer;

import lejos.nxt.Button;

public class StateFollowBall extends State {

	private static StateFollowBall instance = new StateFollowBall();
	int dir;
	int str;
	int left;
	int right;

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
		bot.EIR.update();
		dir = bot.EIR.getDir();
		bot.io.debugln("IR direction value is:" + dir, 0x01);
		str = bot.EIR.getStrength();
		bot.io.debugln("IR strength value is:" + str, 0x01);
		left = bot.arduino.getLightLeft();
		bot.io.debugln("Left light value is:" + left, 0x04);
		right = bot.arduino.getLightRight();
		bot.io.debugln("Right light value is:" + right, 0x04);
		bot.io.debugln(""+left+":"+right);
		//io.debugln("" + dir + ":" + str);
		if (str > 350) {
			bot.stopAll();
			bot.moveForward(300);
			return;
		} else if((right > bot.WHITE_VALUE) && (left > bot.WHITE_VALUE)) {
			bot.nav.moveDir(270);
			bot.sleep(1000);
		} else if (right > bot.WHITE_VALUE) {
			bot.nav.moveDir(340);
			bot.sleep(200);
		} else if (left > bot.WHITE_VALUE) {
			bot.nav.moveDir(180);
			bot.sleep(200);
		} else {

			if (dir == 5) {
				bot.moveForward();
			} else if (dir < 5) {
				bot.turnLeft();
			} else if (dir > 5) {
				bot.turnRight();
			} else {
				bot.stopAll();
			}
		}
	


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