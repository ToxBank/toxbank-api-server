package org.toxbank.rest.protocol.resource.db;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.convertors.RDFJenaConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.toxbank.client.io.rdf.TOXBANK;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.CallableProtocolUpload;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.ReadUser;
import org.toxbank.rest.user.resource.UserDBResource;

/**
 * Protocol resource
 * @author nina
 *
 * @param <Q>
 */
public class ProtocolDBResource<Q extends ReadProtocol> extends QueryResource<Q,DBProtocol> {

	
	protected boolean singleItem = false;
	protected boolean version = false;
	protected boolean editable = true;

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	
						new ProtocolQueryURIReporter(getRequest())
						,MediaType.TEXT_URI_LIST,filenamePrefix);
				
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON) ||
					variant.getMediaType().equals(MediaType.TEXT_CSV) 
					
					) {
				return new RDFJenaConvertor<DBProtocol, IQueryRetrieval<DBProtocol>>(
						new ProtocolRDFReporter<IQueryRetrieval<DBProtocol>>(
								getRequest(),variant.getMediaType(),getDocumentation())
						,variant.getMediaType(),filenamePrefix) {
					@Override
					protected String getDefaultNameSpace() {
						return TOXBANK.URI;
					}					
				};
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML))
				return new OutputWriterConvertor(
						new ProtocolQueryHTMLReporter(getRequest(),!singleItem,isEditable()),
						MediaType.TEXT_HTML);
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
	protected boolean isEditable() {
		return editable
			   ?version?true:!singleItem	
			   :false;
	}

	protected Q getProtocolQuery(Object key,int userID,Object search, Object modified, boolean showCreateLink) throws ResourceException {
		
		if (key==null) {
			ReadProtocol query = new ReadProtocol();
			
			if (search != null) {
				DBProtocol p = new DBProtocol();
				p.setTitle(search.toString());
				query.setValue(p);
			} else if (modified != null) try {
				DBProtocol p = new DBProtocol();
				p.setTimeModified(Long.parseLong(modified.toString()));
				query.setValue(p);
			} catch (Exception x) {x.printStackTrace();}
//			query.setFieldname(search.toString());
			editable = showCreateLink;
			if (userID>0) {
				query.setFieldname(new DBUser(userID));
			} else query.setShowUnpublished(false);
			return (Q)query;
		}			
		else {
			editable = showCreateLink;
			singleItem = true;
			int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
			ReadProtocol query =  new ReadProtocol(id[0],id[1]);
			query.setShowUnpublished(true);
			if (userID>0) query.setFieldname(new DBUser(userID));
			return (Q)query;
		}
	}
	
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		Object search = null;
		try {
			search = form.getFirstValue("search").toString();
		} catch (Exception x) {
			search = null;
		}		
		Object modified = null;
		try {
			modified = form.getFirstValue("modifiedSince").toString();
		} catch (Exception x) {
			modified = null;
		}			
		boolean showCreateLink = false;
		try {
			String n = form.getFirstValue("new");
			showCreateLink = n==null?false:Boolean.parseBoolean(n);
		} catch (Exception x) {
			showCreateLink = false;
		}
		Object key = request.getAttributes().get(FileResource.resourceKey);
		int userID = -1;
		try {
			Object userKey = request.getAttributes().get(UserDBResource.resourceKey);
			if (userKey!=null)
				userID = ReadUser.parseIdentifier(userKey.toString());
		} catch (Exception x) {}
		
		try {
			return getProtocolQuery(key,userID,search,modified,showCreateLink);
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %s",key),
					x
					);
		}
	} 

	@Override
	protected QueryURIReporter<DBProtocol, Q> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ProtocolQueryURIReporter(getRequest());
	}

	@Override
	public String getConfigFile() {
		return "conf/tbprotocol-db.pref";
	}
	
	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.MULTIPART_FORM_DATA.equals(mediaType);
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBProtocol item) throws ResourceException {
		if (Method.DELETE.equals(method))
			return createCallable(method,(List<FileItem>) null, item);
		else throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED,method.toString());
	}
	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			List<FileItem> input, DBProtocol item) throws ResourceException {
		/*
		if ((getRequest().getClientInfo().getUser()==null) ||
				getRequest().getClientInfo().getUser().getIdentifier()==null)
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			
		DBUser user = new DBUser();
		user.setUserName(getRequest().getClientInfo().getUser().getIdentifier());
		*/
		Connection conn = null;
		try {
			ProtocolQueryURIReporter r = new ProtocolQueryURIReporter(getRequest(),"");
			class TDBConnection extends DBConnection {
				public TDBConnection(Context context,String configFile) {
					super(context,configFile);
				}
				public String getDir() {
					loadProperties();
					return properties.getProperty("dir.protocol");
				}
			};
			TDBConnection dbc = new TDBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(getRequest());

			String dir = dbc.getDir();
			if ("".equals(dir)) dir = null;
			return new CallableProtocolUpload(method,item,null,input,conn,r,getToken(),getRequest().getRootRef().toString(),
						dir==null?null:new File(dir)
			);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}

	}
	
	@Override
	protected Q createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(FileResource.resourceKey);
		if (Method.POST.equals(method)) {
			if (key==null) return null;//post allowed only on /protocol level, not on /protocol/id
		} else if (Method.DELETE.equals(method)) {
			if (key!=null) return super.createUpdateQuery(method, context, request, response);
		} else if (Method.PUT.equals(method)) {
			if (key!=null) {
				int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
				return (Q)new ReadProtocol(id[0],id[1]);
			}
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);		
	}
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage);
	}
	
	protected TaskCreator getTaskCreator(Form form, final Method method, boolean async, final Reference reference) throws Exception {
		if (Method.DELETE.equals(method))
			return super.getTaskCreator(form, method, async, reference);
		else
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Not multipart web form!");
	}
}
