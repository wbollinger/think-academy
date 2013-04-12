package soccer;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.addon.MMXMotor;
import lejos.nxt.addon.NXTMMX;

public class Robot {

	public int LightCorrection;
	
	public final int WHITE_VALUE = 500;
	
	public static final byte CONNECT_CAPS_CHARGER = (byte) 1;
	public static final byte DISCONNECT_CAPS_CHARGER = (byte) 2;
	public static final byte CONNECT_CAPS_SOLENOID = (byte) 3;
	public static final byte DISCONNECT_CAPS_SOLENOID = (byte) 4;
	
	protected int MOTOR_POWER = 100;

	State current_state;
	boolean exit;

	public String name;

	protected static double r;
	protected static double b;

	public final static Vector2D F0 = new Vector2D(-1.0, 0.0);
	public final static Vector2D F1 = new Vector2D(0.5, -1.0 * Math.sqrt(3)
			/ 2.0);
	public final static Vector2D F2 = new Vector2D(0.5, Math.sqrt(3) / 2.0);

	public Navigator nav;

	NXTMotor motA;
	NXTMotor motB;
	NXTMotor motC;
	
	int aFudge;
	int bFudge;
	int cFudge;
	
	NXTMMX mux;
	MMXMotor dribbler;

	NXTRegulatedMotor regMotA;
	NXTRegulatedMotor regMotB;
	NXTRegulatedMotor regMotC;
	
	LightSensor lightLeft;
	LightSensor lightRight;

	BrickIO io;

	public ArduSoccer arduino;
	public IRSeekerV2 IR;
	public EnhIRSeekerV2 EIR;
	public CompassHTSensor compass;
	public UltrasonicSensor USY;
	public UltrasonicSensor USX;

	// this is a singleton
	private static Robot robot;

	public static Robot getRobot() {
		if (robot == null) {
			robot = RobotFactory.makeRobot();
		}
		if (robot == null) {

		}
		return robot;
	}

	protected Robot(String name) {
		this.name = name;
		current_state = StateStart.getInstance();
		io = new BrickIO();
		exit = false;
		nav = new Navigator(this);
		
		aFudge = 0;
		bFudge = 0;
		cFudge = 0;
	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getR() {
		return r;
	}

	public double getB() {
		return b;
	}

	public void followBall() {
	}
	
	public void fireSolenoid() {
	}

	public double pointToGoal() {
		return 0.0;
	}

	public void setPower(int power) {
		MOTOR_POWER = power;
	}

	public void moveForward() {
	}

	public void moveBackward() {
	}

	public void moveForward(int time) {
	}

	public void stopAll() {
	}

	public void floatAll() {

	}

	public void turnLeft() {
	}

	public void turnLeftPrecise(double degrees) {
	}

	public void turnLeft(int time) {
	}

	public void turnRight() {
	}

	public void turnRightPrecise(double degrees) {
	}

	public void turnRight(int time) {
	}

	public void moveArcRight() {

	}

	public void moveArcLeft() {

	}

	public int getUSX() {
		int distance = 0;

		for (int i = 0; i < 4; i++) {
			distance = USX.getDistance();
			if (distance != 255) {
				return distance;
			}

		}

		return distance;

	}

	public int getUSY() {
		int distance = 0;

		for (int i = 0; i < 4; i++) {
			distance = USX.getDistance();
			if (distance != 255) {
				return distance;
			}

		}

		return distance;

	}

	public void tacoMeterTurn() {
	}

	public void run() {
		while (Button.ESCAPE.isUp() && !this.exit) {
			update();
		}
	}

	public boolean check(MotorPort mot) {
		if (mot.getTachoCount() == -1) {
			return false;
		} else {
			return true;
		}

	}

	/*
	 * public boolean check(SensorPort sen){ if(sen.i2cStatus() == -5){
	 * LightSensor lit = new LightSensor(sen); if(lit.getLightValue() == 0){
	 * return false; }else{ return true; } }else{
	 * 
	 * }
	 * 
	 * }
	 */
	public void update() {
		if (current_state != null) {
			current_state.execute(this);
		}
	}

	/**
	 * This method changes the current state to the new state. It first calls
	 * the exit() method of the current state, then assigns the new state to
	 * current_state and finally calls the enter() method of the new state.
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

	public void joystickControl(double x, double y, int button) {
	
		if (button == 128) {
			
		} else if (button == 64) {
			
		} else {
			
		}
		if (button == 16) {
			
		} else if (button == 32) {
			
		}
		if (button == 4096) {
			
		} else if (button == 16384) {
			
		}
		if (button == 1) {
			Sound.playTone(220, 100);
		} else if (button == 2) {
			Sound.playTone(330, 100);
		} else if (button == 4) {
			Sound.playTone(440, 100);
		} else if (button == 8) {
			Sound.playTone(550, 100);
		}
	}
	
	public void setFudge(int a, int b, int c) {
		aFudge = a;
		bFudge = b;
		cFudge = c;
	}
}
