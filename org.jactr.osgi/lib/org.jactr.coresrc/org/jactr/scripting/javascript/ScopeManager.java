/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.scripting.javascript;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.jactr.core.model.IModel;

/**
 * Maps javascript contexts to a model
 * 
 * @author harrison
 * @created March 3, 2003
 */
public class ScopeManager
{

  private static ScriptContext              _publicScope;

  private static Map<IModel, ScriptContext> _scopeMap = Collections
      .synchronizedMap(new WeakHashMap<IModel, ScriptContext>());

  private static ScriptEngine               _engine;

  /**
   * Gets the publicScope attribute of the ScopeManager class
   * 
   * @return The publicScope value
   */
  public static ScriptContext getPublicScope()
  {
    createIfNecessary();
    return _publicScope;
  }

  public static ScriptEngine getEngine()
  {
    createIfNecessary();
    return _engine;
  }

  private static void createIfNecessary()
  {
    synchronized (ScopeManager.class)
    {
      if (_engine != null && _publicScope != null) return;

      _engine = new ScriptEngineManager().getEngineByName("JavaScript");
      _publicScope = _engine.getContext();

      defineVariable(_publicScope, "out", System.out);
      defineVariable(_publicScope, "err", System.err);
    }
  }

  /**
   * Gets the scopeForModel attribute of the ScopeManager class
   * 
   * @param m
   *          Description of the Parameter
   * @return The scopeForModel value
   */
  public static ScriptContext getScopeForModel(IModel m)
  {
    synchronized (_scopeMap)
    {
      ScriptContext sc = _scopeMap.get(m);
      if (sc == null)
      {
        sc = newScope(getPublicScope());
        _scopeMap.put(m, sc);
      }
      return sc;
    }
  }

  static public ScriptContext newScope(ScriptContext scriptable)
  {
    ScriptContext newContext = new SimpleScriptContext();
    newContext.setBindings(scriptable.getBindings(ScriptContext.ENGINE_SCOPE),
        ScriptContext.ENGINE_SCOPE);

    return newContext;
  }

  /**
   * @param scope
   * @param variableName
   * @param object
   */
  static public void defineVariable(ScriptContext scope, String variableName,
      Object object)
  {
    scope.setAttribute(variableName, object, ScriptContext.ENGINE_SCOPE);
  }

  static public Object getVariable(ScriptContext scope, String variableName)
  {
    return scope.getAttribute(variableName);
  }

  /**
   * Constructor for the ScopeManager object
   */
  private ScopeManager()
  {
    // NoOp
  }
}