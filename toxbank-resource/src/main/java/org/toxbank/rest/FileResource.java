package org.toxbank.rest;

import java.io.File;
import java.io.Serializable;

import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public abstract class FileResource<T extends Serializable> extends CatalogResource<T> {
	public static final String resourceKey = "key";

	
	protected String directoryPrefix;
	protected final String prefix;
//	protected FileUpload upload;
	
	public String getDirectoryPrefix() {
		return directoryPrefix;
	}
	public void setDirectoryPrefix(String directoryPrefix) {
		this.directoryPrefix = directoryPrefix;
	}
	public FileResource() {
		this("");
		
	}
	public FileResource(String prefix) {
		super();
		this.prefix = prefix;
	}	
	protected File directory;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		/*
		upload = new FileUpload();
		upload.setRequest(getRequest());
		upload.setResponse(getResponse());
		upload.setContext(getContext());
		upload.setApplication(getApplication());
		*/
		
	}
	
	protected org.restlet.data.Reference getSourceReference(org.restlet.data.Form form, T model) throws ResourceException {
		return null;
	};
	protected net.idea.restnet.i.task.ICallableTask createCallable(org.restlet.data.Form form, T item) throws ResourceException {
		return null;
	};
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		/*
		return  upload.upload(entity,variant,true,false,
				getToken()
				);
				*/
		return null;
		
	}	
}
