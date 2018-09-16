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
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;
import org.jactr.modules.pm.vocal.six.DefaultVocalModule6;

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
    _instance.register(DefaultAuralModule6.class,
        new DefaultAuralParticipant());
    _instance.register(DefaultVocalModule6.class,
        new DefaultVocalParticipant());
    _instance.register(DefaultMotorModule6.class,
        new DefaultMotorParticipant());
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
    // exact match?
    Consumer<IModel> rtn = _classParticipants.get(className);

    if (rtn == null) // check assignability
      for (Class<?> canidate : _classParticipants.keySet())
      if (canidate.isAssignableFrom(className))
      {
      rtn = _classParticipants.get(canidate);
      break;
      }

    return Optional.ofNullable(rtn);
  }
}
