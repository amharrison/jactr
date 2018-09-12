// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g 2013-01-08 11:23:57

package org.jactr.io.antlr3.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.ProxyAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.action.SetAction;
import org.jactr.core.production.condition.ChunkCondition;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.ProxyCondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.condition.VariableCondition;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultLogicalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptingManager;
import org.jactr.scripting.action.IActionScript;
import org.jactr.scripting.action.ScriptableAction;
import org.jactr.scripting.condition.IConditionScript;
import org.jactr.scripting.condition.ScriptableCondition;

public class JACTRBuilder extends TreeParser {
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
    public static final int EQUALS=45;
    public static final int LIBRARY=5;
    public static final int NOT=46;
    public static final int CHUNK_TYPE=11;
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

    // delegates
    // delegators

    protected static class Model_scope {
        IModel model;
        Map<String, IChunk> knownChunks;
        Map<String, IChunkType> knownChunkTypes;
        Map<IChunk, Collection<CommonTree>> chunkParameters;
        Map<IProduction, Collection<CommonTree>> productionParameters;
        Collection<IExtension> extensions;
        Collection<String> sourceChunks;
        CommonTree modelDescriptor;
    }
    protected Stack Model_stack = new Stack();


        public JACTRBuilder(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public JACTRBuilder(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return JACTRBuilder.tokenNames; }
    public String getGrammarFileName() { return "/Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g"; }


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
     if(e instanceof BuilderWarning)
      _warnings.add(e);
     else
     {
       LOGGER.error(e.getMessage(), e);
      _errors.add(e);
     }
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
          List<ISlot> children = FastListFactory.newInstance();
          ((ILogicalSlot)slot).getSlots(children);
          for(ISlot s : children)
           cleanupSlot(s);
           
          FastListFactory.recycle(children);
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
        	((Model_scope)Model_stack.peek()).model = model;
        	((Model_scope)Model_stack.peek()).productionParameters = new HashMap<IProduction, Collection<CommonTree>>();
        	//if(((Model_scope)Model_stack.peek()).knownChunks == null) ((Model_scope)Model_stack.peek()).knownChunks = new HashMap<String, IChunk>();
     	//if(((Model_scope)Model_stack.peek()).knownChunkTypes == null)((Model_scope)Model_stack.peek()).knownChunkTypes = new HashMap<String, IChunkType>();
        }




    // $ANTLR start "model"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:354:1: model returns [IModel model] : ^(m= MODEL name= NAME modules extensions buffers library p= parameters ) ;
    public final IModel model() throws RecognitionException {
        Model_stack.push(new Model_scope());

        IModel model = null;

        CommonTree m=null;
        CommonTree name=null;
        Collection<CommonTree> p = null;


         
         model = null;
         ((Model_scope)Model_stack.peek()).knownChunks = new HashMap<String, IChunk>();
         ((Model_scope)Model_stack.peek()).knownChunkTypes = new HashMap<String, IChunkType>();
         ((Model_scope)Model_stack.peek()).chunkParameters = new HashMap<IChunk, Collection<CommonTree>>();
         ((Model_scope)Model_stack.peek()).productionParameters = new HashMap<IProduction, Collection<CommonTree>>();
         ((Model_scope)Model_stack.peek()).extensions = new ArrayList<IExtension>();
         ((Model_scope)Model_stack.peek()).sourceChunks = new ArrayList<String>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:365:1: ( ^(m= MODEL name= NAME modules extensions buffers library p= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:365:3: ^(m= MODEL name= NAME modules extensions buffers library p= parameters )
            {
            m=(CommonTree)match(input,MODEL,FOLLOW_MODEL_in_model330); 

            match(input, Token.DOWN, null); 
            name=(CommonTree)match(input,NAME,FOLLOW_NAME_in_model334); 

             if(LOGGER.isDebugEnabled())
              LOGGER.debug("got model def for "+(name!=null?name.getText():null));
              
              String modelName = (name!=null?name.getText():null);
              model = new BasicModel(modelName);
              
              ((Model_scope)Model_stack.peek()).model = model;
              ((Model_scope)Model_stack.peek()).modelDescriptor = (CommonTree) m;

            pushFollow(FOLLOW_modules_in_model346);
            modules();

            state._fsp--;

            pushFollow(FOLLOW_extensions_in_model348);
            extensions();

            state._fsp--;

            pushFollow(FOLLOW_buffers_in_model350);
            buffers();

            state._fsp--;

            pushFollow(FOLLOW_library_in_model352);
            library();

            state._fsp--;

            pushFollow(FOLLOW_parameters_in_model356);
            p=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             /*
              we apply model parameters, then the other parameters
             */
             applyParameters(model, p);
             
             //now we can resolve the parameters for the chunks 
              for(IChunk chunk : ((Model_scope)Model_stack.peek()).chunkParameters.keySet())
               applyParameters(chunk.getSubsymbolicChunk(), ((Model_scope)Model_stack.peek()).chunkParameters.get(chunk)); 

            //we allow the chunktype parameters to be set with the chunktype creation
            // since it won't likely depend upon the model's parameters..

            // and the production parameters
              for(IProduction production : ((Model_scope)Model_stack.peek()).productionParameters.keySet())
               applyParameters(production.getSubsymbolicProduction(), ((Model_scope)Model_stack.peek()).productionParameters.get(production));
                	  
             /**
              we need to insert the chunks into the buffers
             */
             Map<String, CommonTree> bufferTrees = ASTSupport.getMapOfTrees(m, BUFFER);
             for(String bufferName : bufferTrees.keySet())
             {
              LOGGER.debug("Checking "+bufferName+" for source chunks to insert");
              
              IActivationBuffer buffer = ((Model_scope)Model_stack.peek()).model.getActivationBuffer(bufferName);
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
                chunk = ((Model_scope)Model_stack.peek()).knownChunks.get(chunkName.toLowerCase());
                 
                if(chunk==null)
                 throw new BuilderError(chunkName+" is not a known chunk", chunkIdentifier);
                 
                buffer.addSourceChunk(chunk);
               }
             }
             
             /*
              install the extensions..
             */
             for(IExtension extension : ((Model_scope)Model_stack.peek()).extensions)
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            Model_stack.pop();

        }
        return model;
    }
    // $ANTLR end "model"


    // $ANTLR start "library"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:447:1: library : ^( LIBRARY declarativeMemory proceduralMemory ) ;
    public final void library() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:448:1: ( ^( LIBRARY declarativeMemory proceduralMemory ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:448:3: ^( LIBRARY declarativeMemory proceduralMemory )
            {
            match(input,LIBRARY,FOLLOW_LIBRARY_in_library370); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_declarativeMemory_in_library372);
            declarativeMemory();

            state._fsp--;

            pushFollow(FOLLOW_proceduralMemory_in_library374);
            proceduralMemory();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "library"


    // $ANTLR start "declarativeMemory"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:450:1: declarativeMemory : ( ^( DECLARATIVE_MEMORY ( chunkType )+ ) | DECLARATIVE_MEMORY ) ;
    public final void declarativeMemory() throws RecognitionException {

         

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:454:2: ( ( ^( DECLARATIVE_MEMORY ( chunkType )+ ) | DECLARATIVE_MEMORY ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:454:4: ( ^( DECLARATIVE_MEMORY ( chunkType )+ ) | DECLARATIVE_MEMORY )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:454:4: ( ^( DECLARATIVE_MEMORY ( chunkType )+ ) | DECLARATIVE_MEMORY )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==DECLARATIVE_MEMORY) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==DOWN) ) {
                    alt2=1;
                }
                else if ( (LA2_1==PROCEDURAL_MEMORY) ) {
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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:454:5: ^( DECLARATIVE_MEMORY ( chunkType )+ )
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory391); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:455:14: ( chunkType )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==CHUNK_TYPE) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:455:15: chunkType
                    	    {
                    	    pushFollow(FOLLOW_chunkType_in_declarativeMemory408);
                    	    chunkType();

                    	    state._fsp--;


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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:455:30: DECLARATIVE_MEMORY
                    {
                    match(input,DECLARATIVE_MEMORY,FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory415); 

                    }
                    break;

            }


            	 /*we now have all the chunks and all the chunkTypes
            	  we can add them all to the model and then apply
            	  the parameters and resolve any slot values
            	 */
            	 
            	 /*
            	  resolve the slot values of the chunktypes
            	 */
            	 for(IChunkType chunkType : ((Model_scope)Model_stack.peek()).knownChunkTypes.values())
            	  resolveSlots(chunkType.getSymbolicChunkType(), ((Model_scope)Model_stack.peek()).knownChunks, ((Model_scope)Model_stack.peek()).knownChunkTypes);
            	 
            	 /*
            	  @bug this will not handle default slot values correctly if the chunks
            	  are defined after the chunktype is defined
            	 */ 
            	 
            	 IDeclarativeModule decMod = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule();
            	 Collection<String> sourceChunks = getSourceChunks(((Model_scope)Model_stack.peek()).modelDescriptor);
            	 //now for the chunks
            	 for(IChunk chunk : ((Model_scope)Model_stack.peek()).knownChunks.values())
            	  {
            	   resolveSlots(chunk.getSymbolicChunk(), ((Model_scope)Model_stack.peek()).knownChunks, ((Model_scope)Model_stack.peek()).knownChunkTypes); 
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
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "declarativeMemory"


    // $ANTLR start "proceduralMemory"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:494:1: proceduralMemory : ( ^( PROCEDURAL_MEMORY ( production )+ ) | PROCEDURAL_MEMORY ) ;
    public final void proceduralMemory() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:2: ( ( ^( PROCEDURAL_MEMORY ( production )+ ) | PROCEDURAL_MEMORY ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:4: ( ^( PROCEDURAL_MEMORY ( production )+ ) | PROCEDURAL_MEMORY )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:4: ( ^( PROCEDURAL_MEMORY ( production )+ ) | PROCEDURAL_MEMORY )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==PROCEDURAL_MEMORY) ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1==DOWN) ) {
                    alt4=1;
                }
                else if ( (LA4_1==UP) ) {
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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:5: ^( PROCEDURAL_MEMORY ( production )+ )
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory431); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:25: ( production )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==PRODUCTION) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:25: production
                    	    {
                    	    pushFollow(FOLLOW_production_in_proceduralMemory433);
                    	    production();

                    	    state._fsp--;


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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:495:40: PROCEDURAL_MEMORY
                    {
                    match(input,PROCEDURAL_MEMORY,FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory439); 

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
        return ;
    }
    // $ANTLR end "proceduralMemory"


    // $ANTLR start "modules"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:1: modules : ( ^( MODULES ( module )+ ) | MODULES ) ;
    public final void modules() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:9: ( ( ^( MODULES ( module )+ ) | MODULES ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:11: ( ^( MODULES ( module )+ ) | MODULES )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:11: ( ^( MODULES ( module )+ ) | MODULES )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==MODULES) ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==DOWN) ) {
                    alt6=1;
                }
                else if ( (LA6_1==EXTENSIONS) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:12: ^( MODULES ( module )+ )
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules451); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:22: ( module )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==MODULE) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:22: module
                    	    {
                    	    pushFollow(FOLLOW_module_in_modules453);
                    	    module();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:497:33: MODULES
                    {
                    match(input,MODULES,FOLLOW_MODULES_in_modules459); 

                    }
                    break;

            }


