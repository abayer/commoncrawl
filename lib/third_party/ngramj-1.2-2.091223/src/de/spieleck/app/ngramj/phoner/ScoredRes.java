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

public class ScoredRes
{
    protected byte[] res;
    protected double val;

    public ScoredRes(byte[] res, double val)
    {
        this.res = new byte[res.length];
        for(int i = 0; i < res.length; i++ )
            this.res[i] = res[i];
        this.val = val;
    }

    public byte[] getRes()
    {
        return res;
    }

    public double getVal()
    {
        return val;
    }

    protected String string = null;

    public String toString()
    {
        if ( string == null )
            string = new String(res);
        return string;
    }
}

