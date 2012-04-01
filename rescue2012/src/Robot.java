import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Properties;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.EOPD;
import lejos.nxt.comm.BTConnection;
//import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;

public class Robot {

	// this is a singleton
	private static Robot robot;

	public boolean exit = false;

	String name;

	// Set in constructor; depends on robot name
	double wheelDiameter; // both in cm
	double robotDiameter;
	double angleError;
	boolean leftBlack = false;
	boolean rightBlack = false;
	boolean avoidedLeft = true;
	float newNorth = 0.0f;

	NXTMotor motRight;
	NXTMotor motLeft;
	private int baseMotorPower;
	
	NXTRegulatedMotor motRegRight;
	NXTRegulatedMotor motRegLeft;

	TouchSensor touch;
	LightSensor lightLeft;
	LightSensor lightRight;
	ColorSensor colorsensor;
	UltrasonicSensor ultrasonic;
	boolean useUltrasonicObstacleDetect = true;
	CompassHTSensor compass;
	EOPD eopdSensor;
	RCJSensorMux sensorMux;

	
	State current_state;
	boolean stepMode;

	Map2D map;
	WaveFront nav;
	private int x;
	private int y;
	private int dir;
	boolean gridDone;

	BTConnection btc;
	DataInputStream inStream;
	DataOutputStream outStream;

	private Robot() {
		// check which robot this and set diameters, sensors
		Properties props = Settings.getProperties();
		name = props.getProperty("lejos.usb_name");
		if (name.equals("NXTChris")) {
			wheelDiameter = 5.6; // both in cm
			robotDiameter = 13.6;
			angleError = (360.0 / 305.0);
			lightLeft = new LightSensor(SensorPort.S1);
			lightRight = new LightSensor(SensorPort.S2);
			// touch = new TouchSensor(SensorPort.S3);
			compass = new CompassHTSensor(SensorPort.S3);
		} else if (name.equals("ebay")) {
			wheelDiameter = 5.6; // both in cm
			robotDiameter = 13.6;
			angleError = (360.0 / 305.0);
			// wheelDiameter = 8.16;
			// robotDiameter = 16.4;
			// angleError = 1.0;

			compass = new CompassHTSensor(SensorPort.S3);
			// eopdSensor = new EOPD(SensorPort.S3, true /*longRange*/);
			sensorMux = new RCJSensorMux(SensorPort.S3);
			sensorMux.configurate();
			lightLeft = new LightSensor(SensorPort.S1);
			lightRight = new LightSensor(SensorPort.S2);
		} else if (name.equals("LineBacker")) {
			wheelDiameter = 5.6;
			robotDiameter = 17.0;
			angleError = 1.0;
			lightLeft = new LightSensor(SensorPort.S1);
		} else if (name.equals("Dr_Lakata")) {
			wheelDiameter = 5.6;
			robotDiameter = 15.9;
			angleError = 1.0;
			//touch = new TouchSensor(SensorPort.S3);
			//lightLeft = new LightSensor(SensorPort.S1);
			//lightRight = new LightSensor(SensorPort.S2);
			compass = new CompassHTSensor(SensorPort.S3);
		} else if (name.equals("JPNXT")) {
			// defaults for Jeremy
			wheelDiameter = 4.96;
			robotDiameter = 13.5;
			angleError = 1.0;
			compass = new CompassHTSensor(SensorPort.S3);
			//colorsensor = new ColorSensor(SensorPort.S1);
			lightLeft = new LightSensor(SensorPort.S1);
			lightRight = new LightSensor(SensorPort.S2);

		} else {
			// Unknown robot
		}

		motRight = new NXTMotor(MotorPort.B);
		motLeft = new NXTMotor(MotorPort.C);
		motRegRight = new NXTRegulatedMotor(MotorPort.B);
		motRegLeft = new NXTRegulatedMotor(MotorPort.C);

		setBaseMotorPower(50);
		stop();

		// Touch and Compass sensor are different depending on robot name
		// touch = new TouchSensor(SensorPort.S3);
		// compass = new CompassHTSensor(SensorPort.S3);

		ultrasonic = new UltrasonicSensor(SensorPort.S4);
		ultrasonic.continuous();

		current_state = StateStart.getInstance();
		stepMode = true;

		map = new Map2D();
		resetGrid();
		nav = new WaveFront(map);
		gridDone = false;

		// actual BT connection is done in StateCommand
		btc = null;
		inStream = null;
		outStream = null;
	}
	
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
	
