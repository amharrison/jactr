grammar Lisp;


options {
	output=AST;
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
	
	CLASS_SPEC;
	NAME;
	PARENT;
	
	UNKNOWN;
}

 
scope ModelGlobals{
 List<CommonTree> misplacedChunks;
 Map<String, CommonTree> chunksWrapperMap;
 Map<String, CommonTree> temporaryChunkTypesMap;
 Map<String, CommonTree> chunkMap;
 CommonTree model;
 boolean moduleImported;
}

scope Suppress{
 boolean warnings;
 boolean errors;
}

//so that the lexer gets the correct packaging as well
@lexer::header{
package org.jactr.io.antlr3.parser.lisp;
import org.jactr.io.antlr3.parser.AbstractModelParser;
}
 
//this is for the parser.. 
@parser::header{
package org.jactr.io.antlr3.parser.lisp;

import org.jactr.io.antlr3.builder.*;
import org.jactr.io.antlr3.compiler.*;
import org.jactr.io.antlr3.misc.*;
import org.jactr.io.antlr3.parser.AbstractModelParser;
import org.jactr.io.parser.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jactr.core.module.IModule;
}

@lexer::members{
private AbstractModelParser _parser;

public void setModelParser(AbstractModelParser parser)
{
 _parser = parser;
}

public void reportError(RecognitionException re)
{
 _parser.reportException(re,true);
 super.reportError(re);
}

public void displayRecognitionError(String[] tokenNames,RecognitionException e)
{
 //noop
}
										
}

@parser::members{

static private final transient Log LOGGER = LogFactory.getLog(LispParser.class);

static private Set<String> IGNORE_PARAMETERS;;

static
{
 IGNORE_PARAMETERS = new HashSet<String>();
 IGNORE_PARAMETERS.add(":trace-detail");
 IGNORE_PARAMETERS.add(":v");
}


private ASTSupport _support = new ASTSupport();
private AbstractModelParser _parser;

public void setModelParser(AbstractModelParser parser, CommonTreeAdaptor adaptor)
{
 _parser = parser;
 setTreeAdaptor(adaptor);
 _support.setTreeAdaptor(adaptor);
}

public void reportError(RecognitionException re)
{
 reportException(re);
 super.reportError(re);
}

public void reportException(Exception e)
{
 _parser.reportException(e, false);
}

public void displayRecognitionError(String[] tokenNames,RecognitionException e)
{
 //noop
}

 boolean addChunkToChunkType(ModelGlobals_scope global, CommonTree chunk)
 {
  String chunkTypeName =  ASTSupport.getFirstDescendantWithType(chunk, PARENT).getText().toLowerCase(); 
  CommonTree chunksWrapper = global.chunksWrapperMap.get(chunkTypeName);
  if(chunksWrapper==null)
  {
   LOGGER.debug("Could not find "+chunkTypeName+" checking pseudo chunktypes");
   CommonTree chunkType = global.temporaryChunkTypesMap.get(chunkTypeName);
   if(chunkType==null)
    {
     LOGGER.debug("creating pseudo chunktype : "+chunkTypeName);
     chunkType = _support.createChunkTypeTree(chunkTypeName, null);
     global.temporaryChunkTypesMap.put(chunkTypeName, chunkType);
    } 
   LOGGER.debug("using pseudo chunktype : "+chunkTypeName);  
   chunksWrapper = ASTSupport.getFirstDescendantWithType(chunkType, CHUNKS);
  }
  
  if(chunksWrapper!=null)
   {
    LOGGER.debug("Adding "+chunk.toStringTree());
    chunksWrapper.addChild(chunk);
    return true;
   }
  return false;
 }
 
/**
 attempt to set the parameters specified in parameterList to the objects
 contained in container
*/ 
protected void setParameters(CommonTree parameterList, Map<String, CommonTree> containers)
{
 /*
  parameterList should have an even length if it is to be applied to
  all
  
  if odd, the first parameter is the name of the object
 */
 Collection<CommonTree> targets = new ArrayList<CommonTree>();
 int firstParameter = 0;
 if(parameterList.getChildCount()\%2 == 1)
 {
  CommonTree identifierNodes = (CommonTree) parameterList.getChild(0);
  ArrayList<CommonTree> identifiers = new ArrayList<CommonTree>();
  
  if(identifierNodes.getChildCount()==0)
   identifiers.add(identifierNodes);
  else
   for(int i=0;i<identifierNodes.getChildCount();i++)
    identifiers.add((CommonTree)identifierNodes.getChild(i)); 
  
  for(CommonTree identifier : identifiers)
  {
   String idName = identifier.getText().toLowerCase();
   if(!containers.containsKey(idName))
   {
    reportException(new CompilationWarning(idName+" is not a known object ", identifier));
    return;
   }
   targets.add(containers.get(idName));
  }
  firstParameter = 1;
 }
 else
 {
  targets.addAll(containers.values());
 }
 
 /*
  we have all the targets, now let's zip through the parameter list in pairs
 */
 for(int i=firstParameter;i<parameterList.getChildCount();i++)
 {
  CommonTree parameterName = (CommonTree) parameterList.getChild(i);
  CommonTree parameterValue = (CommonTree) parameterList.getChild(++i);
  
  String lispParamName = parameterName.getText();
  if(!IGNORE_PARAMETERS.contains(lispParamName))
  {
   String jactrParamName = ParameterMap.getJACTRParameterName(lispParamName);
   if(jactrParamName==null)
    {
     reportException(new CompilationWarning(lispParamName+" is not a recognized parameter name", parameterName));
     jactrParamName = lispParamName;
    }
   if(LOGGER.isDebugEnabled())
    LOGGER.debug("Translated "+lispParamName+" to "+jactrParamName); 
  
   //set the parameter for the targets
   for(CommonTree target : targets)
    {
     String pValue = parameterValue.toStringTree();
     if(LOGGER.isDebugEnabled())
      LOGGER.debug("Setting "+target.getFirstChildWithType(NAME).getText()+"."+jactrParamName+" to "+pValue);
     _support.setParameter(target, jactrParamName, pValue, true);
    }
  }
  else
   if(LOGGER.isDebugEnabled())
    LOGGER.debug("Ignoring parameter "+lispParamName);   
 }
 
}
 
}



