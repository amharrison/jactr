package org.jactr.io.parser;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParserImportDelegateFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ParserImportDelegateFactory.class);

  static private IParserImportDelegateFactoryImpl _factory;

  static public IParserImportDelegate createDelegate(Object... params)
  {
    return _factory.createDelegate(params);
  }

  static public void setFactoryImpl(IParserImportDelegateFactoryImpl impl)
  {
    _factory = impl;
  }

  static
  {
    setFactoryImpl(new DefaultFactoryImpl());
  }

  static public interface IParserImportDelegateFactoryImpl
  {
    public IParserImportDelegate createDelegate(Object... params);
  }

  static private class DefaultFactoryImpl implements
      IParserImportDelegateFactoryImpl
  {

    public IParserImportDelegate createDelegate(Object... params)
    {
      return new DefaultParserImportDelegate();
    }

  }
}
