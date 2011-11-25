package org.toxbank.rest.groups;

public class DBGroup {
	protected GroupType groupType = GroupType.PROJECT;
	
	public DBGroup(GroupType groupType,Integer id) {
		this(groupType);
		this.ID = id;
	}
	public DBGroup(GroupType groupType) {
		this.groupType = groupType;
	}
	public GroupType getGroupType() {
		return groupType;
	}
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}
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
	
	protected int ID;
	protected String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	protected String ldapgroup;
	
	public String getLdapgroup() {
		return ldapgroup;
	}
	public void setLdapgroup(String ldapgroup) {
		this.ldapgroup = ldapgroup;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
}