model 	
scope ModelGlobals;
@init{
//initialize the global
$ModelGlobals::misplacedChunks = new ArrayList<CommonTree>();
$ModelGlobals::chunksWrapperMap = new HashMap<String, CommonTree>();
$ModelGlobals::chunkMap = new HashMap<String, CommonTree>();
$ModelGlobals::temporaryChunkTypesMap = new HashMap<String,CommonTree>();
 
CommonTree model = null;
Collection<CommonTree> chunkTypeList = new ArrayList<CommonTree>();
Collection<CommonTree> productionList = new ArrayList<CommonTree>();
ArrayList<CommonTree> sgpList = new ArrayList<CommonTree>();
ArrayList<CommonTree> sdpList = new ArrayList<CommonTree>();
ArrayList<CommonTree> sppList = new ArrayList<CommonTree>();
}
:	( clearAll /*| unknownList */ )* 
                 OPEN_TOKEN 'define-model' name
                 {
                 /*
                  auto import the modules..
                 */
                  model = _support.createModelTree($name.text);
                  $ModelGlobals::model = model;
                 }
                 (module)*
                 {
                  //have we imported anything? if we have, don't auto import
                  if(!$ModelGlobals::moduleImported)
                  try
                   {
                    //auto import 
                    CommonTree modules = ASTSupport.getFirstDescendantWithType(model, MODULES);
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.declarative.six.DefaultDeclarativeModule6.class.getName(), true));
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.procedural.six.DefaultProceduralModule6.class.getName(), true));    
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.procedural.six.learning.DefaultProceduralLearningModule6.class.getName(), true));    
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4.class.getName(), true));                    
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.imaginal.six.DefaultImaginalModule6.class.getName(), true));                                       
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.goal.six.DefaultGoalModule6.class.getName(), true));
                    modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, org.jactr.core.module.retrieval.six.DefaultRetrievalModule6.class.getName(), true));
                    //_parser.getImportDelegate().importInto($ModelGlobals::model, org.jactr.modules.pm.visual.six.DefaultVisualModule6.class.getName(), true);
                   }
                  catch(Exception e)
                  {
                   reportException(e);
                  } 
                   
                 }
                 (extension)*
                 (importDirective)*
                 {
                 //strip values..
                   Map<String, CommonTree> importedChunks = ASTSupport.getMapOfTrees(model, CHUNK);
                   for(String chunkName : importedChunks.keySet())
                    {
                     chunkName = chunkName.toLowerCase();
                     LOGGER.debug("importing "+chunkName);
                     $ModelGlobals::chunkMap.put(chunkName, importedChunks.get(chunkName));
                    }
                   //and chunk type wrappers
                   Map<String, CommonTree> importedWrappers = ASTSupport.getMapOfTrees(model, CHUNK_TYPE);
                   for(String chunkTypeName : importedWrappers.keySet())
                    {
                     chunkTypeName = chunkTypeName.toLowerCase();
                     LOGGER.debug("importing ct "+chunkTypeName);
                     CommonTree wrapper = ASTSupport.getFirstDescendantWithType(importedWrappers.get(chunkTypeName), CHUNKS);
                     $ModelGlobals::chunksWrapperMap.put(chunkTypeName, wrapper);
                    } 
                 }
                 
			( (ct=chunkType {chunkTypeList.add(ct.tree);})
		        | (p=production {productionList.add(p.tree);}) 
		        | addDm 
			  | (dParam=sdp {sdpList.add(dParam.tree);}) 
			  | (gParam=sgp {sgpList.add(gParam.tree);}) 
			  | (pParam=spp {sppList.add(pParam.tree);}) 
			  | goalFocus 
			  /*| unknownList */
			  )*
			  CLOSE_TOKEN 
			   {
			   
			   CommonTree decMem = ASTSupport.getFirstDescendantWithType(model, DECLARATIVE_MEMORY);
			   CommonTree procMem = ASTSupport.getFirstDescendantWithType(model, PROCEDURAL_MEMORY);
			   
			   for(CommonTree chunkTypeNode : chunkTypeList)
			   	decMem.addChild(chunkTypeNode);
			   	
			/*
			  double check the orphaned chunks
			*/
			Collection<CommonTree> del = new ArrayList<CommonTree>();
			for(CommonTree chunk : $ModelGlobals::misplacedChunks)
			 if(addChunkToChunkType((ModelGlobals_scope)ModelGlobals_stack.peek(), chunk))
			   del.add(chunk);
			$ModelGlobals::misplacedChunks.removeAll(del);
			
			if($ModelGlobals::misplacedChunks.size()!=0)
			 {
			  //**** an error condition this chunks are orphans
			  throw new CompilationError("Chunks "+$ModelGlobals::misplacedChunks+" have no known chunktypes",null);
			 }
			 
			/*
			 all the chunks are taken care of.. let's do some parameter mapping
			*/ 
			for(CommonTree decParam : sdpList)
			 setParameters(decParam, $ModelGlobals::chunkMap);
			 
			 
			Map<String, CommonTree> productionMap = new HashMap<String,CommonTree>();
			for(CommonTree productionNode : productionList)
			   	{
				 productionMap.put(((CommonTree)productionNode.getChild(0)).getText().toLowerCase(), productionNode);
			   	 procMem.addChild(productionNode);
			   	}
			/*
			 now let's do the production parameters
			*/
			for(CommonTree prodParam : sppList)
			 setParameters(prodParam, productionMap);
			 
			/*
			 and finally the global parameters
			*/
			Map<String, CommonTree> modelMap = new HashMap<String, CommonTree>();
			modelMap.put($name.text, model);
			for(CommonTree globalParam : sgpList)
			 setParameters(globalParam, modelMap);    	
		}
	   -> 
	   ^({model}); 


