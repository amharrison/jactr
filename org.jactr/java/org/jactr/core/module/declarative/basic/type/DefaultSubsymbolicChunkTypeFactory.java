package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.basic.BasicSubsymbolicChunkType;

public class DefaultSubsymbolicChunkTypeFactory implements
    ISubsymbolicChunkTypeFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSubsymbolicChunkTypeFactory.class);

  public ISubsymbolicChunkType newSubsymbolicChunkType()
  {
    return new BasicSubsymbolicChunkType();
  }

  public void bind(ISubsymbolicChunkType subsymbolic, IChunkType wrapper,
      Collection<IChunkType> parents)
  {
    BasicSubsymbolicChunkType ct = (BasicSubsymbolicChunkType) subsymbolic;
    ct.bind(wrapper, parents);

  }

  public void unbind(ISubsymbolicChunkType subsymbolic)
  {
    BasicSubsymbolicChunkType ct = (BasicSubsymbolicChunkType) subsymbolic;
    ct.bind(null, Collections.EMPTY_LIST);

  }

  public void merge(ISubsymbolicChunkType master, ISubsymbolicChunkType mergie)
  {
    // noop

  }

  public void dispose(ISubsymbolicChunkType subsymbolic)
  {
    subsymbolic.dispose();

  }

}
