package org.toxbank.demo.aa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.aa.opensso.OpenSSOAuthorizer;
import net.idea.restnet.db.DBConnection;
import net.toxbank.client.Resources;
import net.toxbank.client.policy.AccessRights;
import net.toxbank.client.policy.PolicyRule;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.ReadProtocolAccessLocal;


/**
 * 
 * Lets the user to read the protocol, if he is an owner.
 * Lets the user to update the protocol, if he is an owner and the protocol is not published.
 * Otherwise, we resort to the OpenSSO policy
 */
public class ProtocolAuthorizer  extends OpenSSOAuthorizer {
	protected int maxDepth = Integer.MAX_VALUE;
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	protected ReadProtocolAccessLocal query;
	protected QueryExecutor<ReadProtocolAccessLocal> executor;
	@Override
	protected boolean authorize(OpenSSOToken ssoToken, Request request)
			throws Exception {
		AccessRights policy = null;
		
		//first check if local access is allowed , e.g. same name
		Template template1 = new Template(String.format("%s%s/{%s}",request.getRootRef(),Resources.protocol,FileResource.resourceKey));
		Template template2 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.authors));
		Template template3 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.versions));
		Template template4 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.previous));
		Template template5 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.document));
		Template template6 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.datatemplate));
		Map<String, Object> vars = new HashMap<String, Object>();
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		template1.parse(ref.toString(),vars);
		template2.parse(ref.toString(),vars);
		template3.parse(ref.toString(),vars);
		template4.parse(ref.toString(),vars);
		template5.parse(ref.toString(),vars);
		template6.parse(ref.toString(),vars);

		/**
		 * Try if there is a protocol identifier, or this is a top level query, in the later case, try the OpenSSO AA
		 */
		if (vars.get(FileResource.resourceKey)!=null) { 
			
			String uri = String.format("%s%s/%s",request.getRootRef(),Resources.protocol,vars.get(FileResource.resourceKey));
			try {
				int[] ids = ReadProtocol.parseIdentifier(vars.get(FileResource.resourceKey).toString());
				if (ids==null || ids.length!=2 || (ids[0]<=0) || (ids[1] <=0) )
					return super.authorize(ssoToken, request);

				try {retrieveUserAttributes(ssoToken, request);} catch (Exception x) { return super.authorize(ssoToken, request); }
				DBProtocol protocol = new DBProtocol(ids[0],ids[1]);
				String username = request.getClientInfo().getUser().getIdentifier();
				policy = verify(protocol,username);
				
				
				/**
				 * The policy will let the user read the protocol, if he is an owner
				 * The policy will allow the user to update the protocol, if he is an owner and the protocol is not published
				 * Otherwise, we resort to OpenSSO policy
				 */
				if (policy !=null)
					for (PolicyRule rule : policy.getRules()) {
						Boolean allowed = rule.allows(request.getMethod().toString());
						if ((allowed!=null) && allowed) return true;
					}
				
				//not a top level
				//this is a hack; we need wild card policies for admin users and non-wild card for the rest!
				if ("protocol_service".equals(username)) {
					setPrefix("protocol");
					setMaxDepth(Integer.MAX_VALUE);
				} else
					setMaxDepth(1);
			} catch (ResourceException x) {
				return super.authorize(ssoToken, request);
			}
		} else {
			setPrefix(null);
		}
		/**
		 *  otherwise try if there is an OpenSSO policy to let me in
		 */
		if (super.authorize(ssoToken, request)) {
			//parent method only retrieves user name for non-GET 
			if (request.getClientInfo().getUser().getIdentifier()==null) { 
				try {retrieveUserAttributes(ssoToken, request);} 
				catch (Exception x) {}
			}
			return true;
		} else return false;
	}
	@Override
	protected boolean isEnabled() {
		return true;
	}
	

	public AccessRights verify(DBProtocol protocol, String username) throws Exception {
		//TODO make use of same connection for performance reasons
		Connection c = null;
		ResultSet rs = null;
		try {
			if (query==null) query = new ReadProtocolAccessLocal();
			query.setFieldname(protocol);
			query.setValue(username);
			DBConnection dbc = new DBConnection(getApplication().getContext(),"conf/tbprotocol-db.pref");
			c = dbc.getConnection();
			if (executor==null)  executor = new QueryExecutor<ReadProtocolAccessLocal>();
			executor.setConnection(c);
			rs = executor.process(query);
			AccessRights policy = null;
			while (rs.next()) {
				policy = query.getObject(rs);
				break;
			}
			return policy;
		} catch (Exception x) {
			throw x;
		} finally {
			try {executor.close();} catch (Exception x) {};
			try {if (rs!=null) rs.close();} catch (Exception x) {};
			try {if (c!=null) c.close();} catch (Exception x) {};
		}
		
	}
	
	@Override
	public String uri2check(Reference root,Reference ref) throws Exception {
		if (prefix==null) return ref==null?null:ref.toString();
	    if (ref == null) return null;
	    
	    String u = root.toString();
		Reference fullPrefix = new Reference(String.format("%s%s%s/", 
					u,
					u.lastIndexOf("/")==u.length()-1?"":"/",
					prefix));
		
		u = ref.toString();
		Reference uri = new Reference(String.format("%s%s", 
				u,
				u.lastIndexOf("/")==u.length()-1?"":"/"
				));
		u = ref.toString();
		Reference uri2check = new Reference(u==null?null:
										u.lastIndexOf("/")==u.length()-1?u:String.format("%s/",u)); //add trailing slash
		int prefix_len = fullPrefix.toString().length();
		int level = 0;
		while (!fullPrefix.equals(uri)) {
			uri2check = uri;
			if (level>=maxDepth) break;
			
			uri = uri.getParentRef();
			if (uri.toString().length()<prefix_len) return null; //smth wrong
			level++;

		}
		u = uri.toString();
		if (u.lastIndexOf("/")==(u.length()-1))
			return u.substring(0,u.length()-1);
		else return u;
	}
}
