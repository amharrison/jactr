package org.jactr.core.module.declarative.search.filter;

import javolution.text.TextBuilder;

/*
 * default logging
 */

public interface ILoggedChunkFilter extends IChunkFilter
{

  public TextBuilder getMessageBuilder();
}
