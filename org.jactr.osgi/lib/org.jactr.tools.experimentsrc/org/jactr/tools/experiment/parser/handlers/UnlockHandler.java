package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.UnlockAction;
import org.w3c.dom.Element;

public class UnlockHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "unlock";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new UnlockAction(element.getAttribute("name"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}