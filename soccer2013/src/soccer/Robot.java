package soccer;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.IRSeekerV2;

public class Robot {

	State current_state;
	boolean exit;

	public String name;

	protected static double r;
	protected static double b;

	public final static Vector2D F0 = new Vector2D(-1.0, 0.0);
	public final static Vector2D F1 = new Vector2D(0.5, -1.0 * Math.sqrt(3) / 2.0);
	public final static Vector2D F2 = new Vector2D(0.5, Math.sqrt(3) / 2.0);

	public Navigator nav;

	NXTMotor motA;
	NXTMotor motB;
	NXTMotor motC;

	BrickIO io;

	IRSeekerV2 IR;
	CompassHTSensor compass;

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
	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double getR() {
		return r;
	}

	public static double getB() {
		return b;
	}

	public void followBall() {
	}

	public void moveForward() {
	}

	public void moveBackward() {
	}

	public void moveForward(int time) {
	}

	public void stopAll() {
	}

	public void turnLeft() {
	}

	public void turnLeft(int time) {
	}

	public void turnRight() {
	}

	public void turnRight(int time) {
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

}
