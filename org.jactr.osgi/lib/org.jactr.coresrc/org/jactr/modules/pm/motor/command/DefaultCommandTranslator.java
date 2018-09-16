package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.object.manager.IEfferentObjectManager;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;

public class DefaultCommandTranslator implements ICommandTranslator
{
  /**
   * Logger definition
   */
  static private final transient Log             LOGGER = LogFactory
                                                            .getLog(DefaultCommandTranslator.class);

  private Collection<ICommandTranslatorDelegate> _delegates;

  private Map<String, IIdentifier>               _muscleCache;

  public DefaultCommandTranslator()
  {
    _delegates = new ArrayList<ICommandTranslatorDelegate>();
    _muscleCache = new TreeMap<String, IIdentifier>();
  }

  public void add(ICommandTranslatorDelegate delegate)
  {
    _delegates.add(delegate);
  }

  public void remove(ICommandTranslatorDelegate delegate)
  {
    _delegates.remove(delegate);
  }

  public Collection<ICommandTranslatorDelegate> getDelegates()
  {
    return new ArrayList<ICommandTranslatorDelegate>(_delegates);
  }

  protected ICommandTranslatorDelegate getDelegate(ChunkTypeRequest request)
      throws IllegalArgumentException
  {
    for (ICommandTranslatorDelegate delegate : _delegates)
      if (delegate.handles(request)) return delegate;

    throw new IllegalArgumentException(
        "No ICommandTranslatorDelegate can handle " + request);
  }

  public IEfferentObject getMuscle(ChunkTypeRequest request, IModel model)
      throws IllegalArgumentException
  {
    return getDelegate(request).getMuscle(request, model);
  }

  public IEfferentCommand translate(ChunkTypeRequest request,
      IEfferentObject muscle, IModel model) throws IllegalArgumentException
  {
    return getDelegate(request).translate(request, muscle, model);
  }

  public void adjust(ChunkTypeRequest request, DeltaTracker tracker,
      IEfferentCommand command, IModel model) throws IllegalArgumentException,
      OperationNotSupportedException
  {
    getDelegate(request).adjust(request, tracker, command, model);
  }

  public IEfferentObject getMuscle(String muscleName, IModel model)
      throws IllegalArgumentException
  {
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);
    IEfferentObjectManager manager = agent.getEfferentObjectManager();
    IEfferentObject rtn = null;
    IIdentifier mid = _muscleCache.get(muscleName.toLowerCase());
    if (mid == null)
      for (IIdentifier id : manager.getIdentifiers())
      {
        rtn = manager.get(id);
        if (MotorUtilities.isMotor(rtn)
            && muscleName.equalsIgnoreCase(MotorUtilities.getName(rtn)))
        {
          _muscleCache.put(muscleName.toLowerCase(), id);
          break;
        }
        else
          rtn = null;
      }
    else
      rtn = manager.get(mid);

    if (rtn == null)
      throw new IllegalArgumentException("No muscle named " + muscleName
          + " found.");

    return rtn;
  }

}
