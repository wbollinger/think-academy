package soccer;

import lejos.nxt.*;
import lejos.nxt.addon.MServo;

/**
 * This class is used to query devices attached to an Arduino that is connected
 * to the NXT via i2c
 */
public class ArduSoccer extends I2CSensor {
	public static final byte NXTSERVO_ADDRESS = (byte) 0xb0;
	public static final byte ArduRCJ_VBATT = 0x41;// I2C Register to read
													// battery

	// I2C
	private SensorPort portConnected;

	byte[] bufReadResponse;
	int[] sensors;


	int disYBack;
	int disXRight;
	int disXLeft;
	int lightRight;
	int lightLeft;
	int touch;
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param port
	 *            the NXTServo is connected to
	 * 
	 */
	public ArduSoccer(SensorPort port) {
		super(port, NXTSERVO_ADDRESS, I2CPort.STANDARD_MODE, TYPE_LOWSPEED_9V);

		portConnected = port;

		bufReadResponse = new byte[6];
		sensors = new int[4];
	}

	/**
	 * Read the battery voltage data from NXTServo module (in millivolts)
	 * 
	 * @return the battery voltage in millivolts
	 */
	public int getBattery() {
		getData(ArduRCJ_VBATT, bufReadResponse, 1);

		// 37 is calculated from 4700 mv /128
		return (37 * (0xFF & bufReadResponse[0]));
	}

	public int[] readAddressValues(byte startByte) {

		getData(startByte, bufReadResponse, 6);

		int sensorOne = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));
		int sensorTwo = (((0xFF & bufReadResponse[3]) << 8) | ((0xFF & bufReadResponse[2])));
		int sensorThree = (((0xFF & bufReadResponse[5]) << 8) | ((0xFF & bufReadResponse[4])));

		sensors[0] = sensorOne;
		sensors[1] = sensorTwo;
		sensors[2] = sensorThree;

		return (sensors);
	}

	public int[] readAddressBytes(byte startByte) {

		getData(startByte, bufReadResponse, 4);

		int sensorOne = (0xFF & bufReadResponse[0]);
		int sensorTwo = (0xFF & bufReadResponse[1]);
		int sensorThree = (0xFF & bufReadResponse[2]);
		int sensorFour = (0xFF & bufReadResponse[3]);

		sensors[0] = sensorOne;
		sensors[1] = sensorTwo;
		sensors[2] = sensorThree;
		sensors[3] = sensorFour;

		return (sensors);
	}
	
	public int getDisYBack(){
		return disYBack;
	}
	public int getDisXRight(){
		return disXRight;
	}
	public int getDisXLeft(){
		return disXLeft;
		
	}
	public int getLightRight(){
		return lightRight;
	}
	public int getLightLeft(){
		return lightLeft;
	}
	public int getTouch(){
		return touch;
	}

//	public int readLightLeft() {
//
//		getData((byte) 82, bufReadResponse, 2);
//
//		int sensorLeft = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));
//
//		return (sensorLeft);
//	}
//
//	public int readLightRight() {
//
//		getData((byte) 84, bufReadResponse, 2);
//
//		int sensorRight = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));
//
//		return (sensorRight);
//	}
//
//	public int readPingYBack() {
//
//		getData((byte) 75, bufReadResponse, 1);
//
//		int sensorPing = 0xFF & bufReadResponse[0];
//
//		return (sensorPing);
//	}
//	public int readPingXLeft() {
//
//		getData((byte) 76, bufReadResponse, 1);
//
//		int sensorPing = 0xFF & bufReadResponse[0];
//
//		return (sensorPing);
//	}
//	public int readPingXRight() {
//
//		getData((byte) 74, bufReadResponse, 1);
//
//		int sensorPing = 0xFF & bufReadResponse[0];
//
//		return (sensorPing);
//	}
//
//	public int readTouch() {
//
//		getData((byte) 0x6C, bufReadResponse, 2);
//
//		int sensorTouch = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));
//
//		return (sensorTouch);
//	}

	public int readAddress(byte addr) {

		getData((byte) addr, bufReadResponse, 2);

		int value = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));

		return (value);
	}
	
	public boolean writeCommand(byte addr, byte command) {
		if(sendData((byte) addr, command) == 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public void update(){
		getData((byte) 74, bufReadResponse, 3);

		disXRight = 0xFF & bufReadResponse[0];
		disYBack = 0xFF & bufReadResponse[1];
		disXLeft = 0xFF & bufReadResponse[2];
		
		getData((byte) 82, bufReadResponse, 4);

		lightLeft = (((0xFF & bufReadResponse[1]) << 8) | ((0xFF & bufReadResponse[0])));
		lightRight = (((0xFF & bufReadResponse[3]) << 8) | ((0xFF & bufReadResponse[2])));
		

	}

}
