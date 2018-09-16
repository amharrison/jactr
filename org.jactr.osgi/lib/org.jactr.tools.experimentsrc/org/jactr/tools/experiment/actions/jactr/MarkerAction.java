package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tools.marker.IMarker;
import org.jactr.tools.marker.MarkerManager;
import org.jactr.tools.marker.impl.DefaultMarker;

public class MarkerAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MarkerAction.class);

  private String                     _modelNames;

  private String                     _typeName;

  private String                     _name;

  private final IExperiment          _experiment;

  private final boolean              _shouldOpen;

  public MarkerAction(String modelNames, String typeName, String name,
      boolean open, IExperiment experiment)
  {
    _modelNames = modelNames;
    _typeName = typeName;
    _name = name;
    _experiment = experiment;
    _shouldOpen = open;
  }

  public void fire(IVariableContext context)
  {
    Collection<IModel> models = null;
    if (!_modelNames.equals(""))
      models = VariableResolver.getModels(_modelNames,
          _experiment.getVariableResolver(), context);
    else
    {
      IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
      if (model != null) models = Collections.singleton(model);
    }

    String typeName = _experiment.getVariableResolver()
        .resolve(_typeName, _experiment.getVariableContext()).toString();
    String name = _experiment.getVariableResolver()
        .resolve(_name, _experiment.getVariableContext()).toString();

    HashSet<IMarker> existingMarkers = new HashSet<IMarker>();

    for (IModel model : models)
    {

      if (_shouldOpen)
      {
        IMarker marker = new DefaultMarker(model, name, typeName);
        marker.open(_experiment.getTime());
      }
      else
      {
        existingMarkers.clear();
        Set<IMarker> markers = MarkerManager.get().getMarkers(model,
            existingMarkers);
        for (IMarker marker : markers)
          if (marker.getType().equals(typeName)
              && marker.getName().equals(name))
            marker.close(_experiment.getTime());
      }
    }
  }

}
