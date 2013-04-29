/*
 * Created on Mar 19, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.compiler;

import java.util.Arrays;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.module.IModule;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.compiler.CompilationError;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.misc.CommonTreeException;

/**
 * verifies that all class_spec references are valid. specifically MODULE,
 * EXTENSION, PROXY_ACTION, PROXY_CONDITION
 * 
 * @author developer
 */
public class ClassVerifyingUnitCompiler implements IUnitCompiler
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ClassVerifyingUnitCompiler.class);

  /**
   * @see org.jactr.io.compiler.IUnitCompiler#compile(org.antlr.runtime.tree.CommonTree,
   *      java.util.Collection, java.util.Collection)
   */
  public void compile(CommonTree node, Collection<Exception> info, Collection<Exception> warnings,
      Collection<Exception> errors)
  {
    CommonTree classSpecNode = ASTSupport.getFirstDescendantWithType(node,
        JACTRBuilder.CLASS_SPEC);
    if (classSpecNode == null)
      errors.add(new CompilationError("Could not find class spec child of "
          + node, node));
    else
      try
      {
        Class type = Object.class;
        switch (node.getType())
        {
          case JACTRBuilder.MODULE:
            type = IModule.class;
            break;
          case JACTRBuilder.EXTENSION:
            type = IExtension.class;
            break;
          case JACTRBuilder.PROXY_ACTION:
            type = IAction.class;
            break;
          case JACTRBuilder.PROXY_CONDITION:
            type = ICondition.class;
            break;
        }

        tryToLoadClass(classSpecNode, classSpecNode.getText(), type);
      }
      catch (Exception e)
      {
        String message = "Could not load " + classSpecNode.getText() + " for "
            + node;
        if (LOGGER.isDebugEnabled()) LOGGER.debug(message, e);

        if (!(e instanceof CommonTreeException))
          errors.add(new CompilationError(message, classSpecNode, e));
        else if (e instanceof CompilationWarning)
          warnings.add(e);
        else
          errors.add(e);
      }
  }

  /**
   * will throw an exception if it doesn't work
   * 
   * @param className
   */
  protected void tryToLoadClass(CommonTree classSpecNode, String className,
      Class ofType) throws Exception
  {
    Class loadedClass = getClass().getClassLoader().loadClass(className);
    if (ofType != null)
      if (!ofType.isAssignableFrom(loadedClass))
        throw new RuntimeException("Loaded class " + loadedClass
            + " does not match required type " + ofType.getName());
  }

  /**
   * @see org.jactr.io.compiler.IUnitCompiler#getRelevantTypes()
   */
  public Collection<Integer> getRelevantTypes()
  {
    Integer[] types = { JACTRBuilder.MODULE, JACTRBuilder.EXTENSION,
        JACTRBuilder.PROXY_ACTION, JACTRBuilder.PROXY_CONDITION };

    return Arrays.asList(types);
  }

  /**
   * @see org.jactr.io.compiler.IUnitCompiler#postCompile()
   */
  public void postCompile()
  {

  }

  /**
   * @see org.jactr.io.compiler.IUnitCompiler#preCompile()
   */
  public void preCompile()
  {

  }

}
