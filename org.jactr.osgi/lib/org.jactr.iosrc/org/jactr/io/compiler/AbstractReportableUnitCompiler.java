package org.jactr.io.compiler;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.compiler.CompilationError;
import org.jactr.io.antlr3.compiler.CompilationInfo;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.misc.CommonTreeException;
import org.jactr.io.compiler.IReportableUnitCompiler.Level;

/**
 * basic abstract {@link IReportableUnitCompiler}. Clients should extend and implement {@link #compile(CommonTree)}
 * from which you will call {@link #report(String, CommonTree)} with any messages. The actual message
 * and its reporting back to the {@link DefaultCompiler} will be handled based on {@link Level}.</br>
 * </br>
 * This class is not thread safe.
 * @author harrison
 *
 */
public abstract class AbstractReportableUnitCompiler implements
    IReportableUnitCompiler
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractReportableUnitCompiler.class);

  private Level                      _level;
  private Collection<Exception> _errors;
  private Collection<Exception> _warnings;
  private Collection<Exception> _infos;
  private Collection<Integer> _types = new TreeSet<Integer>();
  

  protected void setRelevantTypes(Integer ... types)
  {
    _types.addAll(Arrays.asList(types));
  }
  
  final public Collection<Integer> getRelevantTypes()
  {
    return _types;
  }
  
  public Level getReportLevel()
  {
    return _level;
  }

  public void setReportLevel(Level level)
  {
    _level = level;
  }

  final public void compile(CommonTree node, Collection<Exception> info, Collection<Exception> warnings,
      Collection<Exception> errors)
  {
    _errors = errors;
    _warnings = warnings;
    _infos = info;
    
    compile(node);
    
    _errors = null;
    _warnings = null;
    _infos = null;
  }
  
  protected Exception report(String message, CommonTree node)
  {
    return report(message, node, null);
  }

  protected CommonTreeException report(String message, CommonTree node, Throwable thrown)
  {
    CommonTreeException e = null;
    switch(getReportLevel())
    {
      case INFO : e = new CompilationInfo(message, node, thrown);
                  _infos.add(e);
                  break;
      case WARN : e = new CompilationWarning(message, node, thrown);
                  _warnings.add(e);
                  break;
      case ERROR :e =  new CompilationError(message, node, thrown);
                  _errors.add(e);
                  break;
    }
    
    return e;
  }
  
  protected Collection<Exception> getCollection(Level level)
  {
    switch(level)
    {
      case INFO : return _infos;
      case WARN : return _warnings;
      case ERROR : return _errors;
    }
    return null;
  }

  /**
   * do the actual compilation checks. the reporting of any issues should be
   * done via {@link #report(String, CommonTree)}, which will format the appropriate
   * message and store it in the correct collection.
   * 
   * @param node
   */
  abstract protected void compile(CommonTree node);

  /**
   * noop
   * 
   * @see org.jactr.io.compiler.IUnitCompiler#postCompile()
   */
  public void postCompile()
  {

  }

  /**
   * noop
   * 
   * @see org.jactr.io.compiler.IUnitCompiler#preCompile()
   */
  public void preCompile()
  {

  }

}
