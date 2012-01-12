package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.toxbank.client.Resources;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;

public class ProtocolDocumentResource extends QueryResource<IQueryRetrieval<DBProtocol>,DBProtocol> {
	protected String suffix = Resources.document;
	
	public ProtocolDocumentResource() {
		this(Resources.document);
	}
	public ProtocolDocumentResource(String suffix) {
		super();
		this.suffix = suffix;
	}
	
	@Override
	public IProcessor<IQueryRetrieval<DBProtocol>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) 
			return new StringConvertor(	
					new ProtocolQueryURIReporter(getRequest(),suffix)
					,MediaType.TEXT_URI_LIST,filenamePrefix);
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) 
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML,filenamePrefix);	
			else	
				return new DownloadDocumentConvertor(createFileReporter(),null,filenamePrefix);
	}
	protected QueryHTMLReporter createHTMLReporter() throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
	
	protected FileReporter createFileReporter() throws ResourceException {
		return new FileReporter();
	}
	
	@Override
	protected IQueryRetrieval<DBProtocol> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		final Object key = request.getAttributes().get(FileResource.resourceKey);		
		try {
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			else {
				int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
				return new ReadFilePointers(id[0],id[1]);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %d",key),
					x
					);
		}
	}
}
