package org.toxbank.rest.user.alerts.resource;

import java.net.URL;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.Resources;
import net.toxbank.client.io.rdf.AlertIO;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.io.rdf.UserIO;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.ReadOrganisation;
import org.toxbank.rest.groups.db.ReadProject;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.alerts.db.DBAlert;
import org.toxbank.rest.user.resource.UserURIReporter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Generates RDF representation of query results for users
 * @author nina
 *
 * @param <Q>
 */
public class AlertRDFReporter<Q extends IQueryRetrieval<DBAlert>> extends QueryRDFReporter<DBAlert, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected UserIO user_ioClass = new UserIO();
	protected AlertIO ioClass = new AlertIO();
	protected UserURIReporter<IQueryRetrieval<DBUser>> userReporter;
	
	public AlertRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
		userReporter = new UserURIReporter<IQueryRetrieval<DBUser>>(request);
	}
	
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new AlertURIReporter(reference);
	}
	@Override
	public void setOutput(Model output) throws AmbitException {
		this.output = output;
		if (output!=null) {
			output.setNsPrefix("tb", TOXBANK.URI);
			output.setNsPrefix("dcterms", DCTerms.getURI());
			output.setNsPrefix("xsd", XSD.getURI());
			output.setNsPrefix("foaf", FOAF.NS);
			output.setNsPrefix("tba", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.alert));
			output.setNsPrefix("tbu", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.user));
			
		}
	}
	@Override
	public Object processItem(DBAlert item) throws AmbitException {
		try {
			item.setResourceURL(new URL(uriReporter.getURI(item)));
			item.getUser().setResourceURL(new URL(userReporter.getURI(item.getUser())));
			ioClass.objectToJena(
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
