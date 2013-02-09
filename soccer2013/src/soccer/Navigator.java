package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class Navigator {

	protected Robot bot;

	public Navigator(Robot bot) {
		this.bot = bot;
	}

	public void moveDir(double dir) {
		Vector2D v = new Vector2D(Vector2D.toRadian(dir));

		// LCD.drawInt((int)Math.round(Vector2D.toRadian(dir)), 0, 0);

		double w0 = v.dot(Robot.F0) / bot.getR();
		double w1 = v.dot(Robot.F1) / bot.getR();
		double w2 = v.dot(Robot.F2) / bot.getR();

		// LCD.drawString(Double.toString(v.getX()), 0, 0);
		// LCD.drawString(Double.toString(v.getY()), 0, 1);

		// LCD.drawString(Double.toString(v.dot(F0)), 0, 0);
		// LCD.drawString(Double.toString(v.dot(F1)), 0, 1);
		// LCD.drawString(Double.toString(v.dot(F2)), 0, 2);

		double max = Math.max(Math.abs(w0),
				Math.max(Math.abs(w1), Math.abs(w2)));

		double scale = 100.0 / max;

		LCD.drawString(Double.toString(scale), 0, 4);

		bot.motA.setPower((int) Math.round(w0 * scale));
		bot.motB.setPower((int) Math.round(w1 * scale));
		bot.motC.setPower((int) Math.round(w2 * scale));
		bot.motA.forward();
		bot.motB.forward();
		bot.motC.forward();
	}

	public void rotateTo(float turnDegree) {

		float headingDegree = bot.compass.getDegrees();
		float targetDegree = headingDegree + turnDegree;
		headingDegree = (float) normalize(headingDegree);
		bot.io.debugln("First Heading: " + headingDegree);
		targetDegree = (float) normalize(targetDegree);
		bot.io.debugln("Target: " + targetDegree);
		
		if (turnDegree > 0) {
			bot.turnRight();
		} else {
			bot.turnLeft();

		}
		
		while (true) {

			headingDegree = bot.compass.getDegrees();
			headingDegree = (float) normalize(headingDegree);
			bot.io.debugln("Heading: " + headingDegree + " Remaining Angle: "
					+ normalize(targetDegree - headingDegree));

			if (targetDegree < headingDegree + 5
					&& targetDegree > headingDegree - 5) {
				break;
			}
		}
		bot.stopAll();
	}

	public void calibrate() {
		bot.compass.startCalibration();
		if (Button.ESCAPE.isDown()) {
			bot.compass.stopCalibration();
		}
	}

	public void rotate360() {

		/*----------WARNING----------*\
		||---This method is a fail---||
		\*----------WARNING----------*/

		float degree = 0;
		float newDegree = degree + 360;
		float dif;

		while (true) {
			bot.turnRight();
			degree = bot.compass.getDegrees();
			dif = Math.abs(degree - newDegree);
			if (dif < 3) {
				break;

			}
		}

	}
	
	public double pointGoal(){
		double wall1dist;
		double wall2dist;
		wall1dist = bot.US.getDistance();
		LCD.drawInt((int)wall1dist, 1, 1);
		bot.sleep(1000);
		bot.turnRightprecise(90);
		bot.sleep(1000);
		wall2dist = bot.US.getDistance();
		LCD.drawInt((int)wall2dist, 2, 2);
		bot.sleep(1000);
		
		double angle = Math.atan2(91-wall2dist, wall1dist);
		angle = (angle*180)/Math.PI;
		LCD.drawInt((int)angle, 3, 3);
		bot.turnLeftprecise(90);
		bot.sleep(1000);
		if(angle > 0){
			bot.turnLeftprecise(angle);
		} else {
			bot.turnRightprecise(Math.abs(angle));
		}
		bot.sleep(1000);
		
		return Math.sqrt(Math.pow(wall1dist,2)+Math.pow(91-wall2dist,2));
	}
	
	public int[] getLocation(){
		int wall1dist;
		int wall2dist;
		int xPos;
		int yPos;
		wall1dist = bot.US.getDistance();
		bot.sleep(1000);
		bot.turnRightprecise(90);
		bot.sleep(1000);
		wall2dist = bot.US.getDistance();
		bot.sleep(1000);
		bot.turnLeftprecise(90);
		
		xPos = 182-wall2dist;
		yPos = wall1dist;
		int array[] = new int[2];
		array[0] = xPos;
		array[1] = yPos;
		
		return array;
		
	}

	protected double normalize(double angle) {
		while (angle >= 360) {
			angle -= 360;
		}

		while (angle <= -360) {
			angle += 360;
		}

		if (angle >= 180) {
			angle = angle - 360;

		}
		if (angle <= -180) {
			angle = angle + 180;

		}

		return angle;
	}

	public void run() {

		moveDir(240);
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */

}
