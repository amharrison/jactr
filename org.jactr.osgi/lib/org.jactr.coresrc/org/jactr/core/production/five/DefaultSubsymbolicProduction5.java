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
package org.jactr.core.production.five;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.four.DefaultSubsymbolicProduction4;

/**
 * @author harrison
 * @created February 5, 2003
 */
public class DefaultSubsymbolicProduction5 extends DefaultSubsymbolicProduction4 implements
    ISubsymbolicProduction
{

  static transient Log         LOGGER               = LogFactory
                                                                .getLog(DefaultSubsymbolicProduction5.class
                                                                    .getName());


  /**
   * Constructor for the DefaultSubsymbolicProduction5 object
   * 
   * @param p
   *          Description of Parameter
   * @param parentModel
   *          Description of Parameter
   * @since
   */
  public DefaultSubsymbolicProduction5(DefaultProduction5 p, IModel parentModel)
  {
    super(p,parentModel);
  }
}