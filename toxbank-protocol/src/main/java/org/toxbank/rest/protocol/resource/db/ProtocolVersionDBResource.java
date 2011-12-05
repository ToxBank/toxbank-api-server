package org.toxbank.rest.protocol.resource.db;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.ReadProtocolVersions;

public class ProtocolVersionDBResource extends ProtocolDBResource<ReadProtocolVersions> {

	protected ReadProtocolVersions getProtocolQuery(Object key) throws ResourceException {
		if (key==null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}			
		else {
			singleItem = false;
			int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
			return new ReadProtocolVersions(id[0]);
		}
	}
}
