tree grammar JACTRBuilder;


options {
	ASTLabelType=CommonTree;
}

tokens
{	
	MODEL;      // Model root node
	LIBRARY;    // Library root
	
	MODULES; //module wrapper node
	MODULE; 
	
	EXTENSIONS; // Extensions wrapper node
	EXTENSION;  // extension node

	DECLARATIVE_MEMORY; //declarative wrapper for
	CHUNK_TYPE; 
	CHUNKS;
	CHUNK;
		
	PROCEDURAL_MEMORY;
	PRODUCTION;
	
	PARAMETERS; // parameters wrapper node
	PARAMETER;  // parameter node
	
	BUFFERS;
	BUFFER;

	CONDITIONS; //condition wrapper for productions
	MATCH_CONDITION;
	QUERY_CONDITION;
	SCRIPTABLE_CONDITION;
	PROXY_CONDITION;
	
	ACTIONS;
	ADD_ACTION;
	SET_ACTION;
	REMOVE_ACTION;
	MODIFY_ACTION;
	OUTPUT_ACTION;
	SCRIPTABLE_ACTION;
	PROXY_ACTION;
	
	LANG; //for scriptable
	SCRIPT;
	
	VARIABLE;
	STRING;
	NUMBER;
	IDENTIFIER;
	
	CHUNK_IDENTIFIER;
	CHUNK_TYPE_IDENTIFIER;
	
	SLOTS;
	SLOT;
	//slot conditions
	LT;
	GT;
	EQUALS;
	NOT;
	WITHIN;
	GTE;
	LTE;
	OR;
	AND;
	LOGIC;
	
	CLASS_SPEC;
	NAME;
	PARENT;
	PARENTS;
	
	UNKNOWN;
}

scope Model{
 IModel model;
 Map<String, IChunk> knownChunks;
 Map<String, IChunkType> knownChunkTypes;
 Map<IChunk, Collection<CommonTree>> chunkParameters;
 Map<IProduction, Collection<CommonTree>> productionParameters;
 Collection<IExtension> extensions;
 Collection<String> sourceChunks;
 CommonTree modelDescriptor;
}

@header{
package org.jactr.io.antlr3.builder;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.jactr.io.antlr3.misc.*;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.*;
import org.jactr.core.module.declarative.six.*;
import org.jactr.core.module.procedural.*;
import org.jactr.core.module.procedural.six.*;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.slot.*;
import org.jactr.core.production.action.*;
import org.jactr.core.production.condition.*;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.scripting.action.*;
import org.jactr.scripting.condition.*;
import org.jactr.scripting.*;
import org.jactr.core.production.CannotInstantiateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javolution.util.FastList;
}

