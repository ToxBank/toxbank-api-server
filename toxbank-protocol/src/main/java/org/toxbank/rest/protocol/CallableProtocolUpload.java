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
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;

public class CallableProtocolUpload extends CallableProtectedTask<String> {
	protected List<FileItem> input;
	protected ProtocolQueryURIReporter reporter;
	protected Connection connection;
	protected UpdateExecutor exec;
	
	public CallableProtocolUpload(List<FileItem> input,Connection connection,ProtocolQueryURIReporter r,String token) {
		super(token);
		this.connection = connection;
		this.input = input;
		this.reporter = r;

	}

	@Override
	public TaskResult doCall() throws Exception {
		try {
			DBProtocol protocol = ProtocolFactory.getProtocol(input, 10000000);
			exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			
			if (protocol.getDbProject().getID()<=0) {
				CreateGroup q1 = new CreateGroup(protocol.getDbProject());
				exec.process(q1);
			}
			if (protocol.getDbOrganisation().getID()<=0) {
				CreateGroup q2 = new CreateGroup(protocol.getDbOrganisation());
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

