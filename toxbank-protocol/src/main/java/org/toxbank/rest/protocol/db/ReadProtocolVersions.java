package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.toxbank.rest.protocol.db.ReadProtocol.fields;

public class ReadProtocolVersions extends ReadProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -806521374100593321L;
	public ReadProtocolVersions(Integer id) {
		super(id,-1);
	}
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getID()>0) 
				if (getValue().getVersion()>0)
					return String.format(sql,"where",
							String.format("%s and %s",fields.idprotocol.getCondition(),fields.version.getCondition()));
				else
					return String.format(sql,"where",fields.idprotocol.getCondition());
		}
		return String.format(sql,"","");
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			if (getValue().getID()>0)
				params.add(fields.idprotocol.getParam(getValue()));
				if (getValue().getVersion()>0)
					params.add(fields.version.getParam(getValue()));
					
		}
		return params;
	}	
}
