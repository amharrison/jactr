grammar JACTR;


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
	LOGIC;
	OR;
	AND;
	
	CLASS_SPEC;
	NAME;
	PARENT;
	PARENTS;
	
	UNKNOWN;
}

/*
 Used to track undefined chunks (w/ no chunktype)
 the chunks container for each chunktype
 and the temporarily genertaed chunktypes
*/
scope ModelGlobals{
 List<CommonTree> misplacedChunks;
 Map<String, CommonTree> chunksWrapperMap;
 Map<String, CommonTree> temporaryChunkTypesMap;
 CommonTree modelTree;
}


//so that the lexer gets the correct packaging as well
@lexer::header{
package org.jactr.io.antlr3.parser.xml;

import org.jactr.io.antlr3.parser.*;
}

//this is for the parser.. 
@parser::header{
package org.jactr.io.antlr3.parser.xml;

import org.jactr.io.antlr3.builder.*;
import org.jactr.io.antlr3.compiler.*;
import org.jactr.io.antlr3.misc.*;
import org.jactr.io.antlr3.parser.*;
import org.jactr.io.parser.*;
import org.jactr.core.slot.ISlot;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.net.URL;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

										
}

@parser::members{

static private final transient Log LOGGER = LogFactory.getLog(JACTRParser.class);
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
 if(LOGGER.isDebugEnabled()) LOGGER.debug(re);
 reportException(re);
 super.reportError(re);
}

public void reportException(Exception e)
{
 _parser.reportException(e, false);
}



protected void stealChildren(CommonTree newRoot, CommonTree oldTree)
{
 LOGGER.debug("oldTree has "+oldTree.getChildCount()+" children to be stolen");
 for(int i=0;i<oldTree.getChildCount();i++)
   {
    Tree node = oldTree.getChild(i);
    LOGGER.debug("moved "+node.toStringTree()+" under "+newRoot.toStringTree());
    newRoot.addChild(node);
   } 
}
}

model 	
scope ModelGlobals;
@init{
//initialize the global
$ModelGlobals::misplacedChunks = new ArrayList<CommonTree>();
$ModelGlobals::chunksWrapperMap = new HashMap<String, CommonTree>();
$ModelGlobals::temporaryChunkTypesMap = new HashMap<String, CommonTree>();
CommonTree modelTree = null;
}
:	OPEN_ACTR_TOKEN 
                OPEN_MODEL_TOKEN n=name (v=version)? LONG_CLOSE_TOKEN
                {
                 
                 modelTree = _support.createModelTree(n.tree.getText());
                 $ModelGlobals::modelTree = modelTree;
                }
                (m=modules)?
                {
                if(m!=null)
                 stealChildren(ASTSupport.getFirstDescendantWithType(modelTree, MODULES),m.tree);
                }
                (e=extensions)?
                {
                //pull the extension nodes into the defined EXTENSIONS node of modelTree
                if(e!=null)
                 stealChildren(ASTSupport.getFirstDescendantWithType(modelTree, EXTENSIONS),e.tree);
                }
                (importDirective)*
                library
                {
                 //no need to do anything here
                }
                (b=buffer
                {
                 /*we've got a bunch of buffers..
                  we can't define buffers here, but we can set parameters and contents
                  so we should only be concerned with buffers that we know about (have been defined
                  by the modules)
                 */
                 Map<String, CommonTree> knownBuffers = ASTSupport.getMapOfTrees(modelTree, BUFFER);
                 
                 
                   CommonTree bufferTree = (CommonTree) $b.tree;
                   String bName = ASTSupport.getFirstDescendantWithType(bufferTree, NAME).getText().toLowerCase();
                   CommonTree actualBufferTree = knownBuffers.get(bName);
                   if(actualBufferTree==null)
                    {
                     LOGGER.debug(bName+" is not a known buffer, adding it temporarily");
                     modelTree.getFirstChildWithType(BUFFERS).addChild(bufferTree);
                     //reportException(new CompilationWarning(bName+" is not a known buffer", bufferTree));
                    } 
                   else
                   {
                   LOGGER.debug("Attempting to swap contents of provided buffer "+bufferTree.toStringTree()+" into "+actualBufferTree.toStringTree());  
                   //snag its chunks and paramaeters
                   //stealChildren(ASTSupport.getFirstDescendantWithType(actualBufferTree, PARAMETERS),
                   //              ASTSupport.getFirstDescendantWithType(bufferTree, PARAMETERS));
                   //src, dest
                   ASTSupport.setParameters(ASTSupport.getFirstDescendantWithType(bufferTree, PARAMETERS),
                                 ASTSupport.getFirstDescendantWithType(actualBufferTree, PARAMETERS));
                   //dest, src              
                   stealChildren(ASTSupport.getFirstDescendantWithType(actualBufferTree, CHUNKS),
                                 ASTSupport.getFirstDescendantWithType(bufferTree, CHUNKS));                                 
                   }              
                })*
                (p=parameters)?
                {
                //pull the parameter nodes into the defined PARAMETERS node of modelTree
                if(p!=null)
                 stealChildren(ASTSupport.getFirstDescendantWithType(modelTree, PARAMETERS),p.tree);
                }
                CLOSE_MODEL_TOKEN
                CLOSE_ACTR_TOKEN
                ->
                ^({modelTree})
	; 

importDirective 
	:	OPEN_IMPORT_TOKEN  URL_TOKEN s=string SHORT_CLOSE_TOKEN
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
	   _parser.getImportDelegate().importInto($ModelGlobals::modelTree, location, false);
	   /*
	    having just imported a slew of chunks,types and productions, we do need to update
	    our internal tables, particular the chunkTypeWrappers
	   */
	   //Map<String, CommonTree> chunkTypes = ASTSupport.getMapOfTrees($ModelGlobals::modelTree, CHUNK_TYPE);
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

modules :   m=OPEN_MODULES_TOKEN
            module*
            CLOSE_MODULES_TOKEN
             -> ^(MODULES[$m] module*);

