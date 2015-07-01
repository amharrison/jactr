package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.triggers.EndTrigger;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.w3c.dom.Element;

public class EndHandler implements INodeHandler<ITrigger>
{
  public String getTagName()
  {
    return "end";
  }

  public ITrigger process(Element element, IExperiment experiment)
  {
    return new EndTrigger(experiment);
  }

  public boolean shouldDecend()
  {
    return true;
  }
}