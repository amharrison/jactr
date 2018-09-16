package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tools.experiment.misc.ModelUtilities;

/**
 * clears a model (or all) buffer contents and any pending actions related to
 * the buffers
 * 
 * @author harrison
 */
public class ClearAction implements IAction
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ClearAction.class);

  private String                     _models;

  private String                     _buffers;

  private String                     _modules;

  private IExperiment                _experiment;

  public ClearAction(String models, String buffers, String modules,
      IExperiment experiment)
  {
    _experiment = experiment;
    _models = models;
    _buffers = buffers;
    _modules = modules;
  }

  public void fire(IVariableContext context)
  {
    Collection<IModel> models = null;
    if (!_models.equals(""))
      models = VariableResolver.getModels(_models,
          _experiment.getVariableResolver(), context);
    else
    {
      IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
      if (model != null) models = Collections.singleton(model);
    }

    if (models.size() == 0)
      LOGGER.error("No clue what models to reward. " + _models
          + " did not resolve to any recognized models.");

    Collection<String> buffers = new ArrayList<String>();
    for (String bufferName : _buffers.split(","))
    {
      bufferName = bufferName.trim();
      if (bufferName.equalsIgnoreCase("all")) break;

      if (bufferName.length() > 0) buffers.add(bufferName.toLowerCase());
    }

    for (IModel model : models)
      clear(model, buffers);
  }

  private void resetModules(IModel model)
  {
    if (_modules == null || _modules.length() == 0) return;

    for (IModule module : model.getModules())
    {
      /*
       * we are looking for an empty method matches _modules
       */
      try
      {
        Method method = module.getClass().getMethod(_modules);
        method.invoke(module);
      }
      catch (NoSuchMethodException e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(
              String.format("Could not find %1$s.%2$s, ignoring.",
                  module.getClass(), _modules), e);
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to invoke %1$s.%2$s",
            module.getClass(), _modules), e);
      }
    }
  }

  protected void clear(final IModel model, final Collection<String> buffers)
  {
    ModelUtilities.executeNow(model, new Runnable() {

      public void run()
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Attempting to reset modules for " + model);
        /*
         * 
         */
        resetModules(model);

        Collection<ITimedEvent> firing = model.getTimedEventQueue()
            .getFiringEvents();
        Collection<ITimedEvent> pending = model.getTimedEventQueue()
            .getPendingEvents();

        for (IActivationBuffer buffer : model.getActivationBuffers())
          if (buffers.size() == 0
              || buffers.contains(buffer.getName().toLowerCase()))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Clearing " + model + "." + buffer.getName());
            // clear the buffer proper
            buffer.clear();

            // and now any pending events
            for (ITimedEvent event : firing)
              if (event instanceof IBufferBasedTimedEvent)
                if (buffer == ((IBufferBasedTimedEvent) event).getBuffer())
                  if (!event.hasFired() && !event.hasAborted())
                  {
                    if (LOGGER.isDebugEnabled())
                      LOGGER.debug("Aborting " + event);
                    event.abort();
                  }

            for (ITimedEvent event : pending)
              if (event instanceof IBufferBasedTimedEvent)
                if (buffer == ((IBufferBasedTimedEvent) event).getBuffer())
                  if (!event.hasFired() && !event.hasAborted())
                  {
                    if (LOGGER.isDebugEnabled())
                      LOGGER.debug("Aborting " + event);
                    event.abort();
                  }
          }
      }

    });
  }
}
