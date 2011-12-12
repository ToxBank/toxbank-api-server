package org.toxbank.rest.user.author.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.toxbank.rest.db.exceptions.InvalidProtocolException;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.ReadUser;

public class ReadAuthor extends ReadUser<DBProtocol> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2677470406987442304L;
	private static final String join = "\njoin protocol_authors using(iduser) where ";
	public ReadAuthor(DBProtocol protocol,DBUser user) {
		super(user);
		setFieldname(protocol);
	}
	

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getFieldname()==null || getFieldname().getID()<=0 || getFieldname().getVersion()<=0) throw new InvalidProtocolException();
		params = new ArrayList<QueryParam>();
		params.add(ReadProtocol.fields.idprotocol.getParam(getFieldname()));
		params.add(ReadProtocol.fields.version.getParam(getFieldname()));
		if ((getValue()!=null) && getValue().getID()>0)
			params.add(fields.iduser.getParam(getValue()));
		
		return params;
	}

	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue().getID()>0))
			return String.format(sql,join,
				   String.format("%s and %s and %s",
						   ReadProtocol.fields.idprotocol.getCondition(),
						   ReadProtocol.fields.version.getCondition(),
						   fields.iduser.getCondition()));
		else 
			return String.format(sql,join,
					String.format("%s and %s",
							ReadProtocol.fields.idprotocol.getCondition(),
							ReadProtocol.fields.version.getCondition()
							));
			
	}	
}
