package org.jactr.core.chunk.four;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * uses the ACT-R 4 equations for the learning and setting of associative link
 * strengths and priors
 * 
 * @author harrison
 */
public class AssociativeLinkEquation4 implements IAssociativeLinkEquation
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER = LogFactory
      .getLog(AssociativeLinkEquation4.class);

  final private IDeclarativeLearningModule4 _declarativeLearningModule;

  public AssociativeLinkEquation4(IDeclarativeLearningModule4 decLM)
  {
    _declarativeLearningModule = decLM;
  }

  public double computeLearnedStrength(IAssociativeLink link)
  {
    Link4 link4 = (Link4) link;
    IChunk iChunk = link4.getIChunk();
    IChunk jChunk = link4.getJChunk();
    if (iChunk.equals(jChunk)) return 1;

    IModel m = iChunk.getModel();

    if (!_declarativeLearningModule.isAssociativeLearningEnabled())
      return link4.getStrength();

    ISubsymbolicChunk4 j = jChunk.getAdapter(ISubsymbolicChunk4.class);
    ISubsymbolicChunk4 i = iChunk.getAdapter(ISubsymbolicChunk4.class);

    double numerator = _declarativeLearningModule.getAssociativeLearning()
        * link4.getRStrength();
    double denom = _declarativeLearningModule.getAssociativeLearning()
        * j.getTimesInContext();
    double fcEji = 1.0;
    if (i.getTimesNeeded() == 0)
      fcEji = j.getTimesInContext();
    else
      fcEji = link4.getFNICJ() * (m.getCycle() - i.getCreationCycle())
          / i.getTimesNeeded();
    numerator += fcEji;

    if (LOGGER.isDebugEnabled()) LOGGER.debug(
        "numerator : " + numerator + " denom : " + denom + " fcEji : " + fcEji);

    double rStrength = numerator / denom;
    link4.setRStrength(rStrength);

    return Math.log(numerator / denom);
  }

  public double computeDefaultStrength(IAssociativeLink link)
  {
    Link4 link4 = (Link4) link;
    IChunk iChunk = link4.getIChunk();
    IChunk jChunk = link4.getJChunk();

    /**
     * if a strength already exists (i.e., is not 0), and learning is off, we
     * assume that the strength of this link was modeler specified, so we leave
     * it alone. <br/>
     */
    if (!_declarativeLearningModule.isAssociativeLearningEnabled()
        && Math.abs(0 - link4.getStrength()) > 0.0001)
      return link4.getStrength();

    if (iChunk.equals(jChunk)) return 1;

    int totalChunksInMemory = (int) jChunk.getModel().getDeclarativeModule()
        .getNumberOfChunks();

    int fan = 0;
    IAssociativeLinkContainer alc = jChunk
        .getAdapter(IAssociativeLinkContainer.class);

    ISubsymbolicChunk4 ssc4 = jChunk.getSubsymbolicChunk()
        .getAdapter(ISubsymbolicChunk4.class);

    if (ssc4 != null) fan = (int) alc.getNumberOfOutboundLinks();

    double dRji = 0;
    if (fan > 0)
      dRji = (double) totalChunksInMemory / (double) fan;
    else if (fan == 0) dRji = 1.0;

    /*
     * number of duplicate references, scales the default r accordingly
     */
    if (link4.getCount() > 0) dRji *= link4.getCount();

    if (LOGGER.isDebugEnabled()) LOGGER.debug(this + " defaultRji : " + dRji
        + "(" + Math.log(dRji) + ") fan : " + fan + " count : "
        + link4.getCount() + " totalFacts : " + totalChunksInMemory);

    link4.setRStrength(dRji);

    return Math.log(dRji);
  }

  public void resetStrengths(IModel model)
  {
    Collection<IAssociativeLink> links = FastCollectionFactory.newInstance();
    try
    {
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
    finally
    {
      FastCollectionFactory.recycle(links);
    }
  }

}
