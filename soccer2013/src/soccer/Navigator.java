package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class Navigator {

	protected Robot bot;
	private float facingDegree;

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
	
	public void pointToHeading(float heading){
		facingDegree = bot.compass.getDegrees();
		facingDegree = (float) normalize(facingDegree);
		heading = (float) normalize(heading);
		bot.io.debugln("Heading is: " + heading);
		bot.io.debugln("Robot is facing: " + facingDegree);
		float pointToDegree = heading - facingDegree;
		pointToDegree = (float)normalize(pointToDegree);
		
		bot.io.debugln("Point to: " + pointToDegree);
		
		rotateTo(pointToDegree);
	}

	public void rotateTo(float turnDegree) {

		facingDegree = bot.compass.getDegrees();
		float targetDegree = facingDegree + turnDegree;
		facingDegree = (float) normalize(facingDegree);
		bot.io.debugln("First Heading: " + facingDegree);
		targetDegree = (float) normalize(targetDegree);
		bot.io.debugln("Target: " + targetDegree);
		
		if (turnDegree > 0) {
			bot.turnRight();
		} else {
			bot.turnLeft();

		}
		
		while (true) {

			facingDegree = bot.compass.getDegrees();
			facingDegree = (float) normalize(facingDegree);
			bot.io.debugln("Heading: " + facingDegree + " Remaining Angle: "
					+ normalize(targetDegree - facingDegree));

			if (targetDegree < facingDegree + 5
					&& targetDegree > facingDegree - 5) {
				break;
			}
		}
		bot.stopAll();
	}

	public void calibrate() {
		bot.compass.startCalibration();
		while (true){
		if (Button.ESCAPE.isDown()) {
			bot.compass.stopCalibration();
			return;
		}
		
		}
	}
	
	public double pointToGoal(){
		bot.io.debugln("went into pointToGoal");
		double wall1dist; // pointing north
		double wall2dist; // pointing east
		wall1dist = bot.USY.getDistance();
		bot.io.debugln("first reading");
		wall2dist = bot.USX.getDistance();
		
		bot.io.debugln("Second reading");
		bot.sleep(1000);
		double angle = Math.atan2(91-wall2dist, wall1dist);
		angle = (angle*180)/Math.PI;
		LCD.drawInt((int)angle, 3, 3);
		bot.io.debugln("Angle calculated");
		if(angle > 0){
			bot.turnLeftprecise(angle);
		} else {
			bot.turnRightprecise(Math.abs(angle));
		}
		bot.io.debugln("turned");
		bot.sleep(1000);
		
		return Math.sqrt(Math.pow(wall1dist,2)+Math.pow(91-wall2dist,2));
	}
	
	public int[] getLocation(){
		int wall1dist;
		int wall2dist;
		int xPos;
		int yPos;
		wall1dist = bot.USY.getDistance();
		wall2dist = bot.USX.getDistance();
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
			angle = angle + 360;

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
