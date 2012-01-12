package org.toxbank.rest.groups.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.user.DBUser;

public class AddGroupsPerUser<P extends IDBGroup> extends AbstractUpdate<DBUser,List<P>> {
	public static final String sql_addGroup = "insert ignore into user_%s (iduser,%s,priority) values ";
	
	public AddGroupsPerUser(DBUser user,List<P> groups) {
		super();
		setObject(groups);
		setGroup(user);
	}
	public AddGroupsPerUser(DBUser user, P ref) {
		super();
		setGroup(user);
		List<P> g = new ArrayList<P>();
		g.add(ref);
		setObject(g);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		if ((getGroup()==null) || (getGroup().getID()<=0)) throw new AmbitException("No user!");
		if ((getObject()==null) || (getObject().size()==0)) throw new AmbitException("No group!");

		StringBuilder b = new StringBuilder();
		GroupType gt = getObject().get(0).getGroupType();
		String d = String.format(sql_addGroup, gt.getDBname(), gt.getID());
		for (IDBGroup g : getObject()) {
			b.append(d);
			b.append("(?,?,?)");
			d = ",";
		}
		return new String[] {b.toString()};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getGroup()==null) || (getGroup().getID()<=0)) throw new AmbitException("No user!");
		if ((getObject()==null) || (getObject().size()==0)) throw new AmbitException("No group!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (int i=0; i < getObject().size(); i++) {
			IDBGroup g = getObject().get(i);
			if (g.getID()<=0) continue;
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
			params.add(new QueryParam<Integer>(Integer.class, g.getID()));
			params.add(new QueryParam<Integer>(Integer.class, i+1));
		}
		return params;
	}

	@Override
	public void setID(int index, int id) {
	}

}