module 	:	 OPEN_TOKEN 'module' s=string (i=NO_IMPORT_TOKEN)? CLOSE_TOKEN
               {
                 boolean importContents = true;
                 if(i!=null)
                  {
                  importContents = false;
                  }  
                 String className = $s.tree.getText().trim();
                 //className = className.substring(1, className.length()-1);
                 try
                 {
                  CommonTree modules = ASTSupport.getFirstDescendantWithType($ModelGlobals::model, MODULES);
                  modules.addChild(_parser.getImportDelegate().importModuleInto($ModelGlobals::model, className, importContents));
                  $ModelGlobals::moduleImported=true;
                 }
                 catch(Exception e)
                 {
                   if(e instanceof CommonTreeException)
                     {
                      ((CommonTreeException)e).setStartNode($s.tree);
                      reportException(e);
                     }
                   else  
                    reportException(new CompilationError("Could not access class "+className, $s.tree, e));
                 }
                 
               }
	;



extension 	:	 OPEN_TOKEN 'extension' s=string CLOSE_TOKEN
               {
                 String className = $s.tree.getText().trim();
                 try
                 {
                  CommonTree extensions = ASTSupport.getFirstDescendantWithType($ModelGlobals::model, EXTENSIONS);
                  extensions.addChild(_parser.getImportDelegate().importExtensionInto($ModelGlobals::model, className, true));
                 }
                 catch(Exception e)
                 {
                   if(e instanceof CommonTreeException)
                     {
                      ((CommonTreeException)e).setStartNode($s.tree);
                      reportException(e);
                     }
                   else  
                    reportException(new CompilationError("Could not access class "+className, $s.tree, e));
                 }
               }
	;
	
