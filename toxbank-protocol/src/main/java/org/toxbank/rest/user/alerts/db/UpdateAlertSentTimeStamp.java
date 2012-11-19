package org.toxbank.rest.user.alerts.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.toxbank.rest.db.exceptions.InvalidAlertException;
import org.toxbank.rest.user.DBUser;

public class UpdateAlertSentTimeStamp extends AbstractAlertUpdate<DBUser> {
			
	public static final String[] sql_updateAlert = new String[] {
		"update alert set sent=now() where idquery = ?"
	};
	
	public UpdateAlertSentTimeStamp(DBAlert alert,DBUser author) {
		super(alert);
		setGroup(author);
	}
	public UpdateAlertSentTimeStamp() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidAlertException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Long.class, getObject().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new InvalidAlertException();
		return sql_updateAlert;
	}
	public void setID(int index, int id) {
		getObject().setID(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}