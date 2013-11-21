/*
 ============================================================================
 Name        : hello.c
 Author      :
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C
 ============================================================================
 */
#include <stdio.h>
#ifndef __STARTERKIT_H__
#define  __STARTERKIT_H__



// -------------------------------------------------------------------------------
// Board parameters
// -------------------------------------------------------------------------------
#define CPU_CLOCK			30000000
#define PERIPHERAL_CLOCK	50000000

#define	REG_FILE_0	0x00000000U
#define	REG_FILE_1	0x00001000U	// not implemented on this board
#define	DWC_GPIO_0	0x00002000U
#define	DWC_GPIO_1	0x00003000U	// not implemented on this board
#define	DWC_I2C_0	0x00004000U
#define	DWC_I2C_1	0x00005000U	// not implemented on this board
#define DWC_SPI_0	0x00006000U	// SPI Master
#define DWC_SPI_1	0x00007000U	// SPI Slave
#define	DWC_UART_0	0x00008000U
#define	DWC_UART_1	0x00009000U

#define	DWC_UART_CONSOLE	DWC_UART_1


// -------------------------------------------------------------------------------
//	UART
// -------------------------------------------------------------------------------

// typedef volatile unsigned int UART_REG;
// typedef volatile unsigned int * UART_REG_PTR;
// -------------------------------------------------------------------------------
// --  UART Registers: (Base Address = 0xF0003000)
// -------------------------------------------------------------------------------
// Register          Offset Size  Memory Access    Description
// -------------------------------------------------------------------------------


