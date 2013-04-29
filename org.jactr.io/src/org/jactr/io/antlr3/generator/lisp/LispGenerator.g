tree grammar LispGenerator;


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
	
	CLASS_SPEC;
	NAME;
	PARENT;
	
	UNKNOWN;
}


@header{
/**
 *  Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 *  software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software
 *  Foundation; either version 2.1 of the License, or (at your option) any later
 *  version. This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 *  License for more details. You should have received a copy of the GNU Lesser
 *  General Public License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 */
 
package org.jactr.io.antlr3.generator.lisp;

import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import org.jactr.io.participant.*;
import org.jactr.io.antlr3.builder.*;
import org.jactr.io.antlr3.misc.*;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
}



@members{
//LOGGER
static private transient Log LOGGER = LogFactory.getLog(LispGenerator.class);


private Collection<IASTTrimmer> _trimmers = new ArrayList<IASTTrimmer>();


protected void addTrimmer(IASTTrimmer trimmer)
{
 _trimmers.add(trimmer);
}

protected boolean shouldIgnore(CommonTree element)
{
 if(!(element instanceof CommonTree)) return false;
 CommonTree cElement = (CommonTree)element;
 for(IASTTrimmer trimmer : _trimmers)
  if(trimmer.shouldIgnore(cElement))
   return true;
 return false;
}

/**
 indent the set of string buffers
*/
protected Collection<StringBuilder> indent(Collection<StringBuilder> collection, int spaces)
{
	for(StringBuilder string : collection)
	{
		indent(string, spaces);
	}
	return collection;
}

protected StringBuilder indent(StringBuilder string, int spaces)
{
	 for(int i=0;i<spaces;i++)
	  string.insert(0,' ');
	 return string;
}

protected void newLines(Collection<StringBuilder> collection, int number)
{
	for(int i=0;i<number;i++)
	 collection.add(new StringBuilder(0));
}

protected void comment(Collection<StringBuilder> collection, String message)
{	
	collection.add(new StringBuilder());
	collection.add(new StringBuilder("#|").append(message).append(" |#"));
	collection.add(new StringBuilder());
}

protected Collection<StringBuilder> comment(String... messages)
{
	Collection<StringBuilder> comments = new ArrayList<StringBuilder>();
	comments.add(new StringBuilder(""));
	comments.add(new StringBuilder(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"));
	for(String message : messages)
	 comments.add(new StringBuilder(";; ").append(message));
	comments.add(new StringBuilder(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"));
	comments.add(new StringBuilder(""));
	return comments; 
}

protected boolean isEmpty(CommonTree node)
{
	return node==null || node.getText().length()==0 || node.isNil();
}
}

model returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
 : ^(m=MODEL
           n=NAME  
           mods=modules
           exts=extensions
           bufs=buffers
           ^(LIBRARY  
           decs=declarative_memory 
           procs=procedural_memory) 
           params=parameters
            )
 {
 	 comment(collection, "Automatically generated model ");
 	 comment(collection, "*Unsupported warning* : Extensions, full buffer spec, scriptables and proxies");
 	 
 	 StringBuilder tmp = new StringBuilder("(define-model ");
 	 tmp.append($n.text.replace(" ","-"));
 	 collection.add(tmp);

         collection.addAll(mods);
         collection.addAll(exts);
 	 
 	 comment(collection, "Chunk-types and chunks");
 	 indent(decs, 2);
 	 collection.addAll(decs);
 	 
 	 comment(collection, "Productions");
 	 indent(procs, 2);
 	 collection.addAll(procs);
 	 
 	 indent(bufs,2);
 	 collection.addAll(bufs);
 	 
 	 if(params!=null && params.size()!=0)
 	 {
 	  StringBuilder sb = new StringBuilder(" (sgp ");
 	  for(StringBuilder s : params)
 	   sb.append(s);
 	  sb.append(")");
 	  collection.add(sb); 
 	 }
 	 
 	 collection.add(new StringBuilder(") ;define-model"));
 };

parameters  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ( ^(PARAMETERS (param=parameter {collection.addAll(indent(param,2));})+) 
    | PARAMETERS) //tp hack
{
 if(collection.size()!=0)
  {
   StringBuilder sb = new StringBuilder();
   for(StringBuilder s : collection)
    sb.append(s.toString()).append(" ");
   collection.clear();
   collection.add(sb); 
  } 
};

parameter  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PARAMETER n=NAME s=STRING)
{
         String pName = $n.text;
         String newPName = ParameterMap.getLispParameterName(pName);
         if(newPName==null)
          newPName = pName;
         String pValue = $s.text;
         
         if(pValue.equalsIgnoreCase("true"))
          pValue = "t";
         else
         if(pValue.equalsIgnoreCase("false")
           || pValue.equalsIgnoreCase("NaN")
           || pValue.equalsIgnoreCase("-Infinity"))
          pValue = "nil";
          
	 StringBuilder sb = new StringBuilder(newPName);
	 sb.append(" ");
	 sb.append(pValue);
	 collection.add(sb);
};



modules  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: (^(MODULES 
    (mod=module {collection.addAll(indent(mod,2));}
    )+) | MODULES)
{
};

module  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(mod=MODULE c=CLASS_SPEC (params=parameters)?)
{
	 collection.addAll(comment("module class "+$c.text));
	 StringBuilder tmp = new StringBuilder("(module \"");
	 tmp.append($c.text).append("\"");
	 if(_trimmers.size()==0)
	  tmp.append(" no-import");
	 tmp.append(")"); 
	 collection.add(tmp);
	 
	 //handle the parameters - which we ignore
	if(params!=null)
 	 for(StringBuilder sb : params)
	  comment(collection, "module parameter "+sb.toString());
};
	
		
extensions  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: (^(EXTENSIONS 
    (ext=extension {collection.addAll(indent(ext,2));}
    )+) | EXTENSIONS)
{
};

extension  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ext=EXTENSION c=CLASS_SPEC (params=parameters)?)
{
         collection.addAll(comment("Extension class "+$c.text));
         StringBuilder tmp = new StringBuilder("(extension \"");
         tmp.append($c.text).append("\")");
         collection.add(tmp);
         
         //handle the parameters - which we ignore
	if(params!=null)
 	 for(StringBuilder sb : params)
	  comment(collection, "parameter "+sb.toString());
};

buffers returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: (^(BUFFERS 
      (buf=buffer {collection.addAll(indent(buf,2));}
      )+) | BUFFERS)
{
};

buffer returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	Collection<String> identifiers=new ArrayList<String>();
}
: ^(buff=BUFFER n=NAME  
             (^(CHUNKS 
               (i=CHUNK_IDENTIFIER {identifiers.add($i.text);}
              )+) | CHUNKS) 
             (params=parameters)?)
{
 if($n.text.equals("goal") && identifiers.size()!=0)
  {
   StringBuilder tmp = new StringBuilder("(goal-focus ");
   tmp.append(identifiers.iterator().next());
   tmp.append(")");
   collection.add(tmp);
  }  
  
//handle the parameters - which we ignore
	if(params!=null)
 	 for(StringBuilder sb : params)
	  comment(collection, "buffer parameter "+sb.toString());
};		
		
declarative_memory  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	
	collection.addAll(comment("declarative memory container for chunks and chunk-types"));
}
: (^(DECLARATIVE_MEMORY 
      (ct=chunktype {
                      if(ct.size()!=0)
                       {
      	                collection.addAll(indent(ct,2));
      	                //and add some space after the chunks before the chunktype
      	                newLines(collection, 2);
      	               }
                    }
   )+) | DECLARATIVE_MEMORY)
{
};

