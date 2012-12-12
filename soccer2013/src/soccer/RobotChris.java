package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;

public class RobotChris extends Robot{

	IRSeekerV2 IR;
	
	public RobotChris(String name) {
		super(name);
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
		
		r = 2.5;
		b = 0;
	}
	
	public void followBall() {
		
	}
	
	public void moveForward(){
		
		motC.backward();
		motB.forward();
		
	}
	
	public void stopAll(){
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void turnLeft() {

		motA.forward();
		motB.forward();
		motC.forward();

	}

	public void turnRight() {

		motA.backward();
		motB.backward();
		motC.backward();

	}
	
	public static void main(String[] args) {
		Robot robot = Robot.getRobot();
		robot.followBall();
	}
}
