package org.toxbank.rest.groups;

import net.toxbank.client.resource.Project;

public class DBProject extends Project implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4356100798611119822L;
	protected DBGroup group;
	
	public DBProject() {
		this(null);
	}
	public DBProject(Integer id) {
		group = new DBGroup(GroupType.PROJECT);
		if (id!=null) setID(id);
	}

	public GroupType getGroupType() {
		return group.getGroupType();
	}
	public void setGroupType(GroupType groupType) {
		group.setGroupType(groupType);
	}
	public String getName() {
		return group.getName();
	}
	public void setName(String name) {
		group.setName(name);
	}
	public String getLdapgroup() {
		return group.getLdapgroup();
	}
	public void setLdapgroup(String ldapgroup) {
		group.setLdapgroup(ldapgroup);
	}
	@Override
	public int getID() {
		return group.getID();
	}
	@Override
	public void setID(int iD) {
		group.setID(iD);
		
	}
	@Override
	public String toString() {
		return getName();
	}
}
