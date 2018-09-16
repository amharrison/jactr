package org.jactr.core.module.procedural.six.learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.module.procedural.six.learning.DefaultProductionCompiler6.BufferStruct;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.action.SetAction;
import org.jactr.core.production.condition.ChunkCondition;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.condition.VariableCondition;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;

public class ProductionCompilerEvaluator {
	static private final transient Log LOGGER = LogFactory.getLog(ProductionCompilerEvaluator.class);

	public static Integer empty = 0;
	public static Integer match = 8;
	public static Integer query = 16;
	public static Integer modify = 1;
	public static Integer add = 4;
	public static Integer remove = 2;
	public static Integer[] bufferActions = {empty, match, query, modify, add, remove};
	
	private Evaluator[][] simpleEvaluatorTable;
	private Evaluator[][] retrievalEvaluatorTable;
	private Evaluator[][] perceptualEvaluatorTable;
	
	private Mapper[][] simpleMapperTable;
	private Mapper[][] retrievalMapperTable;
	private Mapper[][] perceptualMapperTable;
	
	private Collapser[][] simpleCollapserTable;
	private Collapser[][] retrievalCollapserTable;
	private Collapser[][] perceptualCollapserTable;
	
	ProductionCompilerEvaluator() {
		initializeMaps(bufferActions);
	}
	

	//simple type: immediate, deterministic, can't jam ( so can have two requests in a row ) and can't compile out
	private boolean isSimpleType(ICompilableContext context, IRequest req) {
		  if(context.isImmediate(req) && context.isDeterministic(req) && !context.isJammable(req) && !context.canCompileOut(req)) return true;
		  return false;
	  }
	  
	  private boolean isRetrievalTypeBuffer(ICompilableContext context, IRequest req) {
		  if(!context.isImmediate(req) && !context.isDeterministic(req) && !context.isJammable(req) && context.canCompileOut(req)) return true;
		  return false;
	  }
	  
	  private boolean isPerceptualTypeBuffer(ICompilableContext context, IRequest req) {
		  if(!context.isImmediate(req) && !context.isDeterministic(req) && context.isJammable(req) && !context.canCompileOut(req)) return true;
		  return false;
	  }
	  
	public boolean canCompile(BufferStruct one, BufferStruct two, ICompilableBuffer buffer) {
		 LOGGER.debug("indices are " + one.index + " and " + two.index);
		 if(isSimpleType(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking goal type compilation map for indices " + one.index + "," + two.index);
    return simpleEvaluatorTable[one.index][two.index].canCompile(one, two, buffer.getName());
		  if(isRetrievalTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking retrieval type compilation map for indices " + one.index + "," + two.index);
      return retrievalEvaluatorTable[one.index][two.index].canCompile(one, two, buffer.getName());
		  if(isPerceptualTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking perceptual type compilation map for indices " + one.index + "," + two.index);
      return perceptualEvaluatorTable[one.index][two.index].canCompile(one, two, buffer.getName());
		  
		return false;
	}
	
	 public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, ICompilableBuffer buffer) {
		 if(isSimpleType(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking goal type mapper table for indices " + one.index + "," + two.index);
    return simpleMapperTable[one.index][two.index].extractMap(one, two, buffer.getName());
		  
		  if(isRetrievalTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking retrieval type compilation map for indices " + one.index + "," + two.index);
      return retrievalMapperTable[one.index][two.index].extractMap(one, two, buffer.getName());
		  if(isPerceptualTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) //LOGGER.debug("checking perceptual type compilation map for indices " + one.index + "," + two.index);
      return perceptualMapperTable[one.index][two.index].extractMap(one, two, buffer.getName());
		return null;
	 }
	 
	 public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, ICompilableBuffer buffer) {
		 if(isSimpleType(buffer.getCompilableContext(), one.getIRequest())) return simpleCollapserTable[one.index][two.index].collapseBuffer(one, two, buffer.getName());
		 
		 if(isRetrievalTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) return retrievalCollapserTable[one.index][two.index].collapseBuffer(one, two, buffer.getName());
		 
		 if(isPerceptualTypeBuffer(buffer.getCompilableContext(), one.getIRequest())) return perceptualCollapserTable[one.index][two.index].collapseBuffer(one, two, buffer.getName());
		 return null;
	 }
	//define this class for evaluator functions; if you need to extend buffers/actions, can subclass off of ProductionCompilerEvaluator and just ProductionCompiler6.add more of these.
	  protected abstract static class Evaluator {
		  public abstract boolean canCompile(BufferStruct one, BufferStruct two, String bufferName);
		  
	    static public Evaluator TRUE = new Evaluator(){
		      @Override
          public boolean canCompile(BufferStruct one, BufferStruct two, String bufferName)
		      {
		    	//LOGGER.debug("Evaluator returning true by default.");
		        return true;
		      }
		    };
		    
		    static public Evaluator FALSE = new Evaluator(){
		      @Override
          public boolean canCompile(BufferStruct one, BufferStruct two, String bufferName)
		      {
		    	//LOGGER.debug("Evaluator returning false by default.");
		        return false;
		      }
		    };
	  }
	  
	  //this class provides mappings from the variables of the first production to the variables/values of the second on a per-buffer basis.
	  protected abstract static class Mapper {
		  public abstract Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName);
		  
		  static public Mapper ERROR = new Mapper() {
			  @Override
        public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
				  return null;
			  }
		  };
		  
		  static public Mapper EMPTY = new Mapper() {
			  @Override
        public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
				  return new HashMap<String, Object>();
			  }
		  };
		  
		  static public Map<String, Object> match(Collection<? extends ISlot> slots1, Collection<? extends ISlot> slots2, VariableBindings instantiationTwo) {
			  
			  Map<String, Object> map = new HashMap<String, Object>();
			  for(ISlot s2 : slots2)
          for(ISlot s1 : slots1)
            //LOGGER.debug("Mapper.match comparing " + s2 + ", " + s1);
					  if(s1.getName().equals(s2.getName()) && ((IConditionalSlot)s1).getCondition() == ((IConditionalSlot)s2).getCondition()) if(s2.isVariableValue())
              map.put(s2.getValue().toString(), s1.getValue());
            else if(s1.isVariableValue())
              map.put(s1.getValue().toString(), s2.getValue());
            else {
              //don't map, neither is a variable.
            }
			  	 
			  return map;
		  }
	  }
	  
	  protected abstract static class Collapser {
		  public abstract BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName);
		  
		  static public Collapser ERROR = new Collapser() {
			  @Override
        public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
				  return null;
			  }
		  };
		  
		  static public Collapser EMPTY = new Collapser() {
			  @Override
        public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
				  return new BufferStruct(bufferName, one.hasStrictHarvesting(), null);
			  }
		  };
		  
		  static public void findQueryCondition(BufferStruct one, BufferStruct two, BufferStruct newStruct) {
			  boolean addAction = false;
			 ICondition secondQuery = null;
			 for(ICondition c : one.getConditions()) {
				  if(c instanceof QueryCondition) {
					  newStruct.conditions.add(clone(c));
					  return;
				  }
				  if(c instanceof AddAction) addAction = true;
			  }
			  
			  //now, query in #2 and whether there is add in #2
			  if(addAction) return;
			  
			  for(ICondition c : two.getConditions())
          if(c instanceof QueryCondition) secondQuery = c;
			  
			  // at this point we know whether there is an add action/query in #2 and that there is def. not a query in buffer one.
			  if(secondQuery == null) return;
			  
			  //now, def. no add action and no query condition in #1 but query condition in #2
			  newStruct.conditions.add(clone(secondQuery));
		  }
		  
