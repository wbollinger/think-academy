package soccer;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class RobotEbay extends Robot {

	public RobotEbay(String name) {
		super(name);

		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);

		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
	}

}
