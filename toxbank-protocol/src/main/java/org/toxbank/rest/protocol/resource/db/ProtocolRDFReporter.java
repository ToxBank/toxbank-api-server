package org.toxbank.rest.protocol.resource.db;

import java.net.URL;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.io.rdf.ProtocolIO;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.protocol.DBProtocol;

public class ProtocolRDFReporter<Q extends IQueryRetrieval<DBProtocol>> extends QueryRDFReporter<DBProtocol, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected ProtocolIO ioClass = new ProtocolIO();
	
	public ProtocolRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new ProtocolQueryURIReporter(reference);
	}
	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			item.setResourceURL(new URL(uriReporter.getURI(item)));
			ioClass.toJena(
				getJenaModel(), // create a new class
				item
			);
			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public void open() throws DbAmbitException {
		
	}

}
