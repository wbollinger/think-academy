package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class Navigator {

	protected Robot bot;
	private float facingDegree;

	private int location[] = new int[2];

	private int lastTachoA;
	private int lastTachoB;
	private int lastTachoC;

	public Navigator(Robot bot) {
		this.bot = bot;
	}

	public void moveDir(double dir) {
		Vector2D v = new Vector2D(Vector2D.toRadian(dir));

		double w0 = v.dot(Robot.F0) / bot.getR();
		double w1 = v.dot(Robot.F1) / bot.getR();
		double w2 = v.dot(Robot.F2) / bot.getR();

		double max = Math.max(Math.abs(w0),
				Math.max(Math.abs(w1), Math.abs(w2)));

		double scale = 100.0 / max;

		bot.io.debugln("" + Double.toString(scale));

		bot.motA.setPower((int) Math.round(w0 * scale));
		bot.motB.setPower((int) Math.round(w1 * scale));
		bot.motC.setPower((int) Math.round(w2 * scale));
		bot.motA.forward();
		bot.motB.forward();
		bot.motC.forward();
	}

	// NOTE: speed is not handled well for values close to and over 100.
	// Keep below 75 for predictable results. -- Chris
	public void moveDir(double dir, int speed) {
		Vector2D v = new Vector2D(Vector2D.toRadian(dir)).times(speed);

		double w0 = v.dot(Robot.F0) / bot.getR();
		double w1 = v.dot(Robot.F1) / bot.getR();
		double w2 = v.dot(Robot.F2) / bot.getR();

		bot.motA.setPower((int) Math.round(w0));
		bot.motB.setPower((int) Math.round(w1));
		bot.motC.setPower((int) Math.round(w2));
		bot.motA.forward();
		bot.motB.forward();
		bot.motC.forward();
	}

	public void pointToHeading(float heading) {
		facingDegree = bot.compass.getDegrees();
		facingDegree = (float) normalize(facingDegree);
		heading = (float) normalize(heading);
		bot.io.debugln("Heading is: " + heading);
		bot.io.debugln("Robot is facing: " + facingDegree);
		float pointToDegree = heading - facingDegree;
		pointToDegree = (float) normalize(pointToDegree);

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

		while (true) {
			if (Button.ENTER.isDown()) {
				bot.compass.stopCalibration();
				return;

			}
		}
	}

	public void whoWantsSomePizza() {
		double heading = bot.compass.getDegrees();
		long timeOne = System.currentTimeMillis();
		while (true) {
			long timeTwo = System.currentTimeMillis();
			if(timeOne - timeTwo > 1000){
				moveDir(240);
			}
			if(timeOne - timeTwo < 1000){
				moveDir(60);
			}
			
			if (bot.compass.getDegrees() < heading + 5
					&& bot.compass.getDegrees() > heading - 5) {
				bot.nav.rotateTo((float) heading);

			}

		}
	}
	
	public void strafe(){
		double heading = bot.compass.getDegrees();

		while(true){
			moveDir(0);
			bot.sleep(1500);
			pointToHeading((float)heading);
			bot.floatAll();
			bot.sleep(100);
			moveDir(180);
			bot.sleep(1500);
			pointToHeading((float)heading);
			bot.floatAll();
			bot.sleep(100);
		}
	}

	public double pointToGoal() {
		bot.io.debugln("went into pointToGoal");
		int array[] = getLocation();
		bot.sleep(1000);
		double angle = Math.atan2(91 - array[0], array[1]);
		angle = (angle * 180) / Math.PI;
		LCD.drawInt((int) angle, 3, 3);
		bot.io.debugln("Angle calculated");
		if (angle > 0) {
			bot.turnLeftprecise(angle);
		} else {
			bot.turnRightprecise(Math.abs(angle));
		}
		bot.io.debugln("turned");
		bot.sleep(1000);

		return Math.sqrt(Math.pow(array[1], 2) + Math.pow(91 - array[0], 2));
	}

	public int[] getLocation() {
		int wall1dist;
		int wall2dist;
		int xPos;
		int yPos;
		wall1dist = bot.USY.getDistance();
		wall2dist = bot.USX.getDistance();
		xPos = 122 - wall2dist;
		yPos = 182 - wall1dist;
		int array[] = new int[2];
		array[0] = xPos;
		array[1] = yPos;

		return array;

	}

	public void updateLocation() {
		int tachoA = bot.motA.getTachoCount();
		int tachoB = bot.motB.getTachoCount();
		int tachoC = bot.motC.getTachoCount();

		int tachoDiffA = lastTachoA - tachoA;
		int tachoDiffB = lastTachoB - tachoB;
		int tachoDiffC = lastTachoC - tachoC;

		double moveDistA = tachoDiffA * bot.getR();
		double moveDistB = tachoDiffB * bot.getR();
		double moveDistC = tachoDiffC * bot.getR();

	}

	public void resetLocation() {
		lastTachoA = 0;
		lastTachoB = 0;
		lastTachoC = 0;
		bot.motA.resetTachoCount();
		bot.motB.resetTachoCount();
		bot.motC.resetTachoCount();
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

}
