package org.jactr.io.antlr3.misc;

/*
 * default logging
 */
import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface IErrorReporter
{

  public void reportError(Exception exception);
  public void reportError(String message, RecognitionException exception);
}
