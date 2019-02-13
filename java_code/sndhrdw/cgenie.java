/***************************************************************************

  cgenie.c

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class cgenie
{
	
	
	static int control_port;
	
	READ_HANDLER( cgenie_sh_control_port_r )
	{
		return control_port;
	}
	
	READ_HANDLER( cgenie_sh_data_port_r )
	{
		return AY8910_read_port_0_r(offset);
	}
	
	WRITE_HANDLER( cgenie_sh_control_port_w )
	{
		control_port = data;
		AY8910_control_port_0_w(offset, data);
	}
	
	WRITE_HANDLER( cgenie_sh_data_port_w )
	{
		AY8910_write_port_0_w(offset, data);
	}
	
}
