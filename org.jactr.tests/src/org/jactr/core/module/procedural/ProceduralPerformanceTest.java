package org.jactr.core.module.procedural;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.ModuleTest;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;

public class ProceduralPerformanceTest extends ModuleTest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProceduralPerformanceTest.class);

  public void testProductionInstantiation() throws Exception
  {
    setUpProductionTest(getModel(), 100);

    // attachLogger(getModel());

    execute(getModel(), 1000);
  }

  protected void attachLogger(IModel model)
  {
    DefaultModelLogger dml = new DefaultModelLogger();

    // dml.setParameter(Logger.CYCLE,"out");
    dml.setParameter(Logger.Stream.TIME.toString(), "out");
    // dml.setParameter(Logger.Stream.GOAL.toString(), "err");
    dml.setParameter(Logger.Stream.PROCEDURAL.toString(), "out");
    // dml.setParameter(Logger.CONFLICT_RESOLUTION, "out");
    // dml.setParameter(Logger.CONFLICT_SET, "out");
    // dml.setParameter(Logger.ACTIVATION_BUFFER,"out");
    // dml.setParameter(Logger.MATCHES,"out");
    // dml.setParameter(Logger.EXACT_MATCH,"out");
    // dml.setParameter(Logger.PARTIAL_MATCH,"out");

    model.install(dml);
  }

  private void execute(IModel model, int seconds) throws Exception
  {
    // make sure we are using profiling for maximum info
    System.setProperty("jactr.profiling", "true");

    IController controller = new DefaultController();
    ACTRRuntime.getRuntime().setController(controller);
    ACTRRuntime.getRuntime().addModel(model);

    try
    {
      // start
      assertTrue(controller.start().get());

      IClock clock = ACTRRuntime.getRuntime().getClock(model);
      for (double percent = 0; percent < 1; percent += 0.1)
      {
        clock.waitForTime(percent * seconds);
        System.out.println((int) (percent * 100) + "%");
        System.out.flush();
      }

      assertTrue(controller.stop().get());
    }
    finally
    {
      ACTRRuntime.getRuntime().removeModel(model);
      ACTRRuntime.getRuntime().setController(null);
    }
  }

  /**
   * creates totalProductions mock production and one real production for
   * instantiation testing..
   * 
   * @param module
   * @param totalProductions
   */
  private void setUpProductionTest(IModel model, int totalProductions)
      throws Exception
  {
    IDeclarativeModule dm = model.getDeclarativeModule();
    /*
     * first we need the test chunktype
     */
    IChunkType test = dm.createChunkType((IChunkType)null, "test").get();

    test.getSymbolicChunkType().addSlot(new BasicSlot("slot"));
    test = dm.addChunkType(test).get();

    /*
     * and the actual goal
     */
    IChunkType chunk = dm.getChunkType("chunk").get();

    IChunk target = dm.createChunk(chunk, "target").get();
    target = dm.addChunk(target).get();

    IChunk goal = dm.createChunk(test, "goal").get();
    goal.getSymbolicChunk().addSlot(new BasicSlot("slot", target));

    /*
     * stick it in the buffer
     */
    model.getActivationBuffer("goal").addSourceChunk(goal);

    /*
     * now we need to create all the foils..
     */
    IProceduralModule pm = model.getProceduralModule();
    for (int i = 0; i < totalProductions; i++)
    {
      IChunk foil = dm.createChunk(chunk, "foil-" + i).get();
      foil = dm.addChunk(foil).get();

      /*
       * and the production
       */
      IProduction fProduction = pm.createProduction("foil-" + i).get();
      /*
       * single condition..
       */
      ChunkTypeCondition condition = new ChunkTypeCondition("goal", test);
      condition.addSlot(new DefaultConditionalSlot("slot",
          IConditionalSlot.EQUALS, foil));
      fProduction.getSymbolicProduction().addCondition(condition);

      /*
       * and a trivial action
       */
      // ModifyAction action = new ModifyAction("goal");
      // action.addSlot(new BasicSlot("slot", foil));
      // fProduction.getSymbolicProduction().addAction(action);
      fProduction = pm.addProduction(fProduction).get();
    }

    /*
     * and the actual production
     */
    IProduction tProduction = pm.createProduction("target").get();
    /*
     * single condition..
     */
    ChunkTypeCondition condition = new ChunkTypeCondition("goal", test);
    condition.addSlot(new DefaultConditionalSlot("slot",
        IConditionalSlot.EQUALS, target));
    tProduction.getSymbolicProduction().addCondition(condition);

    /*
     * and a trivial action
     */
    // ModifyAction action = new ModifyAction("goal");
    // action.addSlot(new BasicSlot("slot", target));
    // tProduction.getSymbolicProduction().addAction(action);
    tProduction = pm.addProduction(tProduction).get();

  }
}
