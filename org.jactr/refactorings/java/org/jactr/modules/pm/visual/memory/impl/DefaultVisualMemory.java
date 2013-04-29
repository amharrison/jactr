package org.jactr.modules.pm.visual.memory.impl;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.parameter.NumericParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.common.memory.filter.NumericIndexFilter;
import org.jactr.modules.pm.common.memory.impl.AbstractPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.memory.impl.encoder.CursorEncoder;
import org.jactr.modules.pm.visual.memory.impl.encoder.EmptySpaceEncoder;
import org.jactr.modules.pm.visual.memory.impl.encoder.GUIEncoder;
import org.jactr.modules.pm.visual.memory.impl.encoder.LineEncoder;
import org.jactr.modules.pm.visual.memory.impl.encoder.PhraseEncoder;
import org.jactr.modules.pm.visual.memory.impl.encoder.TextEncoder;
import org.jactr.modules.pm.visual.memory.impl.filter.AttendedVisualLocationFilter;
import org.jactr.modules.pm.visual.memory.impl.filter.NearestVisualLocationFilter;
import org.jactr.modules.pm.visual.memory.impl.filter.ValueVisualLocationFilter;
import org.jactr.modules.pm.visual.memory.impl.filter.VectorVisualLocationFilter;
import org.jactr.modules.pm.visual.memory.impl.map.ColorFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.DimensionFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.DistanceFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.FINSTVisualFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.HeadingFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.KindFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.PitchFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.SizeFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.VisibilityFeatureMap;

