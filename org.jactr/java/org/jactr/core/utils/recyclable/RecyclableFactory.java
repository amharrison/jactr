package org.jactr.core.utils.recyclable;

/*
 * default logging
 */

/**
 * interface marking a factory that can return and accept recycled instances of
 * an object.
 * 
 * @author harrison
 * @param <T>
 */
public interface RecyclableFactory<T>
{

  public int getRecycleBinSize();

  public void setRecycleBinSize(int size);

  public T newInstance(Object... params);

  public void recycle(T obj);
}
