package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.LockAction;
import org.w3c.dom.Element;

public class LockHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "lock";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new LockAction(element.getAttribute("name"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}