public class DefaultVisualMemory extends AbstractPerceptualMemory implements
    IVisualMemory
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER                        = LogFactory
                                                                       .getLog(DefaultVisualMemory.class);

  static public final String         VISUAL_PESISTENCE_DELAY_PARAM = "VisualPersistenceDelay";

  private double                     _horizontalFOV                = 160;

  private double                     _verticalFOV                  = 120;

  private int                        _horizontalResolution         = 160;

  private int                        _verticalResolution           = 120;

  private int                        _finstLimit                   = 4;

  private double                     _finstDuration                = 3;

  private double                     _onsetDuration                = 0.5;

  private double                     _tolerance                    = 0.5;

  private boolean                    _stickyAttention              = false;

  private double                     _visualPersistence            = 0;

  public DefaultVisualMemory(IVisualModule module)
  {
    super(module, new VisualLocationManager(module));


    addFilter(new NumericIndexFilter(IVisualModule.SCREEN_X_SLOT, true));
    addFilter(new NumericIndexFilter(IVisualModule.SCREEN_Y_SLOT, true));
    addFilter(new NumericIndexFilter(IVisualModule.SCREEN_Z_SLOT, true));
    addFilter(new NumericIndexFilter(IVisualModule.SIZE_SLOT, true));
    addFilter(new NearestVisualLocationFilter());
    addFilter(new AttendedVisualLocationFilter());
    addFilter(new ValueVisualLocationFilter());
    addFilter(new VectorVisualLocationFilter());

    addEncoder(new TextEncoder());
    addEncoder(new PhraseEncoder());
    addEncoder(new LineEncoder());
    addEncoder(new GUIEncoder());
    addEncoder(new EmptySpaceEncoder());
    addEncoder(new CursorEncoder());

    addFeatureMap(new HeadingFeatureMap());
    addFeatureMap(new PitchFeatureMap());
    addFeatureMap(new DistanceFeatureMap());
    addFeatureMap(new KindFeatureMap());
    addFeatureMap(new SizeFeatureMap());
    addFeatureMap(new DimensionFeatureMap());
    addFeatureMap(new ColorFeatureMap(module.getModel()));
    addFeatureMap(new VisibilityFeatureMap());
    addFeatureMap(new FINSTVisualFeatureMap(module.getModel()));
  }

  public int getFINSTLimit()
  {
    return _finstLimit;
  }

  public double getFINSTSpan()
  {
    return _finstDuration;
  }

  public int getHorizontalResolution()
  {
    return _horizontalResolution;
  }

  public double getHorizontalSpan()
  {
    return _horizontalFOV;
  }

  public double getMovementTolerance()
  {
    return _tolerance;
  }

  public double getOnsetDuration()
  {
    return _onsetDuration;
  }

  public int getVerticalResolution()
  {
    return _verticalResolution;
  }

  public double getVerticalSpan()
  {
    return _verticalFOV;
  }

  public void setFINSTLimit(int max)
  {
    _finstLimit = max;
  }

  public void setFINSTSpan(double duration)
  {
    _finstDuration = duration;
  }

  public void setHorizontalResolution(int resolution)
  {
    _horizontalResolution = resolution;
  }

  public void setHorizontalSpan(double fov)
  {
    _horizontalFOV = fov;
  }

  public void setMovementTolerance(double tolerance)
  {
    _tolerance = tolerance;
  }

  public void setVisualPersistenceDelay(double persistenceDelay)
  {
    _visualPersistence = persistenceDelay;
  }

  public double getVisualPersistenceDelay()
  {
    return _visualPersistence;
  }

  public void setOnsetDuration(double duration)
  {
    _onsetDuration = duration;
  }

  public void setVerticalResolution(int resolution)
  {
    _verticalResolution = resolution;
  }

  public void setVerticalSpan(double fov)
  {
    _verticalFOV = fov;
  }

  @Override
  public void attach(ACTRAgent agent)
  {
    super.attach(agent);

    ((VisualLocationManager) getIndexManager()).setDimensions(
        getHorizontalSpan(), getHorizontalResolution(), getVerticalSpan(),
        getVerticalResolution());

    /*
     * notify the finst parameters
     */
    IFINSTFeatureMap finst = getFINSTFeatureMap();
    finst.setMaximumFINSTs(getFINSTLimit());

    /*
     * and we apply the visual persistence delay
     */
    getObjectListener().setPerceptualDelay(getVisualPersistenceDelay());
  }


  public IChunk getVisualLocationChunkAt(double x, double y)
  {
    return ((VisualLocationManager) getIndexManager())
        .getVisualLocationChunkAt(x, y);
  }

  public IVisualModule getVisualModule()
  {
    return (IVisualModule) getModule();
  }

  /**
   * executes the visual search on the current thread. This should only be
   * called if you know what you are doing
   * 
   * @param request
   * @return
   */
  public PerceptualSearchResult searchNow(ChunkTypeRequest request)
  {
    return searchInternal(request);
  }

  /**
   * return true if the chunk is a visual-object of some sort
   * 
   * @param encodedChunk
   * @param originalRequest
   * @return
   * @see org.jactr.modules.pm.common.memory.impl.AbstractPerceptualMemory#isAcceptable(org.jactr.core.chunk.IChunk,
   *      org.jactr.core.production.request.ChunkTypeRequest)
   */
  @Override
  protected boolean isAcceptable(IChunk encodedChunk,
      ChunkTypeRequest originalRequest)
  {
    return encodedChunk.isA(getVisualModule().getVisualChunkType());
  }

  /**
   * @param indexChunk
   * @param encodedChunk
   * @param originalRequest
   * @param expandedRequest
   * @see org.jactr.modules.pm.common.memory.impl.AbstractPerceptualMemory#fillIndexChunk(org.jactr.core.chunk.IChunk,
   *      org.jactr.core.chunk.IChunk,
   *      org.jactr.core.production.request.ChunkTypeRequest,
   *      org.jactr.core.production.request.ChunkTypeRequest)
   */
  @Override
  protected void fillIndexChunk(IChunk indexChunk, IChunk encodedChunk,
      ChunkTypeRequest originalRequest, ChunkTypeRequest expandedRequest)
  {
    Object value = encodedChunk.getSymbolicChunk().getSlot(
        IVisualModule.VALUE_SLOT).getValue();
    expandedRequest.addSlot(new BasicSlot(IVisualModule.VALUE_SLOT, value));

    try
    {
      indexChunk.getWriteLock().lock();
      /*
       * now we just copy everything over, if possible
       */
      ISymbolicChunk sc = indexChunk.getSymbolicChunk();
      for (ISlot slot : expandedRequest.getSlots())
        if (!slot.getName().equalsIgnoreCase(IVisualModule.SCREEN_X_SLOT)
            && !slot.getName().equalsIgnoreCase(IVisualModule.SCREEN_Y_SLOT))
          try
          {
            value = slot.getValue();
            String name = slot.getName();
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Setting " + indexChunk + "." + name + "=" + value);
            ((IMutableSlot) sc.getSlot(name)).setValue(value);
          }
          catch (IllegalChunkStateException e)
          {
            // if the slot was a metaslot there will be an exception
          }
    }
    finally
    {
      indexChunk.getWriteLock().unlock();
    }

  }

  public String getParameter(String key)
  {
    if (STICKY_ATTENTION_PARAM.equalsIgnoreCase(key))
      return Boolean.toString(isStickyAttentionEnabled());
    else if (VISUAL_FIELD_HEIGHT_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getVerticalSpan());
    else if (VISUAL_FIELD_WIDTH_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getHorizontalSpan());
    else if (VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getVerticalSpan());
    else if (VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getHorizontalSpan());
    else if (VISUAL_ONSET_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getOnsetDuration());
    else if (NUMBER_OF_FINSTS_PARAM.equalsIgnoreCase(key))
      return String.format("%d", getFINSTLimit());
    else if (FINST_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getFINSTSpan());
    else if (MOVEMENT_TOLERANCE_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getMovementTolerance());
    else if (VISUAL_PESISTENCE_DELAY_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getVisualPersistenceDelay());
    else if (key.indexOf('.') != 0)
    {
      /*
       * might be a class name, check all the installed pieces, if we've got a
       * match, return true, else false
       */
      for (IIndexFilter filter : getFilters(null))
        if (filter.getClass().getName().equals(key))
          return Boolean.toString(true);

      for (IPerceptualEncoder encoder : getEncoders(null))
        if (encoder.getClass().getName().equals(key))
          return Boolean.toString(true);

      for (IFeatureMap map : getFeatureMaps(null))
        if (map.getClass().getName().equals(key))
          return Boolean.toString(true);

      return Boolean.toString(false);
    }
    return null;
  }

  public void setParameter(String key, String value)
  {
    NumericParameterHandler nph = ParameterHandler.numberInstance();
    if (STICKY_ATTENTION_PARAM.equalsIgnoreCase(key))
      setStickyAttentionEnabled(ParameterHandler.booleanInstance()
          .coerce(value).booleanValue());
    else if (VISUAL_FIELD_HEIGHT_PARAM.equalsIgnoreCase(key))
      setVerticalSpan(nph.coerce(value).doubleValue());
    else if (VISUAL_FIELD_WIDTH_PARAM.equalsIgnoreCase(key))
      setHorizontalSpan(nph.coerce(value).doubleValue());
    else if (VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM.equalsIgnoreCase(key))
      setVerticalResolution(nph.coerce(value).intValue());
    else if (VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM.equalsIgnoreCase(key))
      setHorizontalResolution(nph.coerce(value).intValue());
    else if (VISUAL_ONSET_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      setOnsetDuration(nph.coerce(value).doubleValue());
    else if (NUMBER_OF_FINSTS_PARAM.equalsIgnoreCase(key))
      setFINSTLimit(nph.coerce(value).intValue());
    else if (FINST_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      setFINSTSpan(nph.coerce(value).doubleValue());
    else if (MOVEMENT_TOLERANCE_PARAM.equalsIgnoreCase(key))
      setMovementTolerance(nph.coerce(value).doubleValue());
    else if (VISUAL_PESISTENCE_DELAY_PARAM.equalsIgnoreCase(key))
      setVisualPersistenceDelay(nph.coerce(value).doubleValue());
    else if (key.indexOf('.') > 0)
    {
      /*
       * might be a class
       */
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Assuming " + key + " is a classname");
      try
      {
        boolean shouldAdd = ParameterHandler.booleanInstance().coerce(value)
            .booleanValue();
        Class clazz = ParameterHandler.classInstance().coerce(key);

        if (!shouldAdd)
        {
          for (IIndexFilter filter : getFilters(null))
            if (filter.getClass().getName().equals(key)) removeFilter(filter);

          for (IPerceptualEncoder encoder : getEncoders(null))
            if (encoder.getClass().getName().equals(key))
              removeEncoder(encoder);

          for (IFeatureMap map : getFeatureMaps(null))
            if (map.getClass().getName().equals(key)) removeFeatureMap(map);
        }
        else
        {
          Object instance = clazz.newInstance();

          if (instance instanceof IIndexFilter)
            addFilter((IIndexFilter) instance);
          if (instance instanceof IFeatureMap)
            addFeatureMap((IFeatureMap) instance);
          if (instance instanceof IPerceptualEncoder)
            addEncoder((IPerceptualEncoder) instance);
        }
      }
      catch (Exception e)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("Could not process suspected class name " + key + " = "
              + value, e);
      }
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how set " + key + " = " + value);
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(VISUAL_FIELD_WIDTH_PARAM, VISUAL_FIELD_HEIGHT_PARAM,
        VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM,
        VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM,
        VISUAL_ONSET_DURATION_TIME_PARAM, NUMBER_OF_FINSTS_PARAM,
        FINST_DURATION_TIME_PARAM, MOVEMENT_TOLERANCE_PARAM,
        VISUAL_PESISTENCE_DELAY_PARAM, STICKY_ATTENTION_PARAM);
  }

  public boolean isStickyAttentionEnabled()
  {
    return _stickyAttention;
  }

  public void setStickyAttentionEnabled(boolean enabled)
  {
    _stickyAttention = enabled;
  }

}
