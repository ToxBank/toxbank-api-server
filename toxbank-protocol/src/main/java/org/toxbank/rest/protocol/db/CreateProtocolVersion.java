package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;

public class CreateProtocolVersion  extends AbstractObjectUpdate<DBProtocol>{
	public static final String[] create_sql = {
		"insert into protocol (idprotocol,version,title,abstract,iduser,summarySearchable,idproject,idorganisation,filename)\n" +
		"select (idprotocol,max(version),?,?,iduser,summarySearchable,idproject,idorganisation,?) from protocol where idprotocol=? group by idprotocol\n"+
		"on duplicate key update version=values(version)+1"
	};

	public CreateProtocolVersion(DBProtocol ref) {
		super(ref);
	}
	public CreateProtocolVersion() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject().getID()<=0) throw new AmbitException("No protocol ID");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		ReadProtocol.fields[] f = new ReadProtocol.fields[] {
				ReadProtocol.fields.idprotocol,
				ReadProtocol.fields.title,
				ReadProtocol.fields.anabstract,
				ReadProtocol.fields.filename
		};
		for (ReadProtocol.fields field: f) 
			params1.add(field.getParam(getObject()));
		
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
