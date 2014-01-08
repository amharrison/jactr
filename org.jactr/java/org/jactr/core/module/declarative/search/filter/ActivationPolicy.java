package org.jactr.core.module.declarative.search.filter;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;

/*
 * default logging
 */

/**
 * policies that define how components should treat activation. noise, if
 * enabled, is always included
 * 
 * @author harrison
 */
public enum ActivationPolicy {
  /**
   * summing of base, spread
   */
  SUMMATION {
    @Override
    protected double getActivationForPolicy(ISubsymbolicChunk chunk)
    {
      return chunk.getActivation();
    }
  },
  /**
   * just consider the spreading portion
   */
  SPREAD {
    @Override
    protected double getActivationForPolicy(ISubsymbolicChunk chunk)
    {
      return chunk.getRandomActivation() + chunk.getSpreadingActivation();
    }
  },
  /**
   * just consider baselevel
   */
  BASELEVEL {
    @Override
    protected double getActivationForPolicy(ISubsymbolicChunk chunk)
    {
      return chunk.getRandomActivation() + chunk.getBaseLevelActivation();
    }
  },
  /**
   * max(spread, base)
   */
  MAX {
    @Override
    protected double getActivationForPolicy(ISubsymbolicChunk chunk)
    {
      return chunk.getRandomActivation()
          + Math.max(chunk.getBaseLevelActivation(),
          chunk.getSpreadingActivation());
    }
  },
  /**
   * min(spread, base)
   */
  MIN {
    @Override
    protected double getActivationForPolicy(ISubsymbolicChunk chunk)
    {
      return chunk.getRandomActivation()
          + Math.min(chunk.getBaseLevelActivation(),
          chunk.getSpreadingActivation());
    }
  };

  public double getActivation(IChunk chunk)
  {
    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();

    return getActivationForPolicy(ssc);
  }

  abstract protected double getActivationForPolicy(ISubsymbolicChunk chunk);
}
