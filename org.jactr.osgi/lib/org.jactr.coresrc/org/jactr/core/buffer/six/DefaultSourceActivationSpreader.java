package org.jactr.core.buffer.six;

import java.util.Collection;
/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.ISourceActivationSpreader;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.Logger.Stream;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * default activation spreader. this is not thread safe and assumes that
 * activation calculations are done serially.
 * 
 * @author harrison
 */
public class DefaultSourceActivationSpreader implements
    ISourceActivationSpreader
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSourceActivationSpreader.class);

  private final IActivationBuffer    _buffer;

  private final Map<IChunk, Integer> _activatedChunks;

  private double                     _activationPortion;

  public DefaultSourceActivationSpreader(IActivationBuffer buffer)
  {
    _buffer = buffer;
    _activatedChunks = new HashMap<IChunk, Integer>();
  }

  public Set<IChunk> getActivatedChunks(Set<IChunk> container)
  {
    if (container == null) container = new HashSet<IChunk>();
    container.addAll(_activatedChunks.keySet());
    return container;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

  /**
   * divies source activation amoung the chunks linked to the source chunks.
   */
  public void spreadSourceActivation()
  {
    clearSourceActivation();
    // nothing to spread
    if (_buffer.getActivation() == 0) return;
    /*
     * we use an array list which WILL permit duplicates. Why? shouldn't
     * something appearing N times in a source chunk get N times the source
     * activation? Note: this is not the same as the count used in the links.
     * Count & links deal with the spreading activation, this is source
     * activation. SO, if a source chunk has two slot references to X, it will
     * also have a link from X with a count of 2. X will receive twice the
     * source activation, and will propogate that through the doubled link.
     */
    Collection<IAssociativeLink> jLinks = FastCollectionFactory.newInstance();
    Collection<IChunk> sourceChunks = FastCollectionFactory.newInstance();

    try
    {
      _buffer.getSourceChunks(sourceChunks);

      if (sourceChunks.size() == 0) return;

      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(String
                .format(
                    "Calculating source activation to propogate through %s to %s @ %.2f",
                    _buffer.getName(), sourceChunks, _buffer.getModel()
                        .getAge()));

      /*
       * we need to get each chunk that the source contains. We could use the
       * slots and iterate through those that have chunks as values, but that
       * wouldn't be very flexible in terms of a long term associative
       * perspective. instead we look at the jLinks (links where the source
       * chunk is the iChunk), which will include the slot value and other
       * associated chunks. We ignore the strengths, and just use them to
       * determine the sources of activation
       */
      int numLinks = 0;
      for (IChunk sourceChunk : sourceChunks)
      {
        ISubsymbolicChunk4 ssc4 = sourceChunk
            .getAdapter(ISubsymbolicChunk4.class);
        if (ssc4 != null)
        {
          jLinks.clear();

          ssc4.getJAssociations(jLinks);

          for (IAssociativeLink link : jLinks)
          {
            IChunk jChunk = link.getJChunk();
            if (jChunk.hasBeenDisposed()) continue;

            /*
             * we might be tempted to check for a self link to exclude, but no.
             * We need to include it. While this is propogating source
             * activation, taht is not part of the total activation equation
             * (just spread). Only by propogating source to the self does spread
             * reach the slot values of this chunk.
             */

            int count = 1;
            if (link instanceof Link4) count = ((Link4) link).getCount();

            // we need to check activated chunks because the source chunks
            // might all point to similar chunks
            if (_activatedChunks.containsKey(jChunk))
              count += _activatedChunks.get(jChunk);

            if (LOGGER.isDebugEnabled())
              LOGGER.debug(String.format("%s(i) - %s(j) has %d links",
                  sourceChunk, jChunk, count));

            numLinks += count;
            _activatedChunks.put(jChunk, count);
          }
        }
      }

      /*
       * now we zip through the links
       */
      if (numLinks != 0)
      {
        _activationPortion = _buffer.getActivation() / numLinks;
        IModel model = getBuffer().getModel();

        IMessageBuilder logMsg = null;
        if (Logger.hasLoggers(model))
        {
          logMsg = Logger.messageBuilder();
          logMsg.append(getBuffer().getName()).append(" spreading ")
              .append(String.format("%.2f", _activationPortion));
          logMsg.append(" to each ");
        }

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "Activating associated chunks %s with %.2f", _activatedChunks,
              _activationPortion));

        for (Map.Entry<IChunk, Integer> entry : _activatedChunks.entrySet())
        {
          IChunk chunk = entry.getKey();
          double source = _activationPortion * entry.getValue();
          ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
          ssc.setSourceActivation(_buffer, source);

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s has %.2f", entry.getKey(),
                ssc.getSourceActivation()));

          if (logMsg != null)
            logMsg.append(chunk.getSymbolicChunk().getName()).append(" ");
        }

        if (logMsg != null) Logger.log(model, Stream.ACTIVATION, logMsg);
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("No associated chunks to activate"));
    }
    finally
    {
      FastCollectionFactory.recycle(jLinks);
      FastCollectionFactory.recycle(sourceChunks);
    }
  }

  public void clearSourceActivation()
  {
    if (_activatedChunks.size() == 0) return;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "Clearing source activation (%.2f) propogated from %s to %s @ %.2f",
          _activationPortion, _buffer.getName(), _activatedChunks, _buffer
              .getModel().getAge()));

    for (IChunk chunk : _activatedChunks.keySet())
    {
      // a chunk may have been disposed of by now...
      if (chunk.hasBeenDisposed()) continue;

      ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
      // double activation = ssc.getSourceActivation() - _activationPortion;
      ssc.setSourceActivation(_buffer, 0);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s has %.2f", chunk,
            ssc.getSourceActivation()));
    }

    _activatedChunks.clear();
    _activationPortion = 0;
  }

}
