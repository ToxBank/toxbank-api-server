package org.toxbank.demo.aa;

import org.toxbank.rest.protocol.TBHTMLBeauty;

import net.idea.restnet.aa.resource.PolicyResource;
import net.idea.restnet.c.html.HTMLBeauty;

public class TBPolicyResource extends PolicyResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
		
	}
}
