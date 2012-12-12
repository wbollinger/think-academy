package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;

public class RobotTim extends Robot{

	IRSeekerV2 IR;
	
	public RobotTim(String name) {
		super(name);
		
		io.debugln("I am Tim the Robot!");
		
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
		
		while (true) {
			
			if(IR.getDirection() > 0 && IR.getDirection() < 5){
				if(IR.getSensorValue(3) > 200){
					stopAll();
				} else {
				turnLeft();
				}
				if(IR.getSensorValue(3) > 200){
					stopAll();
				}
			}
			if(IR.getDirection() == 5){
				stopAll();
				moveForward();
			}
			if(IR.getDirection() > 5 && IR.getDirection() < 10){
				turnRight();
			}
			
		}
	}

	public void moveForward(){
		
		motC.stop();
		motA.backward();
		motB.forward();
		
		
	}
	public void moveBackward(){
		motC.stop();
		motA.forward();
		motB.backward();
	}
	
	public void forward(int time) {
		motA.backward();
		motB.forward();
		motC.stop();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
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
	public void turnLeft(int time) {
		motA.backward();
		motB.backward();
		motC.backward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void turnRight() {

		motA.forward();
		motB.forward();
		motC.forward();

	}
	public void turnRight(int time) {
		motA.forward();
		motB.forward();
		motC.forward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	
}
