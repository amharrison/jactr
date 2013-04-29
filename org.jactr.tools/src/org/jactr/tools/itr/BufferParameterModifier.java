package org.jactr.tools.itr;

import java.util.Collection;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class BufferParameterModifier extends AbstractParameterModifier
{

  static public final String BUFFER_NAME = "BufferName";
  
  private String _bufferName;
  
  
  static public void setBufferParameter(CommonTree modelDescriptor, String bufferName, String parameterName, String value)
  {
    Map<String, CommonTree> buffers = ASTSupport.getMapOfTrees(modelDescriptor, JACTRBuilder.BUFFER);
    for(String name : buffers.keySet())
      if(name.equalsIgnoreCase(bufferName))
      {
        ASTSupport support = new ASTSupport();
        support.setParameter(buffers.get(bufferName), parameterName, value, true);
      }
  }
  
  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    setBufferParameter(modelDescriptor, _bufferName, parameter, value);
  }

  @Override
  public String getParameterDisplayName()
  {
    return _bufferName + "." + getParameterName();
  }

  
  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = super.getSetableParameters();
    rtn.add(BUFFER_NAME);
    return rtn;
  }
  
  @Override
  public void setParameter(String key, String value)
  {
    if(BUFFER_NAME.equalsIgnoreCase(key))
      _bufferName = value;
    else super.setParameter(key, value);
  }
}
