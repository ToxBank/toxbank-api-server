package org.toxbank.rest.user;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;
import org.toxbank.rest.user.resource.UserDBResource;

public class UserRouter extends MyRouter {
	public UserRouter(Context context) {
		super(context);
		attachDefault(UserDBResource.class);
		attach(String.format("/{%s}",UserDBResource.resourceKey), UserDBResource.class);
	
	}
}
