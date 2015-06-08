package org.jactr.core.logging.impl;

/*
 * default logging
 */
import javolution.text.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.logging.IMessageBuilder;

public class TextMessageBuilder implements IMessageBuilder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TextMessageBuilder.class);

  private Text                       _internal = new Text("");

  public TextMessageBuilder()
  {
  }

  @Override
  public IMessageBuilder clear()
  {
    _internal.delete(0, _internal.length());
    return this;
  }

  @Override
  public IMessageBuilder append(String str)
  {
    _internal.insert(_internal.length() - 1, new Text(str));
    return this;
  }

  @Override
  public IMessageBuilder prepend(String str)
  {
    _internal.insert(0, new Text(str));
    return this;
  }

  @Override
  public String toString()
  {
    return _internal.toString();
  }

  @Override
  public IMessageBuilder append(Object obj)
  {
    throw new IllegalStateException("pending impl");
  }

  @Override
  public IMessageBuilder prepend(Object obj)
  {
    throw new IllegalStateException("pending impl");

  }

}
