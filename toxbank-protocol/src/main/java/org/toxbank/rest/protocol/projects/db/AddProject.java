package org.toxbank.rest.protocol.projects.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.db.exceptions.InvalidProjectException;
import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;

public class AddProject  extends AbstractUpdate<DBProtocol,DBProject> {
	public static final String sql_addProject = "insert ignore into protocol_projects (idprotocol,version,idproject) values ";
	protected static final String[] sql = new String[] {
		String.format("%s (?,?,?)",sql_addProject)
		};
	
	public AddProject(DBProtocol protocol,DBProject project) {
		super(project);
		setGroup(protocol);
	}
	public AddProject() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProjectException();
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getVersion()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidProjectException();
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();
		return sql;
	}
	public void setID(int index, int id) {
			
	}
}