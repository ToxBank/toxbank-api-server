package org.toxbank.rest.policy;


import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.toxbank.client.policy.AccessRights;
import net.toxbank.client.policy.GroupPolicyRule;
import net.toxbank.client.policy.PolicyRule;
import net.toxbank.client.policy.UserPolicyRule;
import net.toxbank.client.resource.Group;
import net.toxbank.client.resource.User;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.rest.RestException;

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
	
	public void sendPolicy(OpenSSOToken token,AccessRights accessRights) throws Exception {
		if ((accessRights==null) || (accessRights.getResource()==null) || (accessRights.getRules()==null)) throw new Exception("Invalid Policy");
		List<String> xmls = createPolicyXML(accessRights);
		for (String xml : xmls) try {
			int status = sendPolicy(token, xml);
			if (200!=status) 
				throw new RestException(status,String.format("Error when creating policy for URI %s",accessRights.getResource()));
		} catch (RestException x) {
			throw x;
		} catch (Exception x) {
			throw new Exception(String.format("Error when creating policy for URI %s",accessRights.getResource()),x);
		}

	}
	/**
	 * Remove previous policies and create a new one
	 * @param ssoToken
	 * @param accessRights
	 * @throws Exception
	 */
	public void updatePolicy(OpenSSOToken ssoToken,AccessRights accessRights) throws Exception {
		if ((accessRights==null) || (accessRights.getResource()==null) || (accessRights.getRules()==null)) throw new Exception("Policy");
		//First remove current policies
		deleteAllPolicies(ssoToken, accessRights.getResource());
		//then send the new policy
		sendPolicy(ssoToken,accessRights);
		
	}
	
	/**
	 * Deletes all policies for an URI
	 * @param url
	 * @throws Exception
	 */
	public void deleteAllPolicies(OpenSSOToken ssoToken,URL url) throws Exception {
		IOpenToxUser user = new OpenToxUser();
		
		Hashtable<String, String> policies = new Hashtable<String, String>();
		int status = getURIOwner(ssoToken, url.toExternalForm(), user, policies);
		if (200 == status) {
			Enumeration<String> e = policies.keys();
			while (e.hasMoreElements()) {
				String policyID = e.nextElement();
				deletePolicy(ssoToken,policyID);
			}
		} //else throw new RestException(status);
	}

}
