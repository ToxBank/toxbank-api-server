package org.toxbank.rest.user.alerts.notification;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.alerts.db.DBAlert;
import org.toxbank.rest.user.resource.UserURIReporter;

public class CallableNotification extends CallableDBUpdateTask<DBUser,Form,String> {
	protected UserURIReporter<IQueryRetrieval<DBUser>> reporter;
	protected DBUser user;
	
	public CallableNotification(Method method,DBUser item,UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input,
						String baseReference,
						Connection connection,String token)  {
		super(method, input,connection,token);
		this.reporter = reporter;
		this.user = item;
		this.baseReference = baseReference;
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {
		for (DBAlert alert:user.getAlerts()) 
			System.out.println(alert);
 		return user;
	}

	@Override
	protected IQueryUpdate<Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (Method.POST.equals(method)) return null;// new UpdateAlertSentTimeStamp(user);
		
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(DBUser user) throws Exception {
		return reporter.getURI(user);
	}

	@Override
	protected Object executeQuery(IQueryUpdate<Object, DBUser> query)
			throws Exception {
		/*
		Object result = super.executeQuery(query);
		if (Method.POST.equals(method)) {
			DBUser user = query.getObject();
			if ((user.getOrganisations()!=null) && (user.getOrganisations().size()>0)) {
				AddGroupsPerUser q = new AddGroupsPerUser(user,user.getOrganisations());
				exec.process(q);
			}
			if ((user.getProjects()!=null) && (user.getProjects().size()>0)) {
				AddGroupsPerUser q = new AddGroupsPerUser(user,user.getProjects());
				exec.process(q);
			}			
		}
		*/
		return null;
	}

	@Override
	protected boolean isNewResource() {
		return false;
	}
}
