// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef arduled_H_
#define arduled_H_

#include "Arduino.h"

#include <stdint.h>        /* defines the data type uint8_t */


/* User-configurable settings */
#define buffer_size 9      /* size of the buffer in bytes (2 .254) */

#define TITLE_STRING    "Arduino LED Matrix"
#define VERSION_STRING  "V1.00"            // This is NOT the same as the version reported over the NXT interface
                                           // which is defined in NXTI2C.cpp

// Select the Baud rate you want to use:
#define SERIAL_BAUD		(115200)		   // Baud Rate used for DSM2 satellite receiver (and diagnostics)

#define NUM_ANALOG_CH		(4U)		   // Number of Analog Input Channels
#define NUM_DIGITAL_CH		(4U)		   // Number of Digital Input Channels
//#define NUM_SERVO_CH		(4U)		   // total number of servo output channels

//#define MINDSENSORS_NXT_SERVO_COMPATIBLE   // If you want FULL compatibility with Mindsensors NXT Servo Sensor (I2C address and Servo Position Readback)
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

extern DiagnosticsFlags g_DiagnosticsFlags;

/* Global variables used by the main program */
/**
 * The buffer where the received data is stored. The slave
 * Works much like a normal memory IC [I2C EEPROM], it sends
 * The address at which you want to write, then the data, the internal
 * Memory address will update automatically.
 */
extern volatile uint8_t rxBuffer[buffer_size];

/**
 * The transmit buffer, which can be read from the master.
 */
extern volatile uint8_t txBuffer[buffer_size];


#ifdef __cplusplus
extern "C" {
#endif

void loop();
void setup();

#ifdef __cplusplus
} // extern "C"
#endif

//add your function definitions for the project i2cMatrixDevelop here




//Do not add code below this line
#endif
