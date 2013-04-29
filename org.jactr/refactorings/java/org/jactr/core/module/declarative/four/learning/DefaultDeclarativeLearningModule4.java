/*
 * Created on Oct 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.four.learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.four.Link;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * default declarative learning module. learning is accomplished by attaching
 * listeners to chunks and buffers. For instance, we attach a listener to all
 * chunks so we know when they are accessed (used for base level learning). we
 * also listen to all slot changes so that associative links can be adjusted
 * accordingly. And we listen to the procedural module so that we know which
 * production fired, binding to which chunks so that the associative links can
 * be updated
 * 
 * @author developer
 */
public class DefaultDeclarativeLearningModule4 extends AbstractModule implements
    IDeclarativeLearningModule4, IParameterized
{
  /**
   * logger definition
   */
  static private final Log               LOGGER                   = LogFactory
                                                                      .getLog(DefaultDeclarativeLearningModule4.class);

  protected int                          _optimizationLevel       = 0;

  protected double                       _baseLevelLearningRate   = 0.5;

  protected double                       _associativeLearningRate = 1;

  protected IBaseLevelActivationEquation _baseLevelActivationEquation;

  /**
   * this model listener will allow us to perform learning asynchronously, but
   * block until the current batch of learning is complete before running the
   * next cycle.
   */
  protected IModelListener               _modelListener;

  private IDeclarativeModuleListener     _declarativeListener;

  private IProceduralModuleListener      _proceduralListener;

  public DefaultDeclarativeLearningModule4()
  {
    super("DeclarativeLearningV4");

    _modelListener = new ModelListenerAdaptor() {

      @Override
      public void moduleInstalled(ModelEvent event)
      {
        tryToAttach();
      }

      @Override
      public void cycleStarted(ModelEvent event)
      {
        /*
         * we don't need this code to do anything. it will be executed after all
         * the other tasks have been, i.e., all learning is done. if
         * getExecutor() returns the inline, nothing changes
         */
        FutureTask<Object> blockingTask = new FutureTask<Object>(
            new Runnable() {

              public void run()
              {
                // noop once completed, object will be available
              }
            }, new Object());

        getExecutor().execute(blockingTask);

        try
        {
          blockingTask.get();
        }
        catch (Exception e)
        {
          LOGGER.error("Could not block while waiting for learning to finish ",
              e);
        }
      }
    };
  }

  @Override
  public void initialize()
  {
    IDeclarativeModule decl = getModel().getDeclarativeModule();
    IProceduralModule proc = getModel().getProceduralModule();

    if (decl == null || proc == null)
      throw new IllegalModuleStateException(
          "Both IDeclarativeModule and IProceduralModule must be installed before "
              + getClass().getSimpleName());

    /*
     * reset the strengths of the associative links
     */
    Link.resetAllLinks(getModel());
  }

  /**
   * try to attach the declarative and procedural module listeners. this can be
   * called at install (if decMod or procMod preceeded it)
   */
  private void tryToAttach()
  {
    IDeclarativeModule decl = getModel().getDeclarativeModule();
    IProceduralModule proc = getModel().getProceduralModule();

    // can, but havent attached
    if (decl != null && _declarativeListener == null)
    {
      _declarativeListener = createDeclarativeListener();
      decl.addListener(_declarativeListener, getExecutor());
    }

    if (proc != null && _proceduralListener == null)
    {
      _proceduralListener = createProceduralListener();
      proc.addListener(_proceduralListener, getExecutor());
    }
  }

  @Override
  public void install(IModel model)
  {
    super.install(model);

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    /*
     * set the base level activatio equation..
     */
    setBaseLevelActivationEquation(createBaseLevelActivationEquation());
  }

  protected IProceduralModuleListener createProceduralListener()
  {
    return new ProceduralModuleListener(this);
  }

  protected IDeclarativeModuleListener createDeclarativeListener()
  {
    return new DeclarativeModuleListener(this);
  }

  protected IBaseLevelActivationEquation createBaseLevelActivationEquation()
  {
    return new DefaultBaseLevelActivationEquation(getModel());
  }

  @Override
  public void dispose()
  {
    getModel().removeListener(_modelListener);
    _baseLevelActivationEquation = null;
    _modelListener = null;
    //call last so that getModel returns !null
    super.dispose();
  }

  public int getOptimizationLevel()
  {
    return _optimizationLevel;
  }

  public void setOptimizationLevel(int level)
  {
    _optimizationLevel = level;
  }

  public double getAssociativeLearning()
  {
    return _associativeLearningRate;
  }

  public double getBaseLevelLearning()
  {
    return _baseLevelLearningRate;
  }

  public boolean isAssociativeLearningEnabled()
  {
    return !Double.isNaN(_associativeLearningRate);
  }

  public boolean isBaseLevelLearningEnabled()
  {
    return !Double.isNaN(_baseLevelLearningRate);
  }

  public void setAssociativeLearning(double learningRate)
  {
    _associativeLearningRate = learningRate;
  }

  public void setBaseLevelLearning(double learningRate)
  {
    _baseLevelLearningRate = learningRate;
  }

  public boolean isLearningEnabled()
  {
    return isBaseLevelLearningEnabled() || isAssociativeLearningEnabled();
  }

  public void setLearningEnabled(boolean enable)
  {
    if (enable)
    {
      if (!isBaseLevelLearningEnabled()) setBaseLevelLearning(0.5); // default
      if (!isAssociativeLearningEnabled()) setAssociativeLearning(1);
    }
    else
    {
      // turn them both off
      setBaseLevelLearning(Double.NaN);
      setAssociativeLearning(Double.NaN);
    }
  }



  /**
   * @see org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4#getBaseLevelActivationEquation()
   */
  public IBaseLevelActivationEquation getBaseLevelActivationEquation()
  {
    return _baseLevelActivationEquation;
  }

  /**
   * @see org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4#setBaseLevelActivationEquation(org.jactr.core.module.declarative.four.learning.IBaseLevelActivationEquation)
   */
  public void setBaseLevelActivationEquation(
      IBaseLevelActivationEquation equation)
  {
    _baseLevelActivationEquation = equation;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (BASE_LEVEL_LEARNING_RATE.equalsIgnoreCase(key))
      return "" + getBaseLevelLearning();
    else if (ASSOCIATIVE_LEARNING_RATE.equalsIgnoreCase(key))
      return "" + getAssociativeLearning();
    else if (OPTIMIZED_LEARNING.equalsIgnoreCase(key))
      return "" + getOptimizationLevel();
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(BASE_LEVEL_LEARNING_RATE);
    rtn.add(ASSOCIATIVE_LEARNING_RATE);
    rtn.add(OPTIMIZED_LEARNING);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (BASE_LEVEL_LEARNING_RATE.equalsIgnoreCase(key))
      setBaseLevelLearning(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (ASSOCIATIVE_LEARNING_RATE.equalsIgnoreCase(key))
      setAssociativeLearning(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (OPTIMIZED_LEARNING.equalsIgnoreCase(key))
      setOptimizationLevel(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to set " + key + " to " + value);

  }

  public void reset()
  {
    // noop
    
  }

}
