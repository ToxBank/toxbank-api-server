package org.toxbank.rest.protocol.resource.db;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.ReadProtocolVersions;

public class ProtocolVersionDBResource<Q extends ReadProtocol> extends ProtocolDBResource<Q> {

	protected Q getProtocolQuery(Object key,Object search) throws ResourceException {
		if (key==null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}			
		else {
			singleItem = false;
			int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
			return (Q)new ReadProtocolVersions(id[0]);
		}
	}
	
	
	@Override
	protected Q createPOSTQuery(Context context, Request request,
			Response response) throws ResourceException {
		Object key = request.getAttributes().get(FileResource.resourceKey);		
		if (key!=null) { //post allowed only on /protocol/id/versions
			int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
			return (Q)new ReadProtocol(id[0],id[1]);
		}
		else throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}	
}
