package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParameterProcessorTest extends TestCase
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ParameterProcessorTest.class);

  private Number                     _number;

  private Class                      _class;

  private Collection<Double>         _collection;

  public void testNumeric() throws Exception
  {
    DoubleParameterProcessor npp = new DoubleParameterProcessor("number",
        this::setDouble, this::getDouble);

    npp.setParameter("1.0");
    assertNotNull(getDouble());
    assertEquals(1, getDouble().doubleValue(), 0.01);

    assertEquals("1.0", npp.getParameter());
  }

  protected void setDouble(Double number)
  {
    _number = number;
  }

  protected Double getDouble()
  {
    return (Double) _number;
  }

  public void testClassName() throws Exception
  {
    ClassNameParameterProcessor cnpp = new ClassNameParameterProcessor("class",
        this::setClassParameter, this::getClassParameter, getClass()
            .getClassLoader());

    cnpp.setParameter("java.lang.Double");
    assertNotNull(getClassParameter());
    assertEquals(java.lang.Double.class, _class);

    assertEquals("java.lang.Double", cnpp.getParameter());
  }

  protected void setClassParameter(Class c)
  {
    _class = c;
  }

  protected Class getClassParameter()
  {
    return _class;
  }

  public void testCollection() throws Exception
  {
    // the lack of setter/getter is intentional, as it is only needed by the
    // primary processor
    DoubleParameterProcessor npp = new DoubleParameterProcessor("number", null,
        null);

    CollectionParameterProcessor<Double> cpp = new CollectionParameterProcessor<Double>(
        "collection", this::setCollection, this::getCollection, npp, false);

    String normalTest = "(1.1, 2.2, 3.3)";
    Collection<Double> values = Arrays.asList(new Double(1.1), new Double(2.2),
        new Double(3.3));

    cpp.setParameter(normalTest);
    assertNotNull(_collection);
    assertEquals(3, _collection.size());

    assertEquals(values, _collection);

    assertEquals(normalTest, cpp.getParameter());

  }

  protected void setCollection(Collection<Double> collection)
  {
    _collection = collection;
  }

  protected Collection<Double> getCollection()
  {
    return _collection;
  }
}
