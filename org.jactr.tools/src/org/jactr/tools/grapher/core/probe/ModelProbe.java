package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;

public class ModelProbe extends AbstractParameterizedProbe<IModel>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelProbe.class);
  
  private IParameterListener         _listener;

  public ModelProbe(String name, IModel parameterized)
  {
    super(name, parameterized);
  }
  
  @Override
  public void install(IModel parameterized, Executor executor)
  {
    _listener = new IParameterListener() {

      public void parameterChanged(IParameterEvent pe)
      {
        set(pe.getParameterName(), pe.getNewParameterValue());
      }
    };

    parameterized.addListener(_listener, executor);
  }

  @Override
  protected AbstractParameterizedProbe<IModel> newInstance(IModel parameterized)
  {
    return new ModelProbe(parameterized.getName(),
        parameterized);
  }

  @Override
  protected IParameterized asParameterized(IModel parameterizedObject)
  {
    return parameterizedObject;
  }
}
