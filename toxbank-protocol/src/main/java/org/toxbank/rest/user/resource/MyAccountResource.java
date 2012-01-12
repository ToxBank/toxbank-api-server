package org.toxbank.rest.user.resource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.user.db.ReadUser;

public class MyAccountResource<T> extends UserDBResource<T> {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		editable = false;
		singleItem = true;
	}
	@Override
	protected ReadUser createQuery(Context context, Request request, Response response)
			throws ResourceException {
		String search_name = null;
		Object search_value = null;

		try {
			search_name = "username";
			search_value = getClientInfo().getUser().getIdentifier();
		} catch (Exception x) {
			search_value = null;
			x.printStackTrace();
		}				
		if (search_value == null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,"Not logged in!");
		Object key = request.getAttributes().get(UserDBResource.resourceKey);		
		try {
			return getUserQuery(key,search_name,search_value);
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
