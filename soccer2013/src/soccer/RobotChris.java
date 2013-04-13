package soccer;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.MMXMotor;
import lejos.nxt.addon.NXTMMX;

public class RobotChris extends Robot {

	//NXTRegulatedMotor regMotA; 
	//NXTRegulatedMotor regMotB;
	//NXTRegulatedMotor regMotC;
	
	public RobotChris(String name) {
		super(name);
		
		LightCorrection = 90;
		
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		
		setPower(80);
		motA.stop();
		motB.stop();
		motC.stop();
		
		//regMotA = new NXTRegulatedMotor(MotorPort.A);
		//regMotB = new NXTRegulatedMotor(MotorPort.B);
		//regMotC = new NXTRegulatedMotor(MotorPort.C);
		
		//regMotA.setAcceleration(1000);
		//regMotB.setAcceleration(1000);
		//regMotC.setAcceleration(1000);
		
		//regMotA.setSpeed(1000);
		//regMotB.setSpeed(1000);
		//regMotC.setSpeed(1000);

		
		EIR = new EnhIRSeekerV2(SensorPort.S1);
		compass = new CompassHTSensor(SensorPort.S2);
		mux = new NXTMMX(SensorPort.S3);
		dribbler = new MMXMotor(mux, NXTMMX.MMX_MOTOR_1);
		arduino = new ArduSoccer(SensorPort.S4);
		
		arduino.writeCommand(Robot.DISCONNECT_CAPS_SOLENOID);
		sleep(50); // wait for the relay to open
		arduino.writeCommand(Robot.CONNECT_CAPS_CHARGER);

		r = 2.5;
		b = 0;
	}

//	public void followBall() {
//		int dir;
//		int str;
//		int left;
//		int right;
//
//		while (Button.ENTER.isUp()) {
//			EIR.update();
//			dir = EIR.getDir();
//			str = EIR.getStrength();
//			left = lightLeft.readValue();
//			right = lightRight.readValue();
//			io.debugln("" + left + ":" + right);
//			// io.debugln("" + dir + ":" + str);
//			if (str > 350) {
//				stopAll();
//				dribbler.forward();
//				forward(300);
//				return;
//			} else if ((right > WHITE_VALUE) && (left > WHITE_VALUE)) {
//				nav.moveDir(270);
//				sleep(1000);
//			} else if (right > WHITE_VALUE) {
//				nav.moveDir(340);
//				sleep(200);
//			} else if (left > WHITE_VALUE) {
//				nav.moveDir(180);
//				sleep(200);
//			} else {
//
//				if (dir == 5) {
//					moveForward();
//				} else if (dir < 5) {
//					turnLeft();
//				} else if (dir > 5) {
//					turnRight();
//				} else {
//					stopAll();
//				}
//			}
//		}
//	}
	
	@Override
	public void fireSolenoid() {
		arduino.writeCommand(Robot.DISCONNECT_CAPS_CHARGER);
		sleep(200); // wait for the relay to open
		arduino.writeCommand(Robot.CONNECT_CAPS_SOLENOID);
		sleep(1000);
		arduino.writeCommand(Robot.DISCONNECT_CAPS_SOLENOID);
		sleep(200); // wait for the relay to open
		arduino.writeCommand(Robot.CONNECT_CAPS_CHARGER);
	}

	@Override
	public void moveForward() {
		motA.setPower(MOTOR_POWER+aFudge);
		motB.setPower(MOTOR_POWER+bFudge);
		motC.setPower(MOTOR_POWER+cFudge);
		
		//regMotA.setSpeed(1000);
		//regMotB.setSpeed(1000);
		//regMotC.setSpeed(1000);
		
	
		motB.backward();
		motC.forward();
		motA.stop();
	}

	@Override
	public void forward(int time) {
		moveForward();

		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	@Override
	public void moveBackward() {
		motA.setPower(MOTOR_POWER+aFudge);
		motB.setPower(MOTOR_POWER+bFudge);
		motC.setPower(MOTOR_POWER+cFudge);
		
		//regMotA.setSpeed(1000);
		//regMotB.setSpeed(1000);
		//regMotC.setSpeed(1000);
		
		motC.backward();
		motB.forward();
		motA.stop();
	}

	@Override
	public void stopAll() {
		motA.stop();
		motB.stop();
		motC.stop();
		
		//regMotA.stop();
		//regMotB.stop();
		//regMotC.stop();
	}

	@Override
	public void turnRight() {
		motA.setPower(MOTOR_POWER+aFudge);
		motB.setPower(MOTOR_POWER+bFudge);
		motC.setPower(MOTOR_POWER+cFudge);
		
		motA.forward();
		motB.forward();
		motC.forward();
	}

	@Override
	public void turnRightPrecise(double degrees) {
		//io.debugln("I'm a gonna turn right");
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
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
	}

	@Override
	public void turnLeft() {
		motA.setPower(MOTOR_POWER+aFudge);
		motB.setPower(MOTOR_POWER+bFudge);
		motC.setPower(MOTOR_POWER+cFudge);
		motA.backward();
		motB.backward();
		motC.backward();
	}

	@Override
	public void turnLeftPrecise(double degrees) {
		//io.debugln("I'm a gonna turn left");
		motA.resetTachoCount();
		motB.resetTachoCount();
		motC.resetTachoCount();
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
	}

	@Override
	public void moveArcRight() {
		motA.setPower(MOTOR_POWER);
		motA.forward();

	}
	
	//@Override
	public void moveArcRightPrecise() {
		motA.setPower(MOTOR_POWER);
		motA.forward();

	}
	
	@Override
	public void moveArcLeft() {
		motA.setPower(MOTOR_POWER);
		motA.backward();

	}
	
	//@Override
	public void moveArcLeftPrecise() {
		motA.setPower(MOTOR_POWER);
		motA.backward();

	}

}
