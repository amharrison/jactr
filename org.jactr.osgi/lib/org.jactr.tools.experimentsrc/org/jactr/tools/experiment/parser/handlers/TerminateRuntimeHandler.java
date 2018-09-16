package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.TerminateAction;
import org.w3c.dom.Element;

public class TerminateRuntimeHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "terminate";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new TerminateAction();
  }

  public boolean shouldDecend()
  {
    return false;
  }
}