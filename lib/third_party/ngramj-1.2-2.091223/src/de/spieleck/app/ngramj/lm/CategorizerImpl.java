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

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;

import de.spieleck.app.ngramj.*;

/**
 * One class to classify a profile against a set of profiles.
 *
 * Note this has a main() method for testing and tuning purposes.
 */
public class CategorizerImpl
    implements Categorizer, LMConstants 
{
    protected List profiles = new ArrayList();

    /**
     * Construct an uninitialized Categorizer.
     */
    public CategorizerImpl()
        throws IOException
    {
        InputStream ip = getClass().getResourceAsStream("profiles.lst");
        BufferedReader br = new BufferedReader(new InputStreamReader(ip));
        ArrayList al = new ArrayList();
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            InputStream is = getClass().getResourceAsStream(line);
            IterableProfile prof = new LMDataProfile(line, is);
            addProfile(prof);
        }
    }

    /** 
     * Construct an Categorizer from a whole Directory of resources.
     */
    public CategorizerImpl(String dirName)
        throws NGramException, FileNotFoundException
    {
        File fi = new File(dirName);
        if ( ! fi.isDirectory() )
            throw new NGramException("Base must be a directory.");
        String[] names = fi.list(LMFilter);
        init(fi, names);
    }

    public static FilenameFilter LMFilter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".lm");
            }
        };

    /** 
     * Construct an Categorizer from a List of resource file names.
     */
    public CategorizerImpl(String[] fileNames)
        throws NGramException, FileNotFoundException
    {
        init(null, fileNames);
    }

    /**
     * Fetch the set of file resources.
     */
    protected void init(File fi, String[] names)
        throws NGramException, FileNotFoundException
    {
        if ( names == null || names.length == 0 )
            throw new NGramException("Need at least one NGram input file.");
        for (int i = 0; i < names.length; i++ )
        {
            File ifi = new File(fi, names[i]);
            InputStream in = new FileInputStream(ifi);
            IterableProfile prof = new LMDataProfile(names[i], in);
            addProfile(prof);
        }
        System.err.println("Statistics: "
                            +NGramImpl.getNGramImplCount()+" n-grams, "
                            +names.length+" Profiles."
                            +" q="+(NGramImpl.getNGramImplCount()/names.length)
                        );
    }

    /**
     * add an Categorization alternative to the profiles.
     */
    public void addProfile(IterableProfile prof)
    {
        profiles.add(prof);
    }

    /**
     * Match a given profile against the Categorizer
     */
    public Profile match(Profile prof)
    {
        double error = Double.MAX_VALUE;
        Profile opt = null;
        Iterator iter = profiles.iterator();
        while ( iter.hasNext() )
        {
            IterableProfile prof2 = (IterableProfile) iter.next();
            double newError = deltaRank(prof2, prof);
            if ( newError < error )
            {
                error = newError;
                opt = prof2;
            }
        }
        return opt;
    }

    /**
     * Calculate "the distance" between two profiles
     */
    public double deltaRank(IterableProfile prof1, Profile prof2)
    {
        double delta = 0.0;
        Iterator grams = prof1.ngrams();
        int j = 0;
        while ( grams.hasNext() )
        {
            j++;
            NGram ngram = (NGram) grams.next();
            double rank = prof2.getRank(ngram);
            if ( rank != 0.0 )
                delta += Math.abs(rank - j );
            else
                delta += USEDNGRAMS; // XXX ?!
        }
        return delta;
    }

    /**
     * Sample application, like the text_cat main mode.
     */
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length == 1 )
        {
            Categorizer cath = new CategorizerImpl();
            EntryProfile prof = new EntryProfile(args[0], USEDNGRAMS);
            Profile res = cath.match(prof);
            System.err.println("Best match is: "+res);
        }
        else
        {
            Categorizer cath = new CategorizerImpl(args[0]);
            for (int i = 1; i < args.length; i++ )
            {
                EntryProfile prof = new EntryProfile(args[i], USEDNGRAMS);
                Profile res = cath.match(prof);
                System.err.println("Best match is: "+res);
            }
        }
    }

}
