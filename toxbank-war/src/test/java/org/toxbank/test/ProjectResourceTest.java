package org.toxbank.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;

import junit.framework.Assert;
import net.toxbank.client.Resources;
import net.toxbank.client.io.rdf.ProjectIO;
import net.toxbank.client.resource.Project;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.toxbank.rest.groups.DBGroup;
import org.toxbank.rest.groups.db.ReadProject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ProjectResourceTest  extends ResourceTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase(dbFile);
		
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s", port,Resources.project);
	}
	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	/**
	 * The URI should be /protocol/P1/datatemplate
	 */
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertTrue(line.startsWith(String.format("http://localhost:%d%s/G",port,Resources.project)));
			count++;
		}
		return count==3;
	}	
	
	
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
	public void testRDF() throws Exception {
		testGet(String.format("http://localhost:%d%s/G1", port,Resources.project),MediaType.APPLICATION_RDF_XML);
	}
	
	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {
		
		OntModel model = ModelFactory.createOntologyModel();
		model.read(in,null);
		
		ProjectIO ioClass = new ProjectIO();
		List<Project> projects = ioClass.fromJena(model);
		Assert.assertEquals(1,projects.size());
		Assert.assertEquals(String.format("http://localhost:%d%s/G1",port,Resources.project),
													projects.get(0).getResourceURL().toString());
		Assert.assertEquals("DETECTIVE", projects.get(0).getTitle());
		//Assert.assertEquals("detective", projects.get(0).getGroupName());
		return model;
	}	
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof ReadProject);

		return o;
	}
	
	@Test
	public void testCreateEntryFromWebForm() throws Exception {
		Form form = new Form();
		form.add(DBGroup.fields.name.name(), "project");

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM organisation");
		Assert.assertEquals(3,table.getRowCount());
		c.close();

		RemoteTask task = testAsyncPoll(new Reference(String.format("http://localhost:%d%s", port,
				Resources.project)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(),
				Method.POST);
		//wait to complete
		while (!task.isDone()) {
			task.poll();
			Thread.sleep(100);
			Thread.yield();
		}
		Assert.assertTrue(task.getResult().toString().startsWith(String.format("http://localhost:%d/project/G",port)));

        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM project");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idproject,name,ldapgroup from project where idproject>3");
		Assert.assertEquals(1,table.getRowCount());

		String expectedURI = String.format("http://localhost:%d/project/G%s",port,table.getValue(0,"idproject"));
		Assert.assertEquals(expectedURI,task.getResult().toString());
		c.close();

	}	
	
	@Test
	public void testDelete() throws Exception {
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idproject FROM project where idproject=3");
		Assert.assertEquals(new BigInteger("3"),table.getValue(0,"idproject"));
		c.close();		
		String org = String.format("http://localhost:%d%s/G3", port,Resources.project);
		RemoteTask task = testAsyncPoll(new Reference(org),
				MediaType.TEXT_URI_LIST, null,
				Method.DELETE);
		Assert.assertEquals(Status.SUCCESS_OK, task.getStatus());
		//Assert.assertNull(task.getResult());
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM project where idproject=3");
		Assert.assertEquals(0,table.getRowCount());
		c.close();			
	}
}