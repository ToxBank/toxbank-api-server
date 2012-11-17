package org.toxbank.rest.protocol.projects.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;

/**
 * Removes an author of a given protocol. Does not delete the user itself.
 * @author nina
 *
 */
public class DeleteProjectMembership  extends AbstractUpdate<DBProtocol,DBProject> {
	protected static final String[] sql = new String[] {
		"DELETE from protocol_projects where idprotocol=? and version=? and idproject=?"
	};
	protected static final String[] sql_all = new String[] {
		"DELETE from protocol_projects where idprotocol=? and version=?"
	};
	public DeleteProjectMembership(DBProtocol protocol,DBProject project) {
		super(project);
		setGroup(protocol);
	}
	public DeleteProjectMembership() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();

		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getVersion()));
		if ((getObject()!=null) && (getObject().getID()>0))
			params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		//if (getObject()==null || getObject().getID()<=0) throw new InvalidUserException();
		if (getGroup()==null || getGroup().getID()<=0 || getGroup().getVersion()<=0) throw new InvalidProtocolException();
		return ((getObject()!=null) && (getObject().getID()>0))?sql:sql_all;
	}
	public void setID(int index, int id) {
			
	}
}