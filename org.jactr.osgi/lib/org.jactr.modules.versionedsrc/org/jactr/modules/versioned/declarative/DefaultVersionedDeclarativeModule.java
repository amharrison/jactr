package org.jactr.modules.versioned.declarative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.declarative.basic.AbstractDeclarativeModule;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.modules.declarative.DelegatedDeclarativeModule;
import org.jactr.modules.versioned.procedural.ProductionRewriter;

public class DefaultVersionedDeclarativeModule extends DelegatedDeclarativeModule implements
    IVersionedDeclarativeModule, IParameterized
{

  static final transient Log LOGGER                     = LogFactory
                                                                    .getLog(DefaultVersionedDeclarativeModule.class);

  ProductionRewriter         _rewriter;

  public DefaultVersionedDeclarativeModule()
  {

  }

  @Override
  public void install(IModel model)
  {
    super.install(model);
    _rewriter = new ProductionRewriter(_model);
  }

  public Future<IChunkType> getChunkType(final String name, final double version)
  {
    return AbstractModule.delayedFuture(new Callable<IChunkType>() {
      public IChunkType call() throws Exception
      {
        IChunkType ct = getChunkTypeInternal(name, version);

        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("Requesting chunk type " + name + " and returning " + ct);
        return ct;
      }
    }, ((AbstractDeclarativeModule) _delegate).getExecutor());
  }

  protected IChunkType getChunkTypeInternal(String name, double version)
  {
    try
    {
      IChunkType ancestor = _delegate.getChunkType(name).get();
      if (ancestor == null) return null; // no chunktype of this name
      while (true)
      {
        String heir = ancestor.getSubsymbolicChunkType().getParameter("heir");
        if (heir != null)
        {
          IChunkType descendant = _delegate.getChunkType(heir).get();
          if (Double.parseDouble(descendant.getSubsymbolicChunkType()
              .getParameter("version")) > version)
            return ancestor;
          else
            ancestor = descendant;
        }
        else
          return ancestor;
      }
    }
    catch (InterruptedException e)
    {
      LOGGER.error(e);
    }
    catch (ExecutionException e)
    {
      LOGGER.error(e);
    }
    return null;
  }


  public Future<IChunk> getChunk(final String name, final double version)
  {
    return AbstractModule.delayedFuture(new Callable<IChunk>() {
      public IChunk call() throws Exception
      {
        return getChunkInternal(name, version);
      }
    }, ((AbstractDeclarativeModule) _delegate).getExecutor());
  }

  protected IChunk getChunkInternal(String name, double version)
  {
    try
    {
      IChunk ancestor = _delegate.getChunk(name).get();
      if (ancestor == null) return null; // no chunk of this name
      while (true)
      {
        String heir = ancestor.getSubsymbolicChunk().getParameter("heir");
        if (heir != null)
        {
          IChunk descendant = _delegate.getChunk(heir).get();
          if (Double.parseDouble(descendant.getSubsymbolicChunk().getParameter(
              "version")) > version)
            return ancestor;
          else
            ancestor = descendant;
        }
        else
          return ancestor;
      }
    }
    catch (InterruptedException e)
    {
      LOGGER.error(e);
    }
    catch (ExecutionException e)
    {
      LOGGER.error(e);
    }
    return null;
  }

  public Future<IChunkType> refineChunkType(final IChunkType ct,
      final int action, final String propName)
  {
    return AbstractModule.delayedFuture(new Callable<IChunkType>() {
      public IChunkType call() throws Exception
      {
        return refineChunkTypeInternal(ct, action, propName);
      }
    }, ((AbstractDeclarativeModule) _delegate).getExecutor());
  }

  private IChunkType refineChunkTypeInternal(IChunkType ct, int action,
      String propName)
  {
    if (action != IVersionedDeclarativeModule.ADD)
    {
      LOGGER
          .error("cannot refine chunk types in ways other than ADD; ignoring request");
      return null;
    }

    IChunkType newParent = null;
    try
    {
      newParent = getChunkType(propName).get();
      if (newParent == null)
      {
        LOGGER.error("CANNOT add parent " + propName
            + " because don't know about this chunk type.");
        return null;
      }
      return refineChunkTypeInternal(ct, action, newParent);
    }
    catch (InterruptedException e)
    {
      LOGGER.error(e);
    }
    catch (ExecutionException e)
    {
      LOGGER.error(e);
    }

    return null;
  }

  private IChunkType refineChunkTypeInternal(IChunkType ct, int action,
      IChunkType newParent)
  {
    try
    {
      LOGGER.debug("adding " + newParent + " to " + ct);
      Collection<IChunkType> newSupertypeParents = Arrays.asList(ct, newParent);

      final IChunkType newCT = createChunkType(newSupertypeParents,
          ct.getSymbolicChunkType().getName()).get();
      ct.getSubsymbolicChunkType().setParameter("heir", newCT.toString());
      newCT.getSubsymbolicChunkType().setParameter("version",
          Double.toString(_model.getAge()));
      addChunkType(newCT);

      _rewriter.refineChunkType(ct, newCT);

      Collection<IChunk> chunks = new ArrayList<IChunk>();
      chunks.addAll(ct.getSymbolicChunkType().getChunks());
      for (IChunk chunk : chunks)
      {
        // create new chunks with the new ct as a parent
        IChunk newChunk = _delegate.createChunk(newCT,
            chunk.getSymbolicChunk().getName()).get();
        for (ISlot slot : chunk.getSymbolicChunk().getSlots())
        {
          LOGGER.debug("duplicated chunk. adding slot " + slot
              + " to newChunk, class is " + slot.getClass());
          newChunk.getSymbolicChunk().addSlot(slot);
          LOGGER.debug("new chunk's slot's class is "
              + newChunk.getSymbolicChunk().getSlot(slot.getName()).getClass());
        }
        chunk.getSubsymbolicChunk().setParameter("heir", newChunk.toString());
        newChunk.getSubsymbolicChunk().setParameter("version",
            Double.toString(_model.getAge()));

        addChunk(newChunk);

        /*
         * trigger the copy/rewrite of dependent productions
         */
        _rewriter.refineChunk(chunk, newChunk);

        List<IActivationBuffer> containingBuffers = FastListFactory
            .newInstance();
        BufferUtilities.getContainingBuffers(chunk, false, containingBuffers);
        for (IActivationBuffer buffer : containingBuffers)
        {
          buffer.removeSourceChunk(chunk);
          buffer.addSourceChunk(newChunk);
        }

        FastListFactory.recycle(containingBuffers);

      }
      // for now, give the new chunks the default values of the added parent

      for (IChunkType type : ct.getSymbolicChunkType().getChildren())
        if (!type.equals(newCT))
          refineChunkTypeInternal(ct, action, newParent);

      return newCT;

    }
    catch (InterruptedException e)
    {
      LOGGER.error(e);
    }
    catch (ExecutionException e)
    {
      LOGGER.error(e);
    }

    return null;
  }

}
