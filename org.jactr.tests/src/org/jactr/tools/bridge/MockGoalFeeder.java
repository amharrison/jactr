/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.bridge;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;

/**
 * @author developer
 */
public class MockGoalFeeder extends GoalFeeder
{
  /**
   * logger definition
   */
  static private final Log LOGGER        = LogFactory
                                             .getLog(MockGoalFeeder.class);

  /**
   * we track the current trial
   */
  private int              _currentTrial = Integer.MIN_VALUE;

  /**
   * All we do is get the next trial, extract the string from it, and create new
   * goal with it.
   * 
   * @see org.jactr.tools.bridge.GoalFeeder#createNextGoal(org.jactr.core.model.IModel,
   *      VariableBindings)
   */
  @Override
  public IChunk createNextGoal(IModel model,
      VariableBindings variableBindings)
  {
    try
    {
      MockExperiment exp = (MockExperiment) ACTRRuntime.getRuntime()
          .getApplicationData();
      Object[] nextTrial = exp.getTrials().get(exp.getTrialIndex());
      IDeclarativeModule decM = model.getDeclarativeModule();
      IChunkType goalType = decM.getChunkType("key-type-goal").get();
      IChunk nextGoal = decM.createChunk(goalType, "next-goal-" + nextTrial[1])
          .get();
      nextGoal.getSymbolicChunk()
          .addSlot(new BasicSlot("string", nextTrial[1]));

      return nextGoal;
    }
    catch (Exception e)
    {
      LOGGER.error("Could not create goal chunk", e);
      return null;
    }
  }

  /**
   * ok, the stimuli won't be displayed until after the delay has elapsed. So we
   * make the goal available random(0-1) seconds after as a crappy RT surrogate
   * since the model is pathetically simplistic
   * 
   * @see org.jactr.tools.bridge.GoalFeeder#getGoalDelay(org.jactr.core.chunk.IChunk)
   */
  @Override
  public double getGoalDelay(IChunk chunk)
  {
    if (chunk == null) return super.getGoalDelay(chunk);
    MockExperiment exp = (MockExperiment) ACTRRuntime.getRuntime()
        .getApplicationData();
    Object[] nextTrial = exp.getTrials().get(exp.getTrialIndex());
    return 0.05 + Math.random() + (Double) nextTrial[0];
  }

  /**
   * Check the mock experiment to see if there is another trial pending
   */
  @Override
  public boolean hasNextGoal(IModel model, VariableBindings variableBindings)
  {
    MockExperiment exp = (MockExperiment) ACTRRuntime.getRuntime()
        .getApplicationData();
    int trialIndex = exp.getTrialIndex();

    boolean hasNextGoal = (trialIndex < exp.getNumberOfTrials())
        && (trialIndex != _currentTrial);

    if (hasNextGoal) _currentTrial = trialIndex;

    return hasNextGoal;
  }
}
