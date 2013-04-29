package org.jactr.modules.versioned.declarative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.basic.AbstractDeclarativeModule;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.versioned.procedural.ProductionRewriter;

public class DefaultVersionedDeclarativeModule implements
    IVersionedDeclarativeModule, IParameterized
{

  static private final transient Log LOGGER                     = LogFactory
                                                                    .getLog(DefaultVersionedDeclarativeModule.class);

  private IDeclarativeModule         _delegate;

  private IModel                     _model;

  private Map<String, String>        _deferredParameters;

  static public final String         DECLARATIVE_DELEGATE_PARAM = "DeclarativeDelegateClass";

  private ProductionRewriter         _rewriter;

  public DefaultVersionedDeclarativeModule()
  {
    _deferredParameters = new TreeMap<String, String>();
  }

  public void install(IModel model)
  {
    if (_delegate == null)
      throw new IllegalModuleStateException(
          "There must be a primary declarative module installed");

    _model = model;

    /*
     * apply the deferred parameters
     */
    for (Map.Entry<String, String> param : _deferredParameters.entrySet())
      if (_delegate instanceof IParameterized)
        ((IParameterized) _delegate).setParameter(param.getKey(),
            param.getValue());

    _delegate.install(model);

    _rewriter = new ProductionRewriter(_model);
  }

  public void initialize()
  {
    _delegate.initialize();
  }

  public String getName()
  {
    return _delegate.getName();
  }

  public IModel getModel()
  {
    return _model;
  }

  public void dispose()
  {
    _delegate.dispose();
  }

  public void reset()
  {
    _delegate.reset();
  }

  public void uninstall(IModel model)
  {
    _model = null;
    _delegate.uninstall(model);
  }

  public Object getAdapter(Class adapterClass)
  {
    Object rtn = null;
    if (adapterClass.isAssignableFrom(getClass())) rtn = this;

    if (rtn == null) // check delegates
      rtn = _delegate.getAdapter(adapterClass);

    return rtn;
  }

  public void setParameter(String key, String value)
  {
    if (DECLARATIVE_DELEGATE_PARAM.equalsIgnoreCase(key))
      try
      {
        _delegate = (IDeclarativeModule) ParameterHandler.classInstance()
            .coerce(value).newInstance();
      }
      catch (Exception e)
      {
        LOGGER.error(
            String.format("Could not create primary delegate %s %s", value,
                e.getMessage()), e);
        throw new IllegalModuleStateException(String.format(
            "Could not create primary delegate %s %s", value, e.getMessage()));
      }
    else
      _deferredParameters.put(key, value);
  }

  public String getParameter(String key)
  {
    if (key.equals(DECLARATIVE_DELEGATE_PARAM))
      return _delegate.getClass().toString();
    else
      return ((IParameterized) _delegate).getParameter(key);
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    Collection<String> params = ((IParameterized) _delegate)
        .getSetableParameters();
    params.add(DECLARATIVE_DELEGATE_PARAM);
    return params;
  }

  public Future<IChunkType> getChunkType(final String name, final double version)
  {
    return AbstractModule.delayedFuture(new Callable<IChunkType>() {
      public IChunkType call() throws Exception
      {
        LOGGER.debug("Requesting chunk type " + name + " and returning "
            + getChunkTypeInternal(name, version));
        return getChunkTypeInternal(name, version);
      }
    }, ((AbstractDeclarativeModule) _delegate).getExecutor());
  }

  private IChunkType getChunkTypeInternal(String name, double version)
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

  private IChunk getChunkInternal(String name, double version)
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

  public Future<IChunkType> createChunkType(IChunkType parent, String name)
  {
    return createChunkType(parent == null ? Collections.EMPTY_LIST
        : Collections.singleton(parent), name);
  }

  public Future<IChunkType> createChunkType(Collection<IChunkType> parents,
      String name)
  {
    return _delegate.createChunkType(parents, name);
  }

  public Future<IChunkType> addChunkType(IChunkType chunkType)
  {
    return _delegate.addChunkType(chunkType);
  }

  public Future<IChunkType> getChunkType(String name)
  {
    return getChunkType(name, _model.getAge());
  }

  public Future<Collection<IChunkType>> getChunkTypes()
  {
    return _delegate.getChunkTypes();
  }

  public Future<IChunk> createChunk(IChunkType parent, String name)
  {
    return _delegate.createChunk(parent, name);
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

        FastList<IActivationBuffer> containingBuffers = FastList.newInstance();
        BufferUtilities.getContainingBuffers(chunk, false, containingBuffers);
        for (IActivationBuffer buffer : containingBuffers)
        {
          buffer.removeSourceChunk(chunk);
          buffer.addSourceChunk(newChunk);
        }

        FastList.recycle(containingBuffers);

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

  public void dispose(IChunk chunk)
  {
    _delegate.dispose(chunk);
  }

  public IAssociativeLinkageSystem getAssociativeLinkageSystem()
  {
    return _delegate.getAssociativeLinkageSystem();
  }

  public void setAssociativeLinkageSystem(
      IAssociativeLinkageSystem linkageSystem)
  {
    _delegate.setAssociativeLinkageSystem(linkageSystem);
  }

  public Future<IChunk> copyChunk(IChunk sourceChunk)
  {
    return _delegate.copyChunk(sourceChunk);
  }

  public Future<IChunk> copyChunk(IChunk sourceChunk, boolean copySubsymbolics)
  {
    return _delegate.copyChunk(sourceChunk, copySubsymbolics);
  }

  public Future<IChunk> addChunk(IChunk chunk)
  {
    return _delegate.addChunk(chunk);
  }

  public boolean willEncode(IChunk chunk)
  {
    return _delegate.willEncode(chunk);
  }

  public Future<IChunk> getChunk(String name)
  {
    return getChunk(name, _model.getAge());
  }

  public Future<Collection<IChunk>> getChunks()
  {
    return _delegate.getChunks();
  }

  public long getNumberOfChunks()
  {
    return _delegate.getNumberOfChunks();
  }

  public Future<Collection<IChunk>> findExactMatches(ChunkTypeRequest request,
      Comparator<IChunk> sorter, double activationThreshold, boolean bestOne)
  {
    return _delegate.findExactMatches(request, sorter, activationThreshold,
        bestOne);
  }

  public Future<Collection<IChunk>> findPartialMatches(
      ChunkTypeRequest request, Comparator<IChunk> sorter,
      double activationThreshold, boolean bestOne)
  {
    return _delegate.findPartialMatches(request, sorter, activationThreshold,
        bestOne);
  }

  public IChunk getBusyChunk()
  {
    return _delegate.getBusyChunk();
  }

  public IChunk getEmptyChunk()
  {
    return _delegate.getEmptyChunk();
  }

  public IChunk getErrorChunk()
  {
    return _delegate.getErrorChunk();
  }

  public IChunk getFreeChunk()
  {
    return _delegate.getFreeChunk();
  }

  public IChunk getFullChunk()
  {
    return _delegate.getFullChunk();
  }

  public IChunk getNewChunk()
  {
    return _delegate.getNewChunk();
  }

  public IChunk getRequestedChunk()
  {
    return _delegate.getRequestedChunk();
  }

  public IChunk getUnrequestedChunk()
  {
    return _delegate.getUnrequestedChunk();
  }

  public void addListener(IDeclarativeModuleListener listener, Executor executor)
  {
    _delegate.addListener(listener, executor);
  }

  public void removeListener(IDeclarativeModuleListener listener)
  {
    _delegate.removeListener(listener);
  }

}
