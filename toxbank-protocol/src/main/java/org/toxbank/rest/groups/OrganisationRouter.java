package org.toxbank.rest.groups;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;
import org.toxbank.rest.groups.resource.OrganisationDBResource;

public class OrganisationRouter extends MyRouter {
	public OrganisationRouter(Context context) {
		super(context);
		attachDefault(OrganisationDBResource.class);
		attach(String.format("/{%s}",OrganisationDBResource.resourceKey), OrganisationDBResource.class);
	
	}
}
