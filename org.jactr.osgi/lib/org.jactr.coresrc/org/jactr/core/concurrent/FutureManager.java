package org.jactr.core.concurrent;

/*
 * default logging
 */
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FutureManager<K, C>
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER = LogFactory
                                                  .getLog(FutureManager.class);

  final private ConcurrentMap<K, ListenableFuture<C>> _map;

  final private Runnable                           _noop  = new Runnable() {

                                                            public void run()
                                                            {

                                                            }

                                                          };

  public FutureManager()
  {
    _map = new ConcurrentHashMap<K, ListenableFuture<C>>();
  }
  
  public boolean hasFuture(K key)
  {
    return _map.containsKey(key);
  }

  public Future<C> get(K key)
  {
    return _map.get(key);
  }

  /**
   * retrieve an existing future for the key, or create a new one for it if
   * absent
   * 
   * @param key
   * @return
   */
  public Future<C> acquireOrGet(K key)
  {
    ListenableFuture<C> future = newFuture();
    ListenableFuture<C> originalFuture = _map.putIfAbsent(key, future);
    if (originalFuture != null) return originalFuture;
    return future;
  }

  /**
   * release the future by setting the result
   * 
   * @param key
   * @param result
   * @return
   */
  public Future<C> release(K key, C result)
  {
    ListenableFuture<C> future = _map.remove(key);
    if (future != null) future.set(result, null);
    return future;
  }
  
  /**
   * release the future by setting the exception
   * 
   * @param key
   * @param exception
   * @return
   */
  public Future<C> release(K key, Throwable exception)
  {
    ListenableFuture<C> future = _map.remove(key);
    if (future != null) future.set(null, exception);
    return future;
  }

  /**
   * override if you want to use an extended version of managed future
   * 
   * @return
   */
  protected ListenableFuture<C> newFuture()
  {
    return new ListenableFuture<C>(_noop);
  }
}
