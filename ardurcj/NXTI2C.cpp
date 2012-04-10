/* ArduNXT - Arduino based Lego Mondstorms NXT universal Remote Control Interface */
/* Please see http://code.google.com/p/ardunxt/ for further details and the latest version */

// Do not remove the include below
#include "ardurcj.h"

/*************************************************************
 *  Arduino Lego Mindstorms NXT Interface
 *  Written by Christopher Barnes
 *  October 2009 ...
 *
 *  Connect NXT Sensor I2C to Arduino:
 *  Arduino analog input 5 - I2C SCL
 *  Arduino analog input 4 - I2C SDA
 *  Both signals require a pull-up resistor (82KOhm) to Vdd.
 *
 *  Notes:
 *  The NXT is the Master and we are the slave IIC device.
 *  Lego NXT IIC operates at approximately 9600Baud, but as we are  
 *  the slave we don't have to do anything specific for this.
 *  RobotC can support faster IIC operation, this device can
 *  work with the "sensorI2CCustomFast" configuraiton.
 *
 *  All messages start with our slave address which is defined
 *  by ARDUNXT_I2C_ADDRESS (the twi4nxt.c code takes care
 *  of only responding to messages which start with this
 *  address). Most Lego Sensors use an address of 0x01 as 
 *  there is only one sensor connected per NXT IIC Sensor 
 *  interface port. (The address is a 7 bit field, the LSBit
 *  of the addressing byte in IIC is used to indicate read
 *  or write operations, thus for a read the byte is 0x02
 *  and for write it is 0x03) 
 *  However, when used in Mindsensors NXT Servo Sensor compatibility mode
 *  the address is 0x58 (i.e. 0xB0/1 when combined with direction bit)
 *
 *  The NXT reads and writes data from/to memory 
 *  mapped registers, using an 8 bit register address.
 *  Multiple bytes can be read or written at a time, with
 *  sutiable NXT code, as the register address is automatically
 *  incremented after each byte has been read/written.
 *  While a data transfer is in progress the shared memory is not
 *  updated by the ArduNXT, this is to avoid any multi-byte
 *  values from changing between reading the individual bytes.
 *
 *  This code is generic and can be used to share any data
 *  with the NXT.  As the IIC actions required to behave as a
 *  slave device are triggered by interrupts (handled in twi4nxt.c)
 *  this code is very efficient - it wastes no time in loops.
 *
 *  Constant strings for the Device, Manufacturer and version
 *  which can be read by the NXT can be customised.
 *
 ************************************************************/

/*************************************************************
 *  To do:
 *  1) Control over standard features set or overrides
 *
 ************************************************************/

// Make these includes in the main sketch file
//#include <avr/io.h>
//extern void twi4nxt_attachSlaveRxEvent( void (*)(byte*, uint8_t) );
//extern void twi4nxt_attachSlaveTxEvent( void (*)(void) );

//---------------------------------------------------------------------
// Constant Definitions
//---------------------------------------------------------------------
#ifdef MINDSENSORS_NXT_SERVO_COMPATIBLE
#define ARDUNXT_I2C_ADDRESS			((uint8_t)(0x58))        	// Our IIC slave address (this is a 7 bit value)
#else
#define ARDUNXT_I2C_ADDRESS			((uint8_t)(0x01))        	// Our IIC slave address (this is a 7 bit value)
#endif                                                                       
#define NXT_SHARED_CONST_DATA_OFFSET    (0x00)					// The address offset of the first read only register
#define NXT_SHARED_CONST_DATA_SIZE		(0x18)					// The number of bytes allocated to the const memory
#define NXT_SHARED_DATA_OFFSET			(0x40)					// The address offset of the first read/write register
#define NXT_SHARED_DATA_SIZE			(0x40)					// The number of bytes allocated to the shared memory (See below)
#define NXT_TRANSACTION_TIMEOUT			(100U)					// number of milliseconds since last valid data transfer before we timeout connection
#define NXT_VERSION					'V','1','.','0','0',0,0,0		// Version string - must be 8 characters
#define NXT_MANUFACTURER			'D','I','Y','D','r','o','n','e'	// Manufacturer string - must be 8 characters
#define NXT_DEVICE_NAME				'A','r','d','u','R','C','J',0	// Device Name string - must be 8 characters

