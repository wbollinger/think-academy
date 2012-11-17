package soccer;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.*;

public class HolonomicTest {
	
	public final static double r = 2.5;
	public final static double b = 0;
	public final static Vector2D F0 = new Vector2D(-1.0, 0.0);
	public final static Vector2D F1 = new Vector2D(0.5, -1.0*Math.sqrt(3)/2.0);
	public final static Vector2D F2 = new Vector2D(0.5, Math.sqrt(3)/2.0);
	
	NXTMotor motA;
	NXTMotor motB; 
	NXTMotor motC;
		
	public HolonomicTest() {
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);
		motA.setPower(100);
		motB.setPower(100);
		motC.setPower(100);
	}
	
	public void forward() {
		motA.backward();
		motB.forward();
		motC.stop();
	}
	
	public void forward(int time) {
		motA.backward();
		motB.forward();
		motC.stop();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void forwardLeft() {
		motA.stop();
		motB.backward();
		motC.forward();
	}
	
	public void forwardLeft(int time) {
		motA.stop();
		motB.backward();
		motC.forward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void forwardRight() {
		motA.forward();
		motB.stop();
		motC.backward();
	}
	
	public void forwardRight(int time) {
		motA.forward();
		motB.stop();
		motC.backward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void backward() {
		motA.forward();
		motB.backward();
		motC.stop();
	}
	
	public void backward(int time) {
		motA.forward();
		motB.backward();
		motC.stop();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void backwardRight() {
		motA.stop();
		motB.forward();
		motC.backward();
	}
	
	public void backwardRight(int time) {
		motA.stop();
		motB.forward();
		motC.backward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void backwardLeft() {
		motA.backward();
		motB.stop();
		motC.forward();
	}
	
	public void backwardLeft(int time) {
		motA.backward();
		motB.stop();
		motC.forward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void turnLeft() {
		motA.forward();
		motB.forward();
		motC.forward();
	}
	
	public void turnLeft(int time) {
		motA.forward();
		motB.forward();
		motC.forward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void turnRight() {
		motA.backward();
		motB.backward();
		motC.backward();
	}
	
	public void turnRight(int time) {
		motA.backward();
		motB.backward();
		motC.backward();
		sleep(time);
		motA.stop();
		motB.stop();
		motC.stop();
	}
	
	public void moveDir(double dir) {
		Vector2D v = new Vector2D(Vector2D.toRadian(dir));
		
		//LCD.drawInt((int)Math.round(Vector2D.toRadian(dir)), 0, 0);
		
		double w0 = v.dot(F0)/r;
		double w1 = v.dot(F1)/r;
		double w2 = v.dot(F2)/r;
		
		//LCD.drawString(Double.toString(v.getX()), 0, 0);
		//LCD.drawString(Double.toString(v.getY()), 0, 1);
		
		LCD.drawString(Double.toString(v.dot(F0)), 0, 0);
		LCD.drawString(Double.toString(v.dot(F1)), 0, 1);
		LCD.drawString(Double.toString(v.dot(F2)), 0, 2);
			
		double max = Math.max(Math.abs(w0), Math.max(Math.abs(w1), Math.abs(w2)));
		
		double scale = 100.0/max;
		
		LCD.drawString(Double.toString(scale), 0, 4);
		
		motA.setPower((int)Math.round(w0*scale));
		motB.setPower((int)Math.round(w1*scale));
		motC.setPower((int)Math.round(w2*scale));
		motA.forward();
		motB.forward();
		motC.forward();
	}
	
	public void run() {
		moveDir(0);
		sleep(5000);
	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch(Exception e) {
			System.err.println(e);
		}
	}
		
	public static void main(String[] args) throws InterruptedException{
		HolonomicTest test = new HolonomicTest();
		test.run();
	}
}
	
