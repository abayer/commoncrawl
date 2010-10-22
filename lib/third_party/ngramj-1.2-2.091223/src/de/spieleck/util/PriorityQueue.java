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

import java.util.Comparator;

/** A PriorityQueue maintains a partial ordering of its elements such that the
  least element can always be found in constant time. 
  <P>
  This is achieved by a heap implementation, therefore
  Put()'s and pop()'s require log(size) time. Note that this heap is
  realized by linear insertion and not by "bubbling".
  <P>
  This class can either be directly used by passing a 
  <code>java.util.Comparator</code>
  object to the constructor, or by subclassing and overriding the
  <code>lessThan()</code> method.
  <P>
  <B>XXX</B> This is an lazy implementation which spoils the very first
  entry in the heap!
  <P>
  <B>Caution:</B>This class is unsynchronized and therefore
  needs external synchronization in an multithreading environment.
 */

public class PriorityQueue 
{
    protected int size;
    protected Object[] heap;

    protected Comparator myComp;

    public PriorityQueue(int initialSize, Comparator comp)
    {
        myComp = comp;
        initialize(initialSize);
    }

    /** Determines the ordering of objects in this priority queue.    
     *  Subclasses can override this method. 
     */
    protected boolean lessThan(Object a, Object b)
    {
            return myComp.compare(a,b) < 0;
    }

    public void setComparator(Comparator comp)
    {
        myComp = comp;
        resort();
    }

    /** Subclass constructors must call this. */
    protected final void initialize(int initialSize) 
    {
        size = 0;
        int heapSize = initialSize; 
        heap = new Object[heapSize];
    }

    /** Adds an Object to a PriorityQueue in log(size) time. 
        Unless resize occurs ...
      */ 
    public final void put(Object element) 
    {
        size++;
        if ( size >= heap.length )
        {
            Object[] newHeap = new Object[2*heap.length];
            for (int i = 1; i < size; i++ )
            {
                newHeap[i] = heap[i];
                heap[i] = null; // help GC() ?!
            }
            heap = newHeap;
        }
        heap[size] = element;
        upHeap();
    }

    /**
     * Replace the topmost element.
     */
    public final void setTop(Object element)
    {
        heap[1] = element;
        adjustTop();
    }

    /** Returns the least element of the PriorityQueue in constant time. */
    public final Object top() 
    {
        if (size > 0)
            return heap[1];
        else
            return null;
    }

    /** Removes and returns the least element of the PriorityQueue in log(size)
        time. */ 
    public final Object pop() 
    {
        if (size > 0) 
        {
            Object result = heap[1];			    // save first value
            heap[1] = heap[size];			    // move last to first
            heap[size] = null;			    // permit GC of objects
            size--;
            downHeap(1);				    // adjust heap
            return result;
        } 
        else
            return null;
    }

    /** Should be called when the Object at top changes values. Still log(n)
     * worst case, but it's at least twice as fast to <pre>
     *    { pq.top().change(); pq.adjustTop(); }
     * </pre> instead of <pre>
     *    { o = pq.pop(); o.change(); pq.push(o); }
     * </pre>
     */
    public final void adjustTop() 
    {
        downHeap(1);
    }
        

    /** Returns the number of elements currently stored in the PriorityQueue. */
    public final int getSize() 
    {
        return size;
    }

    /** Returns current capacity */
    public final int getCapacity()
    {
        return heap.length;
    }
    
    /** Removes all entries from the PriorityQueue. */
    public final void clear() 
    {
        for (int i = 1; i < size; i++)
            heap[i] = null;
        size = 0;
    }

    /** Rearrange the heap, this is supposed to be
        O(n*log(n)) but better than nothing 
     */
    public void resort()
    {
        int i = size / 2;
        while ( i > 1 )
        {
            downHeap(i);
            i--;
        }
    }

    /**
     * readjust last node in the heap to maintain heap condition.
     */
    protected final void upHeap() 
    {
        int i = size;
        Object node = heap[i];			    // save bottom node
        int j = i >>> 1;
        while (j > 0 && lessThan(node, heap[j])) 
        {
            heap[i] = heap[j];			    // shift parents down
            i = j;
            j = j >>> 1;
        }
        heap[i] = node;				    // install saved node
    }
    
    /**
     * readjust top node in the heap to maintain heap condition.
     */
    protected final void downHeap(int top) 
    {
        int i = top;
        Object node = heap[i];			    // save top node
        int j = i << 1;				    // find smaller child
        int k = j + 1;
        if (k <= size && lessThan(heap[k], heap[j])) 
            j = k;
        while (j <= size && lessThan(heap[j], node)) 
        {
            heap[i] = heap[j];			    // sift up child
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && lessThan(heap[k], heap[j])) 
                j = k;
        }
        heap[i] = node;				    // place saved node
    }
}
