package org.toxbank.rest.groups.db;

import org.toxbank.rest.groups.DBProject;

public class ReadProject extends ReadGroup<DBProject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4642657809539511712L;

	public ReadProject(DBProject group) {
		super(group);
	}
	@Override
	public double calculateMetric(DBProject object) {
		return 1;
	}

	@Override
	public DBProject createObject() {
		return new DBProject();
	}

}
