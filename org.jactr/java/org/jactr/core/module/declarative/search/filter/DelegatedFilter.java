package org.jactr.core.module.declarative.search.filter;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

/**
 * fail-fast filter that only accepts if all delegates do.
 * 
 * @author harrison
 */
public class DelegatedFilter implements IChunkFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DelegatedFilter.class);

  private final Collection<IChunkFilter> _filters;

  public DelegatedFilter(Collection<IChunkFilter> filters)
  {
    _filters = new ArrayList<IChunkFilter>(filters);
  }

  public DelegatedFilter()
  {
    _filters = new ArrayList<IChunkFilter>(2);
  }

  public void add(IChunkFilter filter)
  {
    _filters.add(filter);
  }

  @Override
  public boolean accept(IChunk chunk)
  {
    for (IChunkFilter filter : _filters)
      if (!filter.accept(chunk)) return false;

    return true;
  }

}
