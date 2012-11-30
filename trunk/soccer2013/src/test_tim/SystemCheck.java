package test_tim;

import soccer.Robot;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;
import lejos.robotics.navigation.OmniPilot;

/*
 * A quick system check customizable by the run method
 */
public class SystemCheck {
	
NXTMotor motA;
NXTMotor motB;
NXTMotor motC;
CompassHTSensor compass;
IRSeekerV2 IR;
Robot move;
	
	SystemCheck(){
		move = Robot.getRobot();
		
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		
		compass = new CompassHTSensor(SensorPort.S2);
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		
	}
	
	//This tests all motors by turning them right and left
	public void motorTestAll(int order){
			move.turnLeft(1000);
			move.turnRight(1000);
	}
	//This checks to see if all the motors are connected
	public boolean motorCheckAll(int order) {
		if (motA != null && motB != null && motC != null) {
			LCD.drawString("All of the motors are connected", 0, order);
			return true;
		} else {
			LCD.drawString("One or more of the motors are NOT connected", 0,
					order);
			return false;
		}

	}

	//This checks to see if the motor in the specified port is connected
	public boolean motorCheckSingle(NXTMotor motor, int order) {
		if (motor != null) {
			LCD.drawString("The motor " + motor + " is connected", 0, order);
			return true;
		} else {
			LCD.drawString("The motor " + motor + " is NOT connected", 0, order);
			return false;
		}

	}

	//When this is called a number of sensors must be specified and based
	//off of that the method will check your sensors to see if they are plugged in
	public boolean sensorCheckAll(int numOfSensors, int order) {

		if (numOfSensors == 4) {
			if (SensorPort.S1 != null && SensorPort.S2 != null
					&& SensorPort.S3 != null && SensorPort.S4 != null) {
				LCD.drawString("All of the four sensors are connected", 0,
						order);
				return true;
			} else {
				LCD.drawString(
						"One or more of the four sensors are NOT connected", 0,
						order);
				return false;
			}
		}

		if (numOfSensors == 3) {
			if (SensorPort.S1 != null && SensorPort.S2 != null
					&& SensorPort.S3 != null || SensorPort.S1 != null
					&& SensorPort.S2 != null && SensorPort.S4 != null
					|| SensorPort.S1 != null && SensorPort.S3 != null
					&& SensorPort.S4 != null || SensorPort.S2 != null
					&& SensorPort.S3 != null && SensorPort.S4 != null) {
				LCD.drawString("All of the three sensors are connected", 0,
						order);
				return true;
			} else {
				LCD.drawString(
						"One or more of the three sensors are NOT connected",
						0, order);
				return false;
			}

		}
		if (numOfSensors == 2) {
			if (SensorPort.S1 != null && SensorPort.S2 != null
					|| SensorPort.S1 != null && SensorPort.S3 != null
					|| SensorPort.S1 != null && SensorPort.S4 != null
					|| SensorPort.S2 != null && SensorPort.S3 != null
					|| SensorPort.S2 != null && SensorPort.S4 != null
					|| SensorPort.S3 != null && SensorPort.S4 != null) {
				LCD.drawString("The two sensors are connected", 0, order);
				return true;

			} else {
				LCD.drawString(
						"One or all of the two sensors are NOT connected", 0,
						order);
				return false;
			}
		}
		if (numOfSensors == 1) {
			if (SensorPort.S1 != null || SensorPort.S2 != null
					|| SensorPort.S3 != null || SensorPort.S4 != null) {
				LCD.drawString("The sensor is connected", 0, order);
				return true;
			} else {
				LCD.drawString("The sensor is NOT connected", 0, order);
				return false;
			}
		}
		LCD.drawString("ERROR", 0, order);
		return false;
	}

	//This checks to see if the sensor in the specified port is connected
	public boolean sensorCheckSingle(SensorPort sensorPort, int order) {
		if (sensorPort != null) {
			LCD.drawString("The sensor in " + sensorPort + " is connected", 0,
					order);
			return true;

		} else {
			LCD.drawString("The sensor in " + sensorPort + " is NOT connected",
					0, order);
			return false;
		}
	}

	//The basic diagnostic check
	public void run() {
		motorCheckAll(0);
		sensorCheckAll(1, 1);
	}

	public static void main(String[] args) {

		SystemCheck bot = new SystemCheck();
		bot.run();

	}
}