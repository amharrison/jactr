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
 * Created on May 20, 2005 by developer
 */

package org.jactr.io.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jactr.io.antlr3.generator.lisp.LispCodeGenerator;
import org.jactr.io.antlr3.generator.xml.JACTRCodeGenerator;

public class CodeGeneratorFactory
{

  static private Map<String, ICodeGenerator> _generatorMap = null;

  static
  {
    _generatorMap = new HashMap<String, ICodeGenerator>();
    addCodeGenerator("jactr", new JACTRCodeGenerator());
    addCodeGenerator("lisp", new LispCodeGenerator());
  }

  static public Collection<String> getExtensions()
  {
    synchronized (_generatorMap)
    {
      return new ArrayList<String>(_generatorMap.keySet());
    }
  }

  static public void addCodeGenerator(String extension, ICodeGenerator generator)
  {
    synchronized (_generatorMap)
    {
      _generatorMap.put(extension, generator);
    }
  }

  static public ICodeGenerator getCodeGenerator(String extension)
  {
    return _generatorMap.get(extension);
  }
}
