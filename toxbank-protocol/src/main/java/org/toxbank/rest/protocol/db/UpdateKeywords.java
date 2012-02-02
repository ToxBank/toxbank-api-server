package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;

public class UpdateKeywords extends AbstractObjectUpdate<DBProtocol>{

	public static final String[] update_sql = {
		"insert into keywords (idprotocol,version,keywords) values (?,?,?) ON DUPLICATE key update keywords=values(keywords)"
		};

	public UpdateKeywords(DBProtocol ref) {
		super(ref);
	}
	public UpdateKeywords() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {

		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getVersion()));
		params.add(new QueryParam<String>(String.class, 
						ReadProtocol.fields.keywords.getValue(getObject()).toString()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return update_sql;
	}
	public void setID(int index, int id) {
			
	}
}