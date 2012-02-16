package org.toxbank.rest.user.alerts.db;

import net.idea.modbcum.q.update.AbstractUpdate;

public abstract class AbstractAlertUpdate<T> extends  AbstractUpdate<T,DBAlert> {
	public AbstractAlertUpdate(DBAlert target) {
		setObject(target);
	}
}
