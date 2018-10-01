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
import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.four.IBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.four.IRandomActivationEquation;
import org.jactr.core.module.declarative.four.ISpreadingActivationEquation;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.ACTRParameterProcessor;
import org.jactr.core.utils.parameter.LinkParameterProcessor;
import org.jactr.core.utils.parameter.LongParameterProcessor;

public class DefaultSubsymbolicChunk4 extends AbstractSubsymbolicChunk
    implements ISubsymbolicChunk4
{
  /**
   * logger definition
   */
  static private final Log                LOGGER = LogFactory
      .getLog(DefaultSubsymbolicChunk4.class);

  protected long                          _creationCycle;

  protected Map<IChunk, IAssociativeLink> _jAssociations;              // keyed

  // on
  // jChunk

  protected Map<IChunk, IAssociativeLink> _iAssociations;              // keyed

  // on
  // iChunk

  protected IBaseLevelActivationEquation  _baseLevelActivationEquation;

  protected IRandomActivationEquation     _randomActivationEquation;

  protected ISpreadingActivationEquation  _spreadingActivationEquation;

  public DefaultSubsymbolicChunk4()
  {
    super();

    /*
     * the use of hashMap here is a problem if we get into the very large scale.
     * But concurrent skip map might not be much better. plus, we still need to
     * handle the encoding/merging of the associated chunks correctly..
     */
    _jAssociations = new HashMap<IChunk, IAssociativeLink>();
    _iAssociations = new HashMap<IChunk, IAssociativeLink>();
  }

  @Override
  public void encode(double when)
  {
    if (!_parentChunk.isEncoded()) try
    {
      writeLock().lock();
      IDeclarativeModule decMod = _parentChunk.getModel()
          .getDeclarativeModule();
      IDeclarativeModule4 dm = decMod.getAdapter(IDeclarativeModule4.class);
      if (dm == null)
      {
        if (LOGGER.isWarnEnabled()) LOGGER
            .warn("IDeclarativeModule4 required to get base level constant");
      }
      else
        setBaseLevelActivation(dm.getBaseLevelConstant());

      setCreationCycle(_parentChunk.getModel().getCycle());
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

    _jAssociations.clear();
    _iAssociations.clear();

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
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          CREATION_CYCLE, old, cycle));
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

  public void addLink(IAssociativeLink l)
  {
    try
    {
      writeLock().lock();

      Link4 currentLink = null;
      IChunk iChunk = l.getIChunk();
      IChunk jChunk = l.getJChunk();
      if (jChunk.equals(_parentChunk))
      {
        currentLink = (Link4) getIAssociation(iChunk);
        if (currentLink == null)
          _iAssociations.put(iChunk, l);
        else
        {
          currentLink.increment();
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Already have a link " + l + ", incrementing instead ",
                new RuntimeException());
        }
      }

      if (iChunk.equals(_parentChunk))
      {
        currentLink = (Link4) getJAssociation(jChunk);
        if (currentLink == null)
          _jAssociations.put(jChunk, l);
        else
        {
          currentLink.increment();
          if (LOGGER.isDebugEnabled()) LOGGER.debug(
              "Already have a link " + l + ", use incrementing instead",
              new RuntimeException());
        }
      }

      /*
       * mark all of the links as dirty. that is, their strengths need to be
       * relearned
       */
      for (IAssociativeLink link : _iAssociations.values())
        ((Link4) link).dirty();

      for (IAssociativeLink link : _jAssociations.values())
        ((Link4) link).dirty();
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          _parentChunk.getModel().getAge(), LINKS, null, null));
  }

  public IAssociativeLink getIAssociation(IChunk iChunk)
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

  public Collection<IAssociativeLink> getIAssociations(
      Collection<IAssociativeLink> container)
  {
    if (container == null) container = new ArrayList<IAssociativeLink>();
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

  public IAssociativeLink getJAssociation(IChunk jChunk)
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

  public Collection<IAssociativeLink> getJAssociations(
      Collection<IAssociativeLink> container)
  {
    if (container == null) container = new ArrayList<IAssociativeLink>();
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

  public void removeLink(IAssociativeLink l)
  {
    // Collection<Link> old = getIAssociations();
    try
    {
      writeLock().lock();
      Link4 currentLink = null;
      if (_parentChunk.equals(l.getIChunk()))
      {
        currentLink = (Link4) getJAssociation(l.getJChunk());
        if (currentLink != null)
        {
          currentLink.decrement();
          if (currentLink.getCount() <= 0) _jAssociations.remove(l.getJChunk());
        }
      }

      if (_parentChunk.equals(l.getJChunk()))
      {
        currentLink = (Link4) getIAssociation(l.getIChunk());
        if (currentLink != null)
        {
          currentLink.decrement();
          if (currentLink.getCount() <= 0) _iAssociations.remove(l.getIChunk());
        }
      }

      /*
       * mark all of the links as dirty. that is, their strengths need to be
       * relearned
       */
      for (IAssociativeLink link : _iAssociations.values())
        ((Link4) link).dirty();

      for (IAssociativeLink link : _jAssociations.values())
        ((Link4) link).dirty();
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          _parentChunk.getModel().getAge(), LINKS, null, null));
  }

  @Override
  protected void initializeParameters()
  {
    super.initializeParameters();

    getParameterHelper().addProcessor(new LongParameterProcessor(CREATION_CYCLE,
        this::setCreationCycle, this::getCreationCycle));
    /*
     * links require a little more than the generic logic.
     */

    getParameterHelper().addProcessor(
        new LinkParameterProcessor(LINKS, this::setInitialLinks, () -> {
          Collection<IAssociativeLink> container = Lists.mutable.empty();
          getOutboundLinks(container);
          return container;
        }, new ACTRParameterProcessor(() -> {
          return getParentChunk().getModel();
        }), () -> {
          return getParentChunk();
        }));

  }

  protected void setInitialLinks(Collection<IAssociativeLink> outboundLinks)
  {
    IAssociativeLinkageSystem ls = getParentChunk().getModel()
        .getDeclarativeModule().getAssociativeLinkageSystem();

    if (ls == null) return;

    for (IAssociativeLink l : outboundLinks)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("adding link " + l);

      Link4 oldLink = (Link4) getIAssociation(l.getIChunk());
      if (oldLink != null)
        oldLink.copy(l);
      else
        ls.addLink(l);
    }
  }

