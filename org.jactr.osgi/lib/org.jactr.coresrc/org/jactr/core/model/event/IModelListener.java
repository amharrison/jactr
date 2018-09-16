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

package org.jactr.core.model.event;

import java.util.EventListener;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 11, 2003
 */
public interface IModelListener extends EventListener
{

  /**
   * called when the model has been connected to the runtime and possibly
   * CommonReality
   * 
   * @param me
   */
  public void modelConnected(ModelEvent me);

  /**
   * called on disconnect
   * 
   * @param me
   */
  public void modelDisconnected(ModelEvent me);

  /**
   * called after a module has been installed
   * 
   * @param me
   */
  public void moduleInstalled(ModelEvent me);

  /**
   * called after an extension has been installed
   * 
   * @param me
   */
  public void extensionInstalled(ModelEvent me);

  /**
   * called after an instrument has been installed
   * 
   * @param me
   */
  public void instrumentInstalled(ModelEvent me);

  /**
   * called after a buffer has been installed
   * 
   * @param me
   */
  public void bufferInstalled(ModelEvent me);

  /**
   * called after the model has been initialized - i.e. all the chunks and
   * related content have been added and the model is in a runnable state
   * 
   * @param me
   */
  public void modelInitialized(ModelEvent me);

  /**
   * called once the model has started its execution. this is called shortly
   * after modelConnected
   * 
   * @param me
   */
  public void modelStarted(ModelEvent me);

  /**
   * called when the model enters a suspended state due to a break point or
   * explicitly forced by the controller
   * 
   * @param me
   */
  public void modelSuspended(ModelEvent me);

  /**
   * called when the model resumes
   * 
   * @param me
   */
  public void modelResumed(ModelEvent me);

  /**
   * called when the model's execution has completed. this is always the second
   * to last event. disconnected is the final one
   * 
   * @param me
   */
  public void modelStopped(ModelEvent me);

  /**
   * called in an exception occurs during the normal run. after firing this
   * event, modelStopped will be called
   * 
   * @param me
   */
  public void exceptionThrown(ModelEvent me);

  /**
   * called at the start of each cycle.
   * 
   * @param me
   */
  public void cycleStarted(ModelEvent me);

  /**
   * called at the end of each cycle.
   * 
   * @param me
   */
  public void cycleStopped(ModelEvent me);
}
