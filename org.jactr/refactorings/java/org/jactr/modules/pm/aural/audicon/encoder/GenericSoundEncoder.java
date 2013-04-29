/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural.audicon.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.modalities.aural.ICommonTypes;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.ChunkSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;

/**
 * @author developer
 */
@Deprecated
public class GenericSoundEncoder
    implements IAuralChunkEncoder
{

  /**
   * logger definition
   */
  static private final Log                   LOGGER                = LogFactory
                                                                       .getLog(GenericSoundEncoder.class);

  static private DefaultAuralPropertyHandler _auralPropertyHandler = new DefaultAuralPropertyHandler();

  static public IAuralPropertyHandler getHandler()
  {
    return _auralPropertyHandler;
  }

  protected IFINSTFeatureMap _finstMap;

  /**
   * @param module
   */
  public GenericSoundEncoder(IAuralModule module, IFINSTFeatureMap finstMap)
  {
    _finstMap = finstMap;
  }

  /**
   * @see org.jactr.modules.pm.common.encoder.AbstractAfferentObjectEncoder#canEncode(org.commonreality.object.IAfferentObject)
   */
  public boolean isInterestedIn(IAfferentObject object)
  {
    IAuralPropertyHandler prop = getHandler();

    if (!prop.hasModality(object)) return false;

    try
    {
      if(!prop.isAudible(object)) return false;
      
      for (String type : prop.getTypes(object))
        if (ICommonTypes.DIGIT.equals(type) || ICommonTypes.WORD.equals(type) ||
            ICommonTypes.SPEECH.equals(type) || ICommonTypes.TONE.equals(type))
          return true;
    }
    catch (UnknownPropertyNameException e)
    {
      return false;
    }

    return false;
  }

  /**
   * @see org.jactr.modules.pm.common.encoder.AbstractAfferentObjectEncoder#createChunk(org.commonreality.object.IAfferentObject)
   */
  
  protected IChunk createChunk(IAfferentObject object)
  {
    // IAuralModule module = (IAuralModule) getModule();
    // IChunkType soundChunkType = null;
    // IDeclarativeModule decM = module.getModel().getDeclarativeModule();
    //
    // String name = getHandler().getToken(object);
    // try
    // {
    // for (String kind : getHandler().getTypes(object))
    // {
    // soundChunkType = decM.getChunkType(kind).get();
    // if (soundChunkType != null) break;
    // }
    //
    // if (soundChunkType == null) soundChunkType = module.getSoundChunkType();
    //
    // name = soundChunkType.getSymbolicChunkType().getName() + "-" + name;
    //
    // return decM.createChunk(soundChunkType, name).get();
    // }
    // catch (Exception e)
    // {
    // throw new RuntimeException("Could not create sound chunk for " + name,
    // e);
    // }
    return null;
  }

  /**
   * @see org.jactr.modules.pm.common.encoder.AbstractAfferentObjectEncoder#isDirty(org.commonreality.object.IAfferentObject,
   *      org.commonreality.object.delta.IObjectDelta,
   *      org.jactr.core.chunk.IChunk)
   */

  protected boolean isDirty(IAfferentObject object, IObjectDelta delta,
      IChunk oldChunk)
  {
    return false;
  }

  /**
   * audioEvent will be set for us..
   * 
   * @see org.jactr.modules.pm.common.encoder.AbstractAfferentObjectEncoder#setSlotValues(org.jactr.core.chunk.IChunk,
   *      org.commonreality.object.IAfferentObject)
   */

  protected void setSlotValues(IChunk chunk, IAfferentObject object)
  {
    String content = getHandler().getToken(object);

    ((ChunkSlot) chunk.getSymbolicChunk().getSlot(IAuralModule.CONTENT_SLOT))
        .setValue(content);
  }


  

  
  public void afferentObjectRemoved(final IAfferentObject object)
  {
    /*
     * ok, we don't actually remove from the cache now, rather we do it after a
     * certain amount of time has elapsed.
     */
//    boolean running = ACTRRuntime.getRuntime().getController().isRunning();
//
//    if (running)
//    {
//      /*
//       * queue it up to be removed after
//       */
//      IModel model = getModule().getModel();
//      double now = ACTRRuntime.getRuntime().getClock(model).getTime();
//      ITimedEvent te = new AbstractTimedEvent(now, now +
//          ((IAuralModule) getModule()).getAuralDecayTime()) {
//        @Override
//        public void fire(double now)
//        {
//          super.fire(now);
//          afferentObjectRemovedInternal(object);
//        }
//      };
//
//      model.getTimedEventQueue().enqueue(te);
//    }
//    else
//      afferentObjectRemovedInternal(object);
  }

  public IChunk encode(IAfferentObject afferentObject, IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public IChunk update(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
