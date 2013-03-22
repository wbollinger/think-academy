// Enhanced IR SeekerV2 Function in Lejos, ported from NXC version
//
// This function implements the same algorithm used in the
// Enhanced IRSeekerV2 Block.
//
// This method of determining direction and signal strength combines
// both the DC and AC data from the IRSeeker sensor for improved full
// range performance.  Using just AC mode works great for longer ranges
// but can cause problems at short range.  Using just DC mode works very
// poorly at long range and is also very prone to IR interference.  Using
// both AC and DC modes gives the most reliable direction and signal strength
// determination regardless of is the ball is near or far from the sensor.
// you improved information on the direction to the IR Ball.
//
//   dir      A direction value similar to standard block from 0-9
//            with 0 as no signal
//                 1-4 to the left of the sensor
//                 5 straight ahead
//                 5-9 to the right of the sensor
//            To get approximate direction in degrees use:
//                 (dir - 5) * 30
//   strength A single strength value based on the strength of the
//            IR signal.
//
//
package soccer;

import lejos.nxt.I2CSensor;
import lejos.nxt.I2CPort;
//import lejos.nxt.addon.IRSeekerV2;

public class EnhIRSeekerV2 extends I2CSensor {

	public static final byte address = 0x10;
	byte[] buf = new byte[1];
	byte[] respBuf = new byte[6];
	public static final float noAngle = Float.NaN;

	// the values calculated from last sensor query
	int dir;
	int strength;

	public EnhIRSeekerV2(I2CPort port) {
		super(port, address, I2CPort.STANDARD_MODE, TYPE_LOWSPEED);
	}

	public int getDir() {
		return dir;
	}

	public int getStrength() {
		return strength;
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public void update() {
		int rc;
		int i, iMax;
		long dcSigSum, dcStr;

		dir = 0;
		strength = 0;

		// Read DC signal strengths (skip the dir).  6 bytes of data
		int register = 0x43; // DC
		rc = getData(register, respBuf, 6); // I2CBytes(port, cmdBuf, cResp,
												// respBuf);
		if (rc != 0) {
			return;
		}

		// Find the max DC sig strength
		iMax = 0;

		for (i = 1; i < 5; i++) {
			if (respBuf[i] > respBuf[iMax])
				iMax = i;
		}

		// Calc base DC direction value
		dir = (iMax * 2) + 1;

		// Set base dcStrength based on max signal and average
		dcSigSum = respBuf[iMax] + respBuf[5];

		// Check signal strength of neighboring sensor elements
		if ((iMax > 0) && (respBuf[iMax - 1] > respBuf[iMax] / 2)) {
			dir--;
			dcSigSum += respBuf[iMax - 1];
		}
		if ((iMax < 4) && (respBuf[iMax + 1] > respBuf[iMax] / 2)) {
			dir++;
			dcSigSum += respBuf[iMax + 1];
		}

		// Make DC strength compatible with AC strength. use: sqrt(dcSigSum*500)
		dcSigSum *= 500;
		dcStr = 1;

		for (i = 0; i < 10; i++) {
			dcStr = (dcSigSum / dcStr + dcStr) / 2; // sqrt approx
		}
		strength = (int) dcStr;

		// Decide if using DC strength or should read and use AC strength
		if (strength <= 200) {
			// Use AC Dir
			dir = 0;
			strength = 0;
			// cmdBuf[1] = 0x49; // Recycle rest of cmdBuf from the DC read
			// operation
			register = 0x49;
			rc = getData(register, respBuf, 6); // I2CBytes(port, cmdBuf,
											    // cResp, respBuf);
			if (rc == 0) {
				dir = respBuf[0];
				// Sum the sensor elements to get strength
				if (dir > 0) {
					for (i = 1; i <= 5; i++)
						strength += respBuf[i];
				}
			}
		}
	}
}