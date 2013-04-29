package org.jactr.modules.pm.aural.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DigitAuralEncoder extends AbstractAuralEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DigitAuralEncoder.class);

  public DigitAuralEncoder()
  {
    super("digit");
  }


}
