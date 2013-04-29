package org.jactr.modules.pm.common.symbol;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.modules.pm.IPerceptualModule;

/**
 * default symbol grounder that merely returns the string version of the "text"
 * properity (if it exists) otherwise, it returns the string version of the
 * percepts identifier.
 * 
 * @author harrison
 */
public class DefaultStringSymbolGrounder implements ISymbolGrounder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                 = LogFactory
                                                                .getLog(DefaultStringSymbolGrounder.class);

  private IVisualPropertyHandler     _visualPropertyHandler = new DefaultVisualPropertyHandler();

  private IAuralPropertyHandler      _auralPropertyHandler  = new DefaultAuralPropertyHandler();


  public Object getSymbolForPercept(IAfferentObject percept,
      IPerceptualModule perceivingModule, IDeclarativeModule declarativeModule)
  {
    String symbol = null;

    if (_visualPropertyHandler.hasModality(percept))
    {
      if (_visualPropertyHandler.hasProperty(IVisualPropertyHandler.TEXT,
          percept))
        symbol = _visualPropertyHandler.getText(percept);
      else if (_visualPropertyHandler.hasProperty(IVisualPropertyHandler.TOKEN,
          percept)) symbol = _visualPropertyHandler.getToken(percept);
    }
    else if (_auralPropertyHandler.hasModality(percept))
      if (_auralPropertyHandler.hasProperty(IAuralPropertyHandler.TOKEN,
          percept)) symbol = _auralPropertyHandler.getToken(percept);

    /*
     * catch all
     */
    if (symbol == null)
    {
      symbol = percept.getIdentifier().getName();
      if (symbol == null) symbol = percept.getIdentifier().toString();
    }

    return symbol;
  }

  public Object getSymbolForString(String string, IDeclarativeModule declarativeModule) {
	  return string;
  }
}
