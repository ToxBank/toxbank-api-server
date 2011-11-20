package org.toxbank.rest.protocol;

import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.db.CreateProtocol;

public class CallableProtocolUpload extends CallableProtectedTask<String> {
	protected List<FileItem> input;
	protected ProtocolURIReporter reporter;
	protected Connection connection;
	protected UpdateExecutor exec;
	
	public CallableProtocolUpload(List<FileItem> input,Connection connection,ProtocolURIReporter r,String token) {
		super(token);
		this.connection = connection;
		this.input = input;
		this.reporter = r;

	}

	@Override
	public TaskResult doCall() throws Exception {
		try {
			IProtocol protocol = ProtocolFactory.getProtocol(input, 10000000);
			exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			CreateProtocol q = new CreateProtocol(protocol);
			exec.process(q);
			
			return new TaskResult(reporter.getURI(protocol),true);
		} catch (Exception x) {
			x.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		} finally {
			try {exec.close();} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}

	}

}

