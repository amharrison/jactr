package org.jactr.extensions.cached.procedural.invalidators;

/*
 * default logging
 */
import org.jactr.extensions.cached.procedural.internal.ListenerHub;

public interface IInvalidator
{

  public void invalidate();
  
  public void register(ListenerHub hub);
  
  public void unregister(ListenerHub hub);
}
