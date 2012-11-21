package soccer;
import lejos.nxt.addon.*;
import lejos.nxt.*;

public class IRSeekerTest {
	NXTMotor motC;
	NXTMotor motB;
	NXTMotor motA;
	IRSeekerV2 seeker;
	
	public IRSeekerTest(){
		seeker = new IRSeekerV2(SensorPort.S1, IRSeekerV2.Mode.AC);
		motC = new NXTMotor(MotorPort.C);
		motB = new NXTMotor(MotorPort.B);
		motA = new NXTMotor(MotorPort.A);
		motC.setPower(50);
		motB.setPower(50);
		motA.setPower(50);
		motC.stop();
		motB.stop();
		motA.stop();
	}
	
	public void sleep(int time){
		try {
			Thread.sleep(time);
		} catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void forward360(){
		motA.forward();
		motB.backward();
		sleep(500);
		motA.stop();
		motB.stop();
	}
	public void left240(){
		motA.backward();
		motC.forward();
		sleep(500);
		motA.stop();
		motC.stop();
	}
	
	public void right120(){
		motB.forward();
		motC.backward();
		sleep(500);
		motA.stop();
		motB.stop();
	}
	
	public void spaz(int turn){
		motC.setPower(100);
		motB.setPower(50);
		motA.setPower(100);
		
		motA.forward();
		motB.backward();
		motC.forward();
		sleep(turn);
		
		motC.setPower(50);
		motB.setPower(50);
		motA.setPower(50);
		
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void spaz2(int turn){
		
		motC.setPower(100);
		motB.setPower(100);
		motA.setPower(50);
		
		motA.forward();
		motB.backward();
		motC.backward();
		
		sleep(turn);
		
		motC.setPower(50);
		motB.setPower(50);
		motA.setPower(50);
		
		motA.stop();
		motB.stop();
		motC.stop();
	}
	public void LoRturn(int ball, int turn){
		IRSeekerTest robot = new IRSeekerTest();
		if(ball > 5){
			robot.spaz(turn*185);
		}else if(ball < 5){
			robot.spaz2(turn);
		}else{
			motA.stop();
			motB.stop();
			motC.stop();
		}
	}
	public void findBall(){
		IRSeekerTest robot = new IRSeekerTest();
		int ball = seeker.getDirection();
		int turn;
		
		if (ball == 1|| ball == 9){
		turn = 4;
		}else if(ball == 2|| ball == 8){
		turn = 3;
		}else if(ball == 3|| ball == 7){
		turn = 2;
		}else if (ball == 4|| ball == 6){
		turn = 1;
		}else{
			turn = 0;
		}
		LoRturn(ball,turn);
		motA.stop();
		motB.stop();
		motC.stop();
		robot.sleep(500);
		motA.forward();
		motB.backward();
		robot.sleep(1000);
	
	}
	
	
	
	public static void main(String[]args){ 
	IRSeekerTest robot = new IRSeekerTest();
	while (Button.ESCAPE.isUp()){
		robot.motC.backward();
	}
	robot.motC.stop();
	robot.sleep(250);
	while(Button.ESCAPE.isDown()) {
		robot.motC.forward();
	}
	
	/*while(true){
	robot.findBall();
	}
	*/
}
}