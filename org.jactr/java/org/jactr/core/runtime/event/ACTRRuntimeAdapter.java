package org.jactr.core.runtime.event;

/**
 * Provides empty implementations. Subclasses can override single methods.
 */
public class ACTRRuntimeAdapter implements IACTRRuntimeListener {

	@Override
	public void modelAdded(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void modelRemoved(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void runtimeStarted(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void runtimeStopped(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void runtimeSuspended(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void runtimeResumed(ACTRRuntimeEvent event)
	{
	}

	@Override
	public void modelStarted(ACTRRuntimeEvent event)
	{	
	}

	@Override
	public void modelStopped(ACTRRuntimeEvent event)
	{
	}

}
