package org.toxbank.rest.groups.resource;

import java.sql.Connection;

import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.toxbank.client.Resources;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.CallableGroupCreator;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.groups.db.ReadProject;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.resource.UserDBResource;

public class ProjectDBResource extends GroupDBResource<DBProject> {

	@Override
	public ReadGroup<DBProject> createGroupQuery(Integer key, String search, String groupName) {
		DBProject p = new DBProject();
		if (key!=null) p.setID(key);
		p.setTitle(search);
		p.setGroupName(groupName);
		ReadProject q = new ReadProject(p);
		return q;
	}
	@Override
	public String getGroupBackLink() {
		return  Resources.project;
	}
	@Override
	public String getGroupTitle() {
		return GroupType.PROJECT.toString();
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBProject item) throws ResourceException {
		DBUser user = null;
		Object userKey = getRequest().getAttributes().get(UserDBResource.resourceKey);		
		if ((userKey!=null) && userKey.toString().startsWith("U")) try {
			user = new DBUser(new Integer(Reference.decode(userKey.toString().substring(1))));
		} catch (Exception x) {}
		
		Connection conn = null;
		try {
			GroupQueryURIReporter r = new GroupQueryURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(getRequest());
			return new CallableGroupCreator(method,item,GroupType.PROJECT,user,r,form,getRequest().getRootRef().toString(), conn,getToken());
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	};

}
