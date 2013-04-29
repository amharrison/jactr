tree grammar JACTRGenerator;


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
 
package org.jactr.io.antlr3.generator.xml;

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
static private transient Log LOGGER = LogFactory.getLog(JACTRGenerator.class);


private Collection<IASTTrimmer> _trimmers = new ArrayList<IASTTrimmer>();

protected void addTrimmer(IASTTrimmer trimmer)
{
 _trimmers.add(trimmer);
}

protected boolean shouldIgnore(Tree element)
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
	collection.add(new StringBuilder("<!-- ** ").append(message).append(" ** "));
	collection.add(new StringBuilder("-->"));
}

protected Collection<StringBuilder> comment(String... messages)
{
	Collection<StringBuilder> comments = new ArrayList<StringBuilder>();
	comments.add(new StringBuilder(""));
	comments.add(new StringBuilder("<!-- **************************************************"));
	for(String message : messages)
	 comments.add(new StringBuilder("     * ").append(message));
	comments.add(new StringBuilder("     ************************************************** "));
	comments.add(new StringBuilder("-->"));
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
 	 collection.add(new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
 	 collection.add(new StringBuilder("<actr>"));
 	 
 	 StringBuilder tmp = new StringBuilder("<model name=\"");
 	 tmp.append($n.text);
 	 tmp.append("\" version=\"");
 	 tmp.append(6);
 	 tmp.append("\" >");
 	 collection.add(indent(tmp,1));
 	 
 	 indent(mods, 2);
 	 collection.addAll(mods);
 	 
 	 //the xml dumps the extensions first..
 	 indent(exts, 2);
 	 collection.addAll(exts);
 	 
 	 indent(decs, 2);
 	 collection.addAll(decs);
 	 
 	 indent(procs, 2);
 	 collection.addAll(procs);
 	 
 	 indent(bufs,2);
 	 collection.addAll(bufs);
 	 
 	 indent(params,1);
 	 collection.addAll(params);
 	 
 	 
 	 collection.add(indent(new StringBuilder("</model>"),1));
 	 collection.add(new StringBuilder("</actr>"));
 	 
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
	  	//insert the header and footer
	  	collection.add(0, new StringBuilder("<parameters>"));
	  	collection.add(new StringBuilder("</parameters>"));
	  }
};

parameter  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PARAMETER n=NAME s=STRING)
{
	 StringBuilder sb = new StringBuilder("<parameter name=\"");
	 sb.append($n.text);
	 sb.append("\" value=\"");
	 sb.append($s.text);
	 sb.append("\"/>");
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
	if(collection.size()!=0)
	 {
	 	 //insert header and footer
	 	 collection.add(0, new StringBuilder("<modules>"));
	 	 collection.add(new StringBuilder("</modules>"));
	 }
};

module  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(mod=MODULE c=CLASS_SPEC (params=parameters)?)
{
	 
	 StringBuilder tmp = new StringBuilder("<module class=\"");
	 tmp.append($c.text).append("\" import=\"");
	 tmp.append(_trimmers.size()!=0).append("\"");
	  
	 
	 if(params==null || params.size()==0)
	  tmp.append("/>");
	 else 
	  tmp.append(">");
	  
	 collection.add(tmp);
	 collection.addAll(indent(params,2));
	 
	if(params!=null && params.size()!=0) 
	 collection.add(new StringBuilder("</module>"));
};
	
		
extensions  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: (^(EXTENSIONS 
    (ext=extension {collection.addAll(indent(ext,2));}
    )+) | EXTENSIONS)
{
	if(collection.size()!=0)
	 {
	 	 //insert header and footer
	 	 collection.add(0, new StringBuilder("<extensions>"));
	 	 collection.add(new StringBuilder("</extensions>"));
	 }
};

extension  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ext=EXTENSION c=CLASS_SPEC (params=parameters)?)
{
	 StringBuilder tmp = new StringBuilder("<extension ");
	 tmp.append("class=\"");
	 tmp.append($c.text).append("\"");
	 collection.add(tmp);
	 
