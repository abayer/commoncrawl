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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Set;

/**
 * Actual implementation of a NGramProfile
 * 
 * Methods are provided to build new NGramProfiles profiles.
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 13 $ $Date: 2009-12-23 22:49:31 +0100 (Mi, 23 Dez 2009) $ $Author: nestefan $
 */
public class NGramProfileImpl
  implements NGramProfile
{
  public final static int MODE_NOSINGLEBLANK = 0;
  public final static int MODE_NOBLANK = 1;
  public final static int MODE_BLANK = 2;

  /** separator char */
  public static final char SEPARATOR = '_';

  /** default min length of ngram. */
  public static final int DEFAULT_MIN_NGRAM_LENGTH = 1;

  /** default max length of ngram */
  public static final int DEFAULT_MAX_NGRAM_LENGTH = 5;

  /** Name this Profile. */
  private String name;
  
  /** Internal to provide sorted access. */
  private volatile NGram[] sorted = null;

  /** Internal to provide ordered access. */
  private volatile NGram[] ordered = null;

  /** Minimum length for an ngram. */
  private int minNGramLength;

  /** Maximum length for an ngram. */
  private int maxNGramLength;  

  /** Norm factor for this profile. */
  private int normalization = 0;

  /** Map to store char sequences and ngrams */
  private HashMap ngrams = null;

  /** */
  private Set restricted = null;

  /**
   * Create a new ngram profile with default lengths.
   * 
   * @param name Name of profile
   */
  public NGramProfileImpl(String name) {
    this(name, DEFAULT_MIN_NGRAM_LENGTH, DEFAULT_MAX_NGRAM_LENGTH);
  }

  /**
   * Create a new ngram profile
   * 
   * @param name Name of profile
   * @param minlen min length of ngram sequences
   * @param maxlen max length of ngram sequences
   */
  public NGramProfileImpl(String name, int minlen, int maxlen)
  {
    ngrams = new HashMap();
    this.maxNGramLength = maxlen;
    this.minNGramLength = minlen;
    setName(name);
  }

  public void setRestricted(Set restricted)
  {
      this.restricted = restricted;
  }

  /**
   * Analyze a piece of text
   * 
   * @param text the text to be analyzed
   */
  public void analyze(CharSequence text)
  {
    StringBuffer word = new StringBuffer(30).append(SEPARATOR);
    for (int i = 0; i < text.length(); i++)
    {
      char c = Character.toLowerCase(text.charAt(i));
      if (Character.isLetter(c))
      {
        word.append(c);
      }
      else
      {
        addAnalyze(word);
        word.setLength(1);
      }
    }
    addAnalyze(word);
  }

  private void addAnalyze(StringBuffer word)
  {
    if (word.length() > 1)
    {
      word.append(SEPARATOR);
      addNGrams(word);
    }
  }

  public void clear()
  {
    if ( ngrams != null )
    {
        ngrams.clear();
    }
    normalization = 0;
    ordered = sorted = null;
  }

  public int getCount()
  {
    return ngrams.size();
  }

  public int getNormalization()
  {
    return normalization;
  }

  /**
   * Add ngrams from a single word to this profile
   * 
   * @param word
   */
  public void addNGrams(CharSequence word)
  {
    for (int i = minNGramLength; i <= maxNGramLength && i < word.length(); i++)
    {
      addNGrams(word, i);
    }
  }

  /**
   * @param word
   * @param n sequence length
   */
  private void addNGrams(CharSequence word, int n)
  {
    for (int i = 0, end = word.length() - n; i <= end; i++)
    {
      CharSequence cs = word.subSequence(i, i + n);
      NGram nge = (NGram) ngrams.get(cs);
      if ( nge == null )
      {
        nge = new NGramImpl(cs);
        if ( restricted != null && !restricted.contains(nge) )
          continue;
        ngrams.put(cs, nge);
        ordered = null; // A new element invalidates the ordered access
      }
      nge.inc();
      normalization++;
      sorted = null;
    }
  }

  public Iterator getSorted()
  {
    if (sorted == null)
    {
      sorted = (NGram[]) ngrams.values().toArray(NO_NGRAM);
      Arrays.sort(sorted);
    }
    return Arrays.asList(sorted).iterator();
  }

  public NGram get(CharSequence seq)
  {
    if ( ordered == null )
    {
      ordered = (NGram[]) ngrams.values().toArray(NO_NGRAM);
      Arrays.sort(ordered, CHAR_SEQ_COMPARATOR);
    }
    int i = Arrays.binarySearch(ordered, seq, CHAR_SEQ_COMPARATOR);
    if ( i < 0 )
        return null;
    return ordered[i];
  }

  /**
   * Return ngramprofile as text
   * 
   * @return ngramprofile as text
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer(2000);

    Iterator i = getSorted();

    s.append("NGramProfile: ").append(name).append('\n');
    while (i.hasNext())
    {
      NGram entry = (NGram) i.next();
      s.append(entry).append(' ').append(entry.getCount()).append('\n');
    }
    return s.toString();
  }

  /**
   * Loads a ngram profile from InputStream (assumes UTF-8 encoded content)
   */
  public void load(InputStream is)
    throws IOException
  {
      load(is, MODE_NOSINGLEBLANK);
  }


  /**
   * Loads a ngram profile from InputStream (assumes UTF-8 encoded content)
   */
  public void load(InputStream is, int mode)
    throws IOException
  {
    BufferedReader bis = new BufferedReader(new InputStreamReader(is,"UTF-8"));
    String line;
    ngrams.clear();
    int storeCount = -1;
    String eliminators = ""; //XXX ad hoc correction of reference
    int discards = 0;
    while ((line = bis.readLine()) != null)
    {
      line = line.trim();
      if (line.length() < 2 )
        continue;
      // # starts a comment line
      // - starts a correction line
      if (line.charAt(0) == '-') 
      {
        eliminators += line.charAt(1);
      }
      else if (line.startsWith(FINISHREAD_STR))
      {
        break;
      }
      else if (line.charAt(0) != '#')
      {
        int spacepos = line.indexOf(' ');
        String ngramsequence = line.substring(0, spacepos).trim().replace('_',' ');
        if ( mode == MODE_NOSINGLEBLANK && " ".equals(ngramsequence) )
        {
          // Single spaces are so paar as n-grams (1-grams), that
          // we throw them away!!
          continue;
        }
        else if ( mode == MODE_NOBLANK && ngramsequence.indexOf(' ') >= 0 ) {
            continue;
        }
        int count = Integer.parseInt(line.substring(spacepos + 1).trim());
        if (line.startsWith(NORMALIZATION_STR))
        {
          storeCount = count;
        }
        else if ( ngramsequence.length() >= minNGramLength
                && ngramsequence.length() <= maxNGramLength )
        {
          // XXX Check for eliminations!
          int l;
          for(l = 0; l < eliminators.length(); l++)
          {
            if ( ngramsequence.indexOf(eliminators.charAt(l)) >= 0 )
              break;
          }
          if ( l < eliminators.length() )
          {
            discards++;
// System.out.println(" "+discards+".DISCARD --> <"+ngramsequence+"> <"+eliminators.charAt(l)+">");
          }
          else
          {
// System.out.println("<"+ngramsequence+"> "+" "+((int)ngramsequence.charAt(ngramsequence.length()-1))+" "+count);            
            NGram en = new NGramImpl(ngramsequence, count);
            ngrams.put(ngramsequence, en);
            normalization += count;
          }
        }
      }
    }
    if ( storeCount != -1 )
    {
      if ( storeCount != normalization )
          System.err.println(" WARNING "+storeCount+" != "+normalization);
      //XXX Which one is better :-) normalization = storeCount;
    }
// if ( discards > 0 ) System.err.println("  "+getName()+" has "+discards+" discards.");
  }

  /**
   * Create a new Language profile from (preferably quite large) text file
   * 
   * @param name name of profile
   * @param is
   * @param encoding encoding of stream
   */
  public static NGramProfileImpl createProfile(String name, InputStream is,
      String encoding)
    throws IOException
  {
    NGramProfileImpl newProfile = new NGramProfileImpl(name);
    BufferedReader bis = new BufferedReader(new InputStreamReader(is,encoding));
    String line;
    while ( ( line = bis.readLine() ) != null )
    {
      newProfile.analyze(line);
    }
    return newProfile;
  }

  /**
   * Writes NGramProfile content into OutputStream, content is outputted with
   * UTF-8 encoding
   * 
   * @param os Stream to output to
   * @throws IOException
   */

  public void save(OutputStream os) throws IOException
  {
    Iterator i = getSorted();
    os.write(("# NgramProfile generated at " + new Date() + " for Language Identification\n")
            .getBytes());
    os.write((NORMALIZATION_STR+" " + normalization + "\n").getBytes());
    while (i.hasNext()) {
      NGram e = (NGram) i.next();
      String line = e + " " + e.getCount() + "\n";
      os.write(line.getBytes("UTF-8"));
    }
    os.flush();
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }
}
