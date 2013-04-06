/* Modified version of TWI/IIC library for use with ArduNXT by Christopher Barnes*/
/* Modified code identified below between *** CMB 20/11/2009 START ***
   1) To allow transmission of const data
   2) To allow Slave mode code to reply to read operations with
   as many bytes as the remote Master (e.g. Lego Mindstorms NXT) chooses 
   to read out, rather than having to force the number at the start of
   the read operation.
   This is achieved by making a fresh call to twi_onSlaveTransmit()
   each time the tx buffer becomes empty, thus allowing the higher level
   code to supply one or more further bytes 
*/
/*
  Based on original code:
  twi.c - TWI/I2C library for Wiring & Arduino
  Copyright (c) 2006 Nicholas Zambetti.  All right reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
// Do not remove the include below
#include "ardu2013.h"

//#include <avr/io.h>
//#include <avr/interrupt.h>
#include <util/twi.h>

#ifndef cbi
#define cbi(sfr, bit) (_SFR_BYTE(sfr) &= ~_BV(bit))
#endif

#ifndef sbi
#define sbi(sfr, bit) (_SFR_BYTE(sfr) |= _BV(bit))
#endif

#ifndef TWI_BUFFER_LENGTH
#define TWI_BUFFER_LENGTH 16
#endif

#define TWI_READY 0
#define TWI_MRX   1
#define TWI_MTX   2
#define TWI_SRX   3
#define TWI_STX   4

static volatile uint8_t twi_state;

static void (*twi_onSlaveTransmit)(void);
static void (*twi_onSlaveReceive)(byte*, uint8_t);

static uint8_t          twi_txBuffer[TWI_BUFFER_LENGTH];
static volatile uint8_t twi_txBufferIndex;
static volatile uint8_t twi_txBufferLength;

static uint8_t          twi_rxBuffer[TWI_BUFFER_LENGTH];
static volatile uint8_t twi_rxBufferIndex;

static volatile uint8_t twi_error;

/* 
 * Function twi4nxt_init
 * Desc     readys twi pins and sets twi bitrate
 * Input    none
 * Output   none
 */
void twi4nxt_init(void)
{
  // initialize state
  twi_state = TWI_READY;

  #if defined(__AVR_ATmega168__) || defined(__AVR_ATmega8__) || defined(__AVR_ATmega328P__)
    // activate internal pull-ups for twi
    // as per note from atmega8 manual pg167
    sbi(PORTC, 4);
    sbi(PORTC, 5);
  #else
    // activate internal pull-ups for twi
    // as per note from atmega128 manual pg204
    sbi(PORTD, 0);
    sbi(PORTD, 1);
  #endif

  // initialize twi prescaler and bit rate
  //cbi(TWSR, TWPS0);
  //cbi(TWSR, TWPS1);
  //TWBR = ((CPU_FREQ / TWI_FREQ) - 16) / 2;

  /* twi bit rate formula from atmega128 manual pg 204
  SCL Frequency = CPU Clock Frequency / (16 + (2 * TWBR))
  note: TWBR should be 10 or higher for master mode
  It is 72 for a 16mhz Wiring board with 100kHz TWI */

  // enable twi module, acks, and twi interrupt
  TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWEA);
}

// Return true if we are Ready (i.e. not in the middle of a transaction) 
bool twi4nxt_IsReady(void)
{
  return(twi_state==TWI_READY);
}

/* 
 * Function twi4nxt_setAddress
 * Desc     sets slave address and enables interrupt
 * Input    none
 * Output   none
 */
void twi4nxt_setAddress(uint8_t address)
{
  // set twi slave address (skip over TWGCE bit)
  TWAR = address << 1;
}


/* 
 * Function twi4nxt_attachSlaveRxEvent
 * Desc     sets function called before a slave read operation
 * Input    function: callback function to use
 * Output   none
 */
void twi4nxt_attachSlaveRxEvent( void (*function)(byte*, uint8_t) )
{
  twi_onSlaveReceive = function;
//  Serial.println("AttachSlaveRXEvent");
}


