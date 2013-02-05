package org.toxbank.demo.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.SimpleTaskResource;
import net.idea.restnet.c.reporters.TaskURIReporter;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;
import net.idea.restnet.rdf.reporter.CatalogRDFReporter;
import net.toxbank.client.io.rdf.OPENTOX;

import org.opentox.rdf.OT;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * RDF generation for {@link Task}
 * Copied from restnet-rdf to use different opentox name space
 * @author nina
 *
 */
public class TBTaskRDFReporter<USERID> extends CatalogRDFReporter<UUID> {
	protected ITaskStorage<USERID> storage;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3789102915378513270L;
	protected Reference baseRef;
	protected TaskURIReporter<USERID> urireporter;
	
	public TBTaskRDFReporter(ITaskStorage<USERID> storage, Request request, MediaType mediaType,ResourceDoc doc) {
		super(request, mediaType,doc);
		baseRef = request.getRootRef();
		urireporter = new TaskURIReporter<USERID>(storage,request,doc);
		this.storage = storage;
	}
	
	@Override
	public void header(Writer output, Iterator<UUID> query) {
		try {
			setJenaModel(jenaModel==null?ModelFactory.createOntologyModel():jenaModel);
			jenaModel.setNsPrefix( "ot", OPENTOX.URI );
			getJenaModel().createAnnotationProperty(DC.title.getURI());
			getJenaModel().createAnnotationProperty(DC.date.getURI());
		} catch (Exception x) {
			Context.getCurrentLogger().warning(x.getMessage());
		}	
	}
	

	@Override
	public void processItem(UUID name, Writer output) {
		String ref = null;

		Task<TaskResult,USERID> item = storage.findTask(name.toString());
		try {
			ref = item.getUri().toString();
		} catch (Exception x) {
			ref = item.getUri().toString();
		}
	
		Resource res = getJenaModel().createResource(String.format("%s%s/%s", baseRef,SimpleTaskResource.resource,item.getUuid()));
		getJenaModel().add(res, RDF.type, OPENTOX.TASK);
	
		res.addLiteral(DC.title, item.getName());
		res.addLiteral(DC.date, item.getStarted());
		res.addLiteral(OPENTOX.HASSTATUS, item.getStatus().name());
		
		if (item.getError()!=null) {
			Resource error = getJenaModel().createResource();
			getJenaModel().add(error, RDF.type, OPENTOX.ErrorReport);
			
			ResourceException exception = item.getError();
			if (exception!=null) {
			error.addLiteral(OPENTOX.errorCode,exception.getStatus().getCode());
			//error.addLiteral(OT.DataProperty.actor.createProperty(getJenaModel()),);
			System.out.println(exception.getMessage());
			if (exception.getMessage()!=null)
				error.addLiteral(OPENTOX.message,exception.getMessage());
			if (exception.getMessage()!=null)
			error.addLiteral(OPENTOX.errorDetails,exception.getStatus().getDescription());
			
			try {
		    	StringWriter w = new StringWriter();
		    	if (exception.getCause()==null)
		    		exception.printStackTrace(new PrintWriter(w));
		    	else exception.getCause().printStackTrace(new PrintWriter(w));
				error.addLiteral(OPENTOX.errorCause,w.toString());
			} catch (Exception x) {
				
			}
			
			res.addProperty(OPENTOX.error,error);
			}
		}
		if (item.isDone()) try {
			res.addLiteral(OPENTOX.resultURI,item.getUri().toString());
		} catch (Exception x) {
			x.printStackTrace(); //TODO error handling
		}
		
	}

}
