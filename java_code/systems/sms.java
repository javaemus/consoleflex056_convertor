/******************************************************************************
 Contributors:

	Marat Fayzullin (MG source)
	Charles Mac Donald
	Mathis Rosenhauer
	Brad Oliver

 To do:

 - Version bits for Game Gear (bits 6-5 of port 00)
 - PSG control for Game Gear (needs custom SN76489 with stereo output for each channel)
 - SIO interface for Game Gear (needs netplay, I guess)
 - TMS9928A support for 'f16ffight.sms'
 - Lock first 1K of ROM (only game utilizing this hasn't been dumped)
 - SMS lightgun support
 - On-cart RAM support
 - Pause key - certainly there's an effective way to handle this

 The Game Gear SIO and PSG hardware are not emulated but have some
 placeholders in 'machine/sms.c'

 ******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package systems;

public class sms
{
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3FFF, MRA_RAM ), /* ROM bank #1 */
		new Memory_ReadAddress( 0x4000, 0x7FFF, MRA_RAM ), /* ROM bank #2 */
		new Memory_ReadAddress( 0x8000, 0xBFFF, MRA_RAM ), /* ROM bank #3 / On-cart RAM */
		new Memory_ReadAddress( 0xC000, 0xDFFF, MRA_RAM ), /* RAM */
		new Memory_ReadAddress( 0xE000, 0xFFFF, MRA_RAM ), /* RAM (mirror) */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	extern void sms_system_w(int offset, int data);
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3FFF, MWA_NOP		), /* ROM bank #1 */
		new Memory_WriteAddress( 0x4000, 0x7FFF, MWA_NOP		), /* ROM bank #2 */
		new Memory_WriteAddress( 0x8000, 0xBFFF, sms_cartram_w ), /* ROM bank #3 / On-cart RAM */
		new Memory_WriteAddress( 0xC000, 0xDFFF, sms_ram_w 	), /* RAM */
		new Memory_WriteAddress( 0xE000, 0xFFFB, sms_ram_w 	), /* RAM (mirror) */
		new Memory_WriteAddress( 0xFFFC, 0xFFFF, sms_mapper_w	), /* Bankswitch control */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sms_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0xBE, 0xBE, sms_vdp_data_r ),
		new IO_ReadPort( 0xBD, 0xBD, sms_vdp_ctrl_r ),
		new IO_ReadPort( 0xBF, 0xBF, sms_vdp_ctrl_r ),
		new IO_ReadPort( 0x7E, 0x7F, sms_vdp_curline_r ),
		new IO_ReadPort( 0xF2, 0xF2, sms_fm_detect_r ),
		new IO_ReadPort( 0xDC, 0xDC, input_port_0_r ),
		new IO_ReadPort( 0xC0, 0xC0, input_port_0_r ),
		new IO_ReadPort( 0xDD, 0xDD, sms_version_r ),
		new IO_ReadPort( 0xC1, 0xC1, sms_version_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sms_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0xBE, 0xBE, sms_vdp_data_w ),
		new IO_WritePort( 0xBD, 0xBD, sms_vdp_ctrl_w ),
		new IO_WritePort( 0xBF, 0xBF, sms_vdp_ctrl_w ),
		new IO_WritePort( 0xF2, 0xF2, sms_fm_detect_w ),
		new IO_WritePort( 0x3F, 0x3F, sms_version_w ),
		new IO_WritePort( 0xFF, 0xFF, IOWP_NOP ),
		new IO_WritePort( 0x7F, 0x7F, SN76496_0_w ),
		new IO_WritePort( 0xF0, 0xF0, YM2413_register_port_0_w ),
		new IO_WritePort( 0xF1, 0xF1, YM2413_data_port_0_w	),
		new IO_WritePort( 0x3E, 0x3E, IOWP_NOP ), /* Unknown */
		new IO_WritePort( 0xDE, 0xDF, IOWP_NOP ), /* Unknown */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort gg_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0xBE, 0xBE, sms_vdp_data_r ),
		new IO_ReadPort( 0xBD, 0xBD, sms_vdp_ctrl_r ),
		new IO_ReadPort( 0xBF, 0xBF, sms_vdp_ctrl_r ),
		new IO_ReadPort( 0x7E, 0x7F, sms_vdp_curline_r ),
		new IO_ReadPort( 0xDC, 0xDC, input_port_0_r ),
		new IO_ReadPort( 0xC0, 0xC0, input_port_0_r ),
		new IO_ReadPort( 0xDD, 0xDD, input_port_1_r ),
		new IO_ReadPort( 0xC1, 0xC1, input_port_1_r ),
		new IO_ReadPort( 0x00, 0x00, input_port_2_r ),
		new IO_ReadPort( 0x01, 0x05, gg_sio_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort gg_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0xBE, 0xBE, sms_vdp_data_w ),
		new IO_WritePort( 0xBD, 0xBD, sms_vdp_ctrl_w ),
		new IO_WritePort( 0xBF, 0xBF, sms_vdp_ctrl_w ),
		new IO_WritePort( 0x7F, 0x7F, SN76496_0_w ),
		new IO_WritePort( 0x00, 0x05, gg_sio_w ),
		new IO_WritePort( 0x06, 0x06, gg_psg_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortPtr input_ports_sms = new InputPortPtr(){ public void handler() { 
	
		PORT_START	/* IN0 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 				  | IPF_PLAYER1 );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 				  | IPF_PLAYER1 );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START	/* IN1 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 				  | IPF_PLAYER2 );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2 				  | IPF_PLAYER2 );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );/* Software Reset bit */
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START	/* IN2 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_UNUSED);
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_START1 );/* Game Gear START */
	
		PORT_START	/* DSW - fake */
	
		PORT_DIPNAME( 0x01, 0x00, "YM2413 Detect");
		PORT_DIPSETTING( 0x00, DEF_STR( "Off") );
		PORT_DIPSETTING( 0x01, DEF_STR( "On") );
	
		PORT_DIPNAME( 0x02, 0x00, "Version Type");
		PORT_DIPSETTING( 0x00, "Overseas (Europe); )
		PORT_DIPSETTING( 0x02, "Domestic (Japan); )
	
	INPUT_PORTS_END(); }}; 
	
	static SN76496interface sn76496_interface = new SN76496interface
	(
		1,				/* 1 chip */
		new int[] {4194304},		  /* 4.194304 MHz */
		new int[] { 100 }
	);
	
	static struct YM2413interface ym2413_interface=
	{
		1,
		8000000,
		{ 50 },
	};
	
	
	static struct MachineDriver machine_driver_sms =
	{
		/* basic machine hardware */
		{
			{
				CPU_Z80,
				3579545,
				readmem,writemem,sms_readport,sms_writeport,
				sms_vdp_interrupt, 262,
				0, 0
			}
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,	/* frames per second, vblank duration */
		1,
		sms_init_machine, /* init_machine */
		0, /* stop_machine */
	
		/* video hardware */
		32*8, 28*8, { 0*8, 32*8-1, 0*8, 24*8-1 },
		0,
		32, 32,
		0,
	
		VIDEO_TYPE_RASTER,
		0,
		sms_vdp_start,
		sms_vdp_stop,
		sms_vdp_refresh,
	
		/* sound hardware */
		0,0,0,0,
		{
			{
				SOUND_SN76496,
				&sn76496_interface
			},
			{
				SOUND_YM2413,
				&ym2413_interface
			}
		}
	};
	
	static struct MachineDriver machine_driver_gamegear =
	{
		/* basic machine hardware */
		{
			{
				CPU_Z80,
				3597545,
				readmem,writemem,gg_readport,gg_writeport,
				sms_vdp_interrupt, 262,
				0, 0
			}
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,	/* frames per second, vblank duration */
		1,
		sms_init_machine, /* init_machine */
		0, /* stop_machine */
	
		/* video hardware */
		32*8, 28*8, { 6*8, 26*8-1, 3*8, 21*8-1 },
		0,
		32, 32,
		0,
	
		VIDEO_TYPE_RASTER,
		0,
		gamegear_vdp_start,
		sms_vdp_stop,
		sms_vdp_refresh,
	
		/* sound hardware */
		0,0,0,0,
		{
			{
				SOUND_SN76496,
				&sn76496_interface
			}
		}
	};
	
	static RomLoadPtr rom_sms = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(SMS_ROM_MAXSIZE, REGION_CPU1,0);
	/*	ROM_LOAD ("bios.rom", 0x0000, 0x2000, 0x5AD6EDAC);*/
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gamegear = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(SMS_ROM_MAXSIZE, REGION_CPU1,0);
	ROM_END(); }}; 
	
	static const struct IODevice io_sms[] = {
		{
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"sms\0",            /* file extensions */
			IO_RESET_ALL,		/* reset if file changed */
			0,
			sms_init_cart,		/* init */
			NULL,				/* exit */
			NULL,				/* info */
			NULL,				/* open */
			NULL,				/* close */
			NULL,				/* status */
			NULL,				/* seek */
			NULL,				/* input */
			NULL,				/* output */
			NULL,				/* input_chunk */
			NULL				/* output_chunk */
		},
		{ IO_END }
	};
	
	static const struct IODevice io_gamegear[] = {
		{
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"gg\0",             /* file extensions */
			IO_RESET_ALL,		/* reset if file changed */
			0,
			sms_init_cart,		/* init */
			NULL,				/* exit */
			NULL,				/* info */
			NULL,				/* open */
			NULL,				/* close */
			NULL,				/* status */
			NULL,				/* seek */
			NULL,				/* tell */
			NULL,				/* input */
			NULL,				/* output */
			NULL,				/* input_chunk */
			NULL				/* output_chunk */
		},
		{ IO_END }
	};
	
	/*	  YEAR	NAME	  PARENT	MACHINE   INPUT 	INIT	  COMPANY	FULLNAME */
	CONS( 1987, sms,	  0,		sms,	  sms,		0,		  "Sega",   "Master System" )
	CONS( 1990, gamegear, 0,		gamegear, sms,		0,		  "Sega",   "Game Gear" )
	
}
