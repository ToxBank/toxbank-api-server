package org.toxbank.demo.aa;

import org.toxbank.rest.protocol.TBHTMLBeauty;

import net.idea.restnet.aa.opensso.users.OpenSSOUserResource;
import net.idea.restnet.c.html.HTMLBeauty;

public class TBLoginResource extends OpenSSOUserResource {
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
