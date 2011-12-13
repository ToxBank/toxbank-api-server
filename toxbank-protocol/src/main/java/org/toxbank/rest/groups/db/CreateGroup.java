package org.toxbank.rest.groups.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.groups.IDBGroup;

public class CreateGroup extends AbstractObjectUpdate<IDBGroup>{

	public CreateGroup(IDBGroup group) {
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
		params1.add(new QueryParam<String>(String.class,  getObject().getTitle()));
		params1.add(new QueryParam<String>(String.class,  getObject().getGroupName()));
		return params1;
	}

	@Override
	public void setID(int index, int id) {
		getObject().setID(id);
	}

	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
