package org.jactr.modules.pm.aural.memory.impl;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;

public class DefaultPerceptListener implements IActivePerceptListener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultPerceptListener.class);

  private IAuralModule               _module;

  public DefaultPerceptListener(IAuralModule module)
  {
    _module = module;

  }

  protected IChunk getNamedChunk(String name)
  {
    IChunk rtn = null;
    try
    {
      rtn = _module.getModel().getDeclarativeModule().getChunk(name).get();
    }
    catch (Exception e)
    {
      LOGGER.error(String.format("Failed to get chunk %s from model", name), e);
    }
    return rtn;
  }

  public void reencoded(final IIdentifier identifier, final IChunk oldChunk,
      IChunk newChunk)
  {
    /*
     * its normal to get the reencoded event after removing the chunk from the
     * buffer
     */
    if (oldChunk.isEncoded()) return;

    IModel model = _module.getModel();

    if (model.getDeclarativeModule().willEncode(oldChunk)) return;

    if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder("Percept underlying ");
      sb.append(oldChunk).append(" has changed too much. (").append(identifier)
          .append(")");
      String msg = sb.toString();

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, msg);

      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    }

    IChunk error = _module.getModel().getDeclarativeModule().getErrorChunk();

    _module.getAuralActivationBuffer().setStateChunk(error);
    _module.getAuralActivationBuffer().setErrorChunk(
        getNamedChunk(IStatusBuffer.ERROR_CHANGED_TOO_MUCH_CHUNK));
    _module.getAuralActivationBuffer().setExecutionChunk(error);
  }

  public void removed(final IIdentifier identifier, final IChunk chunk)
  {

    /*
     * and set error. we do this on the model thread just in case there are
     * buffer changes currently going on..
     */
    if (chunk.isEncoded())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(String
                .format(
                    "%s is no longer audible, but chunk has been encoded. Ignoring message.",
                    identifier));
      return;
    }

    /*
     * log
     */
    IModel model = _module.getModel();

    if (model.getDeclarativeModule().willEncode(chunk))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(String
                .format(
                    "%s is no longer audible, but chunk is about to be encoded. Ignoring message.",
                    identifier));
      return;
    }

    if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder("Percept underlying ");
      sb.append(chunk).append(" is no longer audible. (").append(identifier)
          .append(")");
      String msg = sb.toString();

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, msg);

      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    }

    /**
     * only set the error if the chunk is currently in the buffer. If not, it's
     * up to the activation buffer itself..
     */
    if (chunk.hasBeenDisposed()
        || BufferUtilities.getContainingBuffers(chunk, true).contains(
            _module.getAuralActivationBuffer()))
    {
      IChunk error = _module.getModel().getDeclarativeModule().getErrorChunk();

      _module.getAuralActivationBuffer().setStateChunk(error);
      _module.getAuralActivationBuffer().setErrorChunk(
          getNamedChunk(IStatusBuffer.ERROR_NO_LONGER_AVAILABLE_CHUNK));
      _module.getAuralActivationBuffer().setExecutionChunk(error);
    }
  }

  public void updated(IIdentifier identifier, IChunk chunk)
  {
    /*
     * if the chunk is no longer visible, pretend it was removed..
     */
    if (_module.getAuralMemory().getIndexManager().getIndexChunk(chunk) == null)
    {
      removed(identifier, chunk);
      return;
    }

    /*
     * first log that the chunk has changed.
     */
    IModel model = _module.getModel();
    if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder("Percept underlying ");
      sb.append(chunk).append(" has changed. (").append(identifier).append(")");
      String msg = sb.toString();

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, msg);

      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    }

    double start = ACTRRuntime.getRuntime().getClock(model).getTime();

    /*
     * now we need to set the buffer as busy for 50ms
     */
    double end = start + 0.05;

    model.getTimedEventQueue().enqueue(
        new RunnableTimedEvent(start, new Runnable() {

          public void run()
          {
            _module.getAuralActivationBuffer().setExecutionChunk(
                _module.getModel().getDeclarativeModule().getBusyChunk());
          }
        }));

    model.getTimedEventQueue().enqueue(
        new RunnableTimedEvent(end, new Runnable() {

          public void run()
          {
            _module.getAuralActivationBuffer().setExecutionChunk(
                _module.getModel().getDeclarativeModule().getFreeChunk());
          }
        }));
  }

  public void newPercept(IIdentifier identifier, IChunk chunk)
  {
    /*
     * flag the percept as new..
     */
    // removed into anonymous inner in default visual module
    // so that it can be accessed immediately after the chunk is available
    // IFINSTFeatureMap finstMap =
    // _module.getVisualMemory().getFINSTFeatureMap();
    // if (finstMap != null && !finstMap.isAttended(identifier))
    // finstMap.flagAsNew(identifier, chunk, _module.getVisualMemory()
    // .getOnsetDuration());
  }

}
