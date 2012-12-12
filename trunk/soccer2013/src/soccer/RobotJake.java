package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;

public class RobotJake extends Robot{
	NXTMotor motA;
	NXTMotor motB;
	NXTMotor motC;
	IRSeekerV2 IR;
	CompassHTSensor compass;
	
	public RobotJake(String name){
	super(name);
		
	motA = new NXTMotor(MotorPort.A);
	motB = new NXTMotor(MotorPort.B);
	motC = new NXTMotor(MotorPort.C);
	IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
	compass = new CompassHTSensor(SensorPort.S2);
	
	motA.setPower(50);
	motB.setPower(50);
	motC.setPower(50);
	motA.stop();
	motB.stop();
	motC.stop();
}
	public static void main(String[] args) {
		
	}


	}
