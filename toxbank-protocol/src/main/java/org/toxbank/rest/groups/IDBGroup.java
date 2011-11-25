package org.toxbank.rest.groups;

import java.io.Serializable;


public interface IDBGroup extends Serializable {
	public GroupType getGroupType();
	public void setGroupType(GroupType groupType);
	public String getName();
	public void setName(String name);
	public String getLdapgroup();
	public void setLdapgroup(String ldapgroup);
	public int getID();
	public void setID(int iD);
}
