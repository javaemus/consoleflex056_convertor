/* machine/ti85.c */

extern UINT8 ti85_LCD_memory_base;
extern UINT8 ti85_LCD_contrast;
extern UINT8 ti85_LCD_status;
extern UINT8 ti85_timer_interrupt_mask;
extern void ti81_init_machine (void);
extern void ti81_stop_machine (void);
extern void ti85_init_machine (void);
extern void ti85_stop_machine (void);
extern void ti86_init_machine (void);
extern void ti86_stop_machine (void);
extern int ti85_load_snap (int);
extern void ti85_exit_snap (int);
extern int ti85_serial_init (int);
extern void ti85_serial_exit (int);
extern void ti81_nvram_handler (void *, int);
extern void ti85_nvram_handler (void *, int);
extern void ti86_nvram_handler (void *, int);
extern public static WriteHandlerPtr ti81_port_0007_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static ReadHandlerPtr ti85_port_0000_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0001_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0002_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0003_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0004_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0005_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0006_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti85_port_0007_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti86_port_0005_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr ti86_port_0006_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static WriteHandlerPtr ti85_port_0000_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0001_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0002_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0003_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0004_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0005_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0006_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti85_port_0007_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti86_port_0005_w = new WriteHandlerPtr() {public void handler(int offset, int data);
extern public static WriteHandlerPtr ti86_port_0006_w = new WriteHandlerPtr() {public void handler(int offset, int data);

/* vidhrdw/ti85.c */

extern int ti85_vh_start (void);
extern void ti85_vh_stop (void);
extern void ti85_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
extern unsigned char ti85_palette[32*7][3];
extern unsigned short ti85_colortable[32][7];
extern void ti85_init_palette (unsigned char *, unsigned short *, const unsigned char *);
												
