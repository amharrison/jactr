/**
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu This library is free
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

package org.jactr.core.buffer;

import java.util.Collection;
import java.util.concurrent.Executor;

import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.IInitializable;

/**
 * Represents a general case buffer in a model. Most buffers are already defined
 * (goal, retrieval, and the RPM buffers) it is rare that a buffer will be added -
 * but the capacity is necessary. Buffers are also the only source of external
 * activation to be propogated to its contents.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IActivationBuffer extends IInitializable, IAdaptable
{

  public final static String GOAL            = "goal";

  static final public String IMAGINAL        = "imaginal";

  /**
   * Description of the Field
   */
  public final static String RETRIEVAL       = "retrieval";

  /**
   * Description of the Field
   */
  public final static String VISUAL          = "visual";

  /**
   * Description of the Field
   */
  public final static String VISUAL_LOCATION = "visual-location";

  /**
   * Description of the Field
   */
  public final static String AURAL           = "aural";

  /**
   * Description of the Field
   */
  public final static String AURAL_LOCATION  = "aural-location";

  /**
   * Description of the Field
   */
  public final static String MOTOR           = "motor";

  public final static String VOCAL           = "vocal";


  /**
   * called from within an instantiation notifying the buffer that this chunk
   * has been matched in the production that is about to fire.
   * 
   * @param chunk
   */
  public void matched(IChunk chunk);

  /**
   * Set the value of the spreading activation. The source chunk will have this
   * value applied to it and propogated to its contained chunks (contained the
   * source chunk?s slots). Note: the specific behavior of this is not fully
   * defined. Multiple capacity buffers might not implement an all or nothing
   * contract and instead might distribute activation across chunks equally or
   * by some other means.
   * 
   * @param activation
   *            0.0 to deactivate
   */
  public void setActivation(double activation);

  /**
   * return the activation of the buffer
   * 
   * @return The activation value
   */
  public double getActivation();

  /**
   * set the goal value of this buffer.. only used for the goal buffer at
   * present
   * 
   * @param g
   *            The new g value
   */
  public void setG(double g);

  /**
   * return the goal value of this buffer.
   * 
   * @return The g value
   */
  public double getG();

  /**
   * flush the contents of the buffer and notify any listeners
   * 
   * @see org.jactr.core.buffer.event.IActivationBufferListener
   */
  public void clear();
  
  

  /**
   * add a chunk to the buffer. If the buffer's contents are constrained (and
   * they should be) it will call removeSourceChunk as necessary listeners will
   * be notified
   * 
   * @param c
   *            chunk to be added
   * @return the actual chunk added (in case a copy was made)
   * @see org.jactr.core.buffer.IActivationBuffer#removeSourceChunk(IChunk)
   * @see org.jactr.core.buffer.event.IActivationBufferListener
   */
  public IChunk addSourceChunk(IChunk c);

  /**
   * remove a chunk from the buffer, and notify listeners
   * 
   * @see org.jactr.core.buffer.event.IActivationBufferListener
   */
  public void removeSourceChunk(IChunk c);

  /**
   * return a chunk from the buffer - the exact behavior is specified by the
   * particular buffer
   */
  public IChunk getSourceChunk();

  /**
   * return all the chunks in the buffer
   */
  public Collection<IChunk> getSourceChunks();

  public Collection<IChunk> getSourceChunks(Collection<IChunk> container);

  /**
   * returns the buffer's source chunk that is symbolically equal to c, or null
   * if none is available or symbolically equal. For exact equality, use
   * {@link #getSourceChunks()#contains(IChunk)}
   * 
   * @see IChunk#equalsSymbolic(IChunk)
   */
  public IChunk contains(IChunk c);

  /**
   * add a buffer listener
   */
  public void addListener(IActivationBufferListener abl, Executor executor);

  /**
   * remove a buffer listener
   */
  public void removeListener(IActivationBufferListener abl);

  /**
   * return the buffer's name
   * 
   * @return The name value
   */
  public String getName();

  public IModel getModel();

  /**
   * returns true if this buffer is responsible for handling the encoding of
   * chunks that are in it. otherwise, the model will encode at
   * removeSourceChunk
   * 
   * @return
   */
  public boolean handlesEncoding();

  /**
   * return the module that controls this buffer
   * 
   * @return
   */
  public IModule getModule();

  /**
   * clean up resources, called by the module that created the buffer
   */
  public void dispose();

  /**
   * strict harvesting forces the removal of buffer content that is matched
   * against in the LHS, but not acted upon in the RHS. The buffer is not
   * actually responsible for enforcing strict harvesting. see
   * {@link DefaultProceduralModule6}
   * 
   * @return
   */
  public boolean isStrictHarvestingEnabled();

}