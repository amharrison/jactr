package org.jactr.modules.pm.common.memory;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;

public class PerceptualSearchResult
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PerceptualSearchResult.class);

  private final IChunk _indexChunk;
  private final ChunkTypeRequest _originalRequest;
  private final ChunkTypeRequest _expandedRequest;
  private final IChunk _perceptualChunk;
  private final IIdentifier _identifier;

  private IChunk                     _errorChunk;

  private boolean                    _isValid;
  
  public PerceptualSearchResult(IChunk perceptualChunk, IChunk indexChunk, IIdentifier identifier,
      ChunkTypeRequest originalRequest, ChunkTypeRequest expanded)
  {
    _perceptualChunk = perceptualChunk;
    _identifier = identifier;
    _indexChunk = indexChunk;
    _originalRequest = originalRequest;
    _expandedRequest = expanded;
    _isValid = true;
  }

  public void setErrorCode(IChunk error)
  {
    _errorChunk = error;
    invalidate();
  }

  public IChunk getErrorCode()
  {
    return _errorChunk;
  }

  public boolean isValid()
  {
    return _isValid;
  }

  public void invalidate()
  {
    _isValid = false;
  }
   
  public IChunk getPercept()
  {
    return _perceptualChunk;
  }
  
  public IChunk getLocation()
  {
    return _indexChunk;
  }
  
  public ChunkTypeRequest getRequest()
  {
    return _originalRequest;
  }
  
  public ChunkTypeRequest getLocationRequest()
  {
    return _expandedRequest;
  }
  
  public IIdentifier getPerceptIdentifier()
  {
    return _identifier;
  }
  
  @Override
  public String toString()
  {
    return String.format("[Search found %1$s @ %3$s as %2$s]", _identifier, _perceptualChunk, _indexChunk);
  }
}
