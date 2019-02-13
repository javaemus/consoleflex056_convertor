#ifndef TRS80_H
#define TRS80_H

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class trs80H
{
	
	#define TRS80_FONT_W 6
	#define TRS80_FONT_H 12
	
	extern UINT8 trs80_port_ff;
	
	
	extern int trs80_cas_init(int id);
	extern void trs80_cas_exit(int id);
	
	extern int trs80_cmd_init(int id);
	extern void trs80_cmd_exit(int id);
	
	extern int trs80_floppy_init(int id);
	
	extern int trs80_vh_start(void);
	extern void trs80_vh_stop(void);
	extern void trs80_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	
	extern void trs80_sh_sound_init(const char * gamename);
	
	extern void init_trs80(void);
	extern void trs80_init_machine(void);
	extern void trs80_shutdown_machine(void);
	
	extern WRITE_HANDLER ( trs80_port_ff_w );
	extern READ_HANDLER ( trs80_port_ff_r );
	extern READ_HANDLER ( trs80_port_xx_r );
	
	extern int trs80_timer_interrupt(void);
	extern int trs80_frame_interrupt(void);
	
	extern READ_HANDLER ( trs80_irq_status_r );
	extern WRITE_HANDLER ( trs80_irq_mask_w );
	
	extern READ_HANDLER ( trs80_printer_r );
	extern WRITE_HANDLER ( trs80_printer_w );
	
	extern WRITE_HANDLER ( trs80_motor_w );
	
	extern READ_HANDLER ( trs80_keyboard_r );
	
	#endif	/* TRS80_H */
	
}
