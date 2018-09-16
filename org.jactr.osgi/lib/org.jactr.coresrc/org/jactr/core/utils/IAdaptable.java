package org.jactr.core.utils;

/*
 * default logging
 */

/**
 * adaptable interface to be used in preference to checking instanceof,
 * particularly for theory specific code: <code>
 *  ISubsymbolicChunk4 ssc4 = chunk.getSubsymbolicChunk().getAdapter(ISubsymbolicChunk4.class);
 * </code>
 * 
 * @author harrison
 */
public interface IAdaptable
{

  public <T> T getAdapter(Class<T> adapterClass);
}
