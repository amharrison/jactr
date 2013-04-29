package org.jactr.core.module.procedural.six.learning;

/*
 * default logging
 */
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.module.procedural.five.learning.IProductionCompiler;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;

public class DefaultProductionCompiler6 implements IProductionCompiler
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultProductionCompiler6.class);
  
  
  static private SortedMap<Integer, Evaluator> _evaluationTable;

  private IInstantiation             _lastInstantiation;
  
  
  
  

  public IProduction productionFired(IInstantiation instantiation,
      IProceduralModule proceduralModule)
  {
    if (_lastInstantiation != null)
    {

    }

    _lastInstantiation = instantiation;
    
    return null;
  }

  protected boolean canCompile(IProduction one, IProduction two,
      ICompilableBuffer buffer)
  {
    String bufferName = buffer.getName();
    boolean oneMatches = matches(one, bufferName);
    boolean twoMatches = matches(two, bufferName);
    boolean oneQueries = queries(one, bufferName);
    boolean twoQueries = queries(two, bufferName);
    boolean oneAdds = adds(one, bufferName);
    boolean twoAdds = adds(two, bufferName);
    boolean oneModifies = modifies(one, bufferName);
    boolean twoModifies = modifies(two, bufferName);
    boolean oneRemoves = removes(one, bufferName);
    boolean twoRemoves = removes(two, bufferName);

    ICompilableContext context = buffer.getCompilableContext();
    boolean isImmediate = context.isImmediate();
    boolean canCompileOut = context.canCompileOut();
    boolean isJammable = context.isJammable();

    boolean rtn = false;

    /*
     * goal/imaginal buffer..
     */
    if (isImmediate)
    {
    }

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug(one + " and " + two + " can" + (rtn ? "" : "'t")
          + " be compiled on " + bufferName);
      LOGGER.debug(bufferName + " isImmediate=" + isImmediate + " isJammable="
          + isJammable + " compiledOut=" + canCompileOut);
      LOGGER.debug(one + " (" + (oneQueries ? "?" : "")
          + (oneMatches ? "=" : "") + ")->(" + (oneAdds ? "+" : "")
          + (oneModifies ? "=" : "") + (oneRemoves ? "-" : ""));
      LOGGER.debug(two + " (" + (twoQueries ? "?" : "")
          + (twoMatches ? "=" : "") + ")->(" + (twoAdds ? "+" : "")
          + (twoModifies ? "=" : "") + (twoRemoves ? "-" : ""));
    }

    return rtn;
  }

  protected boolean queries(IProduction production, String bufferName)
  {
    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof QueryCondition
          && ((QueryCondition) condition).getBufferName().equals(bufferName))
        return true;
    return false;
  }

  protected boolean matches(IProduction production, String bufferName)
  {
    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition
          && !(condition instanceof QueryCondition)
          && ((IBufferCondition) condition).getBufferName().equals(bufferName))
        return true;
    return false;
  }

  protected boolean modifies(IProduction production, String bufferName)
  {
    for (IAction action : production.getSymbolicProduction().getActions())
      if (action instanceof ModifyAction
          && ((ModifyAction) action).getBufferName().equals(bufferName))
        return true;
    return false;
  }

  protected boolean adds(IProduction production, String bufferName)
  {
    for (IAction action : production.getSymbolicProduction().getActions())
      if (action instanceof AddAction
          && ((AddAction) action).getBufferName().equals(bufferName))
        return true;
    return false;
  }

  protected boolean removes(IProduction production, String bufferName)
  {
    for (IAction action : production.getSymbolicProduction().getActions())
      if (action instanceof RemoveAction
          && ((RemoveAction) action).getBufferName().equals(bufferName))
        return true;
    return false;
  }
  
  
  static private class Evaluator
  {
    
    static public Evaluator TRUE = new Evaluator(){
      public boolean couldCompile(IProduction one, IProduction two)
      {
        return true;
      }
    };
    
    static public Evaluator FALSE = new Evaluator(){
      public boolean couldCompile(IProduction one, IProduction two)
      {
        return false;
      }
    };
    
    public boolean couldCompile(IProduction one, IProduction two)
    {
      return false;
    }
  }
}
