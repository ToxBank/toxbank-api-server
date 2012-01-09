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
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.routing.Template;
import org.toxbank.rest.user.author.db.VerifyUser;
import org.toxbank.rest.user.resource.UserDBResource;

public class UserAuthorizer extends OpenSSOAuthorizer {
	protected VerifyUser query;
	protected QueryExecutor<VerifyUser> executor;
	@Override
	protected boolean authorize(OpenSSOToken ssoToken, Request request)
			throws Exception {
		if (super.authorize(ssoToken, request)) {
			//parent method only retrieves user name for non-GET
			if (Method.GET.equals(request.getMethod())){
				try {retrieveUserAttributes(ssoToken, request);} 
				catch (Exception x) {}
			}
			return true;
		}
		Template template1 = new Template(String.format("%s%s/{%s}",request.getRootRef(),Resources.user,UserDBResource.resourceKey));
		Template template2 = new Template(String.format("%s%s/{%s}%s",request.getRootRef(),Resources.user,UserDBResource.resourceKey,Resources.protocol));
		Map<String, Object> vars = new HashMap<String, Object>();
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		template1.parse(ref.toString(),vars);
		template2.parse(ref.toString(),vars);
		if (vars.get(UserDBResource.resourceKey)==null) return super.authorize(ssoToken, request);
		Integer iduser  = null;
		try {iduser = Integer.parseInt(vars.get(UserDBResource.resourceKey).toString().substring(1));
		} catch (Exception x) { return super.authorize(ssoToken, request); }
		try {retrieveUserAttributes(ssoToken, request);} catch (Exception x) { x.printStackTrace();}
		return verify(iduser,request.getClientInfo().getUser().getIdentifier());
	}
	@Override
	protected boolean isEnabled() {
		return true;
	}
	

	public boolean verify(Integer iduser, String identifier) throws Exception {
		//TODO make use of same connection for performance reasons
		Connection c = null;
		ResultSet rs = null;
		try {
			if (query==null) query = new VerifyUser();
			query.setFieldname(iduser);
			query.setValue(identifier);
			DBConnection dbc = new DBConnection(getApplication().getContext(),"conf/tbprotocol-db.pref");
			c = dbc.getConnection();
			if (executor==null)  executor = new QueryExecutor<VerifyUser>();
			executor.setConnection(c);
			rs = executor.process(query);
			boolean ok = false;
			while (rs.next()) {
				ok = query.getObject(rs);
				break;
			}
			return ok;
		} catch (Exception x) {
			throw x;
		} finally {
			try {executor.close();} catch (Exception x) {};
			try {if (rs!=null) rs.close();} catch (Exception x) {};
			try {if (c!=null) c.close();} catch (Exception x) {};
		}
	}
}