	 if(params==null || params.size()==0)
	   tmp.append("/>");  
	 else
	  {
 	   tmp.append(">");
	   collection.addAll(indent(params,2));
	   collection.add(new StringBuilder("</extension>"));
	  } 
};

buffers returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	newLines(collection, 2);
	collection.addAll(comment("Buffer definitions"));
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
	 StringBuilder tmp = new StringBuilder("<buffer name=\"");
	 tmp.append($n.text).append("\" ");;
	 
	 if(identifiers.size()!=0)
	  tmp.append("chunk=\"").append(identifiers.iterator().next()).append("\"");
	  
	 if(params.size()!=0)
	  tmp.append(">"); 
	 else
	  tmp.append("/>");
	   
	 collection.add(tmp);
	 
	 //now for the parameters
	 if(params.size()!=0)
	  {
	   collection.addAll(indent(params,2));
	   collection.add(new StringBuilder("</buffer>"));
	  } 
};		
		
declarative_memory  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	
	collection.addAll(comment("declarative memory container for chunks and chunk-types"));
}
: (^(DECLARATIVE_MEMORY 
      (ct=chunktype {
      	               collection.addAll(indent(ct,2));
                    }
   )+) | DECLARATIVE_MEMORY)
{
 //header and footer
  collection.add(0,new StringBuilder("<declarative-memory>"));
  collection.add(new StringBuilder("</declarative-memory>"));
};

procedural_memory  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	
	collection.addAll(comment("procedural memory container for productions"));
}
: (^(PROCEDURAL_MEMORY 
      (prod=production {
      	                  collection.addAll(indent(prod,2));
      	                  newLines(collection, 2);
      	                }
      )+) | PROCEDURAL_MEMORY)
{
	//header and footer
	collection.add(0,new StringBuilder("<procedural-memory>"));
	collection.add(new StringBuilder("</procedural-memory>"));
};

chunktype  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ct=CHUNK_TYPE 
              n=NAME
              (p=parents)? 
              (sl=slots)? 
              (ch=chunks)? 
              (params=parameters)?)
{
  if(shouldIgnore($ct))
   {
    //flag with a comment?
    LOGGER.debug("Ignoring chunktype "+$n.text);
   }
   else
   {
   //and add some space after the chunks before the chunktype
      	newLines(collection, 2);
      	               
	StringBuilder tmp = new StringBuilder("<chunk-type name=\"");
	tmp.append($n.text);
	tmp.append("\" ");
	if(p != null && p.length() > 0)
	 {
	 	 tmp.append("parent=\"");
	 	 tmp.append(p);
	 	 tmp.append("\" ");
	 }
	
	if(  (sl==null || sl.size()==0) 
	   &&(params==null || params.size()==0))
	  {
	  	//quick closer
	  	tmp.append("/>");
	  	collection.add(tmp);
	  }
	 else
	 {
	 	//long closer
	 	tmp.append(">");
	 	collection.add(tmp);
	 	if(sl!=null)
	 	 collection.addAll(indent(sl,2));
	 	if(params!=null)
	 	 collection.addAll(indent(params,2));
	 	 
	 	collection.add(new StringBuilder("</chunk-type>")); 
	 }
    }	 
	 
	 //now we do the chunks..
	 if(ch!=null && ch.size()!=0)
	 {
          comment(collection, "all chunks for chunk-type "+$n.text);
          collection.addAll(indent(ch,2));
	 }
};
	
parents returns [String parentStr]
@init{
	parentStr = "";
}
:(^(PARENTS
     (p=PARENT {if(parentStr.length() > 0) parentStr += ",";
     		parentStr+=$p.text;}
     )+) | PARENTS)
{
};

slots  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
:(^(SLOTS 
     (s=slot {collection.addAll(s);}
     | l=logic {collection.addAll(l);}
     )+)|SLOTS)
{
};

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

