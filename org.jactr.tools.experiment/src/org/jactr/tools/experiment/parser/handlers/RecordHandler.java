package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.RecordAction;
import org.w3c.dom.Element;

public class RecordHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "record";
  }

  public IAction process(Element element, IExperiment experiment)
  {
    return new RecordAction(element, experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}