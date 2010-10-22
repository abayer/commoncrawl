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

package de.spieleck.app.ngramj.phoner;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import de.spieleck.app.ngramj.*;
import de.spieleck.util.*;

/**
 * Even better enumeration, does not enumerate the phone
 * numbers, but their corresponding profiles.
 */
public class PhonerProfileEnum
    extends PhoneKeys
{
    protected int i, length;
    protected byte[][] charSets;
    protected byte[] bytes;
    protected int[] j;
    protected int gtcount[], eqcount[];
    protected IntMap grams;
    protected boolean notFirst = false;

    public PhonerProfileEnum(String pnumber)
        throws IllegalArgumentException
    {
        length = pnumber.length();
        charSets = new byte[length][];
        for (i = 0; i < length; i++)
        {
            String ch = String.valueOf(pnumber.charAt(i));
            charSets[i] = (byte[])replacers.get(ch);
            // Non existing characters are taken verbatim
            if ( charSets[i] == null )
                charSets[i] = new byte[] { ch.getBytes()[0] };
        }
        bytes = new byte[length+2];
        // Attach word boundaries at both ends of the number...
        bytes[0] = bytes[i+1] = (byte)' ';
        j = new int[length];
        i = 0;
        j[0] = -1;
        grams = new IntMap(3 << (2*length) );
        // XXX ??? do those dimensions carry thru?
        // Is it clear that no ngram is there more than length times??
        gtcount = new int[length+1];
        eqcount = new int[length+1];
    }

    public Profile next()
    {
// System.err.println("### "+i);
        if ( notFirst )
        {
            addNGrams(length+1, -1);
            i--;
        }
        notFirst = true;
        while ( i >= 0 )
        {
            j[i]++;
            int i1 = i+1;
            if ( j[i] >= charSets[i].length )
            {
                addNGrams(i1, -1);
                i--;
            } 
            else
            {
                if ( j[i] > 0 )
                    addNGrams(i1, -1);
                // Add the new byte
                bytes[i1] = charSets[i][j[i]];
                i = i1;
                addNGrams(i, 1);
                if ( i == length )
                {
                    addNGrams(length+1, 1);
if(false)
{
System.err.println();
for(int k = 0; k < length+2; k++) System.err.print(bytes[k]+" ");
System.err.println();
java.util.Enumeration en = grams.keys();
while ( en.hasMoreElements() )
{
    Object o = en.nextElement();
    int n = grams.get(o);
    if ( n > 0 )
    {
        System.err.println("> "+n+" "+o);
    }
}
for(int k = 1; k < length; k++) System.err.println(k+".: "+gtcount[k]+" "+eqcount[k]);
}
                    return returnProf;
                }
                else
                {
                    j[i] = -1;
                }
            }
        }
        return null;
    }

    protected void addNGrams(int pos, int off)
    {
        int len = 0;
        while ( len < 5 )
        {
            int start = pos - len;
            if ( start < 0 )
                break;
            len++; // we allready need len+1 below here
            NGram ng = NGramImpl.newNGram(bytes, start, len);
            int cng = grams.get(ng);
// System.err.println("~ "+off+" "+ng+" "+cng);
            if ( off > 0 )
            {
                if ( cng != grams.getNullValue() )
                {
                    gtcount[cng]++;
                    eqcount[cng]--;
                    cng += off;
                    grams.put(ng, cng);
                    eqcount[cng]++;
                }
                else 
                {
                    // we don't care about zero counts! gtcount[0]++;
                    eqcount[off]++;
                    grams.put(ng, off);
                }
            }
            else
            {
                if ( cng != grams.getNullValue() )
                {
                    eqcount[cng]--;
                    cng += off;
                    grams.put(ng, cng);
                    gtcount[cng]--;
                    eqcount[cng]++;
                }
            }
        }
    }

    public byte[] getRes()
    {
        return bytes;
    }

    protected Profile returnProf = new Profile()
    {
        public double getRank(NGram ng)
        {
            // It is enough to return a Pseudorank
            // since the evaluation is very coarse:
            int n = grams.get(ng);
            if ( n == grams.getNullValue() || n == 0 )
            {
// System.err.println(ng+" null");
                return 0.0;
            }
            else 
            {
                double h = gtcount[n] + 0.5 * ( eqcount[n] + 1 );
// System.err.println(ng+" "+h);
                return h;
            }
        }
    };
}   

