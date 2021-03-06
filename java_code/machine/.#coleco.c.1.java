/***************************************************************************

  coleco.c

  Machine file to handle emulation of the Colecovision.

  TODO:
	- Extra controller support
***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class .#coleco.c.1
{
	
	static int JoyMode=0;
	int JoyStat[2];
	
	static int coleco_verify_cart (UINT8 *cartdata)
	{
		int retval = IMAGE_VERIFY_FAIL;
	
		/* Verify the file is in Colecovision format */
		if ((cartdata[0] == 0xAA) && (cartdata[1] == 0x55))
			retval = IMAGE_VERIFY_PASS;
		if ((cartdata[0] == 0x55) && (cartdata[1] == 0xAA))
			retval = IMAGE_VERIFY_PASS;
	
		return retval;
	}
	
	int coleco_init_cart (int id)
	{
	    void *cartfile = NULL;
		UINT8 *cartdata;
		int init_result = INIT_FAIL;
	
		/* A cartridge isn't strictly mandatory for the coleco */
		if (!device_filename(IO_CARTSLOT,id) || !strlen(device_filename(IO_CARTSLOT,id) ))
		{
			logerror("Coleco - warning: no cartridge specified!\n");
			return INIT_PASS;
		}
	
		/* Load the specified Cartridge File */
		if (!(cartfile = image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, 0)))
		{
			logerror("Coleco - Unable to locate cartridge: %s\n",device_filename(IO_CARTSLOT,id) );
			return INIT_FAIL;
		}
	
		/* All seems OK */
		cartdata = memory_region(REGION_CPU1) + 0x8000;
		osd_fread (cartfile, cartdata, 0x8000);
	
		/* Verify the cartridge image */
		if (coleco_verify_cart(cartdata) == IMAGE_VERIFY_FAIL)
		{
			logerror("Coleco - Image verify FAIL\n");
			init_result = INIT_FAIL;
		}
		else
		{
			logerror("Coleco - Image verify PASS\n");
			init_result = INIT_PASS;
		}
		osd_fclose (cartfile);
		return init_result;
	}
	
	/***************************************************************************
	
	  The spinner works as follows. There are two spinners, each connected
	  to one controller. Bit 4 and 5 show the state of the contoller:
	
	       5 4
	       1 1 No movement
	       1 0 left
	       0 1 Unknown (?)
	       0 0 right
	
	  If it's moving, the controller generates interrupts (not NMI). I'm not
	  sure how long they last, as regular interrupts on the Z80 have a state
	  (as in, HOLD_LINE or CLEAR_LINE.)
	
	***************************************************************************/
	
	READ_HANDLER ( coleco_paddle_r )
	{
	
		/* Player 1 */
		if ((offset & 0x02)==0)
		{
			/* Keypad and fire 1 */
			if (JoyMode==0)
			{
				int inport0,inport1,data;
	
				inport0 = input_port_0_r(0);
				inport1 = input_port_1_r(0);
	
				if		((inport0 & 0x01) == 0)		/* 0 */
					data = 0x0A;
				else if	((inport0 & 0x02) == 0)		/* 1 */
					data = 0x0D;
				else if ((inport0 & 0x04) == 0)		/* 2 */
					data = 0x07;
				else if ((inport0 & 0x08) == 0)		/* 3 */
					data = 0x0C;
				else if ((inport0 & 0x10) == 0)		/* 4 */
					data = 0x02;
				else if ((inport0 & 0x20) == 0)		/* 5 */
					data = 0x03;
				else if ((inport0 & 0x40) == 0)		/* 6 */
					data = 0x0E;
				else if ((inport0 & 0x80) == 0)		/* 7 */
					data = 0x05;
				else if ((inport1 & 0x01) == 0)		/* 8 */
					data = 0x01;
				else if ((inport1 & 0x02) == 0)		/* 9 */
					data = 0x0B;
				else if ((inport1 & 0x04) == 0)		/* # */
					data = 0x06;
				else if ((inport1 & 0x08) == 0)		/* . */
					data = 0x09;
				else
					data = 0x0F;
	
				return (inport1 & 0x70) | (data);
	
			}
			/* Joystick and fire 2*/
			else
			{
				int data = input_port_2_r(0);
				data &= ~0x30;
				switch (JoyStat[0]) {
				case 0: data |= 0x30; break;
				case 1: data |= 0x20; break;
				case -1: break;
				}
				cpu_set_irq_line (0, 0, CLEAR_LINE);
	
				return data | 0x80;
			}
	
		}
		/* Player 2 */
		else
		{
			/* Keypad and fire 1 */
			if (JoyMode==0)
			{
				int inport3,inport4,data;
	
				inport3 = input_port_3_r(0);
				inport4 = input_port_4_r(0);
	
				if		((inport3 & 0x01) == 0)		/* 0 */
					data = 0x0A;
				else if	((inport3 & 0x02) == 0)		/* 1 */
					data = 0x0D;
				else if ((inport3 & 0x04) == 0)		/* 2 */
					data = 0x07;
				else if ((inport3 & 0x08) == 0)		/* 3 */
					data = 0x0C;
				else if ((inport3 & 0x10) == 0)		/* 4 */
					data = 0x02;
				else if ((inport3 & 0x20) == 0)		/* 5 */
					data = 0x03;
				else if ((inport3 & 0x40) == 0)		/* 6 */
					data = 0x0E;
				else if ((inport3 & 0x80) == 0)		/* 7 */
					data = 0x05;
				else if ((inport4 & 0x01) == 0)		/* 8 */
					data = 0x01;
				else if ((inport4 & 0x02) == 0)		/* 9 */
					data = 0x0B;
				else if ((inport4 & 0x04) == 0)		/* # */
					data = 0x06;
				else if ((inport4 & 0x08) == 0)		/* . */
					data = 0x09;
				else
					data = 0x0F;
	
				return (inport4 & 0x70) | (data);
	
			}
			/* Joystick and fire 2*/
			else
			{
				int data = input_port_5_r(0);
				data &= ~0x30;
				switch (JoyStat[1]) {
				case 0: data |= 0x30; break;
				case 1: data |= 0x20; break;
				case -1: break;
				}
				cpu_set_irq_line (0, 0, CLEAR_LINE);
	
				return data | 0x80;
			}
		}
	
	}
	
	
	WRITE_HANDLER ( coleco_paddle_toggle_off )
	{
		JoyMode=0;
	    return;
	}
	
	WRITE_HANDLER ( coleco_paddle_toggle_on )
	{
		JoyMode=1;
	    return;
	}
	
}
