/*
 * Created on Aug 22, 2003 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jactr.scripting;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ChunkSlot;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ScriptSupport
{

  static private transient Log LOGGER = LogFactory.getLog(ScriptSupport.class);

  final IModel                 _model;

  final IInstantiation         _instantiation;

  final Map<String, Object>    _bindings;

  final Set<String>            _initialBindings;

  public ScriptSupport(IModel model, Map<String, Object> bindings)
  {
    _model = model;
    _instantiation = null;
    _bindings = bindings;
    _initialBindings = new TreeSet<String>(_bindings.keySet());
  }

  public ScriptSupport(IModel model, IInstantiation instantiation)
  {
    _model = model;
    _instantiation = instantiation;
    _bindings = instantiation.getVariableBindings();
    _initialBindings = new TreeSet<String>(_bindings.keySet());
  }

  public void stop() throws ModelTerminatedException
  {
    throw new ModelTerminatedException();
  }

  public void postMockEvent(double firingTime)
  {
    ITimedEvent event = new AbstractTimedEvent(firingTime, firingTime);
    _model.getTimedEventQueue().enqueue(event);
  }

  /**
   * @param variableName
   * @param value
   */
  public void setGlobal(String variableName, Object value)
  {
    ScopeManager.defineVariable(ScopeManager.getPublicScope(), variableName,
        value);
  }

  public double getTime()
  {
    return ACTRRuntime.getRuntime().getClock(_model).getTime();
  }

  public Object getGlobal(String variableName)
  {
    return ScriptableObject.getProperty(ScopeManager.getPublicScope(),
        variableName);
  }

  public void set(String variableName, Object value)
  {
    variableName = variableName.toLowerCase();
    if (_initialBindings.contains(variableName))
      throw new IllegalArgumentException("Cannot redefine existing binding "
          + variableName);
    _bindings.put(variableName, value);
  }

  public Object get(String variableName)
  {
    variableName = variableName.toLowerCase();
    return _bindings.get(variableName);
  }

  public void requires(Object... variableNames) throws CannotMatchException
  {
    for (Object variableName : variableNames)
    {
      String name = variableName.toString().toLowerCase();
      if (!_bindings.containsKey(name))
        throw new CannotMatchException(String.format("%s is undefined.",
            variableName));
    }
  }

  public void set(IChunk chunk, String slotName, Object value)
  {
    ((ChunkSlot) chunk.getSymbolicChunk().getSlot(slotName)).setValue(value);
  }

  public Object get(IChunk chunk, String slotName)
  {
    return chunk.getSymbolicChunk().getSlot(slotName).getValue();
  }

  public IChunk copy(IChunk chunk)
  {
    return copy(chunk, "copy-of-" + chunk.getSymbolicChunk().getName());
  }

  public IChunk copy(IChunk chunk, String newName)
  {
    try
    {
      IChunk rtn = getModel().getDeclarativeModule().copyChunk(chunk).get();
      rtn.getSymbolicChunk().setName(newName);
      return rtn;
    }
    catch (InterruptedException e)
    {
      // fail up
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getClass() + ".interrupted, throwing back up");
      throw new RuntimeException(e);
    }
    catch (ExecutionException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("ScriptSupport.copy threw ExecutionException : ", e);
      throw new RuntimeException(e);
    }
  }

  public IChunk encode(IChunk chunk)
  {
    try
    {
      return getModel().getDeclarativeModule().addChunk(chunk).get();
    }
    catch (InterruptedException e)
    {
      // fail up
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getClass() + ".interrupted, throwing back up");
      throw new RuntimeException(e);
    }
    catch (ExecutionException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("ScriptSupport.copy threw ExecutionException : ", e);
      throw new RuntimeException(e);
    }
  }

  public IModel getModel()
  {
    return _model;
  }

  public ACTRRuntime getRuntime()
  {
    return ACTRRuntime.getRuntime();
  }

  public void output(String string)
  {
    string = OutputAction.replaceVariables(string, _bindings);
    if (Logger.hasLoggers(_model))
      Logger.log(_model, Logger.Stream.OUTPUT, string);
  }

}