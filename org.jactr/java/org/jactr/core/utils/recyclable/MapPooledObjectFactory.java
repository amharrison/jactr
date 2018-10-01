package org.jactr.core.utils.recyclable;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MapPooledObjectFactory<T extends Map<?, ?>>
    extends BasePooledObjectFactory<T>
{
  
  private Supplier<T> _supplier;
  
  public MapPooledObjectFactory(Supplier<T> allocator)
  {
    _supplier = allocator;
  }

  @Override
  public T create() throws Exception
  {
    return _supplier.get();
  }
  
  @Override
  public void passivateObject(final PooledObject<T> p)
  {
    p.getObject().clear();
  }

  @Override
  public PooledObject<T> wrap(T obj)
  {
    return new DefaultPooledObject<T>(obj);
  }

}
