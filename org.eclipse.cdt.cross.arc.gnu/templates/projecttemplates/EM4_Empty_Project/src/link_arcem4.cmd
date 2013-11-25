
SECTIONS
{

  . = 0x100;
  .init : { KEEP (*(.init)) } =0
  .text : { *(.text) }
   
  
  . = 0x80000000;
  .data : { *(.data) }
  .sdata          :
  {
    __SDATA_BEGIN__ = .;
    *(.sdata .sdata.* .gnu.linkonce.s.*)
  }
  _edata = .;
  PROVIDE (edata = .);
  __bss_start = .;
  .sbss           :
  {
    PROVIDE (__sbss_start = .);
    PROVIDE (___sbss_start = .);
    *(.dynsbss)
    *(.sbss .sbss.* .gnu.linkonce.sb.*)
    *(.scommon)
    PROVIDE (__sbss_end = .);
    PROVIDE (___sbss_end = .);
  } 
  .bss            :
  {
   *(.dynbss)
   *(.bss .bss.* .gnu.linkonce.b.*)
   *(COMMON)
   /* Align here to ensure that the .bss section occupies space up to
      _end.  Align after .bss to ensure correct alignment even if the
      .bss section disappears because there are no input sections.  */
   . = ALIGN(32 / 8);
  } 
  . = ALIGN(32 / 8);
  _end = .;
  PROVIDE (end = .);
  /* We want to be able to set a default stack / heap size in a dejagnu
     board description file, but override it for selected test cases.
     The options appear in the wrong order to do this with a single symbol -
     ldflags comes after flags injected with per-file stanzas, and thus
     the setting from ldflags prevails.  */
  .heap   :
  {
         __start_heap = . ;
         . = . + (DEFINED(__HEAP_SIZE) ? __HEAP_SIZE : (DEFINED(__DEFAULT_HEAP_SIZE) ? __DEFAULT_HEAP_SIZE : 16k))  ;
         __end_heap = . ;
  } 
  . = ALIGN(0x8);
  .stack   :
  {
         __stack = . ;
         . = . + (DEFINED(__STACK_SIZE) ? __STACK_SIZE : (DEFINED(__DEFAULT_STACK_SIZE) ? __DEFAULT_STACK_SIZE : 2k))  ;
         __stack_top = . ;
  } 
}



