package soccer;

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
	
	public void run(){}

}
