/*
 * Created on Jul 10, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.modules.pm.aural.audicon.map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.map.DefaultFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.FINSTState;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

/**
 * @author developer
 */
public class FINSTFeatureMap extends DefaultFINSTFeatureMap implements
    IAuralFeatureMap<FINSTState>
{
  /**
   * logger definition
   */
  static private final Log      LOGGER = LogFactory
                                           .getLog(FINSTFeatureMap.class);

  private IAuralPropertyHandler _propertyHandler;

  private IAuralModule          _module;

  /**
   * @param model
   * @param attendedSlotName
   */
  public FINSTFeatureMap(IAuralModule module)
  {
    super(module.getModel(), IAuralModule.ATTENDED_STATUS_SLOT);
    _module = module;
    _propertyHandler = new DefaultAuralPropertyHandler();
    /*
     * aural finsts are effectively infinite, silly i know..
     */
    setMaximumFINSTs(Integer.MAX_VALUE);
  }

  @Override
  public boolean isInterestedIn(IAfferentObject object)
  {
    return _propertyHandler.hasModality(object);
  }

  @Override
  public void afferentObjectRemoved(final IAfferentObject object)
  {
    /*
     * queue up the timed event that will actually do the removal
     */
    double now = ACTRRuntime.getRuntime().getClock(_module.getModel())
        .getTime();
    ITimedEvent te = new AbstractTimedEvent(now, now +
        _module.getAuralDecayTime()) {
      @Override
      public void fire(double now)
      {
        super.fire(now);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Actually removing " + object.getIdentifier() + " at " +
              now);

        /*
         * iterate through the feature maps, calling
         * IAuralFeatureMap.removeFeaturefor()
         */
        for (IFeatureMap fm : _module.getAudicon().getFeatureMaps())
          if (fm instanceof IAuralFeatureMap)
            ((IAuralFeatureMap) fm).removeFeatureFor(object);
      }
    };

    _module.getModel().getTimedEventQueue().enqueue(te);
  }

  /**
   * we route this to the super implementation of afferentObjectRemoved
   * which actually removes it from the finst map
   * @param object
   * @see org.jactr.modules.pm.aural.audicon.map.IAuralFeatureMap#removeFeatureFor(org.commonreality.object.IAfferentObject)
   */
  public void removeFeatureFor(IAfferentObject object)
  {
    super.afferentObjectRemoved(object);
  }
}
