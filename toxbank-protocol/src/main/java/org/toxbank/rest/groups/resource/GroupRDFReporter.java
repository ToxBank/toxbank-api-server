package org.toxbank.rest.groups.resource;

import java.net.URL;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.io.rdf.OrganisationIO;
import net.toxbank.client.io.rdf.ProjectIO;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.resource.IToxBankResource;
import net.toxbank.client.resource.Organisation;
import net.toxbank.client.resource.Project;

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
	protected OrganisationIO oIOClass;
	protected ProjectIO pIOClass;
	
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
				
				switch (item.getGroupType()) {
				case ORGANISATION: {
					if (oIOClass==null) oIOClass = new OrganisationIO();
						oIOClass.toJena(
							getJenaModel(),
							(Organisation)item
						);					
					break;
				}
				case PROJECT: {
					if (pIOClass==null) pIOClass = new ProjectIO();
						pIOClass.toJena(
							getJenaModel(), 
							(Project)item
						);					
					break;
				}
				default: {
					
				}
				}

			}
			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public void open() throws DbAmbitException {
		
	}

}
