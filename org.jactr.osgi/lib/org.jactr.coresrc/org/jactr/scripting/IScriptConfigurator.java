package org.jactr.scripting;

import org.jactr.core.model.IModel;

/*
 * default logging
 */

/**
 * Allows configuration of scripting environments before they are used.
 * 
 * @author harrison
 */
public interface IScriptConfigurator
{

  public void configure(IScriptableFactory factory, IModel model,
      ScriptSupport support, Object scope);
}
