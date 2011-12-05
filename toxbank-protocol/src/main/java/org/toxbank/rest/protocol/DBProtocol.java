package org.toxbank.rest.protocol;

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
	
	
	public DBProtocol(int id, int version) {
		setID(id);
		setVersion(version);
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
	@Override
	public String toString() {
		return String.format("<a href='%s'>%s</a>",getResourceURL(),getTitle()==null?getResourceURL():getTitle());
	}
	
}
