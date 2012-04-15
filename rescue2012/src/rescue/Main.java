package rescue;

public class Main {

	public static void main(String[] args) {
		// create a robot
		Robot robot = Robot.getRobot();

		// Use BlueTooth command loop
		robot.setUseCommands();

		robot.run();
	}
}
