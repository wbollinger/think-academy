import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Properties;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.comm.BTConnection;
//import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;

public class Robot {

	// this is a singleton
	private static Robot robot;

	public boolean exit = false;

	String name;

	double wheelDiameter = 5.6; // both in cm
	double robotDiameter = 13.6;

	// final double angleError =
	// 1.0+((360.0-280.0)/360.0)+((360.0-354.0)/360.0)+((360.0-365.0)/360.0);
	// at full voltage, gives accurate turns at 40 power.
	double angleError = (360.0 / 305.0);

	NXTMotor motRight;
	NXTMotor motLeft;

	TouchSensor touch;

	LightSensor lightLeft;
	LightSensor lightRight;
	UltrasonicSensor ultrasonic;
	CompassHTSensor compass = null;

	public int baseMotorPower;

	State current_state;
	boolean stepMode;

	Map2D map;
	private int x;
	private int y;
	private int dir;

	BTConnection btc;
	DataInputStream inStream;
	DataOutputStream outStream;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getWheelDiameter() {
		return wheelDiameter;
	}

	public void setWheelDiameter(double wheelDiameter) {
		this.wheelDiameter = wheelDiameter;
	}

	public double getRobotDiameter() {
		return robotDiameter;
	}

	public void setRobotDiameter(double robotDiameter) {
		this.robotDiameter = robotDiameter;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int direction) {
		//debugln("setDir(" + direction + ")");
		this.dir = direction;
	}

	public static Robot getRobot() {
		if (robot == null) {
			robot = new Robot();
		}
		return robot;
	}

	private Robot() {
		// check which robot this and set diameters
		Properties props = Settings.getProperties();
		name = props.getProperty("lejos.usb_name");
		if (name.equals("NXTChris")) {
			wheelDiameter = 5.6; // both in cm
			robotDiameter = 13.6;
			// 1.0+((360.0-280.0)/360.0)+((360.0-354.0)/360.0)+((360.0-365.0)/360.0);
			// at full voltage, gives accurate turns at 40 power.
			angleError = (360.0 / 305.0);
		} else if (name.equals("ebay")) {
			wheelDiameter = 8.16;
			robotDiameter = 16.4;
			angleError = 1.0;
		} else if (name.equals("LineBacker")) {
			wheelDiameter = 5.6;
			robotDiameter = 17.0;
			angleError = 1.0;
		} else {
			// defaults for Jeremy?
			wheelDiameter = 5.6;
			robotDiameter = 17.0;
			angleError = 1.0;
		}

		motRight = new NXTMotor(MotorPort.B);
		motLeft = new NXTMotor(MotorPort.C);

		baseMotorPower = 40;

		motRight.setPower(baseMotorPower);
		motLeft.setPower(baseMotorPower);
		motRight.stop();
		motLeft.stop();

		lightLeft = new LightSensor(SensorPort.S1);
		lightRight = new LightSensor(SensorPort.S2);

		touch = new TouchSensor(SensorPort.S3);
		// compass = new CompassHTSensor(SensorPort.S3);

		ultrasonic = new UltrasonicSensor(SensorPort.S4);
		ultrasonic.continuous();

		current_state = StateStart.getInstance();
		stepMode = true;

		map = new Map2D();
		resetGrid();

		// actual BT connection is done in StateCommand
		btc = null;
		inStream = null;
		outStream = null;
	}

	public void resetGrid() {
		setX(1);
		setY(1);
		setDir(90);
		map.reset();
		map.grid[x][y] = 8;
	}

	public void run() {
		while (Button.ESCAPE.isUp() && !this.exit) {
			update();
		}
	}

	/**
	 * Print message over Bluetooth connection if it's running
	 * 
	 * @param msg
	 */
	public void debug(String msg) {
		if ((btc != null) && (outStream != null)) {
			try {
				outStream.writeUTF(msg);
				outStream.flush();
			} catch (IOException e) {
				LCD.drawString("Rbt IO Err", 0, 1);
			}
		} else {
			// System.out.println(msg);
			RConsole.print(msg);
		}
	}

