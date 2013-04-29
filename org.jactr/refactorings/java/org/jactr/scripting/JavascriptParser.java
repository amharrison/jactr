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

package org.jactr.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.jactr.core.model.IModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class JavascriptParser implements Runnable
{

  /**
   * Description of the Field
   */
  protected BufferedReader _reader;
  /**
   * Description of the Field
   */
  protected Context        _context;
  /**
   * Description of the Field
   */
  protected PrintWriter    _output;
  /**
   * Description of the Field
   */
  protected PrintWriter    _error;
  /**
   * Description of the Field
   */
  protected String         _prompt;
  /**
   * Description of the Field
   */
  protected Scriptable     _scope;
  /**
   * Description of the Field
   */
  protected boolean        _exitContext = true;
  /**
   * Description of the Field
   */
  protected boolean        _shouldQuit  = false;

  /**
   * Constructor for the JavascriptParser object
   * 
   * @param reader
   *          Description of the Parameter
   * @param context
   *          Description of the Parameter
   * @param scope
   *          Description of the Parameter
   * @param output
   *          Description of the Parameter
   * @param error
   *          Description of the Parameter
   * @param prompt
   *          Description of the Parameter
   */
  public JavascriptParser(Reader reader, Context context, Scriptable scope,
      Writer output, Writer error, String prompt)
  {
    //reuse a context
    _reader = new BufferedReader(reader);
    _output = new PrintWriter(output);
    if (error == null)
    {
      _error = _output;
    }
    else
    {
      _error = new PrintWriter(error);
    }

    _prompt = prompt;

    if (context != null)
    {
      _context = context;
      _exitContext = false;
    }
    else
    {
      _context = Context.enter();
    }

    if (scope == null)
    {
      _scope = _context.initStandardObjects(null);
    }
    else
    {
      _scope = scope;
    }

  }

  /**
   * Description of the Method
   * 
   * @param variableName
   *          Description of the Parameter
   * @param object
   *          Description of the Parameter
   */
  public void defineVariable(String variableName, Object object)
  {
    Scriptable variable = Context.toObject(object, _scope);
    _scope.put(variableName, _scope, variable);
  }

  /**
   * @param chunk
   * @param slotName
   * @param value
   */

  /**
   * Description of the Method
   * 
   * @param modelName
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public IModel loadModel(String modelName)
  {
    //    org.jactr.io.xml.ACTRParser parser =
    // org.jactr.io.xml.ACTRParser.getParser();
    //    try
    //    {
    //      return parser.parse("file:" + modelName);
    //    }
    //    catch (Exception e)
    //    {
    //      e.printStackTrace(_error);
    //      return null;
    //    }
    throw new RuntimeException("model loading temporarily broken");
  }

  /**
   * Description of the Method
   */
  public void quit()
  {
    _shouldQuit = true;
  }

  /**
   * Main processing method for the JavascriptParser object
   * 
   * @param model
   *          Description of the Parameter
   */
  public void run(IModel model)
  {
    if (model != null)
    {
      throw new RuntimeException(getClass().getName()+" is currently f'ed up");
      /*ACTRRuntime rt = ACTRRuntime.getRuntime(model);
      if (!rt.isRunning())
      {
        //so that the model thread controls the advancement of time
        rt.getClock().setOwner(rt.getModelThread(model));
        IController cont = new DebugController(rt);
        cont.run();
      }*/
    }
  }

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param cycles
   *          Description of the Parameter
   */
  public void step(IModel model, int cycles)
  {
    _error.println("Stepping not implemented yet.");
  }

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   */
  public void step(IModel model)
  {
    step(model, 1);
  }

  /**
   * Main processing method for the JavascriptParser object
   */
  public void run()
  {
    try
    {
      defineVariable(_prompt, this);
      int lineno = 1;
      boolean hitEOF = false;
      do
      {
        int startline = lineno;
        _output.print(_prompt + "> ");
        _output.flush();
        try
        {
          String source = "";
          // Collect lines of source to compile.
          while (true)
          {
            String newline = null;
            newline = _reader.readLine();
            if (newline == null)
            {
              hitEOF = true;
              break;
            }
            source = source + newline + "\n";
            lineno++;
            // Continue collecting as long as more lines
            // are needed to complete the current
            // statement. stringIsCompilableUnit is also
            // true if the source statement will result in
            // any error other than one that might be
            // resolved by appending more source.
            if (_context.stringIsCompilableUnit(source))
            {
              break;
            }
          }
          Object result = _context.evaluateString(_scope, source, "<stream>",
              startline, null);
          if (result != Context.getUndefinedValue())
          {
            _output.println(Context.toString(result));
            _output.flush();
          }
        }
        catch (WrappedException we)
        {
          // Some form of exception was caught by JavaScript and
          // propagated up.
          _error.println(we.getWrappedException().toString());
          we.printStackTrace(_error);
        }
        catch (EvaluatorException ee)
        {
          // Some form of JavaScript error.
          _error.println(_prompt + ">> " + ee.getMessage());
        }
        catch (JavaScriptException jse)
        {
          // Some form of JavaScript error.
          _error.println(_prompt + ">> " + jse.getMessage());
        }
        catch (IOException ioe)
        {
          _error.println(ioe.toString());
        }
        if (_shouldQuit)
        {
          hitEOF = true;
        }
      }
      while (!hitEOF);
    }
    finally
    {
      if (_exitContext)
      {
        Context.exit();
      }
    }
  }
}