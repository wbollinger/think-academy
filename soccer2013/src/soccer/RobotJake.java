package soccer;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;

public class RobotJake extends Robot {

	public RobotJake(String name) {
		super(name);

		LightCorrection = 40;

		MOTOR_POWER = 100;
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);

		// IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		EIR = new EnhIRSeekerV2(SensorPort.S1);

		arduino = new ArduSoccer(SensorPort.S4);

		compass = new CompassHTSensor(SensorPort.S2);
		// USY = new UltrasonicSensor(SensorPort.S3);
		// USX = new UltrasonicSensor(SensorPort.S4);

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		motA.stop();
		motB.stop();
		motC.stop();

		r = 2.5;
		b = 0;

	}

	@Override
	public void followBall() {
		int dir;
		int str;

		while (Button.ENTER.isUp()) {
			EIR.update();
			dir = EIR.getDir();
			str = EIR.getStrength();
			io.debugln("" + dir);
			io.debugln("" + str);
			if (str > 310) {
				stopAll();
				forward(200);
				return;
			} else {

				if (dir == 5) {
					moveForward();
				} else if (dir < 5) {
					turnLeft();
				} else if (dir > 5) {
					turnRight();
				} else {
					stopAll();
				}
			}
		}
	}

	@Override
	public void moveForward() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motC.forward();
		motB.backward();
		motA.stop();

	}

	@Override
	public void moveBackward() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motC.backward();
		motB.forward();
		motA.stop();
	}

	@Override
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

	@Override
	public void stopAll() {

		motA.stop();
		motB.stop();
		motC.stop();
	}

	@Override
	public void floatAll() {

		motA.flt();
		motB.flt();
		motC.flt();
	}

	@Override
	public void turnLeft() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.backward();
		motB.backward();
		motC.backward();

	}

	@Override
	public void turnLeftPrecise(double degrees) {
		// io.debugln("TURNING LEFTISH "+degrees+" DEGREES!!");
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		double count = 3.14 * degrees;
		while (Math.abs(motA.getTachoCount()) < count) {
			motA.backward();
			motB.backward();
			motC.backward();
		}
		stopAll();
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
		// io.debugln("TURNING DUNNED!!");
	}

	@Override
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

	@Override
	public void turnRight() {

		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);

		motA.forward();
		motB.forward();
		motC.forward();

	}

	@Override
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

	@Override
	public void turnRightPrecise(double degrees) {
		// io.debugln("TURNING RIGHTISH "+degrees+" DEGREES!!");
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		double count = 3.14 * degrees;
		while (Math.abs(motA.getTachoCount()) < count) {
			motA.forward();
			motB.forward();
			motC.forward();
		}
		stopAll();
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
		// io.debugln("TURNING DUNNED!!");
	}

	@Override
	public void moveArcRight() {
		motA.setPower(MOTOR_POWER);
		motA.forward();
	}

	@Override
	public void moveArcLeft() {
		motA.setPower(MOTOR_POWER);
		motA.backward();
	}
}