@members{
static private final transient Log LOGGER = LogFactory.getLog(JACTRBuilder.class);


static private String NULL = "null";
static private String NIL = "nil";
static private String T = "t";

private Collection<Exception> _warnings = new ArrayList<Exception>();
private Collection<Exception> _errors = new ArrayList<Exception>();


public void reportError(RecognitionException re)
{
 reportException(re);
}

public void reportException(Exception e)
{
 LOGGER.error(e.getMessage(), e);
 if(e instanceof BuilderWarning)
  _warnings.add(e);
 else
  _errors.add(e);
}

public Collection<Exception> getWarnings()
{
return _warnings;
}

public Collection<Exception> getErrors()
{
return _errors;
}


/**
 apply the parameters in the parametersNode to parameterized
*/
public void applyParameters(IParameterized parameterized, Collection<CommonTree> parameterNodes)
{
 for(CommonTree param: parameterNodes)
 {
  String pName = param.getChild(0).toString();
  String pVal = param.getChild(1).toString();
  
  if(LOGGER.isDebugEnabled())
   LOGGER.debug(parameterized+"."+pName+" => "+pVal);
  try
  { 
   parameterized.setParameter(pName, pVal);
  }
  catch(Exception e)
  {
   throw new BuilderWarning("Error while setting parameter "+parameterized+"."+pName+" to "+pVal, param, e);
  } 
 }
}


protected Object resolveKeywords(String strToResolve)
{
 if(NULL.equalsIgnoreCase(strToResolve))
  return null;
 if(NIL.equalsIgnoreCase(strToResolve))
  return null;
 if(T.equalsIgnoreCase(strToResolve))
  return Boolean.TRUE; 
 
 /**
  I'd rather use Boolean.valueOf() but it returns false
  unless the string is equal to "true" - and doesn't throw
  any formatting exceptions if it is nothing like "false"
 */ 
 if("true".equalsIgnoreCase(strToResolve))
  return Boolean.TRUE;
 if("false".equalsIgnoreCase(strToResolve))
  return Boolean.FALSE;
 
 return strToResolve; 
}

/**
     * we use stringbuilders to mark string literals, which are removed during resolveSlots, but
     * that is only called on chunks and chunktypes, not conditions/actions. this takes care of that.
     * @param slot
     * @return
     */
    protected ISlot cleanupSlot(ISlot slot)
    {
      if (slot instanceof ILogicalSlot)
    {
      FastList<ISlot> children = FastList.newInstance();
      ((ILogicalSlot)slot).getSlots(children);
      for(ISlot s : children)
       cleanupSlot(s);
       
      FastList.recycle(children);
    }
    else
    {
      Object value = slot.getValue();
      if (value instanceof StringBuilder)
        ((IMutableSlot) slot).setValue(value.toString());
    }
      return slot;
    }
    
private Collection<String> getSourceChunks(CommonTree model)
{
 Collection<String> rtn = new ArrayList<String>();
 Map<String, CommonTree> bufferTrees = ASTSupport.getMapOfTrees(model, BUFFER);
 for(String bufferName : bufferTrees.keySet())
 {
  //get the all identifiers
  Collection<CommonTree> identifierTrees = ASTSupport.getAllDescendantsWithType(bufferTrees.get(bufferName), CHUNK_IDENTIFIER);
  
  LOGGER.debug(identifierTrees.size()+" source chunks in "+bufferName);
  
  for(CommonTree chunkIdentifier : identifierTrees)
   {
    String chunkName = chunkIdentifier.getText();
    rtn.add(chunkName.toLowerCase());
   }
  }
  
  return rtn;
}    
    
    
protected void resolveSlots(ISlotContainer slotContainer, Map<String, IChunk> knownChunks, Map<String, IChunkType> knownChunkTypes)
{
 if(LOGGER.isDebugEnabled())
  LOGGER.debug("resolving slots for "+slotContainer);
  
 for(ISlot slot : slotContainer.getSlots())
 {
 //getSlots will usually return an immutable copy to dissuade modifications
  if(slotContainer instanceof IUniqueSlotContainer) //but getSlot should return the actual slot 
   slot = ((IUniqueSlotContainer)slotContainer).getSlot(slot.getName());
 
  if(LOGGER.isDebugEnabled())
   LOGGER.debug("Attempting resolution of "+slot);
   
  if(slot instanceof IMutableSlot)
   {
    IMutableSlot mutable = (IMutableSlot) slot;
    Object value = mutable.getValue();
    if(!mutable.isVariableValue())
     {
      if(value instanceof StringBuilder)
      {
       //this is a real string, not one that needs to be reoslved
       mutable.setValue(value.toString());
      }
      else
      if(value instanceof String)
      {
       String strVal = (String) value;
       
         //attempt to resolve
         boolean changed = true;
         Object newValue = resolveKeywords(strVal);
         if(newValue==strVal)
         {
         /*
          it wasn't a keyword, could still be a string or
          an identifier
         */
          //more resolution is necessary
          //newValue = model.getDeclarativeModule().getChunk(strVal);
          //if(newValue == null) 
          newValue = knownChunks.get(strVal.toLowerCase());
          //if(newValue == null) newValue = model.getDeclarativeModule().getChunkType(strVal);
          if(newValue==null)  newValue = knownChunkTypes.get(strVal.toLowerCase());
           
          if(newValue==null)
           {
            /*
             nope, just a string
            */
            newValue=strVal; 
           }
          changed = newValue!=strVal;
         }
         
         if(changed)
         {
           //resolve
           if(LOGGER.isDebugEnabled())
            LOGGER.debug("changing slot "+slot.getName()+" value from "+strVal+" to "+newValue);
          
           mutable.setValue(newValue);
           if(mutable.getValue()!=newValue)
            reportException(new BuilderError("Could not change slot "+slot.getName()+" value from "+strVal+" to "+newValue, null));
         }
      }
     }
   }
  else
   reportException(new BuilderError("Could not resolve slot since it is immutable "+slot+" contained within "+slotContainer, null)); 
 }
}

    private String partialStream(String[] stream, int index) {
    	String result = "";
    	for(int i = index+1; i < stream.length; i++) {
    		result += stream[i] + " ";
    	}
    	return result;
    }
    
    public void setModel(IModel model) {
    	 Model_stack.push(new Model_scope());
    	$Model::model = model;
    	$Model::productionParameters  = new HashMap<IProduction, Collection<CommonTree>>();
    	//if($Model::knownChunks == null) $Model::knownChunks = new HashMap<String, IChunk>();
 	//if($Model::knownChunkTypes == null)$Model::knownChunkTypes = new HashMap<String, IChunkType>();
    }

}