// If you want an LED to indicate the state of the NXT connection define which pin it is on here
// obviously you will need to be careful not to make use of this pin elsewhere in your code.
// If you don't want an NXT status LED then do not define NXT_LED_PIN
#define NXT_LED_PIN              				(13)

#ifdef MINDSENSORS_NXT_SERVO_COMPATIBLE
#define NUM_SERVOS                              (8)                             // Number of Servos we have control registers for
// Although we don't physically have control of this
// many outputs this makes the registers compatible
// with the Mindsensors NXT Servo Sensor
#else
#define NUM_SERVOS		                        (4)                             // Number of Servos we have control registers for
#endif                                                                                

#define DFLT_SERVO_SPEED						(50)							// Speed of change of Servo pulse width in uS per frame		
//---------------------------------------------------------------------
// Macro Definitions
//---------------------------------------------------------------------
#if defined(NXT_LED_PIN)
#define NXT_LED(state)	digitalWrite(NXT_LED_PIN, state)
#else
#define NXT_LED(state)	{}
#endif

//---------------------------------------------------------------------
// Type and Structure Definitions
//---------------------------------------------------------------------

const static union {
	// The NXT accesses data by register address, which
	// is used as the offset into this byte array
	byte au8Raw[NXT_SHARED_DATA_SIZE]; // The size of this array must be at least as large as the size of the Fields structure below

	// The Arduino code accesses data by variable names
	// BE CAREFUL If you add or remove fields, or change their width (number of bytes)
	// as this will cause the register addresses to move - code in the NXT will need
	// to be modified to keep in sync.
	struct { // Register address (starting at NXT_SHARED_CONST_DATA_OFFSET)
		char szVersion[8];
		char szManufacturer[8];
		char szDeviceName[8];
	} Fields;
} m_NXTInterfaceConstData = { NXT_VERSION, // Version string - must be 8 characters
		NXT_MANUFACTURER, // Manufacturer string - must be 8 characters
		NXT_DEVICE_NAME // Device Name string - must be 8 characters
		};

// This is the main structure used to enable the NXT to access variables
// which are meaningful on the Arduino.
// The NXT needs to be aware that the Arduino is little endian (which the NXT appears to be too)
// Although strictly speaking this should have a 'volatile' qualifier is it more trouble than it is worth
// given the structure of the code.
static union {
	// The NXT accesses data by register address, which
	// is used as the offset into this byte array
	byte au8Raw[NXT_SHARED_DATA_SIZE]; // The size of this array must be at least as large as the size of the Fields structure below

	// The Arduino code accesses data by variable names
	// BE CAREFUL If you add or remove fields, or change their width (number of bytes)
	// as this will cause the register addresses to move - code in the NXT will need
	// to be modified to keep in sync.
	//
	struct { // Register address (starting at NXT_SHARED_DATA_OFFSET)
		volatile byte u8Configuration; // 0x40 Device Configuration
		volatile byte u8Command; // 0x41 Simple Commands from NXT

		// Initial structure is compatible with the Mindsensors NXT Servo Sensor
		volatile UINT_16 u16ServoPosition[NUM_SERVOS]; // 0x42 - 0x51 16bit PWM Servo Position Registers (time in uS)
		volatile byte u8ServoSpeed[NUM_SERVOS]; // 0x52 - 0x59 8bit Servo Speed
		volatile byte u8QuickPosition[NUM_SERVOS]; // 0x5A - 0x61 8bit Quick PWM Servo Position Registers

		// Extension fields

