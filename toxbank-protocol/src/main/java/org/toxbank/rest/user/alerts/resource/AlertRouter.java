package org.toxbank.rest.user.alerts.resource;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;

public class AlertRouter extends MyRouter {
	
	public AlertRouter(Context context) {
		super(context);
		attachDefault(AlertDBResource.class);
		attach(String.format("/{%s}",AlertDBResource.resourceKey), AlertDBResource.class);
	
	}
}