importDirective 
	:	OPEN_TOKEN  'import' s=string CLOSE_TOKEN
	{
	 String url = "";
	 if(s!=null)
	  url = $s.text;
	  
	 /*
	  first we try to get it from the class loader
	 */
	 //URL location = getClass().getClassLoader().getResource(url);
	 URL location = _parser.getImportDelegate().resolveURL(url, _parser.getBaseURL());
	 if(location==null)
	    reportException(new CompilationError("Could not resolve url "+url+" using base "+_parser.getBaseURL(), s.tree));
	 else
	 try
	  {
	   _parser.getImportDelegate().importInto($ModelGlobals::model, location, false);
	   /*
	    having just imported a slew of chunks,types and productions, we do need to update
	    our internal tables, particular the chunkTypeWrappers
	   */
	   //Map<String, CommonTree> chunkTypes = ASTSupport.getMapOfTrees($ModelGlobals::model, CHUNK_TYPE);
	   //for(Map.Entry<String,CommonTree> chunkType : chunkTypes.entrySet())
	   //{
	    //$ModelGlobals::chunksWrapperMap.put(chunkType.getKey(), ASTSupport.getFirstDescendantWithType(chunkType.getValue(), CHUNKS));
	   //}
	  }
	  catch(Exception e)
	  {
	   reportException(new CompilationError("Could not import from "+location+", "+e.getMessage(), s.tree, e));
	  } 
	};	

/**
 clearAll command is effectively ignored
*/
clearAll 
	:	 OPEN_TOKEN 'clear-all' CLOSE_TOKEN;

goalFocus 
	:	o=OPEN_TOKEN 'goal-focus' id=chunkIdentifier c=CLOSE_TOKEN
	{
	 Map<String, CommonTree> buffers = ASTSupport.getMapOfTrees($ModelGlobals::model, BUFFER);
	 if(buffers.containsKey("goal"))
	  {
	   CommonTree buffer = buffers.get("goal");
	   CommonTree chunks = (CommonTree) buffer.getFirstChildWithType(CHUNKS);
	   chunks.addChild($id.tree);
	  }
	 else
	 {
	  CompilationWarning warning = new CompilationWarning("Goal buffer was not found", (CommonTree)adaptor.create($o));
	  warning.setEndNode((CommonTree)adaptor.create($c));
	  reportException(warning);
	 }
	};
	
	
sgp
scope Suppress;
@init{
 $Suppress::warnings = true;
 Collection<CommonTree> order = new ArrayList<CommonTree>();
 CommonTree rtn = null;
}
	: o=OPEN_TOKEN ('sgp') ( i=slotValue {order.add(i.tree);}
	                     /* | u=unknownList {order.add(u.tree);} */
	                      )+ CLOSE_TOKEN 
	  {
	   rtn = (CommonTree) adaptor.create(UNKNOWN, $o); 
	   for(CommonTree child : order)
	    rtn.addChild(child);
	  }
	-> ^({rtn});

sdp
scope Suppress;
@init{
 $Suppress::warnings = true;
 Collection<CommonTree> order = new ArrayList<CommonTree>();
 CommonTree rtn = null;
}
	: o=OPEN_TOKEN ('sdp') ( i=slotValue {order.add(i.tree);}
	                      /* | u=unknownList {order.add(u.tree);} */
	                      )+ CLOSE_TOKEN 
	  {
	   rtn = (CommonTree) adaptor.create(UNKNOWN, $o); 
	   for(CommonTree child : order)
	    rtn.addChild(child);
	  }
	-> ^({rtn});
spp 
scope Suppress;
@init{
 $Suppress::warnings = true;
 Collection<CommonTree> order = new ArrayList<CommonTree>();
 CommonTree rtn = null;
}
	: o=OPEN_TOKEN ('spp') ( i=slotValue {order.add(i.tree);}
	                      /* | u=unknownList {order.add(u.tree);} */
	                      )+ CLOSE_TOKEN 
	  {
	   rtn = (CommonTree) adaptor.create(UNKNOWN, $o); 
	   for(CommonTree child : order)
	    rtn.addChild(child);
	  }
	-> ^({rtn});


unknownList 
@init{
 Collection<CommonTree> order = new ArrayList<CommonTree>();
 CommonTree rtn = null;
}
	:	o=OPEN_TOKEN (v=slotValue {order.add(v.tree);}
	                     | u=unknownList {order.add(u.tree);}
	                     )+ c=CLOSE_TOKEN
	{
	 boolean warn = true;
	 try{
	  warn = ! $Suppress::warnings;
	 }
	 catch(Exception npe)
	 {
	  warn = true;
	 }
	 if(warn)
	  {
	   CompilationWarning warning = new CompilationWarning("Command is unknown", (CommonTree)adaptor.create($o));
	   warning.setEndNode((CommonTree)adaptor.create($c));
	   reportException(warning);
	  }
	  
	 rtn = (CommonTree) adaptor.create(UNKNOWN, $o); 
	 for(CommonTree child : order)
	  rtn.addChild(child);
	}
	 -> ^({rtn});


