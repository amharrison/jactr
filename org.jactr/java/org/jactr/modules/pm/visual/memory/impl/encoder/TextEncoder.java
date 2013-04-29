package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.commonreality.modalities.visual.ICommonTypes;
import org.jactr.modules.pm.visual.IVisualModule;

public class TextEncoder extends BasicTextEncoder
{

  public TextEncoder()
  {
    super(IVisualModule.TEXT_CHUNK_TYPE, ICommonTypes.TEXT);
  }

}