logic returns [Collection<StringBuilder> collection]
@init{
        collection = new ArrayList<StringBuilder>();
}
: ^(l=LOGIC (v=AND|v=OR|v=NOT) (s1=logic|s1=slot) (s2=logic|s2=slot)?)
{
	collection.add(new StringBuilder("<" + $v.text + ">"));
	collection.addAll(s1);
	if(s2 != null) collection.addAll(s2);
	collection.add(new StringBuilder("</" + $v.text + ">"));
};

slot  returns [Collection<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
	String cond = "equals";
}
: ^(sl=SLOT (n=NAME | n=VARIABLE)
         ((EQUALS {cond="equals"; }
           |LT    {cond="less-than";}
           |GT    {cond="greater-than";}
           |NOT   {cond="not";}
           |WITHIN {cond="within";}
           |LTE    {cond="less-than-equals";}
           |GTE    {cond="greater-than-equals";}
          )
          (v=VARIABLE 
         | v=STRING  
         | v=IDENTIFIER 
         | v=NUMBER 
         ))?
        )
{
	 StringBuilder tmp = new StringBuilder("<slot name=\"");
	 tmp.append($n.text);
	 boolean isReserved = "null".equalsIgnoreCase($v.text) ||
	                      "true".equalsIgnoreCase($v.text) ||
	                      "false".equalsIgnoreCase($v.text);
	 tmp.append("\" ");
	 if(cond!=null && $v!=null)
	  {
	   tmp.append(cond);
	   tmp.append("=\"");
	    if($v.type==STRING && !isReserved)
	     tmp.append("'");
	   tmp.append($v.text);
	    if($v.type==STRING && !isReserved)
	     tmp.append("'");
	   tmp.append("\"");
	  }
	 tmp.append("/>");
	 
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
     if(shouldIgnore(ch))
     {
       // should we log this with a comment?
       LOGGER.debug("Ignoring chunk "+$n.text);
     }
     else
     {
	 StringBuilder tmp = new StringBuilder("<chunk name=\"");
	 tmp.append($n.text);
	 tmp.append("\" type=\"");
	 tmp.append($p.text);
	 tmp.append("\" ");
	 
	 if(  (sl==null || sl.size()==0)
	    &&(params==null || params.size()==0))
	   {
	   	//short closure
	   	tmp.append("/>");
	   	collection.add(tmp);
	   }
	  else
	  {
	  	//long closer
	  	tmp.append(">");
	  	collection.add(tmp);
	  	if(sl!=null)
	  	 collection.addAll(indent(sl,2));
	  	if(params!=null)
	  	 collection.addAll(indent(params,2));
	  	collection.add(new StringBuilder("</chunk>"));
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
    //should we note this with a comment?
    LOGGER.debug("Ignoring production "+$n.text);
   }
   else
   {
	 StringBuilder tmp = new StringBuilder("<production name=\"");
	 tmp.append($n.text);
	 tmp.append("\">");
	 
	 collection.add(tmp);
	 
	 collection.addAll(indent(conds,2));
	 collection.addAll(indent(acts,2));
	 
	 if(params!=null)
	  collection.addAll(indent(params,2));
	 
	 collection.add(new StringBuilder("</production>")); 
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
	collection.add(0,new StringBuilder("<conditions>"));
	collection.add(new StringBuilder("</conditions>"));
};

pattern  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=MATCH_CONDITION n=NAME ((c=CHUNK_IDENTIFIER | ct=CHUNK_TYPE_IDENTIFIER | v=VARIABLE) 
            (sl=slots {collection.addAll(indent(sl,2));}
            )?)?)
{
	 StringBuilder tmp = new StringBuilder("<match buffer=\"");
	 tmp.append($n.text);
	 tmp.append("\" ");
	 
	 boolean empty = $v==null && $c==null && $ct==null;
	 if(!empty)
	 {
	   if($v!=null || $c!=null)
	    { 
	  	//referring to a specific chunk
	  	tmp.append("chunk=\"");
	  	if($v!=null)
	  	  tmp.append($v.text);
	  	else
	  	  tmp.append($c.text);  
	  	tmp.append("\"");
	    }
	   else
	   {
	 	 tmp.append("type=\"");
	  	 tmp.append($ct.text);
	   	 tmp.append("\" ");
	   }
	  
	   tmp.append(">");
	   collection.add(0, tmp);
	   collection.add(new StringBuilder("</match>")); 
	 }
	else
	 {
	  tmp.append("/>");
	  collection.add(0, tmp);
	 }
};

queryCondition  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(q=QUERY_CONDITION n=NAME
            (sl=slots {collection.addAll(indent(sl,2));}
            )?)
{
	 StringBuilder tmp = new StringBuilder("<query buffer=\"");
	 tmp.append($n.text);
	 if(collection.size()!=0)
	 {
	   tmp.append("\" >");
	   collection.add(0, tmp);
	   collection.add(new StringBuilder("</query>")); 
	 }
	 else
	 {
	  tmp.append("\"/>");
	  collection.add(0,tmp);
	 }
};

		
scriptableCondition  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(sc=SCRIPTABLE_CONDITION l=LANG s=SCRIPT)
{
         StringBuilder tmp = new StringBuilder("<scriptable-condition lang=\"");
         tmp.append($l.text).append("\">");
	 collection.add(tmp);
	 collection.add(new StringBuilder("<![CDATA["));
	 String code = $s.text;
	 String[] lines = code.split("\n");
	 for(int i=0;i<lines.length;i++)
	  collection.add(indent(new StringBuilder(lines[i]),2));
	 collection.add(new StringBuilder("]]>")); 
	 collection.add(new StringBuilder("</scriptable-condition>")); 
};

proxyCondition  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PROXY_CONDITION c=CLASS_SPEC (sl=slots {collection.addAll(indent(sl,2));})? )
{
	 StringBuilder tmp = new StringBuilder("<proxy-condition class=\"");
	 tmp.append($c.text);
	 if(collection.size()==0)
	   {
	    tmp.append("\" />");
	   } 
	 else
	   {
	    tmp.append("\">");
	    collection.add(new StringBuilder("</proxy-condition>"));
	   }
	 collection.add(0, tmp);   
};

