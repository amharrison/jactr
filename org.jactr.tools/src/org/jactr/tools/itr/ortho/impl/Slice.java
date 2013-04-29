package org.jactr.tools.itr.ortho.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jactr.tools.itr.ortho.ISlice;

public class Slice implements ISlice
{

  final private long                _id;

  final private long                _firstIteration;

  final private long                _lastIteration;

  final private Map<String, Object> _parameters;

  final private Set<String>         _workingDirectories;

  public Slice(long id, long first, long last)
  {
    _id = id;
    _firstIteration = first;
    _lastIteration = last;
    _parameters = new TreeMap<String, Object>();
    _workingDirectories = new TreeSet<String>();
  }

  public long getFirstIteration()
  {
    return _firstIteration;
  }

  public long getId()
  {
    return _id;
  }

  public long getLastIteration()
  {
    return _lastIteration;
  }

  public Map<String, Object> getParameterValues()
  {
    return Collections.unmodifiableMap(_parameters);
  }

  public Object getProperty(String property)
  {
    return _parameters.get(property);
  }

  public Collection<String> getWorkingDirectories()
  {
    return Collections.unmodifiableCollection(_workingDirectories);
  }
  
  /**
   * relative to user.dir
   * @param relativePath
   */
  public void addWorkingDirectory(String relativePath)
  {
    _workingDirectories.add(relativePath);
  }

  public void setProperty(String property, Object value)
  {
    _parameters.put(property, value);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder("[Slice(");
    sb.append(_id).append(")[").append(_firstIteration).append(",").append(
        _lastIteration).append("]");
    sb.append(_parameters).append("]");
    return sb.toString();
  }

}
