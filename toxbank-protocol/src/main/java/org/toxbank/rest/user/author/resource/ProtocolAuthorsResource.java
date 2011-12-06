package org.toxbank.rest.user.author.resource;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.ReadAuthor;
import org.toxbank.rest.user.resource.UserDBResource;

public class ProtocolAuthorsResource extends UserDBResource<DBProtocol> {

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	
	protected ReadAuthor getUserQuery(Object key) throws ResourceException {
		Object protocolKey = getRequest().getAttributes().get(FileResource.resourceKey);	
		if (protocolKey==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		int id[] = ReadProtocol.parseIdentifier(Reference.decode(protocolKey.toString()));
		DBProtocol protocol = new DBProtocol(id[0],id[1]);
		DBUser user = null;
		
		if (key!=null) {
			if (key.toString().startsWith("U")) {
				singleItem = true;
				user = new DBUser(new Integer(Reference.decode(key.toString().substring(1))));
			} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}	
		return new ReadAuthor(protocol, user);
	}
		
}
