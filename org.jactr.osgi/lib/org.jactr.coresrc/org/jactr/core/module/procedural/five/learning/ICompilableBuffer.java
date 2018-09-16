package org.jactr.core.module.procedural.five.learning;

/*
 * default logging
 */
import org.jactr.core.buffer.IActivationBuffer;

/**
 * a marker interface for {@link IActivationBuffer}s that provides details
 * regarding how they can and cannot be compiled. If a production
 * makes reference to any buffer that does not implement this interface, it
 * will not be compiled.
 * 
 * @author harrison
 *
 */
public interface ICompilableBuffer extends IActivationBuffer
{

 public ICompilableContext getCompilableContext();
}