		// Analog Measurement Inputs
		UINT_16 u16AnalogValue[NUM_ANALOG_CH]; // 0x62 - 0x6F Analog Input (Raw value from 0 to 1023)
		INT_8 i8AnalogValue[NUM_ANALOG_CH]; // 0x70 - 0x76 Signed Analog Input (8 bit scaled value)
		byte u8MuxMode; // 0x77 Multiplexer Mode (0 = Radio Control, 1 = NXT Control)
		byte u8Dummy;

		// GPS 
		//GPSRecord  GPS;				                    // 0x78 -    
	} Fields;
} m_NXTInterfaceData;

//---------------------------------------------------------------------
// Variable declarations
//---------------------------------------------------------------------
static uint8_t m_u8NXTNumReceived; // Count of number of bytes received (diagnostics)
static uint8_t m_u8NXTNumRequests; // Count of number of bytes requested (diagnostics)
static uint8_t m_u8NXTAddress; // Register address that the NXT is currently accessing
static unsigned long m_u32NXTLastRequest; // Time (in millis) at which the last valid request occured
static bool m_bNXTAlive; // Record of whether we believe that NXT communications are alive
static bool m_bNXTActivity; // Flag that there has been interrupt handled data transfer activity
static uint8_t m_u8IllegalAddress; // Record of illegal addresses that NXT attempts to address - for debugging purposes

// Servo Control
static UINT_16 m_u16ServoPosition[NUM_SERVOS]; // Current Position of Servo

// A few sanity checks
#if (255 < (NXT_SHARED_DATA_OFFSET + NXT_SHARED_DATA_SIZE))
#error 'NXT Code only supports single byte for address'
#endif

void twi4nxt_setAddress(uint8_t address);
void twi4nxt_attachSlaveTxEvent( void (*function)(void) );
void twi4nxt_attachSlaveRxEvent( void (*function)(byte*, uint8_t) );
void twi4nxt_init(void);
uint8_t twi4nxt_transmitConst(const byte* data, uint8_t length);
uint8_t twi4nxt_transmit(byte* data, uint8_t length);
void twi4nxt_releaseBus(void);
bool twi4nxt_IsReady(void);

void ServoOutput(byte u8Ch, unsigned int u16PulseWidth);


//---------------------------------------------------------------------
// Local Functions
//---------------------------------------------------------------------
static void NXTOnRequest(void);
static void NXTOnReceive(byte *u8Received, uint8_t NumBytesReceived);
static void NXTUpdateValues(void);
static void NXTDiagnostics(void);

//---------------------------------------------------------------------
// Global Functions
//---------------------------------------------------------------------

//---------------------------------------------------------------------
// Initialization - call once when starting up
//---------------------------------------------------------------------
void Init_NXTIIC(void)
{
	int i;

	// Initialize the Wire Library (this is the I2C (TWI/SMBus) library
	Serial.println("Init_NXTIIC");

	// Initialize NXT status LED output pin
#if defined(NXT_LED_PIN)
	pinMode(NXT_LED_PIN, OUTPUT); // LED pin configured as an output
#endif

	// Code based on TWI4NXT
	twi4nxt_setAddress(ARDUNXT_I2C_ADDRESS); // Tell TWI system what slave address we are using
	twi4nxt_attachSlaveTxEvent(NXTOnRequest); // Register function to be called when NXT requests data
	twi4nxt_attachSlaveRxEvent(NXTOnReceive); // Register function to be called we receive data from the NXT
	twi4nxt_init();

	// Initialize variables
	m_u8NXTNumReceived = 0U;
	m_u8NXTNumRequests = 0U;
	m_u8NXTAddress = 0U;
	m_u32NXTLastRequest = 0U;
	m_bNXTAlive = false;
	m_bNXTActivity = false;
	m_u8IllegalAddress = 0U;
	// Initialize NXT shared data to 0
	for (i = 0; i < NXT_SHARED_DATA_SIZE; i++) {
		m_NXTInterfaceData.au8Raw[i] = 0U;
	}

	// Initialize NXT Servo Control - speed and current position
	for (i = 0; i < NUM_SERVOS; i++) {
		m_u16ServoPosition[i] = 0U;
		m_NXTInterfaceData.Fields.u8ServoSpeed[i] = DFLT_SERVO_SPEED;
	}

	// Initial state of configuration flags
	m_NXTInterfaceData.Fields.u8Configuration = g_ConfigurationFlags.u8Value;

}

