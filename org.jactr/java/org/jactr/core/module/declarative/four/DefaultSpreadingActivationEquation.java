package org.jactr.core.module.declarative.four;

/*
 * default logging
 */
import java.util.Collection;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.model.IModel;

public class DefaultSpreadingActivationEquation implements
    ISpreadingActivationEquation
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSpreadingActivationEquation.class);

  private ThreadLocal<Collection<IAssociativeLink>> _linkCollection = new ThreadLocal<Collection<IAssociativeLink>>();

  public double computeSpreadingActivation(IModel model, IChunk c)
  {
    double spread = 0.0;
    ISubsymbolicChunk4 ssc4 = c.getSubsymbolicChunk()
        .getAdapter(ISubsymbolicChunk4.class);

    Collection<IAssociativeLink> collection = _linkCollection.get();
    if (collection == null)
    {
      collection = new FastList<IAssociativeLink>();
      _linkCollection.set(collection);
    }


    for (IAssociativeLink jLink : ssc4.getJAssociations(collection))
    {
      double localSpread = 0;
      ISubsymbolicChunk sc = jLink.getJChunk().getSubsymbolicChunk();
      localSpread = sc.getSourceActivation() * jLink.getStrength();
      
      if(jLink instanceof Link4)
        localSpread *= ((Link4)jLink).getCount();
      
      spread += localSpread;
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("pulling %.2f from %s to %s", localSpread,
            jLink.getJChunk(), c));
    }

    collection.clear();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s spreading activation = %.2f", c, spread));
    if (Double.isNaN(spread) || Double.isInfinite(spread)) spread = 0;

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