		  //s1 - s2 (by slot name)
		  //adds all slots in s1 that are not in s2 to the result
    static List<ISlot> slots = Lists.mutable.empty();
		  static public boolean minusSlots(ISlotContainer sc1, ISlotContainer sc2, ISlotContainer result) {
			  slots.clear();
			  if(sc1 != null) sc1.getSlots(slots);
			  for(ISlot s1 : slots) {
				  boolean mentioned = false;
				  if(sc2 != null) for(ISlot s2 : sc2.getSlots()) {
            if(((IConditionalSlot)s2).getCondition() != IConditionalSlot.EQUALS) {
          	  LOGGER.warn("Encountered unexpected case where action assignment function is not equals... not compiling " + sc1);
          	  return false;
            }
            if(s1.getName().equals(s2.getName()) && ((IConditionalSlot)s1).getCondition() == IConditionalSlot.EQUALS) //both are equals functions.  match up variables.
            mentioned = true;
          }
				  if(!mentioned) result.addSlot(s1);
			  }
			  return true;
		  }
		  
		  //adds all slots that are in sc1 that aren't in sc2 to sc2
		  static public boolean unionSlots(ISlotContainer sc1, ISlotContainer sc2) {
			  slots.clear();
			  if(sc1 == null) return true;
			  sc2.getSlots(slots); //sc2 should never be null
			  for(ISlot s1 : sc1.getSlots()) {
				  boolean matched = false;
				  for(ISlot s2 : slots)
            if(s1.getName().equals(s2.getName()) && ((IConditionalSlot)s1).getCondition() == IConditionalSlot.EQUALS
							  && s1.getValue().equals(s2.getValue())) matched = true;
				  if(!matched) sc2.addSlot(s1);
			  }
			  return true;
		  }
		  
		  static public boolean calcNewCondition(ISlotContainer c1, ISlotContainer c2, ISlotContainer a1, ISlotContainer newCondition) {
			//According to ACT-R, newC = C1 + (C2 - A1)
			  
			 if(!minusSlots(c2, a1, newCondition)) return false;
			 //LOGGER.debug("Conditions one is " + c1 + " and two is " + c2+ " and slots is " + slots);
			 if(!unionSlots(c1, newCondition)) return false;
			 //for(ISlot s : slots) newCondition.addSlot(s);
			 //LOGGER.debug("Found new condition " + newCondition);
			 
			 return true;
		  }
		  
		  static public boolean calcNewAction(ISlotContainer a1, ISlotContainer a2, ISlotContainer newAction) {
			  //                    newA = A1 overwritten by A2
			  //LOGGER.debug("calculating new action from " + a1.getSlots() + " and " + a2.getSlots());
			  slots.clear();
			  a2.getSlots(slots);
			  for(ISlot s : slots) newAction.addSlot(s);
			  //LOGGER.debug("slots from second action are " + newAction.getSlots());
			  if(minusSlots(a1, a2, newAction)) //LOGGER.debug(" and once added in from first action " + newAction.getSlots());
        return true;
			  return false;
		  }
		  
		  static public BufferStruct cloneProduction(BufferStruct toClone, String bufferName) {
			  BufferStruct newStruct = new BufferStruct(bufferName, toClone.hasStrictHarvesting(), toClone.getVariableBindings());
			  
			  for(ICondition condition : toClone.getConditions()) {
				  ICondition cloned = clone(condition);
				  if(cloned != null) newStruct.conditions.add(cloned);
				  else return null;
			  }
			  for(IAction action : toClone.getActions()) {
				  IAction cloned = clone(action);
				  if(cloned != null) newStruct.actions.add(cloned);
				  else return null;
			  }
		  
			  return newStruct;
		  }
		  
		  //clones a condition so that the arraylist of slots is a different object.
		  //doesn't create new slot objects.
		  static public ICondition clone(ICondition condition) {
			  if(condition instanceof QueryCondition)
          return ((QueryCondition)condition).clone();
        else if (condition instanceof VariableCondition)
          return new VariableCondition(((VariableCondition)condition).getBufferName(), ((VariableCondition)condition).getVariableName());
        else if (condition instanceof ChunkTypeCondition && !(condition instanceof ChunkCondition))
          return new ChunkTypeCondition(((ChunkTypeCondition)condition).getBufferName(), ((ChunkTypeCondition)condition).getChunkType(),
						  ((ChunkTypeCondition)condition).getSlots());
        else {
				  LOGGER.debug("don't know how to clone/handle condition of instance " + condition);
				  return null;
			  }
		  }
		 
		  
		  //clones an action so that the arraylist of slots is a different object.
		  //doesn't create new slot objects.
		  static public IAction clone(IAction action) {
			  if(action instanceof RemoveAction)
          return new RemoveAction(((RemoveAction)action).getBufferName());
        else if (action instanceof ModifyAction)
          return new ModifyAction(((ModifyAction)action).getBufferName(), ((ModifyAction)action).getSlots());
        else if (action instanceof AddAction)
          return new AddAction(((AddAction)action).getBufferName(), ((AddAction)action).getReferant(), ((AddAction)action).getSlots());
        else if (action instanceof SetAction)
          return new SetAction(((SetAction)action).getBufferName(), ((SetAction)action).getReferant(), ((SetAction)action).getSlots());
        else {
				  LOGGER.debug("don't know how to clone/handle action of instance " + action);
				  return null;
			  }
		  }
	  }
	
	  
	  //FIXME: needs to check across ALL buffers, not just this one.
	  static public Evaluator no_rhs_reference = new Evaluator(){
		  @Override
      public boolean canCompile(BufferStruct one, BufferStruct two, String bufferName)
		  {
			  //can't compile if the buffer variable is used on the RHS of p2
			  for(IAction action : two.getActions())
          if(action instanceof ModifyAction) {
					  for(ISlot s : ((ModifyAction)action).getSlots())
              if(s.getValue().toString().equals("="+bufferName)) {
							  LOGGER.debug("Evaluator no_rhs_reference returning false because of action " + action + " and slot " + s);
							  return false;
						  }
				  } else if (action instanceof AddAction) {
					  if (((AddAction)action).getReferant().toString().equals("="+bufferName)) return false;
					  for(ISlot s : ((AddAction)action).getSlots())
              if(s.getValue().toString().equals("="+bufferName)) {
							  LOGGER.debug("Evaluator no_rhs_reference returning false because of action " + action + " and slot " + s);
							  return false;
						  }
				  }
				  else if(action instanceof SetAction) {
					  if (((SetAction)action).getReferant().toString().equals("="+bufferName)) return false;
					  for(ISlot s : ((SetAction)action).getSlots())
              if(s.getValue().toString().equals("="+bufferName)) {
							  LOGGER.debug("Evaluator no_rhs_reference returning false because of action " + action + " and slot " + s);
							  return false;
						  }
				  } //don't need to do RemoveAction separately because it subclassess off of ModifyAction
			  return true;
		  }
	  };
	  
	  static public Evaluator p2_busy_or_empty = new Evaluator(){
		  @Override
      public boolean canCompile(BufferStruct one, BufferStruct two, String bufferName)
		  {
			  //second production ProductionCompiler6.query must be "state busy" or "buffer ProductionCompiler6.empty"
			  for(ICondition condition : two.getConditions())
          if (condition instanceof QueryCondition
				          && ((QueryCondition) condition).getBufferName().equals(bufferName)) for(ISlot s : ((QueryCondition) condition).getSlots())
            if(s.getName().equals("state")) {
          	  if(!s.getValue().toString().equals("busy")) {
          		  LOGGER.debug("Evaluator busy_or_ProductionCompiler6.empty returning false because of condition " + condition + " and slot " + s);
          		  return false;
          	  }
            } else if(s.getName().equals("buffer")) {
          	  if(!s.getValue().toString().equals("ProductionCompiler6.empty")) {
          		  LOGGER.debug("Evaluator busy_or_ProductionCompiler6.empty returning false because of condition " + condition + " and slot " + s);
          		  return false;
          	  }
            } else {
          	  LOGGER.debug("Evaluator busy_or_ProductionCompiler6.empty returning false because of condition " + condition + " and slot " + s);
          	  return false;
            }
			  return false;
		  }
		  
	  };
	  
	  static public Evaluator same_queries = new Evaluator(){
		  @Override
      public boolean canCompile(BufferStruct one, BufferStruct two, String bufferName)
		  {
			  //both queries must be the same.
			  ArrayList<ISlot> query1 = new ArrayList<ISlot>();
			  ArrayList<ISlot> query2 = new ArrayList<ISlot>();
			  
			  for(ICondition condition : one.getConditions())
          if (condition instanceof QueryCondition
				          && ((QueryCondition) condition).getBufferName().equals(bufferName)) query1.addAll(((QueryCondition) condition).getSlots());
			  
			  for(ICondition condition : two.getConditions())
          if (condition instanceof QueryCondition
				          && ((QueryCondition) condition).getBufferName().equals(bufferName)) query2.addAll(((QueryCondition) condition).getSlots());
			  
			  for(ISlot s1 : query1) {
				  for(ISlot s2 : query2)
            if(s1.getName().equals(s2.getName()) && s1.equalValues(s2)) {
						  query2.remove(s2);
						  break; //break out of loop to advance to the next s1
					  }
				  //if we get here, no ProductionCompiler6.match!!
				  LOGGER.debug("Evaluator same_queries returning false because of unmatched slot " + s1 + " from production one");
				  return false;
			  }
			  if(query2.size() > 0) {
				  LOGGER.debug("Evaluator same_queries returning false because of unmatched slot(s) " + query2 + " from production two");
				  return false; //unProductionCompiler6.matched item in ProductionCompiler6.query2
			  }
			  
			  return true;
		  }
		  
	  };
	  
