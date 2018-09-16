package org.jactr.tools.itr;

/*
 * default logging
 */
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ChunkTypeSlotModifier extends AbstractParameterModifier
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkTypeSlotModifier.class);

  static public final String         CHUNK_TYPE = "ChunkType";

  private String                     _chunkTypeName;

  @Override
  public void setParameter(String key, String value)
  {
    if (CHUNK_TYPE.equalsIgnoreCase(key))
      _chunkTypeName = value;
    else
      super.setParameter(key, value);
  }

  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    Map<String, CommonTree> chunkTypes = ASTSupport.getMapOfTrees(
        modelDescriptor, JACTRBuilder.CHUNK_TYPE);
    CommonTree chunkTypeDesc = chunkTypes.get(_chunkTypeName);
    if (chunkTypeDesc == null) return;

    Map<String, CommonTree> slots = ASTSupport.getMapOfTrees(chunkTypeDesc,
        JACTRBuilder.SLOT);
    CommonTree slotDesc = slots.get(getParameterName());
    if (slotDesc == null) return;

    /*
     * try as a number first, then string. compiler should resolve the string if
     * it is a chunk identifier
     */
    slotDesc.deleteChild(2);
    ASTSupport support = new ASTSupport();
    try
    {
      Double.parseDouble(value);
      slotDesc.addChild(support.create(JACTRBuilder.NUMBER, value));
    }
    catch (NumberFormatException nfe)
    {
      slotDesc.addChild(support.create(JACTRBuilder.STRING, value));
    }
  }

}
