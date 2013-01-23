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
	public UltrasonicSensor US = new UltrasonicSensor(SensorPort.S2); 
	NXTMotor motA = new NXTMotor(MotorPort.A);
	NXTMotor motB = new NXTMotor(MotorPort.B);
	NXTMotor motC = new NXTMotor(MotorPort.C);
	CompassHTSensor compass = new CompassHTSensor(SensorPort.S1);
	int array[] = new int[]{1,1};
	

	public void run() throws InterruptedException, FileNotFoundException {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
//		pointTGoal();
		getLocation();
		LCD.drawInt(array[0], 0, 0);
		LCD.drawInt(array[1], 0, 1);
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
	
	public void pointTGoal(){
		double wall1dist;
		double wall2dist;
		wall1dist = US.getDistance();
		LCD.drawInt(US.getDistance(), 0, 0);
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
		LCD.drawInt(US.getDistance(), 0, 0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double angle = Math.atan2(91-wall2dist, wall1dist);
		angle = (angle*180)/Math.PI;
		LCD.drawInt((int)angle, 0, 0);
		turnLeftprecise(90);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		turnLeftprecise(angle);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public int[] getLocation(){
		int wall1dist;
		int wall2dist;
		int xPos;
		int yPos;
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
	
	
	
	public void moveForward(){
		
		motA.backward();
		motB.forward();
		
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