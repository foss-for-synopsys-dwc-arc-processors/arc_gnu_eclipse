MEMORY
{
    ICCM : ORIGIN = 0x10000000, LENGTH = 64K
    SRAM : ORIGIN = 0x20000000, LENGTH = 256K
    DRAM : ORIGIN = 0x80000000, LENGTH = 1024M
    DCCM : ORIGIN = 0xC0000000, LENGTH = 64K
}

REGION_ALIAS("REGION_IVT",   ICCM)
REGION_ALIAS("REGION_INIT",  ICCM)
REGION_ALIAS("REGION_TEXT",  ICCM)
REGION_ALIAS("REGION_DATA",  DCCM)
REGION_ALIAS("REGION_SDATA", DCCM)
REGION_ALIAS("REGION_SBSS",  DCCM)
REGION_ALIAS("REGION_BSS",   DCCM)
REGION_ALIAS("REGION_HEAP",  DCCM)
REGION_ALIAS("REGION_STACK", DCCM)

ENTRY(__start)

SECTIONS
{
    .ivt :
    {
      KEEP (*(.ivt));
    } > REGION_IVT
    .init :
    {
	. = 0x100;
	KEEP (*(.init));
    } > REGION_INIT
    .text :
    {
	*(.text)
    } > REGION_TEXT
    .data :
    {
	*(.data)
    } > REGION_DATA
    .sdata :
    {
	__SDATA_BEGIN__ = .;
	*(.sdata .sdata.* .gnu.linkonce.s.*)
	_edata = .;
	PROVIDE (edata = .);
    } > REGION_SDATA
    .sbss :
    {
	__bss_start = .;
	PROVIDE (__sbss_start = .);
	PROVIDE (___sbss_start = .);
	*(.dynsbss)
	*(.sbss .sbss.* .gnu.linkonce.sb.*)
	*(.scommon)
	PROVIDE (__sbss_end = .);
	PROVIDE (___sbss_end = .);
    } > REGION_SBSS
    .bss :
    {
	*(.dynbss)
	*(.bss .bss.* .gnu.linkonce.b.*)
	*(COMMON)
	/* Align here to ensure that the .bss section occupies space up to
	_end.  Align after .bss to ensure correct alignment even if the
	.bss section disappears because there are no input sections.  */
	. = ALIGN(32 / 8);
	_end = .;
	PROVIDE (end = .);
    } > REGION_BSS
    .heap :
    {
	. = ALIGN(32 / 8);
	__start_heap = . ;
	. = . + (DEFINED(__HEAP_SIZE) ? __HEAP_SIZE : (DEFINED(__DEFAULT_HEAP_SIZE) ? __DEFAULT_HEAP_SIZE : 16k))  ;
	__end_heap = . ;
	. = ALIGN(0x8);
    } > REGION_HEAP
    .stack :
    {
	. = ALIGN(32 / 8);
	__stack = . ;
	. = . + (DEFINED(__STACK_SIZE) ? __STACK_SIZE : (DEFINED(__DEFAULT_STACK_SIZE) ? __DEFAULT_STACK_SIZE : 2k))  ;
	__stack_top = . ;
    } > REGION_STACK
}
