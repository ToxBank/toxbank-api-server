package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;

/**
 * Only sets the published flag
 * @author nina
 *
 */
public class PublishProtocol  extends AbstractObjectUpdate<DBProtocol>{


	public static final String[] update_sql = {"update protocol set updated=now(),published=? where idprotocol=? and version=?"};


	public PublishProtocol(DBProtocol ref) {
		super(ref);
	}
	
	public PublishProtocol() {
		this(null);
	}			
	
	@Override
	public void setObject(DBProtocol object) {
		super.setObject(object);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
	
		params1.add(ReadProtocol.fields.published.getParam(getObject()));
			
		if (params1.size()==0) throw new AmbitException("Nothing to update!");
		params1.add(ReadProtocol.fields.idprotocol.getParam(getObject()));
		params1.add(ReadProtocol.fields.version.getParam(getObject()));
		return params1;
	}
	public String[] getSQL() throws AmbitException {
		return update_sql;
	}
	public void setID(int index, int id) {
			
	}
}