//  @Override
//  public Collection<String> getSetableParameters()
//  {
//    Collection<String> str = super.getSetableParameters();
//    str.add(LINKS);
//    str.add(CREATION_CYCLE);
//    return str;
//  }

//  @Override
//  public String getParameter(String key)
//  {
//    String rtn = null;
//    if (LINKS.equalsIgnoreCase(key))
//    {
//      /*
//       * here we are using the adapter to get the link container, even though
//       * this class implements directly, we go through the adapter in case
//       * someone has needed to replace the container. Note also, we dont use
//       * this adaptable, but the chunk's. Should probably remove adaptable from
//       * content classes.
//       */
//      Collection<IAssociativeLink> associations = FastCollectionFactory
//          .newInstance();
//      try
//      {
//        IAssociativeLinkContainer aslc = getParentChunk()
//            .getAdapter(IAssociativeLinkContainer.class);
//
//        aslc.getOutboundLinks(associations); // everyone we spread to
//
//        ACTRParameterHandler actrph = new ACTRParameterHandler(
//            getParentChunk().getModel());
//
//        LinkParameterHandler lph = getParentChunk().getModel()
//            .getDeclarativeModule().getAssociativeLinkageSystem()
//            .getParameterHandler();
//        lph.setDependents(getParentChunk(), actrph);
//
//        CollectionParameterHandler<IAssociativeLink> aph = new CollectionParameterHandler<IAssociativeLink>(
//            lph);
//        rtn = aph.toString(associations);
//      }
//      finally
//      {
//        FastCollectionFactory.recycle(associations);
//      }
//
//    }
//    else if (CREATION_CYCLE.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getCreationCycle());
//    else
//      rtn = super.getParameter(key);
//
//    return rtn;
//  }