procedural_memory  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	
	collection.addAll(comment("procedural memory contents"));
}
: (^(PROCEDURAL_MEMORY 
      (prod=production {
                        if(prod.size()!=0)
                         {
      	                  collection.addAll(indent(prod,2));
      	                  newLines(collection, 2);
      	                 } 
      	                }
      )+) | PROCEDURAL_MEMORY)
{
};

chunktype  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ct=CHUNK_TYPE 
              n=NAME
              (p=PARENT)? 
              (sl=slots)? 
              (ch=chunks)? 
              (params=parameters)?)
{

 if(shouldIgnore($ct))
 {
 }
 else
 {
	StringBuilder tmp = new StringBuilder("(chunk-type ");
	tmp.append($n.text.replace(" ","-"));
	tmp.append(" ");
	if(!isEmpty($p))
	 {
	 	 tmp.append("(:include ");
	 	 tmp.append($p.text.replace(" ","-"));
	 	 tmp.append(") ");
	 }
	 
	//handle the slots
	for(StringBuilder s : sl)
	 tmp.append("(").append(s).append(")");
	
	tmp.append(")");
	collection.add(tmp);
	 
	//handle the parameters - which we ignore
	if(params!=null)
 	 for(StringBuilder sb : params)
	  comment(collection, "chunktype parameter "+sb.toString());
 }
      if(ch.size()!=0)
       {
	 comment(collection, "chunks for "+$n.text);
	 // handle the chunks
	 collection.addAll(ch);
       }	
};
		
