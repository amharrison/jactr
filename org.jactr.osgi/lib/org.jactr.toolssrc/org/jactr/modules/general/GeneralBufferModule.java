package org.jactr.modules.general;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.six.BasicBuffer6;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.utils.parameter.IParameterized;

public class GeneralBufferModule extends AbstractModule implements
    IParameterized
{

  static public final String         BUFFER_NAMES_PARAM   = "BufferNames";

  /**
   * Logger definition
   */
  static private final transient Log LOGGER               = LogFactory
                                                              .getLog(GeneralBufferModule.class);

  private Set<String>                _buffersToContribute = new HashSet<String>();

  public GeneralBufferModule()
  {
    super("buffer");
  }

  @Override
  protected Collection<IActivationBuffer> createBuffers()
  {
    ArrayList<IActivationBuffer> buffers = new ArrayList<IActivationBuffer>();
    for (String bufferName : _buffersToContribute)
      buffers.add(new BasicBuffer6(bufferName, this));

    return buffers;
  }

  @Override
  public void initialize()
  {

  }

  public void reset()
  {

  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(BUFFER_NAMES_PARAM);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if (BUFFER_NAMES_PARAM.equalsIgnoreCase(key))
    {
      String[] buffers = value.split(",");
      for (String buffer : buffers)
      {
        buffer = buffer.trim();
        if (buffer.length() != 0) _buffersToContribute.add(buffer);
      }
    }

  }

}
