package org.toxbank.rest.user.db;

import java.sql.ResultSet;
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

public class ReadUserID<T>  extends AbstractQuery<T, DBUser, EQCondition, DBUser>  implements IQueryRetrieval<DBUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	
	protected static String sql_idtemplate =	"SELECT iduser from user %s %s";


	public ReadUserID(DBUser user) {
		super();
		setValue(user);
	}

	public ReadUserID(Integer id) {
		super();
		setValue(id==null?null:new DBUser(id));
	}
	
	public ReadUserID() {
		this((Integer)null);
	}
		
	public double calculateMetric(DBUser object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	protected String getSQLTemplate() {
		return sql_idtemplate;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			if (getValue().getID()>0)
				params.add(ReadUser.fields.iduser.getParam(getValue()));
			else try {
				if (getValue().getLastname()!=null)
					params.add(ReadUser.fields.lastname.getParam(getValue()));
				if (getValue().getFirstname()!=null)
					params.add(ReadUser.fields.firstname.getParam(getValue()));
				
			} catch (Exception x) {
				x.printStackTrace();
			}
			
		} 
		return params;
	}

	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getID()>0)
				return String.format(getSQLTemplate(),"where ",ReadUser.fields.iduser.getCondition());
			else {
				String where = " ";
				StringBuilder b = new StringBuilder();
				if (getValue().getLastname()!= null) {
					b.append(ReadUser.fields.lastname.getCondition());
					where = " or ";
				}
				if (getValue().getFirstname()!= null) {
					b.append(where);
					b.append(ReadUser.fields.firstname.getCondition());
				}
				return String.format(getSQLTemplate(),"where ",b.toString());
			}
		}
		return String.format(getSQLTemplate(),"","");
			
	}

	public DBUser getObject(ResultSet rs) throws AmbitException {
		try {
			DBUser p =  new DBUser();
			ReadUser.fields.iduser.setParam(p,rs);

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
