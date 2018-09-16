package org.jactr.tools.marker.impl;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IProduction;

public class ProductionMarker extends DefaultMarker
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionMarker.class);

  static public final String         TYPE            = ProductionMarker.class
                                                         .getName();

  static public final String         PRODUCTION_NAME = "productionName";

  public ProductionMarker(String name, IProduction production)
  {
    this(name, TYPE, production);
  }

  public ProductionMarker(String name, String type, IProduction production)
  {
    super(production.getModel(), name, type);
    setProperty(PRODUCTION_NAME, production.getSymbolicProduction().getName());
  }

  public String getProductionName()
  {
    return getProperty(PRODUCTION_NAME);
  }
}
