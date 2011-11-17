package org.toxbank.rest.protocol;

import java.util.Iterator;

import org.toxbank.resource.IAuthor;
import org.toxbank.resource.IDocument;
import org.toxbank.resource.IOrganisation;
import org.toxbank.resource.IProject;
import org.toxbank.resource.IProtocol;
import org.toxbank.resource.ITemplate;

public class Protocol implements IProtocol {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6990062112970959369L;
	protected int ID;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	protected String title;
	protected  String identifier;
	protected String anAbstract;
	protected IAuthor author;
	protected Iterator<String> keywords;
	protected IOrganisation owner;
	protected ITemplate template;
	boolean isSummarySearchable;
	protected IProject project;
	protected IDocument document;
	protected String version;
	protected String fileName;
	
	public Protocol() {
	
	}
	
	public Protocol(int id) {
		setID(id);
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getAnAbstract() {
		return anAbstract;
	}
	public void setAnAbstract(String anAbstract) {
		this.anAbstract = anAbstract;
	}
	public IAuthor getAuthor() {
		return author;
	}
	public void setAuthor(IAuthor author) {
		this.author = author;
	}
	public Iterator<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Iterator<String> keywords) {
		this.keywords = keywords;
	}
	public IOrganisation getOwner() {
		return owner;
	}
	public void setOwner(IOrganisation owner) {
		this.owner = owner;
	}
	public ITemplate getTemplate() {
		return template;
	}
	public void setTemplate(ITemplate template) {
		this.template = template;
	}
	public boolean isSummarySearchable() {
		return isSummarySearchable;
	}
	public void setSummarySearchable(boolean isSummarySearchable) {
		this.isSummarySearchable = isSummarySearchable;
	}
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public IDocument getDocument() {
		return document;
	}
	public void setDocument(IDocument document) {
		this.document = document;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return getIdentifier();
	}
}
