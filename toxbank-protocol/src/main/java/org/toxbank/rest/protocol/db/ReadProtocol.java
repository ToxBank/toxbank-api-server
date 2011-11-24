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

import org.toxbank.resource.IProtocol;
import org.toxbank.rest.protocol.MyProtocol;
import org.toxbank.rest.protocol.metadata.Author;
import org.toxbank.rest.protocol.metadata.Document;
import org.toxbank.rest.protocol.metadata.Project;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadProtocol  extends AbstractQuery<String, IProtocol, EQCondition, IProtocol>  implements IQueryRetrieval<IProtocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;
	public enum fields {
		idprotocol {
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setID(rs.getInt(ordinal()+1));
			}
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<Integer>(Integer.class, (Integer)getValue(protocol));
			}	
			@Override
			public Object getValue(IProtocol protocol) {
				return protocol==null?null:protocol.getID();
			}
			@Override
			public String toString() {
				return "URI";
			}
		},
		identifier {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<String>(String.class, (String)getValue(protocol));
			}
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setIdentifier(rs.getString(ordinal()+1));
			}
			@Override
			public Object getValue(IProtocol protocol) {
				return protocol==null?null:protocol.getIdentifier();
			}
		},
		title {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<String>(String.class, (String)getValue(protocol));
			}			
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setTitle(rs.getString(ordinal()+1));
			}		
			@Override
			public Object getValue(IProtocol protocol) {
				return protocol==null?null:protocol.getTitle();
			}
		},
		anabstract {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<String>(String.class, (String) getValue(protocol));
			}	
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setAbstract(rs.getString(ordinal()+1));
			}		
			@Override
			public Object getValue(IProtocol protocol) {
				return protocol==null?null:protocol.getAbstract();
			}
			public String getHTMLField(IProtocol protocol) {
				Object value = getValue(protocol);
				return String.format("<textarea name='%s' cols='40' rows='5' title='%s'>%s</textarea>\n",
						name(),getDescription(),value==null?"":value.toString());
			}			
			@Override
			public String toString() {
				return "Abstract";
			}
		},
		author {

			@Override
			public QueryParam getParam(IProtocol protocol) {
				Object project = getValue(protocol);
				return new QueryParam<String>(String.class, project==null?null:project.toString());
			}
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setAuthor(new Author(rs.getString(ordinal()+1)));
			}		
			@Override
			public Object getValue(IProtocol protocol) {
				return  protocol==null?null:protocol.getAuthor();
			}			
		},			
		summarySearchable {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<Boolean>(Boolean.class, (Boolean) getValue(protocol));
			}		
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setSummarySearchable(rs.getBoolean(ordinal()+1));
			}
			@Override
			public Object getValue(IProtocol protocol) {
				return protocol==null?null:protocol.isSummarySearchable();
			}
			@Override
			public Class getClassType(IProtocol protocol) {
				return Boolean.class;
			}
			public String getHTMLField(IProtocol protocol) {
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
		project {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				Object project = getValue(protocol);
				return new QueryParam<String>(String.class, project==null?null:project.toString());
			}
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				protocol.setProject(new Project(rs.getString(ordinal()+1)));
			}		
			@Override
			public Object getValue(IProtocol protocol) {
				return  protocol==null?null:protocol.getProject();
			}			
		},
		filename {
			@Override
			public QueryParam getParam(IProtocol protocol) {
				return new QueryParam<String>(String.class, getValue(protocol).toString());
			}			
			@Override
			public void setParam(IProtocol protocol, ResultSet rs) throws SQLException {
				try {
				protocol.setDocument(new Document(new URI(rs.getString(ordinal()+1))));
				} catch (Exception x) {throw new SQLException(x); }
			}
			@Override
			public Object getValue(IProtocol protocol) {
				return  protocol==null?null:protocol.getDocument().getURI();
			}				
			public String getHTMLField(IProtocol protocol) {
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
		public QueryParam getParam(IProtocol protocol) {
			return new QueryParam<String>(String.class,  getValue(protocol).toString());
		}
		public Object getValue(IProtocol protocol) {
			return protocol.getDocument().getURI().toString();
		}
		public Class getClassType(IProtocol protocol) {
			return String.class;
		}
		public void setParam(IProtocol protocol,ResultSet rs) throws SQLException {}
		
		public String getHTMLField(IProtocol protocol) {
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
		"select idprotocol,identifier,title,abstract as anabstract,author,summarySearchable,project,filename from protocol %s %s";


	public ReadProtocol(Integer id) {
		super();
		setValue(id==null?null:new MyProtocol(id));
	}
	public ReadProtocol() {
		this(null);
	}
		
	public double calculateMetric(IProtocol object) {
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

	public IProtocol getObject(ResultSet rs) throws AmbitException {
		try {
			IProtocol p =  new MyProtocol();
			for (fields field:fields.values()) try {
				field.setParam(p,rs);
				
			} catch (Exception x) {
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
