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

package org.jactr.core.chunk.four;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.four.IBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.IRandomActivationEquation;
import org.jactr.core.module.declarative.four.ISpreadingActivationEquation;

/**
 * Subsymbolic requirements for ACT-R 4.0. Includes mechanisms for the
 * manipulation of associative links.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ISubsymbolicChunk4 extends ISubsymbolicChunk,
    IAssociativeLinkContainer
{

  /**
   * Description of the Field
   */
  public final static String CREATION_CYCLE        = "CreationCycle";
  /**
   * Description of the Field
   */
  public final static String LINKS = "Links";

  /**
   * return the number of associations where this is the j chunk
   * 
   * @return The numberOfIAssociations value
   */
  public int getNumberOfIAssociations();

  /**
   * return the number of associations where this is the i chunk
   * 
   * @return The numberOfJAssociations value
   */
  public int getNumberOfJAssociations();

  /**
   * return the associations where this is the j chunk
   * @param container TODO
   * 
   * @return The iAssociations value
   */
  public Collection<IAssociativeLink> getIAssociations(Collection<IAssociativeLink> container);

  /**
   * return the associations where this is the i chunk
   * @param container TODO
   * 
   * @return The jAssociations value
   */
  public Collection<IAssociativeLink> getJAssociations(Collection<IAssociativeLink> container);

  /**
   * get the I association between this chunk (J) and i
   * 
   * @param iChunk
   *          Description of the Parameter
   * @return the Link between this(J) and ref(I) or null if none exists
   */
  public IAssociativeLink getIAssociation(IChunk iChunk);

  /**
   * get the J association between this chunk (I) and j
   * 
   * @param jChunk
   *          Description of the Parameter
   * @return the Link between this(I) and ref(J) or null if none exists
   */
  public IAssociativeLink getJAssociation(IChunk jChunk);

  /**
   * add a link, it will be inspected to determine whether it is a J,I link
   * 
   * @param l
   *          The feature to be added to the Link attribute
   */
  public void addLink(IAssociativeLink l);

  /**
   * Description of the Method
   * 
   * @param l
   *          Description of the Parameter
   */
  public void removeLink(IAssociativeLink l);
  
  
  public long getCreationCycle();
  
  public void setCreationCycle(long cycle);

  public void setBaseLevelActivationEquation(
      IBaseLevelActivationEquation equation);

  public void setRandomActivationEquation(IRandomActivationEquation equation);

  public void setSpreadingActivationEquation(
      ISpreadingActivationEquation equation);
}

