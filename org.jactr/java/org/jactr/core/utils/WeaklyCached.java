package org.jactr.core.utils;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * weakly referenced computational cache.
 * 
 * @author harrison
 * @param <T>
 */
public class WeaklyCached<T>
{

  private final Supplier<T>          _supplier;

  private Optional<WeakReference<T>> _reference = Optional.empty();

  public WeaklyCached(Supplier<T> supplier)
  {
    _supplier = supplier;
  }

  synchronized public void invalidate()
  {
    _reference = Optional.empty();
  }

  synchronized public T get()
  {
    return _reference.orElseGet(() -> new WeakReference<>(_supplier.get()))
        .get();
  }

}
