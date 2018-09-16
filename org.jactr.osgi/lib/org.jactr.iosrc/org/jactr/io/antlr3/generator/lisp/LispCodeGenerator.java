/*
 * Created on May 31, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.generator.lisp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.parser.IParserImportDelegate;
import org.jactr.io.parser.ParserImportDelegateFactory;
import org.jactr.io.participant.ASTParticipantRegistry;
import org.jactr.io.participant.IASTParticipant;

public class LispCodeGenerator implements ICodeGenerator
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(LispCodeGenerator.class);

  public Collection<StringBuilder> generate(CommonTree root, boolean shouldTrim)
  {
    return generate(root, shouldTrim, new ArrayList<Exception>(),
        new ArrayList<Exception>());
  }

  @SuppressWarnings("unchecked")
  public Collection<StringBuilder> generate(CommonTree root,
      boolean shouldTrim, Collection<Exception> warnings,
      Collection<Exception> errors)
  {

    CommonTreeNodeStream nodes = new CommonTreeNodeStream(root);
    LispGenerator generator = new LispGenerator(nodes);

    /*
     * now we set up the trimmers. To do this we need to get the module
     * descriptors
     */
    if (shouldTrim)
    {
      // this is very likely not correct for trimming.

      IParserImportDelegate importer = ParserImportDelegateFactory
          .createDelegate((Object[]) null);

      for (CommonTree moduleNode : ASTSupport.getAllDescendantsWithType(root,
          JACTRBuilder.MODULE))
      {
        /*
         * now we need the class node
         */
        String className = ASTSupport.getFirstDescendantWithType(moduleNode,
            JACTRBuilder.CLASS_SPEC).getText();
        IASTParticipant participant = ASTParticipantRegistry
            .getParticipant(className);
        if (participant != null)
          generator.addTrimmer(participant.getTrimmer(importer));
      }

      for (CommonTree moduleNode : ASTSupport.getAllDescendantsWithType(root,
          JACTRBuilder.EXTENSION))
      {
        /*
         * now we need the class node
         */
        String className = ASTSupport.getFirstDescendantWithType(moduleNode,
            JACTRBuilder.CLASS_SPEC).getText();
        IASTParticipant participant = ASTParticipantRegistry
            .getParticipant(className);
        if (participant != null)
          generator.addTrimmer(participant.getTrimmer(importer));
      }
    }

    Collection<StringBuilder> rtn = null;
    String methodName = null;
    // who you gonna call..
    switch (root.getType())
    {
      case JACTRBuilder.MATCH_CONDITION:
        methodName = "pattern";
        break;
      case JACTRBuilder.PARAMETER:
        methodName = "parameter";
        break;
      case JACTRBuilder.SLOTS:
        methodName = "slots";
        break;
      case JACTRBuilder.PRODUCTION:
        methodName = "production";
        break;
      case JACTRBuilder.BUFFER:
        methodName = "buffer";
        break;
      case JACTRBuilder.CHUNK_TYPE:
        methodName = "chunktype";
        break;
      case JACTRBuilder.CHUNK:
        methodName = "chunk";
        break;
      case JACTRBuilder.EXTENSION:
        methodName = "extension";
        break;
      case JACTRBuilder.MODEL:
        methodName = "model";
        break;
      case JACTRBuilder.MODULE:
        methodName = "module";
        break;
      default:
        throw new IllegalArgumentException(root.getType()
            + " is not a valid type for code generation");
    }

    // get the method via reflection
    try
    {
      Method method = LispGenerator.class.getMethod(methodName, new Class[0]);
      rtn = (Collection<StringBuilder>) method.invoke(generator, new Object[0]);
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("Could not find method " + methodName, e);
      errors.add(e);
      return Collections.EMPTY_LIST;
    }

    return rtn;
  }

  public Collection<StringBuilder> comment(String message)
  {
    StringBuilder sb = new StringBuilder("#| ");
    sb.append(message);
    sb.append(" |#");
    return Arrays.asList(sb);
  }

}
