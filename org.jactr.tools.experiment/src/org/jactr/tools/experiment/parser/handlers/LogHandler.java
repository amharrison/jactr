package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.w3c.dom.Element;

public class LogHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "log";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new LogAction(element.getAttribute("message"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}