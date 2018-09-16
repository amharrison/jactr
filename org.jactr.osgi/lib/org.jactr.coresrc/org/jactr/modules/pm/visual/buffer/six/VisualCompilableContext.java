package org.jactr.modules.pm.visual.buffer.six;

import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.IRequest;

public class VisualCompilableContext implements ICompilableContext {

	public boolean isImmediate(IRequest request) {
		if(request instanceof ChunkRequest) return true;
		return false;
	}

	public boolean isDeterministic(IRequest request) {
		if(request instanceof ChunkRequest) return true;
		return false;
	}

	public boolean isJammable(IRequest request) {
		if(request instanceof ChunkRequest) return false;
		return true;
	}

	public boolean canCompileOut(IRequest request) {
		return false;
	}
}
