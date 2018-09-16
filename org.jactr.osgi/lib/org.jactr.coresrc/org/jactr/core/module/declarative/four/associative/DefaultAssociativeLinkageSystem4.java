package org.jactr.core.module.declarative.four.associative;

import java.util.Collection;
/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunk.four.AssociativeLinkEquation4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.basic.DefaultAssociativeLinkageSystem;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.DeclarativeModuleListenerAdaptor;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * version four associative linkage system. The base class handles the creation
 * of the links for us. This code installs the procedural, declarative, and
 * chunk listeners necessary for the establishment and learning of associative
 * links
 * 
 * @author harrison
 */
public class DefaultAssociativeLinkageSystem4 extends
    DefaultAssociativeLinkageSystem
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAssociativeLinkageSystem4.class);

  private IChunkListener             _chunkListener;

  private IProceduralModuleListener  _proceduralListener;

  private IDeclarativeModuleListener _declarativeListener;

  private Executor                   _executor;

  public DefaultAssociativeLinkageSystem4(
      IDeclarativeLearningModule4 learningModule, Executor executor)
  {
    _executor = executor;
    _chunkListener = createChunkListener(learningModule, executor);
    _proceduralListener = createProceduralListener(learningModule, executor);
    _declarativeListener = createDeclarativeModuleListener(learningModule,
        executor);
    setAssociativeLinkEquation(createLinkEquation(learningModule));
  }

  protected IDeclarativeModuleListener createDeclarativeModuleListener(
      IDeclarativeLearningModule4 learningModule, Executor executor)
  {
    return new DeclarativeModuleListenerAdaptor() {

      @Override
      public void chunkCreated(DeclarativeModuleEvent dme)
      {
        IChunk iChunk = dme.getChunk();

        /*
         * create the initial self-link
         */
        IAssociativeLinkageSystem linkageSystem = dme.getSource()
            .getAssociativeLinkageSystem();

        if (linkageSystem != null)
          linkageSystem.addLink(linkageSystem.createLink(iChunk, iChunk));

        /*
         * chunks with default slot values will by-pass the normal chain of
         * event notification (the slot values are assigned before the chunk
         * listener can be attached). So, we do the containment linking directly
         */

        Collection<ISlot> slots = FastCollectionFactory.newInstance();
        try
        {
          iChunk.getSymbolicChunk().getSlots(slots);
          
          if(slots.size()==0) return;
          
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Post-linking %s and %s", iChunk, slots));
          
          
          for (ISlot slot : slots)
            if(slot.getValue() instanceof IChunk) linkSlotValue(iChunk, (IChunk)slot.getValue(), false);
        }
        finally
        {
          FastCollectionFactory.recycle(slots);
        }
      }
    };
  }

  /**
   * allows us to detect slot changes and handle merging & encoding may return
   * null
   * 
   * @return
   */
  protected IChunkListener createChunkListener(
      IDeclarativeLearningModule4 learningModule, Executor executor)
  {
    return new ChunkListener(this);
  }

  protected IAssociativeLinkEquation createLinkEquation(
      IDeclarativeLearningModule4 learningModule)
  {
    return new AssociativeLinkEquation4(learningModule);
  }

  /**
   * we use the production firing to trigger the learning may return null.
   * 
   * @return
   */
  protected IProceduralModuleListener createProceduralListener(
      IDeclarativeLearningModule4 learningModule, Executor executor)
  {
    return new ProceduralModuleListener(learningModule);
  }

  @Override
  public IChunkListener getChunkListener()
  {
    return _chunkListener;
  }

  @Override
  public void install(IModel model)
  {
    model.getDeclarativeModule().addListener(_declarativeListener, _executor);
    
    if (_proceduralListener == null) return;
    /*
     * attach the procedural listener
     */
    IProceduralModule procMod = model.getProceduralModule();
    procMod.addListener(_proceduralListener, _executor);
  }

  @Override
  public void uninstall(IModel model)
  {
    model.getDeclarativeModule().removeListener(_declarativeListener);
    
    
    if (_proceduralListener == null) return;

    IProceduralModule procMod = model.getProceduralModule();
    procMod.removeListener(_proceduralListener);
  }

  /**
   * will create (or remove) an associative link from value to iChunk,
   * establishing the containment link. if valueIsOld, the link will be
   * decremented until 0 then removed.
   * 
   * @param iChunk
   * @param value
   * @param valueIsOld
   */
  protected void linkSlotValue(IChunk iChunk, IChunk jChunk, boolean valueIsOld)
  {

    IAssociativeLinkContainer iContainer = iChunk
        .getAdapter(IAssociativeLinkContainer.class);
    IAssociativeLinkContainer jContainer = jChunk
        .getAdapter(IAssociativeLinkContainer.class);

    Collection<IAssociativeLink> links = FastCollectionFactory.newInstance();
    iContainer.getInboundLinks(jChunk, links);

    if (!valueIsOld)
    {
      // add
      if (links.size() == 0)
      {
        IAssociativeLink sJI = createLink(iChunk, jChunk);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Adding link between " + iChunk + " and " + jChunk
              + " : " + sJI);

        iContainer.addLink(sJI);
        jContainer.addLink(sJI);
      }
      else
      {
        // hopefully just one
        Link4 sJI = (Link4) links.iterator().next();

        sJI.increment(); // not new, but we need to increment the

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Link already established between " + iChunk + " and "
              + jChunk + " incrementing : " + sJI);
      }
    }
    else if(links.size()!=0)
    {
      /*
       * remove the old value.. decrement and remove if necessary
       */
      //hopefully just one
      Link4 sJI = (Link4) links.iterator().next();
      sJI.decrement();
      if (sJI.getCount() == 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Removing link between " + iChunk + " and " + jChunk
              + " : " + sJI);
        iContainer.removeLink(sJI);
        jContainer.removeLink(sJI);
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("Multiple links established between " + iChunk + " and "
            + jChunk + " decrementing : " + sJI);
    }

    FastCollectionFactory.recycle(links);

    // ISubsymbolicChunk4 sscI = iChunk.getSubsymbolicChunk()
    // .getAdapter(ISubsymbolicChunk4.class);
    // ISubsymbolicChunk4 sscJ = value.getSubsymbolicChunk()
    // .getAdapter(ISubsymbolicChunk4.class);
    // IAssociativeLink sJI = sscJ.getIAssociation(iChunk);
    //
    // if (!valueIsOld)
    // {
    // // add
    // if (sJI == null)
    // {
    // sJI = createLink(iChunk, value);
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug("Adding link between " + iChunk + " and " + value
    // + " : " + sJI);
    // // since we are the linkage system, we can assume this is correct and
    // // not
    // // use linkageSystem.addLink
    // sscI.addLink(sJI);
    // sscJ.addLink(sJI);
    // }
    // else
    // {
    // ((Link4) sJI).increment(); // not new, but we need to increment the
    // // link
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug("Link already established between " + iChunk + " and "
    // + value + " incrementing : " + sJI);
    // }
    // }
    // else if (sJI != null)
    // {
    // // remove
    // ((Link4) sJI).decrement();
    // if (((Link4) sJI).getCount() == 0)
    // {
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug("Removing link between " + iChunk + " and " + value
    // + " : " + sJI);
    // sscI.removeLink(sJI);
    // sscJ.removeLink(sJI);
    // }
    // else if (LOGGER.isDebugEnabled())
    // LOGGER.debug("Multiple links established between " + iChunk + " and "
    // + value + " decrementing : " + sJI);
    // }

  }
}
