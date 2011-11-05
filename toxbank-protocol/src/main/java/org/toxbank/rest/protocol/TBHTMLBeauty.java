package org.toxbank.rest.protocol;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.toxbank.resource.IProtocol;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.resource.TaskResource;

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
			w.write(String.format("<a href='%s%s'>Protocols</a>&nbsp;",baseReference,IProtocol.resource));
		}
	
		
}
