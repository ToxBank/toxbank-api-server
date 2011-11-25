package org.toxbank.rest.groups.resource;

import java.net.URL;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.io.rdf.IOClass;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.resource.IToxBankResource;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.groups.IDBGroup;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;
/**
 * 
 * @author nina
 *
 * @param <Q>
 */
public class GroupRDFReporter<Q extends IQueryRetrieval<IDBGroup>> extends QueryRDFReporter<IDBGroup, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected IOClass ioClass;
	
	public GroupRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new GroupQueryURIReporter(reference);
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
	public Object processItem(IDBGroup item) throws AmbitException {
		try {
			if (item instanceof IToxBankResource) {
				((IToxBankResource) item).setResourceURL(new URL(uriReporter.getURI(item)));
				throw new AmbitException("Not implemented");
				/*
				if (ioClass==null)
					if (item instanceof Organisation) ioClass = new Orga
					ioClass
				}
				ioClass.toJena(
					getJenaModel(), // create a new class
					item
				);
				*/
			}
			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public void open() throws DbAmbitException {
		
	}

}