slots  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
:(^(SLOTS 
     (s=slot {collection.addAll(s);})+)|SLOTS);

conditionalSlots returns [Collection<StringBuilder> collection]
@init{
 collection = new ArrayList<StringBuilder>();
}
 	:	(^(SLOTS (s=conditionalSlot {collection.addAll(s);})+) | SLOTS);
 	

chunks  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: (^(CHUNKS 
     (c=chunk {
     	         collection.addAll(c);
     	        }
     )+) | CHUNKS)
{
};

conditionalSlot  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	String cond = "";
}
: ^(sl=SLOT n=NAME 
         ((EQUALS {cond=""; }
           |LT    {cond="<";}
           |GT    {cond=">";}
           |NOT   {cond="-";}
           |WITHIN {cond="<>";}
           |LTE    {cond="<=";}
           |GTE    {cond=">=";}
          )
          (v=VARIABLE 
         | v=STRING  
         | v=IDENTIFIER 
         | v=NUMBER 
         ))
        )
{
	 StringBuilder tmp = new StringBuilder(cond);
	 tmp.append(" ").append($n.text);
	 String value = $v.text;
	 if("null".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
	  value = "nil";
	 if("true".equalsIgnoreCase(value))
	  value="t";
	 boolean isReserved = "nil".equalsIgnoreCase(value) ||
	                      "t".equalsIgnoreCase(value);
	 tmp.append(" ");
	 if($v.type==STRING && !isReserved)
	  tmp.append("\"");
	 tmp.append(value);
	 if($v.type==STRING && !isReserved)
	  tmp.append("\"");
	 tmp.append(" ");
	 
	 LOGGER.debug("slot yielded : "+tmp);
	 
	 collection.add(tmp);
};

slot  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(sl=SLOT n=NAME 
         EQUALS
          (v=VARIABLE 
         | v=STRING  
         | v=IDENTIFIER 
         | v=NUMBER 
         )
        )
{
	
	 StringBuilder tmp = new StringBuilder();
	 tmp.append(" ").append($n.text);
	 String value = $v.text;
	 if("null".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
	  value = "nil";
	 if("true".equalsIgnoreCase(value))
	  value="t";
	 boolean isReserved = "nil".equalsIgnoreCase(value) ||
	                      "t".equalsIgnoreCase(value);
	 tmp.append(" ");
	 if($v.type==STRING && !isReserved)
	  tmp.append("\"");
	 tmp.append(value);
	 if($v.type==STRING && !isReserved)
	  tmp.append("\"");
	 tmp.append(" ");
	 
	 LOGGER.debug("slot yielded : "+tmp);
	 
	 collection.add(tmp);
};
		
chunk  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ch=CHUNK 
      n=NAME
      p=PARENT 
      (sl=slots)? 
      (params=parameters)?)
{
  if(shouldIgnore($ch))
  {
  }
  else
  {
	 StringBuilder tmp = new StringBuilder("(add-dm (");
	 tmp.append($n.text.replace(" ","-"));
	 tmp.append(" isa ");
	 tmp.append($p.text.replace(" ","-"));
	 tmp.append(" ");
	 
	 for(StringBuilder sb : sl)
	  tmp.append(sb);
	 
	 tmp.append("))"); 
	 collection.add(tmp);
	 
	 if(params!=null && params.size()!=0)
	 {
	  StringBuilder param = new StringBuilder("(sdp ");
	  param.append($n.text);
	  param.append(" ");
	 
	  for(StringBuilder sb : params)
	   param.append(sb);
	  param.append(")");
	  collection.add(param); 
	 }
 }	 
};		 
		
production  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PRODUCTION n=NAME conds=conditions acts=actions (params=parameters)?)
{
 if(shouldIgnore($p))
 {
 }
 else
 {
	 StringBuilder tmp = new StringBuilder("(p ");
	 tmp.append($n.text);
	 collection.add(tmp);
	 
	 collection.addAll(indent(conds,2));
	 
	 collection.add(new StringBuilder(" ==>"));
	 
	 collection.addAll(indent(acts,2));
	 
	 collection.add(new StringBuilder(")"));
	 
	 if(params!=null && params.size()!=0)
	 {
	  StringBuilder param = new StringBuilder("(spp ");
	  param.append($n.text);
	  for(StringBuilder sb: params)
	   param.append(sb);
	  param.append(")");
	 }
 }	 
};
		
