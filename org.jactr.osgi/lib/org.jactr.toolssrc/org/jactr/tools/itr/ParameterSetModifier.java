package org.jactr.tools.itr;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.collections.FastListFactory;

public class ParameterSetModifier implements IParameterSetModifier
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ParameterSetModifier.class);

  private final List<IParameterModifier> _modifiers;

  private final List<String>             _parameterValues;

  private String                         _parameterName;

  public ParameterSetModifier()
  {
    _parameterValues = FastListFactory.newInstance();
    _modifiers = FastListFactory.newInstance();
  }

  public void add(IParameterModifier modifier)
  {
    if (modifier.getParameterValues().size() != getParameterValues().size())
      throw new IllegalArgumentException(modifier
          + " does not have the same number of values as container");
    _modifiers.add(modifier);
  }

  public void remove(IParameterModifier modifier)
  {
    _modifiers.remove(modifier);
  }

  public String getParameterDisplayName()
  {
    return _parameterName;
  }

  public String getParameterName()
  {
    return _parameterName;
  }

  public List<String> getParameterValues()
  {
    return Collections.unmodifiableList(_parameterValues);
  }

  public void setParameter(CommonTree modelDescriptor, int parameterValueIndex)
  {
    if (parameterValueIndex >= _parameterValues.size())
    {
      LOGGER.warn("Cannot set " + _parameterName + " value at index "
          + parameterValueIndex + " values:" + _parameterValues);
      return;
    }

    for (IParameterModifier modifier : _modifiers)
      modifier.setParameter(modelDescriptor, parameterValueIndex);
  }

  public Map<String, String> getNestedParameterValues(int parameterValueIndex)
  {
    Map<String, String> rtn = new TreeMap<String, String>();
    for (IParameterModifier modifier : _modifiers)
      rtn.put(modifier.getParameterDisplayName(), modifier.getParameterValues()
          .get(parameterValueIndex));
    return rtn;
  }

  public void setParameter(CommonTree modelDescriptor, String parameterValue)
  {
    int index = _parameterValues.indexOf(parameterValue);
    setParameter(modelDescriptor, index);
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {

    return null;
  }

  public Collection<String> getSetableParameters()
  {

    return null;
  }

  public void setParameter(String key, String value)
  {
    if (PARAMETER_NAME.equalsIgnoreCase(key))
      _parameterName = value;
    else if (PARAMETER_VALUES.equalsIgnoreCase(key))
    {
      String[] values = value.split(",");
      for (String v : values)
      {
        v = v.trim();
        if (v.length() > 0) _parameterValues.add(v);
      }
    }
  }

}
