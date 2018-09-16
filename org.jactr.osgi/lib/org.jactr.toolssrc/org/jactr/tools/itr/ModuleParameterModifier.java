package org.jactr.tools.itr;

import java.util.ArrayList;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ModuleParameterModifier extends AbstractParameterModifier
{

  static public final String MODULE_CLASS     = "ModuleClass";

  protected String           _moduleClassName = "";

  
  static public void setModuleParameter(CommonTree modelTree, String moduleClassName, String parameter, String value)
  {
    Collection<CommonTree> modules = ASTSupport.getAllDescendantsWithType(
        modelTree, JACTRBuilder.MODULE);

    for (CommonTree module : modules)
    {
      CommonTree classDesc = ASTSupport.getFirstDescendantWithType(module,
          JACTRBuilder.CLASS_SPEC);
      if (classDesc != null && moduleClassName.equals(classDesc.getText()))
      {
        ASTSupport support = new ASTSupport();
        support.setParameter(module, parameter, value, true);
      }
    }
  }
  
  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    setModuleParameter(modelDescriptor, _moduleClassName, parameter,value);
  }

  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>(super.getSetableParameters());
    rtn.add(MODULE_CLASS);
    return rtn;
  }

  public void setParameter(String key, String value)
  {
    if(MODULE_CLASS.equalsIgnoreCase(key))
      _moduleClassName = value;
    else super.setParameter(key, value);
  }
}
