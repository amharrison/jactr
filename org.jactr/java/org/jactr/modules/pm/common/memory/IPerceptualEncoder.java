package org.jactr.modules.pm.common.memory;

import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;

/*
 * default logging
 */

/**
 * interface that is responsible for encoding a percept
 * @author harrison
 *
 */
public interface IPerceptualEncoder
{
  static public final String COMMONREALITY_IDENTIFIER_META_KEY = "org.commonreality.identifier";

  static public final String COMMONREALITY_ONSET_TIME_KEY      = "org.commonreality.onsetTime";
  
  /**
   * 
   * @param afferentObject
   * @return
   */
  public boolean isInterestedIn(IAfferentObject afferentObject);
  
  /**
   * 
   * @param afferentObject
   * @param memory
   * @return
   */
  public IChunk encode(IAfferentObject afferentObject, IPerceptualMemory memory);
  
  /**
   * return true if the prior encoding of the percept needs to be amended
   * @param afferentObject
   * @param oldChunk
   * @param memory
   * @return
   */
  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk, IPerceptualMemory memory);
  
  /**
   * update the contents of the encoded chunk. If the percept has changed too much, a new chunk can be
   * returned.
   * @param afferentObject
   * @param oldChunk
   * @param memory
   * @return
   */
  public IChunk update(IAfferentObject afferentObject, IChunk oldChunk, IPerceptualMemory memory);
}
