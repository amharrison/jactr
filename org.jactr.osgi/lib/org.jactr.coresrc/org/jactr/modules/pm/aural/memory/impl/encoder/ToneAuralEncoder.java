package org.jactr.modules.pm.aural.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ToneAuralEncoder extends AbstractAuralEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ToneAuralEncoder.class);

  public ToneAuralEncoder()
  {
    super("tone");
  }



}
