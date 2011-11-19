package org.toxbank.rest.protocol;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;
import org.toxbank.resource.ITemplate;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.resource.db.ProtocolDBResource;
import org.toxbank.rest.protocol.resource.db.ProtocolDocumentResource;
import org.toxbank.rest.protocol.resource.db.template.DataTemplateResource;

public class ProtocolRouter extends MyRouter {
	public ProtocolRouter(Context context) {
		super(context);
		attachDefault(ProtocolDBResource.class);
		attach(String.format("/{%s}",FileResource.resourceKey), ProtocolDBResource.class);
		attach(String.format("/{%s}/file",FileResource.resourceKey), ProtocolDocumentResource.class);
		attach(String.format("/{%s}%s",FileResource.resourceKey,ITemplate.resource), DataTemplateResource.class);

		
	}
}
