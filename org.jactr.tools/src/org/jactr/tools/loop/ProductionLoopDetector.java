package org.jactr.tools.loop;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.parameter.BooleanParameterProcessor;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHelper;
import org.jactr.instrument.IInstrument;

public class ProductionLoopDetector implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                = LogFactory
                                                               .getLog(ProductionLoopDetector.class);

  private IProduction                _lastProductionFired;

  private double                     _lastFiringTime;

  private double                     _defaultProductionFiringTime;

  private boolean                    _terminateOnDetection = true;

  private FiringSequenceListener     _sequenceListener;

  private ParameterHelper            _parameters           = new ParameterHelper();

  public ProductionLoopDetector()
  {
    _sequenceListener = new FiringSequenceListener(this);
    _parameters.addProcessor(new BooleanParameterProcessor(
        "TerminateOnDetection", this::setTerminateOnDetectionEnabled,
        this::isTerminateOnDetectionEnabled));
  }

  @Override
  public void install(IModel model)
  {
    IProceduralModule pM = model.getProceduralModule();
    pM.addListener(_sequenceListener, ExecutorServices.INLINE_EXECUTOR);
  }

  @Override
  public void uninstall(IModel model)
  {
    IProceduralModule pM = model.getProceduralModule();
    pM.removeListener(_sequenceListener);
  }

  @Override
  public void initialize()
  {

  }

  public void setTerminateOnDetectionEnabled(boolean onOff)
  {
    _terminateOnDetection = onOff;
  }

  public boolean isTerminateOnDetectionEnabled()
  {
    return _terminateOnDetection;
  }

  public void productionFired(double simulationTime,
      IInstantiation instantiation)
  {
    IProduction production = instantiation.getProduction();

    if (production.equals(_lastProductionFired))
    {
      // check the time difference, or the cycle difference.
      double delta = simulationTime - _lastFiringTime;
      if (Math.abs(_defaultProductionFiringTime - delta) < 0.001)
        loopDetected(simulationTime, instantiation);
    }

    _lastFiringTime = simulationTime;
    _lastProductionFired = production;
  }

  /**
   * since this is executed inline with the model, throwing a
   * ModelTerminatedException will do what we expect it to.
   * 
   * @param simulationTime
   * @param instantiation
   */
  protected void loopDetected(double simulationTime,
      IInstantiation instantiation)
  {
    String message = String.format(
        "Loop detected: %s fired @ %0.3f and again @ %0.3f",
        _lastProductionFired, _lastFiringTime, simulationTime);

    LOGGER.error(message, new RuntimeException("trace info"));

    if (isTerminateOnDetectionEnabled()) throw new ModelTerminatedException();
    // throw new ModelTerminatedException(message);
  }

  @Override
  public void setParameter(String key, String value)
  {
    _parameters.setParameter(key, value);
  }

  @Override
  public String getParameter(String key)
  {
    return _parameters.getParameter(key);
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Set<String> rtn = new TreeSet<String>();
    _parameters.getParameterNames(rtn);
    return rtn;
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Set<String> rtn = new TreeSet<String>();
    _parameters.getSetableParameterNames(rtn);
    return rtn;
  }
}
