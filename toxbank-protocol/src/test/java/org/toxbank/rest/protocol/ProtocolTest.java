package org.toxbank.rest.protocol;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.toxbank.rest.protocol.metadata.Document;

public class ProtocolTest {

	@Test
	public void testID() {
		DBProtocol p = new DBProtocol();
		p.setID(666);
		Assert.assertEquals(666, p.getID());
	}

	@Test
	public void testFilename() throws Exception {
		DBProtocol p = new DBProtocol();
		URL url = getClass().getClassLoader().getResource("org/toxbank/protocol/tb.xml");
		p.setDocument(new Document(url.toURI()));
		File file = new File(p.getDocument().getURI());
		Assert.assertTrue(file.exists());
	}

}