	public void debugln(String msg) {
		debug(msg + "\n");
	}

	public void update() {
		if (current_state != null) {
			current_state.execute(this);
		}
	}

	/**
	 * This method changes the current state to the new state. It first calls
	 * the Exit() method of the current state, then assigns the new state to
	 * m_pCurrentState and finally calls the Entry() method of the new state.
	 */
	public void changeState(State new_state) {
		// make sure both states are both valid before attempting to
		// call their methods
		if (current_state == null || new_state == null) {
			System.err.println("STATE DOES NOT EXIST");
		}

		// call the exit method of the existing state
		current_state.exit(this);

		// change state to the new state
		current_state = new_state;

		// call the entry method of the new state
		current_state.enter(this);
	}

	public void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void right(double degrees) {
		motRight.setPower(baseMotorPower);
		motLeft.setPower(baseMotorPower);
		motRight.stop();
		motLeft.stop();

		double t_init, t_final;
		t_init = motLeft.getTachoCount();
		t_final = (int) (degrees * angleError)
				* (getRobotDiameter() / getWheelDiameter()) + t_init;

		while (motLeft.getTachoCount() < t_final) {
			motRight.backward();
			motLeft.forward();
		}
		motRight.stop();
		motLeft.stop();
	}

	public void left(double degrees) {
		motRight.setPower(baseMotorPower);
		motLeft.setPower(baseMotorPower);
		motRight.stop();
		motLeft.stop();

		double t_init, t_final;
		t_init = motRight.getTachoCount();
		t_final = (int) (degrees * angleError)
				* (getRobotDiameter() / getWheelDiameter()) + t_init;

		while (motRight.getTachoCount() < t_final) {
			motLeft.backward();
			motRight.forward();

		}
		motLeft.stop();
		motRight.stop();
	}

	public void turn(double degrees) { // positive # means right turn, negative
										// # means left turn
		motRight.setPower(baseMotorPower); // used when I don't want to think
											// which direction I'm turning
		motLeft.setPower(baseMotorPower);
		motRight.stop();
		motLeft.stop();

		double t_init, t_final;
		t_init = motLeft.getTachoCount();
		t_final = (int) (degrees * angleError)
				* (getRobotDiameter() / getWheelDiameter()) + t_init;

		while (motLeft.getTachoCount() < t_final) {
			motRight.backward();
			motLeft.forward();
		}
		motRight.stop();
		motLeft.stop();
	}

	public void forward(double distance) {
		// Makes the robot go forward for the given distance
		resetAngle();
		while ((motLeft.getTachoCount() * Math.PI / 180)
				* (getWheelDiameter() / 2) < distance) {
			double kP = 1;
			int leftAngle;
			int rightAngle;
			int error;

			forward();

			leftAngle = motLeft.getTachoCount();
			rightAngle = motRight.getTachoCount();
			error = leftAngle - rightAngle;
			motRight.setPower((int) (baseMotorPower + (error * kP)));
			motLeft.setPower((int) (baseMotorPower - (error * kP)));
		}
		stop();
		resetAngle();
	}

	public void backward(double distance) {
		// Makes the robot go backward for the given distance
		while ((motLeft.getTachoCount() * Math.PI / 180 * (getWheelDiameter() / 2)) > -distance) {
			double kP = 1;
			int leftAngle;
			int rightAngle;
			int error;

			backward();

			leftAngle = motLeft.getTachoCount();
			rightAngle = motRight.getTachoCount();
			error = leftAngle - rightAngle;
			motRight.setPower((int) (baseMotorPower - (error * kP)));
			motLeft.setPower((int) (baseMotorPower + (error * kP)));
		}
		stop();
		resetAngle();
	}

	public void forward() {
		// Makes the robot go forward in a straight line
		motLeft.forward();
		motRight.forward();

	}

	public void backward() {
		// Makes the robot go forward
		motLeft.backward();
		motRight.backward();
	}

	public void stop() {
		// Stops both wheel motors
		motLeft.stop();
		motRight.stop();
	}