#define U_RBR     0   	// 0x00 32 bits R   Reset: 0x0   Receive Buffer Register, reading this register when the DLAB bit is zero; Transmit Holding Register, writing to this register when the DLAB is zero; Divisor Latch (Low), when DLAB bit is one
#define U_DLL     0  	// 0x00 32 bits R/W Reset: 0x0   AlternateRegister: RBR Receive Buffer Register, reading this register when the DLAB bit is zero; Transmit Holding Register, writing to this register when the DLAB is zero; Divisor Latch (Low), when DLAB bit is one
#define U_THR     0   	// 0x00 32 bits R/W Reset: 0x0   AlternateRegister: RBR Receive Buffer Register, reading this register when the DLAB bit is zero; Transmit Holding Register, writing to this register when the DLAB is zero; Divisor Latch (Low), when DLAB bit is one
#define U_DLH     1     // 0x04 32 bits R/W Reset: 0x0   AlternateRegister: IER    Divisor Latch High (DLH) Register. Interrupt Enable Register, when the DLAB bit is zero; Divisor Latch (High), when the DLAB bit is one. This register makes up the upper 8-bits of a 16-bit, read/write, Divisor Latch register that contains the baud rate divisor for the UART. If UART_16550_COMPATIBLE == NO, then this register may only be accessed when the DLAB bit (LCR[7]) is set and the UART is not busy (USR[0] is zero), otherwise this register may only be accessed when the DLAB bit (LCR[7]) is set. The output baud rate is equal to the serial clock (pclk if one clock design, sclk if two clock design (CLOCK_MODE == Enabled)) frequency divided by sixteen times the value of the baud rate divisor, as follows: baud rate = (serial clock freq) / (16 * divisor) Note that with the Divisor Latch Registers (DLL and DLH) set to zero, the baud clock is disabled and no serial communications will occur. Also, once the DLH is set, at least 8 clock cycles of the slowest DW_apb_uart clock should be allowed to pass before transmitting or receiving data.
#define U_IER     1     // 0x04 32 bits R/W Reset: 0x0   Interrupt Enable Register: Interrupt Enable Register, when the DLAB bit is zero; Divisor Latch (High), when the DLAB bit is one. Each of the bits used has a different function and will be detailed in the bit field discriptions.
#define U_IIR     2     // 0x08 32 bits R   Reset: 0x1   Interrupt Identification Register
#define U_FCR     2     // 0x08 32 bits R/W Reset: 0x0   AlternateRegister: IIR    FIFO Control Register. This register is only valid when the DW_apb_uart is configured to have FIFO's implemented (FIFO_MODE != NONE). If FIFO's are not implemented, this register does not exist and writing to this register address will have no effect.
#define U_LCR     3     // 0x0c 32 bits R/W Reset: 0x0   Line Control Register
#define U_MCR     4    	// 0x10 32 bits R/W Reset: 0x0   Modem Control Register
#define U_LSR     6    	// 0x18 32 bits R   Reset: 0x60  Line Status Register
#define U_MSR     6 	// 0x18    // 	32 bits R   Reset: 0x0   Modem Status Register It should be noted that whenever bits 0, 1, 2 or 3 is set to logic one, to indicate a change on the modem control inputs, a modem status interrupt will be generated if enabled via the IER regardless of when the change occurred. Since the delta bits (bits 0, 1, 3) can get set after a reset if their respective modem signals are active (see individual bits for details), a read of the MSR after reset can be performed to prevent unwanted interrupts.
#define U_SCR     7		// 0x1c    // 	32 bits R/W Reset: 0x0   Scratchpad Register
#define U_STHR0   12	// 0x30    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR0Shadow Transmit Holding Register
#define U_SRBR0   12	// 0x30    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register
#define U_STHR1   13	// 0x34    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR1Shadow Transmit Holding Register 1
#define U_SRBR1   13	// 0x34    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 1
#define U_STHR2   14	// 0x38    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR2Shadow Transmit Holding Register 2
#define U_SRBR2   14	// 0x38    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 2
#define U_SRBR3   15 	// 0x3c    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 3
#define U_STHR3   15 	// 0x3c    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR3Shadow Transmit Holding Register 3
#define U_STHR4   16 	// 0x40    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR4Shadow Transmit Holding Register 4
#define U_SRBR4   16 	// 0x40    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 4
#define U_STHR5   17 	// 0x44    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR5Shadow Transmit Holding Register 5
#define U_SRBR5   17 	// 0x44    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 5
#define U_SRBR6   18 	// 0x48    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 6
#define U_STHR6   18 	// 0x48    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR6Shadow Transmit Holding Register 6
#define U_SRBR7   19 	// 0x4c    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 7
#define U_STHR7   19	// 0x4c    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR7Shadow Transmit Holding Register 7
#define U_SRBR8   20	// 0x50    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 8
#define U_STHR8   20	// 0x50    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR8Shadow Transmit Holding Register 8
#define U_STHR9   21	// 0x54    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR9Shadow Transmit Holding Register 9
#define U_SRBR9   21	// 0x54    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 9
#define U_STHR10  22	// 0x58    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR10Shadow Transmit Holding Register 10
#define U_SRBR10  22	// 0x58    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 10
#define U_SRBR11  23	// 0x5c    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 11
#define U_STHR11  23	// 0x5c    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR11Shadow Transmit Holding Register 11
#define U_STHR12  24	// 0x60    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR12Shadow Transmit Holding Register 12
#define U_SRBR12  24	// 0x60    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 12
#define U_STHR13  25	// 0x64    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR13Shadow Transmit Holding Register 13
#define U_SRBR13  25	// 0x64    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 13
#define U_SRBR14  26	// 0x68    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 14
#define U_STHR14  26	// 0x68    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR14Shadow Transmit Holding Register 14
#define U_SRBR15  27	// 0x6c    // 	32 bits R   Reset: 0x0   Shadow Receive Buffer Register 15
#define U_STHR15  27	// 0x6c    // 	32 bits R/W Reset: 0x0   AlternateRegister: SRBR15Shadow Transmit Holding Register 15
#define U_FAR     28	// 0x70    // 	32 bits R   Reset: 0x0   FIFO Access Register
#define U_USR     31	// 0x7c    // 	32 bits R   Reset: 0x6   UART Status register.
#define U_TFL     32	// 0x80    // 	32 bits R   Reset: 0x0
#define U_RFL     33	// 0x84    // 	32 bits R   Reset: 0x0   Receive FIFO Level.
#define U_SRR     34	// 0x88    // 	32 bits R/W Reset: 0x0   Software Reset Register.
#define U_SRTS    35	// 0x8c    // 	32 bits R/W Reset: 0x0   Shadow Request to Send.
#define U_SBCR    36	// 0x90    // 	32 bits R/W Reset: 0x0   Shadow Break Control Register.
#define U_SDMAM   37	// 0x94    // 	32 bits R/W Reset: 0x0   Shadow DMA Mode.
#define U_SFE     38	// 0x98    // 	32 bits R/W Reset: 0x0   Shadow FIFO Enable
#define U_SRT     39	// 0x9c    // 	32 bits R/W Reset: 0x0   Shadow RCVR Trigger
#define U_STET    40	// 0xa0    // 	32 bits R/W Reset: 0x0   Shadow TX Empty Trigger
#define U_HTX     41	// 0xa4    // 	32 bits R/W Reset: 0x0   Halt TX
#define U_DMASA   42	// 0xa8    // 	32 bits R   Reset: 0x0   DMA Software Acknowledge
#define U_CPR     61	// 0xf4    // 	32 bits R   Reset: 0x21d32  Component Parameter Register
#define U_UCV     62	// 0xf8    // 	32 bits R   Reset: 0x3331332a   Component Version
#define U_CTR     63	// 0xfc    // 	32 bits R   Reset: 0x44570110   Component Type Register


// U_USR flags
#define U_USR_RFF   (1<<4)
#define U_USR_RFNE  (1<<3)
#define U_USR_TFE   (1<<2)
#define U_USR_TFNF  (1<<1)
#define U_USR_BUSY  (1<<0)


#define UART_BAUD(baud)	(PERIPHERAL_CLOCK / (baud *16))
#define UART_BAUD_DLL(baud) (baud & 0xff)
#define UART_BAUD_DLH(baud) ((baud >> 8)& 0xff)