	public int getBaseMotorPower() {
		return baseMotorPower;
	}

	public void setBaseMotorPower(int baseMotorPower) {
		this.baseMotorPower = baseMotorPower;
		motRight.setPower(baseMotorPower);
		motLeft.setPower(baseMotorPower);
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int direction) {
		// debugln("setDir(" + direction + ")");
		while (direction < 0) {
			direction += 360;
		}
		while (direction > 360) {
			direction -= 360;
		}
		this.dir = direction;
	}

	public int getLightLeft() {
		return (lightLeft.getLightValue());
	}

	public int getLightRight() {
		return (lightRight.getLightValue());
	}

	public static Robot getRobot() {
		if (robot == null) {
			robot = new Robot();
		}
		return robot;
	}

	public void resetGrid() {
		setX(1);
		setY(1);
		setDir(90);
		map.reset();
		map.grid[x][y] = Map2D.ROBOT;
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
	
	public void run() {
		while (Button.ESCAPE.isUp() && !this.exit) {
			update();
		}
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
	
	public void goToHeading(double angle){
		if(angle < 0) {
			angle += 360;
		}
		float diff = (float)(angle - getHeading());
		if (diff > 0) {
			correctLeft(diff);
		}
		if (diff < 0) {
			correctRight(diff);
		}
	}

	public void correctRight(float degrees) {
		float origin = getHeading();
		float expectedVal = origin - degrees;
		right(degrees);
		sleep(100);
		float val = getHeading();
		debugln("" + val);
		val = getHeading();
		debugln("" + val);
		if (expectedVal < 0) {
			expectedVal = expectedVal + 360;
		}

		while (val != expectedVal) {
			if (val - expectedVal > 180) {
				expectedVal = expectedVal + 360;
			}
			if (expectedVal - val > 180) {
				val = val + 360;
			}
			if (val > expectedVal) {
				right(val - expectedVal);
			} else {
				left(expectedVal - val);
			}
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
			}
			sleep(100);
			val = getHeading();
			debugln("" + expectedVal);
		}
	}

	public void correctLeft(float degrees) {
		float origin = getHeading();
		float expectedVal = origin + degrees;
		left(degrees);
		sleep(100);
		float val = getHeading();
		debugln("" + val);
		val = getHeading();
		debugln("" + val);

		while (val != expectedVal) {
			if (expectedVal < 0) {
				expectedVal = expectedVal + 360;
			}
			if (val - expectedVal > 180) {
				expectedVal = expectedVal + 360;
			}
			if (expectedVal - val > 180) {
				val = val + 360;
			}
			if (val > expectedVal) {
				right(val - expectedVal);
			} else {
				left(expectedVal - val);
			}
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
			}

			sleep(100);
			val = getHeading();
			debugln("" + expectedVal);
		}
	}

	// ---------- begin new Right/Left -----------
	public void right(double degrees) {
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(-angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			sleep(10);
		}
		stop();
		// debugln("comp " + getHeading());
	}

	public void left(double degrees) {
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(-angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			sleep(10);
		}
		stop();
		// debugln("comp " + getHeading());
	}

	// ---------- end of new Right/Left -----------

	public void forward() {
		// Makes the robot go forward in a straight line
		motRegRight.forward();
		motRegLeft.forward();
	}
	
