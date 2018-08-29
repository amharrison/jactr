package org.jactr.core.module.retrieval.time;

import java.util.Locale;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DefaultRetrievalTimeEquationTest {
	
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();
	
	@Test
	public void computeRetrievalTimeWithoutChunkTypeRequestMustNotThrowNPE() {
		
		final IModel model = context.mock(IModel.class);
		final IDeclarativeModule4 dm = context.mock(IDeclarativeModule4.class);
		final IChunk errorChunk = context.mock(IChunk.class, "errorChunk");
		final IRetrievalModule4 rm = context.mock(IRetrievalModule4.class);
		final IChunk chunk = context.mock(IChunk.class, "chunk");
		final ISubsymbolicChunk ssc = context.mock(ISubsymbolicChunk.class);
		
		Expectations expectations = new Expectations() {{
			allowing(rm).getModel();
				will(returnValue(model));
			allowing(rm).getLatencyFactor();
				will(returnValue(1.0d));
			allowing(rm).getLatencyExponent();
				will(returnValue(.3d));
			allowing(rm).getRetrievalThreshold();
				will(returnValue(0.0d));
				
			allowing(model).getDeclarativeModule();
				will(returnValue(dm));
				
			allowing(dm).getErrorChunk();
				will(returnValue(errorChunk));
			allowing(dm).getAdapter(IDeclarativeModule4.class);
				will(returnValue(dm));
			allowing(dm).isPartialMatchingEnabled();
				will(returnValue(false));
			
			allowing(chunk).getSubsymbolicChunk();
				will(returnValue(ssc));
			
			allowing(ssc).getActivation();
				will(returnValue(5.5d));
				
		}};
		
		context.checking(expectations);
		DefaultRetrievalTimeEquation eq = new DefaultRetrievalTimeEquation(rm);
		double rt = eq.computeRetrievalTime(chunk);
		
		assertThat(String.format(Locale.ENGLISH, "%1$5.3f", rt), is("0.192"));
	}

}
