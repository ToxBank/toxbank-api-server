package org.toxbank.rest.user.alerts.db;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.toxbank.rest.db.exceptions.InvalidAlertException;
import org.toxbank.rest.user.DBUser;

public class UpdateAlertsSentTimeStamp extends  AbstractUpdate<Object,DBUser> {
	private final String sql = "update alert set sent=now() where idquery in ";
	
	public UpdateAlertsSentTimeStamp(DBUser user) {
		super();
		setObject(user);
	}
	public UpdateAlertsSentTimeStamp() {
		this(null);
	}	
	
	@Override
	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		b.append(sql);
		if (getObject()==null || getObject().getAlerts()== null || getObject().getAlerts().size()==0) throw new InvalidAlertException();

		String d = "(";
		for (DBAlert alert: getObject().getAlerts()) 
			if (alert.getID()>0) {
				b.append(d);
				b.append(alert.getID());
				d = ",";
			} else throw new InvalidAlertException();
		b.append(")");
		return new String[] {b.toString()};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		return null;
	}

	@Override
	public void setID(int index, int id) {
	}

}
