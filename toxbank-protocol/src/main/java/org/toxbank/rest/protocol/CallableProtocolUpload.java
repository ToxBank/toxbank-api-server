package org.toxbank.rest.protocol;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.ProcessorException;
import net.idea.modbcum.p.QueryExecutor;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;
import net.toxbank.client.Resources;
import net.toxbank.client.resource.User;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.db.CreateGroup;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.groups.db.ReadOrganisation;
import org.toxbank.rest.groups.db.ReadProject;
import org.toxbank.rest.policy.SimpleAccessRights;
import org.toxbank.rest.protocol.db.CreateProtocol;
import org.toxbank.rest.protocol.db.CreateProtocolVersion;
import org.toxbank.rest.protocol.db.DeleteProtocol;
import org.toxbank.rest.protocol.db.UpdateKeywords;
import org.toxbank.rest.protocol.db.UpdateProtocol;
import org.toxbank.rest.protocol.db.template.UpdateDataTemplate;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.AddAuthors;
import org.toxbank.rest.user.db.CreateUser;
import org.toxbank.rest.user.db.ReadUser;


public class CallableProtocolUpload extends CallableProtectedTask<String> {
	public enum UpdateMode {create,update,dataTemplateOnly,createversion}
	protected List<FileItem> input;
	protected ProtocolQueryURIReporter reporter;
	protected Connection connection;
	protected UpdateExecutor exec;
	protected String baseReference;
	protected DBUser user;
	protected File dir;
	protected DBProtocol protocol;
	protected Method method;
	protected UpdateMode updateMode = UpdateMode.create;
	
	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(UpdateMode updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isSetDataTemplateOnly() {
		return UpdateMode.dataTemplateOnly.equals(updateMode);
	}

	public void setSetDataTemplateOnly(boolean setDataTemplateOnly) {
		this.updateMode = UpdateMode.dataTemplateOnly;
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
	public CallableProtocolUpload(Method method,DBProtocol protocol,DBUser user,List<FileItem> input,
					Connection connection,
					ProtocolQueryURIReporter r,
					String token,
					String baseReference,
					File dir) {
		super(token);
		this.method = method;
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
		if (Method.POST.equals(method)) return create();
		else if (Method.PUT.equals(method)) return update();
		else if (Method.DELETE.equals(method)) return delete();
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED,method.toString());
	}	
	
	public TaskResult delete() throws Exception {
		try {
			connection.setAutoCommit(false);
			//protocol.setOwner(user);
			exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			if (isSetDataTemplateOnly()) {
				//DeleteProtocol k = new DeleteProtocol(protocol);
				//exec.process(k);				
			} else {
				DeleteProtocol k = new DeleteProtocol(protocol);
				exec.process(k);
				connection.commit();
			}
			return new TaskResult(null,false);
			
		} catch (Exception x) {
			try {connection.rollback();} catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		} finally {
			try {exec.close();} catch (Exception x) {}
			try {connection.setAutoCommit(true);} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}
	}

	public TaskResult create() throws Exception {
		boolean existing = protocol!=null&&protocol.getID()>0;
		try {
			protocol = ProtocolFactory.getProtocol(protocol,input, 10000000,dir);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
		//now write
		switch (updateMode) {
		case dataTemplateOnly:  {
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
//			break;
		}
		default: {
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
				TaskResult result = new TaskResult(uri,true);
				try {
					retrieveAccountNames(connection);
					result.setPolicy(generatePolicy(uri,protocol));
				} 
				catch (Exception x) { result.setPolicy(null);}
			
			
				return result;
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
		} //switch

	}

	public TaskResult update() throws Exception {
		if ((protocol==null)||(protocol.getID()<=0)) throw new Exception("Can't update: Not an existing protocol!");

		try {
			//get only fields from the web form
			DBProtocol newProtocol = ProtocolFactory.getProtocol(null,input, 10000000,dir);
			newProtocol.setID(protocol.getID());
			newProtocol.setVersion(protocol.getVersion());
			newProtocol.setIdentifier(null);
			if (newProtocol.getProject() != null) {
				DBProject p = (DBProject) newProtocol.getProject();
				p.setID(p.parseURI(baseReference));
			}
			if (newProtocol.getOrganisation() != null) {
				DBOrganisation p = (DBOrganisation) newProtocol.getOrganisation();
				p.setID(p.parseURI(baseReference));
			}		
			if (newProtocol.getOwner() != null) {
				DBUser p = (DBUser) newProtocol.getOwner();
				p.setID(p.parseURI(baseReference));
			}					
			if (newProtocol.getAuthors()!=null)
				for (User u: newProtocol.getAuthors()) { 
					DBUser author =u instanceof DBUser?(DBUser)u:new DBUser(u);
	 			    if (author.getID()<=0) author.setID(author.parseURI(baseReference));
				}
			
			protocol = newProtocol;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
		
			try {
				connection.setAutoCommit(false);
				//protocol.setOwner(user);
				exec = new UpdateExecutor<IQueryUpdate>();
				exec.setConnection(connection);
				
				UpdateProtocol q = new UpdateProtocol(protocol);
				exec.process(q);
				
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
				TaskResult result = new TaskResult(uri,false);
				try {
					retrieveAccountNames(connection);
					result.setPolicy(generatePolicy(uri,protocol));
				} 
				catch (Exception x) { result.setPolicy(null);}
			
			
				return result;
			} catch (ProcessorException x) {
				x.printStackTrace();
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
	
	protected void retrieveAccountNames(Connection connection) throws Exception {
		QueryExecutor qexec = new QueryExecutor();
		try {
			
			qexec.setConnection(connection);
			ReadUser getUser = new ReadUser();
			for (DBUser u: protocol.allowReadByUser) { 
				    if (u.getID()<=0) u.setID(u.parseURI(baseReference));
				if (u.getUserName()==null) {
					getUser.setValue(u);
					ResultSet rs = null;
					try { 
						rs = qexec.process(getUser); 
						while (rs.next()) { u.setUserName(getUser.getObject(rs).getUserName()); }
					} catch (Exception x) { if (rs!=null) rs.close(); }
				}	
			}
			ReadGroup getGroup = null;
			ReadOrganisation readOrg = new ReadOrganisation(null);
			ReadProject readProject = new ReadProject(null);
			for (IDBGroup u: protocol.allowReadByGroup) { 
				    if (u.getID()<=0) u.setID(u.parseURI(baseReference));
				if (u.getGroupName()==null) {
					getGroup = u instanceof DBOrganisation?readOrg:readProject;
					getGroup.setValue(u);
					ResultSet rs = null;
					try { 
						rs = qexec.process(getGroup); 
						while (rs.next()) { u.setGroupName(getGroup.getObject(rs).getGroupName()); }
					} catch (Exception x) { if (rs!=null) rs.close(); }
				}
			}
		}
		finally { try {qexec.close(); } catch (Exception x) {}}			
	}
	protected List<String> generatePolicy(String uri,DBProtocol protocol) throws Exception {
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		SimpleAccessRights policyTools = new SimpleAccessRights(config.getPolicyService());
		List<String> policies = new ArrayList<String>();
		if (protocol.allowReadByGroup!=null)
			for (IDBGroup group : protocol.allowReadByGroup) { 
				if (group.getGroupName()==null) continue;
				String policy = policyTools.createGroupReadPolicyXML(group, uri);
				if (policy!=null) policies.add(policy);
			}
		if (protocol.allowReadByUser!=null)
			for (DBUser user : protocol.allowReadByUser) { 
				if (user.getUserName()==null) continue;
				String policy = policyTools.createUserReadPolicyXML(user, uri);
				if (policy!=null) policies.add(policy);
			}		
		return policies.size()>0?policies:null;
	}
}

