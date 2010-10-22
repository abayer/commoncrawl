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

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.BitSet;
import java.text.DecimalFormat;

import de.spieleck.util.CharTrie;
import de.spieleck.util.CharTrieBase;

/**
 * Manage a set of profiles and determine "most similar" ones
 * to a given profile. Allows access to the complete results of
 * previous last ranking. Note this uses a competetive ranking
 * approach, which is memory efficient, time efficient for not
 * too many languages and provides contextual scoring of ngrams.
 *
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 12 $ $Date: 2009-07-26 10:15:14 +0200 (So, 26 Jul 2009) $ $Author: nestefan $
 */
public class NGramProfiles
{
  public final static int CHECK_REPEAT = 1000;

  public final static String NOLANGNAME = "--";

  public final static char END_CHAR = (char) 0;

  public final static DecimalFormat DF = new DecimalFormat("+0.00;-0.00");
  public final static DecimalFormat DFE = new DecimalFormat("0.0E0");

  public final static double LOWSTATSAFETY = 0.8;

  private List profiles = null;

  private HashSet allNGrams = new HashSet(10000);

  private int firstNGrams;

  private int maxLen = -1;

  private CharTrie myTrie = null;

  private float[][] vals;

  private String[] ngs;

  private int mode;

  public NGramProfiles()
    throws IOException
  {
    this(1);
  }

  public NGramProfiles(int mode)
    throws IOException
  {
    InputStream ip = getClass().getResourceAsStream("profiles.lst");
    BufferedReader br = new BufferedReader(new InputStreamReader(ip));
    this.mode = mode;
    init(br);
  }

  public NGramProfiles(BufferedReader br)
    throws IOException
  {
    init(br);
  }

  protected void init(BufferedReader br)
    throws IOException
  {
    profiles = new ArrayList();
    firstNGrams = 0;
    String line;
    while ( ( line = br.readLine() ) != null )
    {
      if ( line.charAt(0) == '#' )
        continue;
      InputStream is = getClass().getResourceAsStream(line+"."+
          NGramProfile.NGRAM_PROFILE_EXTENSION);
      NGramProfileImpl np = new NGramProfileImpl(line);
      np.load(is);
      profiles.add(np);
      Iterator iter = np.getSorted();
      while ( iter.hasNext() )
      {
        NGram ng = (NGram) iter.next();
        if ( ng.length() > maxLen ) maxLen = ng.length();
        firstNGrams++;
        allNGrams.add(ng);
      }
    }
    myTrie = null;
  }

  public void info()
  {
    System.err.println("#profiles="+profiles.size()+", firstNGrams="+firstNGrams+", secondNGrams="+allNGrams.size()+", maxLen="+maxLen+", mode="+mode);
    CosMetric cm = new CosMetric();
    Iterator i1 = profiles.iterator();
    while ( i1.hasNext() )
    {
        NGramProfile p1 = (NGramProfile) i1.next();
        System.out.print("# "+p1.getName());
        Iterator i2 = profiles.iterator();
        while ( i2.hasNext() )
        {
            NGramProfile p2 = (NGramProfile) i2.next();
            System.out.print(" ");
            System.out.print(DF.format(1.0 - cm.diff(p1,p2)));
        }
        System.out.println();
    }
    Ranker r = getRanker();
    //
    Pair[] ranks = new Pair[ngs.length];          
    for(int i = 0; i < ngs.length; i++)
    {
      ranks[i] = new Pair(ngs[i], i);
      for(int k = 0; k < profiles.size(); k++)
        ranks[i].d += vals[i][k] * vals[i][k];
    }
    Arrays.sort(ranks);
    for(int i = 0; i < ranks.length; i++)
    {
        ranks[i].d = Math.sqrt(ranks[i].d);
        System.out.print((i+1)+". "+ranks[i].d+" : <"+ranks[i].ng+">");
        int count = 0;
        for(int j = 0; j< profiles.size(); j++)
        {
            double h = vals[ranks[i].i0][j];
            if ( h > 0.0 )
            {
                System.out.print(" "+getProfileName(j)+":"+DF.format(h));
                count++;
            }
        }
        CharTrie t2 = myTrie.subtrie(reverse(ranks[i].ng.toString()));
        System.out.println(" |"+count+" "+(count*(profiles.size()-count))
                +" ?? "+t2.getId()+" "+ngs[t2.getId()]
            );
    }
    //
    check(r, "Das ist cool men!");
    check(r, "Les Ordinateurs sont appeles a jouer un role");
    check(r, "Sein oder Nichtsein, das ist hier die Frage!");
    check(r, "Zu Ihren Aufgaben zählen u. a. die Einführung und Erprobung neuer Technologien, die Erarbeitung von Rationalisierungslösungen für die Fertigung sowie die Erarbeitung und Durchführung von Technologieversuchen bei der Mustererstellung mit Hinblick auf die Serienfertigung. Desweiteren sind Sie für die Betreuung und Durchführung von Entwicklungsprojekten sowie die Erarbeitung und konstruktive Auslegung von Werkzeugen, Vorrichtungen und Automatisierungseinrichtungen zuständig. Sie sind Ansprechpartner für die Zusammenarbeit und Betreuung von externen Lieferanten bei der Konstruktion und der Herstellung von Maschinen und Vorrichtungen. Sie leiten qualitätsverbessernde Maßnahmen im Prozess und in der Fertigung ein und sind für die kostenbewusste und zukunftsweisende Planung des Bereiches verantwortlich. Zudem führen Sie Ihre Mitarbeiter zielgerichtet entsprechend der INA-Führungsleitlinien.");
    check(r, "Sein oder Nichtsein, das ist hier die Frage! To be or not to be, that is the question!");
    check(r, "Sein oder Nichtsein, das ist hier die Frage! To be or not to be, that is the question! Sein oder Nichtsein, das ist hier die Frage! To be or not to be, that is the question!");
    check(r, "Marcel Andre Casasola Merkle");
    check(r, "question");
    check(r, "methodist");
    check(r, "the");
    check(r, "the methodist question");
    check(r, "this is a methodist question");
    /*
    */
  }

