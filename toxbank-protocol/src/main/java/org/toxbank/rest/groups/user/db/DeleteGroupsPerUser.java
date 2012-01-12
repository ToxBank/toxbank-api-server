package org.toxbank.rest.groups.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.user.DBUser;

/**
 * Remove a group (project or organisation) from the user profile
 * @author nina
 *
 */
public class DeleteGroupsPerUser<P extends IDBGroup> extends AbstractUpdate<DBUser,List<P>> {

	public DeleteGroupsPerUser(DBUser user, P ref) {
		super();
		setGroup(user);
		List<P> g = new ArrayList<P>();
		g.add(ref);
		setObject(g);
 	}
	public DeleteGroupsPerUser(List<P> ref) {
		super(ref);
	}
	public DeleteGroupsPerUser() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().size()<=0) throw new AmbitException("No group id!");
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("No user id!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		for (IDBGroup g : getObject()) 
			params.add(new QueryParam<Integer>(Integer.class, g.getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if ((getGroup()==null) || (getGroup().getID()<=0)) throw new AmbitException("No user!");
		if ((getObject()==null) || (getObject().size()==0)) throw new AmbitException("No group!");

		StringBuilder b = new StringBuilder();
		GroupType gt = getObject().get(0).getGroupType();
		String d = gt.getDeleteByUserSQL() + " and (";
		for (IDBGroup g : getObject()) {
			b.append(d);
			b.append(String.format("%s=?",gt.getID()));
			d = " or ";
		}
		b.append(")");
		return new String[] {b.toString()};
	}
	public void setID(int index, int id) {
			
	}
}