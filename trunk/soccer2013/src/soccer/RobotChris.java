package soccer;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.addon.CompassHTSensor;

public class RobotChris extends Robot {

	public RobotChris(String name) {
		super(name);
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);

		// IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		EIR = new EnhIRSeekerV2(SensorPort.S1);
		compass = new CompassHTSensor(SensorPort.S2);
		//lightLeft = new LightSensor(SensorPort.S3);
		//lightRight = new LightSensor(SensorPort.S4);
		arduino = new ArduSoccer(SensorPort.S4);
		// USY = new UltrasonicSensor(SensorPort.S3);
		// USX = new UltrasonicSensor(SensorPort.S4);

		setPower(80);
		motA.stop();
		motB.stop();
		motC.stop();

		r = 2.5;
		b = 0;
	}

	public void followBall() {
		int dir;
		int str;
		int left;
		int right;

		while (Button.ENTER.isUp()) {
			EIR.update();
			dir = EIR.getDir();
			str = EIR.getStrength();
			left = lightLeft.readValue();
			right = lightRight.readValue();
			io.debugln(""+left+":"+right);
			//io.debugln("" + dir + ":" + str);
			if (str > 350) {
				stopAll();
				forward(300);
				return;
			} else if((right > WHITE_VALUE) && (left > WHITE_VALUE)) {
				nav.moveDir(270);
				sleep(1000);
			} else if (right > WHITE_VALUE) {
				nav.moveDir(340);
				sleep(200);
			} else if (left > WHITE_VALUE) {
				nav.moveDir(180);
				sleep(200);
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

	public void moveForward() {
		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		motC.forward();
		motB.backward();
		motA.stop();
	}

	public void forward(int time) {
		moveForward();

		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void moveBackward() {
		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		motC.backward();
		motB.forward();
		motA.stop();
	}

	public void stopAll() {
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

	public void turnLeft() {
		motA.setPower(MOTOR_POWER);
		motB.setPower(MOTOR_POWER);
		motC.setPower(MOTOR_POWER);
		motA.backward();
		motB.backward();
		motC.backward();
	}

	public void moveArcRight() {
		motA.setPower(MOTOR_POWER);
		motA.forward();

	}

	public void moveArcLeft() {
		motA.setPower(MOTOR_POWER);
		motA.backward();

	}

}
