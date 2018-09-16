package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * A description of a movement to be prepared, initiated and executed
 * @author harrison
 *
 */
public interface IMovement
{
  static enum State {
    INITIALIZED, PREPARING, PREPARED, PROCESSING, EXECUTING, ABORTING, COMPLETED, FAILED
  };

  /**
   * The defining chunk pattern
   * @return
   */
  public ChunkTypeRequest getChunkTypeRequest();
  
  /**
   * the actual muscle that the command will be executed for
   * @return
   */
  public IIdentifier getMuscleIdentifier();
  
  
  public IIdentifier getCommandIdentifier();

  public State getState();
}
