package org.toxbank.rest.groups;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;

public class CallableGroupCreator extends CallableDBUpdateTask<IDBGroup,Form,String> {
	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> reporter;
	protected GroupType type;
	public CallableGroupCreator(
						GroupType type,
						GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> reporter,
						Form input, Connection connection,String token)  {
		super(input,connection,token);
		this.reporter = reporter;
		this.type = type;
	}

	@Override
	protected DBGroup getTarget(Form input) throws Exception {
		DBGroup user = new DBGroup(type);
		user.setTitle(input.getFirstValue(DBGroup.fields.name.name()));
		user.setGroupName(input.getFirstValue(DBGroup.fields.ldapgroup.name()));
 		return user;
	}

	@Override
	protected IQueryUpdate<Object, IDBGroup> createUpdate(IDBGroup group)
			throws Exception {
		return new CreateGroup(group);
	}

	@Override
	protected String getURI(IDBGroup user) throws Exception {
		return reporter.getURI(user);
	}

	

}
