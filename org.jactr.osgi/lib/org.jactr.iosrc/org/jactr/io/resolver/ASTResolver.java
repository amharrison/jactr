/*
 * Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * Created on Jun 4, 2005 by developer
 */

package org.jactr.io.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Predicate;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.IBufferAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.ProxyAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.action.SetAction;
import org.jactr.core.production.action.StopAction;
import org.jactr.core.production.condition.ChunkCondition;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.ProxyCondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.condition.VariableCondition;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.scripting.action.ScriptableAction;
import org.jactr.scripting.condition.ScriptableCondition;

public class ASTResolver
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(ASTResolver.class);

  static private ASTSupport          _support = new ASTSupport();

  static public CommonTree toAST(Object obj, boolean fullResolution)
  {
    if (obj instanceof IModel)
      return toAST((IModel) obj, fullResolution);
    else if (obj instanceof IChunkType)
      return toAST((IChunkType) obj, fullResolution);
    else if (obj instanceof IChunk && !((IChunk) obj).hasBeenDisposed())
      return toAST((IChunk) obj, false);
    else if (obj instanceof IProduction)
      return toAST((IProduction) obj);
    else if (obj instanceof IActivationBuffer)
      return toAST((IActivationBuffer) obj);
    else if (obj instanceof IExtension)
      return toAST((IExtension) obj);
    else if (obj instanceof IModule)
      return toAST((IModule) obj);
    else
      throw new RuntimeException("No known transformation for "
          + obj.getClass().getName());
  }

  /**
   * create a version of the model.with the option to generate just the model
   * header (fullResoltuion), or to filter out content when generating a full
   * dump
   * 
   * @param model
   * @param fullResolution
   *          if false, just the model & parameters is generated. filters are
   *          ignored.
   * @param productionFilter
   * @param chunkTypeFilter
   * @param chunkFilter
   * @return
   */
  static public CommonTree toAST(IModel model, boolean fullResolution,
      Predicate<IProduction> productionFilter,
      Predicate<IChunkType> chunkTypeFilter, Predicate<IChunk> chunkFilter)
  {
    if (productionFilter == null) productionFilter = p -> true;
    if (chunkTypeFilter == null) chunkTypeFilter = c -> true;
    if (chunkFilter == null) chunkFilter = c -> true;

    /**
     * lock so that we are the only one right now
     */
    ReadWriteLock lock = model.getLock();

    lock.readLock().lock();
    try
    {
      CommonTree md = _support.createModelTree(model.getName());

      // insert all the parameters
      setParameters(
          ASTSupport.getFirstDescendantWithType(md, JACTRBuilder.PARAMETERS),
          model);

      if (fullResolution)
      {
        CommonTree modules = ASTSupport.getFirstDescendantWithType(md,
            JACTRBuilder.MODULES);
        for (IModule module : model.getModules())
        {
          CommonTree modDesc = toAST(module);
          modules.addChild(modDesc);
        }

        // if full we add extensions, buffers, chunks, chunktype, and
        // productions
        CommonTree extensions = ASTSupport.getFirstDescendantWithType(md,
            JACTRBuilder.EXTENSIONS);

        for (IExtension extension : model.getExtensions())
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Generating AST for extension "
                + extension.getClass().getName());

          CommonTree ed = toAST(extension);

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Returned ast " + ed.toStringTree());

          extensions.addChild(ed);
        }
        // chunktypes will add the chunks for us
        CommonTree decWrapper = ASTSupport.getFirstDescendantWithType(md,
            JACTRBuilder.DECLARATIVE_MEMORY);
        Set<IChunkType> resolved = new HashSet<IChunkType>();
        List<CommonTree> chunkTypes = new ArrayList<CommonTree>();
        try
        {
          for (IChunkType ct : model.getDeclarativeModule().getChunkTypes()
              .get())
            if (chunkTypeFilter.test(ct))
            {
              List<CommonTree> chunkTypesList = toOrderedAST(ct, resolved,
                  chunkTypeFilter, chunkFilter);
              chunkTypes.addAll(chunkTypesList);
            }
        }
        catch (InterruptedException ie)
        {
          LOGGER.error("Interrupted ", ie);
        }
        catch (ExecutionException e)
        {
          LOGGER.error("Execution ", e);
        }

        // now we can dump them
        for (CommonTree ctNode : chunkTypes)
          decWrapper.addChild(ctNode);

        chunkTypes.clear();

        // productions
        CommonTree proWrapper = ASTSupport.getFirstDescendantWithType(md,
            JACTRBuilder.PROCEDURAL_MEMORY);
        try
        {
          for (IProduction p : model.getProceduralModule().getProductions()
              .get())
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("generating AST for production " + p);
            CommonTree pd = toAST(p);
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("returned ast " + pd.toStringTree());
            proWrapper.addChild(pd);
          }
        }
        catch (InterruptedException ie)
        {
          LOGGER.error("Interrupted ", ie);
        }
        catch (ExecutionException e)
        {
          LOGGER.error("Execution ", e);
        }

        // buffers
        CommonTree buffersWrapper = ASTSupport.getFirstDescendantWithType(md,
            JACTRBuilder.BUFFERS);
        Map<String, CommonTree> chunkTypeNodes = ASTSupport.getMapOfTrees(
            decWrapper, JACTRBuilder.CHUNK_TYPE);
        for (IActivationBuffer buffer : model.getActivationBuffers())
        {
          buffersWrapper.addChild(toAST(buffer));
          /*
           * since the chunks in the buffer aren't in the model, they won't be
           * serialized correctly, so we grab them now and stick them under
           * their respective chunktype
           */
          for (IChunk source : buffer.getSourceChunks())
          {
            CommonTree sourceChunk = toAST(source, false);
            CommonTree chunkType = chunkTypeNodes.get(source.getSymbolicChunk()
                .getChunkType().getSymbolicChunkType().getName().toLowerCase());

            if (chunkType != null)
              ASTSupport.getFirstDescendantWithType(chunkType,
                  JACTRBuilder.CHUNKS).addChild(sourceChunk);
          }
        }
      }
      return md;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  /**
   * create an AST description of the model and optionally, all its children
   * 
   * @param model
   * @param fullResolution
   * @return
   */
  static public CommonTree toAST(IModel model, boolean fullResolution)
  {
    return toAST(model, fullResolution, null, null, null);
  }

  /**
   * return a list of all the commonTrees for this chunktype and its parents
   * sorted in dependency order, with chunkType's commonTree last
   * 
   * @param chunkType
   * @return
   */
  @SuppressWarnings("unchecked")
  static protected List<CommonTree> toOrderedAST(IChunkType chunkType,
      Set<IChunkType> alreadyConverted, Predicate<IChunkType> chunkTypeFilter,
      Predicate<IChunk> chunkFilter)
  {
    if (alreadyConverted.contains(chunkType))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Already converted " + chunkType);
      return Collections.EMPTY_LIST;
    }

    ArrayList<CommonTree> rtn = new ArrayList<CommonTree>();
    Collection<IChunkType> parents = chunkType.getSymbolicChunkType()
        .getParents();
    // if (parent != null)
    for (IChunkType parent : parents)
      if (chunkTypeFilter.test(parent))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("resolving parent " + parent);
        rtn.addAll(toOrderedAST(parent, alreadyConverted, chunkTypeFilter,
            chunkFilter));
      }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Resolving " + chunkType);
    alreadyConverted.add(chunkType);

    if (chunkTypeFilter.test(chunkType))
      rtn.add(toAST(chunkType, chunkFilter));
    return rtn;
  }

  /**
   * return the AST describing this production
   * 
   * @param production
   * @return
   */
  static public CommonTree toAST(IProduction production)
  {
    ISymbolicProduction sp = production.getSymbolicProduction();
    ISubsymbolicProduction ssp = production.getSubsymbolicProduction();

    CommonTree pd = _support.createProductionTree(sp.getName());

    setParameters(
        ASTSupport.getFirstDescendantWithType(pd, JACTRBuilder.PARAMETERS), ssp);

    // now we take care of the actions & conditions..
    setConditions(pd, sp.getConditions());
    setActions(pd, sp.getActions());

    return pd;
  }

  /**
   * return the AST describing this chunk
   * 
   * @param chunk
   * @return
   */
  static public CommonTree toAST(IChunk chunk, boolean skipParameters)
  {
    ISymbolicChunk sc = chunk.getSymbolicChunk();
    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();

    CommonTree cd = _support.createChunkTree(sc.getName(), sc.getChunkType()
        .getSymbolicChunkType().getName());

    if (!skipParameters)
      setParameters(
          ASTSupport.getFirstDescendantWithType(cd, JACTRBuilder.PARAMETERS),
          ssc);

    // slots
    setSlots(ASTSupport.getFirstDescendantWithType(cd, JACTRBuilder.SLOTS), sc);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("generated " + cd.toStringTree() + " for chunk " + chunk);
    return cd;
  }

  static public CommonTree toAST(IChunkType chunkType, boolean fullResolution)
  {
    /*
     * if we aren't doing full res, filter is null. if we are, accept all
     */
    return toAST(chunkType, !fullResolution ? null : (c) -> true);
  }

  /**
   * return the AST describing the chunktype and optionally all it's immediate
   * chunks
   * 
   * @param chunkType
   * @param fullResolution
   * @return
   */
  static public CommonTree toAST(IChunkType chunkType,
      Predicate<IChunk> chunkFilter)
  {
    ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
    ISubsymbolicChunkType ssct = chunkType.getSubsymbolicChunkType();

    Collection<String> parentChunkTypeNames = new ArrayList<String>();

    for (IChunkType parent : sct.getParents())
      parentChunkTypeNames.add(parent.getSymbolicChunkType().getName());

    CommonTree cd = _support.createChunkTypeTree(sct.getName(),
        parentChunkTypeNames);

    setParameters(
        ASTSupport.getFirstDescendantWithType(cd, JACTRBuilder.PARAMETERS),
        ssct);

    // slots
    setSlots(ASTSupport.getFirstDescendantWithType(cd, JACTRBuilder.SLOTS), sct);

    if (chunkFilter != null)
    {
      CommonTree chunks = ASTSupport.getFirstDescendantWithType(cd,
          JACTRBuilder.CHUNKS);
      /*
       * process the chunks, but only include them if they are a direct child of
       * chunk type. leave descendants for the derived chunktypes to return
       */
      for (IChunk chunk : sct.getChunks())
        if (chunk.isAStrict(chunkType))
          if (chunkFilter.test(chunk)) chunks.addChild(toAST(chunk, false));
    }
    return cd;
  }

  /**
   * return the AST describing this buffer
   * 
   * @param buffer
   * @return
   */
  static public CommonTree toAST(IActivationBuffer buffer)
  {
    CommonTree bd = _support.createBufferTree(buffer.getName());

    /*
     * some buffers may have no parameters.. this is kind of stupid they all
     * should
     */
    if (buffer instanceof IParameterized)
      setParameters(
          ASTSupport.getFirstDescendantWithType(bd, JACTRBuilder.PARAMETERS),
          (IParameterized) buffer);

    /*
     * now for the contents of the buffer..
     */
    CommonTree chunks = ASTSupport.getFirstDescendantWithType(bd,
        JACTRBuilder.CHUNKS);
    for (IChunk c : buffer.getSourceChunks())
      try
      {
        String chunkName = c.getSymbolicChunk().getName();
        chunks.addChild(_support.create(JACTRBuilder.CHUNK_IDENTIFIER,
            chunkName));
      }
      catch (Exception e)
      {
        LOGGER
            .debug(
                "chunk access exception, most likely due to chunk merging/encoding during AST generation. Safe to ignore ",
                e);
      }
    return bd;
  }

  /**
   * return the AST describing this extension
   * 
   * @param extension
   * @return
   */
  static public CommonTree toAST(IExtension extension)
  {
    CommonTree ed = _support
        .createExtensionTree(extension.getClass().getName());
    setParameters(
        ASTSupport.getFirstDescendantWithType(ed, JACTRBuilder.PARAMETERS),
        extension);
    return ed;
  }

  static public CommonTree toAST(IModule module)
  {
    CommonTree md = _support.createModuleTree(module.getClass().getName());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("created module ast " + md.toStringTree());
    if (module instanceof IParameterized)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Setting parameters");
      setParameters(
          ASTSupport.getFirstDescendantWithType(md, JACTRBuilder.PARAMETERS),
          (IParameterized) module);
    }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("returning module ast " + md.toStringTree());
    return md;
  }

  static protected void setConditions(CommonTree pd,
      Collection<ICondition> conditions)
  {
    CommonTree condWrapper = ASTSupport.getFirstDescendantWithType(pd,
        JACTRBuilder.CONDITIONS);
    for (ICondition cond : conditions)
    {
      CommonTree desc = toAST(cond);
      if (desc != null)
      {
        condWrapper.addChild(desc);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("generated " + desc.toStringTree() + " for condition "
              + cond);
      }
      else if (LOGGER.isWarnEnabled())
        LOGGER.warn("Could not resolve " + cond + " toAST");
    }
  }

  static public CommonTree toAST(ICondition condition)
  {
    CommonTree desc = null;
    if (condition instanceof IBufferCondition)
    {
      String bufferName = ((IBufferCondition) condition).getBufferName();
      if (condition instanceof QueryCondition)
        desc = _support.createQueryTree(bufferName);
      else
      {
        CommonTree content = null;
        if (condition instanceof ChunkCondition)
          content = _support.create(JACTRBuilder.CHUNK_IDENTIFIER,
              ((ChunkCondition) condition).getChunk().getSymbolicChunk()
                  .getName());
        else if (condition instanceof ChunkTypeCondition)
        {
          // chunktype may be null
          if (((ChunkTypeCondition) condition).getChunkType() != null)
            content = _support.create(JACTRBuilder.CHUNK_TYPE_IDENTIFIER,
                ((ChunkTypeCondition) condition).getChunkType()
                    .getSymbolicChunkType().getName());

        }
        else if (condition instanceof VariableCondition)
          content = _support.create(JACTRBuilder.VARIABLE,
              ((VariableCondition) condition).getVariableName());

        desc = _support.createMatchTree(bufferName, content);
      }
    }
    else if (condition instanceof ProxyCondition)
    {
      // proxy just a tracked node with the text as the class name
      String className = ((ProxyCondition) condition).getDelegateClassName();
      desc = _support.createProxyConditionTree(className);
      ICondition actual = ((ProxyCondition) condition).getDelegate();
      if (actual instanceof ISlotContainer) condition = actual;
    }
    else if (condition instanceof ScriptableCondition)
    {
      String script = ((ScriptableCondition) condition).getScript();
      desc = _support.createScriptableConditionTree(
          ((ScriptableCondition) condition).getFactory().getLanguageName(),
          script);
    }

    if (condition instanceof ISlotContainer && desc != null)
      setSlots(ASTSupport.getFirstDescendantWithType(desc, JACTRBuilder.SLOTS),
          (ISlotContainer) condition);

    return desc;
  }

  static protected void setActions(CommonTree pd, Collection<IAction> actions)
  {
    CommonTree actionsWrapper = ASTSupport.getFirstDescendantWithType(pd,
        JACTRBuilder.ACTIONS);
    for (IAction act : actions)
    {
      CommonTree actionNode = toAST(act);

      if (actionNode != null)
      {
        actionsWrapper.addChild(actionNode);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("generated " + actionNode.toStringTree()
              + " for action " + act);
      }
      else if (LOGGER.isWarnEnabled())
        LOGGER.warn("Could not translate " + act + " toAST");
    }
  }

  static public CommonTree toAST(IAction action)
  {
    CommonTree actionNode = null;
    String bufferName = null;
    if (action instanceof IBufferAction)
      bufferName = ((IBufferAction) action).getBufferName();

    if (action instanceof StopAction)
      actionNode = _support.createProxyActionTree(StopAction.class.getName());
    else if (action instanceof ProxyAction)
    {
      // proxy just a tracked node with the text as the class name
      String className = ((ProxyAction) action).getDelegateClassName();
      actionNode = _support.createProxyActionTree(className);
      IAction actual = ((ProxyAction) action).getDelegate();
      if (actual instanceof ISlotContainer) action = actual;
    }
    else if (action instanceof ScriptableAction)
    {
      String script = ((ScriptableAction) action).getScript();
      actionNode = _support.createScriptableActionTree(
          ((ScriptableAction) action).getFactory().getLanguageName(), script);
    }
    else if (action instanceof AddAction)
    {
      Object ref = ((AddAction) action).getReferant();
      CommonTree content = null;
      if (ref instanceof String) // variable
        content = _support.create(JACTRBuilder.VARIABLE, (String) ref);
      else if (ref instanceof IChunk)
        content = _support.create(JACTRBuilder.CHUNK_IDENTIFIER, ((IChunk) ref)
            .getSymbolicChunk().getName());
      else if (ref instanceof IChunkType)
        content = _support.create(JACTRBuilder.CHUNK_TYPE_IDENTIFIER,
            ((IChunkType) ref).getSymbolicChunkType().getName());

      actionNode = _support.createAddTree(bufferName, content);
    }
    else if (action instanceof SetAction)
    {
      Object ref = ((SetAction) action).getReferant();
      CommonTree content = null;
      if (ref instanceof String) // variable
        content = _support.create(JACTRBuilder.VARIABLE, (String) ref);
      else if (ref instanceof IChunk)
        content = _support.create(JACTRBuilder.CHUNK_IDENTIFIER, ((IChunk) ref)
            .getSymbolicChunk().getName());

      actionNode = _support.createSetTree(bufferName, content);
    }
    else if (action instanceof RemoveAction)
      actionNode = _support.createRemoveTree(bufferName, null);
    else if (action instanceof ModifyAction)
      actionNode = _support.createModifyTree(bufferName);
    else if (action instanceof OutputAction)
      actionNode = _support.createOutputAction(((OutputAction) action)
          .getText());

    // assign the slots
    if (action instanceof ISlotContainer)
      setSlots(
          ASTSupport.getFirstDescendantWithType(actionNode, JACTRBuilder.SLOTS),
          (ISlotContainer) action);

    return actionNode;
  }

  /**
   * @param desc
   * @param sc
   */
  static public void setSlots(CommonTree desc, ISlotContainer sc)
  {
    if (desc == null) return;

    if (!(desc.getType() == JACTRBuilder.SLOTS || desc.getType() == JACTRBuilder.LOGIC))
      throw new IllegalArgumentException(desc + " must be of type SLOTS "
          + JACTRBuilder.SLOTS + " or LOGIC " + JACTRBuilder.LOGIC);
    for (ISlot slot : sc.getSlots())
      if (slot instanceof ILogicalSlot)
      {
        ILogicalSlot ls = (ILogicalSlot) slot;
        CommonTree lsTree = _support.createLogicalSlot(ls.getOperator());
        setSlots(lsTree, ls);
        desc.addChild(lsTree);
      }
      else
        desc.addChild(toAST(slot));
  }

  static private CommonTree toAST(ISlot slot)
  {
    if (slot instanceof ILogicalSlot)
      throw new IllegalArgumentException(slot
          + " is of type ILogicalSlot; that isn't handled here.");

    String slotNameStr = slot.getName();
    Object actualSlotValue = slot.getValue();

    /*
     * don't know where this started.. i should find all occurences in the code
     * and get ride of them TODO remove slot containing slot support
     */
    if (actualSlotValue instanceof ISlot)
      actualSlotValue = ((ISlot) actualSlotValue).getValue();

    Object slotValue = actualSlotValue;
    if (slotValue == null) slotValue = "null";

    CommonTree slotName = null;
    /*
     * support for variable slot names
     */
    if (slotNameStr.startsWith("="))
      slotName = _support.create(JACTRBuilder.VARIABLE, slotNameStr);
    else
      slotName = _support.create(JACTRBuilder.NAME, slotNameStr);

    // now we use the actualSlotValue to determine what token type the value
    // should be
    CommonTree content = null;
    int newType = JACTRBuilder.IDENTIFIER;
    if (slotValue instanceof Number)
      newType = JACTRBuilder.NUMBER;
    else if (slotValue instanceof String || slotValue instanceof StringBuilder)
    {
      // stringbuilder to handle string literals in the builder
      String str = slotValue.toString();
      if (str.startsWith("="))
        newType = JACTRBuilder.VARIABLE;
      else
        newType = JACTRBuilder.STRING;
    }
    content = _support.create(newType, slotValue.toString());

    CommonTree sd = null;
    if (slot instanceof IConditionalSlot)
    {
      CommonTree condition = null;
      int cond = ((IConditionalSlot) slot).getCondition();
      switch (cond)
      {
        case IConditionalSlot.EQUALS:
          condition = _support.create(JACTRBuilder.EQUALS, "=");
          break;
        case IConditionalSlot.LESS_THAN:
          condition = _support.create(JACTRBuilder.LT, "<");
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          condition = _support.create(JACTRBuilder.LTE, "<=");
          break;
        case IConditionalSlot.GREATER_THAN:
          condition = _support.create(JACTRBuilder.GT, ">");
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          condition = _support.create(JACTRBuilder.GTE, ">=");
          break;
        case IConditionalSlot.NOT_EQUALS:
          condition = _support.create(JACTRBuilder.NOT, "!=");
          break;
        case IConditionalSlot.WITHIN:
          condition = _support.create(JACTRBuilder.WITHIN, "<>");
          break;
      }
      sd = _support.createSlot(slotName, condition, content);
    }
    else
      sd = _support.createSlot(slotName, content);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("generated ast:" + sd.toStringTree() + " for slot:" + slot);
    return sd;
  }

  /**
   * @param actrDesc
   * @param parameterized
   */
  static protected void setParameters(CommonTree parameters,
      IParameterized parameterized)
  {
    if (parameters.getType() != JACTRBuilder.PARAMETERS)
      throw new IllegalArgumentException(parameters.toStringTree()
          + " must be of type PARAMETERS " + JACTRBuilder.PARAMETERS);

    Collection<String> setable = parameterized.getSetableParameters();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("attempting to set " + setable.size() + " parameters");
    for (String parameterName : setable)
    {
      Object parameterValue = parameterized.getParameter(parameterName);
      if (parameterValue == null) parameterValue = "null";

      CommonTree p = _support.create(JACTRBuilder.PARAMETER, "parameter");
      p.addChild(_support.create(JACTRBuilder.NAME, parameterName));
      p.addChild(_support.create(JACTRBuilder.STRING, parameterValue.toString()));

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("adding p:" + p.toStringTree());

      parameters.addChild(p);
    }
  }
}
