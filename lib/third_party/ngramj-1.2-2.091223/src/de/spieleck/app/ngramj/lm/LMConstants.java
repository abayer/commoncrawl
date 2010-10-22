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

/**
 * Fairly abstract interface to model an ranking of NGrams.
 */
public interface LMConstants
{
    /**
     * Theses bytes seem to be considered skipable by text_cat in PERL.
     */
    public static final byte[] SKIPABLE = new byte[]
                            {32,48,49,50,51,52,53,54,55,56,57,10,13};

    /**
     * Effectively used NGrams from a text.
     */
    public final static int USEDNGRAMS = 400;
}

