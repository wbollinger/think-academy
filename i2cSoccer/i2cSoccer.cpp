// Do not remove the include below
#include "i2cSoccer.h"

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>    // keeping constants in program memory
#include <Arduino.h>

// NOTE:  turning on serial debug takes over two pins!
// See #define DEBUG in header file
int debug = DEBUG;

#include <NewPing.h>

#define TRIGGER_PIN  10  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     11  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 255 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

// static constructor to setup NewPing pins and maximum distance
NewPing sonar_0(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
NewPing* sonar = NULL;

/***************************************************************************
 * General variables
 **************************************************************************/
volatile AnalogInputFlags g_AnalogFlags[NUM_ANALOG_CH]; // Analog Input channel flags
volatile DigitalInputFlags g_DigitalFlags[NUM_DIGITAL_CH]; // Digital Input channel flags
DiagnosticsFlags   g_DiagnosticsFlags;
ConfigurationFlags g_ConfigurationFlags;

// Functions in NXTAnalog
void Analog_Handler(void);
// Functions in NXTDigital
void Digital_Handler(void);

// Functions in NXTI2C
void NXT_Handler(void);
void Init_NXTIIC(void);

// Functions in Diagnostics
void Init_Diagnostics(void);
void DigitalInput_Monitor(void);
void Diagnostics_Handler(void);


//-----------------------------------------------------------------------------
void Init_ArduSoccer(void)
{
	// Configure ATMega Hardware for ArduNXT

	// Initialize basic variables
	g_DiagnosticsFlags.u8Value = 0U;
//	g_MiscFlags.u8Value = 0U;

#if DEBUG != 0
	// Initialize Serial Port
	Serial.begin(SERIAL_BAUD);

	// Title Header
	Serial.println("");
	Serial.print(TITLE_STRING);
	Serial.println(VERSION_STRING);
#endif

	// Optional settings
	g_DiagnosticsFlags.bAnalogInput = TRUE;

	g_DiagnosticsFlags.bNXTInterface = FALSE;  // show Rx: and Rq: byte counts
	g_DiagnosticsFlags.bPerformance = FALSE;
	g_DiagnosticsFlags.bExecutionMS = FALSE;

	// Initialize all modules
	Init_NXTIIC();

	// Everything initialized - enable interrupts
	sei();
}

//-----------------------------------------------------------------------------
/**
 * Initializes everything; called once on startup.
 */
void setup()
{
	// init watchdog
	//   Hardware "bare metal" default: wdt_enable(WDTO_15MS); // 15ms watchdog
	// Recommendation from:
	//   http://tushev.org/articles/electronics/48-arduino-and-watchdog-timer

	// 2sec watchdog
	wdt_enable(WDTO_2S);

	// Initialize this application and diagnostic output
	Init_ArduSoccer();
	Init_Diagnostics();

	// initialize global pointer to Ping
	sonar = &sonar_0;
}

//-----------------------------------------------------------------------------
/**
 * The main loop which controls the actual output.
 */
void loop()
{
	// feed the watchdog timer
	wdt_reset();

	// Each "Handler" is designed to do a small amount of processing each time it is called in
	// a co-operative approach to multi-tasking.

	Analog_Handler();
	Digital_Handler();

	NXT_Handler();

	// Support for development diagnostics and debugging information output
	if (g_DiagnosticsFlags.bDigitalInput) {
		DigitalInput_Monitor();
	}
	if (g_DiagnosticsFlags.bPerformance) {
		Diagnostics_Handler();
	}
}
