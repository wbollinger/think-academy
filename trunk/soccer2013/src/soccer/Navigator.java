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

	public void turnTo(int angle) {
		int degree = (int) compass.getDegrees();
		int newDegree = degree + angle;
		
		//The new degree, which comes from adding the specified turn (n) 
		//to the current degree the robot is facing
		
		newDegree = (int) normalize(newDegree);
		//LCD.drawString("New Degree:" + newDegree, 0, 2);
		
		//This next bit of code makes it find the optimal direction to turn to
		// as well as actually make it turn
		
		if (angle > 0) {
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
				if (dif < 3) {
					break;
				}
				//sleep(50);	
			}
		}
		move.stopAll();
	}

	protected float normalize(float angle)
	  {
	    while (angle > 180)angle -= 360;
	    while (angle < -180)angle += 360;
	    return angle;
	  }
	
	public void run() {
	
			turnTo(90);
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {

		Navigator bot = new Navigator();
		bot.run();
		bot.move.sleep(1000);
	}

}
