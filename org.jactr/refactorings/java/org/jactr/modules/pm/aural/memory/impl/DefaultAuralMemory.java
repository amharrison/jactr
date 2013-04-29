package org.jactr.modules.pm.aural.memory.impl;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.filter.NumericIndexFilter;
import org.jactr.modules.pm.common.memory.impl.AbstractPerceptualMemory;

public class DefaultAuralMemory extends AbstractPerceptualMemory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAuralMemory.class);

  public DefaultAuralMemory(IAuralModule module,
      IActivePerceptListener listener)
  {
    super(module, new AuralEventIndexManager(module));
    addListener(listener, ExecutorServices.INLINE_EXECUTOR);
    
    addFilter(new NumericIndexFilter(IAuralModule.ONSET_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.OFFSET_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.PITCH_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.AZIMUTH_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.ELEVATION_SLOT, true));
    
  }

  @Override
  protected Comparator<ChunkTypeRequest> createDefaultComparator()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