model	returns [IModel model]
scope Model;
@init{ 
 model = null;
 $Model::knownChunks = new HashMap<String, IChunk>();
 $Model::knownChunkTypes = new HashMap<String, IChunkType>();
 $Model::chunkParameters = new HashMap<IChunk, Collection<CommonTree>>();
 $Model::productionParameters = new HashMap<IProduction, Collection<CommonTree>>();
 $Model::extensions = new ArrayList<IExtension>();
 $Model::sourceChunks = new ArrayList<String>();
}
:	^(m=MODEL name=NAME 
{
 if(LOGGER.isDebugEnabled())
  LOGGER.debug("got model def for "+$name.text);
  
  String modelName = $name.text;
  model = new BasicModel(modelName);
  
  $Model::model = model;
  $Model::modelDescriptor = (CommonTree) $m;
}
       modules extensions buffers library p=parameters)
{
 /*
  we apply model parameters, then the other parameters
 */
 applyParameters(model, p);
 
 //now we can resolve the parameters for the chunks 
  for(IChunk chunk : $Model::chunkParameters.keySet())
   applyParameters(chunk.getSubsymbolicChunk(), $Model::chunkParameters.get(chunk)); 

//we allow the chunktype parameters to be set with the chunktype creation
// since it won't likely depend upon the model's parameters..

// and the production parameters
  for(IProduction production : $Model::productionParameters.keySet())
   applyParameters(production.getSubsymbolicProduction(), $Model::productionParameters.get(production));
    	  
 /**
  we need to insert the chunks into the buffers
 */
 Map<String, CommonTree> bufferTrees = ASTSupport.getMapOfTrees($m, BUFFER);
 for(String bufferName : bufferTrees.keySet())
 {
  LOGGER.debug("Checking "+bufferName+" for source chunks to insert");
  
  IActivationBuffer buffer = $Model::model.getActivationBuffer(bufferName);
  if(buffer==null)
   throw new BuilderError(bufferName+" is not a known buffer", bufferTrees.get(bufferName));
  //get the all identifiers
  Collection<CommonTree> identifierTrees = ASTSupport.getAllDescendantsWithType(bufferTrees.get(bufferName), CHUNK_IDENTIFIER);
  
  LOGGER.debug(identifierTrees.size()+" source chunks in "+bufferName);
  
  for(CommonTree chunkIdentifier : identifierTrees)
   {
    String chunkName = chunkIdentifier.getText();
    IChunk chunk = null;
    /*
     
    */
    chunk = $Model::knownChunks.get(chunkName.toLowerCase());
     
    if(chunk==null)
     throw new BuilderError(chunkName+" is not a known chunk", chunkIdentifier);
     
    buffer.addSourceChunk(chunk);
   }
 }
 
 /*
  install the extensions..
 */
 for(IExtension extension : $Model::extensions)
  model.install(extension);
  
 /*
  everything is done, call initialize
 */
 try
 {
 model.initialize();
 }
 catch(Exception e)
 {
  reportException(e);
 }
};



library
:	^(LIBRARY declarativeMemory proceduralMemory);

declarativeMemory 
@init{
 
}
	:	(^(DECLARATIVE_MEMORY 
	            (chunkType)+) | DECLARATIVE_MEMORY) //hack recommended by TP v3ea8
	{
	 /*we now have all the chunks and all the chunkTypes
	  we can add them all to the model and then apply
	  the parameters and resolve any slot values
	 */
	 
	 /*
	  resolve the slot values of the chunktypes
	 */
	 for(IChunkType chunkType : $Model::knownChunkTypes.values())
	  resolveSlots(chunkType.getSymbolicChunkType(), $Model::knownChunks, $Model::knownChunkTypes);
	 
	 /*
	  @bug this will not handle default slot values correctly if the chunks
	  are defined after the chunktype is defined
	 */ 
	 
	 IDeclarativeModule decMod = $Model::model.getDeclarativeModule();
	 Collection<String> sourceChunks = getSourceChunks($Model::modelDescriptor);
	 //now for the chunks
	 for(IChunk chunk : $Model::knownChunks.values())
	  {
	   resolveSlots(chunk.getSymbolicChunk(), $Model::knownChunks, $Model::knownChunkTypes); 
	   //now we can add the chunk to the model
	   if(!sourceChunks.contains(chunk.getSymbolicChunk().getName().toLowerCase()))
	   try
	   {
	     if(LOGGER.isDebugEnabled())
  	       LOGGER.debug("Adding chunk "+chunk+" to model");
	     decMod.addChunk(chunk).get();
	   }
	   catch(Exception e)
	   {
	     throw new BuilderError("Could not add chunk "+chunk, null, e);
	   }
	  }  
	};

proceduralMemory
	:	(^(PROCEDURAL_MEMORY production+) | PROCEDURAL_MEMORY); //tp hack

