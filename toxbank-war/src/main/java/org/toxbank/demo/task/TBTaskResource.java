package org.toxbank.demo.task;

import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.resource.TaskResource;

import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBTaskResource extends TaskResource<String> {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
