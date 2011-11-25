package org.toxbank.rest.groups.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.groups.DBGroup;

/**
 * Delete a group (project or organisation)
 * @author nina
 *
 */
public class DeleteGroup extends AbstractObjectUpdate<DBGroup> {

	public DeleteGroup(DBGroup ref) {
		super(ref);
	}
	public DeleteGroup() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new AmbitException("No group id!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new AmbitException("No group id!");
		return new String[] { getObject().getGroupType().getDeleteSQL()};
	}
	public void setID(int index, int id) {
			
	}
}