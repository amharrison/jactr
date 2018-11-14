package org.jactr.core.utils.recyclable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class CollectionPooledObjectFactory<T> extends BasePooledObjectFactory<T>
{

  private Supplier<T> _supplier;

  /*
   * Ideally, we'd make the generic T extends Collection<?> but since we need to
   * use this for CompositeSet and CompositeCollection, which are 3.2.2
   * non-generics, we erase that type and hack it back in here.
   */
  @SuppressWarnings("rawtypes")
  private Consumer<T> _passivate = (c) -> {
                                   if (c instanceof Collection)
                                     ((Collection) c).clear();
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
