package test_chris;

//import soccer.Robot;
import soccer.Vector2D;
import lejos.nxt.*;

public class HolonomicTest {
	
	public final static double r = 2.5;
	public final static double b = 0;
	public final static Vector2D F0 = new Vector2D(-1.0, 0.0);
	public final static Vector2D F1 = new Vector2D(0.5, -1.0*Math.sqrt(3)/2.0);
	public final static Vector2D F2 = new Vector2D(0.5, Math.sqrt(3)/2.0);
	
	HolonomicTest robot;
	
	NXTMotor motA;
	NXTMotor motB;
	NXTMotor motC;

	public HolonomicTest() {
		motA = new NXTMotor(MotorPort.A);
		motB = new NXTMotor(MotorPort.B);
		motC = new NXTMotor(MotorPort.C);

		robot = this;
	}
	
	public void forward() {
		robot.motA.backward();
		robot.motB.forward();
		robot.motC.stop();
	}
	
	public void forward(int time) {
		robot.motA.backward();
		robot.motB.forward();
		robot.motC.stop();
		robot.sleep(time);
		robot.motA.stop();
		robot.motB.stop();
		robot.motC.stop();
	}
	
	public void backward() {
		robot.motA.forward();
		robot.motB.backward();
		robot.motC.stop();
	}
	
	public void backward(int time) {
		robot.motA.forward();
		robot.motB.backward();
		robot.motC.stop();
		robot.sleep(time);
		robot.motA.stop();
		robot.motB.stop();
		robot.motC.stop();
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
		
		robot.motA.setPower((int)Math.round(w0*scale));
		robot.motB.setPower((int)Math.round(w1*scale));
		robot.motC.setPower((int)Math.round(w2*scale));
		robot.motA.forward();
		robot.motB.forward();
		robot.motC.forward();
	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		moveDir(0);
		robot.sleep(5000);
	}
		
}
	
