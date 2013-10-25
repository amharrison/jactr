package org.jactr.core.concurrent;

/*
 * default logging
 */

public interface IListenableFutureListener<C> {
  
  public void done(ListenableFuture<C> future);
  
  public void exception(ListenableFuture<C> future);
  
  public void canceled(ListenableFuture<C> future);
  
}