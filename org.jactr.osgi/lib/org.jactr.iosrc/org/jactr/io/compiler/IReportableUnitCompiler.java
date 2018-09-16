package org.jactr.io.compiler;

/*
 * default logging
 */

public interface IReportableUnitCompiler extends IUnitCompiler
{

  static public enum Level {IGNORE, INFO, WARN, ERROR};
  
  
  public void setReportLevel(Level level);
  
  public Level getReportLevel();
}
