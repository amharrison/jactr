package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.commonreality.modalities.visual.ICommonTypes;
import org.jactr.modules.pm.visual.IVisualModule;

public class PhraseEncoder extends BasicTextEncoder
{

  public PhraseEncoder()
  {
    super(IVisualModule.PHRASE_CHUNK_TYPE, ICommonTypes.PHRASE);
  }

}
