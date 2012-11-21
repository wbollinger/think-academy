package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class Navigator {
	
	CompassHTSensor compass;
	Robot move;

	Navigator() {
		move = Robot.getRobot();
		compass = new CompassHTSensor(SensorPort.S2);
	}

	public void rotate(int n) {
		int degree = (int) compass.getDegrees();
		int newDegree = degree + n;
		
		//The new degree, which comes from adding the specified turn (n) 
		//to the current degree the robot is facing
		
		if (newDegree > 359) {
			//This makes sure the new degree stays modulo 360
			//aka after reaching 359 it loops back to 0
			newDegree = newDegree - 360;
		}
		//LCD.drawString("New Degree:" + newDegree, 0, 2);
		
		//This next bit of code makes it find the optimal direction to turn to
		// as well as actually make it turn
		
		if (n < 180) {
			move.turnRight();
			
			int dif;
			
			while (true) {
				degree = Math.round(compass.getDegrees());
				//LCD.drawInt(degree, 0, 1);
				//LCD.drawString("New Degree:" + newDegree, 0, 2);
				dif = Math.abs(degree - newDegree);
				if (dif < 2) {
					break;
				}	
				//Thread.sleep(50);	
			}
		} else {
			move.turnLeft();
			
			int dif = Math.abs(degree - newDegree);
			while (true) {
				degree = Math.round(compass.getDegrees());
				//LCD.drawInt(degree, 0, 1);
				//LCD.drawString("New Degree:" + newDegree, 0, 2);
				dif = Math.abs(degree - newDegree);
				if (dif < 2) {
					break;
				}
				//sleep(50);	
			}
		}
		move.stopAll();
	}

	public void run() {
		
			rotate(270);
			move.sleep(20);
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		Navigator bot = new Navigator();
		bot.run();
		bot.move.sleep(1000);
	}

}
