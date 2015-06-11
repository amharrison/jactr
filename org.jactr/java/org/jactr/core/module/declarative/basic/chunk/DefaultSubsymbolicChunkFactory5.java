package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunk.five.DefaultSubsymbolicChunk5;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.four.associative.ChunkListener;

public class DefaultSubsymbolicChunkFactory5 implements
    ISubsymbolicChunkFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER              = LogFactory
                                                             .getLog(DefaultSubsymbolicChunkFactory5.class);

  static private boolean             _warnedAboutMerging = false;

  private boolean                    _copyILinks         = false;

  private boolean                    _copyJLinks         = true;

  /**
   * @return true, if, when copying chunks, we want to duplicate the j links
   *         (those that would spread to the copied chunk) (default: true)
   */
  public boolean shouldCopyJLinks()
  {
    return _copyJLinks;
  }

  /**
   * @return true, if, when copying chunks, we want to duplicate the i links
   *         (those that the copy would spread through)
   */
  public boolean shouldCopyILinks()
  {
    return _copyILinks;
  }

  public void setShouldCopyJLinks(boolean enable)
  {
    _copyJLinks = enable;
  }

  public void setShouldCopyILinks(boolean enable)
  {
    _copyILinks = enable;
  }

  public ISubsymbolicChunk newSubsymbolicChunk()
  {
    return new DefaultSubsymbolicChunk5();
  }

  public void bind(ISubsymbolicChunk subsymbolic, IChunk wrapper,
      IChunkType type)
  {
    DefaultSubsymbolicChunk5 ssc = (DefaultSubsymbolicChunk5) subsymbolic;

    ssc.bind(wrapper);
  }

  /**
   * mostly a noop. Most subsymbolic values are untouched. The only mergable
   * information at this theoretic level are the similarities (unhandled
   * currently) and the associations. Since associations are handled directly by
   * the {@link IAssociativeLinkageSystem}, it should handle that merging via
   * {@link IChunkListener#mergingInto(org.jactr.core.chunk.event.ChunkEvent)}.
   * See {@link ChunkListener} for how this is handled.
   */
  public void merge(ISubsymbolicChunk master, ISubsymbolicChunk copy)
  {
    synchronized (this.getClass())
    {
      if (LOGGER.isWarnEnabled() && !_warnedAboutMerging)
      {
        LOGGER.warn("Merging of similarities not implemented");
        _warnedAboutMerging = true;
      }
    }
  }

  public void unbind(ISubsymbolicChunk subsymbolic)
  {
    DefaultSubsymbolicChunk5 ssc = (DefaultSubsymbolicChunk5) subsymbolic;

    ssc.bind(null);
  }

  public void dispose(ISubsymbolicChunk subsymbolic)
  {
    subsymbolic.dispose();
  }

  public void copy(ISubsymbolicChunk sourceSSC, ISubsymbolicChunk destinationSSC)
  {
    // as a messy first approx we can just use the parameterized interface
    /*
     * set all the parameters this should handle the associative links as
     * well...
     */

    for (String parameterName : destinationSSC.getSetableParameters())
    {
      String parameterValue = sourceSSC.getParameter(parameterName);
      if (ISubsymbolicChunk4.LINKS.equalsIgnoreCase(parameterName))
      {
        IChunk source = sourceSSC.getParentChunk();
        IChunk dest = destinationSSC.getParentChunk();
        // delegate to the associative linkage system
        IAssociativeLinkageSystem linkage = source.getModel()
            .getDeclarativeModule().getAssociativeLinkageSystem();

        if (linkage != null)
          linkage.copyAndRemapLinks(source, dest, shouldCopyILinks(),
              shouldCopyJLinks());
        else if (LOGGER.isWarnEnabled())
          LOGGER
              .warn(String
                  .format("No associative linkage system available, cannot copy links"));
      }
      else
        try
        {
          destinationSSC.setParameter(parameterName, parameterValue);
        }
        catch (Exception e)
        {
          LOGGER.warn("Could not set parameter " + parameterName + " to "
              + parameterValue, e);
        }
    }

    destinationSSC.getParentChunk().setMetaData(SUBSYMBOLICS_COPIED_KEY,
        Boolean.TRUE);

  }

}
