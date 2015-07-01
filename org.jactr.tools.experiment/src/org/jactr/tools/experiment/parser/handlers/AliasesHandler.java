package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AliasesHandler implements INodeHandler<ITrial>
{
  public String getTagName()
  {
    return "aliases";
  }

  public ITrial process(Element element, IExperiment experiment)
  {
    VariableResolver resolver = experiment.getVariableResolver();
    NodeList children = element.getElementsByTagName("alias");
    for (int i = 0; i < children.getLength(); i++)
    {
      Element alias = (Element) children.item(i);
      String value = alias.getAttribute("value");

      resolver.addAlias(alias.getAttribute("name"), resolver.resolve(value,
          experiment.getVariableContext()).toString());
    }

    return null;
  }

  public boolean shouldDecend()
  {
    return false;
  }
}