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
 * Created on Apr 21, 2005 by developer
 */

package org.jactr.io.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.compiler.JACTRCompiler;
import org.jactr.io.parser.CanceledException;

/**
 * @author developer TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultCompiler
{
  /**
   * Logger definition
   */

  static private transient Log                    LOGGER = LogFactory
                                                             .getLog(DefaultCompiler.class);

  private Map<Integer, Collection<IUnitCompiler>> _unitCompilerMap;

  /**
   * 
   */
  public DefaultCompiler()
  {
    super();
    _unitCompilerMap = new HashMap<Integer, Collection<IUnitCompiler>>();
    installDefaultCompilers();
  }

  protected void installDefaultCompilers()
  {
    addCompiler(new ClassVerifyingUnitCompiler());
  }

  public void addCompiler(IUnitCompiler compiler)
  {
    Collection<Integer> relevantTypes = compiler.getRelevantTypes();
    for (Integer i : relevantTypes)
    {
      Collection<IUnitCompiler> compilers = _unitCompilerMap.get(i);
      if (compilers == null)
      {
        compilers = new ArrayList<IUnitCompiler>();
        _unitCompilerMap.put(i, compilers);
      }
      compilers.add(compiler);
    }
  }

  public Collection<IUnitCompiler> getCompilers()
  {
    Collection<IUnitCompiler> compilers = new HashSet<IUnitCompiler>();
    for (Collection<IUnitCompiler> comps : _unitCompilerMap.values())
      compilers.addAll(comps);
    return compilers;
  }

  public void removeCompiler(IUnitCompiler compiler)
  {
    Collection<Integer> relevantTypes = compiler.getRelevantTypes();
    for (Integer i : relevantTypes)
    {
      Collection<IUnitCompiler> compilers = _unitCompilerMap.get(i);
      if (compilers != null) compilers.remove(compiler);
    }
  }
  
  
  public boolean compile(CommonTree modelTree, Collection<Exception> info,
      Collection<Exception> warnings, Collection<Exception> errors)
  {
    return compile(modelTree, info, warnings, errors, new CommonTreeNodeStream(modelTree));
  }

  public boolean compile(CommonTree modelTree, Collection<Exception> info,
      Collection<Exception> warnings, Collection<Exception> errors, TreeNodeStream stream)
  {
    boolean compiled = false;
    JACTRCompiler compiler = null;

    for (IUnitCompiler comp : getCompilers())
      comp.preCompile();

    try
    {
      compiler = new JACTRCompiler(stream);
      compiler.setUnitCompilerMap(_unitCompilerMap);
      compiler.model();
    }
    catch (CompilationWarning cw)
    {
      warnings.add(cw);
    }
    catch(CanceledException ce)
    {
      throw ce;
    }
    catch (Exception e)
    {
      errors.add(e);
    }
    finally
    {
      Collection<Exception> newErrors = compiler.getErrors();
      if (newErrors.size() == 0) compiled = true;
      errors.addAll(newErrors);
      warnings.addAll(compiler.getWarnings());

      for (IUnitCompiler comp : getCompilers())
        comp.postCompile();
    }
    
    return compiled;
  }
}
