package org.toxbank.rest.protocol;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.reporters.CatalogURIReporter;

import org.restlet.Request;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.toxbank.resource.Resources;

public class ProtocolURIReporter extends CatalogURIReporter<Protocol> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7036662693535963798L;
	public ProtocolURIReporter(Request request,ResourceDoc doc) {
		super(request,doc);
		
		
	}	
	protected ProtocolURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
		
	}
	protected ProtocolURIReporter() {
		super();
	}		
	@Override
	public String getURI(String ref, Protocol item) {
		return String.format("%s%s/%s",ref,Resources.protocol,item.toString());
	}
	@Override
	public String getURI(Protocol item) {
		String ref = baseReference==null?"":baseReference.toString();
		if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);	
		return getURI(ref,item);
	}	

}
