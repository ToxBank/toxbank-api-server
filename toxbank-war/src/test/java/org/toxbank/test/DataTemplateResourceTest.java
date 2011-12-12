package org.toxbank.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;

import junit.framework.Assert;
import net.toxbank.client.Resources;
import net.toxbank.client.resource.Protocol;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;

public class DataTemplateResourceTest extends ResourceTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase(dbFile);
		
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/%s-1-1%s", port,Resources.protocol,
					Protocol.id_prefix,Resources.datatemplate);
	}
	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	/**
	 * The URI should be /protocol/SEURAT-Protocol-1-1/datatemplate
	 */
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(
					String.format("http://localhost:%d%s/%s-1-1%s",port,Resources.protocol,
							Protocol.id_prefix,Resources.datatemplate)
							, line);
			count++;
		}
		return count==1;
	}	
	
	//have to ensure test files are stored in a reachable location
	public void testTXT() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_PLAIN);
	}
	/**
	 * Reading the template and ensuring the same garbage we put in is being read
	 */
	@Override
	public boolean verifyResponseTXT(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals("ABCDEFGH", line);
			count++;
		}
		return count==1;
	}
	
	@Test
	public void testCreateEntryFromMultipartWeb() throws Exception {
		String url = createEntryFromMultipartWeb(new Reference(getTestURI()));
		
		testGet(url,MediaType.TEXT_URI_LIST);		
		
		 IDatabaseConnection c = getConnection();	
		 ITable  table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT p.idprotocol,p.version,filename,template from protocol p where p.idprotocol=1 and p.version=1");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"version"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"idprotocol"));
		File f = new File(new URI(table.getValue(0,"template").toString()));
		Assert.assertTrue(f.exists());
		f.delete();
		c.close();
	}
	
	public String createEntryFromMultipartWeb(Reference uri) throws Exception {
		URL url = getClass().getClassLoader().getResource("org/toxbank/protocol/protocol-sample.pdf");
		File file = new File(url.getFile());
		
		String[] names = new String[0];
		String[] values = new String[0];
		Representation rep = getMultipartWebFormRepresentation(names,values,"template",file,MediaType.APPLICATION_PDF.toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(3,table.getRowCount());
		c.close();

		RemoteTask task = testAsyncPoll(uri,
				MediaType.TEXT_URI_LIST, rep,
				Method.POST);
		//wait to complete
		while (!task.isDone()) {
			task.poll();
			Thread.sleep(100);
			Thread.yield();
		}
		if (!task.isCompletedOK())
			System.out.println(task.getError());
		Assert.assertTrue(task.getResult().toString().startsWith(
							String.format("http://localhost:%d/protocol/%s",port,Protocol.id_prefix)));
		
		return task.getResult().toString();


	}		
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof ReadFilePointers);

		return o;
	}
	
}
