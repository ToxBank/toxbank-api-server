package org.toxbank.rest.user.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.toxbank.client.resource.Alert;
import net.toxbank.client.resource.Alert.RecurrenceFrequency;

import org.toxbank.rest.user.alerts.db.ReadAlert;

public class ReadUsersByAlerts extends ReadUser<Set<RecurrenceFrequency>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 786571615559857812L;
	public ReadUsersByAlerts() {
		this(new HashSet<Alert.RecurrenceFrequency>());
		getFieldname().add(RecurrenceFrequency.weekly);
	}
	public ReadUsersByAlerts(Set<RecurrenceFrequency> set) {
		super();
		setFieldname(set);
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	
	public String getSQL() throws AmbitException {
		String sql = String.format(getSQLTemplate(),
				" where iduser in (select iduser from alert where ",
				ReadAlert.getFrequencySQL(getFieldname()));
		return sql;
	}
}
