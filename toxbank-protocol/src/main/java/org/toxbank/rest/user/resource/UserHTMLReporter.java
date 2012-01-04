package org.toxbank.rest.user.resource;

import java.io.Writer;
import java.net.URLEncoder;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.aa.resource.AdminResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.toxbank.client.Resources;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.toxbank.rest.protocol.TBHTMLBeauty;
import org.toxbank.rest.user.DBUser;

public class UserHTMLReporter extends QueryHTMLReporter<DBUser, IQueryRetrieval<DBUser>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	DBUser.fields[] entryFields = DBUser.fields.values();
	DBUser.fields[] displayFields = DBUser.fields.values();
	
	protected boolean editable = false;
	public UserHTMLReporter() {
		this(null,true,false);
	}
	public UserHTMLReporter(Request baseRef, boolean collapsed,boolean editable) {
		super(baseRef,collapsed,null);
		this.editable = editable;
		
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new UserURIReporter<IQueryRetrieval<DBUser>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<DBUser> query) {
		super.header(w, query);
		Reference uri = uriReporter.getRequest().getResourceRef().clone();
		uri.setQuery(null);
		
		try {
			//
			if (collapsed) { 
			
				if (editable) {
					w.write("<h3>Create new User</h3>");
					StringBuilder curlHint = new StringBuilder();
					curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
					curlHint.append(String.format(" -H 'Content-Type:%s'",MediaType.APPLICATION_WWW_FORM.getName()));
					for (DBUser.fields field : DBUser.fields.values()) {
						switch (field) {
						case iduser: continue;
						default: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"VALUE"));
						}
						}
						
					}
					
					output.write("<form method='POST' action=''>");
					w.write("<table width='99%'>\n");
					output.write(String.format("<tr><td>API call</td><td title='How to create a new user via ToxBank API (cURL example)'><h5>%s</h5></td></tr>",curlHint));
					printForm(output,uri.toString(),null,true);
					
					
					output.write("<tr><td></td><td><input type='submit' enabled='false' value='Create new User'></td></tr>");
					w.write("</table>\n");
					output.write("</form>");
					output.write("<hr>");	
				}
			} else	{
				
				
			}
	    } catch (Exception x) {}
		
		try {
			if (collapsed) {
				w.write("<h3>Users</h3>");
				output.write(String.format("<a href='%s%s'>All users</a>",uriReporter.getRequest().getRootRef(),
							Resources.user));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));
				if (!editable)
					output.write(String.format("&nbsp;|<a href='%s%s?new=true'>Create new User</a>",uriReporter.getRequest().getRootRef(),
								Resources.user));
			} else {
				w.write("<h3>User</h3>");
				output.write(String.format("<a href='%s%s'>Back to users</a>",
								uriReporter.getRequest().getRootRef(),Resources.user));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));				
				output.write(String.format("&nbsp;|<a href='%s%s'>Protocols</a>",
											uri,Resources.protocol));				
			}
			
			String curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s","SUPPORTED-MEDIA-TYPE","TOKEN",uri);

			output.write(String.format("<table><tr><td>API call</td><td title='How to retrieve an user via ToxBank API (cURL example)'><h5>%s</h5></td></tr></table>",
					curlHint));
			output.write("<br>Download User metadata in supported Media Types:&nbsp;");
			//nmimes
			String paging = "page=0&pagesize=10";
			MediaType[] mimes = {
					MediaType.TEXT_URI_LIST,
					MediaType.APPLICATION_RDF_XML,
					MediaType.TEXT_RDF_N3,
					MediaType.APPLICATION_JSON,
					MediaType.TEXT_CSV
					};
			
			String[] image = {
					"link.png",
					"rdf.gif",
					"rdf.gif",
					"json.png",
					"excel.png"
			};	
			

			
			for (int i=0;i<mimes.length;i++) {
				MediaType mime = mimes[i];
				output.write("&nbsp;");
				
				
				curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s",mime,
						"TOKEN",
						uri);

				output.write(String.format(
						"\n<a href=\"%s%s?media=%s&%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>\n",
						uriReporter.getRequest().getResourceRef(),
						"",
						Reference.encode(mime.toString()),
						paging,
						getUriReporter().getBaseReference().toString(),
						image[i],
						mime,
						curlHint
						));	
			}				
			//tables
			
			w.write("<table bgcolor='EEEEEE' width='99%'>\n");
			if (collapsed) {
				output.write("<tr bgcolor='FFFFFF' >\n");	
				for (DBUser.fields field : DBUser.fields.values()) {
					output.write(String.format("<th>%s</th>",field.toString()));
				}
				output.write("</tr>\n");
			} else {
				
			}
		} catch (Exception x) {}
	}
	@Override
	public Object processItem(DBUser protocol) throws AmbitException  {
		try {
			String uri = uriReporter.getURI(protocol);
			if (collapsed) 
				printTable(output, uri,protocol);
			else printForm(output,uri,protocol,false);

		} catch (Exception x) {
			
		}
		return null;
	}
	
	protected void printForm(Writer output, String uri, DBUser user, boolean editable) {
		try {
			DBUser.fields[] fields = editable?entryFields:displayFields;
			for (DBUser.fields field : fields) {
				output.write("<tr bgcolor='FFFFFF'>\n");	
				Object value = user==null?null:field.getValue(user);

				if (editable) {
					value = field.getHTMLField(user);
				} else 
					if (value==null) value = "";
							
				switch (field) {
				case iduser: {
					if (!editable)
						output.write(String.format("<th>%s</th><td align='left'><a href='%s'>%s</a></td>\n",
							field.toString(),
							uri,
							uri));		
					break;
				}	
				default :
					output.write(String.format("<th>%s</th><td align='left'>%s</td>\n",
						field.toString(),value));
				}
							
				output.write("</tr>\n");				
			}
			output.write("<tr bgcolor='FFFFFF'>\n");
			output.write("</tr>\n");
			output.flush();
		} catch (Exception x) {x.printStackTrace();} 
	}	
	protected void printTable(Writer output, String uri, DBUser user) {
		try {
			output.write("<tr bgcolor='FFFFFF'>\n");			
			for (DBUser.fields field : DBUser.fields.values()) {

				Object value = field.getValue(user);
				switch (field) {
				case iduser: {
					output.write(String.format("<td><a href='%s'>%s</a></td>",uri,uri));
					break;
				}	
		
				default:
					output.write(String.format("<td>%s</td>",value==null?"":value.toString().length()>40?value.toString().substring(0,40):value.toString()));
				}
			}
			output.write("</tr>\n");
		} catch (Exception x) {} 
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<DBUser> query) {
		try {
			output.write("</table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	protected HTMLBeauty createHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
