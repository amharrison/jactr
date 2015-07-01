package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class RecordAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RecordAction.class);
  
  static public enum Type {OPEN, CLOSE, SIMPLE};
  
  private final Type _type; 
  private final String _tagName;
  private final Map<String, String> _attributes;
  private final IExperiment _experiment;
  
  public RecordAction(Element element, IExperiment experiment)
  {
    _experiment = experiment;
    Type type = Type.OPEN;
    String tagName = null;
    if(element.hasAttribute("open"))
    {
      type = Type.OPEN;
      tagName = element.getAttribute("open");
    }
    else
      if(element.hasAttribute("close"))
      {
        type = Type.CLOSE;
        tagName = element.getAttribute("close");
      }
      else
      {
        type = Type.SIMPLE;
        tagName = element.getAttribute("simple");
      }
    
    _type = type;
    _tagName = tagName;
    _attributes = new TreeMap<String, String>();
    NamedNodeMap nnm = element.getAttributes();
    for(int i=0;i<nnm.getLength();i++)
    {
      Node node = nnm.item(i);
      String name = node.getNodeName();
      String value = node.getNodeValue();
      if(!_tagName.equals(value))
        _attributes.put(name, node.getNodeValue());
    }
  }
  
  public RecordAction(Type type, String tagName, Map<String, String> attributes, IExperiment experiment)
  {
    _experiment =experiment;
    _type  = type;
    _tagName = tagName;
    _attributes = new TreeMap<String,String>(attributes);
  }

  public void fire(IVariableContext context)
  {
    IDataLogger collector = _experiment.getDataCollector();
    switch(_type)
    {
      case OPEN : collector.open(_tagName, _attributes, context); break;
      case CLOSE : collector.close(_tagName); break;
      case SIMPLE : collector.simple(_tagName, _attributes, context); break;
    }

  }

}
