/*
NGramJ - n-gram based text classification
Copyright (C) 2001- Frank S. Nestel (frank at spieleck.de)

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
package de.spieleck.app.cngram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * Commandline interface that runs a ngram analysis over submitted text,
 * results can be used for automatic language identification.
 *
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class RunNGram
{
  public static final int CREATE = 1;
  public static final int SIMILARITY = 2;
  public static final int SCORE = 3;
  public static final int LANG = 4;
  public static final int TEST = 5;
  public static final int LANG2 = 6;
  public static final int LANG2B = 7;
  public static final int CHECK = 8;
  public static final int PROFILES = 9;

  public final static DecimalFormat DF = new DecimalFormat("0.000");
  public final static DecimalFormat DFE = new DecimalFormat("0.0E0");

  private static void usage(PrintStream out)
  {
    out.println("Usage: RunNGram commandset");
    out.println("          [-create profilename(out) textfile [encoding]]");
    out.println("   or     [-similarity metricName textfile1 textfile2 [encoding]]");
    out.println("   or     [-score metricName profile-name textfile [encoding]]");
    out.println("   or     [-lang metricName textfile [encoding]]");
    out.println("   or     [-test ]");
    out.println("   or     [-lang2 textfile [encoding]]");
    out.println("   or     [-lang2b textfile [encoding]]");
    out.println("   or     [-check textlistFile]");
    out.println("   or     [-profiles metricName profile1 profile2]");
    System.exit(42);
  }

  public static void main(String args[])
    throws Exception
  {
    int command = 0;


    if (args.length == 0)
        usage(System.out);

    for (int i = 0; i < args.length; i++)
    {
      String profilename = "";
      String profilename2 = "";
      String textfile = "";
      String filename2 = "";
      String metricName = null;
      NGramMetric metric = null;
      String encoding = "";

      if ("-c".equals(args[i]) || "-create".equals(args[i]) )
      {
        command = CREATE;
        profilename = args[++i];
        textfile = args[++i];
      }
      else if ("-i".equals(args[i]) || "-similarity".equals(args[i])) 
      { 
        command = SIMILARITY;
        metricName = args[++i];
        metric = (NGramMetric) Class.forName(metricName).newInstance();
        textfile = args[++i];
        filename2 = args[++i];
      }
      else if ("-s".equals(args[i]) || args[i].equals("-score")) 
      {
        command = SCORE;
        metricName = args[++i];
        metric = (NGramMetric) Class.forName(metricName).newInstance();
        profilename = args[++i];
        textfile = args[++i];
      }
      else if ( "-p".equals(args[i]) || "-profiles".equals(args[i]) )
      {
          command = PROFILES;
          metricName = args[++i];
          metric = (NGramMetric) Class.forName(metricName).newInstance();
          profilename = args[++i];
          profilename2 = args[++i];
      }
      else if ("-l".equals(args[i]) || "-lang".equals(args[i]) )
      {
        command = LANG;
        metricName = args[++i];
        metric = (NGramMetric) Class.forName(metricName).newInstance();
        textfile = args[++i];
      }
      else if ("-l2".equals(args[i]) || "-lang2".equals(args[i]) )
      {
        command = LANG2;
        textfile = args[++i];
      }
      else if ("-l2b".equals(args[i]) || "-lang2b".equals(args[i]) )
      {
        command = LANG2B;
        textfile = args[++i];
      }
      else if ("-x".equals(args[i]) || "-check".equals(args[i]) )
      {
        command = CHECK;
        textfile = args[++i];
      }
      else if ( "-t".equals(args[i]) || "-test".equals(args[i]) )
      {
        command = TEST;
      }
      else
      {
          usage(System.err);
      }

      if ( i+1 < args.length && args[i].charAt(0) != '-' )
      {
        encoding = args[++i];
      }
      else
      {
        encoding = "iso-8859-1";
      }
      if ( command == TEST )
      {
          NGramProfiles npi = new NGramProfiles();
          npi.info();
      }
      else if ( command == LANG2 || command == LANG2B )
      {
        long t1 = System.currentTimeMillis();
        NGramProfiles nps = new NGramProfiles();
        NGramProfiles.Ranker ranker = nps.getRanker();
        ranker.account(createReader(textfile,encoding));
        NGramProfiles.RankResult res = ranker.getRankResult();
        long t2 = System.currentTimeMillis();
        printRankResult("speed", res, t2-t1);
        if ( command == LANG2B )
        {
          t1 = t2;
          ranker.reset();
          ranker.account(createReader(textfile,encoding));
          res = ranker.getRankResult();
          t2 = System.currentTimeMillis();
          printRankResult("speed", res, t2-t1);
        }
      }
      else if ( command == CHECK )
      {
        NGramProfiles npi = new NGramProfiles();
        NGramProfiles.Ranker ranker = npi.getRanker();
        File fi = new File(textfile);
        BufferedReader br = new BufferedReader(new FileReader(fi));
        String line;
        while ( ( line = br.readLine() ) != null )
        {
          line = line.trim();
          if ( line.charAt(0) == '#' )
            continue;
          String[] ss = line.split(";");
          long t1 = System.currentTimeMillis();
          ranker.reset();
          ranker.account(createReader(ss[0], ss[1]));
          long t2 = System.currentTimeMillis();
          NGramProfiles.RankResult res = ranker.getRankResult();
          printRankResult(ss[0], res, t2-t1);
        }
      }
      else if ( command == PROFILES )
      {
          FileInputStream fis;
          File f2=new File(profilename);
          fis = new FileInputStream(f2);
          NGramProfileImpl comp1 = new NGramProfileImpl(profilename);
          comp1.load(fis);
          File f3=new File(profilename2);
          fis = new FileInputStream(f3);
          NGramProfileImpl comp2 = new NGramProfileImpl(profilename2);
          comp2.load(fis);
          System.out.println("diff("+profilename+":"+profilename2+")=" + DFE.format(metric.diff(comp1, comp2)));
      }
      else
      {
        long t1 = System.currentTimeMillis();
        NGramProfileImpl newProf = create(textfile, encoding);
        long t2 = System.currentTimeMillis();

        switch (command) {

        case CREATE:
          String fname = profilename+"."+NGramProfile.NGRAM_PROFILE_EXTENSION;
          File f = new File(fname);
          FileOutputStream fos = new FileOutputStream(f);
          newProf.save(fos);
          System.out.println("new profile '" + fname + "' was created.");
          break;

        case SIMILARITY:
          NGramProfile newProf2 = create(filename2, encoding);
          System.out.println("Difference is "+ DFE.format(metric.diff(newProf, newProf2)));
          break;

        case SCORE:
          File f2=new File(profilename+"."+NGramProfile.NGRAM_PROFILE_EXTENSION);
          FileInputStream fis = new FileInputStream(f2);
          NGramProfileImpl compare = new NGramProfileImpl(profilename);
          compare.load(fis);
          System.out.println("Score ("+profilename+") is " + DFE.format(metric.diff(compare, newProf)));

          break;

        case LANG:
          NGramProfiles nps = new NGramProfiles();
          // Set restrict = nps.getAllNGrams();
          long dt1 = t2 - t1;
          t1 = System.currentTimeMillis();
          NGramProfiles.RankResult res = nps.rank(metric, newProf);
          t2 = System.currentTimeMillis();
          int ppos = metricName.lastIndexOf(".");
          printRankResult(metricName.substring(ppos+1)+"("+dt1+")",res,t2-t1);
          break;
        }
      }
    }
  }

  public static Reader createReader(String textfile, String encoding)
    throws IOException
  {
    return new InputStreamReader(new BufferedInputStream(new FileInputStream(textfile)),encoding);
  }

  public static void printRankResult(String msg, NGramProfiles.RankResult res,
                                        long dt)
  {
      System.out.println(msg
              +": "+res.getName(0) +":"+DF.format(res.getScore(0))
              +" "+res.getName(1)+":"+DF.format(res.getScore(1))
              +" "+res.getName(2)+":"+DF.format(res.getScore(2))
              +" .. "+res.getName(-1)+":"+DF.format(res.getScore(-1))
              +" |"+DFE.format(res.getScore(1)/res.getScore(0))
              +" |"+DFE.format(res.getScore(-1)/res.getScore(0))
              +" dt="+dt
            );
  }

  public static NGramProfileImpl create(String textfile, String encoding)
    throws IOException
  {
    File f = new File(textfile);
    FileInputStream fis = new FileInputStream(f);
    NGramProfileImpl prof = NGramProfileImpl.createProfile(textfile,
        fis, encoding);
    fis.close();
    return prof;
  }
}
