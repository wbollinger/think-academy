/* ArduNXT - Arduino based Lego Mondstorms NXT universal Remote Control Interface */
/* Please see http://code.google.com/p/ardunxt/ for further details and the latest version */
/* By Christopher Barnes */

/*
 // Aspects of this software were derived from code used in ArduPilot
 // By Chris Anderson, Jordi Munoz, Bill Premerlani, HappyKillMore
 // James Cohen, JB from rotorFX, Automatik, Fefenin, Peter Meister
 // & Remzibi.
 */

// Standard Arduino Serial output is unbuffered so be careful about the impact that lots of diagnostics has on performance.
// You might like to try an alternative version of the HardwareSerial.h and HardwareSerial.cpp, by Kiril available from:
// Arduino Forum > Software > Development, at
// http://www.arduino.cc/cgi-bin/yabb2/YaBB.pl?board=dev
// Search for "Improved HardwareSerial" and use these new files to replace those in hardware/arduino/cores/arduino 
// There should be enough memory on the ATMEGA328 to increase the buffer sizes too.  These are defined in HardwareSerial.h:
// #define USART_RX_BUFFER_SIZE  (64)
// #derine USART_TX_BUFFER_SIZE  (64)
//TODO
// Commands from NXT:
//  set RC Input centre
//  save config
//  control Mux
// Single status byte for NXT to read to say if anything has changed (bitmap of what?)
#include <avr/interrupt.h>
#include <avr/eeprom.h>
#include <avr/io.h>
#include <util/twi.h>

// Do not remove the include below
#include "ardurcj.h"

/***************************************************************************
 * General variables
 **************************************************************************/
volatile RCInputFlags g_RCIFlags[NUM_RCI_CH]; // RCInput channel flags
DiagnosticsFlags g_DiagnosticsFlags;
ConfigurationFlags g_ConfigurationFlags;
volatile MiscFlags g_MiscFlags;
unsigned int g_u16Pulse[NUM_RCI_CH]; // RCInput pulse widths
unsigned int g_analog0;

// Functions in Diagnostics
void Init_Diagnostics(void);

// Functions in NXTServoOutput
void ServoOutput_Handler(void);

// Functions in NXTAnalog
void Analog_Handler(void);

// Functions in NXTI2C
void NXT_Handler(void);
void Init_NXTIIC(void);

// Functions in Diagnostics
void DigitalInput_Monitor(void);
void Diagnostics_Handler(void);

// Functions in NXTRCInput
void Init_RCInputCh(void);

// Functions in NXTServoOutput
void Init_ServoOutput(void);

//---------------------------------------------------------------------
// Local Functions
//---------------------------------------------------------------------
static void Init_ArduNXT(void);
static void Load_Settings(void);
static void Save_Settings(void);


/***************************************************************************
 * 
 **************************************************************************/
void setup()
{
	Init_ArduNXT(); // Initialize application...
	Init_Diagnostics(); // Initialize diagnostics output
}

// Main loop executed as fast as possible
void loop()
{
	// Each "Handler" is designed to do a small amount of processing each time it is called in
	// a co-operative approach to multi-tasking.
	ServoOutput_Handler();
	//RCInput_Handler();

	Analog_Handler();

	NXT_Handler();
	//Multiplexer_Handler();

	// Support for development diagnostics and debugging information output
	if (g_DiagnosticsFlags.bDigitalInput) {
		DigitalInput_Monitor();
	}
	if (g_DiagnosticsFlags.bPerformance) {
		Diagnostics_Handler();
	}
}

/*****************************************
 *****************************************/
void Init_ArduNXT(void)
{
	// Configure ATMega Hardware for ArduNXT
	// based on functionality of Ardupilot PCB
	// PD0 = RS232 Serial Data input
	// PD1 = RS232 Serial Data output
	// Assignments which are commented out are initialized in the appropriate module...
	//  pinMode(2,INPUT);      // RC Input pin 0
	//  pinMode(3,INPUT);      // RC Input pin 1
	//  pinMode(4,INPUT);      // MUX output
	//  pinMode(5,INPUT);      // Servo output pin 3
	//  pinMode(6,INPUT);      // RC Input pin 2
	//  pinMode(7,OUTPUT);     // GPS RX Input Mux output can we afford to spare a pin for this?
	//  pinMode(8,OUTPUT);     // Servo output pin 2
	//  pinMode(9,OUTPUT);     // Servo output pin 0
	//  pinMode(10,OUTPUT);    // Servo output pin 1
	//  pinMode(11,INPUT);     // RC Input pin 3
	//  pinMode(12,OUTPUT);    // Blue LED output pin
	//  pinMode(13,OUTPUT);    // Yellow LED output pin

	// Initialize basic variables
	g_DiagnosticsFlags.u8Value = 0U;
	g_MiscFlags.u8Value = 0U;

	// Initialize Serial Port
	Serial.begin(SERIAL_BAUD);

	// Title Header
	Serial.println("");
	Serial.print(TITLE_STRING);
	Serial.println(VERSION_STRING);

	Load_Settings(); //Loading saved settings

	// Override saved settings
	g_DiagnosticsFlags.bRCInput = TRUE;
	g_ConfigurationFlags.bDSM2Enable = TRUE;

	// Initialize all modules
	Save_Settings();
	// Init_Multiplexer();
	Init_RCInputCh();
	Init_ServoOutput();
	Init_NXTIIC();

	// Everything initialized - enable interrupts
	sei();
}

/*****************************************************************************
 *****************************************************************************/
void Load_Settings(void)
{
	Serial.println("Load Settings");
	g_DiagnosticsFlags.u8Value = (byte) eeprom_read_byte(
			(const uint8_t *) EE_DIAGNOSTICS_FLAGS);
	g_ConfigurationFlags.u8Value = (byte) eeprom_read_byte(
			(const uint8_t *) EE_CONFIGURATION_FLAGS);
}

void Save_Settings(void)
{
	Serial.println("Save Settings");
	eeprom_busy_wait();
	eeprom_write_byte((uint8_t *) EE_DIAGNOSTICS_FLAGS,
			g_DiagnosticsFlags.u8Value);
	eeprom_write_byte((uint8_t *) EE_CONFIGURATION_FLAGS,
			g_ConfigurationFlags.u8Value);
}
