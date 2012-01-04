package org.toxbank.rest.protocol.resource.db;

import java.io.Writer;
import java.net.URL;
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
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.TBHTMLBeauty;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.resource.UserURIReporter;

public class ProtocolQueryHTMLReporter extends QueryHTMLReporter<DBProtocol, IQueryRetrieval<DBProtocol>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	
	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> groupReporter;
	protected UserURIReporter<IQueryRetrieval<DBUser>> userReporter;
	
	protected boolean editable = false;
	public ProtocolQueryHTMLReporter() {
		this(null,true,false);
	}
	public ProtocolQueryHTMLReporter(Request request, boolean collapsed,boolean editable) {
		super(request,collapsed,null);
		this.editable = editable;
		groupReporter = new GroupQueryURIReporter<IQueryRetrieval<IDBGroup>>(request);
		userReporter = new UserURIReporter<IQueryRetrieval<DBUser>>(request);
		
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new ProtocolQueryURIReporter<IQueryRetrieval<DBProtocol>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<DBProtocol> query) {
		super.header(w, query);
		Reference uri = uriReporter.getRequest().getResourceRef().clone();
		uri.setQuery(null);
		
		try {
			//
			if (editable) {
					w.write(String.format("<h3>Create new Protocol %s</h3>",uri.toString().contains("versions")?"version":""));
					StringBuilder curlHint = new StringBuilder();
					curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
					curlHint.append(String.format(" -H 'Content-Type:%s'",MediaType.APPLICATION_WWW_FORM.getName()));
					for (ReadProtocol.fields field : ReadProtocol.entryFields) {
						switch (field) {
						case idprotocol: continue;
						case project_uri: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"Project URI"));
							break;
						}
						case organisation_uri: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"Organisation URI"));
							break;
						}	
						case user_uri: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"Owner User URI"));
							break;
						}							
						case filename: {
							curlHint.append(String.format(" -F '%s=@%s'",field.name(),"FILE_NAME"));
							break;
						}
						default: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"VALUE"));
						}
						}
					}
					curlHint.append(" ");
					curlHint.append(uri);
					output.write("<form method='POST' action='' ENCTYPE=\"multipart/form-data\">");
					w.write("<table width='99%'>\n");
					output.write(String.format("<tr><td>API call</td><td title='How to create a new protocol via ToxBank API (cURL example)'><h5>%s</h5></td></tr>",curlHint));
					printForm(output,uri.toString(),null,true);
					
					
					output.write(
							"<tr><td></td><td><input type='submit' enabled='false' value='Submit'></td></tr>"
							);
					w.write("</table>\n");
					output.write("</form>");
					output.write("<hr>");	
				
			}
	    } catch (Exception x) {}
		
		try {
			if (collapsed) {
				w.write("<h3>Protocols</h3>");
				output.write(String.format("<a href='%s%s'>All protocols</a>",uriReporter.getRequest().getRootRef(),
							Resources.protocol));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));				
				if (!editable)
					output.write(String.format("&nbsp;|&nbsp;<a href='%s%s?new=true'>Create new Protocol</a>",uriReporter.getRequest().getRootRef(),
								Resources.protocol));
				
			} else {
				w.write("<h3>Protocol</h3>");
				
				output.write(String.format("<a href='%s%s'>Back to protocols</a>",
					uriReporter.getRequest().getRootRef(),Resources.protocol));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s%s'>Authors</a>",uri,Resources.authors));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s%s'>Versions</a>",uri,Resources.versions));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s/%s/%s?search=%s'>Access rights</a>",uriReporter.getBaseReference(),AdminResource.resource,OpenSSOPoliciesResource.resource,URLEncoder.encode(uri.toString())));
				output.write(String.format("&nbsp;|&nbsp;<a href='%s%s?new=true'>Create new version</a>",uri,Resources.versions));
				output.write(String.format("&nbsp;|&nbsp;<form action='%s?method=DELETE' method='POST'><INPUT type='submit' value='Delete this protocol'></form>",uri));

			}
			
			String curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s","SUPPORTED-MEDIA-TYPE","TOKEN",uri);

			output.write(String.format("<table><tr><td>API call</td><td title='How to retrieve a protocol via ToxBank API (cURL example)'><h5>%s</h5></td></tr></table>",
					curlHint));
			output.write("<br>Download Protocol metadata in supported Media Types:&nbsp;");
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
				for (ReadProtocol.fields field : ReadProtocol.displayFields) {
					if (ReadProtocol.fields.idprotocol.equals(field)) continue;
					output.write(String.format("<th>%s</th>",field.toString()));
				}

				output.write("</tr>\n");
			} else {
				
			}
		} catch (Exception x) {}
	}
	@Override
	public Object processItem(DBProtocol item) throws AmbitException  {
		try {
			if ((item.getProject()!=null) && (item.getProject().getResourceURL()==null))
				item.getProject().setResourceURL(new URL(groupReporter.getURI((DBProject)item.getProject())));
			if ((item.getOrganisation()!=null) && (item.getOrganisation().getResourceURL()==null))
				item.getOrganisation().setResourceURL(new URL(groupReporter.getURI((DBOrganisation)item.getOrganisation())));
			if ((item.getOwner()!=null) && (item.getOwner().getResourceURL()==null))
				item.getOwner().setResourceURL(new URL(userReporter.getURI((DBUser)item.getOwner())));
							
			String uri = uriReporter.getURI(item);
			
			if (collapsed) 
				printTable(output, uri,item);
			else printForm(output,uri,item,false);

		} catch (Exception x) {
			
		}
		return null;
	}
	
	protected void printForm(Writer output, String uri, DBProtocol protocol, boolean editable) {
		try {
			ReadProtocol.fields[] fields = editable?ReadProtocol.entryFields:ReadProtocol.displayFields;
			for (ReadProtocol.fields field : fields) {
				output.write("<tr bgcolor='FFFFFF'>\n");	
				Object value = null;
				
				try { value = protocol==null?field.getExampleValue(uri):field.getValue(protocol);} catch (Exception x) {}

				if (editable) {
					value = field.getHTMLField(protocol);
				} else 
					if (value==null) value = "";
							
				switch (field) {
				case idprotocol: {
					if (!editable)
						output.write(String.format("<th title='%s'>%s</th><td align='left'><a href='%s'>%s</a></td><td align='left'></td>\n",
							field.name(),	
							field.toString(),
							uri,
							uri));		
					break;
				}	
				case filename: {
					if (editable)
					output.write(String.format("<th title='%s'>%s</th><td align='left'><input type=\"file\" name=\"%s\" title='%s' size=\"60\"></td><td align='left'></td>",
							field.name(),	
							field.toString(),
							field.name(),
							"PDF file")); 					
					else 
						if ((protocol.getDocument()==null) || (protocol.getDocument().getResourceURL()==null))
							output.write(String.format("<th title='%s'>%s</th><td align='left'>N/A</td><td></td>",
									field.name(),	
									field.toString()));							
						else
						output.write(String.format("<th title='%s'>%s</th><td align='left'><a href='%s%s'>Download</a></td><td></td>",
									field.name(),	
									field.toString(),
									uri,
									Resources.document));

					break;
				}	
				case template: {
					if (editable)
					output.write(String.format("<th title='%s'>%s</th><td align='left'><input type=\"file\" name=\"%s\" title='%s' size=\"60\"></td><td align='left'></td>",
							field.name(),	
							field.toString(),
							field.name(),
							"ISA-TAB template")); 					
					else 
						if (protocol.getDataTemplate()==null)
							output.write(String.format("<th title='%s'>%s</th><td align='left'><a href='%s%s?media=text/html'>Create data template</a></td><td></td>",
									field.name(),	
									field.toString(),
									uri,
									Resources.datatemplate		
							));
							
						else
						output.write(String.format("<th title='%s'>%s</th><td align='left'><a href='%s%s?media=text/plain'>Download</a></td><td></td>",
									field.name(),	
									field.toString(),
									uri,
									Resources.datatemplate));

					break;
				}					
				case author_uri: {
					if (!editable) {
						output.write(String.format("<th>%s</th><td><a href='%s%s'>Authors</a></td>",
								field.toString(),uri,Resources.authors));
						break;
					}
				}
				default :  {
					String help = field.getHelp(uriReporter.getRequest().getRootRef().toString());
					output.write(String.format("<th>%s</th><td align='left'>%s</td><td align='left'>%s</td>\n",
									field.toString(),
									value,
									help==null?"":help));
				}
				}
							
				output.write("</tr>\n");				
			}
			output.flush();
		} catch (Exception x) {x.printStackTrace();} 
	}	
	protected void printTable(Writer output, String uri, DBProtocol protocol) {
		try {
			output.write("<tr bgcolor='FFFFFF'>\n");			
			for (ReadProtocol.fields field : ReadProtocol.displayFields) {

				Object value = null; 
				try { value = field.getValue(protocol);} catch (Exception x) {}
				switch (field) {
				case idprotocol: {
					//output.write(String.format("<td><a href='%s'>%s</a></td>",uri,uri));
					break;
				}	
				case identifier: {
					output.write(String.format("<td><a href='%s'>%s</a></td>",uri,value));
					break;
				}
				case filename: {
					if ((protocol.getDocument()==null) || (protocol.getDocument().getResourceURL()==null))
						output.write("<td>N/A</td>");
					else					
						output.write(String.format("<td><a href='%s%s?media=%s'>Download</a></td>",
								uri,Resources.document,Reference.encode(MediaType.APPLICATION_PDF.toString())));
					break;
				}	
				case template: {
					if ((protocol.getDataTemplate()==null) || (protocol.getDataTemplate().getResourceURL()==null))
						output.write("<td>N/A</td>");
					else
					output.write(String.format("<td><a href='%s%s'>Download</a></td>",uri,Resources.datatemplate));
					break;
				}						
				case author_uri: {
					output.write(String.format("<td><a href='%s%s'>Authors</a></td>",uri,Resources.authors));
					break;
				}				
				case user_uri: {
					output.write(String.format("<td><a href='%s'>%s</a></td>",value.toString(),
							protocol.getOwner().getUserName()==null?"Owner":protocol.getOwner().getUserName()));
					break;
				}
				case idorganisation: {
					output.write(String.format("<td>%s</td>",value.toString()));
					break;
				}
				case idproject: {
					output.write(String.format("<td>%s</td>",value.toString()));
					break;
				}
				default:
					output.write(String.format("<td>%s</td>",value==null?"":
								value.toString().length()>40?value.toString().substring(0,40):value.toString()));
				}
			}
			output.write("</tr>\n");
		} catch (Exception x) {} 
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<DBProtocol> query) {
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
