package org.toxbank.rest.user.db;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.user.DBUser;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadUser<T>  extends AbstractQuery<T, DBUser, EQCondition, DBUser>  implements IQueryRetrieval<DBUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	public enum fields {
		iduser {
			@Override
			public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
				protocol.setID(rs.getInt(name()));
			}
			@Override
			public QueryParam getParam(DBUser protocol) {
				return new QueryParam<Integer>(Integer.class, (Integer)getValue(protocol));
			}	
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getID()>0?protocol.getID():null;
			}
			@Override
			public String toString() {
				return "URI";
			}
		},
	
		username {
			@Override
			public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
				protocol.setUserName(rs.getString(name()));
			}
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getUserName();
			}
		},
		title {
			@Override
			public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
				protocol.setTitle(rs.getString(name()));
			}		
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getTitle();
			}
		},
		firstname {
			@Override
			public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
				protocol.setFirstname(rs.getString(name()));
			}		
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getFirstname();
			}
			public String getCondition() {
				return String.format(" %s regexp ? ",name());
			}
		},
		lastname {
			@Override
			public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
				protocol.setLastname(rs.getString(name()));
			}		
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getLastname();
			}
			public String getCondition() {
				return String.format(" %s regexp ? ",name());
			}
		},	
		weblog {
			@Override
			public void setParam(DBUser user, ResultSet rs) throws SQLException {
				try {
					String n = rs.getString(name());
					user.setWeblog(n==null?null:new URL(n));}
				catch (Exception x) {throw new SQLException(x);}
			}		
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getWeblog();
			}			
		},
		homepage {
			@Override
			public void setParam(DBUser user, ResultSet rs) throws SQLException {
				try {
					String n = rs.getString(name());
					user.setHomepage(n==null?null:new URL(n));
				} catch (Exception x) {throw new SQLException(x);}
			}		
			@Override
			public Object getValue(DBUser protocol) {
				return protocol==null?null:protocol.getHomepage();
			}				
		},

		;
		public String getCondition() {
			return String.format(" %s = ? ",name());
		}
		public QueryParam getParam(DBUser protocol) {
			return new QueryParam<String>(String.class,  getValue(protocol).toString());
		}
		public Class getClassType(DBUser user) {
			return String.class;
		}
		public void setParam(DBUser protocol, ResultSet rs) throws SQLException {
			protocol.setLastname(rs.getString(name()));
		}		
		public Object getValue(DBUser protocol) {
			return protocol==null?null:protocol.getLastname();
		}
		
		public String getHTMLField(DBUser user) {
			Object value = getValue(user);
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
	
	protected static String sql = 
		"SELECT iduser,username,title,firstname,lastname,institute,weblog,homepage from user %s %s";


	public ReadUser(DBUser user) {
		super();
		setValue(user);
	}

	public ReadUser(Integer id) {
		super();
		setValue(id==null?null:new DBUser(id));
	}
	
	public ReadUser() {
		this((Integer)null);
	}
		
	public double calculateMetric(DBUser object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			if (getValue().getID()>0)
				params.add(fields.iduser.getParam(getValue()));
			else try {
				if (getValue().getLastname()!=null)
					params.add(fields.lastname.getParam(getValue()));
				if (getValue().getFirstname()!=null)
					params.add(fields.firstname.getParam(getValue()));
				
			} catch (Exception x) {
				x.printStackTrace();
			}
			
		} 
		return params;
	}

	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getID()>0)
				return String.format(sql,"where ",fields.iduser.getCondition());
			else {
				String where = " ";
				StringBuilder b = new StringBuilder();
				if (getValue().getLastname()!= null) {
					b.append(fields.lastname.getCondition());
					where = " or ";
				}
				if (getValue().getFirstname()!= null) {
					b.append(where);
					b.append(fields.firstname.getCondition());
				}
				return String.format(sql,"where ",b.toString());
			}
		}
		return String.format(sql,"","");
			
	}

	public DBUser getObject(ResultSet rs) throws AmbitException {
		try {
			DBUser p =  new DBUser();
			for (fields field:fields.values()) try {
				field.setParam(p,rs);
				
			} catch (Exception x) {
				System.err.println(field);
				x.printStackTrace();
			}
			return p;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All users":String.format("User id=U%s",getValue().getID());
	}
	public static int parseIdentifier(String key) throws ResourceException {
		if (key.toString().startsWith("U")) try {
			return Integer.parseInt(Reference.decode(key.toString().substring(1)));
		} catch (Exception x) {} 
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

}