/* 
 * Function twi4nxt_attachSlaveTxEvent
 * Desc     sets function called before a slave write operation
 * Input    function: callback function to use
 * Output   none
 */
void twi4nxt_attachSlaveTxEvent( void (*function)(void) )
{
  twi_onSlaveTransmit = function;
//  Serial.println("AttachSalveTXEvent");
}


/* 
 * Function twi4nxt_transmit
 * Desc     fills slave tx buffer with data
 *          must be called in slave tx event callback
 * Input    data: pointer to byte array
 *          length: number of bytes in array
 * Output   1 length too long for buffer
 *          2 not slave transmitter
 *          0 ok
 */
uint8_t twi4nxt_transmit(byte* data, uint8_t length)
{
  uint8_t i;

  // ensure data will fit into buffer
  if(TWI_BUFFER_LENGTH < length){
    return 1;
  }
  
  // ensure we are currently a slave transmitter
  if(TWI_STX != twi_state){
    return 2;
  }
  
  // set length and copy data into tx buffer
  twi_txBufferLength = length;
  for(i = 0; i < length; ++i){
    twi_txBuffer[i] = data[i];
  }
  
  return 0;
}


/*** CMB 20/11/2009 START ***/
/* 
 * Function twi4nxt_transmitConst
 * Desc     fills slave tx buffer with data
 *          must be called in slave tx event callback
 *          This version of the fucntion handles data which is "const" i.e. from Flash/ROM rather than from RAM
 * Input    data: pointer to byte array
 *          length: number of bytes in array
 * Output   1 length too long for buffer
 *          2 not slave transmitter
 *          0 ok
 */
uint8_t twi4nxt_transmitConst(const byte* data, uint8_t length)
{
  uint8_t i;

  // ensure data will fit into buffer
  if(TWI_BUFFER_LENGTH < length){
    return 1;
  }
  
  // ensure we are currently a slave transmitter
  if(TWI_STX != twi_state){
    return 2;
  }
  
  // set length and copy data into tx buffer
  twi_txBufferLength = length;
  for(i = 0; i < length; ++i){
    twi_txBuffer[i] = data[i];
  }
  
  return 0;
}
/*** CMB 20/11/2009 END ***/



/* 
 * Function twi_releaseBus
 * Desc     releases bus control
 * Input    none
 * Output   none
 */
void twi4nxt_releaseBus(void)
{
  // release bus
  TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWEA) | _BV(TWINT);

  // update twi state
  twi_state = TWI_READY;
}


/* 
 * Function twi_reply
 * Desc     sends byte or readys receive line
 * Input    ack: byte indicating to ack or to nack
 * Output   none
 */
/*
static void twi_reply(uint8_t ack)
{
  // transmit master read ready signal, with or without ack
  if(ack){
    TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWINT) | _BV(TWEA);
  }else{
    TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWINT);
  }
}
*/

#define TWI_ACK()    TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWINT) | _BV(TWEA)
#define TWI_NAK()    TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWINT)

/* 
 * Function twi_stop
 * Desc     relinquishes bus master status
 * Input    none
 * Output   none
 */
static void twi_stop(void)
{
  // send stop condition
  TWCR = _BV(TWEN) | _BV(TWIE) | _BV(TWEA) | _BV(TWINT) | _BV(TWSTO);

  // wait for stop condition to be exectued on bus
  // TWINT is not set after a stop condition!  // THIS IS NASTY IN AN INTERRUPT ROUTINE!!!!! - find another way to do this...
  // e.g. check before usign twi_state in foreground (next time)
  //while(TWCR & _BV(TWSTO)){
  //  continue;
  //}

  // update twi state
  twi_state = TWI_READY;
}


