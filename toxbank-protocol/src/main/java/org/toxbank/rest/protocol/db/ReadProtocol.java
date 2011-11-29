package org.toxbank.rest.protocol.db;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.toxbank.client.resource.Organisation;
import net.toxbank.client.resource.Project;

import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.metadata.Document;
import org.toxbank.rest.user.DBUser;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadProtocol  extends AbstractQuery<String, DBProtocol, EQCondition, DBProtocol>  implements IQueryRetrieval<DBProtocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	public enum fields {
		idprotocol {
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setID(rs.getInt(name()));
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Integer>(Integer.class, (Integer)getValue(protocol));
			}	
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.getID()>0?protocol.getID():null;
			}
			@Override
			public String toString() {
				return "URI";
			}
		},
		version {
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setVersion(rs.getString(name()));
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Integer>(Integer.class, (Integer)getValue(protocol));
			}	
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?1:protocol.getVersion()==null?1:protocol.getVersion();
			}
			@Override
			public String toString() {
				return "Version";
			}
		},		
		identifier {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, (String)getValue(protocol));
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setIdentifier(rs.getString(name()));
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.getIdentifier();
			}
		},
		title {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, (String)getValue(protocol));
			}			
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setTitle(rs.getString(name()));
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.getTitle();
			}
		},
		anabstract {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, (String) getValue(protocol));
			}	
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setAbstract(rs.getString(name()));
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.getAbstract();
			}
			public String getHTMLField(DBProtocol protocol) {
				Object value = getValue(protocol);
				return String.format("<textarea name='%s' cols='40' rows='5' title='%s'>%s</textarea>\n",
						name(),getDescription(),value==null?"":value.toString());
			}			
			@Override
			public String toString() {
				return "Abstract";
			}
		},
		iduser {

			@Override
			public QueryParam getParam(DBProtocol protocol) {
				Object project = getValue(protocol);
				return new QueryParam<Integer>(Integer.class, project==null?null:((DBUser) protocol.getOwner()).getID());
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					DBUser user = new DBUser();
					user.setID(rs.getInt(name()));
					protocol.setOwner(user);
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:((DBUser) protocol.getOwner()).getID();
			}			
		},			
		summarySearchable {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Boolean>(Boolean.class, (Boolean) getValue(protocol));
			}		
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setSummarySearchable(rs.getBoolean(name()));
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.isSummarySearchable();
			}
			@Override
			public Class getClassType(DBProtocol protocol) {
				return Boolean.class;
			}
			public String getHTMLField(DBProtocol protocol) {
				Object value = getValue(protocol);
				return String.format("<input name='%s' type='checkbox' title='%s' value='%s'>\n",
						name(),
						getDescription(),
						value==null?"":value.toString());
			}			
			@Override
			public String toString() {
				return "Is summary searchable";
			}
		},
		idproject {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				DBProject project = (DBProject) getValue(protocol);
				return new QueryParam<Integer>(Integer.class, project==null?null:project.getID());
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					Project p = protocol.getProject();
					if (p==null) { 
						DBProject dbp = new DBProject(); 
						protocol.setProject(dbp);
						dbp.setID(rs.getInt(name()));
					} else if (p instanceof DBProject) {
						((DBProject)p).setID(rs.getInt(name()));
					}
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getProject();
			}			
		},
		project {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				/*
				DBProject project = (DBProject) getValue(protocol);
				return new QueryParam<Integer>(Integer.class, project==null?null:project.getID());
				*/
				return null;
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					Project p = protocol.getProject();
					if (p==null) { 
						DBProject dbp = new DBProject(); 
						protocol.setProject(dbp);
						dbp.setTitle(rs.getString(name()));
					} else if (p instanceof DBProject) {
						((DBProject)p).setTitle(rs.getString(name()));
					}
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getProject();
			}			
		},
		idorganisation {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				DBOrganisation project = (DBOrganisation) getValue(protocol);
				return new QueryParam<Integer>(Integer.class, project==null?null:project.getID());
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					Organisation p = protocol.getOrganisation();
					if (p==null) { 
						DBOrganisation dbp = new DBOrganisation(); 
						protocol.setOrganisation(dbp);
						dbp.setID(rs.getInt(name()));
					} else if (p instanceof DBOrganisation) {
						((DBOrganisation)p).setID(rs.getInt(name()));
					}
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getOrganisation();
			}			
		},	
		organisation {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return null;
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					Organisation p = protocol.getOrganisation();
					if (p==null) { 
						DBOrganisation dbp = new DBOrganisation(); 
						protocol.setOrganisation(dbp);
						dbp.setTitle(rs.getString(name()));
					} else if (p instanceof DBOrganisation) {
						((DBOrganisation)p).setTitle(rs.getString(name()));
					}
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getOrganisation();
			}			
		},		
		filename {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, getValue(protocol).toString());
			}			
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
				protocol.setDocument(new Document(new URI(rs.getString(name()))));
				} catch (Exception x) {throw new SQLException(x); }
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getDocument().getURI();
			}				
			public String getHTMLField(DBProtocol protocol) {
				Object value = getValue(protocol);
				return String.format("<input name='%s' type='file' title='%s' size='40' value='%s'>\n",
						name(),
						getDescription(),
						value==null?"":value.toString());
			}		
			@Override
			public String toString() {
				return "Document";
			}
		};
		public String getCondition() {
			return String.format(" %s = ? ",name());
		}
		public QueryParam getParam(DBProtocol protocol) {
			return new QueryParam<String>(String.class,  getValue(protocol).toString());
		}
		public Object getValue(DBProtocol protocol) {
			return protocol.getDocument().getURI().toString();
		}
		public Class getClassType(DBProtocol protocol) {
			return String.class;
		}
		public void setParam(DBProtocol protocol,ResultSet rs) throws SQLException {}
		
		public String getHTMLField(DBProtocol protocol) {
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
	
	protected static String sql = 
		"select idprotocol,version,identifier,title,abstract as anabstract,iduser,summarySearchable," +
		"idproject,project.name as project,idorganisation,organisation.name as organisation,filename " +
		"from protocol join organisation using(idorganisation) join project using(idproject) %s %s ";


	public ReadProtocol(Integer id) {
		super();
		setValue(id==null?null:new DBProtocol(id));
	}
	public ReadProtocol() {
		this(null);
	}
		
	public double calculateMetric(DBProtocol object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			params.add(fields.idprotocol.getParam(getValue()));
		}
		return params;
	}

	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue().getID()>0))
			return String.format(sql,"where",fields.idprotocol.getCondition());
		else 
			return String.format(sql,"","");
			
	}

	public DBProtocol getObject(ResultSet rs) throws AmbitException {
		try {
			DBProtocol p =  new DBProtocol();
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
		return getValue()==null?"All protocols":String.format("Protocol id=P%s",getValue().getID());
	}
}
