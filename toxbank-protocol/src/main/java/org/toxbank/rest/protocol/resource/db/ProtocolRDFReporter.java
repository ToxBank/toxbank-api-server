package org.toxbank.rest.protocol.resource.db;

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
import net.toxbank.client.io.rdf.ProtocolIO;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.resource.Project;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.projects.db.ReadProjectMembership;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.ReadAuthor;
import org.toxbank.rest.user.resource.UserURIReporter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;

public class ProtocolRDFReporter<Q extends IQueryRetrieval<DBProtocol>> extends QueryRDFReporter<DBProtocol, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected ProtocolIO ioClass = new ProtocolIO();
	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> groupReporter;
	protected UserURIReporter<IQueryRetrieval<DBUser>> userReporter;
	
	public ProtocolRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
		groupReporter = new GroupQueryURIReporter<IQueryRetrieval<IDBGroup>>(request);
		userReporter = new UserURIReporter<IQueryRetrieval<DBUser>>(request);
		getProcessors().clear();
		IQueryRetrieval<DBUser> queryA = new ReadAuthor(null,null); 
		MasterDetailsProcessor<DBProtocol,DBUser,IQueryCondition> authorsReader = new MasterDetailsProcessor<DBProtocol,DBUser,IQueryCondition>(queryA) {
			@Override
			protected DBProtocol processDetail(DBProtocol target, DBUser detail)
					throws Exception {

				detail.setResourceURL(new URL(userReporter.getURI(detail)));
				target.addAuthor(detail);
				return target;
			}
		};
		getProcessors().add(authorsReader);
		
		IQueryRetrieval<DBProject> queryP = new ReadProjectMembership(null,new DBProject()); 
		MasterDetailsProcessor<DBProtocol,DBProject,IQueryCondition> projectsReader = new MasterDetailsProcessor<DBProtocol,DBProject,IQueryCondition>(queryP) {
			@Override
			protected DBProtocol processDetail(DBProtocol target, DBProject detail)
					throws Exception {
				detail.setResourceURL(new URL(groupReporter.getURI(detail)));
				target.addProject(detail);
				return target;
			}
		};
		getProcessors().add(projectsReader);
		
		processors.add(new DefaultAmbitProcessor<DBProtocol,DBProtocol>() {
			@Override
			public DBProtocol process(DBProtocol target) throws AmbitException {
				processItem(target);
				return target;
			};
		});				
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new ProtocolQueryURIReporter(reference);
	}
	@Override
	public void setOutput(Model output) throws AmbitException {
		this.output = output;
		if (output!=null) {
			output.setNsPrefix("tbpl", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.protocol));
			output.setNsPrefix("tbpt", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.project));
			output.setNsPrefix("tbo", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.organisation));
			output.setNsPrefix("tbu", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.user));
			output.setNsPrefix("tb", TOXBANK.URI);
			output.setNsPrefix("dcterms", DCTerms.getURI());
			output.setNsPrefix("xsd", XSD.getURI());
			output.setNsPrefix("foaf", FOAF.NS);
		}
	}
	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			if (item.getProjects()!=null)
				for (Project project: item.getProjects()) 
					if ((project!=null) && (project.getResourceURL()==null))
						project.setResourceURL(new URL(groupReporter.getURI((DBProject)project)));
			if ((item.getOrganisation()!=null) && (item.getOrganisation().getResourceURL()==null))
				item.getOrganisation().setResourceURL(new URL(groupReporter.getURI((DBOrganisation)item.getOrganisation())));
			if ((item.getOwner()!=null) && (item.getOwner().getResourceURL()==null))
				item.getOwner().setResourceURL(new URL(userReporter.getURI((DBUser)item.getOwner())));
						
		
			String uri = uriReporter.getURI(item);
			output.setNsPrefix(item.getIdentifier(), String.format("%s/",uri));
			item.setResourceURL(new URL(uri));
			//no local file names should be serialized!
			if (item.getDocument()!=null) item.getDocument().setResourceURL(new URL(String.format("%s%s",uri,Resources.document)));
			if (item.getDataTemplate()!=null) item.getDataTemplate().setResourceURL(new URL(String.format("%s%s",uri,Resources.datatemplate)));
			
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
