package org.toxbank.rest.user.alerts.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.toxbank.client.resource.Alert;
import net.toxbank.client.resource.Query.QueryType;

import org.toxbank.rest.user.DBUser;

public class DBAlert extends Alert<DBUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4711297730366781673L;
	protected int ID;
	public enum _fields {
		idquery {
			@Override
			public void setParam(DBAlert alert, ResultSet rs) throws SQLException {
				alert.setID(rs.getInt(name()));
			}
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				if (value!=null)
					alert.setID(Integer.parseInt(value));
			}
		},
		name {
	
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getTitle();
			}
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setTitle(value);
			}
		},
		query {
	
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setQueryString(value);
			}
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getQueryString();
			}
		},
		qformat {
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setType(QueryType.valueOf(value));
				
			}
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getType();
			}
		},
		rfrequency {
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setRecurrenceFrequency(RecurrenceFrequency.valueOf(value));
				
			}
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getRecurrenceFrequency();
			}
		},
		rinterval {
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setRecurrenceInterval(Integer.parseInt(value));
			}
			@Override
			public void setParam(DBAlert alert, ResultSet rs) throws SQLException {
				alert.setRecurrenceInterval(rs.getInt(name()));
			}			
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getRecurrenceInterval();
			}
		},
		sent {
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				alert.setSentAt(Long.parseLong(value));
			}
			@Override
			public void setParam(DBAlert alert, ResultSet rs) throws SQLException {
				alert.setSentAt(rs.getLong(name()));
			}			
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getSentAt();
			}
		},		
		iduser {
			@Override
			public void setValue(DBAlert alert, String value)
					throws SQLException {
				DBUser user = new DBUser(Integer.parseInt(value));
				alert.setUser(user);
			}
			@Override
			public void setParam(DBAlert alert, ResultSet rs) throws SQLException {
				DBUser user = new DBUser(rs.getInt(name()));
				user.setUserName(rs.getString("username"));
				user.setID(rs.getInt("iduser"));
				alert.setUser(user);
			}
			@Override
			public Object getValue(DBAlert alert) {
				return alert.getUser();
			}
		};
		public String getCondition() {
			return String.format("%s=?",name());
		}
		public void setParam(DBAlert alert, ResultSet rs) throws SQLException {
			setValue(alert, rs.getString(name()));
		}				
		public abstract void setValue(DBAlert alert, String value) throws SQLException;
		public Object getValue(DBAlert alert) {
			return null;
		}
		public String getDescription() { return toString();}
		public String getHTMLField(DBAlert alert) {
			Object value = getValue(alert);
			return String.format("<input name='%s' type='text' size='40' value='%s'>\n",
					name(),getDescription(),value==null?"":value.toString());
		}

	}
	
	public DBAlert() {
		super();
	}
			
	public DBAlert(int id) {
		this(id,null);
	}
	public DBAlert(DBUser user) {
		this(-1,user);
	}
	public DBAlert(int id, DBUser user) {
		super();
		setID(id);
		setUser(user);
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}

}
