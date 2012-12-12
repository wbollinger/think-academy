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
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.addon.CompassHTSensor;

public class CompassArray {

	NXTMotor motA = new NXTMotor(MotorPort.A);
	NXTMotor motB = new NXTMotor(MotorPort.B);
	NXTMotor motC = new NXTMotor(MotorPort.C);
	IRSeekerV2 IR = new IRSeekerV2(SensorPort.S2, IRSeekerV2.Mode.AC);
	CompassHTSensor compass = new CompassHTSensor(SensorPort.S1);
	

	public void run() throws InterruptedException, FileNotFoundException {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
		int count = 500;
		int index = 0;
		int data[] = new int[500];
			
		turnLeft();
		while(count > 0) {
			
			data[index] = (int) compass.getDegrees();
			count--;
			index++;
			
		}	
		stopAll();
		
		FileOutputStream out = null; // declare outside the try block
	    File file = new File("log.txt");

	    try {
	      out = new FileOutputStream(file);
	    } catch(IOException e) {
	    	System.err.println("Failed to create output stream");
	    	System.exit(1);
	    }

	    DataOutputStream dataOut = new DataOutputStream(out);
	    PrintStream printOut = new PrintStream(dataOut);

	    char buf[];
	    int length = 500;

	    try { // write
	      for(int i = 0 ; i<length; i++ ) {
	    	printOut.println(Integer.toString(data[i])+' ');
	      }
	      out.close(); // flush the buffer and write the file
	    } catch (IOException e) {
	      System.err.println("Failed to write to output stream");
	    }
		
		}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		CompassArray start = new CompassArray();
		start.run();

	}
	
	public char[] toCharArray(String x){
		char buf[] = new char[x.length()];
		x.getChars(0, x.length(), buf, 0);
		return buf;
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

	public void turnRight() {

		motA.forward();
		motB.forward();
		motC.forward();

	}
}