package org.toxbank.rest.groups.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.user.DBUser;

public abstract class ReadGroup<G extends IDBGroup> extends AbstractQuery<DBUser, G, EQCondition, G>  implements IQueryRetrieval<G> {
	

 
	/**
	 * 
	 */
	private static final long serialVersionUID = 888018870900333768L;

	public ReadGroup(G group) {
		super();
		setValue(group);
	}
	@Override
	public String getSQL() throws AmbitException {
		if (getValue()==null) throw new AmbitException("No value!");
		return ((getFieldname()!=null) && (getFieldname().getID()>0))
				?getValue().getGroupType().getReadByUserSQL(getValue().getID()<=0,getValue().getTitle())
				:getValue().getGroupType().getReadSQL(getValue().getID()<=0,getValue().getTitle());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("No value!");
		List<QueryParam> params =  new ArrayList<QueryParam>();
		if ((getFieldname()!=null) && (getFieldname().getID()>0))
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
		
		if (getValue().getID()>0) {
			params.add(new QueryParam<Integer>(Integer.class,getValue().getID()));
		} else if (getValue().getTitle()!=null) {
			params.add(new QueryParam<String>(String.class,getValue().getTitle()));
		}
		return params;
	}

	public abstract G createObject();
	//new DBGroup(getValue().getGroupType());
	@Override
	public G getObject(ResultSet rs) throws AmbitException {
		try {
			G group = createObject();
			group.setID(rs.getInt(1));
			group.setTitle(rs.getString(2));
			group.setGroupName(rs.getString(3));
			return group;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(G object) {
		return 1;
	}

}
