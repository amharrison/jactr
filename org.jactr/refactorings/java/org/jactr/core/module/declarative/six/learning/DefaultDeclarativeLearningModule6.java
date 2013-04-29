package org.jactr.core.module.declarative.six.learning;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4;
import org.jactr.core.module.declarative.four.learning.ProceduralModuleListener;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;

public class DefaultDeclarativeLearningModule6 extends
    DefaultDeclarativeLearningModule4
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultDeclarativeLearningModule6.class);

  public DefaultDeclarativeLearningModule6()
  {
  }

  @Override
  protected IProceduralModuleListener createProceduralListener()
  {
    return new ProceduralModuleListener(this) {
      /**
       * we no longer use associative links between chunk contents
       */
      @Override
      protected boolean associateConcurrentChunks()
      {
        return false;
      }
    };
  }
}
