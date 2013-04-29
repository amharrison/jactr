package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;

public class ProductionRelationships
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionRelationships.class);

  private Map<String, IRelationship> _tailRelationships;

  private Map<String, IRelationship> _headRelationships;

  private String                     _productionName;

  private BufferEndStates            _endStates;

  public ProductionRelationships(String productionName,
      BufferEndStates endStates)
  {
    _productionName = productionName;
    _endStates = endStates;

    _tailRelationships = new TreeMap<String, IRelationship>();
    _headRelationships = new TreeMap<String, IRelationship>();
  }

  public void addRelationship(IRelationship relationship)
  {
    String refName = ASTSupport.getName(relationship.getHeadProduction())
        .toLowerCase();
    String queryName = ASTSupport.getName(relationship.getTailProduction())
        .toLowerCase();

    if (_endStates.getProduction() == relationship.getHeadProduction())
      _tailRelationships.put(queryName, relationship);
    
    if (_endStates.getProduction() == relationship.getTailProduction())
      _headRelationships.put(refName, relationship);
  }

  public Collection<IRelationship> getHeadRelationships()
  {
    return Collections.unmodifiableCollection(_headRelationships.values());
  }

  public Collection<IRelationship> getTailRelationships()
  {
    return Collections.unmodifiableCollection(_tailRelationships.values());
  }

  public BufferEndStates getEndStates()
  {
    return _endStates;
  }
}
