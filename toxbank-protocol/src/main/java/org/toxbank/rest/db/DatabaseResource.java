package org.toxbank.rest.db;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.ProcessorException;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.db.DbCreateDatabase;

/**
 * Reports version of the database used. 
 * The database name is defined at compile time.
 * It it is either ambit2, or taken from .m2/settings.xml property
 * <ambit.db>ambit2</ambit.db>
 * @author nina
 * @example_get
 * <pre>
 * curl -X GET http://host:port/ambit2/admin/database
 * </pre>
 * POST creates a new database, if it doesn not exist alreadt. The dbname should match with the predefined database name.
 * User and password should be mysql credentials with create database privileges.
 * @example_post
 * <pre>
 * curl -X GET http://host:port/ambit2/admin/database -d "dbname=newdb" -d "user=uname" -d "pass=pass"
 * </pre>
 * @param <Q>
 * @param <T>
 */
public class DatabaseResource  extends QueryResource<DBVersionQuery,DBVersion> {
	public static final String resource = "database";
	
	public DatabaseResource() {
		super();
		maxRetry = 1;
	}
	
	@Override
	public IProcessor<DBVersionQuery, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			return new StringConvertor(new DBTextReporter(),MediaType.TEXT_PLAIN);
		} if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(new DBHtmlReporter(getRequest()),MediaType.TEXT_HTML);
			/*} 
		
		
			*/
		} else //html 	
			return new StringConvertor(new DBHtmlReporter(getRequest()),MediaType.TEXT_HTML);
		
	}	

	@Override
	protected Representation processNotFound(NotFoundException x, int retry)
			throws Exception {
		return null;
	}
	@Override
	protected DBVersionQuery createQuery(Context context, Request request, Response response)
			throws ResourceException {
		return new DBVersionQuery();
	}


	@Override
	protected Representation get(Variant variant) throws ResourceException {
		DBConnection c = new DBConnection(getContext(),getConfigFile());
		Connection connection = null;
		try {
			connection = c.getConnection();
			DbCreateDatabase dbc = new DbCreateDatabase();
			dbc.setConnection(connection);
			if (!dbc.dbExists(c.getLoginInfo().getDatabase()))
				return processSQLError(null,1, variant);
			else {
				List<String> tables = dbc.tablesExists(c.getLoginInfo().getDatabase());
				if ((tables==null) || tables.size()==0)
					return processSQLError(null,1, variant);
			}
					
		} catch (ResourceException x) {
			x.printStackTrace();
		} catch (Throwable e) {	
			e.printStackTrace();
		
		} finally {
			try {connection.close();} catch (Exception x) {}
		}
		return super.get(variant);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		

		if ((entity == null) || !entity.isAvailable()) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Expected %s dbname={databasename}",MediaType.APPLICATION_WWW_FORM));
				
		Form form = new Form(entity);
		DBConnection c = new DBConnection(getContext(),getConfigFile());
		
		if (!c.allowDBCreate())
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Not allowed to create the database via REST; set <ambit.db.create.allow>true</ambit.db.create.allow> into maven settings.xml file and recompile to allow.");
		
		
		if (!c.getLoginInfo().getDatabase().equals(form.getFirstValue("dbname")))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("dbname parameter doesn't match the database name",form.getFirst("dbname")));

		if (form.getFirstValue("user")==null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"User credentials should be specified via user={} and pass={} web form parameters");
		/*
		try {
			if (dbExists(form.getFirstValue("dbname")))
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Database already exist, if you really need to create a new one, drop it manually first.\n");
		} catch (ResourceException x) {
			throw x;
		} catch (Throwable e) {	
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage());
		} finally {
		
		}
		*/
		return createDB(form.getFirstValue("dbname"),form.getFirstValue("user"),form.getFirstValue("pass"));	
		
		/*
		return new StringRepresentation(String.format("Client %s<br>Server %s<br>Database %s",
				getRequest().getClientInfo().getAddress(),
				form.getFirstValue("user"),
				form.getFirstValue("pass"),
				form.getFirstValue("dbname")
				
				));
				*/
			
	}
	
	public boolean dbExists(String dbname) throws Exception {
		boolean ok = false;
		ResultSet rs = null;
		Statement st = null;
		Connection connection = null;
		try {
    		DBConnection dbc = new DBConnection(getContext(),getConfigFile());
    	
    		connection = dbc.getConnection(getRequest());
			
			st = connection.createStatement();
			rs = st.executeQuery("show databases");
			while (rs.next()) {
				if (dbname.equals(rs.getString(1))) ok = true;
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
			try {if(connection != null) connection.close();} catch (Exception x) {}
		}
		return ok;
	}		
	
	public Representation createDB(String dbname,String user,String pass) throws ResourceException {
		//TODO refactor with Query/Update classes
		Connection c = null;
		
		
		DbCreateDatabase dbCreate=null;
		try {
    		DBConnection dbc = new DBConnection(getContext(),getConfigFile());
    		LoginInfo li = dbc.getLoginInfo();

			c = dbc.getConnection();
			
			
			dbCreate = new DbCreateDatabase();
    		dbCreate.setConnection(c);
    		dbCreate.open();
    		dbCreate.process(dbname);
			
    		if (dbExists(dbname)) {
				getResponse().setStatus(Status.SUCCESS_OK);
	
				DBVersion db = new DBVersion();
				db.setDbname(dbname);
				return generateRepresentation(db, false);
    		} else throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
    				String.format("The server tried to create database '%s' but did not succeeded.",dbname)
    				);
			
			
		} catch (SQLException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (ProcessorException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus((x.getCause() instanceof SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
					x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());			
			getResponse().setEntity(null);
		} finally {
			try {if (dbCreate!=null) dbCreate.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
			return new StringRepresentation(dbname);
		}
	}		
	
	@Override
	protected Representation processSQLError(SQLException x, int retry,
			Variant variant) throws Exception {
		try {
			DBVersion db = new DBVersion();
			DBConnection dbc = new DBConnection(getContext(),getConfigFile());
			db.setDbname(dbc.getLoginInfo().getDatabase());
			dbc = null;
			return generateRepresentation(db, true);
		} catch (Exception xx) {
			x.printStackTrace();
			return null;
		} finally {
			
		}
	}
	

	protected Representation generateRepresentation(DBVersion db, boolean create) 
			 throws Exception {
		try {
			Writer writer = new StringWriter();
			DBHtmlReporter reporter = new DBHtmlReporter(getRequest());
			reporter.setCreate(create);
			reporter.setOutput(writer);
			reporter.header(writer, null);

			reporter.processItem(db);
			reporter.footer(writer, null);
			writer.flush();
			
			return new StringRepresentation(writer.toString(),
					MediaType.TEXT_HTML,
					Language.ENGLISH,CharacterSet.UTF_8);
		} catch (Exception xx) {

			return null;
		} finally {
			
		}
	}
	@Override
	public String getConfigFile() {
		return "conf/tbprotocol-db.pref";
	}
}
