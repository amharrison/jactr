package org.jactr.tools.itr.fit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.math.stat.regression.SimpleRegression;

@Deprecated
public class ModelFitStatistics
{
  SimpleRegression            _regression;

  Collection<ComparisonPoint> _data;

  ModelFitStatistics          _parent;

  boolean                     _isDirty = false;

  double                      _mse;

  public ModelFitStatistics()
  {
    _data = new ArrayList<ComparisonPoint>();
    _regression = new SimpleRegression();
    _parent = null;
  }

  protected ModelFitStatistics(ModelFitStatistics parent)
  {
    this();
    _parent = parent;
  }

  public ModelFitStatistics newChild()
  {
    return new ModelFitStatistics(this);
  }

  public void clear()
  {
    _data.clear();
    _regression.clear();
  }

  public int getN()
  {
    return _data.size();
  }

  public Collection<ComparisonPoint> getData()
  {
    return Collections.unmodifiableCollection(_data);
  }

  public void addData(String label, double model, double data)
  {
    if (label == null) label = "unknown";
    ComparisonPoint cp = new ComparisonPoint(label, data, model);
    addData(cp);

    if (_parent != null) _parent.addData(cp);

  }

  protected void addData(ComparisonPoint cp)
  {
    _regression.addData(cp.getModel(), cp.getData());
    _data.add(cp);
    _isDirty = true;
  }

  protected double calculateFit()
  {
    if (!_isDirty) return _mse;
    double sse = 0;
    double n = 0;

    for (ComparisonPoint cp : getData())
    {
      double delta = cp.getData() - cp.getModel();
      sse += Math.pow(delta, 2);
      n++;
    }

    _mse = sse / n;

    _isDirty = false;
    return _mse;
  }

  public double getMSE()
  {
    return calculateFit();
  }

  public double getRMSE()
  {
    return Math.sqrt(getMSE());
  }

  public double getR()
  {
    return _regression.getR();
  }

  public class ComparisonPoint
  {
    private String _label;

    private double _model;

    private double _data;

    public ComparisonPoint(String label, double data, double model)
    {
      _label = label;
      _data = data;
      _model = model;
    }

    public double getModel()
    {
      return _model;
    }

    public double getData()
    {
      return _data;
    }

    public String getLabel()
    {
      return _label;
    }
  }
}
