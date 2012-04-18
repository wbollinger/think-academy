package rescue;

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
			} catch (Exception e) { // survive a parse error
			}
		}
		Sound.playTone(880, 200); // error beep
		return value;
	}

	private double parseDouble(String arg) {
		double value = -1;
		if ((arg != null) && (arg.length() > 0)) {
			try {
				value = Double.parseDouble(arg);
				return value;
			} catch (Exception e) { // survive a parse error
			}
		}
		Sound.playTone(880, 200); // error beep
		return value;
	}

	// --------------------------------------------------------------------
	public void execute(Robot robot) {
		if (robot.btc == null) {
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
				robot.outStream.writeUTF(robot.name + " battery: "
						+ Battery.getVoltage() + "\n");
				firstTime = false;
			}
			if (showPrompt) {
				// display prompt in PC console
				if (robot.inStream.available() > 0) {
					robot.outStream.writeUTF("+>>>");
				} else {
					robot.outStream.writeUTF(">>>");
				}
				robot.outStream.flush();
			}
			// read in a cmd
			inputString = robot.inStream.readUTF();
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
		String arg0 = null, arg1 = null; // , arg2 = null;
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
			}
		}

		// Select from list:
		if (command.equalsIgnoreCase("step")) {
			if (args.length > 0) {
				int mode = parseInt(arg0);
				robot.setStepMode(mode);
			} else {
				debugln("" + robot.getStepMode());
			}
		} else if (command.equalsIgnoreCase("motorA")) {
			if (args.length > 0) {
				if (arg0.equalsIgnoreCase("off")) {
					robot.setArduinoPoweredUp(false);
				} else {
					robot.setArduinoPoweredUp(true);
				}
			}
			debugln("" + robot.getArduinoPoweredUp());
		} else if (command.equalsIgnoreCase("stop")) {
			robot.stop();
		} else if (command.equalsIgnoreCase("right")) {
			double degrees = parseDouble(arg0);
			robot.right(degrees);
		} else if (command.equalsIgnoreCase("left")) {
			double degrees = parseDouble(arg0);
			robot.left(degrees);
		} else if (command.equalsIgnoreCase("correctRight")) {
			double degrees = parseDouble(arg0);
			robot.correctRight((float) (degrees));
		} else if (command.equalsIgnoreCase("correctLeft")) {
			double degrees = parseDouble(arg0);
			robot.correctLeft((float) (degrees));
		} else if (command.equalsIgnoreCase("forward")) {
			if (args.length > 0) {
				double distance = parseDouble(arg0);
				robot.forward(distance);
				return;
			}
			robot.forward();
		} else if (command.equalsIgnoreCase("reverse")) {
			if (args.length > 0) {
				double distance = parseDouble(arg0);
				robot.backward(distance);
				return;
			}
			robot.backward();
		} else if (command.equalsIgnoreCase("power")) {
			if (args.length > 0) {
				int power = parseInt(arg0);
				robot.setBaseMotorPower(power);
				return;
			} else {
				debugln("power level:" + robot.getBaseMotorPower());
			}
		} else if (command.equalsIgnoreCase("point")) {
			if (args.length > 0) {
				double angle = parseDouble(arg0);
				robot.goToHeading(angle);
				return;
			}

		} else if (command.equalsIgnoreCase("line")) {
			robot.changeState(StateLineFollow.getInstance());
		} else if (command.equalsIgnoreCase("kP")) {
			if (args.length > 0) {
				StateLineFollow.getInstance().Kp = Double.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollow.getInstance().Kp);
			}
		} else if (command.equalsIgnoreCase("Ki")) {
			if (args.length > 0) {
				StateLineFollow.getInstance().Ki = Double.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollow.getInstance().Ki);
			}
		} else if (command.equalsIgnoreCase("Kd")) {
			if (args.length > 0) {
				StateLineFollow.getInstance().Kd = Double.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollow.getInstance().Kd);
			}
		} else if (command.equalsIgnoreCase("lineReg")) {
			robot.changeState(StateLineFollowRegulated.getInstance());
		} else if (command.equalsIgnoreCase("KpReg")) {
			if (args.length > 0) {
				StateLineFollowRegulated.getInstance().Kp = Double
						.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollowRegulated.getInstance().Kp);
			}
		} else if (command.equalsIgnoreCase("KiReg")) {
			if (args.length > 0) {
				StateLineFollowRegulated.getInstance().Ki = Double
						.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollowRegulated.getInstance().Ki);
			}
		} else if (command.equalsIgnoreCase("KdReg")) {
			if (args.length > 0) {
				StateLineFollowRegulated.getInstance().Kd = Double
						.parseDouble(arg0);
			} else {
				debugln("" + StateLineFollowRegulated.getInstance().Kd);
			}
		} else if (command.equalsIgnoreCase("avoid")) {
			robot.changeState(StateAvoidObstacle.getInstance());
		} else if (command.equalsIgnoreCase("leftLook")) {
			robot.turnLeftLookForLine(90);
		} else if (command.equalsIgnoreCase("rightLook")) {
			robot.turnRightLookForLine(90);
		} else if (command.equalsIgnoreCase("forwardLFL")) {
			if (args.length > 0) {
				double distance = parseDouble(arg0);
				robot.forwardLookForLine(distance);
				return;
			}
		} else if (command.equalsIgnoreCase("findLine")) {
			robot.changeState(StateFindLine.getInstance());
		} else if (command.equalsIgnoreCase("eopd")) {
			robot.eopdPoll();
		} else if (command.equalsIgnoreCase("eopdcal")) {
			robot.eopdCal();

		} else if (command.equalsIgnoreCase("eopdcont")) {
			robot.eopdContPoll();
		} else if (command.equalsIgnoreCase("grid")) {
			robot.changeState(StateGridRunNew.getInstance());
		} else if (command.equalsIgnoreCase("resetGrid")) {
			robot.resetGrid();
		} else if (command.equalsIgnoreCase("seedGrid")) {
			robot.map.seed();
			robot.printMap();
		} else if (command.equalsIgnoreCase("map")) {
			robot.printMap();
		} else if (command.equalsIgnoreCase("goTo")) {
			robot.goTo(Integer.parseInt(arg0), Integer.parseInt(arg1));
		} else if (command.equalsIgnoreCase("findCan")) {
			robot.changeState(StateFindCan.getInstance());
		} else if (command.equalsIgnoreCase("locF")) {
			robot.findCanCoarse();
		} else if (command.equalsIgnoreCase("ICIS")) {
			robot.isCanInSquare();
		} else if (command.equalsIgnoreCase("locC")) {
			robot.findCanCoarseSonic();
		} else if (command.equalsIgnoreCase("liftCompass")) {
			if (args.length > 0) {
				int degrees = parseInt(arg0);
				robot.servoDriver.servoCompass.setAngle(degrees);
			}
			debugln("" + robot.servoDriver.servoCompass.getAngle());
		} else if (command.equalsIgnoreCase("gripClaw")) {
			if (args.length > 0) {
				int degrees = parseInt(arg0);
				robot.servoDriver.servoClawGrip.setAngle(degrees);
			}
			debugln("" + robot.servoDriver.servoClawGrip.getAngle());
		} else if (command.equalsIgnoreCase("liftClaw")) {
			if (args.length > 0) {
				int degrees = parseInt(arg0);
				robot.servoDriver.servoClawLift.setAngle(degrees);
			}
			debugln("" + robot.servoDriver.servoClawLift.getAngle());
		} else if (command.equalsIgnoreCase("servo1")) {
			if (args.length > 0) {
				int degrees = parseInt(arg0);
				robot.servoDriver.servo1.setAngle(degrees);
			}
			debugln("" + robot.servoDriver.servo1.getAngle());
		} else if (command.equalsIgnoreCase("compassUp")) {
			robot.liftCompass();
		} else if (command.equalsIgnoreCase("compassDown")) {
			robot.dropCompass();
		} else if (command.equalsIgnoreCase("clawUp")) {
			robot.liftCan();
		} else if (command.equalsIgnoreCase("clawDown")) {
			robot.dropCan();
		} else if (command.equalsIgnoreCase("getAngle")) {
			double angle = robot.getAngle();
			debug("" + angle);
		} else if (command.equalsIgnoreCase("sonic")
				|| command.equalsIgnoreCase("US")) {
			int dist = robot.ultrasonic.getDistance();
			debugln("" + dist);
		} else if (command.equalsIgnoreCase("accelX")) {
			int val = robot.accel.getXAccel();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("accelY")) {
			int val = robot.accel.getYAccel();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("accelZ")) {
			int val = robot.accel.getZAccel();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("lightLeft")) {
			int val = robot.getLightLeft();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("lightRight")) {
			int val = robot.getLightRight();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("lightLoop")) {
			while (!Button.ENTER.isDown()) {
				int val = robot.getLightLeft();
				int val2 = robot.getLightRight();
				int val3 = robot.getLightThird();
				debugln("" + val + " " + val2 + " " + val3);
			}
		} else if (command.equalsIgnoreCase("readLightArduino")) {
			int[] val = robot.servoDriver
					.readAddressValues((byte) parseInt(arg0));
			debugln("" + val[0] + " " + val[1] + " " + val[2]);
		} else if (command.equalsIgnoreCase("tachoLeft")) {
			int val = robot.motLeft.getTachoCount();
			debugln("" + val);
		} else if (command.equalsIgnoreCase("comp")) {
			float deg = robot.getHeading();
			debugln("" + deg);
		} else if (command.equalsIgnoreCase("compOffset")) {
			float deg = robot.getCompOffset();
			debugln("" + deg);
		} else if (command.equalsIgnoreCase("getdir")) {
			int val = robot.getDir();
			debugln(""+val);
		} else if (command.equalsIgnoreCase("setDir")) {
			robot.setDir(parseInt(arg0));
		} else if (command.equalsIgnoreCase("correctLeftLine")) {
			double degrees = parseDouble(arg0);
			robot.correctLeftLine((float) (degrees));
		} else if (command.equalsIgnoreCase("correctRightLine")) {
			double degrees = parseDouble(arg0);
			robot.correctRightLine((float) (degrees));
		} else if (command.equalsIgnoreCase("color")) {
			float deg = robot.colorsensor.getLightValue();
			debugln("" + deg);
		} else if (command.equalsIgnoreCase("colorFollow")) {
			robot.changeState(StateLineFollowSingle.getInstance());
		} else if (command.equalsIgnoreCase("rDiam")) {
			double diam = parseDouble(arg0);
			if (diam > 0) {
				robot.setRobotDiameter(diam);
			}
			debugln("robot diam = " + robot.getRobotDiameter());
		} else if (command.equalsIgnoreCase("wDiam")) {
			double diam = parseDouble(arg0);
			if (diam > 0) {
				robot.setWheelDiameter(diam);
			}
			debugln("wheel diam = " + robot.getWheelDiameter());
		} else if (command.equalsIgnoreCase("eopd")) {
			debugln("" + robot.getEOPD());
		} else if (command.equalsIgnoreCase("thresh")) {
			int newthresh = parseInt(arg0);
			if (newthresh >= 0) {
				StateLineFollowSingle.getInstance().setThreshold(newthresh);
				debugln("threshold = " + newthresh);
			}
		} else if (command.equalsIgnoreCase("x")) {
			int newX = parseInt(arg0);
			if (newX >= 0) {
				robot.setX(newX);
			}
			debugln("X = " + robot.getX());
		} else if (command.equalsIgnoreCase("y")) {
			int newY = parseInt(arg0);
			if (newY >= 0) {
				robot.setX(newY);
			}
			debugln("Y = " + robot.getY());
		} else if (command.equalsIgnoreCase("status")) {
			debugln(" dir = " + robot.getDir());
			debugln(" X/Y = " + robot.getX() + ", " + robot.getY());
			debugln("dist = " + robot.ultrasonic.getDistance());
			debugln("comp = " + robot.getHeading());
		} else if (command.equalsIgnoreCase("bat")) {
			debug("Battery: " + Battery.getVoltage() + "\n");
		} else if (command.equalsIgnoreCase("echo")) {
			for (i = 0; i < args.length; i++) {
				if (i > 0)
					debug(" ");
				debug(args[i]);
			}
			debug("\n");
		} else if (command.equalsIgnoreCase("exit")
				| command.equalsIgnoreCase("quit")) {
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
		} else if (command.equalsIgnoreCase("debug")) {
			debug("Not implemented\n");
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
			}
		} else {
			debug("?\n");
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
	}

	// --------------------------------------------------------------------------------------------
	public void enter(Robot robot) {
		// debug support is not available at this point

		int buttons = Button.ID_ENTER;
		// int i = 0;
		// while ((buttons = Button.readButtons()) == 0) {
		// if (i % 10 == 0) {
		// Sound.playTone(330, 100);
		// } else {
		// robot.sleep(100);
		// }
		// if (i++ > 50) {
		// break;
		// }
		// }

		if (buttons == Button.ID_ENTER) {
			if (robot.btc == null) {
				Sound.playTone(440, 100);
				LCD.drawString("BT command...", 0, 1);
				robot.btc = Bluetooth.waitForConnection();
				Sound.playTone(660, 100);
				robot.inStream = robot.btc.openDataInputStream();
				robot.outStream = robot.btc.openDataOutputStream();

				isCommandLoopRunning = true;
			}
		}
		// print this here - only once is enough
		// debug("Enter command:");
	}

	public void exit(Robot robot) {
	}
}
