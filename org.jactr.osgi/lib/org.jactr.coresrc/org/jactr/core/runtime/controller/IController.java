/*
 * Created on Feb 17, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.runtime.controller;

import java.util.Collection;
import java.util.concurrent.Future;

import org.jactr.core.model.IModel;

/**
 * Controls the behavior of the ACTRRuntime
 */
public interface IController
{
  
  /**
   * attach to the runtime - called by the runtime during
   * ACTRRuntime.setController();
   *
   */
  public void attach();
  
  /**
   * detach the runtime - called by the runtime during
   * ACTRRuntime.setController(null);
   *
   */
  public void detach();
  
  /**
   * reset the runtime. stops all running models, performs some clean up, then
   * resets so that the models can be run again. Does not affect the model states at all
   * nor does it signal the reality interface
   */
//  public void reset();
  
  /**
   * run the models..The runtime is not actually running until at least
   * one model has finished initialization.
   *
   */
  public Future<Boolean> start();
  
  /**
   * returns a future that can be blocked on until the runtime starts fully (i.e. a model is running)
   * @return
   */
  public Future<Boolean> waitForStart();
  
  
  /**
   * start the models but suspend at the start of the first cycle
   * @param suspendImmediately
   */
  public Future<Boolean> start(boolean suspendImmediately);
  
  /**
   * complete().get() will block until all models complete or are terminated
   * 
   * @return
   */
  public Future<Boolean> complete();
  
  public Future<Boolean> waitForCompletion();

  /**
   * stop all the current running models at the nearest possible moment.
   * This is a clean stop.
   *
   */
  public Future<Boolean> stop();
  
  /**
   * force all the models to terminate
   */
  public Future<Boolean> terminate();

  /**
   * pause at the next immediate opportunity
   *
   */
  public Future<Boolean> suspend();
  
  public Future<Boolean> waitForSuspension();

  /**
   * resume from a suspend
   *
   */
  public Future<Boolean> resume();
  
  public Future<Boolean> waitForResumption();

  public boolean isRunning();

  public boolean isSuspended();
  
  
  
  
  public Collection<IModel> getRunningModels();
  
  public Collection<IModel> getTerminatedModels();
  
  public Collection<IModel> getSuspendedModels();
}