#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>    // keeping constants in program memory
#include <Arduino.h>
// Do not remove the include below
#include "arduled.h"

// NOTE:  turning on serial debug prevents two rows of LEDS from working!
#define DEBUG 0
int debug = DEBUG;

int error = 0;

volatile uint8_t rxBuffer[buffer_size];

volatile uint8_t txBuffer[buffer_size];

//-----------------------------------------------------------------------------
/**
 * initialize hardware
 */
void init_ports(void)
{
	// set DDR for all row-pins
	DDRB = 0xff;
	DDRC |= (1 << PINC0) | (1 << PINC1);

	// unset PORT for all row-pins
	PORTB = 0x00;
	PORTC &= ~((1 << PINC0) | (1 << PINC1));

	DDRD = 0x00; // segment selector
	PORTD = 0x00; // segments, has to be 0x00
}

//-----------------------------------------------------------------------------
/**
 * select which row should be displayed
 * \param row number of the row
 */
void selectRow(uint8_t row)
{
	switch (row) {
	case 0:
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
		PORTB = (1 << row);
		PORTC &= ~((1 << PINC0) | (1 << PINC1));
		break;
	case 6:
		PORTB = 0x00;
		PORTC &= ~((1 << PINC1));
		PORTC |= (1 << PINC0);
		break;
	case 7:
		PORTB = 0x00;
		PORTC &= ~((1 << PINC0));
		PORTC |= (1 << PINC1);
		break;
	default:
		PORTB = 0x00;
		PORTC = 0x00;
		// display error
		digitalWrite(A3, HIGH);
		break;
	}
}

//-----------------------------------------------------------------------------
/**
 * set output of the currently selected row
 * \param byte bit-pattern to show
 */
void showByte(uint8_t byte)
{
	DDRD = byte;
}

//-----------------------------------------------------------------------------
/**
 * show a pattern on a certain row (or digit, if you use 7segment
 * displays). the output is cleared before selecting the new row, so there
 * won't be 'shadows' on the display.
 * \param row number of the row
 * \param byte bit-pattern to show
 */
void showRowByte(uint8_t row, uint8_t byte)
{
	showByte(0x00);
	selectRow(row);
	showByte(byte);
}

//-----------------------------------------------------------------------------
void printStatus()
{
	static int nLoops = 0;
	static int nChars = 0;

	if (nLoops++ > 10000) {
		Serial.print(".");
		nLoops = 0;

		if (nChars++ > 78) {
			Serial.println();
			nChars = 0;
		}
	}
}

//-----------------------------------------------------------------------------
/**
 * Development only: displays series of test patterns to check that wiring is OK
 */
void testPatterns()
{
	int i, row, val;

	for (i = 0; i < 8; i++) {
		selectRow(i);
		showByte(0x00);
	}

	for (row = 0; row < 8; row++) {
		showByte(0x00);
		selectRow(row);

		for (val = 0; val < 256; val++) {
			showByte(val);
			delay(50);
		}
	}
}

//-----------------------------------------------------------------------------
/**
 * Initializes everything; called once on startup.
 */
void printVals()
{
#if DEBUG != 0
	if (debug) {
		int i;

		for (i = 0; i <= 7; i++) {
			Serial.print(rxBuffer[i]);
			Serial.print(" ");
		}
		Serial.print("\n");
	}
#endif
}

//-----------------------------------------------------------------------------
/**
 * Initializes everything; called once on startup.
 */
void setup()
{
	int i;

	// use analog pin to drive "Error" LED
	pinMode(A3, OUTPUT);
	digitalWrite(A3, HIGH); // Blink Error LED once
	delay(500);
	digitalWrite(A3, LOW);

	// initialize output ports
	init_ports();

	// Individually light each LED
	//testPatterns();

	// init watchdog
	//   Original "bare metal" setting: wdt_enable(WDTO_15MS); // 15ms watchdog
	// Recommendation from:
	//   http://tushev.org/articles/electronics/48-arduino-and-watchdog-timer
	wdt_enable(WDTO_2S);
	// 2sec watchdog

	// init I2C communication
	//init_twi_slave(0x10);

	for (i = 0; i <= 7; i++) {
		// clear the receive buffer
		rxBuffer[i] = (uint8_t) 0x00;
	}

	for (i = 0; i <= 7; i++) {
		// init some test patterns
		rxBuffer[i] = 0xFF;
	}

	if (debug) {
		Serial.begin(57600);
		Serial.print("\nI2C LED Matrix\n");
		printVals();
	}
}

//-----------------------------------------------------------------------------
/**
 * The main loop which controls the actual output.  The rxBuffer[] is filled
 * from the I2C-library, so we just have go through the array and display its
 * values on the corresponding row.
 */
#define BALANCE_DELAY 4

void loop()
{
	uint8_t row;

	wdt_reset();
	// feed the watchdog

	// display all eight rows - last row doesn't need a delay
	for (row = 0; row < 7; row++) {
		showRowByte(row, rxBuffer[row]);
		delayMicroseconds(BALANCE_DELAY);
	}
	showRowByte(7, rxBuffer[7]);

#if DEBUG != 0
	// turning this on breaks two rows of LEDs!
	if ((loopCnt++ % 100000) == 0) {
		printVals();
	}
#endif
}

