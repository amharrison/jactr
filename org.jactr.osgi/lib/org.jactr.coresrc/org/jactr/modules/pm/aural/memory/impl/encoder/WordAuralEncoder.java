package org.jactr.modules.pm.aural.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WordAuralEncoder extends AbstractAuralEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(WordAuralEncoder.class);

  public WordAuralEncoder()
  {
    super("word");
  }



}
