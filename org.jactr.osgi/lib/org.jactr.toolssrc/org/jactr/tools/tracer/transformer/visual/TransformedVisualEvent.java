package org.jactr.tools.tracer.transformer.visual;

/*
 * default logging
 */
import java.io.Serializable;
import java.util.Map;

import org.commonreality.identifier.IIdentifier;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class TransformedVisualEvent extends AbstractTransformedEvent implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1815622603835386088L;
  
  static public enum Type {ENCODED, FOUND, ADDED, REMOVED, UPDATED};
  
  private final Type _type;
  private final IIdentifier _id;
  private final Map<String,Object> _data;
  private final double[] _fieldOfView = new double[2];
  private final double[] _resolution = new double[2];

  public TransformedVisualEvent(String modelName, IVisualModule visualModule, long systemTime,
      double simulationTime, IIdentifier id, Type type)
  {
    this(modelName, visualModule, systemTime, simulationTime, id, type, null);
  }
  
  public TransformedVisualEvent(String modelName, IVisualModule module, long systemTime,
      double simulationTime, IIdentifier id,Type type, Map<String, Object> data)
  {
    super(modelName, "IVisualModule", systemTime, simulationTime, null);
    _type = type;
    _id = id;
    _data = data;
    IVisualMemory map = module.getVisualMemory();
    
    _fieldOfView[0] = map.getHorizontalSpan();
    _fieldOfView[1] = map.getVerticalSpan();
    _resolution[0] = map.getHorizontalResolution();
    _resolution[1] = map.getVerticalResolution();
  }
  
  public Type getType()
  {
    return _type;
  }
  
  public IIdentifier getIdentifier()
  {
    return _id;
  }
  
  public Map<String, Object> getData()
  {
    return _data;
  }
  
  public double[] getFOV()
  {
    return _fieldOfView;
  }
  
  public double[] getResolution()
  {
    return _resolution;
  }
}