modules :	(^(MODULES module+) | MODULES)
		{
		  /*
		   check to see what modules have been installed
		   we must have at least IDeclarativeModule and
		   IProceduralModule
		  */ 
		  if($Model::model.getModule(IDeclarativeModule.class)==null)
		   {
		    reportException(new BuilderWarning("No IDeclarativeModule was specified, installing DefaultDeclarativeModule6"));
		    $Model::model.install(new DefaultDeclarativeModule6());
		   }
		  
		  if($Model::model.getModule(IProceduralModule.class)==null)
		   {
		    reportException(new BuilderWarning("No IProceduralModule was specified, installing DefaultProceduralModule6"));
		    $Model::model.install(new DefaultProceduralModule6());
		   } 
		   
		}; //tp hack

module 	:	^(m=MODULE c=CLASS_SPEC p=parameters)
{
 String className = $c.text;
 LOGGER.debug("Got module "+className);
 //install the module
 //instantiate
 try
 {
  //load the class
  Class<IModule> moduleClass = (Class<IModule>)getClass().getClassLoader().loadClass(className);
  IModule module = moduleClass.newInstance();
  
  //apply parameters
  if(module instanceof IParameterized)
   applyParameters((IParameterized)module, p); 
   
    //install
  $Model::model.install(module); 
 }
 catch(Exception e)
 {
  throw new BuilderError("Could not install module "+className,$m, e);
 }
};

extensions 
	:	(^(EXTENSIONS extension+) | EXTENSIONS); //tp hack

extension 
	:	^(e=EXTENSION c=CLASS_SPEC p=parameters)
{
 String className = $c.text;
 LOGGER.debug("Got extension "+className);
 //install the module
 //instantiate
 try
 {
  //load the class
  Class<IExtension> extensionClass = (Class<IExtension>)getClass().getClassLoader().loadClass(className);
  IExtension ext = extensionClass.newInstance();
  
  //apply parameters
  if(ext instanceof IParameterized)
   applyParameters((IParameterized)ext, p); 
   
  //install later.. after we have finished the configuration
  $Model::extensions.add(ext);
 }
 catch(Exception ex)
 {
  throw new BuilderError("Could not build extension "+className,$e, ex);
 }
};

/**
 this should be ^(BUFFERS buffer*) but that will produce parse errors
 not clue why.
 How is this different from chunks? 
*/	
buffers
:	(^(BUFFERS  buffer+) | BUFFERS) //tp hack
{
 LOGGER.debug("got buffers tag");
};

buffer 	: ^(b=BUFFER name=NAME chunks p=parameters)
{
 String bufferName = $name.text;
 LOGGER.debug("got buffer name "+bufferName);
 /*
  buffers are created by the modules, so we just apply the parameters
  the chunks will be inserted after the library is done loading
 */
 IActivationBuffer buffer = $Model::model.getActivationBuffer(bufferName);
 if(buffer==null)
  throw new BuilderError(bufferName+" is not a known buffer", $b);
 
 if(buffer instanceof IParameterized)
  applyParameters((IParameterized)buffer, p);
   
};	

chunkType returns [IChunkType chunkType]
@init{
 chunkType = null;
}
:	^(c=CHUNK_TYPE n=NAME p=parents
	s=slots 
	{
	 /*
	  we create the chunktype once we have enough info to work with
	  the compiler ensures us that parents are defined already
	 */
	 String chunkTypeName = $n.text;
	 IModel model = $Model::model;
          
         try
         {
          chunkType = model.getDeclarativeModule().createChunkType(p, chunkTypeName).get();
          if(LOGGER.isDebugEnabled())
           LOGGER.debug("created chunktype "+chunkType);
         }
         catch(Exception e)
         {
          throw new BuilderError("Could not create chunk-type "+chunkTypeName, $c, e);
         } 
         
         //insert all the slots
         for(ISlot slot : s)
          chunkType.getSymbolicChunkType().addSlot(slot);
          
         $Model::knownChunkTypes.put(chunkTypeName.toLowerCase(), chunkType);
         
         /*add the chunkType to the model
          this must be done before chunks is called
         */
         if(LOGGER.isDebugEnabled())
          LOGGER.debug("Adding chunktype "+chunkType+" to model");
	 model.getDeclarativeModule().addChunkType(chunkType);
	}
	chunks  param=parameters)
	{
	 applyParameters(chunkType.getSubsymbolicChunkType(), param);
	 
	};

/**
 return a collection of IChunk or Strings
*/
chunks returns [Collection cl]
@init{
 cl = new ArrayList();
}	:	(^(CHUNKS (c=chunk {cl.add(c);}
                         |id=CHUNK_IDENTIFIER  {cl.add($id.text);}
                         )+) | CHUNKS); //tp hack

