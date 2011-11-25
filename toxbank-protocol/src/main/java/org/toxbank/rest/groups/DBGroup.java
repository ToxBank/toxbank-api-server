package org.toxbank.rest.groups;

public class DBGroup implements IDBGroup {
	protected GroupType groupType = GroupType.PROJECT;
	
	protected DBGroup(GroupType groupType,Integer id) {
		this(groupType);
		this.ID = id;
	}
	protected DBGroup(GroupType groupType) {
		this.groupType = groupType;
	}
	public GroupType getGroupType() {
		return groupType;
	}
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
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
	
	@Override
	public String toString() {
		return name;
	}
}
