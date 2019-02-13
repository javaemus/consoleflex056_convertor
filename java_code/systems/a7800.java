/***************************************************************************

  a7800.c

  Driver file to handle emulation of the Atari 7800.

  Dan Boris

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package systems;

public class a7800
{
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	    new Memory_ReadAddress( 0x0000, 0x001f, a7800_TIA_r ),
	    new Memory_ReadAddress( 0x0020, 0x003f, a7800_MARIA_r ),
	    new Memory_ReadAddress( 0x0040, 0x00FF, a7800_RAM0_r ),
	    new Memory_ReadAddress( 0x0100, 0x011f, a7800_TIA_r ),
	    new Memory_ReadAddress( 0x0120, 0x013f, a7800_MARIA_r ),
	    new Memory_ReadAddress( 0x0140, 0x01FF, a7800_RAM1_r ),
	    new Memory_ReadAddress( 0x0200, 0x021f, a7800_TIA_r ),
	    new Memory_ReadAddress( 0x0220, 0x023f, a7800_MARIA_r ),
	    new Memory_ReadAddress( 0x0280, 0x02FF, a7800_RIOT_r ),
	    new Memory_ReadAddress( 0x0300, 0x031f, a7800_TIA_r ),
	    new Memory_ReadAddress( 0x0320, 0x033f, a7800_MARIA_r ),
	    new Memory_ReadAddress( 0x0480, 0x04ff, MRA_RAM ),    /* RIOT RAM */
	    new Memory_ReadAddress( 0x1800, 0x27FF, MRA_RAM ),
	    new Memory_ReadAddress( 0x2800, 0x2FFF, a7800_MAINRAM_r ),
	    new Memory_ReadAddress( 0x3000, 0x37FF, a7800_MAINRAM_r ),
	    new Memory_ReadAddress( 0x3800, 0x3FFF, a7800_MAINRAM_r ),
	    new Memory_ReadAddress( 0x4000, 0x7FFF, MRA_ROM ),
	    new Memory_ReadAddress( 0x8000, 0xBFFF, MRA_BANK1 ),
	    new Memory_ReadAddress( 0xC000, 0xFFFF, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	    new Memory_WriteAddress( 0x0000, 0x001f, a7800_TIA_w ),
	    new Memory_WriteAddress( 0x0020, 0x003f, a7800_MARIA_w ),
	    new Memory_WriteAddress( 0x0040, 0x00FF, a7800_RAM0_w ),
	    new Memory_WriteAddress( 0x0100, 0x011f, a7800_TIA_w ),
	    new Memory_WriteAddress( 0x0120, 0x013f, a7800_MARIA_w ),
	    new Memory_WriteAddress( 0x0140, 0x01FF, a7800_RAM1_w ),
	    new Memory_WriteAddress( 0x0200, 0x021f, a7800_TIA_w ),
	    new Memory_WriteAddress( 0x0220, 0x023f, a7800_MARIA_w ),
	    new Memory_WriteAddress( 0x0280, 0x02FF, a7800_RIOT_w ),
	    new Memory_WriteAddress( 0x0300, 0x031f, a7800_TIA_w ),
	    new Memory_WriteAddress( 0x0320, 0x033f, a7800_MARIA_w ),
	    new Memory_WriteAddress( 0x0480, 0x04ff, MWA_RAM ),  /* RIOT RAM */
	    new Memory_WriteAddress( 0x1800, 0x27FF, MWA_RAM ),
	    new Memory_WriteAddress( 0x2800, 0x2FFF, a7800_MAINRAM_w ),
	    new Memory_WriteAddress( 0x3000, 0x37FF, a7800_MAINRAM_w ),
	    new Memory_WriteAddress( 0x3800, 0x3FFF, a7800_MAINRAM_w ),
	    new Memory_WriteAddress( 0x4000, 0xFFFF, a7800_cart_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_a7800 = new InputPortPtr(){ public void handler() { 
	    PORT_START      /* IN0 */
	    PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );
	    PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
	    PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
	    PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
	    PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
	    PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);
	    PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
	    PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
	
	    PORT_START      /* IN1 */
	    PORT_BIT ( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
	    PORT_BIT ( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
	    PORT_BIT ( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
	    PORT_BIT ( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON2 );
	    PORT_BIT ( 0xF0, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START      /* IN2 */
	    PORT_BIT (0x7F, IP_ACTIVE_LOW, IPT_UNUSED);
	    PORT_BIT (0x80, IP_ACTIVE_HIGH, IPT_VBLANK);
	
	    PORT_START      /* IN3 */
	    PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN, "Reset", KEYCODE_R, IP_JOY_DEFAULT);
	    PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN, "Start", KEYCODE_S, IP_JOY_DEFAULT);
	    PORT_BIT ( 0xFC, IP_ACTIVE_LOW, IPT_UNUSED);
	
	
	INPUT_PORTS_END(); }}; 
	
	static UINT8 palette[256*3] =
	{
	    /* Grey */
	    0x00,0x00,0x00, 0x1c,0x1c,0x1c, 0x39,0x39,0x39, 0x59,0x59,0x59,
	    0x79,0x79,0x79, 0x92,0x92,0x92, 0xab,0xab,0xab, 0xbc,0xbc,0xbc,
	    0xcd,0xcd,0xcd, 0xd9,0xd9,0xd9, 0xe6,0xe6,0xe6, 0xec,0xec,0xec,
	    0xf2,0xf2,0xf2, 0xf8,0xf8,0xf8, 0xff,0xff,0xff, 0xff,0xff,0xff,
	    /* Gold */
	    0x39,0x17,0x01, 0x5e,0x23,0x04, 0x83,0x30,0x08, 0xa5,0x47,0x16,
	    0xc8,0x5f,0x24, 0xe3,0x78,0x20, 0xff,0x91,0x1d, 0xff,0xab,0x1d,
	    0xff,0xc5,0x1d, 0xff,0xce,0x34, 0xff,0xd8,0x4c, 0xff,0xe6,0x51,
	    0xff,0xf4,0x56, 0xff,0xf9,0x77, 0xff,0xff,0x98, 0xff,0xff,0x98,
	    /* Orange */
	    0x45,0x19,0x04, 0x72,0x1e,0x11, 0x9f,0x24,0x1e, 0xb3,0x3a,0x20,
	    0xc8,0x51,0x22, 0xe3,0x69,0x20, 0xff,0x81,0x1e, 0xff,0x8c,0x25,
	    0xff,0x98,0x2c, 0xff,0xae,0x38, 0xff,0xc5,0x45, 0xff,0xc5,0x59,
	    0xff,0xc6,0x6d, 0xff,0xd5,0x87, 0xff,0xe4,0xa1, 0xff,0xe4,0xa1,
	    /* Red-Orange */
	    0x4a,0x17,0x04, 0x7e,0x1a,0x0d, 0xb2,0x1d,0x17, 0xc8,0x21,0x19,
	    0xdf,0x25,0x1c, 0xec,0x3b,0x38, 0xfa,0x52,0x55, 0xfc,0x61,0x61,
	    0xff,0x70,0x6e, 0xff,0x7f,0x7e, 0xff,0x8f,0x8f, 0xff,0x9d,0x9e,
	    0xff,0xab,0xad, 0xff,0xb9,0xbd, 0xff,0xc7,0xce, 0xff,0xc7,0xce,
	    /* Pink */
	    0x05,0x05,0x68, 0x3b,0x13,0x6d, 0x71,0x22,0x72, 0x8b,0x2a,0x8c,
	    0xa5,0x32,0xa6, 0xb9,0x38,0xba, 0xcd,0x3e,0xcf, 0xdb,0x47,0xdd,
	    0xea,0x51,0xeb, 0xf4,0x5f,0xf5, 0xfe,0x6d,0xff, 0xfe,0x7a,0xfd,
	    0xff,0x87,0xfb, 0xff,0x95,0xfd, 0xff,0xa4,0xff, 0xff,0xa4,0xff,
	    /* Purple */
	    0x28,0x04,0x79, 0x40,0x09,0x84, 0x59,0x0f,0x90, 0x70,0x24,0x9d,
	    0x88,0x39,0xaa, 0xa4,0x41,0xc3, 0xc0,0x4a,0xdc, 0xd0,0x54,0xed,
	    0xe0,0x5e,0xff, 0xe9,0x6d,0xff, 0xf2,0x7c,0xff, 0xf8,0x8a,0xff,
	    0xff,0x98,0xff, 0xfe,0xa1,0xff, 0xfe,0xab,0xff, 0xfe,0xab,0xff,
	    /* Purple-Blue */
	    0x35,0x08,0x8a, 0x42,0x0a,0xad, 0x50,0x0c,0xd0, 0x64,0x28,0xd0,
	    0x79,0x45,0xd0, 0x8d,0x4b,0xd4, 0xa2,0x51,0xd9, 0xb0,0x58,0xec,
	    0xbe,0x60,0xff, 0xc5,0x6b,0xff, 0xcc,0x77,0xff, 0xd1,0x83,0xff,
	    0xd7,0x90,0xff, 0xdb,0x9d,0xff, 0xdf,0xaa,0xff, 0xdf,0xaa,0xff,
	    /* Blue 1 */
	    0x05,0x1e,0x81, 0x06,0x26,0xa5, 0x08,0x2f,0xca, 0x26,0x3d,0xd4,
	    0x44,0x4c,0xde, 0x4f,0x5a,0xee, 0x5a,0x68,0xff, 0x65,0x75,0xff,
	    0x71,0x83,0xff, 0x80,0x91,0xff, 0x90,0xa0,0xff, 0x97,0xa9,0xff,
	    0x9f,0xb2,0xff, 0xaf,0xbe,0xff, 0xc0,0xcb,0xff, 0xc0,0xcb,0xff,
	    /* Blue 2 */
	    0x0c,0x04,0x8b, 0x22,0x18,0xa0, 0x38,0x2d,0xb5, 0x48,0x3e,0xc7,
	    0x58,0x4f,0xda, 0x61,0x59,0xec, 0x6b,0x64,0xff, 0x7a,0x74,0xff,
	    0x8a,0x84,0xff, 0x91,0x8e,0xff, 0x99,0x98,0xff, 0xa5,0xa3,0xff,
	    0xb1,0xae,0xff, 0xb8,0xb8,0xff, 0xc0,0xc2,0xff, 0xc0,0xc2,0xff,
	    /* Light-Blue */
	    0x1d,0x29,0x5a, 0x1d,0x38,0x76, 0x1d,0x48,0x92, 0x1c,0x5c,0xac,
	    0x1c,0x71,0xc6, 0x32,0x86,0xcf, 0x48,0x9b,0xd9, 0x4e,0xa8,0xec,
	    0x55,0xb6,0xff, 0x70,0xc7,0xff, 0x8c,0xd8,0xff, 0x93,0xdb,0xff,
	    0x9b,0xdf,0xff, 0xaf,0xe4,0xff, 0xc3,0xe9,0xff, 0xc3,0xe9,0xff,
	    /* Turquoise */
	    0x2f,0x43,0x02, 0x39,0x52,0x02, 0x44,0x61,0x03, 0x41,0x7a,0x12,
	    0x3e,0x94,0x21, 0x4a,0x9f,0x2e, 0x57,0xab,0x3b, 0x5c,0xbd,0x55,
	    0x61,0xd0,0x70, 0x69,0xe2,0x7a, 0x72,0xf5,0x84, 0x7c,0xfa,0x8d,
	    0x87,0xff,0x97, 0x9a,0xff,0xa6, 0xad,0xff,0xb6, 0xad,0xff,0xb6,
	    /* Green-Blue */
	    0x0a,0x41,0x08, 0x0d,0x54,0x0a, 0x10,0x68,0x0d, 0x13,0x7d,0x0f,
	    0x16,0x92,0x12, 0x19,0xa5,0x14, 0x1c,0xb9,0x17, 0x1e,0xc9,0x19,
	    0x21,0xd9,0x1b, 0x47,0xe4,0x2d, 0x6e,0xf0,0x40, 0x78,0xf7,0x4d,
	    0x83,0xff,0x5b, 0x9a,0xff,0x7a, 0xb2,0xff,0x9a, 0xb2,0xff,0x9a,
	    /* Green */
	    0x04,0x41,0x0b, 0x05,0x53,0x0e, 0x06,0x66,0x11, 0x07,0x77,0x14,
	    0x08,0x88,0x17, 0x09,0x9b,0x1a, 0x0b,0xaf,0x1d, 0x48,0xc4,0x1f,
	    0x86,0xd9,0x22, 0x8f,0xe9,0x24, 0x99,0xf9,0x27, 0xa8,0xfc,0x41,
	    0xb7,0xff,0x5b, 0xc9,0xff,0x6e, 0xdc,0xff,0x81, 0xdc,0xff,0x81,
	    /* Yellow-Green */
	    0x02,0x35,0x0f, 0x07,0x3f,0x15, 0x0c,0x4a,0x1c, 0x2d,0x5f,0x1e,
	    0x4f,0x74,0x20, 0x59,0x83,0x24, 0x64,0x92,0x28, 0x82,0xa1,0x2e,
	    0xa1,0xb0,0x34, 0xa9,0xc1,0x3a, 0xb2,0xd2,0x41, 0xc4,0xd9,0x45,
	    0xd6,0xe1,0x49, 0xe4,0xf0,0x4e, 0xf2,0xff,0x53, 0xf2,0xff,0x53,
	    /* Orange-Green */
	    0x26,0x30,0x01, 0x24,0x38,0x03, 0x23,0x40,0x05, 0x51,0x54,0x1b,
	    0x80,0x69,0x31, 0x97,0x81,0x35, 0xaf,0x99,0x3a, 0xc2,0xa7,0x3e,
	    0xd5,0xb5,0x43, 0xdb,0xc0,0x3d, 0xe1,0xcb,0x38, 0xe2,0xd8,0x36,
	    0xe3,0xe5,0x34, 0xef,0xf2,0x58, 0xfb,0xff,0x7d, 0xfb,0xff,0x7d,
	    /* Light-Orange */
	    0x40,0x1a,0x02, 0x58,0x1f,0x05, 0x70,0x24,0x08, 0x8d,0x3a,0x13,
	    0xab,0x51,0x1f, 0xb5,0x64,0x27, 0xbf,0x77,0x30, 0xd0,0x85,0x3a,
	    0xe1,0x93,0x44, 0xed,0xa0,0x4e, 0xf9,0xad,0x58, 0xfc,0xb7,0x5c,
	    0xff,0xc1,0x60, 0xff,0xc6,0x71, 0xff,0xcb,0x83, 0xff,0xcb,0x83
	};
	
	static unsigned short colortable[] =
	{
	    0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
	    0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f,
	    0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x2a,0x2b,0x2c,0x2d,0x2e,0x2f,
	    0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x3a,0x3b,0x3c,0x3d,0x3e,0x3f,
	    0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,0x49,0x4a,0x4b,0x4c,0x4d,0x4e,0x4f,
	    0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x58,0x59,0x5a,0x5b,0x5c,0x5d,0x5e,0x5f,
	    0x60,0x61,0x62,0x63,0x64,0x65,0x66,0x67,0x68,0x69,0x6a,0x6b,0x6c,0x6d,0x6e,0x6f,
	    0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77,0x78,0x79,0x7a,0x7b,0x7c,0x7d,0x7e,0x7f,
	    0x80,0x81,0x82,0x83,0x84,0x85,0x86,0x87,0x88,0x89,0x8a,0x8b,0x8c,0x8d,0x8e,0x8f,
	    0x90,0x91,0x92,0x93,0x94,0x95,0x96,0x97,0x98,0x99,0x9a,0x9b,0x9c,0x9d,0x9e,0x9f,
	    0xa0,0xa1,0xa2,0xa3,0xa4,0xa5,0xa6,0xa7,0xa8,0xa9,0xaa,0xab,0xac,0xad,0xae,0xaf,
	    0xb0,0xb1,0xb2,0xb3,0xb4,0xb5,0xb6,0xb7,0xb8,0xb9,0xba,0xbb,0xbc,0xbd,0xbe,0xbf,
	    0xc0,0xc1,0xc2,0xc3,0xc4,0xc5,0xc6,0xc7,0xc8,0xc9,0xca,0xcb,0xcc,0xcd,0xce,0xcf,
	    0xd0,0xd1,0xd2,0xd3,0xd4,0xd5,0xd6,0xd7,0xd8,0xd9,0xda,0xdb,0xdc,0xdd,0xde,0xdf,
	    0xe0,0xe1,0xe2,0xe3,0xe4,0xe5,0xe6,0xe7,0xe8,0xe9,0xea,0xeb,0xec,0xed,0xee,0xef,
	    0xf0,0xf1,0xf2,0xf3,0xf4,0xf5,0xf6,0xf7,0xf8,0xf9,0xfa,0xfb,0xfc,0xfd,0xfe,0xff
	};
	
	
	/* Initialise the palette */
	static void a7800_init_palette(unsigned char *sys_palette, unsigned short *sys_colortable,const unsigned char *color_prom)
	{
	    memcpy(sys_palette,palette,sizeof(palette));
	    memcpy(sys_colortable,colortable,sizeof(colortable));
	}
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
	MEMORY_END   /* end of array */
	
	static struct TIAinterface tia_interface =
	new GfxDecodeInfo(
	    31400,
	    100,
	    TIA_DEFAULT_GAIN,
	);
	
	
	static struct POKEYinterface pokey_interface = new GfxDecodeInfo(
	    1,
	    1790000,
	    { 100 },
	);
	
	
	static struct MachineDriver machine_driver_a7800 =
	new GfxDecodeInfo(
	    /* basic machine hardware */
	    {
	        {
	            CPU_M6502,
	            1790000,        /* 1.79Mhz (note: The clock switches to 1.19Mhz */
	                            /* when the TIA or RIOT are accessed) */
	            readmem,writemem,0,0,
	            a7800_interrupt,262
	        }
	    },
	    60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
	    1,
	    a7800_init_machine, /* init_machine */
	    a7800_stop_machine, /* stop_machine */
	
	    /* video hardware */
	    640,263, {0,319,35,35+204}, // 35+199
	    gfxdecodeinfo,
	    sizeof(palette) / sizeof(palette[0]) / 3,
	    sizeof(colortable) / sizeof(colortable[0]),
	    a7800_init_palette,
	
	    VIDEO_TYPE_RASTER,
	    0,
	    a7800_vh_start,
	    a7800_vh_stop,
	    a7800_vh_screenrefresh,
	
	    /* sound hardware */
	    0,0,0,0,
	    {
	        {
	            SOUND_TIA,
	            tia_interface
	        },
	        {
	            SOUND_POKEY,
	            pokey_interface
	        }
	    }
	
	);
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	ROM_START (a7800)
	    ROM_REGION(0x30000,REGION_CPU1,0);
	    ROM_LOAD ("7800.rom", 0xf000, 0x1000, 0x649913e5);
	//      ROM_LOAD ("7800a.rom", 0xc000, 0x4000, 0x649913e5);
	
	ROM_END(); }}; 
	
	static const struct IODevice io_a7800[] = new GfxDecodeInfo(
	    {
	        IO_CARTSLOT,        /* type */
	        1,                  /* count */
	        "a78\0",            /* file extensions */
	        IO_RESET_ALL,       /* reset if file changed */
	        0,
	        a7800_init_cart,	/* init */
	        a7800_exit_rom,		/* exit */
	        NULL,               /* info */
	        NULL,               /* open */
	        NULL,               /* close */
	        NULL,               /* status */
	        NULL,               /* seek */
	        NULL,               /* tell */
	        NULL,               /* input */
	        NULL,               /* output */
	        NULL,               /* input_chunk */
	        NULL,               /* output_chunk */
	        a7800_partialcrc,   /* partial CRC */
	    },
	    { IO_END }
	);
	
	/*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	CONS( 1986, a7800,    0,        a7800,    a7800,    0,        "Atari",  "Atari 7800" )
	
	#ifdef RUNTIME_LOADER
	extern void a7800_runtime_loader_init(void)
	new GfxDecodeInfo(
		int i;
		for (i=0; drivers[i]; i++) {
			if ( strcmp(drivers[i]->name,"a7800")==0) drivers[i]=driver_a7800;
		}
	)
	#endif
}
