package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.SimpleConditionalAction;
import org.w3c.dom.Element;

public class IfHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "if";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new SimpleConditionalAction(element, experiment);
  }

  public boolean shouldDecend()
  {
    return true;
  }
}