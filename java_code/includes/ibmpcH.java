/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class ibmpcH
{
	
	extern ppi8255_interface pc_ppi8255_interface;
	
	void pc_rtc_init(void);
	
	extern WRITE_HANDLER ( pc_EXP_w );
	extern READ_HANDLER ( pc_EXP_r );
	
	
}
