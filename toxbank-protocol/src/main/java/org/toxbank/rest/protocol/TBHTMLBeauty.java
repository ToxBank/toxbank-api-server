package org.toxbank.rest.protocol;

import java.io.IOException;
import java.io.Writer;

import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.resource.TaskResource;
import net.toxbank.client.Resources;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.toxbank.rest.user.alerts.notification.NotificationResource;

public class TBHTMLBeauty extends HTMLBeauty {
	public TBHTMLBeauty() {
		super();

	};

		@Override
		protected String getHomeURI() {
			return "http://toxbank.net/";
		}
		@Override
		protected String getLogoURI(String root) {
			return "http://toxbank.github.com/toxbank-api-server/images/toxbank_rgb-72.png";
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
			w.write(String.format("<a href='%s%s'>Jobs</a>&nbsp;",baseReference,TaskResource.resource));
			w.write(String.format("<a href='%s%s' title='Notifications'>Alerts</a>&nbsp;",baseReference,NotificationResource.resourceKey));
		}
		private final static String[] css = new String[] {
			"<link href=\"%s/style/jquery-ui-1.9.1.custom.min.css\" rel=\"stylesheet\" type=\"text/css\">\n",
			"<link href=\"%s/style/jquery.dataTables.css\" rel=\"stylesheet\" type=\"text/css\">\n",
			"<link href=\"%s/style/layout-default-latest.css\" rel=\"stylesheet\" type=\"text/css\">\n",
			"<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">"
		};
		private final static String[] js = new String[] {
			"<script type='text/javascript' src='%s/jquery/jquery-1.8.2.js'></script>\n",
			"<script type='text/javascript' src='%s/jquery/jquery-ui-1.9.1.custom.min.js'></script>\n",
			"<script type='text/javascript' charset='utf8' src='%s/jquery/jquery.dataTables-1.9.0.min.js'></script>\n",
			"<script type='text/javascript' charset='utf8' src='%s/jquery/jquery.layout-latest.min.js'></script>\n",
			"<script type='text/javascript'>$(document).ready(function() { $('.datatable').dataTable({ \"bJQueryUI\": true});} )</script>\n",
			"<script type='text/javascript'>$(document).ready(function() { $('body').layout({south__size:32,south__spacing_open:0,north__size:66,north__spacing_open:0});} )</script>\n"
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
					
			w.write("<meta name=\"robots\" content=\"index,nofollow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,noFOLLOW\">");
			w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

			w.write("</head>\n");
			w.write("<body>\n");
			
			w.write("<div class='ui-layout-north ui-widget-header' style=\"padding: 1 1 1 1;\">\n");
			
			w.write("<span style='float:left;'>");
			w.write(String.format("<a href=\"%s\"><img src='%s' alt='%s' title='%s' border='0'></a>\n",
					getHomeURI(),getLogoURI(baseReference.toString()),getTitle(),baseReference));
			w.write("</span>\n");
			w.write("<span style='position:relative;' class='ui-button'>");
			writeTopLinks(w, title, request, meta, doc, baseReference);
			w.write("</span>\n");
			
			w.write(String.format("<span style='float:right;' class='ui-button'><a href='%s/%s'>%s</a></span>\n",
					baseReference.toString(),
					getLoginLink(),
					request.getClientInfo().getUser()==null?"Login":"My account"));
			w.write("<span style='float:right;'>");
			writeSearchForm(w, title, request, meta);
			w.write("</span>\n");
			w.write("</div>\n");
			
		}
		
		@Override
		public void writeSearchForm(Writer w, String title, Request request,
				String meta, Method method, Form params) throws IOException {
			String query_smiles = "";
			try {
				Form form = getParams(params,request);
				if ((form != null) && (form.size()>0))
					query_smiles = form.getFirstValue(AbstractResource.search_param);
				else query_smiles = null;
			} catch (Exception x) {
				query_smiles = "";
			}
			w.write(String.format("<br><form action='' method='%s'>\n",method));
			w.write(String.format("<input name='%s' size='40' value='%s' required/>\n",AbstractResource.search_param,query_smiles==null?"":query_smiles));
			w.write("<input type='submit' class='ui-button' value='Search'/>");
			//w.write(baseReference.toString());
			w.write("</form>\n");

		}
		
		@Override
		public void writeHTMLHeader(Writer w, String title, Request request,
				String meta, ResourceDoc doc) throws IOException {

			writeTopHeader(w, title, request, meta,doc);
			/*
			w.write("<div class=\"ui-layout-west\" style=\"padding: 1 1 1 1;\">\n");
			writeSearchForm(w, title, request, meta);
			w.write("</div>\n");
			*/
			w.write("\n<div class=\"ui-layout-center\" style=\"clear:both;padding: 5 5 5 5;\">\n");
		}
		@Override
		public void writeHTMLFooter(Writer output, String title, Request request) 	throws IOException {
			Reference baseReference = request==null?null:request.getRootRef();
			output.write("</div>\n");
			output.write("<div class='ui-layout-south footer ui-widget-header' style='padding: 1 1 1 1'>\n");
			output.write("<span style='float:right;' class='ui-button'><a href='http://www.ideaconsult.net'>Developed by Ideaconsult Ltd. (2011-2013)</a></span>"); 
			output.write("</div>\n");
			output.write("\n");
			output.write(jsGoogleAnalytics()==null?"":jsGoogleAnalytics());
			output.write("</body>");
			output.write("</html>");
		}
		@Override
		public String getLoginLink() {
			return "Login";
		}
}
