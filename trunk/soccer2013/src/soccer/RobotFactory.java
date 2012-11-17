package soccer;

import java.util.Properties;

import lejos.nxt.Settings;

public class RobotFactory {

	public static Robot makeRobot() {
		Properties props = Settings.getProperties();
		String name = props.getProperty("lejos.usb_name");
		Robot robot;
		if(name.equals("NXTChris")) { // Chris' robot
			robot = new RobotChris();
		} else if(name.equals("JPNXT")) { // Jeremy's robot
			robot = new RobotJeremy();
		} else if(name.equals("TIM")) { // Tim's robot
			robot = new RobotTim();
		} else if(name.equals("JAKE")) { // Jake's robot
			robot = new RobotJake();
		} else {
			robot = new Robot();
		}
		
		return robot;
	}
	
}
