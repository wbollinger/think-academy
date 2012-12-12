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
}
