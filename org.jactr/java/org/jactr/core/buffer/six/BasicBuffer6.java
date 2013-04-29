/*
 * Created on Jul 22, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.buffer.six;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.IModule;

public class BasicBuffer6 extends AbstractActivationBuffer6
{
  private IChunk _sourceChunk;

  public BasicBuffer6(String name, IModule module)
  {
    super(name, module);
  }


  protected void setSourceChunkInternal(IChunk sourceChunk)
  {
    try
    {
      getLock().writeLock().lock();
      _sourceChunk = sourceChunk;
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  /**
   * chunkToInsert is a copy of what was passed iff it has been encoded
   * 
   * @see org.jactr.core.buffer.AbstractActivationBuffer#addSourceChunkInternal(org.jactr.core.chunk.IChunk)
   */
  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    IChunk currentSource = getSourceChunk();
    IChunk errorChunk = getErrorChunk();

    /*
     * did something go wrong? set the states..
     */
    if (errorChunk.equals(chunkToInsert))
    {
      setStateChunk(errorChunk);
      chunkToInsert = null;
    }

    /*
     * all is good, let's set the chunk
     */
    if (chunkToInsert != null)
    {
      if (currentSource != null) removeSourceChunk(currentSource);

      setStateChunk(getFreeChunk());
      setErrorChunk(null);
      setBufferChunk(getFullChunk());
      setSourceChunkInternal(chunkToInsert);
    }

    return chunkToInsert;
  }

  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    setSourceChunkInternal(null);
    setBufferChunk(getEmptyChunk());
    setStateChunk(getFreeChunk());
    setErrorChunk(null);
    return true;
  }

  /**
   * @see org.jactr.core.buffer.AbstractActivationBuffer#getSourceChunkInternal()
   */
  @Override
  protected IChunk getSourceChunkInternal()
  {
    return _sourceChunk;
  }

  /**
   * @see org.jactr.core.buffer.AbstractActivationBuffer#getSourceChunksInternal()
   */
  @Override
  protected Collection<IChunk> getSourceChunksInternal(
      Collection<IChunk> container)
  {
    if (_sourceChunk != null) container.add(_sourceChunk);

    return container;
  }


}
