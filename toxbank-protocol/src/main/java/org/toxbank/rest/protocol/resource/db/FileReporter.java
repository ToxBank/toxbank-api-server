package org.toxbank.rest.protocol.resource.db;

import java.io.File;
import java.net.URISyntaxException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.toxbank.rest.protocol.DBProtocol;


public class FileReporter extends QueryReporter<DBProtocol, IQueryRetrieval<DBProtocol>, FileRepresentation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1470164118404153388L;

	public FileReporter() {
		super();
	}
	
	@Override
	public void header(FileRepresentation output,
			IQueryRetrieval<DBProtocol> query) {
	}

	@Override
	public void footer(FileRepresentation output,
			IQueryRetrieval<DBProtocol> query) {
	}

	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			File file = new File(item.getDocument().getResourceURL().toURI());
			setOutput(new FileRepresentation(file, new MediaType(item.getDocument().getMediaType())));
			return item;
		} catch (URISyntaxException x) {
			throw new AmbitException(x);
		}
	}

}
