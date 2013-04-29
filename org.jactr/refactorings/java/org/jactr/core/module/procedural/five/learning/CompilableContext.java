package org.jactr.core.module.procedural.five.learning;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompilableContext implements ICompilableContext
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(CompilableContext.class);

  private boolean _canCompileOut;
  private boolean _isImmediate;
  private boolean _isJammable;
  
  public CompilableContext(boolean isImmediate, boolean isJammable, boolean canCompileOut)
  {
    _isJammable = isJammable;
    _isImmediate = isImmediate;
    _canCompileOut = canCompileOut;
  }
  
  public boolean canCompileOut()
  {
    return _canCompileOut;
  }

  public boolean isImmediate()
  {
    return _isImmediate;
  }

  public boolean isJammable()
  {
    return _isJammable;
  }

}
