package rescue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Properties;

import lejos.nxt.*;
import lejos.nxt.addon.*;
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
	int thresh = 4;
	boolean leftBlack = false;
	boolean rightBlack = false;
	boolean avoidedLeft = true;
	boolean isOnRamp = false;
	int threshSilver = 65;
	int threshBlack = 50;
	float compOffset = 0.0f;
	double cardinal0 = 94.0;
	double cardinal45 = 132.0;
	double cardinal90 = 174.0;
	double cardinal135 = 218.0;
	double cardinal180 = 270.0;
	double cardinal225 = 318.0;
	double cardinal270 = 358.0;
	double cardinal315 = 45.0;
	double doorHeading = 0.0; //measured heading of room entrance
	double kScale = 78.36;
	double kError = 4.7;  // orig value 5.7 returning -1 values
	NXTMotor motRight;
	NXTMotor motLeft;
	NXTMotor arduPower;
	private int baseMotorPower;

	NXTRegulatedMotor motRegRight;
	NXTRegulatedMotor motRegLeft;
	private int baseMotorAcceleration;
	private boolean isRegulated;

	ArduRCJ servoDriver;

	TouchSensor touch;
	LightSensor lightLeft;
	LightSensor lightRight;
	ColorSensor colorsensor;
	UltrasonicSensor ultrasonic;
	boolean useUltrasonicObstacleDetect = true;
	AccelHTSensor accel;
	CompassHTSensor compass;
	EOPD eopd;

	State current_state;
	boolean stepMode;
	boolean enableTurnBeeps;

	Map2D map;
	WaveFront nav;
	private int x;
	private int y;
	boolean canFound;
	boolean platformFound;
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
			servoDriver = new ArduRCJ(SensorPort.S1);
			arduPower = new NXTMotor(MotorPort.A); // power arduino
			sleep(1000);
			servoDriver.servoCompass.setAngle(0);

			accel = new AccelHTSensor(SensorPort.S2);
			compass = new CompassHTSensor(SensorPort.S3);
			ultrasonic = new UltrasonicSensor(SensorPort.S4);
			ultrasonic.continuous();
		} else if (name.equals("ebay")) {
			wheelDiameter = 5.6; // both in cm
			robotDiameter = 13.6;
			angleError = 1; // (360.0/276.0)

			lightLeft = new LightSensor(SensorPort.S1);
			lightRight = new LightSensor(SensorPort.S2);
			compass = new CompassHTSensor(SensorPort.S3);
			// eopdSensor = new EOPD(SensorPort.S3, true /*longRange*/);
			servoDriver = new ArduRCJ(SensorPort.S4);
			arduPower = new NXTMotor(MotorPort.A); // power arduino
			eopd = new EOPD(SensorPort.S1, true /* longRange */);
			ultrasonic = new UltrasonicSensor(SensorPort.S2);
			ultrasonic.continuous();
		} else if (name.equals("LineBacker")) {
			wheelDiameter = 5.6;
			robotDiameter = 17.0;
			angleError = 1.0;
			lightLeft = new LightSensor(SensorPort.S1);
			ultrasonic = new UltrasonicSensor(SensorPort.S4);
			ultrasonic.continuous();
		} else if (name.equals("Dr_Lakata")) {
			wheelDiameter = 5.6;
			robotDiameter = 15.9;
			angleError = 1.0;
			eopd = new EOPD(SensorPort.S2, true /* longRange */);
			// lightLeft = new LightSensor(SensorPort.S1);
			// lightRight = new LightSensor(SensorPort.S2);
			// touch = new TouchSensor(SensorPort.S3);
			compass = new CompassHTSensor(SensorPort.S3);
			ultrasonic = new UltrasonicSensor(SensorPort.S4);
			ultrasonic.continuous();
		} else if (name.equals("JPNXT")) {
			// defaults for Jeremy
			wheelDiameter = 4.96;
			robotDiameter = 13.5;
			angleError = 1.0;

			// colorsensor = new ColorSensor(SensorPort.S1);
			lightLeft = new LightSensor(SensorPort.S1);
			lightRight = new LightSensor(SensorPort.S2);
			compass = new CompassHTSensor(SensorPort.S3);
			ultrasonic = new UltrasonicSensor(SensorPort.S4);
			ultrasonic.continuous();

		} else {
			// Unknown robot
		}

		motRight = new NXTMotor(MotorPort.B);
		motLeft = new NXTMotor(MotorPort.C);
		setBaseMotorPower(60);

		motRegRight = new NXTRegulatedMotor(MotorPort.B);
		motRegLeft = new NXTRegulatedMotor(MotorPort.C);
		setBaseMotorAcceleration(1500);
		stop();

		// Touch and Compass sensor are different depending on robot name
		// touch = new TouchSensor(SensorPort.S3);
		// compass = new CompassHTSensor(SensorPort.S3);

		current_state = StateStart.getInstance();
		stepMode = false;
		enableTurnBeeps = false;

		map = new Map2D();
		resetGrid();
		nav = new WaveFront(map);
		gridDone = false;

		// actual BT connection is done in StateCommand
		btc = null;
		inStream = null;
		outStream = null;

		// Power up Roboduino
		setArduinoPoweredUp(true);
		sleep(2000);

	}

	/**
	 * @return true if Arduino is On (powered up), false if it's Off
	 */
	public boolean getArduinoPoweredUp() {
		if (arduPower != null) {
			if (arduPower.getPower() == 100) {
				return true;
			}
		}
		return false;
	}

	public void setArduinoPoweredUp(boolean powerOn) {
		if (arduPower != null) {
			if (powerOn == true) {
				arduPower.setPower(100);
				arduPower.forward();
			} else {
				arduPower.setPower(0);
				arduPower.flt();
			}
		}
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

	public int getBaseMotorAcceleration() {
		return baseMotorAcceleration;
	}

	public void setBaseMotorAcceleration(int baseMotorAcceleration) {
		this.baseMotorAcceleration = baseMotorAcceleration;
		motRegRight.setAcceleration(baseMotorAcceleration);
		motRegLeft.setAcceleration(baseMotorAcceleration);
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
		return (int)getHeading();
	}
	
	public float getCompOffset(){
		return compOffset;
	}
	
	public void setCompOffset(float val){
		compOffset = val;
	}

	public void setDir(int direction) {
		compOffset = 0.0f;
		double compHeading;
		while (direction < 0) {
			direction += 360;
		}
		while (direction > 360) {
			direction -= 360;
		}
		compHeading = getHeading();
		compOffset = (int)(Util.round(compHeading - direction));
		debugln("compOffset:" + compOffset);
	}

	public float getHeading() {
		float cReading = 0.0f;
		float correctedCReading = 0.0f;

		if (compass != null) {
			if(!isCompassUp()) {
				liftCompass();
				sleep(1000);
			}
			cReading = compass.getDegrees();
		}
		correctedCReading = 360 - cReading + 90;
		//debugln("Old Heading: " + correctedCReading);
		if (correctedCReading >= 360) {
			correctedCReading = correctedCReading - 360;
		}
		correctedCReading = correctedCReading - compOffset;
		//debugln("New Heading: " + correctedCReading);
		if (correctedCReading < 0) {
			correctedCReading = correctedCReading + 360;
		}
		return correctedCReading;
	}

	public int getLightLeft() {
		int val = 1024 - servoDriver.readLightLeft();
		return (val / 10);
	}

	public int getLightRight() {
		int val = 1024 - servoDriver.readLightRight();
		return (val / 10);
	}

	public int getEOPDScaled() {
		int val = 1024 - servoDriver.readEOPD();
		return (val / 10);
	}

	public int getEOPDRaw() {
		int val = servoDriver.readEOPD();
		return val;
	}

	public static Robot getRobot() {
		if (robot == null) {
			robot = new Robot();
		}
		return robot;
	}

	public static void playTone(int freq, int duration) {
		if (robot.enableTurnBeeps == true) {
			Sound.playTone(freq, duration);
		}
	}

	public void toggleBeeps() {
		enableTurnBeeps = !enableTurnBeeps;
	}
	
	public void resetGrid() {
		setX(1);
		setY(1);
		canFound = false;
		platformFound = false;
		gridDone = false;
		
		//setDir(90);
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
	
	public void compassCardinalCalibrate(){
		while(Button.ENTER.isUp()){
		}
		cardinal0 = getHeading() + compOffset;
		debugln("cardinal0 = "+cardinal0);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal45 = getHeading() + compOffset;
		debugln("cardinal45 = "+cardinal45);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal90 = getHeading() + compOffset;
		debugln("cardinal90 = "+cardinal90);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal135 = getHeading() + compOffset;
		debugln("cardinal135 = "+cardinal135);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal180 = getHeading() + compOffset;
		debugln("cardinal180 = "+cardinal180);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal225 = getHeading() + compOffset;
		debugln("cardinal225 = "+cardinal225);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal270 = getHeading() + compOffset;
		debugln("cardinal270 = "+cardinal270);
		while(Button.ENTER.isDown()){
		}
		while(Button.ENTER.isUp()){
		}
		cardinal315 = getHeading() + compOffset;
		debugln("cardinal315 = "+cardinal315);
		while(Button.ENTER.isDown()){
		}	
	}

	public void goToHeading(double angle) {
		if (angle < 0) {
			angle += 360;
		}
		if (angle > 360) {
			angle -= 360;
		}

		float diff = (float) (angle - getHeading());
		if (diff < -180) {
			diff = diff + 360;
		}
		if (diff > 180) {
			diff = diff - 360;
		}
		if (diff > 0) {
			correctLeft(Math.abs(diff));
		}
		if (diff < 0) {
			correctRight(Math.abs(diff));
		}
	}

	public void correctRight(float degrees) {
		float origin = getHeading();
		float expectedVal = origin - degrees;

		right(degrees);
		sleep(500);
		float val = 0;
		val = getHeading();
		if (expectedVal < 0) {
			expectedVal = expectedVal + 360;
		}

		while (val != expectedVal) {
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
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

			sleep(700);
			val = getHeading();
			// debugln("" + expectedVal);
		}

		sleep(100);
		//debugln("Original Heading: " + firstDir);
		//debugln("Current Heading: " + getDir());
	}

	public void correctLeft(float degrees) {
		float origin = getHeading();
		float expectedVal = origin + degrees;

		left(degrees);
		sleep(500);
		float val = getHeading();

		while (val != expectedVal) {
			if (expectedVal == 360) {
				expectedVal = 0;
			}
			if (expectedVal > 360) {
				expectedVal = expectedVal - 360;
			}
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

			sleep(700);
			val = getHeading();
		}

		sleep(100);
		//debugln("Original Heading: " + firstDir);
		//debugln("Current Heading: " + getDir());
	}

	// ---------- begin new Right/Left -----------
	public void right(double degrees) {
		if (!isRegulated) {
			isRegulated = true;
		}

		int angle = (int) ((degrees * angleError) * (getRobotDiameter() / getWheelDiameter()));

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
		if (!isRegulated) {
			isRegulated = true;
		}

		int angle = (int) ((degrees * angleError) * (getRobotDiameter() / getWheelDiameter()));

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
		if (!isRegulated) {
			isRegulated = true;
		}
		motRegRight.forward();
		motRegLeft.forward();
	}

	public void forward(double distance) {
		// Makes the robot go forward for the given distance
		if (!isRegulated) {
			isRegulated = true;
		}
		int angle;
		angle = (int) Util.round(distance / (getWheelDiameter() * Math.PI)
				* 360);

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			sleep(10);
		}
	}

	public void backward() {
		// Makes the robot go forward
		if (!isRegulated) {
			isRegulated = true;
		}
		motRegRight.backward();
		motRegLeft.backward();
	}

	public void backward(double distance) {
		// Makes the robot go backward for the given distance
		if (!isRegulated) {
			isRegulated = true;
		}
		int angle;
		angle = (int) Util.round(distance / (getWheelDiameter() * Math.PI)
				* 360);

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
		if (isRegulated) {
			motRegLeft.stop(true);
			motRegRight.stop(true);
			Robot.playTone(440, 10);
			sleep(100);
			motRegLeft.suspendRegulation();
			motRegRight.suspendRegulation();
		} else {
			motLeft.stop();
			motRight.stop();
		}
	}

	public boolean obstacleSideCheck() {
		// Checks to see whether distance to left is longer than distance to
		// right. Returns true is left is longer.
		sleep(500);
		correctRight(90);
		sleep(1000);

		int aveRightDist;
		aveRightDist = sonicAverage();

		correctLeft(180);
		sleep(500);

		int aveLeftDist;
		aveLeftDist = sonicAverage();
		debugln("right: " + aveRightDist + ". Left: " + aveLeftDist);
		if (aveLeftDist > aveRightDist) {
			return true;
		} else {
			right(180);
			return false;
		}
	}

	public boolean forwardLookForLine(double distance) {
		// Makes the robot go forward for the given distance, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;

		// Makes the robot go forward for the given distance
		if (!isRegulated) {
			isRegulated = true;
		}
		int angle;
		angle = (int) Util.round(distance / (getWheelDiameter() * Math.PI)
				* 360);

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {

			if (leftBlack != true) {
				if (getLightLeft() < threshBlack) {
					leftBlack = true;
				}
			}

			if (rightBlack != true) {
				if (getLightRight() < threshBlack) {
					rightBlack = true;
				}
			}

			if (leftBlack == true && rightBlack == true) {
				debug("I Should Stop Here");
				stop();
				return true;
			}

		}

		// forward();
		//
		// if (rightBlack == true) {
		// motRight.setPower(-20);
		// motLeft.setPower(50);
		// } else if (leftBlack == true) {
		// motLeft.setPower(-20);
		// motRight.setPower(50);
		// }

		// stop();
		return false;
	}

	public boolean correctLeftLine(float degrees) {
		boolean line = false;
		float origin = getHeading();
		float expectedVal = origin + degrees;
		line = turnLeftLookForLine(degrees);
		sleep(100);
		float val = getHeading();
		// debugln("" + val);
		val = getHeading();
		// debugln("" + val);

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
			// debugln("" + expectedVal);
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
		// debugln("" + val);
		val = getHeading();
		// debugln("" + val);
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
			// debugln("" + expectedVal);
		}
		return line;
	}

	public boolean turnLeftLookForLine(double degrees) {
		// Makes the robot turn left the given degrees, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		if (!isRegulated) {
			isRegulated = true;
		}

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(angle, true);
		motRegLeft.rotate(-angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			if (getLightLeft() < threshBlack) {
				leftBlack = true;
				stop();
				return true;
			}

			// if (rightBlack != true) {
			// if (lightRight.getLightValue() < 45) {
			// rightBlack = true;
			// }
			// }
			// if (leftBlack == true && rightBlack == true) {
			// debug("I Should Stop Here");
			// stop();
			// return true;
			// }

		}
		// if (rightBlack == true) {
		// motRight.setPower(0);
		// motLeft.setPower(50);
		// debugln("rightblack = true");
		// } else if (leftBlack == true) {
		// motLeft.setPower(0);
		// motRight.setPower(50);
		// debugln("leftblack = true");
		// }

		stop();
		return false;
	}

	public boolean turnRightLookForLine(double degrees) {
		// Makes the robot turn right for the given degrees, and stop if it
		// sees a line
		leftBlack = false;
		rightBlack = false;
		int angle = (int) ((degrees /* angleError */) * (getRobotDiameter() / getWheelDiameter()));

		if (!isRegulated) {
			isRegulated = true;
		}

		motRegRight.resetTachoCount();
		motRegLeft.resetTachoCount();
		motRegRight.rotate(-angle, true);
		motRegLeft.rotate(angle, true);
		while (motRegRight.isMoving() || motRegLeft.isMoving()) {
			// if (leftBlack != true) {
			// if (lightLeft.getLightValue() < 45) {
			// leftBlack = true;
			// }
			// }
			if (getLightRight() < threshBlack) {
				rightBlack = true;
				stop();
				return true;
			}
			// if (leftBlack == true && rightBlack == true) {
			// debug("I Should Stop Here");
			// stop();
			// return true;
			// }
		}

		stop();
		return false;

	}

	public void findLineRight() {
		// makes the robot turn right, looking to reposition itself so as to
		// resume
		// normal line following.
		debugln("enter findline");
		stop();
		// motRegLeft.forward();
		correctRightLine(180);
		// while (lightLeft.getLightValue() > 45) {
		// }
		stop();
		debugln("stop findline");
	}

	public void findLineLeft() {
		// makes the robot turn left, looking to reposition itself so as to
		// resume
		// normal line following.
		debugln("enter findline");
		stop();
		// motRegRight.forward();
		correctLeftLine(180);
		// while (lightRight.getLightValue() > 45) {
		// }
		stop();
		debugln("stop findline");
	}

	public double eopdAverage() {
		int average = 0;
		int count = 5;

		while (count > 0) {
			average = average + (int) getEopdDistance();
			count = count - 1;
		}
		average = average / 5;

		return average;
	}

	public double getEopdDistance() {

		double distance = Util.round(kScale/Math.sqrt((double) getEOPDRaw()) - kError);
		debugln("" + distance + " (" + kScale/Math.sqrt((double) getEOPDRaw()) + ")");
		return distance;
	}

	public double getEopdVal() {
		int processedVal = eopd.processedValue();

		return processedVal;
	}

	public int sonicAverage() {
		int average = 0;
		int count = 0;
		while (count < 5) {

			average += ultrasonic.getDistance();

			count++;
		}
		average = average / count;
		return average;
	}

	public void eopdContPoll() {

		while (true) {
			debugln("" + getEopdDistance());
			sleep(20);
		}
	}

	public void eopdCal() {

		debugln("" + getEopdDistance());

	}

	public void findCanCoarse() {
		setBaseMotorPower(35);
		motRegRight.setSpeed(500);
		motRegLeft.setSpeed(500);
		robot.correctLeft(75);
		int storage;

		int currentValue = 40;
		if ((int) eopdAverage() < 40 && (int) eopdAverage() > 0) {
			currentValue = (int) eopdAverage();
		}
		int lastValue = currentValue;
		int difference = 0;

		motLeft.forward();
		motRight.backward();

		while (true) {

			if (lastValue - currentValue > thresh) {
				break;
			}
			lastValue = currentValue;
			storage = (int) eopdAverage();
			if (storage < 40 && storage > 0) {
				currentValue = storage;
			}
			difference = lastValue - currentValue;
			debugln("Cur " + currentValue);
			debugln("Last " + lastValue);
			debugln("Dif " + difference);
			sleep(10);
			// Delete this sweep when done
		}
		robot.stop();
		robot.sleep(1000);
		debug("Head L " + getHeading());
		double headL = getHeading();
		// if (headL > 180)
		// {headL = headL - 360;}
		correctRight(90);
		currentValue = 40;
		if ((int) eopdAverage() < 40 && (int) eopdAverage() > 0) {
			currentValue = (int) eopdAverage();
		}
		lastValue = currentValue;

		motLeft.backward();
		motRight.forward();
		while (true) {

			if (lastValue - currentValue > thresh) {
				break;
			}
			lastValue = currentValue;
			storage = (int) eopdAverage();
			if (storage < 40 && storage > 0) {
				currentValue = storage;
			}
			difference = lastValue - currentValue;
			debug("Cur " + currentValue);
			debug(" | Last " + lastValue);
			debugln(" | Dif " + difference);
		}
		robot.stop();
		robot.sleep(1000);
		debugln("headR " + getHeading());
		double headR = getHeading();
		// if (headR > 180)
		// {headR = headR - 360;}
		double headC;

		if (headL < headR) {
			headC = Util.round((headR + headL) / 2 - 180);
		} else {
			headC = Util.round((headR + headL) / 2);
		}

		debugln("Head C " + headC);
		goToHeading(Util.round(headC)); // findCanFine();
		stop();
	}

	public void findCanCoarseSonic() {
		setBaseMotorPower(20);
		motRegRight.setSpeed(500);
		motRegLeft.setSpeed(500);
		robot.correctLeft(75);
		int storage;

		int currentValue = 40;
		if ((sonicAverage() < 40)) {
			currentValue = sonicAverage();
		}
		int lastValue = currentValue;
		int difference = 0;

		while (true) {
			right(3);
			if (lastValue - currentValue > thresh) {
				break;
			}
			lastValue = currentValue;
			storage = sonicAverage();
			if (storage < 40) {
				currentValue = storage;
			}
			difference = lastValue - currentValue;
			debugln("Cur " + currentValue);
			debugln("Last " + lastValue);
			debugln("Dif " + difference);
		}
		robot.stop();
		robot.sleep(1000);
		debug("Head L " + getHeading());
		double headL = getHeading();
		// if (headL > 180)
		// {headL = headL - 360;}
		correctRight(90);
		currentValue = 40;
		if (sonicAverage() < 40) {
			currentValue = sonicAverage();
		}
		lastValue = currentValue;

		while (true) {
			left(3);
			if (lastValue - currentValue > thresh) {
				break;
			}
			lastValue = currentValue;
			storage = sonicAverage();
			if (storage < 40) {
				currentValue = storage;
			}
			difference = lastValue - currentValue;
			debugln("Cur " + currentValue);
			debugln("Last " + lastValue);
			debugln("Dif " + difference);
		}
		robot.stop();
		robot.sleep(1000);
		debugln("headR " + getHeading());
		double headR = getHeading();
		// if (headR > 180)
		// {headR = headR - 360;}
		double headC;

		if (headL < headR) {
			headC = Util.round((headR + headL) / 2 - 180);
		} else {
			headC = Util.round((headR + headL) / 2);
		}

		debugln("Head C " + headC);
		goToHeading(Util.round(headC)); // findCanFine();
		stop();
	}
	
	public boolean isCanInSquare() {
		boolean foundCan = false;
		int sweepResolution = 5;
		int degrees=0;
		int storage;
		
		setBaseMotorPower(20);
		motRegRight.setSpeed(500);
		motRegLeft.setSpeed(500);
		robot.correctLeft(20);

		int currentValue = 45;
		if ((sonicAverage() < 45)) {
			currentValue = sonicAverage();
		}
		int lastValue = currentValue;
		int counter = 0;
		while (degrees < 40) {
			right(sweepResolution);
			storage = sonicAverage();
			if (storage < 45) {
				currentValue = storage;
			}
//			if (Math.abs(lastValue - currentValue) > thresh) {
//				foundCan = true;
//			}
			if (currentValue < 30){
				counter++;
			}
			if (counter>3){
				foundCan = true;
			}
			debugln("Cur " + currentValue + " | Dif " + (lastValue - currentValue));
			degrees += sweepResolution;
			lastValue = currentValue;
		}
		
		robot.stop();
		debugln("Can: " + foundCan);
		return foundCan;
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
		
		double i = 0;
		
		if (n == 0){
			i = cardinal0;
		} else if(n == 45){
			i = cardinal45;
		} else if(n == 90){
			i = cardinal90;
		} else if(n == 135){
			i = cardinal135;
		} else if(n == 180){
			i = cardinal180;
		} else if(n == 225){
			i = cardinal225;
		} else if(n == 270){
			i = cardinal270;
		} else if(n == 315){
			i = cardinal315;
		} else {
			i = n;
		}
		
		goToHeading(i);
		
//		int diff = n - getDir();
//		//debugln("faceDir: diff = " + diff);
//		if (diff == 0) {
//			return;
//		} else {
//			if (diff > 180) {
//				correctRight(Math.abs(360 - diff));
//			} else if (diff > 0) {
//				correctLeft(Math.abs(diff));
//			} else if (diff < -180) {
//				correctLeft(Math.abs(360 + diff));
//			} else {
//				correctRight(Math.abs(diff));
//			}
//		}
//		// setDir(n);
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
			Robot.playTone(440, 100);
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
		int nextX = getX() + (int)Util.round(factor * diffX);
		int nextY = getY() + (int)Util.round(factor * diffY);

		debugln("(" + nextX + ", " + nextY + ") dir=" + getDir());
		debugln("" + factor);
		if (ultrasonic.getDistance() > (Map2D.SCALE * factor)) {
			debugln("path is clear");
			map.grid[getX()][getY()] = 9;
			forward((factor * Map2D.SCALE));
			setX(nextX);
			setY(nextY);
			map.grid[getX()][getY()] = Map2D.ROBOT;
			return true;
		} else {
			stop();
			Sound.buzz();
			map.grid[nextX][nextY] = Map2D.CAN;
			return false;
		}
	}

	public boolean checkForPlatform() {
		forward(30);
		stop();
		int val = sonicAverage();
		debugln("Sonic reading: "+val);
		if (val < 20) {
			debugln("Platform found");
			backward(30);
			stop();
			return true;
		}
		debugln("NO platform");
		backward(30);
		stop();
		return false;
	}

	public int goLeft() {
		faceDir(180);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
	}

	public int goRight() {
		faceDir(0);
		if (goForward()) {
			faceDir(0);
			return 0;
		} else {
			return 1;
		}
	}

	public int goUp() {
		faceDir(90);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
	}

	public int goDown() {
		faceDir(270);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
	}

	public int goUpRight() {
		faceDir(45);
		if (goForward()) {
			faceDir(45);
			return 0;
		} else {
			return 1;
		}
	}

	public int goUpLeft() {
		faceDir(135);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
	}

	public int goDownLeft() {
		faceDir(225);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
	}

	public int goDownRight() {
		faceDir(315);
		if (goForward()) {
			return 0;
		} else {
			return 1;
		}
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
		debugln("Wavefront completed");
		String route = nav.makePath();
		debugln("Route to can sent to Robot");
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
				Robot.playTone(440, 100);
				sleep(100);
			}
		}
		debugln("adjacent to object");
		faceDir(route.charAt(route.length()));

	}

	public void goTo(int x, int y) {
		String route = nav.pathTo(x, y);
		debugln("Pathing complete to: "+x+ ", "+y);
		char dir;
		for (int n = 0; n < route.length(); n++) {
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
				Robot.playTone(440, 100);
				sleep(100);
			}
			debugln("Routing complete to: "+x+ ", "+y);
		}

	}

	public void liftCan() {
		servoDriver.servoClawGrip.setAngle(0); // open claw
		sleep(100);
		dropClaw();
		Robot.playTone(400, 100);
		sleep(100);
		servoDriver.servoClawGrip.setAngle(180); // close claw
		sleep(1000);
		servoDriver.servoClawLift.setAngle(95); // lift claw
		// TODO add code to check for microswitch close to stop claw lift
		sleep(7000);
		servoDriver.servoClawLift.setAngle(86);
		sleep(100);
	}

	public void dropCan() {
		servoDriver.servoClawLift.setAngle(80); // lower claw
		sleep(500); // wait for can to rest on platform
		servoDriver.servoClawLift.setAngle(86);
		servoDriver.servoClawGrip.setAngle(0);
		sleep(100);
	}
	
	public void dropClaw() {
		servoDriver.servoClawLift.setAngle(80); // lower claw
		while(eopdAverage() > 3) {
			sleep(10);
			debugln("eopd reading " + eopdAverage());
		}
		servoDriver.servoClawLift.setAngle(86);
		sleep(100);
	}

	public void liftCompass() {
		servoDriver.servoCompass.setAngle(97);
	}
	
	public boolean isCompassUp() {
		if(servoDriver.servoCompass.getAngle() == 97) {
			return true;
		}
		return false;
	}

	public void dropCompass() {
		servoDriver.servoCompass.setAngle(0);
	}

	public void faceTarget(int target) {
		char dir = nav.dirTo(target);

		if (map.isInCenter(target)) {
		} else if (dir == 'e' || dir == 'c' || dir == 'z' || dir == 'q') {
		} else {
			if (map.findCoordinates(target)[0] <= 2) {
				goTo(2, 2);
			} else {
				goTo(3, 2);
			}
		}
		faceDir(nav.dirTo(target));
	}

	public boolean getUseCommands() {
		return true;
	}

	public boolean getStepMode() {
		return stepMode;
	}

	public void setStepMode(int mode) {
		if (mode != 0) {
			stepMode = true;
		} else {
			stepMode = false;
		}
	}

	public void setUseCommands() {
		// TODO - add boolean variable
		// useCommands = true;
	}

	public boolean getUseRConsole() {
		// TODO Auto-generated method stub
		return false;
	}

//	public int getEOPD() {
//		if (eopd != null) {
//			// Return values between 4 and 100
//			return (eopd.readRawValue() * 100) / 1023;
//		}
//		return -1;
//	}

	public void status() {
		debugln(" dir = " + robot.getDir());
		debugln(" X/Y = " + robot.getX() + ", " + robot.getY());
		debugln("dist = " + robot.ultrasonic.getDistance());
		debugln("comp = " + robot.getHeading() + " (North=" + robot.compOffset
				+ ")");

	}
}