package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.IAssociativeLink;

public class LinkSelector extends AbstractNameSelector<IAssociativeLink>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LinkSelector.class);

  private Matcher                    _chunkTypeMatcher;

  private Matcher                    _chunkMatcher;

  public LinkSelector(String chunkTypeRegEx, String chunkNameRegEx)
  {
    super("");
    _chunkTypeMatcher = Pattern.compile(chunkTypeRegEx).matcher("");
    _chunkMatcher = Pattern.compile(chunkNameRegEx).matcher("");
  }

  /**
   * returns the name of the i chunk
   */
  @Override
  protected String getName(IAssociativeLink element)
  {
    return element.getIChunk().getSymbolicChunk()
        .getName();
  }

  public void add(ISelector selector)
  {
    // contains nothing
  }

  @Override
  public boolean matches(IAssociativeLink element)
  {
    IAssociativeLink link = element;
    IChunk iChunk = link.getIChunk();
    IChunk jChunk = link.getJChunk();

    /*
     * only select those that are linking two encoded chunks.
     */
    if (!iChunk.isEncoded() || !jChunk.isEncoded()) return false;

    String chunkTypeName = iChunk.getSymbolicChunk().getChunkType()
        .getSymbolicChunkType().getName();
    String chunkName = iChunk.getSymbolicChunk().getName();

    _chunkMatcher.reset(chunkName);
    _chunkTypeMatcher.reset(chunkTypeName);

    return _chunkMatcher.matches() && _chunkTypeMatcher.matches();
  }

}
