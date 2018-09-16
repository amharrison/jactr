package org.jactr.extensions.cached.procedural.internal;

/*
 * default logging
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.match.IMatchFailure;
import org.jactr.core.production.condition.match.SlotMatchFailure;
import org.jactr.core.production.condition.match.UnresolvedVariablesMatchFailure;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.NotifyingSlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.extensions.cached.procedural.invalidators.BufferInvalidator;
import org.jactr.extensions.cached.procedural.invalidators.IInvalidator;
import org.jactr.extensions.cached.procedural.invalidators.SlotInvalidator;

/**
 * stores productions that cannot be instantiated given the current state, the
 * {@link CannotInstantiateException}, and a set of Invalidators
 * 
 * @author harrison
 */
public class InstantiationCache
{
  /**
   * Logger definition
   */
  static private final transient Log                   LOGGER = LogFactory
                                                                  .getLog(InstantiationCache.class);

  private Map<IProduction, CannotInstantiateException> _failedProductions;

  private Map<IProduction, List<IInvalidator>>         _invalidators;

  private ListenerHub                                  _listenerHub;

  private final ReentrantReadWriteLock                 _lock  = new ReentrantReadWriteLock();

  public InstantiationCache(IModel model)
  {
    _failedProductions = new HashMap<IProduction, CannotInstantiateException>();
    _invalidators = new HashMap<IProduction, List<IInvalidator>>();
    _listenerHub = new ListenerHub(model);
  }

  public void dispose()
  {
    try
    {
      _lock.writeLock().lock();
      _listenerHub.dispose();
      _invalidators.clear();
      _failedProductions.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  public Set<IProduction> getProductions(Set<IProduction> container)
  {
    try
    {
      _lock.readLock().lock();
      container.addAll(_failedProductions.keySet());
    }
    finally
    {
      _lock.readLock().unlock();
    }

    return container;
  }

  public boolean contains(IProduction production)
  {
    boolean contained = false;
    try
    {
      _lock.readLock().lock();

      contained = _failedProductions.containsKey(production);
    }
    finally
    {
      _lock.readLock().unlock();
    }
    return contained;
  }

  public void remove(IProduction production)
  {
    List<IInvalidator> invalidators = null;
    boolean shouldUnregister = false;
    try
    {
      _lock.writeLock().lock();

      /*
       * do we need to unregister?
       */
      if (_failedProductions.remove(production) != null)
      {
        shouldUnregister = true;
        invalidators = _invalidators.remove(production);
      }
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (shouldUnregister)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Marking %s as potentially instantiable",
            production));
      unregisterAll(production, invalidators);
    }

    if (invalidators != null) FastListFactory.recycle(invalidators);
  }

  public void add(IProduction production, CannotInstantiateException cie)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Testing %s after %s", production, cie));

    List<IInvalidator> invalidators = FastListFactory.newInstance();
    boolean canCache = registerAll(production, cie, invalidators);

    if (canCache)
    {
      try
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String
              .format("Marking %s as uninstantiable", production));

        _lock.writeLock().lock();

        _failedProductions.put(production, cie);

