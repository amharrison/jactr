package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.w3c.dom.Element;

public interface INodeHandler<T>
{

  public String getTagName();
  
  public T process(Element element, IExperiment experiment);
  
  public boolean shouldDecend();
}
