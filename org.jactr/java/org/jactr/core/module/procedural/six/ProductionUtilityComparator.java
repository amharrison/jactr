/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.module.procedural.six;

import org.jactr.core.production.IProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;

/*
 * sorts descending based on current expected Gain
 */
/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class ProductionUtilityComparator implements
    java.util.Comparator<IProduction>
{

  /**
   * slightly different comparator.
   */
  public int compare(IProduction one, IProduction two)
  {
    if (one == two) return 0;

    ISubsymbolicProduction6 sp1 = (ISubsymbolicProduction6) one
        .getSubsymbolicProduction();
    ISubsymbolicProduction6 sp2 = (ISubsymbolicProduction6) two
        .getSubsymbolicProduction();

    double base1 = sp1.getExpectedUtility();
    double base2 = sp2.getExpectedUtility();

    /*
     * if utility learning is not on, we will only have the utility value to
     * work with, not expected.
     */
    if (Double.isNaN(base1)) base1 = sp1.getUtility();
    if (Double.isNaN(base2)) base2 = sp1.getUtility();

    if (base1 == base2)
      return 0;
    else if (base1 < base2) return 1;

    return -1;
  }

  /**
   * Description of the Method
   * 
   * @param one
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  @Override
  public boolean equals(Object one)
  {
    return false;
  }
}
