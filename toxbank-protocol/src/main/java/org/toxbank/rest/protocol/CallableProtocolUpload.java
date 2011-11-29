package org.toxbank.rest.protocol;

import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.ProcessorException;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.CreateUser;

public class CallableProtocolUpload extends CallableProtectedTask<String> {
	protected List<FileItem> input;
	protected ProtocolQueryURIReporter reporter;
	protected Connection connection;
	protected UpdateExecutor exec;
	protected String baseReference;
	
	public CallableProtocolUpload(List<FileItem> input,Connection connection,ProtocolQueryURIReporter r,String token,String baseReference) {
		super(token);
		this.connection = connection;
		this.input = input;
		this.reporter = r;
		this.baseReference = baseReference;
	}

	@Override
	public TaskResult doCall() throws Exception {
		try {
			DBProtocol protocol = ProtocolFactory.getProtocol(input, 10000000);
			exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			
			//user
			DBUser u = protocol.getOwner() instanceof DBUser?
						(DBUser)protocol.getOwner():
						new DBUser(protocol.getOwner());
		    protocol.setOwner(u);
		    if (u.getID()<=0) u.setID(u.parseURI(baseReference));
			if (u.getID()<=0) {
				CreateUser q1 = new CreateUser(u);
				exec.process(q1);
			}			
			//project
			DBProject p = protocol.getProject() instanceof DBProject?
						(DBProject)protocol.getProject():
						new DBProject(protocol.getProject());
		    protocol.setProject(p);
		    if (p.getID()<=0) p.setID(p.parseURI(baseReference));
			if (p.getID()<=0) {
				CreateGroup q1 = new CreateGroup(p);
				exec.process(q1);
			}
			//organisation
			DBOrganisation o = protocol.getOrganisation() instanceof DBOrganisation?
					(DBOrganisation)protocol.getOrganisation():
					new DBOrganisation(protocol.getOrganisation());
			protocol.setOrganisation(o);
		    if (o.getID()<=0) o.setID(o.parseURI(baseReference));
			if (o.getID()<=0) {
				CreateGroup q2 = new CreateGroup(o);
				exec.process(q2);
			}
			CreateProtocol q = new CreateProtocol(protocol);
			exec.process(q);
			
			return new TaskResult(reporter.getURI(protocol),true);
		} catch (ProcessorException x) {
			x.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		} finally {
			try {exec.close();} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}

	}

}

