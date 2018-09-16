/*
 * Created on Apr 4, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.participant.impl;

import java.util.Collection;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.IInstallable;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.misc.DetailedCommonTree;
import org.jactr.io.participant.IASTInjector;

/**
 * @author developer
 */
public class BasicASTInjector implements IASTInjector
{
  /**
   * logger definition
   */
  static private final Log                    LOGGER = LogFactory
                                                         .getLog(BasicASTInjector.class);

  final private CommonTree                    _modelDescriptor;

  final private Class<? extends IInstallable> _participantClass;

  final private Map<String, String>           _parameterMap;

  public BasicASTInjector(CommonTree modelDescriptor,
      Class<? extends IInstallable> participantClass,
      Map<String, String> parameterMap)
  {
    _modelDescriptor = modelDescriptor;
    /*
     * we no longer clear the locations since we cary the url of the origin, we
     * can more accurately differentiate local or imported nodes
     */
    // if (_modelDescriptor != null) clearLocations(_modelDescriptor);
    _parameterMap = parameterMap;
    _participantClass = participantClass;
  }

  /**
   * @see org.jactr.io.participant.IASTInjector#inject(org.antlr.runtime.tree.CommonTree)
   */
  public void inject(CommonTree modelDescriptor, boolean importBuffers)
  {
    importAll(_modelDescriptor, modelDescriptor, importBuffers);
  }

  public void injectParameters(CommonTree rootNode)
  {
    if (_parameterMap != null && _participantClass != null)
    {
      ASTSupport support = new ASTSupport();
      for (Map.Entry<String, String> parameter : _parameterMap.entrySet())
        support.setParameter(rootNode, parameter.getKey(),
            parameter.getValue(), false); // don't overwrite if it exists
      // already
    }
  }

