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

import java.util.Iterator;

/**
 * ALPHA modified Chi^2 Metric without rectification of classes
 * and with centrification between the two profiles.
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class C2xMetric
  implements NGramMetric
{
  public final static double ALPHA = 1.0; // 0.0 is exact

  public double diff(NGramProfile p1, NGramProfile p2)
  {
    double sum = 0.0;
    int norm1 = p1.getNormalization();
    int norm2 = p2.getNormalization();
    int norm = norm1 + norm2;
    double d1 = ((double)norm1) / norm;
    double d2 = ((double)norm2) / norm;
    double u;

    // Treat all NGrams contained in p1;
    Iterator i = p1.getSorted();
    while (i.hasNext())
    {
      NGram ng1 = (NGram) i.next();
      NGram ng2 = p2.get(ng1);
      int c1 = ng1.getCount();
      int c2 = 0;
      if ( ng2 != null )
        c2 = ng2.getCount();
      u = d1 * (c1+c2);
      sum += (c1 - u)*(c1 - u)/(u + ALPHA);
      u = d2 * (c1+c2);
      sum += (c2 - u)*(c2 - u)/(u + ALPHA);
    }

    // Treat NGrams contained ONLY in p2
    i = p2.getSorted();
    while (i.hasNext())
    {
      NGram ng2 = (NGram) i.next();
      if ( p1.get(ng2) == null )
      {
        // int c1 = 0;
        int c2 = ng2.getCount();
        u = d1 * c2; // = d1 * (c1+c2);
        sum += u*u / (u + ALPHA); // = (c1 - u)*(c1 - u)/u;
        u = d2 * c2; // = d2 * (c1+c2);
        sum += (c2 - u)*(c2 - u)/ (u + ALPHA);
      }
    }

    return sum/norm*(1.0+ALPHA); // "Wrong" but nicer normalization
  }
}
