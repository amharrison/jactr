// $ANTLR 3.2 Sep 23, 2009 12:02:23
// /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g
// 2011-11-07 09:22:24

package org.jactr.io.antlr3.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

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
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.compiler.IUnitCompiler;

public class JACTRCompiler extends TreeParser
{
  public static final String[] tokenNames            = new String[] {
      "<invalid>", "<EOR>", "<DOWN>", "<UP>", "MODEL", "LIBRARY", "MODULES",
      "MODULE", "EXTENSIONS", "EXTENSION", "DECLARATIVE_MEMORY", "CHUNK_TYPE",
      "CHUNKS", "CHUNK", "PROCEDURAL_MEMORY", "PRODUCTION", "PARAMETERS",
      "PARAMETER", "BUFFERS", "BUFFER", "CONDITIONS", "MATCH_CONDITION",
      "QUERY_CONDITION", "SCRIPTABLE_CONDITION", "PROXY_CONDITION", "ACTIONS",
      "ADD_ACTION", "SET_ACTION", "REMOVE_ACTION", "MODIFY_ACTION",
      "OUTPUT_ACTION", "SCRIPTABLE_ACTION", "PROXY_ACTION", "LANG", "SCRIPT",
      "VARIABLE", "STRING", "NUMBER", "IDENTIFIER", "CHUNK_IDENTIFIER",
      "CHUNK_TYPE_IDENTIFIER", "SLOTS", "SLOT", "LT", "GT", "EQUALS", "NOT",
      "WITHIN", "GTE", "LTE", "OR", "AND", "LOGIC", "CLASS_SPEC", "NAME",
      "PARENT", "PARENTS", "UNKNOWN"                };

  public static final int      LT                    = 43;

  public static final int      LOGIC                 = 52;

  public static final int      PARAMETERS            = 16;

  public static final int      SCRIPTABLE_ACTION     = 31;

  public static final int      CHUNK                 = 13;

  public static final int      GTE                   = 48;

  public static final int      PROXY_CONDITION       = 24;

  public static final int      LIBRARY               = 5;

  public static final int      EQUALS                = 45;

  public static final int      CHUNK_TYPE            = 11;

  public static final int      NOT                   = 46;

  public static final int      AND                   = 51;

  public static final int      EOF                   = -1;

  public static final int      LTE                   = 49;

  public static final int      ADD_ACTION            = 26;

  public static final int      ACTIONS               = 25;

  public static final int      PARENT                = 55;

  public static final int      NAME                  = 54;

  public static final int      EXTENSIONS            = 8;

  public static final int      UNKNOWN               = 57;

  public static final int      PROCEDURAL_MEMORY     = 14;

  public static final int      IDENTIFIER            = 38;

  public static final int      PARAMETER             = 17;

  public static final int      PRODUCTION            = 15;

  public static final int      DECLARATIVE_MEMORY    = 10;

  public static final int      MODEL                 = 4;

  public static final int      MODULES               = 6;

  public static final int      REMOVE_ACTION         = 28;

  public static final int      MATCH_CONDITION       = 21;

  public static final int      SCRIPT                = 34;

  public static final int      PROXY_ACTION          = 32;

  public static final int      CHUNK_IDENTIFIER      = 39;

  public static final int      NUMBER                = 37;

  public static final int      BUFFER                = 19;

  public static final int      MODULE                = 7;

  public static final int      CONDITIONS            = 20;

  public static final int      SCRIPTABLE_CONDITION  = 23;

  public static final int      CHUNK_TYPE_IDENTIFIER = 40;

  public static final int      PARENTS               = 56;

  public static final int      MODIFY_ACTION         = 29;

  public static final int      VARIABLE              = 35;

  public static final int      CLASS_SPEC            = 53;

  public static final int      SLOT                  = 42;

  public static final int      BUFFERS               = 18;

  public static final int      OR                    = 50;

  public static final int      CHUNKS                = 12;

  public static final int      WITHIN                = 47;

  public static final int      SLOTS                 = 41;

  public static final int      GT                    = 44;

  public static final int      SET_ACTION            = 27;

  public static final int      QUERY_CONDITION       = 22;

  public static final int      EXTENSION             = 9;

  public static final int      OUTPUT_ACTION         = 30;

  public static final int      STRING                = 36;

  public static final int      LANG                  = 33;

  // delegates
  // delegators

  protected static class VariableBindings_scope
  {
    /**
     * used within a production this is a mapping of buffer names to valid slot
     * names pulled from Slots scope currentSlotNames
     */
    Map<String, Set<String>> validSlotNames;

    Set<String>              validVariables;

    boolean                  hasScriptable;
  }

  protected Stack VariableBindings_stack = new Stack();

  protected static class Library_scope
  {
    Map<String, CommonTree> knownChunkTypes;

    Map<String, CommonTree> knownChunks;

    Map<String, CommonTree> knownBufferMap;

    Set<String>             knownProductions;

    Map<String, CommonTree> encounteredChunkTypes;

    Collection<CommonTree>  identifiersPendingResolution;
  }

  protected Stack Library_stack = new Stack();

  protected static class Slots_scope
  {
    String      slotDefiner;

    Set<String> currentSlotNames;
  }

  protected Stack Slots_stack = new Stack();

  public JACTRCompiler(TreeNodeStream input)
  {
    this(input, new RecognizerSharedState());
  }

  public JACTRCompiler(TreeNodeStream input, RecognizerSharedState state)
  {
    super(input, state);

  }

  @Override
  public String[] getTokenNames()
  {
    return JACTRCompiler.tokenNames;
  }

  @Override
  public String getGrammarFileName()
  {
    return "/Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g";
  }

  static private final transient Log              LOGGER    = LogFactory
                                                                .getLog(JACTRCompiler.class);

  private Collection<Exception>                   _warnings = new ArrayList<Exception>();

  private Collection<Exception>                   _errors   = new ArrayList<Exception>();

  private Collection<Exception>                   _infos    = new ArrayList<Exception>();

  private Map<Integer, Collection<IUnitCompiler>> _unitCompilerMap;

  public void setUnitCompilerMap(Map<Integer, Collection<IUnitCompiler>> map)
  {
    _unitCompilerMap = map;
  }

  protected void delegate(CommonTree node)
  {
    if (node == null) return;
    if (_unitCompilerMap == null) return;

    Collection<IUnitCompiler> compilers = _unitCompilerMap.get(node.getType());
    if (compilers != null) for (IUnitCompiler compiler : compilers)
      compiler.compile(node, _infos, _warnings, _errors);
  }

  @Override
  public void reportError(RecognitionException re)
  {
    LOGGER.debug(re.getMessage() + ":" + re.line + "," + re.c + " " + re.token);
    reportException(re);
  }

  public void reportException(Exception e)
  {
    LOGGER.debug("", e);

    if (e instanceof CompilationWarning)
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

  protected Set<String> getCurrentSlotsFromChunkTypeName(
      CommonTree chunkTypeNameNode, Map<String, CommonTree> allChunkTypes)
  {
    Set<String> rtn = new TreeSet<String>();

    String chunkType = chunkTypeNameNode.getText().toLowerCase();

    LOGGER.debug("Getting slots for " + chunkType);

    CommonTree chunkTypeNode = allChunkTypes.get(chunkType);
    if (chunkTypeNode == null)
      throw new CompilationError("Could not find chunk-type " + chunkType,
          chunkTypeNameNode);

    CommonTree slotsNode = (CommonTree) chunkTypeNode
        .getFirstChildWithType(SLOTS);

    for (int i = 0; i < slotsNode.getChildCount(); i++)
    {
      CommonTree slotNode = (CommonTree) slotsNode.getChild(i);
      String slotName = ASTSupport.getFirstDescendantWithType(slotNode, NAME)
          .getText();
      rtn.add(slotName.toLowerCase());
    }

    CommonTree parentsNode = (CommonTree) chunkTypeNode
        .getFirstChildWithType(PARENTS);
    for (int i = 0; i < parentsNode.getChildCount(); i++)
    {
      CommonTree parent = (CommonTree) parentsNode.getChild(i);
      rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
      LOGGER.debug("Getting my parent's slots " + parent.getText());
    }

    // CommonTree parent = (CommonTree)
    // chunkTypeNode.getFirstChildWithType(PARENT);
    // LOGGER.debug("Got parent "+parent);

    // if(parent!=null)
    // {
    // LOGGER.debug("Getting my parent's slots "+parent.getText());
    // rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
    // }

    LOGGER.debug(chunkType + " has the following slots " + rtn);
    return rtn;
  }

  protected Set<String> getCurrentSlotsFromChunkName(CommonTree chunkNameNode,
      Map<String, CommonTree> allChunks, Map<String, CommonTree> allChunkTypes)
  {
    String chunk = chunkNameNode.getText().toLowerCase();
    CommonTree chunkNode = allChunks.get(chunk);
    if (chunkNode == null)
      throw new CompilationError("Could not find chunk " + chunk, chunkNameNode);

    Set<String> rtn = new TreeSet<String>();
    CommonTree parent = (CommonTree) chunkNode.getFirstChildWithType(PARENT);
    if (parent != null)
      rtn.addAll(getCurrentSlotsFromChunkTypeName(parent, allChunkTypes));
    return rtn;
  }

  protected Set<String> getCurrentSlotNames(CommonTree chunkOrTypeNode,
      Map<String, CommonTree> allChunks, Map<String, CommonTree> allChunkTypes)
  {
    try
    {
      return getCurrentSlotsFromChunkName(chunkOrTypeNode, allChunks,
          allChunkTypes);
    }
    catch (CompilationError ce1)
    {
      try
      {
        return getCurrentSlotsFromChunkTypeName(chunkOrTypeNode, allChunkTypes);
      }
      catch (CompilationError ce2)
      {
        reportException(ce1);
        reportException(ce2);
        return new TreeSet<String>();
      }
    }
  }

  /**
   * check the slots defined against those that can be defined, optionally
   * taking care of any variable slot names
   */
  protected void validateSlotNames(String slotDefiner,
      Set<String> validSlotNames, Collection<CommonTree> referencedSlots,
      Set<String> definedVariables)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Valid slots for " + slotDefiner + " " + validSlotNames);
    for (CommonTree slot : referencedSlots)
    {
      CommonTree id = (CommonTree) slot.getChild(0);
      String slotName = id.getText().toLowerCase();
      String value = slotName;

      if (value.startsWith("=")) value = value.substring(1, value.length());

      if (id.getType() == VARIABLE)
      {
        if (!definedVariables.contains(value))
        {
          reportException(new CompilationError(
              String.format(
                  "Could not resolve variable slot name %s = %s. Slots: %s, Variables: %s",
                  slotName, value, validSlotNames, definedVariables), id));
          continue;
        }
      }
      else if (!slotName.startsWith(":") && !validSlotNames.contains(slotName))
        reportException(new CompilationError(slotName
            + " is not a valid slot for " + slotDefiner + ". Possible :"
            + validSlotNames, id));
    }
  }

