// $ANTLR 3.2 Sep 23, 2009 12:02:23
// /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g
// 2013-05-01 08:51:51

package org.jactr.io.antlr3.parser.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.ISlot;
import org.jactr.io.antlr3.compiler.CompilationError;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.misc.CommonTreeException;
import org.jactr.io.antlr3.parser.AbstractModelParser;

public class JACTRParser extends Parser
{
  public static final String[] tokenNames                     = new String[] {
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
      "PARENT", "PARENTS", "UNKNOWN", "OPEN_ACTR_TOKEN", "OPEN_MODEL_TOKEN",
      "LONG_CLOSE_TOKEN", "CLOSE_MODEL_TOKEN", "CLOSE_ACTR_TOKEN",
      "OPEN_IMPORT_TOKEN", "URL_TOKEN", "SHORT_CLOSE_TOKEN",
      "OPEN_MODULES_TOKEN", "CLOSE_MODULES_TOKEN", "OPEN_MODULE_TOKEN",
      "IMPORT_ATTR_TOKEN", "CLOSE_MODULE_TOKEN", "OPEN_EXTENSIONS_TOKEN",
      "CLOSE_EXTENSIONS_TOKEN", "OPEN_EXTENSION_TOKEN",
      "CLOSE_EXTENSION_TOKEN", "OPEN_DECLARATIVE_MEMORY_TOKEN",
      "CLOSE_DECLARATIVE_MEMORY_TOKEN", "OPEN_PROCEDURAL_MEMORY_TOKEN",
      "CLOSE_PROCEDURAL_MEMORY_TOKEN", "OPEN_BUFFER_TOKEN",
      "CLOSE_BUFFER_TOKEN", "OPEN_CHUNK_TYPE_TOKEN", "CLOSE_CHUNK_TYPE_TOKEN",
      "OPEN_CHUNK_TOKEN", "CLOSE_CHUNK_TOKEN", "OPEN_PRODUCTION_TOKEN",
      "CLOSE_PRODUCTION_TOKEN", "OPEN_CONDITIONS_TOKEN",
      "CLOSE_CONDITIONS_TOKEN", "OPEN_ACTIONS_TOKEN", "CLOSE_ACTIONS_TOKEN",
      "OPEN_MATCH_TOKEN", "CLOSE_MATCH_TOKEN", "OPEN_QUERY_TOKEN",
      "CLOSE_QUERY_TOKEN", "OPEN_SCRIPT_COND_TOKEN", "CLOSE_SCRIPT_COND_TOKEN",
      "OPEN_PROXY_COND_TOKEN", "CLOSE_PROXY_COND_TOKEN",
      "OPEN_SCRIPT_ACT_TOKEN", "CLOSE_SCRIPT_ACT_TOKEN", "CDATA_TOKEN",
      "OPEN_PROXY_ACT_TOKEN", "CLOSE_PROXY_ACT_TOKEN", "OPEN_OUTPUT_TOKEN",
      "CLOSE_OUTPUT_TOKEN", "OPEN_REMOVE_TOKEN", "CLOSE_REMOVE_TOKEN",
      "OPEN_MODIFY_TOKEN", "CLOSE_MODIFY_TOKEN", "OPEN_ADD_TOKEN",
      "CLOSE_ADD_TOKEN", "OPEN_SET_TOKEN", "CLOSE_SET_TOKEN", "STOP_TOKEN",
      "OPEN_PARAMETERS_TOKEN", "CLOSE_PARAMETERS_TOKEN",
      "OPEN_PARAMETER_TOKEN", "VERSION_ATTR_TOKEN", "STRING_TOKEN",
      "CLASS_ATTR_TOKEN", "NAME_ATTR_TOKEN", "PARENT_ATTR_TOKEN",
      "TYPE_ATTR_TOKEN", "VALUE_ATTR_TOKEN", "BUFFER_ATTR_TOKEN",
      "LANG_ATTR_TOKEN", "CHUNK_ATTR_TOKEN", "SLOT_EQ_TOKEN", "SLOT_NOT_TOKEN",
      "SLOT_GT_TOKEN", "SLOT_GTE_TOKEN", "SLOT_LT_TOKEN", "SLOT_LTE_TOKEN",
      "SLOT_WITHIN_TOKEN", "OPEN_OR_TOKEN", "CLOSE_OR_TOKEN", "OPEN_AND_TOKEN",
      "CLOSE_AND_TOKEN", "OPEN_NOT_TOKEN", "CLOSE_NOT_TOKEN",
      "OPEN_SLOT_TOKEN", "XML_HEADER_TOKEN", "COMMENT_TOKEN", "CDATA_FRAGMENT",
      "ACTR_FRAGMENT", "OPEN_FRAGMENT", "WS", "CLOSE_FRAGMENT",
      "SLASH_FRAGMENT", "MODEL_FRAGMENT", "NAME_FRAGMENT", "CHUNK_FRAGMENT",
      "CHUNK_TYPE_FRAGMENT", "PRODUCTION_FRAGMENT", "PARAMETER_FRAGMENT",
      "PARAMETERS_FRAGMENT", "BUFFER_FRAGMENT", "MATCH_FRAGMENT",
      "QUERY_FRAGMENT", "OUTPUT_FRAGMENT", "ADD_FRAGMENT", "SET_FRAGMENT",
      "MODIFY_FRAGMENT", "REMOVE_FRAGMENT", "STOP_FRAGMENT",
      "PROXY_COND_FRAGMENT", "PROXY_FRAGMENT", "CONDITION_FRAGMENT",
      "PROXY_ACT_FRAGMENT", "ACTION_FRAGMENT", "SCRIPTABLE_COND_FRAGMENT",
      "SCRIPTABLE_FRAGMENT", "SCRIPTABLE_ACT_FRAGMENT", "MODULE_FRAGMENT",
      "MODULES_FRAGMENT", "EXTENSION_FRAGMENT", "EXTENSIONS_FRAGMENT",
      "THAN_TOKEN", "MEMORY", "DECLARATIVE_MEMORY_FRAGMENT",
      "PROCEDURAL_MEMORY_FRAGMENT", "LETTER_FRAGMENT", "DIGITS_FRAGMENT",
      "IDENTIFIER_TOKEN", "ESCAPE_TOKEN", "NUMBER_TOKEN", "'='" };

  public static final int      ACTR_FRAGMENT                  = 145;

  public static final int      LOGIC                          = 52;

  public static final int      CHUNK_TYPE_FRAGMENT            = 153;

  public static final int      CHUNK                          = 13;

  public static final int      CLOSE_FRAGMENT                 = 148;

  public static final int      LANG_ATTR_TOKEN                = 126;

  public static final int      EXTENSION_FRAGMENT             = 176;

  public static final int      CHUNK_TYPE                     = 11;

  public static final int      NOT                            = 46;

  public static final int      ESCAPE_TOKEN                   = 185;

  public static final int      EOF                            = -1;

  public static final int      STRING_TOKEN                   = 119;

  public static final int      ADD_ACTION                     = 26;

  public static final int      OPEN_SCRIPT_ACT_TOKEN          = 99;

  public static final int      CHUNK_ATTR_TOKEN               = 127;

  public static final int      MODIFY_FRAGMENT                = 163;

  public static final int      OPEN_ACTIONS_TOKEN             = 89;

  public static final int      CHUNK_FRAGMENT                 = 152;

  public static final int      PROCEDURAL_MEMORY              = 14;

  public static final int      CLOSE_OR_TOKEN                 = 136;

  public static final int      DECLARATIVE_MEMORY             = 10;

  public static final int      CLOSE_OUTPUT_TOKEN             = 105;

  public static final int      OPEN_CHUNK_TYPE_TOKEN          = 81;

  public static final int      OPEN_OR_TOKEN                  = 135;

  public static final int      OPEN_PRODUCTION_TOKEN          = 85;

  public static final int      SCRIPT                         = 34;

  public static final int      SLOT_LTE_TOKEN                 = 133;

  public static final int      NUMBER                         = 37;

  public static final int      OPEN_FRAGMENT                  = 146;

  public static final int      IMPORT_ATTR_TOKEN              = 69;

  public static final int      DECLARATIVE_MEMORY_FRAGMENT    = 180;

  public static final int      DIGITS_FRAGMENT                = 183;

  public static final int      PARAMETER_FRAGMENT             = 155;

  public static final int      MODULES_FRAGMENT               = 175;

  public static final int      CHUNK_TYPE_IDENTIFIER          = 40;

  public static final int      CLOSE_MATCH_TOKEN              = 92;

  public static final int      WS                             = 147;

  public static final int      OPEN_BUFFER_TOKEN              = 79;

  public static final int      SLOT_WITHIN_TOKEN              = 134;

  public static final int      OPEN_CONDITIONS_TOKEN          = 87;

  public static final int      SLOT                           = 42;

  public static final int      WITHIN                         = 47;

  public static final int      OPEN_PARAMETER_TOKEN           = 117;

  public static final int      SLOTS                          = 41;

  public static final int      GT                             = 44;

  public static final int      BUFFER_FRAGMENT                = 157;

  public static final int      CLOSE_DECLARATIVE_MEMORY_TOKEN = 76;

  public static final int      QUERY_CONDITION                = 22;

  public static final int      CONDITION_FRAGMENT             = 168;

  public static final int      CLOSE_EXTENSION_TOKEN          = 74;

  public static final int      LANG                           = 33;

  public static final int      OPEN_SLOT_TOKEN                = 141;

  public static final int      CLOSE_PROXY_ACT_TOKEN          = 103;

  public static final int      PARAMETERS                     = 16;

  public static final int      BUFFER_ATTR_TOKEN              = 125;

  public static final int      CLOSE_SET_TOKEN                = 113;

  public static final int      REMOVE_FRAGMENT                = 164;

  public static final int      LIBRARY                        = 5;

  public static final int      XML_HEADER_TOKEN               = 142;

  public static final int      THAN_TOKEN                     = 178;

  public static final int      STOP_TOKEN                     = 114;

  public static final int      SHORT_CLOSE_TOKEN              = 65;

  public static final int      CLOSE_QUERY_TOKEN              = 94;

  public static final int      CLOSE_MODULE_TOKEN             = 70;

  public static final int      PARENT                         = 55;

  public static final int      EXTENSIONS                     = 8;

  public static final int      OUTPUT_FRAGMENT                = 160;

  public static final int      MODULES                        = 6;

  public static final int      SLOT_LT_TOKEN                  = 132;

  public static final int      CLOSE_NOT_TOKEN                = 140;

  public static final int      CLOSE_MODULES_TOKEN            = 67;

  public static final int      PROXY_ACTION                   = 32;

  public static final int      CLOSE_ACTR_TOKEN               = 62;

  public static final int      CLOSE_CHUNK_TYPE_TOKEN         = 82;

  public static final int      MODULE                         = 7;

  public static final int      OPEN_CHUNK_TOKEN               = 83;

  public static final int      OPEN_IMPORT_TOKEN              = 63;

  public static final int      CLASS_ATTR_TOKEN               = 120;

  public static final int      CLOSE_EXTENSIONS_TOKEN         = 72;

  public static final int      ADD_FRAGMENT                   = 161;

  public static final int      SCRIPTABLE_COND_FRAGMENT       = 171;

  public static final int      CHUNKS                         = 12;

  public static final int      PROXY_COND_FRAGMENT            = 166;

  public static final int      SLASH_FRAGMENT                 = 149;

  public static final int      SET_FRAGMENT                   = 162;

  public static final int      EXTENSION                      = 9;

  public static final int      OUTPUT_ACTION                  = 30;

  public static final int      STRING                         = 36;

  public static final int      OPEN_PROCEDURAL_MEMORY_TOKEN   = 77;

  public static final int      CLOSE_PROCEDURAL_MEMORY_TOKEN  = 78;

  public static final int      LT                             = 43;

  public static final int      OPEN_OUTPUT_TOKEN              = 104;

  public static final int      SLOT_GTE_TOKEN                 = 131;

  public static final int      SCRIPTABLE_FRAGMENT            = 172;

  public static final int      PROXY_CONDITION                = 24;

  public static final int      OPEN_EXTENSION_TOKEN           = 73;

  public static final int      EQUALS                         = 45;

  public static final int      STOP_FRAGMENT                  = 165;

  public static final int      COMMENT_TOKEN                  = 143;

  public static final int      ACTIONS                        = 25;

  public static final int      OPEN_MATCH_TOKEN               = 91;

  public static final int      NAME                           = 54;

  public static final int      OPEN_PROXY_ACT_TOKEN           = 102;

  public static final int      SLOT_GT_TOKEN                  = 130;

  public static final int      OPEN_PROXY_COND_TOKEN          = 97;

  public static final int      PROCEDURAL_MEMORY_FRAGMENT     = 181;

  public static final int      PARAMETER                      = 17;

  public static final int      VALUE_ATTR_TOKEN               = 124;

  public static final int      MATCH_CONDITION                = 21;

  public static final int      OPEN_DECLARATIVE_MEMORY_TOKEN  = 75;

  public static final int      OPEN_QUERY_TOKEN               = 93;

  public static final int      NAME_ATTR_TOKEN                = 121;

  public static final int      CLOSE_MODIFY_TOKEN             = 109;

  public static final int      CLOSE_BUFFER_TOKEN             = 80;

  public static final int      CLOSE_PROXY_COND_TOKEN         = 98;

  public static final int      VERSION_ATTR_TOKEN             = 118;

  public static final int      CDATA_TOKEN                    = 101;

  public static final int      CONDITIONS                     = 20;

  public static final int      PROXY_ACT_FRAGMENT             = 169;

  public static final int      QUERY_FRAGMENT                 = 159;

  public static final int      CLOSE_SCRIPT_COND_TOKEN        = 96;

  public static final int      ACTION_FRAGMENT                = 170;

  public static final int      MODIFY_ACTION                  = 29;

  public static final int      PARAMETERS_FRAGMENT            = 156;

  public static final int      VARIABLE                       = 35;

  public static final int      OPEN_ADD_TOKEN                 = 110;

  public static final int      CLASS_SPEC                     = 53;

  public static final int      SLOT_NOT_TOKEN                 = 129;

  public static final int      OR                             = 50;

  public static final int      OPEN_MODEL_TOKEN               = 59;

  public static final int      IDENTIFIER_TOKEN               = 184;

  public static final int      OPEN_EXTENSIONS_TOKEN          = 71;

  public static final int      MEMORY                         = 179;

  public static final int      OPEN_REMOVE_TOKEN              = 106;

  public static final int      SCRIPTABLE_ACT_FRAGMENT        = 173;

  public static final int      SCRIPTABLE_ACTION              = 31;

  public static final int      TYPE_ATTR_TOKEN                = 123;

  public static final int      LETTER_FRAGMENT                = 182;

  public static final int      CLOSE_REMOVE_TOKEN             = 107;

  public static final int      GTE                            = 48;

  public static final int      CLOSE_PRODUCTION_TOKEN         = 86;

  public static final int      PROXY_FRAGMENT                 = 167;

  public static final int      OPEN_SCRIPT_COND_TOKEN         = 95;

  public static final int      AND                            = 51;

  public static final int      CLOSE_ADD_TOKEN                = 111;

  public static final int      LTE                            = 49;

  public static final int      OPEN_MODULE_TOKEN              = 68;

  public static final int      PRODUCTION_FRAGMENT            = 154;

  public static final int      CLOSE_CONDITIONS_TOKEN         = 88;

  public static final int      PARENT_ATTR_TOKEN              = 122;

  public static final int      OPEN_SET_TOKEN                 = 112;

  public static final int      UNKNOWN                        = 57;

  public static final int      SLOT_EQ_TOKEN                  = 128;

  public static final int      MATCH_FRAGMENT                 = 158;

  public static final int      IDENTIFIER                     = 38;

  public static final int      PRODUCTION                     = 15;

  public static final int      OPEN_PARAMETERS_TOKEN          = 115;

  public static final int      MODEL                          = 4;

  public static final int      NAME_FRAGMENT                  = 151;

  public static final int      REMOVE_ACTION                  = 28;

  public static final int      OPEN_AND_TOKEN                 = 137;

  public static final int      CLOSE_PARAMETERS_TOKEN         = 116;

  public static final int      CLOSE_MODEL_TOKEN              = 61;

  public static final int      MODULE_FRAGMENT                = 174;

  public static final int      URL_TOKEN                      = 64;

  public static final int      T__187                         = 187;

  public static final int      NUMBER_TOKEN                   = 186;

  public static final int      MODEL_FRAGMENT                 = 150;

  public static final int      CHUNK_IDENTIFIER               = 39;

  public static final int      BUFFER                         = 19;

  public static final int      CLOSE_CHUNK_TOKEN              = 84;

  public static final int      SCRIPTABLE_CONDITION           = 23;

  public static final int      CDATA_FRAGMENT                 = 144;

  public static final int      PARENTS                        = 56;

  public static final int      CLOSE_AND_TOKEN                = 138;

  public static final int      OPEN_ACTR_TOKEN                = 58;

  public static final int      OPEN_MODIFY_TOKEN              = 108;

  public static final int      OPEN_NOT_TOKEN                 = 139;

  public static final int      CLOSE_ACTIONS_TOKEN            = 90;

  public static final int      LONG_CLOSE_TOKEN               = 60;

  public static final int      BUFFERS                        = 18;

  public static final int      SET_ACTION                     = 27;

  public static final int      OPEN_MODULES_TOKEN             = 66;

  public static final int      CLOSE_SCRIPT_ACT_TOKEN         = 100;

  public static final int      EXTENSIONS_FRAGMENT            = 177;

  // delegates
  // delegators

  protected static class ModelGlobals_scope
  {
    List<CommonTree>        misplacedChunks;

    Map<String, CommonTree> chunksWrapperMap;

    Map<String, CommonTree> temporaryChunkTypesMap;

    CommonTree              modelTree;
  }

  protected Stack ModelGlobals_stack = new Stack();

  public JACTRParser(TokenStream input)
  {
    this(input, new RecognizerSharedState());
  }

  public JACTRParser(TokenStream input, RecognizerSharedState state)
  {
    super(input, state);

  }

  protected TreeAdaptor adaptor = new CommonTreeAdaptor();

  public void setTreeAdaptor(TreeAdaptor adaptor)
  {
    this.adaptor = adaptor;
  }

  public TreeAdaptor getTreeAdaptor()
  {
    return adaptor;
  }

  @Override
  public String[] getTokenNames()
  {
    return JACTRParser.tokenNames;
  }

  @Override
  public String getGrammarFileName()
  {
    return "/Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g";
  }

  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(JACTRParser.class);

  private ASTSupport                 _support = new ASTSupport();

  private AbstractModelParser        _parser;

  public void setModelParser(AbstractModelParser parser,
      CommonTreeAdaptor adaptor)
  {
    _parser = parser;
    setTreeAdaptor(adaptor);
    _support.setTreeAdaptor(adaptor);
  }