addDm
@init{
 Collection<CommonTree> chunks = new ArrayList<CommonTree>();
} 
	:	OPEN_TOKEN 'add-dm' (c=chunk {chunks.add(c.tree);})+ CLOSE_TOKEN 
	{
	//test 
	for(CommonTree chunkNode : chunks)
	 {
		Map<String, CommonTree> chunkMap = $ModelGlobals::chunkMap;
				//check for a name collision
		String chunkName = null;
		try
		{
		chunkName = ASTSupport.getFirstDescendantWithType(chunkNode, NAME).getText().toLowerCase();
		}
		catch(NullPointerException npe)
		{
		 reportException(new CompilationError("Problem while processing chunk "+chunkNode.toStringTree(), chunkNode));
		}
		
		if(chunkMap.containsKey(chunkName))
		 reportException(new CompilationError(chunkName+" has already been defined", chunkNode));

		chunkMap.put(chunkName, chunkNode);
		
		if(!addChunkToChunkType((ModelGlobals_scope)ModelGlobals_stack.peek(), chunkNode))
		  {
		   $ModelGlobals::misplacedChunks.add(chunkNode);
		   //***** log a warning that there is no chunktype ddefined yet
		  } 
	 }
	};

chunk 	
@init{
 //treeAdaptor no longer supports createToken.. but commonAdaptor does
 CommonTree defaultSlots = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(SLOTS,"slots"));
}
:	o=OPEN_TOKEN name chunkParent (s=slots)? CLOSE_TOKEN ->^(CHUNK[$o,"chunk"] name chunkParent {((s==null)?(defaultSlots):s.tree)} PARAMETERS);

/**
 chunktype catches (chunk-type name slot1 slot2 slot3)
*/
chunkType
@init{
 CommonTree chunks = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(CHUNKS,"chunks"));
 CommonTree defaultSlots = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(SLOTS,"slots"));
}
	: o=OPEN_TOKEN ('chunk-type') name chunkTypeParent? (s=shortSlotDefs | l=longSlotDefs)? CLOSE_TOKEN
	 {
	  //so that we can keep track of the chunktypes and their chunks wrapper
	  String chunkTypeName = $name.text.toLowerCase();
	  //this test is here so that we can pass the unit test w/ model
	  if($ModelGlobals.size()!=0)
 	    $ModelGlobals::chunksWrapperMap.put(chunkTypeName.toLowerCase(), chunks);
 	    
 	  //check the temporary chunktypes
 	  if($ModelGlobals::temporaryChunkTypesMap.containsKey(chunkTypeName))
 	   {
 	    LOGGER.debug(chunkTypeName+" has been inserted as a temporary chunktype, stripping contents and disposing");
 	    CommonTree tmp = $ModelGlobals::temporaryChunkTypesMap.get(chunkTypeName);
 	    Collection<CommonTree> tmpChunks = ASTSupport.getTrees(tmp, CHUNK);
 	    for(CommonTree tmpChunk : tmpChunks)
 	     {
 	       LOGGER.debug("Adding "+tmpChunk.toStringTree());
 	       chunks.addChild(tmpChunk);
 	     }
 	   }
 	   
 	   if(s!=null)
 	    defaultSlots = (CommonTree) $s.tree;
 	   else
 	   if(l!=null)
 	    defaultSlots = (CommonTree) $l.tree; 
	 }
	 -> 
	 ^(CHUNK_TYPE[$o,"chunk-type"] name chunkTypeParent? {defaultSlots}  {chunks} PARAMETERS ) ;

production :      o=OPEN_TOKEN ('p' | 'P') name  conditions '==>' actions CLOSE_TOKEN -> ^(PRODUCTION[$o, "production"] name conditions actions PARAMETERS);

actions 
	:	rhs+ -> ^(ACTIONS rhs+);
	
