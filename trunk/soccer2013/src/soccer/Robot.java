package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;

public class Robot {

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
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
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
	
	public void moveForward(){
		motC.forward();
		motB.backward();
	}
	
	public void stopAll(){
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void turnLeft() {
		motA.backward();
		motB.backward();
		motC.backward();
	}

	public void turnRight() {
		motA.forward();
		motB.forward();
		motC.forward();
	}

}
