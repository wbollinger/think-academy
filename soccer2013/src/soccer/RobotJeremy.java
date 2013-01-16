package soccer;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.IRSeekerV2;

public class RobotJeremy extends Robot{
	public UltrasonicSensor US;
	
	public RobotJeremy(String name) {
		super(name);
		
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
	    US = new UltrasonicSensor(SensorPort.S2); 
		
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void followBall() {
		
		while (!Button.ENTER.isDown()) {
			
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
	
	public void turnLeftprecise(double degrees) {
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
		double count = 3.14*degrees;
		while(Math.abs(motA.getTachoCount()) < count){
			motA.backward();
			motB.backward();
			motC.backward();
		}
		stopAll();
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
	}

	public void turnRight() {

		motA.forward();
		motB.forward();
		motC.forward();

	}
	
public void turnRightprecise(double degrees) {
	motA.resetTachoCount();
	motB.resetTachoCount();
	motC.resetTachoCount();
	double count = 3.14*degrees;
	while(Math.abs(motA.getTachoCount()) < count){
		motA.forward();
		motB.forward();
		motC.forward();
	}
	stopAll();
	motA.resetTachoCount();
	motB.resetTachoCount();
	motC.resetTachoCount();
	}

public void pointToGoal(){
	double wall1dist;
	double wall2dist;
	wall1dist = US.getDistance();
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	turnRightprecise(90);
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	wall2dist = US.getDistance();
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	double angle = Math.atan2(91-wall2dist, wall1dist);
	turnLeftprecise(90);
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	turnLeftprecise(angle);
	
	
}
	
}
