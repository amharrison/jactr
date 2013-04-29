package org.jactr.modules.pm.visual.memory;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.IVisualModule;

public interface IVisualMemory extends IPerceptualMemory, IParameterized
{

  static final public String VISUAL_FIELD_WIDTH_PARAM                 = "VisualFieldWidth";

  static final public String VISUAL_FIELD_HEIGHT_PARAM                = "VisualFieldHeight";

  static final public String VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM = "VisualFieldHorizontalResolution";

  static final public String VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM   = "VisualFieldVerticalResolution";



  static final public String MOVEMENT_TOLERANCE_PARAM                 = "MovementTolerance";

  static final public String STICKY_ATTENTION_PARAM                   = "EnableStickyAttention";

  public double getHorizontalSpan();

  public void setHorizontalSpan(double fov);

  public double getVerticalSpan();

  public void setVerticalSpan(double fov);

  public int getHorizontalResolution();

  public void setHorizontalResolution(int resolution);

  public int getVerticalResolution();

  public void setVerticalResolution(int resolution);



  public double getMovementTolerance();

  public void setMovementTolerance(double tolerance);

  
  public void setStickyAttentionEnabled(boolean enabled);
  public boolean isStickyAttentionEnabled();

  public IVisualModule getVisualModule();

  public IChunk getVisualLocationChunkAt(double x, double y);
}
