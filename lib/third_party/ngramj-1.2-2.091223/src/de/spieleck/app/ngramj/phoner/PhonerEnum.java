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

import java.util.Hashtable;

/**
 * Efficient enumeration of PhoneNumberWords to a certain Number
 */
public class PhonerEnum
    extends PhoneKeys
{
    protected int i, length;
    protected byte[][] sets;
    protected byte[] res;
    protected int[] j;

    public PhonerEnum(String pnumber)
        throws IllegalArgumentException
    {
        length = pnumber.length();
        sets = new byte[length][];
        for (i = 0; i < length; i++)
        {
            String ch = String.valueOf(pnumber.charAt(i));
            sets[i] = (byte[])replacers.get(ch);
            // Non existing characters are taken verbatim
            if ( sets[i] == null )
                sets[i] = new byte[] { ch.getBytes()[0] };
        }
        res = new byte[length];
        j = new int[length];
        i = 1;
        j[0] = -1;
    }

    public byte[] next()
    {
        i--;
        while ( i >= 0 )
        {
            j[i]++;
            if ( j[i] >= sets[i].length )
                i--;
            else
            {
                res[i] = sets[i][j[i]];
                i++;
                if ( i == length )
                {
                    return res;
                }
                else
                {
                    j[i] = -1;
                }
            }
        }
        return null;
    }
}

