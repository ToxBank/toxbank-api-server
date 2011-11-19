package org.toxbank.rest.protocol.db.template;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol.fields;

public class UpdateDataTemplate extends AbstractObjectUpdate<IProtocol>{
	public static final String[] create_sql = {
		"update protocol set template = compress(?) where idprotocol=?"
	};

	public UpdateDataTemplate(IProtocol ref) {
		super(ref);
	}
	public UpdateDataTemplate() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class,getObject().getTemplate().toString()));
		params1.add(fields.idprotocol.getParam(getObject()));
		
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	public void setID(int index, int id) {
		getObject().setID(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}