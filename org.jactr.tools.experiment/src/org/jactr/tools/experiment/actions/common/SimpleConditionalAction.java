package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.ICompositeAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.w3c.dom.Element;

public class SimpleConditionalAction implements ICompositeAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER  = LogFactory
                                                 .getLog(SimpleConditionalAction.class);
  static private enum ComparisonType {EQUAL, NOT, CONTAIN, NOT_CONTAIN};

  private Collection<IAction>        _actions;

  // can be resolved
  private String                     _comparisonValue;

  private String                     _variableName;

  private IExperiment                _experiment;

  private ComparisonType _comparison = ComparisonType.EQUAL;

  public SimpleConditionalAction(Element element, IExperiment experiment)
  {
    _experiment = experiment;
    if (element.hasAttribute("equals"))
      _comparisonValue = element.getAttribute("equals");
    else if(element.hasAttribute("not"))
    {
      _comparison = ComparisonType.NOT;
      _comparisonValue = element.getAttribute("not");
    }
    else if(element.hasAttribute("contains"))
    {
      _comparison = ComparisonType.CONTAIN;
      _comparisonValue = element.getAttribute("contains");
    }
    else if(element.hasAttribute("not-contains"))
    {
      _comparison = ComparisonType.NOT_CONTAIN;
      _comparisonValue = element.getAttribute("not-contains");
    }
    _variableName = element.getAttribute("variable");
    _actions = new ArrayList<IAction>();
  }

  public void add(IAction action)
  {
    _actions.add(action);
  }

  public void fire(IVariableContext context)
  {
    String name = _experiment.getVariableResolver().resolve(_variableName,
        context).toString().toLowerCase();
    String value = _experiment.getVariableResolver().resolve(_comparisonValue,
        context).toString().toLowerCase();

    boolean shouldFire = (_comparison==ComparisonType.EQUAL && name.equals(value))
        || (_comparison==ComparisonType.NOT && !name.equals(value))
        || (_comparison==ComparisonType.CONTAIN && name.indexOf(value)!=-1)
        || (_comparison==ComparisonType.NOT_CONTAIN && name.indexOf(value)==-1);

    if (shouldFire) for (IAction action : _actions)
      action.fire(context);
  }

}
