package org.toxbank.rest.protocol.projects.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import net.toxbank.client.resource.Project;

import org.toxbank.rest.db.exceptions.InvalidProjectException;
import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;

/**
 * 
 * @author nina
 *
 */
public class UpdateProjectMembership extends AbstractObjectUpdate<DBProtocol> {
	protected DeleteProjectMembership deleteProject;
	public UpdateProjectMembership(DBProtocol protocol) {
		super(protocol);
		deleteProject = new DeleteProjectMembership();
		deleteProject.setGroup(protocol);
		deleteProject.setObject(null);
	}
	public UpdateProjectMembership() {
		this(null);
	}		
	@Override
	public void setObject(DBProtocol object) {
		super.setObject(object);
		if (deleteProject!=null) deleteProject.setGroup(object);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (index==0) return deleteProject.getParameters(0);
		
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProtocolException();
		if (getObject().getProjects()==null || getObject().getProjects().size()==0) throw new InvalidProjectException("No projects!");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (Project project: getObject().getProjects()) {
			params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
			params.add(new QueryParam<Integer>(Integer.class, getObject().getVersion()));
			if (project instanceof DBProject) {
				if (((DBProject)project).getID()<=0)
					throw new InvalidProjectException(project.getResourceURL().toString());
				params.add(new QueryParam<Integer>(Integer.class, ((DBProject)project).getID()));
			} else throw new InvalidProjectException(project.getResourceURL().toString());
		}
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		//do not update projects if none submitted
		if (getObject().getProjects()==null || getObject().getProjects().size()==0)
			return new String[] {deleteProject.getSQL()[0]};
		
		StringBuilder b = new StringBuilder();
		b.append(AddProject.sql_addProject);
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProtocolException();
		if (getObject().getProjects()==null || (getObject().getProjects().size()==0)) throw new InvalidProjectException("No projects!");
		String d = "";
		for (Project project: getObject().getProjects()) {
			b.append(d);
			b.append("(?,?,?)");
			d = ",";
		}
		return new String[] {deleteProject.getSQL()[0], b.toString()};
	}
	public void setID(int index, int id) {
			
	}
}