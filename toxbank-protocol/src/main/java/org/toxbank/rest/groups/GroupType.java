package org.toxbank.rest.groups;

public enum GroupType {
	PROJECT,
	ORGANISATION;
	public String getID() {
		return String.format("id%s", name().toLowerCase());
	}
	public String getDBname() {
		return name().toLowerCase();
	}
	public String getReadSQL(boolean all) {
		if (all)
			return String.format("SELECT %s,name,ldapgroup FROM %s",getID(),getDBname());
		else
			return String.format("SELECT %s,name,ldapgroup FROM %s where %s=?",getID(),getDBname(),getID());
	}
	public String getDeleteSQL() {
		return String.format("DELETE FROM %s where %s=?",getDBname(),getID());
	}
	public String getCreateSQL() {
		return String.format("INSERT into %s (%s,name,ldapgroup) values (?,?,?)",getDBname(),getID());
	}
}