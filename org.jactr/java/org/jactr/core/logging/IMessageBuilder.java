package org.jactr.core.logging;

/*
 * default logging
 */

/**
 * recyclable, reusable message builder for log building. This is abstracted
 * away from StringBuilder as there are other, better alternatives.
 * 
 * @author harrison
 */
public interface IMessageBuilder
{

  public String toString();

  public IMessageBuilder clear();

  public IMessageBuilder append(String str);

  public IMessageBuilder append(Object obj);

  public IMessageBuilder prepend(String str);

  public IMessageBuilder prepend(Object obj);
}
