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

package org.jactr.core.production.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;

/**
 * OutputAction is a general purpose output function. It takes a string that can
 * contain variable names that will, at firing, be resolved to their bound
 * values. This is useful for tracing and producing pseudo-verbal protocols in
 * the model. The output is directed to the ModelLogEvent.OUTPUT log. a general
 * output action, equivalent to !output! in ACT-R proper Output is of the form
 * "This is test, and this is a =variable"
 */
/**
 * Description of the Class
 * 
 * @author harrison
 * @created February 11, 2003
 */
public class OutputAction extends DefaultAction
{

  private static transient Log LOGGER = LogFactory.getLog(OutputAction.class
                                          .getName());

  /**
   * Description of the Field
   */
  public String                _outputTemplate;

  /**
   * Constructor for the OutputAction object
   */
  public OutputAction()
  {
    this("");
  }

  /**
   * Constructor for the OutputAction object
   * 
   * @param output
   *            Description of the Parameter
   */
  public OutputAction(String output)
  {
    _outputTemplate = output;
  }
  

  /**
   * Sets the text attribute of the OutputAction object
   * 
   * @param text
   *            The new text value
   */
  public void setText(String text)
  {
    _outputTemplate = text;
  }

  /**
   * Gets the text attribute of the OutputAction object
   * 
   * @return The text value
   */
  public String getText()
  {
    return _outputTemplate;
  }

  public IAction bind(VariableBindings bindings)
  {
    OutputAction oa = new OutputAction(_outputTemplate);
    oa.replaceVariables(bindings);
    return oa;
  }

  protected void replaceVariables(VariableBindings bindings)
  {
    if (_outputTemplate != null)
      _outputTemplate = replaceVariables(_outputTemplate, bindings);
  }

  /**
   * Description of the Method
   * 
   * @param instantiation
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    final IModel model = instantiation.getModel();
    

    ITimedEvent event = new AbstractTimedEvent(firingTime, firingTime) {
      @Override
      public void fire(double currentTime)
      {
        super.fire(currentTime);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Outputting " + _outputTemplate);
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.OUTPUT, _outputTemplate);
      }

      @Override
      public String toString()
      {
        return String.format("Output('%s' @ %.2f)", _outputTemplate,
            getEndTime());
      }
    };

    /*
     * queue the event
     */
    model.getTimedEventQueue().enqueue(event);

    return 0;
  }

  /**
   * Description of the Method
   * 
   * @param var
   *            Description of the Parameter
   * @param start
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  static private int findEndOfVariableName(String var, int start)
  {
    int end = ++start;
    char[] charArray = var.toCharArray();
    while (end < charArray.length)
      if (Character.isWhitespace(charArray[end]))
        return end;
      else
        end++;
    return end;
  }

  /**
   * Description of the Method
   * 
   * @param variableBindings
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  static public String replaceVariables(String template, VariableBindings variableBindings)
  {
    StringBuilder sb = new StringBuilder();
    int start = 0;
    int end = 0;

    while (end != -1)
    {
      // find a =variable
      end = template.indexOf("=", start);
      if (end != -1)
      {
        // there is a variable like name

        int endOfVariableName = findEndOfVariableName(template, end);
        String variableName = template.substring(end, endOfVariableName);
        sb.append(template.substring(start, end));
        if (!variableName.equals("="))
        {
          // it's not an equal sign alone
          variableName = variableName.toLowerCase();

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Variable Name " + variableName + " found");
          Object val = resolve(variableName, variableBindings);

          if (val instanceof IChunk)
            val = ((IChunk) val).getSymbolicChunk().getName();

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(variableName + " = " + val);

          if (variableBindings.isBound(variableName))
            sb.append(val);
          else
          {
            sb.append(variableName);
            LOGGER.error(variableBindings.get("=production") +
                ".outputAction : " + variableName +
                " was not found. Available : " + variableBindings);
          }
        }
        else
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(variableName + " is not a variable name, appending");
          sb.append(variableName);
        }
        end = endOfVariableName;
      }
      else if (start < template.length()) sb.append(template.substring(start));
      start = end;
    }
    return sb.toString();
  }
}
