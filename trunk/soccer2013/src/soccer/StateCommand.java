package soccer;

//------------------------------------------------------------------------
//  robot Command state - accept a text command and call robot methods
//------------------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;

public class StateCommand extends State {

	private static StateCommand instance = new StateCommand();

	private boolean firstTime;
	private boolean showPrompt;
	private boolean isCommandLoopRunning;

	private static int warningBeep = 0;

	private StateCommand() {
		firstTime = true;
		showPrompt = true;
		isCommandLoopRunning = false;
	}

	// this is a singleton
	public static StateCommand getInstance() {
		return instance;
	}

	public boolean getCommandLoopRunning() {
		return isCommandLoopRunning;
	}

	private int parseInt(String arg) {
		int value = -1;
		if ((arg != null) && (arg.length() > 0)) {
			try {
				value = Integer.parseInt(arg);
				return value;
			} catch (Exception e) {
				Sound.playTone(880, 200);// survive a parse error
			}
		}
		// error beep
		return value;
	}

	private double parseDouble(String arg) {
		double value = -1;
		if ((arg != null) && (arg.length() > 0)) {
			try {
				value = Double.parseDouble(arg);
				return value;
			} catch (Exception e) {
				Sound.playTone(880, 200);// survive a parse error
			}
		}
		// error beep
		return value;
	}

