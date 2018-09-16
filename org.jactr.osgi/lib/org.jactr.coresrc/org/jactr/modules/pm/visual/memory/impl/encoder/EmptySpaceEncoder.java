package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.commonreality.modalities.visual.ICommonTypes;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.modules.pm.visual.IVisualModule;

public class EmptySpaceEncoder extends AbstractVisualEncoder
{


  public EmptySpaceEncoder()
  {
    super(IVisualModule.EMPTY_CHUNK_TYPE);
  }
  
  @Override
  protected boolean canEncodeVisualObjectType(IAfferentObject afferentObject)
  {
    try
    {
      String[] types = getHandler().getTypes(afferentObject);
      for (String kind : types)
        if (ICommonTypes.EMPTY.equalsIgnoreCase(kind)) return true;
      return false;
    }
    catch (UnknownPropertyNameException e)
    {
      return false;
    }
  }

}
