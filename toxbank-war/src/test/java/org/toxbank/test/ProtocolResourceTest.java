package org.toxbank.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import net.idea.restnet.i.tools.DownloadTool;
import net.toxbank.client.Resources;
import net.toxbank.client.io.rdf.ProtocolIO;
import net.toxbank.client.resource.Protocol;
import net.toxbank.client.resource.Protocol.STATUS;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.toxbank.rest.protocol.db.ReadProtocol;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * test for {@link PropertyResource}
 * @author nina
 *
 */
public class ProtocolResourceTest extends ProtectedResourceTest {
	
	@Override
	protected boolean isAAEnabled() {
		return false;
	}
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase(dbFile);
		
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/%s-1-1", port,Resources.protocol,STATUS.RESEARCH.getPrefix());
	}
	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(
					String.format("http://localhost:%d%s/%s-1-1",port,Resources.protocol,STATUS.RESEARCH.getPrefix())
							, line);
			count++;
		}
		return count==1;
	}	
	
	@Test
	public void testRDF() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}
	
	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {
		
		OntModel model = ModelFactory.createOntologyModel();
		model.read(in,null);
		
		ProtocolIO ioClass = new ProtocolIO();
		List<Protocol> protocols = ioClass.fromJena(model);
		Assert.assertEquals(1,protocols.size());
		Assert.assertEquals(String.format("http://localhost:8181/protocol/%s-1-1",STATUS.RESEARCH.getPrefix()),
					protocols.get(0).getResourceURL().toString());
		Assert.assertEquals("SEURAT-Protocol-1-1", protocols.get(0).getIdentifier());
		Assert.assertEquals("Very important protocol", protocols.get(0).getTitle());
		Assert.assertNotNull(protocols.get(0).getAbstract());
		Assert.assertEquals(5,protocols.get(0).getAbstract().indexOf("\u2122")); // TM symbol
		
		Assert.assertTrue(protocols.get(0).isPublished());
		Assert.assertNotNull(protocols.get(0).getOwner());
		Assert.assertEquals(String.format("http://localhost:%d%s/U1",port,Resources.user),
				protocols.get(0).getOwner().getResourceURL().toString());
		Assert.assertEquals(1,protocols.get(0).getProjects().size());
		//Assert.assertEquals("abcdef", protocols.get(0).getOwner().getFirstname());
		return model;
	}
	
	/*
	@Test
	public void testQueryName() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(
				String.format("http://localhost:%d%s?%s=%s", port,
						PropertyResource.featuredef,QueryResource.search_param,"Property")
				));
		iterator.setCloseModel(true);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			Property p = iterator.next();
			Assert.assertTrue(p.getName().startsWith("Property"));

		}
		iterator.close();
	}	

	@Test
	public void testRDFXML() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			
			Property p = iterator.next();
			Assert.assertEquals("Property 1",p.getName());
			Assert.assertEquals(1,p.getId());
		}
		iterator.close();
	}	
	
	/*
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {

			count++;
		}
		return count>1;
	}
	
	*/


	@Test
	public void testDelete() throws Exception {
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idprotocol,version FROM protocol where idprotocol=1 and version=1");
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"idprotocol"));
		c.close();		
		String org = String.format("http://localhost:%d%s/%s-1-1", port,Resources.protocol,STATUS.RESEARCH.getPrefix());
		RemoteTask task = testAsyncPoll(new Reference(org),
				MediaType.TEXT_URI_LIST, null,
				Method.DELETE);
		Assert.assertEquals(Status.SUCCESS_OK, task.getStatus());
		//Assert.assertNull(task.getResult());
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol where idprotocol=1 and version=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();			
	}
	/*
	@Test
	public void testCopyEntry() throws Exception {
		
		Form form = new Form();  
		form.add(OpenTox.params.feature_uris.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals("http://localhost:8181/feature/1",response.getLocationRef().toString());
		System.out.println(response.getLocationRef());
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}	
	@Test
	public void testCopyEntry1() throws Exception {
		OntModel model = OT.createModel();
		Property p = new Property(null);
		p.setId(1);
		
		Request q = new Request();
		q.setRootRef(new Reference(String.format("http://localhost:%d", port)));
		PropertyURIReporter reporter = new PropertyURIReporter(q,null);
		
		PropertyRDFReporter.addToModel(model, 
				p,
				reporter,
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		
		Form form = new Form();  
		form.add(OpenTox.params.feature_uris.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateEntry2() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("feature.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals("http://localhost:8181/feature/4", response.getLocationRef().toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='cas' and comments='CasRN' and title='http://my.resource.org' and url='Default'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateEntry3() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream("feature1.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/4");
		
		//weird nondeterministic error in ambit.uni-plovdiv.bg
		for (int i=0; i < 100;i++) {
			response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					//String.format("http://localhost:8080/ambit2-www%s",PropertyResource.featuredef),
					//String.format("http://ambit.uni-plovdiv.bg:8080/ambit2%s",PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
			//System.out.println(response.getStatus());
			Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
			//Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/http%3A%2F%2Fother.com%2Ffeature%2F200Default");
			
		}
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where name='http://other.com/feature/200'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	*/
	/*
	@Test
	public void testGetJavaObject() throws Exception {
		testGetJavaObject(String.format("http://localhost:%d%s?%s=%s", port,IProtocol.resource,
				OpenTox.params.sameas.toString(),Reference.encode(Property.opentox_CAS)),
				MediaType.APPLICATION_JAVA_OBJECT,org.restlet.data.Status.SUCCESS_OK);
	}
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof RetrieveFieldNamesByAlias);
		RetrieveFieldNamesByAlias query = (RetrieveFieldNamesByAlias)o;
		Assert.assertEquals(Property.opentox_CAS,query.getValue());
		return o;
	}
	*/
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof ReadProtocol);

		return o;
	}
	
	@Test
	public void testUpdateEntryFromMultipartWeb() throws Exception {
		String uri = String.format("http://localhost:%d%s/%s-2-1", port,Resources.protocol,STATUS.RESEARCH.getPrefix());
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idprotocol,version,iduser from protocol_authors p where p.idprotocol=2 and version=1");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(new BigInteger("3"),table.getValue(0,"iduser"));
		c.close();

		createEntryFromMultipartWeb(new Reference(uri),Method.PUT);
		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT p.idprotocol,p.version,published from protocol p where p.idprotocol=2 and version=1");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(Boolean.TRUE,table.getValue(0,"published"));
		table = 	c.createQueryTable("EXPECTED","SELECT idprotocol,version,iduser from protocol_authors p where p.idprotocol=2 and version=1 order by iduser");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"iduser"));
		Assert.assertEquals(new BigInteger("2"),table.getValue(1,"iduser"));
		
		table = 	c.createQueryTable("EXPECTED","SELECT idprotocol,version,idproject from protocol_projects p where p.idprotocol=2 and version=1 order by idproject");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
	}
	@Test
	public void testCreateVersionEntryFromMultipartWeb() throws Exception {
		String url =createEntryFromMultipartWeb(new Reference(getTestURI()+Resources.versions));
		 IDatabaseConnection c = getConnection();	
		 ITable  table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(5,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT p.idprotocol,p.version,filename from protocol p where p.idprotocol=1 order by version");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"version"));
		Assert.assertEquals(new BigInteger("2"),table.getValue(1,"version"));
		File f = new File(new URI(table.getValue(1,"filename").toString()));
		Assert.assertNotSame(getTestURI(),url);
		Assert.assertTrue(f.exists());
		f.delete();
		c.close();
	}
	@Test
	public void testCreateEntryFromMultipartWeb() throws Exception {
		String url = createEntryFromMultipartWeb(new Reference(String.format("http://localhost:%d%s", port,Resources.protocol)));
		
		testGet(String.format("%s%s",
				url,Resources.document),
				MediaType.APPLICATION_PDF);		
		
		 IDatabaseConnection c = getConnection();	
		 ITable  table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(5,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT p.idprotocol,p.version,filename,pa.iduser,status from protocol p join protocol_authors pa where pa.idprotocol=p.idprotocol and p.version=pa.version and p.idprotocol>2 order by pa.iduser");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"version"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"iduser"));
		Assert.assertEquals(new BigInteger("2"),table.getValue(1,"iduser"));
		Assert.assertEquals(STATUS.SOP.toString(),table.getValue(0,"status"));
		File f = new File(new URI(table.getValue(0,"filename").toString()));
		//System.out.println(f);
		Assert.assertTrue(f.exists());
		f.delete();
		//multiple projects
		table = 	c.createQueryTable("EXPECTED","SELECT p.idprotocol,p.version,idproject from protocol p join protocol_projects pp where pp.idprotocol=p.idprotocol and p.version=pp.version and p.idprotocol>2 order by pp.idproject");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
	}
	public String createEntryFromMultipartWeb(Reference uri) throws Exception {
		return createEntryFromMultipartWeb(uri,Method.POST);
	}
	public String createEntryFromMultipartWeb(Reference uri,Method method) throws Exception {
		URL url = getClass().getClassLoader().getResource("org/toxbank/protocol/protocol-sample.pdf");
		File file = new File(url.getFile());
		
		String[] names = new String[ReadProtocol.fields.values().length+1];
		String[] values = new String[ReadProtocol.fields.values().length+1];
		int i=0;
		for (ReadProtocol.fields field : ReadProtocol.entryFields) {
			switch (field) {
			case idprotocol: continue;
			case filename: continue;
			/*
			case user_uri: { 
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.user,"U1");
				break;
			}
			*/
			case project_uri: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.project,"G1");
				break;
			}
			case organisation_uri: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.organisation,"G2");
				break;
			}
			case user_uri: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.user,"U1");
				break;
			}		
			case author_uri: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.user,"U2");
				break;
			}	
			case allowReadByGroup: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.organisation,"G1");
				break;
			}	
			case allowReadByUser: {
				values[i] = String.format("http://localhost:%d%s/%s",port,Resources.user,"U2");
				break;
			}	
			case status: {
				values[i] = STATUS.SOP.toString();
				break;
			}
			case anabstract: {
				values[i] = "My abstract\u2122";
				break;
			}
			case published: {
				values[i] = Boolean.TRUE.toString();
				break;
			}
			default: {
				values[i] = field.name();
			}
			}
			names[i] = field.name();
			
			i++;
		}
		//yet another author
		values[i] = String.format("http://localhost:%d%s/%s",port,Resources.user,"U1");
		names[i] = ReadProtocol.fields.author_uri.name();
		values[i+1] = null;
		names[i+1] = ReadProtocol.fields.author_uri.name();
		//second project
		values[i+2] = String.format("http://localhost:%d%s/%s",port,Resources.project,"G2");
		names[i+2] = ReadProtocol.fields.project_uri.name();
		
		Representation rep = getMultipartWebFormRepresentation(names,values,file,MediaType.APPLICATION_PDF.toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(4,table.getRowCount());
		c.close();

		RemoteTask task = testAsyncPoll(uri,
				MediaType.TEXT_URI_LIST, rep,
				method);
		//wait to complete
		while (!task.isDone()) {
			task.poll();
			Thread.sleep(100);
			Thread.yield();
		}
		if (!task.isCompletedOK())
			System.out.println(task.getError());
		Assert.assertTrue(task.getResult().toString().startsWith(
							String.format("http://localhost:%d/protocol/%s",port,STATUS.RESEARCH.getPrefix())));
		
		return task.getResult().toString();


	}	
	
	
	@Test
	public void testPublish() throws Exception {
		String uri = String.format("http://localhost:%d%s/%s-2-1", port,Resources.protocol,STATUS.RESEARCH.getPrefix());

		File file = new File(getClass().getClassLoader().getResource("org/toxbank/protocol/protocol-sample.pdf").getFile());
		
		String[] names = new String[11];
		String[] values = new String[11];
		values[0] = Boolean.TRUE.toString();
		names[0] = "published";
		Representation rep = getMultipartWebFormRepresentation(names,values,file,MediaType.APPLICATION_PDF.toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(4,table.getRowCount());
		c.close();

		RemoteTask task = testAsyncPoll(new Reference(uri),MediaType.TEXT_URI_LIST, rep,Method.PUT);
		//wait to complete
		while (!task.isDone()) {
			task.poll();
			Thread.sleep(100);
			Thread.yield();
		}
		if (!task.isCompletedOK())
			System.out.println(task.getError());
		Assert.assertTrue(task.getResult().toString().startsWith(
							String.format("http://localhost:%d/protocol/%s",port,STATUS.RESEARCH.getPrefix())));
		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM protocol");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT published from protocol where idprotocol=2 and version=1 and published=true");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}
	
	public void testDownloadFile() throws Exception {
		testGet(String.format("http://localhost:%d%s/%s-1-1%s", 
					port,
					Resources.protocol,
					STATUS.RESEARCH.getPrefix(),
					Resources.document),
					MediaType.APPLICATION_PDF);
	}	
	@Override
	public boolean verifyResponsePDF(String uri, MediaType media, InputStream in)
			throws Exception {
		
		File file = File.createTempFile("test", ".pdf");
		file.deleteOnExit();
		DownloadTool.download(in, file);
		//System.out.println(file.getAbsolutePath());
		return file.exists();
	}
}
