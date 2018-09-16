package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.utils.parameter.IParameterized;

public class LinkParameterProbe extends AbstractParameterizedProbe<IAssociativeLink>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LinkParameterProbe.class);

  public LinkParameterProbe(String name, IAssociativeLink parameterized)
  {
    super(name, parameterized);
  }

 
  @Override
  protected IParameterized asParameterized(IAssociativeLink parameterizedObject)
  {
    return parameterizedObject;
  }

  @Override
  public void install(IAssociativeLink parameterized, Executor executor)
  {
    //noop
    
  }

  @Override
  protected AbstractParameterizedProbe<IAssociativeLink> newInstance(
      IAssociativeLink parameterized)
  {
    return new LinkParameterProbe(parameterized.toString(), parameterized);
  }
}
