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

package org.jactr.core.chunk;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.references.IReferences;

/**
 * Contains all the methods for manipulating the subsymbolic parameters
 * necessary for learning and activation computation.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ISubsymbolicChunk extends
    org.jactr.core.utils.parameter.IParameterized, IAdaptable
{

  // subsymbolic parameter strings
  /**
   * Description of the Field
   */
  public final static String CREATION_TIME         = "CreationTime";

  /**
   * Description of the Field
   */
  public final static String TIMES_NEEDED          = "TimesNeeded";

  /**
   * Description of the Field
   */
  public final static String TIMES_IN_CONTEXT      = "TimesInContext";

  /**
   * Description of the Field
   */
  public final static String REFERENCE_TIMES       = "ReferenceTimes";

  /**
   * Description of the Field
   */
  public final static String REFERENCE_COUNT       = "ReferenceCount";

  /**
   * Description of the Field
   */
  public final static String BASE_LEVEL_ACTIVATION = "BaseLevelActivation";

  /**
   * Description of the Field
   */
  public final static String SPREADING_ACTIVATION  = "SpreadingActivation";

  public final static String SOURCE_ACTIVATION     = "SourceActivation";

  /**
   * Description of the Field
   */
  public final static String ACTIVATION            = "TotalActivation";

  /**
   * @return chunk's creation time
   */
  public double getCreationTime();

  /**
   * set the creation time and pass on an event.
   * 
   * @param time
   *            The new creationTime value
   */
  public void setCreationTime(double time);

  /**
   * Sets the number of times that this chunk has been needed in a buffer other
   * than the goal buffer. This is used in the calculation of the strengths of
   * association. passes on an event.
   * 
   * @param needed
   *            The new timesNeeded value
   */
  public void setTimesNeeded(double needed);

  /**
   * Return the number of times that this chunk has been needed in a buffer
   * other than the goal buffer.
   * 
   * @return The timesNeeded value
   */
  public double getTimesNeeded();

  /**
   * Description of the Method
   * @param value TODO
   */
  public void incrementTimesNeeded(double value);

  /**
   * Set number of times this chunk has been a slot value of a chunk in the goal
   * buffer. passes on an event.
   * 
   * @param context
   *            The new timesInContext value
   */
  public void setTimesInContext(double context);

  /**
   * Get the number of times this chunk has been a slot value of a chunk in the
   * goal buffer.
   * 
   * @return The timesInContext value
   */
  public double getTimesInContext();

  /**
   * Description of the Method
   * @param value TODO
   */
  public void incrementTimesInContext(double value);

  /**
   * The org.jactr.misc.ReferenceList class maintains an optimized list of
   * access times and access counts. These values are used to compute the basic
   * power-law functions of base-level decay etc. This returns the actual list,
   * programmers should be wary not to corrupt the list.
   * 
   * @return The referenceList value
   */
  public IReferences getReferences();

  /**
   * @param when
   * 
   */
  public void encode(double when);

  /**
   * Is called whenever the chunk is accessed (check via a condition or modified
   * via an action in a IProduction) ? it can only be called once per cycle
   * since the ReferenceList maintains a listing of unique access times.
   * @param time TODO
   */
  public void accessed(double time);

  /**
   * Set the source activation of this chunk. This chunk must be a slot value of
   * another chunk that is in an activation propogating buffer (the goal buffer
   * for instance). This is called by the IActivationBuffer after a call to
   * IActivationBuffer.setSourceChunk(IChunk).
   * @param sourceBuffer TODO
   * @param source
   *            The new sourceActivation value
   */
  public void setSourceActivation(IActivationBuffer sourceBuffer, double source);

  /**
   * returns the transient amount of source activation
   * 
   * @return The sourceActivation value
   */
  public double getSourceActivation();

  /**
   * return the buffer specific portion of source activation. 0 if undefined.
   * 
   * @param buffer
   * @return
   */
  public double getSourceActivation(IActivationBuffer buffer);

  /**
   * Return the current base level activation. This is a function of the current
   * model time and the recency and frequency of access of the chunk.
   * 
   * @return The baseLevelActivation value
   */
  public double getBaseLevelActivation();

  /**
   * Set the base-level activation. If IModel.isBaseLevelLearningEnabled() is
   * true, this value will change on a cycle basis.
   * 
   * @param base
   *            The new baseLevelActivation value
   */
  public void setBaseLevelActivation(double base);

  /**
   * Get the transient amount of spreading activation to this chunk. The
   * spreading activation passes from any chunk with a source activation != 0 to
   * all of its associated chunks (via. Actr.chunk.four.Link).
   * 
   * @return The spreadingActivation value
   */
  public double getSpreadingActivation();

  /**
   * get the transient, random activation of this chunk (calculated at most,
   * once per cycle)
   * 
   * @return
   */
  public double getRandomActivation();

  /**
   * Sets the spreadingActivation attribute of the ISubsymbolicChunk object
   * 
   * @param spread
   *            The new spreadingActivation value
   */
  public void setSpreadingActivation(double spread);

  /**
   * Set the total activation. If none of the activation learning equations are
   * enabled, this value will be passed on to the base-level activation, and all
   * other values will be zeroed.
   * 
   * @param act
   *            The new activation value
   */
  public void setActivation(double act);

  /**
   * Return the total activation (summation of base-level, spreading, and
   * noise). The activation values are only computed once per cycle and then
   * cached. If this call occurs and the value has not been calculated, it will
   * be calculated, cached and then returned.
   * 
   * @return The activation value
   */
  public double getActivation();

  /**
   * Description of the Method
   */
  public void dispose();

  public IChunk getParentChunk();
}
