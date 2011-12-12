package org.toxbank.rest.protocol.db.template;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.toxbank.client.resource.Document;
import net.toxbank.client.resource.Template;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol.fields;

public class ReadFilePointers extends AbstractQuery<String, DBProtocol, EQCondition, DBProtocol>  implements IQueryRetrieval<DBProtocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	
	protected static String sql = 
		"select idprotocol,version,filename,template from protocol where idprotocol=? and version=?";

	public ReadFilePointers(DBProtocol protocol) {
		super();
		setValue(protocol);
	}
	public ReadFilePointers(Integer id, Integer version) {
		super();
		setValue(id==null?null:new DBProtocol(id,version));
	}
	public ReadFilePointers() {
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
			params.add(fields.version.getParam(getValue()));
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
			fields.version.setParam(protocol, rs);
			try { 
				protocol.setDataTemplate(new Template(new URL(rs.getString(fields.template.name()))));
			} catch (Exception x) { 
				protocol.setDataTemplate(null); 
			}
			try { 
				protocol.setDocument(new Document(new URL(rs.getString(fields.filename.name()))));
			} catch (Exception x) {
				protocol.setDocument(null); 
			}			
			if (protocol!=null) protocol.setIdentifier(String.format("SEURAT-Protocol-%d-%d", protocol.getID(),protocol.getVersion()));
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