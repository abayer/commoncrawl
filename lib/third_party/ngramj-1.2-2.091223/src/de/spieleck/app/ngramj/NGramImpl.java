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

/**
 * Class to modell a concrete and simple NGram.
 * <P>
 * To make it slightly more interestion (and efficient),
 * those NGrams follow a Flyweight pattern! I.e.
 * of each different NGram there will only be one
 * instance in the System. This is a bit technical,
 * but one can safely ignore this and just 
 * deliberately call newNGram().
 * </P>
 * 
 * @author Christiaan Fluit
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 12 $ $Date: 2009-07-26 10:15:14 +0200 (So, 26 Jul 2009) $ $Author: nestefan $
 */
public class NGramImpl
    implements NGram
{
    protected byte[] bytes;
    protected int size;

    // Power of 2 !!
    protected static NGramImpl[] known = new NGramImpl[512];
    protected static int knownCount = 0;
    protected static int knownStep  = 400;

    // XXX Seems to be necessary to allow subclassing:
    protected NGramImpl() {}

    private NGramImpl(byte[] bytes, int start, int length)
    {
        this.size = length;
        this.bytes = new byte[length];
        for(int i = 0; i < length; i++ )
            this.bytes[i] = bytes[i+start];
    }

    /**
     * QuasiConstructor.
     * FlyWeight means that we first have to look if we
     * allready know the current beasty.
     */
    public static NGram newNGram(byte[] bytes, int start)
    {
        return newNGram(bytes, start, bytes.length-start);
    }

    /**
     * QuasiConstructor.
     * FlyWeight means that we first have to look if we
     * allready know the current beasty.
     */
    public static NGram newNGram(byte[] bytes, int start, int length)
    {
		return newNGram(bytes, start, length, true);
	}

    public static NGram newNGram(byte[] bytes, int start, int length, boolean cacheObject)
	{
        int c = code(bytes, start, length);
        int p = ( c % known.length );
        if ( p < 0 ) 
            p += known.length;
        // XXX Implementation not synchronized !!!
        while ( known[p] != null )
        {
            if ( known[p].equals(bytes, start, length) )
                break;
            p = ( p + 5 ) % known.length;
        }

		NGramImpl nn = known[p];

        // Do we need a new NGram?!
        if ( nn == null )
        {
            nn = new NGramImpl(bytes, start, length);

			if (cacheObject) {
            	knownCount++;
            	if ( knownCount >= knownStep )
            	{
					// Increase cache size
                	NGramImpl[] oldknown = known;
                	int len = known.length;
                	int l2n = len * 2;
                	known = new NGramImpl[l2n];
                	for (int i = 0; i < len; i++)
                    	if ( oldknown[i] != null )
                    	{
                        	int h = oldknown[i].hashCode() % l2n;
                        	if ( h < 0 ) 
                            	h += l2n;
                        	while ( known[h] != null )
                        	{
                            	h = ( h + 5 ) % l2n;
                        	}
                        	known[h] = oldknown[i];
                    	}
                	knownStep *= 2;
                	p = nn.hashCode() % l2n;
                	if ( p < 0 )
                    	p += l2n;
                	while ( known[p] != null ) 
                    	p = ( p + 5 ) % l2n;
            	}
            	known[p] = nn;
			}
        }

        return nn;
    }

    /**
     * Return the size of the ngram.
     */
    public int getSize()
    {
        return size;
    }

    /** 
     * Return a single byte of the NGram.
     * @throws ArrayIndexOutOfBoundException ...
     */
    public int getByte(int pos)
    {
        return bytes[pos];
    }

    public static int getKnownCount()
    {
        return knownCount;
    }

    public boolean equals(byte[] bytes, int start, int length)
    {
        if ( size != length )
            return false;
        else
        {
            for (int i = 0; i < size; i++ )
                if ( this.bytes[i] != bytes[i+start] )
                    return false;
        }
        return true;
    }

    /**
     * Override the hashCode by s.th. that allows to hash NGrams
     * against tiny byte sequences.
     */
    public int hashCode()
    {
        return code(bytes, 0, bytes.length);
    }

    /**
     * Encode a byte sequence.
     */
    public static int code(byte[] bytes, int start, int length)
    {
        int h = 0;
        for (int i = 0; i < length; i++)
            h = appendCode(h, bytes[i+start]);
        return h;
    }

    /**
     * scrambler for hashcodes...
     */
    public final static int appendCode(int h, byte b)
    {
        return 0x50501005 * h + 0x0AAA0AAA + b;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(super.toString()+"{");
        for (int i = 0; i < size; i++)
        {
            if ( i != 0 )
                sb.append(";");
            sb.append(bytes[i]);
        }
        sb.append("}");
        return sb.toString();
    }

    public NGramImpl getNGramImpl()
    {
        return this;
    }

    public static int getNGramImplCount()
    {
        return knownCount;
    }

}

