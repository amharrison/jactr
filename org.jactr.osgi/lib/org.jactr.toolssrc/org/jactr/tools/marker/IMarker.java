package org.jactr.tools.marker;

/*
 * default logging
 */
import java.util.Map;

import org.jactr.core.model.IModel;

/**
 * a marker to demark a section of time within a model's execution
 * 
 * @author harrison
 */
public interface IMarker
{

  public IModel getModel();

  public long getId();

  public String getName();

  public String getType();

  public double getStartTime();

  public double getEndTime();

  public Map<String, String> getProperties(Map<String, String> container);

  public void open(double time);

  public boolean isOpen();

  public void close(double time);

  /**
   * flags this marker as starting and ending at the same time.
   * 
   * @param time
   */
  public void instantanious(double time);
}