ISR(TWI_vect)
{
  switch(TW_STATUS){
    // All Master support commented out

    // Slave Receiver
    case TW_SR_SLA_ACK:   // addressed, returned ack
    case TW_SR_GCALL_ACK: // addressed generally, returned ack
    case TW_SR_ARB_LOST_SLA_ACK:   // lost arbitration, returned ack
    case TW_SR_ARB_LOST_GCALL_ACK: // lost arbitration, returned ack
      // enter slave receiver mode
      twi_state = TWI_SRX;
      // indicate that rx buffer can be overwritten and ack
      twi_rxBufferIndex = 0;
      TWI_ACK();
//    twi_reply(1);
      break;
    case TW_SR_DATA_ACK:       // data received, returned ack
    case TW_SR_GCALL_DATA_ACK: // data received generally, returned ack
      // if there is still room in the rx buffer
      if(twi_rxBufferIndex < TWI_BUFFER_LENGTH){
        // put byte in buffer and ack
        twi_rxBuffer[twi_rxBufferIndex++] = TWDR;
        TWI_ACK();
//      twi_reply(1);
      }else{
        // otherwise nack
        TWI_NAK();
//      twi_reply(0);
      }
      break;
    case TW_SR_STOP: // stop or repeated start condition received
      // put a null char after data if there's room
      if(twi_rxBufferIndex < TWI_BUFFER_LENGTH){
        twi_rxBuffer[twi_rxBufferIndex] = '\0';
      }
      // callback to user defined callback
      twi_onSlaveReceive(twi_rxBuffer, twi_rxBufferIndex);
      // ack future responses
      TWI_ACK();
//    twi_reply(1);
      // leave slave receiver state
      twi_state = TWI_READY;
      break;
    case TW_SR_DATA_NACK:       // data received, returned nack
    case TW_SR_GCALL_DATA_NACK: // data received generally, returned nack
      // nack back at master
      TWI_NAK();
//    twi_reply(0);
      break;
    
    // Slave Transmitter
    case TW_ST_SLA_ACK:          // addressed, returned ack
    case TW_ST_ARB_LOST_SLA_ACK: // arbitration lost, returned ack
      // enter slave transmitter mode
      twi_state = TWI_STX;
      // ready the tx buffer index for iteration
      twi_txBufferIndex = 0;
      // set tx buffer length to be zero, to verify if user changes it
      twi_txBufferLength = 0;
      // request for txBuffer to be filled and length to be set
      // note: user must call twi_transmit(bytes, length) to do this
      twi_onSlaveTransmit();
      // if they didn't change buffer & length, initialize it
      if(0 == twi_txBufferLength){
        twi_txBufferLength = 1;
        twi_txBuffer[0] = 0x00;
      }
      // transmit first byte from buffer, fall through
      /* no break */
    case TW_ST_DATA_ACK: // byte sent, ack returned
      // copy data to output register
      TWDR = twi_txBuffer[twi_txBufferIndex++];
      // *** CMB 20/11/2009 START ***
      // Allow further bytes to be added
      if(twi_txBufferIndex >= twi_txBufferLength){
        // Buffer now empty
        // ready the tx buffer index for iteration
        twi_txBufferIndex = 0;
        // set tx buffer length to be zero, to verify if user changes it
        twi_txBufferLength = 0;
        // request for txBuffer to be filled and length to be set
        // note: user must call twi_transmit(bytes, length) to do this
        twi_onSlaveTransmit();
      }
      // *** CMB 20/11/2009 END ***
      // if there is more to send, ack, otherwise nack
      if(twi_txBufferIndex < twi_txBufferLength){
        TWI_ACK();
//      twi_reply(1);
      }else{
        TWI_NAK();
//      twi_reply(0);
      }
      break;
    case TW_ST_DATA_NACK: // received nack, we are done 
    case TW_ST_LAST_DATA: // received ack, but we are done already!
      // ack future responses
      TWI_ACK();
//    twi_reply(1);
      // leave slave receiver state
      twi_state = TWI_READY;
      break;

    // All
    case TW_NO_INFO:   // no state information
      break;
    default:  
    case TW_BUS_ERROR: // bus error, illegal stop/start
      twi_error = TW_BUS_ERROR;
      twi_stop();
      break;
  }
}
