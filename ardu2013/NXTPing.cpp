// Do not remove the include below
#include "ardu2013.h"

#include <NewPing.h>

#define MAX_DISTANCE 200 // Maximum distance (in cm) to ping.
#define PING_INTERVAL 33 // Milliseconds between sensor pings (29ms is about the min to avoid cross-sensor echo).
unsigned long pingTimer[NUM_PING_CH]; // Holds the times when the next ping should happen for each sensor.
uint8_t currentSensor = 0; // Keeps track of which sensor is active.

NewPing sonar[NUM_PING_CH] = { // Sensor object array.
		NewPing(13, 13, MAX_DISTANCE), // Each sensor's trigger pin, echo pin, and max distance to ping.
		NewPing(10, 10, MAX_DISTANCE),
		NewPing(11, 11, MAX_DISTANCE),
		NewPing(12, 12, MAX_DISTANCE),
};

// Digital Inputs
// Array of Digital sensor readings - value from 0 to 1023

unsigned char g_u8Ping[NUM_PING_CH];

// HACK
//extern unsigned int g_u16Analog[NUM_ANALOG_CH];

void echoCheck();
//void oneSensorCycle();

//---------------------------------------------------------------------
// Configure the Digital Input code
//---------------------------------------------------------------------
void Init_Ping(void) {
	for (unsigned int i = 0; i < NUM_DIGITAL_CH; i++) {
		// init digital values to zero, clear update flags
		g_u8Ping[i] = 0;
		g_PingFlags[i].bUpdate = FALSE;
	}

	// initialize the switch pin as an input:
	// Turn on internal pull-up resister for switch connected to Digital input
	pinMode(11, INPUT);
	digitalWrite(11, HIGH);
	pinMode(12, INPUT);
	digitalWrite(12, HIGH);
	pinMode(13, INPUT);
	digitalWrite(13, HIGH);

	pingTimer[0] = millis() + 75; // First ping starts at 75ms, gives time for the Arduino to chill before starting.
	for (uint8_t i = 1; i < NUM_PING_CH; i++) // Set the starting time for each sensor.
		pingTimer[i] = pingTimer[i - 1] + PING_INTERVAL;

}

//---------------------------------------------------------------------
// Called from loop() to update Digital readings
//---------------------------------------------------------------------
void Ping_Handler(void) {

	for (uint8_t i = 0; i < NUM_PING_CH; i++) { // Loop through all the sensors.
		if (millis() >= pingTimer[i]) { // Is it this sensor's time to ping?
			pingTimer[i] += PING_INTERVAL * NUM_PING_CH; // Set next time this sensor will be pinged.
//			if (i == 0 && currentSensor == NUM_PING_CH - 1)
//				oneSensorCycle(); // Sensor ping cycle complete, do something with the results.
			sonar[currentSensor].timer_stop(); // Make sure previous timer is canceled before starting a new ping (insurance).
			currentSensor = i; // Sensor being accessed.
			g_u8Ping[currentSensor] = 0; // Make distance zero in case there's no ping echo for this sensor.
			sonar[currentSensor].ping_timer(echoCheck); // Do the ping (processing continues, interrupt will call echoCheck to look for echo).
		}
	}
	// The rest of your code would go here.
}

//---------------------------------------------------------------------
// Used by NXTI2C class to update fields in the NXT shared memory area
//---------------------------------------------------------------------
unsigned char Ping_getChannel(unsigned char u8Ch) {
	return g_u8Ping[u8Ch];
}

void echoCheck() { // If ping received, set the sensor distance to array.
	if (sonar[currentSensor].check_timer())
		g_u8Ping[currentSensor] = sonar[currentSensor].ping_result
				/ US_ROUNDTRIP_CM;
	g_PingFlags[currentSensor].bUpdate = TRUE;
}

/*
void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
	for (uint8_t i = 0; i < NUM_PING_CH; i++) {
		Serial.print(i);
		Serial.print("=");
		Serial.print(g_u8Ping[i]);
		Serial.print("cm ");
	}
	Serial.println();
}
*/
