package test_tim;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;

/*
 * A quick system check customizable by the run method
 */
public class SystemCheck {

	NXTMotor motA = new NXTMotor(MotorPort.A);
	NXTMotor motB = new NXTMotor(MotorPort.B);
	NXTMotor motC = new NXTMotor(MotorPort.C);
	CompassHTSensor compass = new CompassHTSensor(SensorPort.S2);
	IRSeekerV2 IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);

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

	public boolean motorCheckSingle(NXTMotor motor, int order) {
		if (motor != null) {
			LCD.drawString("The motor " + motor + " is connected", 0, order);
			return true;
		} else {
			LCD.drawString("The motor " + motor + " is NOT connected", 0, order);
			return false;
		}

	}

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

	public void run() {
		motorCheckSingle(motA, 0);
		sensorCheckSingle(SensorPort.S1, 1);
	}

	public static void main(String[] args) {

		SystemCheck bot = new SystemCheck();
		bot.run();

	}
}