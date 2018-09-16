/*
 * Created on Apr 6, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.io.antlr3.parser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.parser.CanceledException;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.IParserImportDelegate;
import org.jactr.io.parser.ITreeTracker;
import org.jactr.io.parser.ParserImportDelegateFactory;

/**
 * @author developer
 */
public abstract class AbstractModelParser implements IModelParser
{

  static private final transient Log               LOGGER            = LogFactory
                                                                         .getLog(AbstractModelParser.class);

  static protected TokenStream                     NULL_TOKEN_STREAM = new TokenStream() {

                                                                       public Token LT(
                                                                           int arg0)
                                                                       {
                                                                         return null;
                                                                       }

                                                                       public Token get(
                                                                           int arg0)
                                                                       {
                                                                         return null;
                                                                       }

                                                                       public TokenSource getTokenSource()
                                                                       {
                                                                         return null;
                                                                       }

                                                                       public String toString(
                                                                           int arg0,
                                                                           int arg1)
                                                                       {
                                                                         return null;
                                                                       }

                                                                       public String toString(
                                                                           Token arg0,
                                                                           Token arg1)
                                                                       {
                                                                         return null;
                                                                       }

                                                                       public int LA(
                                                                           int arg0)
                                                                       {
                                                                         return 0;
                                                                       }

                                                                       public void consume()
                                                                       {
                                                                       }

                                                                       public int index()
                                                                       {
                                                                         return 0;
                                                                       }

                                                                       public int mark()
                                                                       {
                                                                         return 0;
                                                                       }

                                                                       public void release(
                                                                           int arg0)
                                                                       {
                                                                       }

                                                                       public void rewind()
                                                                       {
                                                                       }

                                                                       public void rewind(
                                                                           int arg0)
                                                                       {

                                                                       }

                                                                       public void seek(
                                                                           int arg0)
                                                                       {

                                                                       }

                                                                       public int size()
                                                                       {
                                                                         return 0;
                                                                       }

                                                                      public String getSourceName()
                                                                      {
                                                                        return null;
                                                                      }

                                                                     };

  protected URL                                    _url;

  protected CommonTree                             _modelTree;

  protected Collection<Exception>                  _errors;

  protected Collection<Exception>                  _warnings;

  protected IParserImportDelegate                  _delegate         = ParserImportDelegateFactory
                                                                         .createDelegate((Object[]) null);

  protected CharStream                             _inputStream;

  protected Lexer                                  _lexer;

  protected Parser                                 _parser;

  protected Map<Integer, Collection<ITreeTracker>> _treeTrackers;

  /**
   * 
   */
  public AbstractModelParser()
  {
    super();
    _lexer = createLexer();
    _parser = createParser();
  }

  synchronized public void addTreeTracker(ITreeTracker tracker)
  {
    if (_treeTrackers == null)
      _treeTrackers = new TreeMap<Integer, Collection<ITreeTracker>>();

    for (int type : tracker.getRelevantTypes())
    {
      Collection<ITreeTracker> trackers = _treeTrackers.get(type);
      if (trackers == null)
      {
        trackers = new ArrayList<ITreeTracker>();
        _treeTrackers.put(type, trackers);
      }
      trackers.add(tracker);
    }
  }

  synchronized public void removeTreeTracker(ITreeTracker tracker)
  {
    if (_treeTrackers == null) return;

    for (int type : tracker.getRelevantTypes())
    {
      Collection<ITreeTracker> trackers = _treeTrackers.get(type);
      if (trackers != null)
      {
        trackers.remove(tracker);
        if (trackers.size() == 0) _treeTrackers.remove(type);
      }
    }
  }

  synchronized public Collection<ITreeTracker> getTreeTrackers()
  {
    if (_treeTrackers == null) return Collections.EMPTY_LIST;

    HashSet<ITreeTracker> trackers = new HashSet<ITreeTracker>();
    for (Collection<ITreeTracker> tracker : _treeTrackers.values())
      trackers.addAll(tracker);

    return trackers;
  }

  synchronized public void delegate(CommonTree tree)
  {
    if (_treeTrackers == null) return;

    Collection<ITreeTracker> trackers = _treeTrackers.get(tree.getType());
    if (trackers != null) for (ITreeTracker tracker : trackers)
      tracker.treeAssembled(tree);
  }

  public CommonTree getDocumentTree()
  {
    return _modelTree;
  }

