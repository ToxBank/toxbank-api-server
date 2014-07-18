package org.toxbank.rest.protocol.resource.db;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.toxbank.client.Resources;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.CallableProtocolUpload;
import org.toxbank.rest.protocol.CallableProtocolUpload.UpdateMode;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;

public class ProtocolPublicationResource extends ProtocolDocumentResource {

	public ProtocolPublicationResource() {
		super(Resources.publication);
	}


	@Override
	public String getConfigFile() {
		return "conf/tbprotocol-db.pref";
	}
	
	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType) || MediaType.MULTIPART_FORM_DATA.equals(mediaType);
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			List<FileItem> input, DBProtocol item) throws ResourceException {

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
			CallableProtocolUpload callable =  new CallableProtocolUpload(method,item,null,input,conn,r,getToken(),getRequest().getRootRef().toString(),
						dir==null?null:new File(dir));
			callable.setUpdateMode(UpdateMode.publication);
			return callable;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}

		/*
		Form form = new Form();
		form.add(PageParams.params.resulturi.name(),String.format("%s/ProtocolMockup",getRequest().getResourceRef()));
		form.add(PageParams.params.delay.name(),"1");
		return new CallableMockup(form,getToken());
		*/
	}
	
	@Override
	protected IQueryRetrieval<DBProtocol> createUpdateQuery(Method method,
			Context context, Request request, Response response)
			throws ResourceException {
		Object key = request.getAttributes().get(FileResource.resourceKey);
		if (Method.POST.equals(method)) {
			if (key!=null) { //post allowed only on /protocol/id/publication
				int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
				return new ReadFilePointers(id[0],id[1]);
			}
		} else {
			//if (key!=null) return super.createUpdateQuery(method, context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);		
	}	
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage);
	}
	
	protected TaskCreator getTaskCreator(Form form, final Method method, boolean async, final Reference reference) throws Exception {
		if (Method.POST.equals(method))
			return super.getTaskCreator(form, method, async, reference);
		else	
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);			
	}	
	@Override
	protected TaskCreator getTaskCreator(List<FileItem> fileItems,
			Method method, boolean async) throws Exception {
		if (Method.POST.equals(method))
			return super.getTaskCreator(fileItems, method, async);
		else
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			
	}
}
