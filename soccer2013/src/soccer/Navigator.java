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

	public void rotateTo(float turnDegree){
	
		
		float faceDegree = bot.compass.getDegrees();
		faceDegree = (float)normalize(faceDegree);
		float newDegree = faceDegree + turnDegree;
		bot.io.debugln("N" + newDegree);
		newDegree = (float)normalize(newDegree);
		
		while(true){
			bot.io.debugln("N" + newDegree);
			for(int i = 0; i < 1; i ++){
				if(newDegree > 0){
					bot.turnRight();
				}else{
					bot.turnLeft();
				}
			}
			faceDegree = bot.compass.getDegrees();
			faceDegree = (float)normalize(faceDegree);
			bot.io.debugln("F" + faceDegree);
			if(newDegree < faceDegree + 3 && newDegree > faceDegree - 3){
				break;
			}
		}
		bot.stopAll();
	}

	public void calibrate() {
		bot.compass.startCalibration();
		if(Button.ESCAPE.isDown()){
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

	protected double normalize(double angle) {
		while(angle > 360){
			angle -= 360;
		}
		
		while(angle < -360){
			angle += 360;
		}
		
		if(angle > 180){
			angle = angle - 180;
			
		}
		if(angle < -180){
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
