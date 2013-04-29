/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.modules.pm.common.memory.map;

import java.util.Set;

import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;

/**
 * @author developer
 */
public interface IFINSTFeatureMap extends IFeatureMap<FINSTState>
{


  public int getMaximumFINSTs();

  public void setMaximumFINSTs(int max);

  /**
   * reset all the finsts
   */
  public void reset();

  public void flagAsNew(IIdentifier identifier, IChunk chunk, double duration);

  public void flagAsOld(IIdentifier identifier, IChunk chunk);

  public void flagAsAttended(IIdentifier identifier, IChunk chunk,
      double duration);

  public void getAttended(Set<IIdentifier> destination);

  public boolean isAttended(IIdentifier identifier);

  public void getNew(Set<IIdentifier> destination);

  public boolean isNew(IIdentifier identifier);

  public void getOld(Set<IIdentifier> destination);

  public boolean isOld(IIdentifier identifier);
}
