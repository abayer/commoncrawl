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
 * Cosine Metric
 * This is nicely valued between zero and one
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class CosMetric
  implements NGramMetric
{
  public double diff(NGramProfile p1, NGramProfile p2)
  {
    double sum = 0.0;
    double s1  = 0.0;
    double s2  = 0.0;

    // Treat all NGrams contained in p1;
    Iterator i = p1.getSorted();
    while (i.hasNext())
    {
      NGram ng1 = (NGram) i.next();
      NGram ng2 = p2.get(ng1);
      double c1 = ng1.getCount();
      s1  += c1 * c1;
      if ( ng2 != null )
      {
        double c2 = ng2.getCount();
        sum += c1 * c2;
        s2  += c2 * c2;
      }
    }
    // Treat NGrams contained ONLY in p2
    i = p2.getSorted();
    while (i.hasNext())
    {
      NGram ng2 = (NGram) i.next();
      if ( p1.get(ng2) == null )
      {
        double c2 = ng2.getCount();
        s2  += c2 * c2;
      }
    }
    return 1.0 - sum / Math.sqrt(s1*s2);
  }
}
