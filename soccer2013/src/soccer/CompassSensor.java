package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class CompassSensor {

	CompassHTSensor compass;
	OmniDirRobot move;

	CompassSensor() {

		move = new OmniDirRobot();
		compass = new CompassHTSensor(SensorPort.S2);

	}

	// Just what I thought was the old compass averaging code

	/*
	 * public float getHeading() { float cReading = 0.0f; float
	 * correctedCReading = 0.0f;
	 * 
	 * if (compass != null) { cReading = compass.getDegrees(); }
	 * correctedCReading = 360 - cReading + 90; // debugln("Old Heading: " +
	 * correctedCReading); if (correctedCReading >= 360) { correctedCReading =
	 * correctedCReading - 360; } correctedCReading = correctedCReading -
	 * compOffset; // debugln("New Heading: " + correctedCReading); if
	 * (correctedCReading < 0) { correctedCReading = correctedCReading + 360; }
	 * return correctedCReading; }
	 */

	public void degreeTurn(int n) {
		int degree = (int) compass.getDegrees();
		int newDegree = degree + n;
		
		//The new degree, which comes from adding the specified turn (n) 
		//to the current degree the robot is facing
		
		if (newDegree > 359) {
			//This makes sure the new degree stays modulo 360
			//aka after reaching 359 it loops back to 0
			newDegree = newDegree - 360;
		}
		// LCD.drawString("New Degree:" + newDegree, 0, 2);
		
		//This next bit of code makes it find the optimal direction to turn to
		// as well as actually make it turn
		
		if (n < 180) {
			while (true) {
				move.turnRight();
				if (compass.getDegrees() == newDegree) {
					break;
				}
			}
		} else {
			while (true) {
				move.turnLeft();
				if (compass.getDegrees() == newDegree) {
					break;
				}
			}
		}

		move.stopAll();

	}

	public void run() throws InterruptedException {

		while (Button.ESCAPE.isUp()) {

			LCD.drawInt((int) compass.getDegrees(), 0, 1);
			degreeTurn(90);
			Thread.sleep(20);
			LCD.clear();

		}
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		CompassSensor bot = new CompassSensor();
		bot.run();
	}

}
