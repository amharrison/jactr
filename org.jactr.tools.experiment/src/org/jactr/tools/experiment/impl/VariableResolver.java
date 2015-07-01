package org.jactr.tools.experiment.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;

public class VariableResolver
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VariableResolver.class);

  private Collection<IResolver>      _resolvers;

  private Map<String, String>        _aliases;

  static private String              PREFIX = "${";

  static private String              SUFFIX = "}";

  /**
   * will return all models with matching names in the current runtime
   * 
   * @param modelNames
   * @param resolver
   * @param context
   * @return
   */
  static public Collection<IModel> getModels(String modelNames,
      VariableResolver resolver, IVariableContext context)
  {
    modelNames = resolver.resolve(modelNames, context).toString();
    Collection<IModel> allModels = ACTRRuntime.getRuntime().getModels();
    Collection<IModel> models = new ArrayList<IModel>();
    for (String modelName : modelNames.split(","))
    {
      modelName = modelName.trim();
      for (IModel model : allModels)
        if (modelName.equalsIgnoreCase(model.getName())) models.add(model);
    }
    return models;
  }

  public VariableResolver()
  {
    _resolvers = new ArrayList<IResolver>();
    _aliases = new TreeMap<String, String>();
    initialize();
  }

  public void addAlias(String alias, String value)
  {
    _aliases.put(alias, value);
  }

  protected void initialize()
  {

    /**
     * ${SystemProperty}
     */
    add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return System.getProperty(key) != null;
      }

      public Object resolve(String key, IVariableContext context)
      {
        return System.getProperty(key);
      }

    });

    /**
     * ${context}
     */
    add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return true;
      }

      public Object resolve(String key, IVariableContext context)
      {
        Object value = context.get(key);
        return value;
      }

    });

    /**
     * alias resolver
     */
    add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return _aliases.containsKey(key);
      }

      public Object resolve(String key, IVariableContext context)
      {
        return _aliases.get(key);
      }

    });

  }

  public void add(IResolver resolver)
  {
    _resolvers.add(resolver);
  }

  public String getPrefix()
  {
    return PREFIX;
  }

  public String getSuffix()
  {
    return SUFFIX;
  }

  public boolean isVariable(String key)
  {
    return key.startsWith(PREFIX) && key.endsWith(SUFFIX);
  }

  public boolean isResolvable(String key, IVariableContext context)
  {
    if (!isVariable(key)) return false;

    key = key.substring(key.indexOf(PREFIX) + 2, key.lastIndexOf(SUFFIX));

    Object rtn = null;
    for (IResolver resolver : _resolvers)
      if (resolver.isRelevant(key))
      {
        rtn = resolver.resolve(key, context);
        if (rtn != null) break;
      }

    return rtn != null;
  }

  public Object resolve(String key, IVariableContext context)
  {
    if (!isVariable(key)) return key;

    String oKey = key;
    key = key.substring(key.indexOf(PREFIX) + 2, key.lastIndexOf(SUFFIX));

    Object rtn = null;
    for (IResolver resolver : _resolvers)
      if (resolver.isRelevant(key))
      {
        rtn = resolver.resolve(key, context);
        if (rtn != null) break;
      }

    if (rtn == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("Could not resolve %s, returing %s", oKey,
            key));
      rtn = key;
    }
    return rtn;
  }

  public interface IResolver
  {
    public boolean isRelevant(String key);

    public Object resolve(String key, IVariableContext context);
  }
}
