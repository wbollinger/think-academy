package test_tim;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;
import soccer.Navigator;
import soccer.Robot;

public class GoalieNavigator extends Navigator{

	
	Robot move;
	IRSeekerV2 IR;
	
	public GoalieNavigator() {
		move = Robot.getRobot();
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		
	}
	
	public void goalieDefault(){
		while(true){
			moveDir(60);
			move.sleep(3000);
			moveDir(240);
			move.sleep(3000);
		}
	}
	
public void goalieAware(){
	while (true) {
		
		if(IR.getDirection() > 0 && IR.getDirection() < 5){
			if(IR.getSensorValue(3) > 200){
				move.stopAll();
			} else {
				//This goes left
			moveDir(240);
			}
			if(IR.getSensorValue(3) > 200){
				move.stopAll();
			}
		}
		if(IR.getDirection() == 5){
			move.stopAll();
		}
		if(IR.getDirection() > 5 && IR.getDirection() < 10){
			//This goes right
			moveDir(60);
		}
		
	}
}

public void run(){
	
		goalieAware();
	
}
public static void main(String[] args) {

	GoalieNavigator bot = new GoalieNavigator();
	bot.run();
	bot.move.sleep(1000);
}
}	
	

