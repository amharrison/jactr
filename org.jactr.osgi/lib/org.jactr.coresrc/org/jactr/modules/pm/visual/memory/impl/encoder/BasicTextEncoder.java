package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;

public class BasicTextEncoder extends AbstractVisualEncoder
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BasicTextEncoder.class);

  private final String               _commonTypeName;

  public BasicTextEncoder(String chunkTypeName, String commonTypeName)
  {
    super(chunkTypeName);
    _commonTypeName = commonTypeName;
  }

  @Override
  protected boolean canEncodeVisualObjectType(IAfferentObject afferentObject)
  {
    try
    {
      // just make sure there is a textual property
      getHandler().getText(afferentObject);

      String[] types = getHandler().getTypes(afferentObject);
      for (String kind : types)
        if (_commonTypeName.equalsIgnoreCase(kind)) return true;

      return false;
    }
    catch (UnknownPropertyNameException e)
    {
      return false;
    }
  }

  @Override
  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    return super.isDirty(afferentObject, oldChunk, memory)
        || textHasChanged(afferentObject, oldChunk);
  }

  @Override
  protected boolean isTooDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IVisualMemory visualMemory)
  {
    return textHasChanged(afferentObject, oldChunk);
  }

  protected void updateSlots(IAfferentObject afferentObject, IChunk encoding,
      IVisualMemory memory)
  {
    super.updateSlots(afferentObject, encoding, memory);
    /*
     * now set the value...
     */
    ((IMutableSlot) encoding.getSymbolicChunk().getSlot(
        IVisualModule.VALUE_SLOT)).setValue(getHandler()
        .getText(afferentObject));
  }

  protected String guessChunkName(IAfferentObject afferentObject)
  {
    String text =getHandler().getText(afferentObject); 
    return ((text==null || text.length()==0)?super.guessChunkName(afferentObject):text);
  }

  protected boolean textHasChanged(IAfferentObject afferentObject,
      IChunk encoding)
  {
    String currentText = getHandler().getText(afferentObject);
    Object oldValue = encoding.getSymbolicChunk().getSlot(
        IVisualModule.VALUE_SLOT).getValue();
    String oldText = "";
    if (oldValue != null) oldText = oldValue.toString();

    return !currentText.equalsIgnoreCase(oldText);
  }
}
