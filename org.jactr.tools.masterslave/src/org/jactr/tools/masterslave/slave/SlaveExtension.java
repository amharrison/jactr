package org.jactr.tools.masterslave.slave;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.tools.masterslave.master.MasterExtension;

public class SlaveExtension implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SlaveExtension.class);

  private IModel                     _model;

  private MasterExtension            _master;
  
  /**
   * this listener allows us to set the has-completed and is-running slave variables correctly
   */
  private IModelListener _modelListener = new ModelListenerAdaptor(){
    @Override
    public void modelStopped(ModelEvent event)
    {
      IUniqueSlotContainer container = getVariables();
      ((IMutableSlot)container.getSlot(SlaveStateCondition.HAS_COMPLETED_SLOT)).setValue(Boolean.TRUE);
      ((IMutableSlot)container.getSlot(SlaveStateCondition.IS_RUNNING_SLOT)).setValue(Boolean.FALSE);
    }
  };

  static public SlaveExtension getSlaveExtension(IModel model)
  {
    return (SlaveExtension) model.getExtension(SlaveExtension.class);
  }

  public void setMaster(MasterExtension master)
  {
    _master = master;
  }

  public IUniqueSlotContainer getVariables()
  {
    if (_master == null) return null;
    return _master.getSlaveVariables(_model.getName());
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return "slave";
  }

  public void install(IModel model)
  {
    _model = model;
    _model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);
  }

  public void uninstall(IModel model)
  {
    _model.removeListener(_modelListener);
    _model = null;
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
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

}