module 	
@init{
CommonTree moduleNode = null;
CommonTree params = (CommonTree) adaptor.create(PARAMETERS,"parameters");
}:	start=OPEN_MODULE_TOKEN c=classSpec (IMPORT_ATTR_TOKEN '=' i=string)?
                ((end1=SHORT_CLOSE_TOKEN) |
                 (LONG_CLOSE_TOKEN p=parameters end2=CLOSE_MODULE_TOKEN))
                 {
                  //handle the import
                  String className = "";
                  
                  /*
                   we are defensive here because error recovery might let the recognition exception past.
                  */
                  if(c!=null)
                   className = c.tree.getText();
                   
                  boolean importContents = true;
                  if(i!=null)  //looking like a hack in the ANTLR, time to upgrade?
                    importContents = Boolean.parseBoolean($i.text.toLowerCase());
                    
                  LOGGER.debug("Attempting import of module "+className+" and contents ("+importContents+").. IS THIS WORKING??");  
                    
                  try
                  {
                   moduleNode = _parser.getImportDelegate().importModuleInto($ModelGlobals::modelTree, className, importContents);                
                   if(p!=null)
                     ASTSupport.setParameters($p.tree, ASTSupport.getFirstDescendantWithType(moduleNode, PARAMETERS));
                  }
                  catch(Exception e)
                  {
                   LOGGER.error(e);
                  if(e instanceof CommonTreeException)
                    {
                     //mark the exception so that we know what node it occured on
                     ((CommonTreeException)e).setStartNode(c.tree);
                     reportException(e);
                    } 
                   else
                    reportException(new CompilationError("Failed to import module "+className +" because of "+e.getClass().getSimpleName(), c.tree, e));
                  }
                 }
                 ->
                 ^({moduleNode})
                 ;


extensions 
	:	e=OPEN_EXTENSIONS_TOKEN
	        extension*
	        CLOSE_EXTENSIONS_TOKEN
	        ->
	        ^(EXTENSIONS[$e] extension*);

extension 
@init{
CommonTree extNode = null;
CommonTree params = (CommonTree) adaptor.create(PARAMETERS,"parameters");
}:	e=OPEN_EXTENSION_TOKEN c=classSpec
                ((SHORT_CLOSE_TOKEN) |
                 (LONG_CLOSE_TOKEN p=parameters CLOSE_EXTENSION_TOKEN))
                 {
                 //handle the import
                  String className = "";
                  
                  /*
                   we are defensive here because error recovery might let the recognition exception past.
                  */
                  if(c!=null)
                   className = c.tree.getText();
                   
                  boolean importContents = true;
                    
                  LOGGER.debug("Attempting import of extension "+className+" and contents ("+importContents+")");  
                    
                  try
                  {
                   extNode = _parser.getImportDelegate().importExtensionInto($ModelGlobals::modelTree, className, importContents);                
                   if(p!=null)
                     ASTSupport.setParameters($p.tree, ASTSupport.getFirstDescendantWithType(extNode, PARAMETERS));
                  }
                  catch(Exception ex)
                  {
                   LOGGER.error(ex);
                  if(ex instanceof CommonTreeException)
                    {
                     //mark the exception so that we know what node it occured on
                     ((CommonTreeException)ex).setStartNode(c.tree);
                     reportException(ex);
                    } 
                   else
                    reportException(new CompilationError("Failed to import extension "+className +" because of "+ex.getClass().getSimpleName(), c.tree, ex));
                  }
                 }
                 ->
                 ^({extNode})
                 ;

library :	declarativeMemory proceduralMemory ->^(LIBRARY declarativeMemory proceduralMemory);


/**
 no need for rewrite here - the processing will insert the contents into the actual dec node
*/
declarativeMemory 
@init{
 CommonTree decNode = ASTSupport.getFirstDescendantWithType($ModelGlobals::modelTree, DECLARATIVE_MEMORY);
/*
 prepopulate the known chunktypes with the imported contents
*/
 Map<String, CommonTree> importedChunkTypes = ASTSupport.getMapOfTrees(decNode, CHUNK_TYPE);
 for(Map.Entry<String, CommonTree> chunkTypeNode : importedChunkTypes.entrySet())
  $ModelGlobals::chunksWrapperMap.put(chunkTypeNode.getKey(), ASTSupport.getFirstDescendantWithType(chunkTypeNode.getValue(), CHUNKS));  
}
	:	OPEN_DECLARATIVE_MEMORY_TOKEN
	        (ct=chunkType 
	         {
	          /*handle chunkType processing
	           we need the name and its chunks wrapper
	           we can also check its parent if availabel
	          */
	          CommonTree chunkTypeNode = (CommonTree) ct.tree;
	          
	          String chunkTypeName = null;
	          chunkTypeName = ASTSupport.getFirstDescendantWithType(ct.tree, NAME).getText().toLowerCase();
	          CommonTree chunksWrapper = ASTSupport.getFirstDescendantWithType(ct.tree, CHUNKS);
	          
	          LOGGER.debug("Indexing chunktype "+chunkTypeName+" and chunksWrapper "+chunksWrapper);

                  if($ModelGlobals::chunksWrapperMap.containsKey(chunkTypeName))
                   {
                    LOGGER.debug(String.format("\%s already exists, using local version", chunkTypeName));
                    
                    //steal the chunks from the existing
                    CommonTree existingChunkType = importedChunkTypes.get(chunkTypeName);
                    
                    stealChildren(chunksWrapper, $ModelGlobals::chunksWrapperMap.get(chunkTypeName));
                    reportException(new CompilationWarning(String.format("\%s is already defined. Redefining with local version", chunkTypeName), chunkTypeNode));
                    //remove the existing one
                    for(int i=0;i<decNode.getChildCount();i++)
            	      if(decNode.getChild(i)==existingChunkType)
            	        {
            	          LOGGER.debug("Removed old version");
            	          decNode.setChild(i, chunkTypeNode);
            	          break;
            	        }
                   }
	           else
	            decNode.addChild(chunkTypeNode);
	            
 	           $ModelGlobals::chunksWrapperMap.put(chunkTypeName, chunksWrapper);	          
	           
	          
	          if($ModelGlobals::temporaryChunkTypesMap.containsKey(chunkTypeName))
	           {
	            LOGGER.debug(chunkTypeName+" describes a pseduo chunktype. stripping its children for new chunktype");
	            CommonTree tmpCTNode = $ModelGlobals::temporaryChunkTypesMap.remove(chunkTypeName);
	            //snag all the kids
	            Collection<CommonTree> chunks = ASTSupport.getTrees(tmpCTNode, CHUNK);
	            for(CommonTree chunk : chunks)
	             {
	              if(LOGGER.isDebugEnabled())
	                LOGGER.debug("inserting "+chunk.toStringTree()+" from pseudo ct");
	              chunksWrapper.addChild(chunk);
	             }  
	           }
	         }
	        | c=chunk
	         {
	          /*handle chunk processing
	           we need the name so we can track known chunks
	           we need the chunkType name so that we can insert it 
	           into the chunkType's chunks node
	          */
	          String chunkName = ASTSupport.getFirstDescendantWithType(c.tree, NAME).getText();
	          String parentName = ASTSupport.getFirstDescendantWithType(c.tree, PARENT).getText().toLowerCase();
	          CommonTree chunksWrapper = $ModelGlobals::chunksWrapperMap.get(parentName);
	          if(chunksWrapper==null)
	           {
	            //we need to use a pseudo chunktype for now, if we read it, it will be replaced above
	            reportException( new CompilationError(parentName+" is not a known chunktype", ASTSupport.getFirstDescendantWithType(c.tree, PARENT)));
	            //check the tmpChunkTypes
	            CommonTree chunkType = $ModelGlobals::temporaryChunkTypesMap.get(parentName);
	            if(chunkType==null)
	             {
	              LOGGER.debug("Creating pseudo chunktype "+parentName);
	              chunkType = _support.createChunkTypeTree(parentName,null);
	              chunksWrapper = (CommonTree) chunkType.getFirstChildWithType(CHUNKS);
	              $ModelGlobals::temporaryChunkTypesMap.put(parentName, chunkType);
	              $ModelGlobals::chunksWrapperMap.put(parentName, chunksWrapper);
	             }
	           }
	          
	          chunksWrapper.addChild(c.tree); 
	         }
	        )*
		CLOSE_DECLARATIVE_MEMORY_TOKEN
		-> ^({decNode});
		catch[RecognitionException re]
  {
    //attempt error recovery so maintain structure integ
    reportError(re);
    recover(input,re);
  } 

