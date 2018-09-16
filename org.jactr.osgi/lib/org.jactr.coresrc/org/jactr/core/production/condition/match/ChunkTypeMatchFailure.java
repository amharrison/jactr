package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.condition.ICondition;

public class ChunkTypeMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkTypeMatchFailure.class);

  private final IChunkType           _expectedChunkType;

  private final IChunk               _foundChunk;


  public ChunkTypeMatchFailure(IChunkType expected, IChunk found)
  {
    this(null, expected, found);
  }

  public ChunkTypeMatchFailure(ICondition condition, IChunkType expected,
      IChunk found)
  {
    super(condition);
    _expectedChunkType = expected;
    _foundChunk = found;
  }

  public IChunkType getExpectedChunkType()
  {
    return _expectedChunkType;
  }

  public IChunk getFoundChunk()
  {
    return _foundChunk;
  }

  @Override
  public String toString()
  {
    return String.format("%s is not %s (%s)", _foundChunk, _expectedChunkType,
        _foundChunk != null ? _foundChunk.getSymbolicChunk().getChunkType()
            : null);
  }
}
