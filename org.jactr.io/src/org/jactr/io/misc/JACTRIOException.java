package org.jactr.io.misc;

/*
 * default logging
 */
import java.net.URI;

public class JACTRIOException extends RuntimeException
{
  /**
   * 
   */
  private static final long serialVersionUID = -6123564846510756287L;
  
  static public enum Level {INFO,WARN,ERROR};
  static public enum Stage {LEXING,PARSING,COMPILING,BUILDING, UNKNOWN};

  private final Level _level;
  private final Stage _stage;
  private final URI _source;
  
  private final int _line;
  private final long _startOffset;
  private final long _endOffset;
  
  public JACTRIOException(Level level, Stage stage, URI source, String message, int line, long startOffset, long endOffset)
  {
    super(message);
    _level = level;
    _stage = stage;
    _source= source;
    _line = line;
    _startOffset = startOffset;
    _endOffset = endOffset;
  }
  
  public Level getLevel()
  {
    return _level;
  }
  
  public Stage getStage()
  {
    return _stage;
  }
  
  public int getLine()
  {
    return _line;
  }
  
  public long getStartOffset()
  {
    return _startOffset;
  }
  
  public long getEndOffset()
  {
    return _endOffset;
  }
  
  public URI getSource()
  {
    return _source;
  }
  
  @Override
  public Throwable fillInStackTrace()
  {
    return this;
  }
}
