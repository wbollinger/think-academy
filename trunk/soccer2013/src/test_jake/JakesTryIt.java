package test_jake;
import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.*;

public class JakesTryIt {

		NXTMotor motA;
		NXTMotor motB;
		NXTMotor motC;
		IRSeekerV2 IR;
		CompassHTSensor compass;
		
		public JakesTryIt(){
			
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		IR = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		compass = new CompassHTSensor(SensorPort.S2);
		
		motA.setPower(50);
		motB.setPower(50);
		motC.setPower(50);
		motA.stop();
		motB.stop();
		motC.stop();
	}
		
		public void run(){
			moveForward();
		}
		
	public void moveForward(){
			
			motC.forward();
			motB.backward();
			
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
		public static void main(String[] args) {
			
			JakesTryIt robot = new JakesTryIt();
			robot.run();
			while (Button.ESCAPE.isUp()){
				robot.moveForward();
			}
		}
	}
