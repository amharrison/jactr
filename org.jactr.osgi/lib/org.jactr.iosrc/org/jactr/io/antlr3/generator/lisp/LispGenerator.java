// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g 2009-11-18 11:38:49

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


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class LispGenerator extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "MODEL", "LIBRARY", "MODULES", "MODULE", "EXTENSIONS", "EXTENSION", "DECLARATIVE_MEMORY", "CHUNK_TYPE", "CHUNKS", "CHUNK", "PROCEDURAL_MEMORY", "PRODUCTION", "PARAMETERS", "PARAMETER", "BUFFERS", "BUFFER", "CONDITIONS", "MATCH_CONDITION", "QUERY_CONDITION", "SCRIPTABLE_CONDITION", "PROXY_CONDITION", "ACTIONS", "ADD_ACTION", "SET_ACTION", "REMOVE_ACTION", "MODIFY_ACTION", "OUTPUT_ACTION", "SCRIPTABLE_ACTION", "PROXY_ACTION", "LANG", "SCRIPT", "VARIABLE", "STRING", "NUMBER", "IDENTIFIER", "CHUNK_IDENTIFIER", "CHUNK_TYPE_IDENTIFIER", "SLOTS", "SLOT", "LT", "GT", "EQUALS", "NOT", "WITHIN", "GTE", "LTE", "CLASS_SPEC", "NAME", "PARENT", "UNKNOWN"
    };
    public static final int UNKNOWN=53;
    public static final int WITHIN=47;
    public static final int ADD_ACTION=26;
    public static final int LANG=33;
    public static final int SCRIPTABLE_CONDITION=23;
    public static final int MATCH_CONDITION=21;
    public static final int NUMBER=37;
    public static final int CHUNK_TYPE_IDENTIFIER=40;
    public static final int SLOT=42;
    public static final int PRODUCTION=15;
    public static final int GTE=48;
    public static final int PROCEDURAL_MEMORY=14;
    public static final int SLOTS=41;
    public static final int MODULE=7;
    public static final int MODULES=6;
    public static final int SCRIPT=34;
    public static final int DECLARATIVE_MEMORY=10;
    public static final int REMOVE_ACTION=28;
    public static final int CHUNK_TYPE=11;
    public static final int CHUNKS=12;
    public static final int SCRIPTABLE_ACTION=31;
    public static final int PARAMETERS=16;
    public static final int CLASS_SPEC=50;
    public static final int STRING=36;
    public static final int LT=43;
    public static final int GT=44;
    public static final int MODEL=4;
    public static final int CHUNK=13;
    public static final int MODIFY_ACTION=29;
    public static final int EXTENSIONS=8;
    public static final int PROXY_ACTION=32;
    public static final int LTE=49;
    public static final int EQUALS=45;
    public static final int CONDITIONS=20;
    public static final int OUTPUT_ACTION=30;
    public static final int PROXY_CONDITION=24;
    public static final int ACTIONS=25;
    public static final int EOF=-1;
    public static final int VARIABLE=35;
    public static final int LIBRARY=5;
    public static final int BUFFERS=18;
    public static final int PARAMETER=17;
    public static final int QUERY_CONDITION=22;
    public static final int PARENT=52;
    public static final int BUFFER=19;
    public static final int IDENTIFIER=38;
    public static final int NAME=51;
    public static final int NOT=46;
    public static final int EXTENSION=9;
    public static final int CHUNK_IDENTIFIER=39;
    public static final int SET_ACTION=27;

    // delegates
    // delegators


        public LispGenerator(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public LispGenerator(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return LispGenerator.tokenNames; }
    public String getGrammarFileName() { return "/Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g"; }


    //LOGGER
    static private transient Log LOGGER = LogFactory.getLog(LispGenerator.class);


    private Collection<IASTTrimmer> _trimmers = new ArrayList<IASTTrimmer>();


    protected void addTrimmer(IASTTrimmer trimmer)
    {
     _trimmers.add(trimmer);
    }

    protected boolean shouldIgnore(CommonTree element)
    {
     for(IASTTrimmer trimmer : _trimmers)
      if(trimmer.shouldIgnore(element))
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



    // $ANTLR start "model"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:179:1: model returns [Collection<StringBuilder> collection] : ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters ) ;
    public final Collection<StringBuilder> model() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree m=null;
        CommonTree n=null;
        List<StringBuilder> mods = null;

        List<StringBuilder> exts = null;

        List<StringBuilder> bufs = null;

        List<StringBuilder> decs = null;

        List<StringBuilder> procs = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:183:2: ( ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:183:4: ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters )
            {
            m=(CommonTree)match(input,MODEL,FOLLOW_MODEL_in_model307); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_model322); 
            pushFollow(FOLLOW_modules_in_model339);
            mods=modules();

            state._fsp--;

            pushFollow(FOLLOW_extensions_in_model354);
            exts=extensions();

            state._fsp--;

            pushFollow(FOLLOW_buffers_in_model369);
            bufs=buffers();

            state._fsp--;

            match(input,LIBRARY,FOLLOW_LIBRARY_in_model383); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_declarative_memory_in_model400);
            decs=declarative_memory();

            state._fsp--;

            pushFollow(FOLLOW_procedural_memory_in_model416);
            procs=procedural_memory();

            state._fsp--;


            match(input, Token.UP, null); 
            pushFollow(FOLLOW_parameters_in_model433);
            params=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             	 comment(collection, "Automatically generated model ");
             	 comment(collection, "*Unsupported warning* : Extensions, full buffer spec, scriptables and proxies");
             	 
             	 StringBuilder tmp = new StringBuilder("(define-model ");
             	 tmp.append((n!=null?n.getText():null).replace(" ","-"));
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
             

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "model"


    // $ANTLR start "parameters"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:227:1: parameters returns [List<StringBuilder> collection] : ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS ) ;
    public final List<StringBuilder> parameters() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> param = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:1: ( ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:3: ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:3: ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==PARAMETERS) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==DOWN) ) {
                    alt2=1;
                }
                else if ( (LA2_1==UP) ) {
                    alt2=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:5: ^( PARAMETERS (param= parameter )+ )
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters470); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:18: (param= parameter )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==PARAMETER) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:231:19: param= parameter
                    	    {
                    	    pushFollow(FOLLOW_parameter_in_parameters475);
                    	    param=parameter();

                    	    state._fsp--;

                    	    collection.addAll(indent(param,2));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                throw eee;
                        }
                        cnt1++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:232:7: PARAMETERS
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters489); 

                    }
                    break;

            }


             if(collection.size()!=0)
              {
               StringBuilder sb = new StringBuilder();
               for(StringBuilder s : collection)
                sb.append(s.toString()).append(" ");
               collection.clear();
               collection.add(sb); 
              } 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "parameters"


    // $ANTLR start "parameter"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:244:1: parameter returns [Collection<StringBuilder> collection] : ^(p= PARAMETER n= NAME s= STRING ) ;
    public final Collection<StringBuilder> parameter() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree n=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:248:1: ( ^(p= PARAMETER n= NAME s= STRING ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:248:3: ^(p= PARAMETER n= NAME s= STRING )
            {
            p=(CommonTree)match(input,PARAMETER,FOLLOW_PARAMETER_in_parameter513); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_parameter517); 
            s=(CommonTree)match(input,STRING,FOLLOW_STRING_in_parameter521); 

            match(input, Token.UP, null); 

                     String pName = (n!=null?n.getText():null);
                     String newPName = ParameterMap.getLispParameterName(pName);
                     if(newPName==null)
                      newPName = pName;
                     String pValue = (s!=null?s.getText():null);
                     
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "parameter"


    // $ANTLR start "modules"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:272:1: modules returns [List<StringBuilder> collection] : ( ^( MODULES (mod= module )+ ) | MODULES ) ;
    public final List<StringBuilder> modules() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> mod = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:276:1: ( ( ^( MODULES (mod= module )+ ) | MODULES ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:276:3: ( ^( MODULES (mod= module )+ ) | MODULES )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:276:3: ( ^( MODULES (mod= module )+ ) | MODULES )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==MODULES) ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1==DOWN) ) {
                    alt4=1;
                }
                else if ( (LA4_1==EXTENSIONS) ) {
                    alt4=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:276:4: ^( MODULES (mod= module )+ )
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules545); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:277:5: (mod= module )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==MODULE) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:277:6: mod= module
                    	    {
                    	    pushFollow(FOLLOW_module_in_modules555);
                    	    mod=module();

                    	    state._fsp--;

                    	    collection.addAll(indent(mod,2));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:278:11: MODULES
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules569); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "modules"


    // $ANTLR start "module"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:282:1: module returns [Collection<StringBuilder> collection] : ^(mod= MODULE c= CLASS_SPEC (params= parameters )? ) ;
    public final Collection<StringBuilder> module() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree mod=null;
        CommonTree c=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:286:1: ( ^(mod= MODULE c= CLASS_SPEC (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:286:3: ^(mod= MODULE c= CLASS_SPEC (params= parameters )? )
            {
            mod=(CommonTree)match(input,MODULE,FOLLOW_MODULE_in_module592); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_module596); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:286:29: (params= parameters )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==PARAMETERS) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:286:30: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_module601);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 collection.addAll(comment("module class "+(c!=null?c.getText():null)));
            	 StringBuilder tmp = new StringBuilder("(module \"");
            	 tmp.append((c!=null?c.getText():null)).append("\"");
            	 if(_trimmers.size()==0)
            	  tmp.append(" no-import");
            	 tmp.append(")"); 
            	 collection.add(tmp);
            	 
            	 //handle the parameters - which we ignore
            	if(params!=null)
             	 for(StringBuilder sb : params)
            	  comment(collection, "module parameter "+sb.toString());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "module"


    // $ANTLR start "extensions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:303:1: extensions returns [List<StringBuilder> collection] : ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS ) ;
    public final List<StringBuilder> extensions() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> ext = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:307:1: ( ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:307:3: ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:307:3: ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==EXTENSIONS) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==DOWN) ) {
                    alt7=1;
                }
                else if ( (LA7_1==BUFFERS) ) {
                    alt7=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:307:4: ^( EXTENSIONS (ext= extension )+ )
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions629); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:308:5: (ext= extension )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==EXTENSION) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:308:6: ext= extension
                    	    {
                    	    pushFollow(FOLLOW_extension_in_extensions639);
                    	    ext=extension();

                    	    state._fsp--;

                    	    collection.addAll(indent(ext,2));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:309:11: EXTENSIONS
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions653); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "extensions"


    // $ANTLR start "extension"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:313:1: extension returns [Collection<StringBuilder> collection] : ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? ) ;
    public final Collection<StringBuilder> extension() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ext=null;
        CommonTree c=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:317:1: ( ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:317:3: ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? )
            {
            ext=(CommonTree)match(input,EXTENSION,FOLLOW_EXTENSION_in_extension676); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_extension680); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:317:32: (params= parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==PARAMETERS) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:317:33: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_extension685);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

                     collection.addAll(comment("Extension class "+(c!=null?c.getText():null)));
                     StringBuilder tmp = new StringBuilder("(extension \"");
                     tmp.append((c!=null?c.getText():null)).append("\")");
                     collection.add(tmp);
                     
                     //handle the parameters - which we ignore
            	if(params!=null)
             	 for(StringBuilder sb : params)
            	  comment(collection, "parameter "+sb.toString());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "extension"


    // $ANTLR start "buffers"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:330:1: buffers returns [List<StringBuilder> collection] : ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS ) ;
    public final List<StringBuilder> buffers() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> buf = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:334:1: ( ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:334:3: ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:334:3: ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==BUFFERS) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    alt10=1;
                }
                else if ( (LA10_1==LIBRARY) ) {
                    alt10=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:334:4: ^( BUFFERS (buf= buffer )+ )
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers708); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:335:7: (buf= buffer )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==BUFFER) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:335:8: buf= buffer
                    	    {
                    	    pushFollow(FOLLOW_buffer_in_buffers720);
                    	    buf=buffer();

                    	    state._fsp--;

                    	    collection.addAll(indent(buf,2));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:336:13: BUFFERS
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers736); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "buffers"


    // $ANTLR start "buffer"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:340:1: buffer returns [Collection<StringBuilder> collection] : ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? ) ;
    public final Collection<StringBuilder> buffer() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree buff=null;
        CommonTree n=null;
        CommonTree i=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();
        	Collection<String> identifiers=new ArrayList<String>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:345:1: ( ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:345:3: ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? )
            {
            buff=(CommonTree)match(input,BUFFER,FOLLOW_BUFFER_in_buffer758); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_buffer762); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:346:14: ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==CHUNKS) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==DOWN) ) {
                    alt12=1;
                }
                else if ( (LA12_1==UP||LA12_1==PARAMETERS) ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:346:15: ^( CHUNKS (i= CHUNK_IDENTIFIER )+ )
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_buffer781); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:347:16: (i= CHUNK_IDENTIFIER )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==CHUNK_IDENTIFIER) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:347:17: i= CHUNK_IDENTIFIER
                    	    {
                    	    i=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_buffer802); 
                    	    identifiers.add((i!=null?i.getText():null));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:348:21: CHUNKS
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_buffer826); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:349:14: (params= parameters )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==PARAMETERS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:349:15: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_buffer846);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

             if((n!=null?n.getText():null).equals("goal") && identifiers.size()!=0)
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "buffer"


    // $ANTLR start "declarative_memory"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:365:1: declarative_memory returns [List<StringBuilder> collection] : ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY ) ;
    public final List<StringBuilder> declarative_memory() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> ct = null;



        	collection = new ArrayList<StringBuilder>();
        	
        	collection.addAll(comment("declarative memory container for chunks and chunk-types"));

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:371:1: ( ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:371:3: ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:371:3: ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==DECLARATIVE_MEMORY) ) {
                int LA15_1 = input.LA(2);

                if ( (LA15_1==DOWN) ) {
                    alt15=1;
                }
                else if ( (LA15_1==PROCEDURAL_MEMORY) ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:371:4: ^( DECLARATIVE_MEMORY (ct= chunktype )+ )
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory874); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:372:7: (ct= chunktype )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==CHUNK_TYPE) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:372:8: ct= chunktype
                    	    {
                    	    pushFollow(FOLLOW_chunktype_in_declarative_memory886);
                    	    ct=chunktype();

                    	    state._fsp--;


                    	                          if(ct.size()!=0)
                    	                           {
                    	          	                collection.addAll(indent(ct,2));
                    	          	                //and add some space after the chunks before the chunktype
                    	          	                newLines(collection, 2);
                    	          	               }
                    	                        

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:380:10: DECLARATIVE_MEMORY
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory899); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "declarative_memory"


    // $ANTLR start "procedural_memory"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:384:1: procedural_memory returns [List<StringBuilder> collection] : ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY ) ;
    public final List<StringBuilder> procedural_memory() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> prod = null;



        	collection = new ArrayList<StringBuilder>();
        	
        	collection.addAll(comment("procedural memory contents"));

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:390:1: ( ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:390:3: ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:390:3: ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==PROCEDURAL_MEMORY) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==DOWN) ) {
                    alt17=1;
                }
                else if ( (LA17_1==UP) ) {
                    alt17=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:390:4: ^( PROCEDURAL_MEMORY (prod= production )+ )
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory921); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:391:7: (prod= production )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==PRODUCTION) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:391:8: prod= production
                    	    {
                    	    pushFollow(FOLLOW_production_in_procedural_memory933);
                    	    prod=production();

                    	    state._fsp--;


                    	                            if(prod.size()!=0)
                    	                             {
                    	          	                  collection.addAll(indent(prod,2));
                    	          	                  newLines(collection, 2);
                    	          	                 } 
                    	          	                

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:398:13: PROCEDURAL_MEMORY
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory949); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "procedural_memory"


    // $ANTLR start "chunktype"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:402:1: chunktype returns [Collection<StringBuilder> collection] : ^(ct= CHUNK_TYPE n= NAME (p= PARENT )? (sl= slots )? (ch= chunks )? (params= parameters )? ) ;
    public final Collection<StringBuilder> chunktype() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ct=null;
        CommonTree n=null;
        CommonTree p=null;
        Collection<StringBuilder> sl = null;

        Collection<StringBuilder> ch = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:406:1: ( ^(ct= CHUNK_TYPE n= NAME (p= PARENT )? (sl= slots )? (ch= chunks )? (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:406:3: ^(ct= CHUNK_TYPE n= NAME (p= PARENT )? (sl= slots )? (ch= chunks )? (params= parameters )? )
            {
            ct=(CommonTree)match(input,CHUNK_TYPE,FOLLOW_CHUNK_TYPE_in_chunktype972); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunktype991); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:408:15: (p= PARENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==PARENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:408:16: p= PARENT
                    {
                    p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_chunktype1010); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:409:15: (sl= slots )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==SLOTS) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:409:16: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_chunktype1032);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:410:15: (ch= chunks )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==CHUNKS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:410:16: ch= chunks
                    {
                    pushFollow(FOLLOW_chunks_in_chunktype1054);
                    ch=chunks();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:411:15: (params= parameters )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==PARAMETERS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:411:16: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_chunktype1076);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 


             if(shouldIgnore(ct))
             {
             }
             else
             {
            	StringBuilder tmp = new StringBuilder("(chunk-type ");
            	tmp.append((n!=null?n.getText():null).replace(" ","-"));
            	tmp.append(" ");
            	if(!isEmpty(p))
            	 {
            	 	 tmp.append("(:include ");
            	 	 tmp.append((p!=null?p.getText():null).replace(" ","-"));
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
            	 comment(collection, "chunks for "+(n!=null?n.getText():null));
            	 // handle the chunks
            	 collection.addAll(ch);
                   }	


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "chunktype"


    // $ANTLR start "slots"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:449:1: slots returns [Collection<StringBuilder> collection] : ( ^( SLOTS (s= slot )+ ) | SLOTS ) ;
    public final Collection<StringBuilder> slots() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        Collection<StringBuilder> s = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:453:1: ( ( ^( SLOTS (s= slot )+ ) | SLOTS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:453:2: ( ^( SLOTS (s= slot )+ ) | SLOTS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:453:2: ( ^( SLOTS (s= slot )+ ) | SLOTS )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SLOTS) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==DOWN) ) {
                    alt23=1;
                }
                else if ( (LA23_1==UP||LA23_1==CHUNKS||LA23_1==PARAMETERS) ) {
                    alt23=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:453:3: ^( SLOTS (s= slot )+ )
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots1101); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:454:6: (s= slot )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==SLOT) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:454:7: s= slot
                    	    {
                    	    pushFollow(FOLLOW_slot_in_slots1112);
                    	    s=slot();

                    	    state._fsp--;

                    	    collection.addAll(s);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:454:41: SLOTS
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots1119); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "slots"


    // $ANTLR start "conditionalSlots"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:456:1: conditionalSlots returns [Collection<StringBuilder> collection] : ( ^( SLOTS (s= conditionalSlot )+ ) | SLOTS ) ;
    public final Collection<StringBuilder> conditionalSlots() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        Collection<StringBuilder> s = null;



         collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:3: ( ( ^( SLOTS (s= conditionalSlot )+ ) | SLOTS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:5: ( ^( SLOTS (s= conditionalSlot )+ ) | SLOTS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:5: ( ^( SLOTS (s= conditionalSlot )+ ) | SLOTS )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SLOTS) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==DOWN) ) {
                    alt25=1;
                }
                else if ( (LA25_1==UP) ) {
                    alt25=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:6: ^( SLOTS (s= conditionalSlot )+ )
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_conditionalSlots1140); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:14: (s= conditionalSlot )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==SLOT) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:15: s= conditionalSlot
                    	    {
                    	    pushFollow(FOLLOW_conditionalSlot_in_conditionalSlots1145);
                    	    s=conditionalSlot();

                    	    state._fsp--;

                    	    collection.addAll(s);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt24 >= 1 ) break loop24;
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:460:62: SLOTS
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_conditionalSlots1154); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "conditionalSlots"


    // $ANTLR start "chunks"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:463:1: chunks returns [Collection<StringBuilder> collection] : ( ^( CHUNKS (c= chunk )+ ) | CHUNKS ) ;
    public final Collection<StringBuilder> chunks() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        Collection<StringBuilder> c = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:467:1: ( ( ^( CHUNKS (c= chunk )+ ) | CHUNKS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:467:3: ( ^( CHUNKS (c= chunk )+ ) | CHUNKS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:467:3: ( ^( CHUNKS (c= chunk )+ ) | CHUNKS )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==CHUNKS) ) {
                int LA27_1 = input.LA(2);

                if ( (LA27_1==DOWN) ) {
                    alt27=1;
                }
                else if ( (LA27_1==UP||LA27_1==PARAMETERS) ) {
                    alt27=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 27, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:467:4: ^( CHUNKS (c= chunk )+ )
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks1177); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:468:6: (c= chunk )+
                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==CHUNK) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:468:7: c= chunk
                    	    {
                    	    pushFollow(FOLLOW_chunk_in_chunks1188);
                    	    c=chunk();

                    	    state._fsp--;


                    	         	         collection.addAll(c);
                    	         	        

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:471:12: CHUNKS
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks1203); 

                    }
                    break;

            }




            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "chunks"


    // $ANTLR start "conditionalSlot"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:475:1: conditionalSlot returns [Collection<StringBuilder> collection] : ^(sl= SLOT n= NAME ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) ) ) ;
    public final Collection<StringBuilder> conditionalSlot() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree sl=null;
        CommonTree n=null;
        CommonTree v=null;


        	collection = new ArrayList<StringBuilder>();
        	String cond = "";

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:480:1: ( ^(sl= SLOT n= NAME ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) ) ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:480:3: ^(sl= SLOT n= NAME ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) ) )
            {
            sl=(CommonTree)match(input,SLOT,FOLLOW_SLOT_in_conditionalSlot1226); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_conditionalSlot1230); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:481:10: ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:481:11: ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:481:11: ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE )
            int alt28=7;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt28=1;
                }
                break;
            case LT:
                {
                alt28=2;
                }
                break;
            case GT:
                {
                alt28=3;
                }
                break;
            case NOT:
                {
                alt28=4;
                }
                break;
            case WITHIN:
                {
                alt28=5;
                }
                break;
            case LTE:
                {
                alt28=6;
                }
                break;
            case GTE:
                {
                alt28=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:481:12: EQUALS
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_conditionalSlot1244); 
                    cond=""; 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:482:13: LT
                    {
                    match(input,LT,FOLLOW_LT_in_conditionalSlot1260); 
                    cond="<";

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:483:13: GT
                    {
                    match(input,GT,FOLLOW_GT_in_conditionalSlot1279); 
                    cond=">";

                    }
                    break;
                case 4 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:484:13: NOT
                    {
                    match(input,NOT,FOLLOW_NOT_in_conditionalSlot1298); 
                    cond="-";

                    }
                    break;
                case 5 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:485:13: WITHIN
                    {
                    match(input,WITHIN,FOLLOW_WITHIN_in_conditionalSlot1316); 
                    cond="<>";

                    }
                    break;
                case 6 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:486:13: LTE
                    {
                    match(input,LTE,FOLLOW_LTE_in_conditionalSlot1332); 
                    cond="<=";

                    }
                    break;
                case 7 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:487:13: GTE
                    {
                    match(input,GTE,FOLLOW_GTE_in_conditionalSlot1351); 
                    cond=">=";

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:489:11: (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER )
            int alt29=4;
            switch ( input.LA(1) ) {
            case VARIABLE:
                {
                alt29=1;
                }
                break;
            case STRING:
                {
                alt29=2;
                }
                break;
            case IDENTIFIER:
                {
                alt29=3;
                }
                break;
            case NUMBER:
                {
                alt29=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:489:12: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_conditionalSlot1383); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:490:12: v= STRING
                    {
                    v=(CommonTree)match(input,STRING,FOLLOW_STRING_in_conditionalSlot1399); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:491:12: v= IDENTIFIER
                    {
                    v=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_conditionalSlot1416); 

                    }
                    break;
                case 4 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:492:12: v= NUMBER
                    {
                    v=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_conditionalSlot1432); 

                    }
                    break;

            }


            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder(cond);
            	 tmp.append(" ").append((n!=null?n.getText():null));
            	 String value = (v!=null?v.getText():null);
            	 if("null".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
            	  value = "nil";
            	 if("true".equalsIgnoreCase(value))
            	  value="t";
            	 boolean isReserved = "nil".equalsIgnoreCase(value) ||
            	                      "t".equalsIgnoreCase(value);
            	 tmp.append(" ");
            	 if((v!=null?v.getType():0)==STRING && !isReserved)
            	  tmp.append("\"");
            	 tmp.append(value);
            	 if((v!=null?v.getType():0)==STRING && !isReserved)
            	  tmp.append("\"");
            	 tmp.append(" ");
            	 
            	 LOGGER.debug("slot yielded : "+tmp);
            	 
            	 collection.add(tmp);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "conditionalSlot"


    // $ANTLR start "slot"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:518:1: slot returns [Collection<StringBuilder> collection] : ^(sl= SLOT n= NAME EQUALS (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) ) ;
    public final Collection<StringBuilder> slot() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree sl=null;
        CommonTree n=null;
        CommonTree v=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:522:1: ( ^(sl= SLOT n= NAME EQUALS (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:522:3: ^(sl= SLOT n= NAME EQUALS (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )
            {
            sl=(CommonTree)match(input,SLOT,FOLLOW_SLOT_in_slot1477); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_slot1481); 
            match(input,EQUALS,FOLLOW_EQUALS_in_slot1493); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:524:11: (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER )
            int alt30=4;
            switch ( input.LA(1) ) {
            case VARIABLE:
                {
                alt30=1;
                }
                break;
            case STRING:
                {
                alt30=2;
                }
                break;
            case IDENTIFIER:
                {
                alt30=3;
                }
                break;
            case NUMBER:
                {
                alt30=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:524:12: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_slot1508); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:525:12: v= STRING
                    {
                    v=(CommonTree)match(input,STRING,FOLLOW_STRING_in_slot1524); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:526:12: v= IDENTIFIER
                    {
                    v=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_slot1541); 

                    }
                    break;
                case 4 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:527:12: v= NUMBER
                    {
                    v=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_slot1557); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	
            	 StringBuilder tmp = new StringBuilder();
            	 tmp.append(" ").append((n!=null?n.getText():null));
            	 String value = (v!=null?v.getText():null);
            	 if("null".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
            	  value = "nil";
            	 if("true".equalsIgnoreCase(value))
            	  value="t";
            	 boolean isReserved = "nil".equalsIgnoreCase(value) ||
            	                      "t".equalsIgnoreCase(value);
            	 tmp.append(" ");
            	 if((v!=null?v.getType():0)==STRING && !isReserved)
            	  tmp.append("\"");
            	 tmp.append(value);
            	 if((v!=null?v.getType():0)==STRING && !isReserved)
            	  tmp.append("\"");
            	 tmp.append(" ");
            	 
            	 LOGGER.debug("slot yielded : "+tmp);
            	 
            	 collection.add(tmp);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "slot"


    // $ANTLR start "chunk"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:554:1: chunk returns [Collection<StringBuilder> collection] : ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? ) ;
    public final Collection<StringBuilder> chunk() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ch=null;
        CommonTree n=null;
        CommonTree p=null;
        Collection<StringBuilder> sl = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:558:1: ( ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:558:3: ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? )
            {
            ch=(CommonTree)match(input,CHUNK,FOLLOW_CHUNK_in_chunk1603); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunk1614); 
            p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_chunk1624); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:561:7: (sl= slots )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==SLOTS) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:561:8: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_chunk1636);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:562:7: (params= parameters )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==PARAMETERS) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:562:8: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_chunk1650);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

              if(shouldIgnore(ch))
              {
              }
              else
              {
            	 StringBuilder tmp = new StringBuilder("(add-dm (");
            	 tmp.append((n!=null?n.getText():null).replace(" ","-"));
            	 tmp.append(" isa ");
            	 tmp.append((p!=null?p.getText():null).replace(" ","-"));
            	 tmp.append(" ");
            	 
            	 for(StringBuilder sb : sl)
            	  tmp.append(sb);
            	 
            	 tmp.append("))"); 
            	 collection.add(tmp);
            	 
            	 if(params!=null && params.size()!=0)
            	 {
            	  StringBuilder param = new StringBuilder("(sdp ");
            	  param.append((n!=null?n.getText():null));
            	  param.append(" ");
            	 
            	  for(StringBuilder sb : params)
            	   param.append(sb);
            	  param.append(")");
            	  collection.add(param); 
            	 }
             }	 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "chunk"


    // $ANTLR start "production"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:595:1: production returns [Collection<StringBuilder> collection] : ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? ) ;
    public final Collection<StringBuilder> production() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree n=null;
        List<StringBuilder> conds = null;

        List<StringBuilder> acts = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:599:1: ( ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:599:3: ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? )
            {
            p=(CommonTree)match(input,PRODUCTION,FOLLOW_PRODUCTION_in_production1680); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_production1684); 
            pushFollow(FOLLOW_conditions_in_production1688);
            conds=conditions();

            state._fsp--;

            pushFollow(FOLLOW_actions_in_production1692);
            acts=actions();

            state._fsp--;

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:599:55: (params= parameters )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==PARAMETERS) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:599:56: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_production1697);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

             if(shouldIgnore(p))
             {
             }
             else
             {
            	 StringBuilder tmp = new StringBuilder("(p ");
            	 tmp.append((n!=null?n.getText():null));
            	 collection.add(tmp);
            	 
            	 collection.addAll(indent(conds,2));
            	 
            	 collection.add(new StringBuilder(" ==>"));
            	 
            	 collection.addAll(indent(acts,2));
            	 
            	 collection.add(new StringBuilder(")"));
            	 
            	 if(params!=null && params.size()!=0)
            	 {
            	  StringBuilder param = new StringBuilder("(spp ");
            	  param.append((n!=null?n.getText():null));
            	  for(StringBuilder sb: params)
            	   param.append(sb);
            	  param.append(")");
            	 }
             }	 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "production"


    // $ANTLR start "conditions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:629:1: conditions returns [List<StringBuilder> collection] : ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ ) ;
    public final List<StringBuilder> conditions() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> tmp = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:633:1: ( ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:633:3: ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ )
            {
            match(input,CONDITIONS,FOLLOW_CONDITIONS_in_conditions1722); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:633:16: (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+
            int cnt34=0;
            loop34:
            do {
                int alt34=5;
                switch ( input.LA(1) ) {
                case MATCH_CONDITION:
                    {
                    alt34=1;
                    }
                    break;
                case QUERY_CONDITION:
                    {
                    alt34=2;
                    }
                    break;
                case SCRIPTABLE_CONDITION:
                    {
                    alt34=3;
                    }
                    break;
                case PROXY_CONDITION:
                    {
                    alt34=4;
                    }
                    break;

                }

                switch (alt34) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:633:17: tmp= pattern
            	    {
            	    pushFollow(FOLLOW_pattern_in_conditions1727);
            	    tmp=pattern();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:634:17: tmp= queryCondition
            	    {
            	    pushFollow(FOLLOW_queryCondition_in_conditions1749);
            	    tmp=queryCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:635:17: tmp= scriptableCondition
            	    {
            	    pushFollow(FOLLOW_scriptableCondition_in_conditions1771);
            	    tmp=scriptableCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:636:17: tmp= proxyCondition
            	    {
            	    pushFollow(FOLLOW_proxyCondition_in_conditions1793);
            	    tmp=proxyCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);


            match(input, Token.UP, null); 



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "conditions"


    // $ANTLR start "pattern"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:641:1: pattern returns [List<StringBuilder> collection] : ^(p= MATCH_CONDITION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= conditionalSlots )? ) ;
    public final List<StringBuilder> pattern() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree ct=null;
        CommonTree v=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:1: ( ^(p= MATCH_CONDITION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= conditionalSlots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:3: ^(p= MATCH_CONDITION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= conditionalSlots )? )
            {
            p=(CommonTree)match(input,MATCH_CONDITION,FOLLOW_MATCH_CONDITION_in_pattern1835); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_pattern1839); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:30: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
            int alt35=3;
            switch ( input.LA(1) ) {
            case CHUNK_IDENTIFIER:
                {
                alt35=1;
                }
                break;
            case CHUNK_TYPE_IDENTIFIER:
                {
                alt35=2;
                }
                break;
            case VARIABLE:
                {
                alt35=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:31: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_pattern1844); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:52: ct= CHUNK_TYPE_IDENTIFIER
                    {
                    ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_pattern1850); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:645:79: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_pattern1856); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:646:13: (sl= conditionalSlots )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==SLOTS) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:646:14: sl= conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_pattern1875);
                    sl=conditionalSlots();

                    state._fsp--;

                    collection.addAll(sl);

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("=");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append(">");
            	 collection.add(0, tmp);
            	 StringBuilder isa = new StringBuilder();
            	 if(v!=null || c!=null)
            	  {
            	  	//referring to a specific chunk
            	      if(v!=null)	
            	  	isa.append((v!=null?v.getText():null));
            	      else
            	        isa.append((c!=null?c.getText():null));	
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
            	 	 isa.append((ct!=null?ct.getText():null));
            	 }
            	 
            	 collection.add(1, isa);
            	 collection.add(new StringBuilder()); 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "pattern"


    // $ANTLR start "queryCondition"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:679:1: queryCondition returns [List<StringBuilder> collection] : ^(q= QUERY_CONDITION n= NAME (sl= conditionalSlots )? ) ;
    public final List<StringBuilder> queryCondition() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree q=null;
        CommonTree n=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:683:1: ( ^(q= QUERY_CONDITION n= NAME (sl= conditionalSlots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:683:3: ^(q= QUERY_CONDITION n= NAME (sl= conditionalSlots )? )
            {
            q=(CommonTree)match(input,QUERY_CONDITION,FOLLOW_QUERY_CONDITION_in_queryCondition1915); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_queryCondition1919); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:684:13: (sl= conditionalSlots )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==SLOTS) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:684:14: sl= conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_queryCondition1936);
                    sl=conditionalSlots();

                    state._fsp--;

                    collection.addAll(sl);

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("?");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append(">");
            	 collection.add(0, tmp);
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "queryCondition"


    // $ANTLR start "scriptableCondition"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:695:1: scriptableCondition returns [Collection<StringBuilder> collection] : ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) ;
    public final Collection<StringBuilder> scriptableCondition() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree sc=null;
        CommonTree l=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:699:1: ( ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:699:3: ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT )
            {
            sc=(CommonTree)match(input,SCRIPTABLE_CONDITION,FOLLOW_SCRIPTABLE_CONDITION_in_scriptableCondition1979); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptableCondition1983); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptableCondition1987); 

            match(input, Token.UP, null); 

              String scriptText = (s!=null?s.getText():null);
              String[] lines = scriptText.split("\n");
              
              scriptText = scriptText.replace("\n","\n;;");
              collection.addAll(comment("Original script for language "+(l!=null?l.getText():null), scriptText));
              collection.add(new StringBuilder());
              
              StringBuilder scriptLine = new StringBuilder("!eval! (");
              scriptLine.append("\"").append((l!=null?l.getText():null)).append("\" ");
              for(String line : lines)
               {
                //replace all " with \" 
                line = line.replace("\"", "\\\"");
                scriptLine.append("\"").append(line).append("\" ");
               }
              scriptLine.append(")");
              collection.add(scriptLine);
              collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "scriptableCondition"


    // $ANTLR start "proxyCondition"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:721:1: proxyCondition returns [Collection<StringBuilder> collection] : ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) ;
    public final Collection<StringBuilder> proxyCondition() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree c=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:725:1: ( ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:725:3: ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? )
            {
            p=(CommonTree)match(input,PROXY_CONDITION,FOLLOW_PROXY_CONDITION_in_proxyCondition2010); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyCondition2014); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:725:36: (sl= slots )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==SLOTS) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:725:37: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyCondition2019);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 collection.add(new StringBuilder("=proxy>"));
            	 collection.add((new StringBuilder("isa ")).append((c!=null?c.getText():null)));
            	 if(sl!=null) 
            	  collection.addAll(sl);
            	 collection.add(new StringBuilder()); 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "proxyCondition"


    // $ANTLR start "actions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:734:1: actions returns [List<StringBuilder> collection] : ^( ACTIONS (tmp= add | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ ) ;
    public final List<StringBuilder> actions() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> tmp = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:738:1: ( ^( ACTIONS (tmp= add | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:738:3: ^( ACTIONS (tmp= add | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ )
            {
            match(input,ACTIONS,FOLLOW_ACTIONS_in_actions2042); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:738:13: (tmp= add | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+
            int cnt39=0;
            loop39:
            do {
                int alt39=7;
                switch ( input.LA(1) ) {
                case ADD_ACTION:
                    {
                    alt39=1;
                    }
                    break;
                case MODIFY_ACTION:
                    {
                    alt39=2;
                    }
                    break;
                case REMOVE_ACTION:
                    {
                    alt39=3;
                    }
                    break;
                case OUTPUT_ACTION:
                    {
                    alt39=4;
                    }
                    break;
                case SCRIPTABLE_ACTION:
                    {
                    alt39=5;
                    }
                    break;
                case PROXY_ACTION:
                    {
                    alt39=6;
                    }
                    break;

                }

                switch (alt39) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:738:14: tmp= add
            	    {
            	    pushFollow(FOLLOW_add_in_actions2047);
            	    tmp=add();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:739:11: tmp= modify
            	    {
            	    pushFollow(FOLLOW_modify_in_actions2063);
            	    tmp=modify();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:740:11: tmp= remove
            	    {
            	    pushFollow(FOLLOW_remove_in_actions2079);
            	    tmp=remove();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:741:11: tmp= output
            	    {
            	    pushFollow(FOLLOW_output_in_actions2095);
            	    tmp=output();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 5 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:742:11: tmp= scriptableAction
            	    {
            	    pushFollow(FOLLOW_scriptableAction_in_actions2111);
            	    tmp=scriptableAction();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 6 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:743:11: tmp= proxyAction
            	    {
            	    pushFollow(FOLLOW_proxyAction_in_actions2127);
            	    tmp=proxyAction();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);


            match(input, Token.UP, null); 



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "actions"


    // $ANTLR start "add"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:748:1: add returns [List<StringBuilder> collection] : ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= conditionalSlots )? ) ;
    public final List<StringBuilder> add() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree a=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree ct=null;
        CommonTree v=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:1: ( ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= conditionalSlots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:3: ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= conditionalSlots )? )
            {
            a=(CommonTree)match(input,ADD_ACTION,FOLLOW_ADD_ACTION_in_add2163); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_add2167); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:25: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )?
            int alt40=4;
            switch ( input.LA(1) ) {
                case CHUNK_IDENTIFIER:
                    {
                    alt40=1;
                    }
                    break;
                case CHUNK_TYPE_IDENTIFIER:
                    {
                    alt40=2;
                    }
                    break;
                case VARIABLE:
                    {
                    alt40=3;
                    }
                    break;
            }

            switch (alt40) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:26: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_add2172); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:47: ct= CHUNK_TYPE_IDENTIFIER
                    {
                    ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add2178); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:752:74: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_add2184); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:753:15: (sl= conditionalSlots )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==SLOTS) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:753:16: sl= conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_add2206);
                    sl=conditionalSlots();

                    state._fsp--;

                    collection.addAll(sl);

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("+");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append(">");
            	 collection.add(0, tmp);
            	 StringBuilder isa = new StringBuilder();
            	 
            	 if(v!=null || c!=null)
            	  {
            	       if(v!=null)
            	  	isa.append((v!=null?v.getText():null));
            	       else
            	        isa.append((c!=null?c.getText():null));	
            	  }
            	 else if(ct!=null)
            	 {
            	 	 //see the comments in pattern regarding this ambiguity
            	 	 isa.append("isa ");
            	 	 isa.append((ct!=null?ct.getText():null));
            	 }
            	 
            	 collection.add(1, isa);
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "add"


    // $ANTLR start "scriptableAction"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:780:1: scriptableAction returns [Collection<StringBuilder> collection] : ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) ;
    public final Collection<StringBuilder> scriptableAction() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree sc=null;
        CommonTree l=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:784:1: ( ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:784:3: ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT )
            {
            sc=(CommonTree)match(input,SCRIPTABLE_ACTION,FOLLOW_SCRIPTABLE_ACTION_in_scriptableAction2248); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptableAction2252); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptableAction2256); 

            match(input, Token.UP, null); 

              String scriptText = (s!=null?s.getText():null);
              String[] lines = scriptText.split("\n");
              
              scriptText = scriptText.replace("\n","\n;;");
              collection.addAll(comment("Original script for language "+(l!=null?l.getText():null), scriptText));
              collection.add(new StringBuilder());
              
              StringBuilder scriptLine = new StringBuilder("!eval! (");
              scriptLine.append("\"").append((l!=null?l.getText():null)).append("\" ");
              for(String line : lines)
               {
                //replace all " with \" 
                line = line.replace("\"", "\\\"");
                scriptLine.append("\"").append(line).append("\" ");
               }
              scriptLine.append(")");
              collection.add(scriptLine);
              collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "scriptableAction"


    // $ANTLR start "proxyAction"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:806:1: proxyAction returns [Collection<StringBuilder> collection] : ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) ;
    public final Collection<StringBuilder> proxyAction() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree c=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:810:1: ( ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:810:3: ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? )
            {
            p=(CommonTree)match(input,PROXY_ACTION,FOLLOW_PROXY_ACTION_in_proxyAction2281); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyAction2285); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:810:33: (sl= slots )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==SLOTS) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:810:34: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyAction2290);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	if((c!=null?c.getText():null).equals("org.jactr.core.production.action.StopAction"))
                      {
                       //dump the short version
                       collection.add(new StringBuilder("!stop!"));
                      }
                     else
                     { 
             	  collection.add(new StringBuilder("+proxy>"));
             	  collection.add((new StringBuilder("isa ")).append((c!=null?c.getText():null)));
             	  if(sl!=null)
             	   collection.addAll(sl);
            	 }
            	 
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "proxyAction"


    // $ANTLR start "modify"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:828:1: modify returns [List<StringBuilder> collection] : ^(m= MODIFY_ACTION n= NAME (sl= slots )? ) ;
    public final List<StringBuilder> modify() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree m=null;
        CommonTree n=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:832:1: ( ^(m= MODIFY_ACTION n= NAME (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:832:3: ^(m= MODIFY_ACTION n= NAME (sl= slots )? )
            {
            m=(CommonTree)match(input,MODIFY_ACTION,FOLLOW_MODIFY_ACTION_in_modify2317); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_modify2321); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:833:10: (sl= slots )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==SLOTS) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:833:11: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_modify2336);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(sl);

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("=");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append(">");
            	 collection.add(0, tmp);
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "modify"


    // $ANTLR start "remove"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:843:1: remove returns [List<StringBuilder> collection] : ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? ) ;
    public final List<StringBuilder> remove() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree r=null;
        CommonTree n=null;
        CommonTree i=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:847:1: ( ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:847:3: ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? )
            {
            r=(CommonTree)match(input,REMOVE_ACTION,FOLLOW_REMOVE_ACTION_in_remove2373); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_remove2377); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:847:28: (i= IDENTIFIER | i= VARIABLE )?
            int alt44=3;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==IDENTIFIER) ) {
                alt44=1;
            }
            else if ( (LA44_0==VARIABLE) ) {
                alt44=2;
            }
            switch (alt44) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:847:29: i= IDENTIFIER
                    {
                    i=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_remove2382); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:847:44: i= VARIABLE
                    {
                    i=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_remove2388); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:848:8: (sl= slots )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==SLOTS) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:848:9: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_remove2402);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(sl);

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("-");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append(">");
            	 collection.add(0, tmp);
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "remove"


    // $ANTLR start "output"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:858:1: output returns [Collection<StringBuilder> collection] : ^(o= OUTPUT_ACTION s= STRING ) ;
    public final Collection<StringBuilder> output() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree o=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:862:1: ( ^(o= OUTPUT_ACTION s= STRING ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/generator/lisp/LispGenerator.g:862:3: ^(o= OUTPUT_ACTION s= STRING )
            {
            o=(CommonTree)match(input,OUTPUT_ACTION,FOLLOW_OUTPUT_ACTION_in_output2436); 

            match(input, Token.DOWN, null); 
            s=(CommonTree)match(input,STRING,FOLLOW_STRING_in_output2440); 

            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("!output! (");
            	 String text = (s!=null?s.getText():null);
            	 text = text.replace("'", "");
            	 text = text.replace("`","");
            	 text = text.replace(";","");
            	 tmp.append("\"");
            	 tmp.append(text);
            	 tmp.append("\"");
            	 tmp.append(")");
            	 collection.add(tmp);
            	 collection.add(new StringBuilder());


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return collection;
    }
    // $ANTLR end "output"

    // Delegated rules


 

    public static final BitSet FOLLOW_MODEL_in_model307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_model322 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_modules_in_model339 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_extensions_in_model354 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_buffers_in_model369 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_LIBRARY_in_model383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_declarative_memory_in_model400 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_procedural_memory_in_model416 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_parameters_in_model433 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters470 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_parameter_in_parameters475 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAMETER_in_parameter513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_parameter517 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_in_parameter521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODULES_in_modules545 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_module_in_modules555 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_MODULES_in_modules569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODULE_in_module592 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_module596 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_module601 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions629 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_extension_in_extensions639 = new BitSet(new long[]{0x0000000000000208L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENSION_in_extension676 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_extension680 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_extension685 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers708 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_buffer_in_buffers720 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BUFFER_in_buffer758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_buffer762 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_CHUNKS_in_buffer781 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_buffer802 = new BitSet(new long[]{0x0000008000000008L});
    public static final BitSet FOLLOW_CHUNKS_in_buffer826 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_buffer846 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory874 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunktype_in_declarative_memory886 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_production_in_procedural_memory933 = new BitSet(new long[]{0x0000000000008008L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHUNK_TYPE_in_chunktype972 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunktype991 = new BitSet(new long[]{0x0010020000011008L});
    public static final BitSet FOLLOW_PARENT_in_chunktype1010 = new BitSet(new long[]{0x0000020000011008L});
    public static final BitSet FOLLOW_slots_in_chunktype1032 = new BitSet(new long[]{0x0000000000011008L});
    public static final BitSet FOLLOW_chunks_in_chunktype1054 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_chunktype1076 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SLOTS_in_slots1101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_slot_in_slots1112 = new BitSet(new long[]{0x0000040000000008L});
    public static final BitSet FOLLOW_SLOTS_in_slots1119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLOTS_in_conditionalSlots1140 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalSlot_in_conditionalSlots1145 = new BitSet(new long[]{0x0000040000000008L});
    public static final BitSet FOLLOW_SLOTS_in_conditionalSlots1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks1177 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunk_in_chunks1188 = new BitSet(new long[]{0x0000000000002008L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLOT_in_conditionalSlot1226 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_conditionalSlot1230 = new BitSet(new long[]{0x0003F80000000000L});
    public static final BitSet FOLLOW_EQUALS_in_conditionalSlot1244 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LT_in_conditionalSlot1260 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GT_in_conditionalSlot1279 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_NOT_in_conditionalSlot1298 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_WITHIN_in_conditionalSlot1316 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LTE_in_conditionalSlot1332 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GTE_in_conditionalSlot1351 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_VARIABLE_in_conditionalSlot1383 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_conditionalSlot1399 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_conditionalSlot1416 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMBER_in_conditionalSlot1432 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SLOT_in_slot1477 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_slot1481 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_EQUALS_in_slot1493 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_VARIABLE_in_slot1508 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_slot1524 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_slot1541 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMBER_in_slot1557 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHUNK_in_chunk1603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunk1614 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_PARENT_in_chunk1624 = new BitSet(new long[]{0x0000020000010008L});
    public static final BitSet FOLLOW_slots_in_chunk1636 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_chunk1650 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PRODUCTION_in_production1680 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_production1684 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_conditions_in_production1688 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_actions_in_production1692 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_production1697 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONDITIONS_in_conditions1722 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_in_conditions1727 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_queryCondition_in_conditions1749 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_scriptableCondition_in_conditions1771 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_proxyCondition_in_conditions1793 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_MATCH_CONDITION_in_pattern1835 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_pattern1839 = new BitSet(new long[]{0x0000018800000000L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_pattern1844 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_pattern1850 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_pattern1856 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_conditionalSlots_in_pattern1875 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUERY_CONDITION_in_queryCondition1915 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_queryCondition1919 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_conditionalSlots_in_queryCondition1936 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_CONDITION_in_scriptableCondition1979 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptableCondition1983 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptableCondition1987 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_CONDITION_in_proxyCondition2010 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyCondition2014 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyCondition2019 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTIONS_in_actions2042 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_add_in_actions2047 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_modify_in_actions2063 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_remove_in_actions2079 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_output_in_actions2095 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_scriptableAction_in_actions2111 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_proxyAction_in_actions2127 = new BitSet(new long[]{0x00000001F4000008L});
    public static final BitSet FOLLOW_ADD_ACTION_in_add2163 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_add2167 = new BitSet(new long[]{0x0000038800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_add2172 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add2178 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_add2184 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_conditionalSlots_in_add2206 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_ACTION_in_scriptableAction2248 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptableAction2252 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptableAction2256 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_ACTION_in_proxyAction2281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyAction2285 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyAction2290 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFY_ACTION_in_modify2317 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_modify2321 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_modify2336 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REMOVE_ACTION_in_remove2373 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_remove2377 = new BitSet(new long[]{0x0000024800000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_remove2382 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_remove2388 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_remove2402 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OUTPUT_ACTION_in_output2436 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_output2440 = new BitSet(new long[]{0x0000000000000008L});

}