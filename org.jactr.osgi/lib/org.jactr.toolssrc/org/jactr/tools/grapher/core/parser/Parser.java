package org.jactr.tools.grapher.core.parser;

/*
 * default logging
 */
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.grapher.core.probe.ChunkProbe;
import org.jactr.tools.grapher.core.probe.LinkParameterProbe;
import org.jactr.tools.grapher.core.probe.ModelProbe;
import org.jactr.tools.grapher.core.probe.ParameterizedProbe;
import org.jactr.tools.grapher.core.probe.ProductionProbe;
import org.jactr.tools.grapher.core.selector.BufferSelector;
import org.jactr.tools.grapher.core.selector.ChunkSelector;
import org.jactr.tools.grapher.core.selector.ChunkTypeSelector;
import org.jactr.tools.grapher.core.selector.ClassNamedParameterSelector;
import org.jactr.tools.grapher.core.selector.ExtensionSelector;
import org.jactr.tools.grapher.core.selector.InstrumentSelector;
import org.jactr.tools.grapher.core.selector.LinkSelector;
import org.jactr.tools.grapher.core.selector.ModelSelector;
import org.jactr.tools.grapher.core.selector.ModuleSelector;
import org.jactr.tools.grapher.core.selector.ProductionSelector;
import org.jactr.tools.marker.markerof.IMarkerOf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(Parser.class);

  static public final String         PROBE_TAG        = "probe";

  static public final String         MARKER_TAG       = "marker";

  static public final String         LINK_TAG         = "link";

  static public final String         MODEL_TAG        = "model";

  static public final String         CHUNK_TAG        = "chunk";

  static public final String         CHUNK_TYPE_TAG   = "chunk-type";

  static public final String         PRODUCTION_TAG   = "production";

  static public final String         MODULE_TAG       = "module";

  static public final String         BUFFER_TAG       = "buffer";

  static public final String         EXTENSION_TAG    = "extension";

  static public final String         PATTERN_ATTR     = "pattern";

  static public final String         POLL_ATTR        = "poll";

  static public final String         SELECT_ALL       = ".*";

  static public final String         TIME_WINDOW_ATTR = "window";

  static public final String         GROUP_TAG        = "group";

  static public final String         ID_ATTR          = "id";

  private Document                   _document;

  public Parser(URL resource) throws ParserConfigurationException,
      SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    _document = parser.parse(resource.openStream());
  }

  public double getTimeWindow()
  {
    try
    {
      return Double.parseDouble(_document.getDocumentElement().getAttribute(
          TIME_WINDOW_ATTR));
    }
    catch (NumberFormatException nfe)
    {
      return 1;
    }
  }

  public Collection<ModelSelector> buildModelSelectors()
  {
    NodeList nl = _document.getDocumentElement()
        .getElementsByTagName(GROUP_TAG);
    Collection<ModelSelector> rtn = new ArrayList<ModelSelector>();

    /*
     * groups could be provided..
     */
    if (nl.getLength() == 0)
      nl = _document.getDocumentElement().getElementsByTagName(MODEL_TAG);

    for (int i = 0; i < nl.getLength(); i++)
    {
      String groupName = "";
      Element node = (Element) nl.item(i);
      if (node.getNodeName().equals(GROUP_TAG))
      {
        groupName = node.getAttribute(ID_ATTR);
        /*
         * group the children
         */
        NodeList kids = node.getChildNodes();
        for (int j = 0; j < kids.getLength(); j++)
          if (kids.item(j).getNodeName().equalsIgnoreCase(MODEL_TAG))
          {
            ModelSelector selector = buildModelSelector((Element) kids.item(j));
            selector.setGroupId(groupName);
            rtn.add(selector);
          }
      }
      else
        rtn.add(buildModelSelector(node));
    }

    return rtn;
  }

  protected ModelSelector buildModelSelector(Element element)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;

    ModelSelector selector = new ModelSelector(pattern);

    ModelProbe lastProbe = null;
    NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++)
    {
      String nodeName = nl.item(i).getNodeName();
      if (nodeName.equals(MODULE_TAG) || nodeName.equals(BUFFER_TAG)
          || nodeName.equals(EXTENSION_TAG))
        selector.add(buildGeneralSelector((Element) nl.item(i)));
      if (nodeName.equals(PRODUCTION_TAG))
        selector.add(buildProductionSelector((Element) nl.item(i)));
      if (nodeName.equals(CHUNK_TYPE_TAG))
        selector.add(buildChunkTypeSelector((Element) nl.item(i)));
      if (nodeName.equalsIgnoreCase(CHUNK_TAG))
        selector.add(buildChunkSelector((Element) nl.item(i)));
      if (nodeName.equalsIgnoreCase(CHUNK_TAG))
        selector.add(buildChunkSelector((Element) nl.item(i)));
      if (nodeName.equalsIgnoreCase(PROBE_TAG))
      {
        lastProbe = buildModelProbe((Element) nl.item(i), lastProbe);
        selector.add(lastProbe);
      }
    }

    return selector;
  }

  protected ProductionSelector buildProductionSelector(Element element)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;

    ProductionSelector selector = new ProductionSelector(pattern);

    NodeList nl = element.getChildNodes();
    ProductionProbe lastProbe = null;
    for (int i = 0; i < nl.getLength(); i++)
      if (nl.item(i).getNodeName().equals(PROBE_TAG))
      {
        lastProbe = buildProductionProbe((Element) nl.item(i), lastProbe);
        selector.add(lastProbe);
      }
      else if (nl.item(i).getNodeName().equalsIgnoreCase(MARKER_TAG))
      {
        IMarkerOf markerOf = buildMarkerOf((Element) nl.item(i));
        selector.add(markerOf);
      }

    return selector;
  }

  protected ChunkSelector buildChunkSelector(Element element)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;

    ChunkSelector selector = new ChunkSelector(pattern);

    NodeList nl = element.getChildNodes();
    ChunkProbe lastProbe = null;
    for (int i = 0; i < nl.getLength(); i++)
    {
      String nodeName = nl.item(i).getNodeName();
      if (nodeName.equalsIgnoreCase(LINK_TAG))
        selector.add(buildLinkSelector((Element) nl.item(i)));
      else if (nodeName.equalsIgnoreCase(PROBE_TAG))
      {
        lastProbe = buildChunkProbe((Element) nl.item(i), lastProbe);
        selector.add(lastProbe);
      }
      else if (nodeName.equalsIgnoreCase(MARKER_TAG))
      {
        IMarkerOf markerOf = buildMarkerOf((Element) nl.item(i));
        selector.add(markerOf);
      }
    }

    return selector;
  }

  protected LinkSelector buildLinkSelector(Element element)
  {
    String chunkPattern = element.getAttribute(CHUNK_TAG);
    if (chunkPattern.length() == 0) chunkPattern = SELECT_ALL;

    String chunkTypePattern = element.getAttribute(CHUNK_TYPE_TAG);
    if (chunkTypePattern.length() == 0) chunkTypePattern = SELECT_ALL;

    LinkSelector selector = new LinkSelector(chunkTypePattern, chunkPattern);

    NodeList nl = element.getChildNodes();
    LinkParameterProbe lastProbe = null;
    for (int i = 0; i < nl.getLength(); i++)
      if (nl.item(i).getNodeName().equals(PROBE_TAG))
      {
        lastProbe = buildLinkProbe((Element) nl.item(i), lastProbe);
        selector.add(lastProbe);
      }

    return selector;
  }

  protected ClassNamedParameterSelector buildGeneralSelector(Element element)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;

    String tagName = element.getTagName();
    ClassNamedParameterSelector selector = null;

    if (tagName.equals("buffer")) selector = new BufferSelector(pattern);
    else
    if (tagName.equals("module")) selector = new ModuleSelector(pattern);
    else
    if (tagName.equals("extension")) selector = new ExtensionSelector(pattern);
    else
    if (tagName.equals("instrument"))
      selector = new InstrumentSelector(pattern);
    else
      selector = new ClassNamedParameterSelector(pattern);

    NodeList nl = element.getChildNodes();
    ParameterizedProbe lastProbe = null;
    for (int i = 0; i < nl.getLength(); i++)
      if (nl.item(i).getNodeName().equals(PROBE_TAG))
      {
        lastProbe = buildParameterizedProbe((Element) nl.item(i), lastProbe);
        selector.add(lastProbe);
      }
      else if (nl.item(i).getNodeName().equalsIgnoreCase(MARKER_TAG))
      {
        IMarkerOf markerOf = buildMarkerOf((Element) nl.item(i));
        selector.add(markerOf);
      }

    return selector;
  }

  protected ChunkTypeSelector buildChunkTypeSelector(Element element)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;

    ChunkTypeSelector selector = new ChunkTypeSelector(pattern);

    // NodeList nl = element.getElementsByTagName(PROBE_TAG);
    // ChunkProbe lastProbe = null;
    // for (int i = 0; i < nl.getLength(); i++)
    // {
    // lastProbe = buildChunkProbe((Element) nl.item(i), lastProbe);
    // selector.add(lastProbe);
    // }
    NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++)
      if (nl.item(i).getNodeName().equals(CHUNK_TAG))
        selector.add(buildChunkSelector((Element) nl.item(i)));

    return selector;
  }

  protected ModelProbe buildModelProbe(Element element, ModelProbe probe)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;
    // boolean poll = Boolean.parseBoolean(element.getAttribute(POLL_ATTR));

    if (probe == null) probe = new ModelProbe(pattern, null);
    probe.addPattern(pattern);
    probe.setPollable(Boolean.parseBoolean(element.getAttribute(POLL_ATTR)));
    return probe;
  }

  protected ProductionProbe buildProductionProbe(Element element,
      ProductionProbe probe)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;
    // boolean poll = Boolean.parseBoolean(element.getAttribute(POLL_ATTR));

    if (probe == null) probe = new ProductionProbe(pattern, null);
    probe.addPattern(pattern);
    probe.setPollable(Boolean.parseBoolean(element.getAttribute(POLL_ATTR)));
    return probe;
  }

  protected ParameterizedProbe buildParameterizedProbe(Element element,
      ParameterizedProbe probe)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;
    // boolean poll = Boolean.parseBoolean(element.getAttribute(POLL_ATTR));

    if (probe == null) probe = new ParameterizedProbe(pattern, null);
    probe.addPattern(pattern);
    // probe.setPollable(Boolean.parseBoolean(element.getAttribute(POLL_ATTR)));
    // for now they must be pollable as we dont have general listener for
    // parameterized
    probe.setPollable(true);
    return probe;
  }

  protected LinkParameterProbe buildLinkProbe(Element element,
      LinkParameterProbe probe)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;
    // boolean poll = Boolean.parseBoolean(element.getAttribute(POLL_ATTR));

    if (probe == null) probe = new LinkParameterProbe(pattern, null);
    probe.addPattern(pattern);
    // probe.setPollable(Boolean.parseBoolean(element.getAttribute(POLL_ATTR)));
    // for now they must be pollable as we dont have general listener for
    // parameterized
    probe.setPollable(true);
    return probe;
  }

  protected ChunkProbe buildChunkProbe(Element element, ChunkProbe probe)
  {
    String pattern = element.getAttribute(PATTERN_ATTR);
    if (pattern.length() == 0) pattern = SELECT_ALL;
    // boolean poll = Boolean.parseBoolean(element.getAttribute(POLL_ATTR));

    if (probe == null) probe = new ChunkProbe(pattern, null);
    probe.addPattern(pattern);
    probe.setPollable(Boolean.parseBoolean(element.getAttribute(POLL_ATTR)));
    return probe;
  }

  protected IMarkerOf buildMarkerOf(Element element)
  {
    String className = element.getAttribute("class");
    try
    {
      Class clazz = getClass().getClassLoader().loadClass(className);
      IMarkerOf markerOf = (IMarkerOf) clazz.newInstance();
      return markerOf;
    }
    catch (Exception e)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String.format("Could not instantiate markerOf class %s",
                className), e);
      return null;
    }
  }
}
