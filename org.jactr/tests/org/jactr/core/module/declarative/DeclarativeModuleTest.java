/*
 * Created on Nov 21, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.search.filter.AcceptAllFilter;
import org.jactr.core.module.declarative.search.filter.PartialMatchActivationFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.fluent.FluentModel;

import junit.framework.TestCase;

public class DeclarativeModuleTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(DeclarativeModuleTest.class);

  private IModel           _model;

  @Override
  public void setUp() throws Exception
  {
    _model = FluentModel.named("test").withCoreModules().build();
    _model.initialize();
  }

  /**
   * test chunktype creation and addition as well as parenting
   * 
   * @throws Exception
   */
  public void testChunkTypes() throws Exception
  {
    IDeclarativeModule dm = _model.getDeclarativeModule();
    assertNotNull(dm);

    int existing = dm.getChunkTypes().get().size();
    /*
     * lets create a chunk type
     */
    IChunkType testType = createChunkType(dm, "test", null, "a", "b", "c");
    assertNotNull(testType);

    /*
     * and add it..
     */
    Future<IChunkType> added = dm.addChunkType(testType);
    assertNotNull(added);

    testType = added.get();
    assertNotNull(testType);

    /*
     * the model should have one chunktype
     */
    added = dm.getChunkType("test");
    assertNotNull(added);

    assertEquals(testType, added.get());

    assertEquals(existing + 1, dm.getChunkTypes().get().size());

    assertEquals(3, testType.getSymbolicChunkType().getSlots().size());

    /*
     * lets create a new chunk type
     */
    IChunkType secondType = createChunkType(dm, "test2", testType, "d");
    assertNotNull(secondType);

    added = dm.addChunkType(secondType);
    assertNotNull(added);

    secondType = added.get();
    assertNotNull(secondType);

    added = dm.getChunkType("test2");
    assertNotNull(added);

    assertEquals(secondType, added.get());

    assertEquals(existing + 2, dm.getChunkTypes().get().size());

    assertEquals(4, secondType.getSymbolicChunkType().getSlots().size());

    assertTrue(secondType.isA(testType));

    assertFalse(testType.isA(secondType));
  }

  /**
   * test chunk creation, addition, & merging
   */
  public void testChunks() throws Exception
  {
    IDeclarativeModule dm = _model.getDeclarativeModule();
    assertNotNull(dm);
    /*
     * first we need a chunktype
     */
    IChunkType rootType = createChunkType(dm, "test", null, "a", "b", "c");
    rootType = dm.addChunkType(rootType).get();
    assertNotNull(rootType);

    /*
     * now lets create a chunk
     */
    IChunk firstChunk = createChunk(dm, "one", rootType, "a", 1, "b", 2, "c", 3);
    assertNotNull(firstChunk);

    assertEquals(0, rootType.getSymbolicChunkType().getChunks().size());

    firstChunk = dm.addChunk(firstChunk).get();

    assertEquals(1, rootType.getSymbolicChunkType().getChunks().size());

    assertEquals(firstChunk, dm.getChunk("one").get());

    /*
     * now lets create a chunk that will be merged
     */
    IChunk mergie = createChunk(dm, "mergie", rootType, "a", 1, "b", 2, "c", 3);
    assertNotNull(mergie);

    assertNotSame(mergie, firstChunk);

    IChunk newMergie = dm.addChunk(mergie).get();

    // the same contents
    assertEquals(mergie, firstChunk);

    // new and old handles
    assertTrue(newMergie == firstChunk);
    assertFalse(mergie == firstChunk);

    // no record of the name
    assertNull(dm.getChunk("mergie").get());

    assertEquals(1, rootType.getSymbolicChunkType().getChunks().size());
  }

  /**
   * test exact match searching. searching using strings, numbers, boolean, and
   * chunks
   * 
   * @throws Exception
   */
  public void testExactMatch() throws Exception
  {
    IDeclarativeModule dm = _model.getDeclarativeModule();
    assertNotNull(dm);
    /*
     * first we need some chunktypes
     */
    IChunkType test1 = createChunkType(dm, "test1", null, "a", "b", "c");
    test1 = dm.addChunkType(test1).get();
    assertNotNull(test1);

    IChunkType test2 = createChunkType(dm, "test2", test1, "d");
    test2 = dm.addChunkType(test2).get();
    assertNotNull(test2);

    IChunk t11 = dm.addChunk(
        createChunk(dm, "t11", test1, "a", 1, "b", "bee", "c", false)).get();
    IChunk t12 = dm.addChunk(
        createChunk(dm, "t12", test1, "a", 4, "b", t11, "c", true)).get();
    IChunk t13 = dm.addChunk(
        createChunk(dm, "t13", test1, "a", 0, "b", t11, "c", t12)).get();
    dm.addChunk(
        createChunk(dm, "t21", test2, "a", 1, "b", "bee", "c", false, "d", t13))
        .get();

    assertEquals(1, test1.getSymbolicChunkType().getChildren().size());
    assertEquals(4, test1.getSymbolicChunkType().getChunks().size());
    assertEquals(1, test2.getSymbolicChunkType().getChunks().size());

    ChunkTypeRequest pattern = new ChunkTypeRequest(test1,
        Arrays.asList(new IConditionalSlot[] { new DefaultConditionalSlot("a",
            IConditionalSlot.LESS_THAN, 4) }));

    assertEquals(3, dm.findExactMatches(pattern, null, new AcceptAllFilter())
        .get().size());

    pattern = new ChunkTypeRequest(test1, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", IConditionalSlot.LESS_THAN, 4),
        new DefaultConditionalSlot("c", IConditionalSlot.NOT_EQUALS, false) }));
    assertEquals(1, dm.findExactMatches(pattern, null, new AcceptAllFilter())
        .get().size());

    pattern = new ChunkTypeRequest(test2, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", 1),
        new DefaultConditionalSlot("b", "bee") }));
    assertEquals(1, dm.findExactMatches(pattern, null, new AcceptAllFilter())
        .get().size());

    pattern = new ChunkTypeRequest(test1, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", 1),
        new DefaultConditionalSlot("b", "bee") }));
    assertEquals(2, dm.findExactMatches(pattern, null, new AcceptAllFilter())
        .get().size());
  }

  /**
   * test partial match searching. searching using strings, numbers, boolean,
   * and chunks
   * 
   * @throws Exception
   */
  public void testPartialMatch() throws Exception
  {
    IDeclarativeModule dm = _model.getDeclarativeModule();
    assertNotNull(dm);
    /*
     * first we need some chunktypes
     */
    IChunkType test1 = createChunkType(dm, "test1", null, "a", "b", "c");
    test1 = dm.addChunkType(test1).get();
    assertNotNull(test1);

    IChunkType test2 = createChunkType(dm, "test2", test1, "d");
    test2 = dm.addChunkType(test2).get();
    assertNotNull(test2);

    IChunk t11 = dm.addChunk(
        createChunk(dm, "t11", test1, "a", 1, "b", "bee", "c", false)).get();
    IChunk t12 = dm.addChunk(
        createChunk(dm, "t12", test1, "a", 4, "b", t11, "c", true)).get();
    IChunk t13 = dm.addChunk(
        createChunk(dm, "t13", test1, "a", 0, "b", t11, "c", t12)).get();
    dm.addChunk(
        createChunk(dm, "t21", test2, "a", 1, "b", "bee", "c", false, "d", t13))
        .get();

    assertEquals(1, test1.getSymbolicChunkType().getChildren().size());
    assertEquals(4, test1.getSymbolicChunkType().getChunks().size());
    assertEquals(1, test2.getSymbolicChunkType().getChunks().size());

    ChunkTypeRequest pattern = new ChunkTypeRequest(test1,
        Arrays.asList(new IConditionalSlot[] { new DefaultConditionalSlot("a",
            IConditionalSlot.LESS_THAN, 4) }));

    // chunk filter for partial matching is important as it verifies that at
    // least one feature matches.

    Collection<IChunk> results = dm.findPartialMatches(pattern, null,
        new PartialMatchActivationFilter(pattern, Double.NEGATIVE_INFINITY))
        .get();

    assertEquals(3, results.size());

    pattern = new ChunkTypeRequest(test1, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", IConditionalSlot.LESS_THAN, 4),
        new DefaultConditionalSlot("c", IConditionalSlot.NOT_EQUALS, true) }));

    assertEquals(
        3,
        dm.findPartialMatches(pattern, null,
            new PartialMatchActivationFilter(pattern, Double.NEGATIVE_INFINITY))
            .get()
            .size());

    pattern = new ChunkTypeRequest(test2, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", 1),
        new DefaultConditionalSlot("b", "bee") }));

    assertEquals(
        1,
        dm.findPartialMatches(pattern, null,
            new PartialMatchActivationFilter(pattern, Double.NEGATIVE_INFINITY))
            .get().size());

    pattern = new ChunkTypeRequest(test1, Arrays.asList(new IConditionalSlot[] {
        new DefaultConditionalSlot("a", 1),
        new DefaultConditionalSlot("b", "bee") }));

    assertEquals(
        2,
        dm.findPartialMatches(pattern, null,
            new PartialMatchActivationFilter(pattern, Double.NEGATIVE_INFINITY))
            .get().size());
  }

  static public IChunk createChunk(IDeclarativeModule module, String name,
      IChunkType type, Object... slotsAndValues) throws Exception
  {
    Future<IChunk> futureChunk = module.createChunk(type, name);
    assertNotNull(futureChunk);

    IChunk chunk = futureChunk.get();
    assertNotNull(chunk);

    assertTrue(chunk.isA(type));

    /*
     * set the slot values..
     */
    for (int i = 0; i < slotsAndValues.length; i += 2)
    {
      BasicSlot slot = new BasicSlot(slotsAndValues[i].toString(),
          slotsAndValues[i + 1]);
      chunk.getSymbolicChunk().addSlot(slot);
    }

    return chunk;
  }

  static public IChunkType createChunkType(IDeclarativeModule module,
      String name, IChunkType parent, String... slots) throws Exception
  {
    Future<IChunkType> chunkType = module.createChunkType(parent, name);
    assertNotNull(chunkType);

    IChunkType actualChunkType = chunkType.get();
    assertNotNull(actualChunkType);

    for (String slotName : slots)
      actualChunkType.getSymbolicChunkType().addSlot(new BasicSlot(slotName));

    return actualChunkType;
  }
}
