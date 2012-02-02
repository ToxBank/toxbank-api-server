package org.toxbank.rest.groups;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.query.QueryParam;
import net.toxbank.client.resource.Group;

public class DBGroup extends Group implements IDBGroup {
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

	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	public enum fields {
		idgroup {
			@Override
			public void setParam(IDBGroup group, ResultSet rs) throws SQLException {
				group.setID(rs.getInt(name()));
			}		
			@Override
			public Object getValue(IDBGroup group) {
				return group==null?null:group.getID();
			}
			@Override
			public Class getClassType(IDBGroup group) {
				return Integer.class;
			}
			@Override
			public String toString() {
				return "URI";
			}
		},
		name {
			@Override
			public void setParam(IDBGroup group, ResultSet rs) throws SQLException {
				group.setTitle(rs.getString(name()));
			}		
			@Override
			public Object getValue(IDBGroup group) {
				return group==null?null:group.getTitle();
			}
		},
		ldapgroup {
			@Override
			public void setParam(IDBGroup protocol, ResultSet rs) throws SQLException {
				protocol.setGroupName(rs.getString(name()));
			}
			@Override
			public Object getValue(IDBGroup protocol) {
				return protocol==null?null:protocol.getGroupName();
			}
			@Override
			public String toString() {
				return "Group name, as assigned by the AA service";
			}			
		},				
	    ;
		public String getCondition() {
			return String.format(" %s = ? ",name());
		}
		public QueryParam getParam(IDBGroup group) {
			return new QueryParam<String>(String.class,  getValue(group).toString());
		}
		public abstract Object getValue(IDBGroup group) ;
		public Class getClassType(IDBGroup group) {
			return String.class;
		}
		public void setParam(IDBGroup group,ResultSet rs) throws SQLException {}
		
		public String getHTMLField(IDBGroup protocol) {
			Object value = getValue(protocol);
			return String.format("<input name='%s' type='text' size='40' value='%s'>\n",
					name(),getDescription(),value==null?"":value.toString());
		}
		public String getDescription() { return toString();}
		@Override
		public String toString() {
			String name= name();
			return  String.format("%s%s",
					name.substring(0,1).toUpperCase(),
					name.substring(1).toLowerCase());
		}

	}	
	public int parseURI(String baseReference)  {
		return -1;
	}

}
