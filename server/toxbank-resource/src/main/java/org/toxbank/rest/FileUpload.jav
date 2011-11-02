package org.toxbank.rest;

import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.db.DBConnection;

public class FileUpload<USERID> {
	protected Request request;
	protected Application application;
	protected SourceDataset dataset;
	protected boolean firstCompoundOnly = false;
	
	public boolean isFirstCompoundOnly() {
		return firstCompoundOnly;
	}

	public void setFirstCompoundOnly(boolean firstCompoundOnly) {
		this.firstCompoundOnly = firstCompoundOnly;
	}

	public SourceDataset getDataset() {
		return dataset;
	}

	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	protected Response response;
	protected Context context;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Representation upload(Representation entity, 
			Variant variant,
			boolean newEntry, 
			boolean propertyOnly,
			USERID token
			)
				throws ResourceException {	

		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			return processWWWForm(new Form(entity),token);
		} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
			  DiskFileItemFactory factory = new DiskFileItemFactory();
              //factory.setSizeThreshold(100);
	          RestletFileUpload upload = new RestletFileUpload(factory);
	          Connection connection = null;
	          try {
	              List<FileItem> items = upload.parseRequest(getRequest());

	              
	              CallableFileImport callable =
	              new CallableFileImport(getRequest().getClientInfo(),
	            		  dataset,
	            		  items,
	            		  DatasetsHTMLReporter.fileUploadField,
	            		  connection,
	            		  reporter,
	            		  compoundReporter,
	            		  firstCompoundOnly,
	            		  token);
	              
	              callable.setPropertyOnly(propertyOnly);

				  Task<Reference,Object> task =  ((TaskApplication)getApplication()).addTask(
							 "File import",
							callable,
							getRequest().getRootRef(),
							token);
							
				  ITaskStorage storage = ((TaskApplication)getApplication()).getTaskStorage();				  
				  FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
				  task.update();
				  getResponse().setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
	              return tc.createTaskRepresentation(task.getUuid(), variant,getRequest(), getResponse(),null);

				  
	          } catch (Exception x) {
	        	  try { connection.close(); } catch (Exception xx) {xx.printStackTrace();}
	        	  throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
	          }
		} else if (isAllowedMediaType(entity.getMediaType())) {
			/*
					Connection connection = null;
					try {
						DBConnection dbc = new DBConnection(getApplication().getContext());
						connection = dbc.getConnection(getRequest());						
			          DatasetURIReporter<IQueryRetrieval<ISourceDataset>> reporter = 
			            	  	new DatasetURIReporter<IQueryRetrieval<ISourceDataset>> (getRequest(),null);	
		              ConformerURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter = 
		            	  	new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(getRequest(),null);			          
			          
			          CallableFileImport callable = new CallableFileImport(getRequest().getClientInfo(),
			        		  dataset,
			        		  entity,
			        		  connection,
			        		  reporter,
			        		  compoundReporter,
			        		  firstCompoundOnly,
			        		  token);
			          
		              callable.setPropertyOnly(propertyOnly);
			          Task<TaskResult,String> task =  ((AmbitApplication)getApplication()).addTask(
							  
							  	 String.format("File import %s [%d]", entity.getDownloadName()==null?entity.getMediaType():entity.getDownloadName(),entity.getSize()),
								callable,
								getRequest().getRootRef(),
								token==null?null:token.toString());		
			          ITaskStorage storage = ((TaskApplication)getApplication()).getTaskStorage();
			          FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
					  task.update();
					  getResponse().setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);			          
			          return tc.createTaskRepresentation(task.getUuid(), variant,getRequest(), getResponse(),null);
			          
					} catch (Exception x) {
						try { connection.close(); } catch (Exception xx) {xx.printStackTrace();}
 		        	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);

					}
					*/
			 throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,
						String.format("Unsupported Content-type=%s",entity.getMediaType()));
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("Unsupported Content-type=%s",entity.getMediaType()));
	
		
            
	}
	
	protected boolean isAllowedMediaType(MediaType mediaType) {
		return 
		ChemicalMediaType.CHEMICAL_MDLSDF.equals(mediaType) ||
		MediaType.APPLICATION_RDF_XML.equals(mediaType) ||
		MediaType.APPLICATION_RDF_TURTLE.equals(mediaType) ||
		MediaType.TEXT_RDF_N3.equals(mediaType) ||
		MediaType.TEXT_CSV.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_SMILES.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_MDLMOL.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_CML.equals(mediaType);
	}
	
}
