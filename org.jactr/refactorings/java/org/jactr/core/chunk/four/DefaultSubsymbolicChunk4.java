/*
 * Created on Oct 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.chunk.four;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.ACTRParameterHandler;
import org.jactr.core.utils.parameter.CollectionParameterHandler;
import org.jactr.core.utils.parameter.LinkParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DefaultSubsymbolicChunk4 extends AbstractSubsymbolicChunk
    implements ISubsymbolicChunk4
{
  /**
   * logger definition
   */
  static private final Log    LOGGER = LogFactory
                                         .getLog(DefaultSubsymbolicChunk4.class);

  protected long              _creationCycle;

  protected Map<IChunk, Link> _jAssociations;                                    // keyed

  // on
  // jChunk

  protected Map<IChunk, Link> _iAssociations;                                    // keyed

  // on
  // iChunk

  public DefaultSubsymbolicChunk4(IChunk parent)
  {
    super(parent);
    _jAssociations = new HashMap<IChunk, Link>();
    _iAssociations = new HashMap<IChunk, Link>();
  }

  @Override
  public void encode(double when)
  {
    if (!_parentChunk.isEncoded())
      try
      {
        writeLock().lock();
        IDeclarativeModule decMod = _parentChunk.getModel()
            .getDeclarativeModule();
        if (!(decMod instanceof IDeclarativeModule4))
        {
          if (LOGGER.isWarnEnabled())
            LOGGER
                .warn("IDeclarativeModule4 required to get base level constant");
        }
        else
          setBaseLevelActivation(((IDeclarativeModule4) decMod)
              .getBaseLevelConstant());

        setCreationCycle(_parentChunk.getModel().getCycle());

        /*
         * add a link to ourselves
         */
        addLink(new Link(_parentChunk, _parentChunk));
      }
      finally
      {
        writeLock().unlock();
      }

    super.encode(when);
  }

  @Override
  public void dispose()
  {
    super.dispose();

    try
    {
      writeLock().lock();
      /*
       * remove our associations. Why do we bother? a chunk can be disposed
       * during the normal life cycle of a model. For example, if a visual
       * object were stashed in the visual-location.objects slot, the
       * visual-location will have a link to the visual object. When the visual
       * object is disposed of because it cannot be seen, but was never actually
       * encoded, we need to remove the associative links
       */
      for (Link l : _jAssociations.values())
        if (!l.getJChunk().hasBeenDisposed())
        {
          ((ISubsymbolicChunk4) l.getJChunk().getSubsymbolicChunk())
              .removeLink(l);
        }

      for (Link l : _iAssociations.values())
        if (!l.getIChunk().hasBeenDisposed())
        {
          ((ISubsymbolicChunk4) l.getIChunk().getSubsymbolicChunk())
              .removeLink(l);
        }

      _jAssociations.clear();
      _iAssociations.clear();
    }
    finally
    {
      writeLock().unlock();
    }
  }

  public void setCreationCycle(long cycle)
  {
    long old = 0;
    try
    {
      writeLock().lock();
      old = _creationCycle;
      _creationCycle = cycle;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), CREATION_CYCLE, old,
          cycle));
  }

  public long getCreationCycle()
  {
    try
    {
      readLock().lock();
      return _creationCycle;
    }
    finally
    {
      readLock().unlock();
    }
  }

  @Override
  protected double computeSpreadingActivation()
  {
    double spread = 0.0;

    for (Link jLink : getJAssociations(null))
    {
      ISubsymbolicChunk sc = jLink.getJChunk().getSubsymbolicChunk();
      spread += sc.getSourceActivation() * jLink.getStrength();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(_parentChunk + " Pulling " + spread + " from " + jLink);
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("SpreadingActivation " + spread);
    if (Double.isNaN(spread) || Double.isInfinite(spread)) spread = 0;

    return spread;
  }

  public void addLink(Link l)
  {
    try
    {
      writeLock().lock();
      Link currentLink = null;
      IChunk iChunk = l.getIChunk();
      IChunk jChunk = l.getJChunk();
      if (jChunk.equals(_parentChunk))
      {
        currentLink = getIAssociation(iChunk);
        if (currentLink == null)
          _iAssociations.put(iChunk, l);
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("Already have a link " + l + ", use increment instead ",
              new RuntimeException());
      }

      if (iChunk.equals(_parentChunk))
      {
        currentLink = getJAssociation(jChunk);
        if (currentLink == null)
          _jAssociations.put(jChunk, l);
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("Already have a link " + l + ", use increment instead",
              new RuntimeException());
      }
    }
    finally
    {
      writeLock().unlock();
    }

    // if (_parentChunk.hasParameterListeners() && old != null)
    // _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
    // .getClock(_parentChunk.getModel()).getTime(), LINKS, old,
    // getIAssociations()));
  }

  public Link getIAssociation(IChunk iChunk)
  {
    try
    {
      readLock().lock();
      return _iAssociations.get(iChunk);
    }
    finally
    {
      readLock().unlock();
    }
  }

  public Collection<Link> getIAssociations(Collection<Link> container)
  {
    if (container == null) container = new ArrayList<Link>();
    try
    {
      readLock().lock();
      container.addAll(_iAssociations.values());
      return container;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public Link getJAssociation(IChunk jChunk)
  {
    try
    {
      readLock().lock();
      return _jAssociations.get(jChunk);
    }
    finally
    {
      readLock().unlock();
    }
  }

  public Collection<Link> getJAssociations(Collection<Link> container)
  {
    if (container == null) container = new ArrayList<Link>();
    try
    {
      readLock().lock();
      container.addAll(_jAssociations.values());
      return container;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public int getNumberOfIAssociations()
  {
    try
    {
      readLock().lock();
      return _iAssociations.size();
    }
    finally
    {
      readLock().unlock();
    }
  }

  public int getNumberOfJAssociations()
  {
    try
    {
      readLock().lock();
      return _jAssociations.size();
    }
    finally
    {
      readLock().unlock();
    }

  }

  public void removeLink(Link l)
  {
    // Collection<Link> old = getIAssociations();
    try
    {
      writeLock().lock();
      Link currentLink = null;
      if (_parentChunk.equals(l.getIChunk()))
      {
        currentLink = getJAssociation(l.getJChunk());
        if (currentLink != null)
        {
          currentLink.decrement();
          if (currentLink.getCount() <= 0)
            _jAssociations.remove(l.getJChunk());
        }
      }

      if (_parentChunk.equals(l.getJChunk()))
      {
        currentLink = getIAssociation(l.getIChunk());
        if (currentLink != null)
        {
          currentLink.decrement();
          if (currentLink.getCount() <= 0)
            _iAssociations.remove(l.getIChunk());
        }
      }
    }
    finally
    {
      writeLock().unlock();
    }

    // if (_parentChunk.hasParameterListeners())
    // _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
    // .getClock(_parentChunk.getModel()).getTime(), LINKS, old,
    // getIAssociations()));
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> str = super.getPossibleParameters();
    str.add(LINKS);
    str.add(CREATION_CYCLE);
    return str;
  }

  @Override
  public String getParameter(String key)
  {
    String rtn = null;
    if (LINKS.equalsIgnoreCase(key))
    {
      Collection<Link> associations = getIAssociations(null); // everyone we
      // spread
      // to
      ACTRParameterHandler actrph = new ACTRParameterHandler(getParentChunk()
          .getModel());
      LinkParameterHandler lph = new LinkParameterHandler(getParentChunk(),
          actrph);
      CollectionParameterHandler<Link> aph = new CollectionParameterHandler<Link>(
          lph);
      rtn = aph.toString(associations);
    }
    else if (CREATION_CYCLE.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getCreationCycle());
    else
      rtn = super.getParameter(key);

    return rtn;
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (CREATION_CYCLE.equalsIgnoreCase(key))
      setCreationCycle(ParameterHandler.numberInstance().coerce(value)
          .longValue());
    else if (LINKS.equalsIgnoreCase(key))
    {
      ACTRParameterHandler actrph = new ACTRParameterHandler(getParentChunk()
          .getModel());
      LinkParameterHandler lph = new LinkParameterHandler(getParentChunk(),
          actrph);
      CollectionParameterHandler<Link> aph = new CollectionParameterHandler<Link>(
          lph);
      for (Link l : aph.coerce(value))
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("adding link " + l);

        Link oldLink = getIAssociation(l.getIChunk());
        if (oldLink != null)
        {
          oldLink.setCount(Math.max(oldLink.getCount(), l.getCount()));
          oldLink.setStrength(Math.max(oldLink.getStrength(), l.getStrength()));
        }
        else
        {
          addLink(l);
          /*
           * we also have to add it to the iChunk
           */
          ((ISubsymbolicChunk4) l.getIChunk().getSubsymbolicChunk()).addLink(l);
        }
      }
    }

    super.setParameter(key, value);
  }
}
