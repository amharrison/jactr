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
package org.jactr.core.chunk.five;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.basic.AbstractChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.four.DefaultSubsymbolicChunk4;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.parameter.ACTRParameterHandler;
import org.jactr.core.utils.parameter.CollectionParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.core.utils.parameter.SimilarityParameterHandler;

/**
 * default implementation of ISubsymbolicChunk
 * 
 * @author harrison
 * @created December 4, 2002
 * @see org.jactr.core.chunk.ISubsymbolicChunk
 */
public class DefaultSubsymbolicChunk5 extends DefaultSubsymbolicChunk4
    implements ISubsymbolicChunk5
{

  /**
   * Description of the Field
   * 
   * @since
   */
  public final static String                          SIMILARITY_ACTIVATION = "SimilarityActivation";

  private static transient Log                        LOGGER                = LogFactory
                                                                                .getLog(DefaultSubsymbolicChunk5.class
                                                                                    .getName());

  protected double                                    _similarityActivation;

  protected Map<IChunk, Double>                       _similarityMap;

  /**
   * last pattern used to calculate similarity activation
   */
  protected transient SoftReference<ChunkTypeRequest> _lastPattern          = new SoftReference<ChunkTypeRequest>(
                                                                                null);

  public DefaultSubsymbolicChunk5(AbstractChunk parentChunk)
  {
    super(parentChunk);
    _similarityMap = new HashMap<IChunk, Double>();
    setSimilarity(parentChunk, 1);
  }

  /**
   * Sets the Parameter attribute of the DefaultSubsymbolicChunk5 object
   * 
   * @param key
   *          The new Parameter value
   * @param value
   *          The new Parameter value
   * @since
   */
  @Override
  public void setParameter(String key, String value)
  {

    if (SIMILARITY_ACTIVATION.equalsIgnoreCase(key))
      setSimilarityActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (SIMILARITIES.equalsIgnoreCase(key))
    {
      ACTRParameterHandler actrph = new ACTRParameterHandler(getParentChunk()
          .getModel());
      SimilarityParameterHandler sph = new SimilarityParameterHandler(actrph);
      CollectionParameterHandler<Object[]> aph = new CollectionParameterHandler<Object[]>(
          sph);
      for (Object[] similarity : aph.coerce(value))
        setSimilarity((IChunk) similarity[0], (Double) similarity[1]);
    }
  }

  /**
   * Sets the Similarity attribute of the DefaultSubsymbolicChunk5 object
   * 
   * @param c
   *          The new Similarity value
   * @param value
   *          The new Similarity value
   * @since
   */
  public void setSimilarity(IChunk c, double value)
  {
    if (c == null) return;

    double oldValue = getSimilarity(c);
    if (oldValue == value) return;

    try
    {
      writeLock().lock();
      _similarityMap.put(c, value);
    }
    finally
    {
      writeLock().unlock();
    }
    if (_parentChunk.hasListeners())
      _parentChunk.dispatch(new ChunkEvent(_parentChunk, c, oldValue, value));
  }

  /**
   * Sets the SimilarityActivation attribute of the DefaultSubsymbolicChunk5
   * object
   * 
   * @param act
   *          The new SimilarityActivation value
   * @since
   */
  public void setSimilarityActivation(double act)
  {
    double oldSim = getSimilarityActivation();

    try
    {
      writeLock().lock();
      _similarityActivation = act;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), SIMILARITY_ACTIVATION,
          oldSim, _similarityActivation));
  }

  /**
   * this uses a JIT get parameter.. why? to set them in their respective
   * methods could grind the system to a halt since many of these values would
   * change many times during a model run.
   * 
   * @param key
   *          Description of Parameter
   * @return The Parameter value
   * @since
   */
  @Override
  public String getParameter(String key)
  {
    if (SIMILARITIES.equalsIgnoreCase(key))
    {
      ACTRParameterHandler actrph = new ACTRParameterHandler(getParentChunk()
          .getModel());
      SimilarityParameterHandler sph = new SimilarityParameterHandler(actrph);
      CollectionParameterHandler<Object[]> aph = new CollectionParameterHandler<Object[]>(
          sph);
      return aph.toString(getSimilarities(null));
    }
    else if (SIMILARITY_ACTIVATION.equalsIgnoreCase(key))
      return ParameterHandler.numberInstance().toString(
          getSimilarityActivation());

    return super.getParameter(key);
  }

  /**
   * Gets the PossibleParameters attribute of the DefaultSubsymbolicChunk5
   * object
   * 
   * @return The PossibleParameters value
   * @since
   */
  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> rtn = super.getPossibleParameters();
    rtn.add(SIMILARITIES);
    rtn.add(SIMILARITY_ACTIVATION);
    return rtn;
  }

  /**
   * Gets the Similarity attribute of the DefaultSubsymbolicChunk5 object
   * 
   * @param c
   *          Description of Parameter
   * @return The Similarity value
   * @since
   */
  public double getSimilarity(IChunk c)
  {
    if (c == null) return Double.NaN;

    try
    {
      readLock().lock();
      Double rtn = _similarityMap.get(c);
      if (rtn == null) return Double.NaN;
      return rtn.doubleValue();
    }
    finally
    {
      readLock().unlock();
    }

  }

  /**
   * Gets the Similarities attribute of the DefaultSubsymbolicChunk5 object
   * 
   * @return The Similarities value
   * @since
   */
  public Collection<Object[]> getSimilarities(Collection<Object[]> container)
  {
    if (container == null) container = new ArrayList<Object[]>();

    try
    {
      readLock().lock();
      for (Map.Entry<IChunk, Double> entry : _similarityMap.entrySet())
        container.add(new Object[] { entry.getKey(), entry.getValue() });
      return container;
    }
    finally
    {
      readLock().unlock();
    }
  }

  /**
   * Gets the SimilarityActivation attribute of the DefaultSubsymbolicChunk5
   * object
   * 
   * @return The SimilarityActivation value
   * @since
   */
  public double getSimilarityActivation()
  {
    try
    {
      readLock().lock();
      return _similarityActivation;
    }
    finally
    {
      readLock().unlock();
    }
  }

  /*
   * @bug is source activation added to total activation?? No : the self
   * referential associative link will handle that for us
   */
  /**
   * Gets the Activation attribute of the DefaultSubsymbolicChunk5 object
   * 
   * @param p
   *          Description of Parameter
   * @return The Activation value
   * @since
   */
  public double getActivation(ChunkTypeRequest p)
  {
    // calculate the usual values..
    refreshActivationValues();

    boolean newPattern = p != _lastPattern.get();
    if (newPattern)
    {
      setSimilarityActivation(computeSimilarityActivation(p));
      _lastPattern = new SoftReference<ChunkTypeRequest>(p);
    }
    return getActivation() + getSimilarityActivation();
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  @Override
  public void dispose()
  {
    super.dispose();
    try
    {
      writeLock().lock();
      _lastPattern = null;
      _similarityMap.clear();
      _similarityMap = null;
    }
    finally
    {
      writeLock().unlock();
    }
  }

  /**
   * Description of the Method
   * 
   * @param p
   *          Description of Parameter
   * @since
   */
  protected double computeSimilarityActivation(ChunkTypeRequest p)
  {
    if (p == null) return 0;

    IModel parentModel = _parentChunk.getModel();
    IDeclarativeModule5 idm = (IDeclarativeModule5) parentModel
        .getModule(IDeclarativeModule5.class);

    if (idm == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("parent model does not support similarities");
      return 0;
    }

    ISymbolicChunk sc = _parentChunk.getSymbolicChunk();

    double maxSim = idm.getMaximumSimilarity();
    double mmp = idm.getMismatchPenalty();
    double simAct = 0;
    for (IConditionalSlot s : p.getConditionalSlots())
    {
      String slotName = s.getName();
      try
      {
        Object chunkSlotValue = sc.getSlot(slotName).getValue();
        // System.out.println("Checking "+slotName+" "+s.getSlotValue()+" ?=
        // "+chunkSlotValue);
        double sim = 0;
        if (!s.matchesCondition(chunkSlotValue))
          sim = idm.getSimilarity(s.getValue(), chunkSlotValue);
        else
          sim = maxSim;
        // System.out.println(_parentChunk+" sim :"+sim);
        simAct += (sim * mmp);
      }
      catch (Exception e)
      {
        simAct += idm.getMaximumDifference() * mmp;
      }
    }
    // System.out.println("S :"+_similarityActivation);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("SimilarityActivation " + simAct);
    // System.out.println(_parentChunk+" S:"+_similarityActivation);
    return simAct;
  }

}