  protected void validateVariables(Set<String> definedVariables,
      Collection<CommonTree> referencedSlots, boolean canDefine,
      boolean hasScriptable)
  {
    for (CommonTree slot : referencedSlots)
      /*
       * the commented out bit below only works for variable values, we actually
       * need all the variables, in case of variable slot name
       */
      // CommonTree variable = ASTSupport.getFirstDescendantWithType(slot,
      // VARIABLE);
      // if(variable!=null)
      for (CommonTree variable : ASTSupport.getAllDescendantsWithType(slot,
          VARIABLE))
      {
        /*
         * the slot defines a variable
         */
        String variableName = variable.getText();
        variableName = variableName.substring(1, variableName.length())
            .toLowerCase();

        if (!definedVariables.contains(variableName))
          if (canDefine)
            definedVariables.add(variableName);
          else if (hasScriptable)
            reportException(new CompilationWarning(
                variableName
                    + " was not bound in the left hand side, assuming it was defined in the scriptable condition",
                variable));
          else
            reportException(new CompilationError(variableName
                + " was not bound in the left hand side, valid variables "
                + definedVariables, variable));
      }
  }

  private String partialStream(String[] stream, int index)
  {
    String result = "";
    for (int i = index + 1; i < stream.length; i++)
      result += stream[i] + " ";
    return result;
  }

  // $ANTLR start "model"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:331:1:
  // model : ^(m= MODEL name= NAME modules extensions buffers library parameters
  // ) ;
  public final void model() throws RecognitionException
  {
    Library_stack.push(new Library_scope());

    CommonTree m = null;
    CommonTree name = null;

    ((Library_scope) Library_stack.peek()).knownBufferMap = new HashMap<String, CommonTree>();
    ((Library_scope) Library_stack.peek()).knownChunkTypes = new HashMap<String, CommonTree>();
    ((Library_scope) Library_stack.peek()).encounteredChunkTypes = new HashMap<String, CommonTree>();
    ((Library_scope) Library_stack.peek()).knownChunks = new HashMap<String, CommonTree>();
    ((Library_scope) Library_stack.peek()).identifiersPendingResolution = new ArrayList<CommonTree>();
    ((Library_scope) Library_stack.peek()).knownProductions = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:342:2:
      // ( ^(m= MODEL name= NAME modules extensions buffers library parameters )
      // )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:342:4:
      // ^(m= MODEL name= NAME modules extensions buffers library parameters )
      {
        m = (CommonTree) match(input, MODEL, FOLLOW_MODEL_in_model348);

        match(input, Token.DOWN, null);
        name = (CommonTree) match(input, NAME, FOLLOW_NAME_in_model352);
        pushFollow(FOLLOW_modules_in_model354);
        modules();

        state._fsp--;

        pushFollow(FOLLOW_extensions_in_model356);
        extensions();

        state._fsp--;

        pushFollow(FOLLOW_buffers_in_model358);
        buffers();

        state._fsp--;

        pushFollow(FOLLOW_library_in_model360);
        library();

        state._fsp--;

        pushFollow(FOLLOW_parameters_in_model362);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        LOGGER.debug("got model def for "
            + (name != null ? name.getText() : null));
        delegate(name);

        /*
         * check the contents of the buffers
         */
        for (CommonTree buffer : ((Library_scope) Library_stack.peek()).knownBufferMap
            .values())
        {
          Map<String, CommonTree> chunks = ASTSupport.getMapOfTrees(buffer,
              CHUNK_IDENTIFIER);
          for (String chunkName : chunks.keySet())
            if (!((Library_scope) Library_stack.peek()).knownChunks
                .containsKey(chunkName.toLowerCase()))
              reportException(new CompilationError(chunkName
                  + " is not a known chunk",
                  chunks.get(chunkName.toLowerCase())));
        }

        /*
         * now take care of the final resolutions
         */
        for (CommonTree id : ((Library_scope) Library_stack.peek()).identifiersPendingResolution)
        {
          String idName = id.getText().toLowerCase();
          if (!(idName.equalsIgnoreCase("nil")
              || idName.equalsIgnoreCase("null")
              || idName.equalsIgnoreCase("t")
              || idName.equalsIgnoreCase("true") || idName
                .equalsIgnoreCase("false")))
            if (!((Library_scope) Library_stack.peek()).knownChunks
                .containsKey(idName)
                && !((Library_scope) Library_stack.peek()).knownChunkTypes
                    .containsKey(idName)
                && !((Library_scope) Library_stack.peek()).knownProductions
                    .contains(idName))
              reportException(new CompilationError("Unknown chunk " + idName,
                  id));
        }

        delegate(m);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Library_stack.pop();

    }
    return;
  }

  // $ANTLR end "model"

