/*
NGramJ - n-gram based text classification
Copyright (C) 2001 Frank S. Nestel (frank at spieleck.de)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published 
by the Free Software Foundation; either version 2.1 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program (lesser.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package de.spieleck.app.ngramj.lm;

import java.io.*;
import java.util.ArrayList;

import de.spieleck.app.ngramj.*;

/** 
 * Create a text_cat compatible LM file resource.
 */
public class LMWriter
    implements LMConstants
{
    public static void main(String[] args)
        throws IOException
    {
        if ( args.length != 2 )
        {
            System.err.println("LMWriter: Need exactly 2 arguments.");
            System.exit(1);
        }
        InputStream in = new FileInputStream(args[0]);
        OutputStream out = new FileOutputStream(args[1]);
        ArrayList order = ProtoReader.read(in, true);
        int limit;
        if ( order.size() < USEDNGRAMS )
            limit = ((CountedNGram)order.get(order.size()-1)).getCount();
        else
            limit = ((CountedNGram)order.get(USEDNGRAMS-1)).getCount();
        int k, i = 0;
        while ( i < order.size() 
                && ((CountedNGram)order.get(i)).getCount() >= limit )
        {
            CountedNGram gram = (CountedNGram)order.get(i);
            int cnt = gram.getCount();
            for (k = 0; k < gram.getSize(); k++)
            {
                byte b = (byte)gram.getByte(k);
                if ( b == (byte)' ' )
                    out.write((byte)'_');
                else
                    out.write(b);
            }
            // Whether text_cat will recognize this separator???
            // was ' ' \t and \t ' ' seems to be better!
            out.write('\t'); 
            out.write(' '); 
            String h = Integer.toString(cnt);
            for (k = 0; k < h.length(); k++)
                out.write((byte)h.charAt(k));
            out.write((byte)13);
            out.write((byte)10);
            i++;
        }
        out.flush();
        out.close();
    }
}
