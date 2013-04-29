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
package org.jactr.modules.pm.aural.audicon;

import java.util.Collection;

import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.audicon.encoder.IAuralChunkEncoder;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

/**
 * @author developer
 */
@Deprecated
public interface IAudicon
{
  static public final String AUDIO_EVENT_SOUND_LINK = IAudicon.class.getName()
                                                        + ".audioSoundLink";

  public IAuralModule getModule();

  public IFINSTFeatureMap getFINSTFeatureMap();

  /**
   * @param afferentIdentifier
   * @return null if none is available
   */
  public IChunk getAudioEventFor(IIdentifier afferentIdentifier);

  /**
   * clean up and release any resources. this is only called by the aural module
   * on its own disposal
   */
  public void dispose();

  public void clear();

  public Collection<IFeatureMap> getFeatureMaps();

  public void addFeatureMap(IFeatureMap featureMap);

  public void addEncoder(IAuralChunkEncoder encoder);

  public Collection<IAuralChunkEncoder> getEncoders();

  public double getLastChangeTime();

}