  // $ANTLR start "library"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:375:1:
  // library : ^(l= LIBRARY declarativeMemory proceduralMemory ) ;
  public final void library() throws RecognitionException
  {
    CommonTree l = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:376:1:
      // ( ^(l= LIBRARY declarativeMemory proceduralMemory ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:376:3:
      // ^(l= LIBRARY declarativeMemory proceduralMemory )
      {
        l = (CommonTree) match(input, LIBRARY, FOLLOW_LIBRARY_in_library376);

        /*
         * snag all the known chunk types. this needs to be done before the
         * compilation checks on the chunktypes. As such, we snag everyone
         * here..
         */
        Map<String, CommonTree> known = ASTSupport.getMapOfTrees(l, CHUNK_TYPE);
        ((Library_scope) Library_stack.peek()).knownChunkTypes.putAll(known);

        match(input, Token.DOWN, null);
        pushFollow(FOLLOW_declarativeMemory_in_library381);
        declarativeMemory();

        state._fsp--;

        pushFollow(FOLLOW_proceduralMemory_in_library383);
        proceduralMemory();

        state._fsp--;

        match(input, Token.UP, null);
        delegate(l);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "library"

  // $ANTLR start "declarativeMemory"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:386:1:
  // declarativeMemory : ( ^(d= DECLARATIVE_MEMORY ( chunkType )+ ) | d=
  // DECLARATIVE_MEMORY ) ;
  public final void declarativeMemory() throws RecognitionException
  {
    CommonTree d = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:2:
      // ( ( ^(d= DECLARATIVE_MEMORY ( chunkType )+ ) | d= DECLARATIVE_MEMORY )
      // )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:4:
      // ( ^(d= DECLARATIVE_MEMORY ( chunkType )+ ) | d= DECLARATIVE_MEMORY )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:4:
        // ( ^(d= DECLARATIVE_MEMORY ( chunkType )+ ) | d= DECLARATIVE_MEMORY )
        int alt2 = 2;
        int LA2_0 = input.LA(1);

        if (LA2_0 == DECLARATIVE_MEMORY)
        {
          int LA2_1 = input.LA(2);

          if (LA2_1 == DOWN)
            alt2 = 1;
          else if (LA2_1 == PROCEDURAL_MEMORY)
            alt2 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 2, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

          throw nvae;
        }
        switch (alt2)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:5:
          // ^(d= DECLARATIVE_MEMORY ( chunkType )+ )
          {
            d = (CommonTree) match(input, DECLARATIVE_MEMORY,
                FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory400);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:29:
            // ( chunkType )+
            int cnt1 = 0;
            loop1: do
            {
              int alt1 = 2;
              int LA1_0 = input.LA(1);

              if (LA1_0 == CHUNK_TYPE) alt1 = 1;

              switch (alt1)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:29:
                // chunkType
                {
                  pushFollow(FOLLOW_chunkType_in_declarativeMemory403);
                  chunkType();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt1 >= 1) break loop1;
                  EarlyExitException eee = new EarlyExitException(1, input);
                  throw eee;
              }
              cnt1++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:387:43:
          // d= DECLARATIVE_MEMORY
          {
            d = (CommonTree) match(input, DECLARATIVE_MEMORY,
                FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory411);

          }
            break;

        }

        delegate(d);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "declarativeMemory"

  // $ANTLR start "proceduralMemory"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:392:1:
  // proceduralMemory : ( ^(p= PROCEDURAL_MEMORY ( production )+ ) | p=
  // PROCEDURAL_MEMORY ) ;
  public final void proceduralMemory() throws RecognitionException
  {
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:2:
      // ( ( ^(p= PROCEDURAL_MEMORY ( production )+ ) | p= PROCEDURAL_MEMORY ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:4:
      // ( ^(p= PROCEDURAL_MEMORY ( production )+ ) | p= PROCEDURAL_MEMORY )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:4:
        // ( ^(p= PROCEDURAL_MEMORY ( production )+ ) | p= PROCEDURAL_MEMORY )
        int alt4 = 2;
        int LA4_0 = input.LA(1);

        if (LA4_0 == PROCEDURAL_MEMORY)
        {
          int LA4_1 = input.LA(2);

          if (LA4_1 == DOWN)
            alt4 = 1;
          else if (LA4_1 == UP)
            alt4 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 4, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 4, 0, input);

          throw nvae;
        }
        switch (alt4)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:5:
          // ^(p= PROCEDURAL_MEMORY ( production )+ )
          {
            p = (CommonTree) match(input, PROCEDURAL_MEMORY,
                FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory429);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:27:
            // ( production )+
            int cnt3 = 0;
            loop3: do
            {
              int alt3 = 2;
              int LA3_0 = input.LA(1);

              if (LA3_0 == PRODUCTION) alt3 = 1;

              switch (alt3)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:28:
                // production
                {
                  pushFollow(FOLLOW_production_in_proceduralMemory432);
                  production();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt3 >= 1) break loop3;
                  EarlyExitException eee = new EarlyExitException(3, input);
                  throw eee;
              }
              cnt3++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:393:44:
          // p= PROCEDURAL_MEMORY
          {
            p = (CommonTree) match(input, PROCEDURAL_MEMORY,
                FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory441);

          }
            break;

        }

        delegate(p);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "proceduralMemory"

  // $ANTLR start "modules"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:1:
  // modules : ( ^( MODULES ( module )+ ) | MODULES ) ;
  public final void modules() throws RecognitionException
  {
    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:9:
      // ( ( ^( MODULES ( module )+ ) | MODULES ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:11:
      // ( ^( MODULES ( module )+ ) | MODULES )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:11:
        // ( ^( MODULES ( module )+ ) | MODULES )
        int alt6 = 2;
        int LA6_0 = input.LA(1);

        if (LA6_0 == MODULES)
        {
          int LA6_1 = input.LA(2);

          if (LA6_1 == DOWN)
            alt6 = 1;
          else if (LA6_1 == EXTENSIONS)
            alt6 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 6, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

          throw nvae;
        }
        switch (alt6)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:13:
          // ^( MODULES ( module )+ )
          {
            match(input, MODULES, FOLLOW_MODULES_in_modules456);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:23:
            // ( module )+
            int cnt5 = 0;
            loop5: do
            {
              int alt5 = 2;
              int LA5_0 = input.LA(1);

              if (LA5_0 == MODULE) alt5 = 1;

              switch (alt5)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:23:
                // module
                {
                  pushFollow(FOLLOW_module_in_modules458);
                  module();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt5 >= 1) break loop5;
                  EarlyExitException eee = new EarlyExitException(5, input);
                  throw eee;
              }
              cnt5++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:398:34:
          // MODULES
          {
            match(input, MODULES, FOLLOW_MODULES_in_modules464);

          }
            break;

        }

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "modules"

  // $ANTLR start "module"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:400:1:
  // module : ^(m= MODULE c= CLASS_SPEC parameters ) ;
  public final void module() throws RecognitionException
  {
    CommonTree m = null;
    CommonTree c = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:400:9:
      // ( ^(m= MODULE c= CLASS_SPEC parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:400:11:
      // ^(m= MODULE c= CLASS_SPEC parameters )
      {
        m = (CommonTree) match(input, MODULE, FOLLOW_MODULE_in_module478);

        match(input, Token.DOWN, null);
        c = (CommonTree) match(input, CLASS_SPEC,
            FOLLOW_CLASS_SPEC_in_module482);
        pushFollow(FOLLOW_parameters_in_module484);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        LOGGER.debug("Got module " + (c != null ? c.getText() : null));
        // check the class Name
        // String className = (c!=null?c.getText():null);
        // try
        // {
        // getClass().getClassLoader().loadClass(className);
        // }
        // catch(Exception e)
        // {
        // reportException( new
        // CompilationError("Could not load class "+className, c, e));
        // }
        delegate(m);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "module"

  // $ANTLR start "extensions"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:416:1:
  // extensions : ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS ) ;
  public final void extensions() throws RecognitionException
  {
    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:2:
      // ( ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:4:
      // ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:4:
        // ( ^( EXTENSIONS ( extension )+ ) | EXTENSIONS )
        int alt8 = 2;
        int LA8_0 = input.LA(1);

        if (LA8_0 == EXTENSIONS)
        {
          int LA8_1 = input.LA(2);

          if (LA8_1 == DOWN)
            alt8 = 1;
          else if (LA8_1 == BUFFERS)
            alt8 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 8, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

          throw nvae;
        }
        switch (alt8)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:5:
          // ^( EXTENSIONS ( extension )+ )
          {
            match(input, EXTENSIONS, FOLLOW_EXTENSIONS_in_extensions499);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:18:
            // ( extension )+
            int cnt7 = 0;
            loop7: do
            {
              int alt7 = 2;
              int LA7_0 = input.LA(1);

              if (LA7_0 == EXTENSION) alt7 = 1;

              switch (alt7)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:18:
                // extension
                {
                  pushFollow(FOLLOW_extension_in_extensions501);
                  extension();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt7 >= 1) break loop7;
                  EarlyExitException eee = new EarlyExitException(7, input);
                  throw eee;
              }
              cnt7++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:417:32:
          // EXTENSIONS
          {
            match(input, EXTENSIONS, FOLLOW_EXTENSIONS_in_extensions507);

          }
            break;

        }

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "extensions"

  // $ANTLR start "extension"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:419:1:
  // extension : ^(ex= EXTENSION c= CLASS_SPEC parameters ) ;
  public final void extension() throws RecognitionException
  {
    CommonTree ex = null;
    CommonTree c = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:420:2:
      // ( ^(ex= EXTENSION c= CLASS_SPEC parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:420:4:
      // ^(ex= EXTENSION c= CLASS_SPEC parameters )
      {
        ex = (CommonTree) match(input, EXTENSION,
            FOLLOW_EXTENSION_in_extension521);

        match(input, Token.DOWN, null);
        c = (CommonTree) match(input, CLASS_SPEC,
            FOLLOW_CLASS_SPEC_in_extension525);
        pushFollow(FOLLOW_parameters_in_extension527);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        LOGGER.debug("Got extension " + (c != null ? c.getText() : null));
        // check the class Name
        // String className = (c!=null?c.getText():null);
        // try
        // {
        // getClass().getClassLoader().loadClass(className);
        // }
        // catch(Exception e)
        // {
        // reportException( new
        // CompilationError("Could not load class "+className, c, e));
        // }
        delegate(ex);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "extension"

  // $ANTLR start "buffers"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:436:1:
  // buffers : ( ^( BUFFERS ( buffer )+ ) | BUFFERS ) ;
  public final void buffers() throws RecognitionException
  {
    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:1:
      // ( ( ^( BUFFERS ( buffer )+ ) | BUFFERS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:3:
      // ( ^( BUFFERS ( buffer )+ ) | BUFFERS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:3:
        // ( ^( BUFFERS ( buffer )+ ) | BUFFERS )
        int alt10 = 2;
        int LA10_0 = input.LA(1);

        if (LA10_0 == BUFFERS)
        {
          int LA10_1 = input.LA(2);

          if (LA10_1 == DOWN)
            alt10 = 1;
          else if (LA10_1 == LIBRARY)
            alt10 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 10, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 10, 0, input);

          throw nvae;
        }
        switch (alt10)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:4:
          // ^( BUFFERS ( buffer )+ )
          {
            match(input, BUFFERS, FOLLOW_BUFFERS_in_buffers543);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:15:
            // ( buffer )+
            int cnt9 = 0;
            loop9: do
            {
              int alt9 = 2;
              int LA9_0 = input.LA(1);

              if (LA9_0 == BUFFER) alt9 = 1;

              switch (alt9)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:15:
                // buffer
                {
                  pushFollow(FOLLOW_buffer_in_buffers546);
                  buffer();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt9 >= 1) break loop9;
                  EarlyExitException eee = new EarlyExitException(9, input);
                  throw eee;
              }
              cnt9++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:440:26:
          // BUFFERS
          {
            match(input, BUFFERS, FOLLOW_BUFFERS_in_buffers552);

          }
            break;

        }

        LOGGER.debug("got buffers tag");

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "buffers"

  // $ANTLR start "buffer"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:445:1:
  // buffer : ^(b= BUFFER name= NAME chunks parameters ) ;
  public final void buffer() throws RecognitionException
  {
    CommonTree b = null;
    CommonTree name = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:445:9:
      // ( ^(b= BUFFER name= NAME chunks parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:445:11:
      // ^(b= BUFFER name= NAME chunks parameters )
      {
        b = (CommonTree) match(input, BUFFER, FOLLOW_BUFFER_in_buffer567);

        match(input, Token.DOWN, null);
        name = (CommonTree) match(input, NAME, FOLLOW_NAME_in_buffer571);
        pushFollow(FOLLOW_chunks_in_buffer573);
        chunks();

        state._fsp--;

        pushFollow(FOLLOW_parameters_in_buffer575);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        delegate(name);
        LOGGER.debug("got buffer name "
            + (name != null ? name.getText() : null));
        ((Library_scope) Library_stack.peek()).knownBufferMap.put(
            (name != null ? name.getText() : null).toLowerCase(), b);
        delegate(b);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "buffer"

  // $ANTLR start "chunkType"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:453:1:
  // chunkType : ^(c= CHUNK_TYPE n= NAME parents slots chunks parameters ) ;
  public final void chunkType() throws RecognitionException
  {
    Slots_stack.push(new Slots_scope());

    CommonTree c = null;
    CommonTree n = null;

    ((Slots_scope) Slots_stack.peek()).currentSlotNames = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:458:2:
      // ( ^(c= CHUNK_TYPE n= NAME parents slots chunks parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:458:4:
      // ^(c= CHUNK_TYPE n= NAME parents slots chunks parameters )
      {
        c = (CommonTree) match(input, CHUNK_TYPE,
            FOLLOW_CHUNK_TYPE_in_chunkType605);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_chunkType609);

        delegate(n);

        String chunkTypeName = (n != null ? n.getText() : null).toLowerCase();
        if (((Library_scope) Library_stack.peek()).encounteredChunkTypes
            .containsKey(chunkTypeName))
          reportException(new CompilationWarning(chunkTypeName
              + " is an already defined chunk-type, redefining", c));

        LOGGER.debug("indexing chunkType " + chunkTypeName);
        ((Library_scope) Library_stack.peek()).encounteredChunkTypes.put(
            chunkTypeName, c);

        ((Slots_scope) Slots_stack.peek()).currentSlotNames
            .addAll(getCurrentSlotsFromChunkTypeName(n,
                ((Library_scope) Library_stack.peek()).knownChunkTypes));
        ((Slots_scope) Slots_stack.peek()).slotDefiner = n != null ? n
            .getText() : null;

        pushFollow(FOLLOW_parents_in_chunkType616);
        parents();

        state._fsp--;

        pushFollow(FOLLOW_slots_in_chunkType618);
        slots();

        state._fsp--;

        pushFollow(FOLLOW_chunks_in_chunkType622);
        chunks();

        state._fsp--;

        pushFollow(FOLLOW_parameters_in_chunkType624);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        delegate(c);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Slots_stack.pop();

    }
    return;
  }

  // $ANTLR end "chunkType"

  // $ANTLR start "chunks"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:478:1:
  // chunks : ( ^( CHUNKS ( chunk | cid= CHUNK_IDENTIFIER )+ ) | CHUNKS ) ;
  public final void chunks() throws RecognitionException
  {
    CommonTree cid = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:9:
      // ( ( ^( CHUNKS ( chunk | cid= CHUNK_IDENTIFIER )+ ) | CHUNKS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:11:
      // ( ^( CHUNKS ( chunk | cid= CHUNK_IDENTIFIER )+ ) | CHUNKS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:11:
        // ( ^( CHUNKS ( chunk | cid= CHUNK_IDENTIFIER )+ ) | CHUNKS )
        int alt12 = 2;
        int LA12_0 = input.LA(1);

        if (LA12_0 == CHUNKS)
        {
          int LA12_1 = input.LA(2);

          if (LA12_1 == DOWN)
            alt12 = 1;
          else if (LA12_1 == PARAMETERS)
            alt12 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 12, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 12, 0, input);

          throw nvae;
        }
        switch (alt12)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:13:
          // ^( CHUNKS ( chunk | cid= CHUNK_IDENTIFIER )+ )
          {
            match(input, CHUNKS, FOLLOW_CHUNKS_in_chunks642);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:22:
            // ( chunk | cid= CHUNK_IDENTIFIER )+
            int cnt11 = 0;
            loop11: do
            {
              int alt11 = 3;
              int LA11_0 = input.LA(1);

              if (LA11_0 == CHUNK)
                alt11 = 1;
              else if (LA11_0 == CHUNK_IDENTIFIER) alt11 = 2;

              switch (alt11)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:482:23:
                // chunk
                {
                  pushFollow(FOLLOW_chunk_in_chunks645);
                  chunk();

                  state._fsp--;

                }
                  break;
                case 2:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:483:15:
                // cid= CHUNK_IDENTIFIER
                {
                  cid = (CommonTree) match(input, CHUNK_IDENTIFIER,
                      FOLLOW_CHUNK_IDENTIFIER_in_chunks664);
                  ((Library_scope) Library_stack.peek()).identifiersPendingResolution
                      .add(cid);

                }
                  break;

                default:
                  if (cnt11 >= 1) break loop11;
                  EarlyExitException eee = new EarlyExitException(11, input);
                  throw eee;
              }
              cnt11++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:483:94:
          // CHUNKS
          {
            match(input, CHUNKS, FOLLOW_CHUNKS_in_chunks674);

          }
            break;

        }

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "chunks"

  // $ANTLR start "chunk"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:485:1:
  // chunk : ^(c= CHUNK n= NAME p= PARENT slots parameters ) ;
  public final void chunk() throws RecognitionException
  {
    CommonTree c = null;
    CommonTree n = null;
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:485:7:
      // ( ^(c= CHUNK n= NAME p= PARENT slots parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:485:9:
      // ^(c= CHUNK n= NAME p= PARENT slots parameters )
      {
        c = (CommonTree) match(input, CHUNK, FOLLOW_CHUNK_in_chunk687);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_chunk691);
        p = (CommonTree) match(input, PARENT, FOLLOW_PARENT_in_chunk695);
        pushFollow(FOLLOW_slots_in_chunk697);
        slots();

        state._fsp--;

        pushFollow(FOLLOW_parameters_in_chunk699);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        delegate(n);
        delegate(p);

        LOGGER.debug("got chunk def " + (n != null ? n.getText() : null)
            + " isa " + (p != null ? p.getText() : null));
        String chunkName = (n != null ? n.getText() : null).toLowerCase();
        String chunkTypeName = (p != null ? p.getText() : null).toLowerCase();

        if (!((Library_scope) Library_stack.peek()).knownChunkTypes
            .containsKey(chunkTypeName))
          reportException(new CompilationError(chunkTypeName
              + " is not a valid chunk-type", p));

        // check for overlap of chunk
        if (((Library_scope) Library_stack.peek()).knownChunks
            .containsKey(chunkName))
          reportException(new CompilationWarning(chunkName
              + " is already defined, redefining", n));

        LOGGER.debug("indexing chunk " + chunkName);
        ((Library_scope) Library_stack.peek()).knownChunks.put(chunkName, c);

        // check the slots defined against the known slots

        // we can't access the slots ANTLR rule directly.. so we'll use the tree
        // to get what we need
        validateSlotNames(((Slots_scope) Slots_stack.peek()).slotDefiner,
            ((Slots_scope) Slots_stack.peek()).currentSlotNames,
            ASTSupport.getAllDescendantsWithType(c, SLOT),
            Collections.EMPTY_SET);

        delegate(c);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "chunk"

  // $ANTLR start "production"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:512:1:
  // production : ^(p= PRODUCTION n= NAME conditions actions parameters ) ;
  public final void production() throws RecognitionException
  {
    VariableBindings_stack.push(new VariableBindings_scope());

    CommonTree p = null;
    CommonTree n = null;

    ((VariableBindings_scope) VariableBindings_stack.peek()).validSlotNames = new HashMap<String, Set<String>>();
    ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:518:2:
      // ( ^(p= PRODUCTION n= NAME conditions actions parameters ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:518:4:
      // ^(p= PRODUCTION n= NAME conditions actions parameters )
      {
        p = (CommonTree) match(input, PRODUCTION,
            FOLLOW_PRODUCTION_in_production723);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_production727);
        pushFollow(FOLLOW_conditions_in_production729);
        conditions();

        state._fsp--;

        pushFollow(FOLLOW_actions_in_production731);
        actions();

        state._fsp--;

        pushFollow(FOLLOW_parameters_in_production733);
        parameters();

        state._fsp--;

        match(input, Token.UP, null);

        delegate(n);
        String name = (n != null ? n.getText() : null).toLowerCase();
        LOGGER.debug("Got a production def " + name);
        if (((Library_scope) Library_stack.peek()).knownProductions
            .contains(name))
          reportException(new CompilationWarning("production " + name
              + " is already defined", p));
        ((Library_scope) Library_stack.peek()).knownProductions.add(name);
        delegate(p);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      VariableBindings_stack.pop();

    }
    return;
  }

  // $ANTLR end "production"

  // $ANTLR start "conditions"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:529:1:
  // conditions : ^(c= CONDITIONS ( check | query | scriptCond | proxyCond )+ )
  // ;
  public final void conditions() throws RecognitionException
  {
    CommonTree c = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:2:
      // ( ^(c= CONDITIONS ( check | query | scriptCond | proxyCond )+ ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:4:
      // ^(c= CONDITIONS ( check | query | scriptCond | proxyCond )+ )
      {
        c = (CommonTree) match(input, CONDITIONS,
            FOLLOW_CONDITIONS_in_conditions750);

        match(input, Token.DOWN, null);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:19:
        // ( check | query | scriptCond | proxyCond )+
        int cnt13 = 0;
        loop13: do
        {
          int alt13 = 5;
          switch (input.LA(1))
          {
            case MATCH_CONDITION:
            {
              alt13 = 1;
            }
              break;
            case QUERY_CONDITION:
            {
              alt13 = 2;
            }
              break;
            case SCRIPTABLE_CONDITION:
            {
              alt13 = 3;
            }
              break;
            case PROXY_CONDITION:
            {
              alt13 = 4;
            }
              break;

          }

          switch (alt13)
          {
            case 1:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:20:
            // check
            {
              pushFollow(FOLLOW_check_in_conditions753);
              check();

              state._fsp--;

            }
              break;
            case 2:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:26:
            // query
            {
              pushFollow(FOLLOW_query_in_conditions755);
              query();

              state._fsp--;

            }
              break;
            case 3:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:32:
            // scriptCond
            {
              pushFollow(FOLLOW_scriptCond_in_conditions757);
              scriptCond();

              state._fsp--;

            }
              break;
            case 4:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:530:43:
            // proxyCond
            {
              pushFollow(FOLLOW_proxyCond_in_conditions759);
              proxyCond();

              state._fsp--;

            }
              break;

            default:
              if (cnt13 >= 1) break loop13;
              EarlyExitException eee = new EarlyExitException(13, input);
              throw eee;
          }
          cnt13++;
        }
        while (true);

        match(input, Token.UP, null);
        delegate(c);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "conditions"

  // $ANTLR start "actions"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:1:
  // actions : ^(a= ACTIONS ( add | set | remove | modify | scriptAct | proxyAct
  // | output )+ ) ;
  public final void actions() throws RecognitionException
  {
    CommonTree a = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:9:
      // ( ^(a= ACTIONS ( add | set | remove | modify | scriptAct | proxyAct |
      // output )+ ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:11:
      // ^(a= ACTIONS ( add | set | remove | modify | scriptAct | proxyAct |
      // output )+ )
      {
        a = (CommonTree) match(input, ACTIONS, FOLLOW_ACTIONS_in_actions775);

        match(input, Token.DOWN, null);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:23:
        // ( add | set | remove | modify | scriptAct | proxyAct | output )+
        int cnt14 = 0;
        loop14: do
        {
          int alt14 = 8;
          switch (input.LA(1))
          {
            case ADD_ACTION:
            {
              alt14 = 1;
            }
              break;
            case SET_ACTION:
            {
              alt14 = 2;
            }
              break;
            case REMOVE_ACTION:
            {
              alt14 = 3;
            }
              break;
            case MODIFY_ACTION:
            {
              alt14 = 4;
            }
              break;
            case SCRIPTABLE_ACTION:
            {
              alt14 = 5;
            }
              break;
            case PROXY_ACTION:
            {
              alt14 = 6;
            }
              break;
            case OUTPUT_ACTION:
            {
              alt14 = 7;
            }
              break;

          }

          switch (alt14)
          {
            case 1:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:24:
            // add
            {
              pushFollow(FOLLOW_add_in_actions778);
              add();

              state._fsp--;

            }
              break;
            case 2:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:28:
            // set
            {
              pushFollow(FOLLOW_set_in_actions780);
              set();

              state._fsp--;

            }
              break;
            case 3:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:32:
            // remove
            {
              pushFollow(FOLLOW_remove_in_actions782);
              remove();

              state._fsp--;

            }
              break;
            case 4:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:39:
            // modify
            {
              pushFollow(FOLLOW_modify_in_actions784);
              modify();

              state._fsp--;

            }
              break;
            case 5:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:46:
            // scriptAct
            {
              pushFollow(FOLLOW_scriptAct_in_actions786);
              scriptAct();

              state._fsp--;

            }
              break;
            case 6:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:56:
            // proxyAct
            {
              pushFollow(FOLLOW_proxyAct_in_actions788);
              proxyAct();

              state._fsp--;

            }
              break;
            case 7:
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:532:65:
            // output
            {
              pushFollow(FOLLOW_output_in_actions790);
              output();

              state._fsp--;

            }
              break;

            default:
              if (cnt14 >= 1) break loop14;
              EarlyExitException eee = new EarlyExitException(14, input);
              throw eee;
          }
          cnt14++;
        }
        while (true);

        match(input, Token.UP, null);
        delegate(a);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "actions"

  // $ANTLR start "check"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:534:1:
  // check : ^(m= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct=
  // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? )? ) ;
  public final void check() throws RecognitionException
  {
    Slots_stack.push(new Slots_scope());

    CommonTree m = null;
    CommonTree n = null;
    CommonTree c = null;
    CommonTree ct = null;
    CommonTree v = null;

    ((Slots_scope) Slots_stack.peek()).currentSlotNames = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:539:3:
      // ( ^(m= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct=
      // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:539:5:
      // ^(m= MATCH_CONDITION n= NAME ( (c= CHUNK_IDENTIFIER | ct=
      // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? )? )
      {
        m = (CommonTree) match(input, MATCH_CONDITION,
            FOLLOW_MATCH_CONDITION_in_check817);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_check821);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:540:17:
        // ( (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (
        // slots )? )?
        int alt17 = 2;
        int LA17_0 = input.LA(1);

        if (LA17_0 == VARIABLE || LA17_0 >= CHUNK_IDENTIFIER
            && LA17_0 <= CHUNK_TYPE_IDENTIFIER) alt17 = 1;
        switch (alt17)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:540:18:
          // (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) (
          // slots )?
          {
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:540:18:
            // (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
            int alt15 = 3;
            switch (input.LA(1))
            {
              case CHUNK_IDENTIFIER:
              {
                alt15 = 1;
              }
                break;
              case CHUNK_TYPE_IDENTIFIER:
              {
                alt15 = 2;
              }
                break;
              case VARIABLE:
              {
                alt15 = 3;
              }
                break;
              default:
                NoViableAltException nvae = new NoViableAltException("", 15, 0,
                    input);

                throw nvae;
            }

            switch (alt15)
            {
              case 1:
              // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:540:19:
              // c= CHUNK_IDENTIFIER
              {
                c = (CommonTree) match(input, CHUNK_IDENTIFIER,
                    FOLLOW_CHUNK_IDENTIFIER_in_check844);
                ((Slots_scope) Slots_stack.peek()).slotDefiner = c != null ? c
                    .getText() : null;
                ((Slots_scope) Slots_stack.peek()).currentSlotNames
                    .addAll(getCurrentSlotNames(c,
                        ((Library_scope) Library_stack.peek()).knownChunks,
                        ((Library_scope) Library_stack.peek()).knownChunkTypes));

              }
                break;
              case 2:
              // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:541:19:
              // ct= CHUNK_TYPE_IDENTIFIER
              {
                ct = (CommonTree) match(input, CHUNK_TYPE_IDENTIFIER,
                    FOLLOW_CHUNK_TYPE_IDENTIFIER_in_check868);
                ((Slots_scope) Slots_stack.peek()).slotDefiner = ct != null ? ct
                    .getText() : null;
                ((Slots_scope) Slots_stack.peek()).currentSlotNames
                    .addAll(getCurrentSlotNames(ct,
                        ((Library_scope) Library_stack.peek()).knownChunks,
                        ((Library_scope) Library_stack.peek()).knownChunkTypes));

              }
                break;
              case 3:
              // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:542:19:
              // v= VARIABLE
              {
                v = (CommonTree) match(input, VARIABLE,
                    FOLLOW_VARIABLE_in_check892);

              }
                break;

            }

            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:544:18:
            // ( slots )?
            int alt16 = 2;
            int LA16_0 = input.LA(1);

            if (LA16_0 == SLOTS) alt16 = 1;
            switch (alt16)
            {
              case 1:
              // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:544:18:
              // slots
              {
                pushFollow(FOLLOW_slots_in_check931);
                slots();

                state._fsp--;

              }
                break;

            }

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        delegate(c);
        delegate(ct);
        delegate(v);

        LOGGER.debug("ABOUT TO START CHECK");
        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();
        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(m, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            true,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables
            .add(bufferName);

        Set<String> validSlotNames = ((Slots_scope) Slots_stack.peek()).currentSlotNames;
        // validSlotNames.add("isa");
        if (((Slots_scope) Slots_stack.peek()).slotDefiner != null)
        {
          LOGGER.debug("ABOUT TO VALIDATE SLOT NAMES");
          validateSlotNames(
              ((Slots_scope) Slots_stack.peek()).slotDefiner,
              validSlotNames,
              definedSlots,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables);
          ((VariableBindings_scope) VariableBindings_stack.peek()).validSlotNames
              .put(bufferName, validSlotNames);
        }
        else if (definedSlots.size() != 0)
          reportException(new CompilationWarning(
              "Could not infer chunktype of contents of " + bufferName
                  + ". Cannot test slot names", m));

        if (v != null)
        {
          String varName = (v != null ? v.getText() : null).toLowerCase(); // will
                                                                           // be
                                                                           // "=variable"
          varName = varName.substring(1, varName.length());
          if (!((VariableBindings_scope) VariableBindings_stack.peek()).validVariables
              .contains(varName))
            reportException(new CompilationError(
                varName
                    + " must be bound on the left hand side before it can be checked "
                    + ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
                v));
        }
        delegate(m);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Slots_stack.pop();

    }
    return;
  }

  // $ANTLR end "check"

  // $ANTLR start "unknownList"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:585:1:
  // unknownList : ^(u= UNKNOWN ( . )* ) ;
  public final void unknownList() throws RecognitionException
  {
    CommonTree u = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:586:2:
      // ( ^(u= UNKNOWN ( . )* ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:586:4:
      // ^(u= UNKNOWN ( . )* )
      {
        u = (CommonTree) match(input, UNKNOWN, FOLLOW_UNKNOWN_in_unknownList968);

        if (input.LA(1) == Token.DOWN)
        {
          match(input, Token.DOWN, null);
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:586:16:
          // ( . )*
          loop18: do
          {
            int alt18 = 2;
            int LA18_0 = input.LA(1);

            if (LA18_0 >= MODEL && LA18_0 <= UNKNOWN)
              alt18 = 1;
            else if (LA18_0 == UP) alt18 = 2;

            switch (alt18)
            {
              case 1:
              // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:586:16:
              // .
              {
                matchAny(input);

              }
                break;

              default:
                break loop18;
            }
          }
          while (true);

          match(input, Token.UP, null);
        }
        delegate(u);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "unknownList"

  // $ANTLR start "query"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:590:1:
  // query : ^(q= QUERY_CONDITION n= NAME slots ) ;
  public final void query() throws RecognitionException
  {
    CommonTree q = null;
    CommonTree n = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:590:9:
      // ( ^(q= QUERY_CONDITION n= NAME slots ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:590:11:
      // ^(q= QUERY_CONDITION n= NAME slots )
      {
        q = (CommonTree) match(input, QUERY_CONDITION,
            FOLLOW_QUERY_CONDITION_in_query988);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_query992);
        pushFollow(FOLLOW_slots_in_query994);
        slots();

        state._fsp--;

        match(input, Token.UP, null);

        delegate(n);
        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();
        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(q, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            true,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        delegate(q);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "query"

  // $ANTLR start "scriptCond"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:604:1:
  // scriptCond : ^(s= SCRIPTABLE_CONDITION LANG SCRIPT ) ;
  public final void scriptCond() throws RecognitionException
  {
    CommonTree s = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:605:2:
      // ( ^(s= SCRIPTABLE_CONDITION LANG SCRIPT ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:605:4:
      // ^(s= SCRIPTABLE_CONDITION LANG SCRIPT )
      {
        s = (CommonTree) match(input, SCRIPTABLE_CONDITION,
            FOLLOW_SCRIPTABLE_CONDITION_in_scriptCond1011);

        match(input, Token.DOWN, null);
        match(input, LANG, FOLLOW_LANG_in_scriptCond1013);
        match(input, SCRIPT, FOLLOW_SCRIPT_in_scriptCond1015);

        match(input, Token.UP, null);

        delegate(s);
        ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable = true;

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "scriptCond"

  // $ANTLR start "proxyCond"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:611:1:
  // proxyCond : ^(p= PROXY_CONDITION CLASS_SPEC ( slots )? ) ;
  public final void proxyCond() throws RecognitionException
  {
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:611:11:
      // ( ^(p= PROXY_CONDITION CLASS_SPEC ( slots )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:611:13:
      // ^(p= PROXY_CONDITION CLASS_SPEC ( slots )? )
      {
        p = (CommonTree) match(input, PROXY_CONDITION,
            FOLLOW_PROXY_CONDITION_in_proxyCond1031);

        match(input, Token.DOWN, null);
        match(input, CLASS_SPEC, FOLLOW_CLASS_SPEC_in_proxyCond1033);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:611:44:
        // ( slots )?
        int alt19 = 2;
        int LA19_0 = input.LA(1);

        if (LA19_0 == SLOTS) alt19 = 1;
        switch (alt19)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:611:44:
          // slots
          {
            pushFollow(FOLLOW_slots_in_proxyCond1035);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(p, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            true,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);
        delegate(p);
        ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable = true;

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "proxyCond"

  // $ANTLR start "add"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:620:1:
  // add : ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct=
  // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? ) ;
  public final void add() throws RecognitionException
  {
    Slots_stack.push(new Slots_scope());

    CommonTree a = null;
    CommonTree n = null;
    CommonTree c = null;
    CommonTree ct = null;
    CommonTree v = null;

    ((Slots_scope) Slots_stack.peek()).currentSlotNames = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:624:3:
      // ( ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct=
      // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:624:5:
      // ^(a= ADD_ACTION n= NAME (c= CHUNK_IDENTIFIER | ct=
      // CHUNK_TYPE_IDENTIFIER | v= VARIABLE ) ( slots )? )
      {
        a = (CommonTree) match(input, ADD_ACTION, FOLLOW_ADD_ACTION_in_add1064);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_add1068);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:625:17:
        // (c= CHUNK_IDENTIFIER | ct= CHUNK_TYPE_IDENTIFIER | v= VARIABLE )
        int alt20 = 3;
        switch (input.LA(1))
        {
          case CHUNK_IDENTIFIER:
          {
            alt20 = 1;
          }
            break;
          case CHUNK_TYPE_IDENTIFIER:
          {
            alt20 = 2;
          }
            break;
          case VARIABLE:
          {
            alt20 = 3;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 20, 0,
                input);

            throw nvae;
        }

        switch (alt20)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:625:18:
          // c= CHUNK_IDENTIFIER
          {
            c = (CommonTree) match(input, CHUNK_IDENTIFIER,
                FOLLOW_CHUNK_IDENTIFIER_in_add1090);
            ((Slots_scope) Slots_stack.peek()).slotDefiner = c != null ? c
                .getText() : null;
            ((Slots_scope) Slots_stack.peek()).currentSlotNames
                .addAll(getCurrentSlotNames(c,
                    ((Library_scope) Library_stack.peek()).knownChunks,
                    ((Library_scope) Library_stack.peek()).knownChunkTypes));

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:626:19:
          // ct= CHUNK_TYPE_IDENTIFIER
          {
            ct = (CommonTree) match(input, CHUNK_TYPE_IDENTIFIER,
                FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add1114);
            ((Slots_scope) Slots_stack.peek()).slotDefiner = ct != null ? ct
                .getText() : null;
            ((Slots_scope) Slots_stack.peek()).currentSlotNames
                .addAll(getCurrentSlotNames(ct,
                    ((Library_scope) Library_stack.peek()).knownChunks,
                    ((Library_scope) Library_stack.peek()).knownChunkTypes));

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:627:19:
          // v= VARIABLE
          {
            v = (CommonTree) match(input, VARIABLE, FOLLOW_VARIABLE_in_add1138);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:627:32:
        // ( slots )?
        int alt21 = 2;
        int LA21_0 = input.LA(1);

        if (LA21_0 == SLOTS) alt21 = 1;
        switch (alt21)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:627:32:
          // slots
          {
            pushFollow(FOLLOW_slots_in_add1142);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        delegate(c);
        delegate(ct);
        delegate(v);

        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();

        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(a, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            false,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        if (((Slots_scope) Slots_stack.peek()).slotDefiner != null)
          validateSlotNames(
              ((Slots_scope) Slots_stack.peek()).slotDefiner,
              ((Slots_scope) Slots_stack.peek()).currentSlotNames,
              definedSlots,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables);
        else if (definedSlots.size() != 0)
          reportException(new CompilationWarning(
              "Could not infer chunktype of contents of " + bufferName
                  + ". Cannot test slot names", a));

        Collection<CommonTree> definedLogicSlots = ASTSupport
            .getAllDescendantsWithType(a, LOGIC);

        if (!bufferName.equalsIgnoreCase("retrieval")
            && definedLogicSlots.size() > 0)
          reportException(new CompilationError(
              "Cannot have logic in +add for buffers other than retrieval", a));

        if (v != null)
        {
          String varName = (v != null ? v.getText() : null).toLowerCase(); // will
                                                                           // be
                                                                           // "=variable"
          varName = varName.substring(1, varName.length());
          if (!((VariableBindings_scope) VariableBindings_stack.peek()).validVariables
              .contains(varName))
            reportException(new CompilationError(
                varName
                    + " must be bound on the left hand side before it can be added "
                    + ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
                v));
        }

        delegate(a);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Slots_stack.pop();

    }
    return;
  }

  // $ANTLR end "add"

  // $ANTLR start "set"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:664:1:
  // set : ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? ( slots
  // )? ) ;
  public final void set() throws RecognitionException
  {
    Slots_stack.push(new Slots_scope());

    CommonTree a = null;
    CommonTree n = null;
    CommonTree c = null;
    CommonTree v = null;

    ((Slots_scope) Slots_stack.peek()).currentSlotNames = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:668:3:
      // ( ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? ( slots
      // )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:668:5:
      // ^(a= SET_ACTION n= NAME (c= CHUNK_IDENTIFIER | v= VARIABLE )? ( slots
      // )? )
      {
        a = (CommonTree) match(input, SET_ACTION, FOLLOW_SET_ACTION_in_set1166);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_set1170);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:669:17:
        // (c= CHUNK_IDENTIFIER | v= VARIABLE )?
        int alt22 = 3;
        int LA22_0 = input.LA(1);

        if (LA22_0 == CHUNK_IDENTIFIER)
          alt22 = 1;
        else if (LA22_0 == VARIABLE) alt22 = 2;
        switch (alt22)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:669:18:
          // c= CHUNK_IDENTIFIER
          {
            c = (CommonTree) match(input, CHUNK_IDENTIFIER,
                FOLLOW_CHUNK_IDENTIFIER_in_set1192);
            ((Slots_scope) Slots_stack.peek()).slotDefiner = c != null ? c
                .getText() : null;
            ((Slots_scope) Slots_stack.peek()).currentSlotNames
                .addAll(getCurrentSlotNames(c,
                    ((Library_scope) Library_stack.peek()).knownChunks,
                    ((Library_scope) Library_stack.peek()).knownChunkTypes));

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:670:19:
          // v= VARIABLE
          {
            v = (CommonTree) match(input, VARIABLE, FOLLOW_VARIABLE_in_set1216);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:670:33:
        // ( slots )?
        int alt23 = 2;
        int LA23_0 = input.LA(1);

        if (LA23_0 == SLOTS) alt23 = 1;
        switch (alt23)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:670:33:
          // slots
          {
            pushFollow(FOLLOW_slots_in_set1221);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        delegate(c);
        delegate(v);

        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();

        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(a, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            false,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        if (((Slots_scope) Slots_stack.peek()).slotDefiner != null)
          validateSlotNames(
              ((Slots_scope) Slots_stack.peek()).slotDefiner,
              ((Slots_scope) Slots_stack.peek()).currentSlotNames,
              definedSlots,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables);
        else if (definedSlots.size() != 0)
          reportException(new CompilationWarning(
              "Could not infer chunktype of contents of " + bufferName
                  + ". Cannot test slot names", a));

        if (v != null)
        {
          String varName = (v != null ? v.getText() : null).toLowerCase(); // will
                                                                           // be
                                                                           // "=variable"
          varName = varName.substring(1, varName.length());
          if (!((VariableBindings_scope) VariableBindings_stack.peek()).validVariables
              .contains(varName))
            reportException(new CompilationError(
                varName
                    + " must be bound on the left hand side before it can be added "
                    + ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
                v));
        }

        delegate(a);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Slots_stack.pop();

    }
    return;
  }

  // $ANTLR end "set"

  // $ANTLR start "remove"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:702:1:
  // remove : ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? ( slots
  // )? ) ;
  public final void remove() throws RecognitionException
  {
    Slots_stack.push(new Slots_scope());

    CommonTree r = null;
    CommonTree n = null;
    CommonTree i = null;
    CommonTree v = null;

    ((Slots_scope) Slots_stack.peek()).currentSlotNames = new TreeSet<String>();

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:706:3:
      // ( ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? ( slots )?
      // ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:706:5:
      // ^(r= REMOVE_ACTION n= NAME (i= IDENTIFIER | v= VARIABLE )? ( slots )? )
      {
        r = (CommonTree) match(input, REMOVE_ACTION,
            FOLLOW_REMOVE_ACTION_in_remove1245);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_remove1249);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:707:17:
        // (i= IDENTIFIER | v= VARIABLE )?
        int alt24 = 3;
        int LA24_0 = input.LA(1);

        if (LA24_0 == IDENTIFIER)
          alt24 = 1;
        else if (LA24_0 == VARIABLE) alt24 = 2;
        switch (alt24)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:707:18:
          // i= IDENTIFIER
          {
            i = (CommonTree) match(input, IDENTIFIER,
                FOLLOW_IDENTIFIER_in_remove1271);
            ((Slots_scope) Slots_stack.peek()).slotDefiner = i != null ? i
                .getText() : null;
            ((Slots_scope) Slots_stack.peek()).currentSlotNames
                .addAll(getCurrentSlotNames(i,
                    ((Library_scope) Library_stack.peek()).knownChunks,
                    ((Library_scope) Library_stack.peek()).knownChunkTypes));

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:708:19:
          // v= VARIABLE
          {
            v = (CommonTree) match(input, VARIABLE,
                FOLLOW_VARIABLE_in_remove1295);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:709:20:
        // ( slots )?
        int alt25 = 2;
        int LA25_0 = input.LA(1);

        if (LA25_0 == SLOTS) alt25 = 1;
        switch (alt25)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:709:20:
          // slots
          {
            pushFollow(FOLLOW_slots_in_remove1317);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        delegate(i);
        delegate(v);
        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();

        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(r, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            false,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        if (((Slots_scope) Slots_stack.peek()).slotDefiner != null)
          validateSlotNames(
              ((Slots_scope) Slots_stack.peek()).slotDefiner,
              ((Slots_scope) Slots_stack.peek()).currentSlotNames,
              definedSlots,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables);

        if (v != null)
        {
          String varName = (v != null ? v.getText() : null).toLowerCase(); // will
                                                                           // be
                                                                           // "=variable"
          varName = varName.substring(1, varName.length());
          if (!((VariableBindings_scope) VariableBindings_stack.peek()).validVariables
              .contains(varName))
            reportException(new CompilationError(
                varName
                    + " must be bound on the left hand side before it can be removed "
                    + ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
                v));
        }
        delegate(r);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
      Slots_stack.pop();

    }
    return;
  }

  // $ANTLR end "remove"

  // $ANTLR start "modify"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:736:1:
  // modify : ^(m= MODIFY_ACTION n= NAME ( slots )? ) ;
  public final void modify() throws RecognitionException
  {
    CommonTree m = null;
    CommonTree n = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:736:8:
      // ( ^(m= MODIFY_ACTION n= NAME ( slots )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:736:10:
      // ^(m= MODIFY_ACTION n= NAME ( slots )? )
      {
        m = (CommonTree) match(input, MODIFY_ACTION,
            FOLLOW_MODIFY_ACTION_in_modify1332);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_modify1336);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:736:35:
        // ( slots )?
        int alt26 = 2;
        int LA26_0 = input.LA(1);

        if (LA26_0 == SLOTS) alt26 = 1;
        switch (alt26)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:736:35:
          // slots
          {
            pushFollow(FOLLOW_slots_in_modify1338);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        // check the buffer name
        String bufferName = (n != null ? n.getText() : null).toLowerCase();
        if (Library_stack.size() == 0
            || !((Library_scope) Library_stack.peek()).knownBufferMap
                .containsKey(bufferName))
          reportException(new CompilationError(bufferName
              + " is an unknown buffer", n));

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(m, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            false,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);

        if (((VariableBindings_scope) VariableBindings_stack.peek()).validSlotNames
            .containsKey(bufferName))
          // we can check the slots..
          validateSlotNames(
              "=" + bufferName,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validSlotNames
                  .get(bufferName),
              definedSlots,
              ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables);
        else
          reportException(new CompilationError(
              bufferName
                  + " must be bound on the left hand side before it can be modified",
              m));

        delegate(m);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "modify"

  // $ANTLR start "scriptAct"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:758:1:
  // scriptAct : ^(s= SCRIPTABLE_ACTION LANG SCRIPT ) ;
  public final void scriptAct() throws RecognitionException
  {
    CommonTree s = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:759:2:
      // ( ^(s= SCRIPTABLE_ACTION LANG SCRIPT ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:759:4:
      // ^(s= SCRIPTABLE_ACTION LANG SCRIPT )
      {
        s = (CommonTree) match(input, SCRIPTABLE_ACTION,
            FOLLOW_SCRIPTABLE_ACTION_in_scriptAct1355);

        match(input, Token.DOWN, null);
        match(input, LANG, FOLLOW_LANG_in_scriptAct1357);
        match(input, SCRIPT, FOLLOW_SCRIPT_in_scriptAct1359);

        match(input, Token.UP, null);

        delegate(s);
        ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable = true;

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "scriptAct"

  // $ANTLR start "proxyAct"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:765:1:
  // proxyAct : ^(p= PROXY_ACTION CLASS_SPEC ( slots )? ) ;
  public final void proxyAct() throws RecognitionException
  {
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:766:1:
      // ( ^(p= PROXY_ACTION CLASS_SPEC ( slots )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:766:3:
      // ^(p= PROXY_ACTION CLASS_SPEC ( slots )? )
      {
        p = (CommonTree) match(input, PROXY_ACTION,
            FOLLOW_PROXY_ACTION_in_proxyAct1374);

        match(input, Token.DOWN, null);
        match(input, CLASS_SPEC, FOLLOW_CLASS_SPEC_in_proxyAct1376);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:766:31:
        // ( slots )?
        int alt27 = 2;
        int LA27_0 = input.LA(1);

        if (LA27_0 == SLOTS) alt27 = 1;
        switch (alt27)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:766:31:
          // slots
          {
            pushFollow(FOLLOW_slots_in_proxyAct1378);
            slots();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        Collection<CommonTree> definedSlots = ASTSupport
            .getAllDescendantsWithType(p, SLOT);
        validateVariables(
            ((VariableBindings_scope) VariableBindings_stack.peek()).validVariables,
            definedSlots,
            false,
            ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable);
        delegate(p);
        ((VariableBindings_scope) VariableBindings_stack.peek()).hasScriptable = true;

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "proxyAct"

  // $ANTLR start "output"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:774:1:
  // output : ^(o= OUTPUT_ACTION s= STRING ) ;
  public final void output() throws RecognitionException
  {
    CommonTree o = null;
    CommonTree s = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:774:8:
      // ( ^(o= OUTPUT_ACTION s= STRING ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:774:10:
      // ^(o= OUTPUT_ACTION s= STRING )
      {
        o = (CommonTree) match(input, OUTPUT_ACTION,
            FOLLOW_OUTPUT_ACTION_in_output1394);

        match(input, Token.DOWN, null);
        s = (CommonTree) match(input, STRING, FOLLOW_STRING_in_output1398);

        match(input, Token.UP, null);
        delegate(s);
        delegate(o);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "output"

  // $ANTLR start "parents"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:1:
  // parents : ( ^(p= PARENTS ( PARENT )+ ) | p= PARENTS ) ;
  public final void parents() throws RecognitionException
  {
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:9:
      // ( ( ^(p= PARENTS ( PARENT )+ ) | p= PARENTS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:11:
      // ( ^(p= PARENTS ( PARENT )+ ) | p= PARENTS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:11:
        // ( ^(p= PARENTS ( PARENT )+ ) | p= PARENTS )
        int alt29 = 2;
        int LA29_0 = input.LA(1);

        if (LA29_0 == PARENTS)
        {
          int LA29_1 = input.LA(2);

          if (LA29_1 == DOWN)
            alt29 = 1;
          else if (LA29_1 == SLOTS)
            alt29 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 29, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 29, 0, input);

          throw nvae;
        }
        switch (alt29)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:12:
          // ^(p= PARENTS ( PARENT )+ )
          {
            p = (CommonTree) match(input, PARENTS,
                FOLLOW_PARENTS_in_parents1413);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:24:
            // ( PARENT )+
            int cnt28 = 0;
            loop28: do
            {
              int alt28 = 2;
              int LA28_0 = input.LA(1);

              if (LA28_0 == PARENT) alt28 = 1;

              switch (alt28)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:24:
                // PARENT
                {
                  match(input, PARENT, FOLLOW_PARENT_in_parents1415);

                }
                  break;

                default:
                  if (cnt28 >= 1) break loop28;
                  EarlyExitException eee = new EarlyExitException(28, input);
                  throw eee;
              }
              cnt28++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:776:35:
          // p= PARENTS
          {
            p = (CommonTree) match(input, PARENTS,
                FOLLOW_PARENTS_in_parents1423);

          }
            break;

        }

        delegate(p);
        // if(p!=null)
        // {
        for (int i = 0; i < p.getChildCount(); i++)
        {
          String parentName = p.getChild(i).getText();
          if (!((Library_scope) Library_stack.peek()).knownChunkTypes
              .containsKey(parentName))
            reportException(new CompilationError(parentName
                + " is not a defined chunk-type", p));
        }

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "parents"

  // $ANTLR start "slots"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:1:
  // slots : ( ^(s= SLOTS ( logic | slot )+ ) | s= SLOTS ) ;
  public final void slots() throws RecognitionException
  {
    CommonTree s = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:8:
      // ( ( ^(s= SLOTS ( logic | slot )+ ) | s= SLOTS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:10:
      // ( ^(s= SLOTS ( logic | slot )+ ) | s= SLOTS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:10:
        // ( ^(s= SLOTS ( logic | slot )+ ) | s= SLOTS )
        int alt31 = 2;
        int LA31_0 = input.LA(1);

        if (LA31_0 == SLOTS)
        {
          int LA31_1 = input.LA(2);

          if (LA31_1 == DOWN)
            alt31 = 1;
          else if (LA31_1 == UP || LA31_1 == CHUNKS || LA31_1 == PARAMETERS)
            alt31 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 31, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 31, 0, input);

          throw nvae;
        }
        switch (alt31)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:11:
          // ^(s= SLOTS ( logic | slot )+ )
          {
            s = (CommonTree) match(input, SLOTS, FOLLOW_SLOTS_in_slots1440);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:21:
            // ( logic | slot )+
            int cnt30 = 0;
            loop30: do
            {
              int alt30 = 3;
              int LA30_0 = input.LA(1);

              if (LA30_0 == LOGIC)
                alt30 = 1;
              else if (LA30_0 == SLOT) alt30 = 2;

              switch (alt30)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:22:
                // logic
                {
                  pushFollow(FOLLOW_logic_in_slots1443);
                  logic();

                  state._fsp--;

                }
                  break;
                case 2:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:28:
                // slot
                {
                  pushFollow(FOLLOW_slot_in_slots1445);
                  slot();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt30 >= 1) break loop30;
                  EarlyExitException eee = new EarlyExitException(30, input);
                  throw eee;
              }
              cnt30++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:789:38:
          // s= SLOTS
          {
            s = (CommonTree) match(input, SLOTS, FOLLOW_SLOTS_in_slots1454);

          }
            break;

        }

        delegate(s);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "slots"

  // $ANTLR start "parameters"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:791:1:
  // parameters : ( ^(p= PARAMETERS ( parameter )+ ) | p= PARAMETERS ) ;
  public final void parameters() throws RecognitionException
  {
    CommonTree p = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:2:
      // ( ( ^(p= PARAMETERS ( parameter )+ ) | p= PARAMETERS ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:4:
      // ( ^(p= PARAMETERS ( parameter )+ ) | p= PARAMETERS )
      {
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:4:
        // ( ^(p= PARAMETERS ( parameter )+ ) | p= PARAMETERS )
        int alt33 = 2;
        int LA33_0 = input.LA(1);

        if (LA33_0 == PARAMETERS)
        {
          int LA33_1 = input.LA(2);

          if (LA33_1 == DOWN)
            alt33 = 1;
          else if (LA33_1 == UP)
            alt33 = 2;
          else
          {
            NoViableAltException nvae = new NoViableAltException("", 33, 1,
                input);

            throw nvae;
          }
        }
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 33, 0, input);

          throw nvae;
        }
        switch (alt33)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:5:
          // ^(p= PARAMETERS ( parameter )+ )
          {
            p = (CommonTree) match(input, PARAMETERS,
                FOLLOW_PARAMETERS_in_parameters1473);

            match(input, Token.DOWN, null);
            // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:20:
            // ( parameter )+
            int cnt32 = 0;
            loop32: do
            {
              int alt32 = 2;
              int LA32_0 = input.LA(1);

              if (LA32_0 == PARAMETER) alt32 = 1;

              switch (alt32)
              {
                case 1:
                // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:20:
                // parameter
                {
                  pushFollow(FOLLOW_parameter_in_parameters1475);
                  parameter();

                  state._fsp--;

                }
                  break;

                default:
                  if (cnt32 >= 1) break loop32;
                  EarlyExitException eee = new EarlyExitException(32, input);
                  throw eee;
              }
              cnt32++;
            }
            while (true);

            match(input, Token.UP, null);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:795:34:
          // p= PARAMETERS
          {
            p = (CommonTree) match(input, PARAMETERS,
                FOLLOW_PARAMETERS_in_parameters1483);

          }
            break;

        }

        delegate(p);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "parameters"

  // $ANTLR start "parameter"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:797:1:
  // parameter : ^(p= PARAMETER n= NAME s= STRING ) ;
  public final void parameter() throws RecognitionException
  {
    CommonTree p = null;
    CommonTree n = null;
    CommonTree s = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:798:2:
      // ( ^(p= PARAMETER n= NAME s= STRING ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:798:4:
      // ^(p= PARAMETER n= NAME s= STRING )
      {
        p = (CommonTree) match(input, PARAMETER,
            FOLLOW_PARAMETER_in_parameter1499);

        match(input, Token.DOWN, null);
        n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_parameter1503);
        s = (CommonTree) match(input, STRING, FOLLOW_STRING_in_parameter1507);

        match(input, Token.UP, null);
        delegate(n);
        delegate(s);
        delegate(p);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "parameter"

  // $ANTLR start "logic"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:801:1:
  // logic : ^(l= LOGIC (v= AND | v= OR | v= NOT ) ( logic | slot ) ( logic |
  // slot )? ) ;
  public final void logic() throws RecognitionException
  {
    CommonTree l = null;
    CommonTree v = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:2:
      // ( ^(l= LOGIC (v= AND | v= OR | v= NOT ) ( logic | slot ) ( logic | slot
      // )? ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:4:
      // ^(l= LOGIC (v= AND | v= OR | v= NOT ) ( logic | slot ) ( logic | slot
      // )? )
      {
        l = (CommonTree) match(input, LOGIC, FOLLOW_LOGIC_in_logic1523);

        match(input, Token.DOWN, null);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:14:
        // (v= AND | v= OR | v= NOT )
        int alt34 = 3;
        switch (input.LA(1))
        {
          case AND:
          {
            alt34 = 1;
          }
            break;
          case OR:
          {
            alt34 = 2;
          }
            break;
          case NOT:
          {
            alt34 = 3;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 34, 0,
                input);

            throw nvae;
        }

        switch (alt34)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:15:
          // v= AND
          {
            v = (CommonTree) match(input, AND, FOLLOW_AND_in_logic1528);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:21:
          // v= OR
          {
            v = (CommonTree) match(input, OR, FOLLOW_OR_in_logic1532);

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:26:
          // v= NOT
          {
            v = (CommonTree) match(input, NOT, FOLLOW_NOT_in_logic1536);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:33:
        // ( logic | slot )
        int alt35 = 2;
        int LA35_0 = input.LA(1);

        if (LA35_0 == LOGIC)
          alt35 = 1;
        else if (LA35_0 == SLOT)
          alt35 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 35, 0, input);

          throw nvae;
        }
        switch (alt35)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:34:
          // logic
          {
            pushFollow(FOLLOW_logic_in_logic1540);
            logic();

            state._fsp--;

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:40:
          // slot
          {
            pushFollow(FOLLOW_slot_in_logic1542);
            slot();

            state._fsp--;

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:46:
        // ( logic | slot )?
        int alt36 = 3;
        int LA36_0 = input.LA(1);

        if (LA36_0 == LOGIC)
          alt36 = 1;
        else if (LA36_0 == SLOT) alt36 = 2;
        switch (alt36)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:47:
          // logic
          {
            pushFollow(FOLLOW_logic_in_logic1546);
            logic();

            state._fsp--;

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:802:53:
          // slot
          {
            pushFollow(FOLLOW_slot_in_logic1548);
            slot();

            state._fsp--;

          }
            break;

        }

        match(input, Token.UP, null);

        LOGGER.debug("got a logic def " + (v != null ? v.getText() : null));
        delegate(v);
        delegate(l);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "logic"

  // $ANTLR start "slot"
  // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:810:1:
  // slot : ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c=
  // LT | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v= STRING
  // | v= NUMBER ) ) ;
  public final void slot() throws RecognitionException
  {
    CommonTree s = null;
    CommonTree n = null;
    CommonTree c = null;
    CommonTree v = null;

    try
    {
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:1:
      // ( ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c=
      // LT | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v=
      // STRING | v= NUMBER ) ) )
      // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:3:
      // ^(s= SLOT (n= NAME | n= VARIABLE ) (c= EQUALS | c= GT | c= GTE | c= LT
      // | c= LTE | c= NOT | c= WITHIN ) (v= IDENTIFIER | v= VARIABLE | v=
      // STRING | v= NUMBER ) )
      {
        s = (CommonTree) match(input, SLOT, FOLLOW_SLOT_in_slot1567);

        match(input, Token.DOWN, null);
        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:12:
        // (n= NAME | n= VARIABLE )
        int alt37 = 2;
        int LA37_0 = input.LA(1);

        if (LA37_0 == NAME)
          alt37 = 1;
        else if (LA37_0 == VARIABLE)
          alt37 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 37, 0, input);

          throw nvae;
        }
        switch (alt37)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:13:
          // n= NAME
          {
            n = (CommonTree) match(input, NAME, FOLLOW_NAME_in_slot1572);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:20:
          // n= VARIABLE
          {
            n = (CommonTree) match(input, VARIABLE, FOLLOW_VARIABLE_in_slot1576);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:32:
        // (c= EQUALS | c= GT | c= GTE | c= LT | c= LTE | c= NOT | c= WITHIN )
        int alt38 = 7;
        switch (input.LA(1))
        {
          case EQUALS:
          {
            alt38 = 1;
          }
            break;
          case GT:
          {
            alt38 = 2;
          }
            break;
          case GTE:
          {
            alt38 = 3;
          }
            break;
          case LT:
          {
            alt38 = 4;
          }
            break;
          case LTE:
          {
            alt38 = 5;
          }
            break;
          case NOT:
          {
            alt38 = 6;
          }
            break;
          case WITHIN:
          {
            alt38 = 7;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 38, 0,
                input);

            throw nvae;
        }

        switch (alt38)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:33:
          // c= EQUALS
          {
            c = (CommonTree) match(input, EQUALS, FOLLOW_EQUALS_in_slot1582);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:42:
          // c= GT
          {
            c = (CommonTree) match(input, GT, FOLLOW_GT_in_slot1586);

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:47:
          // c= GTE
          {
            c = (CommonTree) match(input, GTE, FOLLOW_GTE_in_slot1590);

          }
            break;
          case 4:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:53:
          // c= LT
          {
            c = (CommonTree) match(input, LT, FOLLOW_LT_in_slot1594);

          }
            break;
          case 5:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:58:
          // c= LTE
          {
            c = (CommonTree) match(input, LTE, FOLLOW_LTE_in_slot1598);

          }
            break;
          case 6:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:64:
          // c= NOT
          {
            c = (CommonTree) match(input, NOT, FOLLOW_NOT_in_slot1602);

          }
            break;
          case 7:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:811:70:
          // c= WITHIN
          {
            c = (CommonTree) match(input, WITHIN, FOLLOW_WITHIN_in_slot1606);

          }
            break;

        }

        // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:812:23:
        // (v= IDENTIFIER | v= VARIABLE | v= STRING | v= NUMBER )
        int alt39 = 4;
        switch (input.LA(1))
        {
          case IDENTIFIER:
          {
            alt39 = 1;
          }
            break;
          case VARIABLE:
          {
            alt39 = 2;
          }
            break;
          case STRING:
          {
            alt39 = 3;
          }
            break;
          case NUMBER:
          {
            alt39 = 4;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 39, 0,
                input);

            throw nvae;
        }

        switch (alt39)
        {
          case 1:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:812:24:
          // v= IDENTIFIER
          {
            v = (CommonTree) match(input, IDENTIFIER,
                FOLLOW_IDENTIFIER_in_slot1635);
            ((Library_scope) Library_stack.peek()).identifiersPendingResolution
                .add(v);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:813:24:
          // v= VARIABLE
          {
            v = (CommonTree) match(input, VARIABLE, FOLLOW_VARIABLE_in_slot1664);

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:813:35:
          // v= STRING
          {
            v = (CommonTree) match(input, STRING, FOLLOW_STRING_in_slot1668);

          }
            break;
          case 4:
          // /Users/harrison/Archive/Development/workspaces/jactr-env-dev/org.jactr.io/src/org/jactr/io/antlr3/compiler/JACTRCompiler.g:813:44:
          // v= NUMBER
          {
            v = (CommonTree) match(input, NUMBER, FOLLOW_NUMBER_in_slot1672);

          }
            break;

        }

        match(input, Token.UP, null);

        delegate(n);
        delegate(v);
        LOGGER.debug("got slot def " + (n != null ? n.getText() : null) + " "
            + (c != null ? c.getText() : null) + " "
            + (v != null ? v.getText() : null));
        delegate(s);

      }

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
    }
    finally
    {
    }
    return;
  }

  // $ANTLR end "slot"

  // Delegated rules

  public static final BitSet FOLLOW_MODEL_in_model348                          = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_model352                           = new BitSet(
                                                                                   new long[] { 0x0000000000000040L });

  public static final BitSet FOLLOW_modules_in_model354                        = new BitSet(
                                                                                   new long[] { 0x0000000000000100L });

  public static final BitSet FOLLOW_extensions_in_model356                     = new BitSet(
                                                                                   new long[] { 0x0000000000040000L });

  public static final BitSet FOLLOW_buffers_in_model358                        = new BitSet(
                                                                                   new long[] { 0x0000000000000020L });

  public static final BitSet FOLLOW_library_in_model360                        = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_model362                     = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_LIBRARY_in_library376                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_declarativeMemory_in_library381            = new BitSet(
                                                                                   new long[] { 0x0000000000004000L });

  public static final BitSet FOLLOW_proceduralMemory_in_library383             = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory400 = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_chunkType_in_declarativeMemory403          = new BitSet(
                                                                                   new long[] { 0x0000000000000808L });

  public static final BitSet FOLLOW_DECLARATIVE_MEMORY_in_declarativeMemory411 = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory429   = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_production_in_proceduralMemory432          = new BitSet(
                                                                                   new long[] { 0x0000000000008008L });

  public static final BitSet FOLLOW_PROCEDURAL_MEMORY_in_proceduralMemory441   = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_MODULES_in_modules456                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_module_in_modules458                       = new BitSet(
                                                                                   new long[] { 0x0000000000000088L });

  public static final BitSet FOLLOW_MODULES_in_modules464                      = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_MODULE_in_module478                        = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_CLASS_SPEC_in_module482                    = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_module484                    = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_EXTENSIONS_in_extensions499                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_extension_in_extensions501                 = new BitSet(
                                                                                   new long[] { 0x0000000000000208L });

  public static final BitSet FOLLOW_EXTENSIONS_in_extensions507                = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_EXTENSION_in_extension521                  = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_CLASS_SPEC_in_extension525                 = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_extension527                 = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_BUFFERS_in_buffers543                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_buffer_in_buffers546                       = new BitSet(
                                                                                   new long[] { 0x0000000000080008L });

  public static final BitSet FOLLOW_BUFFERS_in_buffers552                      = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_BUFFER_in_buffer567                        = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_buffer571                          = new BitSet(
                                                                                   new long[] { 0x0000000000001000L });

  public static final BitSet FOLLOW_chunks_in_buffer573                        = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_buffer575                    = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_CHUNK_TYPE_in_chunkType605                 = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_chunkType609                       = new BitSet(
                                                                                   new long[] { 0x0100000000000000L });

  public static final BitSet FOLLOW_parents_in_chunkType616                    = new BitSet(
                                                                                   new long[] { 0x0000020000000000L });

  public static final BitSet FOLLOW_slots_in_chunkType618                      = new BitSet(
                                                                                   new long[] { 0x0000000000001000L });

  public static final BitSet FOLLOW_chunks_in_chunkType622                     = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_chunkType624                 = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_CHUNKS_in_chunks642                        = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_chunk_in_chunks645                         = new BitSet(
                                                                                   new long[] { 0x0000008000002008L });

  public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_chunks664              = new BitSet(
                                                                                   new long[] { 0x0000008000002008L });

  public static final BitSet FOLLOW_CHUNKS_in_chunks674                        = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_CHUNK_in_chunk687                          = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_chunk691                           = new BitSet(
                                                                                   new long[] { 0x0080000000000000L });

  public static final BitSet FOLLOW_PARENT_in_chunk695                         = new BitSet(
                                                                                   new long[] { 0x0000020000000000L });

  public static final BitSet FOLLOW_slots_in_chunk697                          = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_chunk699                     = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_PRODUCTION_in_production723                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_production727                      = new BitSet(
                                                                                   new long[] { 0x0000000000100000L });

  public static final BitSet FOLLOW_conditions_in_production729                = new BitSet(
                                                                                   new long[] { 0x0000000002000000L });

  public static final BitSet FOLLOW_actions_in_production731                   = new BitSet(
                                                                                   new long[] { 0x0000000000010000L });

  public static final BitSet FOLLOW_parameters_in_production733                = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_CONDITIONS_in_conditions750                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_check_in_conditions753                     = new BitSet(
                                                                                   new long[] { 0x0000000001E00008L });

  public static final BitSet FOLLOW_query_in_conditions755                     = new BitSet(
                                                                                   new long[] { 0x0000000001E00008L });

  public static final BitSet FOLLOW_scriptCond_in_conditions757                = new BitSet(
                                                                                   new long[] { 0x0000000001E00008L });

  public static final BitSet FOLLOW_proxyCond_in_conditions759                 = new BitSet(
                                                                                   new long[] { 0x0000000001E00008L });

  public static final BitSet FOLLOW_ACTIONS_in_actions775                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_add_in_actions778                          = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_set_in_actions780                          = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_remove_in_actions782                       = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_modify_in_actions784                       = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_scriptAct_in_actions786                    = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_proxyAct_in_actions788                     = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_output_in_actions790                       = new BitSet(
                                                                                   new long[] { 0x00000001FC000008L });

  public static final BitSet FOLLOW_MATCH_CONDITION_in_check817                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_check821                           = new BitSet(
                                                                                   new long[] { 0x0000018800000008L });

  public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_check844               = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_check868          = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_VARIABLE_in_check892                       = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_check931                          = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_UNKNOWN_in_unknownList968                  = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_QUERY_CONDITION_in_query988                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_query992                           = new BitSet(
                                                                                   new long[] { 0x0000020000000000L });

  public static final BitSet FOLLOW_slots_in_query994                          = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_SCRIPTABLE_CONDITION_in_scriptCond1011     = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_LANG_in_scriptCond1013                     = new BitSet(
                                                                                   new long[] { 0x0000000400000000L });

  public static final BitSet FOLLOW_SCRIPT_in_scriptCond1015                   = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_PROXY_CONDITION_in_proxyCond1031           = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_CLASS_SPEC_in_proxyCond1033                = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_proxyCond1035                     = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_ADD_ACTION_in_add1064                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_add1068                            = new BitSet(
                                                                                   new long[] { 0x0000018800000000L });

  public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_add1090                = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_CHUNK_TYPE_IDENTIFIER_in_add1114           = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_VARIABLE_in_add1138                        = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_add1142                           = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_SET_ACTION_in_set1166                      = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_set1170                            = new BitSet(
                                                                                   new long[] { 0x0000028800000008L });

  public static final BitSet FOLLOW_CHUNK_IDENTIFIER_in_set1192                = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_VARIABLE_in_set1216                        = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_set1221                           = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_REMOVE_ACTION_in_remove1245                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_remove1249                         = new BitSet(
                                                                                   new long[] { 0x0000024800000008L });

  public static final BitSet FOLLOW_IDENTIFIER_in_remove1271                   = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_VARIABLE_in_remove1295                     = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_remove1317                        = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_MODIFY_ACTION_in_modify1332                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_modify1336                         = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_modify1338                        = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_SCRIPTABLE_ACTION_in_scriptAct1355         = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_LANG_in_scriptAct1357                      = new BitSet(
                                                                                   new long[] { 0x0000000400000000L });

  public static final BitSet FOLLOW_SCRIPT_in_scriptAct1359                    = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_PROXY_ACTION_in_proxyAct1374               = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_CLASS_SPEC_in_proxyAct1376                 = new BitSet(
                                                                                   new long[] { 0x0000020000000008L });

  public static final BitSet FOLLOW_slots_in_proxyAct1378                      = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_OUTPUT_ACTION_in_output1394                = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_STRING_in_output1398                       = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_PARENTS_in_parents1413                     = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_PARENT_in_parents1415                      = new BitSet(
                                                                                   new long[] { 0x0080000000000008L });

  public static final BitSet FOLLOW_PARENTS_in_parents1423                     = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOTS_in_slots1440                         = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_logic_in_slots1443                         = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_slot_in_slots1445                          = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_SLOTS_in_slots1454                         = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_PARAMETERS_in_parameters1473               = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_parameter_in_parameters1475                = new BitSet(
                                                                                   new long[] { 0x0000000000020008L });

  public static final BitSet FOLLOW_PARAMETERS_in_parameters1483               = new BitSet(
                                                                                   new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_PARAMETER_in_parameter1499                 = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_parameter1503                      = new BitSet(
                                                                                   new long[] { 0x0000001000000000L });

  public static final BitSet FOLLOW_STRING_in_parameter1507                    = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_LOGIC_in_logic1523                         = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_AND_in_logic1528                           = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_OR_in_logic1532                            = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_NOT_in_logic1536                           = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_logic_in_logic1540                         = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_slot_in_logic1542                          = new BitSet(
                                                                                   new long[] { 0x0010040000000008L });

  public static final BitSet FOLLOW_logic_in_logic1546                         = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_slot_in_logic1548                          = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_SLOT_in_slot1567                           = new BitSet(
                                                                                   new long[] { 0x0000000000000004L });

  public static final BitSet FOLLOW_NAME_in_slot1572                           = new BitSet(
                                                                                   new long[] { 0x0003F80000000000L });

  public static final BitSet FOLLOW_VARIABLE_in_slot1576                       = new BitSet(
                                                                                   new long[] { 0x0003F80000000000L });

  public static final BitSet FOLLOW_EQUALS_in_slot1582                         = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_GT_in_slot1586                             = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_GTE_in_slot1590                            = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_LT_in_slot1594                             = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_LTE_in_slot1598                            = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_NOT_in_slot1602                            = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_WITHIN_in_slot1606                         = new BitSet(
                                                                                   new long[] { 0x0000007800000000L });

  public static final BitSet FOLLOW_IDENTIFIER_in_slot1635                     = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_VARIABLE_in_slot1664                       = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_STRING_in_slot1668                         = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

  public static final BitSet FOLLOW_NUMBER_in_slot1672                         = new BitSet(
                                                                                   new long[] { 0x0000000000000008L });

}