package org.jactr.core.utils.parameter;

import java.util.Collection;
/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;

public class LinkParameterProcessor
    extends ParameterProcessor<Collection<IAssociativeLink>>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(LinkParameterProcessor.class);

  /**
   * link parameter handler with string transform functions supporting "(iLink
   * count strength fNiCj)"
   * 
   * @param parameterName
   * @param setFunction
   * @param getFunction
   * @param actrProcessor
   * @param sourceChunk
   */
  public LinkParameterProcessor(String parameterName,
      Consumer<Collection<IAssociativeLink>> setFunction,
      Supplier<Collection<IAssociativeLink>> getFunction,
      final ACTRParameterProcessor actrProcessor,
      final Supplier<IChunk> sourceChunk)
  {
    super(parameterName, null, setFunction, null, getFunction);
    setToString(this::asString);
    setFromString(s -> {
      return fromString(sourceChunk, actrProcessor, s);
    });
  }

  protected Collection<IAssociativeLink> fromString(
      Supplier<IChunk> sourceChunk,
      ACTRParameterProcessor actrProcessor, String outboundAssociations)
  {
    Collection<IAssociativeLink> links = Lists.mutable.empty();

    String stripped = outboundAssociations.substring(
        outboundAssociations.indexOf("(") + 1,
        outboundAssociations.lastIndexOf(")"));
    String[] splits = stripped.split(",");
    for (String association : splits)
    {
      association = association.trim();
      if (association.length() == 0) continue;

      IAssociativeLink link = asLink(sourceChunk.get(), actrProcessor,
          association);
      if (link != null) links.add(link);
    }

    return links;
  }

  protected String asString(Collection<IAssociativeLink> outboundAssociations)
  {
    StringBuilder sb = new StringBuilder("(");
    sb.append(outboundAssociations.stream().map(this::asString)
        .collect(Collectors.joining(",")));
    sb.append(")");
    return sb.toString();
  }

  protected IAssociativeLink asLink(IChunk sourceChunk,
      ACTRParameterProcessor actrProcessor, String stringRep)
  {
    String stripped = stringRep.substring(stringRep.indexOf("(") + 1,
        stringRep.lastIndexOf(")"));

    IAssociativeLinkageSystem linkageSystem = sourceChunk.getModel()
        .getDeclarativeModule().getAssociativeLinkageSystem();
    if (linkageSystem == null)
    {
      if (LOGGER.isWarnEnabled()) LOGGER.warn(
          "No IAssociativeLinkageSystem is installed, ignoring associative links");
      return null;
    }

    String[] split = stripped.split(" ");

    try
    {
      // /*
      // * first should be chunk, second a number
      // */
      IChunk iChunk = (IChunk) actrProcessor.getFromStringFunction()
          .apply(split[0].trim());
      if (iChunk == null) throw new NullPointerException(String
          .format("Could not find chunk %s in declarative memory", split[0]));

      int count = (int) Double.parseDouble(split[1].trim());
      double strength = Double.parseDouble(split[2].trim());

      double fnicj = 0;
      if (split.length > 3) fnicj = Double.parseDouble(split[3]);

      Link4 link = (Link4) linkageSystem.createLink(iChunk, sourceChunk);
      link.setCount(count);
      link.setFNICJ(fnicj);
      link.setStrength(strength);

      return link;
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException(
          String.format("Failed to detailed link from %s because %s", stripped,
              e.getMessage()),
          e);
    }
  }

  /**
   * override to change the link representation
   * 
   * @param linkRep
   * @return
   */
  protected String asString(IAssociativeLink linkRep)
  {
    if (linkRep.getIChunk().hasBeenDisposed()
        || !linkRep.getIChunk().isEncoded())
      return "";

    StringBuilder sb = new StringBuilder("(");
    sb.append(linkRep.getIChunk().getSymbolicChunk().getName()).append(" ");

    sb.append(getLink4Parameters(linkRep));

    sb.append(")");

    return sb.toString();
  }

  static protected String getLink4Parameters(IAssociativeLink link)
  {
    return String.format("%d %.2f %.2f", ((Link4) link).getCount(),
        link.getStrength(), ((Link4) link).getFNICJ());
  }
}