//---------------------------------------------------------------------
// Callback function for when NXT requests a byte from us
//---------------------------------------------------------------------
void NXTOnRequest(void)
{
	if (!m_bNXTAlive) {
		// Connection not yet in use - we should receive an address before any read requests
		twi4nxt_transmitConst(&m_NXTInterfaceConstData.au8Raw[7], 1); // Dummy error return (/0) to avoid causing IIC to stall
		return;
	}
	// Send one or more bytes...
	if (m_u8NXTAddress < NXT_SHARED_DATA_OFFSET) {
		// Requested data is from the constant read only section
		byte u8Offset;

		// Calculate the offset into the shared memory array
		u8Offset = m_u8NXTAddress - NXT_SHARED_CONST_DATA_OFFSET;

		if (u8Offset < NXT_SHARED_CONST_DATA_SIZE) {
			twi4nxt_transmitConst(&m_NXTInterfaceConstData.au8Raw[u8Offset], 1);
		} else {
			// Out of range register address requested
		}
		// Auto increment to next byte - so that NXT can make multi-byte requests efficiently
		m_u8NXTAddress++;
	} else {
		// Data requested from shared memory area
		byte u8Offset;

		// Calculate the offset into the shared memory array
		u8Offset = m_u8NXTAddress - NXT_SHARED_DATA_OFFSET;

		if (u8Offset < NXT_SHARED_DATA_SIZE) {
#ifdef MINDSENSORS_NXT_SERVO_COMPATIBLE
			// For full compatibility with Mindsensors NXT Servo Sensor:
			// We need to read back different values from some fields rather than a copy of the values that have been written.
			// This is a bit of a pain and not what I would recommend for any new features as it requires extra code each time
			// and makes the code more complex - which is significant given that this is being executed in an interrupt
			if ((u8Offset >= 2) && (u8Offset < (2 + (NUM_SERVOS << 1)))) {
				// Return the current acutal position rather than the target position which has been set
				// We know that these values are 2 bytes wide - so we can send both bytes for transmission  
				twi4nxt_transmit(
						(byte *) &m_u16ServoPosition[(u8Offset >> 1) - 1], 2);
				m_u8NXTAddress++; // Compensate address for the fact that we have sent two bytes (rather than the usual one)
			} else
#endif // MINDSENSORS_NXT_SERVO_COMPATIBLE                        
			{
				// Normal (recommended) path to read bytes from shared memory area
				twi4nxt_transmit((byte *) &m_NXTInterfaceData.au8Raw[u8Offset],
						1);
			}
			// Auto increment to next byte - so that NXT can make multi-byte requests efficiently
			m_u8NXTAddress++;
		} else {
			// Out of range register address requested
		}
	}
	m_bNXTActivity = true;
	m_u8NXTNumRequests++; // Increment count of the number of valid bytes requested from us
}

//---------------------------------------------------------------------
// Callback function for when we receive one or more bytes from NXT
// All bytes received up to the IIC "stop" signal are received here in one go
// hence we do not need bData to be static retained across multiple calls.
//---------------------------------------------------------------------
void NXTOnReceive(byte *u8Received, uint8_t NumBytesReceived)
{
	bool bData = false;

	if (!m_bNXTAlive) {
		// Connection not yet in use - it is now
		// don't call digitalWrite to turn LED on here as it is verbose and we are in an interrupt routine
		m_bNXTAlive = true; // Remember that it is now working
		m_bNXTActivity = true;
	}

	while (NumBytesReceived--) {
		if (!bData) {
			// First byte we receive is the register address
			m_u8NXTAddress = *u8Received++;
			bData = true; // Having received the address any further data is being written to us
		} else {
			// Subsequent bytes we receive are data
			if (m_u8NXTAddress < NXT_SHARED_DATA_OFFSET) {
				// NXT is attempting to write to an address which is below the shared memory area
				m_u8IllegalAddress = m_u8NXTAddress;
			} else {
				byte u8Offset;

				// Calculate the offset into the shared memory array
				u8Offset = m_u8NXTAddress - NXT_SHARED_DATA_OFFSET;

				// Check that offset is in range
				if (u8Offset < NXT_SHARED_DATA_SIZE) {
					m_NXTInterfaceData.au8Raw[u8Offset] = *u8Received++;
					m_u8NXTNumReceived++; // Increment count of the number of valid data bytes we have received
					m_u8NXTAddress++; // Auto increment register address to support multi-byte transfers
				} else {
					// Request is out of range
					// Do not increment register address in this case to avoid it wrapping back round
					// and appearing to be back in a valid range
					m_u8IllegalAddress = m_u8NXTAddress;
				}
				m_bNXTActivity = true;
			}
		}
	}
}

