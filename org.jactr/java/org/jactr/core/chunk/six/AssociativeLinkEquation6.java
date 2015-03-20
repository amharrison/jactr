package org.jactr.core.chunk.six;

/*
 * default logging
 */
import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.six.learning.IDeclarativeLearningModule6;

public class AssociativeLinkEquation6 implements IAssociativeLinkEquation
{
  /**
   * Logger definition
   */
  static private final transient Log  LOGGER = LogFactory
                                                 .getLog(AssociativeLinkEquation6.class);

  private IDeclarativeLearningModule6 _declarativeLearningModule;

  public AssociativeLinkEquation6(IDeclarativeLearningModule6 decLM)
  {
    _declarativeLearningModule = decLM;
  }

  public double computeLearnedStrength(IAssociativeLink link)
  {
    return computeDefaultStrength(link);
  }

  public double computeDefaultStrength(IAssociativeLink link)
  {
    if (Double.isNaN(_declarativeLearningModule.getMaximumStrength()))
      return 0;

    /*
     * fanji is (1+slotsj) / slotsofji. where slotsj is the number of slots
     * where j is the value across all of DM. slotsofji is the number of slots
     * in i that have j (plus 1 if i==j). slotsofji is actually just
     * link.getCount(). We do not need the +1 modifiers in the numerator or
     * denominator since the self-link is explicit (count >=1, slotsj >=1)
     */
    // just count all the references.
    // this may underestimate, to be completely correct, we need to iterate over
    // all the i associations
    // and sum their counts.

    double numerator = link.getJChunk()
        .getAdapter(IAssociativeLinkContainer.class).getNumberOfOutboundLinks();

    double denominator = ((Link4) link).getCount();

    double strength = _declarativeLearningModule.getMaximumStrength()
        - Math.log(numerator / denominator);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "Sji j:%s i:%s slotsj %.2f / slotsji %.2f = %.2f, yields Sji %.2f",
          link.getJChunk(), link.getIChunk(), numerator, denominator,
          numerator / denominator, strength));

    return strength;
  }

  public void resetStrengths(IModel model)
  {
    try
    {
      FastList<IAssociativeLink> links = FastList.newInstance();
      for (IChunk chunk : model.getDeclarativeModule().getChunks().get())
      {
        IAssociativeLinkContainer alc = chunk
            .getAdapter(IAssociativeLinkContainer.class);
        links.clear();
        alc.getOutboundLinks(links);
        for (IAssociativeLink link : links)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Reseting strength of " + link);
          ((Link4) link).setStrength(computeDefaultStrength(link));
        }
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Could not reset links because of an exception ", e);
    }
  }

}
