// $ANTLR 3.4 /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g 2011-11-15 08:38:55

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


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class LispParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "MODEL", "LIBRARY", "MODULES", "MODULE", "EXTENSIONS", "EXTENSION", "DECLARATIVE_MEMORY", "CHUNK_TYPE", "CHUNKS", "CHUNK", "PROCEDURAL_MEMORY", "PRODUCTION", "PARAMETERS", "PARAMETER", "BUFFERS", "BUFFER", "CONDITIONS", "MATCH_CONDITION", "QUERY_CONDITION", "SCRIPTABLE_CONDITION", "PROXY_CONDITION", "ACTIONS", "ADD_ACTION", "SET_ACTION", "REMOVE_ACTION", "MODIFY_ACTION", "OUTPUT_ACTION", "SCRIPTABLE_ACTION", "PROXY_ACTION", "LANG", "SCRIPT", "VARIABLE", "STRING", "NUMBER", "IDENTIFIER", "CHUNK_IDENTIFIER", "CHUNK_TYPE_IDENTIFIER", "SLOTS", "SLOT", "LT", "GT", "EQUALS", "NOT", "WITHIN", "GTE", "LTE", "OR", "AND", "LOGIC", "CLASS_SPEC", "NAME", "PARENT", "PARENTS", "UNKNOWN", "ADD_TOKEN", "CLOSE_TOKEN", "DIGITS_TOKEN", "EQUALS_TOKEN", "ESCAPE_TOKEN", "GTE_TOKEN", "GT_TOKEN", "IDENTIFIER_TOKEN", "INCLUDE_TOKEN", "ISA_TOKEN", "LETTER_TOKEN", "LTE_TOKEN", "LT_TOKEN", "MATCH_TOKEN", "MINUS_TOKEN", "ML_COMMENT", "NO_IMPORT_TOKEN", "NUMBER_TOKEN", "OPEN_TOKEN", "PLUS_TOKEN", "PUNCTUATION_FRAGMENT", "QUERY_TOKEN", "QUESTION_TOKEN", "REMOVE_TOKEN", "SL_COMMENT", "STRING_TOKEN", "VARIABLE_TOKEN", "WITHIN_TOKEN", "WS_TOKEN", "'!'", "'!STOP!'", "'!output!'", "'!stop!'", "'==>'", "'BIND'", "'EVAL'", "'P'", "'add-dm'", "'bind'", "'chunk-type'", "'clear-all'", "'define-model'", "'eval'", "'extension'", "'goal-focus'", "'import'", "'module'", "'p'", "'sdp'", "'sgp'", "'spp'"
    };

    public static final int EOF=-1;
    public static final int LT=43;
    public static final int LOGIC=52;
    public static final int PARAMETERS=16;
    public static final int SCRIPTABLE_ACTION=31;
    public static final int CHUNK=13;
    public static final int GTE=48;
    public static final int PROXY_CONDITION=24;
    public static final int EQUALS=45;
    public static final int LIBRARY=5;
    public static final int NOT=46;
    public static final int CHUNK_TYPE=11;
    public static final int AND=51;
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
    public static final int WITHIN=47;
    public static final int CHUNKS=12;
    public static final int SLOTS=41;
    public static final int GT=44;
    public static final int SET_ACTION=27;
    public static final int QUERY_CONDITION=22;
    public static final int EXTENSION=9;
    public static final int OUTPUT_ACTION=30;
    public static final int LANG=33;
    public static final int STRING=36;
    public static final int T__87=87;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__90=90;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__99=99;
    public static final int T__100=100;
    public static final int T__101=101;
    public static final int T__102=102;
    public static final int T__103=103;
    public static final int T__104=104;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int T__107=107;
    public static final int T__108=108;
    public static final int ADD_TOKEN=58;
    public static final int CLOSE_TOKEN=59;
    public static final int DIGITS_TOKEN=60;
    public static final int EQUALS_TOKEN=61;
    public static final int ESCAPE_TOKEN=62;
    public static final int GTE_TOKEN=63;
    public static final int GT_TOKEN=64;
    public static final int IDENTIFIER_TOKEN=65;
    public static final int INCLUDE_TOKEN=66;
    public static final int ISA_TOKEN=67;
    public static final int LETTER_TOKEN=68;
    public static final int LTE_TOKEN=69;
    public static final int LT_TOKEN=70;
    public static final int MATCH_TOKEN=71;
    public static final int MINUS_TOKEN=72;
    public static final int ML_COMMENT=73;
    public static final int NO_IMPORT_TOKEN=74;
    public static final int NUMBER_TOKEN=75;
    public static final int OPEN_TOKEN=76;
    public static final int PLUS_TOKEN=77;
    public static final int PUNCTUATION_FRAGMENT=78;
    public static final int QUERY_TOKEN=79;
    public static final int QUESTION_TOKEN=80;
    public static final int REMOVE_TOKEN=81;
    public static final int SL_COMMENT=82;
    public static final int STRING_TOKEN=83;
    public static final int VARIABLE_TOKEN=84;
    public static final int WITHIN_TOKEN=85;
    public static final int WS_TOKEN=86;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators

    protected static class ModelGlobals_scope {
        List<CommonTree> misplacedChunks;
        Map<String, CommonTree> chunksWrapperMap;
        Map<String, CommonTree> temporaryChunkTypesMap;
        Map<String, CommonTree> chunkMap;
        CommonTree model;
        boolean moduleImported;
    }
    protected Stack ModelGlobals_stack = new Stack();


    protected static class Suppress_scope {
        boolean warnings;
        boolean errors;
    }
    protected Stack Suppress_stack = new Stack();



    public LispParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public LispParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return LispParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g"; }



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
     if(parameterList.getChildCount()%2 == 1)
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
     


    public static class model_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "model"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:294:1: model : ( clearAll )* OPEN_TOKEN 'define-model' name ( module )* ( extension )* ( importDirective )* ( (ct= chunkType ) | (p= production ) | addDm | (dParam= sdp ) | (gParam= sgp ) | (pParam= spp ) | goalFocus )* CLOSE_TOKEN -> ^() ;
    public final LispParser.model_return model() throws RecognitionException {
        ModelGlobals_stack.push(new ModelGlobals_scope());

        LispParser.model_return retval = new LispParser.model_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN2=null;
        Token string_literal3=null;
        Token CLOSE_TOKEN10=null;
        LispParser.chunkType_return ct =null;

        LispParser.production_return p =null;

        LispParser.sdp_return dParam =null;

        LispParser.sgp_return gParam =null;

        LispParser.spp_return pParam =null;

        LispParser.clearAll_return clearAll1 =null;

        LispParser.name_return name4 =null;

        LispParser.module_return module5 =null;

        LispParser.extension_return extension6 =null;

        LispParser.importDirective_return importDirective7 =null;

        LispParser.addDm_return addDm8 =null;

        LispParser.goalFocus_return goalFocus9 =null;


        CommonTree OPEN_TOKEN2_tree=null;
        CommonTree string_literal3_tree=null;
        CommonTree CLOSE_TOKEN10_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleTokenStream stream_99=new RewriteRuleTokenStream(adaptor,"token 99");
        RewriteRuleSubtreeStream stream_extension=new RewriteRuleSubtreeStream(adaptor,"rule extension");
        RewriteRuleSubtreeStream stream_chunkType=new RewriteRuleSubtreeStream(adaptor,"rule chunkType");
        RewriteRuleSubtreeStream stream_importDirective=new RewriteRuleSubtreeStream(adaptor,"rule importDirective");
        RewriteRuleSubtreeStream stream_module=new RewriteRuleSubtreeStream(adaptor,"rule module");
        RewriteRuleSubtreeStream stream_addDm=new RewriteRuleSubtreeStream(adaptor,"rule addDm");
        RewriteRuleSubtreeStream stream_sgp=new RewriteRuleSubtreeStream(adaptor,"rule sgp");
        RewriteRuleSubtreeStream stream_clearAll=new RewriteRuleSubtreeStream(adaptor,"rule clearAll");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_goalFocus=new RewriteRuleSubtreeStream(adaptor,"rule goalFocus");
        RewriteRuleSubtreeStream stream_spp=new RewriteRuleSubtreeStream(adaptor,"rule spp");
        RewriteRuleSubtreeStream stream_production=new RewriteRuleSubtreeStream(adaptor,"rule production");
        RewriteRuleSubtreeStream stream_sdp=new RewriteRuleSubtreeStream(adaptor,"rule sdp");

        //initialize the global
        ((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks = new ArrayList<CommonTree>();
        ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunksWrapperMap = new HashMap<String, CommonTree>();
        ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunkMap = new HashMap<String, CommonTree>();
        ((ModelGlobals_scope)ModelGlobals_stack.peek()).temporaryChunkTypesMap = new HashMap<String,CommonTree>();
         
        CommonTree model = null;
        Collection<CommonTree> chunkTypeList = new ArrayList<CommonTree>();
        Collection<CommonTree> productionList = new ArrayList<CommonTree>();
        ArrayList<CommonTree> sgpList = new ArrayList<CommonTree>();
        ArrayList<CommonTree> sdpList = new ArrayList<CommonTree>();
        ArrayList<CommonTree> sppList = new ArrayList<CommonTree>();

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:310:3: ( ( clearAll )* OPEN_TOKEN 'define-model' name ( module )* ( extension )* ( importDirective )* ( (ct= chunkType ) | (p= production ) | addDm | (dParam= sdp ) | (gParam= sgp ) | (pParam= spp ) | goalFocus )* CLOSE_TOKEN -> ^() )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:310:3: ( clearAll )* OPEN_TOKEN 'define-model' name ( module )* ( extension )* ( importDirective )* ( (ct= chunkType ) | (p= production ) | addDm | (dParam= sdp ) | (gParam= sgp ) | (pParam= spp ) | goalFocus )* CLOSE_TOKEN
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:310:3: ( clearAll )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OPEN_TOKEN) ) {
                    int LA1_1 = input.LA(2);

                    if ( (LA1_1==98) ) {
                        alt1=1;
                    }


                }


                switch (alt1) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:310:5: clearAll
            	    {
            	    pushFollow(FOLLOW_clearAll_in_model355);
            	    clearAll1=clearAll();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_clearAll.add(clearAll1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            OPEN_TOKEN2=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_model380); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(OPEN_TOKEN2);


            string_literal3=(Token)match(input,99,FOLLOW_99_in_model382); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_99.add(string_literal3);


            pushFollow(FOLLOW_name_in_model384);
            name4=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name4.getTree());

            if ( state.backtracking==0 ) {
                             /*
                              auto import the modules..
                             */
                              model = _support.createModelTree((name4!=null?input.toString(name4.start,name4.stop):null));
                              ((ModelGlobals_scope)ModelGlobals_stack.peek()).model = model;
                             }

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:319:18: ( module )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==OPEN_TOKEN) ) {
                    int LA2_1 = input.LA(2);

                    if ( (LA2_1==104) ) {
                        alt2=1;
                    }


                }


                switch (alt2) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:319:19: module
            	    {
            	    pushFollow(FOLLOW_module_in_model423);
            	    module5=module();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_module.add(module5.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            if ( state.backtracking==0 ) {
                              //have we imported anything? if we have, don't auto import
                              if(!((ModelGlobals_scope)ModelGlobals_stack.peek()).moduleImported)
                              try
                               {
                                //auto import 
                                CommonTree modules = ASTSupport.getFirstDescendantWithType(model, MODULES);
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.declarative.six.DefaultDeclarativeModule6.class.getName(), true));
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.procedural.six.DefaultProceduralModule6.class.getName(), true));    
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.procedural.six.learning.DefaultProceduralLearningModule6.class.getName(), true));    
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4.class.getName(), true));                    
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.imaginal.six.DefaultImaginalModule6.class.getName(), true));                                       
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.goal.six.DefaultGoalModule6.class.getName(), true));
                                modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.core.module.retrieval.six.DefaultRetrievalModule6.class.getName(), true));
                                //_parser.getImportDelegate().importInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, org.jactr.modules.pm.visual.six.DefaultVisualModule6.class.getName(), true);
                               }
                              catch(Exception e)
                              {
                               reportException(e);
                              } 
                               
                             }

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:342:18: ( extension )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==OPEN_TOKEN) ) {
                    int LA3_1 = input.LA(2);

                    if ( (LA3_1==101) ) {
                        alt3=1;
                    }


                }


                switch (alt3) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:342:19: extension
            	    {
            	    pushFollow(FOLLOW_extension_in_model464);
            	    extension6=extension();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_extension.add(extension6.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:343:18: ( importDirective )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==OPEN_TOKEN) ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1==103) ) {
                        alt4=1;
                    }


                }


                switch (alt4) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:343:19: importDirective
            	    {
            	    pushFollow(FOLLOW_importDirective_in_model486);
            	    importDirective7=importDirective();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_importDirective.add(importDirective7.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            if ( state.backtracking==0 ) {
                             //strip values..
                               Map<String, CommonTree> importedChunks = ASTSupport.getMapOfTrees(model, CHUNK);
                               for(String chunkName : importedChunks.keySet())
                                {
                                 chunkName = chunkName.toLowerCase();
                                 LOGGER.debug("importing "+chunkName);
                                 ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunkMap.put(chunkName, importedChunks.get(chunkName));
                                }
                               //and chunk type wrappers
                               Map<String, CommonTree> importedWrappers = ASTSupport.getMapOfTrees(model, CHUNK_TYPE);
                               for(String chunkTypeName : importedWrappers.keySet())
                                {
                                 chunkTypeName = chunkTypeName.toLowerCase();
                                 LOGGER.debug("importing ct "+chunkTypeName);
                                 CommonTree wrapper = ASTSupport.getFirstDescendantWithType(importedWrappers.get(chunkTypeName), CHUNKS);
                                 ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunksWrapperMap.put(chunkTypeName, wrapper);
                                } 
                             }

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:364:4: ( (ct= chunkType ) | (p= production ) | addDm | (dParam= sdp ) | (gParam= sgp ) | (pParam= spp ) | goalFocus )*
            loop5:
            do {
                int alt5=8;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==OPEN_TOKEN) ) {
                    switch ( input.LA(2) ) {
                    case 95:
                        {
                        alt5=3;
                        }
                        break;
                    case 102:
                        {
                        alt5=7;
                        }
                        break;
                    case 97:
                        {
                        alt5=1;
                        }
                        break;
                    case 94:
                    case 105:
                        {
                        alt5=2;
                        }
                        break;
                    case 106:
                        {
                        alt5=4;
                        }
                        break;
                    case 107:
                        {
                        alt5=5;
                        }
                        break;
                    case 108:
                        {
                        alt5=6;
                        }
                        break;

                    }

                }


                switch (alt5) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:364:6: (ct= chunkType )
            	    {
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:364:6: (ct= chunkType )
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:364:7: ct= chunkType
            	    {
            	    pushFollow(FOLLOW_chunkType_in_model535);
            	    ct=chunkType();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_chunkType.add(ct.getTree());

            	    if ( state.backtracking==0 ) {chunkTypeList.add(ct.tree);}

            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:365:13: (p= production )
            	    {
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:365:13: (p= production )
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:365:14: p= production
            	    {
            	    pushFollow(FOLLOW_production_in_model555);
            	    p=production();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_production.add(p.getTree());

            	    if ( state.backtracking==0 ) {productionList.add(p.tree);}

            	    }


            	    }
            	    break;
            	case 3 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:366:13: addDm
            	    {
            	    pushFollow(FOLLOW_addDm_in_model573);
            	    addDm8=addDm();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_addDm.add(addDm8.getTree());

            	    }
            	    break;
            	case 4 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:367:8: (dParam= sdp )
            	    {
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:367:8: (dParam= sdp )
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:367:9: dParam= sdp
            	    {
            	    pushFollow(FOLLOW_sdp_in_model586);
            	    dParam=sdp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sdp.add(dParam.getTree());

            	    if ( state.backtracking==0 ) {sdpList.add(dParam.tree);}

            	    }


            	    }
            	    break;
            	case 5 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:368:8: (gParam= sgp )
            	    {
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:368:8: (gParam= sgp )
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:368:9: gParam= sgp
            	    {
            	    pushFollow(FOLLOW_sgp_in_model602);
            	    gParam=sgp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sgp.add(gParam.getTree());

            	    if ( state.backtracking==0 ) {sgpList.add(gParam.tree);}

            	    }


            	    }
            	    break;
            	case 6 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:369:8: (pParam= spp )
            	    {
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:369:8: (pParam= spp )
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:369:9: pParam= spp
            	    {
            	    pushFollow(FOLLOW_spp_in_model618);
            	    pParam=spp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_spp.add(pParam.getTree());

            	    if ( state.backtracking==0 ) {sppList.add(pParam.tree);}

            	    }


            	    }
            	    break;
            	case 7 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:370:8: goalFocus
            	    {
            	    pushFollow(FOLLOW_goalFocus_in_model631);
            	    goalFocus9=goalFocus();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_goalFocus.add(goalFocus9.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            CLOSE_TOKEN10=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_model654); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN10);


            if ( state.backtracking==0 ) {
            			   
            			   CommonTree decMem = ASTSupport.getFirstDescendantWithType(model, DECLARATIVE_MEMORY);
            			   CommonTree procMem = ASTSupport.getFirstDescendantWithType(model, PROCEDURAL_MEMORY);
            			   
            			   for(CommonTree chunkTypeNode : chunkTypeList)
            			   	decMem.addChild(chunkTypeNode);
            			   	
            			/*
            			  double check the orphaned chunks
            			*/
            			Collection<CommonTree> del = new ArrayList<CommonTree>();
            			for(CommonTree chunk : ((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks)
            			 if(addChunkToChunkType((ModelGlobals_scope)ModelGlobals_stack.peek(), chunk))
            			   del.add(chunk);
            			((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks.removeAll(del);
            			
            			if(((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks.size()!=0)
            			 {
            			  //**** an error condition this chunks are orphans
            			  throw new CompilationError("Chunks "+((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks+" have no known chunktypes",null);
            			 }
            			 
            			/*
            			 all the chunks are taken care of.. let's do some parameter mapping
            			*/ 
            			for(CommonTree decParam : sdpList)
            			 setParameters(decParam, ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunkMap);
            			 
            			 
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
            			modelMap.put((name4!=null?input.toString(name4.start,name4.stop):null), model);
            			for(CommonTree globalParam : sgpList)
            			 setParameters(globalParam, modelMap);    	
            		}

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 424:5: -> ^()
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:425:5: ^()
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(model, root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            ModelGlobals_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "model"


    public static class module_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "module"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:428:1: module : OPEN_TOKEN 'module' s= string (i= NO_IMPORT_TOKEN )? CLOSE_TOKEN ;
    public final LispParser.module_return module() throws RecognitionException {
        LispParser.module_return retval = new LispParser.module_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token i=null;
        Token OPEN_TOKEN11=null;
        Token string_literal12=null;
        Token CLOSE_TOKEN13=null;
        LispParser.string_return s =null;


        CommonTree i_tree=null;
        CommonTree OPEN_TOKEN11_tree=null;
        CommonTree string_literal12_tree=null;
        CommonTree CLOSE_TOKEN13_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:428:9: ( OPEN_TOKEN 'module' s= string (i= NO_IMPORT_TOKEN )? CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:428:12: OPEN_TOKEN 'module' s= string (i= NO_IMPORT_TOKEN )? CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN11=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_module690); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OPEN_TOKEN11_tree = 
            (CommonTree)adaptor.create(OPEN_TOKEN11)
            ;
            adaptor.addChild(root_0, OPEN_TOKEN11_tree);
            }

            string_literal12=(Token)match(input,104,FOLLOW_104_in_module692); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal12_tree = 
            (CommonTree)adaptor.create(string_literal12)
            ;
            adaptor.addChild(root_0, string_literal12_tree);
            }

            pushFollow(FOLLOW_string_in_module696);
            s=string();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, s.getTree());

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:428:41: (i= NO_IMPORT_TOKEN )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==NO_IMPORT_TOKEN) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:428:42: i= NO_IMPORT_TOKEN
                    {
                    i=(Token)match(input,NO_IMPORT_TOKEN,FOLLOW_NO_IMPORT_TOKEN_in_module701); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    i_tree = 
                    (CommonTree)adaptor.create(i)
                    ;
                    adaptor.addChild(root_0, i_tree);
                    }

                    }
                    break;

            }


            CLOSE_TOKEN13=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_module705); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLOSE_TOKEN13_tree = 
            (CommonTree)adaptor.create(CLOSE_TOKEN13)
            ;
            adaptor.addChild(root_0, CLOSE_TOKEN13_tree);
            }

            if ( state.backtracking==0 ) {
                             boolean importContents = true;
                             if(i!=null)
                              {
                              importContents = false;
                              }  
                             String className = (s!=null?((CommonTree)s.tree):null).getText().trim();
                             //className = className.substring(1, className.length()-1);
                             try
                             {
                              CommonTree modules = ASTSupport.getFirstDescendantWithType(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, MODULES);
                              modules.addChild(_parser.getImportDelegate().importModuleInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, className, importContents));
                              ((ModelGlobals_scope)ModelGlobals_stack.peek()).moduleImported =true;
                             }
                             catch(Exception e)
                             {
                               if(e instanceof CommonTreeException)
                                 {
                                  ((CommonTreeException)e).setStartNode((s!=null?((CommonTree)s.tree):null));
                                  reportException(e);
                                 }
                               else  
                                reportException(new CompilationError("Could not access class "+className, (s!=null?((CommonTree)s.tree):null), e));
                             }
                             
                           }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "module"


    public static class extension_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "extension"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:459:1: extension : OPEN_TOKEN 'extension' s= string CLOSE_TOKEN ;
    public final LispParser.extension_return extension() throws RecognitionException {
        LispParser.extension_return retval = new LispParser.extension_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN14=null;
        Token string_literal15=null;
        Token CLOSE_TOKEN16=null;
        LispParser.string_return s =null;


        CommonTree OPEN_TOKEN14_tree=null;
        CommonTree string_literal15_tree=null;
        CommonTree CLOSE_TOKEN16_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:459:12: ( OPEN_TOKEN 'extension' s= string CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:459:15: OPEN_TOKEN 'extension' s= string CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN14=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_extension736); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OPEN_TOKEN14_tree = 
            (CommonTree)adaptor.create(OPEN_TOKEN14)
            ;
            adaptor.addChild(root_0, OPEN_TOKEN14_tree);
            }

            string_literal15=(Token)match(input,101,FOLLOW_101_in_extension738); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal15_tree = 
            (CommonTree)adaptor.create(string_literal15)
            ;
            adaptor.addChild(root_0, string_literal15_tree);
            }

            pushFollow(FOLLOW_string_in_extension742);
            s=string();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, s.getTree());

            CLOSE_TOKEN16=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_extension744); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLOSE_TOKEN16_tree = 
            (CommonTree)adaptor.create(CLOSE_TOKEN16)
            ;
            adaptor.addChild(root_0, CLOSE_TOKEN16_tree);
            }

            if ( state.backtracking==0 ) {
                             String className = (s!=null?((CommonTree)s.tree):null).getText().trim();
                             try
                             {
                              CommonTree extensions = ASTSupport.getFirstDescendantWithType(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, EXTENSIONS);
                              extensions.addChild(_parser.getImportDelegate().importExtensionInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, className, true));
                             }
                             catch(Exception e)
                             {
                               if(e instanceof CommonTreeException)
                                 {
                                  ((CommonTreeException)e).setStartNode((s!=null?((CommonTree)s.tree):null));
                                  reportException(e);
                                 }
                               else  
                                reportException(new CompilationError("Could not access class "+className, (s!=null?((CommonTree)s.tree):null), e));
                             }
                           }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "extension"


    public static class importDirective_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "importDirective"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:480:1: importDirective : OPEN_TOKEN 'import' s= string CLOSE_TOKEN ;
    public final LispParser.importDirective_return importDirective() throws RecognitionException {
        LispParser.importDirective_return retval = new LispParser.importDirective_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN17=null;
        Token string_literal18=null;
        Token CLOSE_TOKEN19=null;
        LispParser.string_return s =null;


        CommonTree OPEN_TOKEN17_tree=null;
        CommonTree string_literal18_tree=null;
        CommonTree CLOSE_TOKEN19_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:481:2: ( OPEN_TOKEN 'import' s= string CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:481:4: OPEN_TOKEN 'import' s= string CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN17=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_importDirective774); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OPEN_TOKEN17_tree = 
            (CommonTree)adaptor.create(OPEN_TOKEN17)
            ;
            adaptor.addChild(root_0, OPEN_TOKEN17_tree);
            }

            string_literal18=(Token)match(input,103,FOLLOW_103_in_importDirective777); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal18_tree = 
            (CommonTree)adaptor.create(string_literal18)
            ;
            adaptor.addChild(root_0, string_literal18_tree);
            }

            pushFollow(FOLLOW_string_in_importDirective781);
            s=string();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, s.getTree());

            CLOSE_TOKEN19=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_importDirective783); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLOSE_TOKEN19_tree = 
            (CommonTree)adaptor.create(CLOSE_TOKEN19)
            ;
            adaptor.addChild(root_0, CLOSE_TOKEN19_tree);
            }

            if ( state.backtracking==0 ) {
            	 String url = "";
            	 if(s!=null)
            	  url = (s!=null?input.toString(s.start,s.stop):null);
            	  
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
            	   _parser.getImportDelegate().importInto(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, location, false);
            	   /*
            	    having just imported a slew of chunks,types and productions, we do need to update
            	    our internal tables, particular the chunkTypeWrappers
            	   */
            	   //Map<String, CommonTree> chunkTypes = ASTSupport.getMapOfTrees(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, CHUNK_TYPE);
            	   //for(Map.Entry<String,CommonTree> chunkType : chunkTypes.entrySet())
            	   //{
            	    //((ModelGlobals_scope)ModelGlobals_stack.peek()).chunksWrapperMap.put(chunkType.getKey(), ASTSupport.getFirstDescendantWithType(chunkType.getValue(), CHUNKS));
            	   //}
            	  }
            	  catch(Exception e)
            	  {
            	   reportException(new CompilationError("Could not import from "+location+", "+e.getMessage(), s.tree, e));
            	  } 
            	}

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "importDirective"


    public static class clearAll_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "clearAll"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:517:1: clearAll : OPEN_TOKEN 'clear-all' CLOSE_TOKEN ;
    public final LispParser.clearAll_return clearAll() throws RecognitionException {
        LispParser.clearAll_return retval = new LispParser.clearAll_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN20=null;
        Token string_literal21=null;
        Token CLOSE_TOKEN22=null;

        CommonTree OPEN_TOKEN20_tree=null;
        CommonTree string_literal21_tree=null;
        CommonTree CLOSE_TOKEN22_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:518:2: ( OPEN_TOKEN 'clear-all' CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:518:5: OPEN_TOKEN 'clear-all' CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN20=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_clearAll800); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OPEN_TOKEN20_tree = 
            (CommonTree)adaptor.create(OPEN_TOKEN20)
            ;
            adaptor.addChild(root_0, OPEN_TOKEN20_tree);
            }

            string_literal21=(Token)match(input,98,FOLLOW_98_in_clearAll802); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal21_tree = 
            (CommonTree)adaptor.create(string_literal21)
            ;
            adaptor.addChild(root_0, string_literal21_tree);
            }

            CLOSE_TOKEN22=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_clearAll804); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLOSE_TOKEN22_tree = 
            (CommonTree)adaptor.create(CLOSE_TOKEN22)
            ;
            adaptor.addChild(root_0, CLOSE_TOKEN22_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "clearAll"


    public static class goalFocus_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "goalFocus"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:520:1: goalFocus : o= OPEN_TOKEN 'goal-focus' id= chunkIdentifier c= CLOSE_TOKEN ;
    public final LispParser.goalFocus_return goalFocus() throws RecognitionException {
        LispParser.goalFocus_return retval = new LispParser.goalFocus_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token c=null;
        Token string_literal23=null;
        LispParser.chunkIdentifier_return id =null;


        CommonTree o_tree=null;
        CommonTree c_tree=null;
        CommonTree string_literal23_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:521:2: (o= OPEN_TOKEN 'goal-focus' id= chunkIdentifier c= CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:521:4: o= OPEN_TOKEN 'goal-focus' id= chunkIdentifier c= CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_goalFocus816); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            o_tree = 
            (CommonTree)adaptor.create(o)
            ;
            adaptor.addChild(root_0, o_tree);
            }

            string_literal23=(Token)match(input,102,FOLLOW_102_in_goalFocus818); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal23_tree = 
            (CommonTree)adaptor.create(string_literal23)
            ;
            adaptor.addChild(root_0, string_literal23_tree);
            }

            pushFollow(FOLLOW_chunkIdentifier_in_goalFocus822);
            id=chunkIdentifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id.getTree());

            c=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_goalFocus826); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            c_tree = 
            (CommonTree)adaptor.create(c)
            ;
            adaptor.addChild(root_0, c_tree);
            }

            if ( state.backtracking==0 ) {
            	 Map<String, CommonTree> buffers = ASTSupport.getMapOfTrees(((ModelGlobals_scope)ModelGlobals_stack.peek()).model, BUFFER);
            	 if(buffers.containsKey("goal"))
            	  {
            	   CommonTree buffer = buffers.get("goal");
            	   CommonTree chunks = (CommonTree) buffer.getFirstChildWithType(CHUNKS);
            	   chunks.addChild((id!=null?((CommonTree)id.tree):null));
            	  }
            	 else
            	 {
            	  CompilationWarning warning = new CompilationWarning("Goal buffer was not found", (CommonTree)adaptor.create(o));
            	  warning.setEndNode((CommonTree)adaptor.create(c));
            	  reportException(warning);
            	 }
            	}

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "goalFocus"


    public static class sgp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sgp"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:539:1: sgp : o= OPEN_TOKEN ( 'sgp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() ;
    public final LispParser.sgp_return sgp() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.sgp_return retval = new LispParser.sgp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token string_literal24=null;
        Token CLOSE_TOKEN25=null;
        LispParser.slotValue_return i =null;


        CommonTree o_tree=null;
        CommonTree string_literal24_tree=null;
        CommonTree CLOSE_TOKEN25_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");

         ((Suppress_scope)Suppress_stack.peek()).warnings = true;
         Collection<CommonTree> order = new ArrayList<CommonTree>();
         CommonTree rtn = null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:2: (o= OPEN_TOKEN ( 'sgp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:4: o= OPEN_TOKEN ( 'sgp' ) (i= slotValue )+ CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_sgp852); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:17: ( 'sgp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:18: 'sgp'
            {
            string_literal24=(Token)match(input,107,FOLLOW_107_in_sgp855); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_107.add(string_literal24);


            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:25: (i= slotValue )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==IDENTIFIER_TOKEN||LA7_0==NUMBER_TOKEN||(LA7_0 >= STRING_TOKEN && LA7_0 <= VARIABLE_TOKEN)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:546:27: i= slotValue
            	    {
            	    pushFollow(FOLLOW_slotValue_in_sgp862);
            	    i=slotValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_slotValue.add(i.getTree());

            	    if ( state.backtracking==0 ) {order.add(i.tree);}

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            CLOSE_TOKEN25=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_sgp916); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN25);


            if ( state.backtracking==0 ) {
            	   rtn = (CommonTree) adaptor.create(UNKNOWN, o); 
            	   for(CommonTree child : order)
            	    rtn.addChild(child);
            	  }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 554:2: -> ^()
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:554:5: ^()
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(rtn, root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "sgp"


    public static class sdp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sdp"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:556:1: sdp : o= OPEN_TOKEN ( 'sdp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() ;
    public final LispParser.sdp_return sdp() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.sdp_return retval = new LispParser.sdp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token string_literal26=null;
        Token CLOSE_TOKEN27=null;
        LispParser.slotValue_return i =null;


        CommonTree o_tree=null;
        CommonTree string_literal26_tree=null;
        CommonTree CLOSE_TOKEN27_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");

         ((Suppress_scope)Suppress_stack.peek()).warnings = true;
         Collection<CommonTree> order = new ArrayList<CommonTree>();
         CommonTree rtn = null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:2: (o= OPEN_TOKEN ( 'sdp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:4: o= OPEN_TOKEN ( 'sdp' ) (i= slotValue )+ CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_sdp949); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:17: ( 'sdp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:18: 'sdp'
            {
            string_literal26=(Token)match(input,106,FOLLOW_106_in_sdp952); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_106.add(string_literal26);


            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:25: (i= slotValue )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==IDENTIFIER_TOKEN||LA8_0==NUMBER_TOKEN||(LA8_0 >= STRING_TOKEN && LA8_0 <= VARIABLE_TOKEN)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:563:27: i= slotValue
            	    {
            	    pushFollow(FOLLOW_slotValue_in_sdp959);
            	    i=slotValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_slotValue.add(i.getTree());

            	    if ( state.backtracking==0 ) {order.add(i.tree);}

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            CLOSE_TOKEN27=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_sdp1014); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN27);


            if ( state.backtracking==0 ) {
            	   rtn = (CommonTree) adaptor.create(UNKNOWN, o); 
            	   for(CommonTree child : order)
            	    rtn.addChild(child);
            	  }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 571:2: -> ^()
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:571:5: ^()
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(rtn, root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "sdp"


    public static class spp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "spp"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:572:1: spp : o= OPEN_TOKEN ( 'spp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() ;
    public final LispParser.spp_return spp() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.spp_return retval = new LispParser.spp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token string_literal28=null;
        Token CLOSE_TOKEN29=null;
        LispParser.slotValue_return i =null;


        CommonTree o_tree=null;
        CommonTree string_literal28_tree=null;
        CommonTree CLOSE_TOKEN29_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_108=new RewriteRuleTokenStream(adaptor,"token 108");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");

         ((Suppress_scope)Suppress_stack.peek()).warnings = true;
         Collection<CommonTree> order = new ArrayList<CommonTree>();
         CommonTree rtn = null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:2: (o= OPEN_TOKEN ( 'spp' ) (i= slotValue )+ CLOSE_TOKEN -> ^() )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:4: o= OPEN_TOKEN ( 'spp' ) (i= slotValue )+ CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_spp1047); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:17: ( 'spp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:18: 'spp'
            {
            string_literal28=(Token)match(input,108,FOLLOW_108_in_spp1050); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_108.add(string_literal28);


            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:25: (i= slotValue )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==IDENTIFIER_TOKEN||LA9_0==NUMBER_TOKEN||(LA9_0 >= STRING_TOKEN && LA9_0 <= VARIABLE_TOKEN)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:579:27: i= slotValue
            	    {
            	    pushFollow(FOLLOW_slotValue_in_spp1057);
            	    i=slotValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_slotValue.add(i.getTree());

            	    if ( state.backtracking==0 ) {order.add(i.tree);}

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


            CLOSE_TOKEN29=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_spp1112); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN29);


            if ( state.backtracking==0 ) {
            	   rtn = (CommonTree) adaptor.create(UNKNOWN, o); 
            	   for(CommonTree child : order)
            	    rtn.addChild(child);
            	  }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 587:2: -> ^()
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:587:5: ^()
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(rtn, root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "spp"


    public static class unknownList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unknownList"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:590:1: unknownList : o= OPEN_TOKEN (v= slotValue |u= unknownList )+ c= CLOSE_TOKEN -> ^() ;
    public final LispParser.unknownList_return unknownList() throws RecognitionException {
        LispParser.unknownList_return retval = new LispParser.unknownList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token c=null;
        LispParser.slotValue_return v =null;

        LispParser.unknownList_return u =null;


        CommonTree o_tree=null;
        CommonTree c_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");
        RewriteRuleSubtreeStream stream_unknownList=new RewriteRuleSubtreeStream(adaptor,"rule unknownList");

         Collection<CommonTree> order = new ArrayList<CommonTree>();
         CommonTree rtn = null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:595:2: (o= OPEN_TOKEN (v= slotValue |u= unknownList )+ c= CLOSE_TOKEN -> ^() )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:595:4: o= OPEN_TOKEN (v= slotValue |u= unknownList )+ c= CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_unknownList1142); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:595:17: (v= slotValue |u= unknownList )+
            int cnt10=0;
            loop10:
            do {
                int alt10=3;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==IDENTIFIER_TOKEN||LA10_0==NUMBER_TOKEN||(LA10_0 >= STRING_TOKEN && LA10_0 <= VARIABLE_TOKEN)) ) {
                    alt10=1;
                }
                else if ( (LA10_0==OPEN_TOKEN) ) {
                    alt10=2;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:595:18: v= slotValue
            	    {
            	    pushFollow(FOLLOW_slotValue_in_unknownList1147);
            	    v=slotValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_slotValue.add(v.getTree());

            	    if ( state.backtracking==0 ) {order.add(v.tree);}

            	    }
            	    break;
            	case 2 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:596:25: u= unknownList
            	    {
            	    pushFollow(FOLLOW_unknownList_in_unknownList1177);
            	    u=unknownList();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_unknownList.add(u.getTree());

            	    if ( state.backtracking==0 ) {order.add(u.tree);}

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            c=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_unknownList1208); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(c);


            if ( state.backtracking==0 ) {
            	 boolean warn = true;
            	 try{
            	  warn = ! ((Suppress_scope)Suppress_stack.peek()).warnings;
            	 }
            	 catch(Exception npe)
            	 {
            	  warn = true;
            	 }
            	 if(warn)
            	  {
            	   CompilationWarning warning = new CompilationWarning("Command is unknown", (CommonTree)adaptor.create(o));
            	   warning.setEndNode((CommonTree)adaptor.create(c));
            	   reportException(warning);
            	  }
            	  
            	 rtn = (CommonTree) adaptor.create(UNKNOWN, o); 
            	 for(CommonTree child : order)
            	  rtn.addChild(child);
            	}

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 618:3: -> ^()
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:618:6: ^()
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(rtn, root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "unknownList"


    public static class addDm_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "addDm"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:621:1: addDm : OPEN_TOKEN 'add-dm' (c= chunk )+ CLOSE_TOKEN ;
    public final LispParser.addDm_return addDm() throws RecognitionException {
        LispParser.addDm_return retval = new LispParser.addDm_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN30=null;
        Token string_literal31=null;
        Token CLOSE_TOKEN32=null;
        LispParser.chunk_return c =null;


        CommonTree OPEN_TOKEN30_tree=null;
        CommonTree string_literal31_tree=null;
        CommonTree CLOSE_TOKEN32_tree=null;


         Collection<CommonTree> chunks = new ArrayList<CommonTree>();

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:625:2: ( OPEN_TOKEN 'add-dm' (c= chunk )+ CLOSE_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:625:4: OPEN_TOKEN 'add-dm' (c= chunk )+ CLOSE_TOKEN
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN30=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_addDm1234); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OPEN_TOKEN30_tree = 
            (CommonTree)adaptor.create(OPEN_TOKEN30)
            ;
            adaptor.addChild(root_0, OPEN_TOKEN30_tree);
            }

            string_literal31=(Token)match(input,95,FOLLOW_95_in_addDm1236); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal31_tree = 
            (CommonTree)adaptor.create(string_literal31)
            ;
            adaptor.addChild(root_0, string_literal31_tree);
            }

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:625:24: (c= chunk )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==OPEN_TOKEN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:625:25: c= chunk
            	    {
            	    pushFollow(FOLLOW_chunk_in_addDm1241);
            	    c=chunk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, c.getTree());

            	    if ( state.backtracking==0 ) {chunks.add(c.tree);}

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            CLOSE_TOKEN32=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_addDm1247); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLOSE_TOKEN32_tree = 
            (CommonTree)adaptor.create(CLOSE_TOKEN32)
            ;
            adaptor.addChild(root_0, CLOSE_TOKEN32_tree);
            }

            if ( state.backtracking==0 ) {
            	//test 
            	for(CommonTree chunkNode : chunks)
            	 {
            		Map<String, CommonTree> chunkMap = ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunkMap;
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
            		   ((ModelGlobals_scope)ModelGlobals_stack.peek()).misplacedChunks.add(chunkNode);
            		   //***** log a warning that there is no chunktype ddefined yet
            		  } 
            	 }
            	}

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "addDm"


    public static class chunk_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "chunk"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:655:1: chunk : o= OPEN_TOKEN name chunkParent (s= slots )? CLOSE_TOKEN -> ^( CHUNK[$o,\"chunk\"] name chunkParent PARAMETERS ) ;
    public final LispParser.chunk_return chunk() throws RecognitionException {
        LispParser.chunk_return retval = new LispParser.chunk_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token CLOSE_TOKEN35=null;
        LispParser.slots_return s =null;

        LispParser.name_return name33 =null;

        LispParser.chunkParent_return chunkParent34 =null;


        CommonTree o_tree=null;
        CommonTree CLOSE_TOKEN35_tree=null;
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_chunkParent=new RewriteRuleSubtreeStream(adaptor,"rule chunkParent");
        RewriteRuleSubtreeStream stream_slots=new RewriteRuleSubtreeStream(adaptor,"rule slots");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");

         //treeAdaptor no longer supports createToken.. but commonAdaptor does
         CommonTree defaultSlots = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(SLOTS,"slots"));

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:660:3: (o= OPEN_TOKEN name chunkParent (s= slots )? CLOSE_TOKEN -> ^( CHUNK[$o,\"chunk\"] name chunkParent PARAMETERS ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:660:3: o= OPEN_TOKEN name chunkParent (s= slots )? CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_chunk1267); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            pushFollow(FOLLOW_name_in_chunk1269);
            name33=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name33.getTree());

            pushFollow(FOLLOW_chunkParent_in_chunk1271);
            chunkParent34=chunkParent();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_chunkParent.add(chunkParent34.getTree());

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:660:33: (s= slots )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==IDENTIFIER_TOKEN) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:660:34: s= slots
                    {
                    pushFollow(FOLLOW_slots_in_chunk1276);
                    s=slots();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_slots.add(s.getTree());

                    }
                    break;

            }


            CLOSE_TOKEN35=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_chunk1280); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN35);


            // AST REWRITE
            // elements: name, chunkParent
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 660:56: -> ^( CHUNK[$o,\"chunk\"] name chunkParent PARAMETERS )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:660:58: ^( CHUNK[$o,\"chunk\"] name chunkParent PARAMETERS )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CHUNK, o, "chunk")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, stream_chunkParent.nextTree());

                adaptor.addChild(root_1, ((s==null)?(defaultSlots):s.tree));

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(PARAMETERS, "PARAMETERS")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "chunk"


    public static class chunkType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "chunkType"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:665:1: chunkType : o= OPEN_TOKEN ( 'chunk-type' ) name ( chunkTypeParent )? (s= shortSlotDefs |l= longSlotDefs )? CLOSE_TOKEN -> ^( CHUNK_TYPE[$o,\"chunk-type\"] name ( chunkTypeParent )? PARAMETERS ) ;
    public final LispParser.chunkType_return chunkType() throws RecognitionException {
        LispParser.chunkType_return retval = new LispParser.chunkType_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token string_literal36=null;
        Token CLOSE_TOKEN39=null;
        LispParser.shortSlotDefs_return s =null;

        LispParser.longSlotDefs_return l =null;

        LispParser.name_return name37 =null;

        LispParser.chunkTypeParent_return chunkTypeParent38 =null;


        CommonTree o_tree=null;
        CommonTree string_literal36_tree=null;
        CommonTree CLOSE_TOKEN39_tree=null;
        RewriteRuleTokenStream stream_97=new RewriteRuleTokenStream(adaptor,"token 97");
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleSubtreeStream stream_shortSlotDefs=new RewriteRuleSubtreeStream(adaptor,"rule shortSlotDefs");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_chunkTypeParent=new RewriteRuleSubtreeStream(adaptor,"rule chunkTypeParent");
        RewriteRuleSubtreeStream stream_longSlotDefs=new RewriteRuleSubtreeStream(adaptor,"rule longSlotDefs");

         CommonTree chunks = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(CHUNKS,"chunks"));
         CommonTree defaultSlots = (CommonTree) adaptor.create(((CommonTreeAdaptor)adaptor).createToken(SLOTS,"slots"));

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:2: (o= OPEN_TOKEN ( 'chunk-type' ) name ( chunkTypeParent )? (s= shortSlotDefs |l= longSlotDefs )? CLOSE_TOKEN -> ^( CHUNK_TYPE[$o,\"chunk-type\"] name ( chunkTypeParent )? PARAMETERS ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:4: o= OPEN_TOKEN ( 'chunk-type' ) name ( chunkTypeParent )? (s= shortSlotDefs |l= longSlotDefs )? CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_chunkType1311); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:17: ( 'chunk-type' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:18: 'chunk-type'
            {
            string_literal36=(Token)match(input,97,FOLLOW_97_in_chunkType1314); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_97.add(string_literal36);


            }


            pushFollow(FOLLOW_name_in_chunkType1317);
            name37=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name37.getTree());

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:37: ( chunkTypeParent )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==OPEN_TOKEN) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==INCLUDE_TOKEN) ) {
                    alt13=1;
                }
            }
            switch (alt13) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:37: chunkTypeParent
                    {
                    pushFollow(FOLLOW_chunkTypeParent_in_chunkType1319);
                    chunkTypeParent38=chunkTypeParent();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_chunkTypeParent.add(chunkTypeParent38.getTree());

                    }
                    break;

            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:54: (s= shortSlotDefs |l= longSlotDefs )?
            int alt14=3;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==IDENTIFIER_TOKEN) ) {
                alt14=1;
            }
            else if ( (LA14_0==OPEN_TOKEN) ) {
                alt14=2;
            }
            switch (alt14) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:55: s= shortSlotDefs
                    {
                    pushFollow(FOLLOW_shortSlotDefs_in_chunkType1325);
                    s=shortSlotDefs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_shortSlotDefs.add(s.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:670:73: l= longSlotDefs
                    {
                    pushFollow(FOLLOW_longSlotDefs_in_chunkType1331);
                    l=longSlotDefs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_longSlotDefs.add(l.getTree());

                    }
                    break;

            }


            CLOSE_TOKEN39=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_chunkType1335); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN39);


            if ( state.backtracking==0 ) {
            	  //so that we can keep track of the chunktypes and their chunks wrapper
            	  String chunkTypeName = (name37!=null?input.toString(name37.start,name37.stop):null).toLowerCase();
            	  //this test is here so that we can pass the unit test w/ model
            	  if(ModelGlobals_stack.size()!=0)
             	    ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunksWrapperMap.put(chunkTypeName.toLowerCase(), chunks);
             	    
             	  //check the temporary chunktypes
             	  if(((ModelGlobals_scope)ModelGlobals_stack.peek()).temporaryChunkTypesMap.containsKey(chunkTypeName))
             	   {
             	    LOGGER.debug(chunkTypeName+" has been inserted as a temporary chunktype, stripping contents and disposing");
             	    CommonTree tmp = ((ModelGlobals_scope)ModelGlobals_stack.peek()).temporaryChunkTypesMap.get(chunkTypeName);
             	    Collection<CommonTree> tmpChunks = ASTSupport.getTrees(tmp, CHUNK);
             	    for(CommonTree tmpChunk : tmpChunks)
             	     {
             	       LOGGER.debug("Adding "+tmpChunk.toStringTree());
             	       chunks.addChild(tmpChunk);
             	     }
             	   }
             	   
             	   if(s!=null)
             	    defaultSlots = (CommonTree) (s!=null?((CommonTree)s.tree):null);
             	   else
             	   if(l!=null)
             	    defaultSlots = (CommonTree) (l!=null?((CommonTree)l.tree):null); 
            	 }

            // AST REWRITE
            // elements: name, chunkTypeParent
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 697:3: -> ^( CHUNK_TYPE[$o,\"chunk-type\"] name ( chunkTypeParent )? PARAMETERS )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:698:3: ^( CHUNK_TYPE[$o,\"chunk-type\"] name ( chunkTypeParent )? PARAMETERS )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CHUNK_TYPE, o, "chunk-type")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:698:38: ( chunkTypeParent )?
                if ( stream_chunkTypeParent.hasNext() ) {
                    adaptor.addChild(root_1, stream_chunkTypeParent.nextTree());

                }
                stream_chunkTypeParent.reset();

                adaptor.addChild(root_1, defaultSlots);

                adaptor.addChild(root_1, chunks);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(PARAMETERS, "PARAMETERS")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "chunkType"


    public static class production_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "production"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:1: production : o= OPEN_TOKEN ( 'p' | 'P' ) name conditions '==>' actions CLOSE_TOKEN -> ^( PRODUCTION[$o, \"production\"] name conditions actions PARAMETERS ) ;
    public final LispParser.production_return production() throws RecognitionException {
        LispParser.production_return retval = new LispParser.production_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token o=null;
        Token char_literal40=null;
        Token char_literal41=null;
        Token string_literal44=null;
        Token CLOSE_TOKEN46=null;
        LispParser.name_return name42 =null;

        LispParser.conditions_return conditions43 =null;

        LispParser.actions_return actions45 =null;


        CommonTree o_tree=null;
        CommonTree char_literal40_tree=null;
        CommonTree char_literal41_tree=null;
        CommonTree string_literal44_tree=null;
        CommonTree CLOSE_TOKEN46_tree=null;
        RewriteRuleTokenStream stream_94=new RewriteRuleTokenStream(adaptor,"token 94");
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_91=new RewriteRuleTokenStream(adaptor,"token 91");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleSubtreeStream stream_conditions=new RewriteRuleSubtreeStream(adaptor,"rule conditions");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_actions=new RewriteRuleSubtreeStream(adaptor,"rule actions");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:12: (o= OPEN_TOKEN ( 'p' | 'P' ) name conditions '==>' actions CLOSE_TOKEN -> ^( PRODUCTION[$o, \"production\"] name conditions actions PARAMETERS ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:19: o= OPEN_TOKEN ( 'p' | 'P' ) name conditions '==>' actions CLOSE_TOKEN
            {
            o=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_production1380); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(o);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:32: ( 'p' | 'P' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==105) ) {
                alt15=1;
            }
            else if ( (LA15_0==94) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:33: 'p'
                    {
                    char_literal40=(Token)match(input,105,FOLLOW_105_in_production1383); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_105.add(char_literal40);


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:39: 'P'
                    {
                    char_literal41=(Token)match(input,94,FOLLOW_94_in_production1387); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_94.add(char_literal41);


                    }
                    break;

            }


            pushFollow(FOLLOW_name_in_production1390);
            name42=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name42.getTree());

            pushFollow(FOLLOW_conditions_in_production1393);
            conditions43=conditions();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditions.add(conditions43.getTree());

            string_literal44=(Token)match(input,91,FOLLOW_91_in_production1395); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_91.add(string_literal44);


            pushFollow(FOLLOW_actions_in_production1397);
            actions45=actions();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_actions.add(actions45.getTree());

            CLOSE_TOKEN46=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_production1399); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN46);


            // AST REWRITE
            // elements: name, actions, conditions
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 700:87: -> ^( PRODUCTION[$o, \"production\"] name conditions actions PARAMETERS )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:700:90: ^( PRODUCTION[$o, \"production\"] name conditions actions PARAMETERS )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PRODUCTION, o, "production")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, stream_conditions.nextTree());

                adaptor.addChild(root_1, stream_actions.nextTree());

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(PARAMETERS, "PARAMETERS")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "production"


    public static class actions_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "actions"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:702:1: actions : ( rhs )+ -> ^( ACTIONS ( rhs )+ ) ;
    public final LispParser.actions_return actions() throws RecognitionException {
        LispParser.actions_return retval = new LispParser.actions_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.rhs_return rhs47 =null;


        RewriteRuleSubtreeStream stream_rhs=new RewriteRuleSubtreeStream(adaptor,"rule rhs");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:703:2: ( ( rhs )+ -> ^( ACTIONS ( rhs )+ ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:703:4: ( rhs )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:703:4: ( rhs )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==ADD_TOKEN||LA16_0==MATCH_TOKEN||LA16_0==REMOVE_TOKEN||(LA16_0 >= 87 && LA16_0 <= 90)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:703:4: rhs
            	    {
            	    pushFollow(FOLLOW_rhs_in_actions1424);
            	    rhs47=rhs();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rhs.add(rhs47.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            // AST REWRITE
            // elements: rhs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 703:9: -> ^( ACTIONS ( rhs )+ )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:703:12: ^( ACTIONS ( rhs )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ACTIONS, "ACTIONS")
                , root_1);

                if ( !(stream_rhs.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rhs.hasNext() ) {
                    adaptor.addChild(root_1, stream_rhs.nextTree());

                }
                stream_rhs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "actions"


    public static class rhs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rhs"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:1: rhs : ( addBuffer | modifyBuffer | removeBuffer | output | stop | evalAction | bind ) ;
    public final LispParser.rhs_return rhs() throws RecognitionException {
        LispParser.rhs_return retval = new LispParser.rhs_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.addBuffer_return addBuffer48 =null;

        LispParser.modifyBuffer_return modifyBuffer49 =null;

        LispParser.removeBuffer_return removeBuffer50 =null;

        LispParser.output_return output51 =null;

        LispParser.stop_return stop52 =null;

        LispParser.evalAction_return evalAction53 =null;

        LispParser.bind_return bind54 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:6: ( ( addBuffer | modifyBuffer | removeBuffer | output | stop | evalAction | bind ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:8: ( addBuffer | modifyBuffer | removeBuffer | output | stop | evalAction | bind )
            {
            root_0 = (CommonTree)adaptor.nil();


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:8: ( addBuffer | modifyBuffer | removeBuffer | output | stop | evalAction | bind )
            int alt17=7;
            switch ( input.LA(1) ) {
            case ADD_TOKEN:
                {
                alt17=1;
                }
                break;
            case MATCH_TOKEN:
                {
                alt17=2;
                }
                break;
            case REMOVE_TOKEN:
                {
                alt17=3;
                }
                break;
            case 89:
                {
                alt17=4;
                }
                break;
            case 88:
            case 90:
                {
                alt17=5;
                }
                break;
            case 87:
                {
                int LA17_6 = input.LA(2);

                if ( (LA17_6==93||LA17_6==100) ) {
                    alt17=6;
                }
                else if ( (LA17_6==92||LA17_6==96) ) {
                    alt17=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 6, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }

            switch (alt17) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:9: addBuffer
                    {
                    pushFollow(FOLLOW_addBuffer_in_rhs1445);
                    addBuffer48=addBuffer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, addBuffer48.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:21: modifyBuffer
                    {
                    pushFollow(FOLLOW_modifyBuffer_in_rhs1449);
                    modifyBuffer49=modifyBuffer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifyBuffer49.getTree());

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:36: removeBuffer
                    {
                    pushFollow(FOLLOW_removeBuffer_in_rhs1453);
                    removeBuffer50=removeBuffer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, removeBuffer50.getTree());

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:51: output
                    {
                    pushFollow(FOLLOW_output_in_rhs1457);
                    output51=output();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, output51.getTree());

                    }
                    break;
                case 5 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:60: stop
                    {
                    pushFollow(FOLLOW_stop_in_rhs1461);
                    stop52=stop();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stop52.getTree());

                    }
                    break;
                case 6 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:67: evalAction
                    {
                    pushFollow(FOLLOW_evalAction_in_rhs1465);
                    evalAction53=evalAction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, evalAction53.getTree());

                    }
                    break;
                case 7 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:705:80: bind
                    {
                    pushFollow(FOLLOW_bind_in_rhs1469);
                    bind54=bind();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bind54.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rhs"


    public static class conditions_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditions"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:713:1: conditions : (l= lhs )+ ->;
    public final LispParser.conditions_return conditions() throws RecognitionException {
        LispParser.conditions_return retval = new LispParser.conditions_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.lhs_return l =null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");

         CommonTree conditionsNode = (CommonTree) adaptor.create(CONDITIONS, "lhs");
         Collection<CommonTree> bufferSlots = new ArrayList<CommonTree>(2);

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:718:2: ( (l= lhs )+ ->)
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:718:6: (l= lhs )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:718:6: (l= lhs )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==MATCH_TOKEN||LA18_0==QUERY_TOKEN||LA18_0==87) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:718:7: l= lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_conditions1495);
            	    l=lhs();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_lhs.add(l.getTree());

            	    if ( state.backtracking==0 ) {
            	    	     //no matter what, add this as a child
            	    	     conditionsNode.addChild((l!=null?((CommonTree)l.tree):null));
            	    	     
            	    	     if((l!=null?((CommonTree)l.tree):null).getType()==MATCH_CONDITION)
            	    	     {
            	    	      //we may need to expand.. lets check the slots
            	    	      CommonTree slotsContainer = ASTSupport.getFirstDescendantWithType((l!=null?((CommonTree)l.tree):null), SLOTS);
            	    	      
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
            	    	       String bufferName = (l!=null?((CommonTree)l.tree):null).getChild(0).getText();
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

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 765:5: ->
            {
                adaptor.addChild(root_0, conditionsNode);

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditions"


    public static class lhs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "lhs"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:1: lhs : ( queryBuffer | checkBuffer | evalCondition | bind );
    public final LispParser.lhs_return lhs() throws RecognitionException {
        LispParser.lhs_return retval = new LispParser.lhs_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.queryBuffer_return queryBuffer55 =null;

        LispParser.checkBuffer_return checkBuffer56 =null;

        LispParser.evalCondition_return evalCondition57 =null;

        LispParser.bind_return bind58 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:6: ( queryBuffer | checkBuffer | evalCondition | bind )
            int alt19=4;
            switch ( input.LA(1) ) {
            case QUERY_TOKEN:
                {
                alt19=1;
                }
                break;
            case MATCH_TOKEN:
                {
                alt19=2;
                }
                break;
            case 87:
                {
                int LA19_3 = input.LA(2);

                if ( (LA19_3==93||LA19_3==100) ) {
                    alt19=3;
                }
                else if ( (LA19_3==92||LA19_3==96) ) {
                    alt19=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 3, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:8: queryBuffer
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_queryBuffer_in_lhs1522);
                    queryBuffer55=queryBuffer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, queryBuffer55.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:22: checkBuffer
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_checkBuffer_in_lhs1526);
                    checkBuffer56=checkBuffer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, checkBuffer56.getTree());

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:36: evalCondition
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_evalCondition_in_lhs1530);
                    evalCondition57=evalCondition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, evalCondition57.getTree());

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:767:52: bind
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_bind_in_lhs1534);
                    bind58=bind();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bind58.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "lhs"


    public static class checkBuffer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "checkBuffer"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:772:1: checkBuffer : n= MATCH_TOKEN b= bufferContent ( conditionalSlots )? -> {isProxy}? ^( PROXY_CONDITION[$n,\"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? ) -> ^( MATCH_CONDITION[$n,\"match\"] NAME[$n] bufferContent ( conditionalSlots )? ) ;
    public final LispParser.checkBuffer_return checkBuffer() throws RecognitionException {
        LispParser.checkBuffer_return retval = new LispParser.checkBuffer_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        LispParser.bufferContent_return b =null;

        LispParser.conditionalSlots_return conditionalSlots59 =null;


        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_MATCH_TOKEN=new RewriteRuleTokenStream(adaptor,"token MATCH_TOKEN");
        RewriteRuleSubtreeStream stream_bufferContent=new RewriteRuleSubtreeStream(adaptor,"rule bufferContent");
        RewriteRuleSubtreeStream stream_conditionalSlots=new RewriteRuleSubtreeStream(adaptor,"rule conditionalSlots");

         boolean isProxy = false;
         String className = null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:777:2: (n= MATCH_TOKEN b= bufferContent ( conditionalSlots )? -> {isProxy}? ^( PROXY_CONDITION[$n,\"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? ) -> ^( MATCH_CONDITION[$n,\"match\"] NAME[$n] bufferContent ( conditionalSlots )? ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:779:11: n= MATCH_TOKEN b= bufferContent ( conditionalSlots )?
            {
            n=(Token)match(input,MATCH_TOKEN,FOLLOW_MATCH_TOKEN_in_checkBuffer1564); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MATCH_TOKEN.add(n);


            if ( state.backtracking==0 ) {isProxy="proxy".equalsIgnoreCase((n!=null?n.getText():null));}

            pushFollow(FOLLOW_bufferContent_in_checkBuffer1581);
            b=bufferContent();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_bufferContent.add(b.getTree());

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:780:28: ( conditionalSlots )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0 >= GTE_TOKEN && LA20_0 <= IDENTIFIER_TOKEN)||(LA20_0 >= LTE_TOKEN && LA20_0 <= LT_TOKEN)||LA20_0==MINUS_TOKEN||LA20_0==WITHIN_TOKEN) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:780:28: conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_checkBuffer1583);
                    conditionalSlots59=conditionalSlots();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_conditionalSlots.add(conditionalSlots59.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: bufferContent, conditionalSlots, conditionalSlots
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 781:10: -> {isProxy}? ^( PROXY_CONDITION[$n,\"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? )
            if (isProxy) {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:781:24: ^( PROXY_CONDITION[$n,\"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROXY_CONDITION, n, "proxy")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(CLASS_SPEC, ((CommonTree)b.tree).token)
                );

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:781:93: ( conditionalSlots )?
                if ( stream_conditionalSlots.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditionalSlots.nextTree());

                }
                stream_conditionalSlots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 782:10: -> ^( MATCH_CONDITION[$n,\"match\"] NAME[$n] bufferContent ( conditionalSlots )? )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:782:13: ^( MATCH_CONDITION[$n,\"match\"] NAME[$n] bufferContent ( conditionalSlots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(MATCH_CONDITION, n, "match")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                adaptor.addChild(root_1, stream_bufferContent.nextTree());

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:782:66: ( conditionalSlots )?
                if ( stream_conditionalSlots.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditionalSlots.nextTree());

                }
                stream_conditionalSlots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "checkBuffer"


    public static class addBuffer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "addBuffer"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:787:1: addBuffer : n= ADD_TOKEN (b= bufferContent ) ( conditionalSlots )? -> {isProxy}? ^( PROXY_ACTION[$n, \"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? ) -> ^( ADD_ACTION[$n,\"add\"] NAME[$n] bufferContent ( conditionalSlots )? ) ;
    public final LispParser.addBuffer_return addBuffer() throws RecognitionException {
        LispParser.addBuffer_return retval = new LispParser.addBuffer_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        LispParser.bufferContent_return b =null;

        LispParser.conditionalSlots_return conditionalSlots60 =null;


        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_ADD_TOKEN=new RewriteRuleTokenStream(adaptor,"token ADD_TOKEN");
        RewriteRuleSubtreeStream stream_bufferContent=new RewriteRuleSubtreeStream(adaptor,"rule bufferContent");
        RewriteRuleSubtreeStream stream_conditionalSlots=new RewriteRuleSubtreeStream(adaptor,"rule conditionalSlots");

         boolean isProxy = false;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:791:2: (n= ADD_TOKEN (b= bufferContent ) ( conditionalSlots )? -> {isProxy}? ^( PROXY_ACTION[$n, \"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? ) -> ^( ADD_ACTION[$n,\"add\"] NAME[$n] bufferContent ( conditionalSlots )? ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:793:11: n= ADD_TOKEN (b= bufferContent ) ( conditionalSlots )?
            {
            n=(Token)match(input,ADD_TOKEN,FOLLOW_ADD_TOKEN_in_addBuffer1665); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ADD_TOKEN.add(n);


            if ( state.backtracking==0 ) {isProxy="proxy".equalsIgnoreCase((n!=null?n.getText():null));}

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:794:12: (b= bufferContent )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:794:13: b= bufferContent
            {
            pushFollow(FOLLOW_bufferContent_in_addBuffer1683);
            b=bufferContent();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_bufferContent.add(b.getTree());

            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:794:30: ( conditionalSlots )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0 >= GTE_TOKEN && LA21_0 <= IDENTIFIER_TOKEN)||(LA21_0 >= LTE_TOKEN && LA21_0 <= LT_TOKEN)||LA21_0==MINUS_TOKEN||LA21_0==WITHIN_TOKEN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:794:30: conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_addBuffer1686);
                    conditionalSlots60=conditionalSlots();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_conditionalSlots.add(conditionalSlots60.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: conditionalSlots, bufferContent, conditionalSlots
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 795:11: -> {isProxy}? ^( PROXY_ACTION[$n, \"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? )
            if (isProxy) {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:795:25: ^( PROXY_ACTION[$n, \"proxy\"] CLASS_SPEC[((CommonTree)b.tree).token] ( conditionalSlots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROXY_ACTION, n, "proxy")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(CLASS_SPEC, ((CommonTree)b.tree).token)
                );

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:795:92: ( conditionalSlots )?
                if ( stream_conditionalSlots.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditionalSlots.nextTree());

                }
                stream_conditionalSlots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 796:11: -> ^( ADD_ACTION[$n,\"add\"] NAME[$n] bufferContent ( conditionalSlots )? )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:796:14: ^( ADD_ACTION[$n,\"add\"] NAME[$n] bufferContent ( conditionalSlots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ADD_ACTION, n, "add")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                adaptor.addChild(root_1, stream_bufferContent.nextTree());

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:796:60: ( conditionalSlots )?
                if ( stream_conditionalSlots.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditionalSlots.nextTree());

                }
                stream_conditionalSlots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "addBuffer"


    public static class modifyBuffer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "modifyBuffer"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:803:1: modifyBuffer options {backtrack=true; } : n= MATCH_TOKEN ( ( ( var | identifier ) ) | slots )? -> {isOverwrite}? ^( ADD_ACTION[$n,\"add\"] NAME[$n] ( var )? ( identifier )? ) -> ^( MODIFY_ACTION[$n,\"modify\"] NAME[$n] ( slots )? ) ;
    public final LispParser.modifyBuffer_return modifyBuffer() throws RecognitionException {
        LispParser.modifyBuffer_return retval = new LispParser.modifyBuffer_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        LispParser.var_return var61 =null;

        LispParser.identifier_return identifier62 =null;

        LispParser.slots_return slots63 =null;


        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_MATCH_TOKEN=new RewriteRuleTokenStream(adaptor,"token MATCH_TOKEN");
        RewriteRuleSubtreeStream stream_var=new RewriteRuleSubtreeStream(adaptor,"rule var");
        RewriteRuleSubtreeStream stream_slots=new RewriteRuleSubtreeStream(adaptor,"rule slots");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        boolean isOverwrite=false;
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:806:2: (n= MATCH_TOKEN ( ( ( var | identifier ) ) | slots )? -> {isOverwrite}? ^( ADD_ACTION[$n,\"add\"] NAME[$n] ( var )? ( identifier )? ) -> ^( MODIFY_ACTION[$n,\"modify\"] NAME[$n] ( slots )? ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:807:11: n= MATCH_TOKEN ( ( ( var | identifier ) ) | slots )?
            {
            n=(Token)match(input,MATCH_TOKEN,FOLLOW_MATCH_TOKEN_in_modifyBuffer1775); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MATCH_TOKEN.add(n);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:808:11: ( ( ( var | identifier ) ) | slots )?
            int alt23=3;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==VARIABLE_TOKEN) ) {
                alt23=1;
            }
            else if ( (LA23_0==IDENTIFIER_TOKEN) ) {
                int LA23_2 = input.LA(2);

                if ( ((LA23_2 >= ADD_TOKEN && LA23_2 <= CLOSE_TOKEN)||LA23_2==MATCH_TOKEN||LA23_2==REMOVE_TOKEN||(LA23_2 >= 87 && LA23_2 <= 90)) ) {
                    alt23=1;
                }
                else if ( (LA23_2==IDENTIFIER_TOKEN||LA23_2==NUMBER_TOKEN||(LA23_2 >= STRING_TOKEN && LA23_2 <= VARIABLE_TOKEN)) ) {
                    alt23=2;
                }
            }
            switch (alt23) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:13: ( ( var | identifier ) )
                    {
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:13: ( ( var | identifier ) )
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:14: ( var | identifier )
                    {
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:14: ( var | identifier )
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==VARIABLE_TOKEN) ) {
                        alt22=1;
                    }
                    else if ( (LA22_0==IDENTIFIER_TOKEN) ) {
                        alt22=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 22, 0, input);

                        throw nvae;

                    }
                    switch (alt22) {
                        case 1 :
                            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:15: var
                            {
                            pushFollow(FOLLOW_var_in_modifyBuffer1803);
                            var61=var();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_var.add(var61.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:809:21: identifier
                            {
                            pushFollow(FOLLOW_identifier_in_modifyBuffer1807);
                            identifier62=identifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_identifier.add(identifier62.getTree());

                            }
                            break;

                    }


                    if ( state.backtracking==0 ) {isOverwrite=true;}

                    }


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:810:15: slots
                    {
                    pushFollow(FOLLOW_slots_in_modifyBuffer1827);
                    slots63=slots();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_slots.add(slots63.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: slots, var, identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 812:11: -> {isOverwrite}? ^( ADD_ACTION[$n,\"add\"] NAME[$n] ( var )? ( identifier )? )
            if (isOverwrite) {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:812:29: ^( ADD_ACTION[$n,\"add\"] NAME[$n] ( var )? ( identifier )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ADD_ACTION, n, "add")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:812:61: ( var )?
                if ( stream_var.hasNext() ) {
                    adaptor.addChild(root_1, stream_var.nextTree());

                }
                stream_var.reset();

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:812:66: ( identifier )?
                if ( stream_identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_identifier.nextTree());

                }
                stream_identifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 813:11: -> ^( MODIFY_ACTION[$n,\"modify\"] NAME[$n] ( slots )? )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:813:14: ^( MODIFY_ACTION[$n,\"modify\"] NAME[$n] ( slots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(MODIFY_ACTION, n, "modify")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:813:52: ( slots )?
                if ( stream_slots.hasNext() ) {
                    adaptor.addChild(root_1, stream_slots.nextTree());

                }
                stream_slots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving

                if(isOverwrite)
                 reportException(new CompilationWarning("Overwrites not fully support, overwritten chunk will be encoded", retval.tree));
              
        }
        return retval;
    }
    // $ANTLR end "modifyBuffer"


    public static class queryBuffer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "queryBuffer"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:820:1: queryBuffer : n= QUERY_TOKEN ( conditionalSlots )? -> ^( QUERY_CONDITION[$n,\"query\"] NAME[$n] ( conditionalSlots )? ) ;
    public final LispParser.queryBuffer_return queryBuffer() throws RecognitionException {
        LispParser.queryBuffer_return retval = new LispParser.queryBuffer_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        LispParser.conditionalSlots_return conditionalSlots64 =null;


        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_QUERY_TOKEN=new RewriteRuleTokenStream(adaptor,"token QUERY_TOKEN");
        RewriteRuleSubtreeStream stream_conditionalSlots=new RewriteRuleSubtreeStream(adaptor,"rule conditionalSlots");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:821:2: (n= QUERY_TOKEN ( conditionalSlots )? -> ^( QUERY_CONDITION[$n,\"query\"] NAME[$n] ( conditionalSlots )? ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:822:10: n= QUERY_TOKEN ( conditionalSlots )?
            {
            n=(Token)match(input,QUERY_TOKEN,FOLLOW_QUERY_TOKEN_in_queryBuffer1932); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_QUERY_TOKEN.add(n);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:823:10: ( conditionalSlots )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0 >= GTE_TOKEN && LA24_0 <= IDENTIFIER_TOKEN)||(LA24_0 >= LTE_TOKEN && LA24_0 <= LT_TOKEN)||LA24_0==MINUS_TOKEN||LA24_0==WITHIN_TOKEN) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:823:10: conditionalSlots
                    {
                    pushFollow(FOLLOW_conditionalSlots_in_queryBuffer1943);
                    conditionalSlots64=conditionalSlots();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_conditionalSlots.add(conditionalSlots64.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: conditionalSlots
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 823:28: -> ^( QUERY_CONDITION[$n,\"query\"] NAME[$n] ( conditionalSlots )? )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:823:31: ^( QUERY_CONDITION[$n,\"query\"] NAME[$n] ( conditionalSlots )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(QUERY_CONDITION, n, "query")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:823:70: ( conditionalSlots )?
                if ( stream_conditionalSlots.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditionalSlots.nextTree());

                }
                stream_conditionalSlots.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "queryBuffer"


    public static class removeBuffer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "removeBuffer"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:825:1: removeBuffer : n= REMOVE_TOKEN -> ^( REMOVE_ACTION[$n,\"remove\"] NAME[$n] ) ;
    public final LispParser.removeBuffer_return removeBuffer() throws RecognitionException {
        LispParser.removeBuffer_return retval = new LispParser.removeBuffer_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;

        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_REMOVE_TOKEN=new RewriteRuleTokenStream(adaptor,"token REMOVE_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:826:2: (n= REMOVE_TOKEN -> ^( REMOVE_ACTION[$n,\"remove\"] NAME[$n] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:826:38: n= REMOVE_TOKEN
            {
            n=(Token)match(input,REMOVE_TOKEN,FOLLOW_REMOVE_TOKEN_in_removeBuffer1973); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_REMOVE_TOKEN.add(n);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 826:78: -> ^( REMOVE_ACTION[$n,\"remove\"] NAME[$n] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:826:80: ^( REMOVE_ACTION[$n,\"remove\"] NAME[$n] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(REMOVE_ACTION, n, "remove")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(NAME, n)
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "removeBuffer"


    public static class slotCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "slotCondition"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:829:1: slotCondition : ( GT_TOKEN -> GT | GTE_TOKEN -> GTE | LT_TOKEN -> LT | LTE_TOKEN -> LTE | MINUS_TOKEN -> NOT | WITHIN_TOKEN -> WITHIN );
    public final LispParser.slotCondition_return slotCondition() throws RecognitionException {
        LispParser.slotCondition_return retval = new LispParser.slotCondition_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token GT_TOKEN65=null;
        Token GTE_TOKEN66=null;
        Token LT_TOKEN67=null;
        Token LTE_TOKEN68=null;
        Token MINUS_TOKEN69=null;
        Token WITHIN_TOKEN70=null;

        CommonTree GT_TOKEN65_tree=null;
        CommonTree GTE_TOKEN66_tree=null;
        CommonTree LT_TOKEN67_tree=null;
        CommonTree LTE_TOKEN68_tree=null;
        CommonTree MINUS_TOKEN69_tree=null;
        CommonTree WITHIN_TOKEN70_tree=null;
        RewriteRuleTokenStream stream_LT_TOKEN=new RewriteRuleTokenStream(adaptor,"token LT_TOKEN");
        RewriteRuleTokenStream stream_LTE_TOKEN=new RewriteRuleTokenStream(adaptor,"token LTE_TOKEN");
        RewriteRuleTokenStream stream_MINUS_TOKEN=new RewriteRuleTokenStream(adaptor,"token MINUS_TOKEN");
        RewriteRuleTokenStream stream_WITHIN_TOKEN=new RewriteRuleTokenStream(adaptor,"token WITHIN_TOKEN");
        RewriteRuleTokenStream stream_GTE_TOKEN=new RewriteRuleTokenStream(adaptor,"token GTE_TOKEN");
        RewriteRuleTokenStream stream_GT_TOKEN=new RewriteRuleTokenStream(adaptor,"token GT_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:830:2: ( GT_TOKEN -> GT | GTE_TOKEN -> GTE | LT_TOKEN -> LT | LTE_TOKEN -> LTE | MINUS_TOKEN -> NOT | WITHIN_TOKEN -> WITHIN )
            int alt25=6;
            switch ( input.LA(1) ) {
            case GT_TOKEN:
                {
                alt25=1;
                }
                break;
            case GTE_TOKEN:
                {
                alt25=2;
                }
                break;
            case LT_TOKEN:
                {
                alt25=3;
                }
                break;
            case LTE_TOKEN:
                {
                alt25=4;
                }
                break;
            case MINUS_TOKEN:
                {
                alt25=5;
                }
                break;
            case WITHIN_TOKEN:
                {
                alt25=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;

            }

            switch (alt25) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:830:7: GT_TOKEN
                    {
                    GT_TOKEN65=(Token)match(input,GT_TOKEN,FOLLOW_GT_TOKEN_in_slotCondition2002); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GT_TOKEN.add(GT_TOKEN65);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 830:16: -> GT
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(GT, "GT")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:831:3: GTE_TOKEN
                    {
                    GTE_TOKEN66=(Token)match(input,GTE_TOKEN,FOLLOW_GTE_TOKEN_in_slotCondition2012); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GTE_TOKEN.add(GTE_TOKEN66);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 831:13: -> GTE
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(GTE, "GTE")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:832:3: LT_TOKEN
                    {
                    LT_TOKEN67=(Token)match(input,LT_TOKEN,FOLLOW_LT_TOKEN_in_slotCondition2022); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LT_TOKEN.add(LT_TOKEN67);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 832:12: -> LT
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(LT, "LT")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:833:3: LTE_TOKEN
                    {
                    LTE_TOKEN68=(Token)match(input,LTE_TOKEN,FOLLOW_LTE_TOKEN_in_slotCondition2032); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LTE_TOKEN.add(LTE_TOKEN68);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 833:13: -> LTE
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(LTE, "LTE")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 5 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:834:3: MINUS_TOKEN
                    {
                    MINUS_TOKEN69=(Token)match(input,MINUS_TOKEN,FOLLOW_MINUS_TOKEN_in_slotCondition2042); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS_TOKEN.add(MINUS_TOKEN69);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 834:15: -> NOT
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(NOT, "NOT")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 6 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:835:3: WITHIN_TOKEN
                    {
                    WITHIN_TOKEN70=(Token)match(input,WITHIN_TOKEN,FOLLOW_WITHIN_TOKEN_in_slotCondition2052); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WITHIN_TOKEN.add(WITHIN_TOKEN70);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 835:16: -> WITHIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(WITHIN, "WITHIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "slotCondition"


    public static class bufferContent_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "bufferContent"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:846:1: bufferContent : ( chunkIdentifier | isaType | var | string ) ;
    public final LispParser.bufferContent_return bufferContent() throws RecognitionException {
        LispParser.bufferContent_return retval = new LispParser.bufferContent_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.chunkIdentifier_return chunkIdentifier71 =null;

        LispParser.isaType_return isaType72 =null;

        LispParser.var_return var73 =null;

        LispParser.string_return string74 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:2: ( ( chunkIdentifier | isaType | var | string ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:4: ( chunkIdentifier | isaType | var | string )
            {
            root_0 = (CommonTree)adaptor.nil();


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:4: ( chunkIdentifier | isaType | var | string )
            int alt26=4;
            switch ( input.LA(1) ) {
            case IDENTIFIER_TOKEN:
                {
                alt26=1;
                }
                break;
            case ISA_TOKEN:
                {
                alt26=2;
                }
                break;
            case VARIABLE_TOKEN:
                {
                alt26=3;
                }
                break;
            case STRING_TOKEN:
                {
                alt26=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }

            switch (alt26) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:5: chunkIdentifier
                    {
                    pushFollow(FOLLOW_chunkIdentifier_in_bufferContent2069);
                    chunkIdentifier71=chunkIdentifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, chunkIdentifier71.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:23: isaType
                    {
                    pushFollow(FOLLOW_isaType_in_bufferContent2073);
                    isaType72=isaType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, isaType72.getTree());

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:33: var
                    {
                    pushFollow(FOLLOW_var_in_bufferContent2077);
                    var73=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var73.getTree());

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:847:39: string
                    {
                    pushFollow(FOLLOW_string_in_bufferContent2081);
                    string74=string();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string74.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "bufferContent"


    public static class slotValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "slotValue"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:1: slotValue : ( number | identifier | var | string ) ;
    public final LispParser.slotValue_return slotValue() throws RecognitionException {
        LispParser.slotValue_return retval = new LispParser.slotValue_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.number_return number75 =null;

        LispParser.identifier_return identifier76 =null;

        LispParser.var_return var77 =null;

        LispParser.string_return string78 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:12: ( ( number | identifier | var | string ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:14: ( number | identifier | var | string )
            {
            root_0 = (CommonTree)adaptor.nil();


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:14: ( number | identifier | var | string )
            int alt27=4;
            switch ( input.LA(1) ) {
            case NUMBER_TOKEN:
                {
                alt27=1;
                }
                break;
            case IDENTIFIER_TOKEN:
                {
                alt27=2;
                }
                break;
            case VARIABLE_TOKEN:
                {
                alt27=3;
                }
                break;
            case STRING_TOKEN:
                {
                alt27=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }

            switch (alt27) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:15: number
                    {
                    pushFollow(FOLLOW_number_in_slotValue2092);
                    number75=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, number75.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:24: identifier
                    {
                    pushFollow(FOLLOW_identifier_in_slotValue2096);
                    identifier76=identifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, identifier76.getTree());

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:37: var
                    {
                    pushFollow(FOLLOW_var_in_slotValue2100);
                    var77=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var77.getTree());

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:849:43: string
                    {
                    pushFollow(FOLLOW_string_in_slotValue2104);
                    string78=string();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string78.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "slotValue"


    public static class conditionalSlot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalSlot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:851:1: conditionalSlot : slotCondition name slotValue -> ^( SLOT[\"slot\"] name slotCondition slotValue ) ;
    public final LispParser.conditionalSlot_return conditionalSlot() throws RecognitionException {
        LispParser.conditionalSlot_return retval = new LispParser.conditionalSlot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.slotCondition_return slotCondition79 =null;

        LispParser.name_return name80 =null;

        LispParser.slotValue_return slotValue81 =null;


        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");
        RewriteRuleSubtreeStream stream_slotCondition=new RewriteRuleSubtreeStream(adaptor,"rule slotCondition");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:851:17: ( slotCondition name slotValue -> ^( SLOT[\"slot\"] name slotCondition slotValue ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:851:19: slotCondition name slotValue
            {
            pushFollow(FOLLOW_slotCondition_in_conditionalSlot2113);
            slotCondition79=slotCondition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slotCondition.add(slotCondition79.getTree());

            pushFollow(FOLLOW_name_in_conditionalSlot2115);
            name80=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name80.getTree());

            pushFollow(FOLLOW_slotValue_in_conditionalSlot2118);
            slotValue81=slotValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slotValue.add(slotValue81.getTree());

            // AST REWRITE
            // elements: slotCondition, slotValue, name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 851:50: -> ^( SLOT[\"slot\"] name slotCondition slotValue )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:851:53: ^( SLOT[\"slot\"] name slotCondition slotValue )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOT, "slot")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, stream_slotCondition.nextTree());

                adaptor.addChild(root_1, stream_slotValue.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditionalSlot"


    public static class equalSlot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "equalSlot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:853:1: equalSlot : name slotValue -> ^( SLOT[\"slot\"] name EQUALS slotValue ) ;
    public final LispParser.equalSlot_return equalSlot() throws RecognitionException {
        LispParser.equalSlot_return retval = new LispParser.equalSlot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.name_return name82 =null;

        LispParser.slotValue_return slotValue83 =null;


        RewriteRuleSubtreeStream stream_slotValue=new RewriteRuleSubtreeStream(adaptor,"rule slotValue");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:853:12: ( name slotValue -> ^( SLOT[\"slot\"] name EQUALS slotValue ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:853:14: name slotValue
            {
            pushFollow(FOLLOW_name_in_equalSlot2142);
            name82=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name82.getTree());

            pushFollow(FOLLOW_slotValue_in_equalSlot2144);
            slotValue83=slotValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slotValue.add(slotValue83.getTree());

            // AST REWRITE
            // elements: name, slotValue
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 853:30: -> ^( SLOT[\"slot\"] name EQUALS slotValue )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:853:33: ^( SLOT[\"slot\"] name EQUALS slotValue )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOT, "slot")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(EQUALS, "EQUALS")
                );

                adaptor.addChild(root_1, stream_slotValue.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "equalSlot"


    public static class slot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "slot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:855:1: slot : equalSlot ;
    public final LispParser.slot_return slot() throws RecognitionException {
        LispParser.slot_return retval = new LispParser.slot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.equalSlot_return equalSlot84 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:855:7: ( equalSlot )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:855:9: equalSlot
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_equalSlot_in_slot2167);
            equalSlot84=equalSlot();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalSlot84.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "slot"


    public static class cSlot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cSlot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:857:1: cSlot : ( slot | conditionalSlot );
    public final LispParser.cSlot_return cSlot() throws RecognitionException {
        LispParser.cSlot_return retval = new LispParser.cSlot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.slot_return slot85 =null;

        LispParser.conditionalSlot_return conditionalSlot86 =null;



        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:857:8: ( slot | conditionalSlot )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==IDENTIFIER_TOKEN) ) {
                alt28=1;
            }
            else if ( ((LA28_0 >= GTE_TOKEN && LA28_0 <= GT_TOKEN)||(LA28_0 >= LTE_TOKEN && LA28_0 <= LT_TOKEN)||LA28_0==MINUS_TOKEN||LA28_0==WITHIN_TOKEN) ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;

            }
            switch (alt28) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:857:11: slot
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_slot_in_cSlot2177);
                    slot85=slot();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, slot85.getTree());

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:857:18: conditionalSlot
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_conditionalSlot_in_cSlot2181);
                    conditionalSlot86=conditionalSlot();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalSlot86.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cSlot"


    public static class conditionalSlots_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalSlots"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:858:1: conditionalSlots : ( cSlot )+ -> ^( SLOTS[\"slots\"] ( cSlot )+ ) ;
    public final LispParser.conditionalSlots_return conditionalSlots() throws RecognitionException {
        LispParser.conditionalSlots_return retval = new LispParser.conditionalSlots_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.cSlot_return cSlot87 =null;


        RewriteRuleSubtreeStream stream_cSlot=new RewriteRuleSubtreeStream(adaptor,"rule cSlot");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:859:2: ( ( cSlot )+ -> ^( SLOTS[\"slots\"] ( cSlot )+ ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:859:4: ( cSlot )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:859:4: ( cSlot )+
            int cnt29=0;
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0 >= GTE_TOKEN && LA29_0 <= IDENTIFIER_TOKEN)||(LA29_0 >= LTE_TOKEN && LA29_0 <= LT_TOKEN)||LA29_0==MINUS_TOKEN||LA29_0==WITHIN_TOKEN) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:859:5: cSlot
            	    {
            	    pushFollow(FOLLOW_cSlot_in_conditionalSlots2191);
            	    cSlot87=cSlot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_cSlot.add(cSlot87.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt29 >= 1 ) break loop29;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(29, input);
                        throw eee;
                }
                cnt29++;
            } while (true);


            // AST REWRITE
            // elements: cSlot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 859:13: -> ^( SLOTS[\"slots\"] ( cSlot )+ )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:859:16: ^( SLOTS[\"slots\"] ( cSlot )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOTS, "slots")
                , root_1);

                if ( !(stream_cSlot.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_cSlot.hasNext() ) {
                    adaptor.addChild(root_1, stream_cSlot.nextTree());

                }
                stream_cSlot.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditionalSlots"


    public static class slots_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "slots"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:1: slots : ( slot )+ -> ^( SLOTS[\"slots\"] ( slot )+ ) ;
    public final LispParser.slots_return slots() throws RecognitionException {
        LispParser.slots_return retval = new LispParser.slots_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.slot_return slot88 =null;


        RewriteRuleSubtreeStream stream_slot=new RewriteRuleSubtreeStream(adaptor,"rule slot");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:8: ( ( slot )+ -> ^( SLOTS[\"slots\"] ( slot )+ ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:10: ( slot )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:10: ( slot )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==IDENTIFIER_TOKEN) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:11: slot
            	    {
            	    pushFollow(FOLLOW_slot_in_slots2220);
            	    slot88=slot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_slot.add(slot88.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            // AST REWRITE
            // elements: slot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 861:19: -> ^( SLOTS[\"slots\"] ( slot )+ )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:861:22: ^( SLOTS[\"slots\"] ( slot )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOTS, "slots")
                , root_1);

                if ( !(stream_slot.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_slot.hasNext() ) {
                    adaptor.addChild(root_1, stream_slot.nextTree());

                }
                stream_slot.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "slots"


    public static class shortSlotDefs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shortSlotDefs"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:863:1: shortSlotDefs : ( shortSlot )+ -> ^( SLOTS[\"slots\"] ( shortSlot )* ) ;
    public final LispParser.shortSlotDefs_return shortSlotDefs() throws RecognitionException {
        LispParser.shortSlotDefs_return retval = new LispParser.shortSlotDefs_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.shortSlot_return shortSlot89 =null;


        RewriteRuleSubtreeStream stream_shortSlot=new RewriteRuleSubtreeStream(adaptor,"rule shortSlot");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:2: ( ( shortSlot )+ -> ^( SLOTS[\"slots\"] ( shortSlot )* ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:4: ( shortSlot )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:4: ( shortSlot )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==IDENTIFIER_TOKEN) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:5: shortSlot
            	    {
            	    pushFollow(FOLLOW_shortSlot_in_shortSlotDefs2248);
            	    shortSlot89=shortSlot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_shortSlot.add(shortSlot89.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);


            // AST REWRITE
            // elements: shortSlot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 864:17: -> ^( SLOTS[\"slots\"] ( shortSlot )* )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:20: ^( SLOTS[\"slots\"] ( shortSlot )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOTS, "slots")
                , root_1);

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:864:37: ( shortSlot )*
                while ( stream_shortSlot.hasNext() ) {
                    adaptor.addChild(root_1, stream_shortSlot.nextTree());

                }
                stream_shortSlot.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "shortSlotDefs"


    public static class shortSlot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shortSlot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:866:1: shortSlot : name -> ^( SLOT[\"slot\"] name EQUALS IDENTIFIER[\"null\"] ) ;
    public final LispParser.shortSlot_return shortSlot() throws RecognitionException {
        LispParser.shortSlot_return retval = new LispParser.shortSlot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.name_return name90 =null;


        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:866:11: ( name -> ^( SLOT[\"slot\"] name EQUALS IDENTIFIER[\"null\"] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:866:13: name
            {
            pushFollow(FOLLOW_name_in_shortSlot2268);
            name90=name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_name.add(name90.getTree());

            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 866:18: -> ^( SLOT[\"slot\"] name EQUALS IDENTIFIER[\"null\"] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:866:21: ^( SLOT[\"slot\"] name EQUALS IDENTIFIER[\"null\"] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOT, "slot")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(EQUALS, "EQUALS")
                );

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(IDENTIFIER, "null")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "shortSlot"


    public static class longSlot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "longSlot"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:868:1: longSlot : OPEN_TOKEN ! equalSlot CLOSE_TOKEN !;
    public final LispParser.longSlot_return longSlot() throws RecognitionException {
        LispParser.longSlot_return retval = new LispParser.longSlot_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OPEN_TOKEN91=null;
        Token CLOSE_TOKEN93=null;
        LispParser.equalSlot_return equalSlot92 =null;


        CommonTree OPEN_TOKEN91_tree=null;
        CommonTree CLOSE_TOKEN93_tree=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:869:2: ( OPEN_TOKEN ! equalSlot CLOSE_TOKEN !)
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:869:4: OPEN_TOKEN ! equalSlot CLOSE_TOKEN !
            {
            root_0 = (CommonTree)adaptor.nil();


            OPEN_TOKEN91=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_longSlot2292); if (state.failed) return retval;

            pushFollow(FOLLOW_equalSlot_in_longSlot2295);
            equalSlot92=equalSlot();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalSlot92.getTree());

            CLOSE_TOKEN93=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_longSlot2297); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "longSlot"


    public static class longSlotDefs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "longSlotDefs"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:871:1: longSlotDefs : ( longSlot )+ -> ^( SLOTS[\"slots\"] ( longSlot )* ) ;
    public final LispParser.longSlotDefs_return longSlotDefs() throws RecognitionException {
        LispParser.longSlotDefs_return retval = new LispParser.longSlotDefs_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        LispParser.longSlot_return longSlot94 =null;


        RewriteRuleSubtreeStream stream_longSlot=new RewriteRuleSubtreeStream(adaptor,"rule longSlot");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:2: ( ( longSlot )+ -> ^( SLOTS[\"slots\"] ( longSlot )* ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:4: ( longSlot )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:4: ( longSlot )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==OPEN_TOKEN) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:5: longSlot
            	    {
            	    pushFollow(FOLLOW_longSlot_in_longSlotDefs2309);
            	    longSlot94=longSlot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_longSlot.add(longSlot94.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
            } while (true);


            // AST REWRITE
            // elements: longSlot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 872:16: -> ^( SLOTS[\"slots\"] ( longSlot )* )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:20: ^( SLOTS[\"slots\"] ( longSlot )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SLOTS, "slots")
                , root_1);

                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:872:37: ( longSlot )*
                while ( stream_longSlot.hasNext() ) {
                    adaptor.addChild(root_1, stream_longSlot.nextTree());

                }
                stream_longSlot.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "longSlotDefs"


    public static class output_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "output"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:876:1: output : '!output!' list= unknownList -> ^( OUTPUT_ACTION ) ;
    public final LispParser.output_return output() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.output_return retval = new LispParser.output_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token string_literal95=null;
        LispParser.unknownList_return list =null;


        CommonTree string_literal95_tree=null;
        RewriteRuleTokenStream stream_89=new RewriteRuleTokenStream(adaptor,"token 89");
        RewriteRuleSubtreeStream stream_unknownList=new RewriteRuleSubtreeStream(adaptor,"rule unknownList");

         ((Suppress_scope)Suppress_stack.peek()).warnings =true;
         CommonTree string=null;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:881:3: ( '!output!' list= unknownList -> ^( OUTPUT_ACTION ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:881:5: '!output!' list= unknownList
            {
            string_literal95=(Token)match(input,89,FOLLOW_89_in_output2342); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_89.add(string_literal95);


            pushFollow(FOLLOW_unknownList_in_output2347);
            list=unknownList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unknownList.add(list.getTree());

            if ( state.backtracking==0 ) {
             String str = (list!=null?((CommonTree)list.tree):null).toStringTree();
             str = str.replace("(","");
             str = str.replace(")", "");
             string = (CommonTree) adaptor.create(STRING, (list!=null?((CommonTree)list.tree):null).getToken(), str.trim());
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 888:2: -> ^( OUTPUT_ACTION )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:888:5: ^( OUTPUT_ACTION )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(OUTPUT_ACTION, "OUTPUT_ACTION")
                , root_1);

                adaptor.addChild(root_1, string);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "output"


    public static class bind_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "bind"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:1: bind : '!' ( 'bind' | 'BIND' ) '!' list= unknownList -> ^( UNKNOWN ) ;
    public final LispParser.bind_return bind() throws RecognitionException {
        LispParser.bind_return retval = new LispParser.bind_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token char_literal96=null;
        Token string_literal97=null;
        Token string_literal98=null;
        Token char_literal99=null;
        LispParser.unknownList_return list =null;


        CommonTree char_literal96_tree=null;
        CommonTree string_literal97_tree=null;
        CommonTree string_literal98_tree=null;
        CommonTree char_literal99_tree=null;
        RewriteRuleTokenStream stream_96=new RewriteRuleTokenStream(adaptor,"token 96");
        RewriteRuleTokenStream stream_92=new RewriteRuleTokenStream(adaptor,"token 92");
        RewriteRuleTokenStream stream_87=new RewriteRuleTokenStream(adaptor,"token 87");
        RewriteRuleSubtreeStream stream_unknownList=new RewriteRuleSubtreeStream(adaptor,"rule unknownList");
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:7: ( '!' ( 'bind' | 'BIND' ) '!' list= unknownList -> ^( UNKNOWN ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:9: '!' ( 'bind' | 'BIND' ) '!' list= unknownList
            {
            char_literal96=(Token)match(input,87,FOLLOW_87_in_bind2369); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal96);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:13: ( 'bind' | 'BIND' )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==96) ) {
                alt33=1;
            }
            else if ( (LA33_0==92) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }
            switch (alt33) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:14: 'bind'
                    {
                    string_literal97=(Token)match(input,96,FOLLOW_96_in_bind2372); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_96.add(string_literal97);


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:891:21: 'BIND'
                    {
                    string_literal98=(Token)match(input,92,FOLLOW_92_in_bind2374); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_92.add(string_literal98);


                    }
                    break;

            }


            char_literal99=(Token)match(input,87,FOLLOW_87_in_bind2376); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal99);


            pushFollow(FOLLOW_unknownList_in_bind2380);
            list=unknownList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unknownList.add(list.getTree());

            if ( state.backtracking==0 ) {
             CompilationError error = new CompilationError("Bind is not currently supported in lisp", (list!=null?((CommonTree)list.tree):null));
             error.setEndNode(ASTSupport.getLastDescendant((list!=null?((CommonTree)list.tree):null)));
             reportException(error);
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 897:14: -> ^( UNKNOWN )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:897:4: ^( UNKNOWN )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(UNKNOWN, "UNKNOWN")
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "bind"


    public static class evalCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "evalCondition"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:903:1: evalCondition : '!' ( 'eval' | 'EVAL' ) '!' list= unknownList -> {failed}? ^( UNKNOWN ) -> ^( SCRIPTABLE_CONDITION[\"scriptableCondition\"] ) ;
    public final LispParser.evalCondition_return evalCondition() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.evalCondition_return retval = new LispParser.evalCondition_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token char_literal100=null;
        Token string_literal101=null;
        Token string_literal102=null;
        Token char_literal103=null;
        LispParser.unknownList_return list =null;


        CommonTree char_literal100_tree=null;
        CommonTree string_literal101_tree=null;
        CommonTree string_literal102_tree=null;
        CommonTree char_literal103_tree=null;
        RewriteRuleTokenStream stream_93=new RewriteRuleTokenStream(adaptor,"token 93");
        RewriteRuleTokenStream stream_87=new RewriteRuleTokenStream(adaptor,"token 87");
        RewriteRuleTokenStream stream_100=new RewriteRuleTokenStream(adaptor,"token 100");
        RewriteRuleSubtreeStream stream_unknownList=new RewriteRuleSubtreeStream(adaptor,"rule unknownList");

        	  ((Suppress_scope)Suppress_stack.peek()).warnings = true;
        	  StringBuilder script = new StringBuilder();
        	  CommonTree langNode = null; 
        	  CommonTree scriptNode = null;
        	  boolean failed = false;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:912:2: ( '!' ( 'eval' | 'EVAL' ) '!' list= unknownList -> {failed}? ^( UNKNOWN ) -> ^( SCRIPTABLE_CONDITION[\"scriptableCondition\"] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:912:4: '!' ( 'eval' | 'EVAL' ) '!' list= unknownList
            {
            char_literal100=(Token)match(input,87,FOLLOW_87_in_evalCondition2409); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal100);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:912:7: ( 'eval' | 'EVAL' )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==100) ) {
                alt34=1;
            }
            else if ( (LA34_0==93) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }
            switch (alt34) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:912:8: 'eval'
                    {
                    string_literal101=(Token)match(input,100,FOLLOW_100_in_evalCondition2411); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_100.add(string_literal101);


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:912:15: 'EVAL'
                    {
                    string_literal102=(Token)match(input,93,FOLLOW_93_in_evalCondition2413); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_93.add(string_literal102);


                    }
                    break;

            }


            char_literal103=(Token)match(input,87,FOLLOW_87_in_evalCondition2415); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal103);


            pushFollow(FOLLOW_unknownList_in_evalCondition2419);
            list=unknownList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unknownList.add(list.getTree());

            if ( state.backtracking==0 ) {
            	  Token firstScriptToken = null;
            	  for(int i=0;i<(list!=null?((CommonTree)list.tree):null).getChildCount();i++)
            	   {
            	    CommonTree child = (CommonTree) (list!=null?((CommonTree)list.tree):null).getChild(i);
            	    if(child.getType()!=STRING)
            	     {
            	      CompilationError error = new CompilationError("!eval!s only support all string contents currently", (list!=null?((CommonTree)list.tree):null));
            	      error.setEndNode(ASTSupport.getLastDescendant((list!=null?((CommonTree)list.tree):null)));
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

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 945:2: -> {failed}? ^( UNKNOWN )
            if (failed) {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:945:15: ^( UNKNOWN )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(UNKNOWN, "UNKNOWN")
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 946:2: -> ^( SCRIPTABLE_CONDITION[\"scriptableCondition\"] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:946:5: ^( SCRIPTABLE_CONDITION[\"scriptableCondition\"] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SCRIPTABLE_CONDITION, "scriptableCondition")
                , root_1);

                adaptor.addChild(root_1, langNode);

                adaptor.addChild(root_1, scriptNode);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "evalCondition"


    public static class evalAction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "evalAction"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:952:1: evalAction : '!' ( 'eval' | 'EVAL' ) '!' list= unknownList -> {failed}? ^( UNKNOWN ) -> ^( SCRIPTABLE_ACTION[\"scriptableAction\"] ) ;
    public final LispParser.evalAction_return evalAction() throws RecognitionException {
        Suppress_stack.push(new Suppress_scope());

        LispParser.evalAction_return retval = new LispParser.evalAction_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token char_literal104=null;
        Token string_literal105=null;
        Token string_literal106=null;
        Token char_literal107=null;
        LispParser.unknownList_return list =null;


        CommonTree char_literal104_tree=null;
        CommonTree string_literal105_tree=null;
        CommonTree string_literal106_tree=null;
        CommonTree char_literal107_tree=null;
        RewriteRuleTokenStream stream_93=new RewriteRuleTokenStream(adaptor,"token 93");
        RewriteRuleTokenStream stream_87=new RewriteRuleTokenStream(adaptor,"token 87");
        RewriteRuleTokenStream stream_100=new RewriteRuleTokenStream(adaptor,"token 100");
        RewriteRuleSubtreeStream stream_unknownList=new RewriteRuleSubtreeStream(adaptor,"rule unknownList");

        	  ((Suppress_scope)Suppress_stack.peek()).warnings = true;
        	  StringBuilder script = new StringBuilder();
        	  CommonTree langNode = null; 
        	  CommonTree scriptNode = null;
        	  boolean failed = false;

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:961:2: ( '!' ( 'eval' | 'EVAL' ) '!' list= unknownList -> {failed}? ^( UNKNOWN ) -> ^( SCRIPTABLE_ACTION[\"scriptableAction\"] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:961:4: '!' ( 'eval' | 'EVAL' ) '!' list= unknownList
            {
            char_literal104=(Token)match(input,87,FOLLOW_87_in_evalAction2467); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal104);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:961:7: ( 'eval' | 'EVAL' )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==100) ) {
                alt35=1;
            }
            else if ( (LA35_0==93) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }
            switch (alt35) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:961:8: 'eval'
                    {
                    string_literal105=(Token)match(input,100,FOLLOW_100_in_evalAction2469); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_100.add(string_literal105);


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:961:15: 'EVAL'
                    {
                    string_literal106=(Token)match(input,93,FOLLOW_93_in_evalAction2471); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_93.add(string_literal106);


                    }
                    break;

            }


            char_literal107=(Token)match(input,87,FOLLOW_87_in_evalAction2473); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_87.add(char_literal107);


            pushFollow(FOLLOW_unknownList_in_evalAction2477);
            list=unknownList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unknownList.add(list.getTree());

            if ( state.backtracking==0 ) {
            	  Token firstScriptToken = null;
            	  for(int i=0;i<(list!=null?((CommonTree)list.tree):null).getChildCount();i++)
            	   {
            	    CommonTree child = (CommonTree) (list!=null?((CommonTree)list.tree):null).getChild(i);
            	    if(child.getType()!=STRING)
            	     {
            	      CompilationError error = new CompilationError("!eval!s only support all string contents currently", (list!=null?((CommonTree)list.tree):null));
            	      error.setEndNode(ASTSupport.getLastDescendant((list!=null?((CommonTree)list.tree):null)));
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

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 994:2: -> {failed}? ^( UNKNOWN )
            if (failed) {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:994:15: ^( UNKNOWN )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(UNKNOWN, "UNKNOWN")
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 995:2: -> ^( SCRIPTABLE_ACTION[\"scriptableAction\"] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:995:5: ^( SCRIPTABLE_ACTION[\"scriptableAction\"] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(SCRIPTABLE_ACTION, "scriptableAction")
                , root_1);

                adaptor.addChild(root_1, langNode);

                adaptor.addChild(root_1, scriptNode);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            Suppress_stack.pop();

        }
        return retval;
    }
    // $ANTLR end "evalAction"


    public static class stop_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stop"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:1: stop : ( '!stop!' | '!STOP!' ) -> ^( PROXY_ACTION[\"proxyAction\"] CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] ) ;
    public final LispParser.stop_return stop() throws RecognitionException {
        LispParser.stop_return retval = new LispParser.stop_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token string_literal108=null;
        Token string_literal109=null;

        CommonTree string_literal108_tree=null;
        CommonTree string_literal109_tree=null;
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleTokenStream stream_88=new RewriteRuleTokenStream(adaptor,"token 88");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:7: ( ( '!stop!' | '!STOP!' ) -> ^( PROXY_ACTION[\"proxyAction\"] CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:9: ( '!stop!' | '!STOP!' )
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:9: ( '!stop!' | '!STOP!' )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==90) ) {
                alt36=1;
            }
            else if ( (LA36_0==88) ) {
                alt36=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;

            }
            switch (alt36) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:10: '!stop!'
                    {
                    string_literal108=(Token)match(input,90,FOLLOW_90_in_stop2520); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal108);


                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:19: '!STOP!'
                    {
                    string_literal109=(Token)match(input,88,FOLLOW_88_in_stop2522); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_88.add(string_literal109);


                    }
                    break;

            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1000:29: -> ^( PROXY_ACTION[\"proxyAction\"] CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1000:32: ^( PROXY_ACTION[\"proxyAction\"] CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROXY_ACTION, "proxyAction")
                , root_1);

                adaptor.addChild(root_1, 
                (CommonTree)adaptor.create(CLASS_SPEC, "org.jactr.core.production.action.StopAction")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stop"


    public static class chunkParent_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "chunkParent"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1002:1: chunkParent : ISA_TOKEN n= IDENTIFIER_TOKEN -> ^( PARENT[$n] ) ;
    public final LispParser.chunkParent_return chunkParent() throws RecognitionException {
        LispParser.chunkParent_return retval = new LispParser.chunkParent_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        Token ISA_TOKEN110=null;

        CommonTree n_tree=null;
        CommonTree ISA_TOKEN110_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");
        RewriteRuleTokenStream stream_ISA_TOKEN=new RewriteRuleTokenStream(adaptor,"token ISA_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1002:14: ( ISA_TOKEN n= IDENTIFIER_TOKEN -> ^( PARENT[$n] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1002:16: ISA_TOKEN n= IDENTIFIER_TOKEN
            {
            ISA_TOKEN110=(Token)match(input,ISA_TOKEN,FOLLOW_ISA_TOKEN_in_chunkParent2542); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ISA_TOKEN.add(ISA_TOKEN110);


            n=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_chunkParent2546); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(n);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1002:45: -> ^( PARENT[$n] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1002:48: ^( PARENT[$n] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PARENT, n)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "chunkParent"


    public static class name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "name"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1004:1: name : n= IDENTIFIER_TOKEN -> ^( NAME[$n] ) ;
    public final LispParser.name_return name() throws RecognitionException {
        LispParser.name_return retval = new LispParser.name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;

        CommonTree n_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1004:7: (n= IDENTIFIER_TOKEN -> ^( NAME[$n] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1004:9: n= IDENTIFIER_TOKEN
            {
            n=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_name2564); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(n);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1004:28: -> ^( NAME[$n] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1004:31: ^( NAME[$n] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(NAME, n)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "name"


    public static class chunkTypeParent_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "chunkTypeParent"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1006:1: chunkTypeParent : OPEN_TOKEN INCLUDE_TOKEN n= IDENTIFIER_TOKEN CLOSE_TOKEN -> ^( PARENT[$n] ) ;
    public final LispParser.chunkTypeParent_return chunkTypeParent() throws RecognitionException {
        LispParser.chunkTypeParent_return retval = new LispParser.chunkTypeParent_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token n=null;
        Token OPEN_TOKEN111=null;
        Token INCLUDE_TOKEN112=null;
        Token CLOSE_TOKEN113=null;

        CommonTree n_tree=null;
        CommonTree OPEN_TOKEN111_tree=null;
        CommonTree INCLUDE_TOKEN112_tree=null;
        CommonTree CLOSE_TOKEN113_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");
        RewriteRuleTokenStream stream_OPEN_TOKEN=new RewriteRuleTokenStream(adaptor,"token OPEN_TOKEN");
        RewriteRuleTokenStream stream_CLOSE_TOKEN=new RewriteRuleTokenStream(adaptor,"token CLOSE_TOKEN");
        RewriteRuleTokenStream stream_INCLUDE_TOKEN=new RewriteRuleTokenStream(adaptor,"token INCLUDE_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1007:2: ( OPEN_TOKEN INCLUDE_TOKEN n= IDENTIFIER_TOKEN CLOSE_TOKEN -> ^( PARENT[$n] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1007:4: OPEN_TOKEN INCLUDE_TOKEN n= IDENTIFIER_TOKEN CLOSE_TOKEN
            {
            OPEN_TOKEN111=(Token)match(input,OPEN_TOKEN,FOLLOW_OPEN_TOKEN_in_chunkTypeParent2581); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_TOKEN.add(OPEN_TOKEN111);


            INCLUDE_TOKEN112=(Token)match(input,INCLUDE_TOKEN,FOLLOW_INCLUDE_TOKEN_in_chunkTypeParent2583); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INCLUDE_TOKEN.add(INCLUDE_TOKEN112);


            n=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_chunkTypeParent2587); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(n);


            CLOSE_TOKEN113=(Token)match(input,CLOSE_TOKEN,FOLLOW_CLOSE_TOKEN_in_chunkTypeParent2589); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_TOKEN.add(CLOSE_TOKEN113);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1007:60: -> ^( PARENT[$n] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1007:63: ^( PARENT[$n] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PARENT, n)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "chunkTypeParent"


    public static class isaType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "isaType"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1009:1: isaType : ISA_TOKEN id= IDENTIFIER_TOKEN -> ^( CHUNK_TYPE_IDENTIFIER[$id] ) ;
    public final LispParser.isaType_return isaType() throws RecognitionException {
        LispParser.isaType_return retval = new LispParser.isaType_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token id=null;
        Token ISA_TOKEN114=null;

        CommonTree id_tree=null;
        CommonTree ISA_TOKEN114_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");
        RewriteRuleTokenStream stream_ISA_TOKEN=new RewriteRuleTokenStream(adaptor,"token ISA_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1009:9: ( ISA_TOKEN id= IDENTIFIER_TOKEN -> ^( CHUNK_TYPE_IDENTIFIER[$id] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1009:11: ISA_TOKEN id= IDENTIFIER_TOKEN
            {
            ISA_TOKEN114=(Token)match(input,ISA_TOKEN,FOLLOW_ISA_TOKEN_in_isaType2604); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ISA_TOKEN.add(ISA_TOKEN114);


            id=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_isaType2608); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1009:42: -> ^( CHUNK_TYPE_IDENTIFIER[$id] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1009:45: ^( CHUNK_TYPE_IDENTIFIER[$id] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CHUNK_TYPE_IDENTIFIER, id)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "isaType"


    public static class var_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1011:1: var : v= VARIABLE_TOKEN -> ^( VARIABLE[$v] ) ;
    public final LispParser.var_return var() throws RecognitionException {
        LispParser.var_return retval = new LispParser.var_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token v=null;

        CommonTree v_tree=null;
        RewriteRuleTokenStream stream_VARIABLE_TOKEN=new RewriteRuleTokenStream(adaptor,"token VARIABLE_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1011:7: (v= VARIABLE_TOKEN -> ^( VARIABLE[$v] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1011:9: v= VARIABLE_TOKEN
            {
            v=(Token)match(input,VARIABLE_TOKEN,FOLLOW_VARIABLE_TOKEN_in_var2630); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_VARIABLE_TOKEN.add(v);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1011:26: -> ^( VARIABLE[$v] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1011:29: ^( VARIABLE[$v] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VARIABLE, v)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var"


    public static class identifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "identifier"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1013:1: identifier : id= IDENTIFIER_TOKEN -> ^( IDENTIFIER[$id] ) ;
    public final LispParser.identifier_return identifier() throws RecognitionException {
        LispParser.identifier_return retval = new LispParser.identifier_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token id=null;

        CommonTree id_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1014:2: (id= IDENTIFIER_TOKEN -> ^( IDENTIFIER[$id] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1014:4: id= IDENTIFIER_TOKEN
            {
            id=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_identifier2649); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1014:25: -> ^( IDENTIFIER[$id] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1014:28: ^( IDENTIFIER[$id] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(IDENTIFIER, id)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "identifier"


    public static class chunkIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "chunkIdentifier"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1016:1: chunkIdentifier : id= IDENTIFIER_TOKEN -> ^( CHUNK_IDENTIFIER[$id] ) ;
    public final LispParser.chunkIdentifier_return chunkIdentifier() throws RecognitionException {
        LispParser.chunkIdentifier_return retval = new LispParser.chunkIdentifier_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token id=null;

        CommonTree id_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER_TOKEN=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1017:2: (id= IDENTIFIER_TOKEN -> ^( CHUNK_IDENTIFIER[$id] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1017:4: id= IDENTIFIER_TOKEN
            {
            id=(Token)match(input,IDENTIFIER_TOKEN,FOLLOW_IDENTIFIER_TOKEN_in_chunkIdentifier2670); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER_TOKEN.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1017:25: -> ^( CHUNK_IDENTIFIER[$id] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1017:28: ^( CHUNK_IDENTIFIER[$id] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CHUNK_IDENTIFIER, id)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "chunkIdentifier"


    public static class number_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "number"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1019:1: number : num= NUMBER_TOKEN -> ^( NUMBER[$num] ) ;
    public final LispParser.number_return number() throws RecognitionException {
        LispParser.number_return retval = new LispParser.number_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token num=null;

        CommonTree num_tree=null;
        RewriteRuleTokenStream stream_NUMBER_TOKEN=new RewriteRuleTokenStream(adaptor,"token NUMBER_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1019:9: (num= NUMBER_TOKEN -> ^( NUMBER[$num] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1019:11: num= NUMBER_TOKEN
            {
            num=(Token)match(input,NUMBER_TOKEN,FOLLOW_NUMBER_TOKEN_in_number2692); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_NUMBER_TOKEN.add(num);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1019:29: -> ^( NUMBER[$num] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1019:32: ^( NUMBER[$num] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(NUMBER, num)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "number"


    public static class string_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "string"
    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1021:1: string : str= STRING_TOKEN -> ^( STRING[$str] ) ;
    public final LispParser.string_return string() throws RecognitionException {
        LispParser.string_return retval = new LispParser.string_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token str=null;

        CommonTree str_tree=null;
        RewriteRuleTokenStream stream_STRING_TOKEN=new RewriteRuleTokenStream(adaptor,"token STRING_TOKEN");

        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1021:9: (str= STRING_TOKEN -> ^( STRING[$str] ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1021:11: str= STRING_TOKEN
            {
            str=(Token)match(input,STRING_TOKEN,FOLLOW_STRING_TOKEN_in_string2712); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STRING_TOKEN.add(str);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1021:29: -> ^( STRING[$str] )
            {
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1021:32: ^( STRING[$str] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(STRING, str)
                , root_1);

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "string"

    // Delegated rules


 

    public static final BitSet FOLLOW_clearAll_in_model355 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_model380 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_model382 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_model384 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_module_in_model423 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_extension_in_model464 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_importDirective_in_model486 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_chunkType_in_model535 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_production_in_model555 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_addDm_in_model573 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_sdp_in_model586 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_sgp_in_model602 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_spp_in_model618 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_goalFocus_in_model631 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_model654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_module690 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_module692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_string_in_module696 = new BitSet(new long[]{0x0800000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_NO_IMPORT_TOKEN_in_module701 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_module705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_extension736 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_extension738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_string_in_extension742 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_extension744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_importDirective774 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_103_in_importDirective777 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_string_in_importDirective781 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_importDirective783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_clearAll800 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_clearAll802 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_clearAll804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_goalFocus816 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_goalFocus818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_chunkIdentifier_in_goalFocus822 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_goalFocus826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_sgp852 = new BitSet(new long[]{0x0000000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_sgp855 = new BitSet(new long[]{0x0000000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_slotValue_in_sgp862 = new BitSet(new long[]{0x0800000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_sgp916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_sdp949 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_sdp952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_slotValue_in_sdp959 = new BitSet(new long[]{0x0800000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_sdp1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_spp1047 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_108_in_spp1050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_slotValue_in_spp1057 = new BitSet(new long[]{0x0800000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_spp1112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_unknownList1142 = new BitSet(new long[]{0x0000000000000000L,0x0000000000181802L});
    public static final BitSet FOLLOW_slotValue_in_unknownList1147 = new BitSet(new long[]{0x0800000000000000L,0x0000000000181802L});
    public static final BitSet FOLLOW_unknownList_in_unknownList1177 = new BitSet(new long[]{0x0800000000000000L,0x0000000000181802L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_unknownList1208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_addDm1234 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_95_in_addDm1236 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_chunk_in_addDm1241 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_addDm1247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_chunk1267 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_chunk1269 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_chunkParent_in_chunk1271 = new BitSet(new long[]{0x0800000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_slots_in_chunk1276 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_chunk1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_chunkType1311 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_97_in_chunkType1314 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_chunkType1317 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001002L});
    public static final BitSet FOLLOW_chunkTypeParent_in_chunkType1319 = new BitSet(new long[]{0x0800000000000000L,0x0000000000001002L});
    public static final BitSet FOLLOW_shortSlotDefs_in_chunkType1325 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_longSlotDefs_in_chunkType1331 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_chunkType1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_production1380 = new BitSet(new long[]{0x0000000000000000L,0x0000020040000000L});
    public static final BitSet FOLLOW_105_in_production1383 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_production1387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_production1390 = new BitSet(new long[]{0x0000000000000000L,0x0000000000808080L});
    public static final BitSet FOLLOW_conditions_in_production1393 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_91_in_production1395 = new BitSet(new long[]{0x0400000000000000L,0x0000000007820080L});
    public static final BitSet FOLLOW_actions_in_production1397 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_production1399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rhs_in_actions1424 = new BitSet(new long[]{0x0400000000000002L,0x0000000007820080L});
    public static final BitSet FOLLOW_addBuffer_in_rhs1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifyBuffer_in_rhs1449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_removeBuffer_in_rhs1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_output_in_rhs1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stop_in_rhs1461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_evalAction_in_rhs1465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_in_rhs1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_conditions1495 = new BitSet(new long[]{0x0000000000000002L,0x0000000000808080L});
    public static final BitSet FOLLOW_queryBuffer_in_lhs1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_checkBuffer_in_lhs1526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_evalCondition_in_lhs1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_in_lhs1534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCH_TOKEN_in_checkBuffer1564 = new BitSet(new long[]{0x0000000000000000L,0x000000000018000AL});
    public static final BitSet FOLLOW_bufferContent_in_checkBuffer1581 = new BitSet(new long[]{0x8000000000000002L,0x0000000000200163L});
    public static final BitSet FOLLOW_conditionalSlots_in_checkBuffer1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ADD_TOKEN_in_addBuffer1665 = new BitSet(new long[]{0x0000000000000000L,0x000000000018000AL});
    public static final BitSet FOLLOW_bufferContent_in_addBuffer1683 = new BitSet(new long[]{0x8000000000000002L,0x0000000000200163L});
    public static final BitSet FOLLOW_conditionalSlots_in_addBuffer1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCH_TOKEN_in_modifyBuffer1775 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100002L});
    public static final BitSet FOLLOW_var_in_modifyBuffer1803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_modifyBuffer1807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slots_in_modifyBuffer1827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_TOKEN_in_queryBuffer1932 = new BitSet(new long[]{0x8000000000000002L,0x0000000000200163L});
    public static final BitSet FOLLOW_conditionalSlots_in_queryBuffer1943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REMOVE_TOKEN_in_removeBuffer1973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_TOKEN_in_slotCondition2002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GTE_TOKEN_in_slotCondition2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_TOKEN_in_slotCondition2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LTE_TOKEN_in_slotCondition2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_TOKEN_in_slotCondition2042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WITHIN_TOKEN_in_slotCondition2052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_chunkIdentifier_in_bufferContent2069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_isaType_in_bufferContent2073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_bufferContent2077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_bufferContent2081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_slotValue2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_slotValue2096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_slotValue2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_slotValue2104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slotCondition_in_conditionalSlot2113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_conditionalSlot2115 = new BitSet(new long[]{0x0000000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_slotValue_in_conditionalSlot2118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_equalSlot2142 = new BitSet(new long[]{0x0000000000000000L,0x0000000000180802L});
    public static final BitSet FOLLOW_slotValue_in_equalSlot2144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalSlot_in_slot2167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slot_in_cSlot2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalSlot_in_cSlot2181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cSlot_in_conditionalSlots2191 = new BitSet(new long[]{0x8000000000000002L,0x0000000000200163L});
    public static final BitSet FOLLOW_slot_in_slots2220 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_shortSlot_in_shortSlotDefs2248 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_shortSlot2268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_longSlot2292 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_equalSlot_in_longSlot2295 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_longSlot2297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_longSlot_in_longSlotDefs2309 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_89_in_output2342 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_unknownList_in_output2347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_bind2369 = new BitSet(new long[]{0x0000000000000000L,0x0000000110000000L});
    public static final BitSet FOLLOW_96_in_bind2372 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_92_in_bind2374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_87_in_bind2376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_unknownList_in_bind2380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_evalCondition2409 = new BitSet(new long[]{0x0000000000000000L,0x0000001020000000L});
    public static final BitSet FOLLOW_100_in_evalCondition2411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_93_in_evalCondition2413 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_87_in_evalCondition2415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_unknownList_in_evalCondition2419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_evalAction2467 = new BitSet(new long[]{0x0000000000000000L,0x0000001020000000L});
    public static final BitSet FOLLOW_100_in_evalAction2469 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_93_in_evalAction2471 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_87_in_evalAction2473 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_unknownList_in_evalAction2477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_stop2520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_stop2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISA_TOKEN_in_chunkParent2542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_chunkParent2546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_name2564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_TOKEN_in_chunkTypeParent2581 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INCLUDE_TOKEN_in_chunkTypeParent2583 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_chunkTypeParent2587 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_CLOSE_TOKEN_in_chunkTypeParent2589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISA_TOKEN_in_isaType2604 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_isaType2608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIABLE_TOKEN_in_var2630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_identifier2649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_TOKEN_in_chunkIdentifier2670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_TOKEN_in_number2692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_TOKEN_in_string2712 = new BitSet(new long[]{0x0000000000000002L});

}