        _invalidators.put(production, invalidators);
      }
      finally
      {
        _lock.writeLock().unlock();
      }

      /*
       * no need for this to be in the write lock
       */
      // lack of thread safety here.
      for (IInvalidator invalidator : invalidators)
        invalidator.register(_listenerHub);
    }
    else
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(String
                .format(
                    "%s could not be associated with any invalidators, it will be available for instantiation",
                    production));

      FastListFactory.recycle(invalidators);
    }
  }

  public CannotInstantiateException get(IProduction production)
  {
    CannotInstantiateException cie = null;
    try
    {
      _lock.readLock().lock();

      cie = _failedProductions.get(production);
    }
    finally
    {
      _lock.readLock().unlock();
    }

    return cie;
  }

  public void throwIfCached(IProduction production)
      throws CannotInstantiateException
  {
    CannotInstantiateException cie = null;
    try
    {
      _lock.readLock().lock();
      cie = _failedProductions.get(production);
    }
    finally
    {
      _lock.readLock().unlock();
    }

    if (cie != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s has a cached CIE %s", production, cie));

      throw cie;
    }
  }

  /**
   * @param production
   */
  protected void unregisterAll(IProduction production,
      Collection<IInvalidator> invalidators)
  {

    if (invalidators == null) return;
    for (IInvalidator invalidator : invalidators)
      if (invalidator != null) invalidator.unregister(_listenerHub);

  }

  /**
   * analyze the cause of the exception and possibly install invalidators. if no
   * invalidators can be created, this CIE cannot be cached.
   * 
   * @param production
   * @param cie
   * @return
   */
  protected boolean registerAll(IProduction production,
      CannotInstantiateException cie, Collection<IInvalidator> invalidators)
  {

    /*
     * so long as any CME has an IMatchFailure that can be associated with an
     * Invalidator, we are good.
     */
    try
    {
      for (CannotMatchException cme : cie.getExceptions())
      {
        IMatchFailure mf = cme.getMismatch();
        if (mf == null) continue;

        ICondition condition = mf.getCondition();

        /*
         * anything that matches against a buffer gets pegged to the buffer
         * invalidator, but not exclusively
         */
        if (condition instanceof IBufferCondition)
        {
          String name = ((IBufferCondition) mf.getCondition()).getBufferName();
          invalidators.add(new BufferInvalidator(this, production, name));

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Creating invalidator for buffer %s",
                name));
        }

        /*
         * if there is an unresolved variable and it has a container, we use a
         * slot listener for each slot name
         */
        if (mf instanceof UnresolvedVariablesMatchFailure)
        {
          UnresolvedVariablesMatchFailure unmf = (UnresolvedVariablesMatchFailure) mf;
          IUniqueSlotContainer container = unmf.getContainer();

          /*
           * if we cant track it, abort all tracking
           */
          if (!(container instanceof INotifyingSlotContainer))
            throw new IllegalStateException(String.format(
                "Cannot track slot container %s for unresolved variables %s",
                container, unmf.getUnresolvedSlots()));

          /*
           * attach
           */
          for (IConditionalSlot cSlot : unmf.getUnresolvedSlots())
            invalidators.add(new SlotInvalidator(this, production,
                (INotifyingSlotContainer) container, cSlot.getName()));
        }

        /*
         * for slot match failures, we attach to the slot container and listen
         * to changes in that slot. If the slot refers to a variable, we need to
         * find all references to that variable and listen to all of those slots
         */
        if (mf instanceof SlotMatchFailure)
          processSlotMatchFailure(production, (SlotMatchFailure) mf,
              invalidators);
      }
    }
    catch (IllegalStateException ise)
    {
      // thrown is something requires that this production not be cached.
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Could not cache instantiation failure for %s", production), ise);

      invalidators.clear();
    }

    return invalidators.size() > 0;
  }

  protected void processSlotMatchFailure(IProduction production,
      SlotMatchFailure smf, Collection<IInvalidator> invalidators)
      throws IllegalStateException
  {
    /*
     * first the easy bit, we need an invalidator for the exact slot and
     * container. If this value is null, then the slot does not exist in the
     * container at all and we can't do anything else
     */
    IConditionalSlot cSlot = smf.getConditionalSlot();
    ISlot mSlot = smf.getMismatchedSlot();

    if (mSlot == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "%s is not contained in %s, can never match", cSlot.getName(),
            smf.getSlotContainer()));
      return;
    }

    if (mSlot instanceof NotifyingSlot)
    {
      /*
       * we use the slot's container, and not the one used in the test since it
       * could be a proxy/delegate container that wraps multiple sources (i.e.,
       * the motor buffer and associated muscle). By using the actual container,
       * we are sure we are listening to the right piece
       */
      INotifyingSlotContainer container = ((NotifyingSlot) mSlot)
          .getContainer();
      invalidators.add(new SlotInvalidator(this, production, container, mSlot
          .getName()));

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Creating invalidator for %s.%s", container,
            mSlot.getName()));
    }

    /*
     * now we need to check for variable dependencies. That is, the original
     * (unresolved) conditional slot may have referenced a variable. If so,
     * SlotMatchFailure will contain a source. That source could be a
     * IUniqueSlotContainer (and it a INotifyingSlotContainer we can track it),
     * or a buffer.
     */
    if (smf.involvedVariableValue())
    {
      Object variableSource = smf.getVariableDefinition();

      if (variableSource == null)
        throw new IllegalStateException(
            "Could not find source of variable binding, cannot track");

      if (variableSource instanceof IActivationBuffer)
      {
        String bufferName = ((IActivationBuffer) variableSource).getName();
        invalidators.add(new BufferInvalidator(this, production, bufferName));
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "Creating invalidator to listen for variable change of %s",
              bufferName));
      }
      else if (variableSource instanceof ISlot)
      {
        /*
         * if it is a slot, we have to have a INotifyingSlotContainer, if not,
         * we can't do anything..
         */
        if (!(variableSource instanceof NotifyingSlot))
          throw new IllegalStateException(
              String
                  .format(
                      "Source of variable binding %s isn't a slot notifier, cannot track",
                      variableSource));

        NotifyingSlot ns = (NotifyingSlot) variableSource;
        INotifyingSlotContainer container = ns.getContainer();
        invalidators.add(new SlotInvalidator(this, production, container, ns
            .getName()));

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Creating invalidator for %s.%s",
              container, ns.getName()));
      }
    }

  }

}
