package org.jactr.core.concurrent;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.FutureTask;

public class ListenableFuture<C> extends FutureTask<C>
{

  Collection<IListenableFutureListener<C>> _listeners = new ArrayList<IListenableFutureListener<C>>();

  public ListenableFuture(Runnable runner)
  {
    super(runner, null);
  }

  /**
   * set the result or exception on this future, releasing any blocking
   * {@link #get()} calls
   * 
   * @param result
   * @param thrown
   */
  public void set(C result, Throwable thrown)
  {
    if (thrown != null) setException(thrown);
    set(result);
  }

  @Override
  protected void setException(Throwable thrown)
  {
    super.setException(thrown);

    for (IListenableFutureListener<C> listener : _listeners)
      listener.exception(this);
  }

  @Override
  protected void set(C result)
  {
    super.set(result);

    for (IListenableFutureListener<C> listener : _listeners)
      listener.done(this);
  }

  @Override
  public boolean cancel(boolean mayInterrupt)
  {
    boolean rtn = super.cancel(mayInterrupt);

    if (rtn) for (IListenableFutureListener<C> listener : _listeners)
      listener.canceled(this);

    return rtn;
  }

  public void add(IListenableFutureListener<C> listener)
  {
    _listeners.add(listener);
  }

  public void remove(IListenableFutureListener<C> listener)
  {
    _listeners.remove(listener);
  }

}