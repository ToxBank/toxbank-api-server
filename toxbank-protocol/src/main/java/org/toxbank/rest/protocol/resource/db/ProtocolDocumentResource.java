package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.QueryResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.db.ReadProtocol;

public class ProtocolDocumentResource extends QueryResource<ReadProtocol,IProtocol> {

	@Override
	public IProcessor<ReadProtocol, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		//return new DownloadDocumentConvertor(null
			//	,MediaType.APPLICATION_PDF
			//	);
		return null;
	}

	@Override
	protected ReadProtocol createQuery(Context context, Request request,
			Response response) throws ResourceException {
		final Object key = request.getAttributes().get(FileResource.resourceKey);		
		try {
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			else {
				if (key.toString().startsWith("P")) {
					return new ReadProtocol(new Integer(Reference.decode(key.toString().substring(1))));
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

}
