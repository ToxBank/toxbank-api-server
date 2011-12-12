package org.toxbank.rest.groups;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;
import org.toxbank.rest.groups.resource.ProjectDBResource;

public class ProjectRouter extends MyRouter {
	
	public ProjectRouter(Context context) {
		super(context);
		attachDefault(ProjectDBResource.class);
		attach(String.format("/{%s}",ProjectDBResource.resourceKey), ProjectDBResource.class);
	
	}
}
