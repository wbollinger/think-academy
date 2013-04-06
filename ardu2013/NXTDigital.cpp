// Do not remove the include below
#include "ardu2013.h"

// Digital Inputs
// Array of Digital sensor readings - value from 0 to 1023

unsigned char g_u8Digital[NUM_DIGITAL_CH];

// HACK
//extern unsigned int g_u16Analog[NUM_ANALOG_CH];

void DigitalInput_Monitor(void)
{
  // remember previous states and report changes?
}

//---------------------------------------------------------------------
// Configure the Digital Input code
//---------------------------------------------------------------------
void Init_Digital(void)
{
	for (unsigned int i = 0; i < NUM_DIGITAL_CH; i++) {
		// init digital values to zero, clear update flags
		g_u8Digital[i] = 0;
		g_DigitalFlags[i].bUpdate = FALSE;
	}

	// initialize the switch pin as an input:
	// Turn on internal pull-up resister for switch connected to Digital input
	pinMode(11, INPUT);
	digitalWrite(11, HIGH);
	pinMode(12, INPUT);
	digitalWrite(12, HIGH);
	pinMode(13, INPUT);
	digitalWrite(13, HIGH);

}

//---------------------------------------------------------------------
// Called from loop() to update Digital readings
//---------------------------------------------------------------------
void Digital_Handler(void)
{
	// Read touch sensor
	g_u8Digital[0] = digitalRead(11);
	g_u8Digital[1] = digitalRead(12);
	g_u8Digital[2] = digitalRead(13);

	g_DigitalFlags[0].bUpdate = TRUE;
	g_DigitalFlags[1].bUpdate = TRUE;
	g_DigitalFlags[2].bUpdate = TRUE;
}

//---------------------------------------------------------------------
// Used by NXTI2C class to update fields in the NXT shared memory area
//---------------------------------------------------------------------
unsigned char Digital_getChannel(unsigned char u8Ch)
{
  return g_u8Digital[u8Ch];
}
