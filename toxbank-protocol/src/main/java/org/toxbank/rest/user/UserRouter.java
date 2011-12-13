package org.toxbank.rest.user;

import net.idea.restnet.c.routers.MyRouter;
import net.toxbank.client.Resources;

import org.restlet.Context;
import org.toxbank.rest.protocol.ProtocolRouter;
import org.toxbank.rest.protocol.resource.db.ProtocolDBResource;
import org.toxbank.rest.user.resource.UserDBResource;

public class UserRouter extends MyRouter {
	public UserRouter(Context context,ProtocolRouter protocols) {
		super(context);
		attachDefault(UserDBResource.class);
		attach(String.format("/{%s}",UserDBResource.resourceKey), UserDBResource.class);
		attach(String.format("/{%s}%s",UserDBResource.resourceKey,Resources.protocol), ProtocolDBResource.class);
	
	}
}
