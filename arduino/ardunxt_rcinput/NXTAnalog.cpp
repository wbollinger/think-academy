// Do not remove the include below
#include "ardurcj.h"

/*****************************************
 * 
 *****************************************/
void Analog_Handler(void)
{ 
  g_analog0 = analogRead(A0);
  // g_analog1 = analogRead(1);
}

//Switch to interrpt driven/timed...
// with low pass filter - controlable rate.
