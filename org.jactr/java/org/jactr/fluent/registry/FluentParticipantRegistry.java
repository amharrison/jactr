package org.jactr.fluent.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

/**
 * @author harrison
 */
public class FluentParticipantRegistry
{
  /**
   * Logger definition
   */
  static private final transient Log      LOGGER             = LogFactory
      .getLog(FluentParticipantRegistry.class);

  private Map<Class<?>, Consumer<IModel>> _classParticipants = new HashMap<>();

  static FluentParticipantRegistry        _instance          = new FluentParticipantRegistry();

  static
  {
    _instance.register(DefaultDeclarativeModule6.class,
        new DefaultDeclarativeParticipant());
    _instance.register(DefaultVisualModule6.class,
        new DefaultVisualParticipant());
  }

  static public FluentParticipantRegistry get()
  {
    return _instance;
  }

  private FluentParticipantRegistry()
  {
  }

  public void register(Class<?> className, Consumer<IModel> fluentParticipant)
  {
    _classParticipants.put(className, fluentParticipant);
  }

  public Optional<Consumer<IModel>> getParticipant(Class<?> className)
  {
    return Optional.of(_classParticipants.getOrDefault(className, (model) -> {
    }));
  }
}
