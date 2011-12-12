package org.toxbank.demo.task;

import net.idea.restnet.aa.resource.AdminResource;
import net.idea.restnet.c.html.HTMLBeauty;

import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBAdminResource extends AdminResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
