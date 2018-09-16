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

import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.participant.IASTTrimmer;

/**
 * @author developer
 */
public class BasicASTTrimmer implements IASTTrimmer
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(BasicASTTrimmer.class);

  final private CommonTree _modelDescriptor;

  private Set<String>      _ignoreChunkTypes;

  private Set<String>      _ignoreChunks;

  private Set<String>      _ignoreProductions;

  public BasicASTTrimmer(CommonTree modelDescriptor)
  {
    _modelDescriptor = modelDescriptor;
    buildExclusionTables();
  }

  /**
   * 
   */
  private void buildExclusionTables()
  {
    _ignoreChunks = new TreeSet<String>();
    _ignoreChunkTypes = new TreeSet<String>();
    _ignoreProductions = new TreeSet<String>();
    if (_modelDescriptor != null)
    {
      _ignoreChunks.addAll(ASTSupport.getMapOfTrees(_modelDescriptor,
          JACTRBuilder.CHUNK).keySet());
      _ignoreChunkTypes.addAll(ASTSupport.getMapOfTrees(_modelDescriptor,
          JACTRBuilder.CHUNK_TYPE).keySet());
      _ignoreProductions.addAll(ASTSupport.getMapOfTrees(_modelDescriptor,
          JACTRBuilder.PRODUCTION).keySet());
    }
    if (LOGGER.isDebugEnabled())
      {
       LOGGER.debug("Will ignore chunks named : "+_ignoreChunks);
       LOGGER.debug("Will ignore chunktypes named : "+_ignoreChunkTypes);
       LOGGER.debug("Will ignore productions named : "+_ignoreProductions);
      }
  }

  /**
   * @see org.jactr.io.participant.IASTTrimmer#shouldIgnore(org.antlr.runtime.tree.CommonTree)
   */
  public boolean shouldIgnore(CommonTree element)
  {
    switch (element.getType())
    {
      case JACTRBuilder.CHUNK:
        return _ignoreChunks.contains(ASTSupport.getName(element).toLowerCase());
      case JACTRBuilder.CHUNK_TYPE:
        return _ignoreChunkTypes.contains(ASTSupport.getName(element).toLowerCase());
      case JACTRBuilder.PRODUCTION:
        return _ignoreProductions.contains(ASTSupport.getName(element).toLowerCase());
      default:
        return false;
    }
  }

}
