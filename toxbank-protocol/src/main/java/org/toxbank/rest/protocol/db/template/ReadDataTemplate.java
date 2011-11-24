package org.toxbank.rest.protocol.db.template;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.DataTemplate;
import org.toxbank.rest.protocol.db.ReadProtocol.fields;

public class ReadDataTemplate extends AbstractQuery<String, DBProtocol, EQCondition, DBProtocol>  implements IQueryRetrieval<DBProtocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	
	protected static String sql = 
		"select idprotocol,identifier,uncompress(template) from protocol where idprotocol=?";

	public ReadDataTemplate(DBProtocol protocol) {
		super();
		setValue(protocol);
	}

	public ReadDataTemplate() {
		this((DBProtocol)null);
	}
		
	public double calculateMetric(DBProtocol object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			params.add(fields.idprotocol.getParam(getValue()));
		} else throw new AmbitException("No protocol id");
		return params;
	}

	public String getSQL() throws AmbitException {
		return sql;
			
	}

	public DBProtocol getObject(ResultSet rs) throws AmbitException {
		try {
			DBProtocol protocol =  new DBProtocol();
			fields.idprotocol.setParam(protocol, rs);
			fields.identifier.setParam(protocol, rs);
			protocol.setTemplate(new DataTemplate(rs.getString(3)));
			return protocol;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All protocols":String.format("Data template for Protocol id=P%s",getValue().getID());
	}
}