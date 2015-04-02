/*
 * Created on Mar 13, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.lang.reflect.Constructor;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.commonreality.executor.GeneralThreadFactory;
import org.commonreality.executor.ThreadNamer;
import org.commonreality.mina.protocol.IMINAProtocolConfiguration;
import org.commonreality.mina.protocol.SerializingProtocol;
import org.commonreality.mina.service.ClientService;
import org.commonreality.mina.service.IMINAService;
import org.commonreality.mina.transport.IMINATransportProvider;
import org.commonreality.mina.transport.NIOTransportProvider;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.async.credentials.ICredentials;
import org.jactr.tools.async.credentials.PlainTextCredentials;

/**
 * Both RemoteInterface and ShadowControll rely on this common substrate that
 * deals with all the networking
 * 
 * @author developer
 */
public abstract class MINAEndpoint implements IParameterized
{

  /**
   * Logger definition
   */

  static private final transient Log    LOGGER            = LogFactory
                                                              .getLog(MINAEndpoint.class);

  public static final String            TRANSPORT_CLASS   = "transportClass";

  public static final String            PROTOCOL_CLASS    = "protocolClass";

  public static final String            ADDRESS           = "address";

  public static final String            SERVICE_CLASS     = "serviceClass";

  public static final String            CREDENTAILS       = "credentials";

  public static final String            CRED_CLASS        = "credentialsClass";

  private IMINATransportProvider        _transport        = new NIOTransportProvider();

  private IMINAService                  _service          = new ClientService();

  private IMINAProtocolConfiguration    _protocol         = new SerializingProtocol();

  private Class<? extends ICredentials> _credentialsClass = PlainTextCredentials.class;

  private String                        _addressInformation;

  private String                        _credentialInformation;

  private ICredentials                  _actualCredentials;

  private SocketAddress                 _actualAddress;

  private ExecutorService               _executorService;

  private boolean                       _usedDefaultExecutorService;

  /**
   * 
   */
  public MINAEndpoint()
  {
    super();
  }

  /**
   * return the io handler that handles all the communications
   * 
   * @return
   */
  abstract public BaseIOHandler getIOHandler();

  /**
   * set the executor for use by MINA
   * 
   * @param service
   */
  synchronized public void setIOExecutorService(ExecutorService service)
  {
    _executorService = service;
    _usedDefaultExecutorService = false;
  }

  /**
   * return the executor service that mina is relying upon
   */
  synchronized public ExecutorService getIOExecutorService()
  {
    if (_executorService == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("No executor service configured, using single thread executor");
      // _executorService = new OrderedThreadPoolExecutor(1, 1, 5000,
      // TimeUnit.MILLISECONDS, new GeneralThreadFactory("MINAEndpoint"));
      _executorService = Executors
          .newSingleThreadExecutor(new GeneralThreadFactory("MINA-Endpoint"));
      _usedDefaultExecutorService = true;
    }

    return _executorService;
  }

  /**
   * set the provider and possibly update the address information
   * 
   * @param provider
   */
  public void setTransportProvider(IMINATransportProvider provider)
  {
    _transport = provider;
    if (_addressInformation != null)
    {
      /*
       * split strings
       */
      String[] address = _addressInformation.split(":");
      _actualAddress = _transport.createAddress(address);
    }
  }

  /**
   * set the service for this end point (typically ClientServie or
   * ServerService)
   * 
   * @param service
   */
  public void setService(IMINAService service)
  {
    _service = service;
  }

  /**
   * set our protocol
   * 
   * @param protocol
   */
  public void setProtocol(IMINAProtocolConfiguration protocol)
  {
    _protocol = protocol;
  }

  /**
   * set the string version of the address, if the transport has already been
   * provided, the acutal address will be recomputed
   * 
   * @param addressInfo
   */
  public void setAddressInfo(String addressInfo)
  {
    _addressInformation = addressInfo;

    if (_transport != null)
    {
      /*
       * split strings
       */
      String[] address = _addressInformation.split(":");
      _actualAddress = _transport.createAddress(address);
    }
  }

  /**
   * set the connection credentials
   * 
   * @param credentialInfo
   */
  public ICredentials setCredentialInformation(String credentialInfo)
  {
    _credentialInformation = credentialInfo;

    if (_credentialsClass != null)
    {
      String[] creds = _credentialInformation.split(":");

      /*
       * create the credentials
       */
      try
      {
        String[] obj = new String[0];
        Constructor<? extends ICredentials> cons = _credentialsClass
            .getConstructor(new Class[] { obj.getClass() });
        _actualCredentials = cons.newInstance(new Object[] { creds });
        getIOHandler().setCredentials(_actualCredentials);
        return _actualCredentials;
      }
      catch (Exception e)
      {
        throw new RuntimeException("Could not create valid credentials for "
            + _credentialInformation + " with "
            + _credentialsClass.getSimpleName(), e);
      }
    }

    return null;
  }