	// --------------------------------------------------------------------
	public void execute(Robot robot) {
		if (robot.io.btc == null) {
			// no connection: warning beeps and bail
			Sound.playTone(440, 200);
			robot.sleep(200);
			if (warningBeep++ > 9) {
				// clear flag so that exit will really exit
				isCommandLoopRunning = false;
				robot.changeState(StateExit.getInstance());
			}
			return;
		}

		int i;
		String inputString = "";
		try {
			if (firstTime) {
				float volts = Battery.getVoltage();
				robot.io.outStream.writeUTF(robot.name + " battery: " + volts + "\n");
				if (volts < 6.3f) {
					Sound.beep();
					Sound.beep();
					Sound.beep();
					debugln("LOW VOLTAGE!!!");
				}
				firstTime = false;
			}
			if (showPrompt) {
				// display prompt in PC console
				if (robot.io.inStream.available() > 0) {
					robot.io.outStream.writeUTF("+");
				} else {
					robot.io.outStream.writeUTF(">>> ");
				}
				robot.io.outStream.flush();
			}
			// read in a cmd
			inputString = robot.io.inStream.readUTF();
		} catch (IOException e) {
			LCD.drawString("Cmd IO Err", 0, 1);
			Sound.playTone(880, 200);
			System.exit(0);
		}

		// Parse out the command args using ' ' with StringTokenizer
		inputString = inputString.trim();
		if (inputString.equals("")) {
			return;
		}
		StringTokenizer st = new StringTokenizer(inputString, " ");
		String command = st.nextToken();

		// Check for command line arguments; support simple access to arg0
		String arg0 = null, arg1 = null, arg2 = null;
		String[] args = new String[st.countTokens()];
		for (i = 0; i < args.length; i++) {
			args[i] = st.nextToken();
			switch (i) {
			case 0:
				arg0 = args[0];
				break;
			case 1:
				arg1 = args[1];
				break;
			case 2:
				arg2 = args[2];
			}

		}

		try {
			// Select from list:
			if (command.equalsIgnoreCase("stop")) {
				robot.stopAll();
			} else if (command.equalsIgnoreCase("right")) {
				double degrees = parseDouble(arg0);
				robot.turnRight();
			} else if (command.equalsIgnoreCase("rightprecise")) {
				double degrees = parseDouble(arg0);
				robot.turnRightprecise(degrees);
			} else if (command.equalsIgnoreCase("pointtogoal")) {
				debugln("called");
				robot.nav.pointToGoal();
			} else if (command.equalsIgnoreCase("getlocation")) {
				robot.nav.getLocation();
				debugln(robot.nav.getXLocation() + " " + robot.nav.getYLocation());
			} else if (command.equalsIgnoreCase("getusreading")) {
				debugln("The y axis is reading " + robot.USY.getDistance());
				debugln("The x axis is reading " + robot.USX.getDistance());
				debugln("The x axis normalized is reading " + robot.nav.normalizeMeasurement(robot.USX.getDistance()));

			} else if (command.equalsIgnoreCase("getIRreading")) {
				int[] values;
				while (Button.ENTER.isUp()) {
					debugln("Ball in arc " + robot.IR.getDirection());
					values = robot.IR.getSensorValues();
					for (int j = 0; j < 5; j++) {
						debug("" + values[j] + " | ");
						Thread.sleep(50);
					}
					debugln("");
				}
			} else if (command.equalsIgnoreCase("ir")) {
				// enhanced IR direction / strength
				int dir;
				int str;
				int mode;
				while (Button.ENTER.isUp()) {
					dir = -1;
					str = -1;
					mode = -1;
					if (robot.EIR != null) {
						robot.EIR.update();
						dir = robot.EIR.getDir();
						str = robot.EIR.getStrength();
						mode = robot.EIR.getMode();
					}
					debugln("" + dir + " " + str + " " + mode);
					Thread.sleep(200);
				}
			} else if (command.equalsIgnoreCase("left")) {
				double degrees = parseDouble(arg0);
				robot.turnLeft();
			} else if (command.equalsIgnoreCase("leftprecise")) {
				double degrees = parseDouble(arg0);
				robot.turnLeftprecise(degrees);
			} else if (command.equalsIgnoreCase("followBall")) {
				robot.followBall();
			} else if (command.equalsIgnoreCase("forward")) {
				if (args.length > 0) {
					double distance = parseDouble(arg0);
					// robot.forward(distance);
					return;
				}
				robot.moveForward();
			} else if (command.equalsIgnoreCase("moveArcR")) {
				robot.moveArcRight();
			} else if (command.equalsIgnoreCase("moveArcL")) {
				robot.moveArcLeft();
			} else if (command.equalsIgnoreCase("movedir")) {
				if (args.length == 2) {
					double dir = parseDouble(arg0);
					int scale = parseInt(arg1);
					robot.nav.moveDir(dir, scale);
					return;
				}
				if (args.length == 1) {
					double dir = parseDouble(arg0);
					robot.nav.moveDir(dir);
					return;
				}
				robot.moveForward();
			} else if (command.equalsIgnoreCase("rotate")) {
				if (args.length > 0) {
					double rotate = parseDouble(arg0);
					robot.nav.rotateTo((float) rotate);
				}

			} else if (command.equalsIgnoreCase("strafe")) {
				robot.nav.strafe();
			} else if (command.equalsIgnoreCase("heading")) {
				if (args.length > 0) {
					double rotate = parseDouble(arg0);
					robot.nav.pointToHeading((float) rotate);
				}
			} else if (command.equalsIgnoreCase("arcHeading")) {
				if (args.length > 0) {
					double rotate = parseDouble(arg0);
					robot.nav.pointToHeadingArc((float) rotate);
				}
			} else if (command.equalsIgnoreCase("calibrateCompass")) {
				robot.nav.calibrate();
			} else if (command.equalsIgnoreCase("setPower")) {
				if (args.length > 0) {
					int power = parseInt(arg0);
					robot.setPower(power);
				}
			} else if (command.equalsIgnoreCase("reverse") || command.equalsIgnoreCase("backward")) {
				if (args.length > 0) {
					double distance = parseDouble(arg0);
					// robot.backward(distance);
					return;
				}
				robot.moveBackward();
				/*
				 * } else if(command.equalsIgnoreCase("check")){ if (args.length
				 * > 0) { //TODO Add check method
				 * if(arg0.equalsIgnoreCase("SP1")){ SensorPort sen =
				 * SensorPort.S1; LightSensor lig = new
				 * LightSensor(SensorPort.S1); NXTMotor mot = new
				 * NXTMotor(MotorPort.A); debugln("" + sen.i2cStatus());
				 * //check(SensorPort.S1); } if(arg0.equalsIgnoreCase("SP2")){
				 * //check(SensorPort.S2); } if(arg0.equalsIgnoreCase("SP3")){
				 * //check(SensorPort.S3); } if(arg0.equalsIgnoreCase("SP4")){
				 * //check(SensorPort.S4); } if(arg0.equalsIgnoreCase("MPA")){
				 * if(robot.check(MotorPort.A)){
				 * debugln("The motor in port A is plugged in."); }else{
				 * debugln("The motor in port A is unplugged!"); } }
				 * if(arg0.equalsIgnoreCase("MPB")){
				 * if(robot.check(MotorPort.B)){
				 * debugln("The motor in port B is plugged in."); }else{
				 * debugln("The motor in port B is unplugged!"); } }
				 * if(arg0.equalsIgnoreCase("MPC")){
				 * if(robot.check(MotorPort.C)){
				 * debugln("The motor in port C is plugged in."); }else{
				 * debugln("The motor in port C is unplugged!"); } }
				 * if(arg0.equalsIgnoreCase("All")){
				 * if(robot.check(MotorPort.A)){
				 * debugln("The motor in port A is plugged in."); }else{
				 * debugln("The motor in port A is unplugged!."); }
				 * if(robot.check(MotorPort.B)){
				 * debugln("The motor in port B is plugged in."); }else{
				 * debugln("The motor in port B is unplugged!."); }
				 * if(robot.check(MotorPort.C)){
				 * debugln("The motor in port C is plugged in."); }else{
				 * debugln("The motor in port C is unplugged!."); }
				 * //check(SensorPort.S1); //check(SensorPort.S2);
				 * //check(SensorPort.S3); //check(SensorPort.S4); }else{
				 * debugln("Not a valid arguement."); }
				 * 
				 * return; }
				 */
			} else if (command.equalsIgnoreCase("joydata")) {
				showPrompt = false;
				// debugln("joystick command detected");
				double x = Double.parseDouble(args[0]);
				double y = Double.parseDouble(args[1]);
				int button = Integer.parseInt(args[2]);
				robot.joystickControl(x, y, button);
			} else if (command.equalsIgnoreCase("bat")) {
				debug("Battery: " + Battery.getVoltage() + "\n");
			} else if (command.equalsIgnoreCase("echo")) {
				for (i = 0; i < args.length; i++) {
					if (i > 0)
						debug(" ");
					debug(args[i]);
				}
				debug("\n");

			} else if (command.equalsIgnoreCase("StateStriker")) {
				robot.changeState(StateStriker.getInstance());
			} else if (command.equalsIgnoreCase("StateGoalie")) {
				robot.changeState(StateGoalie.getInstance());
			} else if (command.equalsIgnoreCase("exit") | command.equalsIgnoreCase("quit")) {
				// clear flag so that exit will really exit
				isCommandLoopRunning = false;
				robot.changeState(StateExit.getInstance());
			} else if (command.equalsIgnoreCase("shutdown")) {
				NXT.shutDown();
			} else if (command.equalsIgnoreCase("mem")) {
				debug(Runtime.getRuntime().freeMemory() + " free\n");
				debug(Runtime.getRuntime().totalMemory() + " total\n");
				debug(File.freeMemory() + " disk\n");
			} else if (command.equalsIgnoreCase("prompt")) {
				showPrompt = !showPrompt;
			} else if (command.equalsIgnoreCase("comp")) {
				debugln("" + robot.compass.getDegrees());
			} else if (command.equalsIgnoreCase("debug")) {
				debug("Not implemented\n");
			} else if (command.equalsIgnoreCase("disableDebug")) {
				robot.io.setUseDebug(false);
			} else if (command.equalsIgnoreCase("enableDebug")) {
				robot.io.setUseDebug(true);
			} else if (command.equalsIgnoreCase("toggleDebug")) {
				robot.io.setUseDebug(false);
			} else if (command.equalsIgnoreCase("tacoMeterTurn")) {
				robot.tacoMeterTurn();
			} else if (command.equalsIgnoreCase("pingLoop")) {
				while (!Button.ENTER.isDown()) {
					int val = robot.arduino.readLightLeft();
					int val2 = robot.arduino.readLightRight();
					int val3 = robot.arduino.readPing();
					//int val4 = 0;
					debugln("" + val + " " + val2 + " " + val3); // + " " +
									   // val4);
					robot.sleep(50);
				}
			} else if (command.equalsIgnoreCase("readArduino")) {
				int[] val = robot.arduino.readAddressValues((byte) parseInt(arg0));
				debugln("" + val[0] + " " + val[1] + " " + val[2]);
			} else if (command.equalsIgnoreCase("play")) {
				int freq = parseInt(arg0);
				int time = 200;
				if (args.length > 1) {
					time = parseInt(arg1);
				}
				Sound.playTone(freq, time);
			} else if (command.equalsIgnoreCase("prop")) {
				Properties props = Settings.getProperties();
				if (args.length > 0) {
					debugln(props.getProperty(arg0));
				} else {
					Enumeration<?> e = props.propertyNames();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						debugln(key + " = " + props.getProperty(key));
					}
					debugln("The robot's wheel radius is " + robot.getR());
					debugln("The robot's wheel base is " + robot.getB());
				}
				// } else if (command.equalsIgnoreCase("joydata")) {
				// showPrompt = false;
				// debugln("joystick command detected");
				// double x = Double.parseDouble(args[0]);
				// double y = Double.parseDouble(args[1]);
				// int button = Integer.parseInt(args[2]);
				// TODO: Get joystick control working again
				// robot.joystickControl(x, y, button);
			} else if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("?")) {
				debugln("\n Movement Commands:");
				debugln("'stop' (Stops all motors.)");
				debugln("'right' (Rotates the robot clockwise.)");
				debugln("'rightprecise' (Rotates the robot clockwise for desired degrees.)");
				debugln("'left' (Rotates the robot counterclockwise.)");
				debugln("'leftprecise' (Rotates the robot counterclockwise for desired degrees.)");
				debugln("'forward' (Moves the robot forwardr.)");
				debugln("'backward' or 'reverse' (Moves the robot backward.)");
				debugln("'rotate [degree]' (Rotates to a degree. Positve numbers turn right while negitve numbers turn left.)");
				debugln("'pointToGoal' (Points the robot towards the goal.)");
				debugln("'moveDir [direction], [speed]' (Moves the robot in any direction 0-359 at a set speed.)");
				debugln("'heading' (Rotates the robot to a fixed degree.)");
				debugln("'followBall' (Follows the IR ball) \n");

				debugln("/n Sensor Commands:");
				debugln("'calibrateCompass' (Starts calibration. End the calibration by pressoing the enter button.)");
				debugln("'comp' (Displays what the compass is reading.)");
				debugln("'getusreading' (Displays what the Ultrasonic is reading.)");
				debugln("'getIRreading' (Displays what the IR SeekerV2 is reading without modification)");
				debugln("'ir' (Displays IR Seeker values w/ improved processing)");

				debugln("System Commands:");
				debugln("'bat' (Displays the current battery voltage.)");
				debugln("'mem' (Displays the amount of free, total, and used memory in the NXT Brick.)");
				debugln("'debug' (Not implemented)");

				debugln("'shutdown' (Turns the NXT off.)");
				debugln("'prompt' (Toggles the prompt.)");
				debugln("'quit' or 'exit' (Ends the program.)");
				debugln("'play [tone] [length]' (Plays a note of the specified tone and length.)");

				debugln("State Commands:");
				debugln("'StateStriker' (Changes state to StateStriker.)");
				debugln("'StateGoalie' (Changes state to StateGoalie.) \n");

				debugln("Misc. Commands:");
				debugln("'prop' (Displays basic properties of the NXT Brick such as name and volume.) \n");
				debugln("'echo [phrase]' (Displays whatever you type in as the phrase.)");

			} else {
				debugln("?");
				// // 4.5 Check if it is a filename:
				// File f = new File(command);
				// if (f.exists()) {
				// debug(command + " exists\n");
				// f.exec();
				// } else {
				// // Unrecognized command output error message
				// debug(command + " unrecognized\n");
				// }
			}
		} catch (Exception e) {
			// Ignore exceptions in command loop
		}
	}

	// --------------------------------------------------------------------------------------------
	public void enter(Robot robot) {
		// Note: debug support is not yet running at this point

		int buttons = Button.ID_ENTER;
		/*
		 * int i = 0; while ((checkPress = Button.readButtons()) == 0) { if (i %
		 * 10 == 0) { Sound.playTone(330, 100); } else { robot.sleep(100); } if
		 * (i++ > 50) { break; } }
		 */

		if (buttons == Button.ID_ENTER) {
			if (robot.io.btc == null) {
				Sound.playTone(440, 100);
				LCD.drawString("BT command...", 0, 1);
				robot.io.btc = Bluetooth.waitForConnection();
				Sound.playTone(660, 100);
				robot.io.inStream = robot.io.btc.openDataInputStream();
				robot.io.outStream = robot.io.btc.openDataOutputStream();

				isCommandLoopRunning = true;
			}
		}
		// print this here - only once is enough
		// debug("Enter command:");
	}

	public void exit(Robot robot) {
	}
}
