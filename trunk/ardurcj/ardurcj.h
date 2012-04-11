// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef ardurcj_H_
#define ardurcj_H_
#include "Arduino.h"
//add your includes for the project ardurcj here

// Function Prototypes for integration with twi4nxt code
//extern void twi4nxt_attachSlaveRxEvent(void (*)(byte*, uint8_t));
//extern void twi4nxt_attachSlaveTxEvent(void (*)(void));

#define TITLE_STRING    "ArduRCJ Universal Interface"
#define VERSION_STRING  "V1.09"            // This is NOT the same as the version reported over the NXT interface
// which is defined in NXTI2C.pde

// Select the Baud rate you want to use:
#define SERIAL_BAUD		(115200)		   // Baud Rate used for DSM2 satellite receiver (and diagnostics)
//#define SERIAL_BAUD     (38400)          // Baud Rate used for GPS and Diagnostics

#define NUM_ANALOG_CH		(4U)		   // Number of Analog Input Channels
#define NUM_SERVO_CH		(4U)		   // total number of servo output channels
//#define ATTINY_IN_USE                    // If you do not hold the ATTiny in reset, on the Ardupilot prototype,
                                           // then it is in contrl of the multiplexer
#define MINDSENSORS_NXT_SERVO_COMPATIBLE   // If you want FULL compatibility with Mindsensors NXT Servo Sensor (I2C address and Servo Position Readback)
                                           // Some of the code has been migrated from a previous project which uses the following type definitions:
typedef signed char INT_8;
typedef unsigned char UINT_8;
typedef signed int INT_16;
typedef unsigned int UINT_16;
typedef unsigned long UINT_24; // Not sure that there is any way to get a true 24 bit type in Arduino?
typedef signed long INT_32;
typedef unsigned long UINT_32;

#ifndef TRUE
#define TRUE          (0x01)
#endif
#ifndef FALSE
#define FALSE         (0x00)
#endif

// Flags to control which Diagnostics outputs we want to see on the serial port
typedef union {
	struct {
		unsigned bAnalogInput :1; // Analog Input
		unsigned bServoOutput :1; // Servo Output
		unsigned bNXTInterface :1; // I2C NXT interface
		unsigned bMultiplexer :1; // Multiplexer state
		unsigned bDigitalInput :1; // Digital Inputs
		unsigned bDigitalOutput :1; // Digital Outputs
		unsigned bPerformance :1; // Software performance metrics
	};
	struct {
		UINT_8 u8Value;
	};
} DiagnosticsFlags;

// Flags to configure ArduNXT
typedef union {
	struct {
		//unsigned bDSM2Enable :1; // Serial Port used for DSM2 satellite receiver
	};
	struct {
		UINT_8 u8Value;
	};
} ConfigurationFlags;

// Assorted Flags
typedef union {
	struct {
		unsigned bFrameUpdate :1; // PWM Servo Output Frame Update
		unsigned bFrameMissed :1; // PWM Servo Output Frame has been missed - check Diagnostics for loop execution performance...
	};
	struct {
		UINT_8 u8Value;
	};
} MiscFlags;

typedef union {
	struct {
		// High level
		unsigned bValid :1; // Flag to indicate that the channel is being used
		unsigned bUpdate :1; // Flag to indicate that data has been updated
	};
	struct {
		UINT_8 u8Value;
	};
} AnalogInputFlags;

/*************************************************************************
 *
 *************************************************************************/

// Where things are stored in EEPROM
#define EE_DIAGNOSTICS_FLAGS    (0x01)
#define EE_CONFIGURATION_FLAGS	(0x02)

/***************************************************************************
 * General variables
 **************************************************************************/
extern volatile AnalogInputFlags g_AnalogFlags[NUM_ANALOG_CH]; // Analog Input channel flags
extern DiagnosticsFlags g_DiagnosticsFlags;
extern ConfigurationFlags g_ConfigurationFlags;
extern volatile MiscFlags g_MiscFlags;

unsigned Analog_getChannel(unsigned char u8Ch);

//end of add your includes here
#ifdef __cplusplus
extern "C" {
#endif
void loop();
void setup();
#ifdef __cplusplus
} // extern "C"
#endif

//Do not add code below this line
#endif /* ardurcj_H_ */