chunk returns [IChunk ch]
@init{
 ch= null;
}	:	^(c=CHUNK n=NAME p=PARENT s=slots param=parameters)
{
 LOGGER.debug("got chunk def "+$n.text+" isa "+$p.text);
 String chunkName = $n.text;
 String parentName = $p.text;
 IModel model = $Model::model;
 
 if($Model::knownChunks.containsKey(chunkName))
  reportException(new BuilderWarning(chunkName+" already exists, replacing", $c));
 
 IChunkType parentType = null;
 try
 {
  parentType = $Model::model.getDeclarativeModule().getChunkType(parentName).get();
 }
 catch(Exception e)
 {
  LOGGER.error("Could not get chunktype "+parentName, e);
 } 
 
 if(parentType==null)
  throw new BuilderError(parentName+" is not a recognized chunk-type", $p);

 try
 {
   ch = model.getDeclarativeModule().createChunk(parentType, chunkName).get();
 } 
 catch(Exception e)
 {
  throw new BuilderError("Could not create chunk "+chunkName, $c, e);
 }
 
 $Model::knownChunks.put(chunkName.toLowerCase(), ch);
 
 //add slots
 for(ISlot slot : s)
  try
   {
    ch.getSymbolicChunk().addSlot(slot);
   }
  catch(Exception e)
   {
    reportException(new BuilderWarning("Could not add slot "+slot.getName()+" to chunk "+chunkName, $c, e));
   }
   
 //save the parameters for later because chunks in associative links might not exist yet
 $Model::chunkParameters.put(ch, param);
};

production returns [IProduction production]
@init{
production = null;
}
	:	^(p=PRODUCTION n=NAME conds=conditions acts=actions params=parameters)
	{
	 String productionName = $n.text;
	 LOGGER.debug("Got a production def "+productionName);
	 /**
	  we can just create the production, no need to worry about
	  resolution since all declarative elements have been defined
	 */
	 IModel model = $Model::model;
	  try
	  {
	   production = model.getProceduralModule().createProduction(productionName).get();
	  } 
	  catch(Exception e)
	  {
	   throw new BuilderError("Could not create production "+productionName, $p, e);
	  }
	 
	 //insert the contents
	 ISymbolicProduction symProd = production.getSymbolicProduction();
	 for(ICondition condition : conds)
	  {
	   symProd.addCondition(condition);
	  }
	  
	 for(IAction action : acts)
	  symProd.addAction(action); 
	 
	 //store the parameters for later
	  $Model::productionParameters.put(production, params);
	 
	 //add to the model
	 model.getProceduralModule().addProduction(production);
	};

conditions returns[Collection<ICondition> rtn]
@init{
 rtn = new ArrayList<ICondition>();
} 
	:	^(CONDITIONS (c=check {rtn.add(c);}
	            |q=query {rtn.add(q);}
	            |s=scriptCond {rtn.add(s);}
	            |p=proxyCond {rtn.add(p);}
	            )+);

actions returns[Collection<IAction> rtn]
@init{
 rtn = new ArrayList<IAction>();
}
 :	^(ACTIONS (a=add {rtn.add(a);}
                  |s=set {rtn.add(s);}
                  |r=remove {rtn.add(r);}
                  |m=modify {rtn.add(m);}
                  |sc=scriptAct {rtn.add(sc);}
                  |p=proxyAct {rtn.add(p);}
                  |o=output {rtn.add(o);}
                  )+);

/**
 can return ChunkTypeCondition, ChunkCondition, or VariableCondition
*/
check returns[IBufferCondition rtn]
@init{
 rtn = null;
 String bufferName = null;
}
	:	^(MATCH_CONDITION n=NAME {bufferName = $n.text;}
 	              ((c=CHUNK_IDENTIFIER
{
 try
  {
   IChunk chunk = $Model::model.getDeclarativeModule().getChunk($c.text).get();
   if(chunk!=null)
     rtn = new ChunkCondition(bufferName, chunk);
   else
    throw new BuilderError($c.text+" is not a valid chunk", $c); 
  }
  catch(Exception e)
  {
   LOGGER.error("Could not get chunk "+$c.text, e);
  }
}
 	              | ct=CHUNK_TYPE_IDENTIFIER
{
  try
   {
    IChunkType chunkType = $Model::model.getDeclarativeModule().getChunkType($ct.text).get();
    if(chunkType!=null)
     rtn = new ChunkTypeCondition(bufferName, chunkType);
    else
     throw new BuilderError($ct.text+" is not a valid chunktype", $ct);
   }
   catch(Exception e)
   {
    LOGGER.error("Could not get chunktype "+$ct.text, e);
   } 
} 	              
 	              | v=VARIABLE
{
 rtn = new VariableCondition(bufferName, $v.text);
} 	               
 	              ) 
 	              (sl=slots
{
	IChunkType newIsa = null;
	IChunkType isA = (rtn instanceof ChunkTypeCondition) ? ((ChunkTypeCondition)rtn).getChunkType() : null;
	for(ISlot slot : sl) {
	  try
	   {
	   LOGGER.debug("seeing if slot " + slot + " is an isa so we can fold it in.");
    	    if(slot.getName().equals(":isa") && isA != null) { 
    	    	newIsa = $Model::model.getDeclarativeModule().getChunkType(slot.getValue().toString()).get();
	        if(isA.isA(newIsa)) newIsa = null;
    	     } else ((ISlotContainer)rtn).addSlot(cleanupSlot(slot));
           } catch(Exception e)
   	    {
		    LOGGER.error("Could not get chunktype "+slot.getValue(), e);
		    ((ISlotContainer)rtn).addSlot(cleanupSlot(slot));
	    }
    	  
	}
	if(newIsa != null){
	//with multiple inheritance, this test is incorrect
	 //if(!newIsa.isA(((ChunkTypeCondition)rtn).getChunkType()) && !((ChunkTypeCondition)rtn).getChunkType().isA(newIsa)) {
	 //	throw new BuilderError(", $ct);
	 //} else 
	   if(newIsa.isA(isA))
	 	 rtn = new ChunkTypeCondition(rtn.getBufferName(), newIsa, ((ChunkTypeCondition)rtn).getRequest().getSlots());
	}
} 	              
 	              )?)?)
{
  if(rtn==null)
   rtn = new ChunkTypeCondition(bufferName, null);
};


