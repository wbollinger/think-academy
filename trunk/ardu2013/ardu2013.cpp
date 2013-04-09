// Do not remove the include below
#include "ardu2013.h"

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>    // keeping constants in program memory
#include <Arduino.h>

// NOTE:  turning on serial debug takes over two pins!
// See #define DEBUG in header file
int debug = DEBUG;

/***************************************************************************
 * General variables
 **************************************************************************/
volatile AnalogInputFlags g_AnalogFlags[NUM_ANALOG_CH]; // Analog Input channel flags
volatile DigitalInputFlags g_DigitalFlags[NUM_DIGITAL_CH]; // Digital Input channel flags
volatile PingInputFlags g_PingFlags[NUM_PING_CH]; // Digital Input channel flags
DiagnosticsFlags   g_DiagnosticsFlags;
ConfigurationFlags g_ConfigurationFlags;

// Functions in NXTAnalog
void Init_Analog(void);
void Analog_Handler(void);
// Functions in NXTDigital
void Init_Digital(void);
void Digital_Handler(void);
// Functions in NXTPing
void Init_Ping(void);
void Ping_Handler(void);

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

	g_DiagnosticsFlags.bNXTInterface = TRUE;  // show Rx: and Rq: byte counts
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

	Init_Analog();
	Init_Digital();
	Init_Ping();
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
	Ping_Handler();

	NXT_Handler();

	// Support for development diagnostics and debugging information output
	if (g_DiagnosticsFlags.bDigitalInput) {
		DigitalInput_Monitor();
	}
	if (g_DiagnosticsFlags.bPerformance) {
		Diagnostics_Handler();
	}
}
