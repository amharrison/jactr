// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g 2013-05-01 08:51:33

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


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JACTRGenerator extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "MODEL", "LIBRARY", "MODULES", "MODULE", "EXTENSIONS", "EXTENSION", "DECLARATIVE_MEMORY", "CHUNK_TYPE", "CHUNKS", "CHUNK", "PROCEDURAL_MEMORY", "PRODUCTION", "PARAMETERS", "PARAMETER", "BUFFERS", "BUFFER", "CONDITIONS", "MATCH_CONDITION", "QUERY_CONDITION", "SCRIPTABLE_CONDITION", "PROXY_CONDITION", "ACTIONS", "ADD_ACTION", "SET_ACTION", "REMOVE_ACTION", "MODIFY_ACTION", "OUTPUT_ACTION", "SCRIPTABLE_ACTION", "PROXY_ACTION", "LANG", "SCRIPT", "VARIABLE", "STRING", "NUMBER", "IDENTIFIER", "CHUNK_IDENTIFIER", "CHUNK_TYPE_IDENTIFIER", "SLOTS", "SLOT", "LT", "GT", "EQUALS", "NOT", "WITHIN", "GTE", "LTE", "OR", "AND", "LOGIC", "CLASS_SPEC", "NAME", "PARENT", "PARENTS", "UNKNOWN"
    };
    public static final int LT=43;
    public static final int LOGIC=52;
    public static final int PARAMETERS=16;
    public static final int SCRIPTABLE_ACTION=31;
    public static final int CHUNK=13;
    public static final int GTE=48;
    public static final int PROXY_CONDITION=24;
    public static final int LIBRARY=5;
    public static final int EQUALS=45;
    public static final int CHUNK_TYPE=11;
    public static final int NOT=46;
    public static final int AND=51;
    public static final int EOF=-1;
    public static final int LTE=49;
    public static final int ADD_ACTION=26;
    public static final int ACTIONS=25;
    public static final int PARENT=55;
    public static final int NAME=54;
    public static final int EXTENSIONS=8;
    public static final int UNKNOWN=57;
    public static final int PROCEDURAL_MEMORY=14;
    public static final int IDENTIFIER=38;
    public static final int PARAMETER=17;
    public static final int PRODUCTION=15;
    public static final int DECLARATIVE_MEMORY=10;
    public static final int MODEL=4;
    public static final int MODULES=6;
    public static final int REMOVE_ACTION=28;
    public static final int MATCH_CONDITION=21;
    public static final int SCRIPT=34;
    public static final int PROXY_ACTION=32;
    public static final int CHUNK_IDENTIFIER=39;
    public static final int NUMBER=37;
    public static final int BUFFER=19;
    public static final int MODULE=7;
    public static final int CONDITIONS=20;
    public static final int SCRIPTABLE_CONDITION=23;
    public static final int CHUNK_TYPE_IDENTIFIER=40;
    public static final int PARENTS=56;
    public static final int MODIFY_ACTION=29;
    public static final int VARIABLE=35;
    public static final int CLASS_SPEC=53;
    public static final int SLOT=42;
    public static final int BUFFERS=18;
    public static final int OR=50;
    public static final int CHUNKS=12;
    public static final int WITHIN=47;
    public static final int SLOTS=41;
    public static final int GT=44;
    public static final int SET_ACTION=27;
    public static final int QUERY_CONDITION=22;
    public static final int EXTENSION=9;
    public static final int OUTPUT_ACTION=30;
    public static final int STRING=36;
    public static final int LANG=33;

    // delegates
    // delegators


        public JACTRGenerator(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public JACTRGenerator(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return JACTRGenerator.tokenNames; }
    public String getGrammarFileName() { return "/Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g"; }


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



    // $ANTLR start "model"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:185:1: model returns [Collection<StringBuilder> collection] : ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters ) ;
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
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:189:2: ( ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:189:4: ^(m= MODEL n= NAME mods= modules exts= extensions bufs= buffers ^( LIBRARY decs= declarative_memory procs= procedural_memory ) params= parameters )
            {
            m=(CommonTree)match(input,MODEL,FOLLOW_MODEL_in_model328); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_model343); 
            pushFollow(FOLLOW_modules_in_model360);
            mods=modules();

            state._fsp--;

            pushFollow(FOLLOW_extensions_in_model375);
            exts=extensions();

            state._fsp--;

            pushFollow(FOLLOW_buffers_in_model390);
            bufs=buffers();

            state._fsp--;

            match(input,LIBRARY,FOLLOW_LIBRARY_in_model404); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_declarative_memory_in_model421);
            decs=declarative_memory();

            state._fsp--;

            pushFollow(FOLLOW_procedural_memory_in_model437);
            procs=procedural_memory();

            state._fsp--;


            match(input, Token.UP, null); 
            pushFollow(FOLLOW_parameters_in_model454);
            params=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             	 collection.add(new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
             	 collection.add(new StringBuilder("<actr>"));
             	 
             	 StringBuilder tmp = new StringBuilder("<model name=\"");
             	 tmp.append((n!=null?n.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:235:1: parameters returns [List<StringBuilder> collection] : ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS ) ;
    public final List<StringBuilder> parameters() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> param = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:1: ( ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:3: ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:3: ( ^( PARAMETERS (param= parameter )+ ) | PARAMETERS )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:5: ^( PARAMETERS (param= parameter )+ )
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters491); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:18: (param= parameter )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:239:19: param= parameter
                    	    {
                    	    pushFollow(FOLLOW_parameter_in_parameters496);
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:240:7: PARAMETERS
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters510); 

                    }
                    break;

            }


            	 if(collection.size()!=0)
            	  {
            	  	//insert the header and footer
            	  	collection.add(0, new StringBuilder("<parameters>"));
            	  	collection.add(new StringBuilder("</parameters>"));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:250:1: parameter returns [Collection<StringBuilder> collection] : ^(p= PARAMETER n= NAME s= STRING ) ;
    public final Collection<StringBuilder> parameter() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree n=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:254:1: ( ^(p= PARAMETER n= NAME s= STRING ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:254:3: ^(p= PARAMETER n= NAME s= STRING )
            {
            p=(CommonTree)match(input,PARAMETER,FOLLOW_PARAMETER_in_parameter534); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_parameter538); 
            s=(CommonTree)match(input,STRING,FOLLOW_STRING_in_parameter542); 

            match(input, Token.UP, null); 

            	 StringBuilder sb = new StringBuilder("<parameter name=\"");
            	 sb.append((n!=null?n.getText():null));
            	 sb.append("\" value=\"");
            	 sb.append((s!=null?s.getText():null));
            	 sb.append("\"/>");
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:266:1: modules returns [List<StringBuilder> collection] : ( ^( MODULES (mod= module )+ ) | MODULES ) ;
    public final List<StringBuilder> modules() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> mod = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:270:1: ( ( ^( MODULES (mod= module )+ ) | MODULES ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:270:3: ( ^( MODULES (mod= module )+ ) | MODULES )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:270:3: ( ^( MODULES (mod= module )+ ) | MODULES )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:270:4: ^( MODULES (mod= module )+ )
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules566); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:271:5: (mod= module )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:271:6: mod= module
                    	    {
                    	    pushFollow(FOLLOW_module_in_modules576);
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:272:11: MODULES
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules590); 

                    }
                    break;

            }


            	if(collection.size()!=0)
            	 {
            	 	 //insert header and footer
            	 	 collection.add(0, new StringBuilder("<modules>"));
            	 	 collection.add(new StringBuilder("</modules>"));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:282:1: module returns [Collection<StringBuilder> collection] : ^(mod= MODULE c= CLASS_SPEC (params= parameters )? ) ;
    public final Collection<StringBuilder> module() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree mod=null;
        CommonTree c=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:286:1: ( ^(mod= MODULE c= CLASS_SPEC (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:286:3: ^(mod= MODULE c= CLASS_SPEC (params= parameters )? )
            {
            mod=(CommonTree)match(input,MODULE,FOLLOW_MODULE_in_module613); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_module617); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:286:29: (params= parameters )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==PARAMETERS) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:286:30: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_module622);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 
            	 StringBuilder tmp = new StringBuilder("<module class=\"");
            	 tmp.append((c!=null?c.getText():null)).append("\" import=\"");
            	 tmp.append(_trimmers.size()!=0).append("\"");
            	  
            	 
            	 if(params==null || params.size()==0)
            	  tmp.append("/>");
            	 else 
            	  tmp.append(">");
            	  
            	 collection.add(tmp);
            	 collection.addAll(indent(params,2));
            	 
            	if(params!=null && params.size()!=0) 
            	 collection.add(new StringBuilder("</module>"));


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:307:1: extensions returns [List<StringBuilder> collection] : ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS ) ;
    public final List<StringBuilder> extensions() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> ext = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:311:1: ( ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:311:3: ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:311:3: ( ^( EXTENSIONS (ext= extension )+ ) | EXTENSIONS )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:311:4: ^( EXTENSIONS (ext= extension )+ )
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions650); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:312:5: (ext= extension )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:312:6: ext= extension
                    	    {
                    	    pushFollow(FOLLOW_extension_in_extensions660);
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:313:11: EXTENSIONS
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions674); 

                    }
                    break;

            }


            	if(collection.size()!=0)
            	 {
            	 	 //insert header and footer
            	 	 collection.add(0, new StringBuilder("<extensions>"));
            	 	 collection.add(new StringBuilder("</extensions>"));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:323:1: extension returns [Collection<StringBuilder> collection] : ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? ) ;
    public final Collection<StringBuilder> extension() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ext=null;
        CommonTree c=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:327:1: ( ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:327:3: ^(ext= EXTENSION c= CLASS_SPEC (params= parameters )? )
            {
            ext=(CommonTree)match(input,EXTENSION,FOLLOW_EXTENSION_in_extension697); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_extension701); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:327:32: (params= parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==PARAMETERS) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:327:33: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_extension706);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<extension ");
            	 tmp.append("class=\"");
            	 tmp.append((c!=null?c.getText():null)).append("\"");
            	 collection.add(tmp);
            	 
            	 if(params==null || params.size()==0)
            	   tmp.append("/>");  
            	 else
            	  {
             	   tmp.append(">");
            	   collection.addAll(indent(params,2));
            	   collection.add(new StringBuilder("</extension>"));
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
    // $ANTLR end "extension"


    // $ANTLR start "buffers"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:344:1: buffers returns [List<StringBuilder> collection] : ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS ) ;
    public final List<StringBuilder> buffers() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> buf = null;



        	collection = new ArrayList<StringBuilder>();
        	newLines(collection, 2);
        	collection.addAll(comment("Buffer definitions"));

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:350:1: ( ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:350:3: ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:350:3: ( ^( BUFFERS (buf= buffer )+ ) | BUFFERS )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:350:4: ^( BUFFERS (buf= buffer )+ )
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers729); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:351:7: (buf= buffer )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:351:8: buf= buffer
                    	    {
                    	    pushFollow(FOLLOW_buffer_in_buffers741);
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:352:13: BUFFERS
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers757); 

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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:356:1: buffer returns [Collection<StringBuilder> collection] : ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? ) ;
    public final Collection<StringBuilder> buffer() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree buff=null;
        CommonTree n=null;
        CommonTree i=null;
        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();
        	Collection<String> identifiers=new ArrayList<String>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:361:1: ( ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:361:3: ^(buff= BUFFER n= NAME ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS ) (params= parameters )? )
            {
            buff=(CommonTree)match(input,BUFFER,FOLLOW_BUFFER_in_buffer779); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_buffer783); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:362:14: ( ^( CHUNKS (i= CHUNK_IDENTIFIER )+ ) | CHUNKS )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:362:15: ^( CHUNKS (i= CHUNK_IDENTIFIER )+ )
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_buffer802); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:363:16: (i= CHUNK_IDENTIFIER )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:363:17: i= CHUNK_IDENTIFIER
                    	    {
                    	    i=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_buffer823); 
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:364:21: CHUNKS
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_buffer847); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:365:14: (params= parameters )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==PARAMETERS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:365:15: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_buffer867);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<buffer name=\"");
            	 tmp.append((n!=null?n.getText():null)).append("\" ");;
            	 
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:388:1: declarative_memory returns [List<StringBuilder> collection] : ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY ) ;
    public final List<StringBuilder> declarative_memory() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> ct = null;



        	collection = new ArrayList<StringBuilder>();
        	
        	collection.addAll(comment("declarative memory container for chunks and chunk-types"));

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:394:1: ( ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:394:3: ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:394:3: ( ^( DECLARATIVE_MEMORY (ct= chunktype )+ ) | DECLARATIVE_MEMORY )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:394:4: ^( DECLARATIVE_MEMORY (ct= chunktype )+ )
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory895); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:395:7: (ct= chunktype )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:395:8: ct= chunktype
                    	    {
                    	    pushFollow(FOLLOW_chunktype_in_declarative_memory907);
                    	    ct=chunktype();

                    	    state._fsp--;


                    	          	               collection.addAll(indent(ct,2));
                    	                        

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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:398:10: DECLARATIVE_MEMORY
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory920); 

                    }
                    break;

            }


             //header and footer
              collection.add(0,new StringBuilder("<declarative-memory>"));
              collection.add(new StringBuilder("</declarative-memory>"));


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:405:1: procedural_memory returns [List<StringBuilder> collection] : ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY ) ;
    public final List<StringBuilder> procedural_memory() throws RecognitionException {
        List<StringBuilder> collection = null;

        Collection<StringBuilder> prod = null;



        	collection = new ArrayList<StringBuilder>();
        	
        	collection.addAll(comment("procedural memory container for productions"));

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:411:1: ( ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:411:3: ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:411:3: ( ^( PROCEDURAL_MEMORY (prod= production )+ ) | PROCEDURAL_MEMORY )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:411:4: ^( PROCEDURAL_MEMORY (prod= production )+ )
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory942); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:412:7: (prod= production )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:412:8: prod= production
                    	    {
                    	    pushFollow(FOLLOW_production_in_procedural_memory954);
                    	    prod=production();

                    	    state._fsp--;


                    	          	                  collection.addAll(indent(prod,2));
                    	          	                  newLines(collection, 2);
                    	          	                

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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:416:13: PROCEDURAL_MEMORY
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory970); 

                    }
                    break;

            }


            	//header and footer
            	collection.add(0,new StringBuilder("<procedural-memory>"));
            	collection.add(new StringBuilder("</procedural-memory>"));


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:423:1: chunktype returns [Collection<StringBuilder> collection] : ^(ct= CHUNK_TYPE n= NAME (p= parents )? (sl= slots )? (ch= chunks )? (params= parameters )? ) ;
    public final Collection<StringBuilder> chunktype() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ct=null;
        CommonTree n=null;
        String p = null;

        Collection<StringBuilder> sl = null;

        Collection<StringBuilder> ch = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:427:1: ( ^(ct= CHUNK_TYPE n= NAME (p= parents )? (sl= slots )? (ch= chunks )? (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:427:3: ^(ct= CHUNK_TYPE n= NAME (p= parents )? (sl= slots )? (ch= chunks )? (params= parameters )? )
            {
            ct=(CommonTree)match(input,CHUNK_TYPE,FOLLOW_CHUNK_TYPE_in_chunktype993); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunktype1012); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:429:15: (p= parents )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==PARENTS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:429:16: p= parents
                    {
                    pushFollow(FOLLOW_parents_in_chunktype1031);
                    p=parents();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:430:15: (sl= slots )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==SLOTS) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:430:16: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_chunktype1053);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:431:15: (ch= chunks )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==CHUNKS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:431:16: ch= chunks
                    {
                    pushFollow(FOLLOW_chunks_in_chunktype1075);
                    ch=chunks();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:432:15: (params= parameters )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==PARAMETERS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:432:16: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_chunktype1097);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

              if(shouldIgnore(ct))
               {
                //flag with a comment?
                LOGGER.debug("Ignoring chunktype "+(n!=null?n.getText():null));
               }
               else
               {
               //and add some space after the chunks before the chunktype
                  	newLines(collection, 2);
                  	               
            	StringBuilder tmp = new StringBuilder("<chunk-type name=\"");
            	tmp.append((n!=null?n.getText():null));
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
                      comment(collection, "all chunks for chunk-type "+(n!=null?n.getText():null));
                      collection.addAll(indent(ch,2));
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


    // $ANTLR start "parents"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:483:1: parents returns [String parentStr] : ( ^( PARENTS (p= PARENT )+ ) | PARENTS ) ;
    public final String parents() throws RecognitionException {
        String parentStr = null;

        CommonTree p=null;


        	parentStr = "";

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:487:1: ( ( ^( PARENTS (p= PARENT )+ ) | PARENTS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:487:2: ( ^( PARENTS (p= PARENT )+ ) | PARENTS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:487:2: ( ^( PARENTS (p= PARENT )+ ) | PARENTS )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==PARENTS) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==DOWN) ) {
                    alt23=1;
                }
                else if ( (LA23_1==UP||LA23_1==CHUNKS||LA23_1==PARAMETERS||LA23_1==SLOTS) ) {
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:487:3: ^( PARENTS (p= PARENT )+ )
                    {
                    match(input,PARENTS,FOLLOW_PARENTS_in_parents1120); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:488:6: (p= PARENT )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==PARENT) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:488:7: p= PARENT
                    	    {
                    	    p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_parents1130); 
                    	    if(parentStr.length() > 0) parentStr += ",";
                    	         		parentStr+=(p!=null?p.getText():null);

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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:490:12: PARENTS
                    {
                    match(input,PARENTS,FOLLOW_PARENTS_in_parents1145); 

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
        return parentStr;
    }
    // $ANTLR end "parents"


    // $ANTLR start "slots"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:494:1: slots returns [Collection<StringBuilder> collection] : ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS ) ;
    public final Collection<StringBuilder> slots() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        Collection<StringBuilder> s = null;

        Collection<StringBuilder> l = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:498:1: ( ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:498:2: ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:498:2: ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SLOTS) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==DOWN) ) {
                    alt25=1;
                }
                else if ( (LA25_1==UP||LA25_1==CHUNKS||LA25_1==PARAMETERS) ) {
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:498:3: ^( SLOTS (s= slot | l= logic )+ )
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots1166); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:499:6: (s= slot | l= logic )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=3;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==SLOT) ) {
                            alt24=1;
                        }
                        else if ( (LA24_0==LOGIC) ) {
                            alt24=2;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:499:7: s= slot
                    	    {
                    	    pushFollow(FOLLOW_slot_in_slots1177);
                    	    s=slot();

                    	    state._fsp--;

                    	    collection.addAll(s);

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:500:8: l= logic
                    	    {
                    	    pushFollow(FOLLOW_logic_in_slots1190);
                    	    l=logic();

                    	    state._fsp--;

                    	    collection.addAll(l);

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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:501:10: SLOTS
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots1203); 

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


    // $ANTLR start "chunks"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:505:1: chunks returns [Collection<StringBuilder> collection] : ( ^( CHUNKS (c= chunk )+ ) | CHUNKS ) ;
    public final Collection<StringBuilder> chunks() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        Collection<StringBuilder> c = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:509:1: ( ( ^( CHUNKS (c= chunk )+ ) | CHUNKS ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:509:3: ( ^( CHUNKS (c= chunk )+ ) | CHUNKS )
            {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:509:3: ( ^( CHUNKS (c= chunk )+ ) | CHUNKS )
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:509:4: ^( CHUNKS (c= chunk )+ )
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks1225); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:510:6: (c= chunk )+
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
                    	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:510:7: c= chunk
                    	    {
                    	    pushFollow(FOLLOW_chunk_in_chunks1236);
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
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:513:12: CHUNKS
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks1251); 

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


    // $ANTLR start "logic"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:517:1: logic returns [Collection<StringBuilder> collection] : ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? ) ;
    public final Collection<StringBuilder> logic() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree l=null;
        CommonTree v=null;
        Collection<StringBuilder> s1 = null;

        Collection<StringBuilder> s2 = null;



                collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:1: ( ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:3: ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? )
            {
            l=(CommonTree)match(input,LOGIC,FOLLOW_LOGIC_in_logic1273); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:13: (v= AND | v= OR | v= NOT )
            int alt28=3;
            switch ( input.LA(1) ) {
            case AND:
                {
                alt28=1;
                }
                break;
            case OR:
                {
                alt28=2;
                }
                break;
            case NOT:
                {
                alt28=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:14: v= AND
                    {
                    v=(CommonTree)match(input,AND,FOLLOW_AND_in_logic1278); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:20: v= OR
                    {
                    v=(CommonTree)match(input,OR,FOLLOW_OR_in_logic1282); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:25: v= NOT
                    {
                    v=(CommonTree)match(input,NOT,FOLLOW_NOT_in_logic1286); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:32: (s1= logic | s1= slot )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LOGIC) ) {
                alt29=1;
            }
            else if ( (LA29_0==SLOT) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:33: s1= logic
                    {
                    pushFollow(FOLLOW_logic_in_logic1292);
                    s1=logic();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:42: s1= slot
                    {
                    pushFollow(FOLLOW_slot_in_logic1296);
                    s1=slot();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:51: (s2= logic | s2= slot )?
            int alt30=3;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==LOGIC) ) {
                alt30=1;
            }
            else if ( (LA30_0==SLOT) ) {
                alt30=2;
            }
            switch (alt30) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:52: s2= logic
                    {
                    pushFollow(FOLLOW_logic_in_logic1302);
                    s2=logic();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:521:61: s2= slot
                    {
                    pushFollow(FOLLOW_slot_in_logic1306);
                    s2=slot();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	collection.add(new StringBuilder("<" + (v!=null?v.getText():null) + ">"));
            	collection.addAll(s1);
            	if(s2 != null) collection.addAll(s2);
            	collection.add(new StringBuilder("</" + (v!=null?v.getText():null) + ">"));


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
    // $ANTLR end "logic"


    // $ANTLR start "slot"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:529:1: slot returns [Collection<StringBuilder> collection] : ^(sl= SLOT (n= NAME | n= VARIABLE ) ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )? ) ;
    public final Collection<StringBuilder> slot() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree sl=null;
        CommonTree n=null;
        CommonTree v=null;


        	collection = new ArrayList<StringBuilder>();
        	String cond = "equals";

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:534:1: ( ^(sl= SLOT (n= NAME | n= VARIABLE ) ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:534:3: ^(sl= SLOT (n= NAME | n= VARIABLE ) ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )? )
            {
            sl=(CommonTree)match(input,SLOT,FOLLOW_SLOT_in_slot1331); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:534:13: (n= NAME | n= VARIABLE )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==NAME) ) {
                alt31=1;
            }
            else if ( (LA31_0==VARIABLE) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:534:14: n= NAME
                    {
                    n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_slot1336); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:534:23: n= VARIABLE
                    {
                    n=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_slot1342); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:535:10: ( ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( ((LA34_0>=LT && LA34_0<=LTE)) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:535:11: ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE ) (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER )
                    {
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:535:11: ( EQUALS | LT | GT | NOT | WITHIN | LTE | GTE )
                    int alt32=7;
                    switch ( input.LA(1) ) {
                    case EQUALS:
                        {
                        alt32=1;
                        }
                        break;
                    case LT:
                        {
                        alt32=2;
                        }
                        break;
                    case GT:
                        {
                        alt32=3;
                        }
                        break;
                    case NOT:
                        {
                        alt32=4;
                        }
                        break;
                    case WITHIN:
                        {
                        alt32=5;
                        }
                        break;
                    case LTE:
                        {
                        alt32=6;
                        }
                        break;
                    case GTE:
                        {
                        alt32=7;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 0, input);

                        throw nvae;
                    }

                    switch (alt32) {
                        case 1 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:535:12: EQUALS
                            {
                            match(input,EQUALS,FOLLOW_EQUALS_in_slot1356); 
                            cond="equals"; 

                            }
                            break;
                        case 2 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:536:13: LT
                            {
                            match(input,LT,FOLLOW_LT_in_slot1372); 
                            cond="less-than";

                            }
                            break;
                        case 3 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:537:13: GT
                            {
                            match(input,GT,FOLLOW_GT_in_slot1391); 
                            cond="greater-than";

                            }
                            break;
                        case 4 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:538:13: NOT
                            {
                            match(input,NOT,FOLLOW_NOT_in_slot1410); 
                            cond="not";

                            }
                            break;
                        case 5 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:539:13: WITHIN
                            {
                            match(input,WITHIN,FOLLOW_WITHIN_in_slot1428); 
                            cond="within";

                            }
                            break;
                        case 6 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:540:13: LTE
                            {
                            match(input,LTE,FOLLOW_LTE_in_slot1444); 
                            cond="less-than-equals";

                            }
                            break;
                        case 7 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:541:13: GTE
                            {
                            match(input,GTE,FOLLOW_GTE_in_slot1463); 
                            cond="greater-than-equals";

                            }
                            break;

                    }

                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:543:11: (v= VARIABLE | v= STRING | v= IDENTIFIER | v= NUMBER )
                    int alt33=4;
                    switch ( input.LA(1) ) {
                    case VARIABLE:
                        {
                        alt33=1;
                        }
                        break;
                    case STRING:
                        {
                        alt33=2;
                        }
                        break;
                    case IDENTIFIER:
                        {
                        alt33=3;
                        }
                        break;
                    case NUMBER:
                        {
                        alt33=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 33, 0, input);

                        throw nvae;
                    }

                    switch (alt33) {
                        case 1 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:543:12: v= VARIABLE
                            {
                            v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_slot1495); 

                            }
                            break;
                        case 2 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:544:12: v= STRING
                            {
                            v=(CommonTree)match(input,STRING,FOLLOW_STRING_in_slot1511); 

                            }
                            break;
                        case 3 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:545:12: v= IDENTIFIER
                            {
                            v=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_slot1528); 

                            }
                            break;
                        case 4 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:546:12: v= NUMBER
                            {
                            v=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_slot1544); 

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<slot name=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 boolean isReserved = "null".equalsIgnoreCase((v!=null?v.getText():null)) ||
            	                      "true".equalsIgnoreCase((v!=null?v.getText():null)) ||
            	                      "false".equalsIgnoreCase((v!=null?v.getText():null));
            	 tmp.append("\" ");
            	 if(cond!=null && v!=null)
            	  {
            	   tmp.append(cond);
            	   tmp.append("=\"");
            	    if((v!=null?v.getType():0)==STRING && !isReserved)
            	     tmp.append("'");
            	   tmp.append((v!=null?v.getText():null));
            	    if((v!=null?v.getType():0)==STRING && !isReserved)
            	     tmp.append("'");
            	   tmp.append("\"");
            	  }
            	 tmp.append("/>");
            	 
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:574:1: chunk returns [Collection<StringBuilder> collection] : ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? ) ;
    public final Collection<StringBuilder> chunk() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree ch=null;
        CommonTree n=null;
        CommonTree p=null;
        Collection<StringBuilder> sl = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:578:1: ( ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:578:3: ^(ch= CHUNK n= NAME p= PARENT (sl= slots )? (params= parameters )? )
            {
            ch=(CommonTree)match(input,CHUNK,FOLLOW_CHUNK_in_chunk1592); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunk1603); 
            p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_chunk1613); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:581:7: (sl= slots )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==SLOTS) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:581:8: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_chunk1625);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:582:7: (params= parameters )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==PARAMETERS) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:582:8: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_chunk1639);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

                 if(shouldIgnore(ch))
                 {
                   // should we log this with a comment?
                   LOGGER.debug("Ignoring chunk "+(n!=null?n.getText():null));
                 }
                 else
                 {
            	 StringBuilder tmp = new StringBuilder("<chunk name=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append("\" type=\"");
            	 tmp.append((p!=null?p.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:618:1: production returns [Collection<StringBuilder> collection] : ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? ) ;
    public final Collection<StringBuilder> production() throws RecognitionException {
        Collection<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree n=null;
        List<StringBuilder> conds = null;

        List<StringBuilder> acts = null;

        List<StringBuilder> params = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:622:1: ( ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:622:3: ^(p= PRODUCTION n= NAME conds= conditions acts= actions (params= parameters )? )
            {
            p=(CommonTree)match(input,PRODUCTION,FOLLOW_PRODUCTION_in_production1669); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_production1673); 
            pushFollow(FOLLOW_conditions_in_production1677);
            conds=conditions();

            state._fsp--;

            pushFollow(FOLLOW_actions_in_production1681);
            acts=actions();

            state._fsp--;

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:622:55: (params= parameters )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==PARAMETERS) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:622:56: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_production1686);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

              if(shouldIgnore(p))
               {
                //should we note this with a comment?
                LOGGER.debug("Ignoring production "+(n!=null?n.getText():null));
               }
               else
               {
            	 StringBuilder tmp = new StringBuilder("<production name=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append("\">");
            	 
            	 collection.add(tmp);
            	 
            	 collection.addAll(indent(conds,2));
            	 collection.addAll(indent(acts,2));
            	 
            	 if(params!=null)
            	  collection.addAll(indent(params,2));
            	 
            	 collection.add(new StringBuilder("</production>")); 
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:647:1: conditions returns [List<StringBuilder> collection] : ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ ) ;
    public final List<StringBuilder> conditions() throws RecognitionException {
        List<StringBuilder> collection = null;

        List<StringBuilder> tmp = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:651:1: ( ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:651:3: ^( CONDITIONS (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+ )
            {
            match(input,CONDITIONS,FOLLOW_CONDITIONS_in_conditions1711); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:651:16: (tmp= pattern | tmp= queryCondition | tmp= scriptableCondition | tmp= proxyCondition )+
            int cnt38=0;
            loop38:
            do {
                int alt38=5;
                switch ( input.LA(1) ) {
                case MATCH_CONDITION:
                    {
                    alt38=1;
                    }
                    break;
                case QUERY_CONDITION:
                    {
                    alt38=2;
                    }
                    break;
                case SCRIPTABLE_CONDITION:
                    {
                    alt38=3;
                    }
                    break;
                case PROXY_CONDITION:
                    {
                    alt38=4;
                    }
                    break;

                }

                switch (alt38) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:651:17: tmp= pattern
            	    {
            	    pushFollow(FOLLOW_pattern_in_conditions1716);
            	    tmp=pattern();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:652:17: tmp= queryCondition
            	    {
            	    pushFollow(FOLLOW_queryCondition_in_conditions1738);
            	    tmp=queryCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:653:17: tmp= scriptableCondition
            	    {
            	    pushFollow(FOLLOW_scriptableCondition_in_conditions1760);
            	    tmp=scriptableCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:654:17: tmp= proxyCondition
            	    {
            	    pushFollow(FOLLOW_proxyCondition_in_conditions1782);
            	    tmp=proxyCondition();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,1));

            	    }
            	    break;

            	default :
            	    if ( cnt38 >= 1 ) break loop38;
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
            } while (true);


            match(input, Token.UP, null); 

            	collection.add(0,new StringBuilder("<conditions>"));
            	collection.add(new StringBuilder("</conditions>"));


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:661:1: pattern returns [List<StringBuilder> collection] : ^(p= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? ) ;
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
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:1: ( ^(p= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:3: ^(p= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? )
            {
            p=(CommonTree)match(input,MATCH_CONDITION,FOLLOW_MATCH_CONDITION_in_pattern1824); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_pattern1828); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:30: ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==VARIABLE||(LA41_0>=CHUNK_IDENTIFIER && LA41_0<=CHUNK_TYPE_IDENTIFIER)) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:31: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )?
                    {
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:31: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
                    int alt39=3;
                    switch ( input.LA(1) ) {
                    case CHUNK_IDENTIFIER:
                        {
                        alt39=1;
                        }
                        break;
                    case CHUNK_TYPE_IDENTIFIER:
                        {
                        alt39=2;
                        }
                        break;
                    case VARIABLE:
                        {
                        alt39=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 39, 0, input);

                        throw nvae;
                    }

                    switch (alt39) {
                        case 1 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:32: c= CHUNK_IDENTIFIER
                            {
                            c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_pattern1834); 

                            }
                            break;
                        case 2 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:53: ct= CHUNK_TYPE_IDENTIFIER
                            {
                            ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_pattern1840); 

                            }
                            break;
                        case 3 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:665:80: v= VARIABLE
                            {
                            v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_pattern1846); 

                            }
                            break;

                    }

                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:666:13: (sl= slots )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==SLOTS) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:666:14: sl= slots
                            {
                            pushFollow(FOLLOW_slots_in_pattern1865);
                            sl=slots();

                            state._fsp--;

                            collection.addAll(indent(sl,2));

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<match buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append("\" ");
            	 
            	 boolean empty = v==null && c==null && ct==null;
            	 if(!empty)
            	 {
            	   if(v!=null || c!=null)
            	    { 
            	  	//referring to a specific chunk
            	  	tmp.append("chunk=\"");
            	  	if(v!=null)
            	  	  tmp.append((v!=null?v.getText():null));
            	  	else
            	  	  tmp.append((c!=null?c.getText():null));  
            	  	tmp.append("\"");
            	    }
            	   else
            	   {
            	 	 tmp.append("type=\"");
            	  	 tmp.append((ct!=null?ct.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:704:1: queryCondition returns [List<StringBuilder> collection] : ^(q= QUERY_CONDITION n= NAME (sl= slots )? ) ;
    public final List<StringBuilder> queryCondition() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree q=null;
        CommonTree n=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:708:1: ( ^(q= QUERY_CONDITION n= NAME (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:708:3: ^(q= QUERY_CONDITION n= NAME (sl= slots )? )
            {
            q=(CommonTree)match(input,QUERY_CONDITION,FOLLOW_QUERY_CONDITION_in_queryCondition1907); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_queryCondition1911); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:709:13: (sl= slots )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==SLOTS) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:709:14: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_queryCondition1928);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<query buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:728:1: scriptableCondition returns [List<StringBuilder> collection] : ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) ;
    public final List<StringBuilder> scriptableCondition() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree sc=null;
        CommonTree l=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:732:1: ( ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:732:3: ^(sc= SCRIPTABLE_CONDITION l= LANG s= SCRIPT )
            {
            sc=(CommonTree)match(input,SCRIPTABLE_CONDITION,FOLLOW_SCRIPTABLE_CONDITION_in_scriptableCondition1971); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptableCondition1975); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptableCondition1979); 

            match(input, Token.UP, null); 

                     StringBuilder tmp = new StringBuilder("<scriptable-condition lang=\"");
                     tmp.append((l!=null?l.getText():null)).append("\">");
            	 collection.add(tmp);
            	 collection.add(new StringBuilder("<![CDATA["));
            	 String code = (s!=null?s.getText():null);
            	 String[] lines = code.split("\n");
            	 for(int i=0;i<lines.length;i++)
            	  collection.add(indent(new StringBuilder(lines[i]),2));
            	 collection.add(new StringBuilder("]]>")); 
            	 collection.add(new StringBuilder("</scriptable-condition>")); 


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:746:1: proxyCondition returns [List<StringBuilder> collection] : ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) ;
    public final List<StringBuilder> proxyCondition() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree c=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:750:1: ( ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:750:3: ^(p= PROXY_CONDITION c= CLASS_SPEC (sl= slots )? )
            {
            p=(CommonTree)match(input,PROXY_CONDITION,FOLLOW_PROXY_CONDITION_in_proxyCondition2002); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyCondition2006); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:750:36: (sl= slots )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==SLOTS) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:750:37: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyCondition2011);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<proxy-condition class=\"");
            	 tmp.append((c!=null?c.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:766:1: actions returns [List<StringBuilder> collection] : ^( ACTIONS (tmp= add | tmp= set | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ ) ;
    public final List<StringBuilder> actions() throws RecognitionException {
        List<StringBuilder> collection = null;

        List<StringBuilder> tmp = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:770:1: ( ^( ACTIONS (tmp= add | tmp= set | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:770:3: ^( ACTIONS (tmp= add | tmp= set | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+ )
            {
            match(input,ACTIONS,FOLLOW_ACTIONS_in_actions2037); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:770:13: (tmp= add | tmp= set | tmp= modify | tmp= remove | tmp= output | tmp= scriptableAction | tmp= proxyAction )+
            int cnt44=0;
            loop44:
            do {
                int alt44=8;
                switch ( input.LA(1) ) {
                case ADD_ACTION:
                    {
                    alt44=1;
                    }
                    break;
                case SET_ACTION:
                    {
                    alt44=2;
                    }
                    break;
                case MODIFY_ACTION:
                    {
                    alt44=3;
                    }
                    break;
                case REMOVE_ACTION:
                    {
                    alt44=4;
                    }
                    break;
                case OUTPUT_ACTION:
                    {
                    alt44=5;
                    }
                    break;
                case SCRIPTABLE_ACTION:
                    {
                    alt44=6;
                    }
                    break;
                case PROXY_ACTION:
                    {
                    alt44=7;
                    }
                    break;

                }

                switch (alt44) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:770:14: tmp= add
            	    {
            	    pushFollow(FOLLOW_add_in_actions2042);
            	    tmp=add();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:771:11: tmp= set
            	    {
            	    pushFollow(FOLLOW_set_in_actions2058);
            	    tmp=set();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:772:11: tmp= modify
            	    {
            	    pushFollow(FOLLOW_modify_in_actions2074);
            	    tmp=modify();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:773:11: tmp= remove
            	    {
            	    pushFollow(FOLLOW_remove_in_actions2090);
            	    tmp=remove();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 5 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:774:11: tmp= output
            	    {
            	    pushFollow(FOLLOW_output_in_actions2106);
            	    tmp=output();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 6 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:775:11: tmp= scriptableAction
            	    {
            	    pushFollow(FOLLOW_scriptableAction_in_actions2122);
            	    tmp=scriptableAction();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;
            	case 7 :
            	    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:776:11: tmp= proxyAction
            	    {
            	    pushFollow(FOLLOW_proxyAction_in_actions2138);
            	    tmp=proxyAction();

            	    state._fsp--;

            	    collection.addAll(indent(tmp,2));

            	    }
            	    break;

            	default :
            	    if ( cnt44 >= 1 ) break loop44;
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
            } while (true);


            match(input, Token.UP, null); 

            	 collection.add(0, new StringBuilder("<actions>"));
            	 collection.add(new StringBuilder("</actions>"));


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:783:1: add returns [List<StringBuilder> collection] : ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) ;
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
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:1: ( ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:3: ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )? (sl= slots )? )
            {
            a=(CommonTree)match(input,ADD_ACTION,FOLLOW_ADD_ACTION_in_add2174); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_add2178); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:25: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )?
            int alt45=4;
            switch ( input.LA(1) ) {
                case CHUNK_IDENTIFIER:
                    {
                    alt45=1;
                    }
                    break;
                case CHUNK_TYPE_IDENTIFIER:
                    {
                    alt45=2;
                    }
                    break;
                case VARIABLE:
                    {
                    alt45=3;
                    }
                    break;
            }

            switch (alt45) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:26: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_add2183); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:47: ct= CHUNK_TYPE_IDENTIFIER
                    {
                    ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add2189); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:787:74: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_add2195); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:788:15: (sl= slots )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==SLOTS) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:788:16: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_add2217);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<add buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append("\" ");
            	 if(v!=null || c!=null)
            	  {
            	  	tmp.append("chunk=\"");
            	  	if(v!=null)
            	  	  tmp.append((v!=null?v.getText():null));
            	  	else
            	  	  tmp.append((c!=null?c.getText():null));  
            	  	tmp.append("\"");
            	  }
            	 else if(ct!=null)
            	 {
            	 	 //see the comments in pattern regarding this ambiguity
            	 	 tmp.append("type=\"");
            	 	 tmp.append((ct!=null?ct.getText():null));
            	 	 tmp.append("\"");
            	 }
            	 
            	 if(collection.size()==0) 
            	  tmp.append("/>");
            	 else
            	  tmp.append(">");
            	   
            	 collection.add(0,tmp);
            	 if(collection.size()>1)
            	   collection.add(new StringBuilder("</add>"));


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


    // $ANTLR start "set"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:822:1: set returns [List<StringBuilder> collection] : ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) ;
    public final List<StringBuilder> set() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree a=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree v=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:826:1: ( ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:826:3: ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? )
            {
            a=(CommonTree)match(input,SET_ACTION,FOLLOW_SET_ACTION_in_set2260); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_set2264); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:826:25: (c= CHUNK_IDENTIFIER | v= VARIABLE )?
            int alt47=3;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==CHUNK_IDENTIFIER) ) {
                alt47=1;
            }
            else if ( (LA47_0==VARIABLE) ) {
                alt47=2;
            }
            switch (alt47) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:826:26: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_set2269); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:826:47: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_set2275); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:827:15: (sl= slots )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==SLOTS) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:827:16: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_set2297);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<set buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
            	 tmp.append("\" ");
            	 if(v!=null || c!=null)
            	  {
            	  	tmp.append("chunk=\"");
            	  	if(v!=null)
            	  	  tmp.append((v!=null?v.getText():null));
            	  	else
            	  	  tmp.append((c!=null?c.getText():null));  
            	  	tmp.append("\"");
            	  }
            	 
            	 if(collection.size()==0) 
            	  tmp.append("/>");
            	 else
            	  tmp.append(">");
            	   
            	 collection.add(0,tmp);
            	 if(collection.size()>1)
            	   collection.add(new StringBuilder("</set>"));


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
    // $ANTLR end "set"


    // $ANTLR start "scriptableAction"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:853:1: scriptableAction returns [List<StringBuilder> collection] : ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) ;
    public final List<StringBuilder> scriptableAction() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree sc=null;
        CommonTree l=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:857:1: ( ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:857:3: ^(sc= SCRIPTABLE_ACTION l= LANG s= SCRIPT )
            {
            sc=(CommonTree)match(input,SCRIPTABLE_ACTION,FOLLOW_SCRIPTABLE_ACTION_in_scriptableAction2339); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptableAction2343); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptableAction2347); 

            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<scriptable-action lang=\"");
                     tmp.append((l!=null?l.getText():null)).append("\">");
            	 collection.add(tmp);
            	 collection.add(new StringBuilder("<![CDATA["));
            	 String code = (s!=null?s.getText():null);
            	 String[] lines = code.split("\n");
            	 for(int i=0;i<lines.length;i++)
            	  collection.add(indent(new StringBuilder(lines[i]),2));
            	 collection.add(new StringBuilder("]]>")); 
            	 collection.add(new StringBuilder("</scriptable-action>")); 


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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:871:1: proxyAction returns [List<StringBuilder> collection] : ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) ;
    public final List<StringBuilder> proxyAction() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree p=null;
        CommonTree c=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:875:1: ( ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:875:3: ^(p= PROXY_ACTION c= CLASS_SPEC (sl= slots )? )
            {
            p=(CommonTree)match(input,PROXY_ACTION,FOLLOW_PROXY_ACTION_in_proxyAction2372); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyAction2376); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:875:33: (sl= slots )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==SLOTS) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:875:34: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyAction2381);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	if((c!=null?c.getText():null).equals("org.jactr.core.production.action.StopAction"))
                      {
                       //dump the short version
                       collection.add(new StringBuilder("<stop />"));
                      }
                     else
                     { 
                     StringBuilder tmp = new StringBuilder("<proxy-action class=\"");
            	 tmp.append((c!=null?c.getText():null));
            	 if(collection.size()==0)
              	  tmp.append("\" />");
              	 else
              	  {
              	   tmp.append("\">");
              	   collection.add(new StringBuilder("</proxy-action>"));
              	  }
            	 collection.add(0, tmp);
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
    // $ANTLR end "proxyAction"


    // $ANTLR start "modify"
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:897:1: modify returns [List<StringBuilder> collection] : ^(m= MODIFY_ACTION n= NAME (sl= slots )? ) ;
    public final List<StringBuilder> modify() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree m=null;
        CommonTree n=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:901:1: ( ^(m= MODIFY_ACTION n= NAME (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:901:3: ^(m= MODIFY_ACTION n= NAME (sl= slots )? )
            {
            m=(CommonTree)match(input,MODIFY_ACTION,FOLLOW_MODIFY_ACTION_in_modify2411); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_modify2415); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:902:10: (sl= slots )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==SLOTS) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:902:11: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_modify2430);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<modify buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:920:1: remove returns [List<StringBuilder> collection] : ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? ) ;
    public final List<StringBuilder> remove() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree r=null;
        CommonTree n=null;
        CommonTree i=null;
        Collection<StringBuilder> sl = null;



        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:924:1: ( ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:924:3: ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | i= VARIABLE )? (sl= slots )? )
            {
            r=(CommonTree)match(input,REMOVE_ACTION,FOLLOW_REMOVE_ACTION_in_remove2467); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_remove2471); 
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:924:28: (i= IDENTIFIER | i= VARIABLE )?
            int alt51=3;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==IDENTIFIER) ) {
                alt51=1;
            }
            else if ( (LA51_0==VARIABLE) ) {
                alt51=2;
            }
            switch (alt51) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:924:29: i= IDENTIFIER
                    {
                    i=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_remove2476); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:924:44: i= VARIABLE
                    {
                    i=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_remove2482); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:925:8: (sl= slots )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==SLOTS) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:925:9: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_remove2496);
                    sl=slots();

                    state._fsp--;

                    collection.addAll(indent(sl,2));

                    }
                    break;

            }


            match(input, Token.UP, null); 

            		StringBuilder tmp = new StringBuilder("<remove buffer=\"");
            	 tmp.append((n!=null?n.getText():null));
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
    // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:943:1: output returns [List<StringBuilder> collection] : ^(o= OUTPUT_ACTION s= STRING ) ;
    public final List<StringBuilder> output() throws RecognitionException {
        List<StringBuilder> collection = null;

        CommonTree o=null;
        CommonTree s=null;


        	collection = new ArrayList<StringBuilder>();

        try {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:947:1: ( ^(o= OUTPUT_ACTION s= STRING ) )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/generator/xml/JACTRGenerator.g:947:3: ^(o= OUTPUT_ACTION s= STRING )
            {
            o=(CommonTree)match(input,OUTPUT_ACTION,FOLLOW_OUTPUT_ACTION_in_output2530); 

            match(input, Token.DOWN, null); 
            s=(CommonTree)match(input,STRING,FOLLOW_STRING_in_output2534); 

            match(input, Token.UP, null); 

            	 StringBuilder tmp = new StringBuilder("<output>\"");
            	 tmp.append((s!=null?s.getText():null));
            	 tmp.append("\"</output>");
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
    // $ANTLR end "output"

    // Delegated rules


 

    public static final BitSet FOLLOW_MODEL_in_model328 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_model343 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_modules_in_model360 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_extensions_in_model375 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_buffers_in_model390 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_LIBRARY_in_model404 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_declarative_memory_in_model421 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_procedural_memory_in_model437 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_parameters_in_model454 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_parameter_in_parameters496 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAMETER_in_parameter534 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_parameter538 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_in_parameter542 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODULES_in_modules566 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_module_in_modules576 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_MODULES_in_modules590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODULE_in_module613 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_module617 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_module622 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions650 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_extension_in_extensions660 = new BitSet(new long[]{0x0000000000000208L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENSION_in_extension697 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_extension701 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_extension706 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers729 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_buffer_in_buffers741 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BUFFER_in_buffer779 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_buffer783 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_CHUNKS_in_buffer802 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_buffer823 = new BitSet(new long[]{0x0000008000000008L});
    public static final BitSet FOLLOW_CHUNKS_in_buffer847 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_buffer867 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory895 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunktype_in_declarative_memory907 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarative_memory920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory942 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_production_in_procedural_memory954 = new BitSet(new long[]{0x0000000000008008L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_procedural_memory970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHUNK_TYPE_in_chunktype993 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunktype1012 = new BitSet(new long[]{0x0100020000011008L});
    public static final BitSet FOLLOW_parents_in_chunktype1031 = new BitSet(new long[]{0x0000020000011008L});
    public static final BitSet FOLLOW_slots_in_chunktype1053 = new BitSet(new long[]{0x0000000000011008L});
    public static final BitSet FOLLOW_chunks_in_chunktype1075 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_chunktype1097 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARENTS_in_parents1120 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PARENT_in_parents1130 = new BitSet(new long[]{0x0080000000000008L});
    public static final BitSet FOLLOW_PARENTS_in_parents1145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLOTS_in_slots1166 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_slot_in_slots1177 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_slots1190 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_SLOTS_in_slots1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks1225 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunk_in_chunks1236 = new BitSet(new long[]{0x0000000000002008L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks1251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOGIC_in_logic1273 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_AND_in_logic1278 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_OR_in_logic1282 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_NOT_in_logic1286 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_logic1292 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_slot_in_logic1296 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_logic1302 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_slot_in_logic1306 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SLOT_in_slot1331 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_slot1336 = new BitSet(new long[]{0x0003F80000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_slot1342 = new BitSet(new long[]{0x0003F80000000008L});
    public static final BitSet FOLLOW_EQUALS_in_slot1356 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LT_in_slot1372 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GT_in_slot1391 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_NOT_in_slot1410 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_WITHIN_in_slot1428 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LTE_in_slot1444 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GTE_in_slot1463 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_VARIABLE_in_slot1495 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_slot1511 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_slot1528 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMBER_in_slot1544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHUNK_in_chunk1592 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunk1603 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_PARENT_in_chunk1613 = new BitSet(new long[]{0x0000020000010008L});
    public static final BitSet FOLLOW_slots_in_chunk1625 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_chunk1639 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PRODUCTION_in_production1669 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_production1673 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_conditions_in_production1677 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_actions_in_production1681 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_parameters_in_production1686 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONDITIONS_in_conditions1711 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_in_conditions1716 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_queryCondition_in_conditions1738 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_scriptableCondition_in_conditions1760 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_proxyCondition_in_conditions1782 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_MATCH_CONDITION_in_pattern1824 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_pattern1828 = new BitSet(new long[]{0x0000018800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_pattern1834 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_pattern1840 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_pattern1846 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_pattern1865 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUERY_CONDITION_in_queryCondition1907 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_queryCondition1911 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_queryCondition1928 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_CONDITION_in_scriptableCondition1971 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptableCondition1975 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptableCondition1979 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_CONDITION_in_proxyCondition2002 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyCondition2006 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyCondition2011 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTIONS_in_actions2037 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_add_in_actions2042 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_set_in_actions2058 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_modify_in_actions2074 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_remove_in_actions2090 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_output_in_actions2106 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_scriptableAction_in_actions2122 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_proxyAction_in_actions2138 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_ADD_ACTION_in_add2174 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_add2178 = new BitSet(new long[]{0x0000038800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_add2183 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add2189 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_add2195 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_add2217 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SET_ACTION_in_set2260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_set2264 = new BitSet(new long[]{0x0000028800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_set2269 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_set2275 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_set2297 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_ACTION_in_scriptableAction2339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptableAction2343 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptableAction2347 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_ACTION_in_proxyAction2372 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyAction2376 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyAction2381 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFY_ACTION_in_modify2411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_modify2415 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_modify2430 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REMOVE_ACTION_in_remove2467 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_remove2471 = new BitSet(new long[]{0x0000024800000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_remove2476 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_remove2482 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_remove2496 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OUTPUT_ACTION_in_output2530 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_output2534 = new BitSet(new long[]{0x0000000000000008L});

}