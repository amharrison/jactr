package org.jactr.tools.itr;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ProductionParameterModifier extends AbstractParameterModifier
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER             = LogFactory
                                                      .getLog(ProductionParameterModifier.class);

  static public final String   PRODUCTION_PATTERN = "ProductionPattern";

  private Pattern              _production;

  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    if(_production==null) return;
    
    Map<String, CommonTree> productions = ASTSupport.getMapOfTrees(modelDescriptor,
        JACTRBuilder.PRODUCTION);
    for (String productionName : productions.keySet())
      if (_production.matcher(productionName).matches())
      {
        ASTSupport support = new ASTSupport();
        support.setParameter(productions.get(productionName), parameter, value, true);
      }
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = super.getSetableParameters();
    rtn.add(PRODUCTION_PATTERN);
    return rtn;
  }

  @Override
  public String getParameterDisplayName()
  {
    return _production.toString() + "." + getParameterName();
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (PRODUCTION_PATTERN.equalsIgnoreCase(key))
      try
      {
        _production = Pattern.compile(value);
      }
      catch (PatternSyntaxException e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER.error("Could not compile production pattern : " + value, e);
        _production = null;
      }
    else
      super.setParameter(key, value);
  }
}
