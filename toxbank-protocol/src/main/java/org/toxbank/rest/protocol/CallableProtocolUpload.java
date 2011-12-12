package org.toxbank.rest.protocol;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.ProcessorException;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;
import net.toxbank.client.Resources;
import net.toxbank.client.resource.User;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.db.CreateProtocolVersion;
import org.toxbank.rest.protocol.db.UpdateKeywords;
import org.toxbank.rest.protocol.db.template.UpdateDataTemplate;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.AddAuthors;
import org.toxbank.rest.user.db.CreateUser;

public class CallableProtocolUpload extends CallableProtectedTask<String> {
	protected List<FileItem> input;
	protected ProtocolQueryURIReporter reporter;
	protected Connection connection;
	protected UpdateExecutor exec;
	protected String baseReference;
	protected DBUser user;
	protected File dir;
	protected DBProtocol protocol;
	protected boolean setDataTemplateOnly = false;
	public boolean isSetDataTemplateOnly() {
		return setDataTemplateOnly;
	}

	public void setSetDataTemplateOnly(boolean setDataTemplateOnly) {
		this.setDataTemplateOnly = setDataTemplateOnly;
	}

	/**
	 * 
	 * @param protocol  NULL if a new protocol, otherwise the protocol which version to be created
	 * @param user
	 * @param input
	 * @param connection
	 * @param r
	 * @param token
	 * @param baseReference
	 * @param dir
	 */
	public CallableProtocolUpload(DBProtocol protocol,DBUser user,List<FileItem> input,
					Connection connection,
					ProtocolQueryURIReporter r,
					String token,
					String baseReference,
					File dir) {
		super(token);
		this.protocol = protocol;
		this.connection = connection;
		this.input = input;
		this.reporter = r;
		this.baseReference = baseReference;
		this.user = user;
		this.dir = dir;
	}

	@Override
	public TaskResult doCall() throws Exception {
		boolean existing = protocol!=null&&protocol.getID()>0;
		try {
			protocol = ProtocolFactory.getProtocol(protocol,input, 10000000,dir);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
		//now write
		
		if (setDataTemplateOnly) //data template only
			try {
				if ((protocol.getDataTemplate()!=null) && protocol.getDataTemplate().getResourceURL().toString().startsWith("file:")) {
					connection.setAutoCommit(false);
					//protocol.setOwner(user);
					exec = new UpdateExecutor<IQueryUpdate>();
					exec.setConnection(connection);					
					UpdateDataTemplate k = new UpdateDataTemplate(protocol);
					exec.process(k);
					connection.commit();
					String uri = String.format("%s%s",reporter.getURI(protocol),Resources.datatemplate);
					return new TaskResult(uri,false);
				} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Data template");
			} catch (ProcessorException x) {
				try {connection.rollback();} catch (Exception xx) {}
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			} catch (Exception x) {
				try {connection.rollback();} catch (Exception xx) {}
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} finally {
				try {exec.close();} catch (Exception x) {}
				try {connection.setAutoCommit(true);} catch (Exception x) {}
				try {connection.close();} catch (Exception x) {}
			}
		else //everything else 
		try {
			connection.setAutoCommit(false);
			//protocol.setOwner(user);
			exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			
			CreateUser quser = new CreateUser(null);
			//user
			DBUser user = protocol.getOwner() instanceof DBUser?
						(DBUser)protocol.getOwner():
						new DBUser(protocol.getOwner());
		    protocol.setOwner(user);
		    if (user.getID()<=0) user.setID(user.parseURI(baseReference));
			if (user.getID()<=0) {
				quser.setObject(user);
				exec.process(quser);
			}	
			
			for (User u: protocol.getAuthors()) { 
				DBUser author =u instanceof DBUser?(DBUser)u:new DBUser(u);
 			    if (author.getID()<=0) author.setID(author.parseURI(baseReference));
					if (author.getID()<=0) {
						quser.setObject(author);
						exec.process(quser);
					}	
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
			
			if (existing) {
				CreateProtocolVersion q = new CreateProtocolVersion(protocol);
				exec.process(q);
			} else {
				CreateProtocol q = new CreateProtocol(protocol);
				exec.process(q);
			}
			
			String uri = reporter.getURI(protocol);
			
			if (protocol.getKeywords().size()>0) {
				UpdateKeywords k = new UpdateKeywords(protocol);
				exec.process(k);
			}
			
			if ((protocol.getAuthors()!=null) && protocol.getAuthors().size()>0) {
				AddAuthors k = new AddAuthors(protocol);
				exec.process(k);
			}
			
			if ((protocol.getDataTemplate()!=null) && 
					(protocol.getDataTemplate().getResourceURL()!=null) &&
					 protocol.getDataTemplate().getResourceURL().toString().startsWith("file:")) {
				UpdateDataTemplate k = new UpdateDataTemplate(protocol);
				exec.process(k);
			}	
			
			connection.commit();
			return new TaskResult(uri,true);
		} catch (ProcessorException x) {
			try {connection.rollback();} catch (Exception xx) {}
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} catch (Exception x) {
			try {connection.rollback();} catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		} finally {
			try {exec.close();} catch (Exception x) {}
			try {connection.setAutoCommit(true);} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}

	}

}

