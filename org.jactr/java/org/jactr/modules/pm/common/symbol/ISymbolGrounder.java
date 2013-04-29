package org.jactr.modules.pm.common.symbol;

import org.commonreality.object.IAfferentObject;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.modules.pm.IPerceptualModule;

/*
 * default logging
 */

/**
 * generic mechanism to provide symbol grounding based on perceptual
 * information. This links to the {@link IDeclarativeModule} to allow the
 * creation or accessing of concept chunks that can be used in lieu of the
 * strings often used in the 'value' slot of percepts.
 * 
 * @author harrison
 */
public interface ISymbolGrounder
{

  /**
   * returns a consistent mapping to a percept value. The returned value may be
   * a chunk, a string, or what-have-you. For spreading of activation purposes,
   * a chunk is ideal.
   * 
   * @param percept
   * @param perceivingModule
   * @param declarativeModule
   * @return
   */
  public Object getSymbolForPercept(IAfferentObject percept,
      IPerceptualModule perceivingModule, IDeclarativeModule declarativeModule);
  
  // public Object getSymbolForString(String string, IDeclarativeModule
  // declarativeModule);
}
