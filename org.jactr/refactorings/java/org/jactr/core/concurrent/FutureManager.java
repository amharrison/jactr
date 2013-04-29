package org.jactr.core.concurrent;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FutureManager<K, C>
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER = LogFactory
                                                  .getLog(FutureManager.class);

  final private Map<K, LocalFuture<C>> _map;

  public FutureManager()
  {
    _map = new HashMap<K, LocalFuture<C>>();
  }
  
  public Future<C> acquire(K key)
  {
    LocalFuture<C> future = new LocalFuture<C>();
    _map.put(key, future);
    return future;
  }

  public Future<C> release(K key, C result)
  {
    LocalFuture<C> future = _map.remove(key);
    if (future != null) future.set(result, null);
    return future;
  }
  
  public Future<C> release(K key, Throwable exception)
  {
    LocalFuture<C> future = _map.remove(key);
    if (future != null) future.set(null, exception);
    return future;
  }

  private class LocalFuture<C> extends FutureTask<C>
  {
    public LocalFuture()
    {
      super(new Runnable() {

        public void run()
        {

        }

      }, null);
    }

    public void set(C result, Throwable thrown)
    {
      if (thrown != null) setException(thrown);
      set(result);
    }
  }
}
