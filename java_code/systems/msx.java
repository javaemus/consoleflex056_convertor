/*
** msx.c : driver for MSX1
**
** Todo:
** - Add support for other MSX models (br,fr,de,ru etc.)
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package systems;

public class msx
{
	
	static MEMORY_READ_START (readmem)
	    { 0x0000, 0x1fff, MRA_BANK1 },
	    { 0x2000, 0x3fff, MRA_BANK2 },
	    { 0x4000, 0x5fff, MRA_BANK3 },
	    { 0x6000, 0x7fff, MRA_BANK4 },
	    { 0x8000, 0x9fff, MRA_BANK5 },
	    { 0xa000, 0xbfff, MRA_BANK6 },
	    { 0xc000, 0xdfff, MRA_BANK7 },
	    { 0xe000, 0xffff, MRA_BANK8 },
	MEMORY_END
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	    new Memory_WriteAddress( 0x0000, 0x3fff, msx_writemem0 ),
	    new Memory_WriteAddress( 0x4000, 0x7fff, msx_writemem1 ),
	    new Memory_WriteAddress( 0x8000, 0xbfff, msx_writemem2 ),
	    new Memory_WriteAddress( 0xc000, 0xffff, msx_writemem3 ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static PORT_READ_START (readport)
	    { 0x90, 0x91, msx_printer_r },
	    { 0xa0, 0xa7, msx_psg_r },
	    { 0xa8, 0xab, ppi8255_0_r },
		{ 0x98, 0x98, TMS9928A_vram_r },
		{ 0x99, 0x99, TMS9928A_register_r },
	PORT_END
	
	static PORT_WRITE_START (writeport)
	    { 0x7c, 0x7d, msx_fmpac_w },
	    { 0x90, 0x91, msx_printer_w },
	    { 0xa0, 0xa7, msx_psg_w },
	    { 0xa8, 0xab, ppi8255_0_w },
		{ 0x98, 0x98, TMS9928A_vram_w },
		{ 0x99, 0x99, TMS9928A_register_w },
	PORT_END
	
	static PORT_READ_START (readport2)
	    { 0x90, 0x91, msx_printer_r },
	    { 0xa0, 0xa7, msx_psg_r },
	    { 0xa8, 0xab, ppi8255_0_r },
	    { 0x98, 0x98, v9938_vram_r },
	    { 0x99, 0x99, v9938_status_r },
		{ 0xb5, 0xb5, msx_rtc_reg_r },
		{ 0xfc, 0xff, msx_mapper_r },
	PORT_END
	
	static PORT_WRITE_START (writeport2)
	    { 0x7c, 0x7d, msx_fmpac_w },
	    { 0x90, 0x91, msx_printer_w },
	    { 0xa0, 0xa7, msx_psg_w },
	    { 0xa8, 0xab, ppi8255_0_w },
	    { 0x98, 0x98, v9938_vram_w },
	    { 0x99, 0x99, v9938_command_w },
	    { 0x9a, 0x9a, v9938_palette_w },
	    { 0x9b, 0x9b, v9938_register_w },
		{ 0xb4, 0xb4, msx_rtc_latch_w },
		{ 0xb5, 0xb5, msx_rtc_reg_w },
		{ 0xfc, 0xff, msx_mapper_w },
	PORT_END
	
	/* start define for the special ports (DIPS, joystick, mouse) */
	#define MSX_DIPS \
	 PORT_START /* 6 */    \
	  PORT_BIT (0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);   \
	  PORT_BIT (0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);   \
	  PORT_BIT (0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);   \
	  PORT_BIT (0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);   \
	  PORT_BIT (0x10, IP_ACTIVE_LOW, IPT_BUTTON1);   \
	  PORT_BIT (0x20, IP_ACTIVE_LOW, IPT_BUTTON2);   \
	  PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	  PORT_DIPNAME( 0x80, 0, "Game port 1");   \
	   PORT_DIPSETTING(    0x00, "Joystick");   \
	   PORT_DIPSETTING(    0x80, "Mouse");   \
	    \
	 PORT_START /* 7 */    \
	  PORT_BIT (0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2);   \
	  PORT_BIT (0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2);   \
	  PORT_BIT (0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2);   \
	  PORT_BIT (0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2);   \
	  PORT_BIT (0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);   \
	  PORT_BIT (0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);   \
	  PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	  PORT_DIPNAME( 0x80, 0, "Game port 2");   \
	   PORT_DIPSETTING( 0x00, "Joystick");   \
	   PORT_DIPSETTING( 0x80, "Mouse");   \
	    \
	 PORT_START /* 8 */    \
	  PORT_DIPNAME( 0x40, 0, "Swap game port 1 and 2");   \
	   PORT_DIPSETTING( 0, DEF_STR( "No") );    \
	   PORT_DIPSETTING( 0x40, DEF_STR( "Yes") );    \
	  PORT_DIPNAME( 0x80, 0, "SIMPL");   \
	   PORT_DIPSETTING( 0x00, DEF_STR ( Off );    \
	   PORT_DIPSETTING( 0x80, DEF_STR ( On );    \
	  PORT_DIPNAME( 0x20, 0x20, "Enforce 4/8 sprites/line");   \
	   PORT_DIPSETTING( 0, DEF_STR( "No") );    \
	   PORT_DIPSETTING( 0x20, DEF_STR( "Yes") );    \
	  PORT_DIPNAME ( 0x03, 0, "Render resolution");\
	   PORT_DIPSETTING( 0, "High");\
	   PORT_DIPSETTING( 1, "Low");\
	   PORT_DIPSETTING( 2, "Auto");\
	    \
	 PORT_START /* 9 */    \
	  PORT_ANALOGX( 0xff00, 0x00, IPT_TRACKBALL_X | IPF_PLAYER1, 100, 0, 0, 0, KEYCODE_NONE, KEYCODE_NONE, JOYCODE_NONE, JOYCODE_NONE);   \
	  PORT_ANALOGX( 0x00ff, 0x00, IPT_TRACKBALL_Y | IPF_PLAYER1, 100, 0, 0, 0, KEYCODE_NONE, KEYCODE_NONE, JOYCODE_NONE, JOYCODE_NONE);   \
	    \
	 PORT_START /* 10 */    \
	  PORT_ANALOGX( 0xff00, 0x00, IPT_TRACKBALL_X | IPF_PLAYER2, 100, 0, 0, 0, KEYCODE_NONE, KEYCODE_NONE, JOYCODE_NONE, JOYCODE_NONE);   \
	  PORT_ANALOGX( 0x00ff, 0x00, IPT_TRACKBALL_Y | IPF_PLAYER2, 100, 0, 0, 0, KEYCODE_NONE, KEYCODE_NONE, JOYCODE_NONE, JOYCODE_NONE);   \
	/* end define for the special ports (DIPS, joystick, mouse) */
	
	#define KEYB_ROW0	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "0 );, KEYCODE_0, IP_JOY_NONE)	\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "1 !", KEYCODE_1, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "2 @", KEYCODE_2, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "3 #", KEYCODE_3, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "4 $", KEYCODE_4, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "5 %", KEYCODE_5, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "6 ^", KEYCODE_6, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "7 &", KEYCODE_7, IP_JOY_NONE);
	
	#define KEYB_EXPERT11_ROW0   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "0 );, KEYCODE_0, IP_JOY_NONE)    \
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "1 !", KEYCODE_1, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "2 \"", KEYCODE_2, IP_JOY_NONE);   \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "3 #", KEYCODE_3, IP_JOY_NONE);   \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "4 $", KEYCODE_4, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "5 %", KEYCODE_5, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "6 ^", KEYCODE_6, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "7 &", KEYCODE_7, IP_JOY_NONE);
	
	#define KEYB_HOTBIT_ROW0   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "0 );, KEYCODE_0, IP_JOY_NONE)    \
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "1 !", KEYCODE_1, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "2 @", KEYCODE_2, IP_JOY_NONE);   \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "3 #", KEYCODE_3, IP_JOY_NONE);   \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "4 $", KEYCODE_4, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "5 %", KEYCODE_5, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "6 \"", KEYCODE_6, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "7 &", KEYCODE_7, IP_JOY_NONE);
	
	#define KEYB_ROW1	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "8 *", KEYCODE_8, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "9 (", KEYCODE_9, IP_JOY_NONE);\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "- _", KEYCODE_MINUS, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "= +", KEYCODE_EQUALS, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "\\ |", KEYCODE_BACKSLASH, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "[ {", KEYCODE_OPENBRACE, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "] }", KEYCODE_CLOSEBRACE, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "; :", KEYCODE_COLON, IP_JOY_NONE);
	
	#define KEYB_HOTBIT_ROW1   \
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "8 *", KEYCODE_8, IP_JOY_NONE);   \
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "9 (", KEYCODE_9, IP_JOY_NONE);   \
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "- _", KEYCODE_MINUS, IP_JOY_NONE);   \
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "= +", KEYCODE_EQUALS, IP_JOY_NONE);  \
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "\\ ^", KEYCODE_BACKSLASH, IP_JOY_NONE);  \
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "' `", KEYCODE_QUOTE, IP_JOY_NONE);   \
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "\" `", KEYCODE_BACKSLASH2, IP_JOY_NONE);  \
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "\xc7 \xe7", KEYCODE_ASTERISK, IP_JOY_NONE);
	
	#define KEYB_EXPERT11_ROW1   \
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "8 '", KEYCODE_8, IP_JOY_NONE);   \
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "9 (", KEYCODE_9, IP_JOY_NONE);   \
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "- _", KEYCODE_MINUS, IP_JOY_NONE);   \
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "= +", KEYCODE_EQUALS, IP_JOY_NONE);  \
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "{ }", KEYCODE_OPENBRACE, IP_JOY_NONE);  \
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "' `", KEYCODE_QUOTE, IP_JOY_NONE);   \
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "[ ]", KEYCODE_CLOSEBRACE, IP_JOY_NONE);  \
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "~ ^", KEYCODE_TILDE, IP_JOY_NONE);
	
	#define KEYB_ROW2	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "' \"", KEYCODE_QUOTE, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "` ~", KEYCODE_TILDE, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", <", KEYCODE_COMMA, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". >", KEYCODE_STOP, IP_JOY_NONE);	\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "Dead Key", KEYCODE_NONE, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	
	#define KEYB_HOTBIT_ROW2   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "~ ^", KEYCODE_TILDE, IP_JOY_NONE);  \
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "[ ]", KEYCODE_OPENBRACE, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", ;", KEYCODE_COMMA, IP_JOY_NONE);   \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". :", KEYCODE_STOP, IP_JOY_NONE);    \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "< >", KEYCODE_CLOSEBRACE, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	
	#define KEYB_EXPERT10_ROW2   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "' \"", KEYCODE_QUOTE, IP_JOY_NONE);  \
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "\xc7 \xe7", KEYCODE_TILDE, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", <", KEYCODE_COMMA, IP_JOY_NONE);   \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". >", KEYCODE_STOP, IP_JOY_NONE);    \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "Dead Key", KEYCODE_NONE, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	
	#define KEYB_EXPERT11_ROW2   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "* @", KEYCODE_ASTERISK, IP_JOY_NONE);  \
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "\xc7 \xe7", KEYCODE_BACKSLASH, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", <", KEYCODE_COMMA, IP_JOY_NONE);   \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". >", KEYCODE_STOP, IP_JOY_NONE);    \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, ": ;", KEYCODE_COLON, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	
	#define KEYB_ROW3	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "c C", KEYCODE_C, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "d D", KEYCODE_D, IP_JOY_NONE);\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "e E", KEYCODE_E, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "f F", KEYCODE_F, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "g G", KEYCODE_G, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "h H", KEYCODE_H, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "i I", KEYCODE_I, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "j J", KEYCODE_J, IP_JOY_NONE);
	
	#define KEYB_ROW4	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "k K", KEYCODE_K, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "l L", KEYCODE_L, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "m M", KEYCODE_M, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "n N", KEYCODE_N, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "o O", KEYCODE_O, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "p P", KEYCODE_P, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "q Q", KEYCODE_Q, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "r R", KEYCODE_R, IP_JOY_NONE);
	
	#define KEYB_ROW5	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "s S", KEYCODE_S, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "t T", KEYCODE_T, IP_JOY_NONE);\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "u U", KEYCODE_U, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "v V", KEYCODE_V, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "w W", KEYCODE_W, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "x X", KEYCODE_X, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "y Y", KEYCODE_Y, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "z Z", KEYCODE_Z, IP_JOY_NONE);
	
	#define KEYB_ROW6	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "CTRL", KEYCODE_LCONTROL, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "GRAPH", KEYCODE_PGUP, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "CAPS", KEYCODE_CAPSLOCK, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "CODE", KEYCODE_PGDN, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "F1", KEYCODE_F1, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "F2", KEYCODE_F2, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "F3", KEYCODE_F3, IP_JOY_NONE);
	
	#define KEYB_EXPERT11_ROW6   \
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "CTRL", KEYCODE_LCONTROL, IP_JOY_NONE);   \
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "LGRAPH", KEYCODE_PGUP, IP_JOY_NONE);  \
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "CAPS", KEYCODE_CAPSLOCK, IP_JOY_NONE);   \
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "RGRAPH", KEYCODE_PGDN, IP_JOY_NONE);   \
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "F1", KEYCODE_F1, IP_JOY_NONE);   \
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "F2", KEYCODE_F2, IP_JOY_NONE);   \
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "F3", KEYCODE_F3, IP_JOY_NONE);
	
	#define KEYB_ROW7	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "F4", KEYCODE_F4, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "F5", KEYCODE_F5, IP_JOY_NONE);\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "ESC", KEYCODE_ESC, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "TAB", KEYCODE_TAB, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "STOP", KEYCODE_RCONTROL, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "BACKSPACE", KEYCODE_BACKSPACE, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "SELECT", KEYCODE_END, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "ENTER", KEYCODE_ENTER, IP_JOY_NONE);
	
	#define KEYB_ROW8	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SPACE", KEYCODE_SPACE, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "HOME", KEYCODE_HOME, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "INSERT", KEYCODE_INSERT, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "DEL", KEYCODE_DEL, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "LEFT", KEYCODE_LEFT, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "UP", KEYCODE_UP, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "DOWN", KEYCODE_DOWN, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "RIGHT", KEYCODE_RIGHT, IP_JOY_NONE);
	
	#define KEYB_ROW9	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM *", KEYCODE_ASTERISK, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM +", KEYCODE_PLUS_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM /", KEYCODE_SLASH_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 0", KEYCODE_0_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 1", KEYCODE_1_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 2", KEYCODE_2_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 3", KEYCODE_3_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 4", KEYCODE_4_PAD, IP_JOY_NONE);
	
	#define KEYB_ROW10	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 5", KEYCODE_5_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 6", KEYCODE_6_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 7", KEYCODE_7_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 8", KEYCODE_8_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM 9", KEYCODE_9_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM -", KEYCODE_MINUS_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM ,", KEYCODE_ENTER_PAD, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "NUM .", KEYCODE_DEL_PAD, IP_JOY_NONE);
	
	static InputPortPtr input_ports_msx = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_ROW0
	  KEYB_ROW1
	
	 PORT_START /* 1 */
	  KEYB_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  PORT_BITX (0xff00, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_msxuk = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_ROW0
	  KEYB_ROW1
	
	 PORT_START /* 1 */
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "' \"", KEYCODE_QUOTE, IP_JOY_NONE);
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "\xa3 ~", KEYCODE_TILDE, IP_JOY_NONE);
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", <", KEYCODE_COMMA, IP_JOY_NONE);
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". >", KEYCODE_STOP, IP_JOY_NONE);
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "Dead Key", KEYCODE_NONE, IP_JOY_NONE);
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  PORT_BITX (0xff00, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	#define KEYB_JAP_ROW0	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "0", KEYCODE_0, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "1 !", KEYCODE_1, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "2 \"", KEYCODE_2, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "3 #", KEYCODE_3, IP_JOY_NONE);\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "4 $", KEYCODE_4, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "5 %", KEYCODE_5, IP_JOY_NONE);\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "6 &", KEYCODE_6, IP_JOY_NONE);\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "7 '", KEYCODE_7, IP_JOY_NONE);
	
	#define KEYB_JAP_ROW1	\
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "8 (", KEYCODE_8, IP_JOY_NONE);\
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "9 );, KEYCODE_9, IP_JOY_NONE)	\
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "- =", KEYCODE_MINUS, IP_JOY_NONE);\
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "^ ~", KEYCODE_EQUALS, IP_JOY_NONE);\
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "\xa5 |", KEYCODE_BACKSLASH, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "@ `", KEYCODE_OPENBRACE, IP_JOY_NONE);\
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "[ }", KEYCODE_CLOSEBRACE, IP_JOY_NONE);\
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "; +", KEYCODE_COLON, IP_JOY_NONE);
	
	#define KEYB_KOR_ROW1   \
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "8 (", KEYCODE_8, IP_JOY_NONE);   \
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "9 );, KEYCODE_9, IP_JOY_NONE)    \
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "- =", KEYCODE_MINUS, IP_JOY_NONE);   \
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "^ ~", KEYCODE_EQUALS, IP_JOY_NONE);  \
	  PORT_BITX (0x1000, IP_ACTIVE_LOW, IPT_KEYBOARD, "WON |", KEYCODE_BACKSLASH, IP_JOY_NONE);\
	  PORT_BITX (0x2000, IP_ACTIVE_LOW, IPT_KEYBOARD, "@ `", KEYCODE_OPENBRACE, IP_JOY_NONE);   \
	  PORT_BITX (0x4000, IP_ACTIVE_LOW, IPT_KEYBOARD, "[ }", KEYCODE_CLOSEBRACE, IP_JOY_NONE);  \
	  PORT_BITX (0x8000, IP_ACTIVE_LOW, IPT_KEYBOARD, "; +", KEYCODE_COLON, IP_JOY_NONE);
	
	#define KEYB_JAP_ROW2	\
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, ": *", KEYCODE_QUOTE, IP_JOY_NONE);\
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "] }", KEYCODE_TILDE, IP_JOY_NONE);\
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, ", <", KEYCODE_COMMA, IP_JOY_NONE);\
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, ". >", KEYCODE_STOP, IP_JOY_NONE);	\
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "/ ?", KEYCODE_SLASH, IP_JOY_NONE);\
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "  _", KEYCODE_NONE, IP_JOY_NONE);	\
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "a A", KEYCODE_A, IP_JOY_NONE);	\
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "b B", KEYCODE_B, IP_JOY_NONE);
	
	static InputPortPtr input_ports_msxj = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_JAP_ROW0
	  KEYB_JAP_ROW1
	
	 PORT_START /* 1 */
	  KEYB_JAP_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "CTRL", KEYCODE_LCONTROL, IP_JOY_NONE);
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "GRAPH", KEYCODE_PGUP, IP_JOY_NONE);
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "CAPS", KEYCODE_CAPSLOCK, IP_JOY_NONE);
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "KANA", KEYCODE_PGDN, IP_JOY_NONE);
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "F1", KEYCODE_F1, IP_JOY_NONE);
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "F2", KEYCODE_F2, IP_JOY_NONE);
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "F3", KEYCODE_F3, IP_JOY_NONE);
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  PORT_BITX (0xff00, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_msxkr = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_JAP_ROW0
	  KEYB_KOR_ROW1
	
	 PORT_START /* 1 */
	  KEYB_JAP_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "CTRL", KEYCODE_LCONTROL, IP_JOY_NONE);
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "GRAPH", KEYCODE_PGUP, IP_JOY_NONE);
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "CAPS", KEYCODE_CAPSLOCK, IP_JOY_NONE);
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "Hangul", KEYCODE_PGDN, IP_JOY_NONE);
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "F1", KEYCODE_F1, IP_JOY_NONE);
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "F2", KEYCODE_F2, IP_JOY_NONE);
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "F3", KEYCODE_F3, IP_JOY_NONE);
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  PORT_BITX (0xff00, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hotbit = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_HOTBIT_ROW0
	  KEYB_HOTBIT_ROW1
	
	 PORT_START /* 1 */
	  KEYB_HOTBIT_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  PORT_BITX (0xff00, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	#define KEYB_EXPERT11_ROW9   \
	  PORT_BITX (0x0100, IP_ACTIVE_LOW, IPT_KEYBOARD, "+", KEYCODE_8, IP_JOY_NONE);   \
	  PORT_BITX (0x0200, IP_ACTIVE_LOW, IPT_KEYBOARD, "-", KEYCODE_9, IP_JOY_NONE);   \
	  PORT_BITX (0x0400, IP_ACTIVE_LOW, IPT_KEYBOARD, "*", KEYCODE_MINUS, IP_JOY_NONE);   \
	  PORT_BITX (0x0800, IP_ACTIVE_LOW, IPT_KEYBOARD, "/", KEYCODE_EQUALS, IP_JOY_NONE);  \
	  PORT_BITX (0xf000, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	static InputPortPtr input_ports_expert11 = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_EXPERT11_ROW0
	  KEYB_EXPERT11_ROW1
	
	 PORT_START /* 1 */
	  KEYB_EXPERT11_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_EXPERT11_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  KEYB_EXPERT11_ROW9
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_expert10 = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_ROW0
	  KEYB_ROW1
	
	 PORT_START /* 1 */
	  KEYB_EXPERT10_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_EXPERT11_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  KEYB_EXPERT11_ROW9
	
	 PORT_START /* 5 */
	  PORT_BITX (0xffff, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)    \
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_msx2 = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_ROW0
	  KEYB_ROW1
	
	 PORT_START /* 1 */
	  KEYB_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  KEYB_ROW6
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  KEYB_ROW9
	
	 PORT_START /* 5 */
	  KEYB_ROW10
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_msx2j = new InputPortPtr(){ public void handler() { 
	
	 PORT_START /* 0 */
	  KEYB_JAP_ROW0
	  KEYB_JAP_ROW1
	
	 PORT_START /* 1 */
	  KEYB_JAP_ROW2
	  KEYB_ROW3
	
	 PORT_START /* 2 */
	  KEYB_ROW4
	  KEYB_ROW5
	
	 PORT_START /* 3 */
	  PORT_BITX (0x0001, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);
	  PORT_BITX (0x0002, IP_ACTIVE_LOW, IPT_KEYBOARD, "CTRL", KEYCODE_LCONTROL, IP_JOY_NONE);
	  PORT_BITX (0x0004, IP_ACTIVE_LOW, IPT_KEYBOARD, "GRAPH", KEYCODE_PGUP, IP_JOY_NONE);
	  PORT_BITX (0x0008, IP_ACTIVE_LOW, IPT_KEYBOARD, "CAPS", KEYCODE_CAPSLOCK, IP_JOY_NONE);
	  PORT_BITX (0x0010, IP_ACTIVE_LOW, IPT_KEYBOARD, "KANA", KEYCODE_PGDN, IP_JOY_NONE);
	  PORT_BITX (0x0020, IP_ACTIVE_LOW, IPT_KEYBOARD, "F1", KEYCODE_F1, IP_JOY_NONE);
	  PORT_BITX (0x0040, IP_ACTIVE_LOW, IPT_KEYBOARD, "F2", KEYCODE_F2, IP_JOY_NONE);
	  PORT_BITX (0x0080, IP_ACTIVE_LOW, IPT_KEYBOARD, "F3", KEYCODE_F3, IP_JOY_NONE);
	  KEYB_ROW7
	
	 PORT_START /* 4 */
	  KEYB_ROW8
	  KEYB_ROW9
	
	 PORT_START /* 5 */
	  KEYB_ROW10
	
	 MSX_DIPS
	
	INPUT_PORTS_END(); }}; 
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
	    1,  /* 1 chip */
	    1789773,    /* 1.7897725 MHz */
	    new int[] { 10 },
	    new ReadHandlerPtr[] { msx_psg_port_a_r },
	    new ReadHandlerPtr[] { msx_psg_port_b_r },
	    new WriteHandlerPtr[] { msx_psg_port_a_w },
	    new WriteHandlerPtr[] { msx_psg_port_b_w }
	);
	
	static struct k051649_interface k051649_interface =
	{
	    1789773,  /* Clock */
	    25,         /* Volume */
	};
	
	static struct DACinterface dac_interface =
	{
	    1,
	    { 10 }
	};
	
	static struct YM2413interface ym2413_interface=
	{
	    1,                      /* 1 chip */
	    3579545,                /* 3.57Mhz.. ? */
	    { 25 }	                /* Volume */
	};
	
	static struct Wave_interface wave_interface = {
	    1,              /* number of waves */
	    { 25 }          /* mixing levels */
	};
	
	int msx_vh_start(void)
	{
		int i;
	
		i = TMS9928A_start(TMS99x8A, 0x4000);
		if (i == 0) TMS9928A_int_callback(msx_vdp_interrupt);
	
		return i;
	}
	
	int msx2_vh_start(void)
	{
	    return v9938_init (MODEL_V9938, 0x20000, msx_vdp_interrupt);
	}
	
	static struct MachineDriver machine_driver_msx =
	{
	    /* basic machine hardware */
	    {
	        {
	            CPU_Z80_MSX,
	            3579545,    /* 3.579545 Mhz */
	            readmem,writemem,readport,writeport,
	            msx_interrupt,1
	        }
	    },
	    60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
	    1,
	    msx_ch_reset, /* init_machine */
	    msx_ch_stop, /* stop_machine */
	
	    /* video hardware */
	    32*8, 24*8, { 0*8, 32*8-1, 0*8, 24*8-1 },
	    0,
	    TMS9928A_PALETTE_SIZE,TMS9928A_COLORTABLE_SIZE,
	    tms9928A_init_palette,
	
	    VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_TYPE_RASTER,
	    0,
	    msx_vh_start,
	    TMS9928A_stop,
	    TMS9928A_refresh,
	
	    /* sound hardware */
	    0,0,0,0,
	    {
	        {
	            SOUND_AY8910,
	            &ay8910_interface
	        },
	        {
	            SOUND_K051649,
	            &k051649_interface
	        },
	        {
	            SOUND_YM2413,
	            &ym2413_interface
	        },
	        {
	            SOUND_DAC,
	            &dac_interface
	        },
	        {
	            SOUND_WAVE,
	            &wave_interface
	        }
	    }
	};
	
	static struct MachineDriver machine_driver_msx_pal =
	{
	    /* basic machine hardware */
	    {
	        {
	            CPU_Z80_MSX,
	            3579545,    /* 3.579545 Mhz */
	            readmem,writemem,readport,writeport,
	            msx_interrupt,1
	        }
	    },
	    50, DEFAULT_REAL_60HZ_VBLANK_DURATION,
	    1,
	    msx_ch_reset, /* init_machine */
	    msx_ch_stop, /* stop_machine */
	
	    /* video hardware */
	    32*8, 24*8, { 0*8, 32*8-1, 0*8, 24*8-1 },
	    0,
	    TMS9928A_PALETTE_SIZE,TMS9928A_COLORTABLE_SIZE,
	    tms9928A_init_palette,
	
	    VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_TYPE_RASTER,
	    0,
	    msx_vh_start,
	    TMS9928A_stop,
	    TMS9928A_refresh,
	
	    /* sound hardware */
	    0,0,0,0,
	    {
	        {
	            SOUND_AY8910,
	            &ay8910_interface
	        },
	        {
	            SOUND_K051649,
	            &k051649_interface
	        },
	        {
	            SOUND_YM2413,
	            &ym2413_interface
	        },
	        {
	            SOUND_DAC,
	            &dac_interface
	        },
	        {
	            SOUND_WAVE,
	            &wave_interface
	        }
	    }
	};
	
	static struct MachineDriver machine_driver_msx2 =
	{
	    /* basic machine hardware */
	    {
	        {
	            CPU_Z80_MSX,
	            3579545,    /* 3.579545 Mhz */
	            readmem,writemem,readport2,writeport2,
	            msx2_interrupt,262
	        }
	    },
	    60, 0,
	    1,
	    msx2_ch_reset, /* init_machine */
	    msx2_ch_stop, /* stop_machine */
	
	    /* video hardware */
	    512 + 32, (212 + 16) * 2, { 0, 512 + 32 - 1, 0, (212 + 16) * 2 - 1 },
	    0,
	    512,
		512,
	    v9938_init_palette,
	
	    VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_TYPE_RASTER,
	    0,
	    msx2_vh_start,
	    v9938_exit,
	    v9938_refresh,
	
	    /* sound hardware */
	    0,0,0,0,
	    {
	        {
	            SOUND_AY8910,
	            &ay8910_interface
	        },
	        {
	            SOUND_K051649,
	            &k051649_interface
	        },
	        {
	            SOUND_YM2413,
	            &ym2413_interface
	        },
	        {
	            SOUND_DAC,
	            &dac_interface
	        },
	        {
	            SOUND_WAVE,
	            &wave_interface
	        }
	    },
		msx2_nvram
	};
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	ROM_START (msx)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msx.rom", 0x0000, 0x8000, 0x94ee12f3);
	ROM_END(); }}; 
	
	ROM_START (msxj)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msxj.rom", 0x0000, 0x8000, 0xee229390);
	ROM_END(); }}; 
	
	ROM_START (msxuk)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msxuk.rom", 0x0000, 0x8000, 0xe9ccd789);
	ROM_END(); }}; 
	
	ROM_START (msxkr)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msxkr.rom", 0x0000, 0x8000, 0x3ab0cd3b);
	    ROM_LOAD_OPTIONAL ("msxhan.rom", 0x8000, 0x4000, 0x97478efb)
	ROM_END(); }}; 
	
	ROM_START (hotbit11)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("hotbit11.rom", 0x0000, 0x8000, 0xb6942694);
	ROM_END(); }}; 
	
	ROM_START (hotbit12)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("hotbit12.rom", 0x0000, 0x8000, 0xf59a4a0c);
	ROM_END(); }}; 
	
	ROM_START (expert10)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("expert10.rom", 0x0000, 0x8000, 0x07610d77);
	ROM_END(); }}; 
	
	ROM_START (expert11)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("expert11.rom", 0x0000, 0x8000, 0xefb4b972);
	ROM_END(); }}; 
	
	ROM_START (msx2)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msx20.rom", 0x0000, 0x8000, 0xf05ed518);
	    ROM_LOAD ("msx20ext.rom", 0x8000, 0x4000, 0x95db2959);
	ROM_END(); }}; 
	
	ROM_START (msx2a)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msx21.rom", 0x0000, 0x8000, 0x6cdaf3a5);
	    ROM_LOAD ("msx21ext.rom", 0x8000, 0x4000, 0x66237ecf);
	ROM_END(); }}; 
	
	ROM_START (msx2j)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msx20j.rom", 0x0000, 0x8000, 0x9b3e7b97);
	    ROM_LOAD ("msx20xtj.rom", 0x8000, 0x4000, 0x43e7a7fc);
	ROM_END(); }}; 
	/*
	ROM_START (msxkra)
	    ROM_REGION (0x10000, REGION_CPU1,0);
	    ROM_LOAD ("msxkra.rom", 0x0000, 0x8000, 0xa781f7ca);
	    ROM_LOAD_OPTIONAL ("msxhan.rom", 0x8000, 0x4000, 0x97478efb)
	ROM_END(); }}; 
	*/
	
	static const struct IODevice io_msx[] = {
	{
	    IO_CARTSLOT,                /* type */
	    MSX_MAX_CARTS,              /* count */
	    "rom\0",                    /* file extensions */
		IO_RESET_NONE,				/* reset if file changed */
	    0,
	    msx_load_rom,               /* init */
	    msx_exit_rom,               /* exit */
	    NULL,                       /* info */
	    NULL,                       /* open */
	    NULL,                       /* close */
	    NULL,                       /* status */
	    NULL,                       /* seek */
	    NULL,                       /* tell */
	    NULL,                       /* input */
	    NULL,                       /* output */
	    NULL,                       /* input_chunk */
	    NULL                        /* output_chunk */
	},
	    {
	        IO_FLOPPY,              /* type */
	        2,                      /* count */
	        "dsk\0",                /* file extensions */
	        IO_RESET_NONE,          /* reset if file changed */
	        0,
	        msx_floppy_init,   	/* init */
	        basicdsk_floppy_exit,   /* exit */
	        NULL,                   /* info */
	        NULL,                   /* open */
	        NULL,                   /* close */
	        floppy_status,          /* status */
	        NULL,                   /* seek */
	        NULL,                   /* tell */
	        NULL,                   /* input */
	        NULL,                   /* output */
	        NULL,                   /* input_chunk */
	        NULL                    /* output_chunk */
	    },
	    IO_CASSETTE_WAVE (1, "cas\0wav\0", NULL, msx_cassette_init, msx_cassette_exit),
		IO_PRINTER_PORT (1, "prn\0"),
	    { IO_END }
	};
	
	#define io_msxj io_msx
	#define io_msxkr io_msx
	/* #define io_msxkra io_msx */
	#define io_msxuk io_msx
	#define io_msx2  io_msx
	#define io_msx2a io_msx
	#define io_msx2j io_msx
	#define io_hotbit11 io_msx
	#define io_hotbit12 io_msx
	#define io_expert10 io_msx
	#define io_expert11 io_msx
	
	/*    YEAR  NAME      PARENT  MACHINE  INPUT     INIT   COMPANY              FULLNAME */
	COMP( 1983, msx,      0,      msx_pal, msx,      msx,   "ASCII & Microsoft", "MSX 1" )
	COMP( 1983, msxj,     msx,    msx,     msxj,     msx,   "ASCII & Microsoft", "MSX 1 (Japan)" )
	COMP( 1983, msxkr,    msx,    msx,     msxkr,    msx,   "ASCII & Microsoft", "MSX 1 (Korea)" )
	/* COMP( 1983, msxkra, msx, msx, msxkr, msx, "ASCII & Microsoft", "MSX 1 (Korea ALT)" ) */
	COMP( 1983, msxuk,    msx,    msx_pal, msxuk,    msx,   "ASCII & Microsoft", "MSX 1 (UK)" )
	COMP( 1985, hotbit11, msx,    msx,     hotbit,   msx,   "Sharp / Epcom",     "HB-8000 Hotbit 1.1" )
	COMP( 1985, hotbit12, msx,    msx,     hotbit,   msx,   "Sharp / Epcom",     "HB-8000 Hotbit 1.2" )
	COMP( 1985, expert10, msx,    msx,     expert10, msx,   "Gradiente",         "XP-800 Expert 1.0" )
	COMP( 1985, expert11, msx,    msx,     expert11, msx,   "Gradiente",         "XP-800 Expert 1.1" )
	COMPX( 1985, msx2,    msx,    msx2,    msx2,      msx2,   "ASCII & Microsoft", "MSX 2", GAME_NOT_WORKING )
	COMPX( 1985, msx2a,   msx,    msx2,    msx2,      msx2,   "ASCII & Microsoft", "MSX 2 (BASIC 2.1)", GAME_NOT_WORKING )
	COMPX( 1985, msx2j,  msx,    msx2,     msx2j,     msx2,   "ASCII & Microsoft", "MSX 2 (Japan)", GAME_NOT_WORKING )
	
}