//---------------------------------------------------------------------
// Handler to synchronize data between NXT shared memory and other parts of the system
//---------------------------------------------------------------------
void NXT_Handler(void)
{
	if (m_bNXTAlive) {
		NXT_LED(HIGH);
		// Switch NXT status LED On

		// Check if the connection is still alive
		if (m_bNXTActivity) {
			// remember time of latest activity
			m_u32NXTLastRequest = millis();
			m_bNXTActivity = false;
		} else if ((millis() - m_u32NXTLastRequest) > NXT_TRANSACTION_TIMEOUT) {
			// No activity Timeout
			NXT_LED(LOW);
			// Switch NXT status LED Off
			if (bit_is_clear(PINC, 5)) // Check for SCL stuck low
					{
				Serial.println("***** Release I2C Bus *****");
				twi4nxt_releaseBus(); // release Bus
			}
			m_bNXTAlive = false;
			m_u8NXTAddress = 0; // Reset register address
		}

		if (m_u8NXTNumReceived) {
			// We have received some data from NXT - so something has been changed
			// Values in Fields can be used directly by other code, or we can take notice of them regularly here.

			// Decode and handle COMMANDS from NXT
			switch (m_NXTInterfaceData.Fields.u8Command) {
			case 1: // Set RCInput Center values to current stick positions
				//RCInput_SetCentre();
				break;

			case 2:
//			  Serial.println("Rq Command 2");
				break;

			default:
				break;
			}
			m_NXTInterfaceData.Fields.u8Command = 0; // Clear any command now that we have done it

			// While the number of variables is small it is easier to just update everything,
			// if there were a lot more we might want to track which specific fields had been changed.
			g_ConfigurationFlags.u8Value =
					m_NXTInterfaceData.Fields.u8Configuration;

			// Examples
			// ========
			for (byte i = 0; i < NUM_SERVOS; i++) {
				if (m_NXTInterfaceData.Fields.u8QuickPosition[i]) {
					// We have been given a "Quick" position (which has units of 10uS) to apply immediately
					m_u16ServoPosition[i] = 0; // Clear current position so that the Quick position takes effect for the next frame
					m_NXTInterfaceData.Fields.u16ServoPosition[i] =
							m_NXTInterfaceData.Fields.u8QuickPosition[i] * 10;

					// Clear out record of Quck position 'request' now that we have actioned it
					m_NXTInterfaceData.Fields.u8QuickPosition[i] = 0;
				}
			}
		}

		// Values in Fields (to be read by the NXT) could be written to directly by applicable code throught the system,
		// or we can update them regularly here (which enables us to avoid changing multi-byte fields while the NXT may be part way through reading them).
		if (twi4nxt_IsReady()) {
			NXTUpdateValues();
		}
	}

	if (g_DiagnosticsFlags.bNXTInterface) {
		NXTDiagnostics();
	} else {
		m_u8NXTNumReceived = 0;
	}
}

