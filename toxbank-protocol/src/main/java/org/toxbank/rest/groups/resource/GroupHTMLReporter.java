package org.toxbank.rest.groups.resource;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryHTMLReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.toxbank.resource.Resources;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.protocol.TBHTMLBeauty;


public abstract class GroupHTMLReporter extends QueryHTMLReporter<IDBGroup, IQueryRetrieval<IDBGroup>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	
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

				
				if (editable) {
					w.write("<h3>Create new entry</h3>");
					StringBuilder curlHint = new StringBuilder();
					curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
					/*
					for (ReadProtocol.fields field : ReadProtocol.fields.values()) {
						switch (field) {
						case idprotocol: continue;
						default: {
							curlHint.append(String.format(" -d '%s=%s'",field.name(),"VALUE"));
						}
						}
						
					}
					*/
					output.write("<form method='POST' action='' ENCTYPE=\"multipart/form-data\">");
					w.write("<table width='99%'>\n");
					output.write(String.format("<tr><td>API call</td><td title='How to create a new %s via ToxBank API (cURL example)'><h5>%s</h5></td></tr>", getTitle(),curlHint));
					//printForm(output,uri.toString(),null,true);
					printTable(output,uri.toString(),null);
					
					
					output.write(String.format("<tr><td></td><td><input type='submit' enabled='false' value='Create new %s'></td></tr>",getTitle()));
					w.write("</table>\n");
					output.write("</form>");
					output.write("<hr>");	
				}
			} else	{
				
				
			}
	    } catch (Exception x) {}
		
		try {
			if (collapsed) {
				w.write(String.format("<h3>%s</h3>",getTitle()));
				output.write(String.format("<a href='%s%s'>All %ss</a>",
							uriReporter.getRequest().getRootRef(),
							getBackLink(),
							getTitle()));
				if (!editable)
					output.write(String.format("&nbsp;<a href='%s%s?new=true'>Create new entry</a>",
							uriReporter.getRequest().getRootRef(),getBackLink()));
			} else {
				
				
				w.write(String.format("<h3>%s</h3>",getTitle()));
				output.write(String.format("<a href='%s%s'>Back</a>",
					uriReporter.getRequest().getRootRef(),getBackLink()));
			}
			
			String curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s","SUPPORTED-MEDIA-TYPE","TOKEN",uri);

			output.write(String.format("<table><tr><td>API call</td><td title='How to retrieve a %s via ToxBank API (cURL example)'><h5>%s</h5></td></tr></table>",
					curlHint,getTitle()));
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
			
			w.write("<table bgcolor='EEEEEE' width='99%'>\n");
			if (collapsed) {
				output.write("<tr bgcolor='FFFFFF' >\n");	
				/*
				for (ReadProtocol.fields field : ReadProtocol.fields.values()) {
					output.write(String.format("<th>%s</th>",field.toString()));
				}
				*/
				output.write(String.format("<th>%s</th>","Template"));
				output.write("</tr>\n");
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
	/*
	protected void printForm(Writer output, String uri, IDBGroup protocol, boolean editable) {
		try {
			ReadProtocol.fields[] fields = editable?entryFields:displayFields;
			for (ReadProtocol.fields field : fields) {
				output.write("<tr bgcolor='FFFFFF'>\n");	
				Object value = field.getValue(protocol);

				if (editable) {
					value = field.getHTMLField(protocol);
				} else 
					if (value==null) value = "";
							
				switch (field) {
				case idprotocol: {
					if (!editable)
						output.write(String.format("<th>%s</th><td align='left'><a href='%s'>%s</a></td>\n",
							field.toString(),
							uri,
							uri));		
					break;
				}	
				case filename: {
					if (editable)
					output.write(String.format("<th>%s</th><td align='left'><input type=\"file\" name=\"%s\" title='%s' size=\"60\"></td>",
							field.toString(),
							field.name(),
							"PDF file")); 					
					else 
						output.write(String.format("<th>%s</th><td align='left'><a href='%s/file'>Download</a></td>",field.toString(),uri));

					break;
				}	
				default :
					output.write(String.format("<th>%s</th><td align='left'>%s</td>\n",
						field.toString(),value));
				}
							
				output.write("</tr>\n");				
			}
			output.write("<tr bgcolor='FFFFFF'>\n");
			output.write(String.format("<th>%s</th><td align='left'><a href='%s%s'>Data template</a></td>","Data template",uri,Resources.datatemplate));
			output.write("</tr>\n");
			output.flush();
		} catch (Exception x) {x.printStackTrace();} 
	}	
	*/
	protected void printTable(Writer output, String uri, IDBGroup item) {
		try {
			output.write("<tr bgcolor='FFFFFF'>\n");		
			output.write(String.format("<td><a href='%s'>%s</a></td>",uri,uri));
			output.write(String.format("<td>%s</td>",item.getName()));
			output.write(String.format("<td>%s</td>",item.getLdapgroup()));
			output.write("</tr>\n");
		} catch (Exception x) {} 
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<IDBGroup> query) {
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
