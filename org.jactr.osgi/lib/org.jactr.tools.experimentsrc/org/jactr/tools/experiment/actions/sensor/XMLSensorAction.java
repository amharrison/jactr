package org.jactr.tools.experiment.actions.sensor;

/*
 * default logging
 */
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.ISensor;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLSensorAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER  = LogFactory
                                                 .getLog(XMLSensorAction.class);


  private String                     _location;

  private IExperiment                _experiment;

  private boolean                    _immediateExecution;
  
  private String _forWhom ="";

  public XMLSensorAction(String location, boolean executeImmediately, IExperiment experiment)
  {
    this(location, executeImmediately, experiment, "");
  }
  
  public XMLSensorAction(String location, boolean executeImmediately,
      IExperiment experiment, String forWhom)
  {
    _location = location;
    _experiment = experiment;
    _immediateExecution = executeImmediately;
    _forWhom = forWhom;
  }

  protected Element transform(Element timeNode, IVariableContext context)
  {
    VariableResolver resolver = _experiment.getVariableResolver();
    NamedNodeMap attrs = timeNode.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++)
    {
      Node attr = attrs.item(i);
      String nodeValue = attr.getNodeValue();

      if (resolver.isVariable(nodeValue))
      {
        nodeValue = resolver.resolve(nodeValue, context).toString();
        attr.setNodeValue(nodeValue);
      }
    }

    /*
     * descend
     */
    NodeList children = timeNode.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      if (children.item(i) instanceof Element)
        transform((Element) children.item(i), context);

    return timeNode;
  }

  public void fire(IVariableContext context)
  {
    context.set("modelName", _forWhom);
    
    String location = _experiment.getVariableResolver()
        .resolve(_location, context).toString();
    URL loc = getClass().getClassLoader().getResource(location);
    if (loc == null)
      throw new IllegalArgumentException("Could not find resource url for "
          + _location + " [" + location + "]");

    org.commonreality.sensors.xml2.XMLSensor newSensor = getSensor2();

    if (newSensor == null)
      throw new IllegalStateException("Could not find XMLSensor");

    try
    {
      if (!_immediateExecution)
      {
        // sensor.configure(Collections.singletonMap(XMLSensor.DATA_URL,
        // loc.toString()));
        if (newSensor != null) newSensor.load(loc);
      }
      else
      {
        List<Element> elements = FastListFactory.newInstance();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.parse(loc.openStream());

        Element root = doc.getDocumentElement();
        NodeList nl = root.getElementsByTagName("time");
        for (int i = 0; i < nl.getLength(); i++)
        {
          Node node = nl.item(i);
          if (node instanceof Element) if (newSensor != null)
            elements.add(transform((Element) node, context));
        }

        if (newSensor != null) newSensor.flush(elements);

        FastListFactory.recycle(elements);
      }
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Unable to queue up sensor data ", e);
    }
  }



  private org.commonreality.sensors.xml2.XMLSensor getSensor2()
  {
    for (ISensor sensor : CommonReality.getSensors())
      if (sensor instanceof org.commonreality.sensors.xml2.XMLSensor)
        return (org.commonreality.sensors.xml2.XMLSensor) sensor;
    return null;
  }

}
