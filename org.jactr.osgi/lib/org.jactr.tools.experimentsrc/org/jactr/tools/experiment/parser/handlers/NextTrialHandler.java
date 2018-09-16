package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.NextTrialAction;
import org.w3c.dom.Element;

public class NextTrialHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "next-trial";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new NextTrialAction(experiment, element.getAttribute("id"));
  }

  public boolean shouldDecend()
  {
    return false;
  }
}