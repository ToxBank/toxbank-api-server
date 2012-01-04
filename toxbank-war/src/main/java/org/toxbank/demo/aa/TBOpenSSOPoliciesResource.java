package org.toxbank.demo.aa;

import java.util.Iterator;

import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.aa.opensso.policy.Policy;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBOpenSSOPoliciesResource extends OpenSSOPoliciesResource {

	@Override
	protected Iterator<Policy> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return super.createQuery(context, request, response);
	}
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
