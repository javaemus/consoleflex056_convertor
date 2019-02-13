/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class ibmpcH
{
	
	extern ppi8255_interface pc_ppi8255_interface;
	
	WRITE_HANDLER( pc_rtc_w );
	READ_HANDLER( pc_rtc_r );
	void pc_rtc_init(void);
	
	extern WRITE_HANDLER ( pc_EXP_w );
	extern READ_HANDLER ( pc_EXP_r );
	
	
}
