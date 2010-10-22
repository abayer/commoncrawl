///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2006, Frank S. Nestel, All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package de.spieleck.app.ngramj;

/**
 * Dummy Interface to make verious version information available to runtime.
 * <b>Note:</b>Version.java is created automagically from Version.java.tpl
 *
 * <p><a href="$URL: https://svn.sourceforge.net/svnroot/ngramj/src/de/spieleck/app/ngramj/Version.java.tpl $">$URL: https://svn.sourceforge.net/svnroot/ngramj/src/de/spieleck/app/ngramj/Version.java.tpl $</a></p>
 *
 * @author Frank S. Nestel
 * @author $Author: nestefan $
 * @version $Revision: 13 $ $Date: 2009-12-23 22:49:31 +0100 (Mi, 23 Dez 2009) $ $Author: nestefan $
 */
public interface Version
{
    public final static String PROGRAM = "NGramJ v1";
    public final static String ID = "@ID@";
    public final static String VERSION = "@version@";
    public final static String REVISION = "$Revision: 13 $";
    public final static String DATE = "$Date: 2009-12-23 22:49:31 +0100 (Mi, 23 Dez 2009) $";
    public final static String BUILD_STAMP = "@buildstamp@";
    public final static String LONG_VERSION = "NGramJ "+VERSION
                                            +"/"+REVISION+"/"+BUILD_STAMP+"";
}
