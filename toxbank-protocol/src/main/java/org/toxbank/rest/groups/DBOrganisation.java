package org.toxbank.rest.groups;

import net.toxbank.client.resource.Organisation;

public class DBOrganisation extends Organisation implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6081839402557578567L;
	protected DBGroup group;
	public DBOrganisation() {
		group = new DBGroup(GroupType.ORGANISATION);
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
}
