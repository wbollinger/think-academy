// Do not remove the include below
#include "ardurcj.h"

unsigned int g_u16Analog[NUM_ANALOG_CH];

//---------------------------------------------------------------------
// Configure the Analog Input code
//---------------------------------------------------------------------
void Init_Analog(void)
{
	for (unsigned int i = 0; i < NUM_ANALOG_CH; i++) {

	}
}

//---------------------------------------------------------------------
// Called from loop() to update Analog readings
//---------------------------------------------------------------------
void Analog_Handler(void)
{ 
	g_u16Analog[0] = analogRead(A0);
	g_AnalogFlags[0].bUpdate = TRUE;
}

//---------------------------------------------------------------------
// Used by NXTI2C class to update fields in the NXT shared memory area
//---------------------------------------------------------------------
unsigned int Analog_getChannel(unsigned char u8Ch)
{
  return g_u16Analog[u8Ch];
}
