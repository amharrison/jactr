package org.jactr.extensions.cached.procedural;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.extensions.cached.procedural.internal.CachedProductionInstantiator;
import org.jactr.extensions.cached.procedural.internal.InstantiationCache;

/**
 * drop in that tracks the procedural module, caching those productions that
 * cannot instantiate and recycling that exception until the state changes
 * enough to justify retrying the production instantiation
 * 
 * @author harrison
 */
public class CachedProductionSystem implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER           = LogFactory
                                                            .getLog(CachedProductionSystem.class);

  static public final String           ENABLE_PARAM     = "EnableCaching";

  static public final String           VALIDATE_PARAM   = "ValidateInstantiations";

  private IModel                       _model;

  private CachedProductionInstantiator _instantiator;

  private InstantiationCache           _cache;

  private boolean                      _enableCaching   = false;

  private boolean                      _validateCaching = false;

  public void setParameter(String key, String value)
  {
    if (ENABLE_PARAM.equalsIgnoreCase(key))
      _enableCaching = ParameterHandler.booleanInstance().coerce(value)
          .booleanValue();
    else if (VALIDATE_PARAM.equalsIgnoreCase(key))
      _validateCaching = ParameterHandler.booleanInstance().coerce(value)
          .booleanValue();
  }

  public String getParameter(String key)
  {
    if (ENABLE_PARAM.equalsIgnoreCase(key))
      return Boolean.toString(_enableCaching);
    else if (VALIDATE_PARAM.equalsIgnoreCase(key))
      return Boolean.toString(_validateCaching);
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(ENABLE_PARAM, VALIDATE_PARAM);
  }

  public void initialize() throws Exception
  {
    /*
     * create the cache and install the instantiator
     */
    if (_enableCaching)
    {
      _cache = new InstantiationCache(_model);
      _instantiator = new CachedProductionInstantiator(_cache, _validateCaching);

      _model.getProceduralModule().setProductionInstantiator(_instantiator);
    }
  }

  public void install(IModel model)
  {
    _model = model;
  }

  public void uninstall(IModel model)
  {
    _model = null;

    if (_enableCaching)
    {
      _cache.dispose();

      if (LOGGER.isDebugEnabled())
      {
        long attempts = _instantiator.getInstantiationAttempts();
        long requests = _instantiator.getInstantiationRequests();
        double improvement = (float) (requests - attempts) / requests * 100;
        LOGGER.debug(String.format("%d requests %d actual %.2f%% savings",
            requests, attempts, improvement));
      }
      
    }
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return "cached-productions";
  }

}