  public Collection<Exception> getParseErrors()
  {
    if (_errors == null) return Collections.EMPTY_LIST;
    return Collections.unmodifiableCollection(_errors);
  }

  public Collection<Exception> getParseWarnings()
  {
    if (_warnings == null) return Collections.EMPTY_LIST;
    return Collections.unmodifiableCollection(_warnings);
  }

  public void setInput(URL url) throws IOException
  {
    setBaseURL(url);
    setInput(new ANTLRInputStream(url.openStream()));
  }

  public void setBaseURL(URL url)
  {
    _url = url;
  }

  public URL getBaseURL()
  {
    return _url;
  }

  /**
   * @see org.jactr.io.parser.IModelParser#getImportDelegate()
   */
  public IParserImportDelegate getImportDelegate()
  {
    return _delegate;
  }

  /**
   * @see org.jactr.io.parser.IModelParser#setImportDelegate(org.jactr.io.parser.IParserImportDelegate)
   */
  public void setImportDelegate(IParserImportDelegate delegate)
  {
    _delegate = delegate;
  }

  /**
   * @see org.jactr.io.parser.IModelParser#setInput(java.lang.String)
   */
  public void setInput(String content) throws IOException
  {
    setInput(new ANTLRStringStream(content));
  }

  public void setInput(CharStream antlrStream)
  {
    _inputStream = antlrStream;
  }

  public boolean parse()
  {
    reset();

    getTreeTrackers();

    _lexer.setCharStream(_inputStream);
    CommonTokenStream tokens = new CommonTokenStream(_lexer);
    _parser.setTokenStream(tokens);

    preParse(_parser);
    try
    {
      _modelTree = parseInternal(_parser);
    }
    catch (CanceledException pce)
    {
      throw pce;
    }
    catch (Exception re)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Error while parsing ", re);
      reportException(re, false);
    }
    finally
    {
      postParse(_parser);
    }

    if (_errors != null) return _errors.size() == 0;
    return true;
  }

  public void reset()
  {
    if (_lexer != null) _lexer.reset();

    if (_parser != null) _parser.reset();

    _modelTree = null;

    _errors = null;
    _warnings = null;

    _delegate.reset();
  }

  public void dispose()
  {
    reset();
  }

  abstract protected Lexer createLexer();

  abstract protected Parser createParser();

  abstract protected CommonTree parseInternal(Parser parser)
      throws RecognitionException;

  synchronized protected void preParse(Parser parser)
  {
    if (_treeTrackers == null) return;
    HashSet<ITreeTracker> fired = new HashSet<ITreeTracker>();
    for (Map.Entry<Integer, Collection<ITreeTracker>> entry : _treeTrackers
        .entrySet())
      for (ITreeTracker tracker : entry.getValue())
        if (!fired.contains(tracker))
        {
          fired.add(tracker);
          tracker.preParse();
        }
  }

  synchronized protected void postParse(Parser parser)
  {
    if (_treeTrackers == null) return;

    boolean errors = _errors!=null && _errors.size()>0;
    HashSet<ITreeTracker> fired = new HashSet<ITreeTracker>();
    for (Map.Entry<Integer, Collection<ITreeTracker>> entry : _treeTrackers
        .entrySet())
      for (ITreeTracker tracker : entry.getValue())
        if (!fired.contains(tracker))
        {
          fired.add(tracker);
          tracker.postParse(errors);
        }
  }

  public void reportException(Exception e, boolean isLexing)
  {
    if (e instanceof RecognitionException)
    {
      RecognitionException re = (RecognitionException) e;
      String message = null;
      if (!isLexing)
        message = _parser.getErrorMessage(re, _parser.getTokenNames());
      else
        message = _lexer.getErrorMessage(re, _lexer.getTokenNames());

      if (message != null)
      {
        /*
         * create a new BS exception with a more meaningful message
         */
        final String eMessage = message;
        RecognitionException nre = new RecognitionException() {
          @Override
          public String getMessage()
          {
            return eMessage;
          }
        };

        nre.c = re.c;
        nre.charPositionInLine = re.charPositionInLine;
        nre.index = re.index;
        nre.line = re.line;
        nre.node = re.node;
        nre.token = re.token;

        e = nre;
      }
    }

    if (e instanceof CompilationWarning)
    {
      if (_warnings == null) _warnings = new ArrayList<Exception>();
      _warnings.add(e);
    }
    else
    {
      if (_errors == null) _errors = new ArrayList<Exception>();
      _errors.add(e);
    }
  }
}
