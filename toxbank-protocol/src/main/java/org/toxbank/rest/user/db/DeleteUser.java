package org.toxbank.rest.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.user.DBUser;

/**
 * Delete an user
 * @author nina
 *
 */
public class DeleteUser extends AbstractObjectUpdate<DBUser> {

	public DeleteUser(DBUser ref) {
		super(ref);
	}
	public DeleteUser() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new AmbitException("No user id!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new AmbitException("No user id!");
		return new String[] { "DELETE from user where iduser=?"};
	}
	public void setID(int index, int id) {
			
	}
}