rhs 	:	(addBuffer | modifyBuffer | removeBuffer | output | stop | evalAction | bind);

	
/*
 conditions is not a straight forward collect and rewrite since
 we need to check any matches for ':slotName' which is a signal to expand
 the match into a match and a query
*/		
conditions 
@init{
 CommonTree conditionsNode = (CommonTree) adaptor.create(CONDITIONS, "lhs");
 Collection<CommonTree> bufferSlots = new ArrayList<CommonTree>(2);
}
	:   (l=lhs {
	     //no matter what, add this as a child
	     conditionsNode.addChild($l.tree);
	     
	     if($l.tree.getType()==MATCH_CONDITION)
	     {
	      //we may need to expand.. lets check the slots
	      CommonTree slotsContainer = ASTSupport.getFirstDescendantWithType($l.tree, SLOTS);
	      
	      //no slots?
	      if(slotsContainer==null) continue;
	      
	      //iterate through the slots..
	      for(int i=0;i<slotsContainer.getChildCount();i++)
	      {
	       CommonTree slot = (CommonTree) slotsContainer.getChild(i);
	       //check the name
	       String name = slot.getChild(0).getText();
	       if(name.startsWith(":"))
	        {
	         //store and remove
	         slotsContainer.deleteChild(i);
	         bufferSlots.add(slot);
	         
	         //while we're at it, lets strip the ':'
	         name = name.substring(1);
	         ((CommonTree)slot.getChild(0)).getToken().setText(name);
	        }
	      }
	      
	      //we need to create a query, we dont bother merging them if one already exists
	      if(bufferSlots.size()!=0)
	      {
	       String bufferName = $l.tree.getChild(0).getText();
	       CommonTree query = _support.createQueryTree(bufferName);
	       slotsContainer = (CommonTree) query.getChild(1);
	       for(CommonTree slot : bufferSlots)
	        slotsContainer.addChild(slot);
	        
	       //and add the query
	       conditionsNode.addChild(query);
	       
	       bufferSlots.clear();
	      }	      
	     }
	   }
	   )+
	   -> {conditionsNode};	

lhs 	:	queryBuffer | checkBuffer | evalCondition | bind;

/**
 create either the standard match or a proxy condition
*/
checkBuffer 
@init{
 boolean isProxy = false;
 String className = null;
}
	:	/* e=EQUALS_TOKEN name {isProxy="proxy".equalsIgnoreCase($name.text);}
	         GT_TOKEN */
	         n=MATCH_TOKEN {isProxy="proxy".equalsIgnoreCase($n.text);}
	          b=bufferContent conditionalSlots? 
	        -> {isProxy}? ^(PROXY_CONDITION[$n,"proxy"] CLASS_SPEC[((CommonTree)b.tree).token] conditionalSlots?) 
	        -> ^(MATCH_CONDITION[$n,"match"] NAME[$n] bufferContent conditionalSlots?);

/**
 create either the standard add or a proxy add
*/
addBuffer 
@init{
 boolean isProxy = false;
}
	:	 /* p=PLUS_TOKEN name {isProxy="proxy".equalsIgnoreCase($name.text);}
	         GT_TOKEN  */
	         n=ADD_TOKEN {isProxy="proxy".equalsIgnoreCase($n.text);}
	          (b=bufferContent) conditionalSlots? 
	         -> {isProxy}? ^(PROXY_ACTION[$n, "proxy"] CLASS_SPEC[((CommonTree)b.tree).token] conditionalSlots?)
	         -> ^(ADD_ACTION[$n,"add"] NAME[$n] bufferContent conditionalSlots?);
/**
 we need to support three options
 "touch"  =buffer>
 "overwrite" =buffer> =identifier
 standard =buffer> slotName slotValue
*/	
modifyBuffer 
options{backtrack=true;}
@init{boolean isOverwrite=false;}
	:	/* (e=EQUALS_TOKEN name GT_TOKEN) //common */
	         n=MATCH_TOKEN
	         (
	           ((var | identifier) {isOverwrite=true;})
	           | slots
	         )?
	         -> {isOverwrite}? ^(ADD_ACTION[$n,"add"] NAME[$n] var? identifier?) 
	         -> ^(MODIFY_ACTION[$n,"modify"] NAME[$n] slots?);
  finally{
    if(isOverwrite)
     reportException(new CompilationWarning("Overwrites not fully support, overwritten chunk will be encoded", retval.tree));
  }	         
	

queryBuffer 
	:	/* q=QUESTION_TOKEN name GT_TOKEN  */
	        n=QUERY_TOKEN
	        conditionalSlots? -> ^(QUERY_CONDITION[$n,"query"] NAME[$n] conditionalSlots?);
	
removeBuffer 
	:	 /*m=MINUS_TOKEN name GT_TOKEN */ n=REMOVE_TOKEN /*bufferContent slots?*/ ->^(REMOVE_ACTION[$n,"remove"] NAME[$n] /*bufferContent? slots?*/);
		

slotCondition 
	:    GT_TOKEN -> GT |
		GTE_TOKEN -> GTE |
		LT_TOKEN -> LT |
		LTE_TOKEN -> LTE |
		MINUS_TOKEN -> NOT |
		WITHIN_TOKEN -> WITHIN;

/*
 the contents of a buffer action/condition
 =goal>
  isa chunk
  or
  chunkName
  or 
  =variableName
*/
bufferContent 
	:	(chunkIdentifier | isaType | var | string);

slotValue 	:	(number | identifier | var | string);

conditionalSlot : slotCondition name  slotValue  -> ^(SLOT["slot"] name slotCondition slotValue) ;

