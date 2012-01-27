package org.toxbank.rest.protocol.db;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IStoredProcStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;

public class CreateProtocolVersion  extends AbstractObjectUpdate<DBProtocol> implements IStoredProcStatement {
	protected static final ReadProtocol.fields[] f = new ReadProtocol.fields[] {
			ReadProtocol.fields.idprotocol,
			ReadProtocol.fields.version,
			ReadProtocol.fields.title,
			ReadProtocol.fields.anabstract,
			ReadProtocol.fields.filename
	};
	protected String[] create_sql = {"{CALL createProtocolVersion(?,?,?,?,?,?)}"};

	public CreateProtocolVersion(DBProtocol ref) {
		super(ref);
	}
	public CreateProtocolVersion() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject().getID()<=0) throw new AmbitException("No protocol ID");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();

		for (ReadProtocol.fields field: f) 
			params1.add(field.getParam(getObject()));
		
		params1.add(new QueryParam<Integer>(Integer.class, -1));
		
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
		return false;
	}
	
	@Override
	public boolean isStoredProcedure() {
		return true;
	}
	
	/**
	 * Allows retrieving stored procedure output parameters; 
	 * Does nothing by default
	 */
	@Override
	public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException {
		getObject().setVersion(statement.getInt(f.length+1));
	}
}