actions  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(ACTIONS (tmp=add {collection.addAll(indent(tmp,2));}
        | tmp=set {collection.addAll(indent(tmp,2));}
        | tmp=modify {collection.addAll(indent(tmp,2));}
        | tmp=remove {collection.addAll(indent(tmp,2));}
        | tmp=output {collection.addAll(indent(tmp,2));}
        | tmp=scriptableAction {collection.addAll(indent(tmp,2));}
        | tmp=proxyAction {collection.addAll(indent(tmp,2));}
        )+)
{
	 collection.add(0, new StringBuilder("<actions>"));
	 collection.add(new StringBuilder("</actions>"));
};

add  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(a=ADD_ACTION n=NAME (c=CHUNK_IDENTIFIER | ct=CHUNK_TYPE_IDENTIFIER | v=VARIABLE)? 
              (sl=slots {collection.addAll(indent(sl,2));}
              )?)
{
	 StringBuilder tmp = new StringBuilder("<add buffer=\"");
	 tmp.append($n.text);
	 tmp.append("\" ");
	 if($v!=null || $c!=null)
	  {
	  	tmp.append("chunk=\"");
	  	if($v!=null)
	  	  tmp.append($v.text);
	  	else
	  	  tmp.append($c.text);  
	  	tmp.append("\"");
	  }
	 else if($ct!=null)
	 {
	 	 //see the comments in pattern regarding this ambiguity
	 	 tmp.append("type=\"");
	 	 tmp.append($ct.text);
	 	 tmp.append("\"");
	 }
	 
	 if(collection.size()==0) 
	  tmp.append("/>");
	 else
	  tmp.append(">");
	   
	 collection.add(0,tmp);
	 if(collection.size()>1)
	   collection.add(new StringBuilder("</add>"));
};


