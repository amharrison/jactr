package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.ProxyAction;
import org.w3c.dom.Element;

public class ProxyActionHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "action";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new ProxyAction(element.getAttribute("class"), element
        .getAttribute("models"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}