unknownList 
	:	^(UNKNOWN .*);
/*
 NAME is the buffer name
*/
query returns [QueryCondition rtn]
@init{
 rtn = null;
}
:	^(QUERY_CONDITION n=NAME sl=slots)
{
 rtn = new QueryCondition($n.text);
 for(ISlot slot : sl)
  rtn.addSlot(cleanupSlot(slot));
};

scriptCond  returns[ScriptableCondition rtn]
@init{
 rtn = null;
}
	:	^(SCRIPTABLE_CONDITION l=LANG s=SCRIPT)
	{
	 //we currently dont support lang..
	 try
	  {
	   IScriptableFactory factory = ScriptingManager.getFactory($l.text);
	   if(factory==null)
	    throw new BuilderError("Could not find scripting engine for " + $l.text, $l);
	    
	   IConditionScript script = factory.createConditionScript($s.text);
	   rtn = new ScriptableCondition(script);
	  }
	  catch(Exception e)
	  {
	   throw new BuilderError("Could not compile script because "+e.getMessage(), $s, e);
	  }
	};
	
proxyCond returns [ProxyCondition rtn]
@init{
 rtn = null;
}
:	^(PROXY_CONDITION c=CLASS_SPEC (sl=slots)?)
         {
          try
          {
           rtn = new ProxyCondition($c.text);
           if(sl!=null)
            for(ISlot slot : sl)
             rtn.addSlot(cleanupSlot(slot));
          }
          catch(Exception e)
          {
           throw new BuilderError("Could not create proxy condition"+$c.text, $c, e);
          }
         };
	
		
add returns[AddAction rtn]
@init{
 rtn = null;
 String bufferName = null;
 Object ref = null;
}
:	^(a=ADD_ACTION n=NAME {bufferName = $n.text;}
 	              (c=CHUNK_IDENTIFIER
{
try
  {
   ref = $Model::model.getDeclarativeModule().getChunk($c.text).get();
   if(ref==null)
     throw new BuilderError($c.text+" is not a valid chunk", $c);
  }
  catch(Exception e)
  {
   LOGGER.error("Could not get chunk "+$c.text, e);
  }
} 	              
 	              | ct = CHUNK_TYPE_IDENTIFIER
{
try
   {
    ref = $Model::model.getDeclarativeModule().getChunkType($ct.text).get(); 
    if(ref==null)
     throw new BuilderError($ct.text+" is not a chunktype", $ct);
   }
   catch(Exception e)
   {
    LOGGER.error("Could not get chunktype "+$ct.text,e);
   } 
} 	              
 	              | v=VARIABLE 
{
 ref = $v.text;
} 	              
 	              )  (sl=slots)?)
{
 IChunkType newIsa = null;
 IChunkType isA = (IChunkType) ((ref instanceof IChunkType) ? ref : null);
 rtn = new AddAction(bufferName, ref);
 
 if(sl != null) 
 {
   for(ISlot slot : sl) 
   {
    try 
    {
    /**
     retrieval buffer can take a :isa chunktype or :isa not chunktype
    */
     if(slot.getName().equals(ISlot.ISA) && isA != null) 
     {
	if(bufferName.equals(IActivationBuffer.RETRIEVAL)) 
	 rtn.addSlot(cleanupSlot(slot));
	else
	{
	  newIsa = $Model::model.getDeclarativeModule().getChunkType(slot.getValue().toString()).get();
	  IConditionalSlot cSlot = (IConditionalSlot) slot;
	  if(cSlot.getCondition() == IConditionalSlot.EQUALS) 
	  {
	    if(!isA.isA(newIsa)) 
	    {
	      if(newIsa.isA(isA)) 
	      {
	        rtn.setChunkType(newIsa);
	        isA = newIsa;
	      }
	      else 
	       LOGGER.warn("can't handle multiple inheritance yet, so can't specify add of both " + newIsa + " and " + isA);
	    }
	 } 
	 else
	 {
	  throw new BuilderError("Cannot handle :isa not in a buffer other than retrieval", $a);
	 }
	}
       } 
       else 
	 rtn.addSlot(cleanupSlot(slot));
      } 
      catch(Exception e) 
      {
	LOGGER.error("Could not get chunktype "+slot.getValue(), e);
	rtn.addSlot(cleanupSlot(slot));
      } 
   }
  }
  
};

		
set returns[SetAction rtn]
@init{
 rtn = null;
 String bufferName = null;
 Object ref = null;
}
:	^(a=SET_ACTION n=NAME {bufferName = $n.text;}
 	              (c=CHUNK_IDENTIFIER
{
try
  {
   ref = $Model::model.getDeclarativeModule().getChunk($c.text).get();
   if(ref==null)
     throw new BuilderError($c.text+" is not a valid chunk", $c);
  }
  catch(Exception e)
  {
   LOGGER.error("Could not get chunk "+$c.text, e);
  }
} 	                            
 	              | v=VARIABLE 
{
 ref = $v.text;
} 	              
 	              )?  (sl=slots)?)
{
 rtn = new SetAction(bufferName, ref);
 if(sl!=null)
  for(ISlot slot : sl)
   rtn.addSlot(cleanupSlot(slot));  
};


