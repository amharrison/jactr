package org.jactr.tools.tracer.sinks.trace.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.jactr.tools.tracer.transformer.ITransformedEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class TraceFileManagerTest {
	
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();
	
	@Test
	public void mustNotThrowExceptionIfFlushIsCalledBeforeNewRecord() throws IOException {
		
		ITransformedEvent event = context.mock(ITransformedEvent.class);
		
		Expectations expectations = new Expectations() {{
			allowing(event).getSimulationTime();
				will(returnValue(0.0d));
		}};
		
		context.checking(expectations);
		
		File outputDirectory = File.createTempFile("output", "dir");
		try {
			TraceFileManager manager = new TraceFileManager(outputDirectory);
			manager.flush();
			manager.record(event);
			assertTrue(true);
		} catch (Exception ex) {
			fail("Unexpected exception: "+ex.getClass().getName()
					+": "+ex.getMessage());
		} finally {
			outputDirectory.delete();
		}
	}
}
