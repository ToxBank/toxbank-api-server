package org.toxbank.rest.protocol;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;
import net.toxbank.client.resource.Document;

import org.junit.Test;

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
		p.setDocument(new Document(url));
		File file = new File(p.getDocument().getResourceURL().toURI());
		Assert.assertTrue(file.exists());
	}

}