  /**
   * @param credClass
   */
  public void setCredentialsClass(Class<? extends ICredentials> credClass)
  {
    _credentialsClass = credClass;
    if (_credentialInformation != null)
      setCredentialInformation(_credentialInformation);
  }

  /**
   * return the actual credentials that we are using
   * 
   * @return
   */
  public ICredentials getActualCredentials()
  {
    return _actualCredentials;
  }

  /**
   * return the socket address that we are actually connected to
   * 
   * @return
   */
  public SocketAddress getActualAddress()
  {
    return _actualAddress;
  }

  synchronized protected void connect() throws Exception
  {
    /*
     * let's connect
     */
    if (_transport == null)
      throw new RuntimeException(
          "Must specify transport (IMINATransportProvider)");

    if (_protocol == null)
      throw new RuntimeException(
          "Must specify protocol (IMINAProcotolConfiguration)");

    if (_addressInformation == null)
      throw new RuntimeException("Must specify adderss information");

    if (_service == null)
      throw new RuntimeException("Must specific service (IMINAService)");

    if (_credentialInformation == null)
      throw new RuntimeException("Must specify credential information");

    if (_credentialsClass == null)
      throw new RuntimeException("Must specify credentials provider class");

    if (_actualCredentials == null)
      throw new RuntimeException("Actual credentials must be set");

    _service.configure(_transport, _protocol, getIOHandler(),
        getIOExecutorService());

    try
    {
      _actualAddress = _service.start(_actualAddress);
    }
    catch (Exception e)
    {
      _service = null;
      throw new RuntimeException("Could not start service", e);
    }
  }

  /**
   * wait for all the pending writes
   * 
   * @throws Exception
   */
  protected void disconnect() throws Exception
  {
    disconnect(false);
  }

  /**
   * try to establish the connection
   */
  synchronized protected void disconnect(boolean force)
      throws Exception
  {
    try
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for pending writes");
      if (!force) getIOHandler().waitForPendingWrites();

      /*
       * close all connections..
       */
      getIOHandler().disconnect(force);

      if (LOGGER.isDebugEnabled()) LOGGER.debug("Shutting down");

      _service.stop(_actualAddress);

      if (_usedDefaultExecutorService && _executorService != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Default executor service was used, shuting down");
        
        if(force)
          _executorService.shutdownNow();
        else
          _executorService.shutdown();
        
        _usedDefaultExecutorService = false;
        _executorService = null;
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not shutdown service ", e);
    }
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (key.equalsIgnoreCase(ADDRESS)) return _addressInformation;
    if (key.equalsIgnoreCase(CREDENTAILS)) return _credentialInformation;
    if (key.equalsIgnoreCase(CRED_CLASS)) return _credentialsClass.getName();

    if (key.equalsIgnoreCase(SERVICE_CLASS))
    {
      if (_service != null) return _service.getClass().getName();
      return null;
    }

    if (key.equalsIgnoreCase(PROTOCOL_CLASS))
    {
      if (_protocol != null) return _protocol.getClass().getName();

      return null;
    }

    if (key.equalsIgnoreCase(TRANSPORT_CLASS))
    {
      if (_transport != null) return _transport.getClass().getName();

      return null;
    }

    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> str = new ArrayList<String>();
    str.add(ADDRESS);
    str.add(CREDENTAILS);
    str.add(CRED_CLASS);
    str.add(SERVICE_CLASS);
    str.add(PROTOCOL_CLASS);
    str.add(TRANSPORT_CLASS);
    return str;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (ADDRESS.equalsIgnoreCase(key))
      setAddressInfo(value);
    else if (CREDENTAILS.equalsIgnoreCase(key))
      setCredentialInformation(value);
    else if (CRED_CLASS.equalsIgnoreCase(key))
      setCredentialsClass(getClass(value));
    else if (PROTOCOL_CLASS.equalsIgnoreCase(key))
      setProtocol((IMINAProtocolConfiguration) instance(value));
    else if (TRANSPORT_CLASS.equalsIgnoreCase(key))
      setTransportProvider((IMINATransportProvider) instance(value));
    else if (SERVICE_CLASS.equalsIgnoreCase(key))
      setService((IMINAService) instance(value));
  }

  /**
   * utility for the instantiation of mina parameters
   * 
   * @param className
   * @return
   */
  protected Class getClass(String className)
  {
    try
    {
      return getClass().getClassLoader().loadClass(className);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not find class " + className, e);
    }
  }

  /**
   * utility for the instantiation of mina parameters
   * 
   * @param className
   * @return
   */
  protected Object instance(String className)
  {
    try
    {
      Class c = getClass(className);
      return c.newInstance();
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not instantiate " + className, e);
    }
  }

}
