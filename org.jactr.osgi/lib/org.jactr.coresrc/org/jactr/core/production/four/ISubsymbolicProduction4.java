/*
 * Created on Oct 24, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.production.four;

import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.utils.references.IReferences;

public interface ISubsymbolicProduction4 extends ISubsymbolicProduction
{
  public final static String CREATION_CYCLE  = "CreationCycle";

  static public final String SUCCESS         = "Success";

  static public final String FAILURE         = "Failure";

  /**
   * Description of the Field
   */
  public final static String P               = "P";

  /**
   * Description of the Field
   */
  public final static String C               = "C";

  /**
   * Description of the Field
   */
  public final static String EFFORT_COUNT    = "EffortCount";

  /**
   * Description of the Field
   */
  public final static String EFFORT_TIMES    = "EffortTimes";

  /**
   * Description of the Field
   */
  public final static String PRIOR_SUCCESSES = "PriorSuccessCount";

  /**
   * Description of the Field
   */
  public final static String PRIOR_FAILURES  = "PriorFailureCount";

  /**
   * Description of the Field
   */
  public final static String PRIOR_EFFORTS   = "PriorEffortCount";

  /**
   * Description of the Field
   */
  public final static String SUCCESS_COUNT   = "SuccessCount";

  /**
   * Description of the Field
   */
  public final static String SUCCESS_TIMES   = "SuccessTimes";

  /**
   * Description of the Field
   */
  public final static String FAILURE_COUNT   = "FailureCount";

  /**
   * Description of the Field
   */
  public final static String FAILURE_TIMES   = "FailureTimes";

  /**
   * Description of the Field
   */
  public final static String GAIN            = "ExpectedGain";

  /**
   * Description of the Field
   */
  public final static String REFERENCE_TIMES = "ReferenceTimes";

  /**
   * Description of the Field
   */
  public final static String REFERENCE_COUNT = "ReferenceCount";

  /**
   * what production cycle was this production created during
   */
  public int getCreationCycle();

  /**
   * what production cycle was this production created during
   */
  public void setCreationCycle(int i);

  /**
   * at what times did this production's firing result in a success
   */
  public IReferences getSuccesses();

  /**
   * at what times did this production's firing result in a failure
   */
  public IReferences getFailures();

  /**
   * 
   */
  public IReferences getEfforts();

  /**
   * the number of prior successes, (sans actual access times, often inherited
   * from parents)
   */
  public int getPriorSuccesses();

  /**
   * the number of prior failures
   */
  public int getPriorFailures();

  /**
   * the number of prior attempts
   */
  public double getPriorEfforts();

  /**
   * Sets the priorSuccesses
   */
  public void setPriorSuccesses(int successes);

  /**
   * Sets the priorFailures
   */
  public void setPriorFailures(int failures);

  /**
   * Sets the priorEfforts
   */
  public void setPriorEfforts(double efforts);

  /**
   * compute and return the current expected gain based on P, G, and C
   */
  public double getExpectedGain();

  /**
   * compute the probability of success
   */
  public double getP();

  /**
   * get the cost of firing the production
   */
  public double getC();

  /**
   * set the probability of success
   */
  public void setP(double p);

  /**
   * set the cost
   */
  public void setC(double c);
}
