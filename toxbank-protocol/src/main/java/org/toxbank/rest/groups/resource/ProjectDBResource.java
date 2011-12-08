package org.toxbank.rest.groups.resource;

import java.sql.Connection;

import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.Resources;
import org.toxbank.rest.groups.CallableGroupCreator;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.groups.db.ReadProject;

public class ProjectDBResource extends GroupDBResource<DBProject> {

	@Override
	public ReadGroup<DBProject> createGroupQuery(Integer key, String search) {
		DBProject p = new DBProject();
		if (key!=null) p.setID(key);
		p.setTitle(search);
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
		Connection conn = null;
		try {
			GroupQueryURIReporter r = new GroupQueryURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(getRequest());
			return new CallableGroupCreator(GroupType.PROJECT,r,form,conn,getToken());
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	};

}