  public static void check(Ranker r, CharSequence seq)
  {
    System.out.println("## \""+seq+"\": ");
    System.gc();
    System.gc();
    long t1 = System.currentTimeMillis();
    for(int i = 0; i < CHECK_REPEAT; i++)
    {
      r.reset();
      r.account(seq);
    }
    long t2 = System.currentTimeMillis();
    RankResult rs = r.getRankResult();
    double sum = 0.0;
    for(int i = 0; i < rs.getLength(); i++)
    {
      if ( rs.getScore(i) > 0.0 )
      {
        System.out.println("  "+rs.getName(i)
              +" "+DF.format(rs.getScore(i))
              +" "+(i+1)
          );
        sum += rs.getScore(i);
      }
    }
    System.out.println("#===== took="+(t2-t1)+"ms: "+DFE.format(0.001*(t2-t1)/seq.length()/CHECK_REPEAT)+" s/ch. other="+DF.format(1.0-sum));
  }

  public Ranker getRanker()
  {
    int[] otherCount = new int[maxLen+1];
    synchronized(profiles)
    {
        if ( myTrie == null )
        {
          // Create a reverse reference of all strings
          // which makes it easy to create reverse Trie's
          ngs = new String[allNGrams.size()];
          Iterator it = allNGrams.iterator();
          int j = 0;
          while( it.hasNext() )
          {
            NGram ng = (NGram) it.next();
            ngs[j++] = reverse(ng);
          }
          Arrays.sort(ngs);
          // Create Strings in correct order but sorted from reverse end.
          myTrie = CharTrieBase.createTrie(ngs);
          // reverse back to obtain the right ngrams sorted from the back
          for(int i = 0; i < ngs.length; i++)
          {
CharTrie ct = myTrie.subtrie(ngs[i]); if ( i != ct.getId() ) System.err.println("PANIK! "+i+".: <"+ngs[i]+"> "+ct.getId()); 
            ngs[i] = reverse(ngs[i]);
          }
          vals = new float[ngs.length][profiles.size()];
          for(int k = 0; k < profiles.size(); k++)
          {
            NGramProfile ngp = ((NGramProfile)profiles.get(k));
            double norm[] = new double[maxLen+1];
            int count[] = new int[maxLen+1];
            for(int i = 0; i < ngs.length; i++)
            {
              NGram ng = ngp.get(ngs[i]);
              if ( ng != null && ng.getCount() > LOWSTATSAFETY )
              {
                int ngl = ng.length();
                double raw1 = ng.getCount() - LOWSTATSAFETY;
                count[ngl]++;
                norm[ngl] += raw1;
                vals[i][k] = (float) raw1;
              }
            }
            for(int i = 1; i <= maxLen; i++)
            {
              norm[i] *= (1.0+count[i])/count[i];
              norm[i] += 1.0;
            }
            for(int i = 0; i < ngs.length; i++)
            {
              NGram ng = ngp.get(ngs[i]);
              if ( ng != null && ng.getCount() > 0 )
              {
                int ngl = ng.length();
                vals[i][k] /= norm[ngl];
              }
            }
          }
          // Horizontal additive zero sum + nonlinear weighting
          for(int i = 0; i < ngs.length; i++)
          {
            double sum = 0.0;
            for(int k = 0; k < profiles.size(); k++)
            {
              double h = vals[i][k];
              sum += h;
            }
            double av = sum / profiles.size();
            /**
             * Assumed minimum amount of score for significance.
             * XXX Heuristics for the following constant:
             * Higher means faster and less noise
             * Lower means better adaption to mixed language text
             */
            // double n = modeTrans(av, ngs[i].length()) / av / 100.0 * (-Math.log(av));
            double n = modeTrans(av, ngs[i].length()) / av / 40.0;
            for(int k = 0; k < profiles.size(); k++)
            {
              vals[i][k] = (float) ((vals[i][k] - av) * n);
            }
          }
        }
    }
    return new Ranker()
      {
          private double score[] = new double[profiles.size()+1];
          private double rscore[] = new double[profiles.size()+1];
          private boolean flushed = false;

          {
            reset();
          }

          public RankResult getRankResult()
          {
            flush();
            double pscore[] = new double[profiles.size()];
            double sum = 0.0;
            for(int i = 0; i <= profiles.size(); i++)
            {
              sum += rscore[i];
            }
            for(int i = 0; i < profiles.size(); i++)
            {
              pscore[i] = rscore[i] / sum;
            }
            return new SimpleRankResult(pscore, true);
          }

          public void reset()
          {
            for(int i = 0; i < score.length; i++)
            {
              rscore[i] = score[i] = 0.0;
            }
            rscore[score.length-1] = 0.5; // 0.2 is too low;
          }

          public void flush()
          {
            if ( !flushed )
            {
              flushed = true;
              double maxValue = -1.0;
              for(int i = 0; i < score.length; i++)
              {
                maxValue = Math.max(maxValue, score[i]);
              }
              double limit = maxValue / 2.0;
              double f = 1.0/(maxValue - limit);
              for(int i = 0; i < score.length; i++)
              {
                double delta = score[i] - limit;
                if ( delta > 0.0 )
                  rscore[i] += delta * f;
                // We do not reset to zero, this makes classification contextual
                score[i] /= 2.0;
              }
            }
          }

          public void account(CharSequence seq, int pos)
          {
            CharTrie currentNode = myTrie;
            int p2 = pos;
            while(currentNode != null)
            {
              char ch;
              if ( p2 == -1 )
              {
                ch = ' ';
              }
              else 
              {
                ch = Character.toLowerCase(seq.charAt(p2));
                if ( isSeparator(ch) )
                  ch = ' ';
              }
              CharTrie t2 = currentNode.subtrie(ch);
              if ( t2 == null )
                break;
              int id = t2.getId();
              if ( id != CharTrie.NO_INDEX )
              {
                flushed = false;
                for(int i = 0; i < profiles.size(); i++)
                {
                  score[i] += vals[id][i];
                }
              }
              if ( p2-- == -1 )
                break;
              currentNode = t2;
            }
            char startChar = seq.charAt(pos);
            boolean startSep = isSeparator(startChar);
            double max = 0.0;
            for(int i = 0; i < score.length; i++)
            {
              max = Math.max(max, score[i]);
            }
            if ( startSep && max > 1.0 )
            {
              flush();
            }
          }

          public void account(CharSequence seq)
          {
            for(int i = 0; i < seq.length(); i++)
              account(seq, i);
          }

          public void account(Reader reader)
            throws IOException
          {
            BufferedReader br;
            if ( reader instanceof BufferedReader )
              br = (BufferedReader) reader;
            else
              br = new BufferedReader(reader);
            String line;
            while( ( line = br.readLine() ) != null )
            {
              account(line);
            }
          }
      };
  }

