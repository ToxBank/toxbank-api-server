package org.toxbank.rest.protocol;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.toxbank.resource.IProtocol;

public class ProtocolHTMLReporter  extends ProtocolURIReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	protected HTMLBeauty htmlBeauty;
	
	public HTMLBeauty getHtmlBeauty() {
		return htmlBeauty;
	}
	public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
		this.htmlBeauty = htmlBeauty;
	}
	public ProtocolHTMLReporter(Request request,ResourceDoc doc) {
		super(request,doc);
	}
	
	@Override
	public void header(Writer output, Iterator<IProtocol> query) {
		try {
			if (htmlBeauty==null) htmlBeauty = new HTMLBeauty();
			
			htmlBeauty.writeHTMLHeader(output, "Protocol", getRequest(),
					getDocumentation());//,"<meta http-equiv=\"refresh\" content=\"10\">");
			
		
		} catch (Exception x) {
			
		}
	}
	public void processItem(IProtocol item, Writer output) {
		try {
			String t = super.getURI(item);
			output.write(String.format("<a href='%s'>%s</a><br>", t,item.toString()));
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	@Override
	public void footer(Writer output, Iterator<IProtocol> query) {
		try {
			if (htmlBeauty == null) htmlBeauty = new HTMLBeauty();
			htmlBeauty.writeHTMLFooter(output, "", getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}

