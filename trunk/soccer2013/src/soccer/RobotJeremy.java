package soccer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
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
	    USY = new UltrasonicSensor(SensorPort.S2);
	    USX = new UltrasonicSensor(SensorPort.S3);
	    
		
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
	
	public double pointToGoal(){
		io.debugln("went into pointgoal");
		double wall1dist; // pointing north
		double wall2dist; // pointing east
		wall1dist = USY.getDistance();
		io.debugln("first reading");
		wall2dist = USX.getDistance();
		
		io.debugln("Second reading");
		sleep(1000);
		double angle = Math.atan2(91-wall2dist, wall1dist);
		angle = (angle*180)/Math.PI;
		LCD.drawInt((int)angle, 3, 3);
		io.debugln("Angle calculated");
		if(angle > 0){
			turnLeftprecise(angle);
		} else {
			turnRightprecise(Math.abs(angle));
		}
		io.debugln("turned");
		sleep(1000);
		
		return Math.sqrt(Math.pow(wall1dist,2)+Math.pow(91-wall2dist,2));
	}
	
	public void moveForward(){
		
		motC.forward();
		motB.backward();
		
	}
	
	public void moveBackward(){
		
		motC.backward();
		motB.forward();
		
	}

	public void moveWestForward(){
		
		motA.backward();
		motB.forward();
		
	}
	
	public void moveEastForward(){
		
		motA.forward();
		motC.backward();
		
	}
	
	public void moveEastBackward(){
		
		motA.forward();
		motB.backward();
		
	}
	
	public void moveWestBackward(){
		
		motA.backward();
		motC.forward();
		
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

public double[] getLocation(){
	double wall1dist;
	double wall2dist;
	double xPos;
	double yPos;
	double array[] = new double[]{1,1};
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
	turnLeftprecise(90);
	
	xPos = 182-wall2dist;
	yPos = wall1dist;
	array[0] = xPos;
	array[1] = yPos;
	return array;
	
}
	
}
