package org.jactr.core.production.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.match.ChunkTypeMatchFailure;
import org.jactr.core.production.condition.match.LogicMatchFailure;
import org.jactr.core.production.condition.match.SlotMatchFailure;
import org.jactr.core.production.condition.match.UnresolvedVariablesMatchFailure;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.IMutableVariableNameSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.IVariableNameSlot;
import org.jactr.core.utils.collections.FastListFactory;

/*
 * default logging
 */

/**
 * basic slot based request
 */
public class SlotBasedRequest implements IRequest, ISlotContainer
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER  = LogFactory
      .getLog(SlotBasedRequest.class);

  protected Collection<ISlot>        _slots;

  protected Collection<ISlot>        _unresolved;

  private boolean                    _locked = false;

  @SuppressWarnings("unchecked")
  public SlotBasedRequest()
  {
    this(Collections.EMPTY_LIST);
  }

  public SlotBasedRequest(Collection<? extends ISlot> slots)
  {
    _slots = new ArrayList<ISlot>(Math.max(slots.size(), 5));
    for (ISlot slot : slots)
      addSlot(slot);

    // if (slot instanceof ILogicalSlot)
    // _slots.add(new DefaultLogicalSlot(slot));
    // else
    // _slots.add(new DefaultVariableConditionalSlot(slot));
  }

  /**
   * returns the number of slots in this container that match those in the
   * provided container.
   * 
   * @param container
   * @return
   */
  public int countMatches(IChunk chunk, VariableBindings bindings)
  {
    ISymbolicChunk sc = chunk.getSymbolicChunk();
    String name = sc.getName();
    List<ISlot> slots = FastListFactory.newInstance();
    getSlots(slots);
    int count = 0;

    for (ISlot slot : slots)
      try
      {
        resolveSlot(slot, bindings, name, sc);
        count++;
      }
      catch (Exception e)
      {

      }

    FastListFactory.recycle(slots);

    return count;
  }

  @Override
  public boolean matches(IChunk reference)
  {
    ISymbolicChunk sc = reference.getSymbolicChunk();
    String name = sc.getName();
    List<ISlot> slots = FastListFactory.newInstance();
    getSlots(slots);
    VariableBindings bindings = new VariableBindings();

    try
    {
      for (ISlot slot : slots)
        try
        {
          resolveSlot(slot, bindings, name, sc);
        }
        catch (Exception e)
        {
          return false;
        }

      return true;
    }
    finally
    {
      FastListFactory.recycle(slots);
    }
  }

  protected boolean resolveSlot(ISlot slot, VariableBindings bindings,
      String slotContainerName, IUniqueSlotContainer container)
      throws CannotMatchException
  {
    if (slot instanceof IConditionalSlot)
      return resolveConditionalSlot((IConditionalSlot) slot, bindings,
          slotContainerName, container);
    else if (slot instanceof ILogicalSlot)
      return resolveLogicalSlot((ILogicalSlot) slot, bindings,
          slotContainerName, container);

    if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format(
        "A slot other than conditional or logical was attempted to resolve? %s",
        slot));
    return false;
  }

  /**
   * resolve a logical slot. We do this by recursing down and only checking on
   * the return result and exceptions based on the logical condition
   * 
   * @param slotToResolve
   * @param bindings
   * @param slotContainerName
   * @param container
   * @return
   * @throws CannotMatchException
   */
  protected boolean resolveLogicalSlot(ILogicalSlot slotToResolve,
      VariableBindings bindings, String slotContainerName,
      IUniqueSlotContainer container) throws CannotMatchException
  {
    List<ISlot> slots = FastListFactory.newInstance();

    int op = slotToResolve.getOperator();
    try
    {
      slotToResolve.getSlots(slots);
      boolean anyIsResolved = false;
      CannotMatchException anyException = null;

      for (ISlot slot : slots)
        try
        {
          boolean resolved = resolveSlot(slot, bindings, slotContainerName,
              container);

          // for or
          if (resolved) anyIsResolved = true;

          // any unresolved w/ AND
          if (!resolved && op == ILogicalSlot.AND) return false;
        }
        catch (CannotMatchException cme)
        {
          anyException = cme;

          if (op == ILogicalSlot.AND) throw cme;
        }

      if (op == ILogicalSlot.OR)
      {
        /*
         * if any was resolved, we return true. we ignore the CME
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("%s was%s resolved", slotToResolve,
              anyIsResolved ? "" : "nt"));

        if (!anyIsResolved) throw anyException;

        return anyIsResolved;
      }
      else if (op == ILogicalSlot.AND)
      {
        /*
         * we short circuit AND above.
         */
        if (LOGGER.isDebugEnabled()) LOGGER.debug(String
            .format("No exceptions or unresolved slots for %s", slotToResolve));
      }
      else if (op == ILogicalSlot.NOT)
        /*
         * NOTs are expecting an exception., but they should still be resolved
         */
        if (anyException == null) throw new CannotMatchException(
            new LogicMatchFailure(container, slotToResolve));

      // otherwise
      return true;
    }
    finally
    {
      FastListFactory.recycle(slots);
    }
  }

  /**
   * attempt to resolve a single (non logical) slot. this will attempt to
   * resolve variable names and values, in addition to extending the binding set
   * (if there is a container to match against). Impossible matching errors may
   * result in cannot match.
   * 
   * @param slotToResolve
   * @param model
   * @param bindings
   * @param slotContainer
   * @return true if it is fully resolved, false if it cannot be resolved at
   *         present
   */
  protected boolean resolveConditionalSlot(IConditionalSlot slotToResolve,
      VariableBindings bindings, String slotContainerName,
      IUniqueSlotContainer slotContainer) throws CannotMatchException
  {
    /*
     * the name is a variable..
     */
    if (slotToResolve instanceof IMutableVariableNameSlot
        && ((IMutableVariableNameSlot) slotToResolve).isVariableName())
    {
      String variableName = slotToResolve.getName().toLowerCase();
      /*
       * if we can resolve, do so, otherwise, mark as unresolved for now
       */
      if (bindings.isBound(variableName))
      {
        Object obj = bindings.get(variableName);
        ((IMutableVariableNameSlot) slotToResolve).setName(obj.toString());
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("slot name %s resolved to %s",
              variableName, obj.toString()));
      }
      else
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("slot name %s unresolved", variableName));
        return false;
      }
    }

    /*
     * resolve the value using the variable bindings
     */

    /*
     * the simple case is pure binding, no value slots to test against. If it is
     * not already bound, we cannot resolve
     */
    if (slotContainer == null)
    {
      if (slotToResolve.isVariableValue())
      {
        String variableName = ((String) slotToResolve.getValue()).toLowerCase();
        if (bindings.isBound(variableName))
        {
          Object obj = bindings.get(variableName);

          slotToResolve.setValue(obj);

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Resolved %s %s = %s",
                slotToResolve.getName(), variableName, obj));
        }
        else
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s %s unresolved ",
                slotToResolve.getName(), variableName));

          return false;
        }
      }
    }
    else
    {
      /*
       * the variable has not been bound yet. we have a container if it contains
       * a match, we will set the value and continue. Otherwise, we return false
       * (unresolved)
       */

      /*
       * snag the matching value slot in the container
       */
      ISlot valueSlot = null;
      try
      {
        valueSlot = slotContainer.getSlot(slotToResolve.getName());

        if (valueSlot == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s does not contain slot %s",
                slotContainerName, slotToResolve.getName()));

          throw new CannotMatchException(
              new SlotMatchFailure(slotContainer, slotToResolve));
        }
      }
      catch (CannotMatchException cme)
      {
        throw cme;
      }
      catch (Exception e)
      {
        /*
         * chunks and types will throw an exception if they dont have the slot,
         * but buffers will not..
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("%s does not contain slot %s",
              slotContainerName, slotToResolve.getName()));

        throw new CannotMatchException(
            new SlotMatchFailure(slotContainer, slotToResolve));
      }

      boolean locallyBound = false;
      String variableName = null;
      if (slotToResolve.isVariableValue())
      {
        /*
         * the conditional is variablized. is it bound already? if so, resolve.
         * We will test after this logic
         */
        variableName = ((String) slotToResolve.getValue()).toLowerCase();
        if (bindings.isBound(variableName))
        {

          Object value = bindings.get(variableName);
          slotToResolve.setValue(value);

          // test
          // test moved down below
          // if (!slotToResolve.matchesCondition(valueSlot.getValue()))
          // {
          // // cleanup. normally this isn't needed unless we're nested in
          // // logicals
          // throw new CannotMatchException(new SlotMatchFailure(null,
          // slotContainer, slotToResolve, valueSlot,
          // bindings.getSource(variableName)));
          // }
        }
        else if (slotToResolve.getCondition() == IConditionalSlot.EQUALS)
          /*
           * since the slot was not previously bound, we will do so here.
           */
          if (slotToResolve.matchesCondition(valueSlot.getValue()))
          {
          locallyBound = true;
          bindings.bind(variableName, valueSlot.getValue(), valueSlot);

          if (LOGGER.isDebugEnabled()) LOGGER.debug(
              String.format("Bound %s %s", variableName, valueSlot.getValue()));
          }
          else
          /*
           * it doesn't match, this is likely a slotName =toBeBound, but
           * container.slotName==null
           */
          throw new CannotMatchException(
              new SlotMatchFailure(slotContainer, slotToResolve, valueSlot));
      }

      /*
       * Or final test of the slot value against the conditional.
       */
      if (!slotToResolve.isVariableValue())
        /*
         * this will not test ISA correctly.
         */
        if (slotToResolve.getName().equalsIgnoreCase(ISlot.ISA))
        {
        /*
         * isa s must have a chunk as the container and a chunk type as the
         * value/
         */
        if (!(slotContainer instanceof ISymbolicChunk))
          throw new CannotMatchException(
              new SlotMatchFailure(slotContainer, slotToResolve, valueSlot));

        if (!(slotToResolve.getValue() instanceof IChunkType))
          throw new CannotMatchException(
              new SlotMatchFailure(slotContainer, slotToResolve, valueSlot));

        ISymbolicChunk chunk = (ISymbolicChunk) slotContainer;
        IChunkType ct = (IChunkType) slotToResolve.getValue();

        if (!chunk.isA(ct)) throw new CannotMatchException(
            new ChunkTypeMatchFailure(ct, chunk.getParentChunk()));

        }
        else if (!slotToResolve.matchesCondition(valueSlot.getValue()))
        {
        if (locallyBound) bindings.unbind(variableName);

        throw new CannotMatchException(
            new SlotMatchFailure(slotContainer, slotToResolve, valueSlot));
        }

    }

    return true;
  }

  /**
   * attempt to resolve all the bindings, returning the number of unresolved.
   * This is the main entry call for the {@link IRequest} class. An alternative
   * entry call is available for those that want to bind against a specific slot
   * container
   * {@link #bind(IModel, String, IUniqueSlotContainer, VariableBindings, boolean)}
   */
  public int bind(IModel model, VariableBindings bindings,
      boolean iterativeCall) throws CannotMatchException
  {
    if (_unresolved == null)
    {
      _unresolved = FastListFactory.newInstance();
      _unresolved.addAll(getConditionalAndLogicalSlots());
    }

    for (Iterator<ISlot> slots = _unresolved.iterator(); slots.hasNext();)
    {
      ISlot slot = slots.next();
      if (resolveSlot(slot, bindings, null, null))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Resolved %s", slot));

        slots.remove();
      }
    }

    if (_unresolved.size() > 0 && !iterativeCall)
    {
      /*
       * package the CME
       */
      Collection<IConditionalSlot> unresolved = new ArrayList<IConditionalSlot>(
          _unresolved.size());
      for (ISlot slot : _unresolved)
        if (slot instanceof IConditionalSlot)
          unresolved.add((IConditionalSlot) slot);

      throw new CannotMatchException(new UnresolvedVariablesMatchFailure(
          unresolved, bindings.getVariables(), null));
    }

    return _unresolved.size();
  }

  /**
   * bind the slot values in this request against those slots contained in the
   * container. This allows us to generally bind against anything that contains
   * a slot (chunk, chunktype, or buffer for queries)
   * 
   * @param model
   * @param container
   * @param bindings
   * @param iterativeCall
   * @return
   * @throws CannotMatchException
   */
  public int bind(IModel model, String containerName,
      IUniqueSlotContainer container, VariableBindings bindings,
      boolean iterativeCall) throws CannotMatchException
  {
    if (_unresolved == null)
    {
      _unresolved = FastListFactory.newInstance();
      _unresolved.addAll(getConditionalAndLogicalSlots());
    }

    for (Iterator<ISlot> slots = _unresolved.iterator(); slots.hasNext();)
    {
      ISlot slot = slots.next();
      if (resolveSlot(slot, bindings, containerName, container))
      {
        slots.remove();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Resolved %s", slot));
      }
    }

    if (_unresolved.size() > 0 && !iterativeCall)
    {
      /*
       * package the CME
       */
      Collection<IConditionalSlot> unresolved = new ArrayList<IConditionalSlot>(
          _unresolved.size());
      for (ISlot slot : _unresolved)
        if (slot instanceof IConditionalSlot)
          unresolved.add((IConditionalSlot) slot);

      throw new CannotMatchException(new UnresolvedVariablesMatchFailure(
          unresolved, bindings.getVariables(), null));
    }

    return _unresolved.size();
  }

  /**
   * attempt to resolve the slot values using a container
   * 
   * @param model
   * @param containerName
   * @param container
   * @param bindings
   * @param slots
   * @throws CannotMatchException
   */
  public void bindSlots(IModel model, String containerName,
      IUniqueSlotContainer container, VariableBindings bindings,
      Collection<ISlot> slots) throws CannotMatchException
  {
    for (ISlot slot : slots)
      resolveSlot(slot, bindings, containerName, container);
  }

  /**
   * bind and resolve as many slots in the collection as possible.
   * 
   * @throws CannotMatchException
   *           if there is a critical binding error
   */
  public void bindSlots(IModel model, VariableBindings bindings,
      Collection<ISlot> slots) throws CannotMatchException
  {
    for (ISlot slot : slots)
      resolveSlot(slot, bindings, null, null);
  }

  @Override
  public SlotBasedRequest clone()
  {
    return new SlotBasedRequest(_slots);
  }

  protected void setLocked(boolean locked)
  {
    if (_locked != locked)
    {
      _locked = locked;
      if (_locked)
        _slots = Collections.unmodifiableCollection(_slots);
      else
        _slots = new ArrayList<ISlot>(_slots);
    }
  }

  public void addSlot(ISlot slot)
  {
    if (_locked)
      throw new RuntimeException("Cannot modify a locked slot container");

    // _slots.add(slot.clone());

    if (slot instanceof IVariableNameSlot)
      _slots.add(slot.clone());
    else if (slot instanceof ILogicalSlot)
      _slots.add(slot.clone());
    else
      /*
       * why not just do slot.clone here? if this is a standard slot, we want to
       * create it as a conditional.
       */
      _slots.add(new DefaultConditionalSlot(slot));
  }

  // TODO:
  // possible breakage here. used to return all slots, now returns all slots
  // except for logic slots
  public Collection<? extends IConditionalSlot> getConditionalSlots()
  {
    Collection<IConditionalSlot> slots = FastListFactory.newInstance();
    for (ISlot slot : _slots)
      if (slot instanceof IConditionalSlot) slots.add((IConditionalSlot) slot);

    return slots;
    // return Collections.unmodifiableCollection(_slots);
  }

  public Collection<? extends ISlot> getConditionalAndLogicalSlots()
  {
    Collection<ISlot> slots = FastListFactory.newInstance();
    for (ISlot slot : _slots)
      if (slot instanceof IConditionalSlot || slot instanceof ILogicalSlot)
        slots.add(slot);
    // else LOGGER.error("Ignoring slot " + slot +
    // " because not conditional or logical");
    return slots;
  }

  public Collection<? extends ISlot> getSlots()
  {
    if (!_locked) return _slots;

    return Collections.unmodifiableCollection(_slots);
  }

  public void removeSlot(ISlot slot)
  {
    if (_locked)
      throw new RuntimeException("Cannot modify a locked slot container");

    _slots.remove(slot);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    if (container == null) if (_slots != null)
      container = new ArrayList<ISlot>(_slots.size() + 1);
    else
      container = new ArrayList<ISlot>();

    container.addAll(_slots);
    return container;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_slots == null ? 0 : _slots.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SlotBasedRequest other = (SlotBasedRequest) obj;
    if (_slots == null)
    {
      if (other._slots != null) return false;
    }
    else if (!_slots.equals(other._slots)) return false;
    return true;
  }

}
