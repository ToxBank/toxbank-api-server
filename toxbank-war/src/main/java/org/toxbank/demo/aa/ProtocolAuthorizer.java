package org.toxbank.demo.aa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.aa.opensso.OpenSSOAuthorizer;
import net.idea.restnet.db.DBConnection;
import net.toxbank.client.Resources;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.policy.Policy;
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
	protected ReadProtocolAccessLocal query;
	protected QueryExecutor<ReadProtocolAccessLocal> executor;
	@Override
	protected boolean authorize(OpenSSOToken ssoToken, Request request)
			throws Exception {
		Policy policy = null;
		
		//first check if local access is allowed , e.g. same name
		Template template1 = new Template(String.format("%s%s/{%s}",request.getRootRef(),Resources.protocol,FileResource.resourceKey));
		Template template2 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.authors));
		Template template3 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.protocol,FileResource.resourceKey,Resources.versions));
		Map<String, Object> vars = new HashMap<String, Object>();
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		template1.parse(ref.toString(),vars);
		template2.parse(ref.toString(),vars);
		template3.parse(ref.toString(),vars);

		/**
		 * Try if there is a protocol identifier, or this is a top level query, in the later case, try the OpenSSO AA
		 */
		if (vars.get(FileResource.resourceKey)!=null) { 
			
			
			try {
				int[] ids = ReadProtocol.parseIdentifier(vars.get(FileResource.resourceKey).toString());
				if (ids==null || ids.length!=2 || (ids[0]<=0) || (ids[1] <=0) )
					return super.authorize(ssoToken, request);

				try {retrieveUserAttributes(ssoToken, request);} catch (Exception x) { return super.authorize(ssoToken, request); }
				DBProtocol protocol = new DBProtocol(ids[0],ids[1]);
				policy = verify(protocol,request.getClientInfo().getUser().getIdentifier());
				
				/**
				 * The policy will let the user read the protocol, if he is an owner
				 * The policy will allow the user to update the protocol, if he is an owner and the protocol is not published
				 * Otherwise, we resort to OpenSSO policy
				 */
				if ((policy !=null) && policy.isAllow(request.getMethod().toString())) return true;
				
				
			} catch (ResourceException x) {
				return super.authorize(ssoToken, request);
			}
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
	

	public Policy verify(DBProtocol protocol, String username) throws Exception {
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
			Policy policy = null;
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
}
