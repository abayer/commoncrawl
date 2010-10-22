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

import de.spieleck.app.cngram.NGramMetric;
import de.spieleck.app.cngram.NGramProfile;
import de.spieleck.app.cngram.NGram;

/**
 * Weight Cosine Metric
 * This is also nicely valued between zero and one.
 * @author frank nestel
 * @author $Author: nestefan $
 * @version $Revision: 2 $ $Date: 2006-03-27 23:00:21 +0200 (Mo, 27 Mrz 2006) $ $Author: nestefan $
 */
public class CosWeightMetric
  implements NGramMetric
{
  private final double blankWeight;
  private final int lengthLimit;

  public CosWeightMetric(double blankWeight, int lengthLimit)
  {
      this.blankWeight = blankWeight;
      this.lengthLimit = lengthLimit;
  }

  public CosWeightMetric()
  {
      this(0.0, 3);
  }

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
      if ( ng2 != null )
      {
        double c2 = ng2.getCount();
        double weight = weight(ng1, c1, c2);
        sum += c1 * c2 * weight;
        s1  += c1 * c1 * weight;
        s2  += c2 * c2 * weight;
      }
      else
      {
        double weight = weight(ng1, c1, 0.0);
        s1  += c1 * c1 * weight;
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
        double weight = weight(ng2, 0.0, c2);
        s2  += c2 * c2 * weight;
      }
    }
    return 1.0 - sum / Math.sqrt(s1*s2);
  }

  public double weight(NGram ng, double w1, double w2)
  {
    int len = ng.length();
    if ( len > lengthLimit )
        return 0.0;
    boolean hasBlank = ng.charAt(0) == '_' || ng.charAt(len-1) == '_';
    if ( blankWeight == 0.0 && hasBlank ) 
        return 0.0;
    double res = ( len == 1 ? 1.0 : len == 2 ? 3.5 : 2.0 ) /(Math.sqrt(w1+w2)); 
    if ( hasBlank ) res *= blankWeight;
    return res;
  }

  public String toString()
  {
      return "CosWeightMetric("+blankWeight+","+lengthLimit+")";
  }
}
