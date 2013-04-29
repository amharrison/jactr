package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * delegate version of {@link ICommandTranslator}
 * @author harrison
 *
 */
public interface ICommandTranslatorDelegate extends ICommandTranslator
{

  /**
   * returns true if this delegate can handle a command as specified by
   * this chunk pattern
   * @param request
   * @return
   */
  public boolean handles(ChunkTypeRequest request);
}
