package org.toxbank.rest.protocol;

import java.net.URL;

import net.toxbank.client.resource.Protocol;

import org.toxbank.resource.IDocument;
import org.toxbank.resource.ITemplate;

public class DBProtocol extends Protocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6632168193661223228L;
	protected int ID;
	protected IDocument document;
	protected ITemplate template;
	
	public DBProtocol() {
		
	}
	
	public DBProtocol(int id) {
		setID(id);
	}
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	public IDocument getDocument() {
		return document;
	}
	public void setDocument(IDocument document) {
		this.document = document;
	}
	
	public ITemplate getTemplate() {
		return template;
	}
	public void setTemplate(ITemplate template) {
		this.template = template;
	}
	private boolean summarySearchable;

	public boolean isSummarySearchable() {
		return summarySearchable;
	}

	public void setSummarySearchable(boolean summarySearchable) {
		this.summarySearchable = summarySearchable;
	}
	private URL project;
	public URL getProject() {
		return project;
	}

	public void setProject(URL project) {
		this.project = project;
	}

}
