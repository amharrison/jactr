package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.EndTrialAction;
import org.w3c.dom.Element;

public class EndTrialHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "end-trial";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new EndTrialAction(experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}