equalSlot 	: name slotValue  -> ^(SLOT["slot"] name EQUALS slotValue);

slot 	:	equalSlot;

cSlot 	:	 slot | conditionalSlot;
conditionalSlots 
	:	(cSlot)+ -> ^(SLOTS["slots"]  (cSlot)+ ) ;	
	
slots 	:	(slot)+  -> ^(SLOTS["slots"]  slot+ ) ;	

shortSlotDefs 
	:	(shortSlot)+ -> ^(SLOTS["slots"] shortSlot*);

shortSlot :	name -> ^(SLOT["slot"] name EQUALS IDENTIFIER["null"]);

longSlot 
	:	OPEN_TOKEN! equalSlot CLOSE_TOKEN!;

longSlotDefs 
	:	(longSlot)+ -> 	^(SLOTS["slots"] longSlot*);



output 
scope Suppress;
@init{
 $Suppress::warnings=true;
 CommonTree string=null;
}	: '!output!'  list=unknownList 
{
 String str = $list.tree.toStringTree();
 str = str.replace("(","");
 str = str.replace(")", "");
 string = (CommonTree) adaptor.create(STRING, $list.tree.getToken(), str.trim());
}
 -> ^(OUTPUT_ACTION {string});


bind 	:	'!' ('bind'|'BIND')'!' list=unknownList
{
 CompilationError error = new CompilationError("Bind is not currently supported in lisp", $list.tree);
 error.setEndNode(ASTSupport.getLastDescendant($list.tree));
 reportException(error);
}
-> ^(UNKNOWN);

/**
 we support a limited set of evals, specifically, all string
 which are thn turned into scripables
*/
evalCondition
scope Suppress;
@init{
	  $Suppress::warnings = true;
	  StringBuilder script = new StringBuilder();
	  CommonTree langNode = null; 
	  CommonTree scriptNode = null;
	  boolean failed = false;
} 
	:	'!'('eval'|'EVAL')'!' list=unknownList
	{
	  Token firstScriptToken = null;
	  for(int i=0;i<$list.tree.getChildCount();i++)
	   {
	    CommonTree child = (CommonTree) $list.tree.getChild(i);
	    if(child.getType()!=STRING)
	     {
	      CompilationError error = new CompilationError("!eval!s only support all string contents currently", $list.tree);
	      error.setEndNode(ASTSupport.getLastDescendant($list.tree));
	      reportException(error);
	      failed = true;
	     }  
	     
	    if(failed) break; 
	    
	    if(langNode==null) 
	     langNode = (CommonTree) adaptor.create(LANG, child.getToken());
	    else
	     {
	      /*
	       append to the script string after a few tweaks
	      */
	      if(firstScriptToken==null)
	       firstScriptToken = child.getToken();
	       
	      String line = child.getText();
	      line = line.replace("\\\"","\"");
	      script.append(line).append("\n");
	     }  
	   }
	   scriptNode = (CommonTree) adaptor.create(SCRIPT, firstScriptToken, script.toString()); 
	} 
	-> {failed}? ^(UNKNOWN)
	-> ^(SCRIPTABLE_CONDITION["scriptableCondition"] {langNode} {scriptNode} );
	
/**
 we support a limited set of evals, specifically, all string
 which are thn turned into scripables
*/
evalAction
scope Suppress;
@init{
	  $Suppress::warnings = true;
	  StringBuilder script = new StringBuilder();
	  CommonTree langNode = null; 
	  CommonTree scriptNode = null;
	  boolean failed = false;
} 
	:	'!'('eval'|'EVAL')'!' list=unknownList
	{
	  Token firstScriptToken = null;
	  for(int i=0;i<$list.tree.getChildCount();i++)
	   {
	    CommonTree child = (CommonTree) $list.tree.getChild(i);
	    if(child.getType()!=STRING)
	     {
	      CompilationError error = new CompilationError("!eval!s only support all string contents currently", $list.tree);
	      error.setEndNode(ASTSupport.getLastDescendant($list.tree));
	      reportException(error);
	      failed=true;
	      break;
	     }
	    
	    
	    if(langNode==null) 
	     langNode = (CommonTree) adaptor.create(LANG, child.getToken());
	    else
	     {
	      /*
	       append to the script string after a few tweaks
	      */
	      if(firstScriptToken==null)
	       firstScriptToken = child.getToken();
	       
	      String line = child.getText();
	      line = line.replace("\\\"","\"");
	      script.append(line).append("\n");
	     }  
	   }
	   scriptNode = (CommonTree) adaptor.create(SCRIPT, firstScriptToken, script.toString()); 
	} 
	-> {failed}? ^(UNKNOWN)
	-> ^(SCRIPTABLE_ACTION["scriptableAction"] {langNode} {scriptNode} );
	
	
	
	
