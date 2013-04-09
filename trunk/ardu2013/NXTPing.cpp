// Do not remove the include below
#include "ardu2013.h"

#include <NewPing.h>

#define MAX_DISTANCE 255 // Maximum distance (in cm) to ping.
#define PING_INTERVAL 33 // Milliseconds between sensor pings (29ms is about the min to avoid cross-sensor echo).
unsigned long pingTimer[NUM_PING_CH]; // Holds the times when the next ping should happen for each sensor.
uint8_t currentSensor = 0; // Keeps track of which sensor is active.

NewPing sonar[NUM_PING_CH] = { // Sensor object array.
		NewPing(2, 3, MAX_DISTANCE), // Each sensor's trigger pin, echo pin, and max distance to ping.
		NewPing(4, 5, MAX_DISTANCE), NewPing(7, 8, MAX_DISTANCE), NewPing(12,
				11, MAX_DISTANCE), };

unsigned char g_u8Ping[NUM_PING_CH];

unsigned char pingVals[NUM_PING_CH][5];

int index; // This keeps track of which value is being updated in the PingVals array

void echoCheck();
void oneSensorCycle();
int averageValues(unsigned char vals[]);

//---------------------------------------------------------------------
// Configure the Ping Input code
//---------------------------------------------------------------------
void Init_Ping(void) {
	for (unsigned int i = 0; i < NUM_PING_CH; i++) {
		// init ping values to zero, clear update flags
		g_u8Ping[i] = 0;
		g_PingFlags[i].bUpdate = FALSE;
		index = 0;
		for(i = 0; i < NUM_PING_CH; i++) {
			for(int j = 0; j < 5; j++) {
				pingVals[i][j] = 255;
			}
		}

	}

	pingTimer[0] = millis() + 75; // First ping starts at 75ms, gives time for the Arduino to chill before starting.
	for (uint8_t i = 1; i < NUM_PING_CH; i++) // Set the starting time for each sensor.
		pingTimer[i] = pingTimer[i - 1] + PING_INTERVAL;

}

//---------------------------------------------------------------------
// Called from loop() to update Ping readings
//---------------------------------------------------------------------
void Ping_Handler(void) {

	for (uint8_t i = 0; i < NUM_PING_CH; i++) { // Loop through all the sensors.
		if (millis() >= pingTimer[i]) { // Is it this sensor's time to ping?
			pingTimer[i] += PING_INTERVAL * NUM_PING_CH; // Set next time this sensor will be pinged.
			if (i == 0 && currentSensor == NUM_PING_CH - 1)
				oneSensorCycle(); // Sensor ping cycle complete, do something with the results.
			sonar[currentSensor].timer_stop(); // Make sure previous timer is canceled before starting a new ping (insurance).
			currentSensor = i; // Sensor being accessed.
			g_u8Ping[currentSensor] = 255; // Make distance 255 in case there's no ping echo for this sensor.
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
	if (sonar[currentSensor].check_timer()) {
		//pingVals[currentSensor][index] = sonar[currentSensor].ping_result
		// / US_ROUNDTRIP_CM;
		// g_u8Ping[currentSensor] = averageValues(pingVals[currentSensor]);
		g_u8Ping[currentSensor] = sonar[currentSensor].ping_result
				/ US_ROUNDTRIP_CM;
	}
	g_PingFlags[currentSensor].bUpdate = TRUE;
}

void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.

	if (index < 5) { // update index so that readings are stored correctly in pingVals
		index++;
	} else {
		index = 0;
	}

	for (uint8_t i = 0; i < NUM_PING_CH; i++) {

		Serial.print(i);
		Serial.print("=");
		Serial.print(g_u8Ping[i]);
		Serial.print("cm ");

	}
	Serial.println();

}

int averageValues(unsigned char vals[]) {
	int temp = 0;
	for (int i = 0; i < 5; i++) {
		temp += vals[i];
	}
	temp /= 5;
	return temp;
}
