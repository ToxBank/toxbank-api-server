package org.toxbank.rest.protocol;

import junit.framework.Assert;

import org.junit.Test;

public class ProtocolTest {

	@Test
	public void testID() {
		MyProtocol p = new MyProtocol();
		p.setID(666);
		Assert.assertEquals(666, p.getID());
	}

	@Test
	public void testFilename() {
		MyProtocol p = new MyProtocol();
		p.setFileName("test.owl");
		Assert.assertEquals("test.owl", p.getFileName());
	}

}
