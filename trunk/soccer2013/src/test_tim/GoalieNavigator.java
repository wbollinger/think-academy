package test_tim;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;
import soccer.Navigator;
import soccer.Robot;

public class GoalieNavigator extends Navigator {


	public GoalieNavigator(Robot bot) {
		super(bot);
	

	}

//	public void goalieDefault() {
//		while (true) {
//			moveDir(60);
//			bot.sleep(3000);
//			moveDir(240);
//			bot.sleep(3000);
//		}
//	}

	public void goalieAware() {
		while (true) {

			if (bot.IR.getDirection() > 0 && bot.IR.getDirection() < 5) {
				//turns left?
					moveDir(240);
			}
			if (bot.IR.getDirection() == 5) {
				bot.stopAll();
			}
			if (bot.IR.getDirection() > 5) {
				//turns right?
				moveDir(60);
			}

		}
	
	}

	public void run() {
		goalieAware();

	}
	public static void main(String[] args){
		Robot robot =  Robot.getRobot();
		GoalieNavigator test = new GoalieNavigator(robot);
		test.run();
	}

}
