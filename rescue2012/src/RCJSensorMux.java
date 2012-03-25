import lejos.nxt.I2CPort;
import lejos.nxt.addon.SensorMux;

public class RCJSensorMux extends SensorMux {

	public final byte digitalRegisters[] = { 0x40, 0x50, 0x60, 0x70 };
	public final byte analogRegisters[] = { 0x36, 0x38, 0x3A, 0x3C };

	public RCJSensorMux(I2CPort port) {
		super(port);
	}

	/**
	 * This method return the value from an analog sensor. Currently, SMux
	 * supports Touch sensor and Sound sensor
	 * 
	 * @param channel
	 *            the index of the channel
	 * @return the analog value
	 */
	public int getAnalogValue(int channel) {
		byte[] buf = new byte[1];
		byte register = analogRegisters[channel - 1];
		/* int ret = */getData(register, buf, 1);
		int analogValue = buf[0] & 0xff;
		return analogValue;
	}

	/**
	 * Method used to receive data from an EOPD Sensor
	 * 
	 * @param channel
	 *            the index of the channel
	 * @return the value
	 */
	public int readEOPD(int channel) {
		int value = 0;
		value = getAnalogValue(channel);
		// value = ((1023 - value) * 100/ 1023);
		return value;
	}

}
