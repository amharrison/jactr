package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;

public class LinkParameterProcessor extends
    ParameterProcessor<IAssociativeLink>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LinkParameterProcessor.class);

  /**
   * full constructor for others to overload.
   * 
   * @param parameterName
   * @param fromString
   * @param setFunction
   * @param toString
   * @param getFunction
   * @param actrProcessor
   * @param sourceChunk
   */
  public LinkParameterProcessor(String parameterName,
      Function<String, IAssociativeLink> fromString,
      Consumer<IAssociativeLink> setFunction,
      Function<IAssociativeLink, String> toString,
      Supplier<IAssociativeLink> getFunction)
  {
    super(parameterName, fromString, setFunction, toString, getFunction);
  }

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
      Consumer<IAssociativeLink> setFunction,
      Supplier<IAssociativeLink> getFunction,
      final ACTRParameterProcessor actrProcessor, final IChunk sourceChunk)
  {
    super(
        parameterName,
        (String s) -> {
          String stripped = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));

          IAssociativeLinkageSystem linkageSystem = sourceChunk.getModel()
              .getDeclarativeModule().getAssociativeLinkageSystem();
          if (linkageSystem == null)
          {
            if (LOGGER.isWarnEnabled())
              LOGGER
                  .warn("No IAssociativeLinkageSystem is installed, ignoring associative links");
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
            if (iChunk == null)
              throw new NullPointerException(String.format(
                  "Could not find chunk %s in declarative memory", split[0]));

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
            throw new IllegalArgumentException(String.format(
                "Failed to detailed link from %s because %s", stripped,
                e.getMessage()), e);
          }

        },
        setFunction,
        (IAssociativeLink value) -> {
          if (value.getIChunk().hasBeenDisposed()
              || !value.getIChunk().isEncoded()) return "";

          StringBuilder sb = new StringBuilder("(");
          sb.append(value.getIChunk().getSymbolicChunk().getName()).append(" ");

          sb.append(getLink4Parameters(value));

          sb.append(")");
          return sb.toString();
        }, getFunction);
  }



  static protected String getLink4Parameters(IAssociativeLink link)
  {
    return String.format("%d %.2f %.2f", ((Link4) link).getCount(),
        link.getStrength(), ((Link4) link).getFNICJ());
  }
}
