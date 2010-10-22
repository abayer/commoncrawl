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

/**
 * store NGram
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class NGramImpl
  extends LightCharSequence
  implements NGram
{
  private char[] chars;

  private int count = 0;

  public NGramImpl(CharSequence seq)
  {
    super(seq);
  }

  public NGramImpl(CharSequence seq, int count)
  {
    this(seq);
    setCount(count);
  }

  public int getCount()
  {
    return count;
  }

  public void setCount(int count)
  {
    this.count = count;
  }

  public int compareTo(Object o)
  {
    int d = ((NGramImpl) o).count - count;
    if ( d != 0 )
      return d;
    return NGramProfile.CHAR_SEQ_COMPARATOR.compare(this,(NGramImpl)o);
  }

  public void inc()
  {
    count++;
  }

  public String toString()
  {
    return super.toString();
  }
}