/*	  @Deprecated
	  //should be handeled by mapper_no_add_p1...
	  static public Mapper mapper_match_and_match = new Mapper() {
		  public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
			  //just match the variables from the conditions of the first one to the conditions of the second one.
			  if(one.getConditions().size() > 1 || two.getConditions().size() > 1) {
				   LOGGER.warn("Should not be here in ProductionCompilerEvaluator.Mapper.matches_and_match; more than one condition for one of the productions." + one.getConditions() + "," + two.getConditions());
				   return null;
			  }
			  
			  return Mapper.match(((ISlotContainer)one.conditions.iterator().next()).getSlots(), ((ISlotContainer)two.conditions.iterator().next()).getSlots(), two.getVariableBindings());
		  }
	  };*/
	  
	  //TODO: this (and others) assumes no remove.  but I guess that is reasonable in this case, otherwise wouldn't need mapping.
	  static public Mapper mapper_no_add_p1 = new Mapper() {
		  @Override
      public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
			  if(one.getConditions().size() > 1 || one.getActions().size() > 1 || two.getConditions().size() > 1) {
				   LOGGER.warn("Should not be here in ProductionCompilerEvaluator.Mapper.match_no_add_p1; more than one condition or action for one of the productions." + one.getConditions() + "," + one.getActions() + "," + two.getConditions());
				   return null;
			  }
			  
			  //fold conditions into actions for a good snapshot
			  if(one.conditions.size() > 0 && one.actions.size() > 0) {
				  if(!Collapser.minusSlots((ISlotContainer)one.conditions.iterator().next(), (ISlotContainer)one.actions.iterator().next(), (ISlotContainer)one.actions.iterator().next())) return null;
				  return Mapper.match(((ISlotContainer)one.actions.iterator().next()).getSlots(), ((ISlotContainer)two.conditions.iterator().next()).getSlots(), two.getVariableBindings());
			  } else if(one.conditions.size() > 0)
          //if only condition in first production
				  return Mapper.match(((ISlotContainer)one.conditions.iterator().next()).getSlots(), ((ISlotContainer)two.conditions.iterator().next()).getSlots(), two.getVariableBindings());
        else {
				  //only action in first production
				  LOGGER.warn("Should not be here in ProductionCompilerEvaluator.Mapper.match_no_add_p1 - only actions (not conditions)  p1, yet not an add?");
				  return null;
			  }
		  }
	  };
	  
	  static public Mapper mapper_add_p1 = new Mapper() {
		  @Override
      public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
			  ICondition conditionTwo = null;
			  for(ICondition condition : two.getConditions())
          if(!(condition instanceof QueryCondition)) {
					  if(conditionTwo != null) {
						  LOGGER.warn("somehow got to mapper_add_p1 incorrectly... more than one non-query conditionTwo " + two.getConditions());
						  return null;
					  }
					  conditionTwo = condition;
				  }
			  
			  ISlotContainer add1 = null;
			  for(IAction a1 : one.getActions())
          if(a1 instanceof AddAction) {
					  add1 = (ISlotContainer) a1;
					  break;
				  }
			  if(add1 == null)  {
				  LOGGER.warn("Should not be here in ProductionCompilerEvaluator.Mapper.match_add_p1; no add action." + one.getConditions() + "," + one.getActions() + "," + two.getConditions());
				  return null;
			  }
        else
          return Mapper.match(add1.getSlots(), ((ISlotContainer)conditionTwo).getSlots(), two.getVariableBindings());
		  }
	  };
	  

	 //Not sure this needs to be different from mapper_add_p1, but it is in lisp's ACT-R documentation, so I'll leave it for now.
	  static public Mapper mapper_nondeterministic_add_p1 = new Mapper() {
		  @Override
      public Map<String, Object> extractMap(BufferStruct one, BufferStruct two, String bufferName) {
			  //need to relate add action, chunk retrieved, and match condition
			  
			  Map<String, Object> map = new HashMap<String, Object>();
			  for(IAction a : one.getActions())
          if(a instanceof AddAction) for(ISlot s : ((AddAction)a).getSlots()) {
            if(s.getName().toString().startsWith("=")) {
          	  LOGGER.warn("variable slot name in retrieval add action; can't handle " + a + "," + s);
          	  return null;
            }
            if(s.isVariableValue()) map.put(s.getValue().toString(), one.bindings.get(s.getValue().toString()));
          }
			  
			  for(ICondition c : two.getConditions())
          if(c instanceof ChunkTypeCondition) for(ISlot s : ((ChunkTypeCondition)c).getSlots())
            if(s.isVariableValue()) map.put(s.getValue().toString(), two.bindings.get(s.getValue().toString()));
			  
			  return map;
		  }
	  };
	  
	  
	  //things like [match][match+modify] or [match+modify][match+modify+add]
	  static public Collapser collapse_no_add_p1 = new Collapser() {
		  @Override
      public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  BufferStruct newStruct = new BufferStruct(bufferName, one.hasStrictHarvesting(), null);
			  
			  findQueryCondition(one, two, newStruct);


			  ICondition conditionOne = null;
			  ICondition conditionTwo = null;
			  for(ICondition condition : one.getConditions())
          if(!(condition instanceof QueryCondition)) {
					  if(conditionOne != null) {
						  LOGGER.warn("something is wrong in collapse_no_add_in_p1... more than one non-query conditionOne.");
						  return null;
					  }
					  conditionOne = condition;
				  }
			  for(ICondition condition : two.getConditions())
          if(!(condition instanceof QueryCondition)) {
					  if(conditionTwo != null) {
						  LOGGER.warn("something is wrong in collapse_no_add_in_p1... more than one non-query conditionTwo.");
						  return null;
					  }
					  conditionTwo = condition;
				  }

			  if(one.getActions().size() > 2) {
				  LOGGER.warn("something is wrong in collapse_no_add_in_p1... more than two actionOnes (and we know there is no add).");
				  return null;
			  }
			  
			  if(one.getActions().size() > 0) {
				  //LOGGER.debug("Conditions one is " + one.getConditions() + " and two is " + two.getConditions());
				  if(conditionOne != null || conditionTwo != null) {
					  ChunkTypeCondition match = new ChunkTypeCondition(bufferName, ((ChunkTypeCondition)conditionOne).getChunkType());
					  if(!calcNewCondition((ISlotContainer)conditionOne, (ISlotContainer)conditionTwo, (ISlotContainer)one.getActions().iterator().next(), match)) return null;
					  newStruct.conditions.add(match);
				  }
				  
				   for(IAction a1 : one.getActions())
            if(a1 instanceof ModifyAction) {
						  boolean modded = false;
						  for(IAction a2 : two.getActions())
                if(a2 instanceof ModifyAction) {
								  modded = true;
								  ModifyAction mod = new ModifyAction(bufferName);
								  if(!calcNewAction((ISlotContainer)a1, (ISlotContainer)a2, mod)) return null;
								  newStruct.actions.add(mod);
							  }
                else
                  newStruct.actions.add(clone(a2));
						  if(!modded) newStruct.actions.add(clone(a1));
					  } else if(a1 instanceof RemoveAction)
              newStruct.actions.add(clone(a1));
            else {
						  LOGGER.warn("uncaught case - something other than a modify (and we know it's not an add) in production one's action " + one.getActions());
						  return null;
					  }
			  } else {
				  //LOGGER.debug("Conditions one is " + one.getConditions() + " and two is " + two.getConditions());
				  if(conditionOne != null || conditionTwo != null) {
					  ChunkTypeCondition match = conditionOne != null ? new ChunkTypeCondition(bufferName, ((ChunkTypeCondition)conditionOne).getChunkType())
					  	: new ChunkTypeCondition(bufferName, ((ChunkTypeCondition)conditionTwo).getChunkType());
					  if(!calcNewCondition((ISlotContainer)conditionOne, (ISlotContainer)conditionTwo, null, match)) return null;
					  newStruct.conditions.add(match);
				  }
				  
				  for(IAction a2 : two.getActions())
            newStruct.actions.add(clone(a2));
			  }
			  

			  

			  return newStruct;
		  }
	  };
	  
	  //we can assume this won't ever get called if strict harvesting is relevant / enabled.
	  static public Collapser collapse_add_p1 = new Collapser() {
		  @Override
      public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  BufferStruct newStruct = new BufferStruct(bufferName, one.hasStrictHarvesting(), null);
			  findQueryCondition(one, two, newStruct);
			  for(ICondition condition : one.getConditions())
          if(!(condition instanceof QueryCondition)) newStruct.getConditions().add(clone(condition));
			  
			  AddAction plusOne = null;
			  IAction modTwo = null;
			  
			  for(IAction action : one.getActions())
          if(action instanceof ModifyAction)
            newStruct.getActions().add(clone(action));
          else if (action instanceof AddAction) plusOne = (AddAction) action;
			  
			  for(IAction action : two.getActions())
          if(action instanceof ModifyAction)
            modTwo = action;
          else {
					  LOGGER.warn("encountered unhandled case of non-modify action " + action);
					  return null;
				  }
			  
			  if(plusOne == null) {
				  LOGGER.warn("encountered unhandled case of no plus case in p1 " + one.getActions());
				  return null;
			  }
			  if(modTwo != null) {
						  AddAction add = new AddAction(bufferName, plusOne.getChunkType());
						  if(!calcNewAction((ISlotContainer)plusOne, (ISlotContainer)modTwo, add)) return null;
						  newStruct.actions.add(add);
			  }
        else
          newStruct.getActions().add(clone(plusOne));
			  
			  return newStruct;
		  }
	  };
	  
