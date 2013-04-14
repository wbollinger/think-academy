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
//            IR signal.  In DC mode the strength may range up to
//            values of 390->400
//
//
package soccer;

import lejos.nxt.I2CSensor;
import lejos.nxt.I2CPort;

public class EnhIRSeekerV2 extends I2CSensor {

	public static final byte address = 0x10;
	
	byte[] byteBuf = new byte[6];  // Java treats bytes as "signed"
	int[]  respBuf = new int[6];   // convert them to unsigned ints

	// the values calculated from last sensor query
	int dir;
	int strength;
	int mode;

	public EnhIRSeekerV2(I2CPort port) {
		super(port, address, I2CPort.STANDARD_MODE, TYPE_LOWSPEED);
	}

	public int getDir() {
		return dir;
	}

	public int getStrength() {
		return strength;
	}

	/**
	 * Check if last IR seeker measurement was DC or AC mode
	 * 
	 * @return 1 for DC mode (close), 2 for AC mode (far)
	 */
	public int getMode() {
		return mode;
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
		long dcSigSum;

		dir = 0;
		strength = 0;
		mode = -1;

		// Read DC signal strengths (skip the dir).  6 bytes of data
		int register = 0x43; // DC
		rc = getData(register, byteBuf, 6);
		if (rc != 0) {
			return;
		}

		// Make values unsigned and find the max DC sig strength
		respBuf[0] = 0xFF & byteBuf[0];
		respBuf[5] = 0xFF & byteBuf[5];
		iMax = 0;
		for (i = 1; i < 5; i++) {
			respBuf[i] = 0xFF & byteBuf[i];
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
		
		// The old NXC way of computing sqr root
//		long dcStr = 1;
//
//		for (i = 0; i < 10; i++) {
//			dcStr = (dcSigSum / dcStr + dcStr) / 2; // sqrt approx
//		}
//		strength = (int) dcStr;

		strength = (int) Math.sqrt((double) dcSigSum);
		mode = 1;

		// Decide if using DC strength or should read and use AC strength
		if (strength <= 200) {
			// Use AC Dir
			dir = 0;
			strength = 0;
			mode = 2;
			
			// Reuse same buf from the DC read operation
			register = 0x49;
			rc = getData(register, byteBuf, 6);
			if (rc == 0) {
				dir = 0xFF & byteBuf[0];
				// Sum the sensor elements to get strength
				if (dir > 0) {
					for (i = 1; i <= 5; i++)
						strength += (0xFF & byteBuf[i]);
				}
			}
		}
	}
}