  @Override
  public void reportError(RecognitionException re)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug(re);
    reportException(re);
    super.reportError(re);
  }

  public void reportException(Exception e)
  {
    _parser.reportException(e, false);
  }

  protected void stealChildren(CommonTree newRoot, CommonTree oldTree)
  {
    LOGGER.debug("oldTree has " + oldTree.getChildCount()
        + " children to be stolen");
    for (int i = 0; i < oldTree.getChildCount(); i++)
    {
      Tree node = oldTree.getChild(i);
      LOGGER.debug("moved " + node.toStringTree() + " under "
          + newRoot.toStringTree());
      newRoot.addChild(node);
    }
  }

  public static class model_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "model"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:184:1:
  // model : OPEN_ACTR_TOKEN OPEN_MODEL_TOKEN n= name (v= version )?
  // LONG_CLOSE_TOKEN (m= modules )? (e= extensions )? ( importDirective )*
  // library (b= buffer )* (p= parameters )? CLOSE_MODEL_TOKEN CLOSE_ACTR_TOKEN
  // -> ^() ;
  public final JACTRParser.model_return model() throws RecognitionException
  {
    ModelGlobals_stack.push(new ModelGlobals_scope());

    JACTRParser.model_return retval = new JACTRParser.model_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token OPEN_ACTR_TOKEN1 = null;
    Token OPEN_MODEL_TOKEN2 = null;
    Token LONG_CLOSE_TOKEN3 = null;
    Token CLOSE_MODEL_TOKEN6 = null;
    Token CLOSE_ACTR_TOKEN7 = null;
    JACTRParser.name_return n = null;

    JACTRParser.version_return v = null;

    JACTRParser.modules_return m = null;

    JACTRParser.extensions_return e = null;

    JACTRParser.buffer_return b = null;

    JACTRParser.parameters_return p = null;

    JACTRParser.importDirective_return importDirective4 = null;

    JACTRParser.library_return library5 = null;

    RewriteRuleTokenStream stream_OPEN_MODEL_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MODEL_TOKEN");
    RewriteRuleTokenStream stream_OPEN_ACTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_ACTR_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_ACTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_ACTR_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_MODEL_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_MODEL_TOKEN");
    RewriteRuleSubtreeStream stream_buffer = new RewriteRuleSubtreeStream(
        adaptor, "rule buffer");
    RewriteRuleSubtreeStream stream_importDirective = new RewriteRuleSubtreeStream(
        adaptor, "rule importDirective");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");
    RewriteRuleSubtreeStream stream_library = new RewriteRuleSubtreeStream(
        adaptor, "rule library");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");
    RewriteRuleSubtreeStream stream_modules = new RewriteRuleSubtreeStream(
        adaptor, "rule modules");
    RewriteRuleSubtreeStream stream_extensions = new RewriteRuleSubtreeStream(
        adaptor, "rule extensions");
    RewriteRuleSubtreeStream stream_version = new RewriteRuleSubtreeStream(
        adaptor, "rule version");

    // initialize the global
    ((ModelGlobals_scope) ModelGlobals_stack.peek()).misplacedChunks = new ArrayList<CommonTree>();
    ((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap = new HashMap<String, CommonTree>();
    ((ModelGlobals_scope) ModelGlobals_stack.peek()).temporaryChunkTypesMap = new HashMap<String, CommonTree>();
    CommonTree modelTree = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:193:1:
      // ( OPEN_ACTR_TOKEN OPEN_MODEL_TOKEN n= name (v= version )?
      // LONG_CLOSE_TOKEN (m= modules )? (e= extensions )? ( importDirective )*
      // library (b= buffer )* (p= parameters )? CLOSE_MODEL_TOKEN
      // CLOSE_ACTR_TOKEN -> ^() )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:193:3:
      // OPEN_ACTR_TOKEN OPEN_MODEL_TOKEN n= name (v= version )?
      // LONG_CLOSE_TOKEN (m= modules )? (e= extensions )? ( importDirective )*
      // library (b= buffer )* (p= parameters )? CLOSE_MODEL_TOKEN
      // CLOSE_ACTR_TOKEN
      {
        OPEN_ACTR_TOKEN1 = (Token) match(input, OPEN_ACTR_TOKEN,
            FOLLOW_OPEN_ACTR_TOKEN_in_model362);
        stream_OPEN_ACTR_TOKEN.add(OPEN_ACTR_TOKEN1);

        OPEN_MODEL_TOKEN2 = (Token) match(input, OPEN_MODEL_TOKEN,
            FOLLOW_OPEN_MODEL_TOKEN_in_model381);
        stream_OPEN_MODEL_TOKEN.add(OPEN_MODEL_TOKEN2);

        pushFollow(FOLLOW_name_in_model385);
        n = name();

        state._fsp--;

        stream_name.add(n.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:194:41:
        // (v= version )?
        int alt1 = 2;
        int LA1_0 = input.LA(1);

        if (LA1_0 == VERSION_ATTR_TOKEN) alt1 = 1;
        switch (alt1)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:194:42:
          // v= version
          {
            pushFollow(FOLLOW_version_in_model390);
            v = version();

            state._fsp--;

            stream_version.add(v.getTree());

          }
            break;

        }

        LONG_CLOSE_TOKEN3 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_model394);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN3);

        modelTree = _support.createModelTree(n.tree.getText());
        ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree = modelTree;

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:200:17:
        // (m= modules )?
        int alt2 = 2;
        int LA2_0 = input.LA(1);

        if (LA2_0 == OPEN_MODULES_TOKEN) alt2 = 1;
        switch (alt2)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:200:18:
          // m= modules
          {
            pushFollow(FOLLOW_modules_in_model433);
            m = modules();

            state._fsp--;

            stream_modules.add(m.getTree());

          }
            break;

        }

        if (m != null)
          stealChildren(
              ASTSupport.getFirstDescendantWithType(modelTree, MODULES), m.tree);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:205:17:
        // (e= extensions )?
        int alt3 = 2;
        int LA3_0 = input.LA(1);

        if (LA3_0 == OPEN_EXTENSIONS_TOKEN) alt3 = 1;
        switch (alt3)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:205:18:
          // e= extensions
          {
            pushFollow(FOLLOW_extensions_in_model474);
            e = extensions();

            state._fsp--;

            stream_extensions.add(e.getTree());

          }
            break;

        }

        // pull the extension nodes into the defined EXTENSIONS node of
        // modelTree
        if (e != null)
          stealChildren(
              ASTSupport.getFirstDescendantWithType(modelTree, EXTENSIONS),
              e.tree);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:211:17:
        // ( importDirective )*
        loop4: do
        {
          int alt4 = 2;
          int LA4_0 = input.LA(1);

          if (LA4_0 == OPEN_IMPORT_TOKEN) alt4 = 1;

          switch (alt4)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:211:18:
            // importDirective
            {
              pushFollow(FOLLOW_importDirective_in_model513);
              importDirective4 = importDirective();

              state._fsp--;

              stream_importDirective.add(importDirective4.getTree());

            }
              break;

            default:
              break loop4;
          }
        }
        while (true);

        pushFollow(FOLLOW_library_in_model533);
        library5 = library();

        state._fsp--;

        stream_library.add(library5.getTree());

        // no need to do anything here

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:216:17:
        // (b= buffer )*
        loop5: do
        {
          int alt5 = 2;
          int LA5_0 = input.LA(1);

          if (LA5_0 == OPEN_BUFFER_TOKEN) alt5 = 1;

          switch (alt5)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:216:18:
            // b= buffer
            {
              pushFollow(FOLLOW_buffer_in_model572);
              b = buffer();

              state._fsp--;

              stream_buffer.add(b.getTree());

              /*
               * we've got a bunch of buffers.. we can't define buffers here,
               * but we can set parameters and contents so we should only be
               * concerned with buffers that we know about (have been defined by
               * the modules)
               */
              Map<String, CommonTree> knownBuffers = ASTSupport.getMapOfTrees(
                  modelTree, BUFFER);

              CommonTree bufferTree = b != null ? (CommonTree) b.tree : null;
              String bName = ASTSupport
                  .getFirstDescendantWithType(bufferTree, NAME).getText()
                  .toLowerCase();
              CommonTree actualBufferTree = knownBuffers.get(bName);
              if (actualBufferTree == null)
              {
                LOGGER.debug(bName
                    + " is not a known buffer, adding it temporarily");
                modelTree.getFirstChildWithType(BUFFERS).addChild(bufferTree);
                // reportException(new
                // CompilationWarning(bName+" is not a known buffer",
                // bufferTree));
              }
              else
              {
                LOGGER.debug("Attempting to swap contents of provided buffer "
                    + bufferTree.toStringTree() + " into "
                    + actualBufferTree.toStringTree());
                // snag its chunks and paramaeters
                // stealChildren(ASTSupport.getFirstDescendantWithType(actualBufferTree,
                // PARAMETERS),
                // ASTSupport.getFirstDescendantWithType(bufferTree,
                // PARAMETERS));
                // src, dest
                ASTSupport.setParameters(ASTSupport.getFirstDescendantWithType(
                    bufferTree, PARAMETERS), ASTSupport
                    .getFirstDescendantWithType(actualBufferTree, PARAMETERS));
                // dest, src
                stealChildren(ASTSupport.getFirstDescendantWithType(
                    actualBufferTree, CHUNKS),
                    ASTSupport.getFirstDescendantWithType(bufferTree, CHUNKS));
              }

            }
              break;

            default:
              break loop5;
          }
        }
        while (true);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:249:17:
        // (p= parameters )?
        int alt6 = 2;
        int LA6_0 = input.LA(1);

        if (LA6_0 == OPEN_PARAMETERS_TOKEN) alt6 = 1;
        switch (alt6)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:249:18:
          // p= parameters
          {
            pushFollow(FOLLOW_parameters_in_model613);
            p = parameters();

            state._fsp--;

            stream_parameters.add(p.getTree());

          }
            break;

        }

        // pull the parameter nodes into the defined PARAMETERS node of
        // modelTree
        if (p != null)
          stealChildren(
              ASTSupport.getFirstDescendantWithType(modelTree, PARAMETERS),
              p.tree);

        CLOSE_MODEL_TOKEN6 = (Token) match(input, CLOSE_MODEL_TOKEN,
            FOLLOW_CLOSE_MODEL_TOKEN_in_model651);
        stream_CLOSE_MODEL_TOKEN.add(CLOSE_MODEL_TOKEN6);

        CLOSE_ACTR_TOKEN7 = (Token) match(input, CLOSE_ACTR_TOKEN,
            FOLLOW_CLOSE_ACTR_TOKEN_in_model669);
        stream_CLOSE_ACTR_TOKEN.add(CLOSE_ACTR_TOKEN7);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 257:17: -> ^()
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:258:17:
          // ^()
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(modelTree, root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
      ModelGlobals_stack.pop();

    }
    return retval;
  }

  // $ANTLR end "model"

  public static class importDirective_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "importDirective"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:261:1:
  // importDirective : OPEN_IMPORT_TOKEN URL_TOKEN s= string SHORT_CLOSE_TOKEN ;
  public final JACTRParser.importDirective_return importDirective()
      throws RecognitionException
  {
    JACTRParser.importDirective_return retval = new JACTRParser.importDirective_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token OPEN_IMPORT_TOKEN8 = null;
    Token URL_TOKEN9 = null;
    Token SHORT_CLOSE_TOKEN10 = null;
    JACTRParser.string_return s = null;

    CommonTree OPEN_IMPORT_TOKEN8_tree = null;
    CommonTree URL_TOKEN9_tree = null;
    CommonTree SHORT_CLOSE_TOKEN10_tree = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:262:2:
      // ( OPEN_IMPORT_TOKEN URL_TOKEN s= string SHORT_CLOSE_TOKEN )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:262:4:
      // OPEN_IMPORT_TOKEN URL_TOKEN s= string SHORT_CLOSE_TOKEN
      {
        root_0 = (CommonTree) adaptor.nil();

        OPEN_IMPORT_TOKEN8 = (Token) match(input, OPEN_IMPORT_TOKEN,
            FOLLOW_OPEN_IMPORT_TOKEN_in_importDirective720);
        OPEN_IMPORT_TOKEN8_tree = (CommonTree) adaptor
            .create(OPEN_IMPORT_TOKEN8);
        adaptor.addChild(root_0, OPEN_IMPORT_TOKEN8_tree);

        URL_TOKEN9 = (Token) match(input, URL_TOKEN,
            FOLLOW_URL_TOKEN_in_importDirective723);
        URL_TOKEN9_tree = (CommonTree) adaptor.create(URL_TOKEN9);
        adaptor.addChild(root_0, URL_TOKEN9_tree);

        pushFollow(FOLLOW_string_in_importDirective727);
        s = string();

        state._fsp--;

        adaptor.addChild(root_0, s.getTree());
        SHORT_CLOSE_TOKEN10 = (Token) match(input, SHORT_CLOSE_TOKEN,
            FOLLOW_SHORT_CLOSE_TOKEN_in_importDirective729);
        SHORT_CLOSE_TOKEN10_tree = (CommonTree) adaptor
            .create(SHORT_CLOSE_TOKEN10);
        adaptor.addChild(root_0, SHORT_CLOSE_TOKEN10_tree);

        String url = "";
        if (s != null)
          url = s != null ? input.toString(s.start, s.stop) : null;

        /*
         * first we try to get it from the class loader
         */
        // URL location = getClass().getClassLoader().getResource(url);
        URL location = _parser.getImportDelegate().resolveURL(url,
            _parser.getBaseURL());
        if (location == null)
          reportException(new CompilationError("Could not resolve url " + url
              + " using base " + _parser.getBaseURL(), s.tree));
        else
          try
          {
            _parser.getImportDelegate().importInto(
                ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree,
                location, false);
            /*
             * having just imported a slew of chunks,types and productions, we
             * do need to update our internal tables, particular the
             * chunkTypeWrappers
             */
            // Map<String, CommonTree> chunkTypes =
            // ASTSupport.getMapOfTrees(((ModelGlobals_scope)ModelGlobals_stack.peek()).modelTree,
            // CHUNK_TYPE);
            // for(Map.Entry<String,CommonTree> chunkType :
            // chunkTypes.entrySet())
            // {
            // ((ModelGlobals_scope)ModelGlobals_stack.peek()).chunksWrapperMap.put(chunkType.getKey(),
            // ASTSupport.getFirstDescendantWithType(chunkType.getValue(),
            // CHUNKS));
            // }
          }
          catch (Exception e)
          {
            reportException(new CompilationError("Could not import from "
                + location + ", " + e.getMessage(), s.tree, e));
          }

      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "importDirective"

  public static class modules_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "modules"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:295:1:
  // modules : m= OPEN_MODULES_TOKEN ( module )* CLOSE_MODULES_TOKEN -> ^(
  // MODULES[$m] ( module )* ) ;
  public final JACTRParser.modules_return modules() throws RecognitionException
  {
    JACTRParser.modules_return retval = new JACTRParser.modules_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token m = null;
    Token CLOSE_MODULES_TOKEN12 = null;
    JACTRParser.module_return module11 = null;

    RewriteRuleTokenStream stream_CLOSE_MODULES_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_MODULES_TOKEN");
    RewriteRuleTokenStream stream_OPEN_MODULES_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MODULES_TOKEN");
    RewriteRuleSubtreeStream stream_module = new RewriteRuleSubtreeStream(
        adaptor, "rule module");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:295:9:
      // (m= OPEN_MODULES_TOKEN ( module )* CLOSE_MODULES_TOKEN -> ^(
      // MODULES[$m] ( module )* ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:295:13:
      // m= OPEN_MODULES_TOKEN ( module )* CLOSE_MODULES_TOKEN
      {
        m = (Token) match(input, OPEN_MODULES_TOKEN,
            FOLLOW_OPEN_MODULES_TOKEN_in_modules744);
        stream_OPEN_MODULES_TOKEN.add(m);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:296:13:
        // ( module )*
        loop7: do
        {
          int alt7 = 2;
          int LA7_0 = input.LA(1);

          if (LA7_0 == OPEN_MODULE_TOKEN) alt7 = 1;

          switch (alt7)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:296:13:
            // module
            {
              pushFollow(FOLLOW_module_in_modules758);
              module11 = module();

              state._fsp--;

              stream_module.add(module11.getTree());

            }
              break;

            default:
              break loop7;
          }
        }
        while (true);

        CLOSE_MODULES_TOKEN12 = (Token) match(input, CLOSE_MODULES_TOKEN,
            FOLLOW_CLOSE_MODULES_TOKEN_in_modules773);
        stream_CLOSE_MODULES_TOKEN.add(CLOSE_MODULES_TOKEN12);

        // AST REWRITE
        // elements: module
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 298:14: -> ^( MODULES[$m] ( module )* )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:298:17:
          // ^( MODULES[$m] ( module )* )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(MODULES, m), root_1);

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:298:31:
            // ( module )*
            while (stream_module.hasNext())
              adaptor.addChild(root_1, stream_module.nextTree());
            stream_module.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "modules"

  public static class module_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "module"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:300:1:
  // module : OPEN_MODULE_TOKEN c= classSpec ( IMPORT_ATTR_TOKEN '=' i= string
  // )? ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
  // CLOSE_MODULE_TOKEN ) ) -> ^() ;
  public final JACTRParser.module_return module() throws RecognitionException
  {
    JACTRParser.module_return retval = new JACTRParser.module_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token OPEN_MODULE_TOKEN13 = null;
    Token IMPORT_ATTR_TOKEN14 = null;
    Token char_literal15 = null;
    Token SHORT_CLOSE_TOKEN16 = null;
    Token LONG_CLOSE_TOKEN17 = null;
    Token CLOSE_MODULE_TOKEN18 = null;
    JACTRParser.classSpec_return c = null;

    JACTRParser.string_return i = null;

    JACTRParser.parameters_return p = null;

    RewriteRuleTokenStream stream_IMPORT_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token IMPORT_ATTR_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_MODULE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_MODULE_TOKEN");
    RewriteRuleTokenStream stream_187 = new RewriteRuleTokenStream(adaptor,
        "token 187");
    RewriteRuleTokenStream stream_OPEN_MODULE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MODULE_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_string = new RewriteRuleSubtreeStream(
        adaptor, "rule string");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");
    RewriteRuleSubtreeStream stream_classSpec = new RewriteRuleSubtreeStream(
        adaptor, "rule classSpec");

    CommonTree moduleNode = null;
    adaptor.create(PARAMETERS, "parameters");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:304:2:
      // ( OPEN_MODULE_TOKEN c= classSpec ( IMPORT_ATTR_TOKEN '=' i= string )? (
      // ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
      // CLOSE_MODULE_TOKEN ) ) -> ^() )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:304:4:
      // OPEN_MODULE_TOKEN c= classSpec ( IMPORT_ATTR_TOKEN '=' i= string )? ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
      // CLOSE_MODULE_TOKEN ) )
      {
        OPEN_MODULE_TOKEN13 = (Token) match(input, OPEN_MODULE_TOKEN,
            FOLLOW_OPEN_MODULE_TOKEN_in_module809);
        stream_OPEN_MODULE_TOKEN.add(OPEN_MODULE_TOKEN13);

        pushFollow(FOLLOW_classSpec_in_module813);
        c = classSpec();

        state._fsp--;

        stream_classSpec.add(c.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:304:34:
        // ( IMPORT_ATTR_TOKEN '=' i= string )?
        int alt8 = 2;
        int LA8_0 = input.LA(1);

        if (LA8_0 == IMPORT_ATTR_TOKEN) alt8 = 1;
        switch (alt8)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:304:35:
          // IMPORT_ATTR_TOKEN '=' i= string
          {
            IMPORT_ATTR_TOKEN14 = (Token) match(input, IMPORT_ATTR_TOKEN,
                FOLLOW_IMPORT_ATTR_TOKEN_in_module816);
            stream_IMPORT_ATTR_TOKEN.add(IMPORT_ATTR_TOKEN14);

            char_literal15 = (Token) match(input, 187, FOLLOW_187_in_module818);
            stream_187.add(char_literal15);

            pushFollow(FOLLOW_string_in_module822);
            i = string();

            state._fsp--;

            stream_string.add(i.getTree());

          }
            break;

        }

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:305:17:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
        // CLOSE_MODULE_TOKEN ) )
        int alt9 = 2;
        int LA9_0 = input.LA(1);

        if (LA9_0 == SHORT_CLOSE_TOKEN)
          alt9 = 1;
        else if (LA9_0 == LONG_CLOSE_TOKEN)
          alt9 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 9, 0, input);

          throw nvae;
        }
        switch (alt9)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:305:18:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:305:18:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:305:19:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN16 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_module844);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN16);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:306:18:
          // ( LONG_CLOSE_TOKEN p= parameters CLOSE_MODULE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:306:18:
            // ( LONG_CLOSE_TOKEN p= parameters CLOSE_MODULE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:306:19:
            // LONG_CLOSE_TOKEN p= parameters CLOSE_MODULE_TOKEN
            {
              LONG_CLOSE_TOKEN17 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_module867);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN17);

              pushFollow(FOLLOW_parameters_in_module871);
              p = parameters();

              state._fsp--;

              stream_parameters.add(p.getTree());
              CLOSE_MODULE_TOKEN18 = (Token) match(input, CLOSE_MODULE_TOKEN,
                  FOLLOW_CLOSE_MODULE_TOKEN_in_module873);
              stream_CLOSE_MODULE_TOKEN.add(CLOSE_MODULE_TOKEN18);

            }

          }
            break;

        }

        // handle the import
        String className = "";

        /*
         * we are defensive here because error recovery might let the
         * recognition exception past.
         */
        if (c != null) className = c.tree.getText();

        boolean importContents = true;
        if (i != null) // looking like a hack in the ANTLR, time to upgrade?
          importContents = Boolean.parseBoolean((i != null ? input.toString(
              i.start, i.stop) : null).toLowerCase());

        LOGGER.debug("Attempting import of module " + className
            + " and contents (" + importContents + ").. IS THIS WORKING??");

        try
        {
          moduleNode = _parser.getImportDelegate().importModuleInto(
              ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree,
              className, importContents);
          if (p != null)
            ASTSupport.setParameters(p != null ? (CommonTree) p.tree : null,
                ASTSupport.getFirstDescendantWithType(moduleNode, PARAMETERS));

          /*
           * try to grab the proper bounds for this node
           */
          // ((DetailedCommonTree) moduleNode)
          // .setStartOffset(((CommonToken) OPEN_MODULE_TOKEN13)
          // .getStartIndex());
          //
          // if (CLOSE_MODULE_TOKEN18 != null)
          // ((DetailedCommonTree) moduleNode)
          // .setEndOffset(((CommonToken) CLOSE_MODULE_TOKEN18)
          // .getStopIndex());
          // else
          // ((DetailedCommonTree) moduleNode)
          // .setEndOffset(((CommonToken) SHORT_CLOSE_TOKEN16)
          // .getStopIndex());
        }
        catch (Exception e)
        {
          LOGGER.error(e);
          if (e instanceof CommonTreeException)
          {
            // mark the exception so that we know what node it occured on
            ((CommonTreeException) e).setStartNode(c.tree);
            reportException(e);
          }
          else
            reportException(new CompilationError("Failed to import module "
                + className + " because of " + e.getClass().getSimpleName(),
                c.tree, e));
        }

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 342:18: -> ^()
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:343:18:
          // ^()
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(moduleNode, root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "module"

  public static class extensions_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "extensions"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:347:1:
  // extensions : e= OPEN_EXTENSIONS_TOKEN ( extension )* CLOSE_EXTENSIONS_TOKEN
  // -> ^( EXTENSIONS[$e] ( extension )* ) ;
  public final JACTRParser.extensions_return extensions()
      throws RecognitionException
  {
    JACTRParser.extensions_return retval = new JACTRParser.extensions_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token e = null;
    Token CLOSE_EXTENSIONS_TOKEN20 = null;
    JACTRParser.extension_return extension19 = null;

    RewriteRuleTokenStream stream_OPEN_EXTENSIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_EXTENSIONS_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_EXTENSIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_EXTENSIONS_TOKEN");
    RewriteRuleSubtreeStream stream_extension = new RewriteRuleSubtreeStream(
        adaptor, "rule extension");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:348:2:
      // (e= OPEN_EXTENSIONS_TOKEN ( extension )* CLOSE_EXTENSIONS_TOKEN -> ^(
      // EXTENSIONS[$e] ( extension )* ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:348:4:
      // e= OPEN_EXTENSIONS_TOKEN ( extension )* CLOSE_EXTENSIONS_TOKEN
      {
        e = (Token) match(input, OPEN_EXTENSIONS_TOKEN,
            FOLLOW_OPEN_EXTENSIONS_TOKEN_in_extensions965);
        stream_OPEN_EXTENSIONS_TOKEN.add(e);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:349:10:
        // ( extension )*
        loop10: do
        {
          int alt10 = 2;
          int LA10_0 = input.LA(1);

          if (LA10_0 == OPEN_EXTENSION_TOKEN) alt10 = 1;

          switch (alt10)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:349:10:
            // extension
            {
              pushFollow(FOLLOW_extension_in_extensions976);
              extension19 = extension();

              state._fsp--;

              stream_extension.add(extension19.getTree());

            }
              break;

            default:
              break loop10;
          }
        }
        while (true);

        CLOSE_EXTENSIONS_TOKEN20 = (Token) match(input, CLOSE_EXTENSIONS_TOKEN,
            FOLLOW_CLOSE_EXTENSIONS_TOKEN_in_extensions988);
        stream_CLOSE_EXTENSIONS_TOKEN.add(CLOSE_EXTENSIONS_TOKEN20);

        // AST REWRITE
        // elements: extension
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 351:10: -> ^( EXTENSIONS[$e] ( extension )* )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:352:10:
          // ^( EXTENSIONS[$e] ( extension )* )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(EXTENSIONS, e), root_1);

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:352:27:
            // ( extension )*
            while (stream_extension.hasNext())
              adaptor.addChild(root_1, stream_extension.nextTree());
            stream_extension.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "extensions"

  public static class extension_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "extension"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:354:1:
  // extension : e= OPEN_EXTENSION_TOKEN c= classSpec ( ( SHORT_CLOSE_TOKEN ) |
  // ( LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN ) ) -> ^() ;
  public final JACTRParser.extension_return extension()
      throws RecognitionException
  {
    JACTRParser.extension_return retval = new JACTRParser.extension_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token e = null;
    Token SHORT_CLOSE_TOKEN21 = null;
    Token LONG_CLOSE_TOKEN22 = null;
    Token CLOSE_EXTENSION_TOKEN23 = null;
    JACTRParser.classSpec_return c = null;

    JACTRParser.parameters_return p = null;

    RewriteRuleTokenStream stream_OPEN_EXTENSION_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_EXTENSION_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_EXTENSION_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_EXTENSION_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");
    RewriteRuleSubtreeStream stream_classSpec = new RewriteRuleSubtreeStream(
        adaptor, "rule classSpec");

    CommonTree extNode = null;
    adaptor.create(PARAMETERS, "parameters");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:358:2:
      // (e= OPEN_EXTENSION_TOKEN c= classSpec ( ( SHORT_CLOSE_TOKEN ) | (
      // LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN ) ) -> ^() )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:358:4:
      // e= OPEN_EXTENSION_TOKEN c= classSpec ( ( SHORT_CLOSE_TOKEN ) | (
      // LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN ) )
      {
        e = (Token) match(input, OPEN_EXTENSION_TOKEN,
            FOLLOW_OPEN_EXTENSION_TOKEN_in_extension1030);
        stream_OPEN_EXTENSION_TOKEN.add(e);

        pushFollow(FOLLOW_classSpec_in_extension1034);
        c = classSpec();

        state._fsp--;

        stream_classSpec.add(c.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:359:17:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
        // CLOSE_EXTENSION_TOKEN ) )
        int alt11 = 2;
        int LA11_0 = input.LA(1);

        if (LA11_0 == SHORT_CLOSE_TOKEN)
          alt11 = 1;
        else if (LA11_0 == LONG_CLOSE_TOKEN)
          alt11 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 11, 0, input);

          throw nvae;
        }
        switch (alt11)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:359:18:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:359:18:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:359:19:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN21 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_extension1054);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN21);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:360:18:
          // ( LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:360:18:
            // ( LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:360:19:
            // LONG_CLOSE_TOKEN p= parameters CLOSE_EXTENSION_TOKEN
            {
              LONG_CLOSE_TOKEN22 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_extension1077);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN22);

              pushFollow(FOLLOW_parameters_in_extension1081);
              p = parameters();

              state._fsp--;

              stream_parameters.add(p.getTree());
              CLOSE_EXTENSION_TOKEN23 = (Token) match(input,
                  CLOSE_EXTENSION_TOKEN,
                  FOLLOW_CLOSE_EXTENSION_TOKEN_in_extension1083);
              stream_CLOSE_EXTENSION_TOKEN.add(CLOSE_EXTENSION_TOKEN23);

            }

          }
            break;

        }

        // handle the import
        String className = "";

        /*
         * we are defensive here because error recovery might let the
         * recognition exception past.
         */
        if (c != null) className = c.tree.getText();

        boolean importContents = true;

        LOGGER.debug("Attempting import of extension " + className
            + " and contents (" + importContents + ")");

        try
        {
          extNode = _parser.getImportDelegate().importExtensionInto(
              ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree,
              className, importContents);
          if (p != null)
            ASTSupport.setParameters(p != null ? (CommonTree) p.tree : null,
                ASTSupport.getFirstDescendantWithType(extNode, PARAMETERS));
        }
        catch (Exception ex)
        {
          LOGGER.error(ex);
          if (ex instanceof CommonTreeException)
          {
            // mark the exception so that we know what node it occured on
            ((CommonTreeException) ex).setStartNode(c.tree);
            reportException(ex);
          }
          else
            reportException(new CompilationError("Failed to import extension "
                + className + " because of " + ex.getClass().getSimpleName(),
                c.tree, ex));
        }

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 394:18: -> ^()
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:395:18:
          // ^()
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(extNode, root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "extension"

  public static class library_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "library"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:398:1:
  // library : declarativeMemory proceduralMemory -> ^( LIBRARY
  // declarativeMemory proceduralMemory ) ;
  public final JACTRParser.library_return library() throws RecognitionException
  {
    JACTRParser.library_return retval = new JACTRParser.library_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.declarativeMemory_return declarativeMemory24 = null;

    JACTRParser.proceduralMemory_return proceduralMemory25 = null;

    RewriteRuleSubtreeStream stream_proceduralMemory = new RewriteRuleSubtreeStream(
        adaptor, "rule proceduralMemory");
    RewriteRuleSubtreeStream stream_declarativeMemory = new RewriteRuleSubtreeStream(
        adaptor, "rule declarativeMemory");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:398:9:
      // ( declarativeMemory proceduralMemory -> ^( LIBRARY declarativeMemory
      // proceduralMemory ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:398:11:
      // declarativeMemory proceduralMemory
      {
        pushFollow(FOLLOW_declarativeMemory_in_library1170);
        declarativeMemory24 = declarativeMemory();

        state._fsp--;

        stream_declarativeMemory.add(declarativeMemory24.getTree());
        pushFollow(FOLLOW_proceduralMemory_in_library1172);
        proceduralMemory25 = proceduralMemory();

        state._fsp--;

        stream_proceduralMemory.add(proceduralMemory25.getTree());

        // AST REWRITE
        // elements: declarativeMemory, proceduralMemory
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 398:46: -> ^( LIBRARY declarativeMemory proceduralMemory )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:398:48:
          // ^( LIBRARY declarativeMemory proceduralMemory )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(LIBRARY, "LIBRARY"), root_1);

            adaptor.addChild(root_1, stream_declarativeMemory.nextTree());
            adaptor.addChild(root_1, stream_proceduralMemory.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "library"

  public static class declarativeMemory_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "declarativeMemory"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:401:1:
  // declarativeMemory : OPEN_DECLARATIVE_MEMORY_TOKEN (ct= chunkType | c= chunk
  // )* CLOSE_DECLARATIVE_MEMORY_TOKEN -> ^() ;
  public final JACTRParser.declarativeMemory_return declarativeMemory()
      throws RecognitionException
  {
    JACTRParser.declarativeMemory_return retval = new JACTRParser.declarativeMemory_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token OPEN_DECLARATIVE_MEMORY_TOKEN26 = null;
    Token CLOSE_DECLARATIVE_MEMORY_TOKEN27 = null;
    JACTRParser.chunkType_return ct = null;

    JACTRParser.chunk_return c = null;

    RewriteRuleTokenStream stream_CLOSE_DECLARATIVE_MEMORY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_DECLARATIVE_MEMORY_TOKEN");
    RewriteRuleTokenStream stream_OPEN_DECLARATIVE_MEMORY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_DECLARATIVE_MEMORY_TOKEN");
    RewriteRuleSubtreeStream stream_chunkType = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkType");
    RewriteRuleSubtreeStream stream_chunk = new RewriteRuleSubtreeStream(
        adaptor, "rule chunk");

    CommonTree decNode = ASTSupport.getFirstDescendantWithType(
        ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree,
        DECLARATIVE_MEMORY);
    /*
     * prepopulate the known chunktypes with the imported contents
     */
    Map<String, CommonTree> importedChunkTypes = ASTSupport.getMapOfTrees(
        decNode, CHUNK_TYPE);
    for (Map.Entry<String, CommonTree> chunkTypeNode : importedChunkTypes
        .entrySet())
      ((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap.put(
          chunkTypeNode.getKey(), ASTSupport.getFirstDescendantWithType(
              chunkTypeNode.getValue(), CHUNKS));

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:414:2:
      // ( OPEN_DECLARATIVE_MEMORY_TOKEN (ct= chunkType | c= chunk )*
      // CLOSE_DECLARATIVE_MEMORY_TOKEN -> ^() )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:414:4:
      // OPEN_DECLARATIVE_MEMORY_TOKEN (ct= chunkType | c= chunk )*
      // CLOSE_DECLARATIVE_MEMORY_TOKEN
      {
        OPEN_DECLARATIVE_MEMORY_TOKEN26 = (Token) match(input,
            OPEN_DECLARATIVE_MEMORY_TOKEN,
            FOLLOW_OPEN_DECLARATIVE_MEMORY_TOKEN_in_declarativeMemory1198);
        stream_OPEN_DECLARATIVE_MEMORY_TOKEN
            .add(OPEN_DECLARATIVE_MEMORY_TOKEN26);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:415:10:
        // (ct= chunkType | c= chunk )*
        loop12: do
        {
          int alt12 = 3;
          int LA12_0 = input.LA(1);

          if (LA12_0 == OPEN_CHUNK_TYPE_TOKEN)
            alt12 = 1;
          else if (LA12_0 == OPEN_CHUNK_TOKEN) alt12 = 2;

          switch (alt12)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:415:11:
            // ct= chunkType
            {
              pushFollow(FOLLOW_chunkType_in_declarativeMemory1212);
              ct = chunkType();

              state._fsp--;

              stream_chunkType.add(ct.getTree());

              /*
               * handle chunkType processing we need the name and its chunks
               * wrapper we can also check its parent if availabel
               */
              CommonTree chunkTypeNode = ct.tree;

              String chunkTypeName = null;
              chunkTypeName = ASTSupport
                  .getFirstDescendantWithType(ct.tree, NAME).getText()
                  .toLowerCase();
              CommonTree chunksWrapper = ASTSupport.getFirstDescendantWithType(
                  ct.tree, CHUNKS);

              LOGGER.debug("Indexing chunktype " + chunkTypeName
                  + " and chunksWrapper " + chunksWrapper);

              if (((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
                  .containsKey(chunkTypeName))
              {
                LOGGER.debug(String.format(
                    "%s already exists, using local version", chunkTypeName));

                // steal the chunks from the existing
                CommonTree existingChunkType = importedChunkTypes
                    .get(chunkTypeName);

                stealChildren(
                    chunksWrapper,
                    ((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
                        .get(chunkTypeName));
                reportException(new CompilationWarning(String.format(
                    "%s is already defined. Redefining with local version",
                    chunkTypeName), chunkTypeNode));
                // remove the existing one
                for (int i = 0; i < decNode.getChildCount(); i++)
                  if (decNode.getChild(i) == existingChunkType)
                  {
                    LOGGER.debug("Removed old version");
                    decNode.setChild(i, chunkTypeNode);
                    break;
                  }
              }
              else
                decNode.addChild(chunkTypeNode);

              ((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
                  .put(chunkTypeName, chunksWrapper);

              if (((ModelGlobals_scope) ModelGlobals_stack.peek()).temporaryChunkTypesMap
                  .containsKey(chunkTypeName))
              {
                LOGGER
                    .debug(chunkTypeName
                        + " describes a pseduo chunktype. stripping its children for new chunktype");
                CommonTree tmpCTNode = ((ModelGlobals_scope) ModelGlobals_stack
                    .peek()).temporaryChunkTypesMap.remove(chunkTypeName);
                // snag all the kids
                Collection<CommonTree> chunks = ASTSupport.getTrees(tmpCTNode,
                    CHUNK);
                for (CommonTree chunk : chunks)
                {
                  if (LOGGER.isDebugEnabled())
                    LOGGER.debug("inserting " + chunk.toStringTree()
                        + " from pseudo ct");
                  chunksWrapper.addChild(chunk);
                }
              }

            }
              break;
            case 2:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:467:12:
            // c= chunk
            {
              pushFollow(FOLLOW_chunk_in_declarativeMemory1240);
              c = chunk();

              state._fsp--;

              stream_chunk.add(c.getTree());

              ASTSupport.getFirstDescendantWithType(c.tree, NAME).getText();
              String parentName = ASTSupport
                  .getFirstDescendantWithType(c.tree, PARENT).getText()
                  .toLowerCase();
              CommonTree chunksWrapper = ((ModelGlobals_scope) ModelGlobals_stack
                  .peek()).chunksWrapperMap.get(parentName);
              if (chunksWrapper == null)
              {
                // we need to use a pseudo chunktype for now, if we read it, it
                // will be replaced above
                reportException(new CompilationError(parentName
                    + " is not a known chunktype",
                    ASTSupport.getFirstDescendantWithType(c.tree, PARENT)));
                // check the tmpChunkTypes
                CommonTree chunkType = ((ModelGlobals_scope) ModelGlobals_stack
                    .peek()).temporaryChunkTypesMap.get(parentName);
                if (chunkType == null)
                {
                  LOGGER.debug("Creating pseudo chunktype " + parentName);
                  chunkType = _support.createChunkTypeTree(parentName, null);
                  chunksWrapper = (CommonTree) chunkType
                      .getFirstChildWithType(CHUNKS);
                  ((ModelGlobals_scope) ModelGlobals_stack.peek()).temporaryChunkTypesMap
                      .put(parentName, chunkType);
                  ((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
                      .put(parentName, chunksWrapper);
                }
              }

              chunksWrapper.addChild(c.tree);

            }
              break;

            default:
              break loop12;
          }
        }
        while (true);

        CLOSE_DECLARATIVE_MEMORY_TOKEN27 = (Token) match(input,
            CLOSE_DECLARATIVE_MEMORY_TOKEN,
            FOLLOW_CLOSE_DECLARATIVE_MEMORY_TOKEN_in_declarativeMemory1268);
        stream_CLOSE_DECLARATIVE_MEMORY_TOKEN
            .add(CLOSE_DECLARATIVE_MEMORY_TOKEN27);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 497:3: -> ^()
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:497:6:
          // ^()
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(decNode, root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {

      // attempt error recovery so maintain structure integ
      reportError(re);
      recover(input, re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "declarativeMemory"

  public static class proceduralMemory_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "proceduralMemory"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:505:1:
  // proceduralMemory : OPEN_PROCEDURAL_MEMORY_TOKEN (p= production )*
  // CLOSE_PROCEDURAL_MEMORY_TOKEN -> ^() ;
  public final JACTRParser.proceduralMemory_return proceduralMemory()
      throws RecognitionException
  {
    JACTRParser.proceduralMemory_return retval = new JACTRParser.proceduralMemory_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token OPEN_PROCEDURAL_MEMORY_TOKEN28 = null;
    Token CLOSE_PROCEDURAL_MEMORY_TOKEN29 = null;
    JACTRParser.production_return p = null;

    RewriteRuleTokenStream stream_OPEN_PROCEDURAL_MEMORY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PROCEDURAL_MEMORY_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_PROCEDURAL_MEMORY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_PROCEDURAL_MEMORY_TOKEN");
    RewriteRuleSubtreeStream stream_production = new RewriteRuleSubtreeStream(
        adaptor, "rule production");

    CommonTree procNode = ASTSupport.getFirstDescendantWithType(
        ((ModelGlobals_scope) ModelGlobals_stack.peek()).modelTree,
        PROCEDURAL_MEMORY);

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:509:2:
      // ( OPEN_PROCEDURAL_MEMORY_TOKEN (p= production )*
      // CLOSE_PROCEDURAL_MEMORY_TOKEN -> ^() )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:509:4:
      // OPEN_PROCEDURAL_MEMORY_TOKEN (p= production )*
      // CLOSE_PROCEDURAL_MEMORY_TOKEN
      {
        OPEN_PROCEDURAL_MEMORY_TOKEN28 = (Token) match(input,
            OPEN_PROCEDURAL_MEMORY_TOKEN,
            FOLLOW_OPEN_PROCEDURAL_MEMORY_TOKEN_in_proceduralMemory1300);
        stream_OPEN_PROCEDURAL_MEMORY_TOKEN.add(OPEN_PROCEDURAL_MEMORY_TOKEN28);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:510:3:
        // (p= production )*
        loop13: do
        {
          int alt13 = 2;
          int LA13_0 = input.LA(1);

          if (LA13_0 == OPEN_PRODUCTION_TOKEN) alt13 = 1;

          switch (alt13)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:510:4:
            // p= production
            {
              pushFollow(FOLLOW_production_in_proceduralMemory1308);
              p = production();

              state._fsp--;

              stream_production.add(p.getTree());

              procNode.addChild(p.tree);

            }
              break;

            default:
              break loop13;
          }
        }
        while (true);

        CLOSE_PROCEDURAL_MEMORY_TOKEN29 = (Token) match(input,
            CLOSE_PROCEDURAL_MEMORY_TOKEN,
            FOLLOW_CLOSE_PROCEDURAL_MEMORY_TOKEN_in_proceduralMemory1322);
        stream_CLOSE_PROCEDURAL_MEMORY_TOKEN
            .add(CLOSE_PROCEDURAL_MEMORY_TOKEN29);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 516:3: -> ^()
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:516:6:
          // ^()
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(procNode, root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "proceduralMemory"

  public static class buffer_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "buffer"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:519:1:
  // buffer : start= OPEN_BUFFER_TOKEN n= name (c= chunkRef )? ( (
  // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN )
  // ) -> ^( BUFFER[$start,$n.text] name ) ;
  public final JACTRParser.buffer_return buffer() throws RecognitionException
  {
    JACTRParser.buffer_return retval = new JACTRParser.buffer_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN30 = null;
    Token LONG_CLOSE_TOKEN31 = null;
    Token CLOSE_BUFFER_TOKEN32 = null;
    JACTRParser.name_return n = null;

    JACTRParser.chunkRef_return c = null;

    JACTRParser.parameters_return p = null;

    RewriteRuleTokenStream stream_OPEN_BUFFER_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_BUFFER_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_BUFFER_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_BUFFER_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");

    CommonTree chunks = (CommonTree) adaptor.create(CHUNKS, "chunks");
    CommonTree param = (CommonTree) adaptor.create(PARAMETERS, "parameters");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:523:3:
      // (start= OPEN_BUFFER_TOKEN n= name (c= chunkRef )? ( ( SHORT_CLOSE_TOKEN
      // ) | ( LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN ) ) -> ^(
      // BUFFER[$start,$n.text] name ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:523:5:
      // start= OPEN_BUFFER_TOKEN n= name (c= chunkRef )? ( ( SHORT_CLOSE_TOKEN
      // ) | ( LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN ) )
      {
        start = (Token) match(input, OPEN_BUFFER_TOKEN,
            FOLLOW_OPEN_BUFFER_TOKEN_in_buffer1346);
        stream_OPEN_BUFFER_TOKEN.add(start);

        pushFollow(FOLLOW_name_in_buffer1350);
        n = name();

        state._fsp--;

        stream_name.add(n.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:523:36:
        // (c= chunkRef )?
        int alt14 = 2;
        int LA14_0 = input.LA(1);

        if (LA14_0 == CHUNK_ATTR_TOKEN) alt14 = 1;
        switch (alt14)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:523:37:
          // c= chunkRef
          {
            pushFollow(FOLLOW_chunkRef_in_buffer1355);
            c = chunkRef();

            state._fsp--;

            stream_chunkRef.add(c.getTree());

          }
            break;

        }

        if (c != null) chunks.addChild(c.tree);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:528:17:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN p= parameters
        // CLOSE_BUFFER_TOKEN ) )
        int alt15 = 2;
        int LA15_0 = input.LA(1);

        if (LA15_0 == SHORT_CLOSE_TOKEN)
          alt15 = 1;
        else if (LA15_0 == LONG_CLOSE_TOKEN)
          alt15 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 15, 0, input);

          throw nvae;
        }
        switch (alt15)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:528:18:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:528:18:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:528:19:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN30 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_buffer1395);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN30);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:529:18:
          // ( LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:529:18:
            // ( LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:529:19:
            // LONG_CLOSE_TOKEN p= parameters CLOSE_BUFFER_TOKEN
            {
              LONG_CLOSE_TOKEN31 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_buffer1419);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN31);

              pushFollow(FOLLOW_parameters_in_buffer1423);
              p = parameters();

              state._fsp--;

              stream_parameters.add(p.getTree());
              CLOSE_BUFFER_TOKEN32 = (Token) match(input, CLOSE_BUFFER_TOKEN,
                  FOLLOW_CLOSE_BUFFER_TOKEN_in_buffer1425);
              stream_CLOSE_BUFFER_TOKEN.add(CLOSE_BUFFER_TOKEN32);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: name
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 530:18: -> ^( BUFFER[$start,$n.text] name )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:531:18:
          // ^( BUFFER[$start,$n.text] name )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(BUFFER, start,
                        n != null ? input.toString(n.start, n.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_name.nextTree());
            adaptor.addChild(root_1, chunks);
            adaptor.addChild(root_1, p == null ? param : p.tree);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "buffer"

  public static class chunkType_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "chunkType"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:533:1:
  // chunkType : c= OPEN_CHUNK_TYPE_TOKEN n= name (p= parents )? ( (
  // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
  // CLOSE_CHUNK_TYPE_TOKEN ) ) -> ^( CHUNK_TYPE[$c] name ) ;
  public final JACTRParser.chunkType_return chunkType()
      throws RecognitionException
  {
    JACTRParser.chunkType_return retval = new JACTRParser.chunkType_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token c = null;
    Token SHORT_CLOSE_TOKEN33 = null;
    Token LONG_CLOSE_TOKEN34 = null;
    Token CLOSE_CHUNK_TYPE_TOKEN35 = null;
    JACTRParser.name_return n = null;

    JACTRParser.parents_return p = null;

    JACTRParser.slots_return s = null;

    JACTRParser.parameters_return para = null;

    RewriteRuleTokenStream stream_CLOSE_CHUNK_TYPE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_CHUNK_TYPE_TOKEN");
    RewriteRuleTokenStream stream_OPEN_CHUNK_TYPE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_CHUNK_TYPE_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_parents = new RewriteRuleSubtreeStream(
        adaptor, "rule parents");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");

    CommonTree chunksNode = (CommonTree) adaptor.create(CHUNKS, "chunks");
    CommonTree slotsNode = (CommonTree) adaptor.create(SLOTS, "slots");
    CommonTree paramNode = (CommonTree) adaptor
        .create(PARAMETERS, "parameters");
    CommonTree parentsNode = (CommonTree) adaptor.create(PARENTS, "parents");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:540:2:
      // (c= OPEN_CHUNK_TYPE_TOKEN n= name (p= parents )? ( ( SHORT_CLOSE_TOKEN
      // ) | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
      // CLOSE_CHUNK_TYPE_TOKEN ) ) -> ^( CHUNK_TYPE[$c] name ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:540:4:
      // c= OPEN_CHUNK_TYPE_TOKEN n= name (p= parents )? ( ( SHORT_CLOSE_TOKEN )
      // | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
      // CLOSE_CHUNK_TYPE_TOKEN ) )
      {
        c = (Token) match(input, OPEN_CHUNK_TYPE_TOKEN,
            FOLLOW_OPEN_CHUNK_TYPE_TOKEN_in_chunkType1489);
        stream_OPEN_CHUNK_TYPE_TOKEN.add(c);

        pushFollow(FOLLOW_name_in_chunkType1493);
        n = name();

        state._fsp--;

        stream_name.add(n.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:540:35:
        // (p= parents )?
        int alt16 = 2;
        int LA16_0 = input.LA(1);

        if (LA16_0 == PARENT_ATTR_TOKEN) alt16 = 1;
        switch (alt16)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:540:36:
          // p= parents
          {
            pushFollow(FOLLOW_parents_in_chunkType1498);
            p = parents();

            state._fsp--;

            stream_parents.add(p.getTree());

          }
            break;

        }

        LOGGER.debug("Got chunktype " + n.tree.getText());
        // check the parent
        if (p != null) for (int i = 0; i < p.tree.getChildCount(); i++)
        {
          String parentName = p.tree.getChild(i).getText();
          LOGGER.debug("seeing if we know about parent name " + parentName);
          if (!((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
              .containsKey(parentName))
            reportException(new CompilationError(parentName
                + " is not a defined chunk-type", p.tree));
        }

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:555:12:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para=
        // parameters )? CLOSE_CHUNK_TYPE_TOKEN ) )
        int alt19 = 2;
        int LA19_0 = input.LA(1);

        if (LA19_0 == SHORT_CLOSE_TOKEN)
          alt19 = 1;
        else if (LA19_0 == LONG_CLOSE_TOKEN)
          alt19 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 19, 0, input);

          throw nvae;
        }
        switch (alt19)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:555:13:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:555:13:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:555:14:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN33 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_chunkType1518);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN33);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:556:13:
          // ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
          // CLOSE_CHUNK_TYPE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:556:13:
            // ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
            // CLOSE_CHUNK_TYPE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:556:14:
            // LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
            // CLOSE_CHUNK_TYPE_TOKEN
            {
              LONG_CLOSE_TOKEN34 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_chunkType1536);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN34);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:557:14:
              // (s= slots )?
              int alt17 = 2;
              int LA17_0 = input.LA(1);

              if (LA17_0 == OPEN_SLOT_TOKEN) alt17 = 1;
              switch (alt17)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:557:15:
                // s= slots
                {
                  pushFollow(FOLLOW_slots_in_chunkType1555);
                  s = slots();

                  state._fsp--;

                  stream_slots.add(s.getTree());

                }
                  break;

              }

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:558:14:
              // (para= parameters )?
              int alt18 = 2;
              int LA18_0 = input.LA(1);

              if (LA18_0 == OPEN_PARAMETERS_TOKEN) alt18 = 1;
              switch (alt18)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:558:15:
                // para= parameters
                {
                  pushFollow(FOLLOW_parameters_in_chunkType1576);
                  para = parameters();

                  state._fsp--;

                  stream_parameters.add(para.getTree());

                }
                  break;

              }

              CLOSE_CHUNK_TYPE_TOKEN35 = (Token) match(input,
                  CLOSE_CHUNK_TYPE_TOKEN,
                  FOLLOW_CLOSE_CHUNK_TYPE_TOKEN_in_chunkType1593);
              stream_CLOSE_CHUNK_TYPE_TOKEN.add(CLOSE_CHUNK_TYPE_TOKEN35);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: name
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 560:13: -> ^( CHUNK_TYPE[$c] name )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:561:13:
          // ^( CHUNK_TYPE[$c] name )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(CHUNK_TYPE, c), root_1);

            adaptor.addChild(root_1, stream_name.nextTree());
            adaptor.addChild(root_1, p == null ? parentsNode : p.tree);
            adaptor.addChild(root_1, s == null ? slotsNode : s.tree);
            adaptor.addChild(root_1, chunksNode);
            adaptor.addChild(root_1, para == null ? paramNode : para.tree);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "chunkType"

  public static class chunk_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "chunk"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:564:1:
  // chunk : start= OPEN_CHUNK_TOKEN ( (n= name type ) | ( type n= name ) ) ( (
  // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
  // CLOSE_CHUNK_TOKEN ) ) -> ^( CHUNK[$start,$n.text] name type ) ;
  public final JACTRParser.chunk_return chunk() throws RecognitionException
  {
    JACTRParser.chunk_return retval = new JACTRParser.chunk_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN38 = null;
    Token LONG_CLOSE_TOKEN39 = null;
    Token CLOSE_CHUNK_TOKEN40 = null;
    JACTRParser.name_return n = null;

    JACTRParser.slots_return s = null;

    JACTRParser.parameters_return para = null;

    JACTRParser.type_return type36 = null;

    JACTRParser.type_return type37 = null;

    RewriteRuleTokenStream stream_CLOSE_CHUNK_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_CHUNK_TOKEN");
    RewriteRuleTokenStream stream_OPEN_CHUNK_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_CHUNK_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");
    RewriteRuleSubtreeStream stream_type = new RewriteRuleSubtreeStream(
        adaptor, "rule type");

    CommonTree slotsNode = (CommonTree) adaptor.create(SLOTS, "slots");
    CommonTree paramNode = (CommonTree) adaptor
        .create(PARAMETERS, "parameters");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:4:
      // (start= OPEN_CHUNK_TOKEN ( (n= name type ) | ( type n= name ) ) ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters
      // )? CLOSE_CHUNK_TOKEN ) ) -> ^( CHUNK[$start,$n.text] name type ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:6:
      // start= OPEN_CHUNK_TOKEN ( (n= name type ) | ( type n= name ) ) ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters
      // )? CLOSE_CHUNK_TOKEN ) )
      {
        start = (Token) match(input, OPEN_CHUNK_TOKEN,
            FOLLOW_OPEN_CHUNK_TOKEN_in_chunk1664);
        stream_OPEN_CHUNK_TOKEN.add(start);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:29:
        // ( (n= name type ) | ( type n= name ) )
        int alt20 = 2;
        int LA20_0 = input.LA(1);

        if (LA20_0 == NAME_ATTR_TOKEN)
          alt20 = 1;
        else if (LA20_0 == TYPE_ATTR_TOKEN)
          alt20 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 20, 0, input);

          throw nvae;
        }
        switch (alt20)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:30:
          // (n= name type )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:30:
            // (n= name type )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:31:
            // n= name type
            {
              pushFollow(FOLLOW_name_in_chunk1670);
              n = name();

              state._fsp--;

              stream_name.add(n.getTree());
              pushFollow(FOLLOW_type_in_chunk1672);
              type36 = type();

              state._fsp--;

              stream_type.add(type36.getTree());

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:46:
          // ( type n= name )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:46:
            // ( type n= name )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:568:47:
            // type n= name
            {
              pushFollow(FOLLOW_type_in_chunk1678);
              type37 = type();

              state._fsp--;

              stream_type.add(type37.getTree());
              pushFollow(FOLLOW_name_in_chunk1682);
              n = name();

              state._fsp--;

              stream_name.add(n.getTree());

            }

          }
            break;

        }

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:569:4:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN (s= slots )? (para=
        // parameters )? CLOSE_CHUNK_TOKEN ) )
        int alt23 = 2;
        int LA23_0 = input.LA(1);

        if (LA23_0 == SHORT_CLOSE_TOKEN)
          alt23 = 1;
        else if (LA23_0 == LONG_CLOSE_TOKEN)
          alt23 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 23, 0, input);

          throw nvae;
        }
        switch (alt23)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:569:5:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:569:5:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:569:6:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN38 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_chunk1692);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN38);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:5:
          // ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
          // CLOSE_CHUNK_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:5:
            // ( LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
            // CLOSE_CHUNK_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:6:
            // LONG_CLOSE_TOKEN (s= slots )? (para= parameters )?
            // CLOSE_CHUNK_TOKEN
            {
              LONG_CLOSE_TOKEN39 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_chunk1703);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN39);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:23:
              // (s= slots )?
              int alt21 = 2;
              int LA21_0 = input.LA(1);

              if (LA21_0 == OPEN_SLOT_TOKEN) alt21 = 1;
              switch (alt21)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:24:
                // s= slots
                {
                  pushFollow(FOLLOW_slots_in_chunk1708);
                  s = slots();

                  state._fsp--;

                  stream_slots.add(s.getTree());

                }
                  break;

              }

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:34:
              // (para= parameters )?
              int alt22 = 2;
              int LA22_0 = input.LA(1);

              if (LA22_0 == OPEN_PARAMETERS_TOKEN) alt22 = 1;
              switch (alt22)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:570:35:
                // para= parameters
                {
                  pushFollow(FOLLOW_parameters_in_chunk1715);
                  para = parameters();

                  state._fsp--;

                  stream_parameters.add(para.getTree());

                }
                  break;

              }

              CLOSE_CHUNK_TOKEN40 = (Token) match(input, CLOSE_CHUNK_TOKEN,
                  FOLLOW_CLOSE_CHUNK_TOKEN_in_chunk1723);
              stream_CLOSE_CHUNK_TOKEN.add(CLOSE_CHUNK_TOKEN40);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: type, name
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 572:4: -> ^( CHUNK[$start,$n.text] name type )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:573:13:
          // ^( CHUNK[$start,$n.text] name type )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(CHUNK, start,
                        n != null ? input.toString(n.start, n.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_name.nextTree());
            adaptor.addChild(root_1, stream_type.nextTree());
            adaptor.addChild(root_1, s == null ? slotsNode : s.tree);
            adaptor.addChild(root_1, para == null ? paramNode : para.tree);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "chunk"

  public static class production_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "production"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:577:1:
  // production : start= OPEN_PRODUCTION_TOKEN n= name LONG_CLOSE_TOKEN
  // conditions actions (param= parameters )? CLOSE_PRODUCTION_TOKEN -> ^(
  // PRODUCTION[$start,$n.text] name conditions actions ) ;
  public final JACTRParser.production_return production()
      throws RecognitionException
  {
    JACTRParser.production_return retval = new JACTRParser.production_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN41 = null;
    Token CLOSE_PRODUCTION_TOKEN44 = null;
    JACTRParser.name_return n = null;

    JACTRParser.parameters_return param = null;

    JACTRParser.conditions_return conditions42 = null;

    JACTRParser.actions_return actions43 = null;

    RewriteRuleTokenStream stream_CLOSE_PRODUCTION_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_PRODUCTION_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_OPEN_PRODUCTION_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PRODUCTION_TOKEN");
    RewriteRuleSubtreeStream stream_conditions = new RewriteRuleSubtreeStream(
        adaptor, "rule conditions");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");
    RewriteRuleSubtreeStream stream_parameters = new RewriteRuleSubtreeStream(
        adaptor, "rule parameters");
    RewriteRuleSubtreeStream stream_actions = new RewriteRuleSubtreeStream(
        adaptor, "rule actions");

    CommonTree paramsNode = (CommonTree) adaptor.create(PARAMETERS,
        "parameters");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:581:2:
      // (start= OPEN_PRODUCTION_TOKEN n= name LONG_CLOSE_TOKEN conditions
      // actions (param= parameters )? CLOSE_PRODUCTION_TOKEN -> ^(
      // PRODUCTION[$start,$n.text] name conditions actions ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:581:4:
      // start= OPEN_PRODUCTION_TOKEN n= name LONG_CLOSE_TOKEN conditions
      // actions (param= parameters )? CLOSE_PRODUCTION_TOKEN
      {
        start = (Token) match(input, OPEN_PRODUCTION_TOKEN,
            FOLLOW_OPEN_PRODUCTION_TOKEN_in_production1785);
        stream_OPEN_PRODUCTION_TOKEN.add(start);

        pushFollow(FOLLOW_name_in_production1789);
        n = name();

        state._fsp--;

        stream_name.add(n.getTree());
        LONG_CLOSE_TOKEN41 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_production1791);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN41);

        pushFollow(FOLLOW_conditions_in_production1795);
        conditions42 = conditions();

        state._fsp--;

        stream_conditions.add(conditions42.getTree());
        pushFollow(FOLLOW_actions_in_production1799);
        actions43 = actions();

        state._fsp--;

        stream_actions.add(actions43.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:584:3:
        // (param= parameters )?
        int alt24 = 2;
        int LA24_0 = input.LA(1);

        if (LA24_0 == OPEN_PARAMETERS_TOKEN) alt24 = 1;
        switch (alt24)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:584:4:
          // param= parameters
          {
            pushFollow(FOLLOW_parameters_in_production1806);
            param = parameters();

            state._fsp--;

            stream_parameters.add(param.getTree());

          }
            break;

        }

        CLOSE_PRODUCTION_TOKEN44 = (Token) match(input, CLOSE_PRODUCTION_TOKEN,
            FOLLOW_CLOSE_PRODUCTION_TOKEN_in_production1812);
        stream_CLOSE_PRODUCTION_TOKEN.add(CLOSE_PRODUCTION_TOKEN44);

        // AST REWRITE
        // elements: actions, conditions, name
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 586:3: -> ^( PRODUCTION[$start,$n.text] name conditions actions )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:587:3:
          // ^( PRODUCTION[$start,$n.text] name conditions actions )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(PRODUCTION, start,
                        n != null ? input.toString(n.start, n.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_name.nextTree());
            adaptor.addChild(root_1, stream_conditions.nextTree());
            adaptor.addChild(root_1, stream_actions.nextTree());
            adaptor.addChild(root_1, param == null ? paramsNode : param.tree);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "production"

  public static class conditions_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "conditions"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:594:1:
  // conditions : start= OPEN_CONDITIONS_TOKEN (l= lhs )* CLOSE_CONDITIONS_TOKEN
  // ->;
  public final JACTRParser.conditions_return conditions()
      throws RecognitionException
  {
    JACTRParser.conditions_return retval = new JACTRParser.conditions_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token CLOSE_CONDITIONS_TOKEN45 = null;
    JACTRParser.lhs_return l = null;

    RewriteRuleTokenStream stream_CLOSE_CONDITIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_CONDITIONS_TOKEN");
    RewriteRuleTokenStream stream_OPEN_CONDITIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_CONDITIONS_TOKEN");
    RewriteRuleSubtreeStream stream_lhs = new RewriteRuleSubtreeStream(adaptor,
        "rule lhs");

    CommonTree conditionsNode = null;
    Collection<CommonTree> bufferSlots = new ArrayList<CommonTree>(2);

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:599:2:
      // (start= OPEN_CONDITIONS_TOKEN (l= lhs )* CLOSE_CONDITIONS_TOKEN ->)
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:599:5:
      // start= OPEN_CONDITIONS_TOKEN (l= lhs )* CLOSE_CONDITIONS_TOKEN
      {
        start = (Token) match(input, OPEN_CONDITIONS_TOKEN,
            FOLLOW_OPEN_CONDITIONS_TOKEN_in_conditions1853);
        stream_OPEN_CONDITIONS_TOKEN.add(start);

        conditionsNode = (CommonTree) adaptor.create(CONDITIONS, start, "lhs");

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:603:5:
        // (l= lhs )*
        loop25: do
        {
          int alt25 = 2;
          int LA25_0 = input.LA(1);

          if (LA25_0 == OPEN_MATCH_TOKEN || LA25_0 == OPEN_QUERY_TOKEN
              || LA25_0 == OPEN_SCRIPT_COND_TOKEN
              || LA25_0 == OPEN_PROXY_COND_TOKEN) alt25 = 1;

          switch (alt25)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:603:6:
            // l= lhs
            {
              pushFollow(FOLLOW_lhs_in_conditions1869);
              l = lhs();

              state._fsp--;

              stream_lhs.add(l.getTree());

              // no matter what, add this as a child
              conditionsNode.addChild(l != null ? (CommonTree) l.tree : null);

              if ((l != null ? (CommonTree) l.tree : null).getType() == MATCH_CONDITION)
              {
                // we may need to expand.. lets check the slots
                CommonTree slotsContainer = ASTSupport
                    .getFirstDescendantWithType(l != null ? (CommonTree) l.tree
                        : null, SLOTS);

                // no slots?
                if (slotsContainer == null) continue;

                // iterate through the slots..
                for (int i = 0; i < slotsContainer.getChildCount(); i++)
                {
                  CommonTree slot = (CommonTree) slotsContainer.getChild(i);
                  // check the name
                  String name = slot.getChild(0).getText();
                  if (name.startsWith(":"))
                  {
                    if (!name.equalsIgnoreCase(":isa"))
                    {
                      // store and remove
                      slotsContainer.deleteChild(i);
                      bufferSlots.add(slot);
                      // decrement i to skip back since deleting while reduce
                      // the childcount
                      i--;
                    }
                    // while we're at it, lets strip the ':'
                    name = name.substring(1);
                    ((CommonTree) slot.getChild(0)).getToken().setText(name);
                  }
                }

                // we need to create a query, we dont bother merging them if one
                // already exists
                if (bufferSlots.size() != 0)
                {
                  String bufferName = (l != null ? (CommonTree) l.tree : null)
                      .getChild(0).getText();
                  CommonTree query = _support.createQueryTree(bufferName);
                  slotsContainer = (CommonTree) query.getChild(1);
                  for (CommonTree slot : bufferSlots)
                    slotsContainer.addChild(slot);

                  // and add the query
                  conditionsNode.addChild(query);

                  bufferSlots.clear();
                }
              }

            }
              break;

            default:
              break loop25;
          }
        }
        while (true);

        CLOSE_CONDITIONS_TOKEN45 = (Token) match(input, CLOSE_CONDITIONS_TOKEN,
            FOLLOW_CLOSE_CONDITIONS_TOKEN_in_conditions1884);
        stream_CLOSE_CONDITIONS_TOKEN.add(CLOSE_CONDITIONS_TOKEN45);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 654:5: ->
        {
          adaptor.addChild(root_0, conditionsNode);

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "conditions"

  public static class lhs_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "lhs"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:1:
  // lhs : ( match | query | scriptCond | proxyCond ) ;
  public final JACTRParser.lhs_return lhs() throws RecognitionException
  {
    JACTRParser.lhs_return retval = new JACTRParser.lhs_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.match_return match46 = null;

    JACTRParser.query_return query47 = null;

    JACTRParser.scriptCond_return scriptCond48 = null;

    JACTRParser.proxyCond_return proxyCond49 = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:6:
      // ( ( match | query | scriptCond | proxyCond ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:9:
      // ( match | query | scriptCond | proxyCond )
      {
        root_0 = (CommonTree) adaptor.nil();

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:9:
        // ( match | query | scriptCond | proxyCond )
        int alt26 = 4;
        switch (input.LA(1))
        {
          case OPEN_MATCH_TOKEN:
          {
            alt26 = 1;
          }
            break;
          case OPEN_QUERY_TOKEN:
          {
            alt26 = 2;
          }
            break;
          case OPEN_SCRIPT_COND_TOKEN:
          {
            alt26 = 3;
          }
            break;
          case OPEN_PROXY_COND_TOKEN:
          {
            alt26 = 4;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 26, 0,
                input);

            throw nvae;
        }

        switch (alt26)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:10:
          // match
          {
            pushFollow(FOLLOW_match_in_lhs1907);
            match46 = match();

            state._fsp--;

            adaptor.addChild(root_0, match46.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:18:
          // query
          {
            pushFollow(FOLLOW_query_in_lhs1911);
            query47 = query();

            state._fsp--;

            adaptor.addChild(root_0, query47.getTree());

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:26:
          // scriptCond
          {
            pushFollow(FOLLOW_scriptCond_in_lhs1915);
            scriptCond48 = scriptCond();

            state._fsp--;

            adaptor.addChild(root_0, scriptCond48.getTree());

          }
            break;
          case 4:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:656:39:
          // proxyCond
          {
            pushFollow(FOLLOW_proxyCond_in_lhs1919);
            proxyCond49 = proxyCond();

            state._fsp--;

            adaptor.addChild(root_0, proxyCond49.getTree());

          }
            break;

        }

      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "lhs"

  public static class actions_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "actions"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:658:1:
  // actions : start= OPEN_ACTIONS_TOKEN ( rhs )* CLOSE_ACTIONS_TOKEN -> ^(
  // ACTIONS[$start,\"rhs\"] ( rhs )* ) ;
  public final JACTRParser.actions_return actions() throws RecognitionException
  {
    JACTRParser.actions_return retval = new JACTRParser.actions_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token CLOSE_ACTIONS_TOKEN51 = null;
    JACTRParser.rhs_return rhs50 = null;

    RewriteRuleTokenStream stream_OPEN_ACTIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_ACTIONS_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_ACTIONS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_ACTIONS_TOKEN");
    RewriteRuleSubtreeStream stream_rhs = new RewriteRuleSubtreeStream(adaptor,
        "rule rhs");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:658:9:
      // (start= OPEN_ACTIONS_TOKEN ( rhs )* CLOSE_ACTIONS_TOKEN -> ^(
      // ACTIONS[$start,\"rhs\"] ( rhs )* ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:658:11:
      // start= OPEN_ACTIONS_TOKEN ( rhs )* CLOSE_ACTIONS_TOKEN
      {
        start = (Token) match(input, OPEN_ACTIONS_TOKEN,
            FOLLOW_OPEN_ACTIONS_TOKEN_in_actions1935);
        stream_OPEN_ACTIONS_TOKEN.add(start);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:659:2:
        // ( rhs )*
        loop27: do
        {
          int alt27 = 2;
          int LA27_0 = input.LA(1);

          if (LA27_0 == OPEN_SCRIPT_ACT_TOKEN || LA27_0 == OPEN_PROXY_ACT_TOKEN
              || LA27_0 == OPEN_OUTPUT_TOKEN || LA27_0 == OPEN_REMOVE_TOKEN
              || LA27_0 == OPEN_MODIFY_TOKEN || LA27_0 == OPEN_ADD_TOKEN
              || LA27_0 == OPEN_SET_TOKEN || LA27_0 == STOP_TOKEN) alt27 = 1;

          switch (alt27)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:659:2:
            // rhs
            {
              pushFollow(FOLLOW_rhs_in_actions1938);
              rhs50 = rhs();

              state._fsp--;

              stream_rhs.add(rhs50.getTree());

            }
              break;

            default:
              break loop27;
          }
        }
        while (true);

        CLOSE_ACTIONS_TOKEN51 = (Token) match(input, CLOSE_ACTIONS_TOKEN,
            FOLLOW_CLOSE_ACTIONS_TOKEN_in_actions1942);
        stream_CLOSE_ACTIONS_TOKEN.add(CLOSE_ACTIONS_TOKEN51);

        // AST REWRITE
        // elements: rhs
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 661:2: -> ^( ACTIONS[$start,\"rhs\"] ( rhs )* )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:662:2:
          // ^( ACTIONS[$start,\"rhs\"] ( rhs )* )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(ACTIONS, start, "rhs"), root_1);

            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:662:26:
            // ( rhs )*
            while (stream_rhs.hasNext())
              adaptor.addChild(root_1, stream_rhs.nextTree());
            stream_rhs.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "actions"

  public static class rhs_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "rhs"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:1:
  // rhs : ( add | set | modify | remove | scriptAct | proxyAct | output | stop
  // ) ;
  public final JACTRParser.rhs_return rhs() throws RecognitionException
  {
    JACTRParser.rhs_return retval = new JACTRParser.rhs_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.add_return add52 = null;

    JACTRParser.set_return set53 = null;

    JACTRParser.modify_return modify54 = null;

    JACTRParser.remove_return remove55 = null;

    JACTRParser.scriptAct_return scriptAct56 = null;

    JACTRParser.proxyAct_return proxyAct57 = null;

    JACTRParser.output_return output58 = null;

    JACTRParser.stop_return stop59 = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:6:
      // ( ( add | set | modify | remove | scriptAct | proxyAct | output | stop
      // ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:8:
      // ( add | set | modify | remove | scriptAct | proxyAct | output | stop )
      {
        root_0 = (CommonTree) adaptor.nil();

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:8:
        // ( add | set | modify | remove | scriptAct | proxyAct | output | stop
        // )
        int alt28 = 8;
        switch (input.LA(1))
        {
          case OPEN_ADD_TOKEN:
          {
            alt28 = 1;
          }
            break;
          case OPEN_SET_TOKEN:
          {
            alt28 = 2;
          }
            break;
          case OPEN_MODIFY_TOKEN:
          {
            alt28 = 3;
          }
            break;
          case OPEN_REMOVE_TOKEN:
          {
            alt28 = 4;
          }
            break;
          case OPEN_SCRIPT_ACT_TOKEN:
          {
            alt28 = 5;
          }
            break;
          case OPEN_PROXY_ACT_TOKEN:
          {
            alt28 = 6;
          }
            break;
          case OPEN_OUTPUT_TOKEN:
          {
            alt28 = 7;
          }
            break;
          case STOP_TOKEN:
          {
            alt28 = 8;
          }
            break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 28, 0,
                input);

            throw nvae;
        }

        switch (alt28)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:9:
          // add
          {
            pushFollow(FOLLOW_add_in_rhs1967);
            add52 = add();

            state._fsp--;

            adaptor.addChild(root_0, add52.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:13:
          // set
          {
            pushFollow(FOLLOW_set_in_rhs1969);
            set53 = set();

            state._fsp--;

            adaptor.addChild(root_0, set53.getTree());

          }
            break;
          case 3:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:17:
          // modify
          {
            pushFollow(FOLLOW_modify_in_rhs1971);
            modify54 = modify();

            state._fsp--;

            adaptor.addChild(root_0, modify54.getTree());

          }
            break;
          case 4:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:24:
          // remove
          {
            pushFollow(FOLLOW_remove_in_rhs1973);
            remove55 = remove();

            state._fsp--;

            adaptor.addChild(root_0, remove55.getTree());

          }
            break;
          case 5:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:31:
          // scriptAct
          {
            pushFollow(FOLLOW_scriptAct_in_rhs1975);
            scriptAct56 = scriptAct();

            state._fsp--;

            adaptor.addChild(root_0, scriptAct56.getTree());

          }
            break;
          case 6:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:41:
          // proxyAct
          {
            pushFollow(FOLLOW_proxyAct_in_rhs1977);
            proxyAct57 = proxyAct();

            state._fsp--;

            adaptor.addChild(root_0, proxyAct57.getTree());

          }
            break;
          case 7:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:50:
          // output
          {
            pushFollow(FOLLOW_output_in_rhs1979);
            output58 = output();

            state._fsp--;

            adaptor.addChild(root_0, output58.getTree());

          }
            break;
          case 8:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:664:57:
          // stop
          {
            pushFollow(FOLLOW_stop_in_rhs1981);
            stop59 = stop();

            state._fsp--;

            adaptor.addChild(root_0, stop59.getTree());

          }
            break;

        }

      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "rhs"

  public static class emptyMatch_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "emptyMatch"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:666:1:
  // emptyMatch : start= OPEN_MATCH_TOKEN b= bufferRef SHORT_CLOSE_TOKEN -> ^(
  // MATCH_CONDITION[$start,$b.text] bufferRef ) ;
  public final JACTRParser.emptyMatch_return emptyMatch()
      throws RecognitionException
  {
    JACTRParser.emptyMatch_return retval = new JACTRParser.emptyMatch_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN60 = null;
    JACTRParser.bufferRef_return b = null;

    RewriteRuleTokenStream stream_OPEN_MATCH_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MATCH_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:667:2:
      // (start= OPEN_MATCH_TOKEN b= bufferRef SHORT_CLOSE_TOKEN -> ^(
      // MATCH_CONDITION[$start,$b.text] bufferRef ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:667:5:
      // start= OPEN_MATCH_TOKEN b= bufferRef SHORT_CLOSE_TOKEN
      {
        start = (Token) match(input, OPEN_MATCH_TOKEN,
            FOLLOW_OPEN_MATCH_TOKEN_in_emptyMatch1995);
        stream_OPEN_MATCH_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_emptyMatch1999);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        SHORT_CLOSE_TOKEN60 = (Token) match(input, SHORT_CLOSE_TOKEN,
            FOLLOW_SHORT_CLOSE_TOKEN_in_emptyMatch2002);
        stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN60);

        // AST REWRITE
        // elements: bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 668:3: -> ^( MATCH_CONDITION[$start,$b.text] bufferRef )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:669:3:
          // ^( MATCH_CONDITION[$start,$b.text] bufferRef )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(MATCH_CONDITION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "emptyMatch"

  public static class matchShort_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "matchShort"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:671:1:
  // matchShort : start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
  // SHORT_CLOSE_TOKEN -> ^( MATCH_CONDITION[$start,$b.text] bufferRef ) ;
  public final JACTRParser.matchShort_return matchShort()
      throws RecognitionException
  {
    JACTRParser.matchShort_return retval = new JACTRParser.matchShort_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN61 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.isa_return t = null;

    JACTRParser.chunkRef_return c = null;

    RewriteRuleTokenStream stream_OPEN_MATCH_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MATCH_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_isa = new RewriteRuleSubtreeStream(adaptor,
        "rule isa");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:672:2:
      // (start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
      // SHORT_CLOSE_TOKEN -> ^( MATCH_CONDITION[$start,$b.text] bufferRef ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:672:5:
      // start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
      // SHORT_CLOSE_TOKEN
      {
        start = (Token) match(input, OPEN_MATCH_TOKEN,
            FOLLOW_OPEN_MATCH_TOKEN_in_matchShort2029);
        stream_OPEN_MATCH_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_matchShort2033);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:672:40:
        // (t= isa | c= chunkRef )
        int alt29 = 2;
        int LA29_0 = input.LA(1);

        if (LA29_0 == TYPE_ATTR_TOKEN)
          alt29 = 1;
        else if (LA29_0 == CHUNK_ATTR_TOKEN)
          alt29 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 29, 0, input);

          throw nvae;
        }
        switch (alt29)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:672:41:
          // t= isa
          {
            pushFollow(FOLLOW_isa_in_matchShort2038);
            t = isa();

            state._fsp--;

            stream_isa.add(t.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:672:49:
          // c= chunkRef
          {
            pushFollow(FOLLOW_chunkRef_in_matchShort2044);
            c = chunkRef();

            state._fsp--;

            stream_chunkRef.add(c.getTree());

          }
            break;

        }

        SHORT_CLOSE_TOKEN61 = (Token) match(input, SHORT_CLOSE_TOKEN,
            FOLLOW_SHORT_CLOSE_TOKEN_in_matchShort2047);
        stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN61);

        // AST REWRITE
        // elements: bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 673:3: -> ^( MATCH_CONDITION[$start,$b.text] bufferRef )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:674:3:
          // ^( MATCH_CONDITION[$start,$b.text] bufferRef )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(MATCH_CONDITION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            adaptor.addChild(root_1, t == null ? c.tree : t.tree);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "matchShort"

  public static class matchLong_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "matchLong"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:677:1:
  // matchLong : start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
  // LONG_CLOSE_TOKEN ( lslots )? CLOSE_MATCH_TOKEN -> ^(
  // MATCH_CONDITION[$start,$b.text] bufferRef ( lslots )? ) ;
  public final JACTRParser.matchLong_return matchLong()
      throws RecognitionException
  {
    JACTRParser.matchLong_return retval = new JACTRParser.matchLong_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN62 = null;
    Token CLOSE_MATCH_TOKEN64 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.isa_return t = null;

    JACTRParser.chunkRef_return c = null;

    JACTRParser.lslots_return lslots63 = null;

    RewriteRuleTokenStream stream_OPEN_MATCH_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MATCH_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_MATCH_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_MATCH_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_isa = new RewriteRuleSubtreeStream(adaptor,
        "rule isa");
    RewriteRuleSubtreeStream stream_lslots = new RewriteRuleSubtreeStream(
        adaptor, "rule lslots");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:678:2:
      // (start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
      // LONG_CLOSE_TOKEN ( lslots )? CLOSE_MATCH_TOKEN -> ^(
      // MATCH_CONDITION[$start,$b.text] bufferRef ( lslots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:678:5:
      // start= OPEN_MATCH_TOKEN b= bufferRef (t= isa | c= chunkRef )
      // LONG_CLOSE_TOKEN ( lslots )? CLOSE_MATCH_TOKEN
      {
        start = (Token) match(input, OPEN_MATCH_TOKEN,
            FOLLOW_OPEN_MATCH_TOKEN_in_matchLong2076);
        stream_OPEN_MATCH_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_matchLong2080);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:678:40:
        // (t= isa | c= chunkRef )
        int alt30 = 2;
        int LA30_0 = input.LA(1);

        if (LA30_0 == TYPE_ATTR_TOKEN)
          alt30 = 1;
        else if (LA30_0 == CHUNK_ATTR_TOKEN)
          alt30 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 30, 0, input);

          throw nvae;
        }
        switch (alt30)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:678:41:
          // t= isa
          {
            pushFollow(FOLLOW_isa_in_matchLong2085);
            t = isa();

            state._fsp--;

            stream_isa.add(t.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:678:49:
          // c= chunkRef
          {
            pushFollow(FOLLOW_chunkRef_in_matchLong2091);
            c = chunkRef();

            state._fsp--;

            stream_chunkRef.add(c.getTree());

          }
            break;

        }

        LONG_CLOSE_TOKEN62 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_matchLong2094);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN62);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:679:5:
        // ( lslots )?
        int alt31 = 2;
        int LA31_0 = input.LA(1);

        if (LA31_0 == OPEN_OR_TOKEN || LA31_0 == OPEN_AND_TOKEN
            || LA31_0 == OPEN_NOT_TOKEN || LA31_0 == OPEN_SLOT_TOKEN)
          alt31 = 1;
        switch (alt31)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:679:5:
          // lslots
          {
            pushFollow(FOLLOW_lslots_in_matchLong2100);
            lslots63 = lslots();

            state._fsp--;

            stream_lslots.add(lslots63.getTree());

          }
            break;

        }

        CLOSE_MATCH_TOKEN64 = (Token) match(input, CLOSE_MATCH_TOKEN,
            FOLLOW_CLOSE_MATCH_TOKEN_in_matchLong2106);
        stream_CLOSE_MATCH_TOKEN.add(CLOSE_MATCH_TOKEN64);

        // AST REWRITE
        // elements: bufferRef, lslots
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 681:3: -> ^( MATCH_CONDITION[$start,$b.text] bufferRef ( lslots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:682:3:
          // ^( MATCH_CONDITION[$start,$b.text] bufferRef ( lslots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(MATCH_CONDITION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            adaptor.addChild(root_1, t == null ? c.tree : t.tree);
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:682:75:
            // ( lslots )?
            if (stream_lslots.hasNext()) adaptor.addChild(root_1, stream_lslots.nextTree());
            stream_lslots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "matchLong"

  public static class match_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "match"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:684:1:
  // match : ( matchLong | matchShort | emptyMatch );
  public final JACTRParser.match_return match() throws RecognitionException
  {
    JACTRParser.match_return retval = new JACTRParser.match_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.matchLong_return matchLong65 = null;

    JACTRParser.matchShort_return matchShort66 = null;

    JACTRParser.emptyMatch_return emptyMatch67 = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:684:8:
      // ( matchLong | matchShort | emptyMatch )
      int alt32 = 3;
      alt32 = dfa32.predict(input);
      switch (alt32)
      {
        case 1:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:684:10:
        // matchLong
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_matchLong_in_match2135);
          matchLong65 = matchLong();

          state._fsp--;

          adaptor.addChild(root_0, matchLong65.getTree());

        }
          break;
        case 2:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:684:22:
        // matchShort
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_matchShort_in_match2139);
          matchShort66 = matchShort();

          state._fsp--;

          adaptor.addChild(root_0, matchShort66.getTree());

        }
          break;
        case 3:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:684:35:
        // emptyMatch
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_emptyMatch_in_match2143);
          emptyMatch67 = emptyMatch();

          state._fsp--;

          adaptor.addChild(root_0, emptyMatch67.getTree());

        }
          break;

      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "match"

  public static class query_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "query"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:686:1:
  // query : start= OPEN_QUERY_TOKEN b= bufferRef LONG_CLOSE_TOKEN slots
  // CLOSE_QUERY_TOKEN -> ^( QUERY_CONDITION[$start,$b.text] bufferRef slots ) ;
  public final JACTRParser.query_return query() throws RecognitionException
  {
    JACTRParser.query_return retval = new JACTRParser.query_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN68 = null;
    Token CLOSE_QUERY_TOKEN70 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.slots_return slots69 = null;

    RewriteRuleTokenStream stream_OPEN_QUERY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_QUERY_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_QUERY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_QUERY_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:686:8:
      // (start= OPEN_QUERY_TOKEN b= bufferRef LONG_CLOSE_TOKEN slots
      // CLOSE_QUERY_TOKEN -> ^( QUERY_CONDITION[$start,$b.text] bufferRef slots
      // ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:686:10:
      // start= OPEN_QUERY_TOKEN b= bufferRef LONG_CLOSE_TOKEN slots
      // CLOSE_QUERY_TOKEN
      {
        start = (Token) match(input, OPEN_QUERY_TOKEN,
            FOLLOW_OPEN_QUERY_TOKEN_in_query2159);
        stream_OPEN_QUERY_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_query2163);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        LONG_CLOSE_TOKEN68 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_query2165);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN68);

        pushFollow(FOLLOW_slots_in_query2170);
        slots69 = slots();

        state._fsp--;

        stream_slots.add(slots69.getTree());
        CLOSE_QUERY_TOKEN70 = (Token) match(input, CLOSE_QUERY_TOKEN,
            FOLLOW_CLOSE_QUERY_TOKEN_in_query2174);
        stream_CLOSE_QUERY_TOKEN.add(CLOSE_QUERY_TOKEN70);

        // AST REWRITE
        // elements: bufferRef, slots
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 689:3: -> ^( QUERY_CONDITION[$start,$b.text] bufferRef slots )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:690:3:
          // ^( QUERY_CONDITION[$start,$b.text] bufferRef slots )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(QUERY_CONDITION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            adaptor.addChild(root_1, stream_slots.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "query"

  public static class scriptCond_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "scriptCond"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:693:1:
  // scriptCond : start= OPEN_SCRIPT_COND_TOKEN (l= lang )? LONG_CLOSE_TOKEN
  // cdata CLOSE_SCRIPT_COND_TOKEN -> ^( SCRIPTABLE_CONDITION[$start,\"script\"]
  // cdata ) ;
  public final JACTRParser.scriptCond_return scriptCond()
      throws RecognitionException
  {
    JACTRParser.scriptCond_return retval = new JACTRParser.scriptCond_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN71 = null;
    Token CLOSE_SCRIPT_COND_TOKEN73 = null;
    JACTRParser.lang_return l = null;

    JACTRParser.cdata_return cdata72 = null;

    RewriteRuleTokenStream stream_CLOSE_SCRIPT_COND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_SCRIPT_COND_TOKEN");
    RewriteRuleTokenStream stream_OPEN_SCRIPT_COND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_SCRIPT_COND_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_cdata = new RewriteRuleSubtreeStream(
        adaptor, "rule cdata");
    RewriteRuleSubtreeStream stream_lang = new RewriteRuleSubtreeStream(
        adaptor, "rule lang");

    Tree lang = (Tree) adaptor.create(LANG, "js");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:697:2:
      // (start= OPEN_SCRIPT_COND_TOKEN (l= lang )? LONG_CLOSE_TOKEN cdata
      // CLOSE_SCRIPT_COND_TOKEN -> ^( SCRIPTABLE_CONDITION[$start,\"script\"]
      // cdata ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:697:4:
      // start= OPEN_SCRIPT_COND_TOKEN (l= lang )? LONG_CLOSE_TOKEN cdata
      // CLOSE_SCRIPT_COND_TOKEN
      {
        start = (Token) match(input, OPEN_SCRIPT_COND_TOKEN,
            FOLLOW_OPEN_SCRIPT_COND_TOKEN_in_scriptCond2210);
        stream_OPEN_SCRIPT_COND_TOKEN.add(start);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:697:33:
        // (l= lang )?
        int alt33 = 2;
        int LA33_0 = input.LA(1);

        if (LA33_0 == LANG_ATTR_TOKEN) alt33 = 1;
        switch (alt33)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:697:34:
          // l= lang
          {
            pushFollow(FOLLOW_lang_in_scriptCond2215);
            l = lang();

            state._fsp--;

            stream_lang.add(l.getTree());

          }
            break;

        }

        if (l != null) lang = l.tree;

        LONG_CLOSE_TOKEN71 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_scriptCond2239);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN71);

        pushFollow(FOLLOW_cdata_in_scriptCond2243);
        cdata72 = cdata();

        state._fsp--;

        stream_cdata.add(cdata72.getTree());
        CLOSE_SCRIPT_COND_TOKEN73 = (Token) match(input,
            CLOSE_SCRIPT_COND_TOKEN,
            FOLLOW_CLOSE_SCRIPT_COND_TOKEN_in_scriptCond2247);
        stream_CLOSE_SCRIPT_COND_TOKEN.add(CLOSE_SCRIPT_COND_TOKEN73);

        // AST REWRITE
        // elements: cdata
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 705:3: -> ^( SCRIPTABLE_CONDITION[$start,\"script\"] cdata )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:706:3:
          // ^( SCRIPTABLE_CONDITION[$start,\"script\"] cdata )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(SCRIPTABLE_CONDITION, start, "script"), root_1);

            adaptor.addChild(root_1, lang);
            adaptor.addChild(root_1, stream_cdata.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "scriptCond"

  public static class proxyCond_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "proxyCond"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:708:1:
  // proxyCond : start= OPEN_PROXY_COND_TOKEN classSpec ( SHORT_CLOSE_TOKEN | (
  // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN ) ) -> ^(
  // PROXY_CONDITION[$start,\"proxy\"] classSpec ( slots )? ) ;
  public final JACTRParser.proxyCond_return proxyCond()
      throws RecognitionException
  {
    JACTRParser.proxyCond_return retval = new JACTRParser.proxyCond_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN75 = null;
    Token LONG_CLOSE_TOKEN76 = null;
    Token CLOSE_PROXY_COND_TOKEN78 = null;
    JACTRParser.classSpec_return classSpec74 = null;

    JACTRParser.slots_return slots77 = null;

    RewriteRuleTokenStream stream_OPEN_PROXY_COND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PROXY_COND_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_PROXY_COND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_PROXY_COND_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_classSpec = new RewriteRuleSubtreeStream(
        adaptor, "rule classSpec");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:709:2:
      // (start= OPEN_PROXY_COND_TOKEN classSpec ( SHORT_CLOSE_TOKEN | (
      // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN ) ) -> ^(
      // PROXY_CONDITION[$start,\"proxy\"] classSpec ( slots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:709:5:
      // start= OPEN_PROXY_COND_TOKEN classSpec ( SHORT_CLOSE_TOKEN | (
      // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN ) )
      {
        start = (Token) match(input, OPEN_PROXY_COND_TOKEN,
            FOLLOW_OPEN_PROXY_COND_TOKEN_in_proxyCond2275);
        stream_OPEN_PROXY_COND_TOKEN.add(start);

        pushFollow(FOLLOW_classSpec_in_proxyCond2277);
        classSpec74 = classSpec();

        state._fsp--;

        stream_classSpec.add(classSpec74.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:710:3:
        // ( SHORT_CLOSE_TOKEN | ( LONG_CLOSE_TOKEN ( slots )?
        // CLOSE_PROXY_COND_TOKEN ) )
        int alt35 = 2;
        int LA35_0 = input.LA(1);

        if (LA35_0 == SHORT_CLOSE_TOKEN)
          alt35 = 1;
        else if (LA35_0 == LONG_CLOSE_TOKEN)
          alt35 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 35, 0, input);

          throw nvae;
        }
        switch (alt35)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:710:4:
          // SHORT_CLOSE_TOKEN
          {
            SHORT_CLOSE_TOKEN75 = (Token) match(input, SHORT_CLOSE_TOKEN,
                FOLLOW_SHORT_CLOSE_TOKEN_in_proxyCond2282);
            stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN75);

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:711:4:
          // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:711:4:
            // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:711:5:
            // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_COND_TOKEN
            {
              LONG_CLOSE_TOKEN76 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_proxyCond2288);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN76);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:711:22:
              // ( slots )?
              int alt34 = 2;
              int LA34_0 = input.LA(1);

              if (LA34_0 == OPEN_SLOT_TOKEN) alt34 = 1;
              switch (alt34)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:711:22:
                // slots
                {
                  pushFollow(FOLLOW_slots_in_proxyCond2290);
                  slots77 = slots();

                  state._fsp--;

                  stream_slots.add(slots77.getTree());

                }
                  break;

              }

              CLOSE_PROXY_COND_TOKEN78 = (Token) match(input,
                  CLOSE_PROXY_COND_TOKEN,
                  FOLLOW_CLOSE_PROXY_COND_TOKEN_in_proxyCond2293);
              stream_CLOSE_PROXY_COND_TOKEN.add(CLOSE_PROXY_COND_TOKEN78);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: slots, classSpec
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 712:4: -> ^( PROXY_CONDITION[$start,\"proxy\"] classSpec ( slots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:713:4:
          // ^( PROXY_CONDITION[$start,\"proxy\"] classSpec ( slots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(PROXY_CONDITION, start, "proxy"), root_1);

            adaptor.addChild(root_1, stream_classSpec.nextTree());
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:713:48:
            // ( slots )?
            if (stream_slots.hasNext()) adaptor.addChild(root_1, stream_slots.nextTree());
            stream_slots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "proxyCond"

  public static class scriptAct_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "scriptAct"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:716:1:
  // scriptAct : start= OPEN_SCRIPT_ACT_TOKEN (l= lang )? LONG_CLOSE_TOKEN cdata
  // CLOSE_SCRIPT_ACT_TOKEN -> ^( SCRIPTABLE_ACTION[$start,\"script\"] cdata ) ;
  public final JACTRParser.scriptAct_return scriptAct()
      throws RecognitionException
  {
    JACTRParser.scriptAct_return retval = new JACTRParser.scriptAct_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN79 = null;
    Token CLOSE_SCRIPT_ACT_TOKEN81 = null;
    JACTRParser.lang_return l = null;

    JACTRParser.cdata_return cdata80 = null;

    RewriteRuleTokenStream stream_CLOSE_SCRIPT_ACT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_SCRIPT_ACT_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_OPEN_SCRIPT_ACT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_SCRIPT_ACT_TOKEN");
    RewriteRuleSubtreeStream stream_cdata = new RewriteRuleSubtreeStream(
        adaptor, "rule cdata");
    RewriteRuleSubtreeStream stream_lang = new RewriteRuleSubtreeStream(
        adaptor, "rule lang");

    Tree lang = (Tree) adaptor.create(LANG, "js");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:720:2:
      // (start= OPEN_SCRIPT_ACT_TOKEN (l= lang )? LONG_CLOSE_TOKEN cdata
      // CLOSE_SCRIPT_ACT_TOKEN -> ^( SCRIPTABLE_ACTION[$start,\"script\"] cdata
      // ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:720:4:
      // start= OPEN_SCRIPT_ACT_TOKEN (l= lang )? LONG_CLOSE_TOKEN cdata
      // CLOSE_SCRIPT_ACT_TOKEN
      {
        start = (Token) match(input, OPEN_SCRIPT_ACT_TOKEN,
            FOLLOW_OPEN_SCRIPT_ACT_TOKEN_in_scriptAct2331);
        stream_OPEN_SCRIPT_ACT_TOKEN.add(start);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:720:32:
        // (l= lang )?
        int alt36 = 2;
        int LA36_0 = input.LA(1);

        if (LA36_0 == LANG_ATTR_TOKEN) alt36 = 1;
        switch (alt36)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:720:33:
          // l= lang
          {
            pushFollow(FOLLOW_lang_in_scriptAct2336);
            l = lang();

            state._fsp--;

            stream_lang.add(l.getTree());

          }
            break;

        }

        if (l != null) lang = l.tree;

        LONG_CLOSE_TOKEN79 = (Token) match(input, LONG_CLOSE_TOKEN,
            FOLLOW_LONG_CLOSE_TOKEN_in_scriptAct2360);
        stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN79);

        pushFollow(FOLLOW_cdata_in_scriptAct2364);
        cdata80 = cdata();

        state._fsp--;

        stream_cdata.add(cdata80.getTree());
        CLOSE_SCRIPT_ACT_TOKEN81 = (Token) match(input, CLOSE_SCRIPT_ACT_TOKEN,
            FOLLOW_CLOSE_SCRIPT_ACT_TOKEN_in_scriptAct2368);
        stream_CLOSE_SCRIPT_ACT_TOKEN.add(CLOSE_SCRIPT_ACT_TOKEN81);

        // AST REWRITE
        // elements: cdata
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 728:3: -> ^( SCRIPTABLE_ACTION[$start,\"script\"] cdata )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:729:3:
          // ^( SCRIPTABLE_ACTION[$start,\"script\"] cdata )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(SCRIPTABLE_ACTION, start, "script"), root_1);

            adaptor.addChild(root_1, lang);
            adaptor.addChild(root_1, stream_cdata.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "scriptAct"

  public static class cdata_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "cdata"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:731:1:
  // cdata : c= CDATA_TOKEN -> ^( SCRIPT[scriptText] ) ;
  public final JACTRParser.cdata_return cdata() throws RecognitionException
  {
    JACTRParser.cdata_return retval = new JACTRParser.cdata_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token c = null;

    RewriteRuleTokenStream stream_CDATA_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CDATA_TOKEN");

    String scriptText = "";

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:735:3:
      // (c= CDATA_TOKEN -> ^( SCRIPT[scriptText] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:735:5:
      // c= CDATA_TOKEN
      {
        c = (Token) match(input, CDATA_TOKEN, FOLLOW_CDATA_TOKEN_in_cdata2399);
        stream_CDATA_TOKEN.add(c);

        scriptText = c != null ? c.getText() : null;
        scriptText = scriptText.substring(9, scriptText.length() - 3);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 740:2: -> ^( SCRIPT[scriptText] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:741:2:
          // ^( SCRIPT[scriptText] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(SCRIPT, scriptText), root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "cdata"

  public static class proxyAct_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "proxyAct"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:743:1:
  // proxyAct : start= OPEN_PROXY_ACT_TOKEN classSpec ( ( SHORT_CLOSE_TOKEN ) |
  // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN ) ) -> ^(
  // PROXY_ACTION[$start,\"proxy\"] classSpec ( slots )? ) ;
  public final JACTRParser.proxyAct_return proxyAct()
      throws RecognitionException
  {
    JACTRParser.proxyAct_return retval = new JACTRParser.proxyAct_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN83 = null;
    Token LONG_CLOSE_TOKEN84 = null;
    Token CLOSE_PROXY_ACT_TOKEN86 = null;
    JACTRParser.classSpec_return classSpec82 = null;

    JACTRParser.slots_return slots85 = null;

    RewriteRuleTokenStream stream_CLOSE_PROXY_ACT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_PROXY_ACT_TOKEN");
    RewriteRuleTokenStream stream_OPEN_PROXY_ACT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PROXY_ACT_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_classSpec = new RewriteRuleSubtreeStream(
        adaptor, "rule classSpec");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:744:2:
      // (start= OPEN_PROXY_ACT_TOKEN classSpec ( ( SHORT_CLOSE_TOKEN ) | (
      // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN ) ) -> ^(
      // PROXY_ACTION[$start,\"proxy\"] classSpec ( slots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:744:5:
      // start= OPEN_PROXY_ACT_TOKEN classSpec ( ( SHORT_CLOSE_TOKEN ) | (
      // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN ) )
      {
        start = (Token) match(input, OPEN_PROXY_ACT_TOKEN,
            FOLLOW_OPEN_PROXY_ACT_TOKEN_in_proxyAct2425);
        stream_OPEN_PROXY_ACT_TOKEN.add(start);

        pushFollow(FOLLOW_classSpec_in_proxyAct2427);
        classSpec82 = classSpec();

        state._fsp--;

        stream_classSpec.add(classSpec82.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:745:4:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )?
        // CLOSE_PROXY_ACT_TOKEN ) )
        int alt38 = 2;
        int LA38_0 = input.LA(1);

        if (LA38_0 == SHORT_CLOSE_TOKEN)
          alt38 = 1;
        else if (LA38_0 == LONG_CLOSE_TOKEN)
          alt38 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 38, 0, input);

          throw nvae;
        }
        switch (alt38)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:745:5:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:745:5:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:745:6:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN83 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_proxyAct2434);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN83);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:746:6:
          // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:746:6:
            // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:746:7:
            // LONG_CLOSE_TOKEN ( slots )? CLOSE_PROXY_ACT_TOKEN
            {
              LONG_CLOSE_TOKEN84 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_proxyAct2443);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN84);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:746:24:
              // ( slots )?
              int alt37 = 2;
              int LA37_0 = input.LA(1);

              if (LA37_0 == OPEN_SLOT_TOKEN) alt37 = 1;
              switch (alt37)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:746:24:
                // slots
                {
                  pushFollow(FOLLOW_slots_in_proxyAct2445);
                  slots85 = slots();

                  state._fsp--;

                  stream_slots.add(slots85.getTree());

                }
                  break;

              }

              CLOSE_PROXY_ACT_TOKEN86 = (Token) match(input,
                  CLOSE_PROXY_ACT_TOKEN,
                  FOLLOW_CLOSE_PROXY_ACT_TOKEN_in_proxyAct2448);
              stream_CLOSE_PROXY_ACT_TOKEN.add(CLOSE_PROXY_ACT_TOKEN86);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: classSpec, slots
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 747:4: -> ^( PROXY_ACTION[$start,\"proxy\"] classSpec ( slots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:748:4:
          // ^( PROXY_ACTION[$start,\"proxy\"] classSpec ( slots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(PROXY_ACTION, start, "proxy"), root_1);

            adaptor.addChild(root_1, stream_classSpec.nextTree());
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:748:45:
            // ( slots )?
            if (stream_slots.hasNext()) adaptor.addChild(root_1, stream_slots.nextTree());
            stream_slots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "proxyAct"

  public static class output_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "output"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:751:1:
  // output : start= OPEN_OUTPUT_TOKEN s= string CLOSE_OUTPUT_TOKEN -> ^(
  // OUTPUT_ACTION[$start,$s.text] string ) ;
  public final JACTRParser.output_return output() throws RecognitionException
  {
    JACTRParser.output_return retval = new JACTRParser.output_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token CLOSE_OUTPUT_TOKEN87 = null;
    JACTRParser.string_return s = null;

    RewriteRuleTokenStream stream_CLOSE_OUTPUT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_OUTPUT_TOKEN");
    RewriteRuleTokenStream stream_OPEN_OUTPUT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_OUTPUT_TOKEN");
    RewriteRuleSubtreeStream stream_string = new RewriteRuleSubtreeStream(
        adaptor, "rule string");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:751:9:
      // (start= OPEN_OUTPUT_TOKEN s= string CLOSE_OUTPUT_TOKEN -> ^(
      // OUTPUT_ACTION[$start,$s.text] string ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:751:11:
      // start= OPEN_OUTPUT_TOKEN s= string CLOSE_OUTPUT_TOKEN
      {
        start = (Token) match(input, OPEN_OUTPUT_TOKEN,
            FOLLOW_OPEN_OUTPUT_TOKEN_in_output2480);
        stream_OPEN_OUTPUT_TOKEN.add(start);

        pushFollow(FOLLOW_string_in_output2485);
        s = string();

        state._fsp--;

        stream_string.add(s.getTree());
        CLOSE_OUTPUT_TOKEN87 = (Token) match(input, CLOSE_OUTPUT_TOKEN,
            FOLLOW_CLOSE_OUTPUT_TOKEN_in_output2504);
        stream_CLOSE_OUTPUT_TOKEN.add(CLOSE_OUTPUT_TOKEN87);

        // AST REWRITE
        // elements: string
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 753:17: -> ^( OUTPUT_ACTION[$start,$s.text] string )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:754:17:
          // ^( OUTPUT_ACTION[$start,$s.text] string )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(OUTPUT_ACTION, start,
                        s != null ? input.toString(s.start, s.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_string.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "output"

  public static class remove_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "remove"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:756:1:
  // remove : start= OPEN_REMOVE_TOKEN b= bufferRef ( chunkRef )? ( (
  // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN ) )
  // -> ^( REMOVE_ACTION[$start,$b.text] bufferRef ( chunkRef )? ( slots )? ) ;
  public final JACTRParser.remove_return remove() throws RecognitionException
  {
    JACTRParser.remove_return retval = new JACTRParser.remove_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN89 = null;
    Token LONG_CLOSE_TOKEN90 = null;
    Token CLOSE_REMOVE_TOKEN92 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.chunkRef_return chunkRef88 = null;

    JACTRParser.slots_return slots91 = null;

    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_REMOVE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_REMOVE_TOKEN");
    RewriteRuleTokenStream stream_OPEN_REMOVE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_REMOVE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:756:9:
      // (start= OPEN_REMOVE_TOKEN b= bufferRef ( chunkRef )? ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN
      // ) ) -> ^( REMOVE_ACTION[$start,$b.text] bufferRef ( chunkRef )? ( slots
      // )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:756:11:
      // start= OPEN_REMOVE_TOKEN b= bufferRef ( chunkRef )? ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN
      // ) )
      {
        start = (Token) match(input, OPEN_REMOVE_TOKEN,
            FOLLOW_OPEN_REMOVE_TOKEN_in_remove2556);
        stream_OPEN_REMOVE_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_remove2560);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:756:47:
        // ( chunkRef )?
        int alt39 = 2;
        int LA39_0 = input.LA(1);

        if (LA39_0 == CHUNK_ATTR_TOKEN) alt39 = 1;
        switch (alt39)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:756:47:
          // chunkRef
          {
            pushFollow(FOLLOW_chunkRef_in_remove2562);
            chunkRef88 = chunkRef();

            state._fsp--;

            stream_chunkRef.add(chunkRef88.getTree());

          }
            break;

        }

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:757:3:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )?
        // CLOSE_REMOVE_TOKEN ) )
        int alt41 = 2;
        int LA41_0 = input.LA(1);

        if (LA41_0 == SHORT_CLOSE_TOKEN)
          alt41 = 1;
        else if (LA41_0 == LONG_CLOSE_TOKEN)
          alt41 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 41, 0, input);

          throw nvae;
        }
        switch (alt41)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:757:4:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:757:4:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:757:5:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN89 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_remove2569);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN89);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:758:3:
          // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:758:3:
            // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:758:4:
            // LONG_CLOSE_TOKEN ( slots )? CLOSE_REMOVE_TOKEN
            {
              LONG_CLOSE_TOKEN90 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_remove2578);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN90);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:758:21:
              // ( slots )?
              int alt40 = 2;
              int LA40_0 = input.LA(1);

              if (LA40_0 == OPEN_SLOT_TOKEN) alt40 = 1;
              switch (alt40)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:758:21:
                // slots
                {
                  pushFollow(FOLLOW_slots_in_remove2580);
                  slots91 = slots();

                  state._fsp--;

                  stream_slots.add(slots91.getTree());

                }
                  break;

              }

              CLOSE_REMOVE_TOKEN92 = (Token) match(input, CLOSE_REMOVE_TOKEN,
                  FOLLOW_CLOSE_REMOVE_TOKEN_in_remove2583);
              stream_CLOSE_REMOVE_TOKEN.add(CLOSE_REMOVE_TOKEN92);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: chunkRef, slots, bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 759:3: -> ^( REMOVE_ACTION[$start,$b.text] bufferRef ( chunkRef )? (
        // slots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:760:3:
          // ^( REMOVE_ACTION[$start,$b.text] bufferRef ( chunkRef )? ( slots )?
          // )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(REMOVE_ACTION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:760:45:
            // ( chunkRef )?
            if (stream_chunkRef.hasNext()) adaptor.addChild(root_1, stream_chunkRef.nextTree());
            stream_chunkRef.reset();
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:760:55:
            // ( slots )?
            if (stream_slots.hasNext()) adaptor.addChild(root_1, stream_slots.nextTree());
            stream_slots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "remove"

  public static class modify_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "modify"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:762:1:
  // modify : start= OPEN_MODIFY_TOKEN b= bufferRef ( ( LONG_CLOSE_TOKEN slots
  // CLOSE_MODIFY_TOKEN ) | SHORT_CLOSE_TOKEN ) -> ^(
  // MODIFY_ACTION[$start,$b.text] bufferRef ( slots )? ) ;
  public final JACTRParser.modify_return modify() throws RecognitionException
  {
    JACTRParser.modify_return retval = new JACTRParser.modify_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token LONG_CLOSE_TOKEN93 = null;
    Token CLOSE_MODIFY_TOKEN95 = null;
    Token SHORT_CLOSE_TOKEN96 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.slots_return slots94 = null;

    RewriteRuleTokenStream stream_CLOSE_MODIFY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_MODIFY_TOKEN");
    RewriteRuleTokenStream stream_OPEN_MODIFY_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_MODIFY_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:762:9:
      // (start= OPEN_MODIFY_TOKEN b= bufferRef ( ( LONG_CLOSE_TOKEN slots
      // CLOSE_MODIFY_TOKEN ) | SHORT_CLOSE_TOKEN ) -> ^(
      // MODIFY_ACTION[$start,$b.text] bufferRef ( slots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:762:11:
      // start= OPEN_MODIFY_TOKEN b= bufferRef ( ( LONG_CLOSE_TOKEN slots
      // CLOSE_MODIFY_TOKEN ) | SHORT_CLOSE_TOKEN )
      {
        start = (Token) match(input, OPEN_MODIFY_TOKEN,
            FOLLOW_OPEN_MODIFY_TOKEN_in_modify2615);
        stream_OPEN_MODIFY_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_modify2619);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:763:17:
        // ( ( LONG_CLOSE_TOKEN slots CLOSE_MODIFY_TOKEN ) | SHORT_CLOSE_TOKEN )
        int alt42 = 2;
        int LA42_0 = input.LA(1);

        if (LA42_0 == LONG_CLOSE_TOKEN)
          alt42 = 1;
        else if (LA42_0 == SHORT_CLOSE_TOKEN)
          alt42 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 42, 0, input);

          throw nvae;
        }
        switch (alt42)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:763:18:
          // ( LONG_CLOSE_TOKEN slots CLOSE_MODIFY_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:763:18:
            // ( LONG_CLOSE_TOKEN slots CLOSE_MODIFY_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:763:19:
            // LONG_CLOSE_TOKEN slots CLOSE_MODIFY_TOKEN
            {
              LONG_CLOSE_TOKEN93 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_modify2640);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN93);

              pushFollow(FOLLOW_slots_in_modify2642);
              slots94 = slots();

              state._fsp--;

              stream_slots.add(slots94.getTree());
              CLOSE_MODIFY_TOKEN95 = (Token) match(input, CLOSE_MODIFY_TOKEN,
                  FOLLOW_CLOSE_MODIFY_TOKEN_in_modify2644);
              stream_CLOSE_MODIFY_TOKEN.add(CLOSE_MODIFY_TOKEN95);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:763:64:
          // SHORT_CLOSE_TOKEN
          {
            SHORT_CLOSE_TOKEN96 = (Token) match(input, SHORT_CLOSE_TOKEN,
                FOLLOW_SHORT_CLOSE_TOKEN_in_modify2649);
            stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN96);

          }
            break;

        }

        // AST REWRITE
        // elements: slots, bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 764:17: -> ^( MODIFY_ACTION[$start,$b.text] bufferRef ( slots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:765:17:
          // ^( MODIFY_ACTION[$start,$b.text] bufferRef ( slots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(MODIFY_ACTION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:765:59:
            // ( slots )?
            if (stream_slots.hasNext()) adaptor.addChild(root_1, stream_slots.nextTree());
            stream_slots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "modify"

  public static class add_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "add"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:767:1:
  // add : start= OPEN_ADD_TOKEN b= bufferRef (t= isa | c= chunkRef ) ( (
  // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN ) ) ->
  // ^( ADD_ACTION[$start,$b.text] bufferRef ( lslots )? ) ;
  public final JACTRParser.add_return add() throws RecognitionException
  {
    JACTRParser.add_return retval = new JACTRParser.add_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN97 = null;
    Token LONG_CLOSE_TOKEN98 = null;
    Token CLOSE_ADD_TOKEN100 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.isa_return t = null;

    JACTRParser.chunkRef_return c = null;

    JACTRParser.lslots_return lslots99 = null;

    RewriteRuleTokenStream stream_OPEN_ADD_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_ADD_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_ADD_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_ADD_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_isa = new RewriteRuleSubtreeStream(adaptor,
        "rule isa");
    RewriteRuleSubtreeStream stream_lslots = new RewriteRuleSubtreeStream(
        adaptor, "rule lslots");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:768:2:
      // (start= OPEN_ADD_TOKEN b= bufferRef (t= isa | c= chunkRef ) ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN )
      // ) -> ^( ADD_ACTION[$start,$b.text] bufferRef ( lslots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:768:4:
      // start= OPEN_ADD_TOKEN b= bufferRef (t= isa | c= chunkRef ) ( (
      // SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN )
      // )
      {
        start = (Token) match(input, OPEN_ADD_TOKEN,
            FOLLOW_OPEN_ADD_TOKEN_in_add2706);
        stream_OPEN_ADD_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_add2710);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:768:37:
        // (t= isa | c= chunkRef )
        int alt43 = 2;
        int LA43_0 = input.LA(1);

        if (LA43_0 == TYPE_ATTR_TOKEN)
          alt43 = 1;
        else if (LA43_0 == CHUNK_ATTR_TOKEN)
          alt43 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 43, 0, input);

          throw nvae;
        }
        switch (alt43)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:768:38:
          // t= isa
          {
            pushFollow(FOLLOW_isa_in_add2715);
            t = isa();

            state._fsp--;

            stream_isa.add(t.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:768:46:
          // c= chunkRef
          {
            pushFollow(FOLLOW_chunkRef_in_add2721);
            c = chunkRef();

            state._fsp--;

            stream_chunkRef.add(c.getTree());

          }
            break;

        }

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:769:4:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( lslots )?
        // CLOSE_ADD_TOKEN ) )
        int alt45 = 2;
        int LA45_0 = input.LA(1);

        if (LA45_0 == SHORT_CLOSE_TOKEN)
          alt45 = 1;
        else if (LA45_0 == LONG_CLOSE_TOKEN)
          alt45 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 45, 0, input);

          throw nvae;
        }
        switch (alt45)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:769:5:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:769:5:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:769:6:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN97 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_add2730);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN97);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:771:5:
          // ( LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:771:5:
            // ( LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:771:6:
            // LONG_CLOSE_TOKEN ( lslots )? CLOSE_ADD_TOKEN
            {
              LONG_CLOSE_TOKEN98 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_add2745);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN98);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:771:23:
              // ( lslots )?
              int alt44 = 2;
              int LA44_0 = input.LA(1);

              if (LA44_0 == OPEN_OR_TOKEN || LA44_0 == OPEN_AND_TOKEN
                  || LA44_0 == OPEN_NOT_TOKEN || LA44_0 == OPEN_SLOT_TOKEN)
                alt44 = 1;
              switch (alt44)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:771:23:
                // lslots
                {
                  pushFollow(FOLLOW_lslots_in_add2747);
                  lslots99 = lslots();

                  state._fsp--;

                  stream_lslots.add(lslots99.getTree());

                }
                  break;

              }

              CLOSE_ADD_TOKEN100 = (Token) match(input, CLOSE_ADD_TOKEN,
                  FOLLOW_CLOSE_ADD_TOKEN_in_add2750);
              stream_CLOSE_ADD_TOKEN.add(CLOSE_ADD_TOKEN100);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: lslots, bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 772:3: -> ^( ADD_ACTION[$start,$b.text] bufferRef ( lslots )? )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:773:3:
          // ^( ADD_ACTION[$start,$b.text] bufferRef ( lslots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(ADD_ACTION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            adaptor.addChild(root_1, c != null ? c.tree : t != null ? t.tree
                : null);
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:773:85:
            // ( lslots )?
            if (stream_lslots.hasNext()) adaptor.addChild(root_1, stream_lslots.nextTree());
            stream_lslots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "add"

  public static class set_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "set"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:775:1:
  // set : start= OPEN_SET_TOKEN b= bufferRef chunkRef ( ( SHORT_CLOSE_TOKEN ) |
  // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN ) ) -> ^(
  // SET_ACTION[$start,$b.text] bufferRef chunkRef ( slots )? ) ;
  public final JACTRParser.set_return set() throws RecognitionException
  {
    JACTRParser.set_return retval = new JACTRParser.set_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;
    Token SHORT_CLOSE_TOKEN102 = null;
    Token LONG_CLOSE_TOKEN103 = null;
    Token CLOSE_SET_TOKEN105 = null;
    JACTRParser.bufferRef_return b = null;

    JACTRParser.chunkRef_return chunkRef101 = null;

    JACTRParser.slots_return slots104 = null;

    RewriteRuleTokenStream stream_OPEN_SET_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_SET_TOKEN");
    RewriteRuleTokenStream stream_LONG_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LONG_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_SET_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_SET_TOKEN");
    RewriteRuleSubtreeStream stream_slots = new RewriteRuleSubtreeStream(
        adaptor, "rule slots");
    RewriteRuleSubtreeStream stream_chunkRef = new RewriteRuleSubtreeStream(
        adaptor, "rule chunkRef");
    RewriteRuleSubtreeStream stream_bufferRef = new RewriteRuleSubtreeStream(
        adaptor, "rule bufferRef");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:776:2:
      // (start= OPEN_SET_TOKEN b= bufferRef chunkRef ( ( SHORT_CLOSE_TOKEN ) |
      // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN ) ) -> ^(
      // SET_ACTION[$start,$b.text] bufferRef chunkRef ( slots )? ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:776:4:
      // start= OPEN_SET_TOKEN b= bufferRef chunkRef ( ( SHORT_CLOSE_TOKEN ) | (
      // LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN ) )
      {
        start = (Token) match(input, OPEN_SET_TOKEN,
            FOLLOW_OPEN_SET_TOKEN_in_set2784);
        stream_OPEN_SET_TOKEN.add(start);

        pushFollow(FOLLOW_bufferRef_in_set2788);
        b = bufferRef();

        state._fsp--;

        stream_bufferRef.add(b.getTree());
        pushFollow(FOLLOW_chunkRef_in_set2790);
        chunkRef101 = chunkRef();

        state._fsp--;

        stream_chunkRef.add(chunkRef101.getTree());
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:777:4:
        // ( ( SHORT_CLOSE_TOKEN ) | ( LONG_CLOSE_TOKEN ( slots )?
        // CLOSE_SET_TOKEN ) )
        int alt47 = 2;
        int LA47_0 = input.LA(1);

        if (LA47_0 == SHORT_CLOSE_TOKEN)
          alt47 = 1;
        else if (LA47_0 == LONG_CLOSE_TOKEN)
          alt47 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 47, 0, input);

          throw nvae;
        }
        switch (alt47)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:777:5:
          // ( SHORT_CLOSE_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:777:5:
            // ( SHORT_CLOSE_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:777:6:
            // SHORT_CLOSE_TOKEN
            {
              SHORT_CLOSE_TOKEN102 = (Token) match(input, SHORT_CLOSE_TOKEN,
                  FOLLOW_SHORT_CLOSE_TOKEN_in_set2797);
              stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN102);

            }

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:779:5:
          // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN )
          {
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:779:5:
            // ( LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN )
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:779:6:
            // LONG_CLOSE_TOKEN ( slots )? CLOSE_SET_TOKEN
            {
              LONG_CLOSE_TOKEN103 = (Token) match(input, LONG_CLOSE_TOKEN,
                  FOLLOW_LONG_CLOSE_TOKEN_in_set2812);
              stream_LONG_CLOSE_TOKEN.add(LONG_CLOSE_TOKEN103);

              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:779:23:
              // ( slots )?
              int alt46 = 2;
              int LA46_0 = input.LA(1);

              if (LA46_0 == OPEN_SLOT_TOKEN) alt46 = 1;
              switch (alt46)
              {
                case 1:
                // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:779:23:
                // slots
                {
                  pushFollow(FOLLOW_slots_in_set2814);
                  slots104 = slots();

                  state._fsp--;

                  stream_slots.add(slots104.getTree());

                }
                  break;

              }

              CLOSE_SET_TOKEN105 = (Token) match(input, CLOSE_SET_TOKEN,
                  FOLLOW_CLOSE_SET_TOKEN_in_set2817);
              stream_CLOSE_SET_TOKEN.add(CLOSE_SET_TOKEN105);

            }

          }
            break;

        }

        // AST REWRITE
        // elements: slots, chunkRef, bufferRef
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 780:3: -> ^( SET_ACTION[$start,$b.text] bufferRef chunkRef ( slots )?
        // )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:781:3:
          // ^( SET_ACTION[$start,$b.text] bufferRef chunkRef ( slots )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(SET_ACTION, start,
                        b != null ? input.toString(b.start, b.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_bufferRef.nextTree());
            adaptor.addChild(root_1, stream_chunkRef.nextTree());
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:781:51:
            // ( slots )?
            if (stream_slots.hasNext()) adaptor.addChild(root_1, stream_slots.nextTree());
            stream_slots.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "set"

  public static class stop_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "stop"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:783:1:
  // stop : start= STOP_TOKEN -> ^( PROXY_ACTION[$start,\"stop\"]
  // CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] ) ;
  public final JACTRParser.stop_return stop() throws RecognitionException
  {
    JACTRParser.stop_return retval = new JACTRParser.stop_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token start = null;

    RewriteRuleTokenStream stream_STOP_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STOP_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:783:7:
      // (start= STOP_TOKEN -> ^( PROXY_ACTION[$start,\"stop\"]
      // CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:783:9:
      // start= STOP_TOKEN
      {
        start = (Token) match(input, STOP_TOKEN, FOLLOW_STOP_TOKEN_in_stop2850);
        stream_STOP_TOKEN.add(start);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 784:17: -> ^( PROXY_ACTION[$start,\"stop\"]
        // CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:785:17:
          // ^( PROXY_ACTION[$start,\"stop\"]
          // CLASS_SPEC[\"org.jactr.core.production.action.StopAction\"] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(PROXY_ACTION, start, "stop"), root_1);

            adaptor.addChild(root_1, adaptor.create(CLASS_SPEC,
                "org.jactr.core.production.action.StopAction"));

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "stop"

  public static class parameters_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "parameters"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:787:1:
  // parameters : pm= OPEN_PARAMETERS_TOKEN ( parameter )+
  // CLOSE_PARAMETERS_TOKEN -> ^( PARAMETERS[$pm,\"parameters\"] ( parameter )+
  // ) ;
  public final JACTRParser.parameters_return parameters()
      throws RecognitionException
  {
    JACTRParser.parameters_return retval = new JACTRParser.parameters_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token pm = null;
    Token CLOSE_PARAMETERS_TOKEN107 = null;
    JACTRParser.parameter_return parameter106 = null;

    RewriteRuleTokenStream stream_OPEN_PARAMETERS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PARAMETERS_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_PARAMETERS_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_PARAMETERS_TOKEN");
    RewriteRuleSubtreeStream stream_parameter = new RewriteRuleSubtreeStream(
        adaptor, "rule parameter");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:788:2:
      // (pm= OPEN_PARAMETERS_TOKEN ( parameter )+ CLOSE_PARAMETERS_TOKEN -> ^(
      // PARAMETERS[$pm,\"parameters\"] ( parameter )+ ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:788:4:
      // pm= OPEN_PARAMETERS_TOKEN ( parameter )+ CLOSE_PARAMETERS_TOKEN
      {
        pm = (Token) match(input, OPEN_PARAMETERS_TOKEN,
            FOLLOW_OPEN_PARAMETERS_TOKEN_in_parameters2905);
        stream_OPEN_PARAMETERS_TOKEN.add(pm);

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:789:11:
        // ( parameter )+
        int cnt48 = 0;
        loop48: do
        {
          int alt48 = 2;
          int LA48_0 = input.LA(1);

          if (LA48_0 == OPEN_PARAMETER_TOKEN) alt48 = 1;

          switch (alt48)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:789:11:
            // parameter
            {
              pushFollow(FOLLOW_parameter_in_parameters2917);
              parameter106 = parameter();

              state._fsp--;

              stream_parameter.add(parameter106.getTree());

            }
              break;

            default:
              if (cnt48 >= 1) break loop48;
              EarlyExitException eee = new EarlyExitException(48, input);
              throw eee;
          }
          cnt48++;
        }
        while (true);

        CLOSE_PARAMETERS_TOKEN107 = (Token) match(input,
            CLOSE_PARAMETERS_TOKEN,
            FOLLOW_CLOSE_PARAMETERS_TOKEN_in_parameters2930);
        stream_CLOSE_PARAMETERS_TOKEN.add(CLOSE_PARAMETERS_TOKEN107);

        // AST REWRITE
        // elements: parameter
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 791:11: -> ^( PARAMETERS[$pm,\"parameters\"] ( parameter )+ )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:791:14:
          // ^( PARAMETERS[$pm,\"parameters\"] ( parameter )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(PARAMETERS, pm, "parameters"), root_1);

            if (!stream_parameter.hasNext()) throw new RewriteEarlyExitException();
            while (stream_parameter.hasNext())
              adaptor.addChild(root_1, stream_parameter.nextTree());
            stream_parameter.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "parameters"

  public static class parameter_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "parameter"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:793:1:
  // parameter : pm= OPEN_PARAMETER_TOKEN n= parameterName p= parameterValue
  // SHORT_CLOSE_TOKEN -> ^( PARAMETER[$pm,$n.text] parameterName parameterValue
  // ) ;
  public final JACTRParser.parameter_return parameter()
      throws RecognitionException
  {
    JACTRParser.parameter_return retval = new JACTRParser.parameter_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token pm = null;
    Token SHORT_CLOSE_TOKEN108 = null;
    JACTRParser.parameterName_return n = null;

    JACTRParser.parameterValue_return p = null;

    RewriteRuleTokenStream stream_OPEN_PARAMETER_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_PARAMETER_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_parameterValue = new RewriteRuleSubtreeStream(
        adaptor, "rule parameterValue");
    RewriteRuleSubtreeStream stream_parameterName = new RewriteRuleSubtreeStream(
        adaptor, "rule parameterName");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:794:2:
      // (pm= OPEN_PARAMETER_TOKEN n= parameterName p= parameterValue
      // SHORT_CLOSE_TOKEN -> ^( PARAMETER[$pm,$n.text] parameterName
      // parameterValue ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:794:4:
      // pm= OPEN_PARAMETER_TOKEN n= parameterName p= parameterValue
      // SHORT_CLOSE_TOKEN
      {
        pm = (Token) match(input, OPEN_PARAMETER_TOKEN,
            FOLLOW_OPEN_PARAMETER_TOKEN_in_parameter2963);
        stream_OPEN_PARAMETER_TOKEN.add(pm);

        pushFollow(FOLLOW_parameterName_in_parameter2967);
        n = parameterName();

        state._fsp--;

        stream_parameterName.add(n.getTree());
        pushFollow(FOLLOW_parameterValue_in_parameter2971);
        p = parameterValue();

        state._fsp--;

        stream_parameterValue.add(p.getTree());
        SHORT_CLOSE_TOKEN108 = (Token) match(input, SHORT_CLOSE_TOKEN,
            FOLLOW_SHORT_CLOSE_TOKEN_in_parameter2973);
        stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN108);

        // AST REWRITE
        // elements: parameterValue, parameterName
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 795:4: -> ^( PARAMETER[$pm,$n.text] parameterName parameterValue )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:795:7:
          // ^( PARAMETER[$pm,$n.text] parameterName parameterValue )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor
                .becomeRoot(
                    adaptor.create(PARAMETER, pm,
                        n != null ? input.toString(n.start, n.stop) : null),
                    root_1);

            adaptor.addChild(root_1, stream_parameterName.nextTree());
            adaptor.addChild(root_1, stream_parameterValue.nextTree());

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "parameter"

  public static class slots_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "slots"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:1:
  // slots : ( slot )+ -> ^( SLOTS ( slot )+ ) ;
  public final JACTRParser.slots_return slots() throws RecognitionException
  {
    JACTRParser.slots_return retval = new JACTRParser.slots_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.slot_return slot109 = null;

    RewriteRuleSubtreeStream stream_slot = new RewriteRuleSubtreeStream(
        adaptor, "rule slot");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:8:
      // ( ( slot )+ -> ^( SLOTS ( slot )+ ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:10:
      // ( slot )+
      {
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:10:
        // ( slot )+
        int cnt49 = 0;
        loop49: do
        {
          int alt49 = 2;
          int LA49_0 = input.LA(1);

          if (LA49_0 == OPEN_SLOT_TOKEN) alt49 = 1;

          switch (alt49)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:10:
            // slot
            {
              pushFollow(FOLLOW_slot_in_slots2996);
              slot109 = slot();

              state._fsp--;

              stream_slot.add(slot109.getTree());

            }
              break;

            default:
              if (cnt49 >= 1) break loop49;
              EarlyExitException eee = new EarlyExitException(49, input);
              throw eee;
          }
          cnt49++;
        }
        while (true);

        // AST REWRITE
        // elements: slot
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 797:16: -> ^( SLOTS ( slot )+ )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:797:19:
          // ^( SLOTS ( slot )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(SLOTS, "SLOTS"), root_1);

            if (!stream_slot.hasNext()) throw new RewriteEarlyExitException();
            while (stream_slot.hasNext())
              adaptor.addChild(root_1, stream_slot.nextTree());
            stream_slot.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "slots"

  public static class lslots_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "lslots"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:1:
  // lslots : ( lslot )+ -> ^( SLOTS ( lslot )+ ) ;
  public final JACTRParser.lslots_return lslots() throws RecognitionException
  {
    JACTRParser.lslots_return retval = new JACTRParser.lslots_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.lslot_return lslot110 = null;

    RewriteRuleSubtreeStream stream_lslot = new RewriteRuleSubtreeStream(
        adaptor, "rule lslot");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:9:
      // ( ( lslot )+ -> ^( SLOTS ( lslot )+ ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:11:
      // ( lslot )+
      {
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:11:
        // ( lslot )+
        int cnt50 = 0;
        loop50: do
        {
          int alt50 = 2;
          int LA50_0 = input.LA(1);

          if (LA50_0 == OPEN_OR_TOKEN || LA50_0 == OPEN_AND_TOKEN
              || LA50_0 == OPEN_NOT_TOKEN || LA50_0 == OPEN_SLOT_TOKEN) alt50 = 1;

          switch (alt50)
          {
            case 1:
            // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:11:
            // lslot
            {
              pushFollow(FOLLOW_lslot_in_lslots3015);
              lslot110 = lslot();

              state._fsp--;

              stream_lslot.add(lslot110.getTree());

            }
              break;

            default:
              if (cnt50 >= 1) break loop50;
              EarlyExitException eee = new EarlyExitException(50, input);
              throw eee;
          }
          cnt50++;
        }
        while (true);

        // AST REWRITE
        // elements: lslot
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 799:18: -> ^( SLOTS ( lslot )+ )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:799:21:
          // ^( SLOTS ( lslot )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(SLOTS, "SLOTS"), root_1);

            if (!stream_lslot.hasNext()) throw new RewriteEarlyExitException();
            while (stream_lslot.hasNext())
              adaptor.addChild(root_1, stream_lslot.nextTree());
            stream_lslot.reset();

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "lslots"

  public static class version_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "version"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:801:1:
  // version : VERSION_ATTR_TOKEN s= STRING_TOKEN -> ^( NUMBER[$s] ) ;
  public final JACTRParser.version_return version() throws RecognitionException
  {
    JACTRParser.version_return retval = new JACTRParser.version_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token VERSION_ATTR_TOKEN111 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_VERSION_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token VERSION_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:801:9:
      // ( VERSION_ATTR_TOKEN s= STRING_TOKEN -> ^( NUMBER[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:801:11:
      // VERSION_ATTR_TOKEN s= STRING_TOKEN
      {
        VERSION_ATTR_TOKEN111 = (Token) match(input, VERSION_ATTR_TOKEN,
            FOLLOW_VERSION_ATTR_TOKEN_in_version3033);
        stream_VERSION_ATTR_TOKEN.add(VERSION_ATTR_TOKEN111);

        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_version3037);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 801:45: -> ^( NUMBER[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:801:48:
          // ^( NUMBER[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(NUMBER, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "version"

  public static class classSpec_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "classSpec"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:803:1:
  // classSpec : CLASS_ATTR_TOKEN s= string -> ^(
  // CLASS_SPEC[((CommonTree)s.tree).token ] ) ;
  public final JACTRParser.classSpec_return classSpec()
      throws RecognitionException
  {
    JACTRParser.classSpec_return retval = new JACTRParser.classSpec_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token CLASS_ATTR_TOKEN112 = null;
    JACTRParser.string_return s = null;

    RewriteRuleTokenStream stream_CLASS_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLASS_ATTR_TOKEN");
    RewriteRuleSubtreeStream stream_string = new RewriteRuleSubtreeStream(
        adaptor, "rule string");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:804:2:
      // ( CLASS_ATTR_TOKEN s= string -> ^(
      // CLASS_SPEC[((CommonTree)s.tree).token ] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:804:5:
      // CLASS_ATTR_TOKEN s= string
      {
        CLASS_ATTR_TOKEN112 = (Token) match(input, CLASS_ATTR_TOKEN,
            FOLLOW_CLASS_ATTR_TOKEN_in_classSpec3055);
        stream_CLASS_ATTR_TOKEN.add(CLASS_ATTR_TOKEN112);

        pushFollow(FOLLOW_string_in_classSpec3059);
        s = string();

        state._fsp--;

        stream_string.add(s.getTree());

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 804:31: -> ^( CLASS_SPEC[((CommonTree)s.tree).token ] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:804:34:
          // ^( CLASS_SPEC[((CommonTree)s.tree).token ] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(CLASS_SPEC, s.tree.token), root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "classSpec"

  public static class name_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "name"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:807:1:
  // name : NAME_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) ;
  public final JACTRParser.name_return name() throws RecognitionException
  {
    JACTRParser.name_return retval = new JACTRParser.name_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token NAME_ATTR_TOKEN113 = null;

    RewriteRuleTokenStream stream_NAME_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token NAME_ATTR_TOKEN");
    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:807:7:
      // ( NAME_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:807:9:
      // NAME_ATTR_TOKEN s= STRING_TOKEN
      {
        NAME_ATTR_TOKEN113 = (Token) match(input, NAME_ATTR_TOKEN,
            FOLLOW_NAME_ATTR_TOKEN_in_name3087);
        stream_NAME_ATTR_TOKEN.add(NAME_ATTR_TOKEN113);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_name3094);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 808:18: -> ^( NAME[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:808:21:
          // ^( NAME[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(NAME, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "name"

  public static class parents_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "parents"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:813:1:
  // parents : PARENT_ATTR_TOKEN s= STRING_TOKEN ->;
  public final JACTRParser.parents_return parents() throws RecognitionException
  {
    JACTRParser.parents_return retval = new JACTRParser.parents_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token PARENT_ATTR_TOKEN114 = null;

    RewriteRuleTokenStream stream_PARENT_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token PARENT_ATTR_TOKEN");
    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");

    CommonTree parentsNode = (CommonTree) adaptor.create(PARENTS, "parents");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:817:3:
      // ( PARENT_ATTR_TOKEN s= STRING_TOKEN ->)
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:817:5:
      // PARENT_ATTR_TOKEN s= STRING_TOKEN
      {
        PARENT_ATTR_TOKEN114 = (Token) match(input, PARENT_ATTR_TOKEN,
            FOLLOW_PARENT_ATTR_TOKEN_in_parents3129);
        stream_PARENT_ATTR_TOKEN.add(PARENT_ATTR_TOKEN114);

        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_parents3137);
        stream_STRING_TOKEN.add(s);

        String[] parentNames = (s != null ? s.getText() : null).toLowerCase()
            .split(",");
        for (String parentName : parentNames)
          parentsNode.addChild((CommonTree) adaptor.create(PARENT, parentName));
        LOGGER.debug("created parents tree " + parentsNode);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 826:4: ->
        {
          adaptor.addChild(root_0, parentsNode);

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "parents"

  public static class isa_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "isa"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:828:1:
  // isa : TYPE_ATTR_TOKEN s= STRING_TOKEN -> ^( CHUNK_TYPE_IDENTIFIER[$s] ) ;
  public final JACTRParser.isa_return isa() throws RecognitionException
  {
    JACTRParser.isa_return retval = new JACTRParser.isa_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token TYPE_ATTR_TOKEN115 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_TYPE_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token TYPE_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:828:9:
      // ( TYPE_ATTR_TOKEN s= STRING_TOKEN -> ^( CHUNK_TYPE_IDENTIFIER[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:828:11:
      // TYPE_ATTR_TOKEN s= STRING_TOKEN
      {
        TYPE_ATTR_TOKEN115 = (Token) match(input, TYPE_ATTR_TOKEN,
            FOLLOW_TYPE_ATTR_TOKEN_in_isa3162);
        stream_TYPE_ATTR_TOKEN.add(TYPE_ATTR_TOKEN115);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_isa3166);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 828:42: -> ^( CHUNK_TYPE_IDENTIFIER[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:828:45:
          // ^( CHUNK_TYPE_IDENTIFIER[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(CHUNK_TYPE_IDENTIFIER, s), root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "isa"

  public static class type_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "type"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:830:1:
  // type : TYPE_ATTR_TOKEN s= STRING_TOKEN -> ^( PARENT[$s] ) ;
  public final JACTRParser.type_return type() throws RecognitionException
  {
    JACTRParser.type_return retval = new JACTRParser.type_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token TYPE_ATTR_TOKEN116 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_TYPE_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token TYPE_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:830:7:
      // ( TYPE_ATTR_TOKEN s= STRING_TOKEN -> ^( PARENT[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:830:9:
      // TYPE_ATTR_TOKEN s= STRING_TOKEN
      {
        TYPE_ATTR_TOKEN116 = (Token) match(input, TYPE_ATTR_TOKEN,
            FOLLOW_TYPE_ATTR_TOKEN_in_type3182);
        stream_TYPE_ATTR_TOKEN.add(TYPE_ATTR_TOKEN116);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_type3186);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 830:40: -> ^( PARENT[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:830:43:
          // ^( PARENT[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(PARENT, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "type"

  public static class value_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "value"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:832:1:
  // value : VALUE_ATTR_TOKEN s= STRING_TOKEN -> ^( IDENTIFIER[$s] ) ;
  public final JACTRParser.value_return value() throws RecognitionException
  {
    JACTRParser.value_return retval = new JACTRParser.value_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token VALUE_ATTR_TOKEN117 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_VALUE_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token VALUE_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:832:8:
      // ( VALUE_ATTR_TOKEN s= STRING_TOKEN -> ^( IDENTIFIER[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:832:10:
      // VALUE_ATTR_TOKEN s= STRING_TOKEN
      {
        VALUE_ATTR_TOKEN117 = (Token) match(input, VALUE_ATTR_TOKEN,
            FOLLOW_VALUE_ATTR_TOKEN_in_value3202);
        stream_VALUE_ATTR_TOKEN.add(VALUE_ATTR_TOKEN117);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_value3206);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 832:42: -> ^( IDENTIFIER[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:832:45:
          // ^( IDENTIFIER[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(
                adaptor.create(IDENTIFIER, s), root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "value"

  public static class bufferRef_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "bufferRef"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:834:1:
  // bufferRef : BUFFER_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) ;
  public final JACTRParser.bufferRef_return bufferRef()
      throws RecognitionException
  {
    JACTRParser.bufferRef_return retval = new JACTRParser.bufferRef_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token BUFFER_ATTR_TOKEN118 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_BUFFER_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token BUFFER_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:834:12:
      // ( BUFFER_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:834:14:
      // BUFFER_ATTR_TOKEN s= STRING_TOKEN
      {
        BUFFER_ATTR_TOKEN118 = (Token) match(input, BUFFER_ATTR_TOKEN,
            FOLLOW_BUFFER_ATTR_TOKEN_in_bufferRef3222);
        stream_BUFFER_ATTR_TOKEN.add(BUFFER_ATTR_TOKEN118);

        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_bufferRef3226);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 834:47: -> ^( NAME[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:834:50:
          // ^( NAME[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(NAME, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "bufferRef"

  public static class lang_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "lang"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:836:1:
  // lang : LANG_ATTR_TOKEN s= STRING_TOKEN -> ^( LANG[$s] ) ;
  public final JACTRParser.lang_return lang() throws RecognitionException
  {
    JACTRParser.lang_return retval = new JACTRParser.lang_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token LANG_ATTR_TOKEN119 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_LANG_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token LANG_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:836:7:
      // ( LANG_ATTR_TOKEN s= STRING_TOKEN -> ^( LANG[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:836:9:
      // LANG_ATTR_TOKEN s= STRING_TOKEN
      {
        LANG_ATTR_TOKEN119 = (Token) match(input, LANG_ATTR_TOKEN,
            FOLLOW_LANG_ATTR_TOKEN_in_lang3242);
        stream_LANG_ATTR_TOKEN.add(LANG_ATTR_TOKEN119);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_lang3246);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 836:40: -> ^( LANG[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:836:43:
          // ^( LANG[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(LANG, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "lang"

  public static class parameterValue_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "parameterValue"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:838:1:
  // parameterValue : VALUE_ATTR_TOKEN s= STRING_TOKEN -> ^( STRING[$s] ) ;
  public final JACTRParser.parameterValue_return parameterValue()
      throws RecognitionException
  {
    JACTRParser.parameterValue_return retval = new JACTRParser.parameterValue_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token VALUE_ATTR_TOKEN120 = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_VALUE_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token VALUE_ATTR_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:839:2:
      // ( VALUE_ATTR_TOKEN s= STRING_TOKEN -> ^( STRING[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:839:4:
      // VALUE_ATTR_TOKEN s= STRING_TOKEN
      {
        VALUE_ATTR_TOKEN120 = (Token) match(input, VALUE_ATTR_TOKEN,
            FOLLOW_VALUE_ATTR_TOKEN_in_parameterValue3263);
        stream_VALUE_ATTR_TOKEN.add(VALUE_ATTR_TOKEN120);

        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_parameterValue3267);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 839:36: -> ^( STRING[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:839:39:
          // ^( STRING[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(STRING, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "parameterValue"

  public static class parameterName_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "parameterName"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:841:1:
  // parameterName : NAME_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) ;
  public final JACTRParser.parameterName_return parameterName()
      throws RecognitionException
  {
    JACTRParser.parameterName_return retval = new JACTRParser.parameterName_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;
    Token NAME_ATTR_TOKEN121 = null;

    RewriteRuleTokenStream stream_NAME_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token NAME_ATTR_TOKEN");
    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:842:2:
      // ( NAME_ATTR_TOKEN s= STRING_TOKEN -> ^( NAME[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:842:4:
      // NAME_ATTR_TOKEN s= STRING_TOKEN
      {
        NAME_ATTR_TOKEN121 = (Token) match(input, NAME_ATTR_TOKEN,
            FOLLOW_NAME_ATTR_TOKEN_in_parameterName3285);
        stream_NAME_ATTR_TOKEN.add(NAME_ATTR_TOKEN121);

        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_parameterName3289);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 842:35: -> ^( NAME[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:842:38:
          // ^( NAME[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(NAME, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "parameterName"

  public static class string_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "string"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:845:1:
  // string : s= STRING_TOKEN -> ^( STRING[$s] ) ;
  public final JACTRParser.string_return string() throws RecognitionException
  {
    JACTRParser.string_return retval = new JACTRParser.string_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;

    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:846:1:
      // (s= STRING_TOKEN -> ^( STRING[$s] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:846:3:
      // s= STRING_TOKEN
      {
        s = (Token) match(input, STRING_TOKEN,
            FOLLOW_STRING_TOKEN_in_string3308);
        stream_STRING_TOKEN.add(s);

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 846:18: -> ^( STRING[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:846:21:
          // ^( STRING[$s] )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(STRING, s),
                root_1);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "string"

  public static class chunkRef_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "chunkRef"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:848:1:
  // chunkRef : CHUNK_ATTR_TOKEN s= string -> {isVariable}? ^(
  // VARIABLE[((CommonTree)s.tree).token] ) -> ^(
  // CHUNK_IDENTIFIER[((CommonTree)s.tree).token] ) ;
  public final JACTRParser.chunkRef_return chunkRef()
      throws RecognitionException
  {
    JACTRParser.chunkRef_return retval = new JACTRParser.chunkRef_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token CHUNK_ATTR_TOKEN122 = null;
    JACTRParser.string_return s = null;

    RewriteRuleTokenStream stream_CHUNK_ATTR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CHUNK_ATTR_TOKEN");
    RewriteRuleSubtreeStream stream_string = new RewriteRuleSubtreeStream(
        adaptor, "rule string");

    boolean isVariable = false;
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:853:2:
      // ( CHUNK_ATTR_TOKEN s= string -> {isVariable}? ^(
      // VARIABLE[((CommonTree)s.tree).token] ) -> ^(
      // CHUNK_IDENTIFIER[((CommonTree)s.tree).token] ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:853:4:
      // CHUNK_ATTR_TOKEN s= string
      {
        CHUNK_ATTR_TOKEN122 = (Token) match(input, CHUNK_ATTR_TOKEN,
            FOLLOW_CHUNK_ATTR_TOKEN_in_chunkRef3329);
        stream_CHUNK_ATTR_TOKEN.add(CHUNK_ATTR_TOKEN122);

        pushFollow(FOLLOW_string_in_chunkRef3333);
        s = string();

        state._fsp--;

        stream_string.add(s.getTree());

        String text = s.tree.getText().toLowerCase();
        isVariable = text.startsWith("=");
        /*
         * if(text.startsWith("=")) idOrVar = adaptor.create(VARIABLE, s.token);
         * else idOrVar = adaptor.create(IDENTIFIER, s.token);
         */

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(
            adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 864:2: -> {isVariable}? ^( VARIABLE[((CommonTree)s.tree).token] )
        if (isVariable)
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:864:19:
        // ^( VARIABLE[((CommonTree)s.tree).token] )
        {
          CommonTree root_1 = (CommonTree) adaptor.nil();
          root_1 = (CommonTree) adaptor.becomeRoot(adaptor
              .create(VARIABLE, s.tree.token), root_1);

          adaptor.addChild(root_0, root_1);
        }
        else
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:865:5:
        // ^( CHUNK_IDENTIFIER[((CommonTree)s.tree).token] )
        {
          CommonTree root_1 = (CommonTree) adaptor.nil();
          root_1 = (CommonTree) adaptor.becomeRoot(adaptor
              .create(CHUNK_IDENTIFIER, s.tree.token), root_1);

          adaptor.addChild(root_0, root_1);
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "chunkRef"

  public static class condition_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "condition"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:867:1:
  // condition : ( (s= SLOT_EQ_TOKEN -> EQUALS[$s] ) | (s= SLOT_NOT_TOKEN ->
  // NOT[$s] ) | (s= SLOT_GT_TOKEN -> GT[$s] ) | (s= SLOT_GTE_TOKEN -> GTE[$s] )
  // | (s= SLOT_LT_TOKEN -> LT[$s] ) | (s= SLOT_LTE_TOKEN -> LTE[$s] ) | (s=
  // SLOT_WITHIN_TOKEN -> WITHIN[$s] ) );
  public final JACTRParser.condition_return condition()
      throws RecognitionException
  {
    JACTRParser.condition_return retval = new JACTRParser.condition_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token s = null;

    RewriteRuleTokenStream stream_SLOT_GTE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_GTE_TOKEN");
    RewriteRuleTokenStream stream_SLOT_WITHIN_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_WITHIN_TOKEN");
    RewriteRuleTokenStream stream_SLOT_NOT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_NOT_TOKEN");
    RewriteRuleTokenStream stream_SLOT_LTE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_LTE_TOKEN");
    RewriteRuleTokenStream stream_SLOT_EQ_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_EQ_TOKEN");
    RewriteRuleTokenStream stream_SLOT_LT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_LT_TOKEN");
    RewriteRuleTokenStream stream_SLOT_GT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SLOT_GT_TOKEN");

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:868:2:
      // ( (s= SLOT_EQ_TOKEN -> EQUALS[$s] ) | (s= SLOT_NOT_TOKEN -> NOT[$s] ) |
      // (s= SLOT_GT_TOKEN -> GT[$s] ) | (s= SLOT_GTE_TOKEN -> GTE[$s] ) | (s=
      // SLOT_LT_TOKEN -> LT[$s] ) | (s= SLOT_LTE_TOKEN -> LTE[$s] ) | (s=
      // SLOT_WITHIN_TOKEN -> WITHIN[$s] ) )
      int alt51 = 7;
      switch (input.LA(1))
      {
        case SLOT_EQ_TOKEN:
        {
          alt51 = 1;
        }
          break;
        case SLOT_NOT_TOKEN:
        {
          alt51 = 2;
        }
          break;
        case SLOT_GT_TOKEN:
        {
          alt51 = 3;
        }
          break;
        case SLOT_GTE_TOKEN:
        {
          alt51 = 4;
        }
          break;
        case SLOT_LT_TOKEN:
        {
          alt51 = 5;
        }
          break;
        case SLOT_LTE_TOKEN:
        {
          alt51 = 6;
        }
          break;
        case SLOT_WITHIN_TOKEN:
        {
          alt51 = 7;
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 51, 0, input);

          throw nvae;
      }

      switch (alt51)
      {
        case 1:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:868:4:
        // (s= SLOT_EQ_TOKEN -> EQUALS[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:868:4:
          // (s= SLOT_EQ_TOKEN -> EQUALS[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:868:5:
          // s= SLOT_EQ_TOKEN
          {
            s = (Token) match(input, SLOT_EQ_TOKEN,
                FOLLOW_SLOT_EQ_TOKEN_in_condition3368);
            stream_SLOT_EQ_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 868:21: -> EQUALS[$s]
            {
              adaptor.addChild(root_0, adaptor.create(EQUALS, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 2:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:869:3:
        // (s= SLOT_NOT_TOKEN -> NOT[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:869:3:
          // (s= SLOT_NOT_TOKEN -> NOT[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:869:4:
          // s= SLOT_NOT_TOKEN
          {
            s = (Token) match(input, SLOT_NOT_TOKEN,
                FOLLOW_SLOT_NOT_TOKEN_in_condition3383);
            stream_SLOT_NOT_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 869:21: -> NOT[$s]
            {
              adaptor.addChild(root_0, adaptor.create(NOT, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 3:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:870:3:
        // (s= SLOT_GT_TOKEN -> GT[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:870:3:
          // (s= SLOT_GT_TOKEN -> GT[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:870:4:
          // s= SLOT_GT_TOKEN
          {
            s = (Token) match(input, SLOT_GT_TOKEN,
                FOLLOW_SLOT_GT_TOKEN_in_condition3398);
            stream_SLOT_GT_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 870:20: -> GT[$s]
            {
              adaptor.addChild(root_0, adaptor.create(GT, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 4:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:871:3:
        // (s= SLOT_GTE_TOKEN -> GTE[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:871:3:
          // (s= SLOT_GTE_TOKEN -> GTE[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:871:4:
          // s= SLOT_GTE_TOKEN
          {
            s = (Token) match(input, SLOT_GTE_TOKEN,
                FOLLOW_SLOT_GTE_TOKEN_in_condition3413);
            stream_SLOT_GTE_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 871:21: -> GTE[$s]
            {
              adaptor.addChild(root_0, adaptor.create(GTE, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 5:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:872:3:
        // (s= SLOT_LT_TOKEN -> LT[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:872:3:
          // (s= SLOT_LT_TOKEN -> LT[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:872:4:
          // s= SLOT_LT_TOKEN
          {
            s = (Token) match(input, SLOT_LT_TOKEN,
                FOLLOW_SLOT_LT_TOKEN_in_condition3428);
            stream_SLOT_LT_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 872:20: -> LT[$s]
            {
              adaptor.addChild(root_0, adaptor.create(LT, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 6:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:873:3:
        // (s= SLOT_LTE_TOKEN -> LTE[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:873:3:
          // (s= SLOT_LTE_TOKEN -> LTE[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:873:4:
          // s= SLOT_LTE_TOKEN
          {
            s = (Token) match(input, SLOT_LTE_TOKEN,
                FOLLOW_SLOT_LTE_TOKEN_in_condition3443);
            stream_SLOT_LTE_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 873:21: -> LTE[$s]
            {
              adaptor.addChild(root_0, adaptor.create(LTE, s));

            }

            retval.tree = root_0;
          }

        }
          break;
        case 7:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:874:3:
        // (s= SLOT_WITHIN_TOKEN -> WITHIN[$s] )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:874:3:
          // (s= SLOT_WITHIN_TOKEN -> WITHIN[$s] )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:874:4:
          // s= SLOT_WITHIN_TOKEN
          {
            s = (Token) match(input, SLOT_WITHIN_TOKEN,
                FOLLOW_SLOT_WITHIN_TOKEN_in_condition3458);
            stream_SLOT_WITHIN_TOKEN.add(s);

            // AST REWRITE
            // elements:
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(
                adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 874:24: -> WITHIN[$s]
            {
              adaptor.addChild(root_0, adaptor.create(WITHIN, s));

            }

            retval.tree = root_0;
          }

        }
          break;

      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "condition"

  public static class lslot_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "lslot"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:1:
  // lslot : ( logic | slot ) ;
  public final JACTRParser.lslot_return lslot() throws RecognitionException
  {
    JACTRParser.lslot_return retval = new JACTRParser.lslot_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    JACTRParser.logic_return logic123 = null;

    JACTRParser.slot_return slot124 = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:8:
      // ( ( logic | slot ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:10:
      // ( logic | slot )
      {
        root_0 = (CommonTree) adaptor.nil();

        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:10:
        // ( logic | slot )
        int alt52 = 2;
        int LA52_0 = input.LA(1);

        if (LA52_0 == OPEN_OR_TOKEN || LA52_0 == OPEN_AND_TOKEN
            || LA52_0 == OPEN_NOT_TOKEN)
          alt52 = 1;
        else if (LA52_0 == OPEN_SLOT_TOKEN)
          alt52 = 2;
        else
        {
          NoViableAltException nvae = new NoViableAltException("", 52, 0, input);

          throw nvae;
        }
        switch (alt52)
        {
          case 1:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:11:
          // logic
          {
            pushFollow(FOLLOW_logic_in_lslot3474);
            logic123 = logic();

            state._fsp--;

            adaptor.addChild(root_0, logic123.getTree());

          }
            break;
          case 2:
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:876:17:
          // slot
          {
            pushFollow(FOLLOW_slot_in_lslot3476);
            slot124 = slot();

            state._fsp--;

            adaptor.addChild(root_0, slot124.getTree());

          }
            break;

        }

      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "lslot"

  public static class logic_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "logic"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:878:1:
  // logic : ( (l= OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN -> ^( LOGIC OR[$l,
  // \"or\"] lslot lslot ) ) | (l= OPEN_AND_TOKEN lslot lslot CLOSE_AND_TOKEN ->
  // ^( LOGIC AND[$l, \"and\"] lslot lslot ) ) | (l= OPEN_NOT_TOKEN lslot
  // CLOSE_NOT_TOKEN -> ^( LOGIC NOT[$l, \"not\"] lslot ) ) );
  public final JACTRParser.logic_return logic() throws RecognitionException
  {
    JACTRParser.logic_return retval = new JACTRParser.logic_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token l = null;
    Token CLOSE_OR_TOKEN127 = null;
    Token CLOSE_AND_TOKEN130 = null;
    Token CLOSE_NOT_TOKEN132 = null;
    JACTRParser.lslot_return lslot125 = null;

    JACTRParser.lslot_return lslot126 = null;

    JACTRParser.lslot_return lslot128 = null;

    JACTRParser.lslot_return lslot129 = null;

    JACTRParser.lslot_return lslot131 = null;

    RewriteRuleTokenStream stream_CLOSE_NOT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_NOT_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_AND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_AND_TOKEN");
    RewriteRuleTokenStream stream_OPEN_AND_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_AND_TOKEN");
    RewriteRuleTokenStream stream_OPEN_NOT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_NOT_TOKEN");
    RewriteRuleTokenStream stream_CLOSE_OR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token CLOSE_OR_TOKEN");
    RewriteRuleTokenStream stream_OPEN_OR_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_OR_TOKEN");
    RewriteRuleSubtreeStream stream_lslot = new RewriteRuleSubtreeStream(
        adaptor, "rule lslot");
    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:879:2:
      // ( (l= OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN -> ^( LOGIC OR[$l,
      // \"or\"] lslot lslot ) ) | (l= OPEN_AND_TOKEN lslot lslot
      // CLOSE_AND_TOKEN -> ^( LOGIC AND[$l, \"and\"] lslot lslot ) ) | (l=
      // OPEN_NOT_TOKEN lslot CLOSE_NOT_TOKEN -> ^( LOGIC NOT[$l, \"not\"] lslot
      // ) ) )
      int alt53 = 3;
      switch (input.LA(1))
      {
        case OPEN_OR_TOKEN:
        {
          alt53 = 1;
        }
          break;
        case OPEN_AND_TOKEN:
        {
          alt53 = 2;
        }
          break;
        case OPEN_NOT_TOKEN:
        {
          alt53 = 3;
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 53, 0, input);

          throw nvae;
      }

      switch (alt53)
      {
        case 1:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:879:10:
        // (l= OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN -> ^( LOGIC OR[$l,
        // \"or\"] lslot lslot ) )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:879:10:
          // (l= OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN -> ^( LOGIC OR[$l,
          // \"or\"] lslot lslot ) )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:879:11:
          // l= OPEN_OR_TOKEN lslot lslot CLOSE_OR_TOKEN
          {
            l = (Token) match(input, OPEN_OR_TOKEN,
                FOLLOW_OPEN_OR_TOKEN_in_logic3495);
            stream_OPEN_OR_TOKEN.add(l);

            pushFollow(FOLLOW_lslot_in_logic3497);
            lslot125 = lslot();

            state._fsp--;

            stream_lslot.add(lslot125.getTree());
            pushFollow(FOLLOW_lslot_in_logic3499);
            lslot126 = lslot();

            state._fsp--;

            stream_lslot.add(lslot126.getTree());
            CLOSE_OR_TOKEN127 = (Token) match(input, CLOSE_OR_TOKEN,
                FOLLOW_CLOSE_OR_TOKEN_in_logic3501);
            stream_CLOSE_OR_TOKEN.add(CLOSE_OR_TOKEN127);

            // AST REWRITE
            // elements: lslot, lslot
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(adaptor, "rule retval",
                retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 879:54: -> ^( LOGIC OR[$l, \"or\"] lslot lslot )
            {
              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:879:57:
              // ^( LOGIC OR[$l, \"or\"] lslot lslot )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    adaptor.create(LOGIC, "LOGIC"), root_1);

                adaptor.addChild(root_1, adaptor.create(OR, l, "or"));
                adaptor.addChild(root_1, stream_lslot.nextTree());
                adaptor.addChild(root_1, stream_lslot.nextTree());

                adaptor.addChild(root_0, root_1);
              }

            }

            retval.tree = root_0;
          }

        }
          break;
        case 2:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:880:3:
        // (l= OPEN_AND_TOKEN lslot lslot CLOSE_AND_TOKEN -> ^( LOGIC AND[$l,
        // \"and\"] lslot lslot ) )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:880:3:
          // (l= OPEN_AND_TOKEN lslot lslot CLOSE_AND_TOKEN -> ^( LOGIC AND[$l,
          // \"and\"] lslot lslot ) )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:880:4:
          // l= OPEN_AND_TOKEN lslot lslot CLOSE_AND_TOKEN
          {
            l = (Token) match(input, OPEN_AND_TOKEN,
                FOLLOW_OPEN_AND_TOKEN_in_logic3524);
            stream_OPEN_AND_TOKEN.add(l);

            pushFollow(FOLLOW_lslot_in_logic3526);
            lslot128 = lslot();

            state._fsp--;

            stream_lslot.add(lslot128.getTree());
            pushFollow(FOLLOW_lslot_in_logic3528);
            lslot129 = lslot();

            state._fsp--;

            stream_lslot.add(lslot129.getTree());
            CLOSE_AND_TOKEN130 = (Token) match(input, CLOSE_AND_TOKEN,
                FOLLOW_CLOSE_AND_TOKEN_in_logic3530);
            stream_CLOSE_AND_TOKEN.add(CLOSE_AND_TOKEN130);

            // AST REWRITE
            // elements: lslot, lslot
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(adaptor, "rule retval",
                retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 880:49: -> ^( LOGIC AND[$l, \"and\"] lslot lslot )
            {
              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:880:52:
              // ^( LOGIC AND[$l, \"and\"] lslot lslot )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    adaptor.create(LOGIC, "LOGIC"), root_1);

                adaptor.addChild(root_1, adaptor.create(AND, l, "and"));
                adaptor.addChild(root_1, stream_lslot.nextTree());
                adaptor.addChild(root_1, stream_lslot.nextTree());

                adaptor.addChild(root_0, root_1);
              }

            }

            retval.tree = root_0;
          }

        }
          break;
        case 3:
        // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:881:3:
        // (l= OPEN_NOT_TOKEN lslot CLOSE_NOT_TOKEN -> ^( LOGIC NOT[$l, \"not\"]
        // lslot ) )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:881:3:
          // (l= OPEN_NOT_TOKEN lslot CLOSE_NOT_TOKEN -> ^( LOGIC NOT[$l,
          // \"not\"] lslot ) )
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:881:4:
          // l= OPEN_NOT_TOKEN lslot CLOSE_NOT_TOKEN
          {
            l = (Token) match(input, OPEN_NOT_TOKEN,
                FOLLOW_OPEN_NOT_TOKEN_in_logic3553);
            stream_OPEN_NOT_TOKEN.add(l);

            pushFollow(FOLLOW_lslot_in_logic3555);
            lslot131 = lslot();

            state._fsp--;

            stream_lslot.add(lslot131.getTree());
            CLOSE_NOT_TOKEN132 = (Token) match(input, CLOSE_NOT_TOKEN,
                FOLLOW_CLOSE_NOT_TOKEN_in_logic3557);
            stream_CLOSE_NOT_TOKEN.add(CLOSE_NOT_TOKEN132);

            // AST REWRITE
            // elements: lslot
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            new RewriteRuleSubtreeStream(adaptor, "rule retval",
                retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 881:43: -> ^( LOGIC NOT[$l, \"not\"] lslot )
            {
              // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:881:46:
              // ^( LOGIC NOT[$l, \"not\"] lslot )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    adaptor.create(LOGIC, "LOGIC"), root_1);

                adaptor.addChild(root_1, adaptor.create(NOT, l, "not"));
                adaptor.addChild(root_1, stream_lslot.nextTree());

                adaptor.addChild(root_0, root_1);
              }

            }

            retval.tree = root_0;
          }

        }
          break;

      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "logic"

  public static class slot_return extends ParserRuleReturnScope
  {
    CommonTree tree;

    @Override
    public Object getTree()
    {
      return tree;
    }
  };

  // $ANTLR start "slot"
  // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:883:1:
  // slot : ss= OPEN_SLOT_TOKEN slotName= name cond= condition '=' s=
  // STRING_TOKEN SHORT_CLOSE_TOKEN -> ^( SLOT[$ss] condition ) ;
  public final JACTRParser.slot_return slot() throws RecognitionException
  {
    JACTRParser.slot_return retval = new JACTRParser.slot_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token ss = null;
    Token s = null;
    Token char_literal133 = null;
    Token SHORT_CLOSE_TOKEN134 = null;
    JACTRParser.name_return slotName = null;

    JACTRParser.condition_return cond = null;

    RewriteRuleTokenStream stream_OPEN_SLOT_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token OPEN_SLOT_TOKEN");
    RewriteRuleTokenStream stream_187 = new RewriteRuleTokenStream(adaptor,
        "token 187");
    RewriteRuleTokenStream stream_STRING_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token STRING_TOKEN");
    RewriteRuleTokenStream stream_SHORT_CLOSE_TOKEN = new RewriteRuleTokenStream(
        adaptor, "token SHORT_CLOSE_TOKEN");
    RewriteRuleSubtreeStream stream_condition = new RewriteRuleSubtreeStream(
        adaptor, "rule condition");
    RewriteRuleSubtreeStream stream_name = new RewriteRuleSubtreeStream(
        adaptor, "rule name");

    Tree value = null;
    Tree sName = null;

    try
    {
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:888:1:
      // (ss= OPEN_SLOT_TOKEN slotName= name cond= condition '=' s= STRING_TOKEN
      // SHORT_CLOSE_TOKEN -> ^( SLOT[$ss] condition ) )
      // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:888:3:
      // ss= OPEN_SLOT_TOKEN slotName= name cond= condition '=' s= STRING_TOKEN
      // SHORT_CLOSE_TOKEN
      {
        ss = (Token) match(input, OPEN_SLOT_TOKEN,
            FOLLOW_OPEN_SLOT_TOKEN_in_slot3585);
        stream_OPEN_SLOT_TOKEN.add(ss);

        pushFollow(FOLLOW_name_in_slot3608);
        slotName = name();

        state._fsp--;

        stream_name.add(slotName.getTree());
        pushFollow(FOLLOW_condition_in_slot3612);
        cond = condition();

        state._fsp--;

        stream_condition.add(cond.getTree());
        char_literal133 = (Token) match(input, 187, FOLLOW_187_in_slot3614);
        stream_187.add(char_literal133);

        s = (Token) match(input, STRING_TOKEN, FOLLOW_STRING_TOKEN_in_slot3618);
        stream_STRING_TOKEN.add(s);

        // process the string to figure out what it is..
        // choices are variable, string, number, identifier
        String str = s.getText();
        int type = IDENTIFIER;
        try
        {
          Double.parseDouble(str);
          type = NUMBER;
        }
        catch (NumberFormatException nfe)
        {
          // not a number
          if (str.startsWith("'") && str.endsWith("'"))
          {
            type = STRING;
            // strip the string
            str = str.substring(1, str.length() - 1);
          }
          else if (str.startsWith("=")) type = VARIABLE;
        }

        // slot name may be a variable
        if (slotName.tree.getText().startsWith("="))
          sName = (Tree) adaptor.create(VARIABLE, slotName.tree.token);
        else
          sName = slotName.tree;

        value = (Tree) adaptor.create(type, s, str);

        // special circumstances here
        // LOGGER.debug("Checking to see if isa to enforce equals condition... "
        // + slotName.tree.getText());
        if (slotName.tree.getText().equalsIgnoreCase(ISlot.ISA))
        {
          if (!(cond.tree.getText().equalsIgnoreCase("equals") || cond.tree
              .getText().equalsIgnoreCase("not")))
            reportException(new CompilationError(
                "isa slot test must have equals or not as a condition",
                cond.tree));
          if (type != IDENTIFIER)
            reportException(new CompilationError(
                "isa slot test must have a chunk-type as a value", cond.tree));
          else if (!((ModelGlobals_scope) ModelGlobals_stack.peek()).chunksWrapperMap
              .containsKey(s.getText().toLowerCase()))
            reportException(new CompilationError(s.getText()
                + " is not a known chunktype", cond.tree));
        }

        SHORT_CLOSE_TOKEN134 = (Token) match(input, SHORT_CLOSE_TOKEN,
            FOLLOW_SHORT_CLOSE_TOKEN_in_slot3657);
        stream_SHORT_CLOSE_TOKEN.add(SHORT_CLOSE_TOKEN134);

        // AST REWRITE
        // elements: condition
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        new RewriteRuleSubtreeStream(adaptor, "rule retval",
            retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 937:17: -> ^( SLOT[$ss] condition )
        {
          // /Users/harrison/Archive/Development/git/jactr/org.jactr.io/src/org/jactr/io/antlr3/parser/xml/JACTR.g:938:17:
          // ^( SLOT[$ss] condition )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(adaptor.create(SLOT, ss),
                root_1);

            adaptor.addChild(root_1, sName);
            adaptor.addChild(root_1, stream_condition.nextTree());
            adaptor.addChild(root_1, value);

            adaptor.addChild(root_0, root_1);
          }

        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

    }
    catch (RecognitionException re)
    {
      reportError(re);
      recover(input, re);
      retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
          input.LT(-1), re);

    }
    finally
    {
    }
    return retval;
  }

  // $ANTLR end "slot"

  // Delegated rules

  protected DFA32        dfa32             = new DFA32(this);

  static final String    DFA32_eotS        = "\13\uffff";

  static final String    DFA32_eofS        = "\13\uffff";

  static final String    DFA32_minS        = "\1\133\1\175\1\167\1\101\1\uffff\2\167\2\74\2\uffff";

  static final String    DFA32_maxS        = "\1\133\1\175\1\167\1\177\1\uffff\2\167\2\101\2\uffff";

  static final String    DFA32_acceptS     = "\4\uffff\1\3\4\uffff\1\1\1\2";

  static final String    DFA32_specialS    = "\13\uffff}>";

  static final String[]  DFA32_transitionS = { "\1\1", "\1\2", "\1\3",
      "\1\4\71\uffff\1\5\3\uffff\1\6", "", "\1\7", "\1\10",
      "\1\11\4\uffff\1\12", "\1\11\4\uffff\1\12", "", "" };

  static final short[]   DFA32_eot         = DFA
                                               .unpackEncodedString(DFA32_eotS);

  static final short[]   DFA32_eof         = DFA
                                               .unpackEncodedString(DFA32_eofS);

  static final char[]    DFA32_min         = DFA
                                               .unpackEncodedStringToUnsignedChars(DFA32_minS);

  static final char[]    DFA32_max         = DFA
                                               .unpackEncodedStringToUnsignedChars(DFA32_maxS);

  static final short[]   DFA32_accept      = DFA
                                               .unpackEncodedString(DFA32_acceptS);

  static final short[]   DFA32_special     = DFA
                                               .unpackEncodedString(DFA32_specialS);

  static final short[][] DFA32_transition;

  static
  {
    int numStates = DFA32_transitionS.length;
    DFA32_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++)
      DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
  }

  class DFA32 extends DFA
  {

    public DFA32(BaseRecognizer recognizer)
    {
      this.recognizer = recognizer;
      this.decisionNumber = 32;
      this.eot = DFA32_eot;
      this.eof = DFA32_eof;
      this.min = DFA32_min;
      this.max = DFA32_max;
      this.accept = DFA32_accept;
      this.special = DFA32_special;
      this.transition = DFA32_transition;
    }

    @Override
    public String getDescription()
    {
      return "684:1: match : ( matchLong | matchShort | emptyMatch );";
    }
  }

  public static final BitSet FOLLOW_OPEN_ACTR_TOKEN_in_model362                             = new BitSet(
                                                                                                new long[] { 0x0800000000000000L });

  public static final BitSet FOLLOW_OPEN_MODEL_TOKEN_in_model381                            = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_model385                                        = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0040000000000000L                                                 });

  public static final BitSet FOLLOW_version_in_model390                                     = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_model394                            = new BitSet(
                                                                                                new long[] {
      0x8000000000000000L, 0x0000000000000884L                                                 });

  public static final BitSet FOLLOW_modules_in_model433                                     = new BitSet(
                                                                                                new long[] {
      0x8000000000000000L, 0x0000000000000884L                                                 });

  public static final BitSet FOLLOW_extensions_in_model474                                  = new BitSet(
                                                                                                new long[] {
      0x8000000000000000L, 0x0000000000000884L                                                 });

  public static final BitSet FOLLOW_importDirective_in_model513                             = new BitSet(
                                                                                                new long[] {
      0x8000000000000000L, 0x0000000000000884L                                                 });

  public static final BitSet FOLLOW_library_in_model533                                     = new BitSet(
                                                                                                new long[] {
      0x2000000000000000L, 0x0008000000008000L                                                 });

  public static final BitSet FOLLOW_buffer_in_model572                                      = new BitSet(
                                                                                                new long[] {
      0x2000000000000000L, 0x0008000000008000L                                                 });

  public static final BitSet FOLLOW_parameters_in_model613                                  = new BitSet(
                                                                                                new long[] { 0x2000000000000000L });

  public static final BitSet FOLLOW_CLOSE_MODEL_TOKEN_in_model651                           = new BitSet(
                                                                                                new long[] { 0x4000000000000000L });

  public static final BitSet FOLLOW_CLOSE_ACTR_TOKEN_in_model669                            = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_IMPORT_TOKEN_in_importDirective720                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000001L                                                 });

  public static final BitSet FOLLOW_URL_TOKEN_in_importDirective723                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_string_in_importDirective727                            = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_importDirective729                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MODULES_TOKEN_in_modules744                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000018L                                                 });

  public static final BitSet FOLLOW_module_in_modules758                                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000018L                                                 });

  public static final BitSet FOLLOW_CLOSE_MODULES_TOKEN_in_modules773                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MODULE_TOKEN_in_module809                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0100000000000000L                                                 });

  public static final BitSet FOLLOW_classSpec_in_module813                                  = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000022L                                                 });

  public static final BitSet FOLLOW_IMPORT_ATTR_TOKEN_in_module816                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0800000000000000L                            });

  public static final BitSet FOLLOW_187_in_module818                                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_string_in_module822                                     = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_module844                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_module867                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000000000L                                                 });

  public static final BitSet FOLLOW_parameters_in_module871                                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000040L                                                 });

  public static final BitSet FOLLOW_CLOSE_MODULE_TOKEN_in_module873                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_EXTENSIONS_TOKEN_in_extensions965                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000300L                                                 });

  public static final BitSet FOLLOW_extension_in_extensions976                              = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000300L                                                 });

  public static final BitSet FOLLOW_CLOSE_EXTENSIONS_TOKEN_in_extensions988                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_EXTENSION_TOKEN_in_extension1030                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0100000000000000L                                                 });

  public static final BitSet FOLLOW_classSpec_in_extension1034                              = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_extension1054                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_extension1077                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000000000L                                                 });

  public static final BitSet FOLLOW_parameters_in_extension1081                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000400L                                                 });

  public static final BitSet FOLLOW_CLOSE_EXTENSION_TOKEN_in_extension1083                  = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_declarativeMemory_in_library1170                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000002000L                                                 });

  public static final BitSet FOLLOW_proceduralMemory_in_library1172                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_DECLARATIVE_MEMORY_TOKEN_in_declarativeMemory1198  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x00000000000A1000L                                                 });

  public static final BitSet FOLLOW_chunkType_in_declarativeMemory1212                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x00000000000A1000L                                                 });

  public static final BitSet FOLLOW_chunk_in_declarativeMemory1240                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x00000000000A1000L                                                 });

  public static final BitSet FOLLOW_CLOSE_DECLARATIVE_MEMORY_TOKEN_in_declarativeMemory1268 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PROCEDURAL_MEMORY_TOKEN_in_proceduralMemory1300    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000204000L                                                 });

  public static final BitSet FOLLOW_production_in_proceduralMemory1308                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000204000L                                                 });

  public static final BitSet FOLLOW_CLOSE_PROCEDURAL_MEMORY_TOKEN_in_proceduralMemory1322   = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_BUFFER_TOKEN_in_buffer1346                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_buffer1350                                      = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x8000000000000002L                                                 });

  public static final BitSet FOLLOW_chunkRef_in_buffer1355                                  = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_buffer1395                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_buffer1419                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000000000L                                                 });

  public static final BitSet FOLLOW_parameters_in_buffer1423                                = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000010000L                                                 });

  public static final BitSet FOLLOW_CLOSE_BUFFER_TOKEN_in_buffer1425                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_CHUNK_TYPE_TOKEN_in_chunkType1489                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_chunkType1493                                   = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0400000000000002L                                                 });

  public static final BitSet FOLLOW_parents_in_chunkType1498                                = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_chunkType1518                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_chunkType1536                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000040000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_chunkType1555                                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000040000L                                                 });

  public static final BitSet FOLLOW_parameters_in_chunkType1576                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000040000L                                                 });

  public static final BitSet FOLLOW_CLOSE_CHUNK_TYPE_TOKEN_in_chunkType1593                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_CHUNK_TOKEN_in_chunk1664                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0A00000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_chunk1670                                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0A00000000000000L                                                 });

  public static final BitSet FOLLOW_type_in_chunk1672                                       = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_type_in_chunk1678                                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_chunk1682                                       = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_chunk1692                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_chunk1703                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000100000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_chunk1708                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000100000L                                                 });

  public static final BitSet FOLLOW_parameters_in_chunk1715                                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000100000L                                                 });

  public static final BitSet FOLLOW_CLOSE_CHUNK_TOKEN_in_chunk1723                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PRODUCTION_TOKEN_in_production1785                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_production1789                                  = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_production1791                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000800000L                                                 });

  public static final BitSet FOLLOW_conditions_in_production1795                            = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000002000000L                                                 });

  public static final BitSet FOLLOW_actions_in_production1799                               = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0008000000400000L                                                 });

  public static final BitSet FOLLOW_parameters_in_production1806                            = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000400000L                                                 });

  public static final BitSet FOLLOW_CLOSE_PRODUCTION_TOKEN_in_production1812                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_CONDITIONS_TOKEN_in_conditions1853                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x00000002A9000000L                                                 });

  public static final BitSet FOLLOW_lhs_in_conditions1869                                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x00000002A9000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_CONDITIONS_TOKEN_in_conditions1884                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_match_in_lhs1907                                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_query_in_lhs1911                                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_scriptCond_in_lhs1915                                   = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_proxyCond_in_lhs1919                                    = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_ACTIONS_TOKEN_in_actions1935                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0005554804000000L                                                 });

  public static final BitSet FOLLOW_rhs_in_actions1938                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0005554804000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_ACTIONS_TOKEN_in_actions1942                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_add_in_rhs1967                                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_set_in_rhs1969                                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_modify_in_rhs1971                                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_remove_in_rhs1973                                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_scriptAct_in_rhs1975                                    = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_proxyAct_in_rhs1977                                     = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_output_in_rhs1979                                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_stop_in_rhs1981                                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MATCH_TOKEN_in_emptyMatch1995                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_emptyMatch1999                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_emptyMatch2002                     = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MATCH_TOKEN_in_matchShort2029                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_matchShort2033                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x8800000000000000L                                                 });

  public static final BitSet FOLLOW_isa_in_matchShort2038                                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_chunkRef_in_matchShort2044                              = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_matchShort2047                     = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MATCH_TOKEN_in_matchLong2076                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_matchLong2080                              = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x8800000000000000L                                                 });

  public static final BitSet FOLLOW_isa_in_matchLong2085                                    = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_chunkRef_in_matchLong2091                               = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_matchLong2094                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000010000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslots_in_matchLong2100                                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000010000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_MATCH_TOKEN_in_matchLong2106                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_matchLong_in_match2135                                  = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_matchShort_in_match2139                                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_emptyMatch_in_match2143                                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_QUERY_TOKEN_in_query2159                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_query2163                                  = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_query2165                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_query2170                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000040000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_QUERY_TOKEN_in_query2174                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_SCRIPT_COND_TOKEN_in_scriptCond2210                = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x4000000000000000L                                                 });

  public static final BitSet FOLLOW_lang_in_scriptCond2215                                  = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_scriptCond2239                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000002000000000L                                                 });

  public static final BitSet FOLLOW_cdata_in_scriptCond2243                                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000100000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_SCRIPT_COND_TOKEN_in_scriptCond2247               = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PROXY_COND_TOKEN_in_proxyCond2275                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0100000000000000L                                                 });

  public static final BitSet FOLLOW_classSpec_in_proxyCond2277                              = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_proxyCond2282                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_proxyCond2288                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000400000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_proxyCond2290                                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000400000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_PROXY_COND_TOKEN_in_proxyCond2293                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_SCRIPT_ACT_TOKEN_in_scriptAct2331                  = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x4000000000000000L                                                 });

  public static final BitSet FOLLOW_lang_in_scriptAct2336                                   = new BitSet(
                                                                                                new long[] { 0x1000000000000000L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_scriptAct2360                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000002000000000L                                                 });

  public static final BitSet FOLLOW_cdata_in_scriptAct2364                                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000001000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_SCRIPT_ACT_TOKEN_in_scriptAct2368                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_CDATA_TOKEN_in_cdata2399                                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PROXY_ACT_TOKEN_in_proxyAct2425                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0100000000000000L                                                 });

  public static final BitSet FOLLOW_classSpec_in_proxyAct2427                               = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_proxyAct2434                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_proxyAct2443                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000008000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_proxyAct2445                                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000008000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_PROXY_ACT_TOKEN_in_proxyAct2448                   = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_OUTPUT_TOKEN_in_output2480                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_string_in_output2485                                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000020000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_OUTPUT_TOKEN_in_output2504                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_REMOVE_TOKEN_in_remove2556                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_remove2560                                 = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x8000000000000002L                                                 });

  public static final BitSet FOLLOW_chunkRef_in_remove2562                                  = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_remove2569                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_remove2578                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000080000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_remove2580                                     = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000080000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_REMOVE_TOKEN_in_remove2583                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_MODIFY_TOKEN_in_modify2615                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_modify2619                                 = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_modify2640                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_modify2642                                     = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000200000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_MODIFY_TOKEN_in_modify2644                        = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_modify2649                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_ADD_TOKEN_in_add2706                               = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_add2710                                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x8800000000000000L                                                 });

  public static final BitSet FOLLOW_isa_in_add2715                                          = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_chunkRef_in_add2721                                     = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_add2730                            = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_add2745                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000800000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslots_in_add2747                                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000800000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_ADD_TOKEN_in_add2750                              = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_SET_TOKEN_in_set2784                               = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x2000000000000000L                                                 });

  public static final BitSet FOLLOW_bufferRef_in_set2788                                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x8000000000000000L                                                 });

  public static final BitSet FOLLOW_chunkRef_in_set2790                                     = new BitSet(
                                                                                                new long[] {
      0x1000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_set2797                            = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LONG_CLOSE_TOKEN_in_set2812                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0002000000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_slots_in_set2814                                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0002000000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_SET_TOKEN_in_set2817                              = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_STOP_TOKEN_in_stop2850                                  = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PARAMETERS_TOKEN_in_parameters2905                 = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0020000000000000L                                                 });

  public static final BitSet FOLLOW_parameter_in_parameters2917                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0030000000000000L                                                 });

  public static final BitSet FOLLOW_CLOSE_PARAMETERS_TOKEN_in_parameters2930                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_PARAMETER_TOKEN_in_parameter2963                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_parameterName_in_parameter2967                          = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x1000000000000000L                                                 });

  public static final BitSet FOLLOW_parameterValue_in_parameter2971                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_parameter2973                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_slot_in_slots2996                                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000002L, 0x0000000000000000L, 0x0000000000002000L                            });

  public static final BitSet FOLLOW_lslot_in_lslots3015                                     = new BitSet(
                                                                                                new long[] {
      0x0000000000000002L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_VERSION_ATTR_TOKEN_in_version3033                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_version3037                             = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_CLASS_ATTR_TOKEN_in_classSpec3055                       = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_string_in_classSpec3059                                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_NAME_ATTR_TOKEN_in_name3087                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_name3094                                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_PARENT_ATTR_TOKEN_in_parents3129                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_parents3137                             = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_TYPE_ATTR_TOKEN_in_isa3162                              = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_isa3166                                 = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_TYPE_ATTR_TOKEN_in_type3182                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_type3186                                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_VALUE_ATTR_TOKEN_in_value3202                           = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_value3206                               = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_BUFFER_ATTR_TOKEN_in_bufferRef3222                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_bufferRef3226                           = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_LANG_ATTR_TOKEN_in_lang3242                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_lang3246                                = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_VALUE_ATTR_TOKEN_in_parameterValue3263                  = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_parameterValue3267                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_NAME_ATTR_TOKEN_in_parameterName3285                    = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_parameterName3289                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_STRING_TOKEN_in_string3308                              = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_CHUNK_ATTR_TOKEN_in_chunkRef3329                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_string_in_chunkRef3333                                  = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_EQ_TOKEN_in_condition3368                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_NOT_TOKEN_in_condition3383                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_GT_TOKEN_in_condition3398                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_GTE_TOKEN_in_condition3413                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_LT_TOKEN_in_condition3428                          = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_LTE_TOKEN_in_condition3443                         = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_SLOT_WITHIN_TOKEN_in_condition3458                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_logic_in_lslot3474                                      = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_slot_in_lslot3476                                       = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_OR_TOKEN_in_logic3495                              = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslot_in_logic3497                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslot_in_logic3499                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000000100L                            });

  public static final BitSet FOLLOW_CLOSE_OR_TOKEN_in_logic3501                             = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_AND_TOKEN_in_logic3524                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslot_in_logic3526                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslot_in_logic3528                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000000400L                            });

  public static final BitSet FOLLOW_CLOSE_AND_TOKEN_in_logic3530                            = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_NOT_TOKEN_in_logic3553                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000002A80L                            });

  public static final BitSet FOLLOW_lslot_in_logic3555                                      = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0000000000001000L                            });

  public static final BitSet FOLLOW_CLOSE_NOT_TOKEN_in_logic3557                            = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_OPEN_SLOT_TOKEN_in_slot3585                             = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0200000000000000L                                                 });

  public static final BitSet FOLLOW_name_in_slot3608                                        = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x000000000000007FL                            });

  public static final BitSet FOLLOW_condition_in_slot3612                                   = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000000L, 0x0800000000000000L                            });

  public static final BitSet FOLLOW_187_in_slot3614                                         = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0080000000000000L                                                 });

  public static final BitSet FOLLOW_STRING_TOKEN_in_slot3618                                = new BitSet(
                                                                                                new long[] {
      0x0000000000000000L, 0x0000000000000002L                                                 });

  public static final BitSet FOLLOW_SHORT_CLOSE_TOKEN_in_slot3657                           = new BitSet(
                                                                                                new long[] { 0x0000000000000002L });

}