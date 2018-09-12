package org.jactr.io.antlr3.misc;

/*
 * default logging
 */
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.io.misc.JACTRIOException;
import org.jactr.io.misc.JACTRIOException.Level;
import org.jactr.io.misc.JACTRIOException.Stage;

public class DefaultErrorReporter implements IErrorReporter
{

  private final URI                  _source;

  private List<JACTRIOException> _error;

  private List<JACTRIOException> _warn;

  private List<JACTRIOException> _info;

  private final Stage                _defaultStage;

  public DefaultErrorReporter(URI source, Stage stage)
  {
    _source = source;
    _defaultStage = stage;
    acquireCollections();
  }

  private void acquireCollections()
  {
    _error = FastListFactory.newInstance();
    _warn = FastListFactory.newInstance();
    _info = FastListFactory.newInstance();
  }

  private void releaseCollections()
  {
    FastListFactory.recycle(_error);
    FastListFactory.recycle(_warn);
    FastListFactory.recycle(_info);
  }

  public void reportError(Exception exception)
  {
    JACTRIOException io = null;
    if (exception instanceof JACTRIOException)
      io = (JACTRIOException) exception;
    else
    {
      io = new JACTRIOException(Level.ERROR, Stage.UNKNOWN, _source, exception
          .getMessage(), -1, -1, -1);
      io.initCause(exception);
    }

    store(io);
  }

  /**
   * directly from antlr
   */
  public void reportError(String message, RecognitionException exception)
  {
    Stage stage = _defaultStage;
    if (stage == null)
    {
      stage = Stage.UNKNOWN;
      if (exception.input instanceof CharStream)
        stage = Stage.LEXING;
      else if (exception.input instanceof TokenStream)
        stage = Stage.PARSING;
      else if (exception.input instanceof TreeNodeStream)
        stage = Stage.COMPILING;
    }

    Level level = Level.ERROR;
    int line = exception.line;
    int start = -1;
    int end = -1;
    
    if(stage==Stage.LEXING)
    {
      /*
       * start is the char position..
       */
      start = exception.index;
      end = start+1;
    }
    else if(stage==Stage.PARSING)
    {
      /*
       * token
       */
      Token token = exception.token;
      if(token instanceof CommonToken)
      {
        start = ((CommonToken)token).getStartIndex();
        end = ((CommonToken)token).getStopIndex();
      }
    }
    else if(stage==Stage.COMPILING || stage==Stage.BUILDING) /*
     * trees
     */
    if(exception.node instanceof DetailedCommonTree)
    {
      DetailedCommonTree tree = (DetailedCommonTree) exception.node;
      start = tree.getStartOffset();
      end = tree.getStopOffset();
    }
    
    JACTRIOException io = new JACTRIOException(level, stage, _source, message, line, start, end);
    io.initCause(exception);
    
    store(io);
  }

  protected void store(JACTRIOException exception)
  {
    switch (exception.getLevel())
    {
      case INFO:
        _info.add(exception);
        break;
      case WARN:
        _warn.add(exception);
        break;
      case ERROR:
        _error.add(exception);
        break;
    }
  }

  synchronized public void reset()
  {
    releaseCollections();
    acquireCollections();
  }
  
  synchronized public void get(Level level, Collection<JACTRIOException> container)
  {
    switch(level)
    {
      case INFO : container.addAll(_info); break;
      case WARN : container.addAll(_warn); break;
      case ERROR : container.addAll(_error); break;
    }
  }

}
