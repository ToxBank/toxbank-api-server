package org.toxbank.demo.task;

import net.idea.restnet.aa.opensso.policy.OpenSSOPolicyResource;
import net.idea.restnet.aa.resource.AdminRouter;

import org.restlet.Context;
import org.toxbank.demo.aa.TBOpenSSOPoliciesResource;
import org.toxbank.demo.aa.TBPolicyResource;
import org.toxbank.rest.db.DatabaseResource;

public class TBAdminRouter extends AdminRouter  {

	public TBAdminRouter(Context context) {
		super(context);
	}
	@Override
	protected void init() {
		attachDefault(TBAdminResource.class);
		/**
		 * Policy creation
		 */
		attach(String.format("/%s",TBOpenSSOPoliciesResource.resource),TBOpenSSOPoliciesResource.class);
		attach(String.format("/%s/{%s}",TBOpenSSOPoliciesResource.resource,OpenSSOPolicyResource.policyKey),TBPolicyResource.class);
		attach(String.format("/%s",DatabaseResource.resource),DatabaseResource.class);

	}
}