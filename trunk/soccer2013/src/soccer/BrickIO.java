package soccer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;

public class BrickIO {

	BTConnection btc;
	DataInputStream inStream;
	DataOutputStream outStream;
	
	private boolean stepMode;
	private boolean useCommands;
	private boolean useDebug;
	
	private int debugFlags = 0xFF;
	/*
	 * 0x01  1   IR data
	 * 0x02  2   Ping data
	 * 0x04  4   Light sensor data
	 * 0x08  8   Compass data
	 * 0x10  16  Zone data
	 * 0x20  32  Unassigned
	 * 0x40  64  Unassigned
	 * 0x80  128 Unassigned
	 */
	
	
	public void debug(String msg) {
		if (useDebug == false)
			return;
		if ((btc != null) && (outStream != null)) {
			try {
				outStream.writeUTF(msg);
				outStream.flush();
			} catch (IOException e) {
				LCD.drawString("Rbt IO Err", 0, 1);
			}
		}
		// else {
		// For competition, do nothing here
		// System.out.println(msg);
		// RConsole.print(msg);
		// }
	}
	
	public void debugln(String msg) {
		if (useDebug == false)
			return;
		debug(msg + "\n");
	}
	
	public void debug(String msg, int msgType) {
		if (useDebug == false)
			return;
		if ((btc != null) && (outStream != null) && ((msgType & debugFlags) != 0x00)) {
			try {
				outStream.writeUTF(msg);
				outStream.flush();
			} catch (IOException e) {
				LCD.drawString("Rbt IO Err", 0, 1);
			}
		}
		// else {
		// For competition, do nothing here
		// System.out.println(msg);
		// RConsole.print(msg);
		// }
	}
	
	public void debugln(String msg, int msgType) {
		if (useDebug == false)
			return;
		debug(msg + "\n", msgType);
	}

	
	
	public boolean isStepMode() {
		return stepMode;
	}

	public void setStepMode(boolean stepMode) {
		this.stepMode = stepMode;
	}

	public boolean getUseCommands() {
		return useCommands;
	}

	public void setUseCommands(boolean useCommands) {
		this.useCommands = useCommands;
	}

	public boolean isUseDebug() {
		return useDebug;
	}

	public void setUseDebug(boolean useDebug) {
		this.useDebug = useDebug;
	}
	
	public void setDebugFlags(int flags) {
		debugFlags = flags;
	}
}
