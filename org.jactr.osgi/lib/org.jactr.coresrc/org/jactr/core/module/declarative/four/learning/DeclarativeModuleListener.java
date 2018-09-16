/*
 * Created on Oct 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.four.learning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.module.declarative.basic.DefaultAssociativeLinkageSystem;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.DeclarativeModuleListenerAdaptor;

/**
 * this listener is just to attach the ChunkListener to newly created chunks so
 * that we can add and remove links
 * 
 * @author developer
 */
public class DeclarativeModuleListener extends DeclarativeModuleListenerAdaptor
{
  /**
   * logger definition
   */
  static private final Log          LOGGER = LogFactory
                                               .getLog(DeclarativeModuleListener.class);

  DefaultDeclarativeLearningModule4 _learningModule;

  DefaultAssociativeLinkageSystem   _linkageSystem;

  IChunkListener                    _chunkListener;

  public DeclarativeModuleListener(DefaultDeclarativeLearningModule4 learning,
      DefaultAssociativeLinkageSystem linkage)
  {
    _learningModule = learning;
    _linkageSystem = linkage;
    _chunkListener = createChunkListener();
  }

  protected IChunkListener createChunkListener()
  {
    return new ChunkListener();
  }

  protected IDeclarativeLearningModule4 getLearningModule()
  {
    return _learningModule;
  }

  public void chunkCreated(DeclarativeModuleEvent dme)
  {
    IChunk chunk = dme.getChunk();
    // attach to the chunk.. we need to know all changes to the contents of this
    // chunk
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("New chunk created, attaching listener to " + chunk);

    chunk.addListener(_chunkListener, _learningModule.getExecutor());
    if (_linkageSystem.getChunkListener() != null)
      chunk.addListener(_linkageSystem.getChunkListener(), _learningModule
          .getExecutor());
  }

  public void chunkDisposed(DeclarativeModuleEvent dme)
  {
    IChunk chunk = dme.getChunk();

    chunk.removeListener(_chunkListener);
    if (_linkageSystem.getChunkListener() != null)
      chunk.removeListener(_linkageSystem.getChunkListener());
  }

}