set  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(a=SET_ACTION n=NAME (c=CHUNK_IDENTIFIER | v=VARIABLE)? 
              (sl=slots {collection.addAll(indent(sl,2));}
              )?)
{
	 StringBuilder tmp = new StringBuilder("<set buffer=\"");
	 tmp.append($n.text);
	 tmp.append("\" ");
	 if($v!=null || $c!=null)
	  {
	  	tmp.append("chunk=\"");
	  	if($v!=null)
	  	  tmp.append($v.text);
	  	else
	  	  tmp.append($c.text);  
	  	tmp.append("\"");
	  }
	 
	 if(collection.size()==0) 
	  tmp.append("/>");
	 else
	  tmp.append(">");
	   
	 collection.add(0,tmp);
	 if(collection.size()>1)
	   collection.add(new StringBuilder("</set>"));
};

scriptableAction  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(sc=SCRIPTABLE_ACTION l=LANG s=SCRIPT)
{
	 StringBuilder tmp = new StringBuilder("<scriptable-action lang=\"");
         tmp.append($l.text).append("\">");
	 collection.add(tmp);
	 collection.add(new StringBuilder("<![CDATA["));
	 String code = $s.text;
	 String[] lines = code.split("\n");
	 for(int i=0;i<lines.length;i++)
	  collection.add(indent(new StringBuilder(lines[i]),2));
	 collection.add(new StringBuilder("]]>")); 
	 collection.add(new StringBuilder("</scriptable-action>")); 
};
		
proxyAction  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(p=PROXY_ACTION c=CLASS_SPEC (sl=slots {collection.addAll(indent(sl,2));})? )
{
	if($c.text.equals("org.jactr.core.production.action.StopAction"))
          {
           //dump the short version
           collection.add(new StringBuilder("<stop />"));
          }
         else
         { 
         StringBuilder tmp = new StringBuilder("<proxy-action class=\"");
	 tmp.append($c.text);
	 if(collection.size()==0)
  	  tmp.append("\" />");
  	 else
  	  {
  	   tmp.append("\">");
  	   collection.add(new StringBuilder("</proxy-action>"));
  	  }
	 collection.add(0, tmp);
	 }
};
		
modify  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(m=MODIFY_ACTION n=NAME 
         (sl=slots {collection.addAll(indent(sl,2));}
         )?)
{
	 StringBuilder tmp = new StringBuilder("<modify buffer=\"");
	 tmp.append($n.text);
	 if(collection.size()!=0)
	  {
	   tmp.append("\">");
	   collection.add(0, tmp);
	   collection.add(new StringBuilder("</modify>"));
	  }
	 else
	  {
	   tmp.append("\"/>");
	   collection.add(0, tmp);
	  }  
};

remove  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(r=REMOVE_ACTION n=NAME (i=IDENTIFIER | i=VARIABLE)?
       (sl=slots {collection.addAll(indent(sl,2));}
    )?)
{
		StringBuilder tmp = new StringBuilder("<remove buffer=\"");
	 tmp.append($n.text);
	 if(collection.size()!=0)
	  {
	   tmp.append("\">");
	   collection.add(0, tmp);
	   collection.add(new StringBuilder("</remove>"));
	  }
	 else
	  {
	   tmp.append("\"/>");
	   collection.add(0, tmp);
	  }
};
		
output  returns [List<StringBuilder> collection]
@init{
	collection = new ArrayList<StringBuilder>();
}
: ^(o=OUTPUT_ACTION s=STRING)
{
	 StringBuilder tmp = new StringBuilder("<output>\"");
	 tmp.append($s.text);
	 tmp.append("\"</output>");
	 collection.add(tmp);
};
