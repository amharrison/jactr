tree grammar JACTRCompiler;


options {
	ASTLabelType=CommonTree;
	tokenVocab=JACTRBuilder;
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
	LOGIC;
	OR;
	AND;
	
	CLASS_SPEC;
	NAME;
	PARENT;
	PARENTS;
	
	UNKNOWN;
}


scope Library{
 Map<String, CommonTree> knownChunkTypes;
 Map<String, CommonTree> knownChunks;
 
 Map<String, CommonTree> knownBufferMap;
 Set<String> knownProductions;
 
 Map<String, CommonTree> encounteredChunkTypes;
 
 
 Collection<CommonTree> identifiersPendingResolution;
}

scope Slots{
 String slotDefiner;
 //will be case insensitive
 Set<String> currentSlotNames;
}

scope VariableBindings{
 /**
  used within a production this is a mapping
  of buffer names to valid slot names pulled
  from Slots scope currentSlotNames
 */
 Map<String, Set<String>> validSlotNames;
 Set<String> validVariables;
 boolean hasScriptable;
}


@header{
package org.jactr.io.antlr3.compiler;
import org.jactr.io.antlr3.misc.*;
import org.jactr.io.compiler.IUnitCompiler;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
}

@members{
static private final transient Log LOGGER = LogFactory.getLog(JACTRCompiler.class);

private Collection<Exception> _warnings = new ArrayList<Exception>();
private Collection<Exception> _errors = new ArrayList<Exception>();
private Collection<Exception> _infos = new ArrayList<Exception>();

private Map<Integer, Collection<IUnitCompiler>> _unitCompilerMap;

public void setUnitCompilerMap(Map<Integer, Collection<IUnitCompiler>> map)
{
 _unitCompilerMap = map;
}

protected void delegate(CommonTree node)
{
 if(node==null) return;
 if(_unitCompilerMap==null) return;
 
 Collection<IUnitCompiler> compilers = _unitCompilerMap.get(node.getType());
 if(compilers!=null)
  for(IUnitCompiler compiler : compilers)
   compiler.compile(node, _infos, _warnings, _errors);
}

public void reportError(RecognitionException re)
{
 LOGGER.debug(re.getMessage()+":"+re.line+","+re.c+" "+re.token);
 reportException(re);
}

public void reportException(Exception e)
{
 LOGGER.debug("",e);
 
 if(e instanceof CompilationWarning)
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


protected Set<String> getCurrentSlotsFromChunkTypeName(CommonTree chunkTypeNameNode, Map<String, CommonTree> allChunkTypes)
{
 Set<String> rtn = new TreeSet<String>();
 
 String chunkType = chunkTypeNameNode.getText().toLowerCase();
 
 LOGGER.debug("Getting slots for "+chunkType);
 
 CommonTree chunkTypeNode = allChunkTypes.get(chunkType);
 if(chunkTypeNode==null)
  throw new CompilationError("Could not find chunk-type "+chunkType, chunkTypeNameNode);
 
 CommonTree slotsNode = (CommonTree) chunkTypeNode.getFirstChildWithType(SLOTS); 
 
 for(int i=0;i<slotsNode.getChildCount();i++)
 {
  CommonTree slotNode = (CommonTree) slotsNode.getChild(i);
  String slotName = ASTSupport.getFirstDescendantWithType(slotNode, NAME).getText();
  rtn.add(slotName.toLowerCase());
 }
 
      CommonTree parentsNode = (CommonTree) chunkTypeNode.getFirstChildWithType(PARENTS);
     for(int i = 0; i < parentsNode.getChildCount(); i++)
     {
    	 CommonTree parent = (CommonTree) parentsNode.getChild(i);
    	 rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
    	 LOGGER.debug("Getting my parent's slots "+parent.getText());
     }
     
 //CommonTree parent = (CommonTree) chunkTypeNode.getFirstChildWithType(PARENT);
 //LOGGER.debug("Got parent "+parent);
 
 //if(parent!=null)
  //{
   //LOGGER.debug("Getting my parent's slots "+parent.getText());
   //rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
  //} 
  
 LOGGER.debug(chunkType+" has the following slots "+rtn); 
 return rtn;
}

protected Set<String> getCurrentSlotsFromChunkName(CommonTree chunkNameNode, Map<String, CommonTree> allChunks, Map<String, CommonTree> allChunkTypes)
{
 String chunk = chunkNameNode.getText().toLowerCase();
 CommonTree chunkNode = allChunks.get(chunk);
 if(chunkNode==null)
  throw new CompilationError("Could not find chunk "+chunk, chunkNameNode);
 
 Set<String> rtn = new TreeSet<String>();
 CommonTree parent = (CommonTree) chunkNode.getFirstChildWithType(PARENT);
 if(parent!=null)
  rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
 return rtn;
}

protected Set<String> getCurrentSlotNames(CommonTree chunkOrTypeNode, Map<String, CommonTree> allChunks, Map<String, CommonTree> allChunkTypes)
{
 try
 {
  return getCurrentSlotsFromChunkName(chunkOrTypeNode, allChunks, allChunkTypes);
 }
 catch(CompilationError ce1)
 {
  try
   {
    return getCurrentSlotsFromChunkTypeName(chunkOrTypeNode, allChunkTypes);
   }
  catch(CompilationError ce2)
   {
    reportException(ce1);
    reportException(ce2);
    return new TreeSet<String>();
   }  
 }
}

/**
 check the slots defined against those that can be defined, optionally taking care of
 any variable slot names
*/
protected void validateSlotNames(String slotDefiner, Set<String> validSlotNames, Collection<CommonTree> referencedSlots, Set<String> definedVariables)
{
 if(LOGGER.isDebugEnabled()) LOGGER.debug("Valid slots for "+slotDefiner+" "+validSlotNames);
 for(CommonTree slot : referencedSlots)
 {
  CommonTree id = (CommonTree) slot.getChild(0);
  String slotName = id.getText().toLowerCase();
  String value = slotName; 
  
  if(value.startsWith("="))
   value = value.substring(1,value.length());
   
  if(id.getType()==VARIABLE)
  {
   if(!definedVariables.contains(value))
   {
    reportException(new CompilationError(String.format("Could not resolve variable slot name \%s = \%s. Slots: \%s, Variables: \%s", slotName, value, validSlotNames, definedVariables), id));
    continue;
   }
  }
  else
   if(!slotName.startsWith(":") && !validSlotNames.contains(slotName))
    reportException(new CompilationError(slotName+" is not a valid slot for "+slotDefiner+". Possible :"+validSlotNames, id));
 }
}



protected void validateVariables(Set<String> definedVariables, Collection<CommonTree> referencedSlots, boolean canDefine, boolean hasScriptable)
{
 for(CommonTree slot : referencedSlots)
  {
  /*
   the commented out bit below only works for variable values, we
   actually need all the variables, in case of variable slot name
  */
   //CommonTree variable = ASTSupport.getFirstDescendantWithType(slot, VARIABLE);
   //if(variable!=null)
   for(CommonTree variable : ASTSupport.getAllDescendantsWithType(slot, VARIABLE))
   {
    /*
     the slot defines a variable
    */
    String variableName = variable.getText();
    variableName = variableName.substring(1, variableName.length()).toLowerCase();
    
    if(!definedVariables.contains(variableName))
     if(canDefine)
      definedVariables.add(variableName);
     else
     if(hasScriptable)
      reportException(new CompilationWarning(variableName+" was not bound in the left hand side, assuming it was defined in the scriptable condition", variable));
     else
      reportException(new CompilationError(variableName+" was not bound in the left hand side, valid variables "+definedVariables, variable));
   }
  }
}

    private String partialStream(String[] stream, int index) {
    	String result = "";
    	for(int i = index+1; i < stream.length; i++) {
    		result += stream[i] + " ";
    	}
    	return result;
    }
    
}



model
scope Library;
@init{

 $Library::knownBufferMap = new HashMap<String, CommonTree>();
 $Library::knownChunkTypes = new HashMap<String, CommonTree>();
 $Library::encounteredChunkTypes = new HashMap<String, CommonTree>();
 $Library::knownChunks = new HashMap<String, CommonTree>();
 $Library::identifiersPendingResolution = new ArrayList<CommonTree>();
 $Library::knownProductions = new TreeSet<String>();
}
	:	^(m=MODEL name=NAME modules extensions buffers library parameters)
{
  LOGGER.debug("got model def for "+$name.text);
  delegate($name);
  
  /*
   check the contents of the buffers
  */
  for(CommonTree buffer : $Library::knownBufferMap.values())
  {
   Map<String, CommonTree> chunks = ASTSupport.getMapOfTrees(buffer, CHUNK_IDENTIFIER);
   for(String chunkName : chunks.keySet())
    if(!$Library::knownChunks.containsKey(chunkName.toLowerCase()))
     reportException(new CompilationError(chunkName+" is not a known chunk", chunks.get(chunkName.toLowerCase())));
  }
  
  /*
   now take care of the final resolutions
  */
  for(CommonTree id : $Library::identifiersPendingResolution)
   {
    String idName = id.getText().toLowerCase();
    if(!(idName.equalsIgnoreCase("nil") || idName.equalsIgnoreCase("null") || idName.equalsIgnoreCase("t")
      || idName.equalsIgnoreCase("true") || idName.equalsIgnoreCase("false")))
     if(!$Library::knownChunks.containsKey(idName) 
     && !$Library::knownChunkTypes.containsKey(idName) 
     && !$Library::knownProductions.contains(idName))
      reportException(new CompilationError("Unknown chunk "+idName, id));
   }
   
   delegate($m);
};

library
:	^(l=LIBRARY
{ 
  /*
   snag all the known chunk types. this needs to be done before the compilation checks
   on the chunktypes. As such, we snag everyone here..
  */
  $Library::knownChunkTypes.putAll(ASTSupport.getMapOfTrees($l, CHUNK_TYPE));
 }
 declarativeMemory proceduralMemory) {delegate($l);};

declarativeMemory 
	:	(^(d=DECLARATIVE_MEMORY  chunkType+) | d=DECLARATIVE_MEMORY) //hack recommend by TP v3ea9
	{
	 delegate($d);
	};

proceduralMemory
	:	(^(p=PROCEDURAL_MEMORY (production)+) | p=PROCEDURAL_MEMORY)
	{
	 delegate($p);
	};

modules :	( ^(MODULES module+) | MODULES); //hack recommend by TP

module 	:	^(m=MODULE c=CLASS_SPEC parameters)
{
 LOGGER.debug("Got module "+$c.text);
 //check the class Name
 //String className = $c.text;
 //try
 //{
 // getClass().getClassLoader().loadClass(className);
 //}
 //catch(Exception e)
 //{
 // reportException( new CompilationError("Could not load class "+className, $c, e));
 //}
 delegate($m);
};

extensions 
	:	(^(EXTENSIONS extension+) | EXTENSIONS);

extension 
	:	^(ex=EXTENSION c=CLASS_SPEC parameters)
{
 LOGGER.debug("Got extension "+$c.text);
  //check the class Name
 //String className = $c.text;
 //try
 //{
 // getClass().getClassLoader().loadClass(className);
 //}
 //catch(Exception e)
 //{
 // reportException( new CompilationError("Could not load class "+className, $c, e));
 //}
 delegate($ex);
};

/**

*/	
buffers
:	(^(BUFFERS  buffer+) | BUFFERS)
{
 LOGGER.debug("got buffers tag");
};

buffer 	: ^(b=BUFFER name=NAME chunks parameters)
{
 delegate($name);
 LOGGER.debug("got buffer name "+$name.text);
 $Library::knownBufferMap.put($name.text.toLowerCase(), $b);
 delegate($b);
};	
    
chunkType 
scope Slots;
@init{
 $Slots::currentSlotNames = new TreeSet<String>();
}
	:	^(c=CHUNK_TYPE n=NAME 
	{
	 delegate($n);
	 
	 String chunkTypeName = $n.text.toLowerCase();
	 if($Library::encounteredChunkTypes.containsKey(chunkTypeName))
	  reportException(new CompilationWarning(chunkTypeName+" is an already defined chunk-type, redefining", $c));
	 
	 LOGGER.debug("indexing chunkType "+chunkTypeName);
	 $Library::encounteredChunkTypes.put(chunkTypeName, $c); 
	 
	 $Slots::currentSlotNames.addAll(getCurrentSlotsFromChunkTypeName($n, $Library::knownChunkTypes));
	 $Slots::slotDefiner = $n.text;
	}
	parents slots 
	chunks parameters)
	{
	 delegate($c);
	};

/**
 using the chunk identifier pending resolution here, we can get a free verification for the 
 buffer definition above.
*/
chunks 	:	( ^(CHUNKS (chunk|
              cid=CHUNK_IDENTIFIER {$Library::identifiersPendingResolution.add($cid);})+ ) | CHUNKS );

chunk	:	^(c=CHUNK n=NAME p=PARENT slots parameters)
{
delegate($n);
delegate($p);

 LOGGER.debug("got chunk def "+$n.text+" isa "+$p.text);
 String chunkName = $n.text.toLowerCase();
 String chunkTypeName = $p.text.toLowerCase();
 
 if(!$Library::knownChunkTypes.containsKey(chunkTypeName))
  reportException(new CompilationError(chunkTypeName+" is not a valid chunk-type", $p));
  
 //check for overlap of chunk 
 if($Library::knownChunks.containsKey(chunkName))
  reportException(new CompilationWarning(chunkName+" is already defined, redefining",$n));
  
 LOGGER.debug("indexing chunk "+chunkName);  
 $Library::knownChunks.put(chunkName, $c); 
 
 //check the slots defined against the known slots
 
 //we can't access the slots ANTLR rule directly.. so we'll use the tree to get what we need
 validateSlotNames($Slots::slotDefiner, $Slots::currentSlotNames, ASTSupport.getAllDescendantsWithType($c, SLOT), Collections.EMPTY_SET);
 
 delegate($c);
};

production
scope VariableBindings;
@init{
 $VariableBindings::validSlotNames = new HashMap<String, Set<String>>();
 $VariableBindings::validVariables = new TreeSet<String>();
}
	:	^(p=PRODUCTION n=NAME conditions actions parameters)
	{
	delegate($n);
	 String name = $n.text.toLowerCase();
	 LOGGER.debug("Got a production def "+name);
	 if($Library::knownProductions.contains(name))
	  reportException(new CompilationWarning("production "+name+" is already defined", $p));
	 $Library::knownProductions.add(name);
	 delegate($p);
	};

conditions 
	:	^(c=CONDITIONS (check|query|scriptCond|proxyCond)+) {delegate($c);};

actions :	^(a=ACTIONS (add|set|remove|modify|scriptAct|proxyAct|output)+) {delegate($a);};

check
scope Slots;
@init{
 $Slots::currentSlotNames = new TreeSet<String>();
}
 	:	^(m=MATCH_CONDITION n=NAME 
 	              ((c=CHUNK_IDENTIFIER {$Slots::slotDefiner=$c.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($c, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | ct=CHUNK_TYPE_IDENTIFIER {$Slots::slotDefiner=$ct.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($ct, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | v=VARIABLE 
 	              ) 
 	               slots?)?
 	              )
{
 delegate($n);
 delegate($c);
 delegate($ct);
 delegate($v);

	LOGGER.debug("ABOUT TO START CHECK"); 
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
   
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($m, SLOT);
  validateVariables($VariableBindings::validVariables, definedSlots, true, $VariableBindings::hasScriptable);
  
  $VariableBindings::validVariables.add(bufferName);
   
  Set<String> validSlotNames = $Slots::currentSlotNames;
  //validSlotNames.add("isa");
  if($Slots::slotDefiner!=null)  
   {
   LOGGER.debug("ABOUT TO VALIDATE SLOT NAMES");
    validateSlotNames($Slots::slotDefiner,  validSlotNames, definedSlots,$VariableBindings::validVariables);  
    $VariableBindings::validSlotNames.put(bufferName, validSlotNames);
   }
  else if(definedSlots.size()!=0)
   reportException(new CompilationWarning("Could not infer chunktype of contents of "+bufferName+". Cannot test slot names", $m));  
   
  if($v!=null)
   {
     String varName = $v.text.toLowerCase(); //will be "=variable"
     varName = varName.substring(1, varName.length());
     if(!$VariableBindings::validVariables.contains(varName))
      reportException(new CompilationError(varName+" must be bound on the left hand side before it can be checked "+$VariableBindings::validVariables, $v));
  } 
  delegate($m);
};


unknownList 
	:	^(u=UNKNOWN .*) {delegate($u);};
/*
 NAME is the buffer name
*/
query  	:	^(q=QUERY_CONDITION n=NAME slots)
{
delegate($n);
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
  
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($q, SLOT); 
  validateVariables($VariableBindings::validVariables, definedSlots, true, $VariableBindings::hasScriptable); 
  
  delegate($q); 
};

scriptCond  
	:	^(s=SCRIPTABLE_CONDITION LANG SCRIPT) 
{
 delegate($s);
 $VariableBindings::hasScriptable = true;
};
	
proxyCond :	^(p=PROXY_CONDITION CLASS_SPEC slots?) 
{
 Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($p, SLOT); 
 validateVariables($VariableBindings::validVariables, definedSlots, true, $VariableBindings::hasScriptable); 
 delegate($p);
 $VariableBindings::hasScriptable = true;
};
	
		
add
scope Slots;
@init{
 $Slots::currentSlotNames = new TreeSet<String>();
}	:	^(a=ADD_ACTION n=NAME 
 	              (c=CHUNK_IDENTIFIER {$Slots::slotDefiner=$c.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($c, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | ct=CHUNK_TYPE_IDENTIFIER {$Slots::slotDefiner=$ct.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($ct, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | v=VARIABLE)  slots?)
{
  delegate($n);
  delegate($c);
  delegate($ct);
  delegate($v);
  
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
  
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($a, SLOT);
  validateVariables($VariableBindings::validVariables, definedSlots, false, $VariableBindings::hasScriptable);
   
  if($Slots::slotDefiner!=null)
   validateSlotNames($Slots::slotDefiner, $Slots::currentSlotNames, definedSlots,$VariableBindings::validVariables);
  else if(definedSlots.size()!=0)
   reportException(new CompilationWarning("Could not infer chunktype of contents of "+bufferName+". Cannot test slot names", $a));   
   
  Collection<CommonTree> definedLogicSlots = ASTSupport.getAllDescendantsWithType($a, LOGIC);
  
  if(!bufferName.equalsIgnoreCase("retrieval") && definedLogicSlots.size() > 0) 
   reportException(new CompilationError("Cannot have logic in +add for buffers other than retrieval", $a));
  
  if($v!=null)
   {
     String varName = $v.text.toLowerCase(); //will be "=variable"
     varName = varName.substring(1, varName.length());
     if(!$VariableBindings::validVariables.contains(varName))
      reportException(new CompilationError(varName+" must be bound on the left hand side before it can be added "+$VariableBindings::validVariables, $v));
  } 
  
  delegate($a);
};

set
scope Slots;
@init{
 $Slots::currentSlotNames = new TreeSet<String>();
}	:	^(a=SET_ACTION n=NAME 
 	              (c=CHUNK_IDENTIFIER {$Slots::slotDefiner=$c.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($c, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | v=VARIABLE)?  slots?)
{
  delegate($n);
  delegate($c);
  delegate($v);
  
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
  
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($a, SLOT);
  validateVariables($VariableBindings::validVariables, definedSlots, false, $VariableBindings::hasScriptable);
   
  if($Slots::slotDefiner!=null)
   validateSlotNames($Slots::slotDefiner, $Slots::currentSlotNames, definedSlots,$VariableBindings::validVariables);
  else if(definedSlots.size()!=0)
   reportException(new CompilationWarning("Could not infer chunktype of contents of "+bufferName+". Cannot test slot names", $a));   
   
   
  if($v!=null)
   {
     String varName = $v.text.toLowerCase(); //will be "=variable"
     varName = varName.substring(1, varName.length());
     if(!$VariableBindings::validVariables.contains(varName))
      reportException(new CompilationError(varName+" must be bound on the left hand side before it can be added "+$VariableBindings::validVariables, $v));
  } 
  
  delegate($a);
};

remove
scope Slots;
@init{
 $Slots::currentSlotNames = new TreeSet<String>();
}	:	^(r=REMOVE_ACTION n=NAME 
 	              (i=IDENTIFIER {$Slots::slotDefiner=$i.text; $Slots::currentSlotNames.addAll(getCurrentSlotNames($i, $Library::knownChunks, $Library::knownChunkTypes));}
 	              | v=VARIABLE 
 	              )? slots?)
{
delegate($n);
delegate($i);
delegate($v);
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
   
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($r, SLOT);
  validateVariables($VariableBindings::validVariables, definedSlots, false, $VariableBindings::hasScriptable);
   
  if($Slots::slotDefiner!=null)
   validateSlotNames($Slots::slotDefiner, $Slots::currentSlotNames, definedSlots,$VariableBindings::validVariables); 
   
  if($v!=null)
   {
     String varName = $v.text.toLowerCase(); //will be "=variable"
     varName = varName.substring(1, varName.length());
     if(!$VariableBindings::validVariables.contains(varName))
      reportException(new CompilationError(varName+" must be bound on the left hand side before it can be removed "+$VariableBindings::validVariables, $v));
  } 
  delegate($r);
};

modify :	^(m=MODIFY_ACTION n=NAME slots?)
{
 delegate($n);
  //check the buffer name
  String bufferName = $n.text.toLowerCase();
  if($Library.size()==0 || !$Library::knownBufferMap.containsKey(bufferName))
   reportException(new CompilationError(bufferName+" is an unknown buffer", $n));
  
  Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($m, SLOT);
  validateVariables($VariableBindings::validVariables, definedSlots, false, $VariableBindings::hasScriptable);
   
  if($VariableBindings::validSlotNames.containsKey(bufferName))
  {
   //we can check the slots..
   validateSlotNames("="+bufferName, $VariableBindings::validSlotNames.get(bufferName), definedSlots,$VariableBindings::validVariables);
  }
  else
   reportException(new CompilationError(bufferName+" must be bound on the left hand side before it can be modified", $m));
   
  delegate($m); 
};

scriptAct 
	:	^(s=SCRIPTABLE_ACTION LANG SCRIPT) 
{
 delegate($s);
 $VariableBindings::hasScriptable = true;
};

proxyAct
:	^(p=PROXY_ACTION CLASS_SPEC slots?) 
{
 Collection<CommonTree> definedSlots = ASTSupport.getAllDescendantsWithType($p, SLOT); 
 validateVariables($VariableBindings::validVariables, definedSlots, false, $VariableBindings::hasScriptable); 
 delegate($p);
 $VariableBindings::hasScriptable = true;
};

output	:	^(o=OUTPUT_ACTION s=STRING) {delegate($s); delegate($o);};

parents :	(^(p=PARENTS PARENT+) | p=PARENTS) 
{
	delegate($p);
	//if($p!=null)
	//{
	for(int i = 0; i < $p.getChildCount(); i++)
        {	
           String parentName = $p.getChild(i).getText();
           if(!$Library::knownChunkTypes.containsKey(parentName))
	           reportException(new CompilationError(parentName+" is not a defined chunk-type", $p));
	}	
};

slots 	:	(^(s=SLOTS (logic|slot)+) | s=SLOTS) {delegate($s);};

/**
 much like buffers, this should be ^(PARAMETERS ..) but it produces parse errors
*/
parameters 
	:	(^(p=PARAMETERS parameter+) | p=PARAMETERS) {delegate($p); };

parameter 
	:	^(p=PARAMETER n=NAME s=STRING) {delegate($n); delegate($s); delegate($p);};


logic
	:	^(l=LOGIC (v=AND|v=OR|v=NOT) (logic|slot) (logic|slot)?)
	{
	LOGGER.debug("got a logic def " + $v.text);
		delegate(v);
		delegate(l);
	};


slot	
:	^(s=SLOT (n=NAME|n=VARIABLE) (c=EQUALS|c=GT|c=GTE|c=LT|c=LTE|c=NOT|c=WITHIN) 
                      (v=IDENTIFIER {$Library::identifiersPendingResolution.add($v);}
                      |v=VARIABLE|v=STRING|v=NUMBER))
{
delegate($n);
delegate($v);
 LOGGER.debug("got slot def "+$n.text+" "+$c.text+" "+$v.text);
 delegate($s);
 };

