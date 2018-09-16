package org.jactr.tools.loop;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;

public class FiringSequenceListener extends ProceduralModuleListenerAdaptor
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(FiringSequenceListener.class);

  private final ProductionLoopDetector _pld;

  public FiringSequenceListener(ProductionLoopDetector pld)
  {
    _pld = pld;
  }

  @Override
  public void productionFired(ProceduralModuleEvent pme)
  {
    _pld.productionFired(pme.getSimulationTime(),
        (IInstantiation) pme.getProduction());
  }
}
