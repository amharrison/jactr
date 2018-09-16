package org.jactr.modules.pm.visual.scene;

/*
 * default logging
 */
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * the participant allows the IO system to inject the contents of scene.jactr
 * into any model that usess SceneChangeExtension in the model definition file.
 * It also injects parameters if they are not provided.
 * 
 * @author harrison
 *
 */
public class SceneChangeParticipant extends BasicASTParticipant
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SceneChangeParticipant.class);

  public SceneChangeParticipant()
  {
    super(SceneChangeExtension.class.getClassLoader().getResource(
        "org/jactr/modules/pm/visual/scene/scene.jactr"));
    setInstallableClass(SceneChangeExtension.class);
    
    TreeMap<String, String> parameters = new TreeMap<String, String>();
    parameters.put(SceneChangeExtension.SCENE_CHANGE_THRESHOLD_PARAM, "0.25");
    parameters.put(SceneChangeExtension.ACCELERATED_DETECTION_PARAM, "FALSE");
    setParameterMap(parameters);
  }
}
