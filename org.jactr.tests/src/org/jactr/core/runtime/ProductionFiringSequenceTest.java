/*
 * Created on Dec 11, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.runtime;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author developer
 *
 */
public class ProductionFiringSequenceTest extends TestCase
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ProductionFiringSequenceTest.class);

  final private Collection<String> _productionFiringSequence = new ArrayList<String>();
  
  
  
  protected void setProductionFiringSequence(Collection<String> sequence)
  {
    _productionFiringSequence.addAll(sequence);
  }
  
  protected Collection<String> getProductionFiringSequence()
  {
    return new ArrayList<String>(_productionFiringSequence);
  }
  
  /** 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  /** 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  
  
  
}


