package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;

import org.opentox.rdf.BibTex;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.resource.IProtocol;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class ProtocolRDFReporter<Q extends IQueryRetrieval<IProtocol>> extends QueryRDFReporter<IProtocol, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;

	public ProtocolRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new ProtocolQueryURIReporter(reference);
	}
	@Override
	public Object processItem(IProtocol item) throws AmbitException {
		return addToModel(getJenaModel(), item,uriReporter);
	}
	public static Individual addToModel(OntModel jenaModel,IProtocol item, 
				QueryURIReporter<IProtocol, IQueryRetrieval<IProtocol>> uriReporter) {
		
		

		
		Individual entry = null;
		String uri = uriReporter.getURI(item);
		
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null)) {
			entry = jenaModel.createIndividual(BibTex.BTClass.Entry.getOntClass(jenaModel));
		} else {
			entry = jenaModel.createIndividual(uri,BibTex.BTClass.Entry.getOntClass(jenaModel));
	
		}
		
		entry.addProperty(DCTerms.title, item.getTitle());
		entry.addProperty(DCTerms.identifier, item.getIdentifier());
		entry.addProperty(DCTerms.abstract_, item.getAbstract());

		entry.addProperty(DCTerms.source, String.format("%s/file", uri));
		//entry.addProperty(RDFS.seeAlso,item.getURL());
		return entry;
		
	}
	public void open() throws DbAmbitException {
		
	}

}