  /**
   * import chunktypes from source to dest. If dest already contains a matching
   * (named) chunk-type, dest will be used instead and it will steal the chunks
   * from source.
   * 
   * @param sourceDeclarativeMemory
   * @param destinationDeclarativeMemory
   */
  static public void importChunkTypes(CommonTree sourceDeclarativeMemory,
      CommonTree destinationDeclarativeMemory)
  {
    // snag all the chuhnktypes
    Collection<CommonTree> importChunkTypes = ASTSupport.getTrees(
        sourceDeclarativeMemory, JACTRBuilder.CHUNK_TYPE);
    Map<String, CommonTree> destChunkTypes = ASTSupport.getMapOfTrees(
        destinationDeclarativeMemory, JACTRBuilder.CHUNK_TYPE);

    for (CommonTree chunkTypeTree : importChunkTypes)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Importing chunktypes " + chunkTypeTree.toStringTree());

      String name = ASTSupport.getName(chunkTypeTree).toLowerCase();
      CommonTree destChunkType = destChunkTypes.get(name);

      if (destChunkType != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Already have chunk type " + name
              + ", stealing children");

        /*
         * lets replace the slots and parameters
         * @bug no. We want to keep the local version, replace the imported
         * one..
         */
        // steal(chunkTypeTree, destChunkType, JACTRBuilder.SLOTS);
        // steal(chunkTypeTree, destChunkType, JACTRBuilder.PARAMETERS);

        /*
         * and now the children
         */
        CommonTree chunksWrapper = ASTSupport.getFirstDescendantWithType(
            destChunkType, JACTRBuilder.CHUNKS);
        Map<String, CommonTree> existingChunks = ASTSupport.getMapOfTrees(
            chunksWrapper, JACTRBuilder.CHUNK);
        Collection<CommonTree> chunksToSteal = ASTSupport.getTrees(
            chunkTypeTree, JACTRBuilder.CHUNK);

        for (CommonTree chunkToInsert : chunksToSteal)
        {
          String chunkName = ASTSupport.getName(chunkToInsert).toLowerCase();
          if (!existingChunks.containsKey(chunkName))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(name + " does not already have chunk " + chunkName
                  + ", adding");
            chunksWrapper.addChild(chunkToInsert);
          }
          else
            /*
             * we have to find the exact child, and replace it
             */
            for (int i = 0; i < chunksWrapper.getChildCount(); i++)
            {
              CommonTree childToReplace = (CommonTree) chunksWrapper
                  .getChild(i);
              if (ASTSupport.getName(childToReplace)
                  .equalsIgnoreCase(chunkName))
              {
                chunksWrapper.setChild(i, chunkToInsert);
                if (LOGGER.isDebugEnabled())
                  LOGGER.debug(String.format(
                      "Replaced %s at %d with imported version", chunkName, i));
              }
            }
        }
      }
      else
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "%s doesn't already exist, importing entirely", name));

        ASTSupport.addChunkType(destinationDeclarativeMemory, chunkTypeTree);
      }
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Contents of declarative "
          + ASTSupport.getFirstDescendantWithType(destinationDeclarativeMemory,
              JACTRBuilder.DECLARATIVE_MEMORY).toStringTree());
  }

  private void steal(CommonTree srcTree, CommonTree destTree, int nodeType)
  {
    CommonTree toSteal = null;
    for (int i = 0; i < srcTree.getChildCount(); i++)
      if (srcTree.getChild(i).getType() == nodeType || nodeType == -1)
      {
        toSteal = (CommonTree) srcTree.getChild(i);
        break;
      }

    if (toSteal == null) return;

    for (int i = 0; i < destTree.getChildCount(); i++)
      if (destTree.getChild(i).getType() == nodeType || nodeType == -1)
      {
        destTree.setChild(i, toSteal);
        break;
      }
  }

  protected void importBuffers(CommonTree srcModel, CommonTree destModel)
  {
    // and buffers
    Map<String, CommonTree> importBuffers = ASTSupport.getMapOfTrees(srcModel,
        JACTRBuilder.BUFFER);
    CommonTree buffers = ASTSupport.getFirstDescendantWithType(destModel,
        JACTRBuilder.BUFFERS);

    Map<String, CommonTree> existingBuffers = ASTSupport.getMapOfTrees(buffers,
        JACTRBuilder.BUFFER);

    for (CommonTree bufferTree : importBuffers.values())
    {
      String bufferName = ASTSupport.getName(bufferTree);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Importing buffer  " + bufferTree.toStringTree());

      CommonTree existingBuffer = existingBuffers.get(bufferName.toLowerCase());

      if (existingBuffer == null) buffers.addChild(bufferTree);
      else
      {
        /*
         * if the local (dest) doesn't have any parameters, steal them.
         */
        CommonTree originalParameters = ASTSupport.getFirstDescendantWithType(
            existingBuffer, JACTRBuilder.PARAMETERS);
        CommonTree newParameters = ASTSupport.getFirstDescendantWithType(
            bufferTree, JACTRBuilder.PARAMETERS);
        if ((originalParameters == null || originalParameters.getChildCount() == 0)
            && newParameters != null && newParameters.getChildCount() > 0)
          steal(newParameters, originalParameters, JACTRBuilder.PARAMETER);
      }

    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Contents of BUFFERS : " + buffers.toStringTree());
  }

  protected void importProductions(CommonTree srcModel, CommonTree destModel)
  {
    // and productions
    Map<String, CommonTree> importProductions = ASTSupport.getMapOfTrees(
        srcModel, JACTRBuilder.PRODUCTION);
    Map<String, CommonTree> currentProductions = ASTSupport.getMapOfTrees(
        destModel, JACTRBuilder.PRODUCTION);

    for (CommonTree productionTree : importProductions.values())
    {
      String name = ASTSupport.getName(productionTree).toLowerCase();
      if (!currentProductions.containsKey(name))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Importing p " + productionTree.toStringTree());
        ASTSupport.addProduction(destModel, productionTree);
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("Already contains production " + name);
    }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Contents of declarative "
          + ASTSupport.getFirstDescendantWithType(destModel,
              JACTRBuilder.PROCEDURAL_MEMORY).toStringTree());
  }

  protected void importAll(CommonTree srcModel, CommonTree modelDescriptor,
      boolean importBuffers)
  {
    if (srcModel != null)
    {
      importChunkTypes(srcModel, modelDescriptor);
      importProductions(srcModel, modelDescriptor);
      if (importBuffers) importBuffers(srcModel, modelDescriptor);
    }
  }

  /**
   * clear the location information of imported nodes.
   * 
   * @param root
   */
  @Deprecated
  protected void clearLocations(CommonTree root)
  {
    if (root instanceof DetailedCommonTree)
    {
      ((DetailedCommonTree) root).setStartOffset(-1);
      ((DetailedCommonTree) root).setEndOffset(-1);
    }

    root.setTokenStartIndex(-1);
    root.setTokenStopIndex(-1);
    CommonToken token = (CommonToken) root.getToken();
    if (token != null)
    {
      token.setLine(-1);
      token.setStartIndex(-1);
      token.setStopIndex(-1);
      token.setCharPositionInLine(-1);
      token.setTokenIndex(-1);
    }

    for (int i = 0; i < root.getChildCount(); i++)
      clearLocations((CommonTree) root.getChild(i));
  }
}
