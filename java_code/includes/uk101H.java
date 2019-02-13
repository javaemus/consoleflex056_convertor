/* machine/uk101.c */

extern void uk101_init_machine(void);
extern void uk101_stop_machine(void);
extern extern extern extern extern int uk101_init_cassette(int id);
extern void uk101_exit_cassette(int id);

/* vidhrdw/uk101.c */

extern int uk101_vh_start (void);
extern void uk101_vh_stop (void);
extern void uk101_vh_screenrefresh (struct mame_bitmap *bitmap,
												int full_refresh);
extern void superbrd_vh_screenrefresh (struct mame_bitmap *bitmap,
												int full_refresh);

