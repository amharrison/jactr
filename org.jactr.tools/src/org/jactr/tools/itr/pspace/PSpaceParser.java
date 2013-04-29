package org.jactr.tools.itr.pspace;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.itr.IParameterModifier;
import org.jactr.tools.itr.analysis.IAnalyzer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PSpaceParser
{

  static public Document load(URI config) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.parse(config.toASCIIString());
    return document;
  }
  
  
  static public Collection<IAnalyzer> buildAnalyzers(Document document) throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Collection<IAnalyzer>)instantiate(document,"analyzer");
  }
  
  static public Collection<IParameterModifier> buildModifiers(Document document) throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Collection<IParameterModifier>)instantiate(document,"modifier");
  }
  
  
  
  static protected Collection instantiate(Document document, String tagName) throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    ArrayList rtn = new ArrayList();
    NodeList list = document.getDocumentElement().getElementsByTagName(tagName);
    for(int i=0;i<list.getLength();i++)
    {
      Element child = (Element) list.item(i);
      String className = child.getAttribute("class");
      
      Object instance = PSpaceParser.class.getClassLoader().loadClass(className).newInstance();
      
      if(instance instanceof IParameterized)
        setParameters((IParameterized)instance, child);
      
      rtn.add(instance);
    }
    
    return rtn;
  }
  
  static protected void setParameters(IParameterized parameterized, Element pNode)
  {
    NodeList list = pNode.getElementsByTagName("parameter");
    for(int i=0;i<list.getLength();i++)
    {
      Element vNode = (Element) list.item(i);
      parameterized.setParameter(vNode.getAttribute("name"), vNode.getAttribute("value"));
    }
  }
}
