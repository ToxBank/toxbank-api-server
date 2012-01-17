package org.toxbank.rest.groups.user.db;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.user.DBUser;

public class AddGroupPerUser<P extends IDBGroup> extends AbstractUpdate<DBUser,P> {
	protected AddGroupsPerUser<P> wrapped;
	
	public AddGroupPerUser(DBUser user, P ref) {
		super();
		wrapped = new AddGroupsPerUser<P>(user,ref);
	}
	@Override
	public void setGroup(DBUser group) {
		wrapped.setGroup(group);
	}
	@Override
	public DBUser getGroup() {
		return wrapped == null?wrapped.getGroup():null;
	}
	public void setObject(P object) {
		if (wrapped != null)
		wrapped.getObject().set(0,object);
	};
	@Override
	public P getObject() {
		return wrapped==null?null:wrapped.getObject().get(0);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return wrapped==null?null:wrapped.getSQL();
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		return wrapped==null?null:wrapped.getParameters(index);
	}

	@Override
	public void setID(int index, int id) {
		
		
	}

}
