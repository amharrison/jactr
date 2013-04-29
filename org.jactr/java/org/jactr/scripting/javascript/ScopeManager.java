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

import org.jactr.core.model.IModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Maps javascript contexts to a model
 * 
 * @author harrison
 * @created March 3, 2003
 */
public class ScopeManager
{

  private static Scriptable              _publicScope;

  private static Map<IModel, Scriptable> _scopeMap = Collections
                                                       .synchronizedMap(new WeakHashMap<IModel, Scriptable>());

  /**
   * Gets the publicScope attribute of the ScopeManager class
   * 
   * @return The publicScope value
   */
  public static Scriptable getPublicScope()
  {
    if (_publicScope == null)
    {
      Context cx = Context.enter();
      cx.setLanguageVersion(Context.VERSION_1_6);
      _publicScope = cx.initStandardObjects(null, false);
      defineVariable(_publicScope, "out", System.out);
      defineVariable(_publicScope, "err", System.err);
      Context.exit();
    }
    return _publicScope;
  }

  /**
   * Gets the scopeForModel attribute of the ScopeManager class
   * 
   * @param m
   *          Description of the Parameter
   * @return The scopeForModel value
   */
  public static Scriptable getScopeForModel(IModel m)
  {
    synchronized (_scopeMap)
    {
      Scriptable sc = _scopeMap.get(m);
      if (sc == null)
      {
        Context cx = Context.enter();
        Scriptable publicScope = getPublicScope();
        try
        {
          sc = cx.newObject(publicScope);
          sc.setPrototype(publicScope);
          sc.setParentScope(null);
        }
        catch (Exception e)
        {
          // NoOp
        }
        Context.exit();
        _scopeMap.put(m, sc);
      }
      return sc;
    }
  }

  static public Scriptable newScope(Scriptable scriptable)
  {
    Context cx = Context.enter();
    Scriptable rtn = cx.newObject(scriptable);
    rtn.setPrototype(scriptable);
    rtn.setParentScope(null);
    Context.exit();
    return rtn;
  }


  /**
   * @param scope
   * @param variableName
   * @param object
   */
  static public void defineVariable(Scriptable scope, String variableName,
      Object object)
  {
    Object variable = Context.javaToJS(object, scope);
    ScriptableObject.putProperty(scope, variableName, variable);
  }


  static public Object getVariable(Scriptable scope, String variableName)
  {
    return ScriptableObject.getProperty(scope, variableName);
  }

  /**
   * Constructor for the ScopeManager object
   */
  private ScopeManager()
  {
    // NoOp
  }
}