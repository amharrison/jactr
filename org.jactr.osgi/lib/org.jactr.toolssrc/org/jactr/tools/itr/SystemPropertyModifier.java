package org.jactr.tools.itr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;

public class SystemPropertyModifier implements IParameterModifier
{

  static public final String PROPERTY_NAME_PARAM   = "SystemProperty";

  static public final String PROPERTY_VALUES_PARAM = "PropertyValues";

  private List<String>       _propertyValues;

  private String             _propertyName;

  public SystemPropertyModifier()
  {
    _propertyValues = new ArrayList<String>();
  }

  public String getParameterName()
  {
    return _propertyName;
  }

  public String getParameterDisplayName()
  {
    return getParameterName();
  }

  public List<String> getParameterValues()
  {
    return Collections.unmodifiableList(_propertyValues);
  }
  
  public void setParameter(CommonTree modelDescriptor, String parameterValue)
  {
    System.setProperty(_propertyName, parameterValue);
  }

  public void setParameter(CommonTree modelDescriptor, int parameterValueIndex)
  {
    setParameter(modelDescriptor, _propertyValues.get(parameterValueIndex));
  }

  public String getParameter(String key)
  {
    if (PROPERTY_NAME_PARAM.equalsIgnoreCase(key)) return _propertyName;
    if (PROPERTY_VALUES_PARAM.equalsIgnoreCase(key))
      return _propertyValues.toString();

    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Arrays.asList(new String[] { PROPERTY_NAME_PARAM,
        PROPERTY_VALUES_PARAM });
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if (PROPERTY_NAME_PARAM.equalsIgnoreCase(key))
      _propertyName = value;
    else if (PROPERTY_VALUES_PARAM.equalsIgnoreCase(key))
    {
      String[] split = value.split(",");
      for (String val : split)
      {
        val = val.trim();
        if (val.length() != 0) _propertyValues.add(val);
      }
    }
  }

}
