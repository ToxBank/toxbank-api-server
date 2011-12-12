package org.toxbank.demo.aa;

import net.idea.restnet.aa.resource.PolicyResource;
import net.idea.restnet.c.html.HTMLBeauty;

import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBPolicyResource extends PolicyResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
		
	}
}
