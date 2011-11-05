package org.toxbank.rest.protocol.resource.file;

import java.io.File;
import java.util.Iterator;

import net.idea.modbcum.i.reporter.Reporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;
import org.toxbank.resource.IProtocol;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.ProtocolHTMLReporter;
import org.toxbank.rest.protocol.ProtocolURIReporter;
import org.toxbank.rest.protocol.ProtocolsIterator;

public class ProtocolFileResource extends FileResource<IProtocol> {
	
	public ProtocolFileResource() {
		super(IProtocol.resource);
		try {
			directoryPrefix =  System.getProperty("java.io.tmpdir");

		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out.println(directoryPrefix);
		File file = new File(String.format("%s%s", directoryPrefix,prefix));
		if (!file.exists()) file.mkdir();
		System.out.println(file);
	}
	@Override
	protected Reporter createURIReporter() {
		return
		new ProtocolURIReporter(getRequest(),getDocumentation());
	}
	@Override
	protected Reporter createHTMLReporter() {
		return new ProtocolHTMLReporter(getRequest(),getDocumentation());
	}
	@Override
	protected Iterator<IProtocol> createQuery(Context context, Request request,
			Response response) throws ResourceException {
	
		
		final Object key = request.getAttributes().get(resourceKey);
		System.out.println(key);
		File file = new File(String.format("%s%s", directoryPrefix,prefix));

		return new ProtocolsIterator(file,key==null?null:key.toString());
		
	}

	
}
