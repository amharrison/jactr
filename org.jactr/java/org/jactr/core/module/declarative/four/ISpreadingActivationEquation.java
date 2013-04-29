package org.jactr.core.module.declarative.four;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;

/**
 * equation for the computation of spreading activation
 * 
 * @author harrison
 */
public interface ISpreadingActivationEquation
{

  public double computeSpreadingActivation(IModel model, IChunk c);
}