conditions  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(CONDITIONS (tmp=pattern {collection.addAll(indent(tmp,1));}
              | tmp=queryCondition {collection.addAll(indent(tmp,1));}
              | tmp=scriptableCondition {collection.addAll(indent(tmp,1));}
              | tmp=proxyCondition {collection.addAll(indent(tmp,1));}
              )+)
{
};

pattern  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=MATCH_CONDITION n=NAME (c=CHUNK_IDENTIFIER | ct=CHUNK_TYPE_IDENTIFIER | v=VARIABLE) 
            (sl=conditionalSlots {collection.addAll(sl);}
            )?)
{
	 StringBuilder tmp = new StringBuilder("=");
	 tmp.append($n.text);
	 tmp.append(">");
	 collection.add(0, tmp);
	 StringBuilder isa = new StringBuilder();
	 if($v!=null || $c!=null)
	  {
	  	//referring to a specific chunk
	      if($v!=null)	
	  	isa.append($v.text);
	      else
	        isa.append($c.text);	
	  }
	 else
	 {
	 	 //identifier may be a type, may be a chunk.. ug.
	 	 //however, a bug in the xml parser means that it doesn't 
	 	 // actually differentiate between the two - infact,
	 	 // the constructor doesn't either - whether the identifier
	 	 // refers to a chunk or a chunktype isn't decided until
	 	 // construction when the constructor guesses which you want..
	 	 // preferencing chunktypes - so we'll just dump type..
	 	 isa.append("isa ");
	 	 isa.append($ct.text);
	 }
	 
	 collection.add(1, isa);
	 collection.add(new StringBuilder()); 
};

queryCondition  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(q=QUERY_CONDITION n=NAME
            (sl=conditionalSlots {collection.addAll(sl);}
            )?)
{
	 StringBuilder tmp = new StringBuilder("?");
	 tmp.append($n.text);
	 tmp.append(">");
	 collection.add(0, tmp);
	 collection.add(new StringBuilder());
};

		
scriptableCondition  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(sc=SCRIPTABLE_CONDITION l=LANG s=SCRIPT)
{
  String scriptText = $s.text;
  String[] lines = scriptText.split("\n");
  
  scriptText = scriptText.replace("\n","\n;;");
  collection.addAll(comment("Original script for language "+$l.text, scriptText));
  collection.add(new StringBuilder());
  
  StringBuilder scriptLine = new StringBuilder("!eval! (");
  scriptLine.append("\"").append($l.text).append("\" ");
  for(String line : lines)
   {
    //replace all " with \" 
    line = line.replace("\"", "\\\"");
    scriptLine.append("\"").append(line).append("\" ");
   }
  scriptLine.append(")");
  collection.add(scriptLine);
  collection.add(new StringBuilder());
};

