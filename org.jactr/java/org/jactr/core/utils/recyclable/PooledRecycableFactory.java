package org.jactr.core.utils.recyclable;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class PooledRecycableFactory<T> implements RecyclableFactory<T>
{

  private ObjectPool<T> _pool;

  public PooledRecycableFactory(PooledObjectFactory<T> factory)
  {
    GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<T>();
    config.setBlockWhenExhausted(false);
    config.setMaxIdle(10);
    config.setMinIdle(1);
    config.setMaxTotal(Integer.MAX_VALUE);

    _pool = new GenericObjectPool<>(factory, config);
  }

  @Override
  public int getRecycleBinSize()
  {
    return 0;
  }

  @Override
  public void setRecycleBinSize(int size)
  {

  }

  @Override
  public T newInstance(Object... params)
  {
    try
    {
      return _pool.borrowObject();
    }
    catch (Exception e)
    {
      return null;
    }
  }

  @Override
  public void recycle(T obj)
  {
    try
    {
      _pool.returnObject(obj);
    }
    catch (Exception e)
    {

    }
  }


}