stop 	:	('!stop!'|'!STOP!') -> ^(PROXY_ACTION["proxyAction"] CLASS_SPEC["org.jactr.core.production.action.StopAction"]);

chunkParent 	:	ISA_TOKEN n=IDENTIFIER_TOKEN -> ^(PARENT[$n]);

name 	:	n=IDENTIFIER_TOKEN -> ^(NAME[$n]);

chunkTypeParent 
	:	OPEN_TOKEN INCLUDE_TOKEN n=IDENTIFIER_TOKEN CLOSE_TOKEN -> ^(PARENT[$n]);

isaType :	ISA_TOKEN id=IDENTIFIER_TOKEN  -> ^(CHUNK_TYPE_IDENTIFIER[$id]) ;	

var  	:	v=VARIABLE_TOKEN -> ^(VARIABLE[$v]);

identifier 
	:	id=IDENTIFIER_TOKEN  -> ^(IDENTIFIER[$id]) ;

chunkIdentifier 
	:	id=IDENTIFIER_TOKEN  -> ^(CHUNK_IDENTIFIER[$id]) ;	
	
number 	:	num=NUMBER_TOKEN  -> ^(NUMBER[$num]) ;

string 	:	str=STRING_TOKEN  -> ^(STRING[$str]);


REMOVE_TOKEN 
	:	'-' id=IDENTIFIER_TOKEN '>' {setText($id.text);};
	
ADD_TOKEN 
	:	'+' id=IDENTIFIER_TOKEN '>' {setText($id.text);};
	
QUERY_TOKEN 
	:	'?' id=IDENTIFIER_TOKEN '>' {setText($id.text);};		


VARIABLE_TOKEN 
	:	'='IDENTIFIER_TOKEN;
	
MATCH_TOKEN 
	:	'=' id=IDENTIFIER_TOKEN '>' {setText($id.text);};
		
	
ISA_TOKEN :	(('i'|'I')('s'|'S')('a'|'A'));

NO_IMPORT_TOKEN 
	:	 ('n'|'N')('o'|'O')'-'('i'|'I')('m'|'M')('p'|'P')('o'|'O')('r'|'R')('t'|'T');

QUESTION_TOKEN :	'?';
EQUALS_TOKEN  :	'=';
PLUS_TOKEN 	:	'+';
MINUS_TOKEN 	:	'-';

INCLUDE_TOKEN 
	:	':'('i'|'I')('n'|'N')('c'|'C')('l'|'L')('u'|'U')('d'|'D')('e'|'E');

 GT_TOKEN 	:	'>';
 GTE_TOKEN 	:	'>=';
 LT_TOKEN	:	'<';
 LTE_TOKEN	:	'<=';
 WITHIN_TOKEN 	:	'<>';

SL_COMMENT
	:	';' (options {greedy=false;} : .)* ('\r')? '\n'
		{$channel=HIDDEN;}
	;

// multiple-line comments


ML_COMMENT
	:	'#|'
		( options {greedy=false;} : . )*
		'|#'
		{$channel=HIDDEN;}
	;
	

IDENTIFIER_TOKEN 
    : ( '_' | ':' | '*' | LETTER_TOKEN  )+ 
       (  DIGITS_TOKEN | 
          LETTER_TOKEN | 
          PUNCTUATION_FRAGMENT 
          | '-' 
          | '+'
        )*
	;

/* everything but - (since we need it for numbers/remove) and + (for add)
*/
fragment 	
PUNCTUATION_FRAGMENT 
	:	 ('_' | ':' | '*' | '.' | ',' | '$' | '^' | '&' | '%' | '/');	
	
STRING_TOKEN
    :
      '\"'
      ( ~('\"'|'\\') | ESCAPE_TOKEN)*
      '\"'
      {
       String str = getText();
       setText(str.substring(1, str.length()-1));
      }  ;

fragment
ESCAPE_TOKEN
    :	'\\' 'b'
    |   '\\' 't'
    |   '\\' 'n'
    |   '\\' 'f'
    |   '\\' 'r'
    |   '\\' '\"'
    |   '\\' '\''
    |   '\\' '\\';
    

WS_TOKEN	:	(   ' '
       	|   '\t'
        |  ( '\n'
            |	'\r'
            )
        )+
        { $channel=HIDDEN; }
	;


NUMBER_TOKEN 
	:	 ('-')? DIGITS_TOKEN* ('.' DIGITS_TOKEN+)?;

fragment LETTER_TOKEN	: 'a'..'z' 
	| 'A'..'Z'
	;

fragment DIGITS_TOKEN : ('0'..'9');
		
OPEN_TOKEN 
	:	'('; 

CLOSE_TOKEN 
	:	')';
