package soccer;

import java.util.Properties;

import lejos.nxt.Settings;

public class RobotFactory {

	public static Robot makeRobot() {
		Properties props = Settings.getProperties();
		String name = props.getProperty("lejos.usb_name");
		Robot robot;
		if (name.equals("NXTChris") || name.equals("bbot")) { // Chris' robot
			robot = new RobotChris(name);
			robot.name = name;
		} else if (name.equals("JPNXT")) { // Jeremy's robot
			robot = new RobotJeremy(name);
			robot.name = name;
		} else if (name.equals("Tim")) { // Tim's robot
			robot = new RobotTim(name);
			robot.name = name;
		} else if (name.equals("ebay")) { // Dead display, no sound
			robot = new RobotEbay(name);
			robot.name = name;
		} else if (name.equals("LineBacker")) { // Jake's robot
			robot = new RobotJake(name);
			robot.name = name;
		} else {
			robot = new Robot(name);
		}

		return robot;
	}

}
