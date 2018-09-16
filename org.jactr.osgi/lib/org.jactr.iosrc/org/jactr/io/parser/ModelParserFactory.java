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
package org.jactr.io.parser;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ModelParserFactory
{

  static private Map<String, Class< ? extends IModelParser>> _parsers;

  static private Log                                         LOGGER = LogFactory
                                                                        .getLog(ModelParserFactory.class);

  static
  {
    _parsers = new HashMap<String, Class< ? extends IModelParser>>();
    addParser("lisp", org.jactr.io.antlr3.parser.lisp.LispModelParser.class);
    addParser("jactr", org.jactr.io.antlr3.parser.xml.JACTRModelParser.class);
  }

  /**
   * @param mp
   */
  static public void addParser(String extension,
      Class< ? extends IModelParser> parserClass)
  {
    if (extension.startsWith(".")) extension = extension.substring(1);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding " + parserClass + " on [" + extension + "]");

    _parsers.put(extension.toLowerCase(), parserClass);
  }

  /**
   * @return
   */
  static public Collection<String> getValidExtensions()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Valid extensions : " + _parsers.keySet());
    return Collections.unmodifiableCollection(_parsers.keySet());
  }

  /**
   * instantiate a new parser for this extension
   * 
   * @param extension
   * @return
   */
  static public IModelParser instantiateParser(String extension)
  {
    for (Map.Entry<String, Class< ? extends IModelParser>> parseEntry : _parsers
        .entrySet())
      if (extension.equalsIgnoreCase(parseEntry.getKey()))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Attempting to instantiate parser : "
              + parseEntry.getValue().getName());
        try
        {
          IModelParser mp = parseEntry.getValue().newInstance();
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("ModelParser created and configured, " + mp);
          return mp;
        }
        catch (Exception e)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Failed to instantiate class "
                + parseEntry.getValue().getName(), e);
        }
      }
    return null;
  }

  /**
   * @param url
   * @return
   */
  static public IModelParser getModelParser(URL url) throws IOException
  {
    String fileName = url.getFile().toLowerCase();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Scanning parsers for match to " + fileName
          + " (case insensitive)");
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1,
        fileName.length());
    IModelParser mp = instantiateParser(extension);
    if (mp != null) mp.setInput(url);

    return mp;
  }
}