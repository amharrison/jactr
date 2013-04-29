/*
 * Created on Nov 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.retrieval.buffer;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IllegalActivationBufferStateException;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.module.procedural.six.learning.DefaultCompilableContext;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.utils.StringUtilities;

/**
 * default retrieval buffer
 * 
 * @author developer
 */
public class DefaultRetrievalBuffer6 extends DefaultDelegatedRequestableBuffer6
    implements ICompilableBuffer
{
  /**
   * logger definition
   */
  static private final Log           LOGGER = LogFactory
                                                .getLog(DefaultRetrievalBuffer6.class);

  ICompilableContext _compilableContext = new DefaultCompilableContext(false, false, false, true);

  
  // protected boolean _ignoreStatus = true;
  //
  // protected double _minimumRetrievalTime = 0.05;
  //
  // protected StartRetrieval _currentRetrieval = null;
  //
  // protected IChunk _sourceChunk;
  protected RetrievalRequestDelegate _retrievalDelegate;

  public DefaultRetrievalBuffer6(String name, IRetrievalModule module)
  {
    super(name, module);

    // addRequestDelegate(new RetrievalRequestDelegate(module, this));
    _retrievalDelegate = new RetrievalRequestDelegate(module);
    _retrievalDelegate.setAsynchronous(true);
  }

  @Override
  public void initialize()
  {
    super.initialize();
    /*
     * we cant install this until now because we need the clear chunktype
     * defined
     */
    try
    {
      addRequestDelegate(new ClearRequestDelegate(getModel()
          .getDeclarativeModule().getChunkType("clear").get()));
    }
    catch (Exception e)
    {
      throw new IllegalActivationBufferStateException(
          "Could not find clear chunktype", e);
    }

    addRequestDelegate(_retrievalDelegate);
  }

  @Override
  protected Collection<IChunk> clearInternal()
  {
    _retrievalDelegate.clear();
    return super.clearInternal();
  }

  /**
   * this lets the retrieval buffer contain the original chunk and not its copy
   * 
   * @return false
   * @see org.jactr.core.buffer.six.AbstractActivationBuffer6#copyChunkOnInsertion()
   */
  @Override
  protected boolean shouldCopyOnInsertion(IChunk chunk)
  {
    return false;
  }



  @Override
  protected void setSourceChunkInternal(IChunk sourceChunk)
  {
    super.setSourceChunkInternal(sourceChunk);
    if (sourceChunk != null)
    {
      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.RETRIEVAL, String.format(
            "Retrieved %s", StringUtilities.toString(sourceChunk)));
    }
  }


  public ICompilableContext getCompilableContext()
  {
	 return _compilableContext;
  }


}
