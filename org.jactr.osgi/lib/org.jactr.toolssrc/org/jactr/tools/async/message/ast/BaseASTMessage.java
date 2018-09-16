/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.message.ast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.serialization.Serializer;
import org.jactr.tools.async.message.BaseMessage;

/**
 * @author developer
 */
public class BaseASTMessage extends BaseMessage implements Serializable,
    IASTMessage
{
  /**
   * 
   */
  private static final long serialVersionUID = -7905429360085152901L;

  /**
   * logger definition
   */
  static private final transient Log                          LOGGER           = LogFactory
                                                                                   .getLog(BaseASTMessage.class);

  private transient CommonTree _ast;

  private transient boolean    _compress;

  static private transient ThreadLocal<ByteArrayOutputStream> _localBAOS       = new ThreadLocal<ByteArrayOutputStream>();

  static private transient ThreadLocal<byte[]>                _localInput      = new ThreadLocal<byte[]>();

  public BaseASTMessage(CommonTree ast)
  {
    _ast = ast;
  }

  public void compressAST()
  {
    _compress = true;
  }

  public CommonTree getAST()
  {
    return _ast;
  }
  
  protected void setAST(CommonTree ast)
  {
    _ast = ast;
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    out.defaultWriteObject();
    if (_ast != null)
    {
      out.writeBoolean(true);
      out.writeBoolean(_compress);

      if (_compress)
      {
        /*
         * go to a GZIPOutputStream
         */
        ByteArrayOutputStream baos = _localBAOS.get();
        if (baos == null)
        {
          baos = new ByteArrayOutputStream();
          _localBAOS.set(baos);
        }
        baos.reset();

        DataOutputStream zip = new DataOutputStream(new GZIPOutputStream(baos));
        Serializer.write(_ast, zip);
        zip.flush();
        zip.close();
        // byte[] bytes = baos.toByteArray();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String
              .format("Writing %d compressed bytes", baos.size()));

        out.writeInt(baos.size());
        baos.writeTo(out);
        
// if (LOGGER.isDebugEnabled())
        // LOGGER.debug("Compressed AST to "+bytes.length);
        // out.writeInt(bytes.length);
        // out.write(bytes);
      }
      else
        Serializer.write(_ast, out);
    }
    else
      out.writeBoolean(false);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException
  {
    in.defaultReadObject();
    boolean astIsNotNull = in.readBoolean();
    if (astIsNotNull)
    {
      boolean wasCompressed = in.readBoolean();

      if (wasCompressed)
      {
        /*
         * pull through GZIPInputStream. this can be done more effeciently..
         */
        int len = in.readInt();

        byte[] bytes = _localInput.get();
        if (bytes == null || bytes.length < len)
        {
          bytes = new byte[len];
          _localInput.set(bytes);
        }

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Reading "+len+" bytes to decompress");

        in.read(bytes, 0, len);

        // in.readFully(bytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes, 0, len);
        DataInputStream zip = new DataInputStream(new GZIPInputStream(bais));
        _ast = Serializer.read(zip);
      }
      else
        _ast = Serializer.read(in);
    }
  }
}
