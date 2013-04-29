/*
 * Created on Dec 11, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.concurrent;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An executor that can be wrapped around others and will check to be sure that
 * any execute calls are not made from a thread that is managed by this executor
 * service. this can prevent a fairly nasty and stupid deadlock occurence.
 * <code>
 * SafeExecutorService ses = new SafeExecutorService(false);
 SafeExecutorThreadFactory setf = new SafeExecutorThreadFactory(ses, Executors.defaultThreadFactory());
 ses.setDelegate(Executors.newSingleThreadExecutor(setf));
 </code>
 * 
 * @author developer
 */
public class SafeExecutorService extends AbstractExecutorService
{

  private ExecutorService                   _delegate;

  final private boolean                     _permitSelfExecution;

  final private WeakHashMap<Thread, Thread> _managedThreads = new WeakHashMap<Thread, Thread>();

  public SafeExecutorService(boolean permitSelfExecutions)
  {
    this(null, permitSelfExecutions);
  }

  /**
   * 
   */
  public SafeExecutorService(ExecutorService delegate,
      boolean permitSelfExecutions)
  {
    _delegate = delegate;
    _permitSelfExecution = permitSelfExecutions;
  }

  public void setDelegate(ExecutorService service)
  {
    _delegate = service;
  }

  public ExecutorService getDelegate()
  {
    return _delegate;
  }

  public boolean allowsSelfExecutions()
  {
    return _permitSelfExecution;
  }

  /**
   * @see java.util.concurrent.ExecutorService#awaitTermination(long,
   *      java.util.concurrent.TimeUnit)
   */
  public boolean awaitTermination(long arg0, TimeUnit arg1)
      throws InterruptedException
  {
    return _delegate.awaitTermination(arg0, arg1);
  }

  /**
   * @see java.util.concurrent.ExecutorService#isShutdown()
   */
  public boolean isShutdown()
  {
    return _delegate.isShutdown();
  }

  /**
   * @see java.util.concurrent.ExecutorService#isTerminated()
   */
  public boolean isTerminated()
  {
    return _delegate.isTerminated();
  }

  /**
   * @see java.util.concurrent.ExecutorService#shutdown()
   */
  public void shutdown()
  {
    _delegate.shutdown();
  }

  /**
   * @see java.util.concurrent.ExecutorService#shutdownNow()
   */
  public List<Runnable> shutdownNow()
  {
    return _delegate.shutdownNow();
  }

  /**
   * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
   */
  public void execute(Runnable arg0)
  {
    canExecute(arg0);
    _delegate.execute(arg0);
  }

  public void addManagedThread(Thread thread)
  {
    _managedThreads.put(thread, thread);
  }

  public void removeManagedThread(Thread thread)
  {
    _managedThreads.remove(thread);
  }

  public boolean isManagedThread(Thread thread)
  {
    return _managedThreads.containsKey(thread);
  }

  /**
   * test to see if the current thread is managed and we permit self executions
   */
  protected void canExecute(Runnable arg0)
  {
    if (!allowsSelfExecutions() && isManagedThread(Thread.currentThread()))
      throw new IllegalStateException(
          "SafeExecutorService does not permit executing events from a managed thread, cannot execute "
              + arg0);
  }
}
