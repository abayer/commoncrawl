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

import java.util.Hashtable;

/**
 * The "structure" of phone keys.
 */
public class PhoneKeys
{
    protected final static Hashtable replacers = new Hashtable(10);
    static
    {
        replacers.put("1",new byte[]{(byte)'1'});
        replacers.put("2",new byte[]{(byte)'2',(byte)'a',(byte)'b',(byte)'c'});
        replacers.put("3",new byte[]{(byte)'3',(byte)'d',(byte)'e',(byte)'f'});
        replacers.put("4",new byte[]{(byte)'4',(byte)'g',(byte)'h',(byte)'i'});
        replacers.put("5",new byte[]{(byte)'5',(byte)'j',(byte)'k',(byte)'l'});
        replacers.put("6",new byte[]{(byte)'6',(byte)'m',(byte)'n',(byte)'o'});
        replacers.put("7",new byte[]{(byte)'7',(byte)'p',(byte)'q',(byte)'r',(byte)'s'});
        replacers.put("8",new byte[]{(byte)'8',(byte)'t',(byte)'u',(byte)'v'});
        replacers.put("9",new byte[]{(byte)'9',(byte)'w',(byte)'x',(byte)'y',(byte)'z'});
        replacers.put("0",new byte[]{(byte)'0',(byte)' ',(byte)'.',(byte)'-'});
    }
}

