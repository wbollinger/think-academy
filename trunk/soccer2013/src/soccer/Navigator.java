package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class Navigator {
	
	CompassHTSensor compass;
	Robot move;

	public Navigator() {
		move = Robot.getRobot();
		compass = new CompassHTSensor(SensorPort.S1);
	}
	
	public void moveDir(double dir) {
		Vector2D v = new Vector2D(Vector2D.toRadian(dir));
		
		//LCD.drawInt((int)Math.round(Vector2D.toRadian(dir)), 0, 0);
		
		double w0 = v.dot(Robot.F0)/Robot.getR();
		double w1 = v.dot(Robot.F1)/Robot.getR();
		double w2 = v.dot(Robot.F2)/Robot.getR();
		
		//LCD.drawString(Double.toString(v.getX()), 0, 0);
		//LCD.drawString(Double.toString(v.getY()), 0, 1);
		
//		LCD.drawString(Double.toString(v.dot(F0)), 0, 0);
//		LCD.drawString(Double.toString(v.dot(F1)), 0, 1);
//		LCD.drawString(Double.toString(v.dot(F2)), 0, 2);
			
		double max = Math.max(Math.abs(w0), Math.max(Math.abs(w1), Math.abs(w2)));
		
		double scale = 100.0/max;
		
		LCD.drawString(Double.toString(scale), 0, 4);
		
		move.motA.setPower((int)Math.round(w0*scale));
		move.motB.setPower((int)Math.round(w1*scale));
		move.motC.setPower((int)Math.round(w2*scale));
		move.motA.forward();
		move.motB.forward();
		move.motC.forward();
	}

	public void rotate360(){
		
		/*----------WARNING----------*\
	    ||---This method is a fail---||
	    \*----------WARNING----------*/
		
		float degree = 0;
		float newDegree = degree + 360;
		float dif;
		
		while(true){
			move.turnRight();
			degree = compass.getDegrees();
				dif = Math.abs(degree - newDegree);
				if (dif < 3) {
					break;
				
			}
		}
		
		}
	
	public void turnTo(int angle) {
		/*----------WARNING-----------*\
	    ||-This method is also a fail-||
	    \*----------WARNING-----------*/
		
		int degree = (int) compass.getDegrees();
		int newDegree = degree + angle;
		
		//The new degree, which comes from adding the specified turn (n) 
		//to the current degree the robot is facing
		
		if(newDegree > 359){
			newDegree = newDegree - 360;
		}
		
		
		//LCD.drawString("New Degree:" + newDegree, 0, 2);
		
		//This next bit of code makes it find the optimal direction to turn to
		// as well as actually make it turn
		
		if (newDegree < 180) {
			move.turnRight();
			
			int dif;
			
			while (true) {
				degree = Math.round(compass.getDegrees());
				//LCD.drawInt(degree, 0, 1);
				//LCD.drawString("New Degree:" + newDegree, 0, 2);
				dif = Math.abs(degree - newDegree);
				if (dif < 3) {
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

			moveDir(240);
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
