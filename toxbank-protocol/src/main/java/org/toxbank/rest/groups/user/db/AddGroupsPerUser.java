package org.toxbank.rest.groups.user.db;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.user.DBUser;

public class AddGroupsPerUser extends AbstractUpdate<List<IDBGroup>, DBUser> {

	@Override
	public String[] getSQL() throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
