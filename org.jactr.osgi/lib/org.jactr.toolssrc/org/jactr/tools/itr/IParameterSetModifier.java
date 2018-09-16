package org.jactr.tools.itr;

import java.util.Map;

/*
 * default logging
 */

public interface IParameterSetModifier extends IParameterModifier
{

  public void add(IParameterModifier modifier);

  public void remove(IParameterModifier modifier);

  public Map<String, String> getNestedParameterValues(int parameterValueIndex);
}
