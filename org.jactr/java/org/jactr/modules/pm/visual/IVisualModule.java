/*
 * Created on Jul 7, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.visual;

import java.util.concurrent.Executor;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.modules.pm.IPerceptualMemoryModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.modules.pm.visual.memory.IVisualMemory;

public interface IVisualModule extends IPerceptualMemoryModule
{

  
  /**
   * buffer names
   */
  static public final String VISUAL_LOCATION_BUFFER           = IActivationBuffer.VISUAL_LOCATION;

  static public final String VISUAL_BUFFER                    = IActivationBuffer.VISUAL;

  /**
   * chunk type names
   */
  static public final String VISUAL_LOCATION_CHUNK_TYPE       = "visual-location";

  static public final String VISUAL_CHUNK_TYPE                = "visual-object";

  static public final String VISUAL_COMMAND_CHUNK_TYPE        = "vision-command";

  static public final String CLEAR_CHUNK_TYPE                 = "clear";

  static public final String MOVE_ATTENTION_CHUNK_TYPE        = "move-attention";

  static public final String ASSIGN_FINST_CHUNK_TYPE          = "assign-finst";

  static final public String START_TRACKING_CHUNK_TYPE        = "start-tracking";

  static public final String COLOR_CHUNK_TYPE                 = "color";

  static public final String TEXT_CHUNK_TYPE                  = "text";

  static public final String PHRASE_CHUNK_TYPE                = "phrase";

  static public final String OVAL_CHUNK_TYPE                  = "oval";

  static public final String GUI_CHUNK_TYPE                   = "gui";

  static public final String CURSOR_CHUNK_TYPE                = "cursor";

  static public final String EMPTY_CHUNK_TYPE                 = "empty-space";

  static public final String LINE_CHUNK_TYPE                  = "line";

  /**
   * Comment for <code>SCREEN_X_SLOT</code>
   */
  static public final String SCREEN_X_SLOT                    = "screen-x";

  /**
   * Comment for <code>SCREEN_Y</code>
   */
  static public final String SCREEN_Y_SLOT                    = "screen-y";

  /**
   * Comment for <code>SCREEN_Z</code>
   */
  static public final String SCREEN_Z_SLOT                    = "distance";

  /**
   * Comment for <code>KIND</code>
   */
  static public final String KIND_SLOT                        = "kind";

  /**
   * Comment for <code>COLOR</code>
   */
  static public final String COLOR_SLOT                       = "color";

  /**
   * Comment for <code>SIZE</code>
   */
  static public final String SIZE_SLOT                        = "size";

  /**
   * Comment for <code>NEAREST</code>
   */
  static public final String NEAREST_SLOT                     = "nearest";

  /**
   * Comment for <code>OBJECTS</code>
   * deprecated, the objects slot has been removed
   */
  @Deprecated
  static public final String OBJECTS_SLOT                     = "objects";

  /**
   * Comment for <code>SCREEN_POSITION</code>
   */
  static public final String SCREEN_POSITION_SLOT             = "screen-pos";

  /**
   * Comment for <code>VALUE</code>
   */
  static public final String VALUE_SLOT                       = "value";

  /**
   * Comment for <code>WIDTH</code>
   */
  static public final String WIDTH_SLOT                       = "width";

  /**
   * Comment for <code>TYPE</code>
   */
  static public final String TYPE_SLOT                        = "type";

  /**
   * Comment for <code>TOKEN</code>
   */
  static public final String TOKEN_SLOT                       = "token";

  /**
   * Comment for <code>HEIGHT</code>
   */
  static public final String HEIGHT_SLOT                      = "height";

  static public final String ATTENDED_STATUS_SLOT             = ":attended";

  static public final String TIME_STATUS_SLOT                 = ":tstamp";

  static public final String CURRENT_CHUNK                    = "current";

  

  public void addListener(IVisualModuleListener listener, Executor executor);

  public void removeListener(IVisualModuleListener listener);

  public boolean hasListeners();

  public void dispatch(VisualModuleEvent event);

  public IVisualLocationBuffer getVisualLocationBuffer();

  public IVisualActivationBuffer getVisualActivationBuffer();

  public IVisualSearchTimeEquation getSearchTimeEquation();

  public void setSearchTimeEquation(IVisualSearchTimeEquation equation);

  public IVisualEncodingTimeEquation getEncodingTimeEquation();

  public void setEncodingTimeEquation(IVisualEncodingTimeEquation equation);

  public IChunkType getVisualLocationChunkType();

  public IChunkType getVisualChunkType();
  
  /**
   * return the backing short-term iconic memory store
   * @return
   */
  public IVisualMemory getVisualMemory();



  /**
   * engage the visual tracking mechanism..
   * 
   * @param visualChunk
   *            to the visual object or null to turn off
   */
  public void setTrackedVisualChunk(IChunk visualChunk);

  /**
   * reset the visual system. this will typically clear both buffers and force
   * the visual chunk encoders to flush their pre-encoded visual chunks. the
   * visual map should remain in tact unless a refresh command is sent to common
   * reality
   */
  public void reset(boolean resetFINSTs);

  public void assignFINST(IChunk visualChunk);

}
