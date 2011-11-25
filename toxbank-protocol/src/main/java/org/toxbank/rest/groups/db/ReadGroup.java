package org.toxbank.rest.groups.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.toxbank.rest.groups.DBGroup;
import org.toxbank.rest.groups.DBGroup.GroupType;

public class ReadGroup extends AbstractQuery<GroupType, DBGroup, EQCondition, DBGroup>  implements IQueryRetrieval<DBGroup> {
	

	protected static String sql = "select %s,name,ldapgroup from %s %s";
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 888018870900333768L;
	public ReadGroup(GroupType groupType,Integer id) {
		super();
		setValue(id==null?null:new DBGroup(groupType,id));
	}
	public ReadGroup(DBGroup group) {
		super();
	}
	@Override
	public String getSQL() throws AmbitException {
		if (getValue()==null) throw new AmbitException("No value!");
		return getValue().getGroupType().getReadSQL(getValue().getID()<=0);
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("No value!");
		List<QueryParam> params = null;
		if (getValue().getID()>0) {
			params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class,getValue().getID()));
		}
		return params;
	}

	@Override
	public DBGroup getObject(ResultSet rs) throws AmbitException {
		try {
			DBGroup group = new DBGroup(getValue().getGroupType());
			group.setID(rs.getInt(1));
			group.setName(rs.getString(2));
			group.setLdapgroup(rs.getString(3));
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
	public double calculateMetric(DBGroup object) {
		return 1;
	}

}
