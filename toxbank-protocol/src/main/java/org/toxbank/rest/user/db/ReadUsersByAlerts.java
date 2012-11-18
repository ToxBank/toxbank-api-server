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
	private static final String sql_tonotify =
		String.format(sql,
		" where iduser in (",
		"select iduser from alert where ifnull(case \n"+
		"when rfrequency = 'hourly' then DATE_ADD(sent,INTERVAL 1 HOUR)\n"+
		"when rfrequency = 'daily' then DATE_ADD(sent,INTERVAL 1 DAY)\n"+
		"when rfrequency='weekly' then DATE_ADD(sent,INTERVAL 1 WEEK)\n"+
		"when rfrequency='monthly' then DATE_ADD(sent,INTERVAL 1 MONTH)\n"+
		"when rfrequency='yearly' then DATE_ADD(sent,INTERVAL 1 YEAR)\n"+
		"else DATE_ADD(sent,INTERVAL 1 DAY) end,now())<=now() )");
	/**
	 * 
	 */
	private static final long serialVersionUID = 786571615559857812L;
	public ReadUsersByAlerts() {
		this(new HashSet<Alert.RecurrenceFrequency>());
	}
	public ReadUsersByAlerts(Set<RecurrenceFrequency> set) {
		super();
		setFieldname(set);
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	
	public String getSQL() throws AmbitException {
		if (getFieldname()==null || getFieldname().size()==0) return sql_tonotify;
		String sql = String.format(getSQLTemplate(),
				" where iduser in (select iduser from alert where ",
				ReadAlert.getFrequencySQL(getFieldname()));
		return sql;
	}
}
