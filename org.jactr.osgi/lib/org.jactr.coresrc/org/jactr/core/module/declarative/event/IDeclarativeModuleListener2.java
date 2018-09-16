package org.jactr.core.module.declarative.event;

/*
 * default logging
 */

/**
 * extension interface for tracking the removal of chunks and types
 * 
 * @author harrison
 */
public interface IDeclarativeModuleListener2 extends IDeclarativeModuleListener
{

  public void chunkTypeRemoved(DeclarativeModuleEvent dme);

  public void chunkRemoved(DeclarativeModuleEvent dme);
}
