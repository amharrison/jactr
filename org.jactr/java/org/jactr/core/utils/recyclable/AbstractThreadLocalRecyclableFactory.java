package org.jactr.core.utils.recyclable;

/*
 * default logging
 */
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractThreadLocalRecyclableFactory<T> implements
    RecyclableFactory<T>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractThreadLocalRecyclableFactory.class);

  private int                        _size  = 10;

  private ThreadLocal<List<T>>       _local = new ThreadLocal<List<T>>();

  public T newInstance(Object... params)
  {
    List<T> bin = getRecycleBin();

    if (bin.size() > 0) return bin.remove(0);

    return instantiate(params);
  }

  public void recycle(T obj)
  {
    cleanUp(obj);

    List<T> bin = getRecycleBin();
    bin.add(obj);

    while (bin.size() > _size)
      release(bin.remove(0));
  }

  public int getRecycleBinSize()
  {
    return _size;
  }

  public void setRecycleBinSize(int size)
  {
    if (size <= 0) size = 10;
    _size = size;
  }

  /**
   * reset this object to its clean state
   * 
   * @param obj
   */
  abstract protected void cleanUp(T obj);

  /**
   * called if we are just going to release this to GC. last chance to cleanup
   * resources, etc.
   * 
   * @param obj
   */
  protected void release(T obj)
  {
    // noop
  }

  /**
   * instantiate a new T
   * 
   * @return
   */
  abstract protected T instantiate(Object... params);

  protected List<T> getRecycleBin()
  {
    List<T> rtn = _local.get();
    if (rtn == null)
    {
      rtn = new LinkedList<T>();
      _local.set(rtn);
    }
    return rtn;
  }

}
