package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.SetAction;
import org.w3c.dom.Element;

public class SetValueHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "set";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new SetAction(element.getAttribute("name"), element
        .getAttribute("value"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}