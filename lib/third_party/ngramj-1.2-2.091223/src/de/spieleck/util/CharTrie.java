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
package de.spieleck.util;

/**
 * A simple interface to an index for an array of
 * CharSequence objects.
 *
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public interface CharTrie
{
  /** Fictive end-of-string character. XXX Should be impossible character. */
  public final static char END_CHAR = (char) 0;

  /** Trie node not bound to the array. */
  public final static int NO_INDEX = -1;


  /**
   * Traverse to the subtrie below the current position,
   * guided by the presented character.
   */
  public CharTrie subtrie(char c);

  /**
   * Traverse to the subtrie below the current position,
   * guided by the presented CharSequence.
   */
  public CharTrie subtrie(CharSequence chs);

  /**
   * Return the index into the initial array, held by this
   * node. -1 means no such index.
   */
  public int getId();

}

