package org.toxbank.rest.protocol;

import java.io.IOException;
import java.io.Writer;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.toxbank.client.Resources;

import org.restlet.Request;
import org.restlet.data.Reference;

public class TBHTMLBeauty extends HTMLBeauty {
	public TBHTMLBeauty() {
		super();

	};

		@Override
		protected String getHomeURI() {
			return "http://http://toxbank.net/";
		}
		@Override
		protected String getLogoURI(String root) {
			return "http://toxbank.net/sites/default/files/TB%20banner.jpg";
		}
		@Override
		public String getTitle() {
			return "ToxBank protocol web service";
		}
		public void writeTopLinks(Writer w,String title,Request request,String meta,ResourceDoc doc, Reference baseReference) throws IOException {
			w.write(String.format("<a href='%s%s'>Protocols</a>&nbsp;",baseReference,Resources.protocol));
			w.write(String.format("<a href='%s%s'>Organisations</a>&nbsp;",baseReference,Resources.organisation));
			w.write(String.format("<a href='%s%s'>Projects</a>&nbsp;",baseReference,Resources.project));
			w.write(String.format("<a href='%s%s'>Users</a>&nbsp;",baseReference,Resources.user));
		}
		private final static String[] css = new String[] {
			"<link href=\"%s/style/jquery-ui-1.8.18.custom.css\" rel=\"stylesheet\" type=\"text/css\">\n",
			"<link href=\"%s/style/jquery.dataTables.css\" rel=\"stylesheet\" type=\"text/css\">\n",
		};
		private final static String[] js = new String[] {
			"<script type='text/javascript' src='%s/jquery/jquery-1.7.1.min.js'></script>\n",
			"<script type='text/javascript' src='%s/jquery/jquery-ui-1.8.18.custom.min.js'></script>\n",
			"<script type='text/javascript' charset='utf8' src='%s/jquery/jquery.dataTables-1.9.0.min.js'></script>\n",
			"<script type='text/javascript'>$(document).ready(function() { $('.datatable').dataTable({ \"bJQueryUI\": true});} )</script>\n"
		};
		@Override
		public void writeTopHeader(Writer w,String title,Request request,String meta,ResourceDoc doc) throws IOException {

			
			Reference baseReference = request==null?null:request.getRootRef();
			w.write(
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				);
			
			w.write(String.format("<html %s %s %s>",
					"xmlns=\"http://www.w3.org/1999/xhtml\"",
					"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"",
					"xmlns:ot=\"http://opentox.org/api/1.1/\"")
					);
			
			w.write(String.format("<head> <meta property=\"dc:creator\" content=\"%s\"/> <meta property=\"dc:title\" content=\"%s\"/>",
					request.getResourceRef(),
					title
					)
					);
			
			Reference ref = request.getResourceRef().clone();
			ref.addQueryParameter("media", Reference.encode("application/rdf+xml"));
			w.write(String.format("<link rel=\"meta\" type=\"application/rdf+xml\" title=\"%s\" href=\"%s\"/>",
					title,
					ref
					)); 
			
			w.write(String.format("<link rel=\"primarytopic\" type=\"application/rdf+xml\" href=\"%s\"/>",
					ref
					)); 		
			w.write(String.format("<title>%s</title>",title));

			for (String s : css)
				w.write(String.format(s,request.getRootRef().toString()));
			for (String s : js)
				w.write(String.format(s,request.getRootRef().toString()));
			w.write(meta);
					
			w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
			w.write("<meta name=\"robots\" content=\"index,nofollow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,noFOLLOW\">");
			w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

			w.write("</head>\n");
			w.write("<body>");
			w.write("\n");
			w.write("<div style= \"width: 100%; background-color: #516373;");
			w.write("border: 1px solid #333; padding: 0px; margin: 0px auto;\">");
			w.write("<div class=\"spacer\"></div>");
			
			String top;
			if (doc!= null) {
				top = String.format("&nbsp;<a style=\"color:#99CC00\" href='%s' target='_Ontology' title='Opentox %s (%s), describes representation of OpenTox REST resources'>OpenTox %s</a>&nbsp;",
						doc.getPrimaryTopic(),
						doc.getResource(),doc.getPrimaryTopic(),doc.getResource());
				top += String.format("<a style=\"color:#99CC00\" href='%s' target='_API' title='REST API documentation'>REST API</a>&nbsp;",doc.getPrimaryDoc());
			} else top = "";

			w.write(String.format("<div class=\"row\"><span class=\"left\">&nbsp;%s",top));
			w.write("</span>");
		
			
			w.write(String.format("	<span class=\"right\">%s&nbsp;<a style=\"color:#99CC00\" href='%s/%s'>%s</a>",
					top,
					baseReference.toString(),
					getLoginLink(),
					request.getClientInfo().getUser()==null?"Login":"My account"));
			
			
			w.write("</span></div>");
			w.write("	<div class=\"spacer\"></div>");
			w.write("</div>");
			w.write("<div>");		
			writeTopLinks(w, title, request, meta, doc, baseReference);
			w.write("</div>");

			w.write("\n<div id=\"targetDiv\"></div>\n");
			w.write("\n<div id=\"statusDiv\"></div>\n");

		}
		@Override
		public String getLoginLink() {
			return "Login";
		}
}
