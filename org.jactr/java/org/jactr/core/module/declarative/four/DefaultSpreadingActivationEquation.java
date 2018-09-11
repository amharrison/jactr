package org.jactr.core.module.declarative.four;

import java.util.ArrayList;
/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.Logger.Stream;
import org.jactr.core.model.IModel;

public class DefaultSpreadingActivationEquation implements
    ISpreadingActivationEquation
{
  /**
   * Logger definition
   */
  static private final transient Log                LOGGER          = LogFactory
                                                                        .getLog(DefaultSpreadingActivationEquation.class);

  private ThreadLocal<Collection<IAssociativeLink>> _linkCollection = new ThreadLocal<Collection<IAssociativeLink>>();

  private ThreadLocal<StringBuilder>                _stringBuilder  = new ThreadLocal<StringBuilder>();

  /**
   * return the thread local cached string builder so we don't keep
   * instantiating
   * 
   * @param initialize
   * @return
   */
  private StringBuilder getStringBuilder(String initialize)
  {
    StringBuilder sb = _stringBuilder.get();
    if (sb == null)
    {
      sb = new StringBuilder(initialize);
      _stringBuilder.set(sb);
    }
    else
    {
      sb.delete(0, sb.length());
      sb.append(initialize);
    }
    return sb;
  }

  public double computeSpreadingActivation(IModel model, IChunk c)
  {
    double spread = 0.0;
    ISubsymbolicChunk4 ssc4 = c.getAdapter(ISubsymbolicChunk4.class);

    Collection<IAssociativeLink> collection = _linkCollection.get();
    if (collection == null)
    {
      collection = new ArrayList<IAssociativeLink>();
      _linkCollection.set(collection);
    }


    StringBuilder logMsg = null;
    if (Logger.hasLoggers(model)) logMsg = getStringBuilder("");

    for (IAssociativeLink jLink : ssc4.getJAssociations(collection))
    {
      double localSpread = 0;
      IChunk jChunk = jLink.getJChunk();
      ISubsymbolicChunk sc = jChunk.getSubsymbolicChunk();
      double source = sc.getSourceActivation();
      double strength = jLink.getStrength();

      if (jLink instanceof Link4) strength *= ((Link4) jLink).getCount();

      localSpread = source * strength;

      if (logMsg != null)
        logMsg.append(String.format("(%s.source %.2f x %.2f = %.2f) ", jChunk
            .getSymbolicChunk().getName(), source, strength, localSpread));

      spread += localSpread;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("pulling %.2f from %s to %s", localSpread,
            jChunk, c));
    }

    if (Double.isNaN(spread) || Double.isInfinite(spread)) spread = 0;

    if (logMsg != null)
    {
      logMsg.insert(0, String.format("%s.spreading = %.2f %s", c
          .getSymbolicChunk().getName(), spread,
          collection.size() != 0 ? "from " : ""));
      Logger.log(model, Stream.ACTIVATION, logMsg.toString());
    }

    collection.clear();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s spreading activation = %.2f", c, spread));

    return spread;
  }

  @Override
  public String getName()
  {
    return "spread4";
  }

  @Override
  public double computeAndSetActivation(IChunk chunk, IModel model)
  {
    double spread = computeSpreadingActivation(model, chunk);
    ((AbstractSubsymbolicChunk) chunk.getSubsymbolicChunk())
        .setSpreadingActivation(spread);
    return spread;
  }

}
