package org.jactr.modules.pm.vocal;

/*
 * default logging
 */
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.commonreality.modalities.vocal.VocalizationCommand;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.vocal.buffer.IVocalActivationBuffer;
import org.jactr.modules.pm.vocal.event.IVocalModuleListener;
import org.jactr.modules.pm.vocal.event.VocalModuleEvent;

public interface IVocalModule extends IPerceptualModule
{
  /**
   * defines how execution time conflicts are resolved between actr
   * and common reality
   * @author harrison
   *
   */
  static enum ExecutionTimeResolution {MINIMUM,MAXIMUM,ACTR,CR};

  static public final String CLEAR_CHUNK_TYPE       = "clear";

  static public final String SPEAK_CHUNK_TYPE = "speak";

  static public final String SUBVOCALIZE_CHUNK_TYPE       = "subvocalize";
  
  static public final String STRING_SLOT = "string";

  static public final String VOCAL_BUFFER = IActivationBuffer.VOCAL;
  
  static public final String PREPARATION_EQUATION = "PreparationTimeEquation";
  
  static public final String VOCALIZATION_EQUATION = "VocalizationTimeEquation";
  
  /**
   * returns the currently prepared, but not vocalized speach
   * @return
   */
  public String getPreparedVocalization();
  
  public IVocalExecutionTimeEquation getExecutionTimeEquation();
  
  public IVocalPreparationTimeEquation getPreparationTimeEquation();
  
  public IVocalProcessingTimeEquation getProcessingTimeEquation();
  
  public IChunkType getSpeakChunkType();
  
  public IChunkType getSubvocalizeChunkType();
  
  public ExecutionTimeResolution getExecutionTimeResolution();
  
  
  
  
  /**
   * reset the module
   */
  public void reset();
  
  
  
  public IVocalActivationBuffer getVocalBuffer();
  

//  public void prepare(ChunkTypeRequest request, String text, boolean isVocalization);
//
//  public void execute(IIdentifier commandId, boolean isVocalization);
  
  public Future<VocalizationCommand> prepare(IRequest request,
      double estimatedDuration);
  
  public Future<VocalizationCommand> execute(VocalizationCommand command);
  
  public boolean hasListeners();
  
  public void addListener(IVocalModuleListener listener, Executor executor);
  
  public void removeListener(IVocalModuleListener listener);
  
  public void dispatch(VocalModuleEvent event);
  
}
