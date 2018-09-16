package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.RewardAction;
import org.w3c.dom.Element;

public class RewardModelHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "reward";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new RewardAction(element.getAttribute("model"), element
        .getAttribute("value"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}