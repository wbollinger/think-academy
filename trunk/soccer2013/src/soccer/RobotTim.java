package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;

public class RobotTim extends Robot {

	TouchSensor touch = new TouchSensor(SensorPort.S2);
	final int MOTOR_POWER = 100;

	public RobotTim(String name) {
		super(name);
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		compass = new CompassHTSensor(SensorPort.S2);
		USY = new UltrasonicSensor(SensorPort.S3);
		USX = new UltrasonicSensor(SensorPort.S4);

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
			if (touch.isPressed()) {
				stopAll();
				return;
			} else {

				if (IR.getDirection() == 5) {
					moveForward();
				} else if (IR.getDirection() < 5) {
					turnLeft();
				} else if (IR.getDirection() > 5) {
					turnRight();
				} else {
					stopAll();
				}
			}
		}
	}

	public void moveForward() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motC.forward();
		motB.backward();
		motA.stop();

	}

	public void moveBackward() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motC.backward();
		motB.forward();
		motA.stop();
	}

	public void forward(int time) {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.backward();
		motB.forward();
		motC.stop();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void stopAll() {

		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void floatAll() {

		motA.flt();
		motB.flt();
		motC.flt();
	}

	public void turnLeft() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.backward();
		motB.backward();
		motC.backward();

	}

	public void turnLeft(int time) {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.backward();
		motB.backward();
		motC.backward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void turnRight() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.forward();
		motB.forward();
		motC.forward();

	}

	public void turnRight(int time) {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.forward();
		motB.forward();
		motC.forward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}

}
