/*
 * Created on Oct 21, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.model.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.ICycleProcessor;
import org.jactr.core.model.IModel;
import org.jactr.core.model.IllegalModelStateException;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.six.DefaultCycleProcessor6;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.DefaultAdaptable;
import org.jactr.core.utils.collections.CachedCollection;
import org.jactr.core.utils.collections.CachedMap;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.instrument.IInstrument;

public class BasicModel extends DefaultAdaptable implements IModel
{
  /**
   * logger definition
   */
  static private final Log                                          LOGGER                = LogFactory
                                                                                              .getLog(BasicModel.class);

  static public final String                                        CYCLE_SKIPPING_PARAM  = "EnableUnusedCycleSkipping";

  static public final String                                        PERSISTENT_PARAM      = "EnablePersistentExecution";

  static public final String                                        AGE_PARAM             = "Age";

  static private final Collection<String>                           READABLE_PARAMETERS   = Collections
                                                                                              .unmodifiableCollection(Arrays
                                                                                                  .asList(
                                                                                                      AGE_PARAM,
                                                                                                      CYCLE_SKIPPING_PARAM,
                                                                                                      PERSISTENT_PARAM));

  static private final Collection<String>                           WRITABLE_PARAMETERS   = Collections
                                                                                              .unmodifiableCollection(Arrays
                                                                                                  .asList(
                                                                                                      AGE_PARAM,
                                                                                                      CYCLE_SKIPPING_PARAM,
                                                                                                      PERSISTENT_PARAM));

  protected boolean                                                 _isInitialized        = false;

  protected TimedEventQueue                                         _timedEventQueue;

  protected IDeclarativeModule                                      _declarativeModule;

  protected IProceduralModule                                       _proceduralModule;

  protected CachedMap<String, IActivationBuffer>                    _buffers;

  protected CachedCollection<IModule>                               _modules;

  protected CachedCollection<IExtension>                            _extensions;

  protected CachedCollection<IInstrument>                           _installedInstruments;

  protected boolean                                                 _cycleSkippingEnabled = true;

  protected boolean                                                 _persistentExecution  = false;

  protected ReentrantReadWriteLock                                  _lock                 = new ReentrantReadWriteLock();

  protected ACTREventDispatcher<IModel, IModelListener>             _eventDispatcher;

  protected ACTREventDispatcher<IParameterized, IParameterListener> _parameterEventDispatcher;

  protected Map<String, Object>                                     _metaData;

  protected Map<String, Object>                                     _parameterMap;

  protected String                                                  _name;

  protected boolean                                                 _modulesInitialized   = false;

  protected long                                                    _cycle;

  protected double                                                  _age;

  protected ICycleProcessor                                         _cycleProcessor;

  public BasicModel()
  {
    _timedEventQueue = new TimedEventQueue(this);
    _buffers = new CachedMap<String, IActivationBuffer>(
        new TreeMap<String, IActivationBuffer>());
    _modules = new CachedCollection<IModule>(new ArrayList<IModule>());
    _extensions = new CachedCollection<IExtension>(new ArrayList<IExtension>());
    _installedInstruments = new CachedCollection<IInstrument>(
        new ArrayList<IInstrument>());
    _eventDispatcher = new ACTREventDispatcher<IModel, IModelListener>();
    _parameterEventDispatcher = new ACTREventDispatcher<IParameterized, IParameterListener>();
    _metaData = new TreeMap<String, Object>();
    _parameterMap = new TreeMap<String, Object>();
    setCycleProcessor(createCycleProcessor());
  }

  public void setCycleProcessor(ICycleProcessor processor)
  {
    _cycleProcessor = processor;
  }

  public ICycleProcessor getCycleProcessor()
  {
    return _cycleProcessor;
  }

  protected ICycleProcessor createCycleProcessor()
  {
    return new DefaultCycleProcessor6();
  }

  public BasicModel(String name)
  {
    this();
    setName(name);
  }

  public void dispose()
  {
    /*
     * explicitly uninstall
     */
    for (IInstrument instrument : new ArrayList<IInstrument>(
        _installedInstruments))
      uninstall(instrument);

    for (IExtension ext : new ArrayList<IExtension>(_extensions))
      uninstall(ext);

    /*
     * modules are responsible for buffer disposal
     */
    for (IModule module : _modules)
      module.dispose();

    try
    {
      // acquire lock
      _lock.writeLock().lock();

      /*
       * no more events
       */
      _eventDispatcher.clear();

      _extensions.clear();
      // _extensions = null;

      _modules.clear();
      // _modules = null;

      _buffers.clear();
      // _buffers = null;

      _metaData.clear();
      // _metaData = null;

      _parameterMap.clear();
      // _parameterMap = null;

      _timedEventQueue.dispose();
      // _timedEventQueue = null;

      // _declarativeModule = null;
      // _proceduralModule = null;
    }
    finally
    {
      // release
      _lock.writeLock().unlock();
    }
  }

  public ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  public void addActivationBuffer(IActivationBuffer buffer)
  {
    boolean added = false;
    try
    {
      // acquire lock
      _lock.writeLock().lock();

      _buffers.put(buffer.getName().toLowerCase(), buffer);
      added = true;
    }
    finally
    {
      // release
      _lock.writeLock().unlock();

      if (added) dispatch(new ModelEvent(this, buffer));
    }
  }

  public IActivationBuffer getActivationBuffer(String name)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return _buffers.get(name.toLowerCase());
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public Collection<IActivationBuffer> getActivationBuffers()
  {
    ArrayList<IActivationBuffer> buffers = new ArrayList<IActivationBuffer>(
        _buffers.size());
    getActivationBuffers(buffers);
    return buffers;
  }

  public void getActivationBuffers(Collection<IActivationBuffer> container)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      _buffers.getValues(container);
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public IDeclarativeModule getDeclarativeModule()
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return _declarativeModule;
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public IExtension getExtension(Class<? extends IExtension> extensionClass)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      for (IExtension extension : _extensions)
        if (extensionClass.isAssignableFrom(extension.getClass()))
          return extension;

      return null;
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public Collection<IExtension> getExtensions()
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return Collections.unmodifiableCollection(_extensions);
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public IInstrument getInstrument(Class<? extends IInstrument> instrumentClass)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      for (IInstrument installable : _installedInstruments)
        if (instrumentClass.isAssignableFrom(installable.getClass()))
          return installable;

      return null;
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public Collection<IInstrument> getInstruments()
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return Collections.unmodifiableCollection(_installedInstruments);
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public IModule getModule(Class<? extends IModule> moduleClass)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      for (IModule module : _modules)
        if (moduleClass.isAssignableFrom(module.getClass())) return module;

      return null;
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public Collection<IModule> getModules()
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return Collections.unmodifiableCollection(_modules);
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public IProceduralModule getProceduralModule()
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return _proceduralModule;
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public void install(IModule module)
  {
    boolean added = false;
    try
    {
      // acquire lock
      _lock.writeLock().lock();

      if (module instanceof IDeclarativeModule)
      {
        if (_declarativeModule != null)
          throw new IllegalModelStateException(
              "Only one declarative module may exist in a model at a time");

        _declarativeModule = (IDeclarativeModule) module;
      }

      if (module instanceof IProceduralModule)
      {
        if (_proceduralModule != null)
          throw new IllegalModelStateException(
              "Only one procedural module may exist in a model at a time");

        _proceduralModule = (IProceduralModule) module;
      }

      _modules.add(module);

      added = true;
    }
    finally
    {
      // release
      _lock.writeLock().unlock();

      if (added)
      {
        /*
         * install out of the lock in case this takes a sig amount of time
         */

        module.install(this);
        if (hasBeenInitialized()) module.initialize();
        dispatch(new ModelEvent(this, module));
      }
    }
  }

  public void install(IExtension extension)
  {
    if (!modulesAreInitialized()) initializeModules();

    boolean added = false;
    try
    {
      // acquire lock
      _lock.writeLock().lock();

      _extensions.add(extension);

      added = true;
    }
    finally
    {
      // release
      _lock.writeLock().unlock();

      if (added)
      {
        /*
         * install out of the lock in case this takes a sig amount of time
         */
        extension.install(this);
        if (hasBeenInitialized())
          try
          {
            extension.initialize();
          }
          catch (Exception e)
          {
            throw new IllegalModelStateException(
                "Exception while initializing extension " + extension, e);
          }

        dispatch(new ModelEvent(this, extension));
      }
    }
  }

  public void install(IInstrument instrument)
  {
    if (!modulesAreInitialized()) initializeModules();

    boolean added = false;
    try
    {
      // acquire lock
      _lock.writeLock().lock();

      _installedInstruments.add(instrument);
      added = true;
    }
    finally
    {
      // release
      _lock.writeLock().unlock();

      if (added)
      {
        /*
         * perform the instll out of the lock
         */
        instrument.install(this);
        if (hasBeenInitialized()) instrument.initialize();
        dispatch(new ModelEvent(this, instrument));
      }
    }

  }

  /**
   * @see org.jactr.core.model.IModel#uninstall(org.jactr.instrument.IInstrument)
   */
  public void uninstall(IInstrument installable)
  {
    try
    {
      _lock.writeLock().lock();
      _installedInstruments.remove(installable);
    }
    finally
    {
      _lock.writeLock().unlock();
    }
    /*
     * pull it out of the lock, since this may take some time
     */
    installable.uninstall(this);
  }

  public void uninstall(IExtension extension)
  {
    try
    {
      _lock.writeLock().lock();
      _extensions.remove(extension);
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    /*
     * pull it out of the lock, since this may take some time
     */
    extension.uninstall(this);
  }

  public String getParameter(String key)
  {
    if (CYCLE_SKIPPING_PARAM.equalsIgnoreCase(key))
      return ParameterHandler.booleanInstance().toString(
          isCycleSkippingEnabled());
    else if (AGE_PARAM.equalsIgnoreCase(key))
      return "" + getAge();
    else if (PERSISTENT_PARAM.equalsIgnoreCase(key))
      return "" + isPersistentExecutionEnabled();

    if (_parameterMap.containsKey(key))
      return _parameterMap.get(key).toString();

    if (LOGGER.isWarnEnabled())
      LOGGER.warn("Unknown parameter " + key + " requested, returning null");
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    TreeSet<String> possible = new TreeSet<String>(READABLE_PARAMETERS);
    possible.addAll(WRITABLE_PARAMETERS);
    return possible;
  }

  public Collection<String> getSetableParameters()
  {
    return WRITABLE_PARAMETERS;
  }

  public void setParameter(String key, String value)
  {
    boolean handled = false;
    if (CYCLE_SKIPPING_PARAM.equalsIgnoreCase(key))
    {
      setCycleSkippingEnabled(ParameterHandler.booleanInstance().coerce(value));
      handled = true;
    }
    else if (AGE_PARAM.equalsIgnoreCase(key))
    {
      setAge(ParameterHandler.numberInstance().coerce(value).doubleValue());
      handled = true;
    }
    else if (PERSISTENT_PARAM.equalsIgnoreCase(key))
    {
      setPersistentExecutionEnabled(ParameterHandler.booleanInstance().coerce(
          value));
      handled = true;
    }
    else
      // check the modules..
      for (IModule module : _modules)
        if (module instanceof IParameterized)
          if (((IParameterized) module).getSetableParameters().contains(key))
          {
            // route it
            if (LOGGER.isWarnEnabled())
              LOGGER
                  .warn("You should not use the model as the parameter nexus, rather assigned it to the module directly");
            ((IParameterized) module).setParameter(key, value);
            handled = true;
          }

    if (!handled)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No registered handler for " + key + " = " + value);
      _parameterMap.put(key, value);
    }

  }

  public Object getMetaData(String key)
  {
    try
    {
      // lock
      _lock.readLock().lock();
      return _metaData.get(key);
    }
    finally
    {
      // unlock
      _lock.readLock().unlock();
    }
  }

  public void setMetaData(String key, Object value)
  {
    try
    {
      // acquire lock
      _lock.writeLock().lock();

      _metaData.put(key, value);
    }
    finally
    {
      // release
      _lock.writeLock().unlock();
    }
  }

  public Collection<String> getMetaDataKeys()
  {
    return new ArrayList<String>(_metaData.keySet());
  }

  public void setAge(double age)
  {
    _age = age;
  }

  public double getAge()
  {
    return _age;
  }

  public void setCycleSkippingEnabled(boolean skipping)
  {
    if (skipping == _cycleSkippingEnabled) return;

    boolean old = _cycleSkippingEnabled;
    _cycleSkippingEnabled = skipping;

    fireParameterEvent(CYCLE_SKIPPING_PARAM, old, skipping);
  }

  public boolean isCycleSkippingEnabled()
  {
    return _cycleSkippingEnabled;
  }

  public void setPersistentExecutionEnabled(boolean persistent)
  {
    if (_persistentExecution == persistent) return;

    boolean old = _persistentExecution;
    _persistentExecution = persistent;
    fireParameterEvent(PERSISTENT_PARAM, old, persistent);
  }

  public boolean isPersistentExecutionEnabled()
  {
    return _persistentExecution;
  }

  public TimedEventQueue getTimedEventQueue()
  {
    return _timedEventQueue;
  }

  public boolean hasBeenInitialized()
  {
    return _isInitialized;
  }

  /**
   * initialize the model, calling initialize on buffers, and extensions in that
   * order. this is called before the model starts to run Modules will have
   * already been initialized, as they are initialized before extensions or
   * instruments are installed..
   * 
   * @see org.jactr.core.model.IModel#initialize()
   */
  public void initialize() throws Exception
  {
    if (!modulesAreInitialized()) initializeModules();

    try
    {
      _lock.writeLock().lock();
      if (hasBeenInitialized())
        throw new IllegalModelStateException(
            "Model has already been initialized");

      _isInitialized = true;

      for (IActivationBuffer buffer : _buffers.values())
        buffer.initialize();

      for (IExtension extension : _extensions)
        extension.initialize();

      for (IInstrument instruments : _installedInstruments)
        instruments.initialize();
    }
    finally
    {
      _lock.writeLock().unlock();

      dispatch(new ModelEvent(this, ModelEvent.Type.INITIALIZED));
    }
  }

  protected boolean modulesAreInitialized()
  {
    return _modulesInitialized;
  }

  protected void initializeModules()
  {
    if (modulesAreInitialized()) return;
    for (IModule module : _modules)
      module.initialize();
    _modulesInitialized = true;
  }

  public void addListener(IModelListener listener, Executor executor)
  {
    _eventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IModelListener listener)
  {
    _eventDispatcher.removeListener(listener);
  }

  public boolean hasListeners()
  {
    return _eventDispatcher.hasListeners();
  }

  public void dispatch(ModelEvent modelEvent)
  {
    if (_eventDispatcher.hasListeners()) _eventDispatcher.fire(modelEvent);
  }

  public void addListener(IParameterListener listener, Executor executor)
  {
    _parameterEventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IParameterListener listener)
  {
    _parameterEventDispatcher.removeListener(listener);
  }

  public boolean hasParameterListeners()
  {
    return _parameterEventDispatcher.hasListeners();
  }

  public void dispatch(ParameterEvent modelEvent)
  {
    if (_parameterEventDispatcher.hasListeners())
      _parameterEventDispatcher.fire(modelEvent);
  }

  protected void fireParameterEvent(String parameterName, Object oldValue,
      Object newValue)
  {
    if (hasParameterListeners())
      dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime().getClock(this)
          .getTime(), parameterName, oldValue, newValue));
  }

  public String getName()
  {
    return _name;
  }

  public void setName(String modelName)
  {
    _name = modelName;
  }

  public long getCycle()
  {
    return _cycle;
  }

  public void setCycle(long cycle)
  {
    if (cycle <= _cycle) return;
    _cycle = cycle;
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
