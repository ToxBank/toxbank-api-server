package org.toxbank.demo.task;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;

public class TBTaskRouter extends MyRouter {
	public TBTaskRouter(Context context) {
		super(context);
		attachDefault(TBTaskResource.class);
		attach(TBTaskResource.resourceID, TBTaskResource.class);
	}
}