typedef enum uart_baudrate {
	UART_CFG_BAUDRATE_2400 = UART_BAUD(2400),
	UART_CFG_BAUDRATE_4800 = UART_BAUD(4800),
	UART_CFG_BAUDRATE_9600 = UART_BAUD(9600),
	UART_CFG_BAUDRATE_115200 = UART_BAUD(115200),
} uart_baudrate_t;

typedef enum uart_data_bits {
	UART_CFG_DATA_5BITS = 0x0,
	UART_CFG_DATA_6BITS = 0x1,
	UART_CFG_DATA_7BITS = 0x2,
	UART_CFG_DATA_8BITS = 0x3,
} uart_data_bits_t;

typedef enum uart_stop {
	UART_CFG_1STOP = 0x0, UART_CFG_2STOP = 0x1 << 2,
} uart_stop_t;

typedef enum uart_parity {
	UART_CFG_PARITY_NONE = 0x0 << 3,
	UART_CFG_PARITY_EVEN = 0x3 << 3,
	UART_CFG_PARITY_ODD = 0x1 << 3,
} uart_parity_t;




// -------------------------------------------------------------------------------
// ASM
// -------------------------------------------------------------------------------

#define  nops( )				\
	({ 							\
		__asm__ __volatile__ (	\
		" nop  "				\
		: :);					\
	})

#define  read_auxreg( reg )		\
	({ 							\
		unsigned int __ret;		\
		__asm__ __volatile__ (	\
		" lr	%0, [%1] "		\
		: "=r"(__ret)			\
		: "i" (reg));			\
		__ret;					\
	})

#define  write_auxreg( value, reg )	\
	({ 								\
		__asm__ __volatile__ (		\
		" sr	%0, [%1] "			\
		: 							\
		: "ir"(value), "i" (reg));	\
	})

#define PERIPHERAL_BASE_ADDRESS		0x20a
#define PERIPHERAL_BASE 	read_auxreg(PERIPHERAL_BASE_ADDRESS)


#endif // __STARTERKIT_H__

/*

 Print a greeting on standard output and exit.

 On embedded platforms this might need to enable semi-hosting or similar.

 For example, for toolchains derived from GNU Tools for Embedded,
 the following should be added to the linker:

 --specs=rdimon.specs -Wl,--start-group -lgcc -lc -lc -lm -lrdimon -Wl,--end-group

 */


//***************************************************************/
//	TYPEDEFS, DEFINES, FUNCTION PROTOTYPES
//***************************************************************/
typedef volatile unsigned int DWCREG;
typedef DWCREG * DWCREG_PTR;

// set maximum lenght of debug message
#define	MAX_DEBUG_MSG	(256)

void uart_print(DWCREG_PTR uartRegs, const char * pBuf);
void uart_initDevice(DWCREG_PTR uartRegs, uart_baudrate_t baud,
		uart_data_bits_t data_bits, uart_stop_t stop, uart_parity_t parity);




//***************************************************************/
//	MAIN
//***************************************************************/
int main(int argc, char *argv[]) {

	DWCREG_PTR uart = (DWCREG_PTR) (DWC_UART_CONSOLE | PERIPHERAL_BASE);

	// initilize uart console
	uart_initDevice(uart, UART_CFG_BAUDRATE_115200, UART_CFG_DATA_8BITS,
			UART_CFG_1STOP, UART_CFG_PARITY_NONE);

	uart_print(uart, "Hello ARC\n\r");
	uart_print(uart, "Hello EM\n\r");
	uart_print(uart, "Hello OpenOCD\n\r");

	return 0;
}


void uart_initDevice(DWCREG_PTR uartRegs, uart_baudrate_t baud, uart_data_bits_t data_bits, uart_stop_t stop, uart_parity_t parity) {

  // build uart configuration for U_LCR register
  unsigned int UCFG = data_bits | stop | parity;

  // disable UART controller
  uartRegs[U_MCR] = 0;
  // enable FIFO in UART controller
  uartRegs[U_FCR] = 0x01;

  // setup baudrate divisor
  uartRegs[U_LCR] = 0x80 | UCFG;
  uartRegs[U_DLL] = UART_BAUD_DLL(baud); //DLL 0x00  div = CPU_clock / 16 * baudrate.
  uartRegs[U_DLH] = UART_BAUD_DLH(baud); //DLH 0x4

  uartRegs[U_LCR] = (unsigned int) data_bits | stop | parity;

  // disable uart interrupts
  uartRegs[U_IER] = 0x0;
}


// simple debug print
void uart_print(DWCREG_PTR uartRegs, const char * pBuf) {
	unsigned int i = MAX_DEBUG_MSG;

    unsigned char byte = *pBuf++;
    while(byte && i--) {

        // wait if FIFO is full
        while(!(uartRegs[U_USR] & U_USR_TFNF));

        // transmit data byte
        uartRegs[U_THR] = byte;
        byte = *pBuf++;
    }
}

