package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import org.commonreality.agents.IAgent;

/**
 * marker interface of filters, maps or encoders that need to be notified of the
 * IAgent. The extender of this will be notified of the agent before
 * abstractPerceptualMemory attaches its own listener for the maps, etc.
 * 
 * @author harrison
 */
public interface INeedsAgent
{

  /**
   * will be called to set and null
   * 
   * @param agent
   */
  public void setAgent(IAgent agent);
}
