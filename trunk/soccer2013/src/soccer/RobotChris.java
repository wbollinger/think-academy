package soccer;

import lejos.nxt.Button;
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
			
			//will return on push of orange button. ONLY FOR DEBUG.
			if(Button.ENTER.isDown()) {
				stopAll();
				return;
			}

			if (IR.getDirection() == 5) {
				if (IR.getSensorValue(3) > 180) {
					stopAll();
					return;
				} else {
					moveForward();
				}
			} else if (IR.getDirection() < 5) {
				turnLeft();
			} else if (IR.getDirection() > 5) {
				turnRight();
			} else {
				stopAll();
			}
		}
	}

	public void moveForward() {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motC.forward();
		motB.backward();
		motA.stop();
	}
	
	public void moveBackward() {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
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
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.forward();
		motB.forward();
		motC.forward();
	}

	public void turnLeft() {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.backward();
		motB.backward();
		motC.backward();
	}

}
