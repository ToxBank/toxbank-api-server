package org.toxbank.rest.protocol.resource.db.template;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.resource.ITemplate;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.Protocol;
import org.toxbank.rest.protocol.db.template.ReadDataTemplate;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryHTMLReporter;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;

public class DataTemplateResource extends QueryResource<ReadDataTemplate,IProtocol> {

	@Override
	public IProcessor<ReadDataTemplate, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new DataTemplateReporter(),MediaType.TEXT_PLAIN);
			
		} else	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	
						new ProtocolQueryURIReporter(getRequest(),ITemplate.resource)
						,MediaType.TEXT_URI_LIST);
				
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON) ||
					variant.getMediaType().equals(MediaType.TEXT_CSV) 
					
					) {
			return new StringConvertor(new DataTemplateReporter(),variant.getMediaType());
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML))
				return new OutputWriterConvertor(
						new ProtocolQueryHTMLReporter(getRequest(),false,false),
						MediaType.TEXT_HTML);
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}	
	@Override
	protected ReadDataTemplate createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object key = request.getAttributes().get(FileResource.resourceKey);		
		try {
	
			if (key==null) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}			
			else {
				if (key.toString().startsWith("P")) {
					return new ReadDataTemplate(new Protocol(new Integer(Reference.decode(key.toString().substring(1)))));
				} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %d",key),
					x
					);
		}
	} 

	@Override
	protected QueryURIReporter<IProtocol, ReadDataTemplate> getURUReporter(
			Request baseReference) throws ResourceException {
		ProtocolQueryURIReporter q = new ProtocolQueryURIReporter(getRequest(),ITemplate.resource);
		return q;
	}

	@Override
	public String getConfigFile() {
		return "conf/tbprotocol-db.pref";
	}
	
	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_RDF_XML.equals(mediaType);
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			List<FileItem> input, IProtocol item) throws ResourceException {
		// TODO Auto-generated method stub
		//return super.createCallable(method, input, item);
		return null;
	}	

}
