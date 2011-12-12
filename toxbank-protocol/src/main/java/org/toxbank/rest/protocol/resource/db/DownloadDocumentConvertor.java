package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.AbstractObjectConvertor;

import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.toxbank.rest.protocol.DBProtocol;

public class DownloadDocumentConvertor extends   AbstractObjectConvertor<DBProtocol, IQueryRetrieval<DBProtocol>,FileRepresentation> {

	public DownloadDocumentConvertor(
			QueryReporter<DBProtocol, IQueryRetrieval<DBProtocol>, FileRepresentation> reporter) {
		super(reporter);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1979008352251532084L;

	/*
	protected File getFile(DBProtocol protocol) throws Exception  {
		return new File(protocol.getDocument().getResourceURL().toURI());
	}

	public Representation process(DBProtocol protocol) throws AmbitException {
		try {
			if (protocol==null) throw new AmbitException("No protocol!");

			File file = getFile(protocol);
			if (!file.exists()) throw new AmbitException("No file!");
			return new FileRepresentation(file, MediaType.APPLICATION_PDF);
			
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}
	
	*/
	@Override
	protected FileRepresentation createOutput(IQueryRetrieval<DBProtocol> query)
			throws AmbitException {
		return null;
	}
	@Override
	public Representation process(FileRepresentation doc) throws AmbitException {
		return doc;
	}

}
