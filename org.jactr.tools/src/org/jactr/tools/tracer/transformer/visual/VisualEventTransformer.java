package org.jactr.tools.tracer.transformer.visual;

/*
 * default logging
 */
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.IACTREvent;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.FeatureMapEvent;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class VisualEventTransformer implements IEventTransformer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualEventTransformer.class);

  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    if (actrEvent instanceof VisualModuleEvent)
    {
      VisualModuleEvent vme = (VisualModuleEvent) actrEvent;
      VisualModuleEvent.Type type = vme.getType();
      if (type == VisualModuleEvent.Type.ENCODED
          || type == VisualModuleEvent.Type.SEARCHED)
        try
        {
          IChunk chunk = vme.getChunk();
          
          if(chunk.hasBeenDisposed()) return null;
          
          IIdentifier id = getIdentifier(chunk);

          TransformedVisualEvent.Type vType = TransformedVisualEvent.Type.FOUND;
          if (type == VisualModuleEvent.Type.ENCODED)
            vType = TransformedVisualEvent.Type.ENCODED;

          return new TransformedVisualEvent(vme.getSource().getModel()
              .getName(), vme.getSource(), vme.getSystemTime(), vme
              .getSimulationTime(), id, vType);
        }
        catch (Exception e)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Could not convert chunk to ast ", e);
        }

      return null;
    }
    else if (actrEvent instanceof FeatureMapEvent)
    {
      FeatureMapEvent vfme = (FeatureMapEvent) actrEvent;

      // should modify the event to handle more than one..
      IIdentifier id = vfme.getIdentifiers().iterator().next();
      TransformedVisualEvent.Type vType = TransformedVisualEvent.Type.ADDED;
      if (vfme.getType() == FeatureMapEvent.Type.UPDATED)
        vType = TransformedVisualEvent.Type.UPDATED;
      else if (vfme.getType() == FeatureMapEvent.Type.REMOVED)
        vType = TransformedVisualEvent.Type.REMOVED;

      IVisualModule module = (IVisualModule) vfme.getSource()
          .getPerceptualMemory().getModule();

      return new TransformedVisualEvent(module.getModel().getName(), module,
          vfme.getSystemTime(), vfme.getSimulationTime(), id, vType,
          new TreeMap<String, Object>());
    }
    return null;
  }

  protected IIdentifier getIdentifier(IChunk chunk)
  {
    IIdentifier id = (IIdentifier) chunk
        .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
    if (id != null) return id;
    // no id, this was a visual-location, try
    // IVisualModule.SEARCH_RESULT_OBJECT_METAKEY
    id = (IIdentifier) chunk
        .getMetaData(IPerceptualMemory.SEARCH_RESULT_IDENTIFIER_KEY);
    return id;
  }
}
