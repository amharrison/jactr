package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import org.jactr.tools.analysis.production.endstates.BufferEndStates;

public interface IRelationshipComputer
{

  public IRelationship computeRelationship(BufferEndStates headEndStates, BufferEndStates tailEndStates);
}
