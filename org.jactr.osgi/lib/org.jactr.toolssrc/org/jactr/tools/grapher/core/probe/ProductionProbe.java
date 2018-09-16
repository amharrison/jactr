package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.event.IProductionListener;
import org.jactr.core.production.event.ProductionListenerAdaptor;
import org.jactr.core.production.six.ISubsymbolicProduction6;
import org.jactr.core.utils.parameter.IParameterized;

public class ProductionProbe extends AbstractParameterizedProbe<IProduction>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionProbe.class);

  private IProductionListener        _listener;

  public ProductionProbe(String name, IProduction production)
  {
    super(name, production);

  }

  @Override
  public void install(IProduction parameterized, Executor executor)
  {
    if (isPolling()) return;

    _listener = new ProductionListenerAdaptor() {
      @Override
      public void parameterChanged(IParameterEvent pe)
      {
        set(pe.getParameterName(), pe.getNewParameterValue());
      }
    };

    parameterized.addListener(_listener, executor);
  }

  @Override
  protected AbstractParameterizedProbe<IProduction> newInstance(
      IProduction parameterized)
  {
    return new ProductionProbe(parameterized.getSymbolicProduction().getName(),
        parameterized);
  }

  @Override
  protected IParameterized asParameterized(IProduction parameterizedObject)
  {
    return parameterizedObject.getSubsymbolicProduction();
  }

  /**
   * special handling for ExpectedUtility since it may be nan until learned. if
   * nan, use utility instead
   */
  @Override
  protected void set(String parameter, Object value)
  {
    if (ISubsymbolicProduction6.EXPECTED_UTILITY_PARAM
        .equalsIgnoreCase(parameter))
      if (value instanceof Number
          && Double.isNaN(((Number) value).doubleValue())
          || "NaN".equals(value))
      {
        super.set(parameter, asParameterized(_parameterized).getParameter(
            ISubsymbolicProduction6.UTILITY_PARAM));
        return;
      }

    super.set(parameter, value);
  }

}
