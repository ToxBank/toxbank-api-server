package org.toxbank.rest.protocol.projects.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.ReadProject;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;

public class ReadProjectMembership extends AbstractQuery<DBProtocol, DBProject, EQCondition, DBProject>  implements IQueryRetrieval<DBProject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2677470406987442304L;
	protected ReadProject readProject; 
	
	public ReadProjectMembership(DBProtocol protocol,DBProject project) {
		super();
		setFieldname(protocol);
		setValue(project);
		readProject = new ReadProject(project);
	}
	

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getFieldname()==null || getFieldname().getID()<=0 || getFieldname().getVersion()<=0) throw new InvalidProtocolException();
		params = new ArrayList<QueryParam>();
		params.add(ReadProtocol.fields.idprotocol.getParam(getFieldname()));
		params.add(ReadProtocol.fields.version.getParam(getFieldname()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return ((getFieldname()!=null) && (getFieldname().getID()>0))
				?getValue().getGroupType().getReadByProtocolSQL(getFieldname())
				:getValue().getGroupType().getReadByProtocolSQL(getFieldname());
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}


	@Override
	public DBProject getObject(ResultSet rs) throws AmbitException {
		return readProject.getObject(rs);
	}


	@Override
	public double calculateMetric(DBProject object) {
		return 1;
	}
}
