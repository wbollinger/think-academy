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
	
public void defaultGoalie(){
	while (true) {
		
		if(IR.getDirection() > 0 && IR.getDirection() < 5){
			if(IR.getSensorValue(3) > 200){
				move.stopAll();
			} else {
			moveDir(180);
			}
			if(IR.getSensorValue(3) > 200){
				move.stopAll();
			}
		}
		if(IR.getDirection() == 5){
			move.stopAll();
		}
		if(IR.getDirection() > 5 && IR.getDirection() < 10){
			moveDir(0);
		}
		
	}
}
}	
	