  public class Pair
    implements Comparable
  {
    String ng;
    double d = 0.0;
    int i0;
    
    public Pair(String ng, int i0)
    {  
        this.ng = ng;
        this.i0 = i0;
    }

    public int compareTo(Object o)
    {
        Pair p2 = (Pair) o;
        double h = d - p2.d;
        if ( h < 0.0 )
            return +1;
        else if ( h > 0.0 )
            return -1;
        return ng.compareTo(p2.ng);
    }
  }

  public double modeTrans(double x, int l)
  {
    double f;
    switch(mode)
    {
      case 10: 
          if ( l == 1 )
            return x;
          f = 1.0 / (l+1);
          return Math.pow(x/f, f);
      case 9: 
          f = 1.0 / (l+1);
          return Math.pow(x, f) / Math.sqrt(f);
      case 8: 
          f = 1.0 / (l+1);
          return Math.pow(x, f) / Math.sqrt(f);
      case 7: 
          f = 1.0 / (l+1);
          return Math.pow(x, f) / f;
      case 6: 
          f = 1.0 / l;
          return Math.pow(x, f) / Math.sqrt(f); 
      case 5: 
          f = 1.0 / l;
          return Math.pow(x, f) / f; 
      case 3: 
          f = 1.0 / l;
          return Math.pow(x, f);
      case 2: 
      case 1:
          f = 1.0 / l;
          return Math.pow(x/f, f);
      case 4: 
          f = 1.0 / l;
          return Math.pow(x*f, f);
    }
    return x;
  }

