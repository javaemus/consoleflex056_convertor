/* machine/nascom1.c */

extern void nascom1_init_machine (void);
extern void nascom1_stop_machine (void);
extern int nascom1_init_cassette (int id);
extern void nascom1_exit_cassette (int id);
extern int nascom1_read_cassette (void);
extern int nascom1_init_cartridge (int id);
extern public static ReadHandlerPtr nascom1_port_00_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr nascom1_port_01_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr nascom1_port_02_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static WriteHandlerPtr nascom1_port_00_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr nascom1_port_01_w = new WriteHandlerPtr() {public void handler(int offset, int data);

/* vidhrdw/nascom1.c */

extern int nascom1_vh_start (void);
extern void nascom1_vh_stop (void);
extern void nascom1_vh_screenrefresh (struct mame_bitmap *bitmap,
												int full_refresh);
extern void nascom2_vh_screenrefresh (struct mame_bitmap *bitmap,
												int full_refresh);
/* systems/nascom1.c */

