package org.toxbank.rest.protocol.db;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.toxbank.client.Resources;
import net.toxbank.client.resource.Document;
import net.toxbank.client.resource.Organisation;
import net.toxbank.client.resource.Project;
import net.toxbank.client.resource.Protocol;
import net.toxbank.client.resource.Protocol.STATUS;
import net.toxbank.client.resource.Template;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadProtocol  extends AbstractQuery<DBUser, DBProtocol, EQCondition, DBProtocol>  implements IQueryRetrieval<DBProtocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	protected Boolean showUnpublished = true;
	
	public Boolean getShowUnpublished() {
		return showUnpublished;
	}
	public void setShowUnpublished(Boolean showUnpublished) {
		this.showUnpublished = showUnpublished;
	}

	public static final ReadProtocol.fields[] entryFields = new ReadProtocol.fields[] {
			fields.filename,
			fields.title,
			fields.anabstract,
			fields.author_uri,
			fields.author_uri,
			fields.author_uri,
			fields.keywords,
			fields.summarySearchable,
			fields.status,
			fields.project_uri,
			fields.organisation_uri,
			fields.user_uri,
			fields.allowReadByUser,
			fields.allowReadByGroup,
			fields.published
			
			
			//ReadProtocol.fields.version
			//ReadProtocol.fields.accesslevel
		};
	public static final ReadProtocol.fields[] displayFields = new ReadProtocol.fields[] {
			fields.idprotocol,
			fields.identifier,
			fields.version,
			fields.published,
			fields.created,
			fields.updated,
			fields.filename,
			fields.title,
			fields.anabstract,
			fields.author_uri,
			fields.status,
			fields.keywords,
			fields.summarySearchable,
			//ReadProtocol.fields.status
			fields.idproject,
			fields.idorganisation,
			fields.user_uri,
			fields.template,

			//ReadProtocol.fields.accesslevel
		};	
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
		keywords {
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					String kw = rs.getString(name());
					if (kw==null) return;
					String[] keywords = kw.split(";");
					for (String keyword:keywords)
						if (!protocol.getKeywords().contains(keyword))
							protocol.addKeyword(keyword);
				} catch (Exception x) {
					throw new SQLException(x);
				}
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, (String)getValue(protocol));
			}	
			@Override
			public Object getValue(DBProtocol protocol) {
				if (protocol == null) return null;
				StringBuilder b = new StringBuilder();
				for (String keyword: protocol.getKeywords()) { 
					b.append(keyword);
					b.append(";");
				}
				return b.toString();
			}
			@Override
			public String toString() {
				return "Keywords";
			}
		},			
		version {
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setVersion(rs.getInt(name()));
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Integer>(Integer.class, (Integer)getValue(protocol));
			}	
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?0:protocol.getVersion()<=0?1:protocol.getVersion();
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
			
			public String getCondition() {
				return String.format(" %s regexp ? ",name());
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
						name(),
						getDescription(),
						value==null?"":value.toString());
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
				return  protocol==null?null:protocol.getOwner()==null?null:((DBUser) protocol.getOwner()).getID();
			}			
			@Override
			public String toString() {
				return "Owner";
			}
		},		
		user_uri {

			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:
						protocol.getOwner()==null?null:protocol.getOwner()==null?null:protocol.getOwner().getResourceURL().toString();
			}		

			@Override
			public String toString() {
				return "User (owner) URI";
			}
			@Override
			public String getExampleValue(String uri) {
				return String.format("%s%s/U1",uri,Resources.user);
			}
			@Override
			public String getHelp(String uri) {
				return String.format("<a href='%s%s' target='Users'>Users list</a>",uri,Resources.user);
			}
		
		},			
		summarySearchable {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				Object value = getValue(protocol);
				return new QueryParam<Boolean>(Boolean.class,value==null?null:(Boolean) value);
			}		
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setSearchable(rs.getBoolean(name()));
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.isSearchable();
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
			@Override
			public String toString() {
				return "Project";
			}
			@Override
			public String getHTMLField(DBProtocol protocol) {
				
				if ((protocol.getProjects()==null) || protocol.getProjects().size()==0) 
					return "";
				else {
					StringBuilder b = new StringBuilder();
					b.append("<select>");
					for (Project project: protocol.getProjects()) {
						b.append("<option value='");
						b.append(project.getResourceURL());
						b.append("'>");
						b.append(project.getTitle());
						b.append("</option>");
					}
					b.append("</select>");
					return b.toString();
				}
			}	
		},
		project_uri {

			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:
						protocol.getProject()==null?null:protocol.getProject().getResourceURL()==null?null:protocol.getProject().getResourceURL().toString();
			}		

			@Override
			public String toString() {
				return "Project URI";
			}
			@Override
			public String getExampleValue(String uri) {
				return String.format("%s%s/G1",uri,Resources.project);
			}
			@Override
			public String getHelp(String uri) {
				return String.format("<a href='%s%s' target='projects'>Projects list</a>",uri,Resources.project);
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
				
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getProjects();
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
			@Override
			public String toString() {
				return "Organisation";
			}
			@Override
			public String getExampleValue(String uri) {
				return String.format("%s%s/G1",uri,Resources.organisation);
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
						dbp.setGroupName(rs.getString("ogroupname"));
					} else if (p instanceof DBOrganisation) {
						((DBOrganisation)p).setTitle(rs.getString(name()));
						((DBOrganisation)p).setGroupName(rs.getString("ogroupname"));
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
		organisation_uri {

			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:
						protocol.getOrganisation()==null?null:
							protocol.getOrganisation().getResourceURL()==null?null:
							protocol.getOrganisation().getResourceURL().toString();
			}		

			@Override
			public String toString() {
				return "Organisation URI";
			}
			@Override
			public String getExampleValue(String uri) {
				return String.format("%s%s/G1",uri,Resources.organisation);
			}
			@Override
			public String getHelp(String uri) {
				return String.format("<a href='%s%s' target='organisations'>Organisations list</a>",uri,Resources.organisation);
			}
		},			
		author_uri {

			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:
						protocol.getResourceURL()==null?"":
						String.format("%s%s",protocol.getResourceURL(),Resources.authors);
			}		

			@Override
			public String toString() {
				return "Author URI";
			}
			@Override
			public String getExampleValue(String uri) {
				return String.format("%s%s/U1",uri,Resources.user);
			}

			@Override
			public String getHelp(String uri) {
				return String.format("<a href='%s%s' target='Users'>Authors list</a>",uri,Resources.user);
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
				protocol.setDocument(new Document(new URL(rs.getString(name()))));
				} catch (Exception x) { protocol.setDocument(null);}
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:protocol.getDocument()==null?null:protocol.getDocument().getResourceURL();
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
		},		
		template {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class, getValue(protocol).toString());
			}			
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
					protocol.setDataTemplate(new Template(new URL(rs.getString(name()))));
				} catch (Exception x) {
					protocol.setDataTemplate(null);
				}
			}
			@Override
			public Object getValue(DBProtocol protocol) {
				return  protocol==null?null:
					protocol.getDataTemplate()==null?null:
					protocol.getDataTemplate().getResourceURL();
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
				return "Data template";
			}
		},
		status {
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<String>(String.class,protocol.getStatus().name());
			}			
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				try {
				protocol.setStatus(Protocol.STATUS.valueOf(rs.getString(name())));
				} catch (Exception x) {protocol.setStatus(STATUS.RESEARCH);}
			}		
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null?null:protocol.getStatus();
			}
			
			public String getCondition() {
				return String.format(" %s = ? ",name());
			}
		},
		created {
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol.getSubmissionDate();
			}
			@Override
			public String toString() {
				return "Submission date";
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Timestamp>(Timestamp.class,new Timestamp(protocol.getSubmissionDate()));
			}
			@Override
			public String getCondition() {
				return String.format(" %s >= ? ",name());
			}
		},			
		updated {
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol.getTimeModified();
			}
			@Override
			public String toString() {
				return "Date updated";
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Timestamp>(Timestamp.class,new Timestamp(protocol.getTimeModified()));
			}
			@Override
			public String getCondition() {
				return String.format(" %s >= ? ",name());
			}
		},		
		published {
			@Override
			public Object getValue(DBProtocol protocol) {
				return protocol==null||protocol.isPublished()==null?null:protocol.isPublished();
			}
			@Override
			public String toString() {
				return "Published status";
			}
			@Override
			public QueryParam getParam(DBProtocol protocol) {
				return new QueryParam<Boolean>(Boolean.class,protocol==null || (protocol.isPublished()==null) ?Boolean.FALSE:new Boolean(protocol.isPublished()));
			}
			@Override
			public String getCondition() {
				return String.format(" %s = ? ",name());
			}
			@Override
			public void setParam(DBProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setPublished(rs.getBoolean(name()));
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
		},		
		allowReadByUser {
			@Override
			public Object getValue(DBProtocol protocol) {
				return "";
			}
			@Override
			public String toString() {
				return "Allow read by a registered user";
			}

		},
		allowReadByGroup {
			@Override
			public Object getValue(DBProtocol protocol) {
				return "";
			}
			@Override
			public String toString() {
				return "Allow read by members of organisaiton or project";
			}
		};
				
		public String getCondition() {
			return String.format(" %s = ? ",name());
		}
	
		public Object getValue(DBProtocol protocol) {
			return protocol.getResourceURL().toString();
		}
		public Class getClassType(DBProtocol protocol) {
			return String.class;
		}
		/**
		 * SQL
		 * @param protocol
		 * @param rs
		 * @throws SQLException
		 */
		public void setParam(DBProtocol protocol,ResultSet rs) throws SQLException {}
		public QueryParam getParam(DBProtocol protocol) {
			return null;
		}	
		/**
		 * HTML
		 * @param protocol
		 * @return
		 */
		public String getHTMLField(DBProtocol protocol) {
			Object value = getValue(protocol);
			return String.format("<input name='%s' type='text' size='60' title='%s' value='%s'>\n",
					name(),getDescription(),value==null?"":value.toString());
		}
		/**
		 * Hints
		 * @return
		 */
		public String getDescription() { return toString();}
		public String getHelp(String uri) {return null;}
		public String getExampleValue(String uri) {return null;}
		
		@Override
		public String toString() {
			String name= name();
			return  String.format("%s%s",
					name.substring(0,1).toUpperCase(),
					name.substring(1).toLowerCase());
		}
	}
	
	protected static String sql = 
		"select idprotocol,version,protocol.title,abstract as anabstract,iduser,summarySearchable," +
		"idorganisation,organisation.name as organisation,organisation.ldapgroup as ogroupname," +
		"filename,keywords,template,updated,status,`created`,published\n" +
		"from protocol join organisation using(idorganisation)\n" +
		"left join keywords using(idprotocol,version) %s %s order by idprotocol,version desc";

	public static final ReadProtocol.fields[] sqlFields = new ReadProtocol.fields[] {
		fields.idprotocol,
		fields.version,
		fields.title,
		fields.anabstract,
		fields.iduser,
		fields.summarySearchable,
		fields.idorganisation,
		fields.organisation,
		fields.filename,
		fields.keywords,
		fields.template,
		fields.status,
		fields.published
		
		//ReadProtocol.fields.accesslevel
	};	
	
	public ReadProtocol(Integer id) {
		this(id,null);
	}
	public ReadProtocol(Integer id, Integer version) {
		super();
		setValue(id==null?null:new DBProtocol(id,version));
		setFieldname(null);
	}
	public ReadProtocol() {
		this(null,null);
	}
		
	public double calculateMetric(DBProtocol object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params =  new ArrayList<QueryParam>();
		if (getValue()!=null) {
			if (getValue().getID()>0) {
				params.add(fields.idprotocol.getParam(getValue()));
				if (getValue().getVersion()>0)
					params.add(fields.version.getParam(getValue()));
				else 
					throw new AmbitException("Protocol version not set!");
			} else if (getValue().getTitle()!=null) {
				params.add(fields.title.getParam(getValue()));
			} else if (getValue().getTimeModified()!=null) {
				params.add(fields.updated.getParam(getValue()));
			}
		}
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) 
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));

		return params;
	}

	public String getSQL() throws AmbitException {
		
		String publishedOnly = getShowUnpublished()?"":" and published=1";
		String byUser = null;
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) byUser = fields.iduser.getCondition();
		
		if (getValue()!=null) {
			if (getValue().getID()>0) {
				if (getValue().getVersion()>0)
					return String.format(sql,"where",
							String.format("%s and %s %s %s %s",
									fields.idprotocol.getCondition(),
									fields.version.getCondition(),
									byUser==null?"":" and ",
									byUser==null?"":byUser,
									publishedOnly));
				else
					throw new AmbitException("Protocol version not set!");
			} else 
				if (getValue().getTitle()!=null)
					return String.format(sql,"where",
										String.format("%s %s %s %s",
												fields.title.getCondition(),
												byUser==null?"":" and ",
												byUser==null?"":byUser,
												publishedOnly));
				else if (getValue().getTimeModified()!=null)
					return String.format(sql,"where",
									String.format("%s %s %s %s",
											fields.updated.getCondition(),
											byUser==null?"":" and ",
											byUser==null?"":byUser,
											publishedOnly));			
		} 
		return getShowUnpublished()?
				String.format(sql,"where",byUser==null?"":byUser):
				String.format(sql,"where",byUser==null?"published=1":String.format("%s %s",byUser,publishedOnly)); //published only

	}

	public DBProtocol getObject(ResultSet rs) throws AmbitException {
		DBProtocol p = null;
		try {
			p =  new DBProtocol();
			for (fields field:sqlFields) try {
				field.setParam(p,rs);
				
			} catch (Exception x) {
				x.printStackTrace();
			}
			try {
				Timestamp ts = rs.getTimestamp(fields.updated.name());
				p.setTimeModified(ts.getTime());
			} catch (Exception x) {}
			try {
				Timestamp ts = rs.getTimestamp(fields.created.name());
				p.setSubmissionDate(ts.getTime());
			} catch (Exception x) {
				x.printStackTrace();
				
			}
			return p;
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			if (p!=null) p.setIdentifier(String.format("SEURAT-Protocol-%d-%d", p.getID(),p.getVersion()));
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All protocols":String.format("Protocol id=P%s",getValue().getID());
	}
	
	public static int[] parseIdentifier(String identifier) throws ResourceException {
		String ids[] = identifier.split("-");
		if ((ids.length!=4) || !identifier.startsWith(Protocol.id_prefix)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid format");
		int[] id = new int[2];
		for (int i=0; i < 2; i++)
			try {
				id[i] = Integer.parseInt(ids[i+2]);
			} catch (NumberFormatException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			}
		return id;
	}
}
