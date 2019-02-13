/******************************************************************************
 *	Sharp MZ700
 *
 *	variables and function prototypes
 *
 *	Juergen Buchmueller <pullmoll@t-online.de>, Jul 2000
 *
 *  Reference: http://sharpmz.computingmuseum.com
 *
 ******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class mz700H
{
	
	/* from src/mess/machine/mz700.c */
	extern void init_mz700(void);
	extern void mz700_init_machine(void);
	extern void mz700_stop_machine(void);
	
	extern int mz700_cassette_init(int id);
	extern void mz700_cassette_exit(int id);
	
	extern int mz700_interrupt(void);
	
	extern READ_HANDLER ( mz700_mmio_r );
	extern WRITE_HANDLER ( mz700_mmio_w );
	extern WRITE_HANDLER ( mz700_bank_w );
	
	/* from src/mess/vidhrdw/mz700.c */
	
	extern char mz700_frame_message[64+1];
	extern int mz700_frame_time;
	
	extern void mz700_init_colors (unsigned char *palette, unsigned short *colortable, const unsigned char *color_prom);
	extern int mz700_vh_start (void);
	extern void mz700_vh_stop (void);
	extern void mz700_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
	
	/******************************************************************************
	 *	Sharp MZ800
	 *
	 ******************************************************************************/
	extern extern extern extern 
	extern extern extern extern extern extern WRITE_HANDLER ( mz800_bank_w );
	extern extern extern 
	extern extern extern extern extern 
	extern void init_mz800(void);
}
