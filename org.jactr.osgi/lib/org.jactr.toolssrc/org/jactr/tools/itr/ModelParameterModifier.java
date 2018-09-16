package org.jactr.tools.itr;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ModelParameterModifier extends AbstractParameterModifier
{
  
  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    ASTSupport support = new ASTSupport();
    support.setParameter(modelDescriptor, parameter, value, true);
  }

  
}