remove returns[RemoveAction rtn]
@init{
 rtn = null;
}
:	^(r=REMOVE_ACTION n=NAME 
 	              (i=IDENTIFIER
 	              | v=VARIABLE 
 	              )? (sl=slots)?)
{
 String bufferName = $n.text;
 if($i!=null || $v!=null)
  reportException(new BuilderWarning("jACT-R core does not currently support remove action precise specifications", (($i==null)?$v:$i)));
 
 rtn = new RemoveAction(bufferName);
 if(sl!=null)
  for(ISlot slot : sl)
   rtn.addSlot(cleanupSlot(slot)); 
};

modify returns[ModifyAction rtn]
@init{
 rtn = null;
}
:	^(MODIFY_ACTION n=NAME sl=slots?)
{
 String bufferName = $n.text;
 rtn = new ModifyAction(bufferName);
 if(sl!=null)
 for(ISlot slot: sl)
  rtn.addSlot(cleanupSlot(slot));
};

scriptAct returns[ScriptableAction rtn]
@init{
 rtn = null;
}
	:	^(root=SCRIPTABLE_ACTION l=LANG s=SCRIPT)
	{
	 //we currently dont support lang..
	 try
	  {
	   IScriptableFactory factory = ScriptingManager.getFactory($l.text);
	   if(factory==null)
	    throw new BuilderError("Could not find scripting engine for "+ $l.text, $l);
	    
	  IActionScript script = factory.createActionScript($s.text);
	  rtn = new ScriptableAction(script);
	  }
	  catch(Exception e)
	  {
	   throw new BuilderError("Could not compile script because "+e.getMessage(), $s, e);
	  }
	};

proxyAct returns [ProxyAction rtn]
@init{
 rtn = null;
}
:	^(PROXY_ACTION c=CLASS_SPEC (sl=slots)?)
         {
          try
          {
           rtn = new ProxyAction($c.text);
           if(sl!=null)
            for(ISlot slot : sl)
             rtn.addSlot(cleanupSlot(slot));
          }
          catch(Exception e)
          {
           throw new BuilderError("Could not create proxy action"+$c.text, $c, e);
          }
         };

output returns[OutputAction rtn]
@init{
 rtn = null;
}
	:	^(OUTPUT_ACTION s=STRING)
	{
	 rtn = new OutputAction($s.text);
	};

parents returns [Collection<IChunkType> pl]
@init{
 pl = new ArrayList<IChunkType>();
 }:     (^(PARENTS 
 		(p=PARENT {	   
 			String parentName = $p.getText();
        	 	IChunkType parentType = $Model::knownChunkTypes.get(parentName.toLowerCase());
           	 	if(parentType==null)
            			throw new BuilderError("Could not find chunk-type "+parentName, $p);
            	 	else pl.add(parentType);
            	 	}
            	 )+) | PARENTS);

slots 	returns [Collection<ISlot> sl]
@init{
 sl =  new ArrayList<ISlot>();
}:	(^(SLOTS (s=slot {sl.add(s);}
		|	l=logic {sl.add(l);}
		)+) | SLOTS); //tp hack