	public void resetAngle() {
		motLeft.resetTachoCount();
		motRight.resetTachoCount();
	}

	/**
	 * left turns return negative angles, and right turns positive angles
	 * 
	 * @return
	 */
	public double getAngle() {

		double t_current = motRight.getTachoCount();
		return (t_current * (getWheelDiameter() / getRobotDiameter()) * (1 / angleError));
	}

	/**
	 * Functions used by StateGridRun, but which made more sense being placed in
	 * Robot
	 * 
	 * @return
	 */
	private void faceDir(int n) {
		int diff = n - getDir();
		debugln("faceDir: diff = " + diff);
		if (diff == 0) {
			return;
		} else {
			if (diff > 180) {
				right(Math.abs(360 - diff));
			} else if (diff > 0) {
				left(Math.abs(diff));
			} else if (diff < -180) {
				left(Math.abs(360 + diff));
			} else {
				right(Math.abs(diff));
			}
		}
		setDir(n);
	}

	private boolean goForward() {
		double factor = 1.0;

		double diffX = Math.cos(getDir() * (Math.PI / 180.0));
		double diffY = Math.sin(getDir() * (Math.PI / 180.0));

		if (diffX != Math.rint(diffX)) {
			factor = Math.abs(1 / diffX);
		}
		int nextX = x + (int) (factor * diffX);
		int nextY = y + (int) (factor * diffY);

		debugln("" + nextX + "/" + nextY + " dir=" + getDir());
		debugln("" + factor);
		if (ultrasonic.getDistance() > Map2D.SCALE) {
			debugln("path is clear");
			map.grid[x][y] = 9;
			forward(factor * Map2D.SCALE);
			x = nextX;
			y = nextY;
			map.grid[x][y] = 8;
			return true;
		} else {
			stop();
			Sound.buzz();
			map.grid[nextX][nextY] = 2;
			return false;
		}
	}

	public void goLeft() {
		faceDir(180);
		goForward();
	}

	public void goRight() {
		debugln("goRight");
		faceDir(0);
		goForward();
	}

	public void goUp() {
		faceDir(90);
		goForward();
	}

	public void goDown() {
		faceDir(270);
		goForward();
	}

	public void goUpRight() {
		faceDir(45);
		goForward();
	}

	public void goUpLeft() {
		faceDir(135);
		goForward();
	}

	public void goDownLeft() {
		faceDir(225);
		goForward();
	}

	public void goDownRight() {
		faceDir(315);
		goForward();
	}

	public void printMap() {
		debugln("------------");
		for (int j = Map2D.ROWS - 1; j >= 0; j--) {
			for (int i = 0; i < Map2D.COLS; i++) {
				debug(map.grid[i][j] + " ");
			}
			debugln("");
		}
	}

	public float getDegrees() {
		if (compass != null) {
			return compass.getDegrees();
		}
		return 0.0f;
	}

	public boolean getUseCommands() {
		return true;
	}

	public boolean getStepMode() {
		return stepMode;
	}

	public void setUseCommands() {
		// TODO - add boolean variable
		// useCommands = true;
	}

	public boolean getUseRConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean leftSideCheck() {
		// Checks to see wheter distane to left is longer than distance to
		// right. Returns true is left is longer.
		right(90);
		sleep(300);
		int rightDist = ultrasonic.getDistance();
		left(180);
		sleep(300);
		int leftDist = ultrasonic.getDistance();
		right(90);
		if (leftDist > rightDist)
			return true;
		else
			return false;
	}

	public void squareLeft(Obstacle obstacle) {
		left(90);
		forward(obstacle.getxLength());
		right(90);
		forward(obstacle.getyLength());
		right(90);
		forward(obstacle.getxLength());
		left(90);
	}

	public void squareRight(Obstacle obstacle) {
		right(90);
		forward(obstacle.getxLength());
		left(90);
		forward(obstacle.getyLength());
		left(90);
		forward(obstacle.getxLength());
		right(90);
	}
}