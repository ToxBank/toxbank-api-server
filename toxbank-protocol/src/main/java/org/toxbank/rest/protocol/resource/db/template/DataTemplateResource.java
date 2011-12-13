package org.toxbank.rest.protocol.resource.db.template;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
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
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.CallableProtocolUpload;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;
import org.toxbank.rest.protocol.resource.db.FileReporter;
import org.toxbank.rest.protocol.resource.db.ProtocolDocumentResource;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;

public class DataTemplateResource extends ProtocolDocumentResource {

	public DataTemplateResource() {
		super(Resources.datatemplate);
	}
	@Override
	protected QueryHTMLReporter createHTMLReporter() throws ResourceException {
		return new DataTemplateHTMLReporter(getRequest(),true,true);
	}

	@Override
	protected FileReporter createFileReporter() throws ResourceException {
		return new FileReporter() {
			@Override
			public Object processItem(DBProtocol item) throws AmbitException {
				try {
					File file = new File(item.getDataTemplate().getResourceURL().toURI());
					setOutput(new FileRepresentation(file,MediaType.TEXT_ALL));
					return item;
				} catch (URISyntaxException x) {
					throw new AmbitException(x);
				}
			}
		};
	}
	

	@Override
	protected QueryURIReporter<DBProtocol, IQueryRetrieval<DBProtocol>> getURUReporter(
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
			callable.setSetDataTemplateOnly(true);
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
			if (key!=null) { //post allowed only on /protocol/id/datatemplate
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
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Not multipart web form!");
	}	
}


