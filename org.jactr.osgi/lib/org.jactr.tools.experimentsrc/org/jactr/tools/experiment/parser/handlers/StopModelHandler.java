package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.StopModelAction;
import org.w3c.dom.Element;

public class StopModelHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "stop-model";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new StopModelAction(experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}