/*	  @Deprecated
	  static public Collapser collapse_add_p1_check_sh = new Collapser() {
		  public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  if(one.hasStrictHarvesting()) {
				  for(Iterator<IAction> it = one.getActions().iterator(); it.hasNext(); ) {
					  IAction action = it.next();
					  if(action instanceof AddAction) {
						  it.remove();
					  }
				  }
				  two.getActions().add(new RemoveAction(bufferName));
				  return collapse_no_add_p1.collapseBuffer(one, two, bufferName);
			  } else return collapse_add_p1.collapseBuffer(one, two, bufferName);
			  
		  }
	  };*/
	  
/*	  @Deprecated
	  //sh stands for "strict harvesting
	  //according to tony, this is deprecated
	  static public Collapser collapse_no_add_p1_check_sh = new Collapser() {
		  public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  if(one.hasStrictHarvesting()) {
				  one.getActions().add(new RemoveAction(bufferName));
			  }
			  return collapse_no_add_p1.collapseBuffer(one, two, bufferName);
		  }
	  };*/
	  
/*	  @Deprecated
	  //if something in retrieval from our add, and no strict harvesting, we can't combine
	  static public Collapser collapse_retrieval_add_p1_check_sh = new Collapser() {
		  public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  if(!one.hasStrictHarvesting()) return null;
			  return collapse_nondeterministic_add_p1.collapseBuffer(one, two, bufferName);
		  }
	  };*/
	  
	 static public Collapser collapse_compileout_add_p1 = new Collapser() {
		  @Override
      public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  BufferStruct newStruct = new BufferStruct(bufferName, one.hasStrictHarvesting(), null);
			  
			  findQueryCondition(one, two, newStruct);
			  for(ICondition c : one.getConditions())
          if(!(c instanceof QueryCondition)) newStruct.conditions.add(clone(c));
			  
			  //exclude modify actions
			  for(IAction a : two.getActions())
          if(a instanceof ModifyAction && !(a instanceof RemoveAction)) {
					  LOGGER.warn("Encountered a modify action in action 2 when collapsing retrieval buffer - this is illegal." + two.getActions() + "," + a);
					  return null;
				  } else if (a instanceof AddAction || a instanceof RemoveAction) newStruct.actions.add(clone(a));
			  
			  return newStruct;
		  }
	  };
	  
	  //p2 is fired before chunk is actually retrieved
	  //p2 has a query or nothing, p1 has an add and whatever else.
	 static public Collapser collapse_nonimmediate_add_p1_not_harvested = new Collapser() {
		  @Override
      public BufferStruct collapseBuffer(BufferStruct one, BufferStruct two, String bufferName) {
			  BufferStruct newStruct = new BufferStruct(bufferName, one.hasStrictHarvesting(), null);
			  
			  for(ICondition condition : two.getConditions())
          if(!(condition instanceof QueryCondition)) {
					  LOGGER.warn("somehow got to collapse_retrieval_add_p1_not_harvested incorrectly... got a non-query conditionTwo " + two.getConditions());
					  return null;
				  }
			  
			  findQueryCondition(one, two, newStruct);
			  for(ICondition c : one.getConditions())
          if(!(c instanceof QueryCondition)) newStruct.conditions.add(clone(c));
			  
			  for(IAction a : one.getActions())
          if (a instanceof AddAction) newStruct.actions.add(clone(a));
			  
			  return newStruct;
		  }
	  };
	  
	  
	  private void initializeMaps(Integer bufferActions[]) {
		  //initialize all values to false, just in case
		  simpleEvaluatorTable = new Evaluator[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  retrievalEvaluatorTable = new Evaluator[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  perceptualEvaluatorTable = new Evaluator[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  simpleMapperTable = new Mapper[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  retrievalMapperTable = new Mapper[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  perceptualMapperTable = new Mapper[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  simpleCollapserTable = new Collapser[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  retrievalCollapserTable = new Collapser[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  perceptualCollapserTable = new Collapser[(int) (Math.pow(2,bufferActions.length)-1)][(int) Math.pow(2,bufferActions.length)-1];
		  
		  
		  for(int i = 0; i < Math.pow(2,bufferActions.length)-1; i++)
        for(int j = 0; j < Math.pow(2,bufferActions.length)-1; j++) {
				  simpleEvaluatorTable[i][j] = Evaluator.FALSE;
				  retrievalEvaluatorTable[i][j] = Evaluator.FALSE;
				  perceptualEvaluatorTable[i][j] = Evaluator.FALSE;
				  simpleMapperTable[i][j] = Mapper.ERROR;
				  retrievalMapperTable[i][j] = Mapper.ERROR;
				  perceptualMapperTable[i][j] = Mapper.ERROR;
				  simpleCollapserTable[i][j] = Collapser.ERROR;
				  retrievalCollapserTable[i][j] = Collapser.ERROR;
				  perceptualCollapserTable[i][j] = Collapser.ERROR;
			  }
		  
		  //collapse equivalent rows/columns to save time
		  Object[][][] tableArray = {simpleEvaluatorTable, simpleMapperTable, simpleCollapserTable, retrievalEvaluatorTable, retrievalMapperTable, retrievalCollapserTable, perceptualEvaluatorTable, perceptualMapperTable, perceptualCollapserTable};
		  for(int i = 0; i < tableArray.length; i++) {
			  //first do rows
			  tableArray[i][add+remove] = tableArray[i][add];
			  tableArray[i][match+add+remove] = tableArray[i][match+add];
			  tableArray[i][match+modify+add+remove] = tableArray[i][match+modify+add];
			  tableArray[i][query+add+remove] = tableArray[i][query+add];
			  tableArray[i][match+query+add+remove] = tableArray[i][match+query+add];
			  tableArray[i][match+query+modify+remove+add] = tableArray[i][match+query+modify+add];
			  
			  //now columns
			  for(int j = 0; j <Math.pow(2, bufferActions.length)-1; j++) {
				  tableArray[i][j][add+remove] = tableArray[i][j][add];
				  tableArray[i][j][match+add+remove] = tableArray[i][j][match+add];
				  tableArray[i][j][match+modify+add+remove] = tableArray[i][j][match+modify+add];
				  tableArray[i][j][query+add+remove] = tableArray[i][j][query+add];
				  tableArray[i][j][match+query+add+remove] = tableArray[i][j][match+query+add];
				  tableArray[i][j][match+query+modify+remove+add] = tableArray[i][j][match+query+modify+add];
			  }
		  }

		  
		  
		  //now begins the long, arduous process of filling these in... not the prettiest way of doing it,
		  //but sure makes readability/understanding much better.
		  
		  simpleEvaluatorTable[empty][match] = Evaluator.TRUE;
		  simpleEvaluatorTable[empty][add] = Evaluator.TRUE;
		  simpleEvaluatorTable[empty][remove] = Evaluator.TRUE;
		  simpleEvaluatorTable[empty][match+modify] = Evaluator.TRUE;
		  simpleEvaluatorTable[empty][match+add] = Evaluator.TRUE;
		  simpleEvaluatorTable[empty][match+modify+add] = Evaluator.TRUE;
		  
		  simpleMapperTable[empty][match] = Mapper.EMPTY;
		  simpleMapperTable[empty][add] = Mapper.EMPTY;
		  simpleMapperTable[empty][remove] = Mapper.EMPTY;
		  simpleMapperTable[empty][match+modify] = Mapper.EMPTY;
		  simpleMapperTable[empty][match+add] = Mapper.EMPTY;
		  simpleMapperTable[empty][match+modify+add] = Mapper.EMPTY;
		  
		  simpleCollapserTable[empty][match] = collapse_no_add_p1;
		  simpleCollapserTable[empty][add] = collapse_no_add_p1;
		  simpleCollapserTable[empty][remove] = collapse_no_add_p1;
		  simpleCollapserTable[empty][match+modify] = collapse_no_add_p1;
		  simpleCollapserTable[empty][match+add] = collapse_no_add_p1;
		  simpleCollapserTable[empty][match+modify+add] = collapse_no_add_p1;
		  
		  
		  simpleEvaluatorTable[match][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][match] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][add] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][remove] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][match+modify] = Evaluator.TRUE; //tested
		  simpleEvaluatorTable[match][match+add] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][match+remove] = Evaluator.TRUE;
		  simpleEvaluatorTable[match][match+modify+add] = Evaluator.TRUE;
		  
		  simpleMapperTable[match][empty] = Mapper.EMPTY;
		  simpleMapperTable[match][match] = mapper_no_add_p1;
		  simpleMapperTable[match][add] = Mapper.EMPTY;
		  simpleMapperTable[match][remove] = Mapper.EMPTY;
		  simpleMapperTable[match][match+modify] = mapper_no_add_p1; //tested
		  simpleMapperTable[match][match+add] = mapper_no_add_p1;
		  simpleMapperTable[match][match+remove] = mapper_no_add_p1;
		  simpleMapperTable[match][match+modify+add] = mapper_no_add_p1;
		  
		  simpleCollapserTable[match][empty] = collapse_no_add_p1;
		  simpleCollapserTable[match][match] = collapse_no_add_p1;
		  simpleCollapserTable[match][add] = collapse_no_add_p1;
		  simpleCollapserTable[match][remove] = collapse_no_add_p1;
		  simpleCollapserTable[match][match+modify] = collapse_no_add_p1; //tested
		  simpleCollapserTable[match][match+add] = collapse_no_add_p1;
		  simpleCollapserTable[match][match+remove] = collapse_no_add_p1;
		  simpleCollapserTable[match][match+modify+add] = collapse_no_add_p1;
		  
		  simpleEvaluatorTable[add][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[add][match] = no_rhs_reference;
		  simpleEvaluatorTable[add][match+modify] = no_rhs_reference;
		  
		  simpleMapperTable[add][empty] = Mapper.EMPTY;
		  simpleMapperTable[add][match] = mapper_add_p1;
		  simpleMapperTable[add][match+modify] = mapper_add_p1;
		  
		  simpleCollapserTable[add][empty] = collapse_add_p1;
		  simpleCollapserTable[add][match] = collapse_add_p1;
		  simpleCollapserTable[add][match+modify] = collapse_add_p1;
		  
		  
		  simpleEvaluatorTable[remove][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[remove][remove] = Evaluator.TRUE;
		  
		  simpleMapperTable[remove][empty] = Mapper.EMPTY;
		  simpleMapperTable[remove][remove] = Mapper.EMPTY;
		  
		  simpleCollapserTable[remove][empty] = collapse_no_add_p1;
		  simpleCollapserTable[remove][remove] = collapse_no_add_p1;
		  
		  
		  simpleEvaluatorTable[query][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[query][add] = Evaluator.TRUE;
		  
		  simpleMapperTable[query][empty] = Mapper.EMPTY;
		  simpleMapperTable[query][add] = Mapper.EMPTY;
		  
		  simpleCollapserTable[query][empty]  = collapse_no_add_p1;
		  simpleCollapserTable[query][add] = collapse_no_add_p1;
		  
		  
		  simpleEvaluatorTable[match+modify][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][match] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][add] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][remove] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][match+modify] = Evaluator.TRUE; //tested
		  simpleEvaluatorTable[match+modify][match+add] = Evaluator.TRUE; //tested
		  simpleEvaluatorTable[match+modify][match+remove] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][match+modify+add] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify][match+modify+remove] = Evaluator.TRUE;
		  
		  simpleMapperTable[match+modify][empty] = Mapper.EMPTY;
		  simpleMapperTable[match+modify][match] = mapper_no_add_p1;
		  simpleMapperTable[match+modify][add] = Mapper.EMPTY;
		  simpleMapperTable[match+modify][remove] = Mapper.EMPTY;
		  simpleMapperTable[match+modify][match+modify] = mapper_no_add_p1; //tested
		  simpleMapperTable[match+modify][match+add] = mapper_no_add_p1; //tested
		  simpleMapperTable[match+modify][match+remove] = mapper_no_add_p1;
		  simpleMapperTable[match+modify][match+modify+add] = mapper_no_add_p1;
		  simpleMapperTable[match+modify][match+modify+remove] = mapper_no_add_p1;
			 
		  simpleCollapserTable[match+modify][empty] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][match] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][add] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][remove] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][match+modify] = collapse_no_add_p1; //tested
		  simpleCollapserTable[match+modify][match+add] = collapse_no_add_p1; //tested
		  simpleCollapserTable[match+modify][match+remove] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][match+modify+add] = collapse_no_add_p1;
		  simpleCollapserTable[match+modify][match+modify+remove] = collapse_no_add_p1;
		  
		  
		  simpleEvaluatorTable[match+add][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+add][match] = no_rhs_reference;
		  simpleEvaluatorTable[match+add][match+modify] = no_rhs_reference; //tested
		  
		  simpleMapperTable[match+add][empty] = Mapper.EMPTY;
		  simpleMapperTable[match+add][match] = mapper_add_p1;
		  simpleMapperTable[match+add][match+modify] = mapper_add_p1;  //tested
		  
		  simpleCollapserTable[match+add][empty] = collapse_add_p1;
		  simpleCollapserTable[match+add][match] = collapse_add_p1;
		  simpleCollapserTable[match+add][match+modify] = collapse_add_p1; //tested
		  
		  
		  simpleEvaluatorTable[match+remove][remove] = Evaluator.TRUE;
		  
		  simpleMapperTable[match+remove][remove] = Mapper.EMPTY;
		  
		  simpleCollapserTable[match+remove][remove] = collapse_no_add_p1;
		  
		  
		  simpleEvaluatorTable[add+query][match] = no_rhs_reference;
		  simpleEvaluatorTable[add+query][match+modify] = no_rhs_reference;
		  
		  simpleMapperTable[add+query][match] = mapper_add_p1;
		  simpleMapperTable[add+query][match+modify] = mapper_add_p1;
		  
		  simpleCollapserTable[add+query][match] = collapse_add_p1;
		  simpleCollapserTable[add+query][match+modify] = collapse_add_p1;
		  
		  
		  simpleEvaluatorTable[match+modify+add][empty] = Evaluator.TRUE;
		  simpleEvaluatorTable[match+modify+add][match] = no_rhs_reference;
		  simpleEvaluatorTable[match+modify+add][match+modify] = no_rhs_reference;
		  
		  simpleMapperTable[match+modify+add][empty] = Mapper.EMPTY;
		  simpleMapperTable[match+modify+add][match] = mapper_add_p1;
		  simpleMapperTable[match+modify+add][match+modify] = mapper_add_p1;
		  
		  simpleCollapserTable[match+modify+add][empty] = collapse_add_p1;
		  simpleCollapserTable[match+modify+add][match] = collapse_add_p1;
		  simpleCollapserTable[match+modify+add][match+modify] = collapse_add_p1;
		  
		  
		  simpleEvaluatorTable[match+modify+remove][remove] = Evaluator.TRUE;
		  
		  simpleMapperTable[match+modify+remove][remove] = Mapper.EMPTY;
		  
		  simpleCollapserTable[match+modify+remove][remove] = collapse_no_add_p1;
		  
		  
		  //Now for "retrieval types"
		  retrievalEvaluatorTable[empty][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][add] = Evaluator.TRUE; //tested
		  retrievalEvaluatorTable[empty][query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][match+remove] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][add+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][match+add+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[empty][match+remove+query] = Evaluator.TRUE;
		  
		  retrievalMapperTable[empty][match] = Mapper.EMPTY;
		  retrievalMapperTable[empty][add] = Mapper.EMPTY; //tested
		  retrievalMapperTable[empty][query] = Mapper.EMPTY;
		  retrievalMapperTable[empty][match+add] = Mapper.EMPTY;
		  retrievalMapperTable[empty][match+remove] = Mapper.EMPTY;
		  retrievalMapperTable[empty][match+query] = Mapper.EMPTY;
		  retrievalMapperTable[empty][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[empty][match+add+query] = Mapper.EMPTY;
		  retrievalMapperTable[empty][match+remove+query] = Mapper.EMPTY;
		  
		  retrievalCollapserTable[empty][match] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][add] = collapse_no_add_p1; //tested
		  retrievalCollapserTable[empty][query] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][match+add] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][match+remove] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][match+query] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][match+add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[empty][match+remove+query] = collapse_no_add_p1;
		  
		  
		  retrievalEvaluatorTable[match][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match+remove] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][add+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match+add+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match][match+remove+query] = Evaluator.TRUE;
		  
		  retrievalMapperTable[match][empty] = Mapper.EMPTY;
		  retrievalMapperTable[match][match] = mapper_no_add_p1;
		  retrievalMapperTable[match][add] = Mapper.EMPTY;
		  retrievalMapperTable[match][query] = Mapper.EMPTY;
		  retrievalMapperTable[match][match+add] = mapper_no_add_p1;
		  retrievalMapperTable[match][match+remove] = mapper_no_add_p1;
		  retrievalMapperTable[match][match+query] = mapper_no_add_p1;
		  retrievalMapperTable[match][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[match][match+add+query] = mapper_no_add_p1;
		  retrievalMapperTable[match][match+remove+query] = mapper_no_add_p1;
		  
		  retrievalCollapserTable[match][empty] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match] = collapse_no_add_p1;
		  retrievalCollapserTable[match][add] = collapse_no_add_p1;
		  retrievalCollapserTable[match][query] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match+add] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match+remove] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match+query] = collapse_no_add_p1;
		  retrievalCollapserTable[match][add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match+add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[match][match+remove+query] = collapse_no_add_p1;
		  
		  
		  retrievalEvaluatorTable[add][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][query] = p2_busy_or_empty; //not yet harvested; otherwise create a situation where the p2 fires once chunk in buffer, but when fire compiled production not in there.
		  retrievalEvaluatorTable[add][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][match+remove] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][add+query] = Evaluator.TRUE;  //FIXME: not sure this is right, but ok for now...
		  retrievalEvaluatorTable[add][remove+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][match+add+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add][match+remove+query] = Evaluator.TRUE; //tested
		  
		  retrievalMapperTable[add][empty] = Mapper.EMPTY;
		  retrievalMapperTable[add][match] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add][add] = Mapper.EMPTY;
		  retrievalMapperTable[add][query] = Mapper.EMPTY;
		  retrievalMapperTable[add][match+add] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add][match+remove] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add][match+query] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[add][remove+query] = Mapper.EMPTY;
		  retrievalMapperTable[add][match+add+query] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add][match+remove+query] = mapper_nondeterministic_add_p1; //tested
		  
		  retrievalCollapserTable[add][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[add][match] = collapse_add_p1;//collapse_cancompileout_add_p1; -- this is wrong because at the end of this there is still a chunk in the buffer and compiling out compiles it out.
		  retrievalCollapserTable[add][add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add][query] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[add][match+add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add][match+remove] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add][match+query] = collapse_add_p1; //collapse_cancompileout_add_p1;
		  retrievalCollapserTable[add][add+query] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add][remove+query] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add][match+remove+query] = collapse_compileout_add_p1; //tested
		  
		  
		  retrievalEvaluatorTable[query][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[query][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[query][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[query][query] = same_queries;
		  retrievalEvaluatorTable[query][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[query][match+query] = same_queries;
		  retrievalEvaluatorTable[query][add+query] = same_queries;
		  retrievalEvaluatorTable[query][match+add+query] = same_queries;
		  
		  retrievalMapperTable[query][empty] = Mapper.EMPTY;
		  retrievalMapperTable[query][match] = Mapper.EMPTY;
		  retrievalMapperTable[query][add] = Mapper.EMPTY;
		  retrievalMapperTable[query][query] = Mapper.EMPTY;
		  retrievalMapperTable[query][match+add] = Mapper.EMPTY;
		  retrievalMapperTable[query][match+query] = Mapper.EMPTY;
		  retrievalMapperTable[query][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[query][match+add+query] = Mapper.EMPTY;
		  
		  retrievalCollapserTable[query][empty] = collapse_no_add_p1;
		  retrievalCollapserTable[query][match] = collapse_no_add_p1;
		  retrievalCollapserTable[query][add] = collapse_no_add_p1;
		  retrievalCollapserTable[query][query] = collapse_no_add_p1;
		  retrievalCollapserTable[query][match+add] = collapse_no_add_p1;
		  retrievalCollapserTable[query][match+query] = collapse_no_add_p1;
		  retrievalCollapserTable[query][add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[query][match+add+query] = collapse_no_add_p1;
		  
		  
		  retrievalEvaluatorTable[match+add][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[match+add][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][match+remove] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add][add+query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[match+add][match+add+query] = Evaluator.TRUE;
		  
		  retrievalMapperTable[match+add][empty] = Mapper.EMPTY;
		  retrievalMapperTable[match+add][match] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add][add] = Mapper.EMPTY;
		  retrievalMapperTable[match+add][query] = Mapper.EMPTY;
		  retrievalMapperTable[match+add][match+add] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add][match+remove] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add][match+query] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[match+add][match+add+query] = mapper_nondeterministic_add_p1;
		  
		  retrievalCollapserTable[match+add][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[match+add][match] = collapse_add_p1; //don't use compile out version since at the end of the firing there is still a chunk in the retrieval buffer
		  retrievalCollapserTable[match+add][add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add][query] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[match+add][match+add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add][match+remove] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add][match+query] = collapse_add_p1;
		  retrievalCollapserTable[match+add][add+query] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add][match+add+query] = collapse_compileout_add_p1;
		  
		  
		  retrievalEvaluatorTable[match+query][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+query][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+query][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+query][remove] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+query][query] = same_queries;
		  retrievalEvaluatorTable[match+query][match+query] = same_queries;
		  retrievalEvaluatorTable[match+query][add+query] = same_queries;
		  retrievalEvaluatorTable[match+query][remove+query] = same_queries;
		  
		  retrievalMapperTable[match+query][empty] = Mapper.EMPTY;
		  retrievalMapperTable[match+query][match] = mapper_no_add_p1;
		  retrievalMapperTable[match+query][add] = Mapper.EMPTY;
		  retrievalMapperTable[match+query][remove] = Mapper.EMPTY;
		  retrievalMapperTable[match+query][query] = Mapper.EMPTY;
		  retrievalMapperTable[match+query][match+query] = mapper_no_add_p1;
		  retrievalMapperTable[match+query][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[match+query][remove+query] = Mapper.EMPTY;
		  
		  retrievalCollapserTable[match+query][empty] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][match] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][add] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][remove] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][query] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][match+query] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][add+query] = collapse_no_add_p1;
		  retrievalCollapserTable[match+query][remove+query] = collapse_no_add_p1;
		  
		  retrievalEvaluatorTable[add+query][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add+query][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add+query][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add+query][query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[add+query][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add+query][match+remove] = Evaluator.TRUE; //tested
		  retrievalEvaluatorTable[add+query][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[add+query][add+query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[add+query][match+add+query] = Evaluator.TRUE; //tested
		  retrievalEvaluatorTable[add+query][match+remove+query] = Evaluator.TRUE; //tested
			
		  retrievalMapperTable[add+query][empty] = Mapper.EMPTY;
		  retrievalMapperTable[add+query][match] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add+query][add] = Mapper.EMPTY;
		  retrievalMapperTable[add+query][query] = Mapper.EMPTY;
		  retrievalMapperTable[add+query][match+add] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add+query][match+remove] = mapper_nondeterministic_add_p1; //tested
		  retrievalMapperTable[add+query][match+query] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[add+query][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[add+query][match+add+query] = mapper_nondeterministic_add_p1; //tested
		  retrievalMapperTable[add+query][match+remove+query] = mapper_nondeterministic_add_p1; //tested
		  
		  retrievalCollapserTable[add+query][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[add+query][match] = collapse_add_p1;
		  retrievalCollapserTable[add+query][add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add+query][query] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[add+query][match+add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add+query][match+remove] = collapse_compileout_add_p1; //tested //remove just lets us pretend that there is strict harvesting, even if there is not
		  retrievalCollapserTable[add+query][match+query] = collapse_add_p1;
		  retrievalCollapserTable[add+query][add+query] = collapse_compileout_add_p1;
		  retrievalCollapserTable[add+query][match+add+query] = collapse_compileout_add_p1; //tested
		  retrievalCollapserTable[add+query][match+remove+query] = collapse_compileout_add_p1; //tested
		  
		  retrievalEvaluatorTable[match+add+query][empty] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add+query][match] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add+query][add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add+query][query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[match+add+query][match+add] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add+query][match+remove] = Evaluator.TRUE; //tested
		  retrievalEvaluatorTable[match+add+query][match+query] = Evaluator.TRUE;
		  retrievalEvaluatorTable[match+add+query][add+query] = p2_busy_or_empty;
		  retrievalEvaluatorTable[match+add+query][match+add+query] = Evaluator.TRUE;
		  
		  retrievalMapperTable[match+add+query][empty] = Mapper.EMPTY;
		  retrievalMapperTable[match+add+query][match] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add+query][add] = Mapper.EMPTY;
		  retrievalMapperTable[match+add+query][query] = Mapper.EMPTY;
		  retrievalMapperTable[match+add+query][match+add] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add+query][match+remove] = mapper_nondeterministic_add_p1; //tested
		  retrievalMapperTable[match+add+query][match+query] = mapper_nondeterministic_add_p1;
		  retrievalMapperTable[match+add+query][add+query] = Mapper.EMPTY;
		  retrievalMapperTable[match+add+query][match+add+query] = mapper_nondeterministic_add_p1;
		  
		  retrievalCollapserTable[match+add+query][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[match+add+query][match] = collapse_add_p1;
		  retrievalCollapserTable[match+add+query][add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add+query][query] = collapse_nonimmediate_add_p1_not_harvested;
		  retrievalCollapserTable[match+add+query][match+add] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add+query][match+remove] = collapse_compileout_add_p1; //tested
		  retrievalCollapserTable[match+add+query][match+query] = collapse_add_p1;
		  retrievalCollapserTable[match+add+query][add+query] = collapse_compileout_add_p1;
		  retrievalCollapserTable[match+add+query][match+add+query] = collapse_compileout_add_p1;
		  
		  retrievalEvaluatorTable[match+remove+query][empty] = Evaluator.TRUE; //tested
		  
		  retrievalMapperTable[match+remove+query][empty] = Mapper.EMPTY; //tested
		  
		  retrievalCollapserTable[match+remove+query][empty] = collapse_no_add_p1; //tested
		  
		  
		  //Now for perceptual ones.
		  perceptualEvaluatorTable[empty][match] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][match+add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][match+remove] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][match+query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][add+query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][match+add+query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[empty][match+remove+query] = Evaluator.TRUE;
		  
		  perceptualMapperTable[empty][match] = Mapper.EMPTY;
		  perceptualMapperTable[empty][add] = Mapper.EMPTY;
		  perceptualMapperTable[empty][query] = Mapper.EMPTY;
		  perceptualMapperTable[empty][match+add] = Mapper.EMPTY;
		  perceptualMapperTable[empty][match+remove] = Mapper.EMPTY;
		  perceptualMapperTable[empty][match+query] = Mapper.EMPTY;
		  perceptualMapperTable[empty][add+query] = Mapper.EMPTY;
		  perceptualMapperTable[empty][match+add+query] = Mapper.EMPTY;
		  perceptualMapperTable[empty][match+remove+query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[empty][match] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][add] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][query] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][match+add] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][match+remove] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][match+query] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][add+query] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][match+add+query] = collapse_no_add_p1;
		  perceptualCollapserTable[empty][match+remove+query] = collapse_no_add_p1;
		  
		  perceptualEvaluatorTable[add][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[add][query] = p2_busy_or_empty;
		  
		  perceptualMapperTable[add][empty] = Mapper.EMPTY;
		  perceptualMapperTable[add][query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[add][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  perceptualCollapserTable[add][query] = collapse_nonimmediate_add_p1_not_harvested;
		 
		  
		  perceptualEvaluatorTable[match][empty] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match][add] = Evaluator.TRUE; -- is this buffer stuffing...?
		  perceptualEvaluatorTable[match][query] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match][match+add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match][match+remove] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match][match+query] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match][add+query] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match][match+add+query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match][match+remove+query] = Evaluator.TRUE;
		  
		  perceptualMapperTable[match][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match][query] = Mapper.EMPTY;
		  perceptualMapperTable[match][match+remove] = mapper_no_add_p1;
		  perceptualMapperTable[match][match+query] = mapper_no_add_p1;
		  perceptualMapperTable[match][match+remove+query] = mapper_no_add_p1;
		  
		  perceptualCollapserTable[match][empty] = collapse_no_add_p1;
		  perceptualCollapserTable[match][query] = collapse_no_add_p1;
		  perceptualCollapserTable[match][match+remove] = collapse_no_add_p1;
		  perceptualCollapserTable[match][match+query] = collapse_no_add_p1;
		  perceptualCollapserTable[match][match+remove+query] = collapse_no_add_p1;
		  
		  
		  perceptualEvaluatorTable[query][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[query][match] = Evaluator.TRUE;
		  perceptualEvaluatorTable[query][add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[query][query] = same_queries;
		  perceptualEvaluatorTable[query][match+add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[query][match+remove] = Evaluator.TRUE;
		  perceptualEvaluatorTable[query][match+query] = same_queries;
		  perceptualEvaluatorTable[query][add+query] = same_queries;
		  perceptualEvaluatorTable[query][match+add+query] = same_queries;
		  perceptualEvaluatorTable[query][match+remove+query] = same_queries;
		  
		  perceptualMapperTable[query][empty] = Mapper.EMPTY;
		  perceptualMapperTable[query][match] = Mapper.EMPTY;
		  perceptualMapperTable[query][add] = Mapper.EMPTY;
		  perceptualMapperTable[query][query] = Mapper.EMPTY;
		  perceptualMapperTable[query][match+add] = Mapper.EMPTY;
		  perceptualMapperTable[query][match+remove] = Mapper.EMPTY;
		  perceptualMapperTable[query][match+query] = Mapper.EMPTY;
		  perceptualMapperTable[query][add+query] = Mapper.EMPTY;
		  perceptualMapperTable[query][match+add+query] = Mapper.EMPTY;
		  perceptualMapperTable[query][match+remove+query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[query][empty] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match] = collapse_no_add_p1;
		  perceptualCollapserTable[query][add] = collapse_no_add_p1;
		  perceptualCollapserTable[query][query] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match+add] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match+remove] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match+query] = collapse_no_add_p1;
		  perceptualCollapserTable[query][add+query] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match+add+query] = collapse_no_add_p1;
		  perceptualCollapserTable[query][match+remove+query] = collapse_no_add_p1;
		  
		  
		  perceptualEvaluatorTable[match+add][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+add][query] = p2_busy_or_empty;
		  
		  perceptualMapperTable[match+add][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match+add][query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[match+add][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  perceptualCollapserTable[match+add][query] = collapse_nonimmediate_add_p1_not_harvested;

		  
		  perceptualEvaluatorTable[match+remove][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+remove][add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+remove][query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+remove][add+query] = Evaluator.TRUE;
		  
		  perceptualMapperTable[match+remove][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove][add] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove][query] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove][add+query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[match+remove][empty] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove][add] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove][query] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove][add+query] = collapse_no_add_p1;

		  
   	 	  perceptualEvaluatorTable[match+query][empty] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match+query][add] = Evaluator.TRUE; buffer stuffing??
		  perceptualEvaluatorTable[match+query][query] = same_queries;
		  //perceptualEvaluatorTable[match+query][match+add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+query][match+remove] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+query][match+query] = Evaluator.TRUE;
		  //perceptualEvaluatorTable[match+query][add+query] = same_queries;
		  //perceptualEvaluatorTable[match+query][match+add+query] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+query][match+remove+query] = Evaluator.TRUE;
		  
		  perceptualMapperTable[match+query][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match+query][query] = Mapper.EMPTY;
		  perceptualMapperTable[match+query][match+remove] = mapper_no_add_p1;
		  perceptualMapperTable[match+query][match+query] = mapper_no_add_p1;
		  perceptualMapperTable[match+query][match+remove+query] = mapper_no_add_p1;
		  
		  perceptualCollapserTable[match+query][empty] = collapse_no_add_p1;
		  perceptualCollapserTable[match+query][query] = collapse_no_add_p1;
		  perceptualCollapserTable[match+query][match+remove] = collapse_no_add_p1;
		  perceptualCollapserTable[match+query][match+query] = collapse_no_add_p1;
		  perceptualCollapserTable[match+query][match+remove+query] = collapse_no_add_p1;

		  
		  perceptualEvaluatorTable[add+query][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[add+query][query] = p2_busy_or_empty;
		  
		  perceptualMapperTable[add+query][empty] = Mapper.EMPTY;
		  perceptualMapperTable[add+query][query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[add+query][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  perceptualCollapserTable[add+query][query] = collapse_nonimmediate_add_p1_not_harvested;
		  
		  
		  perceptualEvaluatorTable[match+add+query][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+add+query][query] = p2_busy_or_empty;
		  
		  perceptualMapperTable[match+add+query][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match+add+query][query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[match+add+query][empty] = collapse_nonimmediate_add_p1_not_harvested;
		  perceptualCollapserTable[match+add+query][query] = collapse_nonimmediate_add_p1_not_harvested;
		  
		  
		  perceptualEvaluatorTable[match+remove+query][empty] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+remove+query][add] = Evaluator.TRUE;
		  perceptualEvaluatorTable[match+remove+query][query] = same_queries;
		  perceptualEvaluatorTable[match+remove+query][add+query] = same_queries;
		  
		  perceptualMapperTable[match+remove+query][empty] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove+query][add] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove+query][query] = Mapper.EMPTY;
		  perceptualMapperTable[match+remove+query][add+query] = Mapper.EMPTY;
		  
		  perceptualCollapserTable[match+remove+query][empty] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove+query][add] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove+query][query] = collapse_no_add_p1;
		  perceptualCollapserTable[match+remove+query][add+query] = collapse_no_add_p1;
		 }
	  
	  
	  
	  
	  
	  
}
