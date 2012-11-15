package soccer;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;

public class OmniDirRobot {

	NXTMotor motA = new NXTMotor(MotorPort.A);
	NXTMotor motB = new NXTMotor(MotorPort.B);
	NXTMotor motC = new NXTMotor(MotorPort.C);
	IRSeekerV2 IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);

	public void run() throws InterruptedException {
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();

		while (true) {
			
			if(IR.getDirection() > 0 && IR.getDirection() < 5){
				if(IR.getSensorValue(3) > 200){
					stopAll();
				} else {
				turnLeft();
				}
				if(IR.getSensorValue(3) > 200){
					stopAll();
				}
			}
			if(IR.getDirection() == 5){
				stopAll();
				moveForward();
			}
			if(IR.getDirection() > 5 && IR.getDirection() < 10){
				turnRight();
			}
			
		}
	}

	public static void main(String[] args) throws InterruptedException {

		OmniDirRobot start = new OmniDirRobot();
		start.run();

	}
	
	public void moveForward(){
		
		motA.backward();
		motB.forward();
		
	}
	
	public void stopAll(){
		motA.stop();
		motB.stop();
		motC.stop();
	}

	public void turnLeft() {

		motA.backward();
		motB.backward();
		motC.backward();

	}

	public void turnRight() {

		motA.forward();
		motB.forward();
		motC.forward();

	}
}