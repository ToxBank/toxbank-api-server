package org.toxbank.rest.user;

import java.net.URL;
import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.groups.db.DeleteGroup;
import org.toxbank.rest.groups.db.UpdateGroup;
import org.toxbank.rest.user.db.CreateUser;
import org.toxbank.rest.user.db.DeleteUser;
import org.toxbank.rest.user.db.ReadUser;
import org.toxbank.rest.user.db.UpdateUser;
import org.toxbank.rest.user.resource.UserURIReporter;

public class CallableUserCreator extends CallableDBUpdateTask<DBUser,Form,String> {
	protected UserURIReporter<IQueryRetrieval<DBUser>> reporter;
	protected DBUser user;
	
	public CallableUserCreator(Method method,DBUser item,UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input, Connection connection,String token)  {
		super(method, input,connection,token);
		this.reporter = reporter;
		this.user = item;
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {
		if (input==null) return user;
		
		DBUser user = new DBUser();
		user.setUserName(input.getFirstValue(ReadUser.fields.username.name()));
		user.setFirstname(input.getFirstValue(ReadUser.fields.firstname.name()));
		user.setLastname(input.getFirstValue(ReadUser.fields.lastname.name()));
		user.setTitle(input.getFirstValue(ReadUser.fields.title.name()));
		try {user.setHomepage(new URL(input.getFirstValue(ReadUser.fields.homepage.name()))); } catch (Exception x) {}
		try {user.setWeblog(new URL(input.getFirstValue(ReadUser.fields.weblog.name())));} catch (Exception x) {} 
 		return user;
	}

	@Override
	protected IQueryUpdate<Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (Method.POST.equals(method)) return  new CreateUser(user);
		else if (Method.DELETE.equals(method)) return  new DeleteUser(user);
		else if (Method.PUT.equals(method)) return new  UpdateUser(user);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(DBUser user) throws Exception {
		return reporter.getURI(user);
	}

	

}