proceduralMemory 
@init{
CommonTree procNode = ASTSupport.getFirstDescendantWithType($ModelGlobals::modelTree, PROCEDURAL_MEMORY);
}
	:	OPEN_PROCEDURAL_MEMORY_TOKEN 
		(p=production
		 {
		  procNode.addChild(p.tree);
		 }
		)*
		CLOSE_PROCEDURAL_MEMORY_TOKEN
		-> ^({procNode});


buffer 
@init{
CommonTree chunks = (CommonTree) adaptor.create(CHUNKS,"chunks");
CommonTree param = (CommonTree) adaptor.create(PARAMETERS,"parameters");
}	:	start=OPEN_BUFFER_TOKEN n=name (c=chunkRef)?
                {
                 if(c!=null)
                  chunks.addChild(c.tree);
                }
                ((SHORT_CLOSE_TOKEN) | 
                 (LONG_CLOSE_TOKEN p=parameters CLOSE_BUFFER_TOKEN))
                 ->
                 ^(BUFFER[$start,$n.text] name {chunks} {((p==null)?param:p.tree)});

chunkType
@init{
 CommonTree chunksNode = (CommonTree) adaptor.create(CHUNKS, "chunks");
 CommonTree slotsNode = (CommonTree) adaptor.create(SLOTS,"slots");
 CommonTree paramNode = (CommonTree) adaptor.create(PARAMETERS,"parameters");
 CommonTree parentsNode = (CommonTree) adaptor.create(PARENTS,"parents");
}
	:	c=OPEN_CHUNK_TYPE_TOKEN n=name (p=parents)?
	{
	 LOGGER.debug("Got chunktype "+n.tree.getText());
	 //check the parent           
	 if(p!=null)
	 {
	   for(int i = 0; i < p.tree.getChildCount(); i++)
           {
           String parentName = p.tree.getChild(i).getText();
	   LOGGER.debug("seeing if we know about parent name " + parentName);
	   if(!$ModelGlobals::chunksWrapperMap.containsKey(parentName))
	     reportException(new CompilationError(parentName+" is not a defined chunk-type", p.tree));
	   }
	 }
	}
	          ((SHORT_CLOSE_TOKEN) |
	           (LONG_CLOSE_TOKEN 
	            (s=slots)? 
	            (para=parameters)? 
	           CLOSE_CHUNK_TYPE_TOKEN))
	           ->
	           ^(CHUNK_TYPE[$c] name {((p==null)?parentsNode:p.tree)} {((s==null)?slotsNode:s.tree)}{chunksNode} {((para==null)?paramNode:para.tree)})
	           ; 

chunk
@init{
 CommonTree slotsNode = (CommonTree) adaptor.create(SLOTS,"slots");
 CommonTree paramNode = (CommonTree) adaptor.create(PARAMETERS,"parameters");
} 	:	start=OPEN_CHUNK_TOKEN ((n=name type) | (type n=name) )
		 ((SHORT_CLOSE_TOKEN) | 
		  (LONG_CLOSE_TOKEN (s=slots)? (para=parameters)?
		  CLOSE_CHUNK_TOKEN))
		 ->
	           ^(CHUNK[$start,$n.text] name type {((s==null)?slotsNode:s.tree)} {((para==null)?paramNode:para.tree)})
	           ;


production 
@init{
 CommonTree paramsNode = (CommonTree) adaptor.create(PARAMETERS, "parameters");
}
	:	start=OPEN_PRODUCTION_TOKEN n=name LONG_CLOSE_TOKEN
		conditions
		actions
		(param=parameters)?
		CLOSE_PRODUCTION_TOKEN
		->
		^(PRODUCTION[$start,$n.text] name conditions actions {((param==null)?paramsNode:param.tree)} );

