package org.toxbank.rest.user.alerts.notification;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.c.task.TaskCreatorForm;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.Task;
import net.toxbank.client.resource.Alert.RecurrenceFrequency;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.alerts.db.DBAlert;
import org.toxbank.rest.user.alerts.db.ReadAlert;
import org.toxbank.rest.user.db.ReadUser;
import org.toxbank.rest.user.db.ReadUsersByAlerts;
import org.toxbank.rest.user.resource.UserDBResource;
import org.toxbank.rest.user.resource.UserURIReporter;


public class NotificationResource<T> extends UserDBResource<T> {
	protected Form params = null;
	public static final String resourceKey = "/notification";
	protected Set<RecurrenceFrequency> frequency;
	public NotificationResource() {
		super();

	}
	@Override
	protected ReadUser createQuery(Context context, Request request, Response response)
			throws ResourceException {
		//Object key = request.getAttributes().get(UserDBResource.resourceKey);
		editable = false;
		singleItem = false;
		Form form = getParams();
		if (form==null) return null; //on post
		String[] search = null;
		try {
			search = form.getValuesArray("search");
		} catch (Exception x) {
			search = null;
		}		

		try {
			frequency = new HashSet<RecurrenceFrequency>();
			if (search==null) frequency.add(RecurrenceFrequency.weekly);
			else for (String freq : search) try {
				frequency.add(RecurrenceFrequency.valueOf(freq));
			} catch (Exception x) {};
			if (frequency.isEmpty() ) frequency.add(RecurrenceFrequency.weekly);
			return new ReadUsersByAlerts(frequency);
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					x.getMessage(),
					x
					);
		}
	} 

	@Override
	protected QueryURIReporter<DBUser, ReadUser<T>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new UserURIReporter(getRequest());
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBUser item) throws ResourceException {
		Connection conn = null;
		try {
			UserURIReporter reporter = new UserURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(getRequest());
			CallableNotification callable = new CallableNotification(method,item,reporter, form,getRequest().getRootRef().toString(),conn,getToken());
			callable.setNotification(new SimpleNotificationEngine());
			return callable;
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	
	}

	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;		
	}
	
	@Override
	protected ReadUser<T> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {

		if (Method.POST.equals(method)) 
			 return createQuery(context, request, response);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	
	@Override
	protected TaskCreator getTaskCreator(Form form, final Method method,
			boolean async, final Reference reference) throws Exception {
		TaskCreatorForm<Object,DBUser> reporter = new TaskCreatorForm<Object,DBUser>(form,async) {
			@Override
			protected ICallableTask getCallable(Form form,
					DBUser item) throws ResourceException {
				return createCallable(method,(Form)null,item);
			}
			@Override
			protected Task<Reference, Object> createTask(
					ICallableTask callable,
					DBUser item) throws ResourceException {
					return addTask(callable, item,reference);
				}
			
			@Override
			public List<UUID> process(IQueryRetrieval<DBUser> query)
					throws AmbitException {

				return super.process(query);
			}
			@Override
			public Object processItem(DBUser item) throws AmbitException {

				return super.processItem(item);
			}
		};
				
		ReadAlert queryP = new ReadAlert(null); 
		queryP.setFrequency(frequency);
		MasterDetailsProcessor<DBUser,DBAlert,IQueryCondition> alertsReader = new MasterDetailsProcessor<DBUser,DBAlert,IQueryCondition>(queryP) {
			@Override
			protected DBUser processDetail(DBUser target, DBAlert detail)
					throws Exception {
				//m/b move list of alerts into Alert bean 
				if (target.getAlerts()==null) target.setAlerts(new ArrayList<DBAlert>());
				target.getAlerts().add(detail);
				//detail.setResourceURL(new URL(userReporter.getURI(detail)));
				return target;
			}
		};
		reporter.getProcessors().add(0,alertsReader);
		return reporter;
	}
	
	@Override
	protected Task<Reference, Object> addTask(ICallableTask callable,
			DBUser item, Reference reference) throws ResourceException {

			if (item.getAlerts()==null) return null;
			
			return ((TaskApplication)getApplication()).addTask(
				String.format("Send notification [%d alert(s)] %s %s ",
						item.getAlerts().size(),
						item==null?"":"to",
						item==null?"":String.format("%s %s",item.getFirstname(),item.getLastname())),									
				callable,
				getRequest().getRootRef(),
				getToken());		
		
	}
	@Override
	protected Form getParams() {
		if (params == null) {
			if (Method.GET.equals(getRequest().getMethod())) 
					params = getRequest().getResourceRef().getQueryAsForm();
		}
		return params;
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		params = new Form(entity);
		synchronized (this) {
			return processAndGenerateTask(Method.POST, null, variant,true);
		}
	}
		
	
}