package org.jactr.tools.misc;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;

public class ChunkTypeUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkTypeUtilities.class);

  /**
   * will fetch the named chunk type, if it exists. If not, it will be created,
   * using parents, and configured before adding to DM and returning
   * 
   * @param chunkTypeName
   * @param model
   * @param configurator
   * @param chunkTypes
   * @return
   */
  public CompletableFuture<IChunkType> getOrCreate(String chunkTypeName,
      IModel model, Consumer<IChunkType> configurator,
      Collection<IChunkType> parents)
  {
    IDeclarativeModule decM = model.getDeclarativeModule();
    // try the get
    CompletableFuture<IChunkType> future = decM.getChunkType(chunkTypeName);
    // if fail, create & configure
    future = future
        .thenCompose((ct) -> {
          if (ct == null)
            return decM.createChunkType(parents, chunkTypeName);
          else
        return CompletableFuture.completedFuture(ct);
        });
    // and configure if necessary
    future
        .thenApply(
            (ct) -> {
              if (!ct.getSubsymbolicChunkType().isEncoded())
                configurator.accept(ct);

              return ct;
            })
        // and finaly encode
        .thenCompose(
            (ct) -> {
              if (!ct.getSubsymbolicChunkType().isEncoded())
                return decM.addChunkType(ct);
              else
            return CompletableFuture.completedFuture(ct);
            });


    return future;
  }
}