proxyCondition  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PROXY_CONDITION c=CLASS_SPEC (sl=slots)?)
{
	 collection.add(new StringBuilder("=proxy>"));
	 collection.add((new StringBuilder("isa ")).append($c.text));
	 if(sl!=null) 
	  collection.addAll(sl);
	 collection.add(new StringBuilder()); 
};

actions  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ACTIONS (tmp=add {collection.addAll(indent(tmp,2));}
        | tmp=modify {collection.addAll(indent(tmp,2));}
        | tmp=remove {collection.addAll(indent(tmp,2));}
        | tmp=output {collection.addAll(indent(tmp,2));}
        | tmp=scriptableAction {collection.addAll(indent(tmp,2));}
        | tmp=proxyAction {collection.addAll(indent(tmp,2));}
        )+)
{
};

add  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(a=ADD_ACTION n=NAME (c=CHUNK_IDENTIFIER | ct=CHUNK_TYPE_IDENTIFIER | v=VARIABLE)? 
              (sl=conditionalSlots {collection.addAll(sl);}
              )?)
{
	 StringBuilder tmp = new StringBuilder("+");
	 tmp.append($n.text);
	 tmp.append(">");
	 collection.add(0, tmp);
	 StringBuilder isa = new StringBuilder();
	 
	 if($v!=null || $c!=null)
	  {
	       if($v!=null)
	  	isa.append($v.text);
	       else
	        isa.append($c.text);	
	  }
	 else if($ct!=null)
	 {
	 	 //see the comments in pattern regarding this ambiguity
	 	 isa.append("isa ");
	 	 isa.append($ct.text);
	 }
	 
	 collection.add(1, isa);
	 collection.add(new StringBuilder());
};

scriptableAction  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(sc=SCRIPTABLE_ACTION l=LANG s=SCRIPT)
{
  String scriptText = $s.text;
  String[] lines = scriptText.split("\n");
  
  scriptText = scriptText.replace("\n","\n;;");
  collection.addAll(comment("Original script for language "+$l.text, scriptText));
  collection.add(new StringBuilder());
  
  StringBuilder scriptLine = new StringBuilder("!eval! (");
  scriptLine.append("\"").append($l.text).append("\" ");
  for(String line : lines)
   {
    //replace all " with \" 
    line = line.replace("\"", "\\\"");
    scriptLine.append("\"").append(line).append("\" ");
   }
  scriptLine.append(")");
  collection.add(scriptLine);
  collection.add(new StringBuilder());
};
		
proxyAction  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PROXY_ACTION c=CLASS_SPEC (sl=slots)?)
{
	if($c.text.equals("org.jactr.core.production.action.StopAction"))
          {
           //dump the short version
           collection.add(new StringBuilder("!stop!"));
          }
         else
         { 
 	  collection.add(new StringBuilder("+proxy>"));
 	  collection.add((new StringBuilder("isa ")).append($c.text));
 	  if(sl!=null)
 	   collection.addAll(sl);
	 }
	 
	 collection.add(new StringBuilder());
};
		
modify  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(m=MODIFY_ACTION n=NAME 
         (sl=slots {collection.addAll(sl);}
         )?)
{
	 StringBuilder tmp = new StringBuilder("=");
	 tmp.append($n.text);
	 tmp.append(">");
	 collection.add(0, tmp);
	 collection.add(new StringBuilder());
};

remove  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(r=REMOVE_ACTION n=NAME (i=IDENTIFIER | i=VARIABLE)?
       (sl=slots {collection.addAll(sl);}
    )?)
{
	 StringBuilder tmp = new StringBuilder("-");
	 tmp.append($n.text);
	 tmp.append(">");
	 collection.add(0, tmp);
	 collection.add(new StringBuilder());
};
		
output  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(o=OUTPUT_ACTION s=STRING)
{
	 StringBuilder tmp = new StringBuilder("!output! (");
	 String text = $s.text;
	 text = text.replace("'", "");
	 text = text.replace("`","");
	 text = text.replace(";","");
	 tmp.append("\"");
	 tmp.append(text);
	 tmp.append("\"");
	 tmp.append(")");
	 collection.add(tmp);
	 collection.add(new StringBuilder());
};
