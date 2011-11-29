package org.toxbank.rest.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.user.DBUser;

public class CreateUser extends AbstractObjectUpdate<DBUser>{

	public CreateUser(DBUser user) {
		setObject(user);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
				"insert into user (iduser,username,firstname,lastname) " +
				"values (?,?,?,?)"
				};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class,  null));
		params1.add(new QueryParam<String>(String.class,  getObject().getUserName()));
		params1.add(new QueryParam<String>(String.class,  getObject().getFirstname()));
		params1.add(new QueryParam<String>(String.class,  getObject().getLastname()));

		return params1;
	}

	@Override
	public void setID(int index, int id) {
		getObject().setID(id);
	}

}
