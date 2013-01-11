package test_tim;

import soccer.Navigator;
import soccer.Robot;
import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;

public class IntArray {

	Robot move = Robot.getRobot();
	CompassHTSensor compass = new CompassHTSensor(SensorPort.S2);
	double[] data = new double[1000];
	
	public void run(){
		while(true){
			move.nav.rotate360();
		}
	}
	
	public static void main(String[] args) {
		IntArray bot = new IntArray();
		bot.run();
		bot.move.sleep(1000);
		
		

	}
}