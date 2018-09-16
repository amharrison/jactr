package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.MarkerAction;
import org.w3c.dom.Element;

public class MarkerCloseHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "marker-close";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new MarkerAction(element.getAttribute("models"), element.getAttribute("type"), element.getAttribute("name"), false, experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}