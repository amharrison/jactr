package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.FireAction;
import org.w3c.dom.Element;

public class FireNamedHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "fire-named";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new FireAction(experiment, element.getAttribute("trigger"));
  }

  public boolean shouldDecend()
  {
    return false;
  }
}