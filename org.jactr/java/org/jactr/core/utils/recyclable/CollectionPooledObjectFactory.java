package org.jactr.core.utils.recyclable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class CollectionPooledObjectFactory<T extends Collection<?>>
    extends BasePooledObjectFactory<T>
{
  
  private Supplier<T> _supplier;

  private Consumer<T> _passivate = (c) -> {
                                   c.clear();
                                 };
  
  public CollectionPooledObjectFactory(Supplier<T> allocator)
  {
    _supplier = allocator;
  }

  public CollectionPooledObjectFactory(Supplier<T> allocator,
      Consumer<T> clearer)
  {
    _supplier = allocator;
    _passivate = clearer;
  }

  @Override
  public T create() throws Exception
  {
    return _supplier.get();
  }
  
  @Override
  public void passivateObject(final PooledObject<T> p)
  {
    _passivate.accept(p.getObject());
  }

  @Override
  public PooledObject<T> wrap(T obj)
  {
    return new DefaultPooledObject<T>(obj);
  }

}
