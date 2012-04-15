// Do not remove the include below
#include "ardurcj.h"

// Array of Analog sensor readings - value from 0 to 1023
unsigned int g_u16Analog[NUM_ANALOG_CH];

//---------------------------------------------------------------------
// Configure the Analog Input code
//---------------------------------------------------------------------
void Init_Analog(void)
{
	for (unsigned int i = 0; i < NUM_ANALOG_CH; i++) {
		// init analog values to zero, clear update flags
		g_u16Analog[i] = 0;

		g_AnalogFlags[i].bUpdate = FALSE;
	}

	// To do?  Enable monitoring of Analog ports of interest
}

//---------------------------------------------------------------------
// Called from loop() to update Analog readings
//---------------------------------------------------------------------
void Analog_Handler(void)
{ 
	// Read both light sensors; make them match by adding an offset
	g_u16Analog[0] = analogRead(A0) + 35;	// Right or left?
	g_u16Analog[1] = analogRead(A1);		//
	g_AnalogFlags[0].bUpdate = TRUE;
	g_AnalogFlags[1].bUpdate = TRUE;

	// Read EOPD sensor
	g_u16Analog[2] = analogRead(A2);
	g_AnalogFlags[2].bUpdate = TRUE;

}

//---------------------------------------------------------------------
// Used by NXTI2C class to update fields in the NXT shared memory area
//---------------------------------------------------------------------
unsigned int Analog_getChannel(unsigned char u8Ch)
{
  return g_u16Analog[u8Ch];
}
