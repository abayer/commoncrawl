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

package de.spieleck.util;

import java.lang.StringBuffer;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * The IntMap provides a simple hashmap from keys to integers.  
 * The API is simplified of the HashMap/Hashtable collection API.
 * Additionally there are call to do simple arithmetic with entries
 * in the map.
 *
 * <p>The convenience of IntMap is avoiding all wrapping of integers.
 * </p>
 * <B>Note:</B> This class is completely unsynchronized for speed!
 */
public class IntMap 
{
    /**
     * Encoding of a null entry.  Since NULL is equal to Integer.MIN_VALUE, 
     * it's impossible to distinguish between the two.
     */
    public final static int NULL = Integer.MIN_VALUE;

    protected Object [] keys;
    protected int nullValue;
    protected int []values;
    protected int size;
    protected int mask;
    protected int limit;

    /**
     * Create a new IntMap.  Default size is 16.
     */
    public IntMap()
    {
        this(16);
    }

    public IntMap(int size)
    {
        this(size, NULL);
    }

    public IntMap(int size, int nullValue)
    {
        if ( size < 8 ) 
            size = 8;
        int z = 0;
        while ( size > 1 )
        {
            z++;
            size /= 2;
        }
        size = 2 << z;
        keys = new Object[size];
        values = new int[size];

        mask = keys.length - 1;
        limit = 3 * keys.length / 4;
        size = 0;
        this.nullValue = nullValue;
    }

    /**
     *
     */
    public int getNullValue()
    {
        return nullValue;
    }

    /**
     * This only works with binary indizes
     */
    protected int firstIndex(Object key)
    {
        return key.hashCode() & mask;
    }

    protected int nextIndex(int index)
    {
        return (index + 5) & mask;
    }

    /**
     * Clear the hashmap.
     * XXX Or should we let the GC the work an use keys = new Object[..]???
     */
    public void clear()
    {
        for (int i = 0; i < values.length; i++) 
            keys[i] = null;
        size = 0;
    }

    /**
     * Returns the current number of entries in the map.
     */
    public int size() 
    { 
        return size;
    }

    /**
     * Get an Element
     */
    public int getElement(Object key)
    {
        return get(key);
    }

    /**
     * Get an Element
     */
    public int get(Object key)
    {
        if ( key == null )
            return nullValue;
        for (int i = firstIndex(key); keys[i] != null; i = nextIndex(i))
        {
            if ( keys[i] == key || key.equals(keys[i]) )
                return values[i];
        }
        return nullValue;
    }

    /** 
     * Optimized operations.
     * avoid get change put cycles.
     */
    public int inc(Object key)
    {
        return add(key, 1);
    }

    /** 
     * Optimized operations.
     * avoid get change put cycles.
     */
    public int dec(Object key)
    {
        return add(key, -1);
    }

    /** 
     * Optimized operations.
     * avoid get change put cycles.
     */
    public int add(Object key, int off)
    {
        if ( key == null )
            return nullValue;
        int i;
        for (i = firstIndex(key); keys[i] != null; i = nextIndex(i))
        {
            if ( keys[i] == key || key.equals(keys[i]) )
            {
                values[i] += off;
                return values[i];
            }
        }
        return nullValue;
    }


    /**
     * Expands the table
     */
    protected void grow()
    {
        int newSize = 2 * keys.length;
        Object[] oldKeys = keys;
        int[] oldValues = values;
        //
        keys = new Object[newSize];
        values = new int[newSize];
        mask = newSize - 1;
        size = 0; // We recound the size below!
        for (int i = 0; i < oldKeys.length; i++) 
            if (oldKeys[i] != null)
                internalPut(oldKeys[i], oldValues[i]);
        limit = 3 * newSize / 4;
    }

    /**
     * Puts a new value in the property table with the appropriate flags
     */
    public int putElement(Object key, int value)
    {
        return put(key, value);
    }

    public int put(Object key, int value)
    {
        if (key == null) 
            return nullValue;
        if ( size >= limit )
            grow();
        return internalPut(key, value);
    }

    protected int internalPut(Object key, int value)
    {
        for(int i = firstIndex(key); true; i = nextIndex(i))
        {
            Object testKey = keys[i];
            if ( testKey == null )
            {
                keys[i] = key;
                values[i] = value;
                size++;
                return nullValue;
            }
            else if ( key == testKey || testKey.equals(key))
            {
                int old = values[i];
                values[i] = value;
                return old;
            }
        }
    }

    /**
     * Deletes the entry.  
     */
    public int remove(Object key)
    {
        if (key == null || size == 0) 
            return nullValue;

        int i;
        for (i = firstIndex(key); keys[i] != null; i = nextIndex(i))
        {
            Object testKey = keys[i];
            if (key == testKey || key.equals(testKey) )
            {
                int value = values[i];
                do 
                {
                    keys[i] = null;
                    int j = i;
                    int r;
                    do 
                    {
                        i = nextIndex(i);
                        if (keys[i] == null)
                            break;
                        r = firstIndex(keys[i]);
                    } 
                    while (   (i <= r && r < j) 
                           || (r < j && j < i) 
                           || (j < i && i <= r)
                           );
                    keys[j] = keys[i];
                    values[j] = values[i];
                } 
                while (keys[i] != null);
                --size;
                return value;
            }
        }
        return nullValue;
    }
     
    public Enumeration keys()
    {
        return new IntMapEnumeration();
    }

    public Iterator iterator()
    {
        return new IntMapIterator();
    }

    public String toString()
    {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("IntMap[");
        boolean isFirst = true;
        for (int i = 0; i < keys.length; i++) 
        {
            if (keys[i] != null) 
            {
                if (! isFirst)
                    sbuf.append(", ");
                isFirst = false;
                sbuf.append(keys[i]);
                sbuf.append(":");
                sbuf.append(values[i]);
            }
        }
        sbuf.append("]");
        return sbuf.toString();
    }

    private class IntMapEnumeration 
        implements Enumeration 
    {
        protected int index = 0;

        public boolean hasMoreElements()
        {
            for (; index < keys.length; index++)
                if (keys[index] != null )
                    return true;
            return false;
        }

        public Object nextElement()
        {
            for (; index < keys.length; index++)
                if (keys[index] != null )
                    return keys[index++];
            return null;
        }
    }

    private class IntMapIterator 
        implements Iterator 
    {
        protected int index = 0;

        public boolean hasNext()
        {
            for (; index < keys.length; index++)
                if (keys[index] != null )
                    return true;
            return false;
        }

        public Object next()
        {
            for (; index < keys.length; index++)
                if (keys[index] != null )
                    return keys[index++];
            return null;
        }

        public void remove()
        {
            throw new java.lang.UnsupportedOperationException(
                                                "IntMapIterator.remove()");
        }
    }

}

