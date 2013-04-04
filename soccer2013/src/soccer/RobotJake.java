package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;

public class RobotJake extends Robot {

	public RobotJake(String name) {
		super(name);

		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		
		arduino = new ArduSoccer(SensorPort.S1);
		compass = new CompassHTSensor(SensorPort.S2);
		IR = new IRSeekerV2(SensorPort.S3, IRSeekerV2.Mode.AC);

		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
	}

}
