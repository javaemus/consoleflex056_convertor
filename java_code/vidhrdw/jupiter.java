/***************************************************************************

  jupiter.c

  Functions to emulate the video hardware of the Jupiter Ace.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class jupiter
{
	
	unsigned char *jupiter_charram;
	size_t jupiter_charram_size;
	
	int jupiter_vh_start (void)
	{
		if( generic_vh_start() )
			return 1;
	    return 0;
	}
	
	void jupiter_vh_stop (void)
	{
		generic_vh_stop();
	}
	
	public static WriteHandlerPtr jupiter_vh_charram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int chr = offset / 8, offs;
	
	    if( data == jupiter_charram[offset] )
			return; /* no change */
	
	    jupiter_charram[offset] = data;
	
	    /* decode character graphics again */
		decodechar(Machine->gfx[0], chr, jupiter_charram, &jupiter_charlayout);
	
	    /* mark all visible characters with that code dirty */
	    for( offs = 0; offs < videoram_size; offs++ )
		{
			if( videoram[offs] == chr )
				dirtybuffer[offs] = 1;
		}
	} };
	
	void jupiter_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
	
		int offs;
	
		/* do we need a full refresh? */
	    if( full_refresh )
			memset(dirtybuffer, 1, videoram_size);
	
	    for( offs = 0; offs < videoram_size; offs++ )
		{
	        if( dirtybuffer[offs]  )
			{
	            int code = videoram[offs];
				int sx, sy;
	
				sy = (offs / 32) * 8;
				sx = (offs % 32) * 8;
	
				drawgfx(bitmap, Machine->gfx[0], code & 0x7f, (code & 0x80) ? 1 : 0, 0,0, sx,sy,
					&Machine->visible_area, TRANSPARENCY_NONE, 0);
	
	            dirtybuffer[offs] = 0;
			}
		}
	}
}
