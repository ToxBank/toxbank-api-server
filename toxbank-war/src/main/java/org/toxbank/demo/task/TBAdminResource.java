package org.toxbank.demo.task;

import org.toxbank.rest.protocol.TBHTMLBeauty;

import net.idea.restnet.aa.resource.AdminResource;
import net.idea.restnet.c.html.HTMLBeauty;

public class TBAdminResource extends AdminResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
