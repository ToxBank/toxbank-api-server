package org.toxbank.rest.user.resource;

import java.net.URL;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.io.rdf.ProtocolIO;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.io.rdf.UserIO;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.user.DBUser;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;

public class UserRDFReporter<Q extends IQueryRetrieval<DBUser>> extends QueryRDFReporter<DBUser, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected UserIO ioClass = new UserIO();
	
	public UserRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new UserURIReporter(reference);
	}
	@Override
	public void setOutput(Model output) throws AmbitException {
		this.output = output;
		if (output!=null) {
			output.setNsPrefix("tb", TOXBANK.URI);
			output.setNsPrefix("dcterms", DCTerms.getURI());
			output.setNsPrefix("xsd", XSD.getURI());
		}
	}
	@Override
	public Object processItem(DBUser item) throws AmbitException {
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
