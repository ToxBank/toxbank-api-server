package org.toxbank.rest.policy;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.toxbank.client.policy.GroupPolicyRule;
import net.toxbank.client.policy.AccessRights;
import net.toxbank.client.policy.PolicyRule;
import net.toxbank.client.policy.UserPolicyRule;
import net.toxbank.client.resource.Group;
import net.toxbank.client.resource.User;

import org.opentox.aa.opensso.OpenSSOPolicy;

public class SimpleAccessRights extends OpenSSOPolicy {
	
	public SimpleAccessRights(String policyService) {
		super(policyService);
	}

	public String createGroupReadPolicyXML(Group group, URL uri) throws Exception {
		return createGroupPolicyXML(group.getGroupName(),  uri, new String[] {"GET"});
	}

	public String createUserReadPolicyXML(User user, URL uri) throws Exception {
		return createUserPolicyXML(user.getUserName(), uri, new String[] {"GET"});
	}
	
	public String createPolicyXML(UserPolicyRule<? extends User> policy, URL uri) throws Exception {
		return createUserPolicyXML(policy.getSubject().getUserName(), uri, policy.getActionsAsArray());
	}
	
	public String createPolicyXML(GroupPolicyRule<? extends Group> policy, URL uri) throws Exception {
		return createGroupPolicyXML(policy.getSubject().getGroupName(), uri, policy.getActionsAsArray());
	}
	
	public List<String> createPolicyXML(AccessRights policy) throws Exception {
		String xmlpolicy = null;
		if ((policy.getResource()!=null) && (policy.getRules()!=null)) {
			List<String> xmlpolicies = new ArrayList<String>();
			for (PolicyRule rule: policy.getRules()) {
				xmlpolicy = null;
				if (rule instanceof UserPolicyRule) {
					xmlpolicy = createPolicyXML((UserPolicyRule)rule,policy.getResource());
				} else if (rule instanceof GroupPolicyRule) {
					xmlpolicy = createPolicyXML((GroupPolicyRule)rule,policy.getResource());
				}
				if (xmlpolicy!=null) xmlpolicies.add(xmlpolicy);
			}
			return xmlpolicies;
		}
		return null;
		
	}

}
