package org.toxbank.rest.user.alerts.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.toxbank.rest.db.exceptions.InvalidUserException;
import org.toxbank.rest.user.DBUser;

/**
 * Adds an alert
 * @author nina
 *
 */
public class AddAlert  extends AbstractAlertUpdate<DBUser> {
	public static final String[] sql_addAlert = new String[] {"insert into alert (name,query,qformat,rfrequency,rinterval,iduser,sent) values (?,?,?,?,?,?,now()) "};
	
	public AddAlert(DBAlert alert,DBUser author) {
		super(alert);
		setGroup(author);
	}
	public AddAlert() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null || getGroup().getID()<=0) throw new InvalidUserException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getTitle()==null?getObject().getQuery().getContent():getObject().getTitle()));
		params.add(new QueryParam<String>(String.class, getObject().getQuery().getContent()));
		params.add(new QueryParam<String>(String.class, getObject().getQuery().getType().name()));
		params.add(new QueryParam<String>(String.class, getObject().getRecurrenceFrequency().name()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getRecurrenceInterval()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		if (getGroup()==null || getGroup().getID()<=0) throw new InvalidUserException();
		return sql_addAlert;
	}
	public void setID(int index, int id) {
		getObject().setID(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}