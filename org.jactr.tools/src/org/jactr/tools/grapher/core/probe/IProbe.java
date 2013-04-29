package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public interface IProbe<T>
{
  
  public IProbe<T> instantiate(T referant);
  
  public void install(T referant, Executor executor);

  public String getTrackedName();

  public boolean getChanges(Set<String> additions,
      Map<String, Object> changes, Set<String> removed);
}
