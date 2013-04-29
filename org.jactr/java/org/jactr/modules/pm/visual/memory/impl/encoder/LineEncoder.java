package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.commonreality.modalities.visual.ICommonTypes;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.modalities.visual.geom.Dimension2D;
import org.commonreality.modalities.visual.geom.Point2D;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;

public class LineEncoder extends AbstractVisualEncoder
{
 
  static public final String[][] KEYS = { { "end1-x", "end1-y" },
      { "end2-x", "end2-y" }         };

  public LineEncoder()
  {
    super(IVisualModule.LINE_CHUNK_TYPE);
  }

  @Override
  protected boolean canEncodeVisualObjectType(IAfferentObject afferentObject)
  {
    try
    {
      String[] types = getHandler().getTypes(afferentObject);
      for (String kind : types)
        if (ICommonTypes.LINE.equalsIgnoreCase(kind))
          return getLine(afferentObject) != null;
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
        || lineHasChanged(afferentObject, oldChunk);
  }

  @Override
  protected boolean isTooDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IVisualMemory visualMemory)
  {
    return super.isTooDirty(afferentObject, oldChunk, visualMemory)
        || lineHasChanged(afferentObject, oldChunk);
  }
  
  @Override
  protected void updateSlots(IAfferentObject afferentObject, IChunk encoding,
      IVisualMemory memory)
  {
    super.updateSlots(afferentObject, encoding, memory);
    double[][] line = getLine(afferentObject);
    if(line==null) return;
    
    ISymbolicChunk sc = encoding.getSymbolicChunk();
    for(int n=0;n<line.length;n++)
      for(int i=0;i<line[n].length;i++)
        ((IMutableSlot)sc.getSlot(KEYS[n][i])).setValue(line[n][i]);
  }

  protected boolean lineHasChanged(IAfferentObject afferentObject,
      IChunk encoding)
  {
    double[][] line = getLine(afferentObject);

    // just for safety
    if (line == null) return false;

    ISymbolicChunk sc = encoding.getSymbolicChunk();
    for(int n=0;n<line.length;n++)
      for(int i=0;i<line[n].length;i++)
        if(line[n][i] != ((Number)sc.getSlot(KEYS[n][i]).getValue()).doubleValue())
          return true;
    
    return false;
  }

  protected double[][] getLine(IAfferentObject afferentObject)
  {
    try
    {
      IVisualPropertyHandler handler = getHandler();
      Point2D center = handler.getRetinalLocation(afferentObject);
      Dimension2D size = handler.getRetinalSize(afferentObject);
      double w2 = size.getWidth() / 2;
      double h2 = size.getHeight() / 2;
      double x = center.getX();
      double y = center.getY();
      double slope = handler.getSlope(afferentObject);

      if (Double.isNaN(slope)) // vertical line
        return new double[][] { { x, y - h2 }, { x, y + h2 } };
      if (slope > 0)
        return new double[][] { { x - w2, y - h2 }, { x + w2, y + h2 } };
      return new double[][] { { x - w2, y + h2 }, { x + w2, y - h2 } };
    }
    catch (Exception e)
    {
      return null;
    }
  }
}
