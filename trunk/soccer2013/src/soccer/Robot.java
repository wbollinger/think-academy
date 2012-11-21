package soccer;

import lejos.nxt.NXTMotor;

public class Robot {
	
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
	
	public void run(){}

}