            		  /*
            		   check to see what modules have been installed
            		   we must have at least IDeclarativeModule and
            		   IProceduralModule
            		  */ 
            		  if(((Model_scope)Model_stack.peek()).model.getModule(IDeclarativeModule.class)==null)
            		   {
            		    reportException(new BuilderWarning("No IDeclarativeModule was specified, installing DefaultDeclarativeModule6"));
            		    ((Model_scope)Model_stack.peek()).model.install(new DefaultDeclarativeModule6());
            		   }
            		  
            		  if(((Model_scope)Model_stack.peek()).model.getModule(IProceduralModule.class)==null)
            		   {
            		    reportException(new BuilderWarning("No IProceduralModule was specified, installing DefaultProceduralModule6"));
            		    ((Model_scope)Model_stack.peek()).model.install(new DefaultProceduralModule6());
            		   } 
            		   
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "modules"


    // $ANTLR start "module"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:518:1: module : ^(m= MODULE c= CLASS_SPEC p= parameters ) ;
    public final void module() throws RecognitionException {
        CommonTree m=null;
        CommonTree c=null;
        Collection<CommonTree> p = null;


        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:518:9: ( ^(m= MODULE c= CLASS_SPEC p= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:518:11: ^(m= MODULE c= CLASS_SPEC p= parameters )
            {
            m=(CommonTree)match(input,MODULE,FOLLOW_MODULE_in_module477); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_module481); 
            pushFollow(FOLLOW_parameters_in_module485);
            p=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             String className = (c!=null?c.getText():null);
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
              ((Model_scope)Model_stack.peek()).model.install(module); 
             }
             catch(Exception e)
             {
              throw new BuilderError("Could not install module "+className,m, e);
             }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "module"


    // $ANTLR start "extensions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:543:1: extensions : ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS ) ;
    public final void extensions() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:2: ( ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:4: ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:4: ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==EXTENSIONS) ) {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==DOWN) ) {
                    alt8=1;
                }
                else if ( (LA8_1==BUFFERS) ) {
                    alt8=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:5: ^( EXTENSIONS ( extension )+ )
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions500); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:18: ( extension )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==EXTENSION) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:18: extension
                    	    {
                    	    pushFollow(FOLLOW_extension_in_extensions502);
                    	    extension();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:544:32: EXTENSIONS
                    {
                    match(input,EXTENSIONS,FOLLOW_EXTENSIONS_in_extensions508); 

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
        return ;
    }
    // $ANTLR end "extensions"


    // $ANTLR start "extension"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:546:1: extension : ^(e= EXTENSION c= CLASS_SPEC p= parameters ) ;
    public final void extension() throws RecognitionException {
        CommonTree e=null;
        CommonTree c=null;
        Collection<CommonTree> p = null;


        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:547:2: ( ^(e= EXTENSION c= CLASS_SPEC p= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:547:4: ^(e= EXTENSION c= CLASS_SPEC p= parameters )
            {
            e=(CommonTree)match(input,EXTENSION,FOLLOW_EXTENSION_in_extension523); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_extension527); 
            pushFollow(FOLLOW_parameters_in_extension531);
            p=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             String className = (c!=null?c.getText():null);
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
              ((Model_scope)Model_stack.peek()).extensions.add(ext);
             }
             catch(Exception ex)
             {
              throw new BuilderError("Could not build extension "+className,e, ex);
             }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "extension"


    // $ANTLR start "buffers"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:572:1: buffers : ( ^( BUFFERS ( buffer )+ ) | BUFFERS ) ;
    public final void buffers() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:1: ( ( ^( BUFFERS ( buffer )+ ) | BUFFERS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:3: ( ^( BUFFERS ( buffer )+ ) | BUFFERS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:3: ( ^( BUFFERS ( buffer )+ ) | BUFFERS )
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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:4: ^( BUFFERS ( buffer )+ )
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers547); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:15: ( buffer )+
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
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:15: buffer
                    	    {
                    	    pushFollow(FOLLOW_buffer_in_buffers550);
                    	    buffer();

                    	    state._fsp--;


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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:578:26: BUFFERS
                    {
                    match(input,BUFFERS,FOLLOW_BUFFERS_in_buffers556); 

                    }
                    break;

            }


             LOGGER.debug("got buffers tag");


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "buffers"


    // $ANTLR start "buffer"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:583:1: buffer : ^(b= BUFFER name= NAME chunks p= parameters ) ;
    public final void buffer() throws RecognitionException {
        CommonTree b=null;
        CommonTree name=null;
        Collection<CommonTree> p = null;


        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:583:9: ( ^(b= BUFFER name= NAME chunks p= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:583:11: ^(b= BUFFER name= NAME chunks p= parameters )
            {
            b=(CommonTree)match(input,BUFFER,FOLLOW_BUFFER_in_buffer572); 

            match(input, Token.DOWN, null); 
            name=(CommonTree)match(input,NAME,FOLLOW_NAME_in_buffer576); 
            pushFollow(FOLLOW_chunks_in_buffer578);
            chunks();

            state._fsp--;

            pushFollow(FOLLOW_parameters_in_buffer582);
            p=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             String bufferName = (name!=null?name.getText():null);
             LOGGER.debug("got buffer name "+bufferName);
             /*
              buffers are created by the modules, so we just apply the parameters
              the chunks will be inserted after the library is done loading
             */
             IActivationBuffer buffer = ((Model_scope)Model_stack.peek()).model.getActivationBuffer(bufferName);
             if(buffer==null)
              throw new BuilderError(bufferName+" is not a known buffer", b);
             
             if(buffer instanceof IParameterized)
              applyParameters((IParameterized)buffer, p);
               


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "buffer"


    // $ANTLR start "chunkType"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:600:1: chunkType returns [IChunkType chunkType] : ^(c= CHUNK_TYPE n= NAME p= parents s= slots chunks param= parameters ) ;
    public final IChunkType chunkType() throws RecognitionException {
        IChunkType chunkType = null;

        CommonTree c=null;
        CommonTree n=null;
        Collection<IChunkType> p = null;

        Collection<ISlot> s = null;

        Collection<CommonTree> param = null;



         chunkType = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:604:1: ( ^(c= CHUNK_TYPE n= NAME p= parents s= slots chunks param= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:604:3: ^(c= CHUNK_TYPE n= NAME p= parents s= slots chunks param= parameters )
            {
            c=(CommonTree)match(input,CHUNK_TYPE,FOLLOW_CHUNK_TYPE_in_chunkType605); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunkType609); 
            pushFollow(FOLLOW_parents_in_chunkType613);
            p=parents();

            state._fsp--;

            pushFollow(FOLLOW_slots_in_chunkType618);
            s=slots();

            state._fsp--;


            	 /*
            	  we create the chunktype once we have enough info to work with
            	  the compiler ensures us that parents are defined already
            	 */
            	 String chunkTypeName = (n!=null?n.getText():null);
            	 IModel model = ((Model_scope)Model_stack.peek()).model;
                      
                     try
                     {
                      chunkType = model.getDeclarativeModule().createChunkType(p, chunkTypeName).get();
                      if(LOGGER.isDebugEnabled())
                       LOGGER.debug("created chunktype "+chunkType);
                     }
                     catch(Exception e)
                     {
                      throw new BuilderError("Could not create chunk-type "+chunkTypeName, c, e);
                     } 
                     
                     //insert all the slots
                     for(ISlot slot : s)
                      chunkType.getSymbolicChunkType().addSlot(slot);
                      
                     ((Model_scope)Model_stack.peek()).knownChunkTypes.put(chunkTypeName.toLowerCase(), chunkType);
                     
                     /*add the chunkType to the model
                      this must be done before chunks is called
                     */
                     if(LOGGER.isDebugEnabled())
                      LOGGER.debug("Adding chunktype "+chunkType+" to model");
            	 model.getDeclarativeModule().addChunkType(chunkType);
            	
            pushFollow(FOLLOW_chunks_in_chunkType625);
            chunks();

            state._fsp--;

            pushFollow(FOLLOW_parameters_in_chunkType630);
            param=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

            	 applyParameters(chunkType.getSubsymbolicChunkType(), param);
            	 
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return chunkType;
    }
    // $ANTLR end "chunkType"


    // $ANTLR start "chunks"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:644:1: chunks returns [Collection cl] : ( ^( CHUNKS (c= chunk | id= CHUNK_IDENTIFIER )+ ) | CHUNKS ) ;
    public final Collection chunks() throws RecognitionException {
        Collection cl = null;

        CommonTree id=null;
        IChunk c = null;



         cl = new ArrayList();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:3: ( ( ^( CHUNKS (c= chunk | id= CHUNK_IDENTIFIER )+ ) | CHUNKS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:5: ( ^( CHUNKS (c= chunk | id= CHUNK_IDENTIFIER )+ ) | CHUNKS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:5: ( ^( CHUNKS (c= chunk | id= CHUNK_IDENTIFIER )+ ) | CHUNKS )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==CHUNKS) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==DOWN) ) {
                    alt12=1;
                }
                else if ( (LA12_1==PARAMETERS) ) {
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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:6: ^( CHUNKS (c= chunk | id= CHUNK_IDENTIFIER )+ )
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks654); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:15: (c= chunk | id= CHUNK_IDENTIFIER )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=3;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==CHUNK) ) {
                            alt11=1;
                        }
                        else if ( (LA11_0==CHUNK_IDENTIFIER) ) {
                            alt11=2;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:650:16: c= chunk
                    	    {
                    	    pushFollow(FOLLOW_chunk_in_chunks659);
                    	    c=chunk();

                    	    state._fsp--;

                    	    cl.add(c);

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:651:27: id= CHUNK_IDENTIFIER
                    	    {
                    	    id=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_chunks691); 
                    	    cl.add((id!=null?id.getText():null));

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
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:652:32: CHUNKS
                    {
                    match(input,CHUNKS,FOLLOW_CHUNKS_in_chunks727); 

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
        return cl;
    }
    // $ANTLR end "chunks"


    // $ANTLR start "chunk"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:654:1: chunk returns [IChunk ch] : ^(c= CHUNK n= NAME p= PARENT s= slots param= parameters ) ;
    public final IChunk chunk() throws RecognitionException {
        IChunk ch = null;

        CommonTree c=null;
        CommonTree n=null;
        CommonTree p=null;
        Collection<ISlot> s = null;

        Collection<CommonTree> param = null;



         ch= null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:657:3: ( ^(c= CHUNK n= NAME p= PARENT s= slots param= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:657:5: ^(c= CHUNK n= NAME p= PARENT s= slots param= parameters )
            {
            c=(CommonTree)match(input,CHUNK,FOLLOW_CHUNK_in_chunk748); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_chunk752); 
            p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_chunk756); 
            pushFollow(FOLLOW_slots_in_chunk760);
            s=slots();

            state._fsp--;

            pushFollow(FOLLOW_parameters_in_chunk764);
            param=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

             LOGGER.debug("got chunk def "+(n!=null?n.getText():null)+" isa "+(p!=null?p.getText():null));
             String chunkName = (n!=null?n.getText():null);
             String parentName = (p!=null?p.getText():null);
             IModel model = ((Model_scope)Model_stack.peek()).model;
             
             if(((Model_scope)Model_stack.peek()).knownChunks.containsKey(chunkName))
              reportException(new BuilderWarning(chunkName+" already exists, replacing", c));
             
             IChunkType parentType = null;
             try
             {
              parentType = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType(parentName).get();
             }
             catch(Exception e)
             {
              LOGGER.error("Could not get chunktype "+parentName, e);
             } 
             
             if(parentType==null)
              throw new BuilderError(parentName+" is not a recognized chunk-type", p);

             try
             {
               ch = model.getDeclarativeModule().createChunk(parentType, chunkName).get();
             } 
             catch(Exception e)
             {
              throw new BuilderError("Could not create chunk "+chunkName, c, e);
             }
             
             ((Model_scope)Model_stack.peek()).knownChunks.put(chunkName.toLowerCase(), ch);
             
             //add slots
             for(ISlot slot : s)
              try
               {
                ch.getSymbolicChunk().addSlot(slot);
               }
              catch(Exception e)
               {
                reportException(new BuilderWarning("Could not add slot "+slot.getName()+" to chunk "+chunkName, c, e));
               }
               
             //save the parameters for later because chunks in associative links might not exist yet
             ((Model_scope)Model_stack.peek()).chunkParameters.put(ch, param);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ch;
    }
    // $ANTLR end "chunk"


    // $ANTLR start "production"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:706:1: production returns [IProduction production] : ^(p= PRODUCTION n= NAME conds= conditions acts= actions params= parameters ) ;
    public final IProduction production() throws RecognitionException {
        IProduction production = null;

        CommonTree p=null;
        CommonTree n=null;
        Collection<ICondition> conds = null;

        Collection<IAction> acts = null;

        Collection<CommonTree> params = null;



        production = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:710:2: ( ^(p= PRODUCTION n= NAME conds= conditions acts= actions params= parameters ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:710:4: ^(p= PRODUCTION n= NAME conds= conditions acts= actions params= parameters )
            {
            p=(CommonTree)match(input,PRODUCTION,FOLLOW_PRODUCTION_in_production787); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_production791); 
            pushFollow(FOLLOW_conditions_in_production795);
            conds=conditions();

            state._fsp--;

            pushFollow(FOLLOW_actions_in_production799);
            acts=actions();

            state._fsp--;

            pushFollow(FOLLOW_parameters_in_production803);
            params=parameters();

            state._fsp--;


            match(input, Token.UP, null); 

            	 String productionName = (n!=null?n.getText():null);
            	 LOGGER.debug("Got a production def "+productionName);
            	 /**
            	  we can just create the production, no need to worry about
            	  resolution since all declarative elements have been defined
            	 */
            	 IModel model = ((Model_scope)Model_stack.peek()).model;
            	  try
            	  {
            	   production = model.getProceduralModule().createProduction(productionName).get();
            	  } 
            	  catch(Exception e)
            	  {
            	   throw new BuilderError("Could not create production "+productionName, p, e);
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
            	  ((Model_scope)Model_stack.peek()).productionParameters.put(production, params);
            	 
            	 //add to the model
            	 model.getProceduralModule().addProduction(production);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return production;
    }
    // $ANTLR end "production"


    // $ANTLR start "conditions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:745:1: conditions returns [Collection<ICondition> rtn] : ^( CONDITIONS (c= check | q= query | s= scriptCond | p= proxyCond )+ ) ;
    public final Collection<ICondition> conditions() throws RecognitionException {
        Collection<ICondition> rtn = null;

        IBufferCondition c = null;

        QueryCondition q = null;

        ScriptableCondition s = null;

        ProxyCondition p = null;



         rtn = new ArrayList<ICondition>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:749:2: ( ^( CONDITIONS (c= check | q= query | s= scriptCond | p= proxyCond )+ ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:749:4: ^( CONDITIONS (c= check | q= query | s= scriptCond | p= proxyCond )+ )
            {
            match(input,CONDITIONS,FOLLOW_CONDITIONS_in_conditions825); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:749:17: (c= check | q= query | s= scriptCond | p= proxyCond )+
            int cnt13=0;
            loop13:
            do {
                int alt13=5;
                switch ( input.LA(1) ) {
                case MATCH_CONDITION:
                    {
                    alt13=1;
                    }
                    break;
                case QUERY_CONDITION:
                    {
                    alt13=2;
                    }
                    break;
                case SCRIPTABLE_CONDITION:
                    {
                    alt13=3;
                    }
                    break;
                case PROXY_CONDITION:
                    {
                    alt13=4;
                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:749:18: c= check
            	    {
            	    pushFollow(FOLLOW_check_in_conditions830);
            	    c=check();

            	    state._fsp--;

            	    rtn.add(c);

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:750:15: q= query
            	    {
            	    pushFollow(FOLLOW_query_in_conditions850);
            	    q=query();

            	    state._fsp--;

            	    rtn.add(q);

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:751:15: s= scriptCond
            	    {
            	    pushFollow(FOLLOW_scriptCond_in_conditions870);
            	    s=scriptCond();

            	    state._fsp--;

            	    rtn.add(s);

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:752:15: p= proxyCond
            	    {
            	    pushFollow(FOLLOW_proxyCond_in_conditions890);
            	    p=proxyCond();

            	    state._fsp--;

            	    rtn.add(p);

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
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
        return rtn;
    }
    // $ANTLR end "conditions"


    // $ANTLR start "actions"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:755:1: actions returns [Collection<IAction> rtn] : ^( ACTIONS (a= add | s= set | r= remove | m= modify | sc= scriptAct | p= proxyAct | o= output )+ ) ;
    public final Collection<IAction> actions() throws RecognitionException {
        Collection<IAction> rtn = null;

        AddAction a = null;

        SetAction s = null;

        RemoveAction r = null;

        ModifyAction m = null;

        ScriptableAction sc = null;

        ProxyAction p = null;

        OutputAction o = null;



         rtn = new ArrayList<IAction>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:759:2: ( ^( ACTIONS (a= add | s= set | r= remove | m= modify | sc= scriptAct | p= proxyAct | o= output )+ ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:759:4: ^( ACTIONS (a= add | s= set | r= remove | m= modify | sc= scriptAct | p= proxyAct | o= output )+ )
            {
            match(input,ACTIONS,FOLLOW_ACTIONS_in_actions926); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:759:14: (a= add | s= set | r= remove | m= modify | sc= scriptAct | p= proxyAct | o= output )+
            int cnt14=0;
            loop14:
            do {
                int alt14=8;
                switch ( input.LA(1) ) {
                case ADD_ACTION:
                    {
                    alt14=1;
                    }
                    break;
                case SET_ACTION:
                    {
                    alt14=2;
                    }
                    break;
                case REMOVE_ACTION:
                    {
                    alt14=3;
                    }
                    break;
                case MODIFY_ACTION:
                    {
                    alt14=4;
                    }
                    break;
                case SCRIPTABLE_ACTION:
                    {
                    alt14=5;
                    }
                    break;
                case PROXY_ACTION:
                    {
                    alt14=6;
                    }
                    break;
                case OUTPUT_ACTION:
                    {
                    alt14=7;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:759:15: a= add
            	    {
            	    pushFollow(FOLLOW_add_in_actions931);
            	    a=add();

            	    state._fsp--;

            	    rtn.add(a);

            	    }
            	    break;
            	case 2 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:760:20: s= set
            	    {
            	    pushFollow(FOLLOW_set_in_actions956);
            	    s=set();

            	    state._fsp--;

            	    rtn.add(s);

            	    }
            	    break;
            	case 3 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:761:20: r= remove
            	    {
            	    pushFollow(FOLLOW_remove_in_actions981);
            	    r=remove();

            	    state._fsp--;

            	    rtn.add(r);

            	    }
            	    break;
            	case 4 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:762:20: m= modify
            	    {
            	    pushFollow(FOLLOW_modify_in_actions1006);
            	    m=modify();

            	    state._fsp--;

            	    rtn.add(m);

            	    }
            	    break;
            	case 5 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:763:20: sc= scriptAct
            	    {
            	    pushFollow(FOLLOW_scriptAct_in_actions1031);
            	    sc=scriptAct();

            	    state._fsp--;

            	    rtn.add(sc);

            	    }
            	    break;
            	case 6 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:764:20: p= proxyAct
            	    {
            	    pushFollow(FOLLOW_proxyAct_in_actions1056);
            	    p=proxyAct();

            	    state._fsp--;

            	    rtn.add(p);

            	    }
            	    break;
            	case 7 :
            	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:765:20: o= output
            	    {
            	    pushFollow(FOLLOW_output_in_actions1081);
            	    o=output();

            	    state._fsp--;

            	    rtn.add(o);

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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "actions"


    // $ANTLR start "check"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:768:1: check returns [IBufferCondition rtn] : ^( MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? ) ;
    public final IBufferCondition check() throws RecognitionException {
        IBufferCondition rtn = null;

        CommonTree n=null;
        CommonTree c=null;
        CommonTree ct=null;
        CommonTree v=null;
        Collection<ISlot> sl = null;



         rtn = null;
         String bufferName = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:776:2: ( ^( MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:776:4: ^( MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )? )
            {
            match(input,MATCH_CONDITION,FOLLOW_MATCH_CONDITION_in_check1124); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_check1128); 
            bufferName = (n!=null?n.getText():null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:777:17: ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==VARIABLE||(LA17_0>=CHUNK_IDENTIFIER && LA17_0<=CHUNK_TYPE_IDENTIFIER)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:777:18: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )?
                    {
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:777:18: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
                    int alt15=3;
                    switch ( input.LA(1) ) {
                    case CHUNK_IDENTIFIER:
                        {
                        alt15=1;
                        }
                        break;
                    case CHUNK_TYPE_IDENTIFIER:
                        {
                        alt15=2;
                        }
                        break;
                    case VARIABLE:
                        {
                        alt15=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 0, input);

                        throw nvae;
                    }

                    switch (alt15) {
                        case 1 :
                            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:777:19: c= CHUNK_IDENTIFIER
                            {
                            c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_check1152); 

                             try
                              {
                               IChunk chunk = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunk((c!=null?c.getText():null)).get();
                               if(chunk!=null)
                                 rtn = new ChunkCondition(bufferName, chunk);
                               else
                                throw new BuilderError((c!=null?c.getText():null)+" is not a valid chunk", c); 
                              }
                              catch(Exception e)
                              {
                               LOGGER.error("Could not get chunk "+(c!=null?c.getText():null), e);
                              }


                            }
                            break;
                        case 2 :
                            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:792:19: ct= CHUNK_TYPE_IDENTIFIER
                            {
                            ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_check1176); 

                              try
                               {
                                IChunkType chunkType = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType((ct!=null?ct.getText():null)).get();
                                if(chunkType!=null)
                                 rtn = new ChunkTypeCondition(bufferName, chunkType);
                                else
                                 throw new BuilderError((ct!=null?ct.getText():null)+" is not a valid chunktype", ct);
                               }
                               catch(Exception e)
                               {
                                LOGGER.error("Could not get chunktype "+(ct!=null?ct.getText():null), e);
                               } 


                            }
                            break;
                        case 3 :
                            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:807:19: v= VARIABLE
                            {
                            v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_check1216); 

                             rtn = new VariableCondition(bufferName, (v!=null?v.getText():null));


                            }
                            break;

                    }

                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:812:17: (sl= slots )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==SLOTS) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:812:18: sl= slots
                            {
                            pushFollow(FOLLOW_slots_in_check1275);
                            sl=slots();

                            state._fsp--;


                            	IChunkType newIsa = null;
                            	IChunkType isA = (rtn instanceof ChunkTypeCondition) ? ((ChunkTypeCondition)rtn).getChunkType() : null;
                            	for(ISlot slot : sl) {
                            	  try
                            	   {
                            	   LOGGER.debug("seeing if slot " + slot + " is an isa so we can fold it in.");
                                	    if(slot.getName().equals(":isa") && isA != null) { 
                                	    	newIsa = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType(slot.getValue().toString()).get();
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
                            	 //	throw new BuilderError(", ct);
                            	 //} else 
                            	   if(newIsa.isA(isA))
                            	 	 rtn = new ChunkTypeCondition(rtn.getBufferName(), newIsa, ((ChunkTypeCondition)rtn).getRequest().getSlots());
                            	}


                            }
                            break;

                    }


                    }
                    break;

            }


            match(input, Token.UP, null); 

              if(rtn==null)
               rtn = new ChunkTypeCondition(bufferName, null);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "check"


    // $ANTLR start "unknownList"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:847:1: unknownList : ^( UNKNOWN ( . )* ) ;
    public final void unknownList() throws RecognitionException {
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:848:2: ( ^( UNKNOWN ( . )* ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:848:4: ^( UNKNOWN ( . )* )
            {
            match(input,UNKNOWN,FOLLOW_UNKNOWN_in_unknownList1329); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:848:14: ( . )*
                loop18:
                do {
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( ((LA18_0>=MODEL && LA18_0<=UNKNOWN)) ) {
                        alt18=1;
                    }
                    else if ( (LA18_0==UP) ) {
                        alt18=2;
                    }


                    switch (alt18) {
                	case 1 :
                	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:848:14: .
                	    {
                	    matchAny(input); 

                	    }
                	    break;

                	default :
                	    break loop18;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "unknownList"


    // $ANTLR start "query"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:852:1: query returns [QueryCondition rtn] : ^( QUERY_CONDITION n= NAME sl= slots ) ;
    public final QueryCondition query() throws RecognitionException {
        QueryCondition rtn = null;

        CommonTree n=null;
        Collection<ISlot> sl = null;



         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:856:1: ( ^( QUERY_CONDITION n= NAME sl= slots ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:856:3: ^( QUERY_CONDITION n= NAME sl= slots )
            {
            match(input,QUERY_CONDITION,FOLLOW_QUERY_CONDITION_in_query1351); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_query1355); 
            pushFollow(FOLLOW_slots_in_query1359);
            sl=slots();

            state._fsp--;


            match(input, Token.UP, null); 

             rtn = new QueryCondition((n!=null?n.getText():null));
             for(ISlot slot : sl)
              rtn.addSlot(cleanupSlot(slot));


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "query"


    // $ANTLR start "scriptCond"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:863:1: scriptCond returns [ScriptableCondition rtn] : ^( SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) ;
    public final ScriptableCondition scriptCond() throws RecognitionException {
        ScriptableCondition rtn = null;

        CommonTree l=null;
        CommonTree s=null;


         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:867:2: ( ^( SCRIPTABLE_CONDITION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:867:4: ^( SCRIPTABLE_CONDITION l= LANG s= SCRIPT )
            {
            match(input,SCRIPTABLE_CONDITION,FOLLOW_SCRIPTABLE_CONDITION_in_scriptCond1380); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptCond1384); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptCond1388); 

            match(input, Token.UP, null); 

            	 //we currently dont support lang..
            	 try
            	  {
            	   IScriptableFactory factory = ScriptingManager.getFactory((l!=null?l.getText():null));
            	   if(factory==null)
            	    throw new BuilderError("Could not find scripting engine for " + (l!=null?l.getText():null), l);
            	    
            	   IConditionScript script = factory.createConditionScript((s!=null?s.getText():null));
            	   rtn = new ScriptableCondition(script);
            	  }
            	  catch(Exception e)
            	  {
            	   throw new BuilderError("Could not compile script because "+e.getMessage(), s, e);
            	  }
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "scriptCond"


    // $ANTLR start "proxyCond"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:885:1: proxyCond returns [ProxyCondition rtn] : ^( PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) ;
    public final ProxyCondition proxyCond() throws RecognitionException {
        ProxyCondition rtn = null;

        CommonTree c=null;
        Collection<ISlot> sl = null;



         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:889:1: ( ^( PROXY_CONDITION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:889:3: ^( PROXY_CONDITION c= CLASS_SPEC (sl= slots )? )
            {
            match(input,PROXY_CONDITION,FOLLOW_PROXY_CONDITION_in_proxyCond1410); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyCond1414); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:889:34: (sl= slots )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==SLOTS) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:889:35: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyCond1419);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

                      try
                      {
                       rtn = new ProxyCondition((c!=null?c.getText():null));
                       if(sl!=null)
                        for(ISlot slot : sl)
                         rtn.addSlot(cleanupSlot(slot));
                      }
                      catch(Exception e)
                      {
                       throw new BuilderError("Could not create proxy condition"+(c!=null?c.getText():null), c, e);
                      }
                     

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "proxyCond"


    // $ANTLR start "add"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:905:1: add returns [AddAction rtn] : ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? ) ;
    public final AddAction add() throws RecognitionException {
        AddAction rtn = null;

        CommonTree a=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree ct=null;
        CommonTree v=null;
        Collection<ISlot> sl = null;



         rtn = null;
         String bufferName = null;
         Object ref = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:911:1: ( ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:911:3: ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (sl= slots )? )
            {
            a=(CommonTree)match(input,ADD_ACTION,FOLLOW_ADD_ACTION_in_add1455); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_add1459); 
            bufferName = (n!=null?n.getText():null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:912:17: (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
            int alt20=3;
            switch ( input.LA(1) ) {
            case CHUNK_IDENTIFIER:
                {
                alt20=1;
                }
                break;
            case CHUNK_TYPE_IDENTIFIER:
                {
                alt20=2;
                }
                break;
            case VARIABLE:
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:912:18: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_add1482); 

                    try
                      {
                       ref = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunk((c!=null?c.getText():null)).get();
                       if(ref==null)
                         throw new BuilderError((c!=null?c.getText():null)+" is not a valid chunk", c);
                      }
                      catch(Exception e)
                      {
                       LOGGER.error("Could not get chunk "+(c!=null?c.getText():null), e);
                      }


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:925:19: ct= CHUNK_TYPE_IDENTIFIER
                    {
                    ct=(CommonTree)match(input,CHUNK_TYPE_IDENTIFIER,FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add1524); 

                    try
                       {
                        ref = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType((ct!=null?ct.getText():null)).get(); 
                        if(ref==null)
                         throw new BuilderError((ct!=null?ct.getText():null)+" is not a chunktype", ct);
                       }
                       catch(Exception e)
                       {
                        LOGGER.error("Could not get chunktype "+(ct!=null?ct.getText():null),e);
                       } 


                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:938:19: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_add1564); 

                     ref = (v!=null?v.getText():null);


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:942:20: (sl= slots )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==SLOTS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:942:21: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_add1607);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

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
            	  newIsa = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType(slot.getValue().toString()).get();
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
            	  throw new BuilderError("Cannot handle :isa not in a buffer other than retrieval", a);
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
              


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "add"


    // $ANTLR start "set"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:998:1: set returns [SetAction rtn] : ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) ;
    public final SetAction set() throws RecognitionException {
        SetAction rtn = null;

        CommonTree a=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree v=null;
        Collection<ISlot> sl = null;



         rtn = null;
         String bufferName = null;
         Object ref = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1004:1: ( ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1004:3: ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? (sl= slots )? )
            {
            a=(CommonTree)match(input,SET_ACTION,FOLLOW_SET_ACTION_in_set1633); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_set1637); 
            bufferName = (n!=null?n.getText():null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1005:17: (c= CHUNK_IDENTIFIER | v= VARIABLE )?
            int alt22=3;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==CHUNK_IDENTIFIER) ) {
                alt22=1;
            }
            else if ( (LA22_0==VARIABLE) ) {
                alt22=2;
            }
            switch (alt22) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1005:18: c= CHUNK_IDENTIFIER
                    {
                    c=(CommonTree)match(input,CHUNK_IDENTIFIER,FOLLOW_CHUNK_IDENTIFIER_in_set1660); 

                    try
                      {
                       ref = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunk((c!=null?c.getText():null)).get();
                       if(ref==null)
                         throw new BuilderError((c!=null?c.getText():null)+" is not a valid chunk", c);
                      }
                      catch(Exception e)
                      {
                       LOGGER.error("Could not get chunk "+(c!=null?c.getText():null), e);
                      }


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1018:19: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_set1714); 

                     ref = (v!=null?v.getText():null);


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1022:21: (sl= slots )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SLOTS) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1022:22: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_set1758);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

             rtn = new SetAction(bufferName, ref);
             if(sl!=null)
              for(ISlot slot : sl)
               rtn.addSlot(cleanupSlot(slot));  


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "set"


    // $ANTLR start "remove"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1031:1: remove returns [RemoveAction rtn] : ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? (sl= slots )? ) ;
    public final RemoveAction remove() throws RecognitionException {
        RemoveAction rtn = null;

        CommonTree r=null;
        CommonTree n=null;
        CommonTree i=null;
        CommonTree v=null;
        Collection<ISlot> sl = null;



         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1035:1: ( ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1035:3: ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? (sl= slots )? )
            {
            r=(CommonTree)match(input,REMOVE_ACTION,FOLLOW_REMOVE_ACTION_in_remove1782); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_remove1786); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1036:17: (i= IDENTIFIER | v= VARIABLE )?
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==IDENTIFIER) ) {
                alt24=1;
            }
            else if ( (LA24_0==VARIABLE) ) {
                alt24=2;
            }
            switch (alt24) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1036:18: i= IDENTIFIER
                    {
                    i=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_remove1808); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1037:19: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_remove1830); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1038:20: (sl= slots )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SLOTS) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1038:21: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_remove1855);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

             String bufferName = (n!=null?n.getText():null);
             if(i!=null || v!=null)
              reportException(new BuilderWarning("jACT-R core does not currently support remove action precise specifications", ((i==null)?v:i)));
             
             rtn = new RemoveAction(bufferName);
             if(sl!=null)
              for(ISlot slot : sl)
               rtn.addSlot(cleanupSlot(slot)); 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "remove"


    // $ANTLR start "modify"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1050:1: modify returns [ModifyAction rtn] : ^( MODIFY_ACTION n= NAME (sl= slots )? ) ;
    public final ModifyAction modify() throws RecognitionException {
        ModifyAction rtn = null;

        CommonTree n=null;
        Collection<ISlot> sl = null;



         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1054:1: ( ^( MODIFY_ACTION n= NAME (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1054:3: ^( MODIFY_ACTION n= NAME (sl= slots )? )
            {
            match(input,MODIFY_ACTION,FOLLOW_MODIFY_ACTION_in_modify1876); 

            match(input, Token.DOWN, null); 
            n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_modify1880); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1054:28: (sl= slots )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==SLOTS) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1054:28: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_modify1884);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

             String bufferName = (n!=null?n.getText():null);
             rtn = new ModifyAction(bufferName);
             if(sl!=null)
             for(ISlot slot: sl)
              rtn.addSlot(cleanupSlot(slot));


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "modify"


    // $ANTLR start "scriptAct"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1063:1: scriptAct returns [ScriptableAction rtn] : ^(root= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) ;
    public final ScriptableAction scriptAct() throws RecognitionException {
        ScriptableAction rtn = null;

        CommonTree root=null;
        CommonTree l=null;
        CommonTree s=null;


         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1067:2: ( ^(root= SCRIPTABLE_ACTION l= LANG s= SCRIPT ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1067:4: ^(root= SCRIPTABLE_ACTION l= LANG s= SCRIPT )
            {
            root=(CommonTree)match(input,SCRIPTABLE_ACTION,FOLLOW_SCRIPTABLE_ACTION_in_scriptAct1907); 

            match(input, Token.DOWN, null); 
            l=(CommonTree)match(input,LANG,FOLLOW_LANG_in_scriptAct1911); 
            s=(CommonTree)match(input,SCRIPT,FOLLOW_SCRIPT_in_scriptAct1915); 

            match(input, Token.UP, null); 

            	 //we currently dont support lang..
            	 try
            	  {
            	   IScriptableFactory factory = ScriptingManager.getFactory((l!=null?l.getText():null));
            	   if(factory==null)
            	    throw new BuilderError("Could not find scripting engine for "+ (l!=null?l.getText():null), l);
            	    
            	  IActionScript script = factory.createActionScript((s!=null?s.getText():null));
            	  rtn = new ScriptableAction(script);
            	  }
            	  catch(Exception e)
            	  {
            	   throw new BuilderError("Could not compile script because "+e.getMessage(), s, e);
            	  }
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "scriptAct"


    // $ANTLR start "proxyAct"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1085:1: proxyAct returns [ProxyAction rtn] : ^( PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) ;
    public final ProxyAction proxyAct() throws RecognitionException {
        ProxyAction rtn = null;

        CommonTree c=null;
        Collection<ISlot> sl = null;



         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1089:1: ( ^( PROXY_ACTION c= CLASS_SPEC (sl= slots )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1089:3: ^( PROXY_ACTION c= CLASS_SPEC (sl= slots )? )
            {
            match(input,PROXY_ACTION,FOLLOW_PROXY_ACTION_in_proxyAct1936); 

            match(input, Token.DOWN, null); 
            c=(CommonTree)match(input,CLASS_SPEC,FOLLOW_CLASS_SPEC_in_proxyAct1940); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1089:31: (sl= slots )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SLOTS) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1089:32: sl= slots
                    {
                    pushFollow(FOLLOW_slots_in_proxyAct1945);
                    sl=slots();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

                      try
                      {
                       rtn = new ProxyAction((c!=null?c.getText():null));
                       if(sl!=null)
                        for(ISlot slot : sl)
                         rtn.addSlot(cleanupSlot(slot));
                      }
                      catch(Exception e)
                      {
                       throw new BuilderError("Could not create proxy action"+(c!=null?c.getText():null), c, e);
                      }
                     

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "proxyAct"


    // $ANTLR start "output"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1104:1: output returns [OutputAction rtn] : ^( OUTPUT_ACTION s= STRING ) ;
    public final OutputAction output() throws RecognitionException {
        OutputAction rtn = null;

        CommonTree s=null;


         rtn = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1108:2: ( ^( OUTPUT_ACTION s= STRING ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1108:4: ^( OUTPUT_ACTION s= STRING )
            {
            match(input,OUTPUT_ACTION,FOLLOW_OUTPUT_ACTION_in_output1976); 

            match(input, Token.DOWN, null); 
            s=(CommonTree)match(input,STRING,FOLLOW_STRING_in_output1980); 

            match(input, Token.UP, null); 

            	 rtn = new OutputAction((s!=null?s.getText():null));
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rtn;
    }
    // $ANTLR end "output"


    // $ANTLR start "parents"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1113:1: parents returns [Collection<IChunkType> pl] : ( ^( PARENTS (p= PARENT )+ ) | PARENTS ) ;
    public final Collection<IChunkType> parents() throws RecognitionException {
        Collection<IChunkType> pl = null;

        CommonTree p=null;


         pl = new ArrayList<IChunkType>();
         
        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1116:3: ( ( ^( PARENTS (p= PARENT )+ ) | PARENTS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1116:9: ( ^( PARENTS (p= PARENT )+ ) | PARENTS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1116:9: ( ^( PARENTS (p= PARENT )+ ) | PARENTS )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==PARENTS) ) {
                int LA29_1 = input.LA(2);

                if ( (LA29_1==DOWN) ) {
                    alt29=1;
                }
                else if ( (LA29_1==SLOTS) ) {
                    alt29=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1116:10: ^( PARENTS (p= PARENT )+ )
                    {
                    match(input,PARENTS,FOLLOW_PARENTS_in_parents2005); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1117:4: (p= PARENT )+
                    int cnt28=0;
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==PARENT) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1117:5: p= PARENT
                    	    {
                    	    p=(CommonTree)match(input,PARENT,FOLLOW_PARENT_in_parents2014); 
                    	    	   
                    	     			String parentName = p.getText();
                    	            	 	IChunkType parentType = ((Model_scope)Model_stack.peek()).knownChunkTypes.get(parentName.toLowerCase());
                    	               	 	if(parentType==null)
                    	                			throw new BuilderError("Could not find chunk-type "+parentName, p);
                    	                	 	else pl.add(parentType);
                    	                	 	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt28 >= 1 ) break loop28;
                                EarlyExitException eee =
                                    new EarlyExitException(28, input);
                                throw eee;
                        }
                        cnt28++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1124:21: PARENTS
                    {
                    match(input,PARENTS,FOLLOW_PARENTS_in_parents2038); 

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
        return pl;
    }
    // $ANTLR end "parents"


    // $ANTLR start "slots"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1126:1: slots returns [Collection<ISlot> sl] : ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS ) ;
    public final Collection<ISlot> slots() throws RecognitionException {
        Collection<ISlot> sl = null;

        ISlot s = null;

        ISlot l = null;



         sl =  new ArrayList<ISlot>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:2: ( ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:4: ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:4: ( ^( SLOTS (s= slot | l= logic )+ ) | SLOTS )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==SLOTS) ) {
                int LA31_1 = input.LA(2);

                if ( (LA31_1==DOWN) ) {
                    alt31=1;
                }
                else if ( (LA31_1==UP||LA31_1==CHUNKS||LA31_1==PARAMETERS) ) {
                    alt31=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:5: ^( SLOTS (s= slot | l= logic )+ )
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots2057); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:13: (s= slot | l= logic )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=3;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==SLOT) ) {
                            alt30=1;
                        }
                        else if ( (LA30_0==LOGIC) ) {
                            alt30=2;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1129:14: s= slot
                    	    {
                    	    pushFollow(FOLLOW_slot_in_slots2062);
                    	    s=slot();

                    	    state._fsp--;

                    	    sl.add(s);

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1130:5: l= logic
                    	    {
                    	    pushFollow(FOLLOW_logic_in_slots2072);
                    	    l=logic();

                    	    state._fsp--;

                    	    sl.add(l);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1131:9: SLOTS
                    {
                    match(input,SLOTS,FOLLOW_SLOTS_in_slots2084); 

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
        return sl;
    }
    // $ANTLR end "slots"


    // $ANTLR start "parameters"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1133:1: parameters returns [Collection<CommonTree> params] : ( ^( PARAMETERS (p= parameter )+ ) | PARAMETERS ) ;
    public final Collection<CommonTree> parameters() throws RecognitionException {
        Collection<CommonTree> params = null;

        CommonTree p = null;



         params = new ArrayList<CommonTree>();

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:2: ( ( ^( PARAMETERS (p= parameter )+ ) | PARAMETERS ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:4: ( ^( PARAMETERS (p= parameter )+ ) | PARAMETERS )
            {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:4: ( ^( PARAMETERS (p= parameter )+ ) | PARAMETERS )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==PARAMETERS) ) {
                int LA33_1 = input.LA(2);

                if ( (LA33_1==DOWN) ) {
                    alt33=1;
                }
                else if ( (LA33_1==UP) ) {
                    alt33=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:5: ^( PARAMETERS (p= parameter )+ )
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters2108); 

                    match(input, Token.DOWN, null); 
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:18: (p= parameter )+
                    int cnt32=0;
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==PARAMETER) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:19: p= parameter
                    	    {
                    	    pushFollow(FOLLOW_parameter_in_parameters2113);
                    	    p=parameter();

                    	    state._fsp--;

                    	    params.add(p);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt32 >= 1 ) break loop32;
                                EarlyExitException eee =
                                    new EarlyExitException(32, input);
                                throw eee;
                        }
                        cnt32++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1141:54: PARAMETERS
                    {
                    match(input,PARAMETERS,FOLLOW_PARAMETERS_in_parameters2122); 

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
        return params;
    }
    // $ANTLR end "parameters"


    // $ANTLR start "parameter"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1145:1: parameter returns [CommonTree param] : ^(p= PARAMETER NAME STRING ) ;
    public final CommonTree parameter() throws RecognitionException {
        CommonTree param = null;

        CommonTree p=null;


         param = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1149:2: ( ^(p= PARAMETER NAME STRING ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1149:4: ^(p= PARAMETER NAME STRING )
            {
            p=(CommonTree)match(input,PARAMETER,FOLLOW_PARAMETER_in_parameter2147); 

            match(input, Token.DOWN, null); 
            match(input,NAME,FOLLOW_NAME_in_parameter2149); 
            match(input,STRING,FOLLOW_STRING_in_parameter2151); 

            match(input, Token.UP, null); 

            	 param = p;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return param;
    }
    // $ANTLR end "parameter"


    // $ANTLR start "logic"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1154:1: logic returns [ISlot ls] : ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? ) ;
    public final ISlot logic() throws RecognitionException {
        ISlot ls = null;

        CommonTree l=null;
        CommonTree v=null;
        ISlot s1 = null;

        ISlot s2 = null;



          ls = null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:1: ( ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:3: ^(l= LOGIC (v= AND | v= OR | v= NOT ) (s1= logic | s1= slot ) (s2= logic | s2= slot )? )
            {
            l=(CommonTree)match(input,LOGIC,FOLLOW_LOGIC_in_logic2178); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:13: (v= AND | v= OR | v= NOT )
            int alt34=3;
            switch ( input.LA(1) ) {
            case AND:
                {
                alt34=1;
                }
                break;
            case OR:
                {
                alt34=2;
                }
                break;
            case NOT:
                {
                alt34=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:14: v= AND
                    {
                    v=(CommonTree)match(input,AND,FOLLOW_AND_in_logic2183); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:20: v= OR
                    {
                    v=(CommonTree)match(input,OR,FOLLOW_OR_in_logic2187); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:25: v= NOT
                    {
                    v=(CommonTree)match(input,NOT,FOLLOW_NOT_in_logic2191); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:32: (s1= logic | s1= slot )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==LOGIC) ) {
                alt35=1;
            }
            else if ( (LA35_0==SLOT) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:33: s1= logic
                    {
                    pushFollow(FOLLOW_logic_in_logic2197);
                    s1=logic();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:42: s1= slot
                    {
                    pushFollow(FOLLOW_slot_in_logic2201);
                    s1=slot();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:51: (s2= logic | s2= slot )?
            int alt36=3;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LOGIC) ) {
                alt36=1;
            }
            else if ( (LA36_0==SLOT) ) {
                alt36=2;
            }
            switch (alt36) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:52: s2= logic
                    {
                    pushFollow(FOLLOW_logic_in_logic2207);
                    s2=logic();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1162:61: s2= slot
                    {
                    pushFollow(FOLLOW_slot_in_logic2211);
                    s2=slot();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            	LOGGER.debug("got a logical slot " + v + " " + s1 + " " + s2);
            	
            	int op = -1;
             	switch((v!=null?v.getType():0))
             	{
              		case AND : op = ILogicalSlot.AND; break;
              		case OR : op = ILogicalSlot.OR; break;
              		case NOT : op = ILogicalSlot.NOT; break;
              		default : reportException(new BuilderError("Cannot have logical slot of type " + (v!=null?v.getType():0), l));
             	}
             	try {
            		ls = new DefaultLogicalSlot(op, s1, s2);
            	} catch(CannotInstantiateException e) {
            		reportException(new BuilderError("Can only put slots as arguments of DefaultLogicalSlot", l));
            	}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ls;
    }
    // $ANTLR end "logic"


    // $ANTLR start "slot"
    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1181:1: slot returns [ISlot sl] : ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c= LT | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v= STRING | v= NUMBER ) ) ;
    public final ISlot slot() throws RecognitionException {
        ISlot sl = null;

        CommonTree s=null;
        CommonTree n=null;
        CommonTree c=null;
        CommonTree v=null;


         sl=null;

        try {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:1: ( ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c= LT | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v= STRING | v= NUMBER ) ) )
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:3: ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c= LT | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v= STRING | v= NUMBER ) )
            {
            s=(CommonTree)match(input,SLOT,FOLLOW_SLOT_in_slot2237); 

            match(input, Token.DOWN, null); 
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:12: (n= NAME | n= VARIABLE )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==NAME) ) {
                alt37=1;
            }
            else if ( (LA37_0==VARIABLE) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:13: n= NAME
                    {
                    n=(CommonTree)match(input,NAME,FOLLOW_NAME_in_slot2242); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:20: n= VARIABLE
                    {
                    n=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_slot2246); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:32: (c= EQUALS | c= GT | c= GTE | c= LT | c= LTE | c= NOT | c= WITHIN )
            int alt38=7;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt38=1;
                }
                break;
            case GT:
                {
                alt38=2;
                }
                break;
            case GTE:
                {
                alt38=3;
                }
                break;
            case LT:
                {
                alt38=4;
                }
                break;
            case LTE:
                {
                alt38=5;
                }
                break;
            case NOT:
                {
                alt38=6;
                }
                break;
            case WITHIN:
                {
                alt38=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:33: c= EQUALS
                    {
                    c=(CommonTree)match(input,EQUALS,FOLLOW_EQUALS_in_slot2252); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:42: c= GT
                    {
                    c=(CommonTree)match(input,GT,FOLLOW_GT_in_slot2256); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:47: c= GTE
                    {
                    c=(CommonTree)match(input,GTE,FOLLOW_GTE_in_slot2260); 

                    }
                    break;
                case 4 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:53: c= LT
                    {
                    c=(CommonTree)match(input,LT,FOLLOW_LT_in_slot2264); 

                    }
                    break;
                case 5 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:58: c= LTE
                    {
                    c=(CommonTree)match(input,LTE,FOLLOW_LTE_in_slot2268); 

                    }
                    break;
                case 6 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:64: c= NOT
                    {
                    c=(CommonTree)match(input,NOT,FOLLOW_NOT_in_slot2272); 

                    }
                    break;
                case 7 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1188:70: c= WITHIN
                    {
                    c=(CommonTree)match(input,WITHIN,FOLLOW_WITHIN_in_slot2276); 

                    }
                    break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1189:23: (v= IDENTIFIER | v= VARIABLE | v= STRING | v= NUMBER )
            int alt39=4;
            switch ( input.LA(1) ) {
            case IDENTIFIER:
                {
                alt39=1;
                }
                break;
            case VARIABLE:
                {
                alt39=2;
                }
                break;
            case STRING:
                {
                alt39=3;
                }
                break;
            case NUMBER:
                {
                alt39=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1189:24: v= IDENTIFIER
                    {
                    v=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_slot2305); 

                    }
                    break;
                case 2 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1190:24: v= VARIABLE
                    {
                    v=(CommonTree)match(input,VARIABLE,FOLLOW_VARIABLE_in_slot2332); 

                    }
                    break;
                case 3 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1190:35: v= STRING
                    {
                    v=(CommonTree)match(input,STRING,FOLLOW_STRING_in_slot2336); 

                    }
                    break;
                case 4 :
                    // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/builder/JACTRBuilder.g:1190:44: v= NUMBER
                    {
                    v=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_slot2340); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

             LOGGER.debug("got slot def "+(n!=null?n.getText():null)+" "+(c!=null?c.getText():null)+" "+(v!=null?v.getText():null));
             String slotName = (n!=null?n.getText():null);
             int condition = IConditionalSlot.EQUALS;
             switch((c!=null?c.getType():0))
             {
              case GT : condition = IConditionalSlot.GREATER_THAN; break;
              case GTE : condition = IConditionalSlot.GREATER_THAN_EQUALS; break;
              case LT : condition = IConditionalSlot.LESS_THAN; break;
              case LTE : condition = IConditionalSlot.LESS_THAN_EQUALS; break;
              case NOT : condition = IConditionalSlot.NOT_EQUALS; break;
              case WITHIN : condition = IConditionalSlot.WITHIN; break;
              default : condition = IConditionalSlot.EQUALS;
             }
             
             if((c!=null?c.getType():0)==WITHIN)
              reportException(new BuilderError("Within is not currently supported", c));
             

             Object value = null;
             if((v!=null?v.getType():0) == NUMBER)
             try
             {
              value = Double.parseDouble((v!=null?v.getText():null));
             }
             catch(NumberFormatException nfe)
             {
              reportException(new BuilderWarning("Could not create number from "+(v!=null?v.getText():null)+" assuming its a string", v, nfe));
              value = (v!=null?v.getText():null);
             }
             else if((v!=null?v.getType():0) == STRING)
             {
              //we pass a stringBuilder if it is to be a string to differentiate
              // it from an unresolved string reference
              value = new StringBuilder((v!=null?v.getText():null));
              //value = (v!=null?v.getText():null);
             }
             else
              {
               //we will attempt resolution.. but we may still need to do it again later
               //first we try the chunks
               try
               {
                value = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunk((v!=null?v.getText():null)).get();
               }
               catch(Exception e)
               {
                LOGGER.error("Could not get chunk "+(v!=null?v.getText():null), e);
               }
               
               if(value==null) //then chunktypes
                try
                {
                 value = ((Model_scope)Model_stack.peek()).model.getDeclarativeModule().getChunkType((v!=null?v.getText():null)).get();
                }
                catch(Exception e)
                {
                 LOGGER.error("Could not get chunktype "+(v!=null?v.getText():null),e);
                } 
                
               if(value==null) //then production??
                try
                {
                 value = ((Model_scope)Model_stack.peek()).model.getProceduralModule().getProduction((v!=null?v.getText():null)).get(); 
                }
                catch(Exception e)
                {
                 LOGGER.error("Could not get production "+(v!=null?v.getText():null),e);
                } 
                
               //if we are still null, then we need to hold
               // on for resolution later
               if(value==null)
                value = resolveKeywords((v!=null?v.getText():null));
              }
              if(slotName.equalsIgnoreCase("isa")) {
             	//special case here
             	LOGGER.error("testing ISA conditions; condition is " + condition + " and value isa " + value.getClass());
             	if (!(value instanceof IChunkType)) {
             		reportException(new BuilderError("isa slot test must have a chunk-type as a value.", v));
             	}
             }
             
              if((n!=null?n.getType():0)==VARIABLE)
               sl = new DefaultVariableConditionalSlot(slotName, condition, value);
              else
               sl = new DefaultConditionalSlot(slotName, condition, value);
             
             if(LOGGER.isDebugEnabled())
              LOGGER.debug("created slot "+sl);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return sl;
    }
    // $ANTLR end "slot"

    // Delegated rules


 

    public static final BitSet FOLLOW_MODEL_in_model330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_model334 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_modules_in_model346 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_extensions_in_model348 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_buffers_in_model350 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_library_in_model352 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_model356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LIBRARY_in_library370 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_declarativeMemory_in_library372 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_proceduralMemory_in_library374 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory391 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunkType_in_declarativeMemory408 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory431 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_production_in_proceduralMemory433 = new BitSet(new long[]{0x0000000000008008L});
    public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODULES_in_modules451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_module_in_modules453 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_MODULES_in_modules459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODULE_in_module477 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_module481 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_module485 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions500 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_extension_in_extensions502 = new BitSet(new long[]{0x0000000000000208L});
    public static final BitSet FOLLOW_EXTENSIONS_in_extensions508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENSION_in_extension523 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_extension527 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_extension531 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers547 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_buffer_in_buffers550 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_BUFFERS_in_buffers556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BUFFER_in_buffer572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_buffer576 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_chunks_in_buffer578 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_buffer582 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_in_chunkType605 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunkType609 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_parents_in_chunkType613 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_slots_in_chunkType618 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_chunks_in_chunkType625 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_chunkType630 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks654 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_chunk_in_chunks659 = new BitSet(new long[]{0x0000008000002008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_chunks691 = new BitSet(new long[]{0x0000008000002008L});
    public static final BitSet FOLLOW_CHUNKS_in_chunks727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHUNK_in_chunk748 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_chunk752 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_PARENT_in_chunk756 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_slots_in_chunk760 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_chunk764 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PRODUCTION_in_production787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_production791 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_conditions_in_production795 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_actions_in_production799 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_parameters_in_production803 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONDITIONS_in_conditions825 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_check_in_conditions830 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_query_in_conditions850 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_scriptCond_in_conditions870 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_proxyCond_in_conditions890 = new BitSet(new long[]{0x0000000001E00008L});
    public static final BitSet FOLLOW_ACTIONS_in_actions926 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_add_in_actions931 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_set_in_actions956 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_remove_in_actions981 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_modify_in_actions1006 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_scriptAct_in_actions1031 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_proxyAct_in_actions1056 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_output_in_actions1081 = new BitSet(new long[]{0x00000001FC000008L});
    public static final BitSet FOLLOW_MATCH_CONDITION_in_check1124 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_check1128 = new BitSet(new long[]{0x0000018800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_check1152 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_check1176 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_check1216 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_check1275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNKNOWN_in_unknownList1329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_QUERY_CONDITION_in_query1351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_query1355 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_slots_in_query1359 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_CONDITION_in_scriptCond1380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptCond1384 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptCond1388 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_CONDITION_in_proxyCond1410 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyCond1414 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyCond1419 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ADD_ACTION_in_add1455 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_add1459 = new BitSet(new long[]{0x0000018800000000L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_add1482 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add1524 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_add1564 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_add1607 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SET_ACTION_in_set1633 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_set1637 = new BitSet(new long[]{0x0000028800000008L});
    public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_set1660 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_set1714 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_set1758 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REMOVE_ACTION_in_remove1782 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_remove1786 = new BitSet(new long[]{0x0000024800000008L});
    public static final BitSet FOLLOW_IDENTIFIER_in_remove1808 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_remove1830 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_remove1855 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFY_ACTION_in_modify1876 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_modify1880 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_modify1884 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCRIPTABLE_ACTION_in_scriptAct1907 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LANG_in_scriptAct1911 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_SCRIPT_in_scriptAct1915 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROXY_ACTION_in_proxyAct1936 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CLASS_SPEC_in_proxyAct1940 = new BitSet(new long[]{0x0000020000000008L});
    public static final BitSet FOLLOW_slots_in_proxyAct1945 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OUTPUT_ACTION_in_output1976 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_output1980 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARENTS_in_parents2005 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PARENT_in_parents2014 = new BitSet(new long[]{0x0080000000000008L});
    public static final BitSet FOLLOW_PARENTS_in_parents2038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLOTS_in_slots2057 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_slot_in_slots2062 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_slots2072 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_SLOTS_in_slots2084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters2108 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_parameter_in_parameters2113 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_PARAMETERS_in_parameters2122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAMETER_in_parameter2147 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_parameter2149 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_STRING_in_parameter2151 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LOGIC_in_logic2178 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_AND_in_logic2183 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_OR_in_logic2187 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_NOT_in_logic2191 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_logic2197 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_slot_in_logic2201 = new BitSet(new long[]{0x0010040000000008L});
    public static final BitSet FOLLOW_logic_in_logic2207 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_slot_in_logic2211 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SLOT_in_slot2237 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NAME_in_slot2242 = new BitSet(new long[]{0x0003F80000000000L});
    public static final BitSet FOLLOW_VARIABLE_in_slot2246 = new BitSet(new long[]{0x0003F80000000000L});
    public static final BitSet FOLLOW_EQUALS_in_slot2252 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GT_in_slot2256 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_GTE_in_slot2260 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LT_in_slot2264 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_LTE_in_slot2268 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_NOT_in_slot2272 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_WITHIN_in_slot2276 = new BitSet(new long[]{0x0000007800000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_slot2305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARIABLE_in_slot2332 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_slot2336 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMBER_in_slot2340 = new BitSet(new long[]{0x0000000000000008L});

}