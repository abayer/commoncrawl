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
 * A very light (and therefore fast, efficient) implementation
 * of a CharSequence. Tailored for Ngram purpose.
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class LightCharSequence
  implements CharSequence
{
  private char[] chars;

  private int hashCode;

  public LightCharSequence(CharSequence seq)
  {
    chars = new char[seq.length()];
    for(int i = 0; i < chars.length; i++)
        chars[i] = seq.charAt(i);
    calcHashCode();
  }

  public LightCharSequence(CharSequence seq, int start, int end)
  {
    chars = new char[end - start];
    for(int i = start; i < end; i++)
      chars[i] = seq.charAt(i - start);
    calcHashCode();
  }

  protected void calcHashCode()
  {
    hashCode = 42 + length();
    for(int i = 0; i < chars.length; i++)
      hashCode = hashCode * 0x110001 + charAt(i) + 1;
  }

  public char charAt(int pos)
  {
    return chars[pos];
  }

  public int length()
  {
    return chars.length;
  }

  public CharSequence subSequence(int start, int end)
  {
    return new LightCharSequence(this, start, end);
  }

  public String toString()
  {
    return new String(chars);
  }

  public boolean equals(Object o)
  {
    if ( !(o instanceof LightCharSequence) )
      return false;
    LightCharSequence l = (LightCharSequence)o;
    if ( l.length() != length() )
      return false;
    for(int i = 0; i < length(); i++)
      if ( l.charAt(i) != charAt(i) )
        return false;
    return true;
  }

  public int hashCode()
  {
    return hashCode;
  }
}

