package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultRelationship implements IRelationship
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultRelationship.class);

  private CommonTree                 _head;

  private CommonTree                 _tail;

  private Map<String, Double>        _scores;

  private double                     _averageScore;

  public DefaultRelationship(CommonTree head, CommonTree tail)
  {
    _head = head;
    _tail = tail;
    _scores = new TreeMap<String, Double>();
  }

  public CommonTree getHeadProduction()
  {
    return _head;
  }

  public CommonTree getTailProduction()
  {
    return _tail;
  }

  public double getScore()
  {
    return _averageScore;
  }

  public double getScore(String bufferName)
  {
    if (_scores.containsKey(bufferName.toLowerCase()))
      return _scores.get(bufferName.toLowerCase());
    return 0;
  }

  public void setScore(String bufferName, double score)
  {
    _scores.put(bufferName, score);
    
    boolean scoreWasPositive = _averageScore >= 0;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("current score : " + _averageScore + " adding " + score);

    // if negative, we dont let positive adjust us
    if (!scoreWasPositive && score > 0) score = 0;

    if (scoreWasPositive && score < 0) _averageScore = 0;

    _averageScore = (_averageScore * _scores.size() + score)
        / (_scores.size() + 1.0);

  }

  public void setPositiveRelationships(String bufferName,
      Collection<IPair> pairs)
  {

  }

  public void setNegativeRelationships(String bufferName,
      Collection<IPair> pairs)
  {

  }

  public void setAmbiguousRelationships(String bufferName,
      Collection<IPair> pairs)
  {

  }

}
