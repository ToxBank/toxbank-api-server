package org.toxbank.rest.groups.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.groups.IDBGroup;



public class UpdateGroup extends AbstractObjectUpdate<IDBGroup>{

	public UpdateGroup(IDBGroup ref) {
		super(ref);
	}
	public UpdateGroup() {
		this(null);
	}			

	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {getObject().getGroupType().getUpdateSQL()};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject()!=null) && (getObject().getID()>0)) {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class,  getObject().getTitle()));
			params1.add(new QueryParam<String>(String.class,  getObject().getGroupName()));
			params1.add(new QueryParam<Integer>(Integer.class,  getObject().getID()));
			return params1;
		} else throw new AmbitException("Empty ID");
	}

	@Override
	public void setID(int index, int id) {
	}

	@Override
	public boolean returnKeys(int index) {
		return false;
	}
}