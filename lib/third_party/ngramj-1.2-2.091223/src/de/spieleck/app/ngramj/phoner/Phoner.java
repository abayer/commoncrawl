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

import java.io.*;

import java.util.Comparator;
import java.util.Iterator;

import de.spieleck.app.ngramj.*;
import de.spieleck.util.PriorityQueue;
import de.spieleck.app.ngramj.lm.LMDataProfile;

/**
 * Mini-Application to create a "useful" word from
 * a phonenumber.
 * <P>
 * This uses a brute force approach to evaluate various options
 * to convert a number in a dialable string. To make bf feasible,
 * one had to be slightly careful...
 * </P>
 */
public class Phoner
    implements Comparator
{
    public final static int LIMIT = 200;

    public final static String DEF_RESOURCE = "frank.lm";

    protected String pnumber;
    protected PriorityQueue order;
    protected double min, max;
    protected int count = 0;

    public Phoner(IterableProfile prof, String pnumber)
        throws IOException, NGramException
    {
        this.pnumber = pnumber;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        PhonerProfileEnum pen = new PhonerProfileEnum(pnumber);
        //
        order = new PriorityQueue(LIMIT, wrongComp);
        Profile ep;
        while ( ( ep = pen.next() ) != null )
        {
            double dr = deltaRank(prof, ep);
            if ( ++count < LIMIT+LIMIT )
                order.put(new ScoredRes(pen.getRes(), dr));
            else if ( dr < ((ScoredRes)order.top()).getVal() )
                order.setTop(new ScoredRes(pen.getRes(), dr));
            if ( dr < min )
                min = dr;
            if ( dr > max )
                max = dr;
        }
    }

    public static Comparator wrongComp = new Comparator()
        {
            public int compare(Object a, Object b)
            {
                double h = ((ScoredRes)a).getVal()-((ScoredRes)b).getVal();
                if ( h > 0.0 )
                    return -1;
                else if ( h < 0.0 )
                    return +1;
                else
                    return  0;
            }
        };

    public void show()
    {
        PrintWriter pw = new PrintWriter(System.out, true);
        show(pw);
    }

    public void show(PrintWriter pw)
    {
        pw.println(pnumber+" ... "+count+" combinations considered."
                          +" score range ["+min+","+max+"]");
        ScoredRes[] ress = new ScoredRes[order.getSize()];
        int i = order.getSize();
        while ( order.getSize() > 0 )
            ress[--i] = (ScoredRes)order.pop();
        for (i = 0; i < ress.length; i++)
            pw.println((i+1)+". "+ress[i]+" "+ress[i].getVal());
    }

    public double deltaRank(IterableProfile prof1, Profile prof2)
    {
        double delta = 0.0;
        Iterator iter = prof1.ngrams();
        int j = 0;
        while ( iter.hasNext() )
        {
            j++;
            double rank = prof2.getRank((NGram)iter.next());
            if ( rank != 0.0 )
                delta += Math.abs(rank - j );
            else
                delta += 401; // XXX fixed!
        }
        return delta;
    }

    public int compare(Object a, Object b)
    {
        double h = ((ScoredRes)a).getVal() - ((ScoredRes)b).getVal();
        if ( h < 0.0 )
            return -1;
        else if ( h > 0.0 )
            return +1;
        else
            return 0;
    }

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    /**
     * Sample commandline implementation
     */
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length == 1 )
        {
            InputStream in = Phoner.class.getResourceAsStream(DEF_RESOURCE);
            IterableProfile prof = new LMDataProfile(DEF_RESOURCE, in);
            Phoner p = new Phoner(prof, args[0]);
            p.show();
            System.err.println("**** #ngram="+NGramImpl.getKnownCount());
        }
        else
        {
            File ifi = new File(args[0]);
            InputStream in = new FileInputStream(ifi);
            IterableProfile prof = new LMDataProfile(args[0], in);
            //
            for (int i = 1; i < args.length; i++ )
            {
                Phoner p = new Phoner(prof, args[i]);
                p.show();
                System.err.println("**** #ngram="+NGramImpl.getKnownCount());
            }
        }
    }
}
