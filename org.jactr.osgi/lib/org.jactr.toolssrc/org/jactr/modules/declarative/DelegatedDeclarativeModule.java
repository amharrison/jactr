package org.jactr.modules.declarative;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DelegatedDeclarativeModule implements IDeclarativeModule,
    IParameterized
{

  static final transient Log    LOGGER                     = LogFactory
                                                               .getLog(DelegatedDeclarativeModule.class);

  protected IDeclarativeModule  _delegate;

  protected IModel              _model;

  protected Map<String, String> _deferredParameters;

  public static final String    DECLARATIVE_DELEGATE_PARAM = "DeclarativeDelegateClass";

  public DelegatedDeclarativeModule()
  {
    _deferredParameters = new TreeMap<String, String>();
  }

  public IDeclarativeModule getDelegate()
  {
    return _delegate;
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



  @SuppressWarnings("unchecked")
  public CompletableFuture<IChunkType> createChunkType(IChunkType parent,
      String name)
  {
    return createChunkType(parent == null ? Collections.EMPTY_LIST
        : Collections.singleton(parent), name);
  }

  public CompletableFuture<IChunkType> createChunkType(
      Collection<IChunkType> parents,
      String name)
  {
    return _delegate.createChunkType(parents, name);
  }

  public CompletableFuture<IChunkType> addChunkType(IChunkType chunkType)
  {
    return _delegate.addChunkType(chunkType);
  }

  public CompletableFuture<IChunkType> getChunkType(String name)
  {
    return _delegate.getChunkType(name);
  }

  public CompletableFuture<Collection<IChunkType>> getChunkTypes()
  {
    return _delegate.getChunkTypes();
  }

  public CompletableFuture<IChunk> createChunk(IChunkType parent, String name)
  {
    return _delegate.createChunk(parent, name);
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

  public CompletableFuture<IChunk> copyChunk(IChunk sourceChunk)
  {
    return _delegate.copyChunk(sourceChunk);
  }

  public CompletableFuture<IChunk> copyChunk(IChunk sourceChunk,
      boolean copySubsymbolics)
  {
    return _delegate.copyChunk(sourceChunk, copySubsymbolics);
  }

  public CompletableFuture<IChunk> addChunk(IChunk chunk)
  {
    return _delegate.addChunk(chunk);
  }

  public boolean willEncode(IChunk chunk)
  {
    return _delegate.willEncode(chunk);
  }

  public CompletableFuture<IChunk> getChunk(String name)
  {
    return _delegate.getChunk(name);
  }

  public CompletableFuture<Collection<IChunk>> getChunks()
  {
    return _delegate.getChunks();
  }

  public long getNumberOfChunks()
  {
    return _delegate.getNumberOfChunks();
  }

  public CompletableFuture<Collection<IChunk>> findExactMatches(
      ChunkTypeRequest request,
      Comparator<IChunk> sorter, IChunkFilter filter)
  {
    return _delegate.findExactMatches(request, sorter, filter);
  }

  public CompletableFuture<Collection<IChunk>> findPartialMatches(
      ChunkTypeRequest request, Comparator<IChunk> sorter,
      IChunkFilter filter)
  {
    return _delegate.findPartialMatches(request, sorter, filter);
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

  public void flush()
  {
    _delegate.flush();

  }

}