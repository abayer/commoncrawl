/*
NGramJ - n-gram based text classification
Copyright (C) 2001- Frank S. Nestel (frank at spieleck.de)

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

package de.spieleck.app.ngramj;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Class to hold (static) methods to read in profile data
 *
 * @author Christiaan Fluit
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 12 $ $Date: 2009-07-26 10:15:14 +0200 (So, 26 Jul 2009) $ $Author: nestefan $
 */
public class ProtoReader
{
    public static ArrayList read(InputStream stream)
        throws IOException 
    {
        return read(stream, false);
    }

    /**
     * Parses InputStream into a weighted list of (byte-)NGrams.
     * @param cache determines, weather created ngrams are cached. false is faster
     * true is required for creation of .lm files.
     */
    public static ArrayList read(InputStream stream, boolean cache)
        throws IOException 
    {
        // XXX to get the last performance kick a high performance
        // HashMap replacement could be dropped in here (e.g. gnu.trove stuff)
        HashMap count = new HashMap(1000);
        BufferedInputStream bi = new BufferedInputStream(stream);
        int b;
        byte ba[] = new byte[5];
        ba[4] = 42;
        int i = 0;
        while ( ( b = bi.read() ) != -1 )
        {
            // XXX ???
            if ( b == 13 || b == 10 || b == 9 )
                b = 32;
            i++;
            if ( b != 32 || ba[3] != 32 )
            {
                ba[0] = ba[1];
                ba[1] = ba[2];
                ba[2] = ba[3];
                ba[3] = ba[4];
                ba[4] = (byte)b;
                newNGram(count, ba, 4, 1, cache);
                if ( i > 1 )
                    newNGram(count, ba,3,2, cache);
                if ( i > 2 )
                    newNGram(count, ba,2,3, cache);
                if ( i > 3 )
                    newNGram(count, ba,1,4, cache);
                if ( i > 4 )
                    newNGram(count, ba,0,5, cache);
            }
        }
        ArrayList order = new ArrayList(count.values());
        Collections.sort(order);
        return order;
    }

    protected static void newNGram(HashMap count, byte[] ba, int start, int len, boolean cache)
    {
        NGram ng = NGramImpl.newNGram(ba, start, len, cache);
        CountedNGram cng = (CountedNGram) count.get(ng);
        if ( cng != null )
            cng.inc();
        else
            count.put(ng, new CountedNGram(ng));
    }

}

