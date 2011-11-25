package org.toxbank.rest.groups.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.groups.DBGroup;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol.fields;

public class CreateGroup extends AbstractObjectUpdate<DBGroup>{

	public CreateGroup(DBGroup group) {
		setObject(group);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {getObject().getGroupType().getCreateSQL()};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class,  null));
		params1.add(new QueryParam<String>(String.class,  getObject().getName()));
		params1.add(new QueryParam<String>(String.class,  getObject().getLdapgroup()));
		return params1;
	}

	@Override
	public void setID(int index, int id) {
		getObject().setID(id);
	}

}
