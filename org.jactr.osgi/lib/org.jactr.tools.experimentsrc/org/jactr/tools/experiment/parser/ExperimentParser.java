package org.jactr.tools.experiment.parser;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.ICompositeAction;
import org.jactr.tools.experiment.parser.handlers.ActionHandlerHandler;
import org.jactr.tools.experiment.parser.handlers.AliasesHandler;
import org.jactr.tools.experiment.parser.handlers.ClearModelHandler;
import org.jactr.tools.experiment.parser.handlers.DataCollectorInitHandler;
import org.jactr.tools.experiment.parser.handlers.DataLoggerHandler;
import org.jactr.tools.experiment.parser.handlers.EndExperimentHandler;
import org.jactr.tools.experiment.parser.handlers.EndHandler;
import org.jactr.tools.experiment.parser.handlers.EndTrialHandler;
import org.jactr.tools.experiment.parser.handlers.FireNamedHandler;
import org.jactr.tools.experiment.parser.handlers.GroupHandler;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.jactr.tools.experiment.parser.handlers.IfHandler;
import org.jactr.tools.experiment.parser.handlers.LockHandler;
import org.jactr.tools.experiment.parser.handlers.LogHandler;
import org.jactr.tools.experiment.parser.handlers.MarkerCloseHandler;
import org.jactr.tools.experiment.parser.handlers.MarkerOpenHandler;
import org.jactr.tools.experiment.parser.handlers.NextTrialHandler;
import org.jactr.tools.experiment.parser.handlers.ProxyActionHandler;
import org.jactr.tools.experiment.parser.handlers.RecordHandler;
import org.jactr.tools.experiment.parser.handlers.RewardModelHandler;
import org.jactr.tools.experiment.parser.handlers.SetValueHandler;
import org.jactr.tools.experiment.parser.handlers.StartHandler;
import org.jactr.tools.experiment.parser.handlers.StopModelHandler;
import org.jactr.tools.experiment.parser.handlers.TerminateRuntimeHandler;
import org.jactr.tools.experiment.parser.handlers.TimeTriggerHandler;
import org.jactr.tools.experiment.parser.handlers.TrialHandler;
import org.jactr.tools.experiment.parser.handlers.TrialHandlerHandler;
import org.jactr.tools.experiment.parser.handlers.TriggerHandler;
import org.jactr.tools.experiment.parser.handlers.TriggerHandlerHandler;
import org.jactr.tools.experiment.parser.handlers.UnlockHandler;
import org.jactr.tools.experiment.parser.handlers.WaitForACTRHandler;
import org.jactr.tools.experiment.trial.ICompoundTrial;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.triggers.EndTrigger;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.StartTrigger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExperimentParser
{
  /**
   * Logger definition
   */
  static final public transient Log          LOGGER = LogFactory
                                                         .getLog(ExperimentParser.class);

  private Map<String, INodeHandler<ITrial>>   _trialProcessors;

  private Map<String, INodeHandler<IAction>>  _actionProcessors;

  private Map<String, INodeHandler<ITrigger>> _triggerProcessors;

  public int                                 _trialCount;

  private IExperiment                         _experiment;

  public ExperimentParser()
  {
    _trialProcessors = new TreeMap<String, INodeHandler<ITrial>>();
    _triggerProcessors = new TreeMap<String, INodeHandler<ITrigger>>();
    _actionProcessors = new TreeMap<String, INodeHandler<IAction>>();
    initialize();
  }

  protected void initialize()
  {
    
    addActionHandler(new MarkerOpenHandler());
    
    addActionHandler(new MarkerCloseHandler());
    
    addTrialHandler(new TrialHandlerHandler(this));

    addTrialHandler(new ActionHandlerHandler(this));

    addTrialHandler(new TriggerHandlerHandler(this));

    addTrialHandler(new DataLoggerHandler());
    
    addTrialHandler(new DataCollectorInitHandler());
    
    addTrialHandler(new AliasesHandler());

    addTrialHandler(new GroupHandler(this));
    
    addTrialHandler(new TrialHandler(this));

    addTriggerHandler(new TriggerHandler());

    addTriggerHandler(new StartHandler());

    addTriggerHandler(new EndHandler());

    addTriggerHandler(new TimeTriggerHandler());
    
    addActionHandler(new FireNamedHandler());
    
    addActionHandler(new NextTrialHandler());
    
    addActionHandler(new EndTrialHandler());

    addActionHandler(new ClearModelHandler());

    addActionHandler(new RewardModelHandler());

    addActionHandler(new LogHandler());

    addActionHandler(new RecordHandler());

    addActionHandler(new EndExperimentHandler());

    addActionHandler(new TerminateRuntimeHandler());
    
    addActionHandler(new StopModelHandler());

    addActionHandler(new ProxyActionHandler());

    addActionHandler(new WaitForACTRHandler());

    addActionHandler(new LockHandler());

    addActionHandler(new UnlockHandler());

    addActionHandler(new SetValueHandler());

    addActionHandler(new IfHandler());
  }

  public void addTrialHandler(INodeHandler<ITrial> handler)
  {
    _trialProcessors.put(handler.getTagName(), handler);
  }

  public void addActionHandler(INodeHandler<IAction> handler)
  {
    _actionProcessors.put(handler.getTagName(), handler);
  }

  public void addTriggerHandler(INodeHandler<ITrigger> handler)
  {
    _triggerProcessors.put(handler.getTagName(), handler);
  }

  protected INodeHandler<ITrigger> getTriggerHandler(String tagName)
  {
    return _triggerProcessors.get(tagName);
  }

  protected INodeHandler<ITrial> getTrialHandler(String tagName)
  {
    return _trialProcessors.get(tagName);
  }

  protected INodeHandler<IAction> getActionHandler(String tagName)
  {
    return _actionProcessors.get(tagName);
  }

  public void configure(Document document, IExperiment experiment)
  {
    _experiment = experiment;

    int iterations = 1;
    String itrStr = null;
    try
    {
      itrStr = _experiment.getVariableResolver().resolve(
          document.getDocumentElement().getAttribute("iterations"),
          experiment.getVariableContext()).toString();
      iterations = Integer.parseInt(itrStr);
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to process " + itrStr + " into integer, using 1", e);
      iterations = 1;
    }

    NodeList children = document.getDocumentElement().getChildNodes();
    boolean first = true;
    while (iterations > 0)
    {
      for (int i = 0; i < children.getLength(); i++)
      {
        Node node = children.item(i);
        if (node instanceof Element)
        {
          Object rtn = processNode((Element) node);
          if (rtn instanceof ITrial)
            _experiment.addTrial((ITrial) rtn);
          else if (first && (rtn instanceof ITrigger))
          {
            /*
             * only add the triggers once..
             */
            if (rtn instanceof StartTrigger)
              _experiment.setStartTrigger((StartTrigger) rtn);
            else if (rtn instanceof EndTrigger)
              _experiment.setEndTrigger((EndTrigger) rtn);
            else
              _experiment.addTrigger((ITrigger) rtn);
          }
        }
      }
      iterations--;
      first = false;
    }
  }

  protected Object processNode(Element element)
  {
    INodeHandler<ITrial> tHandler = getTrialHandler(element.getTagName());
    if (tHandler != null)
    {
      ITrial trial = tHandler.process(element, _experiment);
      if (tHandler.shouldDecend())
        return processTrial(trial, element);
      else
        return trial;
    }

    INodeHandler<ITrigger> trHandler = getTriggerHandler(element.getTagName());
    if (trHandler != null)
    {
      ITrigger trigger = trHandler.process(element, _experiment);
      if (trHandler.shouldDecend())
        return processTrigger(trigger, element);
      else
        return trigger;
    }

    INodeHandler<IAction> aHandler = getActionHandler(element.getTagName());
    if (aHandler != null)
    {
      IAction action = aHandler.process(element, _experiment);
      if (aHandler.shouldDecend())
        return processAction(action, element);
      else
        return action;
    }

    if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to process " + element.getTagName());

    Collection container = new ArrayList();
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      Element nodeElement = (Element) node;
      Object thing = processNode(nodeElement);
      if (thing != null) container.add(thing);
    }

    return container;
  }

  /*
   * should probably move this into the individual processors..
   */
  protected ITrial processTrial(ITrial trial, Element element)
  {
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      Element nodeElement = (Element) node;

      Object rtn = processNode(nodeElement);
      if (rtn instanceof ITrigger)
      {
        if (rtn instanceof StartTrigger)
          trial.setStartTrigger((ITrigger) rtn);
        else if (rtn instanceof EndTrigger)
          trial.setEndTrigger((ITrigger) rtn);
        else
          trial.addTrigger((ITrigger) rtn);
      }
      else if (rtn instanceof ITrial && trial instanceof ICompoundTrial)
      {
        ((ICompoundTrial) trial).add((ITrial) rtn);
      }
      else if (rtn != null)
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("No clue what to do with %s from %s", rtn, nodeElement.getTagName()));
    }

    return trial;
  }

  protected ITrigger processTrigger(ITrigger trigger, Element element)
  {
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      Element nodeElement = (Element) node;

      Object rtn = processNode(nodeElement);
      if (rtn instanceof IAction)
        trigger.add((IAction) rtn);
      else if (rtn instanceof ITrigger)
        trigger.add((ITrigger) rtn);
      else if (rtn != null)
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("No clue what to do with %s from %s", rtn, nodeElement.getTagName()));
    }

    return trigger;
  }

  protected IAction processAction(IAction action, Element element)
  {
    if (action instanceof ICompositeAction)
    {
      ICompositeAction cAction = (ICompositeAction) action;
      NodeList list = element.getChildNodes();
      for (int i = 0; i < list.getLength(); i++)
      {
        Node node = list.item(i);
        if (!(node instanceof Element)) continue;
        Element nodeElement = (Element) node;

        Object rtn = processNode(nodeElement);
        if (rtn instanceof IAction)
          cAction.add((IAction) rtn);
        else if (rtn != null)
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(String.format("No clue what to do with %s from %s", rtn, nodeElement.getTagName()));
      }
    }
    return action;
  }

}
