package org.jactr.core.module.declarative.four;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.basic.IActivationParticipant;
import org.jactr.core.model.IModel;

/**
 * equation for the computation of spreading activation
 * 
 * @author harrison
 */
public interface IRandomActivationEquation extends IActivationParticipant
{

  public double computeRandomActivation(IModel model, IChunk c);
}
