package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;

public class Robot {

	protected static double r;
	protected static double b;
	
	public final static Vector2D F0 = new Vector2D(-1.0, 0.0);
	public final static Vector2D F1 = new Vector2D(0.5, -1.0*Math.sqrt(3)/2.0);
	public final static Vector2D F2 = new Vector2D(0.5, Math.sqrt(3)/2.0);
	
	NXTMotor motA;
	NXTMotor motB;
	NXTMotor motC;
	
	// this is a singleton
	private static Robot robot;
	
	public static Robot getRobot() {
		if (robot == null) {
			robot = RobotFactory.makeRobot();
		}
		if (robot == null) {
			
		}
		return robot;
	}
	
	public Robot() {

	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getR() {
		return r;
	}

	public static double getB() {
		return b;
	}
	
	public void followBall(){}
	
	public void moveForward(){}
	
	public void stopAll(){}

	public void turnLeft() {}

	public void turnRight() {}

}
