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

package de.spieleck.app.ngramj;

import java.io.*;
import java.util.*;

/**
 * A profile to be created from a file.
 * Use this if you have an abitrary bunch of bytes to be repackaged into
 * ngrams. Note if you have an byte-Array in memory, you can feed it into
 * this class via a {@link java.io.ByteArrayInputStream}.
 *
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class EntryProfile
    implements Profile, Constants
{
    protected HashMap grams;
    protected int theLimit = -1;

    public EntryProfile(String fname)
        throws IOException, FileNotFoundException
    {
        this(fname, -1);
    }

    public EntryProfile(String fname, int theLimit)
        throws IOException, FileNotFoundException
    {
        this.theLimit = theLimit;
        FileInputStream fi = new FileInputStream(fname);
        digestStream(fi);
        fi.close();
    }

    public EntryProfile(InputStream stream)
        throws IOException
    {
        this(stream, -1);
    }

    public EntryProfile(InputStream stream, int theLimit)
        throws IOException
    {
        this.theLimit = theLimit;
        digestStream(stream);
    }

    protected void digestStream(InputStream stream)
        throws IOException 
    {
        int i;
        ArrayList order = ProtoReader.read(stream);
        int limit;
        if ( theLimit < 0  )
        {
            limit = -1;
            grams = new HashMap(order.size());
        }
        else if ( order.size() < theLimit )
        {
            limit = ((CountedNGram)order.get(order.size()-1)).getCount();
            grams = new HashMap(order.size());
        }
        else
        {
            limit = ((CountedNGram)order.get(theLimit-1)).getCount();
            grams = new HashMap(theLimit);
        }
        i = 0;
        while ( i < order.size() 
                && ((CountedNGram)order.get(i)).getCount() >= limit )
        {
            int cnt = ((CountedNGram)order.get(i)).getCount();
            int j = i;
            while ( ++j < order.size()
                && ((CountedNGram)order.get(j)).getCount() == cnt ) ;
            double h = (i + j + 1) * 0.5;
            for (int k = i; k < j; k++ )
                grams.put(((CountedNGram)order.get(k)).getNGram(), 
                            new Double(h) );
// XXX Should resolve ties, otherwise behaviour is unpredictable
// due to internal behaviour of sort.
//System.err.println("---> "+((CountedNGram)order.get(i)).getNGram()+" "+i);
//grams.put(((CountedNGram)order.get(i)).getNGram(), new Integer(i) );
            i = j;
        }
    }

    public double getRank(NGram ng)
    {
        Double in = (Double)grams.get(ng);
        if ( in == null )
            return 0.0;
        else
        {
            return in.doubleValue();
        }
    }

}
