package org.jactr.core.module.declarative.six.associative;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.chunk.six.AssociativeLinkEquation6;
import org.jactr.core.module.declarative.four.associative.DefaultAssociativeLinkageSystem4;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.declarative.six.learning.IDeclarativeLearningModule6;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;

/**
 * version six of the linkage system. Links are still created in the same manner (as 4), however
 * since there is no learning of the strengths, the procedural listener is no longer needed, so
 * we override and return null.
 * 
 * @author harrison
 */
public class DefaultAssociativeLinkageSystem6 extends
    DefaultAssociativeLinkageSystem4
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAssociativeLinkageSystem6.class);

  public DefaultAssociativeLinkageSystem6(
      IDeclarativeLearningModule6 learningModule, Executor executor)
  {
    super(learningModule, executor);
  }
  
  
  protected IAssociativeLinkEquation createLinkEquation(IDeclarativeLearningModule4 learningModule)
  {
    return new AssociativeLinkEquation6((IDeclarativeLearningModule6) learningModule);
  }

  /**
   * we use the production firing to trigger the learning
   * 
   * @return
   */
  protected IProceduralModuleListener createProceduralListener(
      IDeclarativeLearningModule4 learningModule, Executor executor)
  {
    return null;
  }

}
