package org.jactr.core.module.declarative.four;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.four.learning.DefaultBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.utils.references.DefaultReferences;
import org.jactr.core.utils.references.IReferences;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class DefaultBaseLevelLearningTest
{

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  /**
   * generate the expectations necessary to test the base level equations.
   * 
   * @param currentTime
   * @param baseLevelLearningRate
   * @param defaultActionTime
   * @param creationTime
   * @param referenceTimes
   * @return
   */
  protected Expectations referenceBaseLevel(final double currentTime,
      final double baseLevelLearningRate, final double defaultActionTime,
      final double creationTime, final IReferences referenceTimes,
      final IModel mockModel, final IChunk mockChunk)
  {
    final IDeclarativeModule4 dm = context.mock(IDeclarativeModule4.class);
    final IDeclarativeLearningModule4 dml4 = context
        .mock(IDeclarativeLearningModule4.class);
    final IProceduralModule procMod = context.mock(IProceduralModule.class);

    final ISubsymbolicChunk ssc = context.mock(ISubsymbolicChunk.class);

    Expectations expectations = new Expectations() {
      {
        allowing(mockModel).getDeclarativeModule();
        will(returnValue(dm));

        allowing(mockModel).getProceduralModule();
        will(returnValue(procMod));

        allowing(mockModel).getModule(IDeclarativeLearningModule4.class);
        will(returnValue(dml4));

        allowing(mockModel).getAge();
        will(returnValue(currentTime));

        allowing(dm).getBaseLevelConstant();
        will(returnValue(0d));

        allowing(dml4).getBaseLevelLearning();
        will(returnValue(baseLevelLearningRate));

        allowing(dml4).isBaseLevelLearningEnabled();
        will(returnValue(!Double.isNaN(baseLevelLearningRate)));

        allowing(procMod).getDefaultProductionFiringTime();
        will(returnValue(0.05d));

        allowing(ssc).getCreationTime();
        will(returnValue(creationTime));

        allowing(ssc).getReferences();
        will(returnValue(referenceTimes));

        allowing(mockChunk).getSubsymbolicChunk();
        will(returnValue(ssc));
      }
    };

    return expectations;
  }

  /**
   * return reference times
   * 
   * @param optimization
   *          0 for none, # of recent samples to use
   * @param times
   * @return
   */
  protected IReferences references(int optimization, double... times)
  {
    IReferences refs = new DefaultReferences(optimization);
    for (double time : times)
      refs.addReferenceTime(time);
    return refs;
  }

  /**
   * base level anomaly in comparison between jACT-R and Lisp versions of
   * JGT@NRL gesture learning model.
   */
  /**
   * <code>
   * 789951.153
   * Starting with blc: 0.0
Computing base-level from 19 references (703211.94 527465.75 526783.4 352191.1 351877.84 351706.38 351595.1 351529.25 264139.03 263885.56 176681.58 176403.27 176135.89 175781.58 88255.03)
 creation time: 720.557 decay: 0.5  Optimized-learning: 15
base-level value: -3.5563608
Total base-level: -3.5563608
Computing activation spreading from buffers
 Spreading 1.0 from buffer VISUAL chunk POLYGON918-0
   sources of activation are: (VISUAL-LOCATION18-0-0 POLYGON SPOTTED DOG)
   Spreading activation  0.0 from source VISUAL-LOCATION18-0-0 level  0.25 times Sji 0.0
   Spreading activation  0.0 from source POLYGON level  0.25 times Sji 0.0
   Spreading activation  0.5476405 from source SPOTTED level  0.25 times Sji 2.190562
   Spreading activation  0.46352246 from source DOG level  0.25 times Sji 1.8540899
 Spreading 1.0 from buffer GOAL chunk SEARCH1-10
   sources of activation are: (ENCODE)
   Spreading activation  0.0 from source ENCODE level  1.0 times Sji 0.0
Total spreading activation: 1.011163
Adding transient noise 0.0
Adding permanent noise 0.0
Chunk MEANING53-0 has an activation of: -2.5451978
Chunk MEANING53-0 has the current best activation -2.5451978
</code>
   */
  @Test
  public void testBLL051515JGT()
  {
    /*
     * initial parameters
     */
    double baseLevelLearning = 0.5;
    double defaultActionTime = 0.05;
    // 0 none, otherwise # of recent samples to include
    int optimizationLevel = 15;

    double clockTime = 789951.153;

    /*
     * chunk specific
     */
    double creationTime = 720.557;
    IReferences refs = references(optimizationLevel, new double[] { 703211.94,
        527465.75, 526783.4, 352191.1, 351877.84, 351706.38, 351595.1,
        351529.25, 264139.03, 263885.56, 176681.58, 176403.27, 176135.89,
        175781.58, 88255.03 });
    refs.setNumberOfReferences(19);

    IModel mockModel = context.mock(IModel.class);
    IChunk mockChunk = context.mock(IChunk.class, "chunk");
    Expectations expectations = referenceBaseLevel(clockTime,
        baseLevelLearning, defaultActionTime, creationTime, refs, mockModel,
        mockChunk);

    context.checking(expectations);

    DefaultBaseLevelActivationEquation bble = new DefaultBaseLevelActivationEquation(
        mockModel);

    double bl = bble.computeBaseLevelActivation(mockModel, mockChunk);

    Assert.assertEquals(String.format(""), -3.556, bl, 0.01);
  }

  /**
   * On month (simulated) later.. <code>
   * 878734.486
   * Computing activation for chunk MEANING14-0
Computing base-level
Starting with blc: 0.0
Computing base-level from 25 references (791320.75 790070.8 703376.5 703198.5 702295.2 615071.07 527492.9 527275.25 527024.57 526952.9 351581.56 264605.0 264283.38 263723.03 176766.86)
 creation time: 193.896 decay: 0.5  Optimized-learning: 15
base-level value: -3.212534
   * </code>
   */
  @Test
  public void testBLL051515JGT2()
  {
    /*
     * initial parameters
     */
    double baseLevelLearning = 0.5;
    double defaultActionTime = 0.05;
    // 0 none, otherwise # of recent samples to include
    int optimizationLevel = 15;

    double clockTime = 878734.486;

    /*
     * chunk specific
     */
    double creationTime = 193.896;
    IReferences refs = references(optimizationLevel, new double[] { 791320.75,
        790070.8, 703376.5, 703198.5, 702295.2, 615071.07, 527492.9, 527275.25,
        527024.57, 526952.9, 351581.56, 264605.0, 264283.38, 263723.03,
        176766.86 });
    refs.setNumberOfReferences(25);

    IModel mockModel = context.mock(IModel.class);
    IChunk mockChunk = context.mock(IChunk.class, "chunk");
    Expectations expectations = referenceBaseLevel(clockTime,
        baseLevelLearning, defaultActionTime, creationTime, refs, mockModel,
        mockChunk);

    context.checking(expectations);

    DefaultBaseLevelActivationEquation bble = new DefaultBaseLevelActivationEquation(
        mockModel);

    double bl = bble.computeBaseLevelActivation(mockModel, mockChunk);

    Assert.assertEquals(String.format(""), -3.212534, bl, 0.01);
  }
}
