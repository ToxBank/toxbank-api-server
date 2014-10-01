package org.toxbank.rest.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.user.DBUser;



public class UpdateUser extends AbstractObjectUpdate<DBUser>{
	private static final String[] sql = {"update user set email=? where iduser=?"};
	
	public UpdateUser(DBUser user) {
		super(user);
	}
	public UpdateUser() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getEmail()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}
	public void setID(int index, int id) {
			
	}
}