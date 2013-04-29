/*
 * Created on Mar 14, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.common;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.handler.demux.DemuxingIoHandler;
import org.apache.mina.handler.demux.ExceptionHandler;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.tools.async.credentials.ICredentials;
import org.jactr.tools.async.message.BulkMessage;
import org.jactr.tools.async.message.IMessage;

/**
 * @author developer
 */
public class BaseIOHandler extends DemuxingIoHandler
{
  /**
   * logger definition
   */
  static private final Log              LOGGER         = LogFactory
                                                           .getLog(BaseIOHandler.class);

  private ICredentials                  _credentials;

  private IoSession                     _activeSession;

  private Lock                          _lock          = new ReentrantLock();

  private Condition                     _connected     = _lock.newCondition();

  private Condition                     _noMoreWrites  = _lock.newCondition();

  private IoFutureListener<WriteFuture> _writeListener = new IoFutureListener<WriteFuture>() {

                                                         public void operationComplete(
                                                             WriteFuture writeRequest)
                                                         {
                                                           try
                                                           {
                                                             _lock.lock();
                                                             _noMoreWrites
                                                                 .signalAll();
                                                           }
                                                           finally
                                                           {
                                                             _lock.unlock();
                                                           }
                                                         }
                                                       };

  public BaseIOHandler()
  {

    addReceivedMessageHandler(BulkMessage.class, new BulkMessageHandler(this));

    addSentMessageHandler(IMessage.class, new MessageHandler<IMessage>() {

      public void handleMessage(IoSession session, IMessage msg)
          throws Exception
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg + " sent to " + session);
      }
    });

    addExceptionHandler(Throwable.class, new ExceptionHandler<Throwable>() {

      public void exceptionCaught(IoSession session, Throwable exception)
          throws Exception
      {
        /*
         * this can occur if we have pending writes but the connection has
         * already been closed from the other side, so we silently ignore it
         */
        if (exception instanceof WriteToClosedSessionException)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("Tried to write to closed session ", exception);
          return;
        }

        /**
         * Error : error
         */
        LOGGER.error(
            "Exception caught from session " + session + ", closing. ",
            exception);

        if (!session.isClosing()) session.close();
      }

    });
  }

  public void setCredentials(ICredentials credentials)
  {
    _credentials = credentials;
  }

  @Override
  public void sessionOpened(IoSession session) throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Opened new session " + session);

    try
    {
      _lock.lock();

      _activeSession = session;
      _connected.signalAll();
    }
    finally
    {
      _lock.unlock();
    }

    super.sessionOpened(session);
  }

  @Override
  public void sessionClosed(IoSession session) throws Exception
  {
    try
    {
      _lock.lock();

      _activeSession = null;
      _connected.signalAll();
      _noMoreWrites.signalAll();
    }
    finally
    {
      _lock.unlock();
    }

    super.sessionClosed(session);
  }

  public boolean isConnected()
  {
    return _activeSession != null;
  }

  public ICredentials getCredentials()
  {
    return _credentials;
  }

  public void waitForPendingWrites() throws InterruptedException
  {
    try
    {
      _lock.lock();
      int pendingWrites = 0;
      while (isConnected()
          && (pendingWrites = _activeSession.getScheduledWriteMessages()) != 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(pendingWrites + " writes are waiting");
        _noMoreWrites.await(500, TimeUnit.MILLISECONDS);
      }

      if (LOGGER.isDebugEnabled() && _activeSession != null)
        LOGGER.debug("connection has "
            + _activeSession.getScheduledWriteMessages() + " remaining");
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * all writes should be through here so that we can track the status of the
   * writes
   * 
   * @param message
   */
  public WriteFuture write(Object message)
  {
    try
    {
      _lock.lock();
      if (!isConnected())
        throw new IllegalStateException("Could not write " + message
            + ", Not connected");

      WriteFuture rtn = _activeSession.write(message);
      rtn.addListener(_writeListener);
      return rtn;
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void disconnect() throws Exception
  {
    disconnect(false);
  }

  /**
   * disconnect from everyone
   * 
   * @throws Exception
   */
  public void disconnect(boolean force) throws Exception
  {
    if (_activeSession == null) return;

    if (_activeSession.isConnected() && !_activeSession.isClosing())
      if (!force)
        _activeSession.close(false).awaitUninterruptibly();
      else
        _activeSession.close(true).awaitUninterruptibly();
  }

  /**
   * wait until all the active connections have been closed
   * 
   * @throws InterruptedException
   */
  public void waitForDisconnect() throws InterruptedException
  {
    try
    {
      _lock.lock();
      while (isConnected())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Waiting for connection to close");
        _connected.await(500, TimeUnit.MILLISECONDS);
      }

      if (LOGGER.isDebugEnabled()) LOGGER.debug("all sessions disconnected");
    }
    finally
    {
      _lock.unlock();
    }
  }


  public boolean waitForConnection(long timeOut) throws InterruptedException
  {
    try
    {
      _lock.lock();

      long start = System.currentTimeMillis();
      while (!isConnected())
      {
        if (timeOut > 0 && System.currentTimeMillis() - start >= timeOut)
          break;

        if (timeOut > 0)
          _connected.await(timeOut, TimeUnit.MILLISECONDS);
        else
          _connected.await();
      }
      return isConnected();
    }
    finally
    {
      _lock.unlock();
    }
  }

  static private class BulkMessageHandler implements
      MessageHandler<BulkMessage>
  {
    BaseIOHandler _handler;

    BulkMessageHandler(BaseIOHandler handler)
    {
      _handler = handler;
    }

    public void handleMessage(IoSession arg0, BulkMessage arg1)
        throws Exception
    {
      Collection<IMessage> messages = arg1.getMessages();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Processing bulk messages " + messages.size());
      for (IMessage message : messages)
        _handler.messageReceived(arg0, message);

    }

  }
}
