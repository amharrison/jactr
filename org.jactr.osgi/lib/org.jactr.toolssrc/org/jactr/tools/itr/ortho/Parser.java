package org.jactr.tools.itr.ortho;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.itr.IParameterModifier;
import org.jactr.tools.itr.LongitudinalParameterSetModifier;
import org.jactr.tools.itr.ParameterSetModifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser
{

  static public Document load(URI config) throws ParserConfigurationException,
      SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.parse(config.toASCIIString());
    return document;
  }

  static public Collection<ISliceIntegrator> buildIntegrators(Document document)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    return instantiate(document.getDocumentElement(), "integrator");
  }

  static public Collection<ISliceAnalyzer> buildAnalyzers(Document document)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    return instantiate(document.getDocumentElement(), "analyzer");
  }

  static public Collection<ISliceListener> buildListeners(Document document)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    return instantiate(document.getDocumentElement(), "listener");
  }

  static public Collection<IParameterModifier> buildModifiers(Document document)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    /*
     * first we grab all the modifiers in population
     */
    ArrayList<IParameterModifier> populationModifiers = new ArrayList<IParameterModifier>();
    NodeList nl = document.getDocumentElement().getElementsByTagName(
        "population");
    for (int i = 0; i < nl.getLength(); i++)
      // populationModifiers.addAll(instantiate((Element) nl.item(i),
      // "modifier"));
      populationModifiers.addAll(parseModifiers((Element) nl.item(i)));

    nl = document.getDocumentElement().getElementsByTagName("longitudinal");
    for (int i = 0; i < nl.getLength(); i++)
    {
      Element longitudinal = (Element) nl.item(i);
      LongitudinalParameterSetModifier lModifier = new LongitudinalParameterSetModifier();
      lModifier.setParameter(IParameterModifier.PARAMETER_NAME, longitudinal
          .getAttribute("name"));
      lModifier.setParameter(IParameterModifier.PARAMETER_VALUES, longitudinal
          .getAttribute("values"));
      NamedNodeMap attrs = longitudinal.getAttributes();
      for (int k = 0; k < attrs.getLength(); k++)
      {
        Node attrNode = attrs.item(k);
        String name = attrNode.getNodeName();
        if (!name.equalsIgnoreCase("name") && !name.equalsIgnoreCase("values"))
          lModifier.associate(name, attrNode.getNodeValue());
      }

      NodeList lChildren = longitudinal.getChildNodes();
      for (int j = 0; j < lChildren.getLength(); j++)
        if (lChildren.item(j) instanceof Element)
          for (IParameterModifier childModifier : parseModifiers((Element) lChildren
              .item(j)))
            lModifier.add(childModifier);

      populationModifiers.add(lModifier);
    }

    return populationModifiers;
  }

  static protected Collection<IParameterModifier> parseModifiers(Element root)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    Collection<IParameterModifier> rtn = new ArrayList<IParameterModifier>();

    String tagName = root.getTagName();
    if (tagName.equalsIgnoreCase("modifier"))
    {
      IParameterModifier rootMod = null;
      String className = root.getAttribute("class");

      rootMod = (IParameterModifier) Parser.class.getClassLoader().loadClass(
          className).newInstance();

      if (rootMod instanceof IParameterized)
        setParameters(rootMod, root);

      rtn.add(rootMod);

      if (rootMod instanceof ParameterSetModifier)
      {
        // descend into the children
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
          if (children.item(i) instanceof Element)
            for (IParameterModifier childModifier : parseModifiers((Element) children
                .item(i)))
              ((ParameterSetModifier) rootMod).add(childModifier);
      }
    }
    else
    {
      NodeList children = root.getChildNodes();
      for (int i = 0; i < children.getLength(); i++)
        if (children.item(i) instanceof Element)
          rtn.addAll(parseModifiers((Element) children.item(i)));
    }

    return rtn;
  }

  static protected Collection instantiate(Element root, String tagName)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException
  {
    ArrayList rtn = new ArrayList();
    NodeList list = root.getElementsByTagName(tagName);
    for (int i = 0; i < list.getLength(); i++)
    {
      Element child = (Element) list.item(i);
      String className = child.getAttribute("class");

      Object instance = Parser.class.getClassLoader().loadClass(className)
          .newInstance();

      if (instance instanceof IParameterized)
        setParameters((IParameterized) instance, child);

      rtn.add(instance);
    }

    return rtn;
  }

  static protected void setParameters(IParameterized parameterized,
      Element pNode)
  {
    // NodeList list = pNode.getElementsByTagName("parameter");
    NodeList list = pNode.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
      if (list.item(i) instanceof Element)
      {
        Element vNode = (Element) list.item(i);
        if (vNode.getTagName().equalsIgnoreCase("parameter"))
          parameterized.setParameter(vNode.getAttribute("name"), vNode
              .getAttribute("value"));
      }
  }
}
