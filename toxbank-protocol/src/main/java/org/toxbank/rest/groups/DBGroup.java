package org.toxbank.rest.groups;

public class DBGroup implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4896116442610216840L;
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
	protected String groupName;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getName() {
		return name;
	}
	@Override
	public void setTitle(String title) {
		this.name = title;
	}
	@Override
	public String getTitle() {
		return name;
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
