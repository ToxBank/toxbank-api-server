package org.toxbank.rest.protocol.resource.db;

import java.io.File;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.AbstractObjectConvertor;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.toxbank.resource.IProtocol;

public class DownloadDocumentConvertor extends   AbstractObjectConvertor<IProtocol, IQueryRetrieval<IProtocol>,FileRepresentation> {

	public DownloadDocumentConvertor(
			QueryReporter<IProtocol, IQueryRetrieval<IProtocol>, FileRepresentation> reporter) {
		super(reporter);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1979008352251532084L;


	public Representation process(IProtocol doc) throws AmbitException {
		try {
			if (doc==null) throw new AmbitException("No document!");
			File file = new File(doc.getDocument().getURI());
			if (!file.exists()) throw new AmbitException("No file!");
			
			return new FileRepresentation(file, MediaType.APPLICATION_PDF);
			
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}


	@Override
	public Representation process(FileRepresentation doc) throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected FileRepresentation createOutput(IQueryRetrieval<IProtocol> query)
			throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}
}
