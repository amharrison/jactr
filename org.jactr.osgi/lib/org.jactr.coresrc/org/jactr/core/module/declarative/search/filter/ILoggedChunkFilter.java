package org.jactr.core.module.declarative.search.filter;

import org.jactr.core.logging.IMessageBuilder;

/*
 * default logging
 */

public interface ILoggedChunkFilter extends IChunkFilter
{

  public IMessageBuilder getMessageBuilder();
}
