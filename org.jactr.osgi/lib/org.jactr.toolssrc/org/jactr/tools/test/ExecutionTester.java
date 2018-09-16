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
package org.jactr.tools.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.io.environment.EnvironmentParser;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.resolver.ASTResolver;

/**
 * Utility class for ensuring that model's fire correctly
 * 
 * @author developer
 */
public class ExecutionTester
{
  /**
   * logger definition
   */
  static private final Log              LOGGER = LogFactory
      .getLog(ExecutionTester.class);

  private Map<IModel, Iterator<String>> _productionSequenceMap;

  private Map<IModel, Set<String>>      _failedProductionMap;

  private Collection<Throwable>         _exceptions;

  public ExecutionTester()
  {
    _productionSequenceMap = new HashMap<IModel, Iterator<String>>();
    _failedProductionMap = new HashMap<IModel, Set<String>>();
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
    Iterator<String> productionSequence = _productionSequenceMap.get(model);
    Set<String> failedProductions = _failedProductionMap.get(model);

    String productionName = instantiation.getProduction()
        .getSymbolicProduction().getName();

    if (failedProductions.contains(productionName)) throw new RuntimeException(
        "(" + model + ") " + productionName + " is never supposed to fire");

    if (!productionSequence.hasNext()) throw new RuntimeException("(" + model
        + ") No more productions should be firing, got " + productionName);

    String expectedName = productionSequence.next();

    if (!productionName.equalsIgnoreCase(expectedName))
      throw new RuntimeException(
          "(" + model + ") Wrong production fired. Expecting " + expectedName
              + " got " + productionName);

    verifyModelState(model, instantiation);
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

  protected void dump(IModel model)
  {
    CommonTree modelDesc = ASTResolver.toAST(model, true);
    for (StringBuilder line : CodeGeneratorFactory.getCodeGenerator("jactr")
        .generate(modelDesc, true))
      LOGGER.debug(line.toString());
  }

  public Collection<Throwable> test(IModel modelToTest,
      Map<String, Collection<String>> productionSequenceMap,
      Map<String, Collection<String>> failedProductionMap)
  {
    if (modelToTest != null)
    {
//      DefaultModelLogger dml = new DefaultModelLogger();
//      dml.setParameter("all", "err");
//      modelToTest.install(dml);
      ACTRRuntime.getRuntime().addModel(modelToTest);
      ACTRRuntime.getRuntime().setController(new DefaultController());
    }

    IProceduralModuleListener listener = new ProceduralModuleListenerAdaptor() {

      @Override
      public void productionFired(ProceduralModuleEvent pme)
      {
        verifyThatProductionShouldFire(pme.getSource().getModel(),
            (IInstantiation) pme.getProduction());
      }
    };

    for (IModel model : ACTRRuntime.getRuntime().getModels())
    {
      String modelName = model.getName();
      if (productionSequenceMap.containsKey(modelName))
      {
        _productionSequenceMap.put(model,
            new ArrayList<String>(productionSequenceMap.get(modelName))
                .iterator());
        _failedProductionMap.put(model,
            new TreeSet<String>(failedProductionMap.get(modelName)));
        model.getProceduralModule().addListener(listener,
            ExecutorServices.INLINE_EXECUTOR);
      }
      else if (LOGGER.isWarnEnabled()) LOGGER.warn(
          "Have no production information for " + modelName + " will not test");
      model.addListener(new ModelListenerAdaptor() {

        @Override
        public void exceptionThrown(ModelEvent me)
        {
          exceptionCaught(me.getException());
        }

        @Override
        public void modelStopped(ModelEvent me)
        {
          Iterator<String> productionSequence = _productionSequenceMap
              .get(me.getSource());
          if (productionSequence != null && productionSequence.hasNext())
            _exceptions.add(new RuntimeException(
                "Not all productions have fired, expecting "
                    + productionSequence.next()));
        }
      }, ExecutorServices.INLINE_EXECUTOR);
    }

    /*
     * all set up - let's execute..
     */
    IController controller = ACTRRuntime.getRuntime().getController();
    try
    {
      controller.start().get();
      controller.complete().get();

      if (_exceptions.size() > 0)
      {
        LOGGER.debug("Completed with exceptions : " + _exceptions);
        dump(modelToTest);
      }

      return _exceptions;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not execute runtime ", e);
    }
    finally
    {
      for (IModel model : new ArrayList<IModel>(
          ACTRRuntime.getRuntime().getModels()))
        try
        {
          ACTRRuntime.getRuntime().removeModel(model);
          model.dispose();
        }
        catch (Exception e2)
        {
          _exceptions.add(e2);
        }
    }
  }

  /**
   * test run.
   * 
   * @param url
   *          of the environment file
   * @param productionSequenceMap
   *          keyed on model name, a sequence of productions that should fire
   * @param failedProductionMap
   *          keyed on model name, a set of productions that should never fire
   */
  public Collection<Throwable> test(URL url,
      Map<String, Collection<String>> productionSequenceMap,
      Map<String, Collection<String>> failedProductionMap)
  {
    try
    {
      EnvironmentParser envP = new EnvironmentParser();
      envP.parse(url);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not load environment ", e);
    }

    return test((IModel) null, productionSequenceMap, failedProductionMap);
  }

  public Collection<Throwable> test(Supplier<IModel> supplier,
      Collection<String> productionSequence,
      Collection<String> failedProductions)
  {
    IModel model = supplier.get();
    String modelName = model.getName();

    Map<String, Collection<String>> sequence = new TreeMap<String, Collection<String>>();
    sequence.put(modelName, productionSequence);
    Map<String, Collection<String>> failed = new TreeMap<String, Collection<String>>();
    failed.put(modelName, failedProductions);

    return test(model, sequence, failed);
  }

  /**
   * @param url
   * @param modelName
   *          not null
   * @param productionSequence
   *          not null or empty
   * @param failedProductions
   *          not null
   */
  public Collection<Throwable> test(URL url, String modelName,
      Collection<String> productionSequence,
      Collection<String> failedProductions)
  {
    Map<String, Collection<String>> sequence = new TreeMap<String, Collection<String>>();
    sequence.put(modelName, productionSequence);
    Map<String, Collection<String>> failed = new TreeMap<String, Collection<String>>();
    failed.put(modelName, failedProductions);

    return test(url, sequence, failed);
  }
}
