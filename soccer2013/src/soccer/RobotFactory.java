package soccer;

import java.util.Properties;

import lejos.nxt.Settings;

public class RobotFactory {

	public static Robot makeRobot() {
		Properties props = Settings.getProperties();
		String name = props.getProperty("lejos.usb_name");
		Robot robot;
		if(name.equals("NXTChris")||name.equals("bbot")) { // Chris' robot
			robot = new RobotChris();
		} else if(name.equals("JPNXT")) { // Jeremy's robot
			robot = new RobotJeremy();
		} else if(name.equals("Dr_Lakata")) { // Tim's robot
			robot = new RobotTim();
		} else if(name.equals("LineBacker")) { // Jake's robot
			robot = new RobotJake();
		} else {
			robot = new Robot();
		}
		
		return robot;
	}
	
}
