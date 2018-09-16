/*
 * Created on Jun 25, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural;

import java.util.concurrent.Executor;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.modules.pm.IPerceptualMemoryModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.event.AuralModuleEvent;
import org.jactr.modules.pm.aural.event.IAuralModuleListener;
import org.jactr.modules.pm.aural.memory.IAuralMemory;

/**
 * @author developer
 */
public interface IAuralModule extends IPerceptualMemoryModule
{

  static public final String CLEAR_CHUNK_TYPE       = "clear";

  static public final String AUDIO_EVENT_CHUNK_TYPE = "audio-event";

  static public final String SOUND_CHUNK_TYPE       = "sound";

  static public final String DIGIT_CHUNK_TYPE       = "digit";

  static public final String WORD_CHUNK_TYPE        = "word";

  static public final String SPEECH_CHUNK_TYPE      = "speech";

  static public final String TONE_CHUNK_TYPE        = "tone";

  static public final String EXTERNAL_CHUNK         = "external";

  static public final String INTERNAL_CHUNK         = "internal";

  static public final String DURATION_SLOT          = "duration";

  static public final String KIND_SLOT              = "kind";

  static public final String LOCATION_SLOT          = "location";

  static public final String ONSET_SLOT             = "onset";

  static public final String OFFSET_SLOT            = "offset";

  static public final String PITCH_SLOT             = "pitch";

  static public final String AZIMUTH_SLOT           = "azimuth";

  static public final String ELEVATION_SLOT         = "elevation";

  static public final String CONTENT_SLOT           = "content";

  static public final String EVENT_SLOT             = "event";

  static public final String ATTENDED_STATUS_SLOT   = ":attended";

  static public final String AURAL_LOCATION_BUFFER  = IActivationBuffer.AURAL_LOCATION;

  static public final String AURAL_BUFFER           = IActivationBuffer.AURAL;

  public IAuralLocationBuffer getAuralLocationBuffer();

  public IAuralActivationBuffer getAuralActivationBuffer();

  public IAuralEncodingTimeEquation getEncodingTimeEquation();



  /**
   * return the backing aural memory. this is only valid after the module has
   * been connected to CR.
   * 
   * @return
   */
  public IAuralMemory getAuralMemory();

  /**
   * how long does a sound take to decay out of the audicon
   * 
   * @return
   */
  public double getAuralDecayTime();

  public void setAuralDecayTime(double time);



  public void addListener(IAuralModuleListener listener, Executor executor);

  public void removeListener(IAuralModuleListener listener);

  public boolean hasListeners();

  public void dispatch(AuralModuleEvent event);

  public IChunkType getClearChunkType();

  public IChunkType getSoundChunkType();

  public IChunkType getAudioEventChunkType();

  public IChunk getLowestChunk();

  public IChunk getHighestChunk();

  public IChunk getInternalChunk();

  public IChunk getExternalChunk();

  public void reset(boolean resetFINSTs);

}
