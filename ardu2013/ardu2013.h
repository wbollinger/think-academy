// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef ardu2013_H_
#define ardu2013_H_
#include "Arduino.h"

#include <stdint.h>        /* defines the data type uint8_t */


/* User-configurable settings */
// NOTE:  turning on serial debug takes over two pins!
#define DEBUG 1
extern int debug;

#define TITLE_STRING    "Arduino NXT Soccer"
#define VERSION_STRING  "V2.00"            // This is NOT the same as the version reported over the NXT interface
                                           // which is defined in NXTI2C.cpp

// Select the Baud rate you want to use:
#define SERIAL_BAUD		(115200)		   // Baud Rate used for diagnostics

#define NUM_ANALOG_CH		(4U)		   // Number of Analog Input Channels
#define NUM_DIGITAL_CH		(4U)		   // Number of Digital Input Channels

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
		unsigned bNXTInterface :1; // I2C NXT interface
		unsigned bDigitalInput :1; // Digital Inputs
		unsigned bDigitalOutput :1; // Digital Outputs
		unsigned bPerformance :1; // Software performance metrics
		unsigned bExecutionMS :1; // Software performance metrics
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

typedef union {
	struct {
		// High level
		unsigned bValid :1; // Flag to indicate that the channel is being used
		unsigned bUpdate :1; // Flag to indicate that data has been updated
	};
	struct {
		UINT_8 u8Value;
	};
} DigitalInputFlags;

/***************************************************************************
 * General Global variables used by the main program
 **************************************************************************/
extern volatile AnalogInputFlags g_AnalogFlags[NUM_ANALOG_CH]; // Analog Input channel flags
extern volatile DigitalInputFlags g_DigitalFlags[NUM_DIGITAL_CH]; // Digital Input channel flags
extern DiagnosticsFlags g_DiagnosticsFlags;

class NewPing;
extern NewPing* sonar;

#ifdef __cplusplus
extern "C" {
#endif

void loop();
void setup();

#ifdef __cplusplus
} // extern "C"
#endif

//add your function definitions for the project here
unsigned      Analog_getChannel(unsigned char u8Ch);
unsigned char Digital_getChannel(unsigned char u8Ch);


//Do not add code below this line
#endif /* ardu2013_H_ */
