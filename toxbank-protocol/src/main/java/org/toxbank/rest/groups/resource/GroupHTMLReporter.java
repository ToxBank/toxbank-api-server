package org.toxbank.rest.groups.resource;

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
import org.toxbank.rest.groups.DBGroup;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.protocol.TBHTMLBeauty;
import org.toxbank.rest.user.DBUser;


public abstract class GroupHTMLReporter extends QueryHTMLReporter<IDBGroup, IQueryRetrieval<IDBGroup>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	protected DBUser user = null;
	
	protected boolean editable = false;
	public GroupHTMLReporter() {
		this(null,true,false);
	}
	public GroupHTMLReporter(Request baseRef, boolean collapsed,boolean editable) {
		super(baseRef,collapsed,null);
		this.editable = editable;
		
	}
	
	
	public abstract String getBackLink();
	public abstract String getTitle();
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new GroupQueryURIReporter<IQueryRetrieval<IDBGroup>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<IDBGroup> query) {
		super.header(w, query);
		Reference uri = uriReporter.getRequest().getResourceRef().clone();
		uri.setQuery(null);
		
		try {
			//
			if (collapsed) { 

				user = (DBUser) ((ReadGroup)query).getFieldname();
				if (editable) {
					w.write("<h3>Create new entry</h3>");
					StringBuilder curlHint = new StringBuilder();
					if (user==null) {
						curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
						curlHint.append(String.format(" -H 'Content-Type:%s'",MediaType.APPLICATION_WWW_FORM.getName()));
						curlHint.append(String.format(" -d '%s=%s'","title","MANDATORY_VALUE"));
						curlHint.append(String.format(" -d '%s=%s' ","ldapgroup","OPTIONAL_VALUE"));
						curlHint.append(uri);
					} else {
						
						curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
						curlHint.append(String.format(" -H 'Content-Type:%s' ",MediaType.APPLICATION_WWW_FORM.getName()));
						
						curlHint.append(String.format(" -d '%s_uri=%s_URI' ",getTitle().toLowerCase(),getTitle()));
						curlHint.append(uri);
					}
					if(user==null)
						output.write(String.format("<form method='POST' action='%s%s'\">",
											user==null?"":"/",user==null?"":getTitle().toLowerCase()));
					else	
						output.write(String.format("<form method='POST' action='%s%s/U%d%s'\">",
								uriReporter.getRequest().getRootRef(),Resources.user,user.getID(),getBackLink()));		
					w.write("<table width='99%'>\n");
					output.write(String.format("<tr><td>API call</td><td title='How to create a new %s via ToxBank API (cURL example)'><h5>%s</h5></td></tr>", 
									getTitle(),curlHint));
					printForm(output,uri.toString(),null,true);
					
					output.write(String.format("<tr><td></td><td><input type='submit' enabled='false' title='%s' value='Submit'></td></tr>",getTitle()));
					w.write("</table>\n");
					output.write("</form>");
					output.write("<hr>");	
				}
			} else	{
				
				
			}
	    } catch (Exception x) {
	    	x.printStackTrace();
	    }
		
		try {
			if (collapsed) {
				w.write(String.format("<h3>%s</h3>",getTitle()));
				output.write(String.format("<a href='%s%s'>All %ss</a>",
							uriReporter.getRequest().getRootRef(),
							getBackLink(),
							getTitle()));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));
				if (!editable) {
					if (user==null)
						output.write(String.format("&nbsp;|<a href='%s%s?new=true'>Create new entry</a>",uriReporter.getRequest().getRootRef(),getBackLink()));
					else 
						output.write(String.format("&nbsp;|<a href='%s%s/U%d%s?new=true'>Create new entry</a>",
															uriReporter.getRequest().getRootRef(),Resources.user,user.getID(),getBackLink()));
				}
			} else {
				
				w.write(String.format("<h3>%s</h3>",getTitle()));
				output.write(String.format("<a href='%s%s'>Back</a>",
					uriReporter.getRequest().getRootRef(),getBackLink()));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));
			}
			
			String curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s","SUPPORTED-MEDIA-TYPE","TOKEN",uri);

			output.write(String.format("<table><tr><td>API call</td><td title='How to retrieve a %s via ToxBank API (cURL example)'><h5>%s</h5></td></tr></table>",
					getTitle(),curlHint,getTitle()));
			output.write("<br>Download in supported Media Types:&nbsp;");
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
			
			w.write("<table class='datatable' width='99%'>\n");
			if (collapsed) {
				output.write("<thead bgcolor='FFFFFF' >\n");	
				for (DBGroup.fields field : DBGroup.fields.values()) {
					output.write(String.format("<th>%s</th>",field.toString()));
				}
				output.write("</thead>\n");
				output.write("<tbody>\n");
			} else {
				
			}
		} catch (Exception x) {}
	}
	@Override
	public Object processItem(IDBGroup protocol) throws AmbitException  {
		try {
			String uri = uriReporter.getURI(protocol);
			if (collapsed) 
				printTable(output, uri,protocol);
			else  printTable(output, uri,protocol);// printForm(output,uri,protocol,false);

		} catch (Exception x) {
			
		}
		return null;
	}

	protected void printForm(Writer output, String uri, IDBGroup group, boolean editable) {
		try {
			if (user!=null) {
				output.write("<tr bgcolor='FFFFFF'>\n");	
				output.write(String.format("<th>%s URI</th><td align='left'><input name='%s_uri' value='' size='80'></td>\n",
									getTitle(),getTitle().toLowerCase()));
				output.write("</tr>\n");			
			} else
			for (DBGroup.fields field : DBGroup.fields.values()) {
				output.write("<tr bgcolor='FFFFFF'>\n");	
				Object value = field.getValue(group);

				if (editable) {
					value = field.getHTMLField(group);
				} else 
					if (value==null) value = "";
							
				switch (field) {
				case idgroup: {
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
			output.flush();
		} catch (Exception x) {x.printStackTrace();} 
	}	

	protected void printTable(Writer output, String uri, IDBGroup item) {
		try {
			output.write("<tr bgcolor='FFFFFF'>\n");		
			output.write(String.format("<td><a href='%s'>%s</a></td>",uri,uri));
			output.write(String.format("<td>%s</td>",item.getTitle()));
			output.write(String.format("<td>%s</td>",item.getGroupName()==null?"":item.getGroupName()));
			output.write("</tr>\n");
		} catch (Exception x) {} 
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<IDBGroup> query) {
		try {
			output.write("</tbody>");
			output.write("</table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	protected HTMLBeauty createHTMLBeauty() {
		return new TBHTMLBeauty();
	}
}
