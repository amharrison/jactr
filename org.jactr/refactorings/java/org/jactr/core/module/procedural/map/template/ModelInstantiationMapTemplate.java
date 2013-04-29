package org.jactr.core.module.procedural.map.template;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.map.instance.IInstaniationMap;
import org.jactr.core.production.IProduction;

public class ModelInstantiationMapTemplate
    extends
    AbstractInstantiationMapTemplate<IModel, IActivationBuffer, BufferInstantiationMapTemplate>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelInstantiationMapTemplate.class);

  final private IModelListener       _bufferListener;

  final private Set<IProduction>     _unmanagedProductions;

  public ModelInstantiationMapTemplate(IModel root)
  {
    super(root);
    _unmanagedProductions = new HashSet<IProduction>();
    _bufferListener = new ModelListenerAdaptor() {
      @Override
      public void bufferInstalled(ModelEvent event)
      {
        addSubMap(getRoot(), event.getBuffer());
      }
    };
  }

  @Override
  protected Set<IActivationBuffer> getSubMapKeys(IModel root)
  {
    Set<IActivationBuffer> rtn = new HashSet<IActivationBuffer>();
    rtn.addAll(root.getActivationBuffers());
    return rtn;
  }

  @Override
  public IInstaniationMap<IModel> instantiate(Object... params)
  {

    return null;
  }

  @Override
  protected BufferInstantiationMapTemplate instantiateSubMap(IModel root,
      IActivationBuffer key)
  {
    return new BufferInstantiationMapTemplate(key);
  }

  @Override
  protected void installInitialMaps(IModel root)
  {
    /*
     * attach a listener to track buffer additions
     */
    root.addListener(_bufferListener, ExecutorServices.INLINE_EXECUTOR);

    super.installInitialMaps(root);
  }

  @Override
  public boolean add(IProduction production)
  {
    boolean added = super.add(production);
    /*
     * if none of the submaps were able to install this production, it is made
     * entirely of scripts, proxies, or variable conditions and therefor must
     * always be considered..
     */
    if (!added) _unmanagedProductions.add(production);
    return true;
  }

  @Override
  public Set<IProduction> get(Set<IProduction> container)
  {
    Set<IProduction> rtn = super.get(container);
    rtn.addAll(_unmanagedProductions);
    return rtn;
  }
}
