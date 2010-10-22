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

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import de.spieleck.app.ngramj.*;

/**
 * Profile implementation which reads itself from a text_cat generated
 * resource.
 */
public class LMDataProfile
    implements LMConstants, IterableProfile
{

    protected List rankedNGrams;
    protected String name;

    public LMDataProfile(String name, InputStream stream)
    {
        this.name = name;
        readStream(stream);
    }

    protected void readStream(InputStream stream)
    {
        BufferedInputStream bi = new BufferedInputStream(stream);
        rankedNGrams = new ArrayList();
        int b = 0;
        byte[] bs = new byte[10];
        try
        {
            do
            {
                int j = 0;
            middleloop:
                while ( ( b = bi.read() ) != -1 )
                {
                    int i;
                    for (i = 0; i < SKIPABLE.length; i++)
                        if ( b == SKIPABLE[i] )
                            break middleloop;
                    if ( j < 10 )
                        bs[j++] = (b != '_') ? (byte) b : (byte)' ';
                }
                if ( j > 0 )
                {
                    rankedNGrams.add(NGramImpl.newNGram(bs, 0, j-1));
                }
            }
            while ( b != -1 );
        }
        catch (IOException e) 
        { 
            System.err.println("exception="+e);
            e.printStackTrace();
        };
    }

    public double getRank(NGram gram)
    {
        //XXX Very inefficient!!!
        Iterator iter = ngrams();
        int i = 0;
        while ( iter.hasNext() )
        {
            i++;
            if ( ((NGram)iter.next()).equals(gram) )
                return i;
        }
        return 0;
    }

    public int getSize()
    {
        return rankedNGrams.size();
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        // return super.toString()+"["+name+","+rankedGrams.length+"]";
        return getName();
    }

    public Iterator ngrams()
    {
        return rankedNGrams.iterator();
    }
}

