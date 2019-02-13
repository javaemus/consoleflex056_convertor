/************************************************************************
Philips P2000 1 Memory map

	CPU: Z80
		0000-0fff	ROM
		1000-4fff	ROM (appl)
		5000-57ff	RAM (Screen T ver)
		5000-5fff	RAM (Screen M ver)
		6000-9fff	RAM (system)
		a000-ffff	RAM (extension)

	Interrupts:

	Ports:
		00-09		Keyboard input
		10-1f		Output ports
		20-2f		Input ports
		30-3f		Scroll reg (T ver)
		50-5f		Beeper
		70-7f		DISAS (M ver)
		88-8B		CTC
		8C-90		Floppy ctrl
		94			RAM Bank select

	Display: SAA5050

************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package systems;

public class p2000t
{
	
	/* port i/o functions */
	
	public static IO_ReadPort p2000t_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(0x00, 0x0f, p2000t_port_000f_r),
		new IO_ReadPort(0x20, 0x2f, p2000t_port_202f_r),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort p2000t_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(0x10, 0x1f, p2000t_port_101f_w),
		new IO_WritePort(0x30, 0x3f, p2000t_port_303f_w),
		new IO_WritePort(0x50, 0x5f, p2000t_port_505f_w),
		new IO_WritePort(0x70, 0x7f, p2000t_port_707f_w),
		new IO_WritePort(0x88, 0x8b, p2000t_port_888b_w),
		new IO_WritePort(0x8c, 0x90, p2000t_port_8c90_w),
		new IO_WritePort(0x94, 0x94, p2000t_port_9494_w),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/* Memory w/r functions */
	
	public static Memory_ReadAddress p2000t_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress(0x0000, 0x0fff, MRA_ROM),
		new Memory_ReadAddress(0x1000, 0x4fff, MRA_RAM),
		new Memory_ReadAddress(0x5000, 0x57ff, videoram_r),
		new Memory_ReadAddress(0x5800, 0x9fff, MRA_RAM),
		new Memory_ReadAddress(0xa000, 0xffff, MRA_NOP),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress p2000t_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress(0x0000, 0x0fff, MWA_ROM),
		new Memory_WriteAddress(0x1000, 0x4fff, MWA_RAM),
		new Memory_WriteAddress(0x5000, 0x57ff, videoram_w, videoram, videoram_size),
		new Memory_WriteAddress(0x5800, 0x9fff, MWA_RAM),
		new Memory_WriteAddress(0xa000, 0xffff, MWA_NOP),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress p2000m_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress(0x0000, 0x0fff, MRA_ROM),
		new Memory_ReadAddress(0x1000, 0x4fff, MRA_RAM),
		new Memory_ReadAddress(0x5000, 0x5fff, videoram_r),
		new Memory_ReadAddress(0x6000, 0x9fff, MRA_RAM),
		new Memory_ReadAddress(0xa000, 0xffff, MRA_NOP),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress p2000m_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress(0x0000, 0x0fff, MWA_ROM),
		new Memory_WriteAddress(0x1000, 0x4fff, MWA_RAM),
		new Memory_WriteAddress(0x5000, 0x5fff, videoram_w, videoram, videoram_size),
		new Memory_WriteAddress(0x6000, 0x9fff, MWA_RAM),
		new Memory_WriteAddress(0xa000, 0xffff, MWA_NOP),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/* graphics output */
	
	static GfxLayout p2000m_charlayout = new GfxLayout
	(
		6, 10,
		256,
		1,
		new int[] { 0 },
		new int[] { 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8,
		  5*8, 6*8, 7*8, 8*8, 9*8 },
		8 * 10
	);
	
	static GfxDecodeInfo p2000m_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, p2000m_charlayout, 0, 128 ),
		new GfxDecodeInfo( -1 )
	};
	
	static	unsigned	char	p2000m_palette[2 * 3] =
	{
		0x00, 0x00, 0x00,
		0xff, 0xff, 0xff
	};
	
	static	unsigned	short	p2000m_colortable[2 * 2] =
	{
		1,0, 0,1
	};
	
	static	void	p2000m_init_palette (unsigned char *sys_palette,
				unsigned short *sys_colortable, const unsigned char *color_prom)
	{
		memcpy (sys_palette, p2000m_palette, sizeof (p2000m_palette));
		memcpy (sys_colortable, p2000m_colortable, sizeof (p2000m_colortable));
	}
	
	/* Keyboard input */
	
	INPUT_PORTS_START (p2000t)
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Left", KEYCODE_LEFT, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "6", KEYCODE_6, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Up", KEYCODE_UP, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Q", KEYCODE_Q, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "3", KEYCODE_3, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "5", KEYCODE_5, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "7", KEYCODE_7, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "4", KEYCODE_4, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Tab", KEYCODE_TAB, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "H", KEYCODE_H, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Z", KEYCODE_Z, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "S", KEYCODE_S, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "D", KEYCODE_D, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "G", KEYCODE_G, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "J", KEYCODE_J, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "F", KEYCODE_F, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad .", KEYCODE_DEL_PAD, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "Space", KEYCODE_SPACE, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 00", KEYCODE_ENTER_PAD, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 0", KEYCODE_0_PAD, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "#", KEYCODE_BACKSLASH, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "Down", KEYCODE_DOWN, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, ",", KEYCODE_COMMA, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "Right", KEYCODE_RIGHT, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Shift Lock", KEYCODE_CAPSLOCK, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "N", KEYCODE_N, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "<", KEYCODE_OPENBRACE, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "X", KEYCODE_X, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "C", KEYCODE_C, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "B", KEYCODE_B, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "M", KEYCODE_M, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "V", KEYCODE_V, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Code", KEYCODE_HOME, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "Y", KEYCODE_Y, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "A", KEYCODE_A, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "W", KEYCODE_W, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "E", KEYCODE_E, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "T", KEYCODE_T, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "U", KEYCODE_U, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "R", KEYCODE_R, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Clrln", KEYCODE_END, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "9", KEYCODE_9, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad +", KEYCODE_PLUS_PAD, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad -", KEYCODE_MINUS_PAD, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "Backspace", KEYCODE_BACKSPACE, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "0", KEYCODE_0, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "1", KEYCODE_1, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "-", KEYCODE_MINUS, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 9", KEYCODE_9_PAD, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "O", KEYCODE_O, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 8", KEYCODE_8_PAD, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 7", KEYCODE_7_PAD, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "Enter", KEYCODE_ENTER, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "P", KEYCODE_P, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "8", KEYCODE_8, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "@", KEYCODE_QUOTE, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 3", KEYCODE_3_PAD, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, ".", KEYCODE_STOP, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 2", KEYCODE_2_PAD, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 1", KEYCODE_1_PAD, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "->", KEYCODE_CLOSEBRACE, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "/", KEYCODE_SLASH, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "K", KEYCODE_K, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "2", KEYCODE_2, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 6", KEYCODE_6_PAD, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "L", KEYCODE_L, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 5", KEYCODE_5_PAD, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Pad 4", KEYCODE_4_PAD, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "1/4", KEYCODE_TILDE, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, ";", KEYCODE_EQUALS, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "I", KEYCODE_I, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, ":", KEYCODE_COLON, IP_JOY_NONE);
		PORT_START
		PORT_BITX (0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "LShift", KEYCODE_LSHIFT, IP_JOY_NONE);
		PORT_BITX (0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "deadkey", KEYCODE_NONE, IP_JOY_NONE);
		PORT_BITX (0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "RShift", KEYCODE_RSHIFT, IP_JOY_NONE);
	INPUT_PORTS_END(); }}; 
	
	/* Sound output */
	
	static struct Speaker_interface speaker_interface =
	{
		1,			/* one speaker */
		{ 100 },	/* mixing levels */
		{ 0 },		/* optional: number of different levels */
	    { NULL }    /* optional: level lookup table */
	};
	
	/* Machine definition */
	
	static  struct  MachineDriver   machine_driver_p2000t =
	{
		{
			{
				CPU_Z80,
				2500000,
				p2000t_readmem, p2000t_writemem,
				p2000t_readport, p2000t_writeport,
				interrupt, 1,
			},
		},
		50, SAA5050_VBLANK,
		1,
		p2000t_init_machine,
		p2000t_stop_machine,
	
		SAA5050_VIDHRDW
	
		0, 0, 0, 0,
		{
			{
				SOUND_SPEAKER,
				&speaker_interface
	        }
	    }
	};
	
	static  struct  MachineDriver   machine_driver_p2000m =
	{
		{
			{
				CPU_Z80,
				2500000,
				p2000m_readmem, p2000m_writemem,
				p2000t_readport, p2000t_writeport,
				interrupt, 1,
			},
		},
		50, 2500,
		1,
		p2000t_init_machine,
		p2000t_stop_machine,
		80 * 6,
		24 * 10,
		{ 0, 80 * 6 - 1, 0, 24 * 10 - 1},
		p2000m_gfxdecodeinfo,
		2, 4,
		p2000m_init_palette,
		VIDEO_TYPE_RASTER,
		0,
		p2000m_vh_start,
		p2000m_vh_stop,
		p2000m_vh_screenrefresh,
		0, 0, 0, 0,
		{
			{
				SOUND_SPEAKER,
				&speaker_interface
	        }
	    }
	};
	
	static RomLoadPtr rom_p2000t = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000, REGION_CPU1,0);
		ROM_LOAD("p2000.rom", 0x0000, 0x1000, 0x650784a3);
		ROM_LOAD("basic.rom", 0x1000, 0x4000, 0x9d9d38f9);
		ROM_REGION(0x01000, REGION_GFX1,0);
		ROM_LOAD("p2000.chr", 0x0140, 0x08c0, BADCRC(0x78c17e3e);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_p2000m = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000, REGION_CPU1,0);
		ROM_LOAD("p2000.rom", 0x0000, 0x1000, 0x650784a3);
		ROM_LOAD("basic.rom", 0x1000, 0x4000, 0x9d9d38f9);
		ROM_REGION(0x01000, REGION_GFX1,0);
		ROM_LOAD("p2000.chr", 0x0140, 0x08c0, BADCRC(0x78c17e3e);
	ROM_END(); }}; 
	
	static	const	struct	IODevice	io_p2000t[] =
	{
		{ IO_END }
	};
	
	static	const	struct	IODevice	io_p2000m[] =
	{
		{ IO_END }
	};
	
	/*		YEAR	NAME		PARENT	MACHINE		INPUT		INIT	COMPANY		FULLNAME */
	COMP (	1980,	p2000t,		0,		p2000t,		p2000t,		0,		"Philips",	"Philips P2000T" )
	COMPX(	1980,	p2000m,		p2000t,	p2000m,		p2000t,		0,		"Philips",	"Philips P2000M", GAME_ALIAS )
	
}