//---------------------------------------------------------------------
// Servo Speed Control
// Each time a frame of Servo pulses have been output we update the position according to the defined speed
//---------------------------------------------------------------------
void NXTOnServoUpdate(void)
{
	UINT_16 u16TargetServoPosition;

	for (byte i = 0; i < NUM_SERVOS; i++) {
		u16TargetServoPosition = m_NXTInterfaceData.Fields.u16ServoPosition[i];

		if (u16TargetServoPosition) {
			// We have a valid Target Position
			if (m_NXTInterfaceData.Fields.u8ServoSpeed[i]
					&& m_u16ServoPosition[i]) {
				// We have a valid Speed (and previous position)
				if (u16TargetServoPosition < m_u16ServoPosition[i]) {
					// Need to reduce pulse width to move towards target position
					m_u16ServoPosition[i] -=
							m_NXTInterfaceData.Fields.u8ServoSpeed[i];
					if (m_u16ServoPosition[i] < u16TargetServoPosition) {
						// Limit movement to target position
						m_u16ServoPosition[i] = u16TargetServoPosition;
					}
					ServoOutput(i, m_u16ServoPosition[i]); // Update PWM generation
				} else if (u16TargetServoPosition > m_u16ServoPosition[i]) {
					// Need to increase pulse width to move towards target position
					m_u16ServoPosition[i] +=
							m_NXTInterfaceData.Fields.u8ServoSpeed[i];
					if (m_u16ServoPosition[i] > u16TargetServoPosition) {
						// Limit movement to target position
						m_u16ServoPosition[i] = u16TargetServoPosition;
					}
					ServoOutput(i, m_u16ServoPosition[i]); // Update PWM generation
				}
			} else if (m_u16ServoPosition[i] != u16TargetServoPosition) {
				// If the Speed register is zero then the servo is set to the target
				// position immediately
				m_u16ServoPosition[i] = u16TargetServoPosition;
				ServoOutput(i, m_u16ServoPosition[i]); // Update PWM generation
			}
		} else {
			m_u16ServoPosition[i] = 0; // Disabled
			ServoOutput(i, 0); // Disable PWM Generation on this channel
		}
	}
}

//---------------------------------------------------------------------
// Function to update fields in the NXT shared memory area
//---------------------------------------------------------------------
static void NXTUpdateValues(void)
{
	// Examples
	// ========
	m_NXTInterfaceData.Fields.u8MuxMode = 1; // NXT control - was Multiplexer_State();

	for (byte i = 0; i < NUM_ANALOG_CH; i++) {
		if (g_AnalogFlags[i].bUpdate) {
			// Analog value (may) have been updated
			int i16AnalogScaled = (int) Analog_getChannel(i) / 4;  // Will scale this value
			i16AnalogScaled = constrain(i16AnalogScaled, -128, 127);        // constrain value to fit in a single signed byte
			m_NXTInterfaceData.Fields.i8AnalogValue[i] = (INT_8) i16AnalogScaled; // 8 bit version of Signed Radio Control input (units of 4uS)

			m_NXTInterfaceData.Fields.u16AnalogValue[i] = Analog_getChannel(i);     // Read raw Analog Value

			g_AnalogFlags[i].bUpdate = FALSE; // Clear Flag to indicate that value has been updated
		}
	}
}

//---------------------------------------------------------------------
// Low level NXT I2C Diagnostics
//---------------------------------------------------------------------
static void NXTDiagnostics(void)
{
	if (m_u8NXTNumReceived) {
		// Number of bytes received from NXT in monitoring period (excluding the device addressing byte)
		Serial.print("Rx ");
		Serial.println((int) m_u8NXTNumReceived);
		m_u8NXTNumReceived = 0;
	}
#ifdef VERBOSE
	if (m_u8NXTNumRequests)
	{
		// Number of bytes requested by the NXT in monitoring period
		Serial.print("Rq ");
		Serial.println((int)m_u8NXTNumRequests);
		m_u8NXTNumRequests = 0;
	}
#endif
	if (m_u8IllegalAddress) {
		Serial.print("Illegal Address ");
		Serial.println((int) m_u8IllegalAddress);
		m_u8IllegalAddress = 0U;
	}
}
