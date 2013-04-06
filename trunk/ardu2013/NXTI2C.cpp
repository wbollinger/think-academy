/* ArduNXT - Arduino based Lego Mondstorms NXT universal Remote Control Interface */
/* Please see http://code.google.com/p/ardunxt/ for further details and the latest version */

// Do not remove the include below
#include "ardu2013.h"
//#include <stddef.h>

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
 *  work with the "sensorI2CCustomFast" configuration.
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
 *  suitable NXT code, as the register address is automatically
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
 *  which can be read by the NXT can be customized.
 *
 ************************************************************/

//---------------------------------------------------------------------
// Constant Definitions
//---------------------------------------------------------------------
#define ARDUNXT_I2C_ADDRESS			((uint8_t)(0x58))        	// Our IIC slave address (this is a 7 bit value)
#define NXT_SHARED_CONST_DATA_OFFSET    (0x00)					// The address offset of the first read only register
#define NXT_SHARED_CONST_DATA_SIZE		(0x18)					// The number of bytes allocated to the const memory
#define NXT_SHARED_DATA_OFFSET			(0x40)					// The address offset of the first read/write register
#define NXT_SHARED_DATA_SIZE			(0x40)					// The number of bytes allocated to the shared memory (See below)
#define NXT_TRANSACTION_TIMEOUT			(100U)					// number of milliseconds since last valid data transfer before we timeout connection
#define NXT_VERSION					'V','1','.','0','0',0,0,0		// Version string - must be 8 characters
#define NXT_MANUFACTURER			'T','h','i','n','k','D','I','Y'	// Manufacturer string - must be 8 characters
#define NXT_DEVICE_NAME				'A','r','d','u','S','O','C',0	// Device Name string - must be 8 characters
// If you want an LED to indicate the state of the NXT connection define which pin it is on here
// obviously you will need to be careful not to make use of this pin elsewhere in your code.
// If you don't want an NXT status LED then do not define NXT_LED_PIN
#define NXT_LED_PIN              			   (13) // (13) default, A3 for LED matrix
#define NUM_SERVOS                             (4)  // dummy value so addresses in shared data don't move
#define NUM_PINGS                              (4)    // Number of Ping sensors

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

		// Initial structure was compatible with the Mindsensors NXT Servo Sensor
		// for NUM_SERVOS == 8
		volatile UINT_16 u16ServoPosition[NUM_SERVOS]; // 0x32 - 0x41 16bit PWM Servo Position Registers (time in uS)

		volatile byte u8PingReadings[NUM_PINGS]; // 0x4A - 0x51 8bit Ping Sensor values
		volatile byte u8Dummy[8 - NUM_PINGS];    // For alignment, should be 8 bytes total

		// Extension fields

		// Analog Measurement Inputs
		UINT_16 u16AnalogValue[NUM_ANALOG_CH]; // 0x52 - 0x5F Analog Input (Raw value from 0 to 1023)
		INT_8 i8DigitalValue[NUM_DIGITAL_CH]; // 0x60 - 0x66 Digital Input (was 8 bit scaled value in original code)

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
static UINT_16 m_u8PingMeasurements[NUM_PINGS]; // Current readings of Ping sensors

// A few sanity checks
#if (255 < (NXT_SHARED_DATA_OFFSET + NXT_SHARED_DATA_SIZE))
#error 'NXT Code only supports single byte for address'
#endif

void twi4nxt_setAddress(uint8_t address);
void twi4nxt_attachSlaveTxEvent(void (*function)(void));
void twi4nxt_attachSlaveRxEvent(void (*function)(byte*, uint8_t));
void twi4nxt_init(void);
uint8_t twi4nxt_transmitConst(const byte* data, uint8_t length);
uint8_t twi4nxt_transmit(byte* data, uint8_t length);
void twi4nxt_releaseBus(void);
bool twi4nxt_IsReady(void);

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
#define OFFSETOF(type, field)    ((unsigned long) &(((type *) 0)->field))

void Init_NXTIIC(void)
{
	int i;

	// Initialize the Wire Library (this is the I2C (TWI/SMBus) library
	Serial.println("Init_NXTIIC");
	Serial.print("Offset ");
	//Serial.print(OFFSETOF(m_NXTInterfaceData.Fields, Fields.u16AnalogValue));

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

	// Initialize NXT Ping values - distance readings
	for (i = 0; i < NUM_PINGS; i++) {
		m_u8PingMeasurements[i] = 0U;
	}

	// Initial state of configuration flags
	//m_NXTInterfaceData.Fields.u8Configuration = g_ConfigurationFlags.u8Value;

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
			// Normal (recommended) path to read bytes from shared memory area
			twi4nxt_transmit((byte *) &m_NXTInterfaceData.au8Raw[u8Offset], 1);

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

		}

		// Values in Fields (to be read by the NXT) could be written to directly by applicable code throughout the system,
		// or we can update them regularly here (which enables us to avoid changing multi-byte fields while the NXT may be part way through reading them).
		if (twi4nxt_IsReady()) {
			NXTUpdateValues();
		}
	}

	if (g_DiagnosticsFlags.bNXTInterface) {
#if DEBUG != 0
		NXTDiagnostics();
#endif
	} else {
		m_u8NXTNumReceived = 0;
	}
}

//---------------------------------------------------------------------
// Function to update fields in the NXT shared memory area
//---------------------------------------------------------------------
static void NXTUpdateValues(void)
{
	for (byte i = 0; i < NUM_ANALOG_CH; i++) {
		if (g_AnalogFlags[i].bUpdate) {
			// Analog value (may) have been updated
			//int i16AnalogScaled = (int) Analog_getChannel(i) / 4;  // Will scale this value
			//m_NXTInterfaceData.Fields.i8AnalogValue[i] = (INT_8) i16AnalogScaled; // 8 bit version of Signed Radio Control input (units of 4uS)

			m_NXTInterfaceData.Fields.u16AnalogValue[i] = Analog_getChannel(i);     // Read raw Analog Value

			g_AnalogFlags[i].bUpdate = FALSE; // Clear Flag to indicate that value has been updated
		}
	}

	for (byte i = 0; i < NUM_DIGITAL_CH; i++) {
		if (g_DigitalFlags[i].bUpdate) {
			// Digital value (may) have been updated
			m_NXTInterfaceData.Fields.i8DigitalValue[i] = (INT_8) Digital_getChannel(i);
			g_DigitalFlags[i].bUpdate = FALSE; // Clear Flag to indicate that value has been updated
		}

	}

	for (byte i = 0; i < NUM_PING_CH; i++) {
			if (g_PingFlags[i].bUpdate) {
				// Digital value (may) have been updated
				m_NXTInterfaceData.Fields.u8PingReadings[i] = (INT_8) Ping_getChannel(i);
				g_PingFlags[i].bUpdate = FALSE; // Clear Flag to indicate that value has been updated
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
#if 1
	if (m_u8NXTNumRequests) {
		// Number of bytes requested by the NXT in monitoring period
		Serial.print("Rq ");
		Serial.println((int) m_u8NXTNumRequests);
		m_u8NXTNumRequests = 0;
	}
#endif
	if (m_u8IllegalAddress) {
		Serial.print("Illegal Address ");
		Serial.println((int) m_u8IllegalAddress);
		m_u8IllegalAddress = 0U;
	}
}
