package org.jactr.extensions.cached.procedural.internal;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.six.DefaultProductionInstantiator;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;

public class CachedProductionInstantiator extends DefaultProductionInstantiator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(CachedProductionInstantiator.class);

  private final InstantiationCache   _cache;

  private boolean                    _validate = false;
  
  private long _instantiationRequests;
  private long _actualInstantiationAttempts;

  public CachedProductionInstantiator(InstantiationCache cache, boolean validate)
  {
    _cache = cache;
    _validate = validate;
  }

  public Collection<IInstantiation> instantiate(IProduction production,
      Collection<VariableBindings> provisionalBindings)
      throws CannotInstantiateException
  {
    
    _instantiationRequests++;
    
    if (!_validate) _cache.throwIfCached(production);

    try
    {
      _actualInstantiationAttempts++;
      
      Collection<IInstantiation> rtn = super.instantiate(production,
          provisionalBindings);

      if (_validate && _cache.contains(production))
      {
        LOGGER
            .error(
                String
                    .format(
                        "Production cache believes that %s should fail with %s, but it has yielded instantiations %s",
                        production, _cache.get(production), rtn), _cache
                    .get(production));
        
        _cache.remove(production);
      }

      return rtn;
    }
    catch (CannotInstantiateException cie)
    {
      _cache.add(production, cie);
      throw cie;
    }
  }
  
  public long getInstantiationRequests()
  {
    return _instantiationRequests;
  }
  
  public long getInstantiationAttempts()
  {
    return _actualInstantiationAttempts;
  }
}
