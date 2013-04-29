package org.jactr.core.module.procedural.six.learning;

import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.production.request.IRequest;

public class DefaultCompilableContext implements ICompilableContext {

	protected boolean _immediate;
	protected boolean _deterministic;
	protected boolean _jammable;
	protected boolean _compileOut;
	public DefaultCompilableContext(boolean immediate, boolean deterministic, boolean jammable, boolean compileOut) {
		_immediate = immediate;
		_deterministic = deterministic;
		_jammable = jammable;
		_compileOut = compileOut;
	}
	
	public boolean isImmediate(IRequest request) {
		return _immediate;
	}

	public boolean isDeterministic(IRequest request) {
		return _deterministic;
	}

	public boolean isJammable(IRequest request) {
		return _jammable;
	}

	public boolean canCompileOut(IRequest request) {
		return _compileOut;
	}
}
