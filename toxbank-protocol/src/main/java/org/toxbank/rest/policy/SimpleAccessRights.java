package org.toxbank.rest.policy;

import java.util.UUID;

import net.toxbank.client.resource.User;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.toxbank.rest.groups.IDBGroup;

public class SimpleAccessRights extends OpenSSOPolicy {
	
	public SimpleAccessRights(String policyService) {
		super(policyService);
	}

	public String createGroupPolicyXML(IDBGroup group, String uri, String[] methods) throws Exception {

		StringBuffer actions = new StringBuffer();
		for (String method: methods) {
			actions.append(String.format(policyActionTemplate,method));
		}
		return String.format(policyGroupTemplate,UUID.randomUUID(),uri,actions,group.getGroupName(),group.getGroupName());
	}
	public String createGroupReadPolicyXML(IDBGroup group, String uri) throws Exception {
		return createGroupPolicyXML(group,  uri, new String[] {"GET"});
	}

	public String createUserPolicyXML(User user, String uri, String[] methods) throws Exception {
		
		StringBuffer actions = new StringBuffer();
		for (String method: methods) {
			actions.append(String.format(policyActionTemplate,method));
		}
		return String.format(policyUserTemplate,UUID.randomUUID(),uri,actions,user.getUserName(),user.getUserName());
	}
	public String createUserReadPolicyXML(User user, String uri) throws Exception {
		return createUserPolicyXML(user, uri, new String[] {"GET"});
	}

}
