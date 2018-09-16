/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.DefaultAdaptable;

public abstract class AbstractModule extends DefaultAdaptable implements
    IModule
{
  /**
   * logger definition
   */
  static public final Log LOGGER    = LogFactory.getLog(AbstractModule.class);

  private IModel          _model;

  private String          _name;

  private Executor        _executor = ExecutorServices.INLINE_EXECUTOR;

  public AbstractModule(String name)
  {
    _name = name;
  }

  /**
   * @param executor
   */
  protected void setExecutor(Executor executor)
  {
    _executor = executor;
  }

  /**
   * default executor INLINE_EXECUTOR is used
   * 
   * @return
   */
  public Executor getExecutor()
  {
    return _executor;
  }

  public void dispose()
  {
    _model = null;
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return _name;
  }

  public void install(IModel model)
  {
    _model = model;
    for (IActivationBuffer buffer : createBuffers())
      _model.addActivationBuffer(buffer);
  }

  /**
   * called to create any buffers used by this buffer. They will be installed
   * into the model by the install(IModel) call
   * 
   * @return
   */
  protected Collection<IActivationBuffer> createBuffers()
  {
    return Collections.EMPTY_LIST;
  }

  public void uninstall(IModel model)
  {
    _model = null;
  }

  abstract public void initialize();

  /**
   * return a safe, noncolliding name - calls to this should make sure that the
   * map is readlocked
   * 
   * @param name
   * @param mapping
   * @return
   */
  protected String getSafeName(String name, Map<String, ?> mapping)
  {
    if (!mapping.containsKey(name.toLowerCase())) return name;

    // does the oldName match the name-# pattern
    int lastIndex = -1;
    if ((lastIndex = name.lastIndexOf("-")) != -1)
    {
      String snippet = name.substring(lastIndex + 1, name.length());
      try
      {
        Integer.parseInt(snippet);
        name = name.substring(0, lastIndex);
      }
      catch (NumberFormatException nfe)
      {
      }
    }

    StringBuilder sb = new StringBuilder(name);

    if (sb.length() > 1 && sb.charAt(sb.length() - 1) != '-') sb.append("-");

    sb.append(mapping.size());
    while (mapping.containsKey(sb.toString()))
      sb.append("-").append(mapping.size());

    String rtn = sb.toString();

    return rtn;
  }

  static public <T> CompletableFuture<T> immediateReturn(T value)
  {
    return CompletableFuture.completedFuture(value);
  }

  /**
   * create a future task and execute it immediately using the INLINE_EXECUTOR
   * 
   * @param <T>
   * @param caller
   * @return
   */
  static public <T> CompletableFuture<T> immediateFuture(Callable<T> caller)
  {
    return delayedFuture(caller, ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * create a future task and execute it on the exector
   * 
   * @param <T>
   * @param caller
   * @param executor
   * @return
   */
  static public <T> CompletableFuture<T> delayedFuture(Callable<T> caller,
      Executor executor)
  {
    return delayedFuture(new Supplier<T>() {
      public T get()
      {
        try
        {
          return caller.call();
        }
        catch (Exception e)
        {
          throw new RuntimeException(e);
        }
      }
    }, executor);
  }

  static public <T> CompletableFuture<T> delayedFuture(Supplier<T> supplier,
      Executor executor)
  {
    if (executor == null) executor = ExecutorServices.INLINE_EXECUTOR;
    CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier,
        executor);
    return future;
  }

}