	public void forward(double distance) {
		// Makes the robot go forward for the given distance
		int angle;
		angle = (int) Util.round(distance/(getWheelDiameter()*Math.PI)*360);
		
		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			sleep(10);
		}
		stop();	
	}

	public void backward() {
		// Makes the robot go forward
		motRegRight.backward();
		motRegLeft.backward();
	}
	
	public void backward(double distance) {
		// Makes the robot go backward for the given distance
		int angle;
		angle = (int) Util.round(distance/(getWheelDiameter()*Math.PI)*360);
	
		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(-angle, true);
		motRegLeft.rotate(-angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			sleep(10);
		}
		stop();
	}
	
	public void stop() {
		// Stops both wheel motors
		motRegLeft.stop();
		motRegRight.stop();
		motRegLeft.suspendRegulation();
		motRegRight.suspendRegulation();
		motLeft.stop();
		motRight.stop();
	}
	
	public boolean obstacleSideCheck() {
		// Checks to see whether distance to left is longer than distance to
		// right. Returns true is left is longer.
		sleep(500);
		correctRight(90);
		sleep(500);
		
		int n = 5;
		int rightDist = 0;
		int aveRightDist = 0;
		for(int i = 0; i < n; i++){
			rightDist = rightDist+ultrasonic.getDistance();
		}
		aveRightDist = rightDist/n;
		
		correctLeft(180);
		sleep(500);
		
		int leftDist = 0;
		int aveLeftDist = 0;
		for(int i = 0; i < n; i++){
			leftDist = leftDist+ultrasonic.getDistance();
		}
		aveLeftDist = leftDist/n;
		correctRight(90);
		debugln("right: " + aveRightDist + ". Left: " + aveLeftDist);
		if (aveLeftDist > aveRightDist) {
			return true;
		}else {
			return false;
		}
	}

	public boolean forwardLookForLine(double distance) {
		// Makes the robot go forward for the given distance, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;

		resetAngle();
		while ((motLeft.getTachoCount() * Math.PI / 180) * (wheelDiameter / 2) < distance) {

			if (leftBlack != true) {
				if (lightLeft.getLightValue() < 45) {
					leftBlack = true;
				}
			}

			if (rightBlack != true) {
				if (lightRight.getLightValue() < 45) {
					rightBlack = true;
				}
			}

			if (leftBlack == true && rightBlack == true) {
				debug("I Should Stop Here");
				stop();
				return true;
			}

			forward();


			if (rightBlack == true) {
				motRight.setPower(-20);
				motLeft.setPower(50);
			} else if (leftBlack == true) {
				motLeft.setPower(-20);
				motRight.setPower(50);
			} 

		}

		stop();
		resetAngle();
		return false;
	}
	
	public boolean correctLeftLine(float degrees) {
		boolean line = false;
		float origin = getHeading();
		float expectedVal = origin + degrees;
		line = turnLeftLookForLine(degrees);
		sleep(100);
		float val = getHeading();
		debugln("" + val);
		val = getHeading();
		debugln("" + val);

		while ((val != expectedVal) && !line) {
			if (expectedVal < 0) {
				expectedVal = expectedVal + 360;
			}
			if (val - expectedVal > 180) {
				expectedVal = expectedVal + 360;
			}
			if (expectedVal - val > 180) {
				val = val + 360;
			}
			if (val > expectedVal) {
				line = turnRightLookForLine(val - expectedVal);
			} else {
				line = turnLeftLookForLine(expectedVal - val);
			}
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
			}

			sleep(100);
			val = getHeading();
			debugln("" + expectedVal);
		}
		return line;
	}

	public boolean correctRightLine(float degrees) {
		boolean line = false;
		float origin = getHeading();
		float expectedVal = origin - degrees;
		line = turnRightLookForLine(degrees);
		sleep(100);
		float val = getHeading();
		debugln("" + val);
		val = getHeading();
		debugln("" + val);
		if (expectedVal < 0) {
			expectedVal = expectedVal + 360;
		}

		while ((val != expectedVal) && !line) {
			if (val - expectedVal > 180) {
				expectedVal = expectedVal + 360;
			}
			if (expectedVal - val > 180) {
				val = val + 360;
			}
			if (val > expectedVal) {
				line = turnRightLookForLine(val - expectedVal);
			} else {
				line = turnLeftLookForLine(expectedVal - val);
			}
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
			}
			sleep(100);
			val = getHeading();
			debugln("" + expectedVal);
		}
		return line;
	}

	public boolean turnLeftLookForLine(double degrees) {
		// Makes the robot turn left the given degrees, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(-angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			if (leftBlack != true) {
				if (lightLeft.getLightValue() < 45) {
					leftBlack = true;
				}
			}

			if (rightBlack != true) {
				if (lightRight.getLightValue() < 45) {
					rightBlack = true;
				}
			}

			if (leftBlack == true && rightBlack == true) {
				debug("I Should Stop Here");
				stop();
				return true;
			}

		}
		if (rightBlack == true) {
			motRight.setPower(-40);
			motLeft.setPower(50);
		} else if (leftBlack == true) {
			motLeft.setPower(-40);
			motRight.setPower(50);
		}

		stop();
		setDir((int) (getDir() + degrees));
		return false;
	}

	public boolean turnRightLookForLine(double degrees) {
		// Makes the robot turn right for the given degrees, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(-angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			if (leftBlack != true) {
				if (lightLeft.getLightValue() < 45) {
					leftBlack = true;
				}
			}

			if (rightBlack != true) {
				if (lightRight.getLightValue() < 45) {
					rightBlack = true;
				}
			}

			if (leftBlack == true && rightBlack == true) {
				debug("I Should Stop Here");
				stop();
				return true;
			}
		}
		if (rightBlack == true) {
			motRight.setPower(-40);
			motLeft.setPower(50);
		} else if (leftBlack == true) {
			motLeft.setPower(-40);
			motRight.setPower(50);
		}

		stop();
		setDir((int) (getDir() + degrees));
		return false;

	}

	public void findLineRight() {
		// makes the robot turn right, looking to reposition itself so as to
		// resume
		// normal line following.
		int logic = 0;
		robot.turnRightLookForLine(180);
		// while (logic != 2) {
		// if (logic == 0) {
		// if (lightRight.getLightValue() < 45) {
		// logic = 1;
		// }
		// } else if (logic == 1)
		// if (lightRight.getLightValue() > 45) {
		// logic = 2;
		// }
		// }
		while (lightLeft.getLightValue() > 45) {
		}
		// Sound.beepSequence();
		robot.stop();
	}

	public void findLineLeft() {
		// makes the robot turn left, looking to reposition itself so as to
		// resume
		// normal line following.
		robot.correctLeftLine(180);
		while (lightRight.getLightValue() > 45) {
		}
		// Sound.beepSequence();
		robot.stop();
	}

	public void findCanCoarse() {
		setBaseMotorPower(20);
		robot.left(45);

		int lastValue = 0;
		int currentValue = 0;
		int difference = 0;
		setBaseMotorPower(20);
		motRight.backward();
		motLeft.forward();
		while (true) {

			if (Math.abs(lastValue - currentValue) > 5) {
				break;
			}
			lastValue = currentValue;
			if (ultrasonic.getDistance() < 35) {
				currentValue = ultrasonic.getDistance();
			}
			difference = lastValue - currentValue;
			debugln("" + difference);
		}

		// while (motRegRight.isMoving() || motRegLeft.isMoving()) {
		// debugln("F " + lowestV);
		// // debugln("N " + ultrasonic.getDistance());
		// if (ultrasonic.getDistance() < lowestV) {
		// lowestV = ultrasonic.getDistance();
		// }
		//
		// }
		// motRegLeft.backward();
		// motRegRight.forward();
		// debugln("S" + ultrasonic.getDistance());
		//
		// while (ultrasonic.getDistance() > lowestV + 1) {
		// debugln("R " + ultrasonic.getDistance());
		// }
		// sleep(20);
		// motRegRight.suspendRegulation();
		// motRegLeft.suspendRegulation();
		// robot.stop();
		// robot.forward(lowestV - 9);

		robot.sleep(200);
		debug("" + getHeading());
		double headL = getHeading();
		correctRight(60);
		setBaseMotorPower(30);
		motLeft.backward();
		motRight.forward();
		lastValue = 0;
		currentValue = 0;
		difference = 0;
		while (true) {

			if (Math.abs(lastValue - currentValue) > 5) {
				break;
			}
			lastValue = currentValue;
			if (ultrasonic.getDistance() < 35) {
				currentValue = ultrasonic.getDistance();
			}
			difference = lastValue - currentValue;
			debugln("" + difference);
		}
		robot.sleep(200);
		debug("" + getHeading());
		double headR = getHeading();

		double headC = (headR + headL) / 2;
		debugln("" + headC);
		goToHeading(Math.round(headC)); // findCanFine();
		stop();
		robot.forward();
		while (ultrasonic.getDistance() > 15);
		stop();
	}

	public void findCanFine() {
		int dist = ultrasonic.getDistance();
		motLeft.backward();
		motRight.forward();
		while (true) {
			dist = ultrasonic.getDistance();
			if (dist < 30) {
				if (dist - ultrasonic.getDistance() > 0) {
					motLeft.backward();
					motRight.forward();
				} else {
					stop();
					left(5);
					break;
				}
			}
		}
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
	public void faceDir(int n) {
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
		// setDir(n);
	}

	public void faceDir(char c) {

		if ((c == 'w')) {
			faceDir(90);

		} else if (c == 'e') {
			faceDir(45);

		} else if (c == 'd') {
			faceDir(0);

		} else if (c == 'c') {
			faceDir(315);

		} else if (c == 'x') {
			faceDir(270);

		} else if (c == 'z') {
			faceDir(225);

		} else if (c == 'a') {
			faceDir(180);

		} else if (c == 'q') {
			faceDir(135);

		} else {
			Sound.playTone(440, 100);
			sleep(100);
		}

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
			map.grid[x][y] = Map2D.ROBOT;
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

	public void followPath(int goal) {
		nav.makeWave(goal);
		String route = nav.makePath();
		char dir;
		for (int n = 0; n < route.length() - 1; n++) {
			dir = route.charAt(n);

			if ((dir == 'w')) {
				goUp();

			} else if (dir == 'e') {
				goUpRight();

			} else if (dir == 'd') {
				goRight();

			} else if (dir == 'c') {
				goDownRight();

			} else if (dir == 'x') {
				goDown();

			} else if (dir == 'z') {
				goDownLeft();

			} else if (dir == 'a') {
				goLeft();

			} else if (dir == 'q') {
				goUpLeft();

			} else {
				Sound.playTone(440, 100);
				sleep(100);
			}
		}

	}

	public void goTo(int x, int y) {
		String route = nav.pathTo(x, y);
		char dir;
		for (int n = 0; n < route.length() - 1; n++) {
			dir = route.charAt(n);

			if ((dir == 'w')) {
				goUp();

			} else if (dir == 'e') {
				goUpRight();

			} else if (dir == 'd') {
				goRight();

			} else if (dir == 'c') {
				goDownRight();

			} else if (dir == 'x') {
				goDown();

			} else if (dir == 'z') {
				goDownLeft();

			} else if (dir == 'a') {
				goLeft();

			} else if (dir == 'q') {
				goUpLeft();

			} else {
				Sound.playTone(440, 100);
				sleep(100);
			}
		}

	}

	public void liftCan() {
		// TODO write code after claw completed
	}

	public void dropCan() {
		// TODO write code after claw completed
	}

	public void facePlatform() {
		char dir = nav.dirTo(Map2D.PLATFORM);

		if (dir == 'e' || dir == 'c' || dir == 'z' || dir == 'q') {
			faceDir(nav.dirTo(Map2D.PLATFORM));
		} else {
			if (getX() <= 2) {
				goTo(2, 2);
			} else {
				goTo(3, 2);
			}
			faceDir(nav.dirTo(Map2D.PLATFORM));
		}
	}

	public float getDegrees() {
		if (compass != null) {
			return compass.getDegrees();
		}
		return 0.0f;
	}

	public void setNewNorth() {
		newNorth = getHeading();
	}

	public float getHeading() {
		float cReading = 0.0f;
		float fixedCReading = 0.0f;

		if (compass != null) {
			cReading = compass.getDegrees();
		}
		fixedCReading = 360 - cReading + 90;
		if (fixedCReading >= 360) {
			fixedCReading = fixedCReading - 360;
		}
		fixedCReading = fixedCReading - newNorth;
		if (fixedCReading < 0) {
			fixedCReading = fixedCReading + 360;
		}
		return fixedCReading;
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

	public int getEOPD() {
		if (eopdSensor != null) {
			// Return values between 4 and 100
			return (eopdSensor.readRawValue() * 100) / 1023;
		} else if (sensorMux != null) {
			return sensorMux.readEOPD(3);
		}
		return -1;
	}

	public void status() {
		debugln(" dir = " + robot.getDir());
		debugln(" X/Y = " + robot.getX() + ", " + robot.getY());
		debugln("dist = " + robot.ultrasonic.getDistance());
		debugln("comp = " + robot.getDegrees());
		if (sensorMux != null) {
			debugln("SMUX: " + sensorMux.getProductID() + " " + sensorMux.getVersion());
			debugln("Type: " + sensorMux.getType());
			debugln("Bat: " + sensorMux.isBatteryLow());
		}
	}
}