/**
 much like buffers, this should be ^(PARAMETERS ..) but it produces parse errors
*/
parameters returns [Collection<CommonTree> params]
@init
{
 $params = new ArrayList<CommonTree>();
}
	:	(^(PARAMETERS (p=parameter {$params.add(p);})+) | PARAMETERS) //tp hack
	{
	};

parameter returns [CommonTree param]
@init{
 $param = null;
}
	:	^(p=PARAMETER NAME STRING)
	{
	 $param = $p;
	};

/** 
 create a logical slot
 **/
 
logic	returns [ISlot ls]
@init{
  ls = null;
}
:	^(l=LOGIC (v=AND|v=OR|v=NOT) (s1=logic|s1=slot) (s2=logic|s2=slot)?)
{
	LOGGER.debug("got a logical slot " + v + " " + s1 + " " + s2);
	
	int op = -1;
 	switch($v.type)
 	{
  		case AND : op = ILogicalSlot.AND; break;
  		case OR : op = ILogicalSlot.OR; break;
  		case NOT : op = ILogicalSlot.NOT; break;
  		default : reportException(new BuilderError("Cannot have logical slot of type " + $v.type, $l));
 	}
 	try {
		ls = new DefaultLogicalSlot(op, s1, s2);
	} catch(CannotInstantiateException e) {
		reportException(new BuilderError("Can only put slots as arguments of DefaultLogicalSlot", $l));
	}
};

/**
 create an unresolved slot 
*/
slot	returns [ISlot sl]
@init{
 sl=null;
}
:	^(s=SLOT (n=NAME|n=VARIABLE) (c=EQUALS|c=GT|c=GTE|c=LT|c=LTE|c=NOT|c=WITHIN) 
                      (v=IDENTIFIER
                      |v=VARIABLE|v=STRING|v=NUMBER))
{
 LOGGER.debug("got slot def "+$n.text+" "+$c.text+" "+$v.text);
 String slotName = $n.text;
 int condition = IConditionalSlot.EQUALS;
 switch($c.type)
 {
  case GT : condition = IConditionalSlot.GREATER_THAN; break;
  case GTE : condition = IConditionalSlot.GREATER_THAN_EQUALS; break;
  case LT : condition = IConditionalSlot.LESS_THAN; break;
  case LTE : condition = IConditionalSlot.LESS_THAN_EQUALS; break;
  case NOT : condition = IConditionalSlot.NOT_EQUALS; break;
  case WITHIN : condition = IConditionalSlot.WITHIN; break;
  default : condition = IConditionalSlot.EQUALS;
 }
 
 if($c.type==WITHIN)
  reportException(new BuilderError("Within is not currently supported", $c));
 

 Object value = null;
 if($v.type == NUMBER)
 try
 {
  value = Double.parseDouble($v.text);
 }
 catch(NumberFormatException nfe)
 {
  reportException(new BuilderWarning("Could not create number from "+$v.text+" assuming its a string", $v, nfe));
  value = $v.text;
 }
 else if($v.type == STRING)
 {
  //we pass a stringBuilder if it is to be a string to differentiate
  // it from an unresolved string reference
  value = new StringBuilder($v.text);
  //value = $v.text;
 }
 else
  {
   //we will attempt resolution.. but we may still need to do it again later
   //first we try the chunks
   try
   {
    value = $Model::model.getDeclarativeModule().getChunk($v.text).get();
   }
   catch(Exception e)
   {
    LOGGER.error("Could not get chunk "+$v.text, e);
   }
   
   if(value==null) //then chunktypes
    try
    {
     value = $Model::model.getDeclarativeModule().getChunkType($v.text).get();
    }
    catch(Exception e)
    {
     LOGGER.error("Could not get chunktype "+$v.text,e);
    } 
    
   if(value==null) //then production??
    try
    {
     value = $Model::model.getProceduralModule().getProduction($v.text).get(); 
    }
    catch(Exception e)
    {
     LOGGER.error("Could not get production "+$v.text,e);
    } 
    
   //if we are still null, then we need to hold
   // on for resolution later
   if(value==null)
    value = resolveKeywords($v.text);
  }
  if(slotName.equalsIgnoreCase("isa")) {
 	//special case here
 	LOGGER.error("testing ISA conditions; condition is " + condition + " and value isa " + value.getClass());
 	if (!(value instanceof IChunkType)) {
 		reportException(new BuilderError("isa slot test must have a chunk-type as a value.", $v));
 	}
 }
 
  if($n.type==VARIABLE)
   sl = new DefaultVariableConditionalSlot(slotName, condition, value);
  else
   sl = new DefaultConditionalSlot(slotName, condition, value);
 
 if(LOGGER.isDebugEnabled())
  LOGGER.debug("created slot "+sl);
};
