package org.toxbank.rest.groups;

import org.toxbank.rest.protocol.DBProtocol;

public enum GroupType {
	PROJECT, ORGANISATION;
	public String getID() {
		return String.format("id%s", name().toLowerCase());
	}

	public String getDBname() {
		return name().toLowerCase();
	}

	public String getSearchSQL(String field,boolean user) {
		return user
			?String.format("SELECT %s,name,ldapgroup,cluster FROM %s where iduser=? and %s regexp ?", getID(),getDBname(),field)	
			:String.format("SELECT %s,name,ldapgroup,cluster FROM %s where %s regexp ?", getID(),getDBname(),field);
	}

	public String getReadSQL(boolean all,String field,String search) {
		if (all)
			if (search != null) return getSearchSQL(field,false);
			else return String.format("SELECT %s,name,ldapgroup,cluster FROM %s", getID(),getDBname());
		else
			return String.format("SELECT %s,name,ldapgroup,cluster FROM %s where %s=?",
					getID(), getDBname(), getID());
	}

	public String getDeleteSQL() {
		return String.format("DELETE FROM %s where %s=?", getDBname(), getID());
	}

	public String getDeleteByUserSQL() {
		return String.format("DELETE FROM user_%s where iduser=? ", getDBname(), getID());
	}
	
	public String getCreateSQL() {
		return String.format(
				"INSERT into %s (%s,name,ldapgroup) values (?,?,?)",
				getDBname(), getID());
	}
	
	public String getUpdateSQL() {
		return String.format(
				"UPDATE %s set name = ?,ldapgroup=? where %s = ?",
				getDBname(), getID());
	}

	public String getReadByUserSQL(boolean all,String field,String search) {
		if (all)
			if (search != null) return getSearchSQL(field,true);
			else return String.format("SELECT %s,name,ldapgroup,cluster FROM %s join user_%s using(%s) where iduser=?", 
						getID(),getDBname(),getDBname(),getID());
		else
			return String.format("SELECT %s,name,ldapgroup,cluster FROM %s join user_%s using(%s) where iduser=? and %s=?",
					getID(), getDBname(),getDBname(),getID(), getID());
	}	
	
	public String getReadByProtocolSQL(DBProtocol protocol) {
		return String.format("SELECT %s,name,ldapgroup,cluster FROM %s join protocol_%ss using(%s) where idprotocol=? and version=?",
					getID(), getDBname(),getDBname(),getID(), protocol.getID(),protocol.getVersion());
	}	
}