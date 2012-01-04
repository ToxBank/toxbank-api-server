package org.toxbank.demo.aa;

import net.idea.restnet.aa.opensso.policy.OpenSSOPolicyResource;
import net.idea.restnet.c.html.HTMLBeauty;

import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBPolicyResource extends OpenSSOPolicyResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
		
	}
}
