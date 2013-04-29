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
package org.jactr.core.model;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.IInitializable;
import org.jactr.core.utils.IMetaContainer;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * core model interface. the majority of functionality is distributed to the
 * various modules that have been installed. It must have a IDeclarativeModule,
 * IProceduralModule at minimal to run. IModules and IExtensions can only be
 * installed, never removed - at least at this level of abstraction.
 * 
 * @author developer
 */
public interface IModel extends IParameterized, IMetaContainer, IInitializable,
    IAdaptable
{

  /**
   * initialize the model. This will call the initialize methods on all
   * extensions, buffers, and installables (in that order) this is to be called
   * after the model has been configured to a runnable point (chunks, types and
   * productions have been installed) - allowing the initializable elements to
   * attach any listeners. this will be called only once during the lifetime of
   * the model. anything that is dependent upon the actual run of the model
   * should attach a listener and catch the modelStarted() event.
   */
  public void initialize() throws Exception;

  /**
   * release all resources
   */
  public void dispose();

  /**
   * has this models intiialize been called
   * 
   * @return
   */
  public boolean hasBeenInitialized();

  /**
   * the model must have a timed event queue. this queue keeps track of all
   * internal model time-based changes (such as posting the results of a
   * retrieval). Most productions output a few timed events
   */
  public TimedEventQueue getTimedEventQueue();

  /**
   * install a buffer into this model, usually called during a modules
   * installation
   * 
   * @param buffer
   */
  public void addActivationBuffer(IActivationBuffer buffer);

  /**
   * returned the named activation buffer. case sensitivity is up to the
   * implementation
   * 
   * @param name
   * @return
   */
  public IActivationBuffer getActivationBuffer(String name);

  /**
   * return the installed activation buffers
   */
  public Collection<IActivationBuffer> getActivationBuffers();

  public void getActivationBuffers(Collection<IActivationBuffer> container);

  /**
   * install an IModule into the model. if it is the declarative or procedural
   * modules it will be installed as the models dec/proc module
   * 
   * @param module
   */
  public void install(IModule module);

  /**
   * return the declarative module - this is the primary access point for
   * declarative memory operations and controls
   * 
   * @return
   */
  public IDeclarativeModule getDeclarativeModule();

  /**
   * return the procedural module - this is the primary access point for
   * procedural access and controls
   * 
   * @return
   */
  public IProceduralModule getProceduralModule();

  /**
   * get the module that is of class. effectively getInstallable(IModule.class);
   * 
   * @param moduleClass
   * @return the module that implements Class, or null
   */
  public IModule getModule(Class<? extends IModule> moduleClass);

  /**
   * get all the installed moduels
   * 
   * @return
   */
  public Collection<IModule> getModules();

  /**
   * install this extension
   * 
   * @param extension
   */
  public void install(IExtension extension);

  public void uninstall(IExtension extension);

  /**
   * return the extension that implements this interface. just
   * getExtension(IExtension.class)
   * 
   * @param extensionClass
   * @return
   */
  public IExtension getExtension(Class<? extends IExtension> extensionClass);

  /**
   * return all the installed extensions
   * 
   * @return
   */
  public Collection<IExtension> getExtensions();

  /**
   * install some other installable element, attempting to install a module or
   * an extention here will reroute it to the more specific methods.
   * 
   * @param installable
   */
  public void install(IInstrument installable);

  public void uninstall(IInstrument installable);

  /**
   * @param instrumentClass
   * @return
   */
  public IInstrument getInstrument(Class<? extends IInstrument> instrumentClass);

  public Collection<IInstrument> getInstruments();

  public void addListener(IModelListener listener, Executor executor);

  public void removeListener(IModelListener listener);

  public boolean hasListeners();

  public void dispatch(ModelEvent modelEvent);

  public void addListener(IParameterListener listener, Executor executor);

  public void removeListener(IParameterListener listener);

  public boolean hasParameterListeners();

  public void dispatch(ParameterEvent modelEvent);

  /**
   * everyone needs a name..
   * 
   * @return
   */
  public String getName();

  public long getCycle();

  /**
   * age of the model used to shift the clock during runs
   * 
   * @return
   */
  public double getAge();

  public ICycleProcessor getCycleProcessor();

  public void setCycleProcessor(ICycleProcessor processor);

  public ReentrantReadWriteLock getLock();
}
