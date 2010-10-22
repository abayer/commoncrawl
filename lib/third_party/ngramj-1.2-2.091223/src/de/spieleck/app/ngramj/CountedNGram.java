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

import java.lang.Comparable;

/**
 * An n-gram attached with a counter.
 *
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class CountedNGram
    // extends NGramImpl
    implements Comparable, NGram
{
    protected int count = 1;
    protected NGram gram;

    public CountedNGram(NGram ng)
    {
        gram = ng;
    }

    public CountedNGram(byte[] ba, int start, int len)
    {
        gram = NGramImpl.newNGram(ba, start, len);
    }

    public int hashCode()
    {
        return gram.hashCode();
    }

    public int getCount() { return count; }
    public void inc() { count++; }

    public NGram getNGram()
    {
        return gram;
    }

    public int compareTo(Object e1)
    {
        return ((CountedNGram)e1).getCount() - getCount();
    }

    public boolean equals(Object e1)
    {
        if ( e1 instanceof CountedNGram ) 
            return getNGram().equals(((CountedNGram)e1).getNGram());
        else if ( e1 instanceof NGram )
            return e1.equals(getNGram());
        return false;
    }

    public String toString()
    {
        return getNGram().toString()+"["+getCount()+"]";
    }

    public int getSize()
    {
        return getNGram().getSize();
    }

    /** 
     * Return a single byte of the NGram.
     * @throws ArrayIndexOutOfBoundException (implicitly)
     */
    public int getByte(int pos)
    {
        return getNGram().getByte(pos);
    }

    /**
     * Compare a ngram to a bunch of bytes
     */
    public boolean equals(byte[] bytes, int start, int length)
    {
        return getNGram().equals(bytes, start, length);
    }

    /**
     * Hand out a special representation of yourself
     */
    public NGramImpl getNGramImpl()
    {
        return getNGram().getNGramImpl();
    }
}