//  @Override
//  public void setParameter(String key, String value)
//  {
//    if (CREATION_CYCLE.equalsIgnoreCase(key))
//      setCreationCycle(
//          ParameterHandler.numberInstance().coerce(value).longValue());
//    else if (LINKS.equalsIgnoreCase(key))
//    {
//      ACTRParameterHandler actrph = new ACTRParameterHandler(
//          getParentChunk().getModel());
//
//      IAssociativeLinkageSystem ls = getParentChunk().getModel()
//          .getDeclarativeModule().getAssociativeLinkageSystem();
//
//      LinkParameterHandler lph = getParentChunk().getModel()
//          .getDeclarativeModule().getAssociativeLinkageSystem()
//          .getParameterHandler();
//      lph.setDependents(getParentChunk(), actrph);
//
//      CollectionParameterHandler<IAssociativeLink> aph = new CollectionParameterHandler<IAssociativeLink>(
//          lph, true);
//      for (IAssociativeLink l : aph.coerce(value))
//      {
//        if (LOGGER.isDebugEnabled()) LOGGER.debug("adding link " + l);
//
//        Link4 oldLink = (Link4) getIAssociation(l.getIChunk());
//        if (oldLink != null)
//          oldLink.copy(l);
//        else
//          ls.addLink(l);
//      }
//    }
//    else
//      super.setParameter(key, value);
//  }

  public void setBaseLevelActivationEquation(
      IBaseLevelActivationEquation equation)
  {
    if (_baseLevelActivationEquation != null)
      removeActivationParticipant(_baseLevelActivationEquation);

    _baseLevelActivationEquation = equation;
    addActivationParticipant(equation);
  }

  public void setRandomActivationEquation(IRandomActivationEquation equation)
  {
    if (_randomActivationEquation != null)
      removeActivationParticipant(_randomActivationEquation);
    _randomActivationEquation = equation;
    addActivationParticipant(equation);
  }

  public void setSpreadingActivationEquation(
      ISpreadingActivationEquation equation)
  {
    if (_spreadingActivationEquation != null)
      removeActivationParticipant(_spreadingActivationEquation);

    _spreadingActivationEquation = equation;
    addActivationParticipant(equation);
  }

  // @Override
  // protected double computeBaseLevelActivation()
  // {
  // if (_baseLevelActivationEquation == null) return _baseLevelActivation;
  //
  // return _baseLevelActivationEquation.computeBaseLevelActivation(
  // _parentChunk.getModel(), _parentChunk);
  // }
  //
  // @Override
  // protected double computeSpreadingActivation()
  // {
  // if (_spreadingActivationEquation != null)
  // return _spreadingActivationEquation.computeSpreadingActivation(
  // _parentChunk.getModel(), _parentChunk);
  //
  // return _spreadingActivation;
  //
  // }
  //
  // @Override
  // protected double computeRandomActivation()
  // {
  // if (_randomActivationEquation != null)
  // return _randomActivationEquation.computeRandomActivation(
  // _parentChunk.getModel(), _parentChunk);
  // return 0;
  // }

  @Override
  public void getOutboundLinks(Collection<IAssociativeLink> container)
  {
    getIAssociations(container);
  }

  @Override
  public long getNumberOfOutboundLinks()
  {
    return getNumberOfIAssociations();
  }

  @Override
  public void getInboundLinks(Collection<IAssociativeLink> container)
  {
    getJAssociations(container);
  }

  @Override
  public long getNumberOfInboundLinks()
  {
    return getNumberOfJAssociations();
  }

  @Override
  public void getOutboundLinks(IChunk receiver,
      Collection<IAssociativeLink> container)
  {
    IAssociativeLink link = getIAssociation(receiver);
    if (link != null) container.add(link);

  }

  @Override
  public void getInboundLinks(IChunk sender,
      Collection<IAssociativeLink> container)
  {
    IAssociativeLink link = getJAssociation(sender);
    if (link != null) container.add(link);
  }
}
