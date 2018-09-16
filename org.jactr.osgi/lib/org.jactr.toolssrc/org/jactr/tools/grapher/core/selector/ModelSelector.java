package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.DeclarativeModuleListenerAdaptor;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.tools.grapher.core.container.IProbeContainer;

public class ModelSelector extends AbstractNameSelector<IModel>
{
  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(ModelSelector.class);

  private IProceduralModuleListener      _proceduralListener;

  private IDeclarativeModuleListener     _declarativeListener;

  private Collection<ProductionSelector> _productionSelectors;

  private Collection<ChunkSelector>      _chunkSelectors;

  private Collection<ChunkTypeSelector>  _chunkTypeSelectors;

  private Collection<ModuleSelector>     _moduleSelectors;

  private Collection<BufferSelector>     _bufferSelectors;

  private Collection<ExtensionSelector>  _extensionSelectors;

  private Collection<InstrumentSelector> _instrumentSelectors;

  public ModelSelector(String regex)
  {
    super(regex);
    _productionSelectors = new ArrayList<ProductionSelector>();
    _chunkSelectors = new ArrayList<ChunkSelector>();
    _chunkTypeSelectors = new ArrayList<ChunkTypeSelector>();
    _moduleSelectors = new ArrayList<ModuleSelector>();
    _extensionSelectors = new ArrayList<ExtensionSelector>();
    _bufferSelectors = new ArrayList<BufferSelector>();
    _instrumentSelectors = new ArrayList<InstrumentSelector>();

    _proceduralListener = new ProceduralModuleListenerAdaptor() {
      @Override
      public void productionAdded(ProceduralModuleEvent pme)
      {
        checkProduction(pme.getProduction(), getProbeContainer(pme.getSource()
            .getModel()));
      }
    };

    _declarativeListener = new DeclarativeModuleListenerAdaptor() {
      @Override
      public void chunkAdded(DeclarativeModuleEvent dme)
      {
        checkChunk(dme.getChunk(),
            getProbeContainer(dme.getSource().getModel()));
      }

      @Override
      public void chunkTypeAdded(DeclarativeModuleEvent dme)
      {
        checkChunkType(dme.getChunkType(), getProbeContainer(dme.getSource()
            .getModel()));
      }
    };
  }

  public void add(ISelector selector)
  {
    selector.setGroupId(getGroupId());
    if (selector instanceof ProductionSelector)
      _productionSelectors.add((ProductionSelector) selector);
    else if (selector instanceof ChunkSelector)
      _chunkSelectors.add((ChunkSelector) selector);
    else if (selector instanceof ChunkTypeSelector)
      _chunkTypeSelectors.add((ChunkTypeSelector) selector);
    else if (selector instanceof ModuleSelector)
      _moduleSelectors.add((ModuleSelector) selector);
    else if (selector instanceof BufferSelector)
      _bufferSelectors.add((BufferSelector) selector);
    else if (selector instanceof ExtensionSelector)
      _extensionSelectors.add((ExtensionSelector) selector);
    else if (selector instanceof InstrumentSelector)
      _instrumentSelectors.add((InstrumentSelector) selector);
  }

  @Override
  protected String getContainerName(IModel element)
  {
    if (getGroupId().length() == 0) return getName(element);
    return getName(element) + "." + getGroupId();
  }

  @Override
  public IProbeContainer install(IModel element, IProbeContainer container)
  {
    container = super.install(element, container);

    // Executor executor = ExecutorServices
    // .getExecutor(ExecutorServices.BACKGROUND);
    Executor executor = ExecutorServices.INLINE_EXECUTOR;

    element.getProceduralModule().addListener(_proceduralListener, executor);
    element.getDeclarativeModule().addListener(_declarativeListener, executor);
    element.addListener(new ModelListenerAdaptor() {

      @Override
      public void extensionInstalled(ModelEvent me)
      {
        IExtension extension = me.getExtension();
        if (extension instanceof IParameterized)
          checkGeneral(extension, getProbeContainer(me.getSource()),
              _extensionSelectors);
      }

      @Override
      public void instrumentInstalled(ModelEvent me)
      {
        IInstrument instrument = me.getInstrument();
        if (instrument instanceof IParameterized)
          checkGeneral((IParameterized) instrument,
              getProbeContainer(me.getSource()), _instrumentSelectors);
      }

      @Override
      public void moduleInstalled(ModelEvent me)
      {
        IModule module = me.getModule();
        if (module instanceof IParameterized)
          checkGeneral((IParameterized) module,
              getProbeContainer(me.getSource()), _moduleSelectors);
      }

    }, executor);

    try
    {
      for (IModule module : element.getModules())
        if (module instanceof IParameterized)
          checkGeneral((IParameterized) module, container, _moduleSelectors);

      for (IExtension extension : element.getExtensions())
        checkGeneral(extension, container, _extensionSelectors);

      for (IActivationBuffer buffer : element.getActivationBuffers())
        if (buffer instanceof IParameterized)
          checkGeneral((IParameterized) buffer, container, _bufferSelectors);

      for (IInstrument instrument : element.getInstruments())
        if (instrument instanceof IParameterized)
          checkGeneral((IParameterized) instrument, container,
              _instrumentSelectors);

      for (IProduction production : element.getProceduralModule()
          .getProductions().get())
        checkProduction(production, container);

      for (IChunkType chunkType : element.getDeclarativeModule()
          .getChunkTypes().get())
        checkChunkType(chunkType, container);

      for (IChunk chunk : element.getDeclarativeModule().getChunks().get())
        checkChunk(chunk, container);
    }
    catch (Exception e)
    {
      LOGGER.error("Could not get individual elements for selector matching ",
          e);
    }

    return container;
  }

  protected void checkProduction(IProduction production,
      IProbeContainer container)
  {
    for (ProductionSelector selector : _productionSelectors)
      if (selector.matches(production))
        selector.install(production, container);
  }

  protected void checkChunk(IChunk chunk, IProbeContainer container)
  {
    for (ChunkSelector selector : _chunkSelectors)
      if (selector.matches(chunk)) selector.install(chunk, container);
  }

  protected void checkChunkType(IChunkType chunkType, IProbeContainer container)
  {
    for (ChunkTypeSelector selector : _chunkTypeSelectors)
      if (selector.matches(chunkType)) selector.install(chunkType, container);
  }

  protected void checkGeneral(IParameterized parameterized,
      IProbeContainer container,
      Collection<? extends ClassNamedParameterSelector> selectors)
  {
    for (ClassNamedParameterSelector selector : selectors)
      if (selector.matches(parameterized))
        selector.install(parameterized, container);
  }

  @Override
  protected String getName(IModel element)
  {
    return element.getName();
  }
}