/*
 conditions is not a straight forward collect and rewrite since
 we need to check any matches for ':slotName' which is a signal to expand
 the match into a match and a query
*/		
conditions 
@init{
 CommonTree conditionsNode = null;
 Collection<CommonTree> bufferSlots = new ArrayList<CommonTree>(2);
}
	:  start=OPEN_CONDITIONS_TOKEN 
	   {
	    conditionsNode = (CommonTree) adaptor.create(CONDITIONS, $start, "lhs");
	   }
	   (l=lhs {
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
	         if(!name.equalsIgnoreCase(":isa")) {
	         	//store and remove
	         	slotsContainer.deleteChild(i);
	         	bufferSlots.add(slot);
	         	//decrement i to skip back since deleting while reduce the childcount
	         	i--;
	         }
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
	   )*
	   CLOSE_CONDITIONS_TOKEN
	   -> {conditionsNode};
	   
lhs 	:  (match | query | scriptCond | proxyCond);	   
	
actions : start=OPEN_ACTIONS_TOKEN
	rhs*
	CLOSE_ACTIONS_TOKEN
	->
	^(ACTIONS[$start,"rhs"] (rhs)* );

rhs 	:	(add|set|modify|remove|scriptAct|proxyAct|output|stop);

emptyMatch 
	:	 start=OPEN_MATCH_TOKEN b=bufferRef  SHORT_CLOSE_TOKEN
		->
		^(MATCH_CONDITION[$start,$b.text] bufferRef );

matchShort 
	:	 start=OPEN_MATCH_TOKEN b=bufferRef (t=isa | c=chunkRef) SHORT_CLOSE_TOKEN
		->
		^(MATCH_CONDITION[$start,$b.text] bufferRef {((t==null)?c.tree:t.tree)});


matchLong 
	:	 start=OPEN_MATCH_TOKEN b=bufferRef (t=isa | c=chunkRef) LONG_CLOSE_TOKEN
		  lslots?
		 CLOSE_MATCH_TOKEN
		->
		^(MATCH_CONDITION[$start,$b.text] bufferRef {((t==null)?c.tree:t.tree)} lslots?);
		
match 	:	matchLong | matchShort | emptyMatch;		
		 
query 	:	start=OPEN_QUERY_TOKEN b=bufferRef LONG_CLOSE_TOKEN
		 slots
		CLOSE_QUERY_TOKEN
		->
		^(QUERY_CONDITION[$start,$b.text] bufferRef slots);		

		
scriptCond 
@init{
 Tree lang = (Tree) adaptor.create(LANG,"js");
}
	:	start=OPEN_SCRIPT_COND_TOKEN (l=lang)?
	        {
	         if(l!=null)
	          lang = (Tree) l.tree;
	        }
	        LONG_CLOSE_TOKEN
		cdata
		CLOSE_SCRIPT_COND_TOKEN
		->
		^(SCRIPTABLE_CONDITION[$start,"script"] {lang} cdata);

proxyCond 
	:	 start=OPEN_PROXY_COND_TOKEN classSpec
		(SHORT_CLOSE_TOKEN
		|(LONG_CLOSE_TOKEN slots? CLOSE_PROXY_COND_TOKEN))
		 ->
		 ^(PROXY_CONDITION[$start,"proxy"] classSpec slots?); 


scriptAct 
@init{
 Tree lang = (Tree) adaptor.create(LANG,"js");
}
	:	start=OPEN_SCRIPT_ACT_TOKEN (l=lang)?
        	{
	         if(l!=null)
	          lang = (Tree)l.tree;
	        }
	        LONG_CLOSE_TOKEN
		cdata
		CLOSE_SCRIPT_ACT_TOKEN
		->
		^(SCRIPTABLE_ACTION[$start,"script"] {lang} cdata);

cdata
@init{
  String scriptText = "";
}
 	:	c=CDATA_TOKEN
	{
	 scriptText = $c.text;
	 scriptText = scriptText.substring(9,scriptText.length()-3);
	}
	-> 
	^(SCRIPT[scriptText]);

proxyAct 
	:	 start=OPEN_PROXY_ACT_TOKEN classSpec
		 ((SHORT_CLOSE_TOKEN)
		  |(LONG_CLOSE_TOKEN slots? CLOSE_PROXY_ACT_TOKEN))
		 ->
		 ^(PROXY_ACTION[$start,"proxy"] classSpec slots?);


output 	:	start=OPEN_OUTPUT_TOKEN  s=string 
                CLOSE_OUTPUT_TOKEN
                ->
                ^(OUTPUT_ACTION[$start,$s.text] string);

remove 	:	start=OPEN_REMOVE_TOKEN b=bufferRef chunkRef?
		((SHORT_CLOSE_TOKEN) | 
		(LONG_CLOSE_TOKEN slots? CLOSE_REMOVE_TOKEN))
		->
		^(REMOVE_ACTION[$start,$b.text] bufferRef chunkRef? slots?);

modify 	:	start=OPEN_MODIFY_TOKEN b=bufferRef 
                ((LONG_CLOSE_TOKEN slots CLOSE_MODIFY_TOKEN) | SHORT_CLOSE_TOKEN)
                ->
                ^(MODIFY_ACTION[$start,$b.text] bufferRef slots?);

add 
	:	start=OPEN_ADD_TOKEN b=bufferRef (t=isa | c=chunkRef) 
		 ((SHORT_CLOSE_TOKEN) 
		  |
		  (LONG_CLOSE_TOKEN lslots? CLOSE_ADD_TOKEN))
		->
		^(ADD_ACTION[$start,$b.text] bufferRef {((c!=null)?c.tree:(t!=null?t.tree:null))} lslots?);
		
set 
	:	start=OPEN_SET_TOKEN b=bufferRef chunkRef
		 ((SHORT_CLOSE_TOKEN) 
		  |
		  (LONG_CLOSE_TOKEN slots? CLOSE_SET_TOKEN))
		->
		^(SET_ACTION[$start,$b.text] bufferRef chunkRef slots?);		

stop 	:	start=STOP_TOKEN
                -> 
                ^(PROXY_ACTION[$start,"stop"] CLASS_SPEC["org.jactr.core.production.action.StopAction"]);

parameters 
	:	pm=OPEN_PARAMETERS_TOKEN
	         parameter+ 
	        CLOSE_PARAMETERS_TOKEN 
	         -> ^(PARAMETERS[$pm,"parameters"] parameter+);

parameter 
	:	pm=OPEN_PARAMETER_TOKEN n=parameterName p=parameterValue SHORT_CLOSE_TOKEN
		 -> ^(PARAMETER[$pm,$n.text] parameterName parameterValue);

slots 	:	slot+ -> ^(SLOTS slot+);

lslots 	:	lslot+ -> ^(SLOTS lslot+);

version :	VERSION_ATTR_TOKEN s=STRING_TOKEN -> ^(NUMBER[$s]);

classSpec 
	:	 CLASS_ATTR_TOKEN s=string -> ^(CLASS_SPEC [((CommonTree)s.tree).token ]);
	         //CLASS_ATTR_TOKEN s=STRING -> ^(CLASS_SPEC [$s ]);

name 	:	NAME_ATTR_TOKEN 
		s=STRING_TOKEN -> ^(NAME[$s]);

//parent  :	 PARENT_ATTR_TOKEN
//	        s=STRING_TOKEN -> ^(PARENT[$s]);
	        
parents 	
@init{
 CommonTree parentsNode = (CommonTree) adaptor.create(PARENTS,"parents");
}
  :	PARENT_ATTR_TOKEN 
  	s=STRING_TOKEN 
  	{	
  		String[] parentNames = $s.text.toLowerCase().split(",");
	  	for(int i = 0; i < parentNames.length; i++) {
  			parentsNode.addChild((CommonTree)adaptor.create(PARENT, parentNames[i]));
  		}
  		LOGGER.debug("created parents tree " + parentsNode);
  	}
  	-> {parentsNode};

isa    	:	TYPE_ATTR_TOKEN s=STRING_TOKEN -> ^(CHUNK_TYPE_IDENTIFIER[$s]);

type 	:	TYPE_ATTR_TOKEN s=STRING_TOKEN -> ^(PARENT[$s]);

value 	:	VALUE_ATTR_TOKEN s=STRING_TOKEN -> ^(IDENTIFIER[$s]);

bufferRef 	:	BUFFER_ATTR_TOKEN s=STRING_TOKEN -> ^(NAME[$s]);

lang 	:	LANG_ATTR_TOKEN s=STRING_TOKEN -> ^(LANG[$s]);

parameterValue 
	:	VALUE_ATTR_TOKEN s=STRING_TOKEN -> ^(STRING[$s]);
	
parameterName 
	:	NAME_ATTR_TOKEN s=STRING_TOKEN -> ^(NAME[$s]);

	
string
:	s=STRING_TOKEN -> ^(STRING[$s]);

chunkRef 
@init{
boolean isVariable = false;
Tree idOrVar = null;
}
	:	CHUNK_ATTR_TOKEN s=string 
	{
	 String text = s.tree.getText().toLowerCase();
	 isVariable = text.startsWith("=");
	 /*
	 if(text.startsWith("="))
	   idOrVar = adaptor.create(VARIABLE, s.token);
	 else
	   idOrVar = adaptor.create(IDENTIFIER, s.token);
	 */  
	}
	-> {isVariable}? ^(VARIABLE[((CommonTree)s.tree).token])
	-> ^(CHUNK_IDENTIFIER[((CommonTree)s.tree).token]);

condition 
	:	(s=SLOT_EQ_TOKEN -> EQUALS[$s]) |
		(s=SLOT_NOT_TOKEN -> NOT[$s]) |
		(s=SLOT_GT_TOKEN -> GT[$s]) |
		(s=SLOT_GTE_TOKEN -> GTE[$s]) |
		(s=SLOT_LT_TOKEN -> LT[$s]) |
		(s=SLOT_LTE_TOKEN -> LTE[$s]) |
		(s=SLOT_WITHIN_TOKEN -> WITHIN[$s]);

lslot 	:	(logic|slot);

logic
	:       (l=OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN -> ^(LOGIC OR[$l, "or"] lslot lslot)) |
		(l=OPEN_AND_TOKEN lslot lslot CLOSE_AND_TOKEN -> ^(LOGIC AND[$l, "and"] lslot lslot)) |
		(l=OPEN_NOT_TOKEN lslot CLOSE_NOT_TOKEN -> ^(LOGIC NOT[$l, "not"] lslot));

slot 	
@init{
 Tree value = null;
 Tree sName = null;
}
:	ss=OPEN_SLOT_TOKEN 
                  slotName=name cond=condition '=' s=STRING_TOKEN 
                  {
                   //process the string to figure out what it is..
                   // choices are variable, string, number, identifier
                   String str = s.getText();
                   int type = IDENTIFIER;
                   try
                   {
                    Double.parseDouble(str);
                    type = NUMBER;
                   }
                   catch(NumberFormatException nfe)
                   {
                    //not a number
                    if(str.startsWith("'")&&str.endsWith("'"))
                     {
                      type = STRING;
                      //strip the string
                      str = str.substring(1, str.length()-1);
                     } 
                    else 
                    if(str.startsWith("="))
                     type = VARIABLE;
                   }
                   
                   //slot name may be a variable
                   if(slotName.tree.getText().startsWith("="))
                      sName = (Tree) adaptor.create(VARIABLE, slotName.tree.token);
                   else
                    sName = slotName.tree;
                   
                   value = (Tree) adaptor.create(type, s, str);
                   
                   //special circumstances here
                   //LOGGER.debug("Checking to see if isa to enforce equals condition... " + slotName.tree.getText());
                    if(slotName.tree.getText().equalsIgnoreCase(ISlot.ISA)) {
                    	 if(!(cond.tree.getText().equalsIgnoreCase("equals") || cond.tree.getText().equalsIgnoreCase("not"))) {
	   			reportException(new CompilationError("isa slot test must have equals or not as a condition", cond.tree));
			}
 			if (type != IDENTIFIER) {
 				reportException(new CompilationError("isa slot test must have a chunk-type as a value", cond.tree));
 			} else {
 				if(!$ModelGlobals::chunksWrapperMap.containsKey(s.getText().toLowerCase()))
 					reportException(new CompilationError(s.getText() + " is not a known chunktype", cond.tree));
 			}
                    }                 
                  }
                SHORT_CLOSE_TOKEN
                ->
                ^(SLOT[$ss] {sName} condition {value});		




XML_HEADER_TOKEN 
	:	'<?' (options {greedy=false;} : .)* '?>'
	{ $channel=HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/ }
	; 
	
COMMENT_TOKEN
	:	'<!--' (options {greedy=false;} : .)* '-->'
	{ $channel=HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/ }
	;

CDATA_FRAGMENT 
	:	('c'|'C')('d'|'D')('a'|'A')('t'|'T')('a'|'A');

CDATA_TOKEN 	: '<!['CDATA_FRAGMENT'['
	 (options {greedy=false;} : .)*
          ']]>';


/*
 until case insensitivity is implemented in Antlr v3, we have to
 be explicit in the token definitions
*/
fragment ACTR_FRAGMENT 
	:	('a'|'A')('c'|'C')('t'|'T')('r'|'R');
	
OPEN_ACTR_TOKEN 
	:	OPEN_FRAGMENT WS? ACTR_FRAGMENT WS? CLOSE_FRAGMENT;
	
CLOSE_ACTR_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? ACTR_FRAGMENT WS? CLOSE_FRAGMENT;		

fragment MODEL_FRAGMENT
	:	('m'|'M')('o'|'O')('d'|'D')('e'|'E')('l'|'L');

OPEN_MODEL_TOKEN
	:	OPEN_FRAGMENT WS? MODEL_FRAGMENT;
	
CLOSE_MODEL_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? MODEL_FRAGMENT WS? CLOSE_FRAGMENT;	
	
VERSION_ATTR_TOKEN 
	:	('v'|'V')('e'|'E')('r'|'R')('s'|'S')('i'|'I')('o'|'O')('n'|'N')'=';	
	
fragment NAME_FRAGMENT 
	:	('n'|'N')('a'|'A')('m'|'M')('e'|'E');
	
NAME_ATTR_TOKEN 
	:	NAME_FRAGMENT'=';	

fragment CHUNK_FRAGMENT 
	:	('c'|'C')('h'|'H')('u'|'U')('n'|'N')('k'|'K');
	
CHUNK_ATTR_TOKEN 
	:	CHUNK_FRAGMENT'=';	
	
OPEN_CHUNK_TOKEN 
	: OPEN_FRAGMENT WS? CHUNK_FRAGMENT;		

CLOSE_CHUNK_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? CHUNK_FRAGMENT WS? CLOSE_FRAGMENT;	
	
PARENT_ATTR_TOKEN
	:	('p'|'P')('a'|'A')('r'|'R')('e'|'E')('n'|'N')('t'|'T')'=';	

fragment CHUNK_TYPE_FRAGMENT 
	:	('c'|'C')('h'|'H')('u'|'U')('n'|'N')('k'|'K')'-'('t'|'T')('y'|'Y')('p'|'P')('e'|'E');

OPEN_CHUNK_TYPE_TOKEN
	:	OPEN_FRAGMENT WS? CHUNK_TYPE_FRAGMENT ;
	
CLOSE_CHUNK_TYPE_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? CHUNK_TYPE_FRAGMENT WS? CLOSE_FRAGMENT;
	
fragment PRODUCTION_FRAGMENT 
	:	('p'|'P')('r'|'R')('o'|'O')('d'|'D')('u'|'U')('c'|'C')
	('t'|'T')('i'|'I')('o'|'O')('n'|'N');	

OPEN_PRODUCTION_TOKEN 
	:	OPEN_FRAGMENT WS? PRODUCTION_FRAGMENT;

CLOSE_PRODUCTION_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? PRODUCTION_FRAGMENT WS? CLOSE_FRAGMENT;	

TYPE_ATTR_TOKEN 
	:	('t'|'T')('y'|'Y')('p'|'P')('e'|'E')'=';	
	
VALUE_ATTR_TOKEN 
	:	('v'|'V')('a'|'A')('l'|'L')('u'|'U')('e'|'E')'=';			
	
fragment PARAMETER_FRAGMENT 
	:	('p'|'P')('a'|'A')('r'|'R')('a'|'A')('m'|'M')
	('e'|'E')('t'|'T')('e'|'E')('r'|'R');	

fragment PARAMETERS_FRAGMENT 
	:	PARAMETER_FRAGMENT ('s'|'S');
	
OPEN_PARAMETER_TOKEN 
	:	 OPEN_FRAGMENT WS? PARAMETER_FRAGMENT;
	
OPEN_PARAMETERS_TOKEN 
	:	OPEN_FRAGMENT WS? PARAMETERS_FRAGMENT WS? CLOSE_FRAGMENT;
	
CLOSE_PARAMETERS_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? PARAMETERS_FRAGMENT WS? CLOSE_FRAGMENT;			
	
fragment BUFFER_FRAGMENT
	:	('b'|'B')('u'|'U')('f'|'F')('f'|'F')('e'|'E')('r'|'R');	

BUFFER_ATTR_TOKEN 
	:	BUFFER_FRAGMENT'=';	
	
OPEN_BUFFER_TOKEN
	:	OPEN_FRAGMENT WS? BUFFER_FRAGMENT;
	
CLOSE_BUFFER_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? BUFFER_FRAGMENT WS? CLOSE_FRAGMENT;	
	
fragment MATCH_FRAGMENT
	:	('m'|'M')('a'|'A')('t'|'T')('c'|'C')('h'|'H');
	
OPEN_MATCH_TOKEN 
	:	OPEN_FRAGMENT WS? MATCH_FRAGMENT;

CLOSE_MATCH_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? MATCH_FRAGMENT WS? CLOSE_FRAGMENT;		
	
fragment QUERY_FRAGMENT 
	:	('q'|'Q')('u'|'U')('e'|'E')('r'|'R')('y'|'Y');		
	
OPEN_QUERY_TOKEN
	:	OPEN_FRAGMENT WS? QUERY_FRAGMENT;

CLOSE_QUERY_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? QUERY_FRAGMENT WS? CLOSE_FRAGMENT;		
	
fragment OUTPUT_FRAGMENT 
	:	('O'|'o')('U'|'u')('T'|'t')('P'|'p')('U'|'u')('T'|'t');	
	
OPEN_OUTPUT_TOKEN 
	:	OPEN_FRAGMENT WS? OUTPUT_FRAGMENT WS? CLOSE_FRAGMENT;
	
CLOSE_OUTPUT_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? OUTPUT_FRAGMENT WS? CLOSE_FRAGMENT;	

fragment ADD_FRAGMENT 
	:	('A'|'a')('D'|'d')('D'|'d');
	
OPEN_ADD_TOKEN
	:	OPEN_FRAGMENT WS? ADD_FRAGMENT;

CLOSE_ADD_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? ADD_FRAGMENT WS? CLOSE_FRAGMENT;
	
fragment SET_FRAGMENT 
	:	('s'|'S')('e'|'E')('t'|'T');
	
OPEN_SET_TOKEN: OPEN_FRAGMENT WS? SET_FRAGMENT;

CLOSE_SET_TOKEN : OPEN_FRAGMENT SLASH_FRAGMENT WS? SET_FRAGMENT WS? CLOSE_FRAGMENT;		

fragment MODIFY_FRAGMENT
	:	('m'|'M')('o'|'O')('d'|'D')('i'|'I')('f'|'F')('y'|'Y');	
	
OPEN_MODIFY_TOKEN
	:	OPEN_FRAGMENT WS? MODIFY_FRAGMENT;

CLOSE_MODIFY_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? MODIFY_FRAGMENT WS? CLOSE_FRAGMENT;	

fragment REMOVE_FRAGMENT
	:	('r'|'R')('e'|'E')('m'|'M')('o'|'O')('v'|'V')('e'|'E');	
	
OPEN_REMOVE_TOKEN 
	:	OPEN_FRAGMENT WS? REMOVE_FRAGMENT;

CLOSE_REMOVE_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? REMOVE_FRAGMENT WS? CLOSE_FRAGMENT;	
	
fragment STOP_FRAGMENT : ('S'|'s')('T'|'t')('O'|'o')('P'|'p');	

STOP_TOKEN 
	:	OPEN_FRAGMENT STOP_FRAGMENT WS? SLASH_FRAGMENT WS? CLOSE_FRAGMENT;


OPEN_PROXY_COND_TOKEN 
	:	OPEN_FRAGMENT WS? PROXY_COND_FRAGMENT;

CLOSE_PROXY_COND_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? PROXY_COND_FRAGMENT WS? CLOSE_FRAGMENT;	

fragment PROXY_COND_FRAGMENT
	:	PROXY_FRAGMENT CONDITION_FRAGMENT;


OPEN_PROXY_ACT_TOKEN 
	:	OPEN_FRAGMENT WS? PROXY_ACT_FRAGMENT;
	
CLOSE_PROXY_ACT_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? PROXY_ACT_FRAGMENT WS? CLOSE_FRAGMENT;
	
fragment PROXY_ACT_FRAGMENT 
	:	PROXY_FRAGMENT ACTION_FRAGMENT;
	
	
OPEN_SCRIPT_COND_TOKEN 
	:	OPEN_FRAGMENT WS? SCRIPTABLE_COND_FRAGMENT;
	
CLOSE_SCRIPT_COND_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? SCRIPTABLE_COND_FRAGMENT WS? CLOSE_FRAGMENT;	 	

fragment SCRIPTABLE_COND_FRAGMENT
	:	SCRIPTABLE_FRAGMENT CONDITION_FRAGMENT;

OPEN_SCRIPT_ACT_TOKEN 
	:	OPEN_FRAGMENT WS? SCRIPTABLE_ACT_FRAGMENT;
	
CLOSE_SCRIPT_ACT_TOKEN 
	:	 OPEN_FRAGMENT SLASH_FRAGMENT WS? SCRIPTABLE_ACT_FRAGMENT WS? CLOSE_FRAGMENT;

fragment SCRIPTABLE_ACT_FRAGMENT 
	:	SCRIPTABLE_FRAGMENT ACTION_FRAGMENT;		

fragment PROXY_FRAGMENT 
	:	('p'|'P')('r'|'R')('o'|'O')('x'|'X')('y'|'Y')'-';

fragment SCRIPTABLE_FRAGMENT 
	:	('s'|'S')('c'|'C')('r'|'R')('i'|'I')('p'|'P')
	('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E')'-';	
	
	
fragment CONDITION_FRAGMENT
	:	('c'|'C')('o'|'O')('n'|'N')('d'|'D')('i'|'I')
	('t'|'T')('i'|'I')('o'|'O')('n'|'N');


OPEN_CONDITIONS_TOKEN 
	:	OPEN_FRAGMENT WS? CONDITION_FRAGMENT ('s'|'S')? WS? CLOSE_FRAGMENT;

CLOSE_CONDITIONS_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? CONDITION_FRAGMENT ('s'|'S')? WS? CLOSE_FRAGMENT;
	
OPEN_ACTIONS_TOKEN
	:	OPEN_FRAGMENT WS? ACTION_FRAGMENT ('s'|'S')? WS? CLOSE_FRAGMENT;
	
CLOSE_ACTIONS_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? ACTION_FRAGMENT('s'|'S')? WS? CLOSE_FRAGMENT;			
	
fragment ACTION_FRAGMENT
	:	('a'|'A')('c'|'C')('t'|'T')('i'|'I')('o'|'O')('n'|'N');			
	
fragment MODULE_FRAGMENT 
	:	('m'|'M')('o'|'O')('d'|'D')('u'|'U')('l'|'L')('e'|'E');	

fragment MODULES_FRAGMENT 
	:	MODULE_FRAGMENT ('s'|'S');

LONG_CLOSE_TOKEN 
	:	CLOSE_FRAGMENT;

SHORT_CLOSE_TOKEN 
	:	SLASH_FRAGMENT WS? CLOSE_FRAGMENT;
	
OPEN_MODULE_TOKEN	: OPEN_FRAGMENT WS? MODULE_FRAGMENT;

CLOSE_MODULE_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? MODULE_FRAGMENT WS? CLOSE_FRAGMENT;
	
OPEN_MODULES_TOKEN 
	:	 OPEN_FRAGMENT WS? MODULES_FRAGMENT WS? CLOSE_FRAGMENT;
	
CLOSE_MODULES_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? MODULES_FRAGMENT WS? CLOSE_FRAGMENT;		

OPEN_IMPORT_TOKEN 
: OPEN_FRAGMENT WS? IMPORT_ATTR_TOKEN;
	
IMPORT_ATTR_TOKEN 
	:	('i'|'I')('m'|'M')('p'|'P')('o'|'O')('r'|'R')('t'|'T');	
	
URL_TOKEN 
	:	('u'|'U')('r'|'R')('l'|'L')'=';	

LANG_ATTR_TOKEN 
	:	('l'|'L')('a'|'A')('n'|'N')('g'|'G')'=';	
	
EXTENSION_FRAGMENT
	:	('e'|'E')('x'|'X')('t'|'T')('e'|'E')('n'|'N')
	('s'|'S')('i'|'I')('o'|'O')('n'|'N');	

EXTENSIONS_FRAGMENT
	:	EXTENSION_FRAGMENT ('s'|'S');
	
OPEN_EXTENSION_TOKEN 
	:	OPEN_FRAGMENT WS? EXTENSION_FRAGMENT;
	
CLOSE_EXTENSION_TOKEN 
	:	 OPEN_FRAGMENT SLASH_FRAGMENT WS? EXTENSION_FRAGMENT WS? CLOSE_FRAGMENT;		

OPEN_EXTENSIONS_TOKEN
	:	OPEN_FRAGMENT WS? EXTENSIONS_FRAGMENT WS? CLOSE_FRAGMENT;

CLOSE_EXTENSIONS_TOKEN 
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? EXTENSIONS_FRAGMENT WS? CLOSE_FRAGMENT;	

CLASS_ATTR_TOKEN 
	:	('c'|'C')('l'|'L')('a'|'A')('s'|'S')('s'|'S')'=';
	
OPEN_OR_TOKEN
	:	OPEN_FRAGMENT WS? ('o'|'O')('r'|'R') WS? CLOSE_FRAGMENT;
	
CLOSE_OR_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? ('o'|'O')('r'|'R') WS? CLOSE_FRAGMENT;

OPEN_AND_TOKEN
	:	OPEN_FRAGMENT WS? ('a'|'A')('n'|'N')('d'|'D') WS? CLOSE_FRAGMENT;
	
CLOSE_AND_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? ('a'|'A')('n'|'N')('d'|'D') WS? CLOSE_FRAGMENT;

OPEN_NOT_TOKEN
	:	OPEN_FRAGMENT WS? SLOT_NOT_TOKEN WS? CLOSE_FRAGMENT;
	
CLOSE_NOT_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT WS? SLOT_NOT_TOKEN WS? CLOSE_FRAGMENT;
	
OPEN_SLOT_TOKEN 
	:	OPEN_FRAGMENT WS? ('s'|'S')('l'|'L')('o'|'O')('t'|'T');	
	
SLOT_EQ_TOKEN 	:	('e'|'E')('q'|'Q')('u'|'U')('a'|'A')('l'|'L')('s'|'S');
SLOT_NOT_TOKEN :	('n'|'N')('o'|'O')('t'|'T');

SLOT_GT_TOKEN 
	:	('g'|'G')('r'|'R')('e'|'E')('a'|'A')('t'|'T')
	('e'|'E')('r'|'R')'-'THAN_TOKEN;

SLOT_GTE_TOKEN 
	:	('g'|'G')('r'|'R')('e'|'E')('a'|'A')('t'|'T')
	('e'|'E')('r'|'R')'-'THAN_TOKEN '-' ('e'|'E')('q'|'Q')('u'|'U')('a'|'A')('l'|'L')('s'|'S');

SLOT_LT_TOKEN
	:	('l'|'L')('e'|'E')('s'|'S')('s'|'S')'-'THAN_TOKEN;

SLOT_LTE_TOKEN 
	:	('l'|'L')('e'|'E')('s'|'S')('s'|'S')'-'THAN_TOKEN'-'('e'|'E')('q'|'Q')('u'|'U')('a'|'A')('l'|'L')('s'|'S');	
	
SLOT_WITHIN_TOKEN
	:	('w'|'W')('i'|'I')('t'|'T')('h'|'H')('i'|'I')('n'|'N');			
	
fragment THAN_TOKEN 
	:	('t'|'T')('h'|'H')('a'|'A')('n'|'N');

fragment DECLARATIVE_MEMORY_FRAGMENT
	:	('d'|'D')('e'|'E')('c'|'C')('l'|'L')('a'|'A')
	('r'|'R')('a'|'A')('t'|'T')('i'|'I')('v'|'V')('e'|'E')MEMORY;	
	
OPEN_DECLARATIVE_MEMORY_TOKEN
	:	OPEN_FRAGMENT  WS? DECLARATIVE_MEMORY_FRAGMENT  WS? CLOSE_FRAGMENT;
	
CLOSE_DECLARATIVE_MEMORY_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT  WS? DECLARATIVE_MEMORY_FRAGMENT  WS? CLOSE_FRAGMENT;		

fragment PROCEDURAL_MEMORY_FRAGMENT
	:	('p'|'P')('r'|'R')('o'|'O')('c'|'C')('e'|'E')('d'|'D')
	('u'|'U')('r'|'R')('a'|'A')('l'|'L')MEMORY;

OPEN_PROCEDURAL_MEMORY_TOKEN
	:	 OPEN_FRAGMENT  WS? PROCEDURAL_MEMORY_FRAGMENT  WS? CLOSE_FRAGMENT;
	
CLOSE_PROCEDURAL_MEMORY_TOKEN
	:	OPEN_FRAGMENT SLASH_FRAGMENT  WS? PROCEDURAL_MEMORY_FRAGMENT WS? CLOSE_FRAGMENT;
	
fragment MEMORY 
	:	'-'('m'|'M')('e'|'E')('m'|'M')('o'|'O')('r'|'R')('y'|'Y');	
	
IDENTIFIER_TOKEN 
    : ( LETTER_FRAGMENT | '_' | ':'| '*') 
       (  LETTER_FRAGMENT | DIGITS_FRAGMENT | '.' | '-' | '_' | ':' |'*')*
;

STRING_TOKEN
    :
      '\"'
      ( ~('\"'|'\\')
      | ESCAPE_TOKEN
      )*
      '\"'
      {
       String str = getText();
       setText(str.substring(1, str.length()-1));
       $start++;
      };

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
    

 WS
 	:	(   ' '
       	|   '\t'
        |  ( '\n'
            |	'\r\n'
            |	'\r'
            )
        )+
        { $channel=HIDDEN; }
	;

NUMBER_TOKEN 
	:	('-')? DIGITS_FRAGMENT ('.' DIGITS_FRAGMENT)?;

fragment LETTER_FRAGMENT	: 'a'..'z' 
	| 'A'..'Z'
	;

fragment DIGITS_FRAGMENT : ('0'..'9')+;

fragment OPEN_FRAGMENT 
	:	 '<';
	
fragment CLOSE_FRAGMENT 
	:	'>';
	
fragment SLASH_FRAGMENT 
	:	'/';
