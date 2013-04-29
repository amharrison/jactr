package org.jactr.modules.versioned.declarative;

import java.util.concurrent.Future;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.IDeclarativeModule;

public interface IVersionedDeclarativeModule extends IDeclarativeModule {

	public final static int ADD = 0;
	public final static int REMOVE = 1;
	
	public Future<IChunkType> getChunkType(String name, double version);
	
	public Future<IChunk> getChunk(String name, double version);
	
	public Future<IChunkType> refineChunkType(IChunkType ct, int action, String propName);
	
}
