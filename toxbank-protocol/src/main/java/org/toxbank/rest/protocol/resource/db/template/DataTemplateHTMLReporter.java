package org.toxbank.rest.protocol.resource.db.template;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.toxbank.client.Resources;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.TBHTMLBeauty;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol.fields;
import org.toxbank.rest.protocol.resource.db.ProtocolQueryURIReporter;

public class DataTemplateHTMLReporter extends QueryHTMLReporter<DBProtocol, IQueryRetrieval<DBProtocol>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	protected boolean editable = false;
	public DataTemplateHTMLReporter() {
		this(null,true,false);
	}
	public DataTemplateHTMLReporter(Request baseRef, boolean collapsed,boolean editable) {
		super(baseRef,collapsed,null);
		this.editable = editable;
		
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new ProtocolQueryURIReporter<IQueryRetrieval<DBProtocol>>(request,Resources.datatemplate);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<DBProtocol> query) {
		super.header(w, query);
		Reference uri = uriReporter.getRequest().getResourceRef().clone();
		uri.setQuery(null);
		
		try {
			//
			if (collapsed) { 

				
				if (editable) {
					w.write("<h3>Create new template</h3>");
					StringBuilder curlHint = new StringBuilder();
					curlHint.append("curl -X POST -H 'subjectid:TOKEN'");
					curlHint.append(String.format(" -F '@%s=%s'",ReadProtocol.fields.template.name(),"FILE"));
				}
			} else	{
				
				
			}
	    } catch (Exception x) {}
		
		try {
			if (collapsed) {
				w.write("<h3>Protocols</h3>");
				output.write(String.format("<a href='%s%s'>All protocols</a>",uriReporter.getRequest().getRootRef(),
							Resources.protocol));
				if (!editable)
					output.write(String.format("&nbsp;<a href='%s%s?new=true'>Create new Protocol</a>",uriReporter.getRequest().getRootRef(),
							Resources.protocol));
			} else {
				w.write("<h3>Protocol</h3>");
				output.write(String.format("<a href='%s%s'>Back to protocols</a>",
					uriReporter.getRequest().getRootRef(),Resources.protocol));
			}
			
			String curlHint = String.format("curl -X GET -H 'Accept:%s' -H 'subjectid:%s' %s","SUPPORTED-MEDIA-TYPE","TOKEN",uri);

			output.write(String.format("<table><tr><td>API call</td><td title='How to retrieve a protocol data template via ToxBank API (cURL example)'><h5>%s</h5></td></tr></table>",
					curlHint));
			output.write("<br>Download Protocol data template in supported Media Types:&nbsp;");
			//nmimes
			String paging = "page=0&pagesize=10";
			MediaType[] mimes = {
					MediaType.TEXT_URI_LIST,
					MediaType.APPLICATION_RDF_XML,
					MediaType.TEXT_RDF_N3,
					MediaType.APPLICATION_PDF,
					MediaType.TEXT_CSV
					};
			
			String[] image = {
					"link.png",
					"rdf.gif",
					"rdf.gif",
					"pdf.png",
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

			
		} catch (Exception x) {}
	}
	@Override
	public Object processItem(DBProtocol protocol) throws AmbitException  {
		try {
			String uri = uriReporter.getURI(protocol);
			Object value =  fields.identifier.getValue(protocol);
			output.write(String.format("<tr bgcolor='FFFFFF'><th width='25%%'>Protocol %s</th><td><a href='%s'>%s</a></td></tr>",
					fields.identifier.toString(),uri.replace(Resources.datatemplate, ""),value));
			output.write(String.format("<tr bgcolor='FFFFFF'><th width='25%%'>%s</th><td>%s</td></tr>",
					"Data template",
					protocol.getDataTemplate()==null?"N/A":
					String.format("<a href='%s?media=text/plain'>Download</a>",uri))
					);
			
			output.write("<form method='POST' action='' ENCTYPE=\"multipart/form-data\">");
			output.write(String.format("<tr bgcolor='FFFFFF'><th title='%s'>%s</th><td align='left'><input type=\"file\" name=\"%s\" title='%s' size=\"60\"></td></tr>",
					ReadProtocol.fields.template.name(),	
					ReadProtocol.fields.template.toString(),
					ReadProtocol.fields.template.name(),
					"ISA-TAB template")); 				
			output.write("<tr bgcolor='FFFFFF'><th></th><td><input type='submit' enabled='false' value='Submit'></td></tr>");
			output.write("</form>");
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
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
