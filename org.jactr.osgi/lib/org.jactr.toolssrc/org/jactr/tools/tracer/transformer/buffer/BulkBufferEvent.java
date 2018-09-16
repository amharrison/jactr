package org.jactr.tools.tracer.transformer.buffer;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class BulkBufferEvent extends AbstractTransformedEvent
{
  /**
   * 
   */
  private static final long          serialVersionUID = 802821762699655390L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BulkBufferEvent.class);

  private boolean                    _isPostConflict  = false;

  public BulkBufferEvent(String modelName, double simulationTime,
      CommonTree ast, boolean postConflict)
  {
    super(modelName, modelName, System.currentTimeMillis(), simulationTime, ast);
    _isPostConflict = postConflict;
  }

  public boolean isPostConflictResolution()
  {
    return _isPostConflict;
  }

}
