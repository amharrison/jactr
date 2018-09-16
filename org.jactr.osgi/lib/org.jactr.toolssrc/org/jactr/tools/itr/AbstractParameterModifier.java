package org.jactr.tools.itr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;

/**
 * base class for an iterative listener that can modify a parameter value in a
 * model.
 * 
 * @author harrison
 */
public abstract class AbstractParameterModifier implements
    IIterativeRunListener, IParameterized, IParameterModifier
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER = LogFactory
      .getLog(AbstractParameterModifier.class);

  private String             _parameterName;

  private List<String>       _parameterValues;

  public AbstractParameterModifier()
  {
    _parameterName = "";
    _parameterValues = new ArrayList<String>();
  }

  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {
    // NoOp

  }

  public void preLoad(int currentRunIndex, int totalRuns) throws TerminateIterativeRunException
  {

  }

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    // NoOp
  }
  
  public List<String> getParameterValues()
  {
    return Collections.unmodifiableList(_parameterValues);
  }
  
  public void setParameterValues(Collection<String> values)
  {
    _parameterValues.clear();
    _parameterValues.addAll(values);
  }
  
  public void setParameterName(String name)
  {
    _parameterName = name;
  }
  
  public String getParameterName()
  {
    return _parameterName;
  }
  
  public String getParameterDisplayName()
  {
    return _parameterName;
  }

  public void setParameter(CommonTree modelDescriptor, String parameterValue)
  {
    setParameter(modelDescriptor, _parameterName, parameterValue);
  }

  abstract protected void setParameter(CommonTree modelDescriptor,
      String parameter, String value);
  
  public void setParameter(CommonTree modelDescriptor, int parameterValueIndex)
  {
    if(parameterValueIndex>= _parameterValues.size())
    {
      LOGGER.warn("Cannot set "+_parameterName+" value at index "+parameterValueIndex+" values:"+_parameterValues);
      return;
    }
    
    setParameter(modelDescriptor, _parameterValues.get(parameterValueIndex));
  }

  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors) throws TerminateIterativeRunException
  {
    // no parameters to set
    if (_parameterValues.size() == 0) return;

    int parameterValueIndex = Math.min(_parameterValues.size() - 1,
        (currentRunIndex - 1) / (totalRuns / _parameterValues.size()));
    _parameterValues.get(parameterValueIndex);

    for (CommonTree modelDescriptor : modelDescriptors)
      setParameter(modelDescriptor, parameterValueIndex);
  }

  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    // NoOp
  }

  public void start(int totalRuns) throws TerminateIterativeRunException
  {
    // NoOp
  }

  public void stop()
  {
    // NoOp
  }

  public String getParameter(String key)
  {
    // NoOp this will never be queried during normal run..
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>();
    rtn.add(PARAMETER_VALUES);
    rtn.add(PARAMETER_NAME);
    return rtn;
  }

  public void setParameter(String key, String value)
  {
    if (PARAMETER_NAME.equalsIgnoreCase(key))
      _parameterName = value;
    else if (PARAMETER_VALUES.equalsIgnoreCase(key))
    {
      String[] values = value.split(",");
      _parameterValues.clear();
      for (String v : values)
      {
        v = v.trim();
        if (v.length() != 0) _parameterValues.add(v);
      }
    }

  }

}
