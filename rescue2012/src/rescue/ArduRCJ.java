package rescue;
import lejos.nxt.*;
import lejos.nxt.addon.MServo;

/**
 * This class is based on the MSC8 class. Unlike the Mindsensors NXT Servo which
 * manages up to 8 RC Servos, this Arduino equivalent supports only 4 servos.
 * 
 * For example, do:
 * 
 * <code>servoCtrl.servo1.setAngle(angle)</code>
 * 
 * to set the angle of the servo at location 1.
 * 
 * @author Juan Antonio Brenha Moral
 * @author S. Wayne Bollinger
 * 
 */
public class ArduRCJ extends I2CSensor {
	public static final byte NXTSERVO_ADDRESS = (byte) 0xb0;
	public static final byte ArduRCJ_VBATT = 0x41;// I2C Register to read battery

	/**
	 * Servos at locations 1 through 4
	 */
	public MServo servoCompass;
	public MServo servoClawGrip;
	public MServo servoClawLift;
	public MServo servo4;

	private MServo[] arrServo; // ServoController manages up to 4 RC Servos

	// I2C
	private SensorPort portConnected;
	
	byte[] bufReadResponse;
	int[] sensors;

	/**
	 * 
	 * Constructor
	 * 
	 * @param port
	 *            the NXTServo is connected to
	 * 
	 */
	public ArduRCJ(SensorPort port) {
		super(port, NXTSERVO_ADDRESS, I2CPort.STANDARD_MODE, TYPE_LOWSPEED_9V);

		portConnected = port;

		// Arduino pin  9 == Servo 1
		//         pin 10 == Servo 2
		//         pin  8 == Servo 3
		servoCompass  = new MServo(portConnected, 1);
		servoClawGrip = new MServo(portConnected, 2);
		servoClawLift = new MServo(portConnected, 3);
		servo4 = new MServo(portConnected, 4);

		arrServo = new MServo[4];
		arrServo[0] = servoCompass;
		arrServo[1] = servoClawGrip;
		arrServo[2] = servoClawLift;
		arrServo[3] = servo4;
		
		bufReadResponse = new byte[6];
		sensors = new int[3];
	}

	/**
	 * Method to get an RC Servo in from the NXTServo
	 * 
	 * @param location
	 *            location of the servo (from 1 to 4)
	 * @return the MServo object
	 * 
	 */
	public MServo getServo(int location) {
		return arrServo[location - 1];
	}

	/**
	 * Read the battery voltage data from NXTServo module (in millivolts)
	 * 
	 * @return the battery voltage in millivolts
	 */
	public int getBattery() {
		byte[] bufReadResponse = new byte[1];
		getData(ArduRCJ_VBATT, bufReadResponse, 1);

		// 37 is calculated from 4700 mv /128
		return (37 * (0xFF & bufReadResponse[0]));
	}
	
	public int[] readAddressValues(byte startByte) {
		
		getData(startByte, bufReadResponse, 6);
		
		int sensorOne = (((0xFF&bufReadResponse[1])<<8)|((0xFF&bufReadResponse[0])));
		int sensorTwo = (((0xFF&bufReadResponse[3])<<8)|((0xFF&bufReadResponse[2])));
		int sensorThree = (((0xFF&bufReadResponse[5])<<8)|((0xFF&bufReadResponse[4])));
		
		
		sensors[0] = sensorOne;
		sensors[1] = sensorTwo;
		sensors[2] = sensorThree;
		
		return (sensors);
	}
	
	public int readLightLeft() {
		
		getData((byte)0x64, bufReadResponse, 2);
		
		int sensorLeft = (((0xFF&bufReadResponse[1])<<8)|((0xFF&bufReadResponse[0])));
		
		return (sensorLeft);
	}
	
	public int readLightRight() {
		
		getData((byte)0x62, bufReadResponse, 2);
		
		int sensorRight = (((0xFF&bufReadResponse[1])<<8)|((0xFF&bufReadResponse[0])));
		
		return (sensorRight);
	}

	public int readEOPD() {
		
		getData((byte)0x66, bufReadResponse, 2);
		
		int sensorRight = (((0xFF&bufReadResponse[1])<<8)|((0xFF&bufReadResponse[0])));
		
		return (sensorRight);
	}

}
