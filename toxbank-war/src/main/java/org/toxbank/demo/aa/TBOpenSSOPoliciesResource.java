package org.toxbank.demo.aa;

import org.toxbank.rest.protocol.TBHTMLBeauty;

import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.c.html.HTMLBeauty;

public class TBOpenSSOPoliciesResource extends OpenSSOPoliciesResource {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
