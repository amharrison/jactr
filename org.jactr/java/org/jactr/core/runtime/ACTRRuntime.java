/*
 * Created on May 8, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;

public class ACTRRuntime
{
  /**
   * logger definition
   */
  static public final Log                                LOGGER      = LogFactory
      .getLog(ACTRRuntime.class);

  static private ACTRRuntime                             _instance;

  private Collection<IModel>                             _allModels;

  private Runnable                                       _onStart;

  private Runnable                                       _onStop;

  private Object                                         _applicationData;

  private IController                                    _controller;

  ACTREventDispatcher<ACTRRuntime, IACTRRuntimeListener> _eventDispatcher;

  private IConnector                                     _commonRealityConnector;

  private File                                           _workingDirectory;

  private IClock                                         _buildClock = new BasicClock();

  /**
   * return the ACTRRuntime singleton
   * 
   * @return
   */
  static public ACTRRuntime getRuntime()
  {
    synchronized (ACTRRuntime.class)
    {
      if (_instance == null) _instance = new ACTRRuntime();
      return _instance;
    }
  }

  protected ACTRRuntime()
  {
    _allModels = Collections.synchronizedList(new ArrayList<IModel>());
    _eventDispatcher = new ACTREventDispatcher<ACTRRuntime, IACTRRuntimeListener>();
    setConnector(new LocalConnector());
    setWorkingDirectory(new File(System.getProperty("user.dir")));
  }

  /**
   * Get the working directory for this instance of the runtime. By default this
   * is user.dir, however, the iterative entry point will override this to a
   * subdirectory per iteration to ensure no overwrites. Instruments can always
   * write to working directory without worry.
   * 
   * @return
   */
  public File getWorkingDirectory()
  {
    return _workingDirectory;
  }

  public void setWorkingDirectory(File workingDirectory)
  {
    _workingDirectory = workingDirectory;
  }

  public IConnector getConnector()
  {
    return _commonRealityConnector;
  }

  public void setConnector(IConnector connector)
  {
    _commonRealityConnector = connector;
  }

  public IController getController()
  {
    return _controller;
  }

  /**
   * will call IController.attach()
   * 
   * @param controller
   */
  public void setController(IController controller)
  {
    IController oldController = _controller;

    if (oldController == controller)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Already attached");
      return;
    }

    if (oldController != null) oldController.detach();

    if (controller != null) try
    {
      controller.attach();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not attach new controller " + controller, e);
      controller = null;
    }

    _controller = controller;
  }

  /**
   * return the clock for the model in this runtime. originally this was a zero
   * parameter method but since when connected to common reality, each model has
   * its own clock instance, we use the proxy clock to return a valid clock if
   * connected to common reality, otherwise it returns a default shared clock
   * 
   * @param model
   *          if null will always return the default shared clock
   * @return
   */
  public IClock getClock(IModel model)
  {
    IConnector connector = getConnector();
    if (connector != null) return connector.getClock(model);

    return _buildClock;
  }

  /**
   * add a model to this runtime. if the runtime is already actively running,
   * this model will be added and immediately started. The model should have had
   * all of its listeners attached before being added to the runtime. this will
   * ensure that if the model is suspended that all events are delivered
   * appropriately
   * 
   * @param model
   */
  public void addModel(IModel model)
  {
    _allModels.add(model);

    /*
     * fire the event
     */
    if (hasListeners())
      dispatch(new ACTRRuntimeEvent(model, ACTRRuntimeEvent.Type.MODEL_ADDED));
  }

  /**
   * remove this model, assuming that it is not running. If it is, a runtime
   * excepiton will be thrown
   * 
   * @param model
   */
  public void removeModel(IModel model)
  {
    try
    {
      /*
       * why wrap this? when the event fires, the controller, which will receive
       * the event may throw an exception if you are removing from a running
       * runtime
       */
      if (hasListeners()) dispatch(
          new ACTRRuntimeEvent(model, ACTRRuntimeEvent.Type.MODEL_REMOVED));

      _allModels.remove(model);
    }
    catch (Exception e)
    {

    }
  }

  /**
   * return all the models associated with the runtime
   * 
   * @return
   */
  public Collection<IModel> getModels()
  {
    return Collections.unmodifiableCollection(_allModels);
  }

  /**
   * runnable to be executed after the runtime is started by the controller
   * 
   * @param onStart
   */
  public void setOnStart(Runnable onStart)
  {
    _onStart = onStart;
  }

  public Runnable getOnStart()
  {
    return _onStart;
  }

  /**
   * runnable to be executed after the runtime has stopped. ie. all models
   * terminated
   */
  public void setOnStop(Runnable onStop)
  {
    _onStop = onStop;
  }

  /**
   * called by controller
   * 
   * @return
   */
  public Runnable getOnStop()
  {
    return _onStop;
  }

  /**
   * @param applicationData
   */
  public void setApplicationData(Object applicationData)
  {
    _applicationData = applicationData;
  }

  public Object getApplicationData()
  {
    return _applicationData;
  }

  public boolean hasListeners()
  {
    return _eventDispatcher.hasListeners();
  }

  public Collection<IACTRRuntimeListener> getListeners()
  {
    return _eventDispatcher.getListeners();
  }

  public void addListener(IACTRRuntimeListener listener, Executor executor)
  {
    _eventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IACTRRuntimeListener listener)
  {
    _eventDispatcher.removeListener(listener);
  }

  public void dispatch(ACTRRuntimeEvent event)
  {
    if (_eventDispatcher.hasListeners()) _eventDispatcher.fire(event);
  }
}
