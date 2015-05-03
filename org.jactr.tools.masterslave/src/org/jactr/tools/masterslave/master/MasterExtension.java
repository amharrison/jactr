package org.jactr.tools.masterslave.master;

/*
 * default logging
 */
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.IllegalModelStateException;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.queue.timedevents.TerminationTimedEvent;
import org.jactr.core.reality.connector.IClockConfigurator;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.UniqueSlotContainer;
import org.jactr.io.IOUtilities;
import org.jactr.tools.masterslave.clock.MasterSlaveClockConfigurator;
import org.jactr.tools.masterslave.slave.SlaveExtension;
import org.jactr.tools.masterslave.slave.SlaveStateCondition;

public class MasterExtension implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER          = LogFactory
                                                                .getLog(MasterExtension.class);

  private IModel                            _model;

  private Map<String, IModel>               _slaveModels    = new TreeMap<String, IModel>();

  private Map<String, IUniqueSlotContainer> _slaveVariables = new TreeMap<String, IUniqueSlotContainer>();

  private IACTRRuntimeListener              _runtimeListener;

  /**
   * finds the installed instanceof the master extension in the given model
   * 
   * @param model
   * @return
   */
  static public MasterExtension getMaster(IModel model)
  {
    return (MasterExtension) model.getExtension(MasterExtension.class);
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return "master";
  }

  @SuppressWarnings("unused")
  public void install(IModel model)
  {
    if (true)
      throw new IllegalModelStateException(
          String
              .format(
                  "%s is currently nonfunctional until the underlying clock wrapper can be updated.",
                  getClass().getSimpleName()));

    _model = model;

    /*
     * the masterslave also requires some tricky clock control, we need the
     * IConnector to do taht.
     */
    IConnector connector = ACTRRuntime.getRuntime().getConnector();
    IClockConfigurator original = connector.getClockConfigurator();
    if (!(original instanceof MasterSlaveClockConfigurator))
    {
      MasterSlaveClockConfigurator mscc = new MasterSlaveClockConfigurator(
          original);
      connector.setClockConfigurator(mscc);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Installed custom clock configurator"));
    }

    _runtimeListener = new ACTRRuntimeAdapter() {

      public void modelStopped(ACTRRuntimeEvent event)
      {
        IModel model = event.getModel();
        String modelName = model.getName();
        if (_slaveModels.containsKey(modelName))
        {
          IUniqueSlotContainer container = getSlaveVariables(modelName);
          ((IMutableSlot) container
              .getSlot(SlaveStateCondition.HAS_COMPLETED_SLOT))
              .setValue(Boolean.TRUE);
          ((IMutableSlot) container
              .getSlot(SlaveStateCondition.IS_RUNNING_SLOT))
              .setValue(Boolean.FALSE);
        }

      }

      public void modelStarted(ACTRRuntimeEvent event)
      {
        IModel model = event.getModel();
        String modelName = model.getName();
        if (_slaveModels.containsKey(modelName))
        {
          IUniqueSlotContainer container = getSlaveVariables(modelName);
          ((IMutableSlot) container
              .getSlot(SlaveStateCondition.IS_RUNNING_SLOT))
              .setValue(Boolean.TRUE);
          ((IMutableSlot) container
              .getSlot(SlaveStateCondition.HAS_COMPLETED_SLOT))
              .setValue(Boolean.FALSE);
        }
      }
    };

    ACTRRuntime.getRuntime().addListener(_runtimeListener, null);
  }

  public void uninstall(IModel model)
  {
    _model = null;

    ACTRRuntime.getRuntime().removeListener(_runtimeListener);
  }

  public String getParameter(String key)
  {

    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.EMPTY_LIST;
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.EMPTY_LIST;
  }

  public void setParameter(String key, String value)
  {

  }

  public void initialize() throws Exception
  {

  }

  public IModel getSlaveModel(String alias)
  {
    return _slaveModels.get(alias.toLowerCase());
  }

  public IUniqueSlotContainer getSlaveVariables(String alias)
  {
    IUniqueSlotContainer container = _slaveVariables.get(alias.toLowerCase());
    if (container == null) container = createSlaveVariables(alias);
    return container;
  }

  private IUniqueSlotContainer createSlaveVariables(String alias)
  {
    // make sure we use mutables
    IUniqueSlotContainer container = new UniqueSlotContainer(true);
    container.addSlot(new BasicSlot(SlaveStateCondition.ALIAS_SLOT, alias));
    container.addSlot(new BasicSlot(SlaveStateCondition.IS_LOADED_SLOT,
        Boolean.FALSE));
    container.addSlot(new BasicSlot(SlaveStateCondition.IS_RUNNING_SLOT,
        Boolean.FALSE));
    container.addSlot(new BasicSlot(SlaveStateCondition.HAS_COMPLETED_SLOT,
        Boolean.FALSE));
    return container;
  }

  protected IModel loadModelAs(URL modelFile, String alias) throws Exception
  {
    Collection<Exception> warnings = new HashSet<Exception>();
    Collection<Exception> errors = new HashSet<Exception>();
    IModel model = null;

    CommonTree modelDescriptor = IOUtilities.loadModelFile(modelFile, warnings,
        errors);

    if (errors.size() != 0) throw errors.iterator().next();

    if (IOUtilities.compileModelDescriptor(modelDescriptor, warnings, errors))
      model = IOUtilities.constructModel(modelDescriptor, warnings, errors);

    if (errors.size() != 0) throw errors.iterator().next();

    if (model instanceof BasicModel) ((BasicModel) model).setName(alias);

    _slaveModels.put(alias.toLowerCase(), model);

    /*
     * link them together
     */
    SlaveExtension se = (SlaveExtension) model
        .getExtension(SlaveExtension.class);
    if (se == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format(
            "%s has no SlaveExtension installed, forcing installation", alias));

      se = new SlaveExtension();
      model.install(se);
    }

    se.setMaster(this);
    IUniqueSlotContainer container = createSlaveVariables(alias);
    ((IMutableSlot) container.getSlot(SlaveStateCondition.IS_LOADED_SLOT))
        .setValue(Boolean.TRUE);
    container.addSlot(new BasicSlot(SlaveStateCondition.MODEL_SLOT, model));
    _slaveVariables.put(alias, container);

    return model;
  }

  protected void startModel(IModel model)
  {
    /*
     * since we can be certain the master is already running, we should just
     * need to add the model..
     */
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    IController controller = runtime.getController();

    if (controller.getRunningModels().contains(model))
      throw new IllegalStateException("Already running");
    else if (!runtime.getModels().contains(model))
    {
      /*
       * temporarily mark it as 'starting', to prevent models from starting
       * multiple times
       */
      IUniqueSlotContainer container = getSlaveVariables(model.getName());
      ((IMutableSlot) container.getSlot(SlaveStateCondition.IS_RUNNING_SLOT))
          .setValue("starting");

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Starting %s", model.getName()));

      runtime.addModel(model);
    }

    if (!controller.isRunning()) controller.start();
  }

  protected void stopModel(IModel model)
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    IController controller = runtime.getController();

    if (controller.getRunningModels().contains(model))
    {
      double now = runtime.getClock(model).getTime();
      model.getTimedEventQueue().enqueue(new TerminationTimedEvent(now, now));
    }

    /*
     * we can't block
     */

  }

  protected void cleanUp(final IModel model) throws IllegalStateException
  {
    String modelName = model.getName().toLowerCase();
    boolean wasSlave = _slaveModels.remove(modelName) != null;
    _slaveVariables.remove(modelName);

    if (!wasSlave)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format(
            "%s was not a slave model, or was already cleanedup", modelName));

      return;
    }

    Runnable cleanUpRunner = new Runnable() {

      public void run()
      {
        ACTRRuntime runtime = ACTRRuntime.getRuntime();
        Collection<IModel> allModels = runtime.getModels();
        Collection<IModel> terminated = runtime.getController()
            .getTerminatedModels();

        if (!allModels.contains(model))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER
                .debug(String.format(
                    "%s is not in the runtime, can safely ignore",
                    model.getName()));

          return;
        }

        if (terminated.contains(model))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s has terminated, cleaning up",
                model.getName()));

          // clean up
          runtime.removeModel(model);
          model.dispose();
        }
        else
        // while we are in the cleanup, but the model hasn't fully terminated
        {
          if (LOGGER.isWarnEnabled())
            LOGGER
                .warn(String
                    .format(
                        "%s is not in the terminated set %s, but is in the active set. Not completely terminated, will try again later.",
                        model, terminated));
          _model.getTimedEventQueue().enqueue(
              new RunnableTimedEvent(ACTRRuntime.getRuntime().getClock(_model)
                  .getTime() + 0.05, this));
        }
      }

    };

    _model.getTimedEventQueue().enqueue(
        new RunnableTimedEvent(ACTRRuntime.getRuntime().getClock(_model)
            .getTime() + 0.05, cleanUpRunner));
  }

  public void copyInto(IChunkType chunkType, IModel destination)
  {

  }

  public void copyInto(IChunk chunk, IModel destination)
  {

  }

  /**
   * @bug this code will not work w/ multiple inheritance
   * @param source
   * @param destination
   * @param sourceToCopy
   */
  protected void createChunkType(IChunkType source, IModel destination,
      Map sourceToCopy)
  {
    /*
     * do we already have it? just using name test
     */
    try
    {
      ISymbolicChunkType sct = source.getSymbolicChunkType();
      IChunkType copy = destination.getDeclarativeModule()
          .getChunkType(sct.getName()).get();

      if (copy != null) return;

      /*
       * it may not be encoded yet, but it could be in the sourceToCopy map
       */
      if (sourceToCopy.get(source) != null) return;

      /*
       * check its parent
       */
      IChunkType parent = sct.getParent();
      if (parent != null)
      {
        createChunkType(parent, destination, sourceToCopy);
        // make it the copy parent now
        parent = (IChunkType) sourceToCopy.get(parent);
      }

      /*
       * now lets create
       */
      copy = destination.getDeclarativeModule()
          .createChunkType(parent, sct.getName()).get();
      sourceToCopy.put(source, copy);

      /*
       * lets traverse the slots, looking for chunks that we have to handle.. we
       * wont assign slot values until after creation
       */
      for (ISlot slot : sct.getSlots())
        if (slot.getValue() instanceof IChunk)
          createChunk((IChunk) slot.getValue(), destination, sourceToCopy);
        else if (slot.getValue() instanceof IChunkType)
          createChunkType((IChunkType) slot.getValue(), destination,
              sourceToCopy);
        else if (slot.getValue() instanceof IProduction)
          LOGGER
              .warn("Copying of productions between models is not currently supported");
    }
    catch (Exception e)
    {
      LOGGER.error("unknown exception while getting chunktype ", e);
    }
  }

  protected void createChunk(IChunk source, IModel destination, Map sourceToCopy)
  {

  }

}