  public String getProfileName(int i)
  {
    if ( i < 0 || i >= profiles.size() )
      return NOLANGNAME;
    return ((NGramProfile)profiles.get(i)).getName();
  }

  public static boolean isSeparator(char ch)
  {
    return ( ch <= ' '
              || Character.isWhitespace(ch)
              || Character.isDigit(ch)
              || ".!?:,;".indexOf(ch) >= 0
            );
  }

  public static String reverse(CharSequence seq)
  {
    char[] chrs = new char[seq.length()];
    for(int i = 0, j = seq.length(); --j >= 0; i++)
      chrs[i] = seq.charAt(j);
    return new String(chrs);
  }

  /**
   * Note this class returns a complete match result, for the
   * sake of thread safety!
   */
  public RankResult rank(NGramMetric metric, NGramProfile profile)
  {
    Iterator it = profiles.iterator();
    String res = null;
    double[] scores = new double[profiles.size()];
    for(int i = 0; i < profiles.size(); i++)
      scores[i] = metric.diff(profile, (NGramProfile)(profiles.get(i)));
    return new SimpleRankResult(scores, false);
  }

  private class SimpleRankResult
    implements RankResult
  {
    private double scores[];
    private NGramProfile[] profs;
    private double remain;

    public SimpleRankResult(double[] scorex, boolean inverse)
    {
      scores = new double[scorex.length];
      System.arraycopy(scorex, 0, scores,0, scorex.length);
      profs = new NGramProfile[scores.length];
      remain = 1.0;
      for(int i = 0; i < scores.length; i++)
      {
        NGramProfile prof = ((NGramProfile)profiles.get(i));
        double m = scores[i];
        remain -= m;
        int j = i;
        while ( --j >= 0 && (inverse ^ (m < scores[j])) )
        {
          scores[j+1] = scores[j];
          profs[j+1] = profs[j];
        }
        scores[j+1] = m;
        profs[j+1] = prof;
      }
    }

    public NGramProfiles getProfiles()
    {
        return NGramProfiles.this;
    }

    public double getScore(int pos)
    {
      if ( pos == getLength() )
        return remain;
      if ( pos < 0 )
        pos += getLength();
      return scores[pos];
    }

    public String getName(int pos)
    {
      if ( pos == getLength() )
        return NOLANGNAME;
      else if ( pos < 0 )
        pos += getLength();
      return profs[pos].getName();
    }

    public int getLength()
    {
      return profs.length;
    }
  }

  public int getProfileCount()
  {
    return profiles.size();
  }

  public Set getAllNGrams()
  {
      // XXX make this read only or is this slowing down too much?
      return allNGrams;
  }

  public interface RankResult
  {
    public NGramProfiles getProfiles();
    public int getLength();
    public double getScore(int pos);
    public String getName(int pos);
  }

  public interface Ranker
  {
    public RankResult getRankResult();
    public void reset();
    public void flush();
    public void account(CharSequence seq, int pos);
    public void account(CharSequence seq);
    public void account(Reader reader)  
      throws IOException;
  }
}

