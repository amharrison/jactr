/*
 * Created on Apr 14, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * Utility class for ensuring that model's fire correctly
 * 
 * @author developer
 */
public class ExecutionTester implements IInstrument, IParameterized
{
  /**
   * logger definition
   */
  static private final Log          LOGGER = LogFactory
      .getLog(ExecutionTester.class);

  private Iterator<String>          _productionSequence;

  private Set<String>               _failedProductions;

  private Set<String>               _ignoreProductions;

  private Collection<Throwable>     _exceptions;

  private IProceduralModuleListener _productionListener;

  private IModelListener            _modelListener;

  public ExecutionTester()
  {
    _ignoreProductions = new TreeSet<String>();
    _failedProductions = new TreeSet<String>();
    _exceptions = new ArrayList<Throwable>();
  }

  /**
   * called by the model listener so that Junit tests can handle the exception
   * gracefully
   * 
   * @param thrown
   */
  public void exceptionCaught(Throwable thrown)
  {
    _exceptions.add(thrown);
  }

  /**
   * called by the proceduralModuleListener after each production fires
   * 
   * @param model
   * @param instantiation
   */
  final public void verifyThatProductionShouldFire(IModel model,
      IInstantiation instantiation)
  {
    Iterator<String> productionSequence = _productionSequence;
    Set<String> failedProductions = _failedProductions;

    String productionName = instantiation.getProduction()
        .getSymbolicProduction().getName();

    // should we ignore?
    if (_ignoreProductions.contains(productionName)) return;

    // is this a fail fast production?
    if (failedProductions.contains(productionName)) throw new RuntimeException(
        "(" + model + ") " + productionName + " is never supposed to fire");

    // finally check the sequence
    if (productionSequence != null)
    {
      if (!productionSequence.hasNext()) throw new RuntimeException("(" + model
          + ") No more productions should be firing, got " + productionName);

      String expectedName = productionSequence.next();

      if (!productionName.equalsIgnoreCase(expectedName))
        throw new RuntimeException(
            "(" + model + ") Wrong production fired. Expecting " + expectedName
                + " got " + productionName);
    }

    verifyModelState(model, instantiation);
  }

  @Override
  public void install(IModel model)
  {
    _productionListener = new ProceduralModuleListenerAdaptor() {

      @Override
      public void productionFired(ProceduralModuleEvent pme)
      {
        verifyThatProductionShouldFire(pme.getSource().getModel(),
            (IInstantiation) pme.getProduction());
      }
    };

    model.getProceduralModule().addListener(_productionListener,
        ExecutorServices.INLINE_EXECUTOR);

    _modelListener = new ModelListenerAdaptor() {

      @Override
      public void exceptionThrown(ModelEvent me)
      {
        exceptionCaught(me.getException());
      }

      @Override
      public void modelStopped(ModelEvent me)
      {
        Iterator<String> productionSequence = _productionSequence;
        if (productionSequence != null && productionSequence.hasNext())
          _exceptions.add(
              new RuntimeException("Not all productions have fired, expecting "
                  + productionSequence.next()));
      }
    };

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

  }

  @Override
  public void uninstall(IModel model)
  {
    model.removeListener(_modelListener);
    model.getProceduralModule().removeListener(_productionListener);
  }

  @Override
  public void initialize()
  {

  }

  public Collection<Throwable> getExceptions()
  {
    return _exceptions;
  }

  private Collection<String> asProductionNames(String valueList)
  {
    String[] productions = valueList.split(",");
    List<String> rtn = new ArrayList<>(productions.length);
    for (String production : productions)
    {
      production = production.trim();
      if (production.length() > 0) rtn.add(production);
    }
    return rtn;
  }

  public void setParameter(String key, String value)
  {
    if ("ProductionSequence".equalsIgnoreCase(key))
      _productionSequence = asProductionNames(value).iterator();
    else if ("FailedProductions".equalsIgnoreCase(key))
    {
      _failedProductions = new TreeSet<String>();
      _failedProductions.addAll(asProductionNames(value));
    }
    else if ("IgnoreProductions".equalsIgnoreCase(key)) _ignoreProductions.addAll(asProductionNames(value));
  }

  public void setProductionSequence(List<String> productionSequence)
  {
    _productionSequence = new ArrayList<String>(productionSequence).iterator();
  }

  public void setIgnoredProductions(Collection<String> ignoreProductions)
  {
    _ignoreProductions.addAll(ignoreProductions);
  }

  public void setFailedProductions(Collection<String> failedProductions)
  {
    _failedProductions = new TreeSet<>();
    _failedProductions.addAll(failedProductions);
  }

  @Override
  public String getParameter(String key)
  {
    return null;
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * this can be overriden if you want to check the state of the model just
   * after a production has fired
   * 
   * @param model
   */
  public void verifyModelState(IModel model, IInstantiation instantiation)
  {

  }
}
