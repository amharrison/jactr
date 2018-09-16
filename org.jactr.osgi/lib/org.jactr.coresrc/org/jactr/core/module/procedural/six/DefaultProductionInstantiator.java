package org.jactr.core.module.procedural.six;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.IProductionInstantiator;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;

public class DefaultProductionInstantiator implements IProductionInstantiator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultProductionInstantiator.class);

  private IProceduralModule          _module;

  public Collection<IInstantiation> instantiate(IProduction production,
      Collection<VariableBindings> provisionalBindings)
      throws CannotInstantiateException
  {
    try
    {
      return production.instantiateAll(provisionalBindings);
    }
    catch (CannotInstantiateException cie)
    {
      throw cie;
    }
    catch (Exception e)
    {
      LOGGER.error(
          "Could not instantiate " + production + " : " + e.getMessage(), e);

      throw new CannotInstantiateException("Could not instantiate "
          + production + " : " + e.getMessage(), e);
    }
  }

  public void setProceduralModule(IProceduralModule module)
  {
    _module = module;
  }

  public IProceduralModule getProceduralModule()
  {
    return _module;
  }

}
