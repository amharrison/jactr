package org.jactr.tools.itr;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ChunkParameterModifier extends AbstractParameterModifier
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER            = LogFactory
                                                     .getLog(ChunkParameterModifier.class);

  static public final String   CHUNK_PATTERN     = "ChunkPattern";

  static public final String   CHUNKTYPE_PATTERN = "ChunkTypePattern";

  private Pattern              _chunk;

  private Pattern              _chunkType        = Pattern.compile(".*");

  @Override
  protected void setParameter(CommonTree modelDescriptor, String parameter,
      String value)
  {
    Map<String, CommonTree> chunktypes = ASTSupport.getMapOfTrees(
        modelDescriptor, JACTRBuilder.CHUNK_TYPE);
    for (String chunkTypeName : chunktypes.keySet())
      if (_chunkType.matcher(chunkTypeName).matches())
      {
        Map<String, CommonTree> chunks = ASTSupport.getMapOfTrees(chunktypes
            .get(chunkTypeName), JACTRBuilder.CHUNK);

        for (String chunkName : chunks.keySet())
          if (_chunk.matcher(chunkName).matches())
          {
            ASTSupport support = new ASTSupport();
            support.setParameter(chunks.get(chunkName), parameter, value, true);
          }
      }
  }

  @Override
  public String getParameterDisplayName()
  {
    return String.format("%s.%s.%s", _chunkType, _chunk, getParameterName());
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = super.getSetableParameters();
    rtn.add(CHUNK_PATTERN);
    rtn.add(CHUNKTYPE_PATTERN);
    return rtn;
  }

  @Override
  public void setParameter(String key, String value)
  {

    if (CHUNK_PATTERN.equalsIgnoreCase(key))
      try
      {
        _chunk = Pattern.compile(value);
      }
      catch (PatternSyntaxException e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER
              .error(String
                  .format(
                      "Could not compile chunk pattern %s using .* instead ",
                      value), e);
        _chunk = Pattern.compile(".*");
      }
    else if (CHUNKTYPE_PATTERN.equalsIgnoreCase(key))
      try
      {
        _chunkType = Pattern.compile(value);
      }
      catch (PatternSyntaxException e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER.error(String
              .format(
                  "Could not compile chunktype pattern %s using .* instead ",
                  value), e);
        _chunkType = Pattern.compile(".*");
      }
    else
      super.setParameter(key, value);
  }
}
