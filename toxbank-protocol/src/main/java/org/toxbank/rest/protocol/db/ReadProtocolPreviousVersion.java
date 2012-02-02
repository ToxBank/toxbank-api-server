package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class ReadProtocolPreviousVersion extends ReadProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -779583796759381542L;
	
	public ReadProtocolPreviousVersion(Integer id, Integer version) {
		super(id,version);
		setPage(0);
		setPageSize(1);
	}
	public String getSQL() throws AmbitException {
		return String.format(sql," where "," idprotocol=? and version<? ");
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			params.add(fields.idprotocol.getParam(getValue()));
			params.add(fields.version.getParam(getValue()));
			return params;
		} else throw new AmbitException();

	}		
}
