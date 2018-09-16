// $ANTLR 3.4 /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g 2011-11-15 08:38:55

package org.jactr.io.antlr3.parser.lisp;
import org.jactr.io.antlr3.parser.AbstractModelParser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class LispLexer extends Lexer {
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
    										


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public LispLexer() {} 
    public LispLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public LispLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g"; }

    // $ANTLR start "T__87"
    public final void mT__87() throws RecognitionException {
        try {
            int _type = T__87;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:32:7: ( '!' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:32:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__87"

    // $ANTLR start "T__88"
    public final void mT__88() throws RecognitionException {
        try {
            int _type = T__88;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:33:7: ( '!STOP!' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:33:9: '!STOP!'
            {
            match("!STOP!"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__88"

    // $ANTLR start "T__89"
    public final void mT__89() throws RecognitionException {
        try {
            int _type = T__89;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:34:7: ( '!output!' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:34:9: '!output!'
            {
            match("!output!"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__89"

    // $ANTLR start "T__90"
    public final void mT__90() throws RecognitionException {
        try {
            int _type = T__90;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:35:7: ( '!stop!' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:35:9: '!stop!'
            {
            match("!stop!"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__90"

    // $ANTLR start "T__91"
    public final void mT__91() throws RecognitionException {
        try {
            int _type = T__91;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:36:7: ( '==>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:36:9: '==>'
            {
            match("==>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__91"

    // $ANTLR start "T__92"
    public final void mT__92() throws RecognitionException {
        try {
            int _type = T__92;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:37:7: ( 'BIND' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:37:9: 'BIND'
            {
            match("BIND"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__92"

    // $ANTLR start "T__93"
    public final void mT__93() throws RecognitionException {
        try {
            int _type = T__93;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:38:7: ( 'EVAL' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:38:9: 'EVAL'
            {
            match("EVAL"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__93"

    // $ANTLR start "T__94"
    public final void mT__94() throws RecognitionException {
        try {
            int _type = T__94;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:39:7: ( 'P' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:39:9: 'P'
            {
            match('P'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__94"

    // $ANTLR start "T__95"
    public final void mT__95() throws RecognitionException {
        try {
            int _type = T__95;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:40:7: ( 'add-dm' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:40:9: 'add-dm'
            {
            match("add-dm"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__95"

    // $ANTLR start "T__96"
    public final void mT__96() throws RecognitionException {
        try {
            int _type = T__96;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:41:7: ( 'bind' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:41:9: 'bind'
            {
            match("bind"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__96"

    // $ANTLR start "T__97"
    public final void mT__97() throws RecognitionException {
        try {
            int _type = T__97;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:42:7: ( 'chunk-type' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:42:9: 'chunk-type'
            {
            match("chunk-type"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__97"

    // $ANTLR start "T__98"
    public final void mT__98() throws RecognitionException {
        try {
            int _type = T__98;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:43:7: ( 'clear-all' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:43:9: 'clear-all'
            {
            match("clear-all"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__98"

    // $ANTLR start "T__99"
    public final void mT__99() throws RecognitionException {
        try {
            int _type = T__99;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:44:7: ( 'define-model' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:44:9: 'define-model'
            {
            match("define-model"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__99"

    // $ANTLR start "T__100"
    public final void mT__100() throws RecognitionException {
        try {
            int _type = T__100;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:45:8: ( 'eval' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:45:10: 'eval'
            {
            match("eval"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__100"

    // $ANTLR start "T__101"
    public final void mT__101() throws RecognitionException {
        try {
            int _type = T__101;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:46:8: ( 'extension' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:46:10: 'extension'
            {
            match("extension"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__101"

    // $ANTLR start "T__102"
    public final void mT__102() throws RecognitionException {
        try {
            int _type = T__102;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:47:8: ( 'goal-focus' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:47:10: 'goal-focus'
            {
            match("goal-focus"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__102"

    // $ANTLR start "T__103"
    public final void mT__103() throws RecognitionException {
        try {
            int _type = T__103;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:48:8: ( 'import' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:48:10: 'import'
            {
            match("import"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__103"

    // $ANTLR start "T__104"
    public final void mT__104() throws RecognitionException {
        try {
            int _type = T__104;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:49:8: ( 'module' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:49:10: 'module'
            {
            match("module"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__104"

    // $ANTLR start "T__105"
    public final void mT__105() throws RecognitionException {
        try {
            int _type = T__105;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:50:8: ( 'p' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:50:10: 'p'
            {
            match('p'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__105"

    // $ANTLR start "T__106"
    public final void mT__106() throws RecognitionException {
        try {
            int _type = T__106;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:51:8: ( 'sdp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:51:10: 'sdp'
            {
            match("sdp"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__106"

    // $ANTLR start "T__107"
    public final void mT__107() throws RecognitionException {
        try {
            int _type = T__107;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:52:8: ( 'sgp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:52:10: 'sgp'
            {
            match("sgp"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__107"

    // $ANTLR start "T__108"
    public final void mT__108() throws RecognitionException {
        try {
            int _type = T__108;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:53:8: ( 'spp' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:53:10: 'spp'
            {
            match("spp"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__108"

    // $ANTLR start "REMOVE_TOKEN"
    public final void mREMOVE_TOKEN() throws RecognitionException {
        try {
            int _type = REMOVE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken id=null;

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1025:2: ( '-' id= IDENTIFIER_TOKEN '>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1025:4: '-' id= IDENTIFIER_TOKEN '>'
            {
            match('-'); 

            int idStart219 = getCharIndex();
            int idStartLine219 = getLine();
            int idStartCharPos219 = getCharPositionInLine();
            mIDENTIFIER_TOKEN(); 
            id = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, idStart219, getCharIndex()-1);
            id.setLine(idStartLine219);
            id.setCharPositionInLine(idStartCharPos219);


            match('>'); 

            setText((id!=null?id.getText():null));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REMOVE_TOKEN"

    // $ANTLR start "ADD_TOKEN"
    public final void mADD_TOKEN() throws RecognitionException {
        try {
            int _type = ADD_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken id=null;

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1028:2: ( '+' id= IDENTIFIER_TOKEN '>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1028:4: '+' id= IDENTIFIER_TOKEN '>'
            {
            match('+'); 

            int idStart236 = getCharIndex();
            int idStartLine236 = getLine();
            int idStartCharPos236 = getCharPositionInLine();
            mIDENTIFIER_TOKEN(); 
            id = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, idStart236, getCharIndex()-1);
            id.setLine(idStartLine236);
            id.setCharPositionInLine(idStartCharPos236);


            match('>'); 

            setText((id!=null?id.getText():null));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ADD_TOKEN"

    // $ANTLR start "QUERY_TOKEN"
    public final void mQUERY_TOKEN() throws RecognitionException {
        try {
            int _type = QUERY_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken id=null;

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1031:2: ( '?' id= IDENTIFIER_TOKEN '>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1031:4: '?' id= IDENTIFIER_TOKEN '>'
            {
            match('?'); 

            int idStart253 = getCharIndex();
            int idStartLine253 = getLine();
            int idStartCharPos253 = getCharPositionInLine();
            mIDENTIFIER_TOKEN(); 
            id = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, idStart253, getCharIndex()-1);
            id.setLine(idStartLine253);
            id.setCharPositionInLine(idStartCharPos253);


            match('>'); 

            setText((id!=null?id.getText():null));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUERY_TOKEN"

    // $ANTLR start "VARIABLE_TOKEN"
    public final void mVARIABLE_TOKEN() throws RecognitionException {
        try {
            int _type = VARIABLE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1035:2: ( '=' IDENTIFIER_TOKEN )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1035:4: '=' IDENTIFIER_TOKEN
            {
            match('='); 

            mIDENTIFIER_TOKEN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VARIABLE_TOKEN"

    // $ANTLR start "MATCH_TOKEN"
    public final void mMATCH_TOKEN() throws RecognitionException {
        try {
            int _type = MATCH_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken id=null;

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1038:2: ( '=' id= IDENTIFIER_TOKEN '>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1038:4: '=' id= IDENTIFIER_TOKEN '>'
            {
            match('='); 

            int idStart280 = getCharIndex();
            int idStartLine280 = getLine();
            int idStartCharPos280 = getCharPositionInLine();
            mIDENTIFIER_TOKEN(); 
            id = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, idStart280, getCharIndex()-1);
            id.setLine(idStartLine280);
            id.setCharPositionInLine(idStartCharPos280);


            match('>'); 

            setText((id!=null?id.getText():null));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MATCH_TOKEN"

    // $ANTLR start "ISA_TOKEN"
    public final void mISA_TOKEN() throws RecognitionException {
        try {
            int _type = ISA_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1041:11: ( ( ( 'i' | 'I' ) ( 's' | 'S' ) ( 'a' | 'A' ) ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1041:13: ( ( 'i' | 'I' ) ( 's' | 'S' ) ( 'a' | 'A' ) )
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1041:13: ( ( 'i' | 'I' ) ( 's' | 'S' ) ( 'a' | 'A' ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1041:14: ( 'i' | 'I' ) ( 's' | 'S' ) ( 'a' | 'A' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ISA_TOKEN"

    // $ANTLR start "NO_IMPORT_TOKEN"
    public final void mNO_IMPORT_TOKEN() throws RecognitionException {
        try {
            int _type = NO_IMPORT_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1044:2: ( ( 'n' | 'N' ) ( 'o' | 'O' ) '-' ( 'i' | 'I' ) ( 'm' | 'M' ) ( 'p' | 'P' ) ( 'o' | 'O' ) ( 'r' | 'R' ) ( 't' | 'T' ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1044:5: ( 'n' | 'N' ) ( 'o' | 'O' ) '-' ( 'i' | 'I' ) ( 'm' | 'M' ) ( 'p' | 'P' ) ( 'o' | 'O' ) ( 'r' | 'R' ) ( 't' | 'T' )
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            match('-'); 

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NO_IMPORT_TOKEN"

    // $ANTLR start "QUESTION_TOKEN"
    public final void mQUESTION_TOKEN() throws RecognitionException {
        try {
            int _type = QUESTION_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1046:16: ( '?' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1046:18: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUESTION_TOKEN"

    // $ANTLR start "EQUALS_TOKEN"
    public final void mEQUALS_TOKEN() throws RecognitionException {
        try {
            int _type = EQUALS_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1047:15: ( '=' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1047:17: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUALS_TOKEN"

    // $ANTLR start "PLUS_TOKEN"
    public final void mPLUS_TOKEN() throws RecognitionException {
        try {
            int _type = PLUS_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1048:13: ( '+' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1048:15: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PLUS_TOKEN"

    // $ANTLR start "MINUS_TOKEN"
    public final void mMINUS_TOKEN() throws RecognitionException {
        try {
            int _type = MINUS_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1049:14: ( '-' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1049:16: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MINUS_TOKEN"

    // $ANTLR start "INCLUDE_TOKEN"
    public final void mINCLUDE_TOKEN() throws RecognitionException {
        try {
            int _type = INCLUDE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1052:2: ( ':' ( 'i' | 'I' ) ( 'n' | 'N' ) ( 'c' | 'C' ) ( 'l' | 'L' ) ( 'u' | 'U' ) ( 'd' | 'D' ) ( 'e' | 'E' ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1052:4: ':' ( 'i' | 'I' ) ( 'n' | 'N' ) ( 'c' | 'C' ) ( 'l' | 'L' ) ( 'u' | 'U' ) ( 'd' | 'D' ) ( 'e' | 'E' )
            {
            match(':'); 

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INCLUDE_TOKEN"

    // $ANTLR start "GT_TOKEN"
    public final void mGT_TOKEN() throws RecognitionException {
        try {
            int _type = GT_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1054:11: ( '>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1054:13: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT_TOKEN"

    // $ANTLR start "GTE_TOKEN"
    public final void mGTE_TOKEN() throws RecognitionException {
        try {
            int _type = GTE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1055:12: ( '>=' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1055:14: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GTE_TOKEN"

    // $ANTLR start "LT_TOKEN"
    public final void mLT_TOKEN() throws RecognitionException {
        try {
            int _type = LT_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1056:10: ( '<' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1056:12: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT_TOKEN"

    // $ANTLR start "LTE_TOKEN"
    public final void mLTE_TOKEN() throws RecognitionException {
        try {
            int _type = LTE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1057:11: ( '<=' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1057:13: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LTE_TOKEN"

    // $ANTLR start "WITHIN_TOKEN"
    public final void mWITHIN_TOKEN() throws RecognitionException {
        try {
            int _type = WITHIN_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1058:15: ( '<>' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1058:17: '<>'
            {
            match("<>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WITHIN_TOKEN"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:2: ( ';' ( options {greedy=false; } : . )* ( '\\r' )? '\\n' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:4: ';' ( options {greedy=false; } : . )* ( '\\r' )? '\\n'
            {
            match(';'); 

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:8: ( options {greedy=false; } : . )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\r') ) {
                    alt1=2;
                }
                else if ( (LA1_0=='\n') ) {
                    alt1=2;
                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:35: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:39: ( '\\r' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1061:40: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1069:2: ( '#|' ( options {greedy=false; } : . )* '|#' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1069:4: '#|' ( options {greedy=false; } : . )* '|#'
            {
            match("#|"); 



            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1070:3: ( options {greedy=false; } : . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='|') ) {
                    int LA3_1 = input.LA(2);

                    if ( (LA3_1=='#') ) {
                        alt3=2;
                    }
                    else if ( ((LA3_1 >= '\u0000' && LA3_1 <= '\"')||(LA3_1 >= '$' && LA3_1 <= '\uFFFF')) ) {
                        alt3=1;
                    }


                }
                else if ( ((LA3_0 >= '\u0000' && LA3_0 <= '{')||(LA3_0 >= '}' && LA3_0 <= '\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1070:31: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match("|#"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "IDENTIFIER_TOKEN"
    public final void mIDENTIFIER_TOKEN() throws RecognitionException {
        try {
            int _type = IDENTIFIER_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1077:5: ( ( '_' | ':' | '*' | LETTER_TOKEN )+ ( DIGITS_TOKEN | LETTER_TOKEN | PUNCTUATION_FRAGMENT | '-' | '+' )* )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1077:7: ( '_' | ':' | '*' | LETTER_TOKEN )+ ( DIGITS_TOKEN | LETTER_TOKEN | PUNCTUATION_FRAGMENT | '-' | '+' )*
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1077:7: ( '_' | ':' | '*' | LETTER_TOKEN )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='*'||LA4_0==':'||(LA4_0 >= 'A' && LA4_0 <= 'Z')||LA4_0=='_'||(LA4_0 >= 'a' && LA4_0 <= 'z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            	    {
            	    if ( input.LA(1)=='*'||input.LA(1)==':'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1078:8: ( DIGITS_TOKEN | LETTER_TOKEN | PUNCTUATION_FRAGMENT | '-' | '+' )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= '$' && LA5_0 <= '&')||(LA5_0 >= '*' && LA5_0 <= ':')||(LA5_0 >= 'A' && LA5_0 <= 'Z')||(LA5_0 >= '^' && LA5_0 <= '_')||(LA5_0 >= 'a' && LA5_0 <= 'z')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            	    {
            	    if ( (input.LA(1) >= '$' && input.LA(1) <= '&')||(input.LA(1) >= '*' && input.LA(1) <= ':')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= '^' && input.LA(1) <= '_')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IDENTIFIER_TOKEN"

    // $ANTLR start "PUNCTUATION_FRAGMENT"
    public final void mPUNCTUATION_FRAGMENT() throws RecognitionException {
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1091:2: ( ( '_' | ':' | '*' | '.' | ',' | '$' | '^' | '&' | '%' | '/' ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            {
            if ( (input.LA(1) >= '$' && input.LA(1) <= '&')||input.LA(1)=='*'||input.LA(1)==','||(input.LA(1) >= '.' && input.LA(1) <= '/')||input.LA(1)==':'||(input.LA(1) >= '^' && input.LA(1) <= '_') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PUNCTUATION_FRAGMENT"

    // $ANTLR start "STRING_TOKEN"
    public final void mSTRING_TOKEN() throws RecognitionException {
        try {
            int _type = STRING_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1093:5: ( '\\\"' (~ ( '\\\"' | '\\\\' ) | ESCAPE_TOKEN )* '\\\"' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1094:7: '\\\"' (~ ( '\\\"' | '\\\\' ) | ESCAPE_TOKEN )* '\\\"'
            {
            match('\"'); 

            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1095:7: (~ ( '\\\"' | '\\\\' ) | ESCAPE_TOKEN )*
            loop6:
            do {
                int alt6=3;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= '\u0000' && LA6_0 <= '!')||(LA6_0 >= '#' && LA6_0 <= '[')||(LA6_0 >= ']' && LA6_0 <= '\uFFFF')) ) {
                    alt6=1;
                }
                else if ( (LA6_0=='\\') ) {
                    alt6=2;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1095:9: ~ ( '\\\"' | '\\\\' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1095:24: ESCAPE_TOKEN
            	    {
            	    mESCAPE_TOKEN(); 


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            match('\"'); 


                   String str = getText();
                   setText(str.substring(1, str.length()-1));
                  

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_TOKEN"

    // $ANTLR start "ESCAPE_TOKEN"
    public final void mESCAPE_TOKEN() throws RecognitionException {
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1105:5: ( '\\\\' 'b' | '\\\\' 't' | '\\\\' 'n' | '\\\\' 'f' | '\\\\' 'r' | '\\\\' '\\\"' | '\\\\' '\\'' | '\\\\' '\\\\' )
            int alt7=8;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'b':
                    {
                    alt7=1;
                    }
                    break;
                case 't':
                    {
                    alt7=2;
                    }
                    break;
                case 'n':
                    {
                    alt7=3;
                    }
                    break;
                case 'f':
                    {
                    alt7=4;
                    }
                    break;
                case 'r':
                    {
                    alt7=5;
                    }
                    break;
                case '\"':
                    {
                    alt7=6;
                    }
                    break;
                case '\'':
                    {
                    alt7=7;
                    }
                    break;
                case '\\':
                    {
                    alt7=8;
                    }
                    break;
                default:
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
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1105:7: '\\\\' 'b'
                    {
                    match('\\'); 

                    match('b'); 

                    }
                    break;
                case 2 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1106:9: '\\\\' 't'
                    {
                    match('\\'); 

                    match('t'); 

                    }
                    break;
                case 3 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1107:9: '\\\\' 'n'
                    {
                    match('\\'); 

                    match('n'); 

                    }
                    break;
                case 4 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1108:9: '\\\\' 'f'
                    {
                    match('\\'); 

                    match('f'); 

                    }
                    break;
                case 5 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1109:9: '\\\\' 'r'
                    {
                    match('\\'); 

                    match('r'); 

                    }
                    break;
                case 6 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1110:9: '\\\\' '\\\"'
                    {
                    match('\\'); 

                    match('\"'); 

                    }
                    break;
                case 7 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1111:9: '\\\\' '\\''
                    {
                    match('\\'); 

                    match('\''); 

                    }
                    break;
                case 8 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1112:9: '\\\\' '\\\\'
                    {
                    match('\\'); 

                    match('\\'); 

                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESCAPE_TOKEN"

    // $ANTLR start "WS_TOKEN"
    public final void mWS_TOKEN() throws RecognitionException {
        try {
            int _type = WS_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1114:10: ( ( ' ' | '\\t' | ( '\\n' | '\\r' ) )+ )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1114:12: ( ' ' | '\\t' | ( '\\n' | '\\r' ) )+
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1114:12: ( ' ' | '\\t' | ( '\\n' | '\\r' ) )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||LA8_0=='\r'||LA8_0==' ') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


             _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS_TOKEN"

    // $ANTLR start "NUMBER_TOKEN"
    public final void mNUMBER_TOKEN() throws RecognitionException {
        try {
            int _type = NUMBER_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:2: ( ( '-' )? ( DIGITS_TOKEN )* ( '.' ( DIGITS_TOKEN )+ )? )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:5: ( '-' )? ( DIGITS_TOKEN )* ( '.' ( DIGITS_TOKEN )+ )?
            {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:5: ( '-' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='-') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:6: '-'
                    {
                    match('-'); 

                    }
                    break;

            }


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:12: ( DIGITS_TOKEN )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:26: ( '.' ( DIGITS_TOKEN )+ )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='.') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:27: '.' ( DIGITS_TOKEN )+
                    {
                    match('.'); 

                    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1125:31: ( DIGITS_TOKEN )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0 >= '0' && LA11_0 <= '9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


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


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NUMBER_TOKEN"

    // $ANTLR start "LETTER_TOKEN"
    public final void mLETTER_TOKEN() throws RecognitionException {
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1127:23: ( 'a' .. 'z' | 'A' .. 'Z' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LETTER_TOKEN"

    // $ANTLR start "DIGITS_TOKEN"
    public final void mDIGITS_TOKEN() throws RecognitionException {
        try {
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1131:23: ( ( '0' .. '9' ) )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIGITS_TOKEN"

    // $ANTLR start "OPEN_TOKEN"
    public final void mOPEN_TOKEN() throws RecognitionException {
        try {
            int _type = OPEN_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1134:2: ( '(' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1134:4: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPEN_TOKEN"

    // $ANTLR start "CLOSE_TOKEN"
    public final void mCLOSE_TOKEN() throws RecognitionException {
        try {
            int _type = CLOSE_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1137:2: ( ')' )
            // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1137:4: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CLOSE_TOKEN"

    public void mTokens() throws RecognitionException {
        // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:8: ( T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | T__93 | T__94 | T__95 | T__96 | T__97 | T__98 | T__99 | T__100 | T__101 | T__102 | T__103 | T__104 | T__105 | T__106 | T__107 | T__108 | REMOVE_TOKEN | ADD_TOKEN | QUERY_TOKEN | VARIABLE_TOKEN | MATCH_TOKEN | ISA_TOKEN | NO_IMPORT_TOKEN | QUESTION_TOKEN | EQUALS_TOKEN | PLUS_TOKEN | MINUS_TOKEN | INCLUDE_TOKEN | GT_TOKEN | GTE_TOKEN | LT_TOKEN | LTE_TOKEN | WITHIN_TOKEN | SL_COMMENT | ML_COMMENT | IDENTIFIER_TOKEN | STRING_TOKEN | WS_TOKEN | NUMBER_TOKEN | OPEN_TOKEN | CLOSE_TOKEN )
        int alt13=47;
        alt13 = dfa13.predict(input);
        switch (alt13) {
            case 1 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:10: T__87
                {
                mT__87(); 


                }
                break;
            case 2 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:16: T__88
                {
                mT__88(); 


                }
                break;
            case 3 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:22: T__89
                {
                mT__89(); 


                }
                break;
            case 4 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:28: T__90
                {
                mT__90(); 


                }
                break;
            case 5 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:34: T__91
                {
                mT__91(); 


                }
                break;
            case 6 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:40: T__92
                {
                mT__92(); 


                }
                break;
            case 7 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:46: T__93
                {
                mT__93(); 


                }
                break;
            case 8 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:52: T__94
                {
                mT__94(); 


                }
                break;
            case 9 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:58: T__95
                {
                mT__95(); 


                }
                break;
            case 10 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:64: T__96
                {
                mT__96(); 


                }
                break;
            case 11 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:70: T__97
                {
                mT__97(); 


                }
                break;
            case 12 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:76: T__98
                {
                mT__98(); 


                }
                break;
            case 13 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:82: T__99
                {
                mT__99(); 


                }
                break;
            case 14 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:88: T__100
                {
                mT__100(); 


                }
                break;
            case 15 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:95: T__101
                {
                mT__101(); 


                }
                break;
            case 16 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:102: T__102
                {
                mT__102(); 


                }
                break;
            case 17 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:109: T__103
                {
                mT__103(); 


                }
                break;
            case 18 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:116: T__104
                {
                mT__104(); 


                }
                break;
            case 19 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:123: T__105
                {
                mT__105(); 


                }
                break;
            case 20 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:130: T__106
                {
                mT__106(); 


                }
                break;
            case 21 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:137: T__107
                {
                mT__107(); 


                }
                break;
            case 22 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:144: T__108
                {
                mT__108(); 


                }
                break;
            case 23 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:151: REMOVE_TOKEN
                {
                mREMOVE_TOKEN(); 


                }
                break;
            case 24 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:164: ADD_TOKEN
                {
                mADD_TOKEN(); 


                }
                break;
            case 25 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:174: QUERY_TOKEN
                {
                mQUERY_TOKEN(); 


                }
                break;
            case 26 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:186: VARIABLE_TOKEN
                {
                mVARIABLE_TOKEN(); 


                }
                break;
            case 27 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:201: MATCH_TOKEN
                {
                mMATCH_TOKEN(); 


                }
                break;
            case 28 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:213: ISA_TOKEN
                {
                mISA_TOKEN(); 


                }
                break;
            case 29 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:223: NO_IMPORT_TOKEN
                {
                mNO_IMPORT_TOKEN(); 


                }
                break;
            case 30 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:239: QUESTION_TOKEN
                {
                mQUESTION_TOKEN(); 


                }
                break;
            case 31 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:254: EQUALS_TOKEN
                {
                mEQUALS_TOKEN(); 


                }
                break;
            case 32 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:267: PLUS_TOKEN
                {
                mPLUS_TOKEN(); 


                }
                break;
            case 33 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:278: MINUS_TOKEN
                {
                mMINUS_TOKEN(); 


                }
                break;
            case 34 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:290: INCLUDE_TOKEN
                {
                mINCLUDE_TOKEN(); 


                }
                break;
            case 35 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:304: GT_TOKEN
                {
                mGT_TOKEN(); 


                }
                break;
            case 36 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:313: GTE_TOKEN
                {
                mGTE_TOKEN(); 


                }
                break;
            case 37 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:323: LT_TOKEN
                {
                mLT_TOKEN(); 


                }
                break;
            case 38 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:332: LTE_TOKEN
                {
                mLTE_TOKEN(); 


                }
                break;
            case 39 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:342: WITHIN_TOKEN
                {
                mWITHIN_TOKEN(); 


                }
                break;
            case 40 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:355: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 41 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:366: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 42 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:377: IDENTIFIER_TOKEN
                {
                mIDENTIFIER_TOKEN(); 


                }
                break;
            case 43 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:394: STRING_TOKEN
                {
                mSTRING_TOKEN(); 


                }
                break;
            case 44 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:407: WS_TOKEN
                {
                mWS_TOKEN(); 


                }
                break;
            case 45 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:416: NUMBER_TOKEN
                {
                mNUMBER_TOKEN(); 


                }
                break;
            case 46 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:429: OPEN_TOKEN
                {
                mOPEN_TOKEN(); 


                }
                break;
            case 47 :
                // /Users/lahiatt/Shared_Documents/Work/NRL-PD/dev-workspace/org.jactr.io/src/org/jactr/io/antlr3/parser/lisp/Lisp.g:1:440: CLOSE_TOKEN
                {
                mCLOSE_TOKEN(); 


                }
                break;

        }

    }


    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA13_eotS =
        "\1\35\1\43\1\46\2\32\1\51\10\32\1\65\1\32\1\72\1\74\1\76\3\32\1"+
        "\102\1\105\15\uffff\1\107\1\uffff\2\32\1\uffff\13\32\1\uffff\3\32"+
        "\6\uffff\2\32\5\uffff\1\107\1\uffff\1\107\1\uffff\13\32\1\147\1"+
        "\32\1\151\1\152\1\153\2\32\1\156\1\157\1\32\1\161\3\32\1\165\3\32"+
        "\1\uffff\1\32\3\uffff\2\32\2\uffff\1\32\1\uffff\3\32\1\uffff\6\32"+
        "\1\u0086\5\32\1\u008c\1\u008d\2\32\1\uffff\5\32\2\uffff\10\32\1"+
        "\u009d\1\32\1\u009f\1\32\1\u00a1\1\32\1\u00a3\1\uffff\1\u00a4\1"+
        "\uffff\1\32\1\uffff\1\u00a6\2\uffff\1\32\1\uffff\1\u00a8\1\uffff";
    static final String DFA13_eofS =
        "\u00a9\uffff";
    static final String DFA13_minS =
        "\1\11\1\123\1\52\1\111\1\126\1\44\1\144\1\151\1\150\1\145\1\166"+
        "\1\157\1\123\1\157\1\44\1\144\3\52\1\123\1\117\1\111\2\75\15\uffff"+
        "\1\44\1\uffff\1\116\1\101\1\uffff\1\144\1\156\1\165\1\145\1\146"+
        "\1\141\1\164\1\141\1\160\1\101\1\144\1\uffff\3\160\6\uffff\1\55"+
        "\1\116\5\uffff\1\44\1\uffff\1\44\1\uffff\1\104\1\114\1\55\1\144"+
        "\1\156\1\141\1\151\1\154\1\145\1\154\1\157\1\44\1\165\3\44\1\111"+
        "\1\103\2\44\1\144\1\44\1\153\1\162\1\156\1\44\1\156\1\55\1\162\1"+
        "\uffff\1\154\3\uffff\1\115\1\114\2\uffff\1\155\1\uffff\2\55\1\145"+
        "\1\uffff\1\163\1\146\1\164\1\145\1\120\1\125\1\44\1\164\1\141\1"+
        "\55\1\151\1\157\2\44\1\117\1\104\1\uffff\1\171\1\154\1\155\1\157"+
        "\1\143\2\uffff\1\122\1\105\1\160\1\154\1\157\1\156\1\165\1\124\1"+
        "\44\1\145\1\44\1\144\1\44\1\163\1\44\1\uffff\1\44\1\uffff\1\145"+
        "\1\uffff\1\44\2\uffff\1\154\1\uffff\1\44\1\uffff";
    static final String DFA13_maxS =
        "\1\172\1\163\1\172\1\111\1\126\1\172\1\144\1\151\1\154\1\145\1\170"+
        "\1\157\1\163\1\157\1\172\1\160\3\172\1\163\1\157\1\151\1\75\1\76"+
        "\15\uffff\1\172\1\uffff\1\116\1\101\1\uffff\1\144\1\156\1\165\1"+
        "\145\1\146\1\141\1\164\1\141\1\160\1\141\1\144\1\uffff\3\160\6\uffff"+
        "\1\55\1\156\5\uffff\1\172\1\uffff\1\172\1\uffff\1\104\1\114\1\55"+
        "\1\144\1\156\1\141\1\151\1\154\1\145\1\154\1\157\1\172\1\165\3\172"+
        "\1\151\1\143\2\172\1\144\1\172\1\153\1\162\1\156\1\172\1\156\1\55"+
        "\1\162\1\uffff\1\154\3\uffff\1\155\1\154\2\uffff\1\155\1\uffff\2"+
        "\55\1\145\1\uffff\1\163\1\146\1\164\1\145\1\160\1\165\1\172\1\164"+
        "\1\141\1\55\1\151\1\157\2\172\1\157\1\144\1\uffff\1\171\1\154\1"+
        "\155\1\157\1\143\2\uffff\1\162\1\145\1\160\1\154\1\157\1\156\1\165"+
        "\1\164\1\172\1\145\1\172\1\144\1\172\1\163\1\172\1\uffff\1\172\1"+
        "\uffff\1\145\1\uffff\1\172\2\uffff\1\154\1\uffff\1\172\1\uffff";
    static final String DFA13_acceptS =
        "\30\uffff\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\2\1\3\1\4\1"+
        "\1\1\5\1\uffff\1\37\2\uffff\1\10\13\uffff\1\23\3\uffff\1\27\1\41"+
        "\1\30\1\40\1\31\1\36\2\uffff\1\44\1\43\1\46\1\47\1\45\1\uffff\1"+
        "\32\1\uffff\1\33\35\uffff\1\34\1\uffff\1\24\1\25\1\26\2\uffff\1"+
        "\6\1\7\1\uffff\1\12\3\uffff\1\16\20\uffff\1\11\5\uffff\1\21\1\22"+
        "\17\uffff\1\42\1\uffff\1\14\1\uffff\1\17\1\uffff\1\35\1\13\1\uffff"+
        "\1\20\1\uffff\1\15";
    static final String DFA13_specialS =
        "\u00a9\uffff}>";
    static final String[] DFA13_transitionS = {
            "\2\34\2\uffff\1\34\22\uffff\1\34\1\1\1\33\1\31\4\uffff\1\36"+
            "\1\37\1\32\1\21\1\uffff\1\20\14\uffff\1\25\1\30\1\27\1\2\1\26"+
            "\1\22\1\uffff\1\32\1\3\2\32\1\4\3\32\1\23\4\32\1\24\1\32\1\5"+
            "\12\32\4\uffff\1\32\1\uffff\1\6\1\7\1\10\1\11\1\12\1\32\1\13"+
            "\1\32\1\14\3\32\1\15\1\24\1\32\1\16\2\32\1\17\7\32",
            "\1\40\33\uffff\1\41\3\uffff\1\42",
            "\1\45\17\uffff\1\45\2\uffff\1\44\3\uffff\32\45\4\uffff\1\45"+
            "\1\uffff\32\45",
            "\1\47",
            "\1\50",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\52",
            "\1\53",
            "\1\54\3\uffff\1\55",
            "\1\56",
            "\1\57\1\uffff\1\60",
            "\1\61",
            "\1\63\31\uffff\1\62\5\uffff\1\63",
            "\1\64",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\66\2\uffff\1\67\10\uffff\1\70",
            "\1\71\3\uffff\1\35\1\uffff\12\35\1\71\6\uffff\32\71\4\uffff"+
            "\1\71\1\uffff\32\71",
            "\1\73\17\uffff\1\73\6\uffff\32\73\4\uffff\1\73\1\uffff\32\73",
            "\1\75\17\uffff\1\75\6\uffff\32\75\4\uffff\1\75\1\uffff\32\75",
            "\1\63\37\uffff\1\63",
            "\1\77\37\uffff\1\77",
            "\1\100\37\uffff\1\100",
            "\1\101",
            "\1\103\1\104",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\110\3\uffff\1\106\17\110\1\106\3\uffff\1\111\2\uffff\32"+
            "\106\3\uffff\1\110\1\106\1\uffff\32\106",
            "",
            "\1\112",
            "\1\113",
            "",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125\37\uffff\1\125",
            "\1\126",
            "",
            "\1\127",
            "\1\130",
            "\1\131",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\132",
            "\1\133\37\uffff\1\133",
            "",
            "",
            "",
            "",
            "",
            "\3\110\3\uffff\1\106\17\110\1\106\3\uffff\1\111\2\uffff\32"+
            "\106\3\uffff\1\110\1\106\1\uffff\32\106",
            "",
            "\3\110\3\uffff\21\110\3\uffff\1\111\2\uffff\32\110\3\uffff"+
            "\2\110\1\uffff\32\110",
            "",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\150",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\154\37\uffff\1\154",
            "\1\155\37\uffff\1\155",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\160",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\162",
            "\1\163",
            "\1\164",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\166",
            "\1\167",
            "\1\170",
            "",
            "\1\171",
            "",
            "",
            "",
            "\1\172\37\uffff\1\172",
            "\1\173\37\uffff\1\173",
            "",
            "",
            "\1\174",
            "",
            "\1\175",
            "\1\176",
            "\1\177",
            "",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\u0084\37\uffff\1\u0084",
            "\1\u0085\37\uffff\1\u0085",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\u008e\37\uffff\1\u008e",
            "\1\u008f\37\uffff\1\u008f",
            "",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "",
            "",
            "\1\u0095\37\uffff\1\u0095",
            "\1\u0096\37\uffff\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c\37\uffff\1\u009c",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\u009e",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\u00a0",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "\1\u00a2",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "",
            "\1\u00a5",
            "",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            "",
            "",
            "\1\u00a7",
            "",
            "\3\32\3\uffff\21\32\6\uffff\32\32\3\uffff\2\32\1\uffff\32\32",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | T__93 | T__94 | T__95 | T__96 | T__97 | T__98 | T__99 | T__100 | T__101 | T__102 | T__103 | T__104 | T__105 | T__106 | T__107 | T__108 | REMOVE_TOKEN | ADD_TOKEN | QUERY_TOKEN | VARIABLE_TOKEN | MATCH_TOKEN | ISA_TOKEN | NO_IMPORT_TOKEN | QUESTION_TOKEN | EQUALS_TOKEN | PLUS_TOKEN | MINUS_TOKEN | INCLUDE_TOKEN | GT_TOKEN | GTE_TOKEN | LT_TOKEN | LTE_TOKEN | WITHIN_TOKEN | SL_COMMENT | ML_COMMENT | IDENTIFIER_TOKEN | STRING_TOKEN | WS_TOKEN | NUMBER_TOKEN | OPEN_TOKEN | CLOSE_TOKEN );";
        }
    }
 

}