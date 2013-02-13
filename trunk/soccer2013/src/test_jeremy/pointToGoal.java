package test_jeremy;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.addon.CompassHTSensor;

public class pointToGoal {
	UltrasonicSensor USY = new UltrasonicSensor(SensorPort.S2);
	UltrasonicSensor USX = new UltrasonicSensor(SensorPort.S3); 
	NXTMotor motA = new NXTMotor(MotorPort.A);
	NXTMotor motB = new NXTMotor(MotorPort.B);
	NXTMotor motC = new NXTMotor(MotorPort.C);
    IRSeekerV2 IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
	CompassHTSensor compass = new CompassHTSensor(SensorPort.S1);
	int array[] = new int[]{1,1};
	

	public void run() throws InterruptedException, FileNotFoundException {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
		pointGoal();
//		moveForward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		moveBackward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		moveWestForward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		moveEastBackward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		moveEastForward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		moveWestBackward();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopAll();
//		LCD.drawInt((int)pointGoal(), 4,4);
//		getLocation();
//		LCD.drawInt(array[0], 0, 0);
//		LCD.drawInt(array[1], 0, 1);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		pointToGoal start = new pointToGoal();
		start.run();

	}
	
	public double pointGoal(){
		double wall1dist;
		double wall2dist;
		wall1dist = USY.getDistance();
		LCD.drawInt((int)wall1dist, 1, 1);
		wall2dist = USX.getDistance();
		LCD.drawInt((int)wall2dist, 2, 2);
		double angle = Math.atan2(91-wall2dist, wall1dist);
		angle = (angle*180)/Math.PI;
		LCD.drawInt((int)angle, 3, 3);
		if(angle > 0){
		turnLeftprecise(angle);
		} else {
			turnRightprecise(Math.abs(angle));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Math.sqrt(Math.pow(wall1dist,2)+Math.pow(91-wall2dist,2));
	}
	
	public int[] getLocation(){
		int wall1dist;
		int wall2dist;
		int xPos;
		int yPos;
		wall1dist = USY.getDistance();
		wall2dist = USX.getDistance();
		xPos = 182-wall2dist;
		yPos = wall1dist;
		array[0] = xPos;
		array[1] = yPos;
		return array;
		
	}
	
	public void postoshoot(){
		double goaldist = pointGoal();
		double balldist = 10; //need method still
		double ballwalldist = 10;//need method still
		IR.getAngle();
		
		
	}
	
public void moveForward(){
		
		motC.backward();
		motB.forward();
		
	}
	
	public void moveBackward(){
		
		motC.forward();
		motB.backward();
		
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
		double count = Math.PI*degrees;
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
}