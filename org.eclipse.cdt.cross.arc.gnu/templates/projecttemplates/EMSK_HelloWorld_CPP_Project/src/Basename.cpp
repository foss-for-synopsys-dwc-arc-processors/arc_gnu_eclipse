/*
 Name        : $(baseName).c
 Author      : $(author)
 Version     : 1.0
 Copyright   : $(copyright)
 Description : Print a greeting on UART output and exit.
 */

#include <stdio.h>
#include "starterkit.h"

// Typedefs, defines, function prototypes.
typedef volatile unsigned int DWCREG;
typedef DWCREG * DWCREG_PTR;

// Set maximum length of debug message
#define	MAX_DEBUG_MSG	(256)

void uart_print(DWCREG_PTR uartRegs, const char * pBuf);
void uart_initDevice(DWCREG_PTR uartRegs, uart_baudrate_t baud,
		uart_data_bits_t data_bits, uart_stop_t stop, uart_parity_t parity);


// Functions
int
main(int argc, char *argv[])
{

	DWCREG_PTR uart = (DWCREG_PTR) (DWC_UART_CONSOLE | PERIPHERAL_BASE);

	// Initialize UART console
	uart_initDevice(uart, UART_CFG_BAUDRATE_115200, UART_CFG_DATA_8BITS,
			UART_CFG_1STOP, UART_CFG_PARITY_NONE);

	uart_print(uart, "$(messagearc)\n\r");

	return 0;
}


void
uart_initDevice(DWCREG_PTR uartRegs, uart_baudrate_t baud,
	uart_data_bits_t data_bits, uart_stop_t stop, uart_parity_t parity)
{

  // Build UART configuration for U_LCR register
  unsigned int UCFG = data_bits | stop | parity;

  // Disable UART controller
  uartRegs[U_MCR] = 0;
  // Enable FIFO in UART controller
  uartRegs[U_FCR] = 0x01;

  // Setup baudrate divisor
  uartRegs[U_LCR] = 0x80 | UCFG;
  uartRegs[U_DLL] = UART_BAUD_DLL(baud); //DLL 0x00  div = CPU_clock / 16 * baudrate.
  uartRegs[U_DLH] = UART_BAUD_DLH(baud); //DLH 0x4

  uartRegs[U_LCR] = (unsigned int) data_bits | stop | parity;

  // Disable UART interrupts
  uartRegs[U_IER] = 0x0;
}


// Simple debug print
void
uart_print(DWCREG_PTR uartRegs, const char * pBuf)
{
	unsigned int i = MAX_DEBUG_MSG;

    unsigned char byte = *pBuf++;
    while(byte && i--) {

        // Wait if FIFO is full
        while(!(uartRegs[U_USR] & U_USR_TFNF));

        // Transmit data byte
        uartRegs[U_THR] = byte;
        byte = *pBuf++;
    }
}

