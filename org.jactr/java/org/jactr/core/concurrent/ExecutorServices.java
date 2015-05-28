/*
 * Created on Nov 20, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ExecutorServices
{
  /**
   * logger definition
   */
  static private final Log                    LOGGER          = LogFactory
                                                                  .getLog(ExecutorServices.class);

  static final public String                  INLINE          = "Inline";

  static final public String                  BACKGROUND      = "Background";

  static final public String                  PERIODIC        = "Periodic";

  static final public String                  POOL            = "Pool";

  static final public ExecutorService         INLINE_EXECUTOR = new InlineExecutor();

  static final public Runnable                NOOP_RUNNABLE   = new Runnable() {

                                                                public void run()
                                                                {

                                                                }
                                                              };

  static private Map<String, ExecutorService> _executors      = new TreeMap<String, ExecutorService>();

  static private boolean                      _isShuttingDown = false;

  static public void initialize()
  {
    addExecutor(INLINE, INLINE_EXECUTOR);

    addExecutor(BACKGROUND,
        Executors.newSingleThreadExecutor(new GeneralThreadFactory(
            "jACTR-Background")));

    addExecutor(PERIODIC,
        Executors.newSingleThreadScheduledExecutor(new GeneralThreadFactory(
            "jACT-R Periodic")));

    addExecutor(POOL, Executors.newFixedThreadPool(
        (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2.0),
            new GeneralThreadFactory("jACT-R Pool")));
  }

  static public void removeExecutor(String name)
  {
    synchronized (_executors)
    {
      ExecutorService service = _executors.remove(name);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Removed " + name + " : " + service);
    }
  }

  static public void addExecutor(String name, ExecutorService service)
  {
    synchronized (_executors)
    {
      if (_executors.containsKey(name) && !_executors.get(name).isShutdown())
        throw new RuntimeException("Cannot overwrite active executors [" + name
            + "]");

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Adding executor " + name + " : " + service);
      _executors.put(name, service);
    }
  }

  static public ExecutorService getExecutor(String name)
  {
    synchronized (_executors)
    {
      if (!_isShuttingDown && !_executors.containsKey(BACKGROUND))
        initialize();
      return _executors.get(name);
    }
  }

  static public void getExecutorNames(Collection<String> container)
  {
    synchronized (_executors)
    {
      container.addAll(_executors.keySet());
    }
  }

  /**
   * wait for a specific executor to finish its processing load
   * 
   * @param executor
   * @param waitInMs
   * @return true if the block was reached, false if timeout or exception
   */
  static public boolean waitFor(ExecutorService executor, long waitInMs)
  {
    /*
     * 
     * 
     */

    Future future = executor.submit(new Runnable() {
      public void run()
      {
        // noop
      }
    });

    try
    {
      future.get(waitInMs, TimeUnit.MILLISECONDS);
      return true;
    }
    catch (Exception te)
    {
      return false;
    }
  }

  /**
   * shutdown and wait for the shutdown of all the executors that are currently
   * installed. if millisecondsToWait is 0, it will wait indefinitely
   */
  static public void shutdown(long millisecondsToWait)
  {
    synchronized (_executors)
    {
      _isShuttingDown = true;
    }

    Collection<String> executorNames = new ArrayList<String>();
    getExecutorNames(executorNames);

    /*
     * issue shutdown
     */
    for (String name : executorNames)
    {
      ExecutorService executor = getExecutor(name);

      if (executor != null && !executor.isShutdown())
      {
        waitFor(executor, Math.max(250, millisecondsToWait));
        executor.shutdown();
      }
    }

    /*
     * and wait
     */
    long interval = 500;
    long abortAt = System.currentTimeMillis() + millisecondsToWait;
    if (millisecondsToWait == 0) abortAt = Long.MAX_VALUE;
    while (abortAt > System.currentTimeMillis() && executorNames.size() != 0)
    {
      for (String name : executorNames)
      {
        ExecutorService executor = getExecutor(name);
        if (executor != null)
          try
          {
            if (executor.awaitTermination(interval, TimeUnit.MILLISECONDS))
              removeExecutor(name);
            else if (LOGGER.isDebugEnabled())
              LOGGER
                  .debug(name + " did not terminate after " + interval + "ms");
          }
          catch (Exception e)
          {
            if (LOGGER.isWarnEnabled())
              LOGGER.warn("Failed to terminate " + name, e);
            removeExecutor(name);
          }
      }

      /*
       * get the current names again..
       */
      executorNames.clear();
      getExecutorNames(executorNames);
    }

    if (executorNames.size() != 0)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("Forcing unresponsive executors to terminate "
            + executorNames + " after " + millisecondsToWait + "ms");
      for (String name : executorNames)
      {
        ExecutorService executor = getExecutor(name);
        if (executor != null) executor.shutdownNow();
      }
    }

    synchronized (_executors)
    {
      _executors.clear();
      _isShuttingDown = false;
    }

  }

  static private class InlineExecutor extends AbstractExecutorService
  {
    public boolean awaitTermination(long arg0, TimeUnit arg1)
        throws InterruptedException
    {
      return true;
    }

    public boolean isShutdown()
    {
      return false;
    }

    public boolean isTerminated()
    {
      return false;
    }

    public void shutdown()
    {
    }

    @SuppressWarnings("unchecked")
    public List<Runnable> shutdownNow()
    {
      return Collections.EMPTY_LIST;
    }

    public void execute(Runnable arg0)
    {
      arg0.